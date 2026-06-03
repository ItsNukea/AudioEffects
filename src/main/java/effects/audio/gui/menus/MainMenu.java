package effects.audio.gui.menus;

import effects.audio.*;
import effects.audio.Window;
import effects.audio.gui.GuiUtil;
import effects.audio.gui.menus.components.EffectTypeJMenuItem;
import effects.audio.io.FileSelector;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class MainMenu extends JPanel {
	private JButton selectFileButton;
	private JButton performOperationButton;
	private JButton selectEffectButton;
	private JPopupMenu effectSelection;

	private File selectedFile;
	private EffectType selectedEffect;

	// Animation state
	private Timer pulseTimer;
	private float pulseAlpha = 1.0f;
	private boolean pulsing = false;

	public MainMenu() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(1200, 800));

		initializeComponents();
		setupLayout();
		setupAnimations();
	}

	private void initializeComponents() {
		selectFileButton = GuiUtil.createStyledButton("Choose your audio", GuiUtil.PRIMARY_COLOR, GuiUtil.PRIMARY_HOVER);
		selectFileButton.addActionListener(_ -> {
			selectedFile = FileSelector.get();
			updateButtonNames();
		});

		selectEffectButton = GuiUtil.createStyledButton("Select Effect", GuiUtil.SECONDARY_COLOR, GuiUtil.SECONDARY_HOVER);

		performOperationButton = GuiUtil.createStyledButton("Apply Effect", GuiUtil.SUCCESS_COLOR, GuiUtil.SUCCESS_HOVER);
		performOperationButton.addActionListener(_ -> Window.setWindow(new ParameterMenu(selectedEffect, selectedFile)));
		performOperationButton.setEnabled(false);

		createEffectSelectionPopup();

		selectEffectButton.addActionListener(_ ->
				effectSelection.show(selectEffectButton, 0, selectEffectButton.getHeight() + 5));
	}

	private void setupLayout() {
		JPanel mainCard = GuiUtil.buildCardLayout(this, 60, 80, 40);

		mainCard.add(createTitlePanel(), BorderLayout.NORTH);
		mainCard.add(createCenterPanel(), BorderLayout.CENTER);
		mainCard.add(createStatusPanel(), BorderLayout.SOUTH);
	}

	private JPanel createTitlePanel() {
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);

		JLabel titleLabel = new JLabel("Audio Effects", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
		titleLabel.setForeground(new Color(27, 94, 32));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		titlePanel.add(titleLabel, BorderLayout.NORTH);

		return titlePanel;
	}

	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 25, 0);
		centerPanel.add(selectFileButton, gbc);

		gbc.gridy = 1;
		centerPanel.add(selectEffectButton, gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(40, 50, 0, 50);
		centerPanel.add(performOperationButton, gbc);

		return centerPanel;
	}

	private JPanel createStatusPanel() {
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		statusPanel.setOpaque(false);
		statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		return statusPanel;
	}

	private void createEffectSelectionPopup() {
		effectSelection = new JPopupMenu() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(0, 0, 0, 50));
				g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 12, 12);
				g2.setColor(new Color(255, 255, 255, 250));
				g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
				g2.dispose();
			}
		};
		effectSelection.setBorder(new RoundedBorder(12));
		effectSelection.setOpaque(false);

		for (EffectType type : EffectType.values()) {
			JMenuItem item = new EffectTypeJMenuItem(type.getPrettyName());

			item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			item.setBorderPainted(false);
			item.setOpaque(false);
			item.setPreferredSize(new Dimension(200, 35));
			item.addActionListener(_ -> {
				selectedEffect = type;
				updateButtonNames();
			});

			effectSelection.add(item);
		}
	}

	private void setupAnimations() {
		AtomicReference<Boolean> increasing = new AtomicReference<>(true);
		pulseTimer = new Timer(1500, _ -> {
            if (performOperationButton.isEnabled() && pulsing) {
                if (increasing.get()) {
                    pulseAlpha += 0.3f;
                    if (pulseAlpha >= 1.0f) { pulseAlpha = 1.0f; increasing.set(false); }
                } else {
                    pulseAlpha -= 0.3f;
                    if (pulseAlpha <= 0.6f) { pulseAlpha = 0.6f; increasing.set(true); }
                }
                performOperationButton.repaint();
            }
        });
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		GuiUtil.paintBackground(g, this::repaint, getWidth(), getHeight());
	}

	public void updateButtonNames() {
		selectEffectButton.setText(selectedEffect == null ? "Select Effect" : selectedEffect.getPrettyName());
		selectFileButton.setText(selectedFile == null ? "Choose your audio" : selectedFile.getName());

		boolean wasEnabled = performOperationButton.isEnabled();
		performOperationButton.setEnabled(selectedFile != null && selectedEffect != null);

		if (!wasEnabled && performOperationButton.isEnabled()) {
			pulsing = true;
			pulseTimer.start();
		} else if (!performOperationButton.isEnabled()) {
			pulsing = false;
			pulseTimer.stop();
		}
	}

	private static class RoundedBorder extends AbstractBorder {
		private final int radius;

		public RoundedBorder(int radius) { this.radius = radius; }

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(200, 200, 200, 100));
			g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
			g2.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) { return new Insets(5, 5, 5, 5); }
	}
}