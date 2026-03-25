package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import phongkham.DTO.RolesDTO;
import phongkham.db.DBConnection;

public class PhanQuyenChiTietDAO {

  /**
   * Lay danh sach vai tro de hien thi ma tran phan quyen.
   */
  public ArrayList<RolesDTO> layTatCaVaiTro() {
    ArrayList<RolesDTO> ds = new ArrayList<>();
    String sql = "SELECT STT, TenVaiTro, MoTa FROM Roles ORDER BY STT";

    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      while (rs.next()) {
        RolesDTO role = new RolesDTO();
        role.setSTT(String.valueOf(rs.getInt("STT")));
        role.setTenVaiTro(rs.getString("TenVaiTro"));
        role.setMoTa(rs.getString("MoTa"));
        ds.add(role);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ds;
  }

  /**
   * Lay toan bo ma quyen chi tiet dang co trong he thong moi.
   */
  public ArrayList<String> layTatCaMaQuyenChiTiet() {
    ArrayList<String> ds = new ArrayList<>();
    String sql =
      "SELECT cp.MaQuyen " +
      "FROM ChiTiet_Permission cp " +
      "JOIN Permission p ON p.id = cp.PermissionID " +
      "ORDER BY p.TenPermission, cp.HanhDong";

    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      while (rs.next()) {
        ds.add(rs.getString("MaQuyen"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ds;
  }

  /**
   * Lay map mo ta theo ma quyen de hien thi tooltip/cot mo ta.
   */
  public Map<String, String> layMoTaTheoMaQuyen() {
    Map<String, String> map = new HashMap<>();
    String sql =
      "SELECT cp.MaQuyen, p.TenPermission, cp.HanhDong " +
      "FROM ChiTiet_Permission cp " +
      "JOIN Permission p ON p.id = cp.PermissionID";

    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      while (rs.next()) {
        String maQuyen = rs.getString("MaQuyen");
        String moTa =
          "Module: " +
          rs.getString("TenPermission") +
          " | Hanh dong: " +
          rs.getString("HanhDong");
        map.put(maQuyen, moTa);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return map;
  }

  /**
   * Lay tap ma quyen da duoc gan cho tung role.
   */
  public Map<Integer, Set<String>> layMapQuyenTheoRole() {
    Map<Integer, Set<String>> ketQua = new HashMap<>();
    String sql =
      "SELECT rp.RoleID, cp.MaQuyen " +
      "FROM Role_Permission rp " +
      "JOIN ChiTiet_Permission cp ON cp.id = rp.ChiTietPermissionID";

    try (
      Connection c = DBConnection.getConnection();
      Statement stm = c.createStatement();
      ResultSet rs = stm.executeQuery(sql)
    ) {
      while (rs.next()) {
        int roleId = rs.getInt("RoleID");
        String maQuyen = rs.getString("MaQuyen");

        ketQua.computeIfAbsent(roleId, k -> new HashSet<>()).add(maQuyen);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ketQua;
  }

  /**
   * Gan/bo 1 quyen cho role ngay tai su kien tick checkbox.
   */
  public boolean capNhatMotQuyenChoRole(
    int roleId,
    String maQuyen,
    boolean duocCap
  ) {
    if (duocCap) {
      String sql =
        "INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID) " +
        "SELECT ?, cp.id FROM ChiTiet_Permission cp WHERE cp.MaQuyen = ?";
      try (
        Connection c = DBConnection.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)
      ) {
        ps.setInt(1, roleId);
        ps.setString(2, maQuyen);
        return ps.executeUpdate() > 0;
      } catch (SQLException e) {
        e.printStackTrace();
        return false;
      }
    }

    String sql =
      "DELETE rp FROM Role_Permission rp " +
      "JOIN ChiTiet_Permission cp ON cp.id = rp.ChiTietPermissionID " +
      "WHERE rp.RoleID = ? AND cp.MaQuyen = ?";
    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setInt(1, roleId);
      ps.setString(2, maQuyen);
      ps.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Ghi de toan bo quyen cua role (dung khi bam Luu).
   */
  public boolean capNhatToanBoQuyenRole(
    int roleId,
    List<String> danhSachMaQuyen
  ) {
    Connection conn = null;
    try {
      conn = DBConnection.getConnection();
      conn.setAutoCommit(false);

      try (
        PreparedStatement psDelete = conn.prepareStatement(
          "DELETE FROM Role_Permission WHERE RoleID = ?"
        );
        PreparedStatement psInsert = conn.prepareStatement(
          "INSERT INTO Role_Permission (RoleID, ChiTietPermissionID) " +
            "SELECT ?, cp.id FROM ChiTiet_Permission cp WHERE cp.MaQuyen = ?"
        )
      ) {
        psDelete.setInt(1, roleId);
        psDelete.executeUpdate();

        if (danhSachMaQuyen != null) {
          for (String maQuyen : danhSachMaQuyen) {
            psInsert.setInt(1, roleId);
            psInsert.setString(2, maQuyen);
            psInsert.addBatch();
          }
          psInsert.executeBatch();
        }
      }

      conn.commit();
      return true;
    } catch (SQLException e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
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
          ex.printStackTrace();
        }
      }
    }
  }

  /**
   * Kiem tra da co bo bang moi chua.
   */
  public boolean daCoBangPhanQuyenChiTiet() {
    String sql =
      "SELECT COUNT(*) " +
      "FROM information_schema.tables " +
      "WHERE table_schema = DATABASE() " +
      "AND table_name IN ('Permission', 'ChiTiet_Permission', 'Role_Permission')";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql);
      ResultSet rs = ps.executeQuery()
    ) {
      if (rs.next()) {
        return rs.getInt(1) == 3;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Dam bao module + ma quyen chi tiet da ton tai.
   */
  public boolean damBaoQuyenChiTiet(
    String tenPermission,
    String hanhDong,
    String maQuyen,
    String moTaModule
  ) {
    Connection conn = null;
    try {
      conn = DBConnection.getConnection();
      conn.setAutoCommit(false);

      try (
        PreparedStatement psInsertModule = conn.prepareStatement(
          "INSERT IGNORE INTO Permission (TenPermission, MoTa) VALUES (?, ?)"
        );
        PreparedStatement psInsertDetail = conn.prepareStatement(
          "INSERT IGNORE INTO ChiTiet_Permission (PermissionID, HanhDong, MaQuyen) " +
            "SELECT p.id, ?, ? FROM Permission p WHERE p.TenPermission = ?"
        )
      ) {
        psInsertModule.setString(1, tenPermission);
        psInsertModule.setString(2, moTaModule);
        psInsertModule.executeUpdate();

        psInsertDetail.setString(1, hanhDong);
        psInsertDetail.setString(2, maQuyen);
        psInsertDetail.setString(3, tenPermission);
        psInsertDetail.executeUpdate();
      }

      conn.commit();
      return true;
    } catch (SQLException e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
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
          ex.printStackTrace();
        }
      }
    }
  }
}
