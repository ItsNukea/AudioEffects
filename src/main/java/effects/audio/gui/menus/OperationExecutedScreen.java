package effects.audio.gui.menus;

import effects.audio.Window;
import effects.audio.effectoperations.EffectOperation;
import effects.audio.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class OperationExecutedScreen extends JPanel {
    public EffectOperation operation;

    private JButton downloadButton;

    public OperationExecutedScreen(EffectOperation operation, File result) {
        super(new BorderLayout());
        this.operation = operation;

        setupLayout();
        setupDownloadAction(result);
    }

    private void setupLayout() {
        JPanel mainCard = GuiUtil.buildCardLayout(this);

        mainCard.add(createTitlePanel(),   BorderLayout.NORTH);
        mainCard.add(createContentPanel(), BorderLayout.CENTER);
        mainCard.add(createButtonPanel(),  BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel finishedLabel = new JLabel("Processing Complete!", SwingConstants.CENTER);
        finishedLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        finishedLabel.setForeground(GuiUtil.SUCCESS_COLOR);
        titlePanel.add(finishedLabel, BorderLayout.CENTER);

        return titlePanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel descriptionLabel = new JLabel("Your processed audio file is ready for download", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descriptionLabel.setForeground(new Color(108, 117, 125));
        contentPanel.add(descriptionLabel, gbc);

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton backButton = GuiUtil.createStyledButton("Back to Menu", GuiUtil.PRIMARY_COLOR, GuiUtil.PRIMARY_HOVER);
        downloadButton = GuiUtil.createStyledButton("Download File", GuiUtil.SUCCESS_COLOR, GuiUtil.SUCCESS_HOVER);

        backButton.addActionListener(_ -> Window.setWindow(new MainMenu()));

        buttonPanel.add(backButton);
        buttonPanel.add(downloadButton);

        return buttonPanel;
    }

    private void setupDownloadAction(File result) {
        downloadButton.addActionListener(_ -> {
            try {
                int i = 0;
                Path targetPath;
                do {
                    targetPath = Paths.get(System.getProperty("user.home") + "/Downloads/",
                            "modified-audio" + (i == 0 ? "" : "-" + i) + ".wav");
                    if (!Files.exists(targetPath)) break;
                    i++;
                } while (true);

                Files.copy(Paths.get(result.getAbsolutePath()), targetPath);

                JOptionPane.showMessageDialog(this,
                        "File saved successfully to:\n" + targetPath,
                        "Download Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                Window.setWindow(new MainMenu());

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + e.getMessage(),
                        "Download Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GuiUtil.paintBackground(g, this::repaint, getWidth(), getHeight());
    }
}