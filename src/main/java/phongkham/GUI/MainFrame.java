package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

  private CardLayout cardLayout;
  private JPanel contentPanel;
  private SidebarPanel sidebarPanel;
  private HomePanel homePanel;
  private ServicePanel servicePanel;
  private ContactPanel contactPanel;
  private AboutPanel aboutPanel;
  private PhieuNhapPanel phieuNhapPanel;

  public MainFrame() {
    initComponents();
    setLocationRelativeTo(null);
  }

  private void initComponents() {
    setTitle("Phòng Khám Đa Khoa");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 700);
    setResizable(true);

    // Panel chính
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Panel tiêu đề
    JPanel headerPanel = createHeaderPanel();
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Panel thanh bên
    sidebarPanel = new SidebarPanel(this);
    mainPanel.add(sidebarPanel, BorderLayout.WEST);

    // Panel nội dung với CardLayout
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);
    contentPanel.setBackground(new Color(245, 247, 250));

    // Khởi tạo các panel
    homePanel = new HomePanel();
    servicePanel = new ServicePanel();
    contactPanel = new ContactPanel();
    aboutPanel = new AboutPanel();
    phieuNhapPanel = new PhieuNhapPanel();

    // Thêm các panel vào CardLayout
    contentPanel.add(homePanel, "HOME");
    contentPanel.add(servicePanel, "SERVICE");
    contentPanel.add(contactPanel, "CONTACT");
    contentPanel.add(aboutPanel, "ABOUT");
    contentPanel.add(phieuNhapPanel, "PHIEUNHAP");

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    add(mainPanel);

    // Hiển thị trang chủ mặc định
    showPanel("HOME");
  }

  private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(Color.WHITE);
    headerPanel.setPreferredSize(new Dimension(0, 70));
    headerPanel.setBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))
    );

    // Logo và tiêu đề
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
    leftPanel.setBackground(Color.WHITE);

    JLabel logoLabel = new JLabel("⚕");
    logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
    logoLabel.setForeground(new Color(37, 99, 235));
    leftPanel.add(logoLabel);

    JLabel titleLabel = new JLabel("Phòng Khám Bệnh");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(new Color(30, 30, 30));
    leftPanel.add(titleLabel);

    // Panel bên phải với hotline
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
    rightPanel.setBackground(Color.WHITE);

    JLabel hotlineLabel = new JLabel("HOTLINE");
    hotlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    hotlineLabel.setForeground(new Color(100, 100, 100));
    rightPanel.add(hotlineLabel);

    JLabel phoneLabel = new JLabel("1900-8888");
    phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    phoneLabel.setForeground(new Color(37, 99, 235));
    rightPanel.add(phoneLabel);

    JButton loginButton = new JButton("⚙ Đăng nhập nhân viên");
    loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    loginButton.setForeground(new Color(37, 99, 235));
    loginButton.setBackground(Color.WHITE);
    loginButton.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(37, 99, 235), 1, true),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)
      )
    );
    loginButton.setFocusPainted(false);
    loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loginButton.addActionListener(e -> {
      new LoginForm().setVisible(true);
      dispose();
    });
    rightPanel.add(loginButton);

    headerPanel.add(leftPanel, BorderLayout.WEST);
    headerPanel.add(rightPanel, BorderLayout.EAST);

    return headerPanel;
  }

  public void showPanel(String panelName) {
    cardLayout.show(contentPanel, panelName);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
    } catch (Exception ex) {
      System.err.println("Lỗi khởi tạo Flatlaf");
    }

    SwingUtilities.invokeLater(() -> {
      new MainFrame().setVisible(true);
    });
  }
}
