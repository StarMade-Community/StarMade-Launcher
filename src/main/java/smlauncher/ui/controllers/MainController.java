package smlauncher.ui.controllers;

import smlauncher.core.config.LaunchConfigManager;
import smlauncher.core.model.GameVersionManager;
import smlauncher.util.logging.LauncherLogger;

import java.io.File;
import java.util.List;

/**
 * Main controller responsible for coordinating actions between
 * configuration, version management, and UI components
 */
public class MainController {
	private final LaunchConfigManager configManager;
	private final GameVersionManager versionManager;
	private final LauncherLogger logger;

	public MainController(LaunchConfigManager configManager,
	                      GameVersionManager versionManager) {
		this.configManager = configManager;
		this.versionManager = versionManager;
		this.logger = LauncherLogger.getInstance();
	}

	/**
	 * Get the current installation directory
	 * @return Path to the installation directory
	 */
	public String getInstallDirectory() {
		return configManager.getInstallDir();
	}

	/**
	 * Set a new installation directory
	 * @param directory New installation directory path
	 */
	public void setInstallDirectory(String directory) {
		File dir = new File(directory);
		if (dir.exists() && dir.isDirectory()) {
			configManager.setInstallDir(directory);
			logger.info("Installation directory updated to: " + directory);
		} else {
			logger.warning("Attempted to set invalid installation directory: " + directory);
		}
	}

	/**
	 * Load versions for a specific game branch
	 * @param branch Game branch to load versions for
	 * @return List of game versions
	 */
	public List<GameVersionManager.GameVersion> loadVersionsForBranch(GameVersionManager.GameBranch branch) {
		logger.debug("Loading versions for branch: " + branch.name);
		return versionManager.loadVersionsForBranch(branch);
	}

	/**
	 * Get the latest version for a specific branch
	 * @param branch Game branch
	 * @return Latest game version
	 */
	public GameVersionManager.GameVersion getLatestVersion(GameVersionManager.GameBranch branch) {
		return versionManager.getLatestVersion(branch);
	}

	/**
	 * Determine the required Java version for a specific game version
	 * @param version Game version
	 * @return Required Java version (8 or 23)
	 */
	public int getRequiredJavaVersion(GameVersionManager.GameVersion version) {
		return versionManager.getRequiredJavaVersion(version);
	}

	/**
	 * Update the last used version in configuration
	 * @param version Version to set as last used
	 */
	public void updateLastUsedVersion(GameVersionManager.GameVersion version) {
		configManager.setLastUsedVersion(version.version);
		logger.info("Updated last used version to: " + version);
	}

	/**
	 * Get the last used version from configuration
	 * @return Last used game version
	 */
	public String getLastUsedVersion() {
		return configManager.getLastUsedVersion();
	}

	// TODO: Add methods for:
	// - Launching the game
	// - Updating the game
	// - Managing server configurations
	// - Handling download/update processes
}