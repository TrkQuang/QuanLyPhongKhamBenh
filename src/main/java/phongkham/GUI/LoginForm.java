package phongkham.GUI;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;

/**
 * LoginForm với BorderLayout - ĐƠN GIẢN NHẤT!
 * Chỉ 80 dòng code!
 */
public class LoginForm extends JFrame {

  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private UsersBUS usersBUS = new UsersBUS();

  public LoginForm() {
    initUI();
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
    btnLogin.setPreferredSize(new Dimension(200, 40));
    btnLogin.setBackground(new Color(41, 128, 185));
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setFocusPainted(false);
    btnLogin.addActionListener(e -> login());

    JPanel btnPanel = new JPanel();
    btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
    btnPanel.add(btnLogin);
    add(btnPanel, BorderLayout.SOUTH); // ✅ Dễ!
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

    Session.login(user);
    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");

    new MainFrame().setVisible(true);
    dispose();
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
