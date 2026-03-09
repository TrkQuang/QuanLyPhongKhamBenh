package phongkham.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;

public class LoginForm extends JFrame {

  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private UsersBUS usersBUS = new UsersBUS();
  private MainFrame mainFrame; // ✅ Reference đến MainFrame

  public LoginForm() {
    initUI();
  }

  /**
   * Set MainFrame reference (gọi từ MainFrame.showLogin())
   */
  public void setMainFrame(MainFrame frame) {
    this.mainFrame = frame;
  }

  private void initUI() {
    setTitle("Đăng nhập - Phòng Khám");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(500, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(20, 20)); // ✅ Chỉ 1 dòng!

    // ===== NORTH: TITLE =====
    JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitle.setForeground(new Color(41, 128, 185));
    lblTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
    add(lblTitle, BorderLayout.NORTH); // ✅ Dễ!

    // ===== CENTER: FORM =====
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
    formPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

    // Username
    formPanel.add(new JLabel("Tên đăng nhập:"));
    txtUsername = new JTextField("admin");
    formPanel.add(txtUsername);

    // Password
    formPanel.add(new JLabel("Mật khẩu:"));
    txtPassword = new JPasswordField("admin123");
    txtPassword.addActionListener(e -> login());
    formPanel.add(txtPassword);

    add(formPanel, BorderLayout.CENTER); // ✅ Dễ!

    // ===== SOUTH: BUTTON =====
    JButton btnLogin = new JButton("ĐĂNG NHẬP");
    btnLogin.setPreferredSize(new Dimension(150, 40));
    btnLogin.setBackground(new Color(41, 128, 185));
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setFocusPainted(false);
    btnLogin.addActionListener(e -> login());

    JButton btnGuest = new JButton("VÀO NHƯ KHÁCH");
    btnGuest.setPreferredSize(new Dimension(150, 40));
    btnGuest.setBackground(new Color(149, 165, 166));
    btnGuest.setForeground(Color.WHITE);
    btnGuest.setFocusPainted(false);
    btnGuest.addActionListener(e -> loginAsGuest());

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
    btnPanel.add(btnLogin);
    btnPanel.add(btnGuest);
    add(btnPanel, BorderLayout.SOUTH); // ✅ Dễ!
  }

  /**
   * Vào hệ thống như khách (không cần đăng nhập)
   */
  private void loginAsGuest() {
    // ✅ Không login → Session.currentUser = null
    Session.logout(); // Clear session nếu có

    // ✅ Đóng LoginForm
    dispose();

    // ✅ Tạo MainFrame mới với guest menu
    MainFrame newFrame = new MainFrame();
    newFrame.setVisible(true);
  }

  private void login() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ!");
      return;
    }

    UsersDTO user = usersBUS.login(username, password);

    if (user == null) {
      JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
      return;
    }

    // ✅ LOGIN thành công → Lưu vào Session (tự động load permissions)
    Session.login(user);

    // ✅ Tìm maBacSi bằng email
    BacSiBUS bacSiBUS = new BacSiBUS();
    BacSiDTO bacSi = bacSiBUS.getByEmail(user.getEmail());
    if (bacSi != null) {
      Session.setCurrentBacSiID(bacSi.getMaBacSi());
    }

    // ✅ Debug: In permissions
    Session.printInfo();

    // ✅ Đóng LoginForm
    dispose();

    // ✅ Callback về MainFrame để reload menu
    if (mainFrame != null) {
      mainFrame.onLoginSuccess();
    } else {
      // Nếu không có mainFrame (chạy standalone) → Tạo mới
      MainFrame newFrame = new MainFrame();
      newFrame.setVisible(true);
      newFrame.showPanel("LICHLAMVIEC");
    }
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {
      e.printStackTrace();
    }
    SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
  }
}
