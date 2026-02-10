package phongkham.dao;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import java.sql.SQLException;

import phongkham.DTO.ThuocDTO;
import phongkham.db.DBConnection;


public class ThuocDAO {
    public ArrayList<ThuocDTO> getAllThuoc(){
        ArrayList<ThuocDTO> ds = new ArrayList<>();

        String sql = "SELECT * FROM Thuoc";
        try(
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            while(rs.next()){
                ThuocDTO t = new ThuocDTO();
                t.setMaThuoc(rs.getString("MaThuoc"));
                t.setTenThuoc(rs.getString("TenThuoc"));
                t.setHoatChat(rs.getString("HoatChat"));
                t.setDonViTinh(rs.getString("DonViTinh"));
                t.setDonGiaBan(rs.getFloat("DonGiaBan"));
                t.setSoLuongTon(rs.getInt("SoLuongTon"));

                ds.add(t);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertThuoc(ThuocDTO t){
        String sql = "INSERT INTO Thuoc VALUES (?,?,?,?,?,?)";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, t.getMaThuoc());
            ps.setString(2, t.getTenThuoc());
            ps.setString(3, t.getHoatChat());
            ps.setString(4, t.getDonViTinh());
            ps.setFloat(5, t.getDonGiaBan());
            ps.setInt(6, t.getSoLuongTon());

            return ps.executeUpdate() > 0 ;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateThuoc(ThuocDTO t){
        String sql = "UPDATE Thuoc SET TenThuoc=?, HoatChat=?, DonViTinh=?, DonGiaBan=?, SoLuongTon=? WHERE MaThuoc=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, t.getTenThuoc());
            ps.setString(2, t.getHoatChat());
            ps.setString(3, t.getDonViTinh());
            ps.setFloat(4, t.getDonGiaBan());
            ps.setInt(5, t.getSoLuongTon());
            ps.setString(6, t.getMaThuoc());

            return ps.executeUpdate() > 0 ;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteThuoc(String MaThuoc){
        String sql = "DELETE FROM Thuoc WHERE MaThuoc=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, MaThuoc);
            return ps.executeUpdate() >0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
}
