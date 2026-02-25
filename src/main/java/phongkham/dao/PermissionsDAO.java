package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import phongkham.DTO.PermissionsDTO;
import phongkham.db.DBConnection;

public class PermissionsDAO {

  public ArrayList<PermissionsDTO> getAllPermisson() {
    ArrayList<PermissionsDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM Permisson";
    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql);
    ) {
      while (rs.next()) {
        PermissionsDTO perm = new PermissionsDTO();
        perm.setMaPermission(rs.getInt("MaPermission"));
        perm.setTenPermission(rs.getString("TenPermission"));
        perm.setMoTa(rs.getString("MoTa"));
        perm.setActive(rs.getBoolean("Active"));
        ds.add(perm);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  public ArrayList<String> getPermissionByUser(String userID) {
    ArrayList<String> list = new ArrayList<>();

    String sql =
      "SELECT p.TenPermission " +
      "FROM Users u " +
      "JOIN UsersRoles ur ON u.UserID = ur.user_id " +
      "JOIN Roles r ON ur.role_id = r.RoleID " +
      "JOIN RolePermissions rp ON r.RoleID = rp.maRole " +
      "JOIN Permissions p ON rp.maPermission = p.MaPermission " +
      "WHERE u.UserID = ? AND p.Active = 1";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, userID);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        list.add(rs.getString("TenPermission"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }
}
