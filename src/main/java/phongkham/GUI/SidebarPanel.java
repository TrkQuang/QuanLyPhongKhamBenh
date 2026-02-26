package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

/**
 * SidebarPanel - CỰC KỲ ĐƠN GIẢN, KHÔNG ICON
 * CHỈ 60 DÒNG CODE!
 */
public class SidebarPanel extends JPanel {

  private MainFrame mainFrame;
  private JButton selectedButton;

  // ✅ ĐỊNH NGHĨA MENU - CHỈ CẦN TEXT VÀ PANEL NAME
  private static final String[][] MENUS = {
    { "Trang chủ", "HOME" },
    { "Dịch vụ", "SERVICE" },
    { "Đặt lịch khám", "DATLICHKHAM" },
    { "Quản lý lịch khám", "QUANLYLICHKHAM" },
    { "Phiếu nhập thuốc", "PHIEUNHAP" },
    { "Hóa đơn khám", "HOADONKHAM" },
    { "Hồ sơ bác sĩ", "BACSI_PROFILE" },
    { "Quản lý khoa", "QUANLYKHOA" },
    { "Đăng ký ca làm", "LICHLAMVIEC" },
    { "Liên hệ", "CONTACT" },
    { "Về chúng tôi", "ABOUT" },
  };

  public SidebarPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;

    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(200, 0));
    setBackground(Color.WHITE);
    setBorder(
      BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230))
    );

    // Menu panel
    JPanel menu = new JPanel();
    menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
    menu.setBackground(Color.WHITE);
    menu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    // ✅ TẠO TẤT CẢ NÚT - CHỈ 1 VÒNG LOOP
    JButton firstBtn = null;
    for (String[] item : MENUS) {
      JButton btn = createButton(item[0], item[1]);
      menu.add(btn);
      menu.add(Box.createRigidArea(new Dimension(0, 5)));
      if (firstBtn == null) firstBtn = btn;
    }

    menu.add(Box.createVerticalGlue());
    add(menu, BorderLayout.CENTER);

    // Chọn nút đầu tiên
    if (firstBtn != null) setSelected(firstBtn);
  }

  // ✅ TẠO NÚT - SIÊU ĐƠN GIẢN
  private JButton createButton(String text, String panelName) {
    JButton btn = new JButton(text);

    btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btn.setForeground(new Color(60, 60, 60));
    btn.setBackground(Color.WHITE);
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setPreferredSize(new Dimension(180, 40));
    btn.setMaximumSize(new Dimension(180, 40));
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    // Hover effect
    btn.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent e) {
          if (btn != selectedButton) {
            btn.setBackground(new Color(245, 245, 245));
          }
        }

        public void mouseExited(java.awt.event.MouseEvent e) {
          if (btn != selectedButton) {
            btn.setBackground(Color.WHITE);
          }
        }
      }
    );

    // Click action
    btn.addActionListener(e -> {
      setSelected(btn);
      mainFrame.showPanel(panelName);
    });

    return btn;
  }

  // ✅ SET SELECTED BUTTON
  private void setSelected(JButton btn) {
    // Reset nút cũ
    if (selectedButton != null) {
      selectedButton.setBackground(Color.WHITE);
      selectedButton.setForeground(new Color(60, 60, 60));
    }

    // Highlight nút mới
    selectedButton = btn;
    btn.setBackground(new Color(37, 99, 235));
    btn.setForeground(Color.WHITE);
  }
}
