package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import phongkham.DTO.KhungGioLamViecDTO;
import phongkham.db.DBConnection;

public class KhungGioLamViecDAO {

  private KhungGioLamViecDTO mapResultSet(ResultSet rs) throws SQLException {
    KhungGioLamViecDTO item = new KhungGioLamViecDTO();
    item.setMaKhungGio(rs.getInt("MaKhungGio"));
    item.setKhungGio(rs.getString("KhungGio"));
    item.setMoTa(rs.getString("MoTa"));
    item.setActive(rs.getInt("Active"));
    return item;
  }

  private ArrayList<KhungGioLamViecDTO> executeQuery(
    String sql,
    Object... params
  ) {
    ArrayList<KhungGioLamViecDTO> result = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(mapResultSet(rs));
        }
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return result;
  }

  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      return ps.executeUpdate() > 0;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public ArrayList<KhungGioLamViecDTO> getAll() {
    return executeQuery(
      "SELECT MaKhungGio, KhungGio, MoTa, Active FROM KhungGioLamViec ORDER BY KhungGio"
    );
  }

  public ArrayList<KhungGioLamViecDTO> getAllActive() {
    return executeQuery(
      "SELECT MaKhungGio, KhungGio, MoTa, Active FROM KhungGioLamViec WHERE Active = 1 ORDER BY KhungGio"
    );
  }

  public boolean insert(String khungGio, String moTa) {
    return executeUpdate(
      "INSERT INTO KhungGioLamViec (KhungGio, MoTa, Active) VALUES (?, ?, 1)",
      khungGio,
      moTa
    );
  }

  public boolean updateActive(int maKhungGio, int active) {
    return executeUpdate(
      "UPDATE KhungGioLamViec SET Active = ? WHERE MaKhungGio = ?",
      active,
      maKhungGio
    );
  }

  public boolean existsByRange(String khungGio) {
    ArrayList<KhungGioLamViecDTO> rows = executeQuery(
      "SELECT MaKhungGio, KhungGio, MoTa, Active FROM KhungGioLamViec WHERE KhungGio = ?",
      khungGio
    );
    return !rows.isEmpty();
  }
}
