package smlauncher.ui.controllers;

import smlauncher.manager.GameVersionManager;
import smlauncher.manager.LaunchConfigManager;
import smlauncher.util.JavaVersion;
import smlauncher.util.LauncherLogger;
import smlauncher.util.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Central controller for managing launcher interactions
 * Coordinates between configuration, version management, and UI components
 */
public class MainController {

	private static final String J23ARGS = "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED";

	// Core dependencies
	private final LaunchConfigManager configManager;
	private final GameVersionManager versionManager;
	private final LauncherLogger logger;

	/**
	 * Constructor
	 * @param configManager Manages launcher configuration
	 * @param versionManager Manages game versions
	 */
	public MainController(LaunchConfigManager configManager, GameVersionManager versionManager) {
		this.configManager = configManager;
		this.versionManager = versionManager;
		this.logger = LauncherLogger.getInstance();
	}

	/**
	 * Get the current installation directory
	 * @return Path to the installation directory
	 */
	public String getInstallDirectory() {
		return configManager.configData.getString("installDir");
	}

	/**
	 * Set a new installation directory
	 * @param directory New installation directory path
	 */
	public void setInstallDirectory(String directory) {
		File dir = new File(directory);
		if(dir.exists() && dir.isDirectory()) {
			configManager.configData.put("installDir", directory);
			configManager.saveConfiguration();
			logger.info("Installation directory updated to: " + directory);
		} else {
			logger.warning("Attempted to set invalid installation directory: " + directory);
			throw new IllegalArgumentException("Invalid directory: " + directory);
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
	 * Get the last used version from configuration
	 * @return Last used game version
	 */
	public GameVersionManager.GameVersion getLastUsedVersion() {
		String lastUsedVersionStr = configManager.configData.getString("lastUsedVersion");

		// If no last used version, return latest release
		if(lastUsedVersionStr == null || "NONE".equals(lastUsedVersionStr)) {
			return versionManager.getLatestVersion(GameVersionManager.GameBranch.RELEASE);
		}

		// Try to find the version
		return versionManager.findVersionByString(lastUsedVersionStr);
	}

	/**
	 * Update the last used version in configuration
	 * @param version Version to set as last used
	 */
	public void updateLastUsedVersion(GameVersionManager.GameVersion version) {
		configManager.configData.put("lastUsedVersion", version.version);
		configManager.saveConfiguration();
		logger.info("Updated last used version to: " + version);
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
	 * Check for available updates
	 * @param currentVersion Current installed version
	 * @return Optional containing the latest version if an update is available
	 */
	public Optional<GameVersionManager.GameVersion> checkForUpdates(GameVersionManager.GameVersion currentVersion) {
		GameVersionManager.GameVersion latestVersion = getLatestVersion(currentVersion.branch);

		if(latestVersion != null && versionManager.compareVersions(latestVersion, currentVersion) > 0) {
			return Optional.of(latestVersion);
		}

		return Optional.empty();
	}

	/**
	 * Prepare game installation
	 * @param version Game version to prepare
	 * @throws IOException If preparation fails
	 */
	public void prepareGameInstallation(GameVersionManager.GameVersion version) throws IOException {
		// Validate version
		if(!versionManager.validateVersion(version)) {
			throw new IllegalArgumentException("Invalid game version");
		}

		// Ensure installation directory exists
		File installDir = new File(getInstallDirectory());
		if(!installDir.exists() && !installDir.mkdirs()) {
			throw new IOException("Could not create installation directory");
		}

		// Verify Java runtime
		int requiredJavaVersion = getRequiredJavaVersion(version);
		if(!isJavaRuntimeAvailable(requiredJavaVersion)) {
			downloadJavaRuntime(requiredJavaVersion);
		}

		// TODO: Additional preparation steps
		// - Download game files
		// - Validate file integrity
		// - Prepare for potential backup
	}

	/**
	 * Launch the game
	 * @param version Game version to launch
	 * @param isServerMode Whether to launch in server mode
	 * @param serverPort Server port (if applicable)
	 * @throws IOException If game launch fails
	 */
	public void launchGame(GameVersionManager.GameVersion version, boolean isServerMode, int serverPort) throws IOException {
		// Validate version
		if(!versionManager.validateVersion(version)) {
			throw new IllegalArgumentException("Invalid game version");
		}

		// Prepare installation
		prepareGameInstallation(version);

		// Construct launch command
		ProcessBuilder processBuilder = createGameLaunchProcess(version, isServerMode, serverPort);

		try {
			// Start the game process
			Process gameProcess = processBuilder.start();

			// Log launch details
			logger.info(String.format("Launched game - Version: %s, Mode: %s, Port: %d", version, isServerMode ? "Server" : "Client", isServerMode ? serverPort : -1));

			// Optional: Monitor game process
			monitorGameProcess(gameProcess, version);
		} catch(IOException e) {
			logger.error("Failed to launch game", e);
			throw new IOException("Could not start game: " + e.getMessage(), e);
		}
	}

	/**
	 * Create process builder for launching the game
	 * @param version Game version
	 * @param isServerMode Server mode flag
	 * @param serverPort Server port
	 * @return Configured ProcessBuilder
	 */
	private ProcessBuilder createGameLaunchProcess(GameVersionManager.GameVersion version, boolean isServerMode, int serverPort) {
		// Construct launch command
		List<String> command = buildLaunchCommand(version, isServerMode, serverPort);

		// Create process builder
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(new File(getInstallDirectory()));
		processBuilder.inheritIO(); // Inherit input/output streams

		return processBuilder;
	}

	/**
	 * Build the launch command for the game
	 * @param version Game version
	 * @param isServerMode Server mode flag
	 * @param serverPort Server port
	 * @return List of command arguments
	 */
	private List<String> buildLaunchCommand(GameVersionManager.GameVersion version, boolean isServerMode, int serverPort) {
		List<String> command = new ArrayList<>();
		command.add(getJavaPath(version, OperatingSystem.getCurrent()));
		if(!version.version.startsWith("0.2") && !version.version.startsWith("0.1")) {
			command.add(J23ARGS);
		}

		if(OperatingSystem.getCurrent() == OperatingSystem.MAC) {
			command.add("-XstartOnFirstThread");
		}

		command.add("-jar");
		command.add("StarMade.jar");

		if(isServerMode) {
			command.add("-server");
			command.add("-port:" + serverPort);
		} else {
			command.add("-force");
		}

		return command;
	}

	private static String getJavaPath(GameVersionManager.GameVersion gameVersion, OperatingSystem currentOS) {
		if(gameVersion.version.startsWith("0.2") || gameVersion.version.startsWith("0.1")) {
			return String.format(currentOS.javaPath, 8);
		} else {
			return String.format(currentOS.javaPath, 23);
		}
	}

	private static JavaVersion getJavaVersion(GameVersionManager.GameVersion gameVersion) {
		if(gameVersion.version.startsWith("0.2") || gameVersion.version.startsWith("0.1")) return JavaVersion.JAVA_8;
		else return JavaVersion.JAVA_23;
	}

	public ArrayList<String> getCommandComponents(GameVersionManager.GameVersion gameVersion, boolean server, int port) {
		ArrayList<String> commandComponents = new ArrayList<>();
		OperatingSystem currentOS = OperatingSystem.getCurrent();
		commandComponents.add(getJavaPath(gameVersion, currentOS));
		if(!gameVersion.version.startsWith("0.2") && !gameVersion.version.startsWith("0.1")) {
			commandComponents.add(J23ARGS);
		}

		if(currentOS == OperatingSystem.MAC) {
			// Run OpenGL on main thread on macOS
			// Needs to be added before "-jar"
			commandComponents.add("-XstartOnFirstThread");
		}

		if(currentOS == OperatingSystem.LINUX) {
			// Override (meaningless?) default library path
			commandComponents.add("-Djava.library.path=lib:native/linux");
		}

		commandComponents.add("-jar");
		commandComponents.add("StarMade.jar");

		// Memory Arguments
		if(!configManager.configData.getString("launchArgs").isEmpty()) {
			String[] launchArgs = configManager.configData.getString("launchArgs").split(" ");
			for(String arg : launchArgs) {
				if(arg.startsWith("-Xms") || arg.startsWith("-Xmx")) continue;
				commandComponents.add(arg.trim());
			}
		}
		commandComponents.add("-Xms1024m");
		commandComponents.add("-Xmx" + configManager.configData.getInt("memory") + "m");

		// Game arguments
		commandComponents.add("-force");
		if(server) {
			commandComponents.add("-server");
			commandComponents.add("-port:" + port);
		}
		return commandComponents;
	}

	/**
	 * Check if Java runtime is available
	 * @param javaVersion Required Java version
	 * @return true if runtime is available, false otherwise
	 */
	private boolean isJavaRuntimeAvailable(int javaVersion) {
		File runtimeDir = new File(getInstallDirectory(), String.format("jre%d", javaVersion));
		return runtimeDir.exists() && runtimeDir.isDirectory();
	}

	/**
	 * Download required Java runtime
	 * @param javaVersion Java version to download
	 * @throws IOException If download fails
	 */
	private void downloadJavaRuntime(int javaVersion) throws IOException {
		// TODO: Implement Java runtime download logic
		logger.info("Downloading Java runtime version " + javaVersion);
		throw new UnsupportedOperationException("Java runtime download not implemented");
	}

	/**
	 * Monitor the game process
	 * @param gameProcess Process to monitor
	 * @param version Game version
	 */
	private void monitorGameProcess(Process gameProcess, GameVersionManager.GameVersion version) {
		new Thread(() -> {
			try {
				int exitCode = gameProcess.waitFor();
				logger.info("Game process for version " + version + " exited with code " + exitCode);

				// Handle potential error conditions
				if(exitCode != 0) {
					logger.warning("Game terminated with non-zero exit code: " + exitCode);
				}
			} catch(InterruptedException e) {
				logger.error("Game process monitoring interrupted", e);
			}
		}).start();
	}
}