package effects.audio.gui.menus;

import effects.audio.*;
import effects.audio.Window;
import effects.audio.io.FileSelector;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainMenu extends JPanel {
	private final JButton selectFileButton;
	private final JButton performOperationButton;
	private final JButton selectEffectButton;
	private final JPopupMenu effectSelection;
	
	private File selectedFile;
	private EffectType selectedEffect;
	
	public MainMenu() {
		super(null);
		String selectFileTitle = "Choose your audio";
		selectFileButton = new JButton(selectFileTitle);
		selectFileButton.addActionListener(_ -> {
			selectedFile = FileSelector.get();
			updateButtonNames();
		});
		selectFileButton.setBounds(400, 350, 200, 50);
		
		performOperationButton = new JButton("Go!");
		performOperationButton.addActionListener(_ -> Window.setWindow(new ParameterMenu(selectedEffect)));
		performOperationButton.setBounds(580, 500, 100, 50);
		performOperationButton.setEnabled(false);
		
		effectSelection = new JPopupMenu("Select an effect");
		effectSelection.setLayout(null);

		for(EffectType type : EffectType.values()) {
			JMenuItem item = new JMenuItem(type.getPrettyName());
			item.addActionListener(_ -> {
				selectedEffect = type;
				updateButtonNames();
			});
			effectSelection.add(item);
		}
		selectEffectButton = new JButton("Select an effect");
		selectEffectButton.setBounds(650, 350, 200, 50);
		selectEffectButton.addActionListener(_ -> effectSelection.show(selectEffectButton, 0, -effectSelection.getPreferredSize().height));

		add(selectFileButton);
		add(performOperationButton);
		add(effectSelection);
		add(selectEffectButton);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g.create();
		drawTitle(graphics);
		
		graphics.dispose();
	}
	
	private static void drawTitle(Graphics2D graphics) {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		String text = "Audio Effects";
		graphics.setFont(new Font("Arial", Font.BOLD, 60));
		int strwidth = getStringWidth(text, graphics, new Font("Arial", Font.PLAIN, 60));
		graphics.drawString(text, (screensize.width - strwidth) / 2, 80);
	}
	
	public void updateButtonNames() {
		String selectEffectTitle = "Select an effect";
		selectEffectButton.setText(selectedEffect == null ? selectEffectTitle : selectedEffect.getPrettyName());
		selectFileButton.setText(selectedFile == null ? "Choose your audio" : selectedFile.getName());
		performOperationButton.setEnabled(selectedFile != null && selectedEffect != null);
	}

	private static int getStringWidth(String text, Graphics2D graphics, Font font) {
		Font previousFont = graphics.getFont();
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();
		int strwidth = metrics.stringWidth(text);
		graphics.setFont(previousFont);
		return strwidth;
	}
}