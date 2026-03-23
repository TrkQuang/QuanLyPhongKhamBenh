package phongkham.gui.admin.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.components.ShadowPanel;

public class StatCard extends ShadowPanel {

  private static final Color BG_NORMAL = Color.WHITE;
  private static final Color BG_HOVER = new Color(241, 245, 255);

  private final JLabel valueLabel;

  public StatCard(String title, String value) {
    super(20);
    setLayout(new BorderLayout(0, 6));
    setBackground(BG_NORMAL);
    setPreferredSize(new Dimension(0, 108));
    setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
    valueLabel.setForeground(UIConstants.TEXT_MAIN);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    titleLabel.setForeground(UIConstants.TEXT_MUTED);

    JPanel textWrap = new JPanel();
    textWrap.setOpaque(false);
    textWrap.setLayout(
      new javax.swing.BoxLayout(textWrap, javax.swing.BoxLayout.Y_AXIS)
    );
    textWrap.add(valueLabel);
    textWrap.add(titleLabel);

    add(textWrap, BorderLayout.CENTER);

    addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
          setBackground(BG_HOVER);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          setBackground(BG_NORMAL);
        }
      }
    );
  }

  public void setValue(String value) {
    valueLabel.setText(value);
  }
}
