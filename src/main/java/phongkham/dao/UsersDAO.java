package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.DTO.UsersDTO;
import phongkham.db.DBConnection;

public class UsersDAO {

  private UsersDTO mapResultSet(ResultSet rs) throws SQLException {
    UsersDTO u = new UsersDTO();
    u.setUserID(rs.getString("UserID"));
    u.setUsername(rs.getString("Username"));
    u.setPassword(rs.getString("Password"));
    u.setEmail(rs.getString("Email"));
    return u;
  }

  public UsersDTO checkLogin(String username, String password) {
    String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, username);
      ps.setString(2, password);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<UsersDTO> getAll() {
    ArrayList<UsersDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM Users";
    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      while (rs.next()) ds.add(mapResultSet(rs));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  public boolean insertUser(UsersDTO u) {
    String sql =
      "INSERT INTO Users (UserID, Username, Password, Email) VALUES (?, ?, ?, ?)";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, u.getUserID());
      ps.setString(2, u.getUsername());
      ps.setString(3, u.getPassword());
      ps.setString(4, u.getEmail());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean updateUser(UsersDTO u) {
    String sql =
      "UPDATE Users SET Username=?, Password=?, Email=? WHERE UserID=?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, u.getUsername());
      ps.setString(2, u.getPassword());
      ps.setString(3, u.getEmail());
      ps.setString(4, u.getUserID());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deleteUser(String userID) {
    String sql = "DELETE FROM Users WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, userID);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public UsersDTO getUserByID(String userID) {
    String sql = "SELECT * FROM Users WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, userID);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return mapResultSet(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
