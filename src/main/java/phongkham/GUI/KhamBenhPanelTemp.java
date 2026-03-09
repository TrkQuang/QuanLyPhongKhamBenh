package phongkham.gui;

import java.awt.*;
import javax.swing.*;

public class KhamBenhPanelTemp extends JPanel {

  public KhamBenhPanelTemp() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 247, 250));

    // Title
    JLabel lblTitle = new JLabel("KHÁM BỆNH", JLabel.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
    lblTitle.setForeground(new Color(30, 30, 30));
    lblTitle.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
    add(lblTitle, BorderLayout.NORTH);

    // Content
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(new Color(245, 247, 250));
    content.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

    JLabel lblInfo = new JLabel(
      "<html><div style='text-align: center;'>" +
        "<h2>🩺 Chức năng Khám Bệnh</h2>" +
        "<p style='margin-top: 20px; font-size: 14px; color: #666;'>" +
        "Tính năng này đang được phát triển...<br><br>" +
        "Sẽ bao gồm:<br>" +
        "• Xem danh sách bệnh nhân chờ khám<br>" +
        "• Nhập thông tin khám bệnh<br>" +
        "• Kê đơn thuốc<br>" +
        "• Ghi chú lời dặn<br>" +
        "• In hóa đơn khám" +
        "</p>" +
        "</div></html>"
    );
    lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

    content.add(lblInfo);

    add(content, BorderLayout.CENTER);
  }
}
