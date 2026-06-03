package effects.audio.gui.menus.components;

import effects.audio.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StyledJButton extends JButton {
    private final Color baseColor;
    private final Color hoverColor;
    private final Color TEXT_LIGHT;

    public StyledJButton(String text, Color baseColor, Color hoverColor, Color TEXT_LIGHT) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
        this.TEXT_LIGHT = TEXT_LIGHT;
    }

    private float hoverAlpha = 0.0f;
    private Timer hoverTimer;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Button shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 12, 12);

        // Button gradient background
        Color currentBase = isEnabled() ? baseColor : new Color(158, 158, 158);
        Color currentHover = isEnabled() ? hoverColor : new Color(138, 138, 138);

        Color topColor = GuiUtil.interpolateColor(currentBase, currentHover, hoverAlpha);
        Color bottomColor = GuiUtil.interpolateColor(
                new Color(Math.max(0, currentBase.getRed() - 20),
                        Math.max(0, currentBase.getGreen() - 20),
                        Math.max(0, currentBase.getBlue() - 20)),
                new Color(Math.max(0, currentHover.getRed() - 20),
                        Math.max(0, currentHover.getGreen() - 20),
                        Math.max(0, currentHover.getBlue() - 20)),
                hoverAlpha
        );

        GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 12, 12);

        // Button border
        g2.setColor(new Color(255, 255, 255, 80));
        g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 5, 12, 12);

        // Text
        g2.setColor(TEXT_LIGHT);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 2;
        g2.drawString(text, x, y);

        g2.dispose();
    }

    {
        setPreferredSize(new Dimension(160, 45));
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    animateHover(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateHover(false);
            }
        });
    }

    private void animateHover(boolean entering) {
        if (hoverTimer != null) hoverTimer.stop();

        hoverTimer = new Timer(20, _ -> {
                if (entering) {
                    hoverAlpha += 0.1f;
                    if (hoverAlpha >= 1.0f) {
                        hoverAlpha = 1.0f;
                        hoverTimer.stop();
                    }
                } else {
                    hoverAlpha -= 0.1f;
                    if (hoverAlpha <= 0.0f) {
                        hoverAlpha = 0.0f;
                        hoverTimer.stop();
                    }
                }
                repaint();
        });
        hoverTimer.start();
    }
}
