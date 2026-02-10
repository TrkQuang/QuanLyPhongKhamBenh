package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
 
import phongkham.DTO.NhaCungCapDTO; 
import phongkham.db.DBConnection;

public class NhaCungCapDAO {
    public ArrayList<NhaCungCapDTO> getAllNhaCungCap(){
        ArrayList<NhaCungCapDTO> ds= new ArrayList<>();

        String sql = "SELECT * FROM NhaCungCap";
        try(
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            while(rs.next()){
                NhaCungCapDTO ncc = new NhaCungCapDTO();
                ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));
                ncc.setDiaChi(rs.getString("DiaChi"));
                ncc.setSDT(rs.getString("SDT"));

                ds.add(ncc);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertNhaCungCap(NhaCungCapDTO ncc){
        String sql = "INSERT INTO NhaCungCap VALUES (?,?,?,?)";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, ncc.getMaNhaCungCap());
            ps.setString(2, ncc.getTenNhaCungCap());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getSDT());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateNhaCungCap(NhaCungCapDTO ncc){
        String sql = "UPDATE NhaCungCap SET TenNhaCungCap=?, DiaChi=?, SDT=? WHERE MaNhaCungCap=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, ncc.getTenNhaCungCap());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSDT());
            ps.setString(4, ncc.getMaNhaCungCap());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNhaCungCap(String MaNhaCungCap){
        String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, MaNhaCungCap);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
