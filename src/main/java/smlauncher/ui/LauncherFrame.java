package smlauncher.ui;

import smlauncher.ui.components.LeftSidePanel;
import smlauncher.ui.components.TopPanel;
import smlauncher.ui.components.FooterPanel;
import smlauncher.ui.controllers.MainController;
import smlauncher.util.logging.LauncherLogger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Main application frame for the StarMade Launcher
 */
public class LauncherFrame extends JFrame {
	private final MainController mainController;
	private final LauncherLogger logger;

	// UI Components
	private TopPanel topPanel;
	private LeftSidePanel leftSidePanel;
	private FooterPanel footerPanel;
	private JPanel centerPanel;

	/**
	 * Constructor for LauncherFrame
	 * @param mainController Controller to manage launcher logic
	 */
	public LauncherFrame(MainController mainController) {
		this.mainController = mainController;
		this.logger = LauncherLogger.getInstance();

		// Configure basic frame properties
		configureFrame();

		// Initialize UI components
		initializeComponents();

		// Set up layout
		setupLayout();

		// Final frame configuration
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Configure basic frame properties
	 */
	private void configureFrame() {
		// Set basic frame properties
		setTitle("StarMade Launcher");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Set minimum size
		setMinimumSize(new Dimension(800, 600));

		// Set icon
		try {
			setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/sprites/icon.png"))).getImage());
		} catch (Exception e) {
			logger.warning("Failed to set launcher icon", e);
		}
	}

	/**
	 * Initialize UI components
	 */
	private void initializeComponents() {
		// Create top panel (includes close/minimize buttons)
		topPanel = new TopPanel(this);

		// Create left side panel (navigation)
		leftSidePanel = new LeftSidePanel(mainController);

		// Create center panel for main content
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.DARK_GRAY);

		// Create footer panel
		footerPanel = new FooterPanel(mainController);
	}

	/**
	 * Set up frame layout
	 */
	private void setupLayout() {
		// Use BorderLayout for main frame
		setLayout(new BorderLayout());

		// Add components to frame
		add(topPanel, BorderLayout.NORTH);
		add(leftSidePanel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);
	}

	/**
	 * Set the content for the center panel
	 * @param content JPanel to display in center
	 */
	public void setCenterContent(JPanel content) {
		centerPanel.removeAll();
		centerPanel.add(content, BorderLayout.CENTER);
		centerPanel.revalidate();
		centerPanel.repaint();
	}
}