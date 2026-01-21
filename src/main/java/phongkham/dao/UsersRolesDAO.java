package phongkham.dao;

import phongkham.DTO.UsersRolesDTO;
import phongkham.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsersRolesDAO {

    
    public boolean Insert(UsersRolesDTO ur) {
        String sql = "INSERT INTO UsersRoles(user_id, role_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ur.getUser_ID());
            ps.setInt(2, ur.getRole_ID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean Delete(String user_id, String role_id) {
        String sql = "DELETE FROM UsersRoles WHERE user_id = ? AND role_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user_id);
            ps.setString(2, role_id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<UsersRolesDTO> getALL(String user_id) {
        ArrayList<UsersRolesDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM UsersRoles WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UsersRolesDTO ur = new UsersRolesDTO(rs.getInt("user_id"),
                                                    rs.getInt("role_id"));
                

                list.add(ur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
