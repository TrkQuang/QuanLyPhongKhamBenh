package phongkham.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import phongkham.db.DBConnection;
import phongkham.DTO.CTDonThuocDTO;

public class CTDonThuocDAO {
  public ArrayList<CTDonThuocDTO> getAll() {
        ArrayList<CTDonThuocDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM CTDonThuoc";

        try (
            Connection c = DBConnection.getConnection();
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);
        ) {
            while (rs.next()) {
                CTDonThuocDTO ct = new CTDonThuocDTO();
                ct.setMaCTDonThuoc(rs.getString("MaCTDonThuoc"));
                ct.setMaDonThuoc(rs.getString("MaDonThuoc"));
                ct.setMaThuoc(rs.getString("MaThuoc"));
                ct.setSoLuong(rs.getInt("SoLuong"));
                ct.setLieuDung(rs.getString("LieuDung"));
                ct.setCachDung(rs.getString("CachDung"));
                ds.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Thêm chi tiết đơn thuốc
    public boolean insertCTDonThuoc(CTDonThuocDTO ct) {
        String sql = "INSERT INTO CTDonThuoc VALUES (?,?,?,?,?,?)";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, ct.getMaCTDonThuoc());
            ps.setString(2, ct.getMaDonThuoc());
            ps.setString(3, ct.getMaThuoc());
            ps.setInt(4, ct.getSoluong());
            ps.setString(5, ct.getLieuDung());
            ps.setString(6, ct.getCachDung());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật chi tiết đơn thuốc
    public boolean updateCTDonThuoc(CTDonThuocDTO ct) {
        String sql = "UPDATE CTDonThuoc "
                   + "SET MaDonThuoc=?, MaThuoc=?, SoLuong=?, LieuDung=?, CachDung=? "
                   + "WHERE MaCTDonThuoc=?";
        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, ct.getMaDonThuoc());
            ps.setString(2, ct.getMaThuoc());
            ps.setInt(3, ct.getSoluong());
            ps.setString(4, ct.getLieuDung());
            ps.setString(5, ct.getCachDung());
            ps.setString(6, ct.getMaCTDonThuoc());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa chi tiết đơn thuốc theo mã
    public boolean deleteMaCTDonThuoc(String maCTDonThuoc) {
        String sql = "DELETE FROM CTDonThuoc WHERE MaCTDonThuoc=?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, maCTDonThuoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lọc chi tiết đơn thuốc theo Mã Đơn Thuốc
    public ArrayList<CTDonThuocDTO> getByMaDonThuoc(String maDonThuoc) {
        ArrayList<CTDonThuocDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM CTDonThuoc WHERE MaDonThuoc=?";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
        ) {
            ps.setString(1, maDonThuoc);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CTDonThuocDTO ct = new CTDonThuocDTO();
                ct.setMaCTDonThuoc(rs.getString("MaCTDonThuoc"));
                ct.setMaDonThuoc(rs.getString("MaDonThuoc"));
                ct.setMaThuoc(rs.getString("MaThuoc"));
                ct.setSoLuong(rs.getInt("SoLuong"));
                ct.setLieuDung(rs.getString("LieuDung"));
                ct.setCachDung(rs.getString("CachDung"));
                ds.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}
