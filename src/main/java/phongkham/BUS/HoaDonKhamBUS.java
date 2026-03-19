package phongkham.BUS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.HoaDonKhamDAO;

public class HoaDonKhamBUS {

  private HoaDonKhamDAO hdDAO = new HoaDonKhamDAO();

  // ================= GET ALL =================
  public ArrayList<HoaDonKhamDTO> getAll() {
    return hdDAO.getAll();
  }

  // ================= SEARCH =================
  public HoaDonKhamDTO search(String key) {
    if (key == null || key.trim().isEmpty()) return null;
    return hdDAO.Search(key);
  }

  // ================= FILTER BY DATE =================
  public ArrayList<HoaDonKhamDTO> filterByDate(LocalDate from, LocalDate to) {
    if (from == null || to == null) return new ArrayList<>();

    LocalDateTime f = from.atStartOfDay();
    LocalDateTime t = to.atStartOfDay();

    return hdDAO.filterByDate(f, t);
  }

  // ================= SEARCH + FILTER =================
  public ArrayList<HoaDonKhamDTO> searchAndFilter(
    String key,
    LocalDate from,
    LocalDate to
  ) {
    ArrayList<HoaDonKhamDTO> result = new ArrayList<>();

    for (HoaDonKhamDTO hd : hdDAO.getAll()) {
      boolean match = true;

      // --- Search ---
      if (key != null && !key.trim().isEmpty()) {
        match =
          String.valueOf(hd.getMaHDKham()).contains(key) ||
          String.valueOf(hd.getMaHoSo()).contains(key) ||
          String.valueOf(hd.getMaGoi()).contains(key);
      }
      // --- Filter Date ---
      if (
        match && from != null && to != null && hd.getNgayThanhToan() != null
      ) {
        LocalDateTime f = from.atStartOfDay();
        LocalDateTime t = to.atStartOfDay();
        if (
          hd.getNgayThanhToan().isBefore(f) || hd.getNgayThanhToan().isAfter(t)
        ) {
          match = false;
        }
      }
      if (match) result.add(hd);
    }
    return result;
  }

  // ================= ADD =================
  public boolean add(HoaDonKhamDTO hd) {
    if (!validateHoaDon(hd)) return false;
    return hdDAO.Insert(hd);
  }



  // ================= DELETE =================
  public boolean delete(String maHD) {
    HoaDonKhamDTO hd = hdDAO.Search(maHD);
    if (hd == null) return false;
    // Không cho xóa nếu đã thanh toán
    if (isDaThanhToan(hd.getTrangThai())) {
      return false;
    }
    return hdDAO.Delete(maHD);
  }

  // ================= VALIDATE =================
  private boolean validateHoaDon(HoaDonKhamDTO hd) {
    if (hd == null) return false;
    if (hd.getMaGoi() == null || hd.getMaGoi().trim().isEmpty()) return false;
    if (
      hd.getTongTien() == null ||
      hd.getTongTien().compareTo(java.math.BigDecimal.ZERO) <= 0
    ) return false;
    return true;
  }

  // ================= TÍNH TỔNG TIỀN =================
  public java.math.BigDecimal tinhTongTien(LocalDate from, LocalDate to) {
    java.math.BigDecimal total = java.math.BigDecimal.ZERO;
    for (HoaDonKhamDTO hd : filterByDate(from, to)) {
      if (hd.getTongTien() != null) {
        total = total.add(hd.getTongTien());
      }
    }
    return total;
  }

  private boolean isDaThanhToan(String trangThai) {
    return "DA_THANH_TOAN".equals(
      StatusNormalizer.normalizePaymentStatus(trangThai)
    );
  }
}
