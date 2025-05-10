package smlauncher.ui;

/**
 * Interface for handling navigation events in the launcher.
 */
public interface NavigationHandler {
	/**
	 * Called when a navigation item is selected.
	 *
	 * @param index The index of the selected navigation item
	 */
	void onNavigate(int index);
}