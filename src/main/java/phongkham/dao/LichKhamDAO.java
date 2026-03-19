package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import phongkham.DTO.LichKhamDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.db.DBConnection;

public class LichKhamDAO {

  // ✅ 1. METHOD DÙNG CHUNG: Map ResultSet → DTO
  private LichKhamDTO mapResultSet(ResultSet rs) throws SQLException {
    LichKhamDTO lk = new LichKhamDTO();
    lk.setMaLichKham(rs.getString("MaLichKham"));
    lk.setMaGoi(rs.getString("MaGoi"));
    lk.setMaBacSi(rs.getString("MaBacSi"));
    lk.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
    lk.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
    lk.setTrangThai(rs.getString("TrangThai"));
    lk.setMaDinhDanhTam(rs.getString("MaDinhDanhTam"));
    return lk;
  }

  // ✅ 2. METHOD DÙNG CHUNG: Execute query trả về List
  private ArrayList<LichKhamDTO> executeQuery(String sql, Object... params) {
    ArrayList<LichKhamDTO> list = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
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

  private int queryCount(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    } catch (SQLException e) {
      System.err.println("❌ Lỗi truy vấn đếm: " + e.getMessage());
    }
    return 0;
  }

  private boolean hasOverlapSchedule(
    String sql,
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc,
    Object... extraParams
  ) {
    Object[] params = new Object[1 + extraParams.length + 6];
    int idx = 0;
    params[idx++] = maBacSi;
    for (Object extra : extraParams) {
      params[idx++] = extra;
    }
    params[idx++] = thoiGianBatDau;
    params[idx++] = thoiGianBatDau;
    params[idx++] = thoiGianKetThuc;
    params[idx++] = thoiGianKetThuc;
    params[idx++] = thoiGianBatDau;
    params[idx] = thoiGianKetThuc;
    return queryCount(sql, params) > 0;
  }

  // ===== CRUD OPERATIONS =====

  public ArrayList<LichKhamDTO> getAll() {
    return executeQuery("SELECT * FROM LichKham ORDER BY ThoiGianBatDau DESC");
  }

  public LichKhamDTO getById(String maLichKham) {
    ArrayList<LichKhamDTO> list = executeQuery(
      "SELECT * FROM LichKham WHERE MaLichKham = ?",
      maLichKham
    );
    return list.isEmpty() ? null : list.get(0);
  }

  public boolean insert(LichKhamDTO lk) {
    if (
      checkTrungLich(
        lk.getMaBacSi(),
        lk.getThoiGianBatDau(),
        lk.getThoiGianKetThuc()
      )
    ) {
      return false;
    }

    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(
      lk.getTrangThai()
    );

    String sql =
      "INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, " +
      "ThoiGianKetThuc, TrangThai, MaDinhDanhTam) VALUES (?, ?, ?, ?, ?, ?, ?)";

    return executeUpdate(
      sql,
      lk.getMaLichKham(),
      lk.getMaGoi(),
      lk.getMaBacSi(),
      lk.getThoiGianBatDau(),
      lk.getThoiGianKetThuc(),
      trangThaiChuan,
      lk.getMaDinhDanhTam()
    );
  }

  public boolean update(LichKhamDTO lk) {
    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(
      lk.getTrangThai()
    );
    String sql =
      "UPDATE LichKham SET MaGoi=?, MaBacSi=?, ThoiGianBatDau=?, " +
      "ThoiGianKetThuc=?, TrangThai=?, MaDinhDanhTam=? WHERE MaLichKham=?";

    return executeUpdate(
      sql,
      lk.getMaGoi(),
      lk.getMaBacSi(),
      lk.getThoiGianBatDau(),
      lk.getThoiGianKetThuc(),
      trangThaiChuan,
      lk.getMaDinhDanhTam(),
      lk.getMaLichKham()
    );
  }

  public boolean delete(String maLichKham) {
    return executeUpdate(
      "DELETE FROM LichKham WHERE MaLichKham = ?",
      maLichKham
    );
  }

  public boolean updateTrangThai(String maLichKham, String trangThai) {
    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(trangThai);
    return executeUpdate(
      "UPDATE LichKham SET TrangThai = ? WHERE MaLichKham = ?",
      trangThaiChuan,
      maLichKham
    );
  }

  // ===== SEARCH OPERATIONS =====
  public ArrayList<LichKhamDTO> getByMaBacSi(String maBacSi) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE MaBacSi = ? ORDER BY ThoiGianBatDau DESC",
      maBacSi
    );
  }

  public ArrayList<LichKhamDTO> getByMaGoi(String maGoi) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE MaGoi = ? ORDER BY ThoiGianBatDau DESC",
      maGoi
    );
  }

  public ArrayList<LichKhamDTO> getByMaDinhDanhTam(String maDinhDanhTam) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE MaDinhDanhTam = ? ORDER BY ThoiGianBatDau DESC",
      maDinhDanhTam
    );
  }

  public ArrayList<LichKhamDTO> getByTrangThai(String trangThai) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE TrangThai = ? ORDER BY ThoiGianBatDau DESC",
      trangThai
    );
  }

  public ArrayList<LichKhamDTO> getByNgay(String ngay) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE DATE(ThoiGianBatDau) = ? ORDER BY ThoiGianBatDau",
      ngay
    );
  }

  public ArrayList<LichKhamDTO> getByBacSiAndNgay(String maBacSi, String ngay) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE MaBacSi = ? AND DATE(ThoiGianBatDau) = ? " +
        "ORDER BY ThoiGianBatDau",
      maBacSi,
      ngay
    );
  }

  public ArrayList<LichKhamDTO> getByKhoangThoiGian(
    String tuNgay,
    String denNgay
  ) {
    return executeQuery(
      "SELECT * FROM LichKham WHERE DATE(ThoiGianBatDau) BETWEEN ? AND ? " +
        "ORDER BY ThoiGianBatDau",
      tuNgay,
      denNgay
    );
  }

  public ArrayList<LichKhamDTO> search(String keyword) {
    String pattern = "%" + keyword + "%";
    return executeQuery(
      "SELECT * FROM LichKham WHERE MaLichKham LIKE ? OR MaBacSi LIKE ? OR " +
        "MaGoi LIKE ? OR MaDinhDanhTam LIKE ? OR TrangThai LIKE ? " +
        "ORDER BY ThoiGianBatDau DESC",
      pattern,
      pattern,
      pattern,
      pattern,
      pattern
    );
  }

  // ===== VALIDATION OPERATIONS =====

  public boolean checkTrungLich(
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc
  ) {
    String sql =
      "SELECT COUNT(*) FROM LichKham WHERE MaBacSi = ? AND UPPER(COALESCE(TrangThai,'')) NOT IN ('DA_HUY','HUY') " +
      "AND ((ThoiGianBatDau <= ? AND ThoiGianKetThuc > ?) " +
      "OR (ThoiGianBatDau < ? AND ThoiGianKetThuc >= ?) " +
      "OR (ThoiGianBatDau >= ? AND ThoiGianKetThuc <= ?))";

    return hasOverlapSchedule(sql, maBacSi, thoiGianBatDau, thoiGianKetThuc);
  }

  public boolean checkTrungLichWhenUpdate(
    String maLichKham,
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc
  ) {
    String sql =
      "SELECT COUNT(*) FROM LichKham WHERE MaBacSi = ? AND MaLichKham != ? " +
      "AND UPPER(COALESCE(TrangThai,'')) NOT IN ('DA_HUY','HUY') " +
      "AND ((ThoiGianBatDau <= ? AND ThoiGianKetThuc > ?) " +
      "OR (ThoiGianBatDau < ? AND ThoiGianKetThuc >= ?) " +
      "OR (ThoiGianBatDau >= ? AND ThoiGianKetThuc <= ?))";

    return hasOverlapSchedule(
      sql,
      maBacSi,
      thoiGianBatDau,
      thoiGianKetThuc,
      maLichKham
    );
  }

  // ===== UTILITY OPERATIONS =====

  public int countByTrangThai(String trangThai) {
    return queryCount(
      "SELECT COUNT(*) FROM LichKham WHERE TrangThai = ?",
      trangThai
    );
  }

  // Đếm lịch khám theo bác sĩ
  public int countByBacSi(String maBacSi) {
    return queryCount(
      "SELECT COUNT(*) FROM LichKham WHERE MaBacSi = ?",
      maBacSi
    );
  }

  // Kiểm tra lịch khám tồn tại
  public boolean exists(String maLichKham) {
    return (
      queryCount(
        "SELECT COUNT(*) FROM LichKham WHERE MaLichKham = ?",
        maLichKham
      ) >
      0
    );
  }
}
