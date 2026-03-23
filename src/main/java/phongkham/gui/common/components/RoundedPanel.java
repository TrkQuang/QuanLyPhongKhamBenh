package phongkham.gui.common.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

  private int radius = 16;
  private boolean drawShadow = true;
  private Color shadowColor = new Color(15, 23, 42, 30);

  public RoundedPanel() {
    setOpaque(false);
  }

  public RoundedPanel(int radius) {
    this();
    this.radius = radius;
  }

  public void setDrawShadow(boolean drawShadow) {
    this.drawShadow = drawShadow;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    int shadowOffset = drawShadow ? 4 : 0;
    if (drawShadow) {
      g2.setColor(shadowColor);
      g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, radius, radius);
    }

    g2.setColor(getBackground());
    g2.fillRoundRect(
      0,
      0,
      getWidth() - shadowOffset,
      getHeight() - shadowOffset,
      radius,
      radius
    );

    Stroke oldStroke = g2.getStroke();
    g2.setStroke(new BasicStroke(1f));
    g2.setColor(new Color(226, 232, 240));
    g2.drawRoundRect(
      0,
      0,
      getWidth() - shadowOffset,
      getHeight() - shadowOffset,
      radius,
      radius
    );
    g2.setStroke(oldStroke);

    g2.dispose();
    super.paintComponent(g);
  }
}
