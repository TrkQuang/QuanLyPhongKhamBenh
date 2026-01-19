package phongkham.DAO;

import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DB.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonThuocDAO {

    // Thêm mới
    public boolean insert(HoaDonThuocDTO hoaDon) {
        String sql = "INSERT INTO HoaDonThuoc (MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan, TenBenhNhan, SdtBenhNhan, Active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, hoaDon.getMaDonThuoc());
            pstmt.setObject(2, hoaDon.getNgayLap());
            pstmt.setDouble(3, hoaDon.getTongTien());
            pstmt.setString(4, hoaDon.getGhiChu());
            pstmt.setString(5, hoaDon.getTrangThaiThanhToan());
            pstmt.setObject(6, hoaDon.getNgayThanhToan());
            pstmt.setString(7, hoaDon.getTenBenhNhan());
            pstmt.setString(8, hoaDon.getSdtBenhNhan());
            pstmt.setBoolean(9, hoaDon.isActive());
            
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error inserting HoaDonThuoc: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật
    public boolean update(HoaDonThuocDTO hoaDon) {
        String sql = "UPDATE HoaDonThuoc SET MaDonThuoc = ?, NgayLap = ?, TongTien = ?, GhiChu = ?, " +
                     "TrangThaiThanhToan = ?, NgayThanhToan = ?, TenBenhNhan = ?, SdtBenhNhan = ?, Active = ? " +
                     "WHERE MaHoaDon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, hoaDon.getMaDonThuoc());
            pstmt.setObject(2, hoaDon.getNgayLap());
            pstmt.setDouble(3, hoaDon.getTongTien());
            pstmt.setString(4, hoaDon.getGhiChu());
            pstmt.setString(5, hoaDon.getTrangThaiThanhToan());
            pstmt.setObject(6, hoaDon.getNgayThanhToan());
            pstmt.setString(7, hoaDon.getTenBenhNhan());
            pstmt.setString(8, hoaDon.getSdtBenhNhan());
            pstmt.setBoolean(9, hoaDon.isActive());
            pstmt.setInt(10, hoaDon.getMaHoaDon());
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error updating HoaDonThuoc: " + e.getMessage());
            return false;
        }
    }

    // Xóa theo ID
    public boolean delete(int maHoaDon) {
        String sql = "DELETE FROM HoaDonThuoc WHERE MaHoaDon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHoaDon);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error deleting HoaDonThuoc: " + e.getMessage());
            return false;
        }
    }

    // Lấy theo ID
    public HoaDonThuocDTO getById(int maHoaDon) {
        String sql = "SELECT * FROM HoaDonThuoc WHERE MaHoaDon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting HoaDonThuoc by ID: " + e.getMessage());
        }
        return null;
    }

    // Lấy tất cả (active)
    public List<HoaDonThuocDTO> getAll() {
        List<HoaDonThuocDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDonThuoc WHERE Active = 1 ORDER BY NgayLap DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting all HoaDonThuoc: " + e.getMessage());
        }
        return list;
    }

    // Lấy hóa đơn theo khoảng ngày
    public List<HoaDonThuocDTO> getByDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<HoaDonThuocDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDonThuoc WHERE NgayLap BETWEEN ? AND ? AND Active = 1 ORDER BY NgayLap DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, startDate);
            pstmt.setObject(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting HoaDonThuoc by date: " + e.getMessage());
        }
        return list;
    }

    // Lấy hóa đơn theo trạng thái thanh toán
    public List<HoaDonThuocDTO> getByPaymentStatus(String trangThai) {
        List<HoaDonThuocDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDonThuoc WHERE TrangThaiThanhToan = ? AND Active = 1 ORDER BY NgayLap DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting HoaDonThuoc by payment status: " + e.getMessage());
        }
        return list;
    }

    // Tính tổng doanh thu trong khoảng ngày
    public double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(TongTien) FROM HoaDonThuoc WHERE NgayLap BETWEEN ? AND ? AND TrangThaiThanhToan = 'Đã thanh toán'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, startDate);
            pstmt.setObject(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble(1);
                    return Double.isNaN(total) ? 0 : total;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error calculating revenue: " + e.getMessage());
        }
        return 0;
    }

    // Cập nhật trạng thái thanh toán
    public boolean updatePaymentStatus(int maHoaDon, String trangThai, LocalDateTime ngayThanhToan) {
        String sql = "UPDATE HoaDonThuoc SET TrangThaiThanhToan = ?, NgayThanhToan = ? WHERE MaHoaDon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trangThai);
            pstmt.setObject(2, ngayThanhToan);
            pstmt.setInt(3, maHoaDon);
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    // Map ResultSet thành DTO
    private HoaDonThuocDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        HoaDonThuocDTO dto = new HoaDonThuocDTO();
        dto.setMaHoaDon(rs.getInt("MaHoaDon"));
        
        Object maDonThuoc = rs.getObject("MaDonThuoc");
        dto.setMaDonThuoc(maDonThuoc != null ? (Integer) maDonThuoc : null);
        
        dto.setNgayLap(rs.getObject("NgayLap", LocalDateTime.class));
        dto.setTongTien(rs.getDouble("TongTien"));
        dto.setGhiChu(rs.getString("GhiChu"));
        dto.setTrangThaiThanhToan(rs.getString("TrangThaiThanhToan"));
        dto.setNgayThanhToan(rs.getObject("NgayThanhToan", LocalDateTime.class));
        dto.setTenBenhNhan(rs.getString("TenBenhNhan"));
        dto.setSdtBenhNhan(rs.getString("SdtBenhNhan"));
        dto.setActive(rs.getBoolean("Active"));
        
        return dto;
    }
}
