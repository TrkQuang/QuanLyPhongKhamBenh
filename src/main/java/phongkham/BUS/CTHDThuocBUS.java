package phongkham.BUS;

import java.util.List;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.dao.CTHDThuocDAO;

public class CTHDThuocBUS {

  private CTHDThuocDAO cthdThuocDAO = new CTHDThuocDAO();

  // Thêm chi tiết thuốc
  public boolean addDetailMedicine(CTHDThuocDTO cthd) {
    if (cthd == null) {
      System.err.println("Chi tiết thuốc không được null");
      return false;
    }
    if (
      cthd.getMaHoaDon() == null ||
      cthd.getMaHoaDon().trim().isEmpty() ||
      cthd.getMaThuoc() == null ||
      cthd.getMaThuoc().trim().isEmpty()
    ) {
      System.err.println("Mã hóa đơn và mã thuốc không được rỗng");
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
    if (
      cthd.getMaCTHDThuoc() == null || cthd.getMaCTHDThuoc().trim().isEmpty()
    ) {
      System.err.println("Mã chi tiết hóa đơn không được rỗng");
      return false;
    }
    if (
      cthd.getMaHoaDon() == null ||
      cthd.getMaHoaDon().trim().isEmpty() ||
      cthd.getMaThuoc() == null ||
      cthd.getMaThuoc().trim().isEmpty()
    ) {
      System.err.println("Mã hóa đơn và mã thuốc không được rỗng");
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
  public boolean deleteDetailMedicine(String maCTHDThuoc) {
    return cthdThuocDAO.delete(maCTHDThuoc);
  }

  // Lấy chi tiết thuốc theo mã
  public CTHDThuocDTO getDetailMedicine(String maCTHDThuoc) {
    return cthdThuocDAO.getById(maCTHDThuoc);
  }

  // Lấy chi tiết thuốc theo mã hóa đơn
  public List<CTHDThuocDTO> getDetailsByInvoice(String maHoaDon) {
    return cthdThuocDAO.getByInvoice(maHoaDon);
  }

  // Lấy tất cả chi tiết thuốc
  public List<CTHDThuocDTO> getAllDetails() {
    return cthdThuocDAO.getAll();
  }

  // Tính tổng tiền của hóa đơn
  public double calculateInvoiceTotal(String maHoaDon) {
    return cthdThuocDAO.getTotalAmount(maHoaDon);
  }

  // Xóa tất cả chi tiết thuốc của một hóa đơn
  public boolean deleteAllDetailsByInvoice(String maHoaDon) {
    return cthdThuocDAO.deleteByInvoice(maHoaDon);
  }

  // Kiểm tra hóa đơn có chi tiết thuốc không
  public boolean hasInvoiceDetails(String maHoaDon) {
    return cthdThuocDAO.hasDetails(maHoaDon);
  }

  // Cập nhật số lượng thuốc
  public boolean updateQuantity(String maCTHDThuoc, int newQuantity) {
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
  public boolean updatePrice(String maCTHDThuoc, double newPrice) {
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
  public int getTotalMedicineQuantitySold(String maThuoc) {
    int total = 0;
    List<CTHDThuocDTO> details = cthdThuocDAO.getAll();
    for (CTHDThuocDTO detail : details) {
      if (detail.getMaThuoc().equals(maThuoc)) {
        total += detail.getSoLuong();
      }
    }
    return total;
  }

  // Lấy tổng doanh thu từ một loại thuốc
  public double getMedicineRevenue(String maThuoc) {
    double total = 0;
    List<CTHDThuocDTO> details = cthdThuocDAO.getAll();
    for (CTHDThuocDTO detail : details) {
      if (detail.getMaThuoc().equals(maThuoc)) {
        total += detail.getThanhTien();
      }
    }
    return total;
  }
}
