package phongkham.BUS;

import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DAO.HoaDonThuocDAO;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDonThuocBUS {
    private HoaDonThuocDAO hoaDonThuocDAO = new HoaDonThuocDAO();

    // Thêm HoaDonThuoc
    public boolean addHoaDonThuoc(HoaDonThuocDTO hoaDon) {
        if (hoaDon == null) {
            System.err.println("Hóa đơn không được null");
            return false;
        }
        if (hoaDon.getTenBenhNhan() == null || hoaDon.getTenBenhNhan().trim().isEmpty()) {
            System.err.println("Tên bệnh nhân không được để trống");
            return false;
        }
        if (hoaDon.getTongTien() < 0) {
            System.err.println("Tổng tiền không được âm");
            return false;
        }

        return hoaDonThuocDAO.insert(hoaDon);
    }

    // Cập nhật HoaDonThuoc
    public boolean updateHoaDonThuoc(HoaDonThuocDTO hoaDon) {
        if (hoaDon == null) {
            System.err.println("Hóa đơn không được null");
            return false;
        }
        if (hoaDon.getMaHoaDon() <= 0) {
            System.err.println("Mã hóa đơn phải > 0");
            return false;
        }
        if (hoaDon.getTenBenhNhan() == null || hoaDon.getTenBenhNhan().trim().isEmpty()) {
            System.err.println("Tên bệnh nhân không được để trống");
            return false;
        }
        if (hoaDon.getTongTien() < 0) {
            System.err.println("Tổng tiền không được âm");
            return false;
        }
        return hoaDonThuocDAO.update(hoaDon);
    }

    // Xóa HoaDonThuoc
    public boolean deleteHoaDonThuoc(int maHoaDon) {
        HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
        if (hoaDon == null) {
            System.err.println("Hóa đơn không tồn tại");
            return false;
        }

        // Không cho xóa hóa đơn đã thanh toán
        if (hoaDon.getTrangThaiThanhToan().equals("Đã thanh toán")) {
            System.err.println("Không thể xóa hóa đơn đã thanh toán");
            return false;
        }

        hoaDon.setActive(false);
        return hoaDonThuocDAO.update(hoaDon);
    }

    // Lấy chi tiết HoaDonThuoc
    public HoaDonThuocDTO getHoaDonThuocDetail(int maHoaDon) {
        return hoaDonThuocDAO.getById(maHoaDon);
    }

    // Lấy tất cả HoaDonThuoc
    public List<HoaDonThuocDTO> getAllHoaDonThuoc() {
        return hoaDonThuocDAO.getAll();
    }

    // Lấy hóa đơn theo khoảng thời gian
    public List<HoaDonThuocDTO> getHoaDonByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return hoaDonThuocDAO.getByDate(startDate, endDate);
    }

    // Lấy hóa đơn chưa thanh toán
    public List<HoaDonThuocDTO> getUnpaidInvoices() {
        return hoaDonThuocDAO.getByPaymentStatus("Chưa thanh toán");
    }

    // Lấy hóa đơn đã thanh toán
    public List<HoaDonThuocDTO> getPaidInvoices() {
        return hoaDonThuocDAO.getByPaymentStatus("Đã thanh toán");
    }

    // Thanh toán hóa đơn
    public boolean payInvoice(int maHoaDon) {
        HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
        if (hoaDon == null) {
            System.err.println("Hóa đơn không tồn tại");
            return false;
        }

        if (hoaDon.getTongTien() <= 0) {
            System.err.println("Tổng tiền phải lớn hơn 0");
            return false;
        }

        return hoaDonThuocDAO.updatePaymentStatus(maHoaDon, "Đã thanh toán", LocalDateTime.now());
    }

    // Hoàn hóa đơn
    // Cập nhật trạng thái thành "Hoàn hóa đơn" và xóa thời gian thanh toán
    public boolean refundInvoice(int maHoaDon) {
        HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
        if (hoaDon == null) {
            System.err.println("Hóa đơn không tồn tại");
            return false;
        }

        if (!hoaDon.getTrangThaiThanhToan().equals("Đã thanh toán")) {
            System.err.println("Chỉ có thể hoàn hóa đơn đã thanh toán");
            return false;
        }

        return hoaDonThuocDAO.updatePaymentStatus(maHoaDon, "Hoàn hóa đơn", null);
    }

    // Tính tổng doanh thu trong khoảng thời gian
    public double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return hoaDonThuocDAO.getTotalRevenue(startDate, endDate);
    }

    // Tính tổng tiền của hóa đơn (Gọi CTHDThuocBUS để tính tổng tiền)
    public double calculateInvoiceTotal(int maHoaDon) {
        HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
        return hoaDon != null ? hoaDon.getTongTien() : 0;
    }

    // Kiểm tra xem hóa đơn có thể chỉnh sửa không
    public boolean canEditInvoice(int maHoaDon) {
        HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
        if (hoaDon == null) return false;
        
        // Không cho chỉnh sửa hóa đơn đã thanh toán
        return !hoaDon.getTrangThaiThanhToan().equals("Đã thanh toán");
    }
}
