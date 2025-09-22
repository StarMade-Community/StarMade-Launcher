package smlauncher.starmade;

public record IndexFileEntry(String version, String build, GameBranch branch, String path) implements Comparable<IndexFileEntry> {

	public static IndexFileEntry create(String line, GameBranch branch) {
		try {
			String[] versionAndPath = line.split(" ", 2);
			String[] versionAndBuild = versionAndPath[0].split("#", 2);
			String version = versionAndBuild[0];
			String build = (versionAndBuild.length == 2) ? versionAndBuild[1] : "";
			String path = versionAndPath[1];
			return new IndexFileEntry(version, build, branch, path);
		} catch(Exception ignored) {
			return null;
		}
	}

	@Override
	public int compareTo(IndexFileEntry other) {
		return build.compareToIgnoreCase(other.build);
	}

	@Override
	public String toString() {
		return String.format("%s v%s (%s)", build, version, branch.name());
	}
}