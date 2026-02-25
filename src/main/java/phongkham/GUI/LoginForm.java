package phongkham.GUI;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;
import phongkham.dao.PermissionsDAO;

/**
 * Form đăng nhập hệ thống quản lý phòng khám
 * Tích hợp RBAC (Role-Based Access Control)
 */
public class LoginForm extends JFrame {

  // ==================== COMPONENTS ====================
  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private JButton btnLogin;
  private JLabel lblMessage;

  // ==================== BUSINESS LOGIC ====================
  // >>> CHỈNH TÊN BUS TẠI ĐÂY NẾU CẦN <<<
  private UsersBUS usersBUS;
  private PermissionsDAO permissionsDAO;

  // ==================== COLORS ====================
  private final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
  private final Color HOVER_COLOR = new Color(100, 149, 237); // Cornflower Blue
  private final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light Gray
  private final Color PANEL_COLOR = Color.WHITE;
  private final Color TEXT_COLOR = new Color(51, 51, 51);
  private final Color ERROR_COLOR = new Color(220, 53, 69);

  // ==================== CONSTRUCTOR ====================
  public LoginForm() {
    // Khởi tạo BUS layer
    initBUS();

    // Khởi tạo giao diện
    initComponents();

    // Căn giữa màn hình
    setLocationRelativeTo(null);
  }

  // ==================== INIT BUS ====================
  /**
   * Khởi tạo các đối tượng BUS
   * >>> CHỈNH TẠI ĐÂY NẾU TÊN BUS KHÁC <<<
   */
  private void initBUS() {
    try {
      usersBUS = new UsersBUS();
      permissionsDAO = new PermissionsDAO();
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(
        this,
        "Lỗi khởi tạo hệ thống: " + e.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  // ==================== INIT COMPONENTS ====================
  private void initComponents() {
    // Cấu hình JFrame
    setTitle("Đăng Nhập - Hệ Thống Quản Lý Phòng Khám");
    setSize(1200, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    // Panel chính với background màu nhạt
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(BACKGROUND_COLOR);

    // Panel login ở giữa
    JPanel loginPanel = createLoginPanel();

    // Wrapper để căn giữa
    JPanel centerWrapper = new JPanel(new GridBagLayout());
    centerWrapper.setBackground(BACKGROUND_COLOR);
    centerWrapper.add(loginPanel);

    mainPanel.add(centerWrapper, BorderLayout.CENTER);

    // Thêm vào frame
    add(mainPanel);
  }

  // ==================== CREATE LOGIN PANEL ====================
  private JPanel createLoginPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(PANEL_COLOR);
    panel.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(new Color(220, 220, 220), 1, true),
        new EmptyBorder(40, 60, 40, 60)
      )
    );

    // Bo góc nhẹ bằng shadow effect
    panel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
        new EmptyBorder(40, 60, 40, 60)
      )
    );

    // ===== TIÊU ĐỀ =====
    JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ PHÒNG KHÁM");
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
    lblTitle.setForeground(PRIMARY_COLOR);
    lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== SUBTITLE =====
    JLabel lblSubtitle = new JLabel("Đăng nhập hệ thống");
    lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    lblSubtitle.setForeground(TEXT_COLOR);
    lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== ICON (optional) =====
    JLabel lblIcon = new JLabel("🏥");
    lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
    lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== USERNAME =====
    JLabel lblUsername = new JLabel("Tên đăng nhập");
    lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblUsername.setForeground(TEXT_COLOR);
    lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

    txtUsername = new JTextField(20);
    txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtUsername.setMaximumSize(new Dimension(350, 40));
    txtUsername.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(new Color(200, 200, 200), 1, true),
        new EmptyBorder(5, 10, 5, 10)
      )
    );

    // Enter để focus password
    txtUsername.addActionListener(e -> txtPassword.requestFocus());

    // ===== PASSWORD =====
    JLabel lblPassword = new JLabel("Mật khẩu");
    lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblPassword.setForeground(TEXT_COLOR);
    lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

    txtPassword = new JPasswordField(20);
    txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtPassword.setMaximumSize(new Dimension(350, 40));
    txtPassword.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(new Color(200, 200, 200), 1, true),
        new EmptyBorder(5, 10, 5, 10)
      )
    );

    // Enter để login
    txtPassword.addActionListener(e -> actionLogin());

    // ===== MESSAGE LABEL =====
    lblMessage = new JLabel(" ");
    lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblMessage.setForeground(ERROR_COLOR);
    lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== LOGIN BUTTON =====
    btnLogin = new JButton("ĐĂNG NHẬP");
    btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setBackground(PRIMARY_COLOR);
    btnLogin.setFocusPainted(false);
    btnLogin.setBorderPainted(false);
    btnLogin.setMaximumSize(new Dimension(350, 45));
    btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Hover effect
    btnLogin.addMouseListener(
      new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          btnLogin.setBackground(HOVER_COLOR);
        }

        public void mouseExited(MouseEvent e) {
          btnLogin.setBackground(PRIMARY_COLOR);
        }
      }
    );

    // Action
    btnLogin.addActionListener(e -> actionLogin());

    // ===== LAYOUT =====
    panel.add(lblIcon);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(lblTitle);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(lblSubtitle);
    panel.add(Box.createRigidArea(new Dimension(0, 40)));

    panel.add(lblUsername);
    panel.add(Box.createRigidArea(new Dimension(0, 8)));
    panel.add(txtUsername);
    panel.add(Box.createRigidArea(new Dimension(0, 20)));

    panel.add(lblPassword);
    panel.add(Box.createRigidArea(new Dimension(0, 8)));
    panel.add(txtPassword);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));

    panel.add(lblMessage);
    panel.add(Box.createRigidArea(new Dimension(0, 20)));

    panel.add(btnLogin);

    return panel;
  }

  // ==================== LOGIN ACTION ====================
  /**
   * Xử lý đăng nhập
   * >>> LOGIC CHÍNH CỦA HỆ THỐNG <<<
   */
  private void actionLogin() {
    // Lấy thông tin từ form
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();

    // Validate
    if (username.isEmpty() || password.isEmpty()) {
      showError("Vui lòng nhập đầy đủ thông tin!");
      return;
    }

    // Disable button khi đang xử lý
    btnLogin.setEnabled(false);
    btnLogin.setText("Đang xử lý...");
    lblMessage.setText("Đang đăng nhập...");
    lblMessage.setForeground(PRIMARY_COLOR);

    // === BƯỚC 1: GỌI BUS ĐỂ ĐĂNG NHẬP ===
    // >>> CHỖ NÀY GỌI LOGIC THẬT CỦA HỆ THỐNG <<<
    UsersDTO user = usersBUS.login(username, password);

    // Kiểm tra kết quả
    if (user == null) {
      // Đăng nhập thất bại
      showError("Sai tài khoản hoặc mật khẩu!");
      btnLogin.setEnabled(true);
      btnLogin.setText("ĐĂNG NHẬP");
      return;
    }

    // === BƯỚC 2: ĐĂNG NHẬP THÀNH CÔNG - LƯU VÀO SESSION ===
    Session.login(user);

    // === BƯỚC 3: LẤY PERMISSIONS CỦA USER ===
    // >>> TÍCH HỢP RBAC TẠI ĐÂY <<<
    try {
      ArrayList<String> permissions = permissionsDAO.getPermissionByUser(
        user.getUserID()
      );
      Session.currentPermissions = permissions;

      // Log kết quả (có thể bỏ sau khi test)
      System.out.println("User: " + Session.getCurrentUsername());
      System.out.println("Permissions: " + Session.currentPermissions.size());
      for (String perm : permissions) {
        System.out.println("  - " + perm);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Lỗi khi load permissions: " + e.getMessage());
      // Vẫn cho phép đăng nhập nhưng không có quyền
      Session.currentPermissions = new ArrayList<>();
    }

    // === BƯỚC 4: MỞ MAIN FRAME ===
    openMainFrame();

    // === BƯỚC 5: ĐÓNG LOGIN FORM ===
    this.dispose();
  }

  // ==================== OPEN MAIN FRAME ====================
  /**
   * Mở màn hình chính sau khi đăng nhập thành công
   * >>> CHỈNH TÊN CLASS MAINFRAME TẠI ĐÂY NẾU CẦN <<<
   */
  private void openMainFrame() {
    SwingUtilities.invokeLater(() -> {
      try {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
          this,
          "Lỗi khi mở màn hình chính: " + e.getMessage(),
          "Lỗi",
          JOptionPane.ERROR_MESSAGE
        );
      }
    });
  }

  // ==================== SHOW ERROR ====================
  private void showError(String message) {
    lblMessage.setText(message);
    lblMessage.setForeground(ERROR_COLOR);
  }

  // ==================== MAIN (TEST STANDALONE) ====================
  /**
   * Main để test form login độc lập
   */
  public static void main(String[] args) {
    // Áp dụng FlatLaf
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Chạy form
    SwingUtilities.invokeLater(() -> {
      LoginForm loginForm = new LoginForm();
      loginForm.setVisible(true);
    });
  }
}
