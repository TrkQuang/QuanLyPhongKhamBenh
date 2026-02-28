package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.db.DBConnection;
import phongkham.DTO.GoiDichVuDTO;
import java.math.BigDecimal;
public class GoiDichVuDAO {

    // Lấy danh sách tất cả gói dịch vụ
    public ArrayList<GoiDichVuDTO> getAll() {
        ArrayList<GoiDichVuDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM GoiDichVu";

        try (
            Connection c = DBConnection.getConnection();
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);
        ) {
            while (rs.next()) {
                GoiDichVuDTO g = new GoiDichVuDTO();
                g.setMaGoi(rs.getString("MaGoi"));
                g.setTenGoi(rs.getString("TenGoi"));
                g.setGiaDichVu(rs.getBigDecimal("GiaDichVu"));
                g.setThoiGianKham(rs.getString("ThoiGianKham"));
                g.setMoTa(rs.getString("MoTa"));
                ds.add(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Thêm gói dịch vụ
    public boolean insertGoiDichVu(GoiDichVuDTO g) {
        String sql = "INSERT INTO GoiDichVu VALUES (?,?,?,?,?)";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, g.getMaGoi());
            ps.setString(2, g.getTenGoi());
            ps.setBigDecimal(3, g.getGiaDichVu());
            ps.setString(4, g.getThoiGianKham());
            ps.setString(5, g.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật gói dịch vụ
    public boolean updateGoiDichVu(GoiDichVuDTO g) {
        String sql = "UPDATE GoiDichVu "
                   + "SET TenGoi=?, GiaDichVu=?, ThoiGianKham=?, MoTa=? "
                   + "WHERE MaGoi=?";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, g.getTenGoi());
            ps.setBigDecimal(2, g.getGiaDichVu());
            ps.setString(3, g.getThoiGianKham());
            ps.setString(4, g.getMoTa());
            ps.setString(5, g.getMaGoi());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa gói dịch vụ theo mã
    public boolean deleteMaGoi(String maGoi) {
        String sql = "DELETE FROM GoiDichVu WHERE MaGoi=?";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, maGoi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy gói dịch vụ theo mã
    public GoiDichVuDTO getByMaGoi(String maGoi) {
        String sql = "SELECT * FROM GoiDichVu WHERE MaGoi=?";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, maGoi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                GoiDichVuDTO g = new GoiDichVuDTO();
                g.setMaGoi(rs.getString("MaGoi"));
                g.setTenGoi(rs.getString("TenGoi"));
                g.setGiaDichVu(rs.getBigDecimal("GiaDichVu"));
                g.setThoiGianKham(rs.getString("ThoiGianKham"));
                g.setMoTa(rs.getString("MoTa"));
                return g;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra mã gói dịch vụ có tồn tại không
    public boolean existsMaGoi(String maGoi) {
        String sql = "SELECT 1 FROM GoiDichVu WHERE MaGoi=?";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, maGoi);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
