package effects.audio;

import effects.audio.gui.menus.MainMenu;

import javax.swing.*;
import java.awt.*;

public class Window {
	private static Window window;
	
	public final JFrame frame;
	
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
		window = new Window(frame);
	}
	
	public void addPanel(JPanel panel) {
		frame.add(panel);
	}
	
	public static Window getWindow() {
		return window;
	}
}