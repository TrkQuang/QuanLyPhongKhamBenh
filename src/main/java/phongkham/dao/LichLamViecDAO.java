package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import phongkham.DTO.LichLamViecDTO;
import phongkham.db.DBConnection;

public class LichLamViecDAO {

  private LichLamViecDTO mapResultSet(ResultSet rs) throws SQLException {
    LichLamViecDTO l = new LichLamViecDTO();
    l.setMaLichLam(rs.getString("MaLichLam"));
    l.setMaBacSi(rs.getString("MaBacSi"));
    l.setNgayLam(rs.getString("NgayLam"));
    l.setCaLam(rs.getString("CaLam"));
    l.setTrangThai(rs.getString("TrangThai"));
    return l;
  }

  private ArrayList<LichLamViecDTO> executeQuery(String sql, Object... params) {
    ArrayList<LichLamViecDTO> ds = new ArrayList<>();
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ds.add(mapResultSet(rs));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public ArrayList<LichLamViecDTO> getAll() {
    return executeQuery("SELECT * FROM LichLamViec");
  }

  public ArrayList<LichLamViecDTO> getByBacSi(String maBacSi) {
    return executeQuery("SELECT * FROM LichLamViec WHERE MaBacSi = ?", maBacSi);
  }

  public boolean insert(LichLamViecDTO l) {
    return executeUpdate(
      "INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam, TrangThai) VALUES (?, ?, ?, ?, ?)",
      l.getMaLichLam(),
      l.getMaBacSi(),
      l.getNgayLam(),
      l.getCaLam(),
      l.getTrangThai()
    );
  }

  public boolean update(LichLamViecDTO l) {
    return executeUpdate(
      "UPDATE LichLamViec SET MaBacSi=?, NgayLam=?, CaLam=?, TrangThai=? WHERE MaLichLam=?",
      l.getMaBacSi(),
      l.getNgayLam(),
      l.getCaLam(),
      l.getTrangThai(),
      l.getMaLichLam()
    );
  }

  public boolean delete(String maLichLam) {
    return executeUpdate(
      "DELETE FROM LichLamViec WHERE MaLichLam = ?",
      maLichLam
    );
  }

  public boolean updateTrangThai(String maLichLam, String trangThai) {
    return executeUpdate(
      "UPDATE LichLamViec SET TrangThai = ? WHERE MaLichLam = ?",
      trangThai,
      maLichLam
    );
  }
}
