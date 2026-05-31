package effects.audio.gui.menus;

import effects.audio.*;
import effects.audio.Window;
import effects.audio.effectoperations.EffectOperationManager;
import effects.audio.params.Parameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ParameterMenu extends JPanel implements KeyListener {
    public EffectType effectType;
    public File selectedFile;

    public ParameterMenu(EffectType effectType, File selectedFile) {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Parameter grid ---
        JPanel gridPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (Parameter<?> parameter : effectType.getParameters()) {
            // Label (right-aligned)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.LINE_END;
            JLabel label = new JLabel(parameter.key + ":");
            gridPanel.add(label, gbc);

            // Field
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.anchor = GridBagConstraints.LINE_START;
            JTextField field = new JTextField(parameter.value.toString(), 20);
            gridPanel.add(field, gbc);

            row++;
        }

        // --- Button bar ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton goButton = new JButton("Go!");
        goButton.setPreferredSize(new Dimension(90, 28));
        goButton.addActionListener(_ -> {
            LoadingScreen screen = new LoadingScreen(effectType, selectedFile);
            Window.setWindow(screen);
            EffectOperationManager.init(screen);
        });
        buttonPanel.add(goButton);

        add(gridPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        this.effectType = effectType;
        this.selectedFile = selectedFile;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void keyTyped(KeyEvent event) {
        if(event.getKeyChar() == KeyEvent.VK_ESCAPE) {
            Window.setWindow(new MainMenu());
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {

    }

    @Override
    public void keyReleased(KeyEvent event) {

    }
}
