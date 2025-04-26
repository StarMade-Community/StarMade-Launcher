package smlauncher.ui.panels;

import smlauncher.ui.controllers.MainController;
import smlauncher.util.logging.LauncherLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;

/**
 * Base panel for navigation sections in the StarMade Launcher
 * Provides common functionality and styling for all navigation panels
 */
public abstract class BasePanel extends JPanel {
	// Main controller for inter-component communication
	protected final MainController mainController;

	// Logger for tracking events and errors
	protected final LauncherLogger logger;

	/**
	 * Constructor for BasePanel
	 * @param mainController Controller to manage launcher logic
	 */
	public BasePanel(MainController mainController) {
		this.mainController = mainController;
		this.logger = LauncherLogger.getInstance();

		// Set default styling
		setBackground(new Color(25, 25, 31)); // Dark background typical of launcher
		setLayout(new BorderLayout());
	}

	/**
	 * Open a URL in the default system browser
	 * @param url URL to open
	 */
	protected void openURL(String url) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				logger.warning("Desktop browsing not supported for URL: " + url);
			}
		} catch (Exception e) {
			logger.warning("Failed to open URL: " + url, e);
		}
	}

	/**
	 * Create a styled title label
	 * @param text Title text
	 * @return JLabel with title styling
	 */
	protected JLabel createTitleLabel(String text) {
		JLabel titleLabel = new JLabel(text, SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		return titleLabel;
	}

	/**
	 * Create a styled description text area
	 * @param text Description text
	 * @return JTextArea with description styling
	 */
	protected JTextArea createDescriptionArea(String text) {
		JTextArea descriptionArea = new JTextArea(text);
		descriptionArea.setEditable(false);
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setBackground(getBackground());
		descriptionArea.setForeground(Color.LIGHT_GRAY);
		descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
		descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		return descriptionArea;
	}

	/**
	 * Create a styled button with consistent appearance
	 * @param text Button text
	 * @param action Action to perform when button is clicked
	 * @return Styled JButton
	 */
	protected JButton createStyledButton(String text, java.awt.event.ActionListener action) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 12));
		button.setBackground(new Color(67, 128, 148)); // Deep blue-green
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.addActionListener(action);

		// Hover effects
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(87, 148, 168)); // Lighter shade on hover
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(67, 128, 148));
			}
		});

		return button;
	}
}