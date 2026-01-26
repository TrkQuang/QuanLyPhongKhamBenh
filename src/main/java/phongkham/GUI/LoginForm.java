package phongkham.GUI;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;

public class LoginForm extends JFrame {

  private JTextField txtEmail;
  private JPasswordField txtPassword;
  private JButton btnLogin;
  private JButton btnTogglePassword;
  private JLabel lblForgotPassword;

  public LoginForm() {
    initComponents();
    setLocationRelativeTo(null);
  }

  private void initComponents() {
    setTitle("Đăng Nhập");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 550);
    setResizable(false);
    setUndecorated(true);

    // Panel chính với gradient background tím đẹp
    JPanel backgroundPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        GradientPaint gradient = new GradientPaint(
          0,
          0,
          new Color(99, 102, 241),
          0,
          getHeight(),
          new Color(139, 92, 246)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    backgroundPanel.setLayout(null);

    // Panel trắng bo tròn với shadow
    JPanel cardPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

        // Card trắng
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);
        g2.dispose();
      }
    };
    cardPanel.setLayout(null);
    cardPanel.setOpaque(false);
    cardPanel.setBounds(40, 80, 320, 400);

    // Icon y tế
    JLabel iconLabel = new JLabel("⚕", SwingConstants.CENTER);
    iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 50));
    iconLabel.setForeground(new Color(99, 102, 241));
    iconLabel.setBounds(0, 30, 320, 50);
    cardPanel.add(iconLabel);

    // Title
    JLabel lblTitle = new JLabel("Đăng Nhập", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitle.setForeground(new Color(30, 30, 30));
    lblTitle.setBounds(0, 90, 320, 35);
    cardPanel.add(lblTitle);

    // Subtitle
    JLabel lblSubtitle = new JLabel(
      "Hệ thống quản lý phòng khám",
      SwingConstants.CENTER
    );
    lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblSubtitle.setForeground(new Color(120, 120, 120));
    lblSubtitle.setBounds(0, 125, 320, 20);
    cardPanel.add(lblSubtitle);

    // Email field
    txtEmail = new JTextField("admin@gmail.com");
    txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtEmail.setBounds(35, 170, 250, 45);
    txtEmail.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(5, 15, 5, 45)
      )
    );
    cardPanel.add(txtEmail);

    // Email icon
    JLabel emailIcon = new JLabel("✉");
    emailIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    emailIcon.setForeground(new Color(150, 150, 150));
    emailIcon.setBounds(255, 180, 25, 25);
    cardPanel.add(emailIcon);

    // Password panel
    JPanel passwordPanel = new JPanel();
    passwordPanel.setLayout(null);
    passwordPanel.setBounds(35, 230, 250, 45);
    passwordPanel.setBackground(Color.WHITE);
    passwordPanel.setBorder(
      BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
    );

    txtPassword = new JPasswordField();
    txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtPassword.setBounds(15, 0, 190, 45);
    txtPassword.setBorder(BorderFactory.createEmptyBorder());
    txtPassword.setEchoChar('•');
    passwordPanel.add(txtPassword);

    // Toggle password button
    btnTogglePassword = new JButton("👁");
    btnTogglePassword.setBounds(210, 7, 30, 30);
    btnTogglePassword.setBorderPainted(false);
    btnTogglePassword.setContentAreaFilled(false);
    btnTogglePassword.setFocusPainted(false);
    btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnTogglePassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    btnTogglePassword.addActionListener(e -> {
      if (txtPassword.getEchoChar() == '•') {
        txtPassword.setEchoChar((char) 0);
        btnTogglePassword.setText("🙈");
      } else {
        txtPassword.setEchoChar('•');
        btnTogglePassword.setText("👁");
      }
    });
    passwordPanel.add(btnTogglePassword);
    cardPanel.add(passwordPanel);

    // Forgot password link
    lblForgotPassword = new JLabel("Quên mật khẩu?", SwingConstants.RIGHT);
    lblForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblForgotPassword.setForeground(new Color(99, 102, 241));
    lblForgotPassword.setBounds(35, 285, 250, 20);
    lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
    lblForgotPassword.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          lblForgotPassword.setForeground(new Color(79, 70, 229));
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
          lblForgotPassword.setForeground(new Color(99, 102, 241));
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
          JOptionPane.showMessageDialog(
            LoginForm.this,
            "Vui lòng liên hệ quản trị viên để đặt lại mật khẩu",
            "Quên mật khẩu",
            JOptionPane.INFORMATION_MESSAGE
          );
        }
      }
    );
    cardPanel.add(lblForgotPassword);

    // Login button với gradient tím
    btnLogin = new JButton("ĐĂNG NHẬP") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (getModel().isPressed()) {
          g2.setColor(new Color(79, 70, 229));
        } else if (getModel().isRollover()) {
          g2.setColor(new Color(79, 70, 229));
        } else {
          g2.setColor(new Color(99, 102, 241));
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
        g2.dispose();
        super.paintComponent(g);
      }
    };
    btnLogin.setBounds(35, 320, 250, 48);
    btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setContentAreaFilled(false);
    btnLogin.setBorderPainted(false);
    btnLogin.setFocusPainted(false);
    btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnLogin.addActionListener(e -> handleLogin());
    cardPanel.add(btnLogin);

    backgroundPanel.add(cardPanel);
    add(backgroundPanel);

    // Enter key listeners
    txtEmail.addKeyListener(
      new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
          if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            handleLogin();
          }
        }
      }
    );

    txtPassword.addKeyListener(
      new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
          if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            handleLogin();
          }
        }
      }
    );
  }

  private void handleLogin() {
    String email = txtEmail.getText().trim();
    String password = new String(txtPassword.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập đầy đủ email và mật khẩu!",
        "Thông báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    // Demo đăng nhập
    if (email.equals("admin@gmail.com") && password.equals("admin123")) {
      JOptionPane.showMessageDialog(
        this,
        "Đăng nhập thành công!\nChào mừng bạn đến với hệ thống",
        "Thành công",
        JOptionPane.INFORMATION_MESSAGE
      );
      // TODO: Mở form chính của ứng dụng
      // this.dispose();
      // new MainForm().setVisible(true);
    } else {
      JOptionPane.showMessageDialog(
        this,
        "Email hoặc mật khẩu không đúng!",
        "Lỗi đăng nhập",
        JOptionPane.ERROR_MESSAGE
      );
      txtPassword.setText("");
      txtPassword.requestFocus();
    }
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {
      LoginForm loginForm = new LoginForm();
      loginForm.setVisible(true);
    });
  }
}
