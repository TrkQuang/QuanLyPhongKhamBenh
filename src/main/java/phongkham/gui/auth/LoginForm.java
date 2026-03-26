package phongkham.gui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.main.MainFrame;

public class LoginForm extends JFrame {

  private final UsersBUS usersBUS;
  private final BacSiBUS bacSiBUS;

  private CustomTextField txtUsername;
  private JPasswordField txtPassword;
  private CustomButton btnLogin;
  private javax.swing.JButton btnTogglePassword;

  public LoginForm() {
    usersBUS = new UsersBUS();
    bacSiBUS = new BacSiBUS();

    setTitle("Đăng nhập nhân viên");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(900, 620);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    setResizable(false);

    add(buildBackgroundPanel(), BorderLayout.CENTER);
  }

  private JPanel buildBackgroundPanel() {
    JPanel background = new JPanel(new GridLayout(1, 1)) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        GradientPaint gradient = new GradientPaint(
          0,
          0,
          new Color(248, 250, 252),
          0,
          getHeight(),
          new Color(236, 244, 255)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(148, 163, 184, 30));
        for (int x = 0; x < getWidth(); x += 38) {
          for (int y = 0; y < getHeight(); y += 38) {
            g2.fillOval(x, y, 2, 2);
          }
        }

        g2.dispose();
        super.paintComponent(g);
      }
    };
    background.setOpaque(false);

    JPanel centerWrap = new JPanel(new GridBagLayout());
    centerWrap.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER;

    JPanel card = buildLoginCard();
    card.setPreferredSize(new Dimension(480, 560));
    card.setMaximumSize(new Dimension(520, 600));
    centerWrap.add(card, gbc);

    background.add(centerWrap);
    return background;
  }

  private JPanel buildLoginCard() {
    final int FORM_WIDTH = 430;
    final int BUTTON_WIDTH = 322;

    RoundedPanel card = new RoundedPanel(16, true);
    card.setBackground(Color.WHITE);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(26, 26, 22, 26));

    JLabel icon = new JLabel("", SwingConstants.CENTER);
    icon.setOpaque(false);
    icon.setIcon(loadLoginIcon(42, 42));
    icon.setPreferredSize(new Dimension(42, 42));
    icon.setMaximumSize(new Dimension(42, 42));
    icon.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    icon.setAlignmentX(CENTER_ALIGNMENT);

    JLabel title = new JLabel("Đăng Nhập Nhân Viên");
    title.setAlignmentX(CENTER_ALIGNMENT);
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(new Color(30, 41, 59));

    JLabel subtitle = new JLabel("Vui lòng đăng nhập để tiếp tục");
    subtitle.setAlignmentX(CENTER_ALIGNMENT);
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    subtitle.setForeground(new Color(100, 116, 139));

    txtUsername = new CustomTextField("USR", "Nhập tên đăng nhập...");
    txtUsername.setPreferredSize(new Dimension(FORM_WIDTH, 42));
    txtUsername.setMaximumSize(new Dimension(FORM_WIDTH, 42));

    JPanel passwordWrap = buildPasswordField();
    passwordWrap.setPreferredSize(new Dimension(FORM_WIDTH, 42));
    passwordWrap.setMaximumSize(new Dimension(FORM_WIDTH, 42));

    JLabel forgot = new JLabel("Quên mật khẩu?");
    forgot.setCursor(
      java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)
    );
    forgot.setForeground(new Color(59, 130, 246));
    forgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    forgot.setHorizontalAlignment(SwingConstants.CENTER);
    forgot.setAlignmentX(CENTER_ALIGNMENT);
    forgot.addMouseListener(
      new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
          forgot.setText("<html><u>Quên mật khẩu?</u></html>");
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
          forgot.setText("Quên mật khẩu?");
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
          DialogHelper.info(
            LoginForm.this,
            "Liên hệ quản trị viên để cấp lại mật khẩu."
          );
        }
      }
    );

    btnLogin = new CustomButton(
      "Đăng nhập",
      new Color(59, 130, 246),
      new Color(37, 99, 235),
      new Color(30, 64, 175),
      12
    );
    btnLogin.setPreferredSize(new Dimension(BUTTON_WIDTH, 42));
    btnLogin.setMaximumSize(new Dimension(BUTTON_WIDTH, 42));
    btnLogin.addActionListener(e -> login());

    JPanel divider = buildDivider();
    divider.setAlignmentX(CENTER_ALIGNMENT);
    divider.setPreferredSize(new Dimension(FORM_WIDTH, 24));
    divider.setMaximumSize(new Dimension(FORM_WIDTH, 24));

    CustomButton btnFaceId = new CustomButton(
      "Login as GUEST",
      new Color(71, 85, 105),
      new Color(51, 65, 85),
      new Color(30, 41, 59),
      12
    );
    btnFaceId.setPreferredSize(new Dimension(BUTTON_WIDTH, 42));
    btnFaceId.setMaximumSize(new Dimension(BUTTON_WIDTH, 42));
    btnFaceId.addActionListener(e -> openGuestMode());

    JLabel footer = new JLabel("Team 5 - Nocopyright", SwingConstants.CENTER);
    footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    footer.setForeground(new Color(100, 116, 139));
    footer.setAlignmentX(CENTER_ALIGNMENT);

    card.add(icon);
    card.add(Box.createVerticalStrut(14));
    card.add(title);
    card.add(Box.createVerticalStrut(4));
    card.add(subtitle);
    card.add(Box.createVerticalStrut(20));
    card.add(centeredRow(txtUsername, FORM_WIDTH, 42));
    card.add(Box.createVerticalStrut(10));
    card.add(centeredRow(passwordWrap, FORM_WIDTH, 42));
    card.add(Box.createVerticalStrut(8));
    card.add(centeredRow(forgot, FORM_WIDTH, 20));
    card.add(Box.createVerticalStrut(14));
    card.add(centeredRow(btnLogin, BUTTON_WIDTH, 42));
    card.add(Box.createVerticalStrut(14));
    card.add(centeredRow(divider, FORM_WIDTH, 24));
    card.add(Box.createVerticalStrut(14));
    card.add(centeredRow(btnFaceId, BUTTON_WIDTH, 42));
    card.add(Box.createVerticalStrut(16));
    card.add(centeredRow(footer, FORM_WIDTH, 18));

    return card;
  }

  private JPanel centeredRow(JComponent component, int width, int height) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    row.setOpaque(false);
    row.setAlignmentX(CENTER_ALIGNMENT);
    row.setPreferredSize(new Dimension(width, height));
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    component.setPreferredSize(new Dimension(width, height));
    row.add(component);
    return row;
  }

  private ImageIcon loadLoginIcon(int width, int height) {
    URL iconUrl = getClass().getResource(
      "/phongkham/gui/img/iconLoginForm.png"
    );
    try {
      if (iconUrl != null) {
        ImageIcon original = new ImageIcon(iconUrl);
        Image scaled = original
          .getImage()
          .getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
      }

      File iconFile = new File(
        "src/main/java/phongkham/gui/img/iconLoginForm.png"
      );
      if (iconFile.exists()) {
        BufferedImage image = ImageIO.read(iconFile);
        Image scaled = image.getScaledInstance(
          width,
          height,
          Image.SCALE_SMOOTH
        );
        return new ImageIcon(scaled);
      }
    } catch (IOException ignored) {
      return null;
    }
    return null;
  }

  private JPanel buildDivider() {
    JPanel divider = new JPanel(new BorderLayout(10, 0));
    divider.setOpaque(false);

    JSeparator left = new JSeparator(SwingConstants.HORIZONTAL);
    left.setForeground(new Color(203, 213, 225));
    left.setBackground(new Color(203, 213, 225));

    JSeparator right = new JSeparator(SwingConstants.HORIZONTAL);
    right.setForeground(new Color(203, 213, 225));
    right.setBackground(new Color(203, 213, 225));

    JLabel text = new JLabel("HOAC", SwingConstants.CENTER);
    text.setFont(new Font("Segoe UI", Font.BOLD, 11));
    text.setForeground(new Color(148, 163, 184));

    divider.add(left, BorderLayout.WEST);
    divider.add(text, BorderLayout.CENTER);
    divider.add(right, BorderLayout.EAST);

    left.setPreferredSize(new Dimension(160, 1));
    right.setPreferredSize(new Dimension(160, 1));

    return divider;
  }

  private JPanel buildPasswordField() {
    RoundedPanel panel = new RoundedPanel(12, false);
    panel.setBackground(Color.WHITE);
    panel.setLayout(new BorderLayout(10, 0));
    panel.setBorder(new EmptyBorder(8, 12, 8, 8));
    panel.setPreferredSize(new Dimension(420, 42));
    panel.setMaximumSize(new Dimension(420, 42));

    JLabel icon = new JLabel("PWD");
    icon.setForeground(new Color(100, 116, 139));
    icon.setFont(new Font("Segoe UI", Font.BOLD, 11));

    txtPassword = new JPasswordField();
    txtPassword.setBorder(null);
    txtPassword.setOpaque(false);
    txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtPassword.setEchoChar('*');
    txtPassword.addActionListener(e -> login());
    txtPassword.addFocusListener(
      new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
          panel.setBackground(new Color(248, 250, 252));
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
          panel.setBackground(Color.WHITE);
        }
      }
    );

    btnTogglePassword = new javax.swing.JButton("Hiện");
    btnTogglePassword.setFocusPainted(false);
    btnTogglePassword.setBorderPainted(false);
    btnTogglePassword.setContentAreaFilled(false);
    btnTogglePassword.setForeground(new Color(59, 130, 246));
    btnTogglePassword.setCursor(
      java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)
    );
    btnTogglePassword.addActionListener(e -> togglePassword());

    panel.add(icon, BorderLayout.WEST);
    panel.add(txtPassword, BorderLayout.CENTER);
    panel.add(btnTogglePassword, BorderLayout.EAST);

    return panel;
  }

  private void togglePassword() {
    boolean hidden = txtPassword.getEchoChar() != (char) 0;
    if (hidden) {
      txtPassword.setEchoChar((char) 0);
      btnTogglePassword.setText("Ẩn");
    } else {
      txtPassword.setEchoChar('*');
      btnTogglePassword.setText("Hiện");
    }
  }

  private void setLoading(boolean loading) {
    btnLogin.setEnabled(!loading);
    btnLogin.setText(loading ? "Đang đăng nhập..." : "Đăng nhập");
    txtUsername.getInnerField().setEnabled(!loading);
    txtPassword.setEnabled(!loading);
  }

  private void login() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
      DialogHelper.warn(this, "Vui lòng nhập tên đăng nhập và mật khẩu.");
      return;
    }

    setLoading(true);

    SwingWorker<UsersDTO, Void> worker = new SwingWorker<UsersDTO, Void>() {
      @Override
      protected UsersDTO doInBackground() {
        return usersBUS.login(username, password);
      }

      @Override
      protected void done() {
        setLoading(false);
        try {
          UsersDTO user = get();
          if (user == null) {
            DialogHelper.error(LoginForm.this, "Sai tài khoản hoặc mật khẩu.");
            return;
          }

          Session.login(user);

          // Reset trước để tránh giữ ID bác sĩ từ session cũ.
          Session.setCurrentBacSiID(null);

          // Chỉ nạp hồ sơ bác sĩ khi user thuộc role bác sĩ.
          Integer roleId = user.getRoleID();
          if (roleId != null && roleId == 2) {
            BacSiBUS safeBacSiBUS = (bacSiBUS != null)
              ? bacSiBUS
              : new BacSiBUS();
            BacSiDTO bacSi = safeBacSiBUS.getByEmail(user.getEmail());
            if (bacSi == null) {
              String maBacSiTuUsername = deriveDoctorIdFromUsername(
                user.getUsername()
              );
              if (maBacSiTuUsername != null) {
                bacSi = safeBacSiBUS.getById(maBacSiTuUsername);
              }
            }
            if (bacSi != null) {
              Session.setCurrentBacSiID(bacSi.getMaBacSi());
            }
          }

          openMainFrame();
        } catch (Exception ex) {
          DialogHelper.error(
            LoginForm.this,
            "Lỗi đăng nhập: " + ex.getMessage()
          );
        }
      }
    };

    worker.execute();
  }

  private void openMainFrame() {
    dispose();
    MainFrame mainFrame = new MainFrame();
    mainFrame.reloadLayoutAfterLogin();
    mainFrame.setVisible(true);
  }

  private void openGuestMode() {
    // Guest không cần đăng nhập: xóa session rồi vào thẳng 2 màn Guest.
    Session.logout();
    dispose();
    MainFrame mainFrame = new MainFrame();
    mainFrame.reloadLayoutAfterLogin();
    mainFrame.setVisible(true);
  }

  private String deriveDoctorIdFromUsername(String username) {
    if (username == null) {
      return null;
    }
    String digits = username.replaceAll("\\D", "");
    if (digits.isEmpty()) {
      return null;
    }
    try {
      int number = Integer.parseInt(digits);
      return String.format("BS%03d", number);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static void showAsDialog(java.awt.Window owner) {
    LoginForm loginForm = new LoginForm();
    JDialog dialog = new JDialog(
      owner,
      "Đăng nhập",
      JDialog.ModalityType.APPLICATION_MODAL
    );
    dialog.setContentPane(loginForm.getContentPane());
    dialog.setSize(new Dimension(900, 620));
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
  }
}
