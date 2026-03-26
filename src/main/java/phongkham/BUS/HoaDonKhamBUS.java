package phongkham.BUS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
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

  public ArrayList<HoaDonKhamDTO> filterForView(
    String maKeyword,
    String maGoiKeyword,
    LocalDate from,
    LocalDate to
  ) {
    String key =
      maKeyword == null ? "" : maKeyword.trim().toLowerCase(Locale.ROOT);
    String goi =
      maGoiKeyword == null ? "" : maGoiKeyword.trim().toLowerCase(Locale.ROOT);

    LocalDateTime fromTime = from != null ? from.atStartOfDay() : null;
    LocalDateTime toTime = to != null ? to.plusDays(1).atStartOfDay() : null;

    ArrayList<HoaDonKhamDTO> result = new ArrayList<>();
    for (HoaDonKhamDTO hd : hdDAO.getAll()) {
      if (!key.isEmpty()) {
        String maHD = safeLower(hd.getMaHDKham());
        String maHS = safeLower(hd.getMaHoSo());
        if (!maHD.contains(key) && !maHS.contains(key)) {
          continue;
        }
      }

      if (!goi.isEmpty()) {
        String maGoi = safeLower(hd.getMaGoi());
        if (!maGoi.contains(goi)) {
          continue;
        }
      }

      if (fromTime != null && toTime != null) {
        LocalDateTime ngay = hd.getNgayThanhToan();
        if (ngay != null && (ngay.isBefore(fromTime) || !ngay.isBefore(toTime))) {
          continue;
        }
      }

      result.add(hd);
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

  public boolean xacNhanThanhToan(String maHD) {
    HoaDonKhamDTO hd = hdDAO.Search(maHD);
    if (hd == null) {
      return false;
    }
    String trangThai = StatusNormalizer.normalizeToken(hd.getTrangThai());
    if ("HOAN_TIEN".equals(trangThai) || "DA_HUY".equals(trangThai)) {
      return false;
    }
    return hdDAO.updateTrangThaiAndNgayThanhToan(
      maHD,
      StatusNormalizer.DA_THANH_TOAN,
      LocalDateTime.now()
    );
  }

  public boolean huyHoaDon(String maHD) {
    HoaDonKhamDTO hd = hdDAO.Search(maHD);
    if (hd == null) {
      return false;
    }
    String trangThai = StatusNormalizer.normalizeToken(hd.getTrangThai());
    if ("HOAN_TIEN".equals(trangThai) || "DA_HUY".equals(trangThai)) {
      return false;
    }
    return hdDAO.updateTrangThaiAndNgayThanhToan(
      maHD,
      "HOAN_TIEN",
      LocalDateTime.now()
    );
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

  private String safeLower(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }
}
