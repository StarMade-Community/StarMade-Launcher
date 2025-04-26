package smlauncher.ui.panels;

import smlauncher.ui.controllers.MainController;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for displaying StarMade community content
 */
public class ContentPanel extends BasePanel {
	// Content types with URLs
	private static final List<ContentType> CONTENT_TYPES = new ArrayList<>() {{
		add(new ContentType("Blueprints", "Share and discover player-created ship designs", "https://starmadedock.net/content/blueprints/"));
		add(new ContentType("Mods", "User-created modifications for StarMade", "https://starmadedock.net/content/mods/"));
		add(new ContentType("Tutorials", "Guides and walkthroughs for StarMade", "https://starmadedock.net/content/tutorials/"));
		add(new ContentType("Sector Exports", "Shareable star systems and space scenes", "https://starmadedock.net/content/sector-exports/"));
	}};

	/**
	 * Represents a content type
	 */
	private static class ContentType {
		final String name;
		final String description;
		final String url;

		ContentType(String name, String description, String url) {
			this.name = name;
			this.description = description;
			this.url = url;
		}
	}

	public ContentPanel(MainController mainController) {
		super(mainController);
		initializeComponents();
	}

	@Override
	protected void initializeComponents() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Title
		JLabel titleLabel = createTitleLabel("StarMade Community Content");
		add(titleLabel, BorderLayout.NORTH);

		// Description
		JTextArea descriptionArea = createDescriptionArea("Explore and share amazing StarMade content created by our community!\n\n" + "From intricate blueprints to gameplay-changing mods, " + "discover the creativity of StarMade players around the world.");
		add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

		// Content grid
		JPanel contentGridPanel = createContentGridPanel();
		add(contentGridPanel, BorderLayout.SOUTH);
	}

	/**
	 * Create a grid panel of content types
	 * @return JPanel with content type buttons
	 */
	private JPanel createContentGridPanel() {
		JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		gridPanel.setBackground(Colors.BACKGROUND_DARK);
		gridPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Colors.ACCENT_BLUE), "Content Categories"));

		for(ContentType contentType : CONTENT_TYPES) {
			JButton contentButton = createContentTypeButton(contentType);
			gridPanel.add(contentButton);
		}

		return gridPanel;
	}

	/**
	 * Create a button for a content type
	 * @param contentType Content type to create a button for
	 * @return Styled JButton for the content type
	 */
	private JButton createContentTypeButton(ContentType contentType) {
		JButton button = createStyledButton(contentType.name, e -> openURL(contentType.url), null);

		// Add tooltip with description
		button.setToolTipText(contentType.description);

		return button;
	}
}