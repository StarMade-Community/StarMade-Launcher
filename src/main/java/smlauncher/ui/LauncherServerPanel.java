package smlauncher.ui;

import smlauncher.StarMadeLauncher;
import smlauncher.starmade.IndexFileEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel for the server mode of the launcher.
 * Contains the launch and update buttons for the dedicated server.
 */
public class LauncherServerPanel extends JPanel {
	private final StarMadeLauncher launcher;
	private JButton launchButton;
	private JButton updateButton;
	private LaunchButtonListener launchButtonListener;
	private UpdateButtonListener updateButtonListener;

	public LauncherServerPanel(StarMadeLauncher launcher) {
		super(true);
		this.launcher = launcher;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new BorderLayout());
		initialize();
	}

	private void initialize() {
		// Create the buttons panel
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setDoubleBuffered(true);
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(buttonsPanel, BorderLayout.SOUTH);

		// Create the launch button
		launchButton = new JButton();
		launchButton.setDoubleBuffered(true);
		launchButton.setIcon(StarMadeLauncher.getIcon("sprites/launch_btn.png"));
		launchButton.setRolloverIcon(StarMadeLauncher.getIcon("sprites/launch_roll.png"));
		launchButton.setBorderPainted(false);
		launchButton.setContentAreaFilled(false);
		launchButton.setFocusPainted(false);
		launchButton.addActionListener(e -> {
			if(launchButtonListener != null) {
				launchButtonListener.onLaunch();
			}
		});
		launchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				launchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				launchButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		// Create the update button
		updateButton = new JButton();
		updateButton.setDoubleBuffered(true);
		updateButton.setIcon(StarMadeLauncher.getIcon("sprites/update_btn.png"));
		updateButton.setRolloverIcon(StarMadeLauncher.getIcon("sprites/update_roll.png"));
		updateButton.setBorderPainted(false);
		updateButton.setContentAreaFilled(false);
		updateButton.setFocusPainted(false);
		updateButton.addActionListener(e -> {
			if(updateButtonListener != null) {
				updateButtonListener.onUpdate(null); // The version will be determined by the listener
			}
		});
		updateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				updateButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		// Add the buttons to the panel
		buttonsPanel.add(updateButton);
		buttonsPanel.add(launchButton);
	}

	/**
	 * Set the launch button listener for this panel.
	 *
	 * @param listener The listener to use
	 */
	public void setLaunchButtonListener(LaunchButtonListener listener) {
		launchButtonListener = listener;
	}

	/**
	 * Set the update button listener for this panel.
	 *
	 * @param listener The listener to use
	 */
	public void setUpdateButtonListener(UpdateButtonListener listener) {
		updateButtonListener = listener;
	}

	/**
	 * Get the launch button.
	 *
	 * @return The launch button
	 */
	public JButton getLaunchButton() {
		return launchButton;
	}

	/**
	 * Get the update button.
	 *
	 * @return The update button
	 */
	public JButton getUpdateButton() {
		return updateButton;
	}

	/**
	 * Update the buttons based on whether an update is needed.
	 *
	 * @param needsUpdate Whether an update is needed
	 */
	public void updateButtons(boolean needsUpdate) {
		if(needsUpdate) {
			launchButton.setEnabled(false);
			updateButton.setEnabled(true);
		} else {
			launchButton.setEnabled(true);
			updateButton.setEnabled(false);
		}
	}

	/**
	 * Interface for handling launch button events.
	 */
	public interface LaunchButtonListener {
		/**
		 * Called when the launch button is clicked.
		 */
		void onLaunch();
	}

	/**
	 * Interface for handling update button events.
	 */
	public interface UpdateButtonListener {
		/**
		 * Called when the update button is clicked.
		 *
		 * @param version The version to update to
		 */
		void onUpdate(IndexFileEntry version);
	}
}