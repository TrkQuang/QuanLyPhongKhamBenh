package phongkham.BUS;

import phongkham.DTO.CTHDThuocDTO;
import phongkham.dao.CTHDThuocDAO;
import java.util.List;
 
public class CTHDThuocBUS {
    private CTHDThuocDAO cthdThuocDAO = new CTHDThuocDAO();

    // Thêm chi tiết thuốc
    public boolean addDetailMedicine(CTHDThuocDTO cthd) {
        if (cthd == null) {
            System.err.println("Chi tiết thuốc không được null");
            return false;
        }
        if (cthd.getMaHoaDon() <= 0 || cthd.getMaThuoc() <= 0) {
            System.err.println("Mã hóa đơn và mã thuốc phải > 0");
            return false;
        }
        if (cthd.getSoLuong() <= 0) {
            System.err.println("Số lượng phải > 0");
            return false;
        }
        if (cthd.getDonGia() <= 0) {
            System.err.println("Đơn giá phải > 0");
            return false;
        }

        return cthdThuocDAO.insert(cthd);
    }

    // Cập nhật chi tiết thuốc
    public boolean updateDetailMedicine(CTHDThuocDTO cthd) {
        if (cthd == null) {
            System.err.println("Chi tiết thuốc không được null");
            return false;
        }
        if (cthd.getMaCTHDThuoc() <= 0) {
            System.err.println("Mã chi tiết hóa đơn phải > 0");
            return false;
        }
        if (cthd.getMaHoaDon() <= 0 || cthd.getMaThuoc() <= 0) {
            System.err.println("Mã hóa đơn và mã thuốc phải > 0");
            return false;
        }
        if (cthd.getSoLuong() <= 0) {
            System.err.println("Số lượng phải > 0");
            return false;
        }
        if (cthd.getDonGia() <= 0) {
            System.err.println("Đơn giá phải > 0");
            return false;
        }

        return cthdThuocDAO.update(cthd);
    }

    // Xóa chi tiết thuốc
    public boolean deleteDetailMedicine(int maCTHDThuoc) {
        return cthdThuocDAO.delete(maCTHDThuoc);
    }

    // Lấy chi tiết thuốc theo mã
    public CTHDThuocDTO getDetailMedicine(int maCTHDThuoc) {
        return cthdThuocDAO.getById(maCTHDThuoc);
    }

    // Lấy chi tiết thuốc theo mã hóa đơn
    public List<CTHDThuocDTO> getDetailsByInvoice(int maHoaDon) {
        return cthdThuocDAO.getByInvoice(maHoaDon);
    }

    // Lấy tất cả chi tiết thuốc
    public List<CTHDThuocDTO> getAllDetails() {
        return cthdThuocDAO.getAll();
    }

    // Tính tổng tiền của hóa đơn
    public double calculateInvoiceTotal(int maHoaDon) {
        return cthdThuocDAO.getTotalAmount(maHoaDon);
    }

    // Xóa tất cả chi tiết thuốc của một hóa đơn
    public boolean deleteAllDetailsByInvoice(int maHoaDon) {
        return cthdThuocDAO.deleteByInvoice(maHoaDon);
    }

    // Kiểm tra hóa đơn có chi tiết thuốc không
    public boolean hasInvoiceDetails(int maHoaDon) {
        return cthdThuocDAO.hasDetails(maHoaDon);
    }

    // Cập nhật số lượng thuốc
    public boolean updateQuantity(int maCTHDThuoc, int newQuantity) {
        if (newQuantity <= 0) {
            System.err.println("Số lượng phải > 0");
            return false;
        }

        CTHDThuocDTO cthd = cthdThuocDAO.getById(maCTHDThuoc);
        if (cthd == null) {
            System.err.println("Chi tiết thuốc không tồn tại");
            return false;
        }

        cthd.setSoLuong(newQuantity);
        return updateDetailMedicine(cthd);
    }

    // Cập nhật đơn giá thuốc
    public boolean updatePrice(int maCTHDThuoc, double newPrice) {
        if (newPrice <= 0) {
            System.err.println("Đơn giá phải > 0");
            return false;
        }

        CTHDThuocDTO cthd = cthdThuocDAO.getById(maCTHDThuoc);
        if (cthd == null) {
            System.err.println("Chi tiết thuốc không tồn tại");
            return false;
        }

        cthd.setDonGia(newPrice);
        return updateDetailMedicine(cthd);
    }

    // Lấy tổng số lượng thuốc đã bán
    public int getTotalMedicineQuantitySold(int maThuoc) {
        int total = 0;
        List<CTHDThuocDTO> details = cthdThuocDAO.getAll();
        for (CTHDThuocDTO detail : details) {
            if (detail.getMaThuoc() == maThuoc) {
                total += detail.getSoLuong();
            }
        }
        return total;
    }

    // Lấy tổng doanh thu từ một loại thuốc
    public double getMedicineRevenue(int maThuoc) {
        double total = 0;
        List<CTHDThuocDTO> details = cthdThuocDAO.getAll();
        for (CTHDThuocDTO detail : details) {
            if (detail.getMaThuoc() == maThuoc) {
                total += detail.getThanhTien();
            }
        }
        return total;
    }
}
