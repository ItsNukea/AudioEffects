package effects.audio.gui.menus.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class PrettyProgressBarUI extends BasicProgressBarUI {
    public PrettyProgressBarUI() {}

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(235, 235, 235));
        g2.fillRoundRect(0, 0, progressBar.getWidth(), progressBar.getHeight(), 17, 17);

        int progressWidth = (int) (progressBar.getPercentComplete() * progressBar.getWidth());
        if (progressWidth > 0) {
            g2.setPaint(new GradientPaint(
                    0, 0, new Color(76, 175, 80),
                    0, progressBar.getHeight(), new Color(46, 125, 50)));
            g2.fillRoundRect(0, 0, progressWidth, progressBar.getHeight(), 17, 17);
        }

        if (progressBar.isStringPainted()) {
            g2.setColor(Color.BLACK);
            g2.setFont(progressBar.getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = progressBar.getString();
            int x = (progressBar.getWidth() - fm.stringWidth(text)) / 2;
            int y = (progressBar.getHeight() + fm.getAscent()) / 2 - 2;
            g2.drawString(text, x, y);
        }
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(235, 235, 235));
        g2.fillRoundRect(0, 0, progressBar.getWidth(), progressBar.getHeight(), 17, 17);

        int boxWidth = progressBar.getWidth() / 6;
        int x = (int) (System.currentTimeMillis() / 50) % (progressBar.getWidth() + boxWidth) - boxWidth;
        g2.setPaint(new GradientPaint(
                x, 0, new Color(76, 175, 80, 150),
                x + boxWidth, 0, new Color(46, 125, 50, 200)));
        g2.fillRoundRect(Math.max(0, x), 0,
                Math.min(boxWidth, progressBar.getWidth() - Math.max(0, x)),
                progressBar.getHeight(), 17, 17);
    }
}
