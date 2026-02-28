package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

/**
 * ServicePanel - CỰC KỲ ĐƠN GIẢN
 * Từ 120 dòng → 50 dòng (-58%)
 */
public class ServicePanel extends JPanel {

  // ✅ Định nghĩa dịch vụ bằng mảng - DỄ HIỂU!
  private static final String[] SERVICES = {
    "Khám Tổng Quát",
    "Tiêm Chủng",
    "Xét Nghiệm",
    "Nha Khoa",
    "Khám Mắt",
    "Chăm Sóc Đặc Biệt",
  };

  public ServicePanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 247, 250));

    // ===== PANEL CHÍNH =====
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(new Color(245, 247, 250));
    content.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

    // ===== TIÊU ĐỀ =====
    content.add(
      createLabel("Dịch Vụ Y Tế", 32, Font.BOLD, new Color(30, 30, 30))
    );
    content.add(Box.createRigidArea(new Dimension(0, 10)));
    content.add(
      createLabel(
        "Các dịch vụ chăm sóc sức khỏe toàn diện",
        16,
        Font.PLAIN,
        new Color(100, 100, 100)
      )
    );
    content.add(Box.createRigidArea(new Dimension(0, 40)));

    // ===== LƯỚI DỊCH VỤ =====
    JPanel grid = new JPanel(new GridLayout(2, 3, 30, 30));
    grid.setBackground(new Color(245, 247, 250));

    // ✅ TẠO TẤT CẢ DỊCH VỤ - CHỈ 1 VÒNG LOOP
    for (String service : SERVICES) {
      grid.add(createServiceCard(service));
    }

    content.add(grid);
    content.add(Box.createVerticalGlue());
    add(content, BorderLayout.CENTER);
  }

  // ✅ Helper: Tạo Label nhanh
  private JLabel createLabel(String text, int size, int style, Color color) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", style, size));
    lbl.setForeground(color);
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    return lbl;
  }

  // ✅ Tạo Card dịch vụ - ĐƠN GIẢN
  private JPanel createServiceCard(String name) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(Color.WHITE);
    card.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
        BorderFactory.createEmptyBorder(30, 20, 30, 20)
      )
    );
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));

    JLabel label = new JLabel(name, SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.BOLD, 16));
    label.setForeground(new Color(30, 30, 30));
    card.add(label, BorderLayout.CENTER);

    // Hover effect
    card.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent e) {
          card.setBackground(new Color(240, 245, 250));
        }

        public void mouseExited(java.awt.event.MouseEvent e) {
          card.setBackground(Color.WHITE);
        }

        public void mouseClicked(java.awt.event.MouseEvent e) {
          JOptionPane.showMessageDialog(card, "Dịch vụ: " + name);
        }
      }
    );

    return card;
  }
}
