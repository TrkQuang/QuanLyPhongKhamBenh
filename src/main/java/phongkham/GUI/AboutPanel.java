package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

/**
 * AboutPanel - ĐƠN GIẢN, CĂNG GIỮA
 * CHỈ 50 DÒNG!
 */
public class AboutPanel extends JPanel {

  public AboutPanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 247, 250));

    // ===== PANEL CHÍNH =====
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(new Color(245, 247, 250));
    content.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

    // ✅ TITLE - CĂNG GIỮA
    JLabel title = new JLabel("Về Chúng Tôi");
    title.setFont(new Font("Segoe UI", Font.BOLD, 32));
    title.setForeground(new Color(30, 30, 30));
    title.setAlignmentX(CENTER_ALIGNMENT); // ✅ Căn giữa
    content.add(title);
    content.add(Box.createRigidArea(new Dimension(0, 10)));

    // ✅ SUBTITLE - CĂNG GIỮA
    JLabel subtitle = new JLabel("Phòng Khám Đa Khoa");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    subtitle.setForeground(new Color(100, 100, 100));
    subtitle.setAlignmentX(CENTER_ALIGNMENT); // ✅ Căn giữa
    content.add(subtitle);
    content.add(Box.createRigidArea(new Dimension(0, 40)));

    // Card
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(40, 40, 40, 40)
      )
    );
    card.setMaximumSize(new Dimension(800, Integer.MAX_VALUE)); // ✅ Giới hạn width
    card.setAlignmentX(CENTER_ALIGNMENT); // ✅ Căn giữa

    // Logo
    JLabel logo = new JLabel("⚕");
    logo.setFont(new Font("Segoe UI", Font.PLAIN, 80));
    logo.setForeground(new Color(37, 99, 235));
    logo.setAlignmentX(CENTER_ALIGNMENT);
    card.add(logo);
    card.add(Box.createRigidArea(new Dimension(0, 20)));

    // Tên
    JLabel name = new JLabel("PHÒNG KHÁM ĐA KHOA");
    name.setFont(new Font("Segoe UI", Font.BOLD, 24));
    name.setAlignmentX(CENTER_ALIGNMENT);
    card.add(name);
    card.add(Box.createRigidArea(new Dimension(0, 30)));

    // Mô tả
    JTextArea desc = new JTextArea(
      "Phòng Khám Đa Khoa là đơn vị y tế uy tín, cung cấp dịch vụ khám chữa bệnh " +
        "chất lượng cao với đội ngũ bác sĩ giàu kinh nghiệm và trang thiết bị hiện đại.\n\n" +
        "Chúng tôi cam kết mang đến cho bạn:\n" +
        "• Dịch vụ y tế chuyên nghiệp và tận tâm\n" +
        "• Trang thiết bị y tế hiện đại\n" +
        "• Đội ngũ bác sĩ chuyên môn cao\n" +
        "• Môi trường khám chữa bệnh sạch sẽ, thân thiện\n" +
        "• Giá cả hợp lý, minh bạch"
    );
    desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    desc.setForeground(new Color(60, 60, 60));
    desc.setLineWrap(true);
    desc.setWrapStyleWord(true);
    desc.setEditable(false);
    desc.setOpaque(false);
    desc.setFocusable(false);
    card.add(desc);

    content.add(card);
    content.add(Box.createRigidArea(new Dimension(0, 40)));

    // Stats
    content.add(createStatsPanel());
    content.add(Box.createVerticalGlue());

    // ✅ ScrollPane
    JScrollPane scroll = new JScrollPane(content);
    scroll.setBorder(null);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    add(scroll, BorderLayout.CENTER);
  }

  // ✅ Tạo panel thống kê - CĂNG GIỮA
  private JPanel createStatsPanel() {
    JPanel stats = new JPanel(new GridLayout(1, 3, 40, 0));
    stats.setBackground(Color.WHITE);
    stats.setMaximumSize(new Dimension(800, 120)); // ✅ Giới hạn width
    stats.setAlignmentX(CENTER_ALIGNMENT); // ✅ Căn giữa
    stats.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(30, 20, 30, 20)
      )
    );

    stats.add(createStatItem("10+", "Năm kinh nghiệm"));
    stats.add(createStatItem("50+", "Bác sĩ chuyên khoa"));
    stats.add(createStatItem("100K+", "Bệnh nhân tin tưởng"));

    return stats;
  }

  // ✅ Tạo item thống kê
  private JPanel createStatItem(String number, String label) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);

    JLabel num = new JLabel(number);
    num.setFont(new Font("Segoe UI", Font.BOLD, 36));
    num.setForeground(new Color(37, 99, 235));
    num.setAlignmentX(CENTER_ALIGNMENT);

    JLabel text = new JLabel(label);
    text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    text.setForeground(new Color(100, 100, 100));
    text.setAlignmentX(CENTER_ALIGNMENT);

    panel.add(num);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(text);

    return panel;
  }
}
