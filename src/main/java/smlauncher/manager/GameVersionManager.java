package smlauncher.manager;

import smlauncher.util.LauncherLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages game versions and branches
 */
public class GameVersionManager {

	// Enum to represent game branches
	public enum GameBranch {
		RELEASE("Release", "http://files.star-made.org/releasebuildindex"),
		DEV("Development", "http://files.star-made.org/devbuildindex"),
		PRE_RELEASE("Pre-Release", "http://files.star-made.org/prebuildindex"),
		ARCHIVE("Archive", "http://files.star-made.org/archivebuildindex");

		public final String name;
		public final String versionIndexUrl;

		GameBranch(String name, String versionIndexUrl) {
			this.name = name;
			this.versionIndexUrl = versionIndexUrl;
		}
	}

	public boolean validateVersion(GameVersion version) {
		if(version == null) {
			return false;
		}
		if(version.version == null || version.version.isEmpty()) {
			return false;
		}
		if(version.build == null || version.build.isEmpty()) {
			return false;
		}
		if(version.branch == null) {
			return false;
		}
		return version.path != null && !version.path.isEmpty();
	}

	public int compareVersions(GameVersion latestVersion, GameVersion currentVersion) {
		if(latestVersion == null || currentVersion == null) {
			return 0;
		}
		if(latestVersion.branch != currentVersion.branch) {
			return 0;
		}
		if(latestVersion.version.equals(currentVersion.version)) {
			return 0;
		}
		if(latestVersion.build.equals(currentVersion.build)) {
			return 0;
		}
		return latestVersion.compareTo(currentVersion);
	}

	public GameVersion findVersionByString(String lastUsedVersionStr) {
		for(GameVersion version : releaseVersions) {
			if(version.version.equals(lastUsedVersionStr)) {
				return version;
			}
		}
		for(GameVersion version : devVersions) {
			if(version.version.equals(lastUsedVersionStr)) {
				return version;
			}
		}
		for(GameVersion version : preReleaseVersions) {
			if(version.version.equals(lastUsedVersionStr)) {
				return version;
			}
		}
		return null;
	}

	/**
	 * Represents a specific game version entry
	 */
	public static class GameVersion implements Comparable<GameVersion> {
		public final String version;
		public final String build;
		public final GameBranch branch;
		public final String path;

		public GameVersion(String version, String build, GameBranch branch, String path) {
			this.version = version;
			this.build = build;
			this.branch = branch;
			this.path = path;
		}

		@Override
		public int compareTo(GameVersion other) {
			return this.build.compareTo(other.build);
		}

		@Override
		public String toString() {
			return String.format("%s v%s (%s)", build, version, branch.name);
		}
	}

	private final LaunchConfigManager configManager;
	private final LauncherLogger logger = LauncherLogger.getInstance();

	// Cached version lists for each branch
	private final List<GameVersion> releaseVersions = new ArrayList<>();
	private final List<GameVersion> devVersions = new ArrayList<>();
	private final List<GameVersion> preReleaseVersions = new ArrayList<>();

	public GameVersionManager(LaunchConfigManager configManager) {
		this.configManager = configManager;
	}

	/**
	 * Load versions for a specific branch
	 * @param branch Game branch to load versions for
	 * @return List of versions for the branch
	 */
	public List<GameVersion> loadVersionsForBranch(GameBranch branch) {
		List<GameVersion> versions = new ArrayList<>();
		try {
			URL url = new URL(branch.versionIndexUrl);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setRequestProperty("User-Agent", "StarMade-Launcher");

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

				String line;
				while((line = reader.readLine()) != null) {
					versions.add(parseVersionEntry(line, branch));
				}

				// Sort versions in descending order
				Collections.sort(versions, Collections.reverseOrder());
			}
		} catch(Exception e) {
			logger.warning("Failed to load versions for branch " + branch.name, e);
		}

		// Cache versions based on branch
		switch(branch) {
			case RELEASE:
				releaseVersions.clear();
				releaseVersions.addAll(versions);
				break;
			case DEV:
				devVersions.clear();
				devVersions.addAll(versions);
				break;
			case PRE_RELEASE:
				preReleaseVersions.clear();
				preReleaseVersions.addAll(versions);
				break;
		}

		return versions;
	}

	/**
	 * Parse a version entry from the index file
	 * @param line Raw version entry line
	 * @param branch Branch of the version
	 * @return Parsed GameVersion
	 */
	private GameVersion parseVersionEntry(String line, GameBranch branch) {
		// Split the line into version/path components
		String[] versionAndPath = line.split(" ", 2);

		// Split version into version and build
		String[] versionComponents = versionAndPath[0].split("#", 2);
		String version = versionComponents[0];
		String build = versionComponents.length > 1 ? versionComponents[1] : "";

		// Extract path
		String path = versionAndPath[1];

		return new GameVersion(version, build, branch, path);
	}

	/**
	 * Get the latest version for a specific branch
	 * @param branch Game branch
	 * @return Latest version, or null if no versions found
	 */
	public GameVersion getLatestVersion(GameBranch branch) {
		List<GameVersion> versions = loadVersionsForBranch(branch);
		return versions.isEmpty() ? null : versions.get(0);
	}

	/**
	 * Determine the Java version required for a specific game version
	 * @param version Game version
	 * @return Required Java version (8 or 23)
	 */
	public int getRequiredJavaVersion(GameVersion version) {
		// Versions below 0.300.100 use Java 8, newer versions use Java 23
		return version.version.compareTo("0.300.100") < 0 ? 8 : 23;
	}
}