package phongkham.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.db.DBConnection;

public class PhieuNhapDAO {

  // ✅ 1. METHOD DÙNG CHUNG: Tạo DTO từ ResultSet
  private PhieuNhapDTO mapResultSet(ResultSet rs) throws SQLException {
    PhieuNhapDTO pn = new PhieuNhapDTO();
    pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
    pn.setMaNCC(rs.getString("MaNCC"));
    pn.setNgayNhap(rs.getDate("NgayNhap"));
    pn.setNguoiGiao(rs.getString("NguoiGiao"));
    pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
    pn.setTrangThai(rs.getString("TrangThai"));
    return pn;
  }

  // ✅ 2. METHOD DÙNG CHUNG: Execute query trả về List
  private ArrayList<PhieuNhapDTO> executeQuery(String sql, Object... params) {
    ArrayList<PhieuNhapDTO> list = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      // Set parameters
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSet(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println("❌ Lỗi query: " + e.getMessage());
    }
    return list;
  }

  // ✅ 3. METHOD DÙNG CHUNG: Execute update
  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("❌ Lỗi update: " + e.getMessage());
    }
    return false;
  }

  // ===== CRUD OPERATIONS =====

  public ArrayList<PhieuNhapDTO> getAll() {
    return executeQuery("SELECT * FROM PhieuNhap ORDER BY NgayNhap DESC");
  }

  public PhieuNhapDTO getById(String maPhieuNhap) {
    ArrayList<PhieuNhapDTO> list = executeQuery(
      "SELECT * FROM PhieuNhap WHERE MaPhieuNhap = ?",
      maPhieuNhap
    );
    return list.isEmpty() ? null : list.get(0);
  }

  public boolean insert(PhieuNhapDTO pn) {
    String sql =
      "INSERT INTO PhieuNhap (MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai) " +
      "VALUES (?, ?, ?, ?, ?, ?)";

    boolean success = executeUpdate(
      sql,
      pn.getMaPhieuNhap(),
      pn.getMaNCC(),
      pn.getNgayNhap(),
      pn.getNguoiGiao(),
      pn.getTongTienNhap(),
      pn.getTrangThai() != null ? pn.getTrangThai() : "CHO_DUYET"
    );

    if (success) System.out.println(
      "✅ Thêm phiếu nhập: " + pn.getMaPhieuNhap()
    );
    return success;
  }

  public boolean update(PhieuNhapDTO pn) {
    String sql =
      "UPDATE PhieuNhap SET MaNCC=?, NgayNhap=?, NguoiGiao=?, TongTienNhap=?, TrangThai=? " +
      "WHERE MaPhieuNhap=?";

    boolean success = executeUpdate(
      sql,
      pn.getMaNCC(),
      pn.getNgayNhap(),
      pn.getNguoiGiao(),
      pn.getTongTienNhap(),
      pn.getTrangThai(),
      pn.getMaPhieuNhap()
    );

    if (success) System.out.println(
      "✅ Cập nhật phiếu nhập: " + pn.getMaPhieuNhap()
    );
    return success;
  }

  public boolean delete(String maPhieuNhap) {
    return executeUpdate(
      "DELETE FROM PhieuNhap WHERE MaPhieuNhap=?",
      maPhieuNhap
    );
  }

  public boolean capNhatTrangThai(String maPN, String trangThaiMoi) {
    boolean success = executeUpdate(
      "UPDATE PhieuNhap SET TrangThai = ? WHERE MaPhieuNhap = ?",
      trangThaiMoi,
      maPN
    );

    if (success) {
      System.out.println(
        "✅ Cập nhật trạng thái: " + maPN + " → " + trangThaiMoi
      );
    } else {
      System.err.println("⚠️ Không tìm thấy phiếu nhập: " + maPN);
    }
    return success;
  }

  // ===== SEARCH OPERATIONS =====

  public ArrayList<PhieuNhapDTO> getByDate(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return executeQuery(
      "SELECT * FROM PhieuNhap WHERE NgayNhap BETWEEN ? AND ? ORDER BY NgayNhap DESC",
      startDate,
      endDate
    );
  }

  public ArrayList<PhieuNhapDTO> getByMaNCC(String maNCC) {
    return executeQuery(
      "SELECT * FROM PhieuNhap WHERE MaNCC = ? ORDER BY NgayNhap DESC",
      maNCC
    );
  }

  public ArrayList<PhieuNhapDTO> getByNguoiGiao(String nguoiGiao) {
    return executeQuery(
      "SELECT * FROM PhieuNhap WHERE NguoiGiao LIKE ? ORDER BY NgayNhap DESC",
      "%" + nguoiGiao + "%"
    );
  }

  public ArrayList<PhieuNhapDTO> getByTrangThai(String trangThai) {
    return executeQuery(
      "SELECT * FROM PhieuNhap WHERE TrangThai = ? ORDER BY NgayNhap DESC",
      trangThai
    );
  }

  // ===== UTILITY OPERATIONS =====

  public boolean deleteByNCC(String maNCC) {
    return executeUpdate("DELETE FROM PhieuNhap WHERE MaNCC = ?", maNCC);
  }

  public boolean hasPhieuNhap(String maNCC) {
    String sql = "SELECT COUNT(*) FROM PhieuNhap WHERE MaNCC = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maNCC);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() && rs.getInt(1) > 0;
      }
    } catch (SQLException e) {
      System.err.println("❌ Lỗi kiểm tra phiếu nhập: " + e.getMessage());
    }
    return false;
  }

  // ✅ THÊM: Tìm kiếm đa điều kiện
  public ArrayList<PhieuNhapDTO> search(String keyword) {
    return executeQuery(
      "SELECT * FROM PhieuNhap WHERE MaPhieuNhap LIKE ? OR NguoiGiao LIKE ? ORDER BY NgayNhap DESC",
      "%" + keyword + "%",
      "%" + keyword + "%"
    );
  }

  // ✅ THÊM: Thống kê tổng tiền theo trạng thái
  public double getTongTienByTrangThai(String trangThai) {
    String sql = "SELECT SUM(TongTienNhap) FROM PhieuNhap WHERE TrangThai = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, trangThai);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getDouble(1) : 0;
      }
    } catch (SQLException e) {
      System.err.println("❌ Lỗi thống kê: " + e.getMessage());
    }
    return 0;
  }
}
