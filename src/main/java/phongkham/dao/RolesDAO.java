package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.DTO.RolesDTO;
import phongkham.db.DBConnection;

public class RolesDAO {

  public ArrayList<RolesDTO> getAllRoles() {
    ArrayList<RolesDTO> ds = new ArrayList<>();

    String sql = "SELECT * FROM Roles";
    try (
      Connection c = DBConnection.getConnection();
      Statement stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      while (rs.next()) {
        RolesDTO rl = new RolesDTO();
        rl.setSTT(rs.getString("STT"));
        rl.setTenVaiTro(rs.getString("TenVaiTro"));
        rl.setMoTa(rs.getString("Mota"));
        ds.add(rl);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  public boolean insertRoles(RolesDTO rl) {
    String sql = "INSERT INTO Roles VALUES (?,?,?)";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, rl.getSTT());
      ps.setString(2, rl.getTenVaiTro());
      ps.setString(3, rl.getMoTa());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean updateRoles(RolesDTO rl) {
    String sql = "UPDATE Roles SET TenVaiTro=?, MoTa=? WHERE STT=?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, rl.getTenVaiTro());
      ps.setString(2, rl.getMoTa());
      ps.setString(3, rl.getSTT());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deleteRoles(RolesDTO rl) {
    String sql = "DELETE FROM Roles Where STT =?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, rl.getSTT());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public RolesDTO getById(String STT) {
    String sql = "SELECT * FROM Roles WHERE STT=?";
    RolesDTO rltemp = null;
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
    ) {
      ps.setString(1, STT);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        rltemp = new RolesDTO();
        rltemp.setSTT(rs.getString("STT"));
        rltemp.setTenVaiTro(rs.getString("TenVaiTro"));
        rltemp.setMoTa(rs.getString("MoTa"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rltemp;
  }
}
