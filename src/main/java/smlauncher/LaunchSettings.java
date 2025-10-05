package smlauncher;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import smlauncher.util.OperatingSystem;

import java.io.File;
import java.io.IOException;

/**
 * Stores launch settings and manages the settings file.
 *
 * @author SlavSquatSuperstar
 */
public final class LaunchSettings {

	private static JSONObject launchSettings;

	// Settings File Methods

	public static void readSettings() {
		File jsonFile = new File(getSettingsFilePath());
		JSONObject defaultSettings = getDefaultLaunchSettings();

		// Create file if not present
		if(!jsonFile.exists()) {
			launchSettings = defaultSettings;
			LogManager.initialize();
			saveSettings();
		} else {
			// Read the settings file
			try {
				launchSettings = new JSONObject(FileUtils.readFileToString(jsonFile, "UTF-8"));
			} catch(IOException exception) {
				LogManager.logException("Could not read launch settings from file!", exception);
			}
		}
		File starMadeDir = new File(getInstallDir());
		if(!starMadeDir.exists()) starMadeDir.mkdirs();
	}

	public static void saveSettings() {
		File settingsFile = new File(getSettingsFilePath());
		try {
			settingsFile.createNewFile();
			FileUtils.write(settingsFile, launchSettings.toString(4), "UTF-8");
		} catch(IOException exception) {
			LogManager.logException("Could not save launch settings to file!", exception);
		}
	}

	private static JSONObject getDefaultLaunchSettings() {
		JSONObject settings = new JSONObject();
		String installDir;
		try {
			String cwd = new File(".").getAbsolutePath();
			if (OperatingSystem.getCurrent().equals(OperatingSystem.MAC) && (cwd.endsWith("/Contents/Resources") || cwd.endsWith("\\Contents\\Resources"))) {
				// Running inside a .app bundle on macOS, set installDir to parent of .app
				File resourcesDir = new File(cwd);
				File contentsDir = resourcesDir.getParentFile();
				File appDir = contentsDir.getParentFile();
				File parentOfApp = appDir.getParentFile();
				installDir = parentOfApp.getAbsolutePath();
			} else {
				installDir = cwd;
			}
		} catch (Exception e) {
			installDir = new File("StarMade").getAbsolutePath();
		}
		settings.put("installDir", installDir);
		settings.put("jvm_args", "");
		settings.put("lastUsedBranch", 0); // Release
		settings.put("lastUsedVersion", "NONE");
		settings.put("launchArgs", "");
		settings.put("memory", 8192);
		return settings;
	}

	private static String getSettingsFilePath() {
		String cwd = new File(".").getAbsolutePath();
		if (OperatingSystem.getCurrent().equals(OperatingSystem.MAC) && (cwd.endsWith("/Contents/Resources") || cwd.endsWith("\\Contents\\Resources"))) {
			File resourcesDir = new File(cwd);
			File contentsDir = resourcesDir.getParentFile();
			File appDir = contentsDir.getParentFile();
			File parentOfApp = appDir.getParentFile();
			return new File(parentOfApp, "launch-settings.json").getAbsolutePath();
		} else {
			return new File("launch-settings.json").getAbsolutePath();
		}
	}

	// Settings Getters and Setters
	public static String getInstallDir() {
		return launchSettings.getString("installDir");
	}

	public static void setInstallDir(String installDir) {
		launchSettings.put("installDir", installDir);
	}

	public static String getJvmArgs() {
		return launchSettings.getString("jvm_args");
	}

	public static void setJvmArgs(String jvmArgs) {
		launchSettings.put("jvm_args", jvmArgs);
	}

	public static String getLaunchArgs() {
		return launchSettings.getString("launchArgs");
	}

	public static void setLaunchArgs(String launchArgs) {
		launchSettings.put("launchArgs", launchArgs);
	}

	public static int getLastUsedBranch() {
		return launchSettings.getInt("lastUsedBranch");
	}

	public static void setLastUsedBranch(int lastUsedBranch) {
		launchSettings.put("lastUsedBranch", lastUsedBranch);
	}

	public static String getLastUsedVersion() {
		return launchSettings.getString("lastUsedVersion");
	}

	public static void setLastUsedVersion(String lastUsedVersion) {
		launchSettings.put("lastUsedVersion", lastUsedVersion);
	}

	public static int getMemory() {
		return launchSettings.getInt("memory");
	}

	public static void setMemory(int memory) {
		launchSettings.put("memory", memory);
	}
}
