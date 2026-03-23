package phongkham.gui.admin.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.components.ShadowPanel;

public class Sidebar extends ShadowPanel {

  private final JProgressBar confirmedRateBar = createBar();
  private final JProgressBar stockSafetyBar = createBar();
  private final JLabel badgeExpired = createBadge(new Color(239, 68, 68));
  private final JLabel badgeNearExpiry = createBadge(new Color(245, 158, 11));
  private final JLabel badgeLowStock = createBadge(new Color(99, 102, 241));

  public Sidebar(
    int confirmedRate,
    int stockSafetyRate,
    int expiredQty,
    int nearExpiryQty,
    int lowStockCount
  ) {
    super(20);
    setLayout(new BorderLayout(0, 10));
    setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));

    JLabel title = new JLabel("Trạng thái hệ thống");
    title.setFont(new Font("Segoe UI", Font.BOLD, 16));
    title.setForeground(UIConstants.TEXT_MAIN);

    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(
      new javax.swing.BoxLayout(body, javax.swing.BoxLayout.Y_AXIS)
    );

    body.add(sectionLabel("Lịch đã xác nhận"));
    body.add(confirmedRateBar);
    body.add(gap(6));

    body.add(sectionLabel("Tồn kho an toàn"));
    body.add(stockSafetyBar);
    body.add(gap(6));

    body.add(sectionLabel("Cảnh báo nhanh"));
    body.add(badgeRow("Hết hạn", badgeExpired));
    body.add(badgeRow("Sắp hết hạn <=30 ngày", badgeNearExpiry));
    body.add(badgeRow("Thuốc tồn thấp", badgeLowStock));

    add(title, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);

    animateBar(confirmedRateBar, clampPercent(confirmedRate));
    animateBar(stockSafetyBar, clampPercent(stockSafetyRate));
    badgeExpired.setText(String.valueOf(Math.max(0, expiredQty)));
    badgeNearExpiry.setText(String.valueOf(Math.max(0, nearExpiryQty)));
    badgeLowStock.setText(String.valueOf(Math.max(0, lowStockCount)));
  }

  private JLabel sectionLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    label.setForeground(UIConstants.TEXT_MUTED);
    return label;
  }

  private JPanel gap(int h) {
    JPanel panel = new JPanel();
    panel.setOpaque(false);
    panel.setPreferredSize(new java.awt.Dimension(1, h));
    panel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, h));
    return panel;
  }

  private JProgressBar createBar() {
    JProgressBar bar = new JProgressBar(0, 100);
    bar.setForeground(UIConstants.PRIMARY);
    bar.setBackground(new Color(226, 232, 240));
    bar.setStringPainted(true);
    bar.setValue(0);
    return bar;
  }

  private int clampPercent(int value) {
    return Math.max(0, Math.min(100, value));
  }

  private void animateBar(JProgressBar bar, int target) {
    Timer timer = new Timer(14, null);
    timer.addActionListener(e -> {
      int current = bar.getValue();
      if (current >= target) {
        bar.setValue(target);
        bar.setString(target + "%");
        timer.stop();
        return;
      }
      int next = Math.min(target, current + 2);
      bar.setValue(next);
      bar.setString(next + "%");
    });
    timer.start();
  }

  private JLabel createBadge(Color color) {
    JLabel badge = new JLabel("0", javax.swing.SwingConstants.CENTER);
    badge.setOpaque(true);
    badge.setForeground(Color.WHITE);
    badge.setBackground(color);
    badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
    badge.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 10, 4, 10));
    return badge;
  }

  private JPanel badgeRow(String label, JLabel badge) {
    JPanel row = new JPanel(new BorderLayout(8, 0));
    row.setOpaque(false);

    JLabel text = new JLabel(label);
    text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    text.setForeground(UIConstants.TEXT_MAIN);

    JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    badgeWrap.setOpaque(false);
    badgeWrap.add(badge);

    row.add(text, BorderLayout.WEST);
    row.add(badgeWrap, BorderLayout.EAST);
    row.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 26));
    return row;
  }
}
