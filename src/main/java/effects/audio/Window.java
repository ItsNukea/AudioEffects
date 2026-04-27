package effects.audio;

import javax.swing.*;
import java.awt.*;

public class Window {
	public final JFrame frame;
	
	private Window(JFrame frame) {
		this.frame = frame;
	}
	
	public static void init() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		JFrame frame = new JFrame("Audio Effects");
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(toolkit.getScreenSize());
		frame.setVisible(true);
		
		Main.window = new Window(frame);
		
		device.setFullScreenWindow(Main.window.frame);
	}
}