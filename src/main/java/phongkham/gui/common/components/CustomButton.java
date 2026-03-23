package phongkham.gui.common.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class CustomButton extends JButton {

  private final Color normalColor;
  private final Color hoverColor;
  private final Color pressedColor;
  private final int radius;

  public CustomButton(
    String text,
    Color normalColor,
    Color hoverColor,
    Color pressedColor,
    int radius
  ) {
    super(text);
    this.normalColor = normalColor;
    this.hoverColor = hoverColor;
    this.pressedColor = pressedColor;
    this.radius = radius;

    setForeground(Color.WHITE);
    setBorder(new EmptyBorder(10, 16, 10, 16));
    setOpaque(false);
    setFocusPainted(false);
    setBorderPainted(false);
    setContentAreaFilled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    if (getModel().isPressed()) {
      g2.setColor(pressedColor);
    } else if (getModel().isRollover()) {
      g2.setColor(hoverColor);
    } else {
      g2.setColor(normalColor);
    }

    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    g2.dispose();

    super.paintComponent(g);
  }
}
