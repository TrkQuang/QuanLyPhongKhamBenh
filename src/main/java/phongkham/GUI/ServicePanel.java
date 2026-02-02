package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class ServicePanel extends JPanel {

  public ServicePanel() {
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
    JLabel titleLabel = new JLabel("Dịch Vụ Y Tế");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
    titleLabel.setForeground(new Color(30, 30, 30));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitleLabel = new JLabel(
      "Các dịch vụ chăm sóc sức khỏe toàn diện"
    );
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    subtitleLabel.setForeground(new Color(100, 100, 100));
    subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    contentPanel.add(subtitleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

    // Services grid
    JPanel servicesGrid = new JPanel(new GridLayout(2, 3, 30, 30));
    servicesGrid.setBackground(new Color(245, 247, 250));
    servicesGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));

    String[] services = {
      "🩺 Khám Tổng Quát",
      "💉 Tiêm Chủng",
      "🔬 Xét Nghiệm",
      "🦷 Nha Khoa",
      "👁 Khám Mắt",
      "🏥 Chăm Sóc Đặc Biệt",
    };

    for (String service : services) {
      servicesGrid.add(createServiceItem(service));
    }

    contentPanel.add(servicesGrid);
    contentPanel.add(Box.createVerticalGlue());

    add(contentPanel, BorderLayout.CENTER);
  }

  private JPanel createServiceItem(String serviceName) {
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(new Color(0, 0, 0, 15));
        g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 15, 15);
        g2.dispose();
      }
    };
    panel.setLayout(new BorderLayout());
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

    JLabel label = new JLabel(serviceName, SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.BOLD, 16));
    label.setForeground(new Color(30, 30, 30));

    panel.add(label, BorderLayout.CENTER);

    panel.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          panel.setBackground(new Color(240, 240, 240));
          panel.repaint();
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
          panel.setBackground(Color.WHITE);
          panel.repaint();
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
          JOptionPane.showMessageDialog(
            panel,
            "Dịch vụ: " + serviceName,
            "Thông tin dịch vụ",
            JOptionPane.INFORMATION_MESSAGE
          );
        }
      }
    );

    return panel;
  }
}
