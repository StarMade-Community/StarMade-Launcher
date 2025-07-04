package smlauncher.news;

import smlauncher.StarMadeLauncher;
import smlauncher.util.BBCodeToHTMLConverter;
import smlauncher.util.Palette;
import smlauncher.util.SteamNewsAPI;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Panel for displaying game news in the launcher.
 *
 * @author TheDerpGamer
 */
public class LauncherNewsPanel extends JPanel {

	public static final boolean PRINT_HTML_MESSAGES = false;

	public LauncherNewsPanel() {
		super(true);
		setBackground(Palette.paneColor);
		setOpaque(true);
	}

	public void updatePanel() {
		removeAll();
		JEditorPane htmlPanel = new JEditorPane();
		htmlPanel.setContentType("text/html");
		htmlPanel.setEditable(false);
		htmlPanel.setBackground(Palette.paneColor);
		htmlPanel.setOpaque(true);
		htmlPanel.setEditable(false);
		StringBuilder sb = new StringBuilder();
		if(!StarMadeLauncher.offlineMode) {
			for(SteamNewsAPI.NewsPost post : SteamNewsAPI.getPosts()) {
				ArrayList<String> lines = BBCodeToHTMLConverter.convert(post.getContents());
				lines.add(0, "<h1>" + post.getTitle() + "</h1>");
				LocalDate ldt = Instant.ofEpochMilli(post.getDate() * 1000L).atZone(ZoneId.systemDefault()).toLocalDate();
				lines.add("<p><a href=\"" + post.getUrl() + "\">Posted on " + ldt.toString() + " by " + post.getAuthor() + "</a></p>");
				lines = BBCodeToHTMLConverter.insertColors(lines, "#eeeeee");
				lines.add("<hr>");

				for(String line : lines) {
					sb.append(line);
					if(PRINT_HTML_MESSAGES) System.out.println(line);
				}
			}
		} else {
			//Text that says launcher is in offline mode
			sb.append("<h1>Offline Mode</h1>");
			sb.append("<p>The launcher is currently in offline mode. News updates are not available.</p>");
			if(PRINT_HTML_MESSAGES) System.out.println("Offline Mode: News updates are not available.");
		}

		htmlPanel.setText(sb.toString());
		add(htmlPanel, BorderLayout.CENTER);
		revalidate();
	}
}
