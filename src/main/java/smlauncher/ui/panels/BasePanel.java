package smlauncher.ui.panels;

import smlauncher.ui.controllers.MainController;
import smlauncher.util.LauncherLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;
import java.util.function.Consumer;

/**
 * Base panel for navigation sections in the StarMade Launcher
 * Provides common functionality, styling, and utility methods for all navigation panels
 */
public abstract class BasePanel extends JPanel {
	// Main controller for inter-component communication
	protected final MainController mainController;

	// Logger for tracking events and errors
	protected final LauncherLogger logger;

	// Color scheme for consistent styling
	protected static final class Colors {
		public static final Color BACKGROUND_DARK = new Color(25, 25, 31);
		public static final Color ACCENT_BLUE = new Color(67, 128, 148);
		public static final Color ACCENT_BLUE_LIGHT = new Color(87, 148, 168);
		public static final Color TEXT_LIGHT = Color.WHITE;
		public static final Color TEXT_MUTED = Color.LIGHT_GRAY;
	}

	// Font scheme for consistent typography
	protected static final class Fonts {
		public static final Font TITLE = new Font("Arial", Font.BOLD, 18);
		public static final Font SUBTITLE = new Font("Arial", Font.BOLD, 14);
		public static final Font BODY = new Font("Arial", Font.PLAIN, 12);
		public static final Font BUTTON = new Font("Arial", Font.BOLD, 12);
	}

	/**
	 * Constructor for BasePanel
	 * @param mainController Controller to manage launcher logic
	 */
	public BasePanel(MainController mainController) {
		this.mainController = mainController;
		this.logger = LauncherLogger.getInstance();

		// Set default styling
		setBackground(Colors.BACKGROUND_DARK);
		setLayout(new BorderLayout());
	}

	/**
	 * Open a URL in the default system browser with enhanced error handling
	 * @param url URL to open
	 * @param onError Optional error handler
	 */
	protected void openURL(String url, Consumer<Exception> onError) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				logger.warning("Desktop browsing not supported for URL: " + url);
				if (onError != null) {
					onError.accept(new UnsupportedOperationException("Desktop browsing not supported"));
				}
			}
		} catch (Exception e) {
			logger.warning("Failed to open URL: " + url, e);
			if (onError != null) {
				onError.accept(e);
			}
		}
	}

	/**
	 * Open a URL in the default system browser
	 * @param url URL to open
	 */
	protected void openURL(String url) {
		openURL(url, null);
	}

	/**
	 * Create a styled title label with more configuration options
	 * @param text Title text
	 * @param alignment Text alignment (SwingConstants)
	 * @param color Text color (optional)
	 * @return JLabel with title styling
	 */
	protected JLabel createTitleLabel(String text, int alignment, Color color) {
		JLabel titleLabel = new JLabel(text, alignment);
		titleLabel.setFont(Fonts.TITLE);
		titleLabel.setForeground(color != null ? color : Colors.TEXT_LIGHT);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		return titleLabel;
	}

	/**
	 * Create a styled title label with default alignment and color
	 * @param text Title text
	 * @return JLabel with title styling
	 */
	protected JLabel createTitleLabel(String text) {
		return createTitleLabel(text, SwingConstants.CENTER, null);
	}

	/**
	 * Create a styled description text area with more configuration options
	 * @param text Description text
	 * @param lineWrap Whether to wrap text
	 * @param color Text color (optional)
	 * @return JTextArea with description styling
	 */
	protected JTextArea createDescriptionArea(String text, boolean lineWrap, Color color) {
		JTextArea descriptionArea = new JTextArea(text);
		descriptionArea.setEditable(false);
		descriptionArea.setLineWrap(lineWrap);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setBackground(getBackground());
		descriptionArea.setForeground(color != null ? color : Colors.TEXT_MUTED);
		descriptionArea.setFont(Fonts.BODY);
		descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		return descriptionArea;
	}

	/**
	 * Create a styled description text area with default line wrapping
	 * @param text Description text
	 * @return JTextArea with description styling
	 */
	protected JTextArea createDescriptionArea(String text) {
		return createDescriptionArea(text, true, null);
	}

	/**
	 * Create a styled button with hover effects and action
	 * @param text Button text
	 * @param action Action to perform when button is clicked
	 * @param buttonColor Background color of the button (optional)
	 * @return Styled JButton
	 */
	protected JButton createStyledButton(String text, java.awt.event.ActionListener action, Color buttonColor) {
		JButton button = new JButton(text);
		button.setFont(Fonts.BUTTON);
		button.setBackground(buttonColor != null ? buttonColor : Colors.ACCENT_BLUE);
		button.setForeground(Colors.TEXT_LIGHT);
		button.setFocusPainted(false);
		button.addActionListener(action);

		// Hover effects
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(buttonColor != null
						? buttonColor.brighter()
						: Colors.ACCENT_BLUE_LIGHT);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(buttonColor != null
						? buttonColor
						: Colors.ACCENT_BLUE);
			}
		});

		return button;
	}

	/**
	 * Create a styled button with default blue color
	 * @param text Button text
	 * @param action Action to perform when button is clicked
	 * @return Styled JButton
	 */
	protected JButton createStyledButton(String text, java.awt.event.ActionListener action) {
		return createStyledButton(text, action, null);
	}

	/**
	 * Abstract method to be implemented by subclasses for panel initialization
	 */
	protected abstract void initializeComponents();
}