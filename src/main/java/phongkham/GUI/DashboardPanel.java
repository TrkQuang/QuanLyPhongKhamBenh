package phongkham.gui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import phongkham.db.DBConnection;

public class DashboardPanel extends JPanel {

  private JLabel lblTongThuoc, lblTongBacSi, lblTongHDKham, lblTongHDThuoc, lblTongUsers;

  public DashboardPanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(240, 242, 245));

    // Header
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(240, 242, 245));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    JLabel lblTitle = new JLabel("📊 Dashboard - Tổng Quan Hệ Thống");
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitle.setForeground(new Color(30, 41, 59));
    headerPanel.add(lblTitle, BorderLayout.WEST);

    add(headerPanel, BorderLayout.NORTH);

    // Stats Panel
    JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
    statsPanel.setBackground(new Color(240, 242, 245));
    statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

    // Tạo các card thống kê
    JPanel cardThuoc = createStatCard(
      "Tổng Số Thuốc",
      "0",
      new Color(59, 130, 246)
    );
    JPanel cardBacSi = createStatCard(
      "Tổng Số Bác Sĩ",
      "0",
      new Color(16, 185, 129)
    );
    JPanel cardHDKham = createStatCard(
      "Hóa Đơn Khám",
      "0",
      new Color(139, 92, 246)
    );
    JPanel cardHDThuoc = createStatCard(
      "Hóa Đơn Thuốc",
      "0",
      new Color(236, 72, 153)
    );
    JPanel cardUsers = createStatCard(
      "Tổng Số Users",
      "0",
      new Color(245, 158, 11)
    );
    JPanel cardEmpty = createEmptyCard(); // Card trống để layout đẹp

    // Lưu reference tới label số liệu (dùng putClientProperty)
    lblTongThuoc = (JLabel) cardThuoc.getClientProperty("valueLabel");
    lblTongBacSi = (JLabel) cardBacSi.getClientProperty("valueLabel");
    lblTongHDKham = (JLabel) cardHDKham.getClientProperty("valueLabel");
    lblTongHDThuoc = (JLabel) cardHDThuoc.getClientProperty("valueLabel");
    lblTongUsers = (JLabel) cardUsers.getClientProperty("valueLabel");

    statsPanel.add(cardThuoc);
    statsPanel.add(cardBacSi);
    statsPanel.add(cardHDKham);
    statsPanel.add(cardHDThuoc);
    statsPanel.add(cardUsers);
    statsPanel.add(cardEmpty);

    add(statsPanel, BorderLayout.CENTER);

    // Load dữ liệu từ database
    loadDashboardData();
  }

  /**
   * Tạo một card thống kê
   */
  private JPanel createStatCard(String title, String value, Color accentColor) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout());
    card.setBackground(Color.WHITE);
    card.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
      )
    );

    // Content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);

    // Title
    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblTitle.setForeground(new Color(100, 116, 139));
    lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Value
    JLabel lblValue = new JLabel(value);
    lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
    lblValue.setForeground(new Color(30, 41, 59));
    lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(lblTitle);
    contentPanel.add(Box.createVerticalStrut(10));
    contentPanel.add(lblValue);

    card.add(contentPanel, BorderLayout.CENTER);

    // Lưu reference tới lblValue để dễ truy cập sau này
    card.putClientProperty("valueLabel", lblValue);

    // Accent bar
    JPanel accentBar = new JPanel();
    accentBar.setBackground(accentColor);
    accentBar.setPreferredSize(new Dimension(4, 0));
    card.add(accentBar, BorderLayout.WEST);

    return card;
  }

  /**
   * Tạo card trống để layout đẹp
   */
  private JPanel createEmptyCard() {
    JPanel card = new JPanel();
    card.setBackground(new Color(240, 242, 245));
    card.setBorder(null);
    return card;
  }

  /**
   * Load dữ liệu thống kê từ database
   */
  private void loadDashboardData() {
    // Load trong thread riêng để không block UI
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      private int tongThuoc = 0;
      private int tongBacSi = 0;
      private int tongHDKham = 0;
      private int tongHDThuoc = 0;
      private int tongUsers = 0;

      @Override
      protected Void doInBackground() throws Exception {
        tongThuoc = getTongThuoc();
        tongBacSi = getTongBacSi();
        tongHDKham = getTongHoaDonKham();
        tongHDThuoc = getTongHoaDonThuoc();
        tongUsers = getTongUsers();
        return null;
      }

      @Override
      protected void done() {
        // Cập nhật UI khi đã load xong
        lblTongThuoc.setText(String.valueOf(tongThuoc));
        lblTongBacSi.setText(String.valueOf(tongBacSi));
        lblTongHDKham.setText(String.valueOf(tongHDKham));
        lblTongHDThuoc.setText(String.valueOf(tongHDThuoc));
        lblTongUsers.setText(String.valueOf(tongUsers));
      }
    };
    worker.execute();
  }

  /**
   * Lấy tổng số thuốc từ database
   */
  private int getTongThuoc() {
    String sql = "SELECT COUNT(*) AS total FROM Thuoc";
    return executeCountQuery(sql);
  }

  /**
   * Lấy tổng số bác sĩ từ database
   */
  private int getTongBacSi() {
    String sql = "SELECT COUNT(*) AS total FROM BacSi";
    return executeCountQuery(sql);
  }

  /**
   * Lấy tổng số hóa đơn khám từ database
   */
  private int getTongHoaDonKham() {
    String sql = "SELECT COUNT(*) AS total FROM HoaDonKham";
    return executeCountQuery(sql);
  }

  /**
   * Lấy tổng số hóa đơn thuốc từ database
   */
  private int getTongHoaDonThuoc() {
    String sql = "SELECT COUNT(*) AS total FROM HoaDonThuoc";
    return executeCountQuery(sql);
  }

  /**
   * Lấy tổng số users từ database
   */
  private int getTongUsers() {
    String sql = "SELECT COUNT(*) AS total FROM Users";
    return executeCountQuery(sql);
  }

  /**
   * Thực thi câu query COUNT và trả về kết quả
   */
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
      System.err.println("❌ Lỗi khi thực thi query: " + sql);
      e.printStackTrace();
    }
    return count;
  }

  /**
   * Refresh dữ liệu dashboard
   */
  public void refreshData() {
    loadDashboardData();
  }
}
