package phongkham.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.db.DBConnection;
import phongkham.DTO.*;
public class LichKhamDAO {

    public ArrayList<LichKhamDTO> getAll() {
        ArrayList<LichKhamDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LichKham";
        try (Connection c = DBConnection.getConnection();
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql); ) {
            while(rs.next()) {
                LichKhamDTO a = new LichKhamDTO();
                a.setMaLichKham(rs.getString("MaLichKham"));
                a.setMaGoi(rs.getString("MaGoi"));
                a.setMaBacSi(rs.getString("MaBacSi"));
                a.setThoiGianBatDau(rs.getString("ThoiGianBatDau"));
                a.setThoiGianKetThuc(rs.getString("ThoiGianKetThuc"));
                a.setTrangThai(rs.getString("TrangThai"));
                a.setMaDinhDanh(rs.getString("MaDinhDanh"));
                ds.add(a);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertLichKham(LichKhamDTO lk) {
        String sql ="INSERT INTO LichKham VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, lk.getMaLichKham());
                ps.setString(2, lk.getMaGoi());
                ps.setString(3, lk.getMaBacSi());
                ps.setString(4, lk.getThoiGianBatDau());
                ps.setString(5, lk.getThoiGianKetThuc());
                ps.setString(6, lk.getTrangThai());
                ps.setString(6, lk.getMaDinhDanh());
                return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteMaLichKham(String MaLichKham){
        String sql = "DELETE FROM LichKham WHERE MaLichKham=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, MaLichKham);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
