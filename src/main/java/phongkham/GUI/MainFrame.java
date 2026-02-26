package phongkham.GUI;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;

/**
 * MainFrame - SIÊU ĐƠN GIẢN
 * CHỈ 80 DÒNG!
 */
public class MainFrame extends JFrame {

  private CardLayout cardLayout;
  private JPanel contentPanel;

  public MainFrame() {
    setTitle("Phòng Khám Đa Khoa");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1200, 700);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // ===== HEADER (1 dòng!) =====
    add(createHeader(), BorderLayout.NORTH);

    // ===== SIDEBAR (1 dòng!) =====
    add(new SidebarPanel(this), BorderLayout.WEST);

    // ===== CONTENT =====
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);
    contentPanel.setBackground(new Color(245, 247, 250));

    // ✅ Add panels (mỗi panel 1 dòng)
    addPanel("HOME", new HomePanel());
    addPanel("SERVICE", new ServicePanel());
    addPanel("CONTACT", new ContactPanel());
    addPanel("ABOUT", new AboutPanel());
    addPanel("PHIEUNHAP", new PhieuNhapPanel());
    addPanel("DATLICHKHAM", new DatLichKhamPanel());
    addPanel("QUANLYLICHKHAM", new LichKhamPanel());
    addPanel("HOADONKHAM", new HoaDonKhamPanel());
    addPanel("BACSI_PROFILE", new BacSiProfilePanel());
    addPanel("QUANLYKHOA", new KhoaPanel());
    addPanel("LICHLAMVIEC", new LichLamViecPanel());

    add(contentPanel, BorderLayout.CENTER);
    showPanel("HOME");
  }

  // ✅ Helper: Tạo Header nhanh
  private JPanel createHeader() {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.WHITE);
    p.setPreferredSize(new Dimension(0, 70));
    p.setBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))
    );

    // Left
    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
    left.setBackground(Color.WHITE);
    left.add(
      new JLabel("⚕  Phòng Khám Bệnh") {
        {
          setFont(new Font("Segoe UI", Font.BOLD, 20));
          setForeground(new Color(37, 99, 235));
        }
      }
    );
    p.add(left, BorderLayout.WEST);

    // Right
    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
    right.setBackground(Color.WHITE);
    right.add(
      new JLabel("HOTLINE: 1900-8888") {
        {
          setFont(new Font("Segoe UI", Font.BOLD, 14));
          setForeground(new Color(37, 99, 235));
        }
      }
    );

    JButton btn = new JButton("⚙ Đăng nhập");
    btn.setForeground(new Color(37, 99, 235));
    btn.setBackground(Color.WHITE);
    btn.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(37, 99, 235)),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)
      )
    );
    btn.setFocusPainted(false);
    btn.addActionListener(e -> {
      new LoginForm().setVisible(true);
      dispose();
    });
    right.add(btn);
    p.add(right, BorderLayout.EAST);

    return p;
  }

  // ✅ Helper: Add panel vào CardLayout
  private void addPanel(String name, JPanel panel) {
    contentPanel.add(panel, name);
  }

  // ✅ Chuyển panel
  public void showPanel(String name) {
    cardLayout.show(contentPanel, name);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {}
    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
  }
}
