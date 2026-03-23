package phongkham.gui.common.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CustomTextField extends JTextField {

  private final String placeholder;
  private final int radius;

  public CustomTextField(String placeholder, int columns) {
    super(columns);
    this.placeholder = placeholder;
    this.radius = 14;
    setOpaque(false);
    setBorder(new EmptyBorder(10, 14, 10, 14));
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );
    g2.setColor(Color.WHITE);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    g2.setColor(new Color(203, 213, 225));
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
    g2.dispose();

    super.paintComponent(g);

    if (getText().isEmpty() && !isFocusOwner()) {
      Insets insets = getInsets();
      g.setColor(new Color(148, 163, 184));
      g.drawString(placeholder, insets.left, getHeight() / 2 + 4);
    }
  }
}
