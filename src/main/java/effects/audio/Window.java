package effects.audio;

import effects.audio.gui.menus.MainMenu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Window {
	private static Window window;
	
	private final JFrame frame;
	
	private Window(JFrame frame) {
		this.frame = frame;
	}
	
	public static void init() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		JFrame frame = new JFrame("Audio Effects");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLayout(null);
		MainMenu mainMenu = new MainMenu();
		mainMenu.setBounds(0, 0, screensize.width, screensize.height);
		frame.add(mainMenu);
		frame.setVisible(true);
		try(InputStream in = Window.class.getResourceAsStream("/icon.png")) {
			BufferedImage image = ImageIO.read(in);
			frame.setIconImage(image);
		} catch (IOException e) {
			e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        window = new Window(frame);
	}
	
	public static void setWindow(JPanel panel) {
		window.frame.add(panel);
		window.frame.repaint();
	}

	public static JPanel getPanel() {
		return (JPanel) window.frame.getContentPane();
	}
	
	public static Window getWindow() {
		return window;
	}

	public static void rename(String name) {
		window.frame.setTitle(name);
	}
}