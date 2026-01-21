package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import phongkham.DTO.*;
import phongkham.db.DBConnection;

public class PhieuKhamDAO {

    public ArrayList<PhieuKhamDTO> getAll() {
        String sql = "SELECT * FROM PhieuKham";
        ArrayList<PhieuKhamDTO> ds = new ArrayList<>();
        try(Connection c = DBConnection.getConnection();
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);) {
                PhieuKhamDTO pk = new PhieuKhamDTO();
                pk.setMaBacSi(rs.getString("MaBacSi"));
                pk.setMaLichKham(rs.getString("MaLichKham"));
                pk.setMaPhieuKham(rs.getString("MaPhieuKham"));
                pk.setThoiGianTao(rs.getString("ThoiGianTao"));
                pk.setTrieuChungSoBo(rs.getString("TrieuChungSoBo"));
                ds.add(pk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertPhieuKham(PhieuKhamDTO pk) {
        String sql = "INSERT INTO PhieuKham VALUES(?,?,?,?,?)";
        try(Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);) {
                ps.setString(1, pk.getMaPhieuKham());
                ps.setString(2, pk.getMaLichKham());
                ps.setString(3, pk.getMaBacSi());
                ps.setString(4, pk.getThoiGianTao());
                ps.setString(5, pk.getTrieuChungSoBo());
                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePhieuKham(PhieuKhamDTO pk) {
        String sql = "UPDATE PhieuKham SET MaLichKham=? MaBacSi=? ThoiGianTao=? TrieuChungSoBo=? WHERE MaPhieuKham=?";
        try(Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);) {
                ps.setString(1, pk.getMaLichKham());
                ps.setString(2, pk.getMaBacSi());
                ps.setString(3, pk.getThoiGianTao());
                ps.setString(4, pk.getTrieuChungSoBo());
                ps.setString(5, pk.getMaPhieuKham());
                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePhieuKham(String MaPhieuKham){
        String sql = "DELETE PhieuKham WHERE MaPhieuKham=?";
        try(Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);) {
                ps.setString(1, MaPhieuKham);
                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
