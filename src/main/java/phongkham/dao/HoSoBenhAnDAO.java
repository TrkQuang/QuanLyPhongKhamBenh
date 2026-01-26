package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.db.DBConnection; 

public class HoSoBenhAnDAO {

    public ArrayList<HoSoBenhAnDTO> getAll() {
        String sql = "SELECT * FROM HoSoBenhAn";
        ArrayList<HoSoBenhAnDTO> ds = new ArrayList<>();
        try(Connection c = DBConnection.getConnection();
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql)) {
            while(rs.next()) {
                HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
                hs.setChanDoan(rs.getString("ChanDoan"));
                hs.setKetLuan(rs.getString("KetLuan"));
                hs.setLoiDan(rs.getString("LoiDan"));
                hs.setMaHoSo(rs.getString("MaHoSo"));
                hs.setMaPhieuKham(rs.getString("MaPhieuKham"));
                hs.setNgayKham(rs.getString("NgayKham"));
                ds.add(hs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertHoSoBenhAn(HoSoBenhAnDTO hsba) {
        String sql = "INSERT INTO HoSoBenhAn VALUES(?,?,?,?,?,?)";
        try(Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) 
            {
                ps.setString(1, hsba.getMaHoSo());
                ps.setString(2, hsba.getMaPhieuKham());
                ps.setString(3, hsba.getNgayKham());
                ps.setString(4, hsba.getChanDoan());
                ps.setString(5, hsba.getKetLuan());
                ps.setString(6, hsba.getLoiDan());

                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateHoSoBenhAn(HoSoBenhAnDTO hsba){
        String sql = "UPDATE HoSoBenhAn SET MaPhieuKham=? NgayKham=? ChanDoan=? KetLuan=? LoiDan=? WHERE MaHoSo = ?";
        try(Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);){

                ps.setString(1, hsba.getMaPhieuKham());
                ps.setString(2, hsba.getNgayKham());
                ps.setString(3, hsba.getChanDoan());
                ps.setString(4, hsba.getKetLuan());
                ps.setString(5, hsba.getLoiDan());
                ps.setString(6, hsba.getMaHoSo());

                return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteHoSoBenhAn(String MaHoSo) {
        String sql = "DELTE FROM HoSoBenhAn WHERE MaHoSo = ?";
        try(Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);) {
                ps.setString(1, MaHoSo);

                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
