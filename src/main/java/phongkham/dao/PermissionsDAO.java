package phongkham.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import phongkham.db.DBConnection;

public class PermissionsDAO {

  public ArrayList<String> getPermissionByUser(String userID) {
    Set<String> unique = new LinkedHashSet<>();
    napQuyenTuMoHinhChiTiet(userID, unique);
    return new ArrayList<>(unique);
  }

  private void napQuyenTuMoHinhChiTiet(String userID, Set<String> unique) {
    String sql =
      "SELECT cp.MaQuyen " +
      "FROM Users u " +
      "JOIN Role_Permission rp ON rp.RoleID = u.RoleID " +
      "JOIN ChiTiet_Permission cp ON cp.id = rp.ChiTietPermissionID " +
      "JOIN Permission pm ON pm.id = cp.PermissionID " +
      "WHERE u.UserID = ?";

    try (
      Connection c = DBConnection.getConnection();
      PreparedStatement ps = c.prepareStatement(sql)
    ) {
      ps.setString(1, userID);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String maQuyen = rs.getString("MaQuyen");
          if (maQuyen != null && !maQuyen.trim().isEmpty()) {
            unique.add(maQuyen.trim().toUpperCase());
          }
        }
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}
