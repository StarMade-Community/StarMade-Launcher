package smlauncher.ui.components;

import smlauncher.ui.controllers.MainController;
import smlauncher.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Navigation panel on the left side of the launcher
 */
public class LeftSidePanel extends JPanel {
	private final MainController mainController;
	private final Map<String, JPanel> navigationPanels;
	private final JList<String> navigationList;
	private final DefaultListModel<String> navigationModel;

	/**
	 * Navigation panel sections
	 */
	public enum NavigationSection {
		NEWS("News"),
		FORUMS("Forums"),
		CONTENT("Content"),
		COMMUNITY("Community");

		private final String displayName;

		NavigationSection(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	public LeftSidePanel(MainController mainController) {
		this.mainController = mainController;
		this.navigationPanels = new HashMap<>();

		// Set panel properties
		setPreferredSize(new Dimension(200, getHeight()));
		setLayout(new BorderLayout());

		// Create navigation list
		navigationModel = new DefaultListModel<>();
		for (NavigationSection section : NavigationSection.values()) {
			navigationModel.addElement(section.getDisplayName());
		}

		navigationList = createNavigationList();

		// Create logo panel
		JPanel logoPanel = createLogoPanel();

		// Add components
		add(logoPanel, BorderLayout.NORTH);
		add(new JScrollPane(navigationList), BorderLayout.CENTER);
	}

	/**
	 * Create navigation list with custom rendering
	 * @return JList for navigation
	 */
	private JList<String> createNavigationList() {
		JList<String> list = new JList<>(navigationModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Custom cell renderer
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(
					JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {

				JLabel label = (JLabel) super.getListCellRendererComponent(
						list, value, index, isSelected, cellHasFocus);

				// Style the label
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("Arial", Font.BOLD, 14));

				// Custom colors for selection
				if (isSelected) {
					label.setBackground(new Color(67, 128, 148)); // Deep blue-green
					label.setForeground(Color.WHITE);
				} else {
					label.setBackground(new Color(25, 25, 31)); // Dark background
					label.setForeground(Color.LIGHT_GRAY);
				}

				return label;
			}
		});

		// Add selection listener
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				String selectedSection = list.getSelectedValue();
				navigateToSection(selectedSection);
			}
		});

		return list;
	}

	/**
	 * Create logo panel for the side navigation
	 * @return JPanel with logo
	 */
	private JPanel createLogoPanel() {
		JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		try {
			ImageIcon logoIcon = new ImageIcon(getClass().getResource("/sprites/logo.png"));
			JLabel logoLabel = new JLabel(logoIcon);
			logoPanel.add(logoLabel);
		} catch (Exception e) {
			logoPanel.add(new JLabel("StarMade"));
		}
		return logoPanel;
	}

	/**
	 * Navigate to a specific section
	 * @param sectionName Name of the section to navigate to
	 */
	private void navigateToSection(String sectionName) {
		JPanel sectionPanel = null;

		// Determine which panel to load
		switch (sectionName) {
			case "News":
				sectionPanel = new NewsPanel(mainController);
				break;
			case "Forums":
				sectionPanel = new ForumsPanel(mainController);
				break;
			case "Content":
				sectionPanel = new ContentPanel(mainController);
				break;
			case "Community":
				sectionPanel = new CommunityPanel(mainController);
				break;
		}

		// Update main frame's center content if panel was created
		if (sectionPanel != null) {
			// This would typically be done through an event or method in LauncherFrame
			// For now, we'll leave a TODO comment
			// TODO: Implement method to update center panel content
			System.out.println("Navigated to: " + sectionName);
		}
	}
}