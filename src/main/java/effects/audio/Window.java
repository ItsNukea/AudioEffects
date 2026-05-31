package effects.audio;

import effects.audio.gui.menus.MainMenu;
import org.slf4j.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Window {
	public static final Logger LOGGER = LoggerFactory.getLogger("Window");
	private static Window window;
	private final JFrame frame;
	
	private Window(JFrame frame) {
		this.frame = frame;
	}
	
	public static void init() {
		LOGGER.info("Initializing window");
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		JFrame frame = new JFrame("Audio Effects");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLayout(null);
		MainMenu mainMenu = new MainMenu();
		mainMenu.setBounds(0, 0, screensize.width, screensize.height);
		frame.setContentPane(mainMenu);
		try(InputStream in = Window.class.getResourceAsStream("/sprites/icon.png")) {
			BufferedImage image = ImageIO.read(in);
			frame.setIconImage(image);
		} catch (Exception e) {
			LOGGER.error("Could not load icon:", e);
            throw new RuntimeException(e);
        }
		frame.setVisible(true);
        window = new Window(frame);
		LOGGER.info("Window initialized");
	}
	
	public static void setWindow(JComponent panel) {
		window.frame.setContentPane(panel);
		window.frame.repaint();
		window.frame.revalidate();
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