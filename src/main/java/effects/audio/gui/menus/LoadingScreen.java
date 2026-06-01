package effects.audio.gui.menus;

import effects.audio.EffectType;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LoadingScreen extends JPanel {
    public final File selectedFile;
    public final EffectType selectedEffect;

    public JProgressBar progressBar;
    public JLabel status;

    private static LoadingScreen instance;

    public LoadingScreen(EffectType effectType, File selectedFile) {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Applying effect...", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

        // Status label
        status = new JLabel("Starting...", JLabel.CENTER);
        status.setFont(status.getFont().deriveFont(Font.PLAIN, 11f));
        status.setForeground(Color.GRAY);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 22));

        // Center stack: title + bar + status
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        centerPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        centerPanel.add(progressBar, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(status, gbc);

        add(centerPanel, BorderLayout.CENTER);


        this.selectedEffect = effectType;
        this.selectedFile = selectedFile;
        instance = this;
    }

    public void setProgress(int percent, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percent);
            this.status.setText(status);
        });
    }

    public void setIndeterminate(boolean indeterminate) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(indeterminate);
            progressBar.setStringPainted(!indeterminate);
        });
    }

    public void setStatus(String newStatus, int percentage) {
        status.setText(newStatus);
    }
}
