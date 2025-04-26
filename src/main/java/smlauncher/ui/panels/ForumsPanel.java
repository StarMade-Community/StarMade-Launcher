package smlauncher.ui.panels;

import smlauncher.ui.controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for displaying StarMade community forums
 */
public class ForumsPanel extends BasePanel {
	// Forum sections with URLs
	private static final List<ForumSection> FORUM_SECTIONS = new ArrayList<>() {{
		add(new ForumSection("General Discussion", "Discuss all things StarMade", "https://starmadedock.net/forums/general-discussion/"));
		add(new ForumSection("Ship Building", "Share and discuss ship designs", "https://starmadedock.net/forums/ship-building/"));
		add(new ForumSection("Modding", "Mod creation and discussion", "https://starmadedock.net/forums/modding/"));
		add(new ForumSection("Suggestions", "Share your ideas for the game", "https://starmadedock.net/forums/suggestions/"));
	}};

	/**
	 * Represents a forum section
	 */
	private static class ForumSection {
		final String name;
		final String description;
		final String url;

		ForumSection(String name, String description, String url) {
			this.name = name;
			this.description = description;
			this.url = url;
		}
	}

	public ForumsPanel(MainController mainController) {
		super(mainController);
		initializeComponents();
	}

	@Override
	protected void initializeComponents() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Title
		JLabel titleLabel = createTitleLabel("StarMade Forums");
		add(titleLabel, BorderLayout.NORTH);

		// Description
		JTextArea descriptionArea = createDescriptionArea("Welcome to the StarMade Community Forums!\n\n" + "Connect with other players, share your experiences, " + "get help, and contribute to the StarMade community. " + "Choose a section below to explore discussions.");
		add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

		// Forums grid
		JPanel forumsGridPanel = createForumsGridPanel();
		add(forumsGridPanel, BorderLayout.SOUTH);
	}

	/**
	 * Create a grid panel of forum sections
	 * @return JPanel with forum section buttons
	 */
	private JPanel createForumsGridPanel() {
		JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		gridPanel.setBackground(Colors.BACKGROUND_DARK);
		gridPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Colors.ACCENT_BLUE), "Forum Sections"));

		for(ForumSection section : FORUM_SECTIONS) {
			JButton sectionButton = createForumSectionButton(section);
			gridPanel.add(sectionButton);
		}

		return gridPanel;
	}

	/**
	 * Create a button for a forum section
	 * @param section Forum section to create a button for
	 * @return Styled JButton for the forum section
	 */
	private JButton createForumSectionButton(ForumSection section) {
		return createStyledButton(section.name, e -> openURL(section.url), null);
	}
}