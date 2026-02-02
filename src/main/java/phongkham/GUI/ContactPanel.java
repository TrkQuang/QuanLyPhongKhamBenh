package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class ContactPanel extends JPanel {

  public ContactPanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 247, 250));

    // Main content
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(new Color(245, 247, 250));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

    // Title
    JLabel titleLabel = new JLabel("Liên Hệ");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
    titleLabel.setForeground(new Color(30, 30, 30));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitleLabel = new JLabel("Chúng tôi luôn sẵn sàng hỗ trợ bạn");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    subtitleLabel.setForeground(new Color(100, 100, 100));
    subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    contentPanel.add(subtitleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

    // Contact info panel
    JPanel infoPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(new Color(0, 0, 0, 15));
        g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
        g2.dispose();
      }
    };
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setOpaque(false);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
    infoPanel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));

    // Phone
    JPanel phonePanel = createInfoRow("📞", "Điện thoại:", "1900-8888");

    // Email
    JPanel emailPanel = createInfoRow("✉", "Email:", "contact@phongkham.vn");

    // Address
    JPanel addressPanel = createInfoRow(
      "📍",
      "Địa chỉ:",
      "123 Đường Nguyễn Trãi, Quận 1, TP.HCM"
    );

    // Working hours
    JPanel hoursPanel = createInfoRow(
      "🕐",
      "Giờ làm việc:",
      "08:00 - 20:00 (Hàng ngày, kể cả Chủ Nhật)"
    );

    infoPanel.add(phonePanel);
    infoPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    infoPanel.add(emailPanel);
    infoPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    infoPanel.add(addressPanel);
    infoPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    infoPanel.add(hoursPanel);

    contentPanel.add(infoPanel);
    contentPanel.add(Box.createVerticalGlue());

    add(contentPanel, BorderLayout.CENTER);
  }

  private JPanel createInfoRow(String icon, String label, String value) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
    iconLabel.setPreferredSize(new Dimension(40, 40));

    JPanel textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textPanel.setOpaque(false);

    JLabel titleLabel = new JLabel(label);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titleLabel.setForeground(new Color(60, 60, 60));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    valueLabel.setForeground(new Color(37, 99, 235));
    valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    textPanel.add(titleLabel);
    textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    textPanel.add(valueLabel);

    panel.add(iconLabel);
    panel.add(Box.createRigidArea(new Dimension(15, 0)));
    panel.add(textPanel);

    return panel;
  }
}
