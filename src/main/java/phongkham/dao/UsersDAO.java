package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.db.DBConnection;

public class UsersDAO {

  private Boolean archiveColumnsSupportedCache = null;

  private boolean hasColumn(ResultSet rs, String columnName) {
    try {
      rs.findColumn(columnName);
      return true;
    } catch (SQLException ex) {
      return false;
    }
  }

  private boolean isArchiveColumnsSupported(Connection c) {
    if (archiveColumnsSupportedCache != null) {
      return archiveColumnsSupportedCache.booleanValue();
    }
    String sql =
      "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
      "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'Users' AND COLUMN_NAME IN ('IsArchived', 'DeletedAt', 'DeletedReason')";
    try (
      PreparedStatement ps = c.prepareStatement(sql);
      ResultSet rs = ps.executeQuery()
    ) {
      int count = rs.next() ? rs.getInt(1) : 0;
      archiveColumnsSupportedCache = (count >= 3);
      return archiveColumnsSupportedCache.booleanValue();
    } catch (SQLException ex) {
      archiveColumnsSupportedCache = Boolean.FALSE;
      return false;
    }
  }

  public boolean supportsArchiveDeletion() {
    try (Connection c = DBConnection.getConnection()) {
      return isArchiveColumnsSupported(c);
    } catch (SQLException ex) {
      return false;
    }
  }

  private UsersDTO mapUser(ResultSet rs) throws SQLException {
    UsersDTO u = new UsersDTO();
    u.setUserID(rs.getString("UserID"));
    u.setUsername(rs.getString("Username"));
    u.setPassword(rs.getString("Password"));
    u.setEmail(rs.getString("Email"));
    u.setRoleID((Integer) rs.getObject("RoleID"));
    u.setActive(rs.getBoolean("Active"));
    u.setArchived(hasColumn(rs, "IsArchived") && rs.getBoolean("IsArchived"));
    u.setDeletedAt(
      hasColumn(rs, "DeletedAt")
        ? rs.getObject("DeletedAt", LocalDateTime.class)
        : null
    );
    u.setDeletedReason(
      hasColumn(rs, "DeletedReason") ? rs.getString("DeletedReason") : ""
    );
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

  private int countRows(String sql, Object... params) {
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  private int countRowsSafe(String sql, Object... params) {
    try {
      return countRows(sql, params);
    } catch (Exception ex) {
      return 0;
    }
  }

  public UsersDTO checkLogin(String username, String password) {
    ArrayList<UsersDTO> list = executeUserQuery(
      "SELECT * FROM Users WHERE Username = ? AND Password = ? AND Active = 1",
      username,
      password
    );
    if (list.isEmpty()) {
      return null;
    }
    UsersDTO user = list.get(0);
    return user.isArchived() ? null : user;
  }

  public ArrayList<UsersDTO> getAll() {
    ArrayList<UsersDTO> raw = executeUserQuery(
      "SELECT * FROM Users ORDER BY UserID"
    );
    ArrayList<UsersDTO> filtered = new ArrayList<>();
    for (UsersDTO user : raw) {
      if (!user.isArchived()) {
        filtered.add(user);
      }
    }
    return filtered;
  }

  public ArrayList<UsersDTO> getByRole(int roleId) {
    ArrayList<UsersDTO> raw = executeUserQuery(
      "SELECT * FROM Users WHERE RoleID = ? ORDER BY UserID",
      roleId
    );
    ArrayList<UsersDTO> filtered = new ArrayList<>();
    for (UsersDTO user : raw) {
      if (!user.isArchived()) {
        filtered.add(user);
      }
    }
    return filtered;
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
    if (list.isEmpty()) {
      return null;
    }
    UsersDTO user = list.get(0);
    return user.isArchived() ? null : user;
  }

  public String findDoctorIdByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }
    String sql = "SELECT MaBacSi FROM BacSi WHERE Email = ? LIMIT 1";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, email.trim());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString(1);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public int countBacSiByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return 0;
    }
    return countRows(
      "SELECT COUNT(*) FROM BacSi WHERE Email = ?",
      email.trim()
    );
  }

  public int countLichKhamByBacSi(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      return 0;
    }
    return countRows(
      "SELECT COUNT(*) FROM LichKham WHERE MaBacSi = ?",
      maBacSi.trim()
    );
  }

  public int countLichLamViecByBacSi(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      return 0;
    }
    return countRows(
      "SELECT COUNT(*) FROM LichLamViec WHERE MaBacSi = ?",
      maBacSi.trim()
    );
  }

  public int countHoSoByBacSi(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      return 0;
    }
    return countRows(
      "SELECT COUNT(*) FROM HoSoBenhAn WHERE MaBacSi = ?",
      maBacSi.trim()
    );
  }

  public int countLoaiBienDongByNguoiThucHien(String username) {
    if (username == null || username.trim().isEmpty()) {
      return 0;
    }
    return countRowsSafe(
      "SELECT COUNT(*) FROM LoThuocBienDong WHERE NguoiThucHien = ?",
      username.trim()
    );
  }

  public int countTieuHuyByNguoiThucHien(String username) {
    if (username == null || username.trim().isEmpty()) {
      return 0;
    }
    return countRowsSafe(
      "SELECT COUNT(*) FROM TieuHuyLoThuoc WHERE NguoiThucHien = ?",
      username.trim()
    );
  }

  public boolean archiveUserPermanently(String userID, String reason) {
    try (Connection c = DBConnection.getConnection()) {
      if (!isArchiveColumnsSupported(c)) {
        return false;
      }
    } catch (SQLException ex) {
      return false;
    }

    String sql =
      "UPDATE Users SET Active = 0, IsArchived = 1, DeletedAt = NOW(), DeletedReason = ? " +
      "WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, reason == null ? "" : reason.trim());
      ps.setString(2, userID);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean hardDeleteUser(String userID) {
    String sql = "DELETE FROM Users WHERE UserID = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, userID);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
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

  public boolean existsBacSiId(String maBacSi) {
    return existsByField(
      "SELECT 1 FROM BacSi WHERE MaBacSi = ? LIMIT 1",
      maBacSi
    );
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
