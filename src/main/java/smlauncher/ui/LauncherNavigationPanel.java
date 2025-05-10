package smlauncher.ui;

import smlauncher.StarMadeLauncher;
import smlauncher.starmade.StackLayout;
import smlauncher.util.Palette;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.Objects;

/**
 * Panel for the left navigation of the launcher.
 * Contains the navigation list with options like NEWS, FORUMS, CONTENT, and COMMUNITY.
 */
public class LauncherNavigationPanel extends JPanel {
	private final StarMadeLauncher launcher;
	private final JList<JLabel> navigationList;
	private NavigationHandler navigationHandler;

	public LauncherNavigationPanel(StarMadeLauncher launcher, NavigationHandler navigationHandler) {
		super(true);
		this.launcher = launcher;
		this.navigationHandler = navigationHandler;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new StackLayout());

		// Create the background image label
		JLabel leftLabel = new JLabel();
		leftLabel.setDoubleBuffered(true);
		try {
			Image image = ImageIO.read(Objects.requireNonNull(StarMadeLauncher.class.getResource("/sprites/left_panel.png")));
			// Resize the image to the left panel
			image = image.getScaledInstance(150, 500, Image.SCALE_SMOOTH);
			leftLabel.setIcon(new ImageIcon(image));
		} catch(IOException exception) {
			// Log the exception
			System.err.println("Failed to load left panel image: " + exception.getMessage());
		}
		add(leftLabel, StackLayout.BOTTOM);

		// Create the top panel for navigation
		JPanel topLeftPanel = new JPanel();
		topLeftPanel.setDoubleBuffered(true);
		topLeftPanel.setOpaque(false);
		topLeftPanel.setLayout(new BorderLayout());
		add(topLeftPanel, StackLayout.TOP);

		// Create the navigation list
		navigationList = createNavigationList();
		topLeftPanel.add(navigationList);

		// Create the logo panel
		JPanel topLeftLogoPanel = new JPanel();
		topLeftLogoPanel.setDoubleBuffered(true);
		topLeftLogoPanel.setOpaque(false);
		topLeftLogoPanel.setLayout(new BorderLayout());
		topLeftPanel.add(topLeftLogoPanel, BorderLayout.NORTH);

		// Add a left inset
		JPanel leftInset = new JPanel();
		leftInset.setDoubleBuffered(true);
		leftInset.setOpaque(false);
		topLeftLogoPanel.add(leftInset, BorderLayout.CENTER);

		// Add logo at top left
		JLabel logo = new JLabel();
		logo.setDoubleBuffered(true);
		logo.setOpaque(false);
		logo.setIcon(StarMadeLauncher.getIcon("sprites/logo.png"));
		leftInset.add(logo);
	}

	/**
	 * Set the navigation handler for this panel.
	 *
	 * @param navigationHandler The navigation handler to use
	 */
	public void setNavigationHandler(NavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	private JList<JLabel> createNavigationList() {
		JList<JLabel> list = new JList<>();
		list.setDoubleBuffered(true);
		list.setOpaque(false);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
			if(isSelected) {
				value.setForeground(Palette.selectedColor);
				value.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Palette.selectedColor));
			} else {
				value.setForeground(Palette.deselectedColor);
				value.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Palette.deselectedColor));
			}
			return value;
		});

		// Highlight on mouse hover
		list.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				if(index != -1) list.setSelectedIndex(index);
				else list.clearSelection();
			}
		});

		// Handle navigation clicks
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					int index = list.locationToIndex(e.getPoint());
					if(index != -1 && navigationHandler != null) {
						navigationHandler.onNavigate(index);
					}
				}
			}
		});

		list.setFixedCellHeight(48);
		DefaultListModel<JLabel> listModel = new DefaultListModel<>();
		listModel.addElement(new JLabel("NEWS"));
		listModel.addElement(new JLabel("FORUMS"));
		listModel.addElement(new JLabel("CONTENT"));
		listModel.addElement(new JLabel("COMMUNITY"));

		for(int i = 0; i < listModel.size(); i++) {
			JLabel label = listModel.get(i);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(new Font("Roboto", Font.BOLD, 18));
			label.setForeground(Palette.selectedColor);
			label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Palette.selectedColor));
			label.setDoubleBuffered(true);
			label.setOpaque(false);
		}

		list.setModel(listModel);
		return list;
	}
}