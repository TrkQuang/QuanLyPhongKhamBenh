package phongkham.dao;

import phongkham.DTO.HoaDonKhamDTO;
import phongkham.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
public class HoaDonKhamDAO {
    public ArrayList<HoaDonKhamDTO> getAll(){
        ArrayList<HoaDonKhamDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDonKham";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                LocalDateTime localDateTime = rs.getObject("NgayThanhToan", LocalDateTime.class);
                HoaDonKhamDTO hd = new HoaDonKhamDTO(rs.getString("MaHDKham"),
                                                    rs.getString("MaPhieuKham"),
                                                    rs.getString("MaGoi"),
                                                    localDateTime,
                                                    rs.getBigDecimal("TongTien"),
                                                    rs.getString("HinhThucThanhToan"));
                                                    
                list.add(hd);
            }
        }catch(SQLException e){
                e.printStackTrace();
        }
        return list;
    }

    public boolean Insert(HoaDonKhamDTO hd){
        String sql = "INSERT INTO HoaDonKham(MaHDKham, MaPhieuKham, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){

            ps.setString(1, hd.getMaHDKham());
            ps.setString(2, hd.getMaPhieuKham());
            ps.setString(3, hd.getMaGoi());
            ps.setObject(4, hd.getNgayThanhToan());
            ps.setBigDecimal(5, hd.getTongTien());
            ps.setString(6, hd.getHinhThucThanhToan());

            return ps.executeUpdate() > 0;
        }catch (SQLException e) {
            e.printStackTrace();
 
        }
        return false;
    }

    public boolean Delete(String MaHDKham){
        String sql = "DELETE FROM HoaDonKham WHERE MaHDKham = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){
             ps.setString(1, MaHDKham);

             return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean Update(HoaDonKhamDTO hd){
        String sql = "UPDATE HoaDonKham SET MaPhieuKham = ?, MaGoi= ?, NgayThanhToan = ?, TongTien = ?, HinhThucThanhToan =? WHERE MaHDKham = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){

            ps.setString(1, hd.getMaPhieuKham());
            ps.setString(2, hd.getMaGoi());
            ps.setObject(3, hd.getNgayThanhToan());
            ps.setBigDecimal(4, hd.getTongTien());
            ps.setString(5, hd.getHinhThucThanhToan());
            ps.setString(6, hd.getMaHDKham());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();  
        }
        return false;

    }

    public HoaDonKhamDTO Search(String MaHDKham){
        String sql = "SELECT * FROM HoaDonKham WHERE MaHDKham = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, MaHDKham);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                LocalDateTime localDateTime = rs.getObject("NgayThanhToan", LocalDateTime.class);
                return new HoaDonKhamDTO(rs.getString("MaHDKham"),
                                                    rs.getString("MaPhieuKham"),
                                                    rs.getString("MaGoi"),
                                                    localDateTime,
                                                    rs.getBigDecimal("TongTien"),
                                                    rs.getString("HinhThucThanhToan"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
}
