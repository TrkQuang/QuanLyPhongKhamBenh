package phongkham.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import phongkham.Utils.Session;
import phongkham.db.DBConnection;

public class DashboardPanel extends JPanel {

  private static class StatItem {

    private final String title;
    private final String sql;
    private final Color color;
    private JLabel valueLabel;

    private StatItem(String title, String sql, Color color) {
      this.title = title;
      this.sql = sql;
      this.color = color;
    }
  }

  private final List<StatItem> statItems;

  public DashboardPanel() {
    statItems = new ArrayList<>();

    setLayout(new BorderLayout());
    setBackground(new Color(240, 242, 245));

    add(createHeader(), BorderLayout.NORTH);
    add(createStatsPanel(), BorderLayout.CENTER);

    loadDashboardData();
  }

  private JPanel createHeader() {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(240, 242, 245));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JLabel lblTitle = new JLabel("Dashboard - " + resolveDashboardTitle());
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitle.setForeground(new Color(30, 41, 59));
    headerPanel.add(lblTitle, BorderLayout.WEST);

    return headerPanel;
  }

  private JPanel createStatsPanel() {
    JPanel statsPanel = new JPanel();
    statsPanel.setBackground(new Color(240, 242, 245));
    statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

    buildStatsByRole();

    int size = Math.max(statItems.size(), 1);
    int rows = (int) Math.ceil(size / 3.0);
    statsPanel.setLayout(new GridLayout(rows, 3, 20, 20));

    for (StatItem item : statItems) {
      statsPanel.add(createStatCard(item));
    }

    int missingCards = rows * 3 - statItems.size();
    for (int i = 0; i < missingCards; i++) {
      JPanel empty = new JPanel();
      empty.setOpaque(false);
      statsPanel.add(empty);
    }

    return statsPanel;
  }

  private void buildStatsByRole() {
    statItems.clear();

    if (Session.hasPermission("DASHBOARD_VIEW")) {
      statItems.add(
        new StatItem(
          "Tổng người dùng",
          "SELECT COUNT(*) AS total FROM Users",
          new Color(59, 130, 246)
        )
      );
      statItems.add(
        new StatItem(
          "Tổng bác sĩ",
          "SELECT COUNT(*) AS total FROM BacSi",
          new Color(16, 185, 129)
        )
      );
      statItems.add(
        new StatItem(
          "Tổng thuốc",
          "SELECT COUNT(*) AS total FROM Thuoc",
          new Color(236, 72, 153)
        )
      );
      statItems.add(
        new StatItem(
          "Hóa đơn khám",
          "SELECT COUNT(*) AS total FROM HoaDonKham",
          new Color(245, 158, 11)
        )
      );
      statItems.add(
        new StatItem(
          "Hóa đơn thuốc",
          "SELECT COUNT(*) AS total FROM HoaDonThuoc WHERE Active = 1",
          new Color(14, 165, 233)
        )
      );
      statItems.add(
        new StatItem(
          "Thuốc tồn thấp",
          "SELECT COUNT(*) AS total FROM Thuoc WHERE SoLuongTon < 20",
          new Color(239, 68, 68)
        )
      );
      return;
    }

    if (
      Session.hasPermission("KHAMBENH_CREATE") ||
      Session.hasPermission("LICHLAMVIEC_VIEW")
    ) {
      statItems.add(
        new StatItem(
          "Lịch làm việc hôm nay",
          "SELECT COUNT(*) AS total FROM LichLamViec WHERE NgayLam = CURDATE() AND TrangThai IN ('DA_DUYET', 'ĐÃ DUYỆT')",
          new Color(16, 185, 129)
        )
      );
      statItems.add(
        new StatItem(
          "Lịch khám chờ xác nhận",
          "SELECT COUNT(*) AS total FROM LichKham WHERE TrangThai IN ('CHO_XAC_NHAN', 'ĐÃ ĐẶT')",
          new Color(245, 158, 11)
        )
      );
      statItems.add(
        new StatItem(
          "Lịch đang khám",
          "SELECT COUNT(*) AS total FROM LichKham WHERE TrangThai IN ('DANG_KHAM', 'ĐANG KHÁM')",
          new Color(59, 130, 246)
        )
      );
      return;
    }

    if (
      Session.hasPermission("HOADONTHUOC_CREATE") ||
      Session.hasPermission("HOADONTHUOC_MANAGE")
    ) {
      statItems.add(
        new StatItem(
          "Đơn chờ lấy thuốc",
          "SELECT COUNT(*) AS total FROM HoaDonThuoc WHERE Active = 1 AND TrangThaiLayThuoc IN ('CHO_LAY', 'ĐANG CHỜ LẤY')",
          new Color(59, 130, 246)
        )
      );
      statItems.add(
        new StatItem(
          "Đơn chưa thanh toán",
          "SELECT COUNT(*) AS total FROM HoaDonThuoc WHERE Active = 1 AND TrangThaiThanhToan IN ('CHUA_THANH_TOAN', 'Chưa thanh toán')",
          new Color(245, 158, 11)
        )
      );
      statItems.add(
        new StatItem(
          "Thuốc tồn thấp",
          "SELECT COUNT(*) AS total FROM Thuoc WHERE SoLuongTon < 20",
          new Color(239, 68, 68)
        )
      );
      return;
    }

    statItems.add(
      new StatItem(
        "Gói dịch vụ",
        "SELECT COUNT(*) AS total FROM GoiDichVu",
        new Color(59, 130, 246)
      )
    );
    statItems.add(
      new StatItem(
        "Bác sĩ đang hoạt động",
        "SELECT COUNT(*) AS total FROM BacSi",
        new Color(16, 185, 129)
      )
    );
    statItems.add(
      new StatItem(
        "Lịch có thể đặt",
        "SELECT COUNT(*) AS total FROM LichKham WHERE TrangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'ĐÃ ĐẶT')",
        new Color(245, 158, 11)
      )
    );
  }

  private String resolveDashboardTitle() {
    if (Session.hasPermission("DASHBOARD_VIEW")) {
      return "Tổng quan quản trị";
    }
    if (
      Session.hasPermission("KHAMBENH_CREATE") ||
      Session.hasPermission("LICHLAMVIEC_VIEW")
    ) {
      return "Bác sĩ";
    }
    if (
      Session.hasPermission("HOADONTHUOC_CREATE") ||
      Session.hasPermission("HOADONTHUOC_MANAGE")
    ) {
      return "Nhà thuốc";
    }
    return "Khách";
  }

  private JPanel createStatCard(StatItem item) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(Color.WHITE);
    card.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
      )
    );

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);

    JLabel lblTitle = new JLabel(item.title);
    lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblTitle.setForeground(new Color(100, 116, 139));
    lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    item.valueLabel = new JLabel("0");
    item.valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
    item.valueLabel.setForeground(new Color(30, 41, 59));
    item.valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(lblTitle);
    contentPanel.add(Box.createVerticalStrut(10));
    contentPanel.add(item.valueLabel);

    card.add(contentPanel, BorderLayout.CENTER);

    JPanel accentBar = new JPanel();
    accentBar.setBackground(item.color);
    accentBar.setPreferredSize(new Dimension(4, 0));
    card.add(accentBar, BorderLayout.WEST);

    return card;
  }

  private void loadDashboardData() {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      private final List<Integer> values = new ArrayList<>();

      @Override
      protected Void doInBackground() {
        for (StatItem item : statItems) {
          values.add(executeCountQuery(item.sql));
        }
        return null;
      }

      @Override
      protected void done() {
        for (int i = 0; i < statItems.size() && i < values.size(); i++) {
          statItems.get(i).valueLabel.setText(String.valueOf(values.get(i)));
        }
      }
    };

    worker.execute();
  }

  private int executeCountQuery(String sql) {
    int count = 0;
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery()
    ) {
      if (rs.next()) {
        count = rs.getInt("total");
      }
    } catch (SQLException e) {
      System.err.println("Loi dashboard query: " + sql);
      e.printStackTrace();
    }
    return count;
  }

  public void refreshData() {
    loadDashboardData();
  }
}
