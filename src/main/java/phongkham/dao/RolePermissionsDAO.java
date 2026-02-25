package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.RolePermissionsDTO;
import phongkham.db.DBConnection;

public class RolePermissionsDAO {

  // Thêm mới
  public boolean insert(RolePermissionsDTO rolePermissions) {
    String sql =
      "INSERT INTO RolePermissions (MaRole, MaPermission, MoTa, Active) VALUES (?, ?, ?, ?)";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, rolePermissions.getMaRole());
      pstmt.setInt(2, rolePermissions.getMaPermission());
      pstmt.setString(3, rolePermissions.getMoTa());
      pstmt.setBoolean(4, rolePermissions.isActive());

      int rowsInserted = pstmt.executeUpdate();
      return rowsInserted > 0;
    } catch (SQLException e) {
      System.err.println(
        "✗ Error inserting RolePermissions: " + e.getMessage()
      );
      return false;
    }
  }

  // Cập nhật
  public boolean update(RolePermissionsDTO rolePermissions) {
    String sql =
      "UPDATE RolePermissions SET MaRole = ?, MaPermission = ?, MoTa = ?, Active = ? WHERE MaRolePermissions = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, rolePermissions.getMaRole());
      pstmt.setInt(2, rolePermissions.getMaPermission());
      pstmt.setString(3, rolePermissions.getMoTa());
      pstmt.setBoolean(4, rolePermissions.isActive());
      pstmt.setInt(5, rolePermissions.getMaRolePermissions());

      int rowsUpdated = pstmt.executeUpdate();
      return rowsUpdated > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error updating RolePermissions: " + e.getMessage());
      return false;
    }
  }

  // Xóa
  public boolean delete(int maRolePermissions) {
    String sql = "DELETE FROM RolePermissions WHERE MaRolePermissions = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, maRolePermissions);
      int rowsDeleted = pstmt.executeUpdate();
      return rowsDeleted > 0;
    } catch (SQLException e) {
      System.err.println("✗ Error deleting RolePermissions: " + e.getMessage());
      return false;
    }
  }

  // Lấy theo ID
  public RolePermissionsDTO getById(int maRolePermissions) {
    String sql =
      "SELECT rp.*, r.TenRole, p.TenPermission " +
      "FROM RolePermissions rp " +
      "LEFT JOIN Roles r ON rp.MaRole = r.MaRole " +
      "LEFT JOIN Permissions p ON rp.MaPermission = p.MaPermission " +
      "WHERE rp.MaRolePermissions = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, maRolePermissions);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToDTO(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println(
        "✗ Error getting RolePermissions by ID: " + e.getMessage()
      );
    }
    return null;
  }

  // Lấy tất cả
  public List<RolePermissionsDTO> getAll() {
    List<RolePermissionsDTO> list = new ArrayList<>();
    String sql =
      "SELECT rp.*, r.TenRole, p.TenPermission " +
      "FROM RolePermissions rp " +
      "LEFT JOIN Roles r ON rp.MaRole = r.MaRole " +
      "LEFT JOIN Permissions p ON rp.MaPermission = p.MaPermission";

    try (
      Connection conn = DBConnection.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)
    ) {
      while (rs.next()) {
        list.add(mapResultSetToDTO(rs));
      }
    } catch (SQLException e) {
      System.err.println(
        "✗ Error getting all RolePermissions: " + e.getMessage()
      );
    }
    return list;
  }

  // Lấy theo Role
  public List<RolePermissionsDTO> getByRole(int maRole) {
    List<RolePermissionsDTO> list = new ArrayList<>();
    String sql =
      "SELECT rp.*, r.TenRole, p.TenPermission " +
      "FROM RolePermissions rp " +
      "LEFT JOIN Roles r ON rp.MaRole = r.MaRole " +
      "LEFT JOIN Permissions p ON rp.MaPermission = p.MaPermission " +
      "WHERE rp.MaRole = ? AND rp.Active = 1";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, maRole);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToDTO(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println(
        "✗ Error getting RolePermissions by Role: " + e.getMessage()
      );
    }
    return list;
  }

  // Kiểm tra permission của role
  public boolean hasPermission(int maRole, int maPermission) {
    String sql =
      "SELECT COUNT(*) FROM RolePermissions WHERE MaRole = ? AND MaPermission = ? AND Active = 1";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, maRole);
      pstmt.setInt(2, maPermission);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    } catch (SQLException e) {
      System.err.println("✗ Error checking permission: " + e.getMessage());
    }
    return false;
  }

  // Thay đổi permissions cho một role
  public boolean replacePermissionsForRole(
    int maRole,
    List<Integer> permissionIds
  ) {
    Connection conn = null;
    try {
      conn = DBConnection.getConnection();
      conn.setAutoCommit(false); // Transaction

      // Xóa tất cả permissions cũ
      String deleteSql = "DELETE FROM role_permissions WHERE ma_role = ?";
      PreparedStatement psDelete = conn.prepareStatement(deleteSql);
      psDelete.setInt(1, maRole);
      psDelete.executeUpdate();

      // Insert permissions mới
      String insertSql =
        "INSERT INTO role_permissions (ma_role, ma_permission) VALUES (?, ?)";
      PreparedStatement psInsert = conn.prepareStatement(insertSql);
      for (Integer permId : permissionIds) {
        psInsert.setInt(1, maRole);
        psInsert.setInt(2, permId);
        psInsert.addBatch();
      }
      psInsert.executeBatch();

      conn.commit(); // Commit transaction
      return true;
    } catch (SQLException e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {}
      }
      e.printStackTrace();
      return false;
    }
  }

  // Map ResultSet thành DTO
  private RolePermissionsDTO mapResultSetToDTO(ResultSet rs)
    throws SQLException {
    RolePermissionsDTO dto = new RolePermissionsDTO();
    dto.setMaRolePermissions(rs.getInt("MaRolePermissions"));
    dto.setMaRole(rs.getInt("MaRole"));
    dto.setMaPermission(rs.getInt("MaPermission"));
    dto.setTenRole(rs.getString("TenRole"));
    dto.setTenPermission(rs.getString("TenPermission"));
    dto.setMoTa(rs.getString("MoTa"));
    dto.setActive(rs.getBoolean("Active"));
    return dto;
  }
}
