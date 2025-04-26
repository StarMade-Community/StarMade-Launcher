package smlauncher.core.config;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages launcher configuration settings
 */
public class LaunchConfigManager {
	
	private static final String CONFIG_FILE_PATH = "./StarMade/launch-settings.json";
	private JSONObject configData;

	public LaunchConfigManager() {
		loadConfiguration();
	}

	/**
	 * Load configuration from file, or create default if not exists
	 */
	private void loadConfiguration() {
		try {
			// Ensure directory exists
			new File(CONFIG_FILE_PATH).getParentFile().mkdirs();

			// Try to read existing config
			File configFile = new File(CONFIG_FILE_PATH);
			if (configFile.exists()) {
				String content = new String(Files.readAllBytes(Paths.get(CONFIG_FILE_PATH)));
				configData = new JSONObject(content);
			} else {
				// Create default configuration
				configData = createDefaultConfiguration();
				saveConfiguration();
			}
		} catch (IOException e) {
			// Fallback to default configuration
			configData = createDefaultConfiguration();
		}
	}

	/**
	 * Create a default configuration
	 * @return Default configuration JSONObject
	 */
	private JSONObject createDefaultConfiguration() {
		JSONObject defaults = new JSONObject();
		defaults.put("installDir", new File("StarMade/StarMade").getAbsolutePath());
		defaults.put("jvmArgs", "");
		defaults.put("lastUsedBranch", 0); // Release
		defaults.put("lastUsedVersion", "NONE");
		defaults.put("launchArgs", "");
		defaults.put("memory", 8192);
		return defaults;
	}

	/**
	 * Save current configuration to file
	 */
	public void saveConfiguration() {
		try {
			Files.write(Paths.get(CONFIG_FILE_PATH),
					configData.toString(4).getBytes());
		} catch (IOException e) {
			// Log error - we'll add proper logging later
			e.printStackTrace();
		}
	}

	// Getter methods for configuration values
	public String getInstallDir() {
		return configData.getString("installDir");
	}

	public void setInstallDir(String dir) {
		configData.put("installDir", dir);
		saveConfiguration();
	}

	public String getLastUsedVersion() {
		return configData.getString("lastUsedVersion");
	}

	public void setLastUsedVersion(String version) {
		configData.put("lastUsedVersion", version);
		saveConfiguration();
	}

	// Add more getter and setter methods as needed
}