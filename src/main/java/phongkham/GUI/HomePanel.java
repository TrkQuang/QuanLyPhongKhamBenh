package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class HomePanel extends JPanel {

  public HomePanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 247, 250));

    // Main content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(new Color(245, 247, 250));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

    // Welcome text
    JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    welcomePanel.setBackground(new Color(245, 247, 250));
    welcomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

    JPanel welcomeTextPanel = new JPanel();
    welcomeTextPanel.setLayout(
      new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS)
    );
    welcomeTextPanel.setBackground(new Color(245, 247, 250));

    JLabel welcomeLabel = new JLabel("Xin chào,");
    welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
    welcomeLabel.setForeground(new Color(30, 30, 30));
    welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitleLabel = new JLabel("Bạn muốn thực hiện dịch vụ gì hôm nay?");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    subtitleLabel.setForeground(new Color(100, 100, 100));
    subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    welcomeTextPanel.add(welcomeLabel);
    welcomeTextPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    welcomeTextPanel.add(subtitleLabel);

    welcomePanel.add(welcomeTextPanel);
    contentPanel.add(welcomePanel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

    // Cards panel
    JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
    cardsPanel.setBackground(new Color(245, 247, 250));
    cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

    // Card 1: Đặt lịch khám
    JPanel appointmentCard = createServiceCard(
      "📅",
      "Đặt lịch khám",
      "Chọn bác sĩ chuyên khoa và thời gian phù hợp với lịch trình của bạn.",
      "Đặt ngay",
      new Color(37, 99, 235),
      new Color(29, 78, 216)
    );

    // Card 2: Mua thuốc
    JPanel medicineCard = createServiceCard(
      "💊",
      "Mua thuốc",
      "Mua thuốc theo đơn thuốc điện tử hoặc nhận tư vấn trực tiếp từ dược sĩ.",
      "Mua ngay",
      new Color(16, 185, 129),
      new Color(5, 150, 105)
    );

    cardsPanel.add(appointmentCard);
    cardsPanel.add(medicineCard);

    contentPanel.add(cardsPanel);
    contentPanel.add(Box.createVerticalGlue());

    add(contentPanel, BorderLayout.CENTER);

    // Footer info
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
    footerPanel.setBackground(Color.WHITE);
    footerPanel.setPreferredSize(new Dimension(0, 80));
    footerPanel.setBorder(
      BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230))
    );

    JLabel addressIcon = new JLabel("📍");
    addressIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));

    JLabel addressLabel = new JLabel("123 Đường Nguyễn Trãi, Quận 1");
    addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    addressLabel.setForeground(new Color(60, 60, 60));

    JLabel versionLabel = new JLabel("Hệ thống quản lý phòng khám v2.4.0");
    versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    versionLabel.setForeground(new Color(150, 150, 150));

    footerPanel.add(addressIcon);
    footerPanel.add(addressLabel);
    footerPanel.add(new JLabel(" | "));
    footerPanel.add(versionLabel);

    add(footerPanel, BorderLayout.SOUTH);
  }

  private JPanel createServiceCard(
    String icon,
    String title,
    String description,
    String buttonText,
    Color buttonColor,
    Color buttonHoverColor
  ) {
    JPanel card = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Shadow
        g2.setColor(new Color(0, 0, 0, 20));
        g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);

        // Card background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
        g2.dispose();
      }
    };
    card.setLayout(new BorderLayout(0, 20));
    card.setOpaque(false);
    card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    // Icon and content
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setOpaque(false);

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 60));
    iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titleLabel.setForeground(new Color(30, 30, 30));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JTextArea descArea = new JTextArea(description);
    descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    descArea.setForeground(new Color(100, 100, 100));
    descArea.setLineWrap(true);
    descArea.setWrapStyleWord(true);
    descArea.setEditable(false);
    descArea.setOpaque(false);
    descArea.setFocusable(false);
    descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(iconLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    contentPanel.add(descArea);

    // Button
    JButton actionButton = new JButton(buttonText + " →") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (getModel().isPressed()) {
          g2.setColor(buttonHoverColor.darker());
        } else if (getModel().isRollover()) {
          g2.setColor(buttonHoverColor);
        } else {
          g2.setColor(buttonColor);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
      }
    };
    actionButton.setText(buttonText + " →");
    actionButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
    actionButton.setForeground(Color.WHITE);
    actionButton.setPreferredSize(new Dimension(0, 50));
    actionButton.setContentAreaFilled(false);
    actionButton.setBorderPainted(false);
    actionButton.setFocusPainted(false);
    actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    actionButton.addActionListener(e -> {
      JOptionPane.showMessageDialog(
        this,
        "Chức năng " + title + " đang được phát triển!",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
    });

    card.add(contentPanel, BorderLayout.CENTER);
    card.add(actionButton, BorderLayout.SOUTH);

    return card;
  }
}
