package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import phongkham.DTO.UsersRolesDTO;
import phongkham.db.DBConnection;

public class UsersRolesDAO {

  public boolean Insert(UsersRolesDTO ur) {
    String sql = "UPDATE Users SET RoleID = ? WHERE UserID = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, ur.getRole_ID());
      ps.setString(2, ur.getUser_ID());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean Delete(String user_id, String role_id) {
    String sql =
      "UPDATE Users SET RoleID = NULL WHERE UserID = ? AND CAST(RoleID AS CHAR) = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, user_id);
      ps.setString(2, role_id);

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public ArrayList<UsersRolesDTO> getRolesByUser(String user_id) {
    ArrayList<UsersRolesDTO> list = new ArrayList<>();
    String sql =
      "SELECT UserID, RoleID FROM Users WHERE UserID = ? AND RoleID IS NOT NULL";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, user_id);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        UsersRolesDTO ur = new UsersRolesDTO(
          rs.getString("UserID"),
          rs.getString("RoleID")
        );

        list.add(ur);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public ArrayList<String> getRoleIDsByUser(String user_id) {
    ArrayList<String> list = new ArrayList<>();
    String sql =
      "SELECT RoleID FROM Users WHERE UserID = ? AND RoleID IS NOT NULL";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql)
    ) {
      ps.setString(1, user_id);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        list.add(rs.getString("RoleID"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
  }
}
