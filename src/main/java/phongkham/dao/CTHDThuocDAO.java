package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.db.DBConnection;

public class CTHDThuocDAO {

  private static final String BASE_SELECT_WITH_THUOC_JOIN =
    "SELECT cthd.*, t.TenThuoc, t.DonVi " +
    "FROM CTHDThuoc cthd " +
    "LEFT JOIN Thuoc t ON cthd.MaThuoc = t.MaThuoc ";

  private List<CTHDThuocDTO> executeQuery(String sql, Object... params) {
    List<CTHDThuocDTO> list = new ArrayList<>();
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
      System.err.println("✗ Error query CTHDThuoc: " + e.getMessage());
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
      System.err.println("✗ Error update CTHDThuoc: " + e.getMessage());
      return false;
    }
  }

  private double querySingleDouble(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next() ? rs.getDouble(1) : 0;
      }
    } catch (SQLException e) {
      System.err.println("✗ Error scalar query CTHDThuoc: " + e.getMessage());
    }
    return 0;
  }

  private int querySingleInt(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    } catch (SQLException e) {
      System.err.println("✗ Error scalar count CTHDThuoc: " + e.getMessage());
    }
    return 0;
  }

  // Tự động sinh mã chi tiết hóa đơn
  public String generateMaCTHD() {
    String sql =
      "SELECT MaCTHDThuoc FROM CTHDThuoc ORDER BY MaCTHDThuoc DESC LIMIT 1";
    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      if (rs.next()) {
        String lastMa = rs.getString("MaCTHDThuoc");
        // Giả sử mã có dạng CTHD001, CTHD002, ...
        if (lastMa != null && lastMa.startsWith("CTHD")) {
          int num = Integer.parseInt(lastMa.substring(4));
          return String.format("CTHD%03d", num + 1);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi sinh mã chi tiết HD: " + e.getMessage());
    }
    // Nếu chưa có, bắt đầu từ CTHD001
    return "CTHD001";
  }

  // Thêm mới
  public boolean insert(CTHDThuocDTO cthd) {
    // Tự động sinh mã nếu chưa có
    if (cthd.getMaCTHDThuoc() == null || cthd.getMaCTHDThuoc().isEmpty()) {
      cthd.setMaCTHDThuoc(generateMaCTHD());
    }

    String sql =
      "INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    return executeUpdate(
      sql,
      cthd.getMaCTHDThuoc(),
      cthd.getMaHoaDon(),
      cthd.getMaThuoc(),
      cthd.getSoLuong(),
      cthd.getDonGia(),
      cthd.getThanhTien(),
      cthd.getGhiChu(),
      cthd.isActive()
    );
  }

  // Cập nhật
  public boolean update(CTHDThuocDTO cthd) {
    String sql =
      "UPDATE CTHDThuoc SET MaHoaDon = ?, MaThuoc = ?, SoLuong = ?, DonGia = ?, " +
      "ThanhTien = ?, GhiChu = ?, Active = ? WHERE MaCTHDThuoc = ?";

    return executeUpdate(
      sql,
      cthd.getMaHoaDon(),
      cthd.getMaThuoc(),
      cthd.getSoLuong(),
      cthd.getDonGia(),
      cthd.getThanhTien(),
      cthd.getGhiChu(),
      cthd.isActive(),
      cthd.getMaCTHDThuoc()
    );
  }

  // Xóa theo ID
  public boolean delete(String maCTHDThuoc) {
    return executeUpdate(
      "DELETE FROM CTHDThuoc WHERE MaCTHDThuoc = ?",
      maCTHDThuoc
    );
  }

  // Lấy theo ID
  public CTHDThuocDTO getById(String maCTHDThuoc) {
    List<CTHDThuocDTO> list = executeQuery(
      BASE_SELECT_WITH_THUOC_JOIN + "WHERE cthd.MaCTHDThuoc = ?",
      maCTHDThuoc
    );
    return list.isEmpty() ? null : list.get(0);
  }

  // Lấy chi tiết theo hóa đơn
  public List<CTHDThuocDTO> getByInvoice(String maHoaDon) {
    return executeQuery(
      BASE_SELECT_WITH_THUOC_JOIN +
        "WHERE cthd.MaHoaDon = ? AND cthd.Active = 1 ORDER BY cthd.MaCTHDThuoc ASC",
      maHoaDon
    );
  }

  // Lấy tất cả chi tiết thuốc
  public List<CTHDThuocDTO> getAll() {
    return executeQuery(
      BASE_SELECT_WITH_THUOC_JOIN +
        "WHERE cthd.Active = 1 ORDER BY cthd.MaCTHDThuoc DESC"
    );
  }

  // Tính tổng tiền hóa đơn
  public double getTotalAmount(String maHoaDon) {
    return querySingleDouble(
      "SELECT COALESCE(SUM(ThanhTien), 0) FROM CTHDThuoc WHERE MaHoaDon = ? AND Active = 1",
      maHoaDon
    );
  }

  // Xóa tất cả chi tiết của hóa đơn
  public boolean deleteByInvoice(String maHoaDon) {
    return executeUpdate(
      "UPDATE CTHDThuoc SET Active = 0 WHERE MaHoaDon = ?",
      maHoaDon
    );
  }

  /**
   * Kiểm tra hóa đơn có chi tiết chưa
   */
  public boolean hasDetails(String maHoaDon) {
    return (
      querySingleInt(
        "SELECT COUNT(*) FROM CTHDThuoc WHERE MaHoaDon = ? AND Active = 1",
        maHoaDon
      ) >
      0
    );
  }

  // Map ResultSet thành DTO
  private CTHDThuocDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
    CTHDThuocDTO dto = new CTHDThuocDTO();
    dto.setMaCTHDThuoc(rs.getString("MaCTHDThuoc"));
    dto.setMaHoaDon(rs.getString("MaHoaDon"));
    dto.setMaThuoc(rs.getString("MaThuoc"));
    dto.setTenThuoc(rs.getString("TenThuoc"));
    dto.setDonVi(rs.getString("DonVi"));
    dto.setSoLuong(rs.getInt("SoLuong"));
    dto.setDonGia(rs.getDouble("DonGia"));
    dto.setThanhTien(rs.getDouble("ThanhTien"));
    dto.setGhiChu(rs.getString("GhiChu"));
    dto.setActive(rs.getBoolean("Active"));
    return dto;
  }
}
