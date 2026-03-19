package phongkham.BUS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import phongkham.DTO.LichKhamDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.LichKhamDAO;

public class LichKhamBUS {

  private LichKhamDAO dao = new LichKhamDAO();

  // ✅ CONSTANTS: Danh sách trạng thái hợp lệ
  private static final List<String> VALID_STATUSES = Arrays.asList(
    "CHO_XAC_NHAN",
    "DA_XAC_NHAN",
    "DANG_KHAM",
    "HOAN_THANH",
    "DA_HUY"
  );

  private static final DateTimeFormatter DATETIME_FORMAT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final DateTimeFormatter DATE_FORMAT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd");

  // ===== CRUD OPERATIONS =====

  public String insert(LichKhamDTO lk) {
    // Validate
    String error = validateLichKham(lk, false);
    if (error != null) return error;

    // Thực hiện thêm
    return dao.insert(lk)
      ? "✅ Thêm lịch khám thành công"
      : "❌ Thêm lịch khám thất bại";
  }

  public String update(LichKhamDTO lk) {
    // Validate
    String error = validateLichKham(lk, true);
    if (error != null) return error;

    // Thực hiện cập nhật
    return dao.update(lk)
      ? "✅ Cập nhật lịch khám thành công"
      : "❌ Cập nhật lịch khám thất bại";
  }

  public String delete(String maLichKham) {
    if (isEmpty(maLichKham)) {
      return "❌ Mã lịch khám không được để trống";
    }

    LichKhamDTO lk = requireExisting(maLichKham);
    if (lk == null) {
      return "❌ Lịch khám không tồn tại";
    }

    if (
      StatusNormalizer.HOAN_THANH.equals(
        StatusNormalizer.normalizeLichKhamStatus(lk.getTrangThai())
      )
    ) {
      return "❌ Không thể xóa lịch khám đã hoàn thành";
    }

    return dao.delete(maLichKham)
      ? "✅ Xóa lịch khám thành công"
      : "❌ Xóa lịch khám thất bại";
  }

  // ===== VALIDATION =====

  /**
   * ✅ METHOD DÙNG CHUNG: Validate lịch khám
   * @param lk Lịch khám cần validate
   * @param isUpdate true nếu đang update, false nếu đang insert
   * @return Error message hoặc null nếu hợp lệ
   */
  private String validateLichKham(LichKhamDTO lk, boolean isUpdate) {
    // 1. Validate null/empty
    if (
      isEmpty(lk.getMaLichKham())
    ) return "❌ Mã lịch khám không được để trống";
    if (isEmpty(lk.getMaBacSi())) return "❌ Mã bác sĩ không được để trống";
    if (
      isEmpty(lk.getThoiGianBatDau())
    ) return "❌ Thời gian bắt đầu không được để trống";
    if (
      isEmpty(lk.getThoiGianKetThuc())
    ) return "❌ Thời gian kết thúc không được để trống";
    if (isEmpty(lk.getTrangThai())) return "❌ Trạng thái không được để trống";

    // 2. Validate tồn tại
    boolean exists = dao.exists(lk.getMaLichKham());
    if (!isUpdate && exists) {
      return "❌ Mã lịch khám đã tồn tại";
    }
    if (isUpdate && !exists) {
      return "❌ Lịch khám không tồn tại";
    }

    // 3. Validate thời gian
    if (lk.getThoiGianBatDau().compareTo(lk.getThoiGianKetThuc()) >= 0) {
      return "❌ Thời gian kết thúc phải lớn hơn thời gian bắt đầu";
    }

    // 4. Validate trùng lịch
    boolean trungLich = isUpdate
      ? dao.checkTrungLichWhenUpdate(
          lk.getMaLichKham(),
          lk.getMaBacSi(),
          lk.getThoiGianBatDau(),
          lk.getThoiGianKetThuc()
        )
      : dao.checkTrungLich(
          lk.getMaBacSi(),
          lk.getThoiGianBatDau(),
          lk.getThoiGianKetThuc()
        );

    if (trungLich) {
      return "❌ Bác sĩ đã có lịch khám trùng với khung giờ này";
    }

    // 5. Validate trạng thái
    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(
      lk.getTrangThai()
    );
    if (!isValidStatus(trangThaiChuan)) {
      return "❌ Trạng thái không hợp lệ (Đã đặt/Đang khám/Hoàn thành/Đã hủy)";
    }
    lk.setTrangThai(trangThaiChuan);

    return null; // ✅ Hợp lệ
  }

  /**
   * ✅ HELPER: Kiểm tra chuỗi rỗng
   */
  private boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * ✅ HELPER: Kiểm tra trạng thái hợp lệ
   */
  private boolean isValidStatus(String status) {
    if (status == null) {
      return false;
    }
    String trangThai = StatusNormalizer.normalizeLichKhamStatus(status);
    return VALID_STATUSES.contains(trangThai);
  }

  // ===== STATUS OPERATIONS =====

  public String huyLichKham(String maLichKham) {
    if (isEmpty(maLichKham)) {
      return "❌ Mã lịch khám không được để trống";
    }

    LichKhamDTO lk = requireExisting(maLichKham);
    if (lk == null) {
      return "❌ Lịch khám không tồn tại";
    }

    String status = StatusNormalizer.normalizeLichKhamStatus(lk.getTrangThai());
    if ("HOAN_THANH".equals(status)) {
      return "❌ Không thể hủy lịch khám đã hoàn thành";
    }
    if ("DA_HUY".equals(status)) {
      return "⚠️ Lịch khám đã được hủy trước đó";
    }

    return dao.updateTrangThai(maLichKham, "DA_HUY")
      ? "✅ Hủy lịch khám thành công"
      : "❌ Hủy lịch khám thất bại";
  }

  public String updateTrangThai(String maLichKham, String trangThai) {
    if (isEmpty(maLichKham)) {
      return "❌ Mã lịch khám không được để trống";
    }
    if (isEmpty(trangThai)) {
      return "❌ Trạng thái không được để trống";
    }
    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(trangThai);
    if (!isValidStatus(trangThaiChuan)) {
      return "❌ Trạng thái không hợp lệ (Đã đặt/Đang khám/Hoàn thành/Đã hủy)";
    }
    if (requireExisting(maLichKham) == null) {
      return "❌ Lịch khám không tồn tại";
    }

    return dao.updateTrangThai(maLichKham, trangThaiChuan)
      ? "✅ Cập nhật trạng thái thành công"
      : "❌ Cập nhật trạng thái thất bại";
  }

  // ===== SEARCH OPERATIONS =====

  public ArrayList<LichKhamDTO> getAll() {
    return dao.getAll();
  }

  public LichKhamDTO getById(String ma) {
    return isEmpty(ma) ? null : dao.getById(ma);
  }

  public ArrayList<LichKhamDTO> getByMaBacSi(String maBacSi) {
    return isEmpty(maBacSi) ? new ArrayList<>() : dao.getByMaBacSi(maBacSi);
  }

  public ArrayList<LichKhamDTO> getByMaGoi(String maGoi) {
    return isEmpty(maGoi) ? new ArrayList<>() : dao.getByMaGoi(maGoi);
  }

  public ArrayList<LichKhamDTO> getByMaDinhDanhTam(String maDinhDanhTam) {
    return isEmpty(maDinhDanhTam)
      ? new ArrayList<>()
      : dao.getByMaDinhDanhTam(maDinhDanhTam);
  }

  public ArrayList<LichKhamDTO> getByTrangThai(String trangThai) {
    if (isEmpty(trangThai)) {
      return new ArrayList<>();
    }
    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(trangThai);
    return dao.getByTrangThai(trangThaiChuan);
  }

  public ArrayList<LichKhamDTO> getByNgay(String ngay) {
    return isEmpty(ngay) ? new ArrayList<>() : dao.getByNgay(ngay);
  }

  public ArrayList<LichKhamDTO> getByBacSiAndNgay(String maBacSi, String ngay) {
    if (isEmpty(maBacSi) || isEmpty(ngay)) {
      return new ArrayList<>();
    }
    return dao.getByBacSiAndNgay(maBacSi, ngay);
  }

  public ArrayList<LichKhamDTO> getByKhoangThoiGian(
    String tuNgay,
    String denNgay
  ) {
    if (isEmpty(tuNgay) || isEmpty(denNgay) || tuNgay.compareTo(denNgay) > 0) {
      return new ArrayList<>();
    }
    return dao.getByKhoangThoiGian(tuNgay, denNgay);
  }

  public ArrayList<LichKhamDTO> search(String keyword) {
    return isEmpty(keyword) ? dao.getAll() : dao.search(keyword);
  }

  // ===== UTILITY OPERATIONS =====

  public int countByTrangThai(String trangThai) {
    return getByTrangThai(trangThai).size();
  }

  public boolean kiemTraTrungLich(
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc
  ) {
    if (maBacSi == null || thoiGianBatDau == null || thoiGianKetThuc == null) {
      return false;
    }
    return dao.checkTrungLich(maBacSi, thoiGianBatDau, thoiGianKetThuc);
  }

  public ArrayList<LichKhamDTO> getLichKhamSapToi(String maBacSi) {
    ArrayList<LichKhamDTO> dsAll = dao.getByMaBacSi(maBacSi);
    ArrayList<LichKhamDTO> dsSapToi = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    for (LichKhamDTO lk : dsAll) {
      try {
        LocalDateTime tgBatDau = LocalDateTime.parse(
          lk.getThoiGianBatDau(),
          DATETIME_FORMAT
        );
        if (
          tgBatDau.isAfter(now) &&
          !"DA_HUY".equals(
            StatusNormalizer.normalizeLichKhamStatus(lk.getTrangThai())
          )
        ) {
          dsSapToi.add(lk);
        }
      } catch (Exception e) {
        System.err.println("❌ Lỗi parse thời gian: " + e.getMessage());
      }
    }

    return dsSapToi;
  }

  public ArrayList<LichKhamDTO> getLichKhamHomNay() {
    String homNay = LocalDateTime.now().format(DATE_FORMAT);
    return dao.getByNgay(homNay);
  }

  public String thongKeLichKham() {
    int soChoXacNhan = countByTrangThai("CHO_XAC_NHAN");
    int soDangKham = countByTrangThai("DANG_KHAM");
    int soHoanThanh = countByTrangThai("HOAN_THANH");
    int soDaHuy = countByTrangThai("DA_HUY");
    int tongSo = soChoXacNhan + soDangKham + soHoanThanh + soDaHuy;

    return String.format(
      "=== THỐNG KÊ LỊCH KHÁM ===\n" +
        "Tổng số: %d\n" +
        "Chờ xác nhận: %d\n" +
        "Đang khám: %d\n" +
        "Hoàn thành: %d\n" +
        "Đã hủy: %d",
      tongSo,
      soChoXacNhan,
      soDangKham,
      soHoanThanh,
      soDaHuy
    );
  }

  public String generateMaLichKham() {
    ArrayList<LichKhamDTO> dsAll = dao.getAll();
    int maxId = 0;

    for (LichKhamDTO lk : dsAll) {
      try {
        String ma = lk.getMaLichKham();
        if (ma.startsWith("LK")) {
          int id = Integer.parseInt(ma.substring(2));
          maxId = Math.max(maxId, id);
        }
      } catch (Exception e) {
        // Bỏ qua mã không hợp lệ
      }
    }

    return String.format("LK%03d", maxId + 1);
  }

  public boolean validateTimeFormat(String time) {
    try {
      LocalDateTime.parse(time, DATETIME_FORMAT);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private LichKhamDTO requireExisting(String maLichKham) {
    if (isEmpty(maLichKham)) {
      return null;
    }
    return dao.getById(maLichKham);
  }
}
