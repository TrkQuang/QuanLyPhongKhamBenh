package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.RolePermissionsDTO;
import phongkham.db.DBConnection;

public class RolePermissionsDAO {

  private static final String BASE_SELECT =
    "SELECT rp.*, r.TenVaiTro, p.TenPermission " +
    "FROM RolePermissions rp " +
    "LEFT JOIN Roles r ON rp.MaRole = r.STT " +
    "LEFT JOIN Permissions p ON rp.MaPermission = p.MaPermission";

  private boolean executeUpdate(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("RolePermissions update error: " + e.getMessage());
      return false;
    }
  }

  private int executeCount(String sql, Object... params) {
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      System.err.println("RolePermissions count error: " + e.getMessage());
    }
    return 0;
  }

  private List<RolePermissionsDTO> executeQueryList(
    String sql,
    Object... params
  ) {
    List<RolePermissionsDTO> list = new ArrayList<>();
    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToDTO(rs));
        }
      }
    } catch (SQLException e) {
      System.err.println("RolePermissions query error: " + e.getMessage());
    }
    return list;
  }

  // Thêm mới
  public boolean insert(RolePermissionsDTO rolePermissions) {
    return executeUpdate(
      "INSERT INTO RolePermissions (MaRole, MaPermission, Active) VALUES (?, ?, ?)",
      rolePermissions.getMaRole(),
      rolePermissions.getMaPermission(),
      rolePermissions.isActive()
    );
  }

  // Cập nhật
  public boolean update(RolePermissionsDTO rolePermissions) {
    return executeUpdate(
      "UPDATE RolePermissions SET MaRole = ?, MaPermission = ?, Active = ? WHERE MaRolePermissions = ?",
      rolePermissions.getMaRole(),
      rolePermissions.getMaPermission(),
      rolePermissions.isActive(),
      rolePermissions.getMaRolePermissions()
    );
  }

  // Xóa
  public boolean delete(int maRolePermissions) {
    return executeUpdate(
      "DELETE FROM RolePermissions WHERE MaRolePermissions = ?",
      maRolePermissions
    );
  }

  // Lấy theo ID
  public RolePermissionsDTO getById(int maRolePermissions) {
    List<RolePermissionsDTO> list = executeQueryList(
      BASE_SELECT + " WHERE rp.MaRolePermissions = ?",
      maRolePermissions
    );
    return list.isEmpty() ? null : list.get(0);
  }

  // Lấy tất cả
  public List<RolePermissionsDTO> getAll() {
    return executeQueryList(BASE_SELECT);
  }

  // Lấy theo Role
  public List<RolePermissionsDTO> getByRole(int maRole) {
    return executeQueryList(
      BASE_SELECT + " WHERE rp.MaRole = ? AND rp.Active = 1",
      maRole
    );
  }

  // Kiểm tra permission của role
  public boolean hasPermission(int maRole, int maPermission) {
    return (
      executeCount(
        "SELECT COUNT(*) FROM RolePermissions WHERE MaRole = ? AND MaPermission = ? AND Active = 1",
        maRole,
        maPermission
      ) >
      0
    );
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
      String deleteSql = "DELETE FROM RolePermissions WHERE MaRole = ?";
      String insertSql =
        "INSERT INTO RolePermissions (MaRole, MaPermission, Active) VALUES (?, ?, 1)";
      try (
        PreparedStatement psDelete = conn.prepareStatement(deleteSql);
        PreparedStatement psInsert = conn.prepareStatement(insertSql)
      ) {
        psDelete.setInt(1, maRole);
        psDelete.executeUpdate();

        for (Integer permId : permissionIds) {
          psInsert.setInt(1, maRole);
          psInsert.setInt(2, permId);
          psInsert.addBatch();
        }
        psInsert.executeBatch();
      }

      conn.commit(); // Commit transaction
      return true;
    } catch (SQLException e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          System.err.println("Rollback failed: " + ex.getMessage());
        }
      }
      e.printStackTrace();
      return false;
    } finally {
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException ex) {
          System.err.println("Close connection failed: " + ex.getMessage());
        }
      }
    }
  }

  // Map ResultSet thành DTO
  private RolePermissionsDTO mapResultSetToDTO(ResultSet rs)
    throws SQLException {
    RolePermissionsDTO dto = new RolePermissionsDTO();
    dto.setMaRolePermissions(rs.getInt("MaRolePermissions"));
    dto.setMaRole(rs.getInt("MaRole"));
    dto.setMaPermission(rs.getInt("MaPermission"));
    dto.setTenRole(rs.getString("TenVaiTro"));
    dto.setTenPermission(rs.getString("TenPermission"));
    dto.setActive(rs.getBoolean("Active"));
    return dto;
  }

  // ========== METHODS CHO QuanLyPhanQuyenPanel ==========

  /**
   * Xóa tất cả Permission của một Role
   * @param roleId - Mã Role (int)
   * @return true nếu thành công
   */
  public boolean deleteByRoleId(int roleId) {
    String sql = "DELETE FROM RolePermissions WHERE MaRole = ?";

    try (
      Connection conn = DBConnection.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
      pstmt.setInt(1, roleId);
      int rowsDeleted = pstmt.executeUpdate();
      System.out.println(
        "Xoa " + rowsDeleted + " RolePermission cua Role " + roleId
      );
      return true; // Trả về true ngay cả khi không có gì để xóa
    } catch (SQLException e) {
      System.err.println("Loi deleteByRoleId: " + e.getMessage());
      return false;
    }
  }
}
