package smlauncher.ui;

import smlauncher.LaunchSettings;
import smlauncher.StarMadeLauncher;
import smlauncher.starmade.StackLayout;
import smlauncher.util.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Panel for the top header of the launcher.
 * Contains the close and minimize buttons.
 */
public class LauncherHeaderPanel extends JPanel {
	private final StarMadeLauncher launcher;
	private int mouseX;
	private int mouseY;
	private JLabel launcherVersionLabel;

	public LauncherHeaderPanel(StarMadeLauncher launcher) {
		super(true);
		this.launcher = launcher;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new StackLayout());
		initialize();
	}

	private void initialize() {
		JLabel topLabel = new JLabel();
		topLabel.setDoubleBuffered(true);
		topLabel.setIcon(StarMadeLauncher.getIcon("sprites/header_top.png"));
		add(topLabel);

		launcherVersionLabel = new JLabel();
		launcherVersionLabel.setDoubleBuffered(true);
		launcherVersionLabel.setFont(new Font("Roboto", Font.BOLD, 12));
		launcherVersionLabel.setForeground(Palette.textColor);
		launcherVersionLabel.setText("StarMade Launcher v" + StarMadeLauncher.LAUNCHER_VERSION);
		launcherVersionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		launcherVersionLabel.setVerticalAlignment(SwingConstants.TOP);
		launcherVersionLabel.setBorder(BorderFactory.createEmptyBorder(60, 10, 0, 0));
		add(launcherVersionLabel);

		JPanel topPanelButtons = new JPanel();
		topPanelButtons.setDoubleBuffered(true);
		topPanelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		topPanelButtons.setOpaque(false);

		JButton closeButton = new JButton(null, StarMadeLauncher.getIcon("sprites/close_icon.png"));
		closeButton.setDoubleBuffered(true);
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);
		closeButton.addActionListener(e -> {
			launcher.dispose();
			System.exit(0);
		});

		JButton minimizeButton = new JButton(null, StarMadeLauncher.getIcon("sprites/minimize_icon.png"));
		minimizeButton.setDoubleBuffered(true);
		minimizeButton.setOpaque(false);
		minimizeButton.setContentAreaFilled(false);
		minimizeButton.setBorderPainted(false);
		minimizeButton.addActionListener(e -> launcher.setState(Frame.ICONIFIED));

		topPanelButtons.add(minimizeButton);
		topPanelButtons.add(closeButton);
		topLabel.add(topPanelButtons);
		topPanelButtons.setBounds(0, 0, 800, 30);

		// Use top panel to drag the window
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				// If the mouse is on the top panel buttons, don't drag the window
				if(mouseX > 770 || mouseY > 30) {
					mouseX = 0;
					mouseY = 0;
				}
				super.mousePressed(e);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if(mouseX != 0 && mouseY != 0) {
					launcher.setLocation(launcher.getLocation().x + e.getX() - mouseX,
							launcher.getLocation().y + e.getY() - mouseY);
				}
				super.mouseDragged(e);
			}
		});

		// Add Schine logo to the right side
		JPanel topRightPanel = new JPanel();
		topRightPanel.setDoubleBuffered(true);
		topRightPanel.setOpaque(false);
		topRightPanel.setLayout(new BorderLayout());
		add(topRightPanel, BorderLayout.EAST);

		JLabel logoLabel = new JLabel();
		logoLabel.setDoubleBuffered(true);
		logoLabel.setOpaque(false);
		logoLabel.setIcon(StarMadeLauncher.getIcon("sprites/launcher_schine_logo.png"));
		topRightPanel.add(logoLabel, BorderLayout.EAST);
	}
}
