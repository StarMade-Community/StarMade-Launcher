package smlauncher.ui;

import smlauncher.StarMadeLauncher;
import smlauncher.starmade.StackLayout;
import smlauncher.util.Palette;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for the footer of the launcher.
 * Contains the play and dedicated server buttons.
 */
public class LauncherFooterPanel extends JPanel {
	private final StarMadeLauncher launcher;
	private JLabel footerLabel;
	private JPanel playPanel;
	private JPanel serverPanel;
	private boolean serverMode;
	private ModeChangeListener modeChangeListener;

	public LauncherFooterPanel(StarMadeLauncher launcher) {
		super(true);
		this.launcher = launcher;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new StackLayout());
		initialize();
	}

	private void initialize() {
		footerLabel = new JLabel();
		footerLabel.setDoubleBuffered(true);
		footerLabel.setIcon(StarMadeLauncher.getIcon("sprites/footer_normalplay_bg.jpg"));
		add(footerLabel);

		JPanel footerPanelButtons = new JPanel();
		footerPanelButtons.setDoubleBuffered(true);
		footerPanelButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		footerPanelButtons.setOpaque(false);
		footerPanelButtons.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton normalPlayButton = new JButton("Play");
		normalPlayButton.setFont(new Font("Roboto", Font.BOLD, 12));
		normalPlayButton.setDoubleBuffered(true);
		normalPlayButton.setOpaque(false);
		normalPlayButton.setContentAreaFilled(false);
		normalPlayButton.setBorderPainted(false);
		normalPlayButton.setForeground(Palette.textColor);
		normalPlayButton.addActionListener(e -> switchToClientMode());

		JButton dedicatedServerButton = new JButton("Dedicated Server");
		dedicatedServerButton.setFont(new Font("Roboto", Font.BOLD, 12));
		dedicatedServerButton.setDoubleBuffered(true);
		dedicatedServerButton.setOpaque(false);
		dedicatedServerButton.setContentAreaFilled(false);
		dedicatedServerButton.setBorderPainted(false);
		dedicatedServerButton.setForeground(Palette.textColor);
		dedicatedServerButton.addActionListener(e -> switchToServerMode());

		footerPanelButtons.add(normalPlayButton);
		footerPanelButtons.add(Box.createRigidArea(new Dimension(30, 0)));
		footerPanelButtons.add(dedicatedServerButton);
		footerLabel.add(footerPanelButtons);
		footerPanelButtons.setBounds(0, 0, 800, 30);
	}

	/**
	 * Set the mode change listener for this panel.
	 *
	 * @param listener The listener to use
	 */
	public void setModeChangeListener(ModeChangeListener listener) {
		modeChangeListener = listener;
	}

	/**
	 * Switch to client mode.
	 */
	public void switchToClientMode() {
		if(serverMode) {
			serverMode = false;
			footerLabel.setIcon(StarMadeLauncher.getIcon("sprites/footer_normalplay_bg.jpg"));
			if(modeChangeListener != null) {
				modeChangeListener.onModeChange(false);
			}
		}
	}

	/**
	 * Switch to server mode.
	 */
	public void switchToServerMode() {
		if(!serverMode) {
			serverMode = true;
			footerLabel.setIcon(StarMadeLauncher.getIcon("sprites/footer_dedicated_bg.jpg"));
			if(modeChangeListener != null) {
				modeChangeListener.onModeChange(true);
			}
		}
	}

	/**
	 * Get the footer label.
	 *
	 * @return The footer label
	 */
	public JLabel getFooterLabel() {
		return footerLabel;
	}

	/**
	 * Check if the panel is in server mode.
	 *
	 * @return True if in server mode, false otherwise
	 */
	public boolean isServerMode() {
		return serverMode;
	}

	/**
	 * Interface for handling mode changes in the footer panel.
	 */
	public interface ModeChangeListener {
		/**
		 * Called when the mode is changed.
		 *
		 * @param serverMode True if server mode is selected, false for client mode
		 */
		void onModeChange(boolean serverMode);
	}
}