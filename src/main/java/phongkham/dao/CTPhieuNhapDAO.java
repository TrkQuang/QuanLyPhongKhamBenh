package phongkham.dao;

import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CTPhieuNhapDAO {
    public ArrayList<CTPhieuNhapDTO> getAll(){
        ArrayList<CTPhieuNhapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                LocalDateTime localDateTime = rs.getObject("HanSuDung", LocalDateTime.class);
                CTPhieuNhapDTO ctpn = new CTPhieuNhapDTO(rs.getString("MaCTPN"),
                                        rs.getString("MaPhieuNhap"),
                                        rs.getString("MaThuoc"),
                                        rs.getInt("SoLuongNhap"),
                                        rs.getBigDecimal("DonGiaNhap"),
                                        localDateTime);
                list.add(ctpn);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public boolean Insert(CTPhieuNhapDTO ctpn){
        String sqp ="INSERT INTO ChiTietPhieuNhap(MaCTPN, MaPhieuNhap, MaThuoc, SoLuongNhap, DonGiaNhap, HanSuDung) VALUE (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqp);){
                ps.setString(1,ctpn.getMaCTPN());
                ps.setString(2,ctpn.getMaPhieuNhap());
                ps.setString(3, ctpn.getMaThuoc());
                ps.setInt(4, ctpn.getSoLuongNhap());
                ps.setBigDecimal(5, ctpn.getDonGiaNhap());
                ps.setObject(6, ctpn.getHanSuDung());
                return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean Delete(String MaCTPN, String MaPhieuNhap){
        String sqp = "DELETE FROM ChiTietPhieuNhap WHERE MaCTPN = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqp);){
                ps.setString(1, MaCTPN);

                return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean Update(CTPhieuNhapDTO ctpn){
        String sql = "UPDATE ChiTietPhieuNhap SET MaPhieuNhap = ?, MaThuoc = ?, SoLuongNhap = ?, DonGiaNhap = ?, HanSuDung = ? WHERE MaCTPN = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){
                ps.setString(1, ctpn.getMaPhieuNhap());
                ps.setString(2, ctpn.getMaThuoc());
                ps.setInt(3, ctpn.getSoLuongNhap());
                ps.setBigDecimal(4, ctpn.getDonGiaNhap());
                ps.setObject(5, ctpn.getHanSuDung());
                ps.setString(6, ctpn.getMaCTPN());

                return ps.executeUpdate() > 0;
        }catch(SQLException e){
                e.printStackTrace();
        }
        return false;
    }

    public CTPhieuNhapDTO Search(String MaCTPN){
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaCTPN = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, MaCTPN);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                LocalDateTime localDateTime = rs.getObject("HanSuDung", LocalDateTime.class);
                return new CTPhieuNhapDTO(rs.getString("MaCTPN"),
                                        rs.getString("MaPhieuNhap"),
                                        rs.getString("MaThuoc"),
                                        rs.getInt("SoLuongNhap"),
                                        rs.getBigDecimal("DonGiaNhap"),
                                        localDateTime);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}

