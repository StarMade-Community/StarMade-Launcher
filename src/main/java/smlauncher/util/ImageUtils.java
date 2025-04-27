package smlauncher.util;

import smlauncher.LogManager;
import smlauncher.StarMadeLauncher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ImageUtils {

	public static ImageIcon getIcon(String s) {
		try {
			return new ImageIcon(ImageIO.read(Objects.requireNonNull(StarMadeLauncher.class.getResource("/" + s))));
		} catch(IOException exception) {
			return new ImageIcon();
		}
	}

	public static ImageIcon getIcon(String s, int width, int height) {
		try {
			return new ImageIcon(ImageIO.read(Objects.requireNonNull(StarMadeLauncher.class.getResource("/" + s))).getScaledInstance(width, height, Image.SCALE_SMOOTH));
		} catch(IOException exception) {
			LogManager.logException("Failed to get icon", exception);
			return new ImageIcon();
		}
	}

}
