package smlauncher.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Top panel of the launcher, including window controls and branding
 */
public class TopPanel extends JPanel {
	private final JFrame parentFrame;

	public TopPanel(JFrame parentFrame) {
		this.parentFrame = parentFrame;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(800, 40));

		// Create logo panel
		JPanel logoPanel = createLogoPanel();
		add(logoPanel, BorderLayout.WEST);

		// Create window controls
		JPanel controlPanel = createWindowControls();
		add(controlPanel, BorderLayout.EAST);

		// Make panel draggable
		addDragSupport();
	}

	/**
	 * Create logo panel for the top of the launcher
	 * @return JPanel with logo
	 */
	private JPanel createLogoPanel() {
		JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		try {
			ImageIcon logoIcon = new ImageIcon(getClass().getResource("/sprites/logo.png"));
			JLabel logoLabel = new JLabel(logoIcon);
			logoPanel.add(logoLabel);
		} catch (Exception e) {
			logoPanel.add(new JLabel("StarMade Launcher"));
		}
		return logoPanel;
	}

	/**
	 * Create window control buttons (minimize, close)
	 * @return JPanel with window controls
	 */
	private JPanel createWindowControls() {
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		// Minimize button
		JButton minimizeButton = createControlButton("−", e -> parentFrame.setState(Frame.ICONIFIED));

		// Close button
		JButton closeButton = createControlButton("×", e -> System.exit(0));

		controlPanel.add(minimizeButton);
		controlPanel.add(closeButton);

		return controlPanel;
	}

	/**
	 * Create a standardized window control button
	 * @param label Button label
	 * @param action Action to perform when clicked
	 * @return Styled JButton
	 */
	private JButton createControlButton(String label, java.awt.event.ActionListener action) {
		JButton button = new JButton(label);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.addActionListener(action);

		// Hover effects
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(Color.DARK_GRAY);
				button.setOpaque(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(null);
				button.setOpaque(false);
			}
		});

		return button;
	}

	/**
	 * Add drag support to move the window
	 */
	private void addDragSupport() {
		Point offset = new Point();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				offset.x = e.getX();
				offset.y = e.getY();
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = parentFrame.getLocation().x;
				int y = parentFrame.getLocation().y;
				parentFrame.setLocation(
						x + e.getX() - offset.x,
						y + e.getY() - offset.y
				);
			}
		});
	}
}