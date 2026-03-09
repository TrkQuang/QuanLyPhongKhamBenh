package phongkham.BUS;

import java.time.LocalDateTime;
import java.util.List;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.dao.HoaDonThuocDAO;

public class HoaDonThuocBUS {

  private HoaDonThuocDAO hoaDonThuocDAO = new HoaDonThuocDAO();

  // Thêm HoaDonThuoc
  public boolean addHoaDonThuoc(HoaDonThuocDTO hoaDon) {
    if (hoaDon == null) {
      System.err.println("Hóa đơn không được null");
      return false;
    }
    if (
      hoaDon.getTenBenhNhan() == null ||
      hoaDon.getTenBenhNhan().trim().isEmpty()
    ) {
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
    if (hoaDon.getMaHoaDon() == null || hoaDon.getMaHoaDon().trim().isEmpty()) {
      System.err.println("Mã hóa đơn không được rỗng");
      return false;
    }
    if (
      hoaDon.getTenBenhNhan() == null ||
      hoaDon.getTenBenhNhan().trim().isEmpty()
    ) {
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
  public boolean deleteHoaDonThuoc(String maHoaDon) {
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
  public HoaDonThuocDTO getHoaDonThuocDetail(String maHoaDon) {
    return hoaDonThuocDAO.getById(maHoaDon);
  }

  // Lấy tất cả HoaDonThuoc
  public List<HoaDonThuocDTO> getAllHoaDonThuoc() {
    return hoaDonThuocDAO.getAll();
  }

  // Lấy hóa đơn theo khoảng thời gian
  public List<HoaDonThuocDTO> getHoaDonByDateRange(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
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
  public boolean payInvoice(String maHoaDon) {
    HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    if (hoaDon.getTongTien() <= 0) {
      System.err.println("Tổng tiền phải lớn hơn 0");
      return false;
    }

    return hoaDonThuocDAO.updatePaymentStatus(
      maHoaDon,
      "Đã thanh toán",
      LocalDateTime.now()
    );
  }

  // Hoàn hóa đơn
  // Cập nhật trạng thái thành "Hoàn hóa đơn" và xóa thời gian thanh toán
  public boolean refundInvoice(String maHoaDon) {
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
  public double calculateTotalRevenue(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return hoaDonThuocDAO.getTotalRevenue(startDate, endDate);
  }

  // Tính tổng tiền của hóa đơn (Gọi CTHDThuocBUS để tính tổng tiền)
  public double calculateInvoiceTotal(String maHoaDon) {
    HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
    return hoaDon != null ? hoaDon.getTongTien() : 0;
  }

  // Kiểm tra xem hóa đơn có thể chỉnh sửa không
  public boolean canEditInvoice(String maHoaDon) {
    HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
    if (hoaDon == null) return false;

    // Không cho chỉnh sửa hóa đơn đã thanh toán
    return !hoaDon.getTrangThaiThanhToan().equals("Đã thanh toán");
  }

  // Hoàn thành lấy thuốc - Trừ kho và cập nhật trạng thái
  public boolean completePickup(String maHoaDon) {
    HoaDonThuocDTO hoaDon = hoaDonThuocDAO.getById(maHoaDon);
    if (hoaDon == null) {
      System.err.println("Hóa đơn không tồn tại");
      return false;
    }

    // Kiểm tra trạng thái hiện tại
    if (!"ĐANG CHỜ LẤY".equals(hoaDon.getTrangThaiLayThuoc())) {
      System.err.println("Hóa đơn không ở trạng thái ĐANG CHỜ LẤY");
      return false;
    }

    // Kiểm tra đã thanh toán chưa
    if (!"Đã thanh toán".equals(hoaDon.getTrangThaiThanhToan())) {
      System.err.println("Hóa đơn chưa thanh toán, không thể lấy thuốc");
      return false;
    }

    try {
      // Lấy chi tiết hóa đơn để trừ kho
      phongkham.BUS.CTHDThuocBUS cthdBUS = new phongkham.BUS.CTHDThuocBUS();
      phongkham.BUS.ThuocBUS thuocBUS = new phongkham.BUS.ThuocBUS();

      java.util.List<phongkham.DTO.CTHDThuocDTO> chiTiet =
        cthdBUS.getDetailsByInvoice(maHoaDon);

      // Kiểm tra tồn kho trước
      for (phongkham.DTO.CTHDThuocDTO ct : chiTiet) {
        phongkham.DTO.ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
        if (thuoc == null) {
          System.err.println("Thuốc " + ct.getMaThuoc() + " không tồn tại");
          return false;
        }
        if (thuoc.getSoLuongTon() < ct.getSoLuong()) {
          System.err.println(
            "Thuốc " +
              thuoc.getTenThuoc() +
              " không đủ số lượng. " +
              "Tồn kho: " +
              thuoc.getSoLuongTon() +
              ", Cần: " +
              ct.getSoLuong()
          );
          return false;
        }
      }

      // Trừ kho bằng phương thức truSoLuongTon (an toàn hơn)
      for (phongkham.DTO.CTHDThuocDTO ct : chiTiet) {
        boolean truKho = thuocBUS.truSoLuongTon(
          ct.getMaThuoc(),
          ct.getSoLuong()
        );
        if (!truKho) {
          System.err.println(
            "Lỗi trừ số lượng tồn thuốc mã: " + ct.getMaThuoc()
          );
          return false;
        }
      }

      // Cập nhật trạng thái hóa đơn
      hoaDon.setTrangThaiLayThuoc("ĐÃ HOÀN THÀNH");
      boolean result = hoaDonThuocDAO.update(hoaDon);

      if (result) {
        System.out.println("✓ Hoàn thành lấy thuốc và trừ kho thành công!");
      }

      return result;
    } catch (Exception e) {
      System.err.println("Lỗi khi hoàn thành lấy thuốc: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  // Lấy hóa đơn theo trạng thái lấy thuốc
  public java.util.List<HoaDonThuocDTO> getByPickupStatus(
    String trangThaiLayThuoc
  ) {
    java.util.List<HoaDonThuocDTO> result = new java.util.ArrayList<>();
    for (HoaDonThuocDTO hd : hoaDonThuocDAO.getAll()) {
      if (trangThaiLayThuoc.equals(hd.getTrangThaiLayThuoc())) {
        result.add(hd);
      }
    }
    return result;
  }
}
