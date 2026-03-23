package phongkham.gui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CustomTextField extends JPanel {

  private final JLabel iconLabel;
  private final JTextField textField;
  private final String placeholder;
  private boolean focused;

  public CustomTextField(String iconText, String placeholder) {
    this.placeholder = placeholder;
    setLayout(new BorderLayout(10, 0));
    setOpaque(false);
    setBorder(new EmptyBorder(8, 12, 8, 12));

    iconLabel = new JLabel(iconText);
    iconLabel.setForeground(new Color(100, 116, 139));
    iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    iconLabel.setCursor(Cursor.getDefaultCursor());

    textField = new JTextField();
    textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    textField.setBorder(null);
    textField.setOpaque(false);

    textField.addFocusListener(
      new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
          focused = true;
          repaint();
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
          focused = false;
          repaint();
        }
      }
    );

    add(iconLabel, BorderLayout.WEST);
    add(textField, BorderLayout.CENTER);
  }

  public String getText() {
    return textField.getText();
  }

  public void setText(String text) {
    textField.setText(text);
  }

  public JTextField getInnerField() {
    return textField;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    g2.setColor(Color.WHITE);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

    g2.setColor(focused ? new Color(59, 130, 246) : new Color(203, 213, 225));
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

    g2.dispose();
    super.paintComponent(g);

    if (textField.getText().isEmpty() && !textField.isFocusOwner()) {
      Graphics2D g3 = (Graphics2D) g.create();
      g3.setColor(new Color(148, 163, 184));
      g3.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      Insets insets = getInsets();
      int x = insets.left + iconLabel.getWidth() + 10;
      int y = (getHeight() + g3.getFontMetrics().getAscent()) / 2 - 3;
      g3.drawString(placeholder, x, y);
      g3.dispose();
    }
  }
}
