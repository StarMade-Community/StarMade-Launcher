package smlauncher.ui.panels;

import smlauncher.LogManager;
import smlauncher.StarMadeLauncher;
import smlauncher.starmade.StackLayout;
import smlauncher.ui.LauncherUI;
import smlauncher.util.ImageUtils;
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
 * [Description]
 *
 * @author TheDerpGamer
 */
public class LeftPanel extends JPanel implements UIElement {

	private final LauncherUI frame;
	private JLabel leftLabel;
	private JPanel topLeftPanel;
	private JPanel topLeftLogoPanel;
	private JList<JLabel> list;


	public LeftPanel(LauncherUI frame) {
		this.frame = frame;
		setDoubleBuffered(true);
		setOpaque(false);
		setLayout(new StackLayout());

		leftLabel = new JLabel();
		topLeftPanel = new JPanel();
		topLeftLogoPanel = new JPanel();
		list = new JList<>();

		initialize();
		registerListeners();
	}

	@Override
	public void initialize() {
		leftLabel.setDoubleBuffered(true);
		try {
			Image image = ImageIO.read(Objects.requireNonNull(StarMadeLauncher.class.getResource("/sprites/left_panel.png")));
			//Resize the image to the left panel
			image = image.getScaledInstance(150, 500, Image.SCALE_SMOOTH);
			leftLabel.setIcon(new ImageIcon(image));
		} catch(IOException exception) {
			LogManager.logWarning("Failed to load left panel image", exception);
		}

		//Stretch the image to the left panel
		add(leftLabel, StackLayout.BOTTOM);
		topLeftPanel.setDoubleBuffered(true);
		topLeftPanel.setOpaque(false);
		topLeftPanel.setLayout(new BorderLayout());
		add(topLeftPanel, StackLayout.TOP);

		//Add list
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
		topLeftPanel.add(list);

		topLeftLogoPanel.setDoubleBuffered(true);
		topLeftLogoPanel.setOpaque(false);
		topLeftLogoPanel.setLayout(new BorderLayout());
		topLeftPanel.add(topLeftLogoPanel, BorderLayout.NORTH);
		//Add a left inset
		JPanel leftInset = new JPanel();
		leftInset.setDoubleBuffered(true);
		leftInset.setOpaque(false);
		topLeftLogoPanel.add(leftInset, BorderLayout.CENTER);
		//Add logo at top left
		JLabel logo = new JLabel();
		logo.setDoubleBuffered(true);
		logo.setOpaque(false);
		logo.setIcon(ImageUtils.getIcon("sprites/logo.png"));
		leftInset.add(logo);
	}

	@Override
	public void registerListeners() {
		list.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				if(index != -1) list.setSelectedIndex(index);
				else list.clearSelection();
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					int index = list.locationToIndex(e.getPoint());
					if(index != -1) {
						switch(index) {
							case 0:
								frame.createNewsPanel();
								break;
							case 1:
								createForumsPanel();
								break;
							case 2:
								createContentPanel();
								break;
							case 3:
								createCommunityPanel();
								break;
						}
					}
				}
			}
		});
	}
}
