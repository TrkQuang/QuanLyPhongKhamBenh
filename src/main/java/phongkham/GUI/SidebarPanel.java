package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class SidebarPanel extends JPanel {

  private MainFrame mainFrame;
  private JButton selectedButton;
  private Color selectedColor = new Color(37, 99, 235);
  private Color hoverColor = new Color(243, 244, 246);
  private Color defaultColor = Color.WHITE;

  public SidebarPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(200, 0));
    setBackground(Color.WHITE);
    setBorder(
      BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230))
    );

    // Panel menu
    JPanel menuPanel = new JPanel();
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
    menuPanel.setBackground(Color.WHITE);
    menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    // Tạo các nút menu
    JButton btnHome = createMenuButton("🏠", "Trang chủ", "HOME");
    JButton btnService = createMenuButton("🏥", "Dịch vụ", "SERVICE");
    JButton btnContact = createMenuButton("📞", "Liên hệ", "CONTACT");
    JButton btnAbout = createMenuButton("ℹ", "Về chúng tôi", "ABOUT");
    JButton btnPhieuNhap = createMenuButton("📦", "Phiếu nhập thuốc", "PHIEUNHAP");


    menuPanel.add(btnHome);
    menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    menuPanel.add(btnService);
    menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    menuPanel.add(btnPhieuNhap);   
    menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    menuPanel.add(btnContact);
    menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    menuPanel.add(btnAbout);
    menuPanel.add(Box.createVerticalGlue());

    add(menuPanel, BorderLayout.CENTER);

    // Panel chân trang
    JPanel footerPanel = new JPanel(new BorderLayout());
    footerPanel.setBackground(new Color(249, 250, 251));
    footerPanel.setPreferredSize(new Dimension(0, 100));
    footerPanel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
      )
    );

    JPanel footerContent = new JPanel();
    footerContent.setLayout(new BoxLayout(footerContent, BoxLayout.Y_AXIS));
    footerContent.setBackground(new Color(249, 250, 251));

    JLabel workHoursTitle = new JLabel("GIỜ LÀM VIỆC");
    workHoursTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
    workHoursTitle.setForeground(new Color(100, 100, 100));
    workHoursTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel workHours = new JLabel("08:00 - 20:00");
    workHours.setFont(new Font("Segoe UI", Font.BOLD, 13));
    workHours.setForeground(new Color(30, 30, 30));
    workHours.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel workDays = new JLabel("Hàng ngày, cả Chủ Nhật");
    workDays.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    workDays.setForeground(new Color(100, 100, 100));
    workDays.setAlignmentX(Component.LEFT_ALIGNMENT);

    footerContent.add(workHoursTitle);
    footerContent.add(Box.createRigidArea(new Dimension(0, 5)));
    footerContent.add(workHours);
    footerContent.add(Box.createRigidArea(new Dimension(0, 3)));
    footerContent.add(workDays);

    footerPanel.add(footerContent, BorderLayout.CENTER);
    add(footerPanel, BorderLayout.SOUTH);

    // Chọn nút trang chủ mặc định
    setSelectedButton(btnHome);
  }

  private JButton createMenuButton(String icon, String text, String panelName) {
    JButton button = new JButton() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (this == selectedButton) {
          g2.setColor(selectedColor);
          g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        } else if (getModel().isRollover()) {
          g2.setColor(hoverColor);
          g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
      }
    };

    button.setText(icon + "  " + text);
    button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    button.setForeground(new Color(30, 30, 30));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    button.setPreferredSize(new Dimension(180, 45));
    button.setMaximumSize(new Dimension(180, 45));
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    button.addActionListener(e -> {
      setSelectedButton(button);
      mainFrame.showPanel(panelName);
    });

    return button;
  }

  private void setSelectedButton(JButton button) {
    if (selectedButton != null) {
      selectedButton.setForeground(new Color(30, 30, 30));
    }
    selectedButton = button;
    selectedButton.setForeground(Color.WHITE);
    repaint();
  }
}
