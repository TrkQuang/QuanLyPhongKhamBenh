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

  public UsersDTO checkLogin(String username, String password) {
    String sql =
      "SELECT * FROM Users WHERE Username = ? AND Password = ? AND Active = 1";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, username);
      ps.setString(2, password);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          UsersDTO u = new UsersDTO();
          u.setUserID(rs.getString("UserID"));
          u.setUsername(rs.getString("Username"));
          u.setPassword(rs.getString("Password"));
          u.setEmail(rs.getString("Email"));
          u.setRoleID((Integer) rs.getObject("RoleID"));
          u.setActive(rs.getBoolean("Active"));
          return u;
        }
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
      while (rs.next()) {
        UsersDTO u = new UsersDTO();
        u.setUserID(rs.getString("UserID"));
        u.setUsername(rs.getString("Username"));
        u.setPassword(rs.getString("Password"));
        u.setEmail(rs.getString("Email"));
        u.setRoleID((Integer) rs.getObject("RoleID"));
        u.setActive(rs.getBoolean("Active"));
        ds.add(u);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  public ArrayList<UsersDTO> getByRole(int roleId) {
    ArrayList<UsersDTO> ds = new ArrayList<>();
    String sql = "SELECT * FROM Users WHERE RoleID = ? ORDER BY UserID";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setInt(1, roleId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          UsersDTO u = new UsersDTO();
          u.setUserID(rs.getString("UserID"));
          u.setUsername(rs.getString("Username"));
          u.setPassword(rs.getString("Password"));
          u.setEmail(rs.getString("Email"));
          u.setRoleID((Integer) rs.getObject("RoleID"));
          u.setActive(rs.getBoolean("Active"));
          ds.add(u);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
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
    String sql = "UPDATE Users SET Active = 0 WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setInt(1, userID);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean disableUser(String userID) {
    String sql = "UPDATE Users SET Active = 0 WHERE UserID = ?";
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

  public boolean enableUser(String userID) {
    String sql = "UPDATE Users SET Active = 1 WHERE UserID = ?";
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
        if (rs.next()) {
          UsersDTO u = new UsersDTO();
          u.setUserID(rs.getString("UserID"));
          u.setUsername(rs.getString("Username"));
          u.setPassword(rs.getString("Password"));
          u.setEmail(rs.getString("Email"));
          u.setRoleID((Integer) rs.getObject("RoleID"));
          u.setActive(rs.getBoolean("Active"));
          return u;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
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
    String sql = "SELECT 1 FROM Users WHERE Username = ? LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean existsEmail(String email) {
    String sql = "SELECT 1 FROM Users WHERE Email = ? LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean existsBacSiEmail(String email) {
    String sql = "SELECT 1 FROM BacSi WHERE Email = ? LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public String generateNextUserID() {
    String sql =
      "SELECT UserID FROM Users WHERE UserID LIKE 'U%' ORDER BY UserID DESC LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      if (rs.next()) {
        String last = rs.getString("UserID");
        int number = Integer.parseInt(last.substring(1));
        return String.format("U%03d", number + 1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "U001";
  }

  public String generateNextBacSiID() {
    String sql =
      "SELECT MaBacSi FROM BacSi WHERE MaBacSi LIKE 'BS%' ORDER BY MaBacSi DESC LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      if (rs.next()) {
        String last = rs.getString("MaBacSi");
        int number = Integer.parseInt(last.substring(2));
        return String.format("BS%02d", number + 1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "BS01";
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
