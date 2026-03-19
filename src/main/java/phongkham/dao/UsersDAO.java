package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.db.DBConnection;

public class UsersDAO {

  private UsersDTO mapUser(ResultSet rs) throws SQLException {
    UsersDTO u = new UsersDTO();
    u.setUserID(rs.getString("UserID"));
    u.setUsername(rs.getString("Username"));
    u.setPassword(rs.getString("Password"));
    u.setEmail(rs.getString("Email"));
    u.setRoleID((Integer) rs.getObject("RoleID"));
    u.setActive(rs.getBoolean("Active"));
    return u;
  }

  private ArrayList<UsersDTO> executeUserQuery(String sql, Object... params) {
    ArrayList<UsersDTO> ds = new ArrayList<>();
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ds.add(mapUser(rs));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  private boolean existsByField(String sql, String value) {
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, value);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private boolean updateActiveByUserId(Object userID, boolean active) {
    String sql = "UPDATE Users SET Active = ? WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setBoolean(1, active);
      ps.setObject(2, userID);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private String generateNextPrefixedId(
    String table,
    String column,
    String prefix,
    int fromIndex,
    int padLength,
    String defaultValue
  ) {
    String sql =
      "SELECT " +
      column +
      " FROM " +
      table +
      " WHERE " +
      column +
      " LIKE ? ORDER BY " +
      column +
      " DESC LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, prefix + "%");
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String last = rs.getString(column);
          int number = Integer.parseInt(last.substring(fromIndex));
          return String.format(prefix + "%0" + padLength + "d", number + 1);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return defaultValue;
  }

  public UsersDTO checkLogin(String username, String password) {
    ArrayList<UsersDTO> list = executeUserQuery(
      "SELECT * FROM Users WHERE Username = ? AND Password = ? AND Active = 1",
      username,
      password
    );
    return list.isEmpty() ? null : list.get(0);
  }

  public ArrayList<UsersDTO> getAll() {
    return executeUserQuery("SELECT * FROM Users");
  }

  public ArrayList<UsersDTO> getByRole(int roleId) {
    return executeUserQuery(
      "SELECT * FROM Users WHERE RoleID = ? ORDER BY UserID",
      roleId
    );
  }

  public boolean insertUser(UsersDTO u) {
    String sql =
      "INSERT INTO Users (UserID, Username, Password, Email, RoleID, Active) VALUES (?, ?, ?, ?, ?, ?)";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, u.getUserID());
      ps.setString(2, u.getUsername());
      ps.setString(3, u.getPassword());
      ps.setString(4, u.getEmail());
      if (u.getRoleID() == null) {
        ps.setNull(5, java.sql.Types.INTEGER);
      } else {
        ps.setInt(5, u.getRoleID());
      }
      ps.setBoolean(6, u.isActive());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean updateUser(UsersDTO u) {
    String sql =
      "UPDATE Users SET Username=?, Password=?, Email=?, RoleID=?, Active=? WHERE UserID=?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, u.getUsername());
      ps.setString(2, u.getPassword());
      ps.setString(3, u.getEmail());
      if (u.getRoleID() == null) {
        ps.setNull(4, java.sql.Types.INTEGER);
      } else {
        ps.setInt(4, u.getRoleID());
      }
      ps.setBoolean(5, u.isActive());
      ps.setString(6, u.getUserID());

      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deleteUser(int userID) {
    return updateActiveByUserId(userID, false);
  }

  public boolean disableUser(String userID) {
    return updateActiveByUserId(userID, false);
  }

  public boolean enableUser(String userID) {
    return updateActiveByUserId(userID, true);
  }

  public UsersDTO getUserByID(String userID) {
    ArrayList<UsersDTO> list = executeUserQuery(
      "SELECT * FROM Users WHERE UserID = ?",
      userID
    );
    return list.isEmpty() ? null : list.get(0);
  }

  public boolean resetPassword(String userID, String newPassword) {
    String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, newPassword);
      ps.setString(2, userID);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean existsUsername(String username) {
    return existsByField(
      "SELECT 1 FROM Users WHERE Username = ? LIMIT 1",
      username
    );
  }

  public boolean existsEmail(String email) {
    return existsByField("SELECT 1 FROM Users WHERE Email = ? LIMIT 1", email);
  }

  public boolean existsBacSiEmail(String email) {
    return existsByField("SELECT 1 FROM BacSi WHERE Email = ? LIMIT 1", email);
  }

  public String generateNextUserID() {
    return generateNextPrefixedId("Users", "UserID", "U", 1, 3, "U001");
  }

  public String generateNextBacSiID() {
    return generateNextPrefixedId("BacSi", "MaBacSi", "BS", 2, 2, "BS01");
  }

  public boolean createDoctorAccountWithProfile(UsersDTO user, BacSiDTO bacSi) {
    String insertUserSql =
      "INSERT INTO Users (UserID, Username, Password, Email, RoleID, Active) VALUES (?, ?, ?, ?, ?, ?)";
    String insertBacSiSql =
      "INSERT INTO BacSi (MaBacSi, HoTen, ChuyenKhoa, SoDienThoai, Email, MaKhoa) VALUES (?, ?, ?, ?, ?, ?)";

    Connection c = null;
    try {
      c = DBConnection.getConnection();
      c.setAutoCommit(false);

      try (
        PreparedStatement psUser = c.prepareStatement(insertUserSql);
        PreparedStatement psBacSi = c.prepareStatement(insertBacSiSql)
      ) {
        psUser.setString(1, user.getUserID());
        psUser.setString(2, user.getUsername());
        psUser.setString(3, user.getPassword());
        psUser.setString(4, user.getEmail());
        psUser.setInt(5, user.getRoleID());
        psUser.setBoolean(6, user.isActive());
        psUser.executeUpdate();

        psBacSi.setString(1, bacSi.getMaBacSi());
        psBacSi.setString(2, bacSi.getHoTen());
        psBacSi.setString(3, bacSi.getChuyenKhoa());
        psBacSi.setString(4, bacSi.getSoDienThoai());
        psBacSi.setString(5, bacSi.getEmail());
        psBacSi.setString(6, bacSi.getMaKhoa());
        psBacSi.executeUpdate();
      }

      c.commit();
      return true;
    } catch (SQLException e) {
      if (c != null) {
        try {
          c.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      e.printStackTrace();
      return false;
    } finally {
      if (c != null) {
        try {
          c.setAutoCommit(true);
          c.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public boolean createPharmacyAccount(UsersDTO user) {
    String sql =
      "INSERT INTO Users (UserID, Username, Password, Email, RoleID, Active) VALUES (?, ?, ?, ?, ?, ?)";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, user.getUserID());
      ps.setString(2, user.getUsername());
      ps.setString(3, user.getPassword());
      ps.setString(4, user.getEmail());
      ps.setInt(5, user.getRoleID());
      ps.setBoolean(6, user.isActive());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
