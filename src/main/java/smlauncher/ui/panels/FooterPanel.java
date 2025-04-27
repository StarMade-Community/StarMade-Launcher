package smlauncher.ui.panels;

import smlauncher.StarMadeLauncher;
import smlauncher.starmade.StackLayout;
import smlauncher.ui.LauncherUI;
import smlauncher.util.ImageUtils;
import smlauncher.util.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class FooterPanel extends JPanel implements UIElement {

	private final LauncherUI frame;
	private final JLabel footerLabel;
	private final JButton normalPlayButton;
	private final JButton dedicatedServerButton;
	private final JPanel footerPanelButtons;

	public FooterPanel(LauncherUI frame) {
		this.frame = frame;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new StackLayout());
		footerLabel = new JLabel();
		normalPlayButton = new JButton("Play");
		dedicatedServerButton = new JButton("Dedicated Server");
		footerPanelButtons = new JPanel();
		initialize();
		registerListeners();
	}

	@Override
	public void initialize() {
		footerLabel.setDoubleBuffered(true);
		footerLabel.setIcon(ImageUtils.getIcon("sprites/footer_normalplay_bg.jpg"));
		add(footerLabel);

		normalPlayButton.setFont(new Font("Roboto", Font.BOLD, 12));
		normalPlayButton.setDoubleBuffered(true);
		normalPlayButton.setOpaque(false);
		normalPlayButton.setContentAreaFilled(false);
		normalPlayButton.setBorderPainted(false);
		normalPlayButton.setForeground(Palette.textColor);

		dedicatedServerButton.setFont(new Font("Roboto", Font.BOLD, 12));
		dedicatedServerButton.setDoubleBuffered(true);
		dedicatedServerButton.setOpaque(false);
		dedicatedServerButton.setContentAreaFilled(false);
		dedicatedServerButton.setBorderPainted(false);
		dedicatedServerButton.setForeground(Palette.textColor);

		footerPanelButtons.setDoubleBuffered(true);
		footerPanelButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		footerPanelButtons.setOpaque(false);
		footerPanelButtons.add(Box.createRigidArea(new Dimension(10, 0)));
		footerPanelButtons.add(normalPlayButton);
		footerPanelButtons.add(Box.createRigidArea(new Dimension(30, 0)));
		footerPanelButtons.add(dedicatedServerButton);
		footerLabel.add(footerPanelButtons);
		footerPanelButtons.setBounds(0, 0, 800, 30);
	}

	@Override
	public void registerListeners() {
		normalPlayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				normalPlayButton.setForeground(Palette.selectedColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				normalPlayButton.setForeground(Palette.textColor);
			}
		});
		normalPlayButton.addActionListener(e -> switchToClientMode(footerLabel));

		dedicatedServerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				dedicatedServerButton.setForeground(Palette.selectedColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				dedicatedServerButton.setForeground(Palette.textColor);
			}
		});
		dedicatedServerButton.addActionListener(e -> {
			if(StarMadeLauncher.updaterThread == null || !StarMadeLauncher.updaterThread.updating) { //Don't allow this while the game is updating
				switchToServerMode(footerLabel);
			}
		});
	}

	public void switchToClientMode(JLabel footerLabel) {
		footerLabel.setIcon(ImageUtils.getIcon("sprites/footer_normalplay_bg.jpg"));
		controller.getServerPanel().setVisible(false);
		controller.getVersionPanel().setVisible(true);
		frame.createPlayPanel(this);
	}

	public void switchToServerMode(JLabel footerLabel) {
		footerLabel.setIcon(ImageUtils.getIcon("sprites/footer_dedicated_bg.jpg"));
		versionPanel.setVisible(false);
		playPanelButtons.removeAll();
		versionPanel.removeAll();
		createServerPanel(this);
		serverPanel.setVisible(true);
	}
}
