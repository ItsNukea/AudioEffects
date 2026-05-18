package effects.audio.gui.menus;

import effects.audio.EffectType;

import javax.swing.*;
import java.awt.*;

public class ParameterMenu extends JPanel {
    public EffectType effectType;

    public ParameterMenu(EffectType effectType) {
        super();
        this.effectType = effectType;
        setLayout(new GridLayout(effectType.getParameters().size(), 2));
    }
}
