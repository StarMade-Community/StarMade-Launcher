package smlauncher.util.starmade;

public interface DownloadCallback {
	void downloaded(long size, long diff);

	void doneDownloading();
}
