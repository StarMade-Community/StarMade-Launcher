package smlauncher.ui;

import smlauncher.LogManager;
import smlauncher.StarMadeLauncher;
import smlauncher.community.LauncherCommunityPanel;
import smlauncher.ui.panels.NewsPanel;
import smlauncher.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class LauncherUI extends JFrame {

	private MainPanel mainPanel;
	private TopPanel topPanel;
	private CenterPanel centerPanel;
	private NewsPanel newsPanel;
	private LeftPanel leftPanel;
	private FooterPanel footerPanel;
	private VersionPanel versionPanel;
	private PlayPanel playPanel;
	private ServerPanel serverPanel;

	public LauncherUI(StarMadeLauncher controller) {
		super("StarMade Launcher");
		setBounds(100, 100, 800, 550);
		setMinimumSize(new Dimension(800, 550));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try {
			URL resource = StarMadeLauncher.class.getResource("/sprites/icon.png");
			if(resource != null) setIconImage(Toolkit.getDefaultToolkit().getImage(resource));
			else setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		} catch(Exception exception) {
			LogManager.logException("Failed to set window icon", exception);
		}

		try {
			controller.initVersionRegistry();
		} catch(Exception exception) {
			LogManager.logException("Failed to fetch version list! Check your internet connection!", exception);
			JOptionPane.showMessageDialog(this, "Failed to fetch version list! Check your internet connection!", "Warning", JOptionPane.WARNING_MESSAGE);
		}

		dispose();
		setUndecorated(true);
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
		setResizable(false);
		getRootPane().setDoubleBuffered(true);
		setVisible(true);

		createMainPanel();
		createNewsPanel();
	}

	private void createMainPanel() {
		mainPanel = new MainPanel();
		setContentPane(mainPanel);

		topPanel = new TopPanel(this);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		leftPanel = new LeftPanel(this);
		mainPanel.add(leftPanel, BorderLayout.WEST);
	}

	public void createPlayPanel(FooterPanel footerPanel) {

	}

	private void createNewsPanel() {
		if(newsPanel == null) newsPanel = new NewsPanel();
		createScroller(newsPanel);
		newsPanel.updatePanel();
		SwingUtilities.invokeLater(() -> {
			JScrollBar vertical = centerScrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMinimum());
		});
	}

	private void createForumsPanel() {
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			String ccURL = "https://starmadedock.net/forums/";
			try {
				Desktop.getDesktop().browse(new URI(ccURL));
			} catch(IOException | URISyntaxException exception) {
				LogManager.logWarning("Failed to open forums", exception);
			}
		}
		/* Todo: Create forums panel
		forumsPanel = new LauncherForumsPanel();
		createScroller(forumsPanel);
		forumsPanel.updatePanel();
		SwingUtilities.invokeLater(() -> {
			JScrollBar vertical = centerScrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMinimum());
		});
		 */
	}

	private void createContentPanel() {
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			String ccURL = "https://starmadedock.net/content/";
			try {
				Desktop.getDesktop().browse(new URI(ccURL));
			} catch(IOException | URISyntaxException exception) {
				LogManager.logWarning("Failed to open content", exception);
			}
		}
	}

	private void createCommunityPanel() {
		communityPanel = new LauncherCommunityPanel();
		createScroller(communityPanel);
		communityPanel.updatePanel();
		SwingUtilities.invokeLater(() -> {
			JScrollBar vertical = centerScrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMinimum());
		});
	}
}
