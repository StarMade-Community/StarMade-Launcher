package smlauncher.ui.panels;

import smlauncher.ui.controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Community panel to display community servers and links
 */
public class CommunityPanel extends BasePanel {
	private static final String STARMADE_DISCORD = "https://discord.gg/starmade";
	private static final String STARLOADER_DISCORD = "https://discord.gg/Y2UR7AXfsE";

	public CommunityPanel(MainController mainController) {
		super(mainController);
		initializeComponents();
	}

	private void initializeComponents() {
		// Set layout to BorderLayout
		setLayout(new BorderLayout(10, 10));

		// Discord connections panel
		JPanel discordPanel = createDiscordConnectionsPanel();
		add(discordPanel, BorderLayout.NORTH);

		// Community description
		JTextArea communityDescription = createCommunityDescription();
		add(new JScrollPane(communityDescription), BorderLayout.CENTER);

		// Community servers table
		JTable communityServersTable = createCommunityServersTable();
		add(new JScrollPane(communityServersTable), BorderLayout.SOUTH);
	}

	/**
	 * Create panel with Discord connection buttons
	 * @return JPanel with Discord buttons
	 */
	private JPanel createDiscordConnectionsPanel() {
		JPanel discordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		discordPanel.setBorder(BorderFactory.createTitledBorder("Community Connections"));

		JButton starmadeDiscordButton = createDiscordButton("StarMade Official Discord", STARMADE_DISCORD);
		JButton starloaderDiscordButton = createDiscordButton("StarLoader Discord", STARLOADER_DISCORD);

		discordPanel.add(starmadeDiscordButton);
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
		JButton button = new JButton(text);
		button.addActionListener((ActionEvent e) -> openURL(url));
		button.setFocusPainted(false);
		return button;
	}

	/**
	 * Create community description text area
	 * @return JTextArea with community description
	 */
	private JTextArea createCommunityDescription() {
		JTextArea communityDescription = new JTextArea();
		communityDescription.setText("Welcome to the StarMade Community!\n\n" + "Connect with other players, share your experiences, " + "get help, and stay up to date with the latest developments.\n\n" + "Whether you're a new player or a seasoned space architect, " + "there's a place for you in the StarMade community.");
		communityDescription.setEditable(false);
		communityDescription.setLineWrap(true);
		communityDescription.setWrapStyleWord(true);
		communityDescription.setBackground(getBackground());
		communityDescription.setForeground(Color.WHITE);
		communityDescription.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return communityDescription;
	}

	/**
	 * Create a table of community servers
	 * @return JTable with community server information
	 */
	private JTable createCommunityServersTable() {
		String[] columnNames = {"Server Name", "Description", "Link"};
		Object[][] data = {{"The Cake Network", "Official StarMade community server", "https://discord.gg/KXpxqdn"}, {"Skies of Eden", "Community-run StarMade server", "https://discord.gg/4gvTW7655H"}};

		JTable serversTable = new JTable(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Make the last column (link) clickable
		serversTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = serversTable.rowAtPoint(evt.getPoint());
				int col = serversTable.columnAtPoint(evt.getPoint());
				if(row >= 0 && col == 2) {
					String url = (String) serversTable.getValueAt(row, col);
					openURL(url);
				}
			}
		});

		// Customize table appearance
		serversTable.setBackground(new Color(25, 25, 31));
		serversTable.setForeground(Color.WHITE);
		serversTable.setSelectionBackground(new Color(67, 128, 148));
		serversTable.getTableHeader().setBackground(new Color(35, 35, 41));
		serversTable.getTableHeader().setForeground(Color.WHITE);

		return serversTable;
	}
}