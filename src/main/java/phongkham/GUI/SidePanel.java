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

    JLabel lblTitle = new JLabel("Tiện ích");
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
    addMenuTitle("LOGIN AS GUEST");

    addMenuItem("Trang chủ", e -> mainFrame.showPanel("HOME"));
    addMenuItem("Đặt lịch khám", e -> mainFrame.showPanel("DATLICHKHAM"));
    addMenuItem("Dịch vụ", e -> mainFrame.showPanel("SERVICE"));
    addMenuItem("Giới thiệu", e -> mainFrame.showPanel("ABOUT"));
    addMenuItem("Liên hệ", e -> mainFrame.showPanel("CONTACT"));

    // Spacer đẩy nút Quay lại xuống dưới
    menuPanel.add(Box.createVerticalGlue());

    // Nút Quay lại nhỏ hơn
    addSmallButton("← Quay lại", e -> mainFrame.showLogin());
  }

  /**
   * Menu cho User đã đăng nhập
   */
  private void loadUserMenu() {
    // ========== BÁC SĨ ==========
    if (Session.hasPermission("BACSI_VIEW")) {
      addMenuTitle("👨‍⚕️ Bác sĩ");

      addMenuItem("Lịch làm việc", e -> mainFrame.showPanel("LICHLAMVIEC"));
      addMenuItem("Lịch khám", e -> mainFrame.showPanel("QUANLYLICHKHAM"));
      addMenuItem("Khám bệnh", e -> mainFrame.showPanel("KHAMBENH"));
      addMenuItem("Hóa đơn khám", e -> mainFrame.showPanel("HOADONKHAM"));
      addMenuItem("Hồ sơ cá nhân", e -> mainFrame.showPanel("BACSI_PROFILE"));

      addMenuSeparator();
    }

    // ========== NHÀ THUỐC ==========
    if (Session.hasPermission("NHATHUOC_VIEW")) {
      addMenuTitle("💊 Nhà thuốc");

      addMenuItem("Quản lý thuốc", e -> mainFrame.showPanel("QUANLYTHUOC"));
      addMenuItem("Phiếu nhập", e -> mainFrame.showPanel("PHIEUNHAP"));
      addMenuItem("🧾 Hóa đơn bán thuốc", e ->
        mainFrame.showPanel("HOADONTHUOC")
      );

      addMenuSeparator();
    }

    // ========== ADMIN ==========
    if (Session.hasPermission("ADMIN_VIEW")) {
      addMenuTitle("⚙️ Quản trị");

      addMenuItem("Quản lý khoa", e -> mainFrame.showPanel("QUANLYKHOA"));
      addMenuItem("Quản lý thuốc", e -> mainFrame.showPanel("QUANLYTHUOC"));
      addMenuItem("Quản lý phiếu nhập", e -> mainFrame.showPanel("PHIEUNHAP"));

      addMenuSeparator();
    }

    // Spacer đẩy nút Đăng xuất xuống dưới
    menuPanel.add(Box.createVerticalGlue());

    // ========== LOGOUT - Nút nhỏ ở dưới cùng ==========
    addSmallButton("Đăng xuất", e -> logout());
  }

  /**
   * Thêm tiêu đề menu
   */
  private void addMenuTitle(String title) {
    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblTitle.setForeground(new Color(148, 163, 184)); // Light gray
    lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
    lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    menuPanel.add(lblTitle);
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

  /**
   * Footer panel - Hiển thị user info
   */
  private JPanel createFooterPanel() {
    JPanel footer = new JPanel();
    footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
    footer.setBackground(new Color(15, 23, 42));
    footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    if (Session.isLoggedIn()) {
      JLabel lblUser = new JLabel("👤 " + Session.getCurrentUsername());
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
