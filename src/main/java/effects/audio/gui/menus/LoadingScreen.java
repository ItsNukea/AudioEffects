package effects.audio.gui.menus;

import effects.audio.EffectType;
import effects.audio.gui.GuiUtil;
import effects.audio.gui.menus.components.PrettyProgressBarUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class LoadingScreen extends JPanel {
    public final File selectedFile;
    public final EffectType selectedEffect;

    public JProgressBar progressBar;
    public JLabel status;
    private JLabel titleLabel;
    private JLabel fileInfoLabel;

    public LoadingScreen(EffectType effectType, File selectedFile) {
        super(new BorderLayout());
        this.selectedEffect = effectType;
        this.selectedFile = selectedFile;

        setupLayout();
        setupStyling();
        setupAnimations();
    }

    private void setupLayout() {
        JPanel mainCard = GuiUtil.buildCardLayout(this);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        titleLabel = new JLabel("Processing Audio", JLabel.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        centerPanel.add(titleLabel, gbc);

        fileInfoLabel = new JLabel(getFileInfoText(), JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        centerPanel.add(fileInfoLabel, gbc);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 15, 40);
        centerPanel.add(progressBar, gbc);

        status = new JLabel("Initializing...", JLabel.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(status, gbc);

        mainCard.add(centerPanel, BorderLayout.CENTER);
    }

    private void setupStyling() {
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(27, 94, 32));

        fileInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileInfoLabel.setForeground(new Color(108, 117, 125));

        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setForeground(GuiUtil.PRIMARY_COLOR);
        progressBar.setBackground(new Color(235, 235, 235));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);

        progressBar.setStringPainted(true);
        progressBar.setUI(new PrettyProgressBarUI());

        status.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        status.setForeground(new Color(108, 117, 125));
    }

    private void setupAnimations() {
        final AtomicReference<Float> alpha = new AtomicReference<>(1.0f);
        final AtomicReference<Boolean> fadingOut = new AtomicReference<>(true);

        new Timer(15, _ -> {
            if(!progressBar.isIndeterminate()) return;

            alpha.set(alpha.get() + (fadingOut.get() ? -0.1f : 0.1f));
            if(alpha.get() <= 0.7f) {
                alpha.set(0.7f);
                fadingOut.set(false);
            }
            if (alpha.get() >= 1.0f) {
                alpha.set(1.0f);
                fadingOut.set(true);
            }

            Color base = new Color(27, 94, 32);
            titleLabel.setForeground(
                    new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (alpha.get() * 255)));
        }).start();
    }

    private String getFileInfoText() {
        String effectName = selectedEffect.getPrettyName();
        String fileName = selectedFile.getName();
        return String.format("Applying %s to '%s'", effectName, fileName);
    }

    public void setStatus(String newStatus, int percentage) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percentage);
            status.setText(newStatus);
            if (!progressBar.isIndeterminate())
                progressBar.setString(percentage + "%");
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GuiUtil.paintBackground(g, this::repaint, getWidth(), getHeight());
    }
}