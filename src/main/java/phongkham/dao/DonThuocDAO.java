package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.DTO.DonThuocDTO;
import phongkham.db.DBConnection;

public class DonThuocDAO {

  public ArrayList<DonThuocDTO> getAll() {
    ArrayList<DonThuocDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM DonThuoc";

    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql);
    ) {
      while (rs.next()) {
        DonThuocDTO dt = new DonThuocDTO();
        dt.setMaDonThuoc(rs.getString("MaDonThuoc"));
        dt.setMaHoSo(rs.getString("MaHoSo"));
        dt.setNgayKeDon(rs.getString("NgayKeDon"));
        dt.setGhiChu(rs.getString("GhiChu"));
        ds.add(dt);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ds;
  }

  public boolean insertDonThuoc(DonThuocDTO dt) {
    String sql = "INSERT INTO DonThuoc VALUES(?,?,?,?)";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, dt.getMaDonThuoc());
      ps.setString(2, dt.getMaHoSo());
      ps.setString(3, dt.getNgayKeDon());
      ps.setString(4, dt.getGhiChu());

      return ps.executeUpdate() > 0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deleteMaDonThuoc(String MaDonThuoc) {
    String sql = "DELETE FROM DonThuoc WHERE MaDonThuoc=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, MaDonThuoc);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean updateDonThuoc(DonThuocDTO dt) {
    String sql =
      "UPDATE donthuoc SET MaHoSo=?, NgayKeDon=?, GhiChu=? WHERE MaDonThuoc=?";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, dt.getMaHoSo());
      ps.setString(2, dt.getNgayKeDon());
      ps.setString(3, dt.getGhiChu());
      ps.setString(4, dt.getMaDonThuoc());

      return ps.executeUpdate() > 0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public DonThuocDTO searchTheoMa(String maDonThuoc) {
    String sql =
      "SELECT * FROM DonThuoc WHERE UPPER(TRIM(MaDonThuoc)) = UPPER(TRIM(?))";
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, maDonThuoc);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        return new DonThuocDTO(
          rs.getString("MaDonThuoc"),
          rs.getString("MaHoSo"),
          rs.getString("NgayKeDon"),
          rs.getString("GhiChu")
        );
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean exists(String maDT) {
    String sql =
      "SELECT 1 FROM DonThuoc WHERE UPPER(TRIM(MaDonThuoc)) = UPPER(TRIM(?))";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, maDT);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }
}
