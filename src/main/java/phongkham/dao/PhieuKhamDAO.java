package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import phongkham.DTO.PhieuKhamDTO;
import phongkham.db.DBConnection;
public class PhieuKhamDAO {

  // ✅ 1. METHOD DÙNG CHUNG: Tạo DTO từ ResultSet
  private PhieuKhamDTO mapResultSet(ResultSet rs) throws SQLException {
    PhieuKhamDTO pk = new PhieuKhamDTO();
    pk.setMaPhieuKham(rs.getString("MaPhieuKham"));
    pk.setMaLichKham(rs.getString("MaLichKham"));
    pk.setMaBacSi(rs.getString("MaBacSi"));
    pk.setThoiGianTao(rs.getString("ThoiGianTao"));
    pk.setTrieuChungSoBo(rs.getString("TrieuChungSoBo"));
    return pk;
  }

  // ✅ 2. METHOD DÙNG CHUNG: Execute query trả về List
  private ArrayList<PhieuKhamDTO> executeQuery(String sql, Object... params) {
    ArrayList<PhieuKhamDTO> list = new ArrayList<>();
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
          // ✅ SỬA LỖI: Thêm while
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

  public ArrayList<PhieuKhamDTO> getAll() {
    return executeQuery("SELECT * FROM PhieuKham ORDER BY ThoiGianTao DESC");
  }

  // ✅ THÊM: getById()
  public PhieuKhamDTO getById(String maPhieuKham) {
    ArrayList<PhieuKhamDTO> list = executeQuery(
      "SELECT * FROM PhieuKham WHERE MaPhieuKham = ?",
      maPhieuKham
    );
    return list.isEmpty() ? null : list.get(0);
  }

  public boolean insert(PhieuKhamDTO pk) {
    String sql =
      "INSERT INTO PhieuKham (MaPhieuKham, MaLichKham, MaBacSi, ThoiGianTao, TrieuChungSoBo) " +
      "VALUES (?, ?, ?, ?, ?)";

    boolean success = executeUpdate(
      sql,
      pk.getMaPhieuKham(),
      pk.getMaLichKham(),
      pk.getMaBacSi(),
      pk.getThoiGianTao(),
      pk.getTrieuChungSoBo()
    );

    if (success) System.out.println(
      "✅ Thêm phiếu khám: " + pk.getMaPhieuKham()
    );
    return success;
  }

  public boolean update(PhieuKhamDTO pk) {
    String sql =
      "UPDATE PhieuKham SET MaLichKham=?, MaBacSi=?, ThoiGianTao=?, TrieuChungSoBo=? " +
      "WHERE MaPhieuKham=?"; // ✅ SỬA LỖI: Thêm dấu phẩy

    boolean success = executeUpdate(
      sql,
      pk.getMaLichKham(),
      pk.getMaBacSi(),
      pk.getThoiGianTao(),
      pk.getTrieuChungSoBo(),
      pk.getMaPhieuKham()
    );

    if (success) System.out.println(
      "✅ Cập nhật phiếu khám: " + pk.getMaPhieuKham()
    );
    return success;
  }

  public boolean delete(String maPhieuKham) {
    String sql = "DELETE FROM PhieuKham WHERE MaPhieuKham=?"; // ✅ SỬA LỖI: Thêm FROM
    return executeUpdate(sql, maPhieuKham);
  }

  // ===== SEARCH OPERATIONS =====

  // ✅ THÊM: Tìm theo mã lịch khám
  public ArrayList<PhieuKhamDTO> getByMaLichKham(String maLichKham) {
    return executeQuery(
      "SELECT * FROM PhieuKham WHERE MaLichKham = ? ORDER BY ThoiGianTao DESC",
      maLichKham
    );
  }

  // ✅ THÊM: Tìm theo mã bác sĩ
  public ArrayList<PhieuKhamDTO> getByMaBacSi(String maBacSi) {
    return executeQuery(
      "SELECT * FROM PhieuKham WHERE MaBacSi = ? ORDER BY ThoiGianTao DESC",
      maBacSi
    );
  }

  // ✅ THÊM: Tìm theo triệu chứng
  public ArrayList<PhieuKhamDTO> getByTrieuChung(String trieuchung) {
    return executeQuery(
      "SELECT * FROM PhieuKham WHERE TrieuChungSoBo LIKE ? ORDER BY ThoiGianTao DESC",
      "%" + trieuchung + "%"
    );
  }

  // ✅ THÊM: Tìm kiếm đa điều kiện
  public ArrayList<PhieuKhamDTO> search(String keyword) {
    return executeQuery(
      "SELECT * FROM PhieuKham WHERE MaPhieuKham LIKE ? OR TrieuChungSoBo LIKE ? ORDER BY ThoiGianTao DESC",
      "%" + keyword + "%",
      "%" + keyword + "%"
    );
  }

  // ✅ THÊM: Đếm số phiếu khám của bác sĩ
  public int countByBacSi(String maBacSi) {
    String sql = "SELECT COUNT(*) FROM PhieuKham WHERE MaBacSi = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maBacSi);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    } catch (SQLException e) {
      System.err.println("❌ Lỗi đếm: " + e.getMessage());
    }
    return 0;
  }

  // ✅ THÊM: Kiểm tra phiếu khám tồn tại
  public boolean exists(String maPhieuKham) {
    String sql = "SELECT COUNT(*) FROM PhieuKham WHERE MaPhieuKham = ?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maPhieuKham);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() && rs.getInt(1) > 0;
      }
    } catch (SQLException e) {
      System.err.println("❌ Lỗi kiểm tra: " + e.getMessage());
    }
    return false;
  }
}
