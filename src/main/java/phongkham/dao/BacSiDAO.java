package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.db.DBConnection;
import phongkham.DTO.BacSiDTO;

public class BacSiDAO {

    public ArrayList<BacSiDTO> getAll() {
        ArrayList<BacSiDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM BacSi";
        try (Connection c = DBConnection.getConnection();
                Statement stm = c.createStatement();
                ResultSet rs = stm.executeQuery(sql);) {
            while (rs.next()) {

                BacSiDTO b = new BacSiDTO();
                b.setMaBacSi(rs.getString("MaBacSi"));
                b.setHoTen(rs.getString("HoTen"));
                b.setChuyenKhoa(rs.getString("ChuyenKhoa"));
                b.setSoDienThoai(rs.getString("SoDienThoai"));
                b.setEmail(rs.getString("Email"));
                b.setMaKhoa(rs.getString("MaKhoa"));
                ds.add(b);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertBacSi(BacSiDTO bs) {
        String sql = "INSERT INTO BacSi VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bs.getMaBacSi());
            ps.setString(2, bs.getHoTen());
            ps.setString(3, bs.getChuyenKhoa());
            ps.setString(4, bs.getSoDienThoai());
            ps.setString(5, bs.getEmail());
            ps.setString(6, bs.getMaKhoa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMaBacSi(String MaBacSi) {
        String sql = "DELETE FROM BacSi WHERE MaBacSi=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, MaBacSi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBacSi(BacSiDTO bs) {
        String sql = "UPDATE BacSi SET HoTen=?, ChuyenKhoa=?, SoDienThoai=?, Email=?, MaKhoa=? WHERE MaBacSi=?";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, bs.getHoTen());
            ps.setString(2, bs.getChuyenKhoa());
            ps.setString(3, bs.getSoDienThoai());
            ps.setString(4, bs.getEmail());
            ps.setString(5, bs.getMaKhoa());

            // Điều kiện WHERE (Cái cuối cùng)
            ps.setString(6, bs.getMaBacSi());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
