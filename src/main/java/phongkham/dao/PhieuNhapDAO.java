package phongkham.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import phongkham.DTO.PhieuNhapDTO;
import phongkham.db.DBConnection;

public class PhieuNhapDAO {
    //lấy tất cả phiếu nhập
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
                pn.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                pn.setNgayNhap(rs.getString("NgayGiao"));
                pn.setNguoiGiao(rs.getString("NguoiGiao"));
                pn.setTongTienNhap(rs.getFloat("TongTienNhap"));

                ds.add(pn);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ds;
    }

    //thêm phiếu nhập
    public boolean insertPhieuNhap(PhieuNhapDTO pn){
        String sql = "INSERT INTO PhieuNhap VALUES (?,?,?,?,?)";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, pn.getMaPhieuNhap());
            ps.setString(2, pn.getMaNhaCungCap());
            ps.setString(3, pn.getNgayNhap());
            ps.setString(4, pn.getNguoiGiao());
            ps.setFloat(5, pn.getTongTienNhap());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //cập nhật phiếu nhập
    public boolean updatePhieuNhap(PhieuNhapDTO pn){
        String sql = "UPDATE PhieuNhap SET MaNhaCungCap=?, NgayNhap=?, NguoiGiao=?, TongTienNhap=? WHERE MaPhieuNhap=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, pn.getMaNhaCungCap());
            ps.setString(2, pn.getNgayNhap());
            ps.setString(3, pn.getNguoiGiao());
            ps.setFloat(4, pn.getTongTienNhap());
            ps.setString(5, pn.getMaPhieuNhap());

            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    //xóa phiếu nhập theo mã
    public boolean deletePhieuNhap(String maPhieuNhap){
        String sql = "DELETE FROM PhieuNhap WHERE MaPhieuNhap=?";
        try(
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, maPhieuNhap);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTrangThai(String maPN, String trangThaiMoi) {
        String sql = "UPDATE PhieuNhap SET TrangThai = ? WHERE MaPhieuNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maPN);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //tìm kiếm theo mã
    public PhieuNhapDTO getById(String maPhieuNhap){
        String sql = "SELECT * FROM PhieuNhap WHERE MaPhieuNhap=?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maPhieuNhap);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        PhieuNhapDTO pn = new PhieuNhapDTO();
                        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                        pn.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                        pn.setNgayNhap(rs.getString("NgayNhap"));
                        pn.setNguoiGiao(rs.getString("NguoiGiao"));
                        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
                        return pn;
                    }
                }
            }catch(SQLException e){
                System.err.println("Lỗi tìm phiếu nhập theo mã: " +e.getMessage());
            }
            return null;
    }

    //tìm kiếm theo khoảng ngày
    public ArrayList<PhieuNhapDTO> getByDate(LocalDateTime startDate, LocalDateTime endDate){
        ArrayList<PhieuNhapDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE NgayNhap BETWEEN ? AND ? AND Active = 1 ORDER BY NgayNhap DESC";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setObject(1, startDate);
                ps.setObject(2, endDate);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        PhieuNhapDTO pn = new PhieuNhapDTO();
                        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                        pn.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                        pn.setNgayNhap(rs.getString("NgayNhap"));
                        pn.setNguoiGiao(rs.getString("NguoiGiao"));
                        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
                        ds.add(pn);
                    }
                }
            }catch(SQLException e){
                System.err.println("Lỗi tìm phiếu nhập theo khoảng ngày: " +e.getMessage());
            }
            return ds;
    }

    //tìm theo mã nhà cung cấp
    public ArrayList<PhieuNhapDTO> getByMaNCC(String maNCC){
        ArrayList<PhieuNhapDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE MaNhaCungCap = ? AND Active = 1 ORDER BY NgayNhap DESC";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maNCC);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        PhieuNhapDTO pn = new PhieuNhapDTO();
                        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                        pn.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                        pn.setNgayNhap(rs.getString("NgayNhap"));
                        pn.setNguoiGiao(rs.getString("NguoiGiao"));
                        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
                        ds.add(pn);
                    }
                }
            }catch(SQLException e){
                System.err.println("Lỗi tìm phiếu nhập theo mã nhà cung cấp: " +e.getMessage());
            }
            return ds;
    }

    //tìm theo người giao
    public ArrayList<PhieuNhapDTO> getByNguoiGiao(String nguoiGiao){
        ArrayList<PhieuNhapDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE NguoiGiao LIKE ? AND Active = 1 ORDER BY NgayNhap DESC";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, "%" + nguoiGiao + "%");
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        PhieuNhapDTO pn = new PhieuNhapDTO();
                        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                        pn.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                        pn.setNgayNhap(rs.getString("NgayNhap"));
                        pn.setNguoiGiao(rs.getString("NguoiGiao"));
                        pn.setTongTienNhap(rs.getFloat("TongTienNhap"));
                        ds.add(pn);
                    }
                }
            }catch(SQLException e){
                System.err.println("Lỗi tìm phiếu nhập theo người giao: " +e.getMessage());
            }
            return ds;
    }

    //xóa phiếu nhập theo mã nhà cung cấp
    public boolean deleteByNCC(String maNhaCungCap){
        String sql = "UPDATE * FROM PhieuNhap SET Active = 0 WHERE MaNhaCungCap = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maNhaCungCap);
                return ps.executeUpdate()>0;
            }catch(SQLException e){
                System.err.println("Lỗi xóa phiếu nhập theo mã nhà cung cấp: " +e.getMessage());
            }
            return false;
    }

    //kiểm tra nhà cung cấp có phiếu nhập chưa
    public boolean hasPhieuNhap(String maNCC) {
        String sql = "SELECT COUNT(*) FROM PhieuNhap WHERE MaNhaCungCap = ? AND Active = 1";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra phiếu nhập của NCC: " + e.getMessage());
        }
        return false;
    }
}
