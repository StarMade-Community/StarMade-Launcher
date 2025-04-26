package smlauncher.ui.panels;

import smlauncher.ui.controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Community panel to display community servers and links
 */
public class CommunityPanel extends BasePanel {
	// Discord connection URLs
	private static final String STARMADE_DISCORD = "https://discord.gg/starmade";
	private static final String STARLOADER_DISCORD = "https://discord.gg/Y2UR7AXfsE";

	// Community server information
	private static final List<CommunityServer> COMMUNITY_SERVERS = new ArrayList<>() {{
		add(new CommunityServer("The Cake Network", "Official StarMade community server", "https://discord.gg/KXpxqdn"));
		add(new CommunityServer("Skies of Eden", "Community-run StarMade server", "https://discord.gg/4gvTW7655H"));
	}};

	/**
	 * Represents a community server entry
	 */
	private static class CommunityServer {
		final String name;
		final String description;
		final String link;

		CommunityServer(String name, String description, String link) {
			this.name = name;
			this.description = description;
			this.link = link;
		}
	}

	public CommunityPanel(MainController mainController) {
		super(mainController);
		initializeComponents();
	}

	@Override
	protected void initializeComponents() {
		// Set layout to BorderLayout with padding
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Title
		JLabel titleLabel = createTitleLabel("StarMade Community");
		add(titleLabel, BorderLayout.NORTH);

		// Community description
		JTextArea descriptionArea = createDescriptionArea("Welcome to the StarMade Community!\n\n" + "Connect with other players, share your experiences, " + "get help, and stay up to date with the latest developments.\n\n" + "Whether you're a new player or a seasoned space architect, " + "there's a place for you in the StarMade community.");
		add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

		// Discord connections panel
		JPanel discordPanel = createDiscordConnectionsPanel();
		add(discordPanel, BorderLayout.SOUTH);
	}

	/**
	 * Create panel with Discord connection buttons
	 * @return JPanel with Discord buttons
	 */
	private JPanel createDiscordConnectionsPanel() {
		JPanel discordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		discordPanel.setBackground(Colors.BACKGROUND_DARK);
		discordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Colors.ACCENT_BLUE), "Community Connections"));

		// Create StarMade Discord button
		JButton starmadeDiscordButton = createDiscordButton("StarMade Official Discord", STARMADE_DISCORD);
		discordPanel.add(starmadeDiscordButton);

		// Create StarLoader Discord button
		JButton starloaderDiscordButton = createDiscordButton("StarLoader Discord", STARLOADER_DISCORD);
		discordPanel.add(starloaderDiscordButton);

		return discordPanel;
	}

	/**
	 * Create a styled Discord connection button
	 * @param text Button text
	 * @param url Discord invite URL
	 * @return Styled JButton
	 */
	private JButton createDiscordButton(String text, String url) {
		return createStyledButton(text, e -> openURL(url), new Color(114, 137, 218)); // Discord color
	}
}