package smlauncher.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class MainPanel extends JPanel implements UIElement {

	public MainPanel() {
		setDoubleBuffered(true);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setLayout(new BorderLayout(0, 0));
		initialize();
		registerListeners();
	}

	@Override
	public void initialize() {

	}

	@Override
	public void registerListeners() {

	}
}
