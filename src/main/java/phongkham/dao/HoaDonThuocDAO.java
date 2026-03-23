package phongkham.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.XuatThuocTheoLoDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.db.DBConnection;

public class HoaDonThuocDAO {

  private static final int MAX_INSERT_RETRY = 5;

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
    try (Connection conn = DBConnection.getConnection()) {
      return generateMaHoaDon(conn);
    } catch (SQLException e) {
      System.err.println("Lỗi sinh mã hóa đơn: " + e.getMessage());
      return "HDT001";
    }
  }

  private String generateMaHoaDon(Connection conn) throws SQLException {
    String sql =
      "SELECT COALESCE(MAX(CAST(SUBSTRING(MaHoaDon, 4) AS UNSIGNED)), 0) " +
      "FROM HoaDonThuoc WHERE MaHoaDon REGEXP '^HDT[0-9]+$'";
    try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
      int next = 1;
      if (rs.next()) {
        next = rs.getInt(1) + 1;
      }
      return String.format("HDT%03d", next);
    }
  }

  private boolean isDuplicateKey(SQLException e) {
    return e != null && (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState()));
  }

  // Thêm mới
  public boolean insert(HoaDonThuocDTO hoaDon) {
    String trangThaiThanhToan = normalizePaymentOrDefault(
      hoaDon.getTrangThaiThanhToan()
    );
    String trangThaiLayThuoc = normalizePickupOrDefault(
      hoaDon.getTrangThaiLayThuoc()
    );

    String sql =
      "INSERT INTO HoaDonThuoc (MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc, TenBenhNhan, SdtBenhNhan, Active) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String providedMaHoaDon = hoaDon.getMaHoaDon();
    boolean autoGenerate = providedMaHoaDon == null || providedMaHoaDon.trim().isEmpty();

    try (Connection conn = DBConnection.getConnection()) {
      for (int attempt = 0; attempt < MAX_INSERT_RETRY; attempt++) {
        String maHoaDon = autoGenerate
          ? generateMaHoaDon(conn)
          : providedMaHoaDon.trim();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
          pstmt.setString(1, maHoaDon);
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

          if (pstmt.executeUpdate() > 0) {
            hoaDon.setMaHoaDon(maHoaDon);
            return true;
          }
          return false;
        } catch (SQLException e) {
          if (autoGenerate && isDuplicateKey(e) && attempt < MAX_INSERT_RETRY - 1) {
            continue;
          }
            System.err.println("✗ Error insert HoaDonThuoc: " + e.getMessage());
          return false;
        }
      }
    } catch (SQLException e) {
      System.err.println("✗ Error insert HoaDonThuoc: " + e.getMessage());
      return false;
    }

    return false;
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

  public boolean updatePaymentAndPickupStatus(
    String maHoaDon,
    String trangThaiThanhToan,
    LocalDateTime ngayThanhToan,
    String trangThaiLayThuoc
  ) {
    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      trangThaiThanhToan
    );
    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      trangThaiLayThuoc
    );
    return executeUpdate(
      "UPDATE HoaDonThuoc SET TrangThaiThanhToan = ?, NgayThanhToan = ?, TrangThaiLayThuoc = ? WHERE MaHoaDon = ?",
      paymentStatus,
      ngayThanhToan,
      pickupStatus,
      maHoaDon
    );
  }

  public List<XuatThuocTheoLoDTO> getXuatTheoLoByMaHoaDon(String maHoaDon) {
    List<XuatThuocTheoLoDTO> list = new ArrayList<>();
    String sql =
      "SELECT MaXuatLo, MaHoaDon, MaCTHDThuoc, MaCTPN, MaThuoc, SoLo, HanSuDung, SoLuongXuat, NgayXuat " +
      "FROM XuatThuocTheoLo WHERE MaHoaDon = ? ORDER BY HanSuDung ASC, MaXuatLo ASC";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maHoaDon);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          XuatThuocTheoLoDTO dto = new XuatThuocTheoLoDTO();
          dto.setMaXuatLo(rs.getLong("MaXuatLo"));
          dto.setMaHoaDon(rs.getString("MaHoaDon"));
          dto.setMaCTHDThuoc(rs.getString("MaCTHDThuoc"));
          dto.setMaCTPN(rs.getString("MaCTPN"));
          dto.setMaThuoc(rs.getString("MaThuoc"));
          dto.setSoLo(rs.getString("SoLo"));
          dto.setHanSuDung(
            rs.getObject("HanSuDung", java.time.LocalDate.class)
          );
          dto.setSoLuongXuat(rs.getInt("SoLuongXuat"));
          dto.setNgayXuat(rs.getObject("NgayXuat", LocalDateTime.class));
          list.add(dto);
        }
      }
    } catch (SQLException e) {
      System.err.println("✗ Error query XuatThuocTheoLo: " + e.getMessage());
    }
    return list;
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
