package smlauncher.util.starmade;

public interface FileDowloadCallback {
	void update(FileDownloadUpdate u);

	void update(String u);

	void done(FileDownloadUpdate u);
}
