package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.db.DBConnection;
import phongkham.DTO.LichLamViecDTO;

public class LichLamViecDAO {

    public ArrayList<LichLamViecDTO> getAll() {
        ArrayList<LichLamViecDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LichLamViec";
        try (Connection c = DBConnection.getConnection();
                Statement stm = c.createStatement();
                ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                LichLamViecDTO l = new LichLamViecDTO();
                l.setMaLichLam(rs.getString("MaLichLam"));
                l.setMaBacSi(rs.getString("MaBacSi"));
                l.setNgayLam(rs.getString("NgayLam"));
                l.setCaLam(rs.getString("CaLam"));
                ds.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<LichLamViecDTO> getByBacSi(String maBacSi) {
        ArrayList<LichLamViecDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LichLamViec WHERE MaBacSi = ?";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maBacSi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichLamViecDTO l = new LichLamViecDTO();
                    l.setMaLichLam(rs.getString("MaLichLam"));
                    l.setMaBacSi(rs.getString("MaBacSi"));
                    l.setNgayLam(rs.getString("NgayLam"));
                    l.setCaLam(rs.getString("CaLam"));
                    ds.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insert(LichLamViecDTO l) {
        String sql = "INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, l.getMaLichLam());
            ps.setString(2, l.getMaBacSi());
            ps.setString(3, l.getNgayLam());
            ps.setString(4, l.getCaLam());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(LichLamViecDTO l) {
        String sql = "UPDATE LichLamViec SET MaBacSi=?, NgayLam=?, CaLam=? WHERE MaLichLam=?";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, l.getMaBacSi());
            ps.setString(2, l.getNgayLam());
            ps.setString(3, l.getCaLam());
            ps.setString(4, l.getMaLichLam());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maLichLam) {
        String sql = "DELETE FROM LichLamViec WHERE MaLichLam = ?";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maLichLam);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}