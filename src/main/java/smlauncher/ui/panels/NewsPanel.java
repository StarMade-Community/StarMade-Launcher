package smlauncher.ui.panels;

import org.json.JSONArray;
import org.json.JSONObject;
import smlauncher.ui.controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for displaying StarMade news from the Steam News API
 */
public class NewsPanel extends BasePanel {
	private static final String STEAM_NEWS_API = "http://api.steampowered.com/ISteamNews/GetNewsForApp/v0002/?appid=244770&count=10&format=json";

	private JPanel newsContainer;

	public NewsPanel(MainController mainController) {
		super(mainController);
		initializeComponents();
	}

	@Override
	protected void initializeComponents() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Title
		JLabel titleLabel = createTitleLabel("StarMade News");
		add(titleLabel, BorderLayout.NORTH);

		// News container
		newsContainer = new JPanel();
		newsContainer.setLayout(new BoxLayout(newsContainer, BoxLayout.Y_AXIS));
		newsContainer.setBackground(Colors.BACKGROUND_DARK);

		JScrollPane newsScrollPane = new JScrollPane(newsContainer);
		add(newsScrollPane, BorderLayout.CENTER);

		// Load news in a background thread
		SwingWorker<List<NewsItem>, Void> newsLoader = new SwingWorker<>() {
			@Override
			protected List<NewsItem> doInBackground() throws Exception {
				return fetchNewsItems();
			}

			@Override
			protected void done() {
				try {
					List<NewsItem> newsItems = get();
					displayNewsItems(newsItems);
				} catch(Exception e) {
					logger.error("Failed to load news items", e);
					showErrorMessage(e);
				}
			}
		};
		newsLoader.execute();
	}

	/**
	 * Fetch news items from the Steam News API
	 * @return List of NewsItem objects
	 * @throws Exception If there's an error fetching or parsing news
	 */
	private List<NewsItem> fetchNewsItems() throws Exception {
		URL url = new URL(STEAM_NEWS_API);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "StarMade Launcher");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		List<NewsItem> newsItems = new ArrayList<>();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

			StringBuilder response = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				response.append(line);
			}

			// Verify successful connection
			int responseCode = connection.getResponseCode();
			if(responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("Failed to fetch news. Response Code: " + responseCode);
			}

			JSONObject jsonResponse = new JSONObject(response.toString());
			JSONArray newsArray = jsonResponse.getJSONObject("appnews").getJSONArray("newsitems");

			for(int i = 0; i < newsArray.length(); i++) {
				JSONObject newsJson = newsArray.getJSONObject(i);
				newsItems.add(NewsItem.fromJson(newsJson));
			}
		} finally {
			connection.disconnect();
		}

		return newsItems;
	}

	/**
	 * Display news items in the panel
	 * @param newsItems List of news items to display
	 */
	private void displayNewsItems(List<NewsItem> newsItems) {
		SwingUtilities.invokeLater(() -> {
			newsContainer.removeAll();

			if(newsItems.isEmpty()) {
				showErrorMessage(new Exception("No news items found"));
				return;
			}

			for(NewsItem newsItem : newsItems) {
				JPanel newsItemPanel = createNewsItemPanel(newsItem);
				newsContainer.add(newsItemPanel);
				newsContainer.add(Box.createVerticalStrut(10)); // Add spacing between news items
			}

			newsContainer.revalidate();
			newsContainer.repaint();
		});
	}

	/**
	 * Create a panel for a single news item
	 * @param newsItem News item to display
	 * @return JPanel representing the news item
	 */
	private JPanel createNewsItemPanel(NewsItem newsItem) {
		JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
		itemPanel.setBackground(Colors.BACKGROUND_DARK);
		itemPanel.setBorder(BorderFactory.createLineBorder(Colors.ACCENT_BLUE));
		itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

		// Title
		JLabel titleLabel = new JLabel(newsItem.title);
		titleLabel.setFont(Fonts.SUBTITLE);
		titleLabel.setForeground(Colors.TEXT_LIGHT);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		itemPanel.add(titleLabel, BorderLayout.NORTH);

		// Content
		JTextArea contentArea = createDescriptionArea(newsItem.content, true, Colors.TEXT_MUTED);
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		contentArea.setEditable(false);
		JScrollPane contentScrollPane = new JScrollPane(contentArea);
		contentScrollPane.setBorder(BorderFactory.createEmptyBorder());
		itemPanel.add(contentScrollPane, BorderLayout.CENTER);

		// Footer
		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBackground(Colors.BACKGROUND_DARK);
		footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel metaLabel = new JLabel(String.format("Posted on %s by %s", newsItem.formattedDate, newsItem.author));
		metaLabel.setForeground(Colors.TEXT_MUTED);
		metaLabel.setFont(Fonts.BODY);
		footerPanel.add(metaLabel, BorderLayout.WEST);

		// Read more button
		JButton readMoreButton = createStyledButton("Read More", e -> openURL(newsItem.url));
		footerPanel.add(readMoreButton, BorderLayout.EAST);

		itemPanel.add(footerPanel, BorderLayout.SOUTH);

		return itemPanel;
	}

	/**
	 * Show an error message when news cannot be loaded
	 * @param exception The exception that occurred
	 */
	private void showErrorMessage(Exception exception) {
		SwingUtilities.invokeLater(() -> {
			newsContainer.removeAll();

			String errorMessage = "Unable to load news at this time. ";
			if(exception != null) {
				errorMessage += "Error: " + exception.getMessage();
			}

			JTextArea errorArea = createDescriptionArea(errorMessage, true, Color.RED);
			errorArea.setEditable(false);

			newsContainer.add(errorArea);
			newsContainer.revalidate();
			newsContainer.repaint();
		});
	}

	/**
	 * Represents a single news item from the Steam News API
	 */
	private static class NewsItem {
		final String title;
		final String content;
		final String url;
		final String author;
		final String formattedDate;

		private NewsItem(String title, String content, String url, String author, String formattedDate) {
			this.title = title;
			this.content = content;
			this.url = url;
			this.author = author;
			this.formattedDate = formattedDate;
		}

		/**
		 * Create a NewsItem from a JSON object
		 * @param json JSONObject representing a news item
		 * @return NewsItem created from the JSON
		 */
		static NewsItem fromJson(JSONObject json) {
			String title = json.getString("title");
			String content = formatContent(json.getString("contents"));
			String url = json.getString("url");
			String author = json.getString("author");

			// Convert timestamp to a readable date
			long timestamp = json.getLong("date");
			LocalDate date = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();

			// Format date in a more readable way
			String formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

			return new NewsItem(title, content, url, author, formattedDate);
		}

		/**
		 * Format the content of a news item
		 * @param rawContent Raw content string
		 * @return Formatted content
		 */
		private static String formatContent(String rawContent) {
			// Remove BBCode-like formatting
			String cleanContent = rawContent.replaceAll("\\[.*?\\]", "") // Remove BBCode tags
					.replaceAll("\n+", " ") // Replace multiple newlines with single space
					.trim();

			// Limit content length
			return cleanContent.length() > 300 ? cleanContent.substring(0, 300) + "..." : cleanContent;
		}
	}
}