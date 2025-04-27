package smlauncher.ui.panels;

import smlauncher.StarMadeLauncher;
import smlauncher.starmade.StackLayout;
import smlauncher.ui.LauncherUI;
import smlauncher.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static java.awt.Frame.ICONIFIED;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class TopPanel extends JPanel implements UIElement {

	private final int[] mouse = new int[2];

	private LauncherUI frame;
	private JPanel buttonPanel;
	private JButton closeButton;
	private JButton minimizeButton;

	public TopPanel(LauncherUI frame) {
		this.frame = frame;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new StackLayout());
		initialize();
		registerListeners();
	}

	@Override
	public void initialize() {
		JLabel topLabel = new JLabel();
		topLabel.setDoubleBuffered(true);
		topLabel.setIcon(ImageUtils.getIcon("sprites/header_top.png"));
		add(topLabel);
		buttonPanel = new JPanel();
		buttonPanel.setDoubleBuffered(true);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false);

		closeButton = new JButton(null, ImageUtils.getIcon("sprites/close_icon.png")); //Todo: Replace these cus they look like shit
		closeButton.setDoubleBuffered(true);
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);

		minimizeButton = new JButton(null, ImageUtils.getIcon("sprites/minimize_icon.png"));
		minimizeButton.setDoubleBuffered(true);
		minimizeButton.setOpaque(false);
		minimizeButton.setContentAreaFilled(false);
		minimizeButton.setBorderPainted(false);
		buttonPanel.add(minimizeButton);
		buttonPanel.add(closeButton);
		topLabel.add(buttonPanel);
		buttonPanel.setBounds(0, 0, 800, 30);
	}

	@Override
	public void registerListeners() {
		closeButton.addActionListener(e -> {
			frame.dispose();
			System.exit(0);
		});

		minimizeButton.addActionListener(e -> frame.setState(ICONIFIED));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouse[0] = e.getX();
				mouse[1] = e.getY();
				//If the mouse is on the top panel buttons, don't drag the window
				if(mouse[0] > 770 || mouse[1] > 30) {
					mouse[0] = 0;
					mouse[1] = 0;
				}
				super.mousePressed(e);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if(mouse[0] != 0 && mouse[1] != 0) setLocation(getLocation().x + e.getX() - mouse[0], getLocation().y + e.getY() - mouse[1]);
				super.mouseDragged(e);
			}
		});
	}
}
