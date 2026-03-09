package phongkham.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;
import phongkham.Utils.Session;

public class MainFrame extends JFrame {

  private CardLayout cardLayout;
  private JPanel contentPanel;
  private SidePanel sidePanel; // ✅ Dùng SidePanel có kiểm tra quyền

  public MainFrame() {
    setTitle("Phòng Khám Đa Khoa");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1200, 700);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // ===== HEADER =====
    add(createHeader(), BorderLayout.NORTH);

    // ===== SIDEBAR (SidePanel có kiểm tra quyền) =====
    sidePanel = new SidePanel(this);
    add(sidePanel, BorderLayout.WEST);

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
    addPanel("KHAMBENH", new KhamBenhPanel());
    addPanel("HOADONKHAM", new HoaDonKhamPanel());
    addPanel("HOADONTHUOC", new HoaDonThuocPanel());
    addPanel("BACSI_PROFILE", new BacSiProfilePanel());
    addPanel("QUANLYKHOA", new KhoaPanel());
    addPanel("QUANLYTHUOC", new QuanLyThuocPanel());
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

    // ✅ Hiển thị username hoặc Hello!
    if (Session.isLoggedIn()) {
      right.add(
        new JLabel("👤 " + Session.getCurrentUsername()) {
          {
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(37, 99, 235));
          }
        }
      );
    } else {
      right.add(
        new JLabel("👋 Hello!") {
          {
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(37, 99, 235));
          }
        }
      );
    }

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

  /**
   * ✅ Hiển thị form đăng nhập
   */
  public void showLogin() {
    this.setVisible(false);
    LoginForm loginForm = new LoginForm();
    loginForm.setMainFrame(this);
    loginForm.setVisible(true);
  }

  /**
   * ✅ Callback sau khi đăng nhập thành công
   */
  public void onLoginSuccess() {
    // Refresh UI
    getContentPane().removeAll();
    setLayout(new BorderLayout());
    add(createHeader(), BorderLayout.NORTH);
    add(sidePanel, BorderLayout.WEST);
    add(contentPanel, BorderLayout.CENTER);

    // Reload menu theo quyền
    sidePanel.loadMenu();

    // ✅ DEBUG: In ra permissions
    System.out.println("===== DEBUG PERMISSIONS =====");
    System.out.println("Username: " + Session.getCurrentUsername());
    System.out.println(
      "Has ADMIN_VIEW: " + Session.hasPermission("ADMIN_VIEW")
    );
    System.out.println(
      "Has NHATHUOC_VIEW: " + Session.hasPermission("NHATHUOC_VIEW")
    );
    System.out.println(
      "Has BACSI_VIEW: " + Session.hasPermission("BACSI_VIEW")
    );
    System.out.println("=============================");

    // Chuyển trang theo quyền
    if (Session.hasPermission("ADMIN_VIEW")) {
      System.out.println("→ Chuyển đến QUANLYKHOA (Admin)");
      showPanel("QUANLYKHOA");
    } else if (Session.hasPermission("NHATHUOC_VIEW")) {
      System.out.println("→ Chuyển đến QUANLYTHUOC (Nhà thuốc)");
      showPanel("QUANLYTHUOC");
    } else if (Session.hasPermission("BACSI_VIEW")) {
      System.out.println("→ Chuyển đến LICHLAMVIEC (Bác sĩ)");
      showPanel("LICHLAMVIEC");
    } else {
      System.out.println("→ Chuyển đến HOME (Guest)");
      showPanel("HOME");
    }

    revalidate();
    repaint();
    this.setVisible(true);

    JOptionPane.showMessageDialog(
      this,
      "Đăng nhập thành công!\nChào mừng " + Session.getCurrentUsername(),
      "Thông báo",
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  /**
   * ✅ Callback khi đăng xuất
   */
  public void onLogout() {
    JOptionPane.showMessageDialog(
      this,
      "Đã đăng xuất!",
      "Thông báo",
      JOptionPane.INFORMATION_MESSAGE
    );

    // Đóng MainFrame
    this.dispose();

    // Mở LoginForm
    LoginForm loginForm = new LoginForm();
    loginForm.setVisible(true);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {}
    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
  }
}
