package effects.audio.gui;

import effects.audio.gui.menus.components.StyledJButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiUtil {

    // -------------------------------------------------------------------------
    // Shared color palette
    // -------------------------------------------------------------------------

    public static final Color PRIMARY_COLOR      = new Color(46, 125, 50);   // Forest Green
    public static final Color PRIMARY_HOVER      = new Color(27, 94, 32);    // Dark Green
    public static final Color SUCCESS_COLOR      = new Color(76, 175, 80);   // Light Green
    public static final Color SUCCESS_HOVER      = new Color(56, 142, 60);   // Medium Green
    public static final Color SECONDARY_COLOR    = new Color(69, 90, 100);   // Blue Grey
    public static final Color SECONDARY_HOVER    = new Color(55, 71, 79);    // Dark Blue Grey
    public static final Color BACKGROUND_START   = new Color(27, 27, 27);    // Dark Grey/Black
    public static final Color BACKGROUND_END     = new Color(46, 125, 50);   // Forest Green
    public static final Color TEXT_LIGHT         = new Color(255, 255, 255);
    public static final Color TEXT_DARK          = new Color(33, 33, 33);
    public static final Color CARD_BACKGROUND    = new Color(255, 255, 255, 250);

    // -------------------------------------------------------------------------
    // Background painting
    // -------------------------------------------------------------------------

    /**
     * Paints an animated gradient background onto {@code g} and schedules a
     * single repaint via {@code repaintCallback} so the animation keeps running.
     */
    public static void paintBackground(Graphics g, Runnable repaintCallback, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        long time = System.currentTimeMillis();
        float offset = (float) (Math.sin(time * 0.001) * 0.1 + 0.5);

        GradientPaint gradient = new GradientPaint(
                0, 0, interpolateColor(BACKGROUND_START, BACKGROUND_END, offset),
                width, height, interpolateColor(BACKGROUND_END, BACKGROUND_START, offset)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        // Subtle dot-pattern overlay
        g2.setColor(new Color(255, 255, 255, 15));
        for (int x = 0; x < width; x += 40)
            for (int y = 0; y < height; y += 40)
                g2.fillOval(x, y, 2, 2);

        g2.dispose();

        Timer repaintTimer = new Timer(50, _ -> repaintCallback.run());
        repaintTimer.setRepeats(false);
        repaintTimer.start();
    }

    // -------------------------------------------------------------------------
    // Color utilities
    // -------------------------------------------------------------------------

    /**
     * Calculates a color between {@code c1} and {@code c2} linearly
     * @param c1 The first color to use
     * @param c2 The second color to use
     * @param factor The interpolation factor (0.0 = c1, 1.0 = c2)
     * @return The interpolated color
     */
    public static Color interpolateColor(Color c1, Color c2, float factor) {
        return new Color(
                (int) (c1.getRed()   + factor * (c2.getRed()   - c1.getRed())),
                (int) (c1.getGreen() + factor * (c2.getGreen() - c1.getGreen())),
                (int) (c1.getBlue()  + factor * (c2.getBlue()  - c1.getBlue()))
        );
    }

    // -------------------------------------------------------------------------
    // Card / layout helpers
    // -------------------------------------------------------------------------

    /**
     * Creates the rounded white card panel that all screens share.
     * The caller is responsible for setting a layout and adding children.
     */
    public static JPanel makeMainCard(Color cardBackground) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(8, 8, getWidth() - 8, getHeight() - 8, 25, 25);
                g2.setColor(cardBackground);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 25, 25);

                g2.dispose();
            }
        };
    }

    /**
     * Wraps {@code mainCard} in a centred {@link GridBagLayout} wrapper and
     * adds it to {@code panel}.  The card is configured with a standard
     * {@link BorderLayout} and default padding.
     *
     * @param vPad    vertical padding inside the card
     * @param hPad   horizontal padding inside the card
     * @param vgap      vertical gap between card sections
     */
    public static JPanel buildCardLayout(JPanel panel, int vPad, int hPad, int vgap) {
        JPanel mainCard = makeMainCard(CARD_BACKGROUND);
        mainCard.setOpaque(false);
        mainCard.setLayout(new BorderLayout(0, vgap));
        mainCard.setBorder(BorderFactory.createEmptyBorder(vPad, hPad, vPad, hPad));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.weightx = 1.0;
        wgbc.weighty = 1.0;
        wrapper.add(mainCard, wgbc);

        panel.setLayout(new BorderLayout());
        panel.add(wrapper, BorderLayout.CENTER);

        return mainCard; // caller adds NORTH / CENTER / SOUTH children
    }

    /** Calls {@link #buildCardLayout} with the standard 60 / 80 / 30 values. */
    public static JPanel buildCardLayout(JPanel panel) {
        return buildCardLayout(panel, 60, 80, 30);
    }

    // -------------------------------------------------------------------------
    // Button factory
    // -------------------------------------------------------------------------

    /** Creates a {@link StyledJButton} using the shared color constants. */
    public static JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        return new StyledJButton(text, baseColor, hoverColor, TEXT_LIGHT);
    }
}