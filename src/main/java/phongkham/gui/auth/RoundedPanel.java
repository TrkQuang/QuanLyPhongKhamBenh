package phongkham.gui.auth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

  private final int radius;
  private final boolean shadow;

  public RoundedPanel(int radius, boolean shadow) {
    this.radius = radius;
    this.shadow = shadow;
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    if (shadow) {
      g2.setColor(new Color(15, 23, 42, 28));
      g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 6, radius, radius);
    }

    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
    g2.setColor(new Color(226, 232, 240));
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

    g2.dispose();
    super.paintComponent(g);
  }
}
