package phongkham.dao;

import phongkham.DTO.CTHDThuocDTO;
import phongkham.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class CTHDThuocDAO {

    // Thêm mới
    public boolean insert(CTHDThuocDTO cthd) {
        String sql = "INSERT INTO CTHDThuoc (MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cthd.getMaHoaDon());
            pstmt.setInt(2, cthd.getMaThuoc());
            pstmt.setInt(3, cthd.getSoLuong());
            pstmt.setDouble(4, cthd.getDonGia());
            pstmt.setDouble(5, cthd.getThanhTien());
            pstmt.setString(6, cthd.getGhiChu());
            pstmt.setBoolean(7, cthd.isActive());
            
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error inserting CTHDThuoc: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật
    public boolean update(CTHDThuocDTO cthd) {
        String sql = "UPDATE CTHDThuoc SET MaHoaDon = ?, MaThuoc = ?, SoLuong = ?, DonGia = ?, " +
                     "ThanhTien = ?, GhiChu = ?, Active = ? WHERE MaCTHDThuoc = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cthd.getMaHoaDon());
            pstmt.setInt(2, cthd.getMaThuoc());
            pstmt.setInt(3, cthd.getSoLuong());
            pstmt.setDouble(4, cthd.getDonGia());
            pstmt.setDouble(5, cthd.getThanhTien());
            pstmt.setString(6, cthd.getGhiChu());
            pstmt.setBoolean(7, cthd.isActive());
            pstmt.setInt(8, cthd.getMaCTHDThuoc());
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error updating CTHDThuoc: " + e.getMessage());
            return false;
        }
    }

    // Xóa theo ID
    public boolean delete(int maCTHDThuoc) {
        String sql = "DELETE FROM CTHDThuoc WHERE MaCTHDThuoc = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maCTHDThuoc);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error deleting CTHDThuoc: " + e.getMessage());
            return false;
        }
    }

    // Lấy theo ID
    public CTHDThuocDTO getById(int maCTHDThuoc) {
        String sql = "SELECT cthd.*, t.TenThuoc, t.DonVi " +
                     "FROM CTHDThuoc cthd " +
                     "LEFT JOIN Thuoc t ON cthd.MaThuoc = t.MaThuoc " +
                     "WHERE cthd.MaCTHDThuoc = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maCTHDThuoc);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting CTHDThuoc by ID: " + e.getMessage());
        }
        return null;
    }

    // Lấy chi tiết theo hóa đơn
    public List<CTHDThuocDTO> getByInvoice(int maHoaDon) {
        List<CTHDThuocDTO> list = new ArrayList<>();
        String sql = "SELECT cthd.*, t.TenThuoc, t.DonVi " +
                     "FROM CTHDThuoc cthd " +
                     "LEFT JOIN Thuoc t ON cthd.MaThuoc = t.MaThuoc " +
                     "WHERE cthd.MaHoaDon = ? AND cthd.Active = 1 " +
                     "ORDER BY cthd.MaCTHDThuoc ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting CTHDThuoc by invoice: " + e.getMessage());
        }
        return list;
    }

    // Lấy tất cả chi tiết thuốc
    public List<CTHDThuocDTO> getAll() {
        List<CTHDThuocDTO> list = new ArrayList<>();
        String sql = "SELECT cthd.*, t.TenThuoc, t.DonVi " +
                     "FROM CTHDThuoc cthd " +
                     "LEFT JOIN Thuoc t ON cthd.MaThuoc = t.MaThuoc " +
                     "WHERE cthd.Active = 1 ORDER BY cthd.MaCTHDThuoc DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting all CTHDThuoc: " + e.getMessage());
        }
        return list;
    }

    // Tính tổng tiền hóa đơn
    public double getTotalAmount(int maHoaDon) {
        String sql = "SELECT SUM(ThanhTien) FROM CTHDThuoc WHERE MaHoaDon = ? AND Active = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                double total = rs.getDouble(1);
                return Double.isNaN(total) ? 0 : total;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error calculating total amount: " + e.getMessage());
        }
        return 0;
    }

    // Xóa tất cả chi tiết của hóa đơn
    public boolean deleteByInvoice(int maHoaDon) {
        String sql = "UPDATE CTHDThuoc SET Active = 0 WHERE MaHoaDon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHoaDon);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error deleting CTHDThuoc by invoice: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra hóa đơn có chi tiết chưa
     */
    public boolean hasDetails(int maHoaDon) {
        String sql = "SELECT COUNT(*) FROM CTHDThuoc WHERE MaHoaDon = ? AND Active = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error checking details: " + e.getMessage());
        }
        return false;
    }

    // Map ResultSet thành DTO
    private CTHDThuocDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        CTHDThuocDTO dto = new CTHDThuocDTO();
        dto.setMaCTHDThuoc(rs.getInt("MaCTHDThuoc"));
        dto.setMaHoaDon(rs.getInt("MaHoaDon"));
        dto.setMaThuoc(rs.getInt("MaThuoc"));
        dto.setTenThuoc(rs.getString("TenThuoc"));
        dto.setDonVi(rs.getString("DonVi"));
        dto.setSoLuong(rs.getInt("SoLuong"));
        dto.setDonGia(rs.getDouble("DonGia"));
        dto.setThanhTien(rs.getDouble("ThanhTien"));
        dto.setGhiChu(rs.getString("GhiChu"));
        dto.setActive(rs.getBoolean("Active"));
        return dto;
    }
}
