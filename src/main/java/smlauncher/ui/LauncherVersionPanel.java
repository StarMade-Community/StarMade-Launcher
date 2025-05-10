package smlauncher.ui;

import smlauncher.StarMadeLauncher;
import smlauncher.VersionRegistry;
import smlauncher.starmade.GameBranch;
import smlauncher.util.Palette;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for version selection in the launcher.
 * Contains dropdowns for branch and version selection, and a port field for server mode.
 */
public class LauncherVersionPanel extends JPanel {
	private final StarMadeLauncher launcher;
	private final boolean serverMode;
	private JComboBox<String> versionDropdown;
	private JComboBox<String> branchDropdown;
	private JTextField portField;
	private VersionSelectionListener versionSelectionListener;

	public LauncherVersionPanel(StarMadeLauncher launcher, boolean serverMode) {
		super(true);
		this.launcher = launcher;
		this.serverMode = serverMode;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new BorderLayout());
		initialize();
	}

	private void initialize() {
		JPanel versionSubPanel = new JPanel();
		versionSubPanel.setDoubleBuffered(true);
		versionSubPanel.setOpaque(false);
		versionSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(versionSubPanel, BorderLayout.SOUTH);

		// Change color of arrow
		UIDefaults defaults = new UIDefaults();
		defaults.put("ComboBox:\"ComboBox.arrowButton\"[Enabled].backgroundPainter", Palette.buttonColor);

		// Branch dropdown
		branchDropdown = createBranchDropdown();
		setClientProperties(branchDropdown, defaults);

		// Version dropdown
		versionDropdown = createDropdown();
		setClientProperties(versionDropdown, defaults);

		versionDropdown.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED && versionSelectionListener != null) {
				String selectedVersion = versionDropdown.getItemAt(versionDropdown.getSelectedIndex()).split(" ")[0];
				versionSelectionListener.onVersionSelected(selectedVersion);
			}
		});

		versionSubPanel.add(branchDropdown);
		versionSubPanel.add(versionDropdown);

		// Port field for server mode
		if(serverMode) {
			portField = createPortField();
			versionSubPanel.add(portField);
		}
	}

	/**
	 * Set the version selection listener for this panel.
	 *
	 * @param listener The listener to use
	 */
	public void setVersionSelectionListener(VersionSelectionListener listener) {
		versionSelectionListener = listener;
	}

	/**
	 * Update the version dropdown with versions from the registry.
	 *
	 * @param versionRegistry The version registry to use
	 */
	public void updateVersionDropdown(VersionRegistry versionRegistry) {
		versionDropdown.removeAllItems();
		GameBranch branch = GameBranch.getForIndex(branchDropdown.getSelectedIndex());

		versionRegistry.getVersions(branch).forEach(version -> {
			versionDropdown.addItem(version.version + " (" + version.build + ")");
		});
	}

	/**
	 * Get the selected version.
	 *
	 * @return The selected version
	 */
	public String getSelectedVersion() {
		if(versionDropdown.getSelectedIndex() == -1) return null;
		return versionDropdown.getItemAt(versionDropdown.getSelectedIndex()).split(" ")[0];
	}

	/**
	 * Set the initial selected version.
	 *
	 * @param version The version to select
	 */
	public void setSelectedVersion(String version) {
		for(int i = 0; i < versionDropdown.getItemCount(); i++) {
			String item = versionDropdown.getItemAt(i);
			if(item.startsWith(version)) {
				versionDropdown.setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * Get the selected branch.
	 *
	 * @return The selected branch
	 */
	public GameBranch getSelectedBranch() {
		return GameBranch.getForIndex(branchDropdown.getSelectedIndex());
	}

	/**
	 * Get the port from the port field.
	 *
	 * @return The port, or -1 if not in server mode
	 */
	public int getPort() {
		if(!serverMode || portField == null) return -1;
		try {
			return Integer.parseInt(portField.getText());
		} catch(NumberFormatException e) {
			return 4242; // Default port
		}
	}

	private JComboBox<String> createBranchDropdown() {
		JComboBox<String> dropdown = createDropdown();
		dropdown.addItem("Release");
		dropdown.addItem("Dev");
		dropdown.addItem("Pre-Release");
		dropdown.setSelectedIndex(0);
		dropdown.setFocusable(false);
		dropdown.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED && versionSelectionListener != null) {
				GameBranch branch = GameBranch.getForIndex(dropdown.getSelectedIndex());
				versionSelectionListener.onBranchSelected(branch);
			}
		});
		return dropdown;
	}

	private JComboBox<String> createDropdown() {
		JComboBox<String> dropdown = new JComboBox<>();
		dropdown.setBackground(Palette.buttonColor);
		dropdown.setForeground(Palette.textColor);
		dropdown.setFont(new Font("Roboto", Font.BOLD, 12));
		dropdown.setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				JButton button = new JButton();
				button.setBackground(Palette.buttonColor);
				button.setBorder(null);
				button.setFocusPainted(false);
				button.setContentAreaFilled(false);
				return button;
			}
		});
		dropdown.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setBackground(Palette.buttonColor);
				setForeground(Palette.textColor);
				return this;
			}
		});
		return dropdown;
	}

	private JTextField createPortField() {
		JTextField field = new JTextField("4242", 5);
		field.setBackground(Palette.buttonColor);
		field.setForeground(Palette.textColor);
		field.setFont(new Font("Roboto", Font.BOLD, 12));
		field.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		field.addMouseListener(new MouseListener() {
			@Override
			public void mouseEntered(MouseEvent e) {
				field.setBackground(Palette.selectedColor);
				field.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				field.setBackground(Palette.buttonColor);
				field.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
		field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if(!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
					e.consume();
				}
				if(field.getText().length() >= 5 && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		return field;
	}

	private void setClientProperties(JComboBox<String> dropdown, UIDefaults defaults) {
		dropdown.putClientProperty("Nimbus.Overrides", defaults);
		dropdown.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
	}

	/**
	 * Interface for handling version selection events.
	 */
	public interface VersionSelectionListener {
		/**
		 * Called when a version is selected.
		 *
		 * @param version The selected version
		 */
		void onVersionSelected(String version);

		/**
		 * Called when a branch is selected.
		 *
		 * @param branch The selected branch
		 */
		void onBranchSelected(GameBranch branch);
	}
}
