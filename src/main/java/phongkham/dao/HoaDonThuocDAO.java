package phongkham.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.db.DBConnection;

public class HoaDonThuocDAO {

  // Tự động sinh mã hóa đơn
  public String generateMaHoaDon() {
    String sql =
      "SELECT MaHoaDon FROM HoaDonThuoc ORDER BY MaHoaDon DESC LIMIT 1";
    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      if (rs.next()) {
        String lastMa = rs.getString("MaHoaDon");
        // Giả sử mã có dạng HDT001, HDT002, ...
        if (lastMa != null && lastMa.startsWith("HDT")) {
          int num = Integer.parseInt(lastMa.substring(3));
          return String.format("HDT%03d", num + 1);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi sinh mã hóa đơn: " + e.getMessage());
    }
    // Nếu chưa có hóa đơn nào, bắt đầu từ HDT001
    return "HDT001";
  }

  // Thêm mới
  public boolean insert(HoaDonThuocDTO hoaDon) {
    // Tự động sinh mã nếu chưa có
    if (hoaDon.getMaHoaDon() == null || hoaDon.getMaHoaDon().isEmpty()) {
      hoaDon.setMaHoaDon(generateMaHoaDon());
    }

    String trangThaiThanhToan = StatusNormalizer.normalizePaymentStatus(
      hoaDon.getTrangThaiThanhToan()
    );
    if (trangThaiThanhToan.isEmpty()) {
      trangThaiThanhToan = StatusNormalizer.CHUA_THANH_TOAN;
    }
    String trangThaiLayThuoc = StatusNormalizer.normalizePickupStatus(
      hoaDon.getTrangThaiLayThuoc()
    );
    if (trangThaiLayThuoc.isEmpty()) {
      trangThaiLayThuoc = StatusNormalizer.CHO_LAY;
    }

    String sql =
      "INSERT INTO HoaDonThuoc (MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc, TenBenhNhan, SdtBenhNhan, Active) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setString(1, hoaDon.getMaHoaDon());
      pstmt.setObject(2, hoaDon.getMaDonThuoc());
      pstmt.setObject(3, hoaDon.getNgayLap());
      pstmt.setDouble(4, hoaDon.getTongTien());
      pstmt.setString(5, hoaDon.getGhiChu());
      pstmt.setString(6, trangThaiThanhToan);
      pstmt.setObject(7, hoaDon.getNgayThanhToan());
      pstmt.setString(8, trangThaiLayThuoc);
      pstmt.setString(9, hoaDon.getTenBenhNhan());
      pstmt.setString(10, hoaDon.getSdtBenhNhan());
      pstmt.setBoolean(11, hoaDon.isActive());

      int rowsInserted = pstmt.executeUpdate();
      return rowsInserted > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error inserting HoaDonThuoc: " + e.getMessage());
      return false;
    }
  }

  // Cập nhật
  public boolean update(HoaDonThuocDTO hoaDon) {
    String trangThaiThanhToan = StatusNormalizer.normalizePaymentStatus(
      hoaDon.getTrangThaiThanhToan()
    );
    if (trangThaiThanhToan.isEmpty()) {
      trangThaiThanhToan = StatusNormalizer.CHUA_THANH_TOAN;
    }
    String trangThaiLayThuoc = StatusNormalizer.normalizePickupStatus(
      hoaDon.getTrangThaiLayThuoc()
    );
    if (trangThaiLayThuoc.isEmpty()) {
      trangThaiLayThuoc = StatusNormalizer.CHO_LAY;
    }

    String sql =
      "UPDATE HoaDonThuoc SET MaDonThuoc = ?, NgayLap = ?, TongTien = ?, GhiChu = ?, " +
      "TrangThaiThanhToan = ?, NgayThanhToan = ?, TrangThaiLayThuoc = ?, TenBenhNhan = ?, SdtBenhNhan = ?, Active = ? " +
      "WHERE MaHoaDon = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setObject(1, hoaDon.getMaDonThuoc());
      pstmt.setObject(2, hoaDon.getNgayLap());
      pstmt.setDouble(3, hoaDon.getTongTien());
      pstmt.setString(4, hoaDon.getGhiChu());
      pstmt.setString(5, trangThaiThanhToan);
      pstmt.setObject(6, hoaDon.getNgayThanhToan());
      pstmt.setString(7, trangThaiLayThuoc);
      pstmt.setString(8, hoaDon.getTenBenhNhan());
      pstmt.setString(9, hoaDon.getSdtBenhNhan());
      pstmt.setBoolean(10, hoaDon.isActive());
      pstmt.setString(11, hoaDon.getMaHoaDon());

      int rowsUpdated = pstmt.executeUpdate();
      return rowsUpdated > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error updating HoaDonThuoc: " + e.getMessage());
      return false;
    }
  }

  // Xóa theo ID
  public boolean delete(String maHoaDon) {
    String sql = "DELETE FROM HoaDonThuoc WHERE MaHoaDon = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setString(1, maHoaDon);
      int rowsDeleted = pstmt.executeUpdate();
      return rowsDeleted > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error deleting HoaDonThuoc: " + e.getMessage());
      return false;
    }
  }

  // Lấy theo ID
  public HoaDonThuocDTO getById(String maHoaDon) {
    String sql = "SELECT * FROM HoaDonThuoc WHERE MaHoaDon = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setString(1, maHoaDon);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToDTO(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println(
        "✗ Error getting HoaDonThuoc by ID: " + e.getMessage()
      );
    }
    return null;
  }

  // Lấy tất cả (active)
  public List<HoaDonThuocDTO> getAll() {
    List<HoaDonThuocDTO> list = new ArrayList<>();
    String sql =
      "SELECT * FROM HoaDonThuoc WHERE Active = 1 ORDER BY NgayLap DESC";

    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      while (rs.next()) {
        list.add(mapResultSetToDTO(rs));
      }
    } catch (SQLException e) {
      System.err.println("✗ Error getting all HoaDonThuoc: " + e.getMessage());
    }
    return list;
  }

  // Lấy hóa đơn theo khoảng ngày
  public List<HoaDonThuocDTO> getByDate(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<HoaDonThuocDTO> list = new ArrayList<>();
    String sql =
      "SELECT * FROM HoaDonThuoc WHERE NgayLap BETWEEN ? AND ? AND Active = 1 ORDER BY NgayLap DESC";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setObject(1, startDate);
      pstmt.setObject(2, endDate);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToDTO(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println(
        "✗ Error getting HoaDonThuoc by date: " + e.getMessage()
      );
    }
    return list;
  }

  // Lấy hóa đơn theo trạng thái thanh toán
  public List<HoaDonThuocDTO> getByPaymentStatus(String trangThai) {
    String statusChuan = StatusNormalizer.normalizePaymentStatus(trangThai);
    List<HoaDonThuocDTO> list = new ArrayList<>();
    for (HoaDonThuocDTO hoaDon : getAll()) {
      if (
        statusChuan.equals(
          StatusNormalizer.normalizePaymentStatus(
            hoaDon.getTrangThaiThanhToan()
          )
        )
      ) {
        list.add(hoaDon);
      }
    }
    return list;
  }

  // Tính tổng doanh thu trong khoảng ngày
  public double getTotalRevenue(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    String sql =
      "SELECT SUM(TongTien) FROM HoaDonThuoc WHERE NgayLap BETWEEN ? AND ? AND UPPER(TRIM(COALESCE(TrangThaiThanhToan, ''))) IN ('DA_THANH_TOAN', 'ĐÃ THANH TOÁN', 'DA THANH TOAN')";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setObject(1, startDate);
      pstmt.setObject(2, endDate);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          double total = rs.getDouble(1);
          return Double.isNaN(total) ? 0 : total;
        }
      }
    } catch (SQLException e) {
      System.err.println("✗ Error calculating revenue: " + e.getMessage());
    }
    return 0;
  }

  // Cập nhật trạng thái thanh toán
  public boolean updatePaymentStatus(
    String maHoaDon,
    String trangThai,
    LocalDateTime ngayThanhToan
  ) {
    String trangThaiChuan = StatusNormalizer.normalizePaymentStatus(trangThai);
    String sql =
      "UPDATE HoaDonThuoc SET TrangThaiThanhToan = ?, NgayThanhToan = ? WHERE MaHoaDon = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setString(1, trangThaiChuan);
      pstmt.setObject(2, ngayThanhToan);
      pstmt.setString(3, maHoaDon);

      int rowsUpdated = pstmt.executeUpdate();
      return rowsUpdated > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error updating payment status: " + e.getMessage());
      return false;
    }
  }

  // Map ResultSet thành DTO
  private HoaDonThuocDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
    HoaDonThuocDTO dto = new HoaDonThuocDTO();
    dto.setMaHoaDon(rs.getString("MaHoaDon"));
    dto.setMaDonThuoc(rs.getString("MaDonThuoc"));

    dto.setNgayLap(rs.getObject("NgayLap", LocalDateTime.class));
    dto.setTongTien(rs.getDouble("TongTien"));
    dto.setGhiChu(rs.getString("GhiChu"));
    dto.setTrangThaiThanhToan(
      StatusNormalizer.normalizePaymentStatus(
        rs.getString("TrangThaiThanhToan")
      )
    );
    dto.setNgayThanhToan(rs.getObject("NgayThanhToan", LocalDateTime.class));
    dto.setTrangThaiLayThuoc(
      StatusNormalizer.normalizePickupStatus(rs.getString("TrangThaiLayThuoc"))
    );
    dto.setTenBenhNhan(rs.getString("TenBenhNhan"));
    dto.setSdtBenhNhan(rs.getString("SdtBenhNhan"));
    dto.setActive(rs.getBoolean("Active"));

    return dto;
  }
}
