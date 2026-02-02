package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class AboutPanel extends JPanel {

  public AboutPanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 247, 250));

    // Nội dung chính
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(new Color(245, 247, 250));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

    // Tiêu đề
    JLabel titleLabel = new JLabel("About US");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
    titleLabel.setForeground(new Color(30, 30, 30));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitleLabel = new JLabel("Phòng Khám Bệnh");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    subtitleLabel.setForeground(new Color(100, 100, 100));
    subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    contentPanel.add(subtitleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

    // Panel nội dung giới thiệu
    JPanel aboutPanel = new JPanel() {
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
    aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
    aboutPanel.setOpaque(false);
    aboutPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
    aboutPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

    // Biểu tượng
    JLabel logoLabel = new JLabel("⚕", SwingConstants.CENTER);
    logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
    logoLabel.setForeground(new Color(37, 99, 235));
    logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel clinicName = new JLabel("PHÒNG KHÁM ĐA KHOA", SwingConstants.CENTER);
    clinicName.setFont(new Font("Segoe UI", Font.BOLD, 24));
    clinicName.setForeground(new Color(30, 30, 30));
    clinicName.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Mô tả
    JTextArea descArea = new JTextArea();
    descArea.setText(
      "Phòng Khám Đa Khoa là đơn vị y tế uy tín, cung cấp dịch vụ khám chữa bệnh " +
        "chất lượng cao với đội ngũ bác sĩ giàu kinh nghiệm và trang thiết bị hiện đại.\n\n" +
        "Chúng tôi cam kết mang đến cho bạn:\n" +
        "• Dịch vụ y tế chuyên nghiệp và tận tâm\n" +
        "• Trang thiết bị y tế hiện đại\n" +
        "• Đội ngũ bác sĩ chuyên môn cao\n" +
        "• Môi trường khám chữa bệnh sạch sẽ, thân thiện\n" +
        "• Giá cả hợp lý, minh bạch"
    );
    descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    descArea.setForeground(new Color(60, 60, 60));
    descArea.setLineWrap(true);
    descArea.setWrapStyleWord(true);
    descArea.setEditable(false);
    descArea.setOpaque(false);
    descArea.setFocusable(false);
    descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

    aboutPanel.add(logoLabel);
    aboutPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    aboutPanel.add(clinicName);
    aboutPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    aboutPanel.add(descArea);

    contentPanel.add(aboutPanel);
    contentPanel.add(Box.createVerticalGlue());

    add(contentPanel, BorderLayout.CENTER);

    // Panel thống kê
    JPanel statsPanel = new JPanel(new GridLayout(1, 3, 40, 0));
    statsPanel.setBackground(Color.WHITE);
    statsPanel.setPreferredSize(new Dimension(0, 120));
    statsPanel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(30, 60, 30, 60)
      )
    );

    statsPanel.add(createStatItem("10+", "Năm kinh nghiệm"));
    statsPanel.add(createStatItem("50+", "Bác sĩ chuyên khoa"));
    statsPanel.add(createStatItem("100K+", "Bệnh nhân tin tưởng"));

    add(statsPanel, BorderLayout.SOUTH);
  }

  private JPanel createStatItem(String number, String label) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);

    JLabel numLabel = new JLabel(number, SwingConstants.CENTER);
    numLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
    numLabel.setForeground(new Color(37, 99, 235));
    numLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel textLabel = new JLabel(label, SwingConstants.CENTER);
    textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    textLabel.setForeground(new Color(100, 100, 100));
    textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    panel.add(numLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(textLabel);

    return panel;
  }
}
