package effects.audio.gui.menus.components;

import effects.audio.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class EffectTypeJMenuItem extends JMenuItem {
    public EffectTypeJMenuItem(String text) {
        super(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isArmed()) {
            g2.setColor(new Color(120, 119, 241, 30));
            g2.fillRoundRect(5, 2, getWidth() - 10, getHeight() - 4, 8, 8);
        }

        g2.setColor(GuiUtil.TEXT_DARK);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(getText(), 15, (getHeight() + fm.getAscent()) / 2 - 2);
        g2.dispose();
    }
}
