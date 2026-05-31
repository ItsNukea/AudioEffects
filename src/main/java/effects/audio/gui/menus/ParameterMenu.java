package effects.audio.gui.menus;

import effects.audio.*;
import effects.audio.Window;
import effects.audio.params.Parameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ParameterMenu extends JPanel implements KeyListener {
    public EffectType effectType;

    public ParameterMenu(EffectType effectType) {
        super(new GridLayout(5, 5));
        Main.LOGGER.info("Initializing parameter menu");
        GridLayout layout = new GridLayout(effectType.getParameters().size(), 2);
        layout.setHgap(10);
        layout.setVgap(10);
        setLayout(layout);

        this.effectType = effectType;

        for(Parameter<?> parameter : effectType.getParameters()) {
            JLabel label = new JLabel(parameter.key);
            label.setSize(new Dimension(500, 20));
            label.setHorizontalAlignment(JLabel.RIGHT);

            JTextField field = new JTextField();
            field.setSize(new Dimension(50, 20));
            field.setText(parameter.value.toString());
            field.setEditable(true);
            field.setSize(new Dimension(100, 20));

            add(label);
            add(field);
        }

        int padding = 20;
        setPreferredSize(new Dimension(200, (effectType.getParameters().size() + padding) * 20 - padding));
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
