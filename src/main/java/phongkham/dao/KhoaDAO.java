package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.db.DBConnection;
import phongkham.DTO.KhoaDTO;

public class KhoaDAO {

    public ArrayList<KhoaDTO> getAll() {
        ArrayList<KhoaDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM Khoa";
        try (Connection c = DBConnection.getConnection();
                Statement stm = c.createStatement();
                ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                KhoaDTO k = new KhoaDTO();
                k.setMaKhoa(rs.getString("MaKhoa"));
                k.setTenKhoa(rs.getString("TenKhoa"));
                ds.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertKhoa(KhoaDTO k) {
        String sql = "INSERT INTO Khoa (MaKhoa, TenKhoa) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k.getMaKhoa());
            ps.setString(2, k.getTenKhoa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteKhoa(String maKhoa) {
        String sql = "DELETE FROM Khoa WHERE MaKhoa = ?";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maKhoa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateKhoa(KhoaDTO k) {
        String sql = "UPDATE Khoa SET TenKhoa = ? WHERE MaKhoa = ?";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, k.getTenKhoa());
            ps.setString(2, k.getMaKhoa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}