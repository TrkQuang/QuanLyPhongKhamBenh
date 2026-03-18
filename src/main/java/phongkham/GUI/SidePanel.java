package phongkham.gui;

import java.awt.*;
import javax.swing.*;
import phongkham.Utils.Session;

public class SidePanel extends JPanel {

  private JPanel menuPanel;
  private MainFrame mainFrame;

  public SidePanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    initComponents();
    loadMenu();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(250, 600));
    setBackground(new Color(30, 41, 59)); // Dark blue

    // Header
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(15, 23, 42));
    headerPanel.setPreferredSize(new Dimension(250, 80));

    JLabel lblTitle = new JLabel("Menu");
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblTitle.setForeground(Color.WHITE);
    headerPanel.add(lblTitle);

    add(headerPanel, BorderLayout.NORTH);

    // Menu Panel
    menuPanel = new JPanel();
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
    menuPanel.setBackground(new Color(30, 41, 59));

    JScrollPane scrollPane = new JScrollPane(menuPanel);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    // Làm scrollpane hòa vào màu nền
    scrollPane.setBackground(new Color(30, 41, 59));
    scrollPane.getViewport().setBackground(new Color(30, 41, 59));
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);

    // Tùy chỉnh scrollbar để hòa vào
    scrollPane.getVerticalScrollBar().setBackground(new Color(30, 41, 59));
    scrollPane
      .getVerticalScrollBar()
      .setUI(
        new javax.swing.plaf.basic.BasicScrollBarUI() {
          @Override
          protected void configureScrollBarColors() {
            this.thumbColor = new Color(71, 85, 105); // Màu thumb sáng hơn một chút
            this.trackColor = new Color(30, 41, 59); // Màu track giống nền
          }

          @Override
          protected JButton createDecreaseButton(int orientation) {
            JButton button = super.createDecreaseButton(orientation);
            button.setPreferredSize(new Dimension(0, 0));
            return button;
          }

          @Override
          protected JButton createIncreaseButton(int orientation) {
            JButton button = super.createIncreaseButton(orientation);
            button.setPreferredSize(new Dimension(0, 0));
            return button;
          }
        }
      );

    add(scrollPane, BorderLayout.CENTER);

    // Footer - User info
    JPanel footerPanel = createFooterPanel();
    add(footerPanel, BorderLayout.SOUTH);
  }

  /**
   * Load menu theo quyền
   */
  public void loadMenu() {
    menuPanel.removeAll();

    // Nếu chưa login → Guest menu
    if (!Session.isLoggedIn()) {
      loadGuestMenu();
    } else {
      // Đã login → Load theo permission
      loadUserMenu();
    }

    menuPanel.revalidate();
    menuPanel.repaint();
  }

  /**
   * Menu cho Guest (chưa đăng nhập)
   */
  private void loadGuestMenu() {
    addMenuItem("Trang chủ", e -> mainFrame.showPanel("HOME"));
    addMenuItem("Đặt lịch khám", e -> mainFrame.showPanel("DATLICHKHAM"));
    addMenuItem("Mua thuốc", e -> mainFrame.showPanel("MUATHUOC"));
    addMenuItem("Liên hệ", e -> mainFrame.showPanel("CONTACT"));

    // Spacer đẩy nút Quay lại xuống dưới
    menuPanel.add(Box.createVerticalGlue());

    // Nút Quay lại nhỏ hơn
    addSmallButton("Đăng nhập", e -> mainFrame.showLogin());
  }

  /**
   * Menu cho User đã đăng nhập - PERMISSION BASED
   */
  private void loadUserMenu() {
    // Dashboard
    if (hasAnyPermission("DASHBOARD_VIEW")) {
      addMenuItem(" Dashboard", e -> mainFrame.showPanel("DASHBOARD"));
    }

    // Quản lý khoa
    if (hasAnyPermission("KHOA_VIEW")) {
      addMenuItem("Quản lý khoa", e -> mainFrame.showPanel("QUANLYKHOA"));
    }

    // Quản lý thuốc
    if (hasAnyPermission("THUOC_VIEW", "THUOC_MANAGE")) {
      addMenuItem("Quản lý thuốc", e -> mainFrame.showPanel("QUANLYTHUOC"));
    }

    // Quản lý gói dịch vụ
    if (hasAnyPermission("GOIDICHVU_VIEW", "GOIDICHVU_MANAGE")) {
      addMenuItem("Quản lý gói dịch vụ", e ->
        mainFrame.showPanel("QUANLYGOIDV")
      );
    }

    // Quản lý nhà cung cấp
    if (hasAnyPermission("NCC_VIEW", "NCC_MANAGE")) {
      addMenuItem("Quản lý nhà cung cấp", e ->
        mainFrame.showPanel("QUANLYNCC")
      );
    }

    // Quản lý phiếu nhập
    if (hasAnyPermission("PHIEUNHAP_VIEW", "PHIEUNHAP_MANAGE")) {
      addMenuItem("Quản lý phiếu nhập", e -> mainFrame.showPanel("PHIEUNHAP"));
    }

    // Hóa đơn thuốc
    if (
      hasAnyPermission(
        "HOADONTHUOC_VIEW",
        "HOADONTHUOC_CREATE",
        "HOADONTHUOC_MANAGE"
      )
    ) {
      addMenuItem("Hóa đơn thuốc", e -> mainFrame.showPanel("HOADONTHUOC"));
    }

    // Hóa đơn khám
    if (hasAnyPermission("HOADONKHAM_VIEW", "HOADONKHAM_MANAGE")) {
      addMenuItem("Hóa đơn khám", e -> mainFrame.showPanel("HOADONKHAM"));
    }

    // Quản lý phân quyền
    if (hasAnyPermission("PHANQUYEN_VIEW", "ROLE_PERMISSION_MANAGE")) {
      addMenuItem("Quản lý phân quyền", e -> mainFrame.showPanel("PHANQUYEN"));
    }

    // Quản lý tài khoản
    if (hasAnyPermission("USER_MANAGE")) {
      addMenuItem("Quản lý tài khoản", e ->
        mainFrame.showPanel("QUANLYTAIKHOAN")
      );
    }

    // Lịch làm việc
    if (hasAnyPermission("LICHLAMVIEC_VIEW", "LICHLAMVIEC_APPROVE")) {
      addMenuItem("Lịch làm việc", e -> mainFrame.showPanel("LICHLAMVIEC"));
    }

    // Lịch khám bệnh
    if (hasAnyPermission("LICHKHAM_VIEW", "LICHKHAM_MANAGE")) {
      addMenuItem("Lịch khám bệnh", e -> mainFrame.showPanel("QUANLYLICHKHAM"));
    }

    // Khám bệnh
    if (hasAnyPermission("KHAMBENH_CREATE", "HOSO_MANAGE")) {
      addMenuItem("Khám bệnh", e -> mainFrame.showPanel("KHAMBENH"));
    }

    // Hồ sơ bác sĩ
    if (hasAnyPermission("BACSI_PROFILE_VIEW")) {
      addMenuItem("Hồ sơ cá nhân", e -> mainFrame.showPanel("BACSI_PROFILE"));
    }

    // Bán thuốc
    if (hasAnyPermission("HOADONTHUOC_CREATE", "HOADONTHUOC_MANAGE")) {
      addMenuItem("Bán thuốc", e -> mainFrame.showPanel("MUATHUOC"));
    }

    // Separator
    addMenuSeparator();

    // Spacer đẩy nút Đăng xuất xuống dưới
    menuPanel.add(Box.createVerticalGlue());

    // Logout
    addSmallButton("Đăng xuất", e -> logout());
  }

  /**
   * Thêm nút nhỏ (cho Quay lại)
   */
  private void addSmallButton(
    String text,
    java.awt.event.ActionListener action
  ) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    btn.setForeground(new Color(148, 163, 184));
    btn.setBackground(new Color(30, 41, 59));
    btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    btn.setFocusPainted(false);
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setMaximumSize(new Dimension(250, 35));
    btn.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Hover effect
    btn.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          btn.setBackground(new Color(51, 65, 85));
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
          btn.setBackground(new Color(30, 41, 59));
        }
      }
    );

    btn.addActionListener(action);

    menuPanel.add(Box.createVerticalStrut(10));
    menuPanel.add(btn);
    menuPanel.add(Box.createVerticalStrut(10));
  }

  /**
   * Thêm menu item
   */
  private void addMenuItem(String text, java.awt.event.ActionListener action) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btn.setForeground(Color.WHITE);
    btn.setBackground(new Color(30, 41, 59));
    btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    btn.setFocusPainted(false);
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setMaximumSize(new Dimension(250, 45));
    btn.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Hover effect
    btn.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          btn.setBackground(new Color(51, 65, 85));
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
          btn.setBackground(new Color(30, 41, 59));
        }
      }
    );

    btn.addActionListener(action);

    menuPanel.add(btn);
  }

  /**
   * Thêm separator
   */
  private void addMenuSeparator() {
    menuPanel.add(Box.createVerticalStrut(10));
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
   * Footer panel - Hiển thị user info
   */
  private JPanel createFooterPanel() {
    JPanel footer = new JPanel();
    footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
    footer.setBackground(new Color(15, 23, 42));
    footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    if (Session.isLoggedIn()) {
      JLabel lblUser = new JLabel(">> " + Session.getCurrentUsername());
      lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
      lblUser.setForeground(new Color(148, 163, 184));
      footer.add(lblUser);
    } else {
      JLabel lblGuest = new JLabel("Khách");
      lblGuest.setFont(new Font("Segoe UI", Font.BOLD, 12));
      lblGuest.setForeground(new Color(148, 163, 184));
      footer.add(lblGuest);
    }

    return footer;
  }

  /**
   * Đăng xuất
   */
  private void logout() {
    int confirm = JOptionPane.showConfirmDialog(
      mainFrame,
      "Bạn có chắc muốn đăng xuất?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      Session.logout();
      mainFrame.onLogout();
    }
  }
}
