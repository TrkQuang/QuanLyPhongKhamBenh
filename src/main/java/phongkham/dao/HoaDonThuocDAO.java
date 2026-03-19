package phongkham.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.db.DBConnection;

public class HoaDonThuocDAO {

  private String normalizePaymentOrDefault(String rawStatus) {
    String status = StatusNormalizer.normalizePaymentStatus(rawStatus);
    return status.isEmpty() ? StatusNormalizer.CHUA_THANH_TOAN : status;
  }

  private String normalizePickupOrDefault(String rawStatus) {
    String status = StatusNormalizer.normalizePickupStatus(rawStatus);
    return status.isEmpty() ? StatusNormalizer.CHO_LAY : status;
  }

  private List<HoaDonThuocDTO> executeQuery(String sql, Object... params) {
    List<HoaDonThuocDTO> list = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToDTO(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println("✗ Error query HoaDonThuoc: " + e.getMessage());
    }
    return list;
  }

  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error update HoaDonThuoc: " + e.getMessage());
      return false;
    }
  }

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

    String trangThaiThanhToan = normalizePaymentOrDefault(
      hoaDon.getTrangThaiThanhToan()
    );
    String trangThaiLayThuoc = normalizePickupOrDefault(
      hoaDon.getTrangThaiLayThuoc()
    );

    String sql =
      "INSERT INTO HoaDonThuoc (MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc, TenBenhNhan, SdtBenhNhan, Active) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    return executeUpdate(
      sql,
      hoaDon.getMaHoaDon(),
      hoaDon.getMaDonThuoc(),
      hoaDon.getNgayLap(),
      hoaDon.getTongTien(),
      hoaDon.getGhiChu(),
      trangThaiThanhToan,
      hoaDon.getNgayThanhToan(),
      trangThaiLayThuoc,
      hoaDon.getTenBenhNhan(),
      hoaDon.getSdtBenhNhan(),
      hoaDon.isActive()
    );
  }

  // Cập nhật
  public boolean update(HoaDonThuocDTO hoaDon) {
    String trangThaiThanhToan = normalizePaymentOrDefault(
      hoaDon.getTrangThaiThanhToan()
    );
    String trangThaiLayThuoc = normalizePickupOrDefault(
      hoaDon.getTrangThaiLayThuoc()
    );

    String sql =
      "UPDATE HoaDonThuoc SET MaDonThuoc = ?, NgayLap = ?, TongTien = ?, GhiChu = ?, " +
      "TrangThaiThanhToan = ?, NgayThanhToan = ?, TrangThaiLayThuoc = ?, TenBenhNhan = ?, SdtBenhNhan = ?, Active = ? " +
      "WHERE MaHoaDon = ?";

    return executeUpdate(
      sql,
      hoaDon.getMaDonThuoc(),
      hoaDon.getNgayLap(),
      hoaDon.getTongTien(),
      hoaDon.getGhiChu(),
      trangThaiThanhToan,
      hoaDon.getNgayThanhToan(),
      trangThaiLayThuoc,
      hoaDon.getTenBenhNhan(),
      hoaDon.getSdtBenhNhan(),
      hoaDon.isActive(),
      hoaDon.getMaHoaDon()
    );
  }

  // Xóa theo ID
  public boolean delete(String maHoaDon) {
    return executeUpdate(
      "DELETE FROM HoaDonThuoc WHERE MaHoaDon = ?",
      maHoaDon
    );
  }

  // Lấy theo ID
  public HoaDonThuocDTO getById(String maHoaDon) {
    List<HoaDonThuocDTO> list = executeQuery(
      "SELECT * FROM HoaDonThuoc WHERE MaHoaDon = ?",
      maHoaDon
    );
    return list.isEmpty() ? null : list.get(0);
  }

  // Lấy tất cả (active)
  public List<HoaDonThuocDTO> getAll() {
    return executeQuery(
      "SELECT * FROM HoaDonThuoc WHERE Active = 1 ORDER BY NgayLap DESC"
    );
  }

  // Lấy hóa đơn theo khoảng ngày
  public List<HoaDonThuocDTO> getByDate(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return executeQuery(
      "SELECT * FROM HoaDonThuoc WHERE NgayLap BETWEEN ? AND ? AND Active = 1 ORDER BY NgayLap DESC",
      startDate,
      endDate
    );
  }

  // Lấy hóa đơn theo trạng thái thanh toán
  public List<HoaDonThuocDTO> getByPaymentStatus(String trangThai) {
    String statusChuan = normalizePaymentOrDefault(trangThai);
    return executeQuery(
      "SELECT * FROM HoaDonThuoc WHERE Active = 1 AND TrangThaiThanhToan = ? ORDER BY NgayLap DESC",
      statusChuan
    );
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
    return executeUpdate(
      "UPDATE HoaDonThuoc SET TrangThaiThanhToan = ?, NgayThanhToan = ? WHERE MaHoaDon = ?",
      trangThaiChuan,
      ngayThanhToan,
      maHoaDon
    );
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
