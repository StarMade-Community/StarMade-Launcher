package smlauncher;

import smlauncher.core.config.LaunchConfigManager;
import smlauncher.core.model.GameVersionManager;
import smlauncher.ui.LauncherFrame;
import smlauncher.ui.controllers.MainController;
import smlauncher.util.logging.LauncherLogger;

import javax.swing.*;

/**
 * Main entry point for the StarMade Launcher application.
 *
 * Responsibilities:
 * - Initialize core application components
 * - Set up logging
 * - Configure and launch the main UI
 * - Handle application-wide initialization
 */
public class StarMadeLauncher {
	// Application-wide configuration manager
	private final LaunchConfigManager configManager;

	// Version management for game versions
	private final GameVersionManager versionManager;

	// Main UI frame
	private LauncherFrame launcherFrame;

	// Main controller to coordinate UI and business logic
	private final MainController mainController;

	// Logger for application-wide logging
	private static final LauncherLogger logger = LauncherLogger.getInstance();

	/**
	 * Private constructor to enforce singleton-like initialization
	 */
	private StarMadeLauncher() {
		// Initialize core components
		configManager = new LaunchConfigManager();
		versionManager = new GameVersionManager(configManager);
		mainController = new MainController(configManager, versionManager);
	}

	/**
	 * Initialize and start the launcher
	 */
	private void initialize() {
		try {
			// Set up Look and Feel
			UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
		} catch (Exception e) {
			logger.warning("Failed to set Look and Feel", e);
		}

		// Create and configure the main UI frame
		SwingUtilities.invokeLater(() -> {
			try {
				launcherFrame = new LauncherFrame(mainController);
				launcherFrame.setVisible(true);
			} catch (Exception e) {
				logger.fatal("Failed to initialize launcher UI", e);
				System.exit(1);
			}
		});
	}

	/**
	 * Main entry point for the StarMade Launcher
	 *
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		// Set up uncaught exception handler
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
				logger.fatal("Uncaught exception in thread " + thread.getName(), throwable)
		);

		// Parse and handle command-line arguments
		handleCommandLineArgs(args);

		// Create and initialize the launcher
		StarMadeLauncher launcher = new StarMadeLauncher();
		launcher.initialize();
	}

	/**
	 * Handle command-line arguments for the launcher
	 *
	 * @param args Command-line arguments
	 */
	private static void handleCommandLineArgs(String[] args) {
		// TODO: Implement argument parsing
		// Potential flags:
		// -debug: Enable debug logging
		// -server: Start in server mode
		// -version: Select specific game version
		// -branch: Select specific game branch
		for (String arg : args) {
			switch (arg) {
				case "-debug":
					logger.setDebugMode(true);
					break;
				// Add more argument handling as needed
			}
		}
	}
}