package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import phongkham.DTO.PhieuNhapDTO;
import phongkham.db.DBConnection;

public class PhieuNhapDAO {
    public ArrayList<PhieuNhapDTO> getAllPhieuNhap(){
        ArrayList<PhieuNhapDTO> ds = new ArrayList<>();

        String sql = "SELECT * FROM PhieuNhap";
        try(
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            while(rs.next()){
                PhieuNhapDTO pn = new PhieuNhapDTO();
                pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                pn.setNgayNhap(rs.getString("NgayNhap"));
                pn.setNguoiGiao(rs.getString("NguoiGiao"));
                pn.setTongTienNhap(rs.getInt("TongTienNhap"));

                ds.add(pn);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertPhieuNhap(PhieuNhapDTO pn){
        String sql = "INSERT INTO PhieuNhap VALUES (?,?,?,?)";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, pn.getMaPhieuNhap());
            ps.setString(2, pn.getNgayNhap());
            ps.setString(3, pn.getNguoiGiao());
            ps.setFloat(4, pn.getTongTienNhap());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePhieuNhap(PhieuNhapDTO pn){
        String sql = "UPDATE PhieuNhap SET NgayNhap=?, NguoiGiao=?, TongTienNhap=? WHERE MaPhieuNhap=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, pn.getNgayNhap());
            ps.setString(2, pn.getNguoiGiao());
            ps.setFloat(3, pn.getTongTienNhap());
            ps.setString(4, pn.getMaPhieuNhap());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePhieuNhap(String MaPhieuNhap){
        String sql = "DELETE FROM PhieuNhap WHERE MaPhieuNhap=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, MaPhieuNhap);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
