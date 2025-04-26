package smlauncher.ui.panels;

import smlauncher.core.model.GameVersionManager;
import smlauncher.ui.controllers.MainController;
import smlauncher.util.logging.LauncherLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * Footer panel for the StarMade Launcher
 * Handles version selection, launch modes, and other footer functionalities
 */
public class FooterPanel extends JPanel {
	private final MainController mainController;
	private final LauncherLogger logger;

	// UI Components
	private JComboBox<String> branchDropdown;
	private JComboBox<String> versionDropdown;
	private JTextField portField;
	private JButton launchButton;
	private JToggleButton modeToggleButton;

	// Launch modes
	private enum LaunchMode {
		CLIENT("Client Mode"),
		SERVER("Server Mode");

		private final String displayName;

		LaunchMode(String displayName) {
			this.displayName = displayName;
		}
	}

	public FooterPanel(MainController mainController) {
		this.mainController = mainController;
		this.logger = LauncherLogger.getInstance();

		setLayout(new BorderLayout());
		setBackground(new Color(25, 25, 31)); // Dark background
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		initializeComponents();
	}

	private void initializeComponents() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		controlPanel.setOpaque(false);

		// Branch Dropdown
		branchDropdown = createBranchDropdown();
		controlPanel.add(branchDropdown);

		// Version Dropdown
		versionDropdown = createVersionDropdown();
		controlPanel.add(versionDropdown);

		// Mode Toggle Button
		modeToggleButton = createModeToggleButton();
		controlPanel.add(modeToggleButton);

		// Port Field (initially hidden)
		portField = createPortField();
		controlPanel.add(portField);
		portField.setVisible(false);

		// Launch Button
		launchButton = createLaunchButton();
		controlPanel.add(launchButton);

		add(controlPanel, BorderLayout.CENTER);

		// Add listeners
		setupListeners();
	}

	private JComboBox<String> createBranchDropdown() {
		JComboBox<String> dropdown = new JComboBox<>(new String[]{"Release", "Pre-Release", "Development"});
		dropdown.setFocusable(false);
		dropdown.setBackground(new Color(25, 25, 31));
		dropdown.setForeground(Color.WHITE);

		// Set initial branch based on last used
		String lastUsedBranch = mainController.getLastUsedVersion();
		if(lastUsedBranch != null) {
			// Logic to select appropriate branch based on last used version
			// This is a placeholder and might need refinement
			dropdown.setSelectedIndex(0);
		}

		return dropdown;
	}

	private JComboBox<String> createVersionDropdown() {
		JComboBox<String> dropdown = new JComboBox<>();
		dropdown.setFocusable(false);
		dropdown.setBackground(new Color(25, 25, 31));
		dropdown.setForeground(Color.WHITE);

		// Populate versions for the selected branch
		updateVersionDropdown();

		return dropdown;
	}

	private void updateVersionDropdown() {
		// Get current selected branch
		GameVersionManager.GameBranch selectedBranch = mapIndexToBranch(branchDropdown.getSelectedIndex());

		// Clear existing items
		versionDropdown.removeAllItems();

		// Fetch and populate versions
		List<GameVersionManager.GameVersion> versions = mainController.loadVersionsForBranch(selectedBranch);

		for(GameVersionManager.GameVersion version : versions) {
			versionDropdown.addItem(formatVersionDisplay(version));
		}
	}

	private String formatVersionDisplay(GameVersionManager.GameVersion version) {
		return version.version + (version.build != null && !version.build.isEmpty() ? " (Build " + version.build + ")" : "");
	}

	private GameVersionManager.GameBranch mapIndexToBranch(int index) {
		switch(index) {
			case 1:
				return GameVersionManager.GameBranch.PRE_RELEASE;
			case 2:
				return GameVersionManager.GameBranch.DEV;
			default:
				return GameVersionManager.GameBranch.RELEASE;
		}
	}

	private JTextField createPortField() {
		JTextField field = new JTextField("4242");
		field.setColumns(6);
		field.setBackground(new Color(25, 25, 31));
		field.setForeground(Color.WHITE);
		field.setHorizontalAlignment(JTextField.CENTER);
		return field;
	}

	private JToggleButton createModeToggleButton() {
		JToggleButton toggleButton = new JToggleButton(LaunchMode.CLIENT.displayName);
		toggleButton.setBackground(new Color(25, 25, 31));
		toggleButton.setForeground(Color.WHITE);
		return toggleButton;
	}

	private JButton createLaunchButton() {
		JButton button = new JButton("Launch");
		button.setBackground(new Color(67, 128, 148)); // Accent blue
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		return button;
	}

	private void setupListeners() {
		// Branch dropdown listener
		branchDropdown.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				updateVersionDropdown();
			}
		});

		// Mode toggle listener
		modeToggleButton.addItemListener(e -> {
			LaunchMode currentMode = e.getStateChange() == ItemEvent.SELECTED ? LaunchMode.SERVER : LaunchMode.CLIENT;

			modeToggleButton.setText(currentMode.displayName);
			portField.setVisible(currentMode == LaunchMode.SERVER);

			// Trigger layout update
			revalidate();
			repaint();
		});

		// Launch button listener
		launchButton.addActionListener(e -> {
			try {
				// Get selected version
				String selectedVersion = (String) versionDropdown.getSelectedItem();

				// Determine launch mode
				boolean isServerMode = modeToggleButton.isSelected();

				// Get port if in server mode
				int port = isServerMode ? Integer.parseInt(portField.getText()) : -1;

				// TODO: Implement actual launch logic
				logger.info(String.format("Attempting to launch %s mode with version %s%s", isServerMode ? "Server" : "Client", selectedVersion, isServerMode ? " on port " + port : ""));

				// Placeholder for actual launch method
				// mainController.launchGame(selectedVersion, isServerMode, port);

			} catch(Exception ex) {
				logger.error("Failed to launch game", ex);
				JOptionPane.showMessageDialog(this, "Failed to launch game: " + ex.getMessage(), "Launch Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}