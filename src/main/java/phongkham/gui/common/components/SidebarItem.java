package phongkham.gui.common.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SidebarItem extends JPanel {

  private static final Color BG_NORMAL = new Color(30, 41, 59);
  private static final Color BG_HOVER = new Color(51, 65, 85);
  private static final Color BG_ACTIVE = new Color(59, 130, 246);
  private static final Color FG_NORMAL = new Color(241, 245, 249);
  private static final Color FG_ACTIVE = Color.WHITE;
  private static final Color ICON_NORMAL = new Color(203, 213, 225);
  private static final Color ICON_ACTIVE = Color.WHITE;
  private static final int ARC = 10;

  private final JLabel iconLabel;
  private final JComponent iconGap;
  private final JLabel textLabel;
  private final String fallbackIconText;
  private String currentIconText;
  private final List<ActionListener> actionListeners = new ArrayList<>();

  private boolean active;
  private boolean hovered;

  public SidebarItem(String text) {
    this(text, null);
  }

  public SidebarItem(String text, Icon icon) {
    setOpaque(false);
    setLayout(new BorderLayout());
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
    setPreferredSize(new Dimension(0, 48));

    JPanel content = new JPanel();
    content.setOpaque(false);
    content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
    content.setBorder(
      javax.swing.BorderFactory.createEmptyBorder(10, 14, 10, 14)
    );

    fallbackIconText = "•";
    currentIconText = fallbackIconText;
    iconLabel = new JLabel(fallbackIconText, SwingConstants.CENTER);
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
    iconLabel.setForeground(ICON_NORMAL);
    iconLabel.setPreferredSize(new Dimension(18, 18));
    iconLabel.setMinimumSize(new Dimension(18, 18));
    iconLabel.setMaximumSize(new Dimension(18, 18));

    if (icon != null) {
      iconLabel.setIcon(icon);
      iconLabel.setText("");
    }

    textLabel = new JLabel(text);
    textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    textLabel.setForeground(FG_NORMAL);

    iconGap = (JComponent) Box.createHorizontalStrut(10);
    content.add(iconLabel);
    content.add(iconGap);
    content.add(textLabel);

    add(content, BorderLayout.CENTER);

    MouseAdapter interactionHandler = new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        hovered = true;
        repaint();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        hovered = false;
        repaint();
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          fireActionEvent();
        }
      }
    };

    addMouseListener(interactionHandler);
    content.addMouseListener(interactionHandler);
    iconLabel.addMouseListener(interactionHandler);
    textLabel.addMouseListener(interactionHandler);

    updateForegrounds();
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    g2.setColor(resolveBackgroundColor());
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

    g2.dispose();
    super.paintComponent(g);
  }

  private Color resolveBackgroundColor() {
    if (active) {
      return BG_ACTIVE;
    }
    return hovered ? BG_HOVER : BG_NORMAL;
  }

  private void updateForegrounds() {
    textLabel.setForeground(active ? FG_ACTIVE : FG_NORMAL);
    iconLabel.setForeground(active ? ICON_ACTIVE : ICON_NORMAL);
    if (iconLabel.getIcon() == null) {
      iconLabel.setText(currentIconText);
    }
  }

  public void addActionListener(ActionListener listener) {
    if (listener != null) {
      actionListeners.add(listener);
    }
  }

  public void removeActionListener(ActionListener listener) {
    actionListeners.remove(listener);
  }

  private void fireActionEvent() {
    ActionEvent event = new ActionEvent(
      this,
      ActionEvent.ACTION_PERFORMED,
      textLabel.getText()
    );
    for (ActionListener listener : actionListeners) {
      listener.actionPerformed(event);
    }
  }

  public void setActive(boolean active) {
    this.active = active;
    textLabel.setFont(
      new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 15)
    );
    updateForegrounds();
    repaint();
  }

  public void setLeadingIconVisible(boolean visible) {
    iconLabel.setVisible(visible);
    iconGap.setVisible(visible);
  }

  public void setLeadingEmoji(String emoji) {
    if (emoji == null || emoji.trim().isEmpty()) {
      iconLabel.setIcon(null);
      currentIconText = fallbackIconText;
      iconLabel.setText(currentIconText);
      return;
    }
    iconLabel.setIcon(null);
    currentIconText = emoji;
    iconLabel.setText(currentIconText);
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
  }

  public void setLeadingIcon(Icon icon) {
    iconLabel.setIcon(icon);
    if (icon == null) {
      iconLabel.setText(currentIconText);
    } else {
      iconLabel.setText("");
    }
  }
}
