package phongkham.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import phongkham.Utils.Session;

public class MainFrame extends JFrame {

  private CardLayout cardLayout;
  private JPanel contentPanel;
  private SidePanel sidePanel;

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
    addPanel("CONTACT", new ContactPanel());
    addPanel("PHIEUNHAP", new PhieuNhapPanel());
    addPanel("DATLICHKHAM", new DatLichKhamPanel());
    addPanel("QUANLYLICHKHAM", new LichKhamPanel());
    addPanel("KHAMBENH", new KhamBenhPanel());
    addPanel("HOADONKHAM", new HoaDonKhamPanel());
    addPanel("HOADONTHUOC", new HoaDonThuocPanel());
    addPanel("BACSI_PROFILE", new BacSiProfilePanel());
    addPanel("QUANLYKHOA", new KhoaPanel());
    addPanel("QUANLYTHUOC", new QuanLyThuocPanel());
    addPanel("QUANLYNCC", new QuanLyNhaCungCapPanel());
    addPanel("QUANLYGOIDV", new QuanLyGoiDichVuPanel());
    addPanel("QUANLYTAIKHOAN", new QuanLyTaiKhoanPanel());
    addPanel("PHANQUYEN", new QuanLyPhanQuyenPanel());
    addPanel("DASHBOARD", new DashboardPanel());
    addPanel("LICHLAMVIEC", new LichLamViecPanel());
    addPanel("MUATHUOC", new BanThuocPanel());

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
      new JLabel("PK  Phòng Khám Bệnh") {
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
        new JLabel("Tai khoan: " + Session.getCurrentUsername()) {
          {
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(37, 99, 235));
          }
        }
      );
    } else {
      right.add(
        new JLabel("Xin chao!") {
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
    if (!canAccessPanel(name)) {
      if (Session.isLoggedIn()) {
        JOptionPane.showMessageDialog(
          this,
          "Bạn không có quyền truy cập chức năng này.",
          "Từ chối truy cập",
          JOptionPane.WARNING_MESSAGE
        );
        cardLayout.show(contentPanel, "HOME");
      } else {
        cardLayout.show(contentPanel, "HOME");
      }
      return;
    }
    cardLayout.show(contentPanel, name);
  }

  private boolean canAccessPanel(String panelName) {
    if (panelName == null || panelName.isBlank()) {
      return false;
    }

    final Set<String> publicPanels = new HashSet<>(
      Arrays.asList("HOME", "CONTACT")
    );

    final Set<String> guestOnlyPanels = new HashSet<>(
      Arrays.asList("DATLICHKHAM", "MUATHUOC")
    );

    if (publicPanels.contains(panelName)) {
      return true;
    }

    if (!Session.isLoggedIn()) {
      return guestOnlyPanels.contains(panelName);
    }

    return switch (panelName) {
      case "DASHBOARD" -> hasAnyPermission("DASHBOARD_VIEW");
      case "QUANLYKHOA" -> hasAnyPermission("KHOA_VIEW");
      case "QUANLYTHUOC" -> hasAnyPermission("THUOC_VIEW", "THUOC_MANAGE");
      case "QUANLYGOIDV" -> hasAnyPermission(
        "GOIDICHVU_VIEW",
        "GOIDICHVU_MANAGE"
      );
      case "QUANLYNCC" -> hasAnyPermission("NCC_VIEW", "NCC_MANAGE");
      case "PHIEUNHAP" -> hasAnyPermission(
        "PHIEUNHAP_VIEW",
        "PHIEUNHAP_MANAGE"
      );
      case "HOADONTHUOC" -> hasAnyPermission(
        "HOADONTHUOC_VIEW",
        "HOADONTHUOC_CREATE",
        "HOADONTHUOC_MANAGE"
      );
      case "HOADONKHAM" -> hasAnyPermission(
        "HOADONKHAM_VIEW",
        "HOADONKHAM_MANAGE"
      );
      case "PHANQUYEN" -> hasAnyPermission(
        "PHANQUYEN_VIEW",
        "ROLE_PERMISSION_MANAGE"
      );
      case "QUANLYTAIKHOAN" -> hasAnyPermission("USER_MANAGE");
      case "LICHLAMVIEC" -> hasAnyPermission(
        "LICHLAMVIEC_VIEW",
        "LICHLAMVIEC_APPROVE"
      );
      case "QUANLYLICHKHAM" -> hasAnyPermission(
        "LICHKHAM_VIEW",
        "LICHKHAM_MANAGE"
      );
      case "KHAMBENH" -> hasAnyPermission("KHAMBENH_CREATE", "HOSO_MANAGE");
      case "BACSI_PROFILE" -> hasAnyPermission("BACSI_PROFILE_VIEW");
      case "DATLICHKHAM", "MUATHUOC" -> true;
      default -> false;
    };
  }

  private boolean hasAnyPermission(String... permissions) {
    if (permissions == null) {
      return false;
    }
    for (String permission : permissions) {
      if (Session.hasPermission(permission)) {
        return true;
      }
    }
    return false;
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

    // Chuyển trang đầu tiên user có quyền truy cập
    String targetPanel = "HOME";
    String[] panelPriority = {
      "DASHBOARD",
      "LICHLAMVIEC",
      "QUANLYTHUOC",
      "QUANLYNCC",
      "QUANLYGOIDV",
      "PHIEUNHAP",
      "QUANLYLICHKHAM",
      "KHAMBENH",
      "HOADONTHUOC",
      "HOADONKHAM",
      "QUANLYTAIKHOAN",
      "PHANQUYEN",
      "BACSI_PROFILE",
    };
    for (String panel : panelPriority) {
      if (canAccessPanel(panel)) {
        targetPanel = panel;
        break;
      }
    }

    showPanel(targetPanel);

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
