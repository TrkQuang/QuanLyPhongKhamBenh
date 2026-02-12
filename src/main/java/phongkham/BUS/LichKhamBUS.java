package phongkham.BUS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import phongkham.DTO.LichKhamDTO;
import phongkham.dao.LichKhamDAO;

public class LichKhamBUS {

  private LichKhamDAO lichKhamDAO = new LichKhamDAO();

  // ================= LẤY TẤT CẢ LỊCH KHÁM =================
  public ArrayList<LichKhamDTO> getAll() {
    return lichKhamDAO.getAll();
  }

  // ================= THÊM LỊCH KHÁM =================
  public String insert(LichKhamDTO lk) {
    // Validate dữ liệu không được để trống
    if (lk.getMaLichKham() == null || lk.getMaLichKham().trim().isEmpty()) {
      return "Mã lịch khám không được để trống";
    }
    if (lk.getMaBacSi() == null || lk.getMaBacSi().trim().isEmpty()) {
      return "Mã bác sĩ không được để trống";
    }
    if (
      lk.getThoiGianBatDau() == null || lk.getThoiGianBatDau().trim().isEmpty()
    ) {
      return "Thời gian bắt đầu không được để trống";
    }
    if (
      lk.getThoiGianKetThuc() == null ||
      lk.getThoiGianKetThuc().trim().isEmpty()
    ) {
      return "Thời gian kết thúc không được để trống";
    }
    if (lk.getTrangThai() == null || lk.getTrangThai().trim().isEmpty()) {
      return "Trạng thái không được để trống";
    }

    // Kiểm tra mã lịch khám đã tồn tại
    if (lichKhamDAO.getById(lk.getMaLichKham()) != null) {
      return "Mã lịch khám đã tồn tại";
    }

    // Validate thời gian
    if (lk.getThoiGianBatDau().compareTo(lk.getThoiGianKetThuc()) >= 0) {
      return "Thời gian kết thúc phải lớn hơn thời gian bắt đầu";
    }

    // Kiểm tra trùng lịch bác sĩ
    if (
      lichKhamDAO.checkTrungLich(
        lk.getMaBacSi(),
        lk.getThoiGianBatDau(),
        lk.getThoiGianKetThuc()
      )
    ) {
      return "Bác sĩ đã có lịch khám trùng với khung giờ này";
    }

    // Validate trạng thái hợp lệ
    String trangThai = lk.getTrangThai();
    if (
      !trangThai.equals("Đã đặt") &&
      !trangThai.equals("Đang khám") &&
      !trangThai.equals("Hoàn thành") &&
      !trangThai.equals("Đã hủy")
    ) {
      return "Trạng thái không hợp lệ (Đã đặt/Đang khám/Hoàn thành/Đã hủy)";
    }

    // Thực hiện thêm
    boolean result = lichKhamDAO.insert(lk);
    if (result) {
      return "Thêm lịch khám thành công";
    }
    return "Thêm lịch khám thất bại";
  }

  // ================= CẬP NHẬT LỊCH KHÁM =================
  public String update(LichKhamDTO lk) {
    // Validate dữ liệu không được để trống
    if (lk.getMaLichKham() == null || lk.getMaLichKham().trim().isEmpty()) {
      return "Mã lịch khám không được để trống";
    }
    if (lk.getMaBacSi() == null || lk.getMaBacSi().trim().isEmpty()) {
      return "Mã bác sĩ không được để trống";
    }
    if (
      lk.getThoiGianBatDau() == null || lk.getThoiGianBatDau().trim().isEmpty()
    ) {
      return "Thời gian bắt đầu không được để trống";
    }
    if (
      lk.getThoiGianKetThuc() == null ||
      lk.getThoiGianKetThuc().trim().isEmpty()
    ) {
      return "Thời gian kết thúc không được để trống";
    }
    if (lk.getTrangThai() == null || lk.getTrangThai().trim().isEmpty()) {
      return "Trạng thái không được để trống";
    }

    // Kiểm tra lịch khám có tồn tại không
    if (lichKhamDAO.getById(lk.getMaLichKham()) == null) {
      return "Lịch khám không tồn tại";
    }

    // Validate thời gian
    if (lk.getThoiGianBatDau().compareTo(lk.getThoiGianKetThuc()) >= 0) {
      return "Thời gian kết thúc phải lớn hơn thời gian bắt đầu";
    }

    // Kiểm tra trùng lịch bác sĩ khi cập nhật
    if (
      lichKhamDAO.checkTrungLichWhenUpdate(
        lk.getMaLichKham(),
        lk.getMaBacSi(),
        lk.getThoiGianBatDau(),
        lk.getThoiGianKetThuc()
      )
    ) {
      return "Bác sĩ đã có lịch khám trùng với khung giờ này";
    }

    // Validate trạng thái hợp lệ
    String trangThai = lk.getTrangThai();
    if (
      !trangThai.equals("Đã đặt") &&
      !trangThai.equals("Đang khám") &&
      !trangThai.equals("Hoàn thành") &&
      !trangThai.equals("Đã hủy")
    ) {
      return "Trạng thái không hợp lệ (Đã đặt/Đang khám/Hoàn thành/Đã hủy)";
    }

    // Thực hiện cập nhật
    boolean result = lichKhamDAO.update(lk);
    if (result) {
      return "Cập nhật lịch khám thành công";
    }
    return "Cập nhật lịch khám thất bại";
  }

  // ================= XÓA LỊCH KHÁM =================
  public String delete(String maLichKham) {
    if (maLichKham == null || maLichKham.trim().isEmpty()) {
      return "Mã lịch khám không được để trống";
    }

    if (lichKhamDAO.getById(maLichKham) == null) {
      return "Lịch khám không tồn tại";
    }

    // Kiểm tra xem lịch khám đã hoàn thành chưa
    LichKhamDTO lk = lichKhamDAO.getById(maLichKham);
    if (lk.getTrangThai().equals("Hoàn thành")) {
      return "Không thể xóa lịch khám đã hoàn thành";
    }

    boolean result = lichKhamDAO.delete(maLichKham);
    if (result) {
      return "Xóa lịch khám thành công";
    }
    return "Xóa lịch khám thất bại";
  }

  // ================= HỦY LỊCH KHÁM (SOFT DELETE) =================
  public String huyLichKham(String maLichKham) {
    if (maLichKham == null || maLichKham.trim().isEmpty()) {
      return "Mã lịch khám không được để trống";
    }

    LichKhamDTO lk = lichKhamDAO.getById(maLichKham);
    if (lk == null) {
      return "Lịch khám không tồn tại";
    }

    if (lk.getTrangThai().equals("Hoàn thành")) {
      return "Không thể hủy lịch khám đã hoàn thành";
    }

    if (lk.getTrangThai().equals("Đã hủy")) {
      return "Lịch khám đã được hủy trước đó";
    }

    boolean result = lichKhamDAO.updateTrangThai(maLichKham, "Đã hủy");
    if (result) {
      return "Hủy lịch khám thành công";
    }
    return "Hủy lịch khám thất bại";
  }

  // ================= CẬP NHẬT TRẠNG THÁI =================
  public String updateTrangThai(String maLichKham, String trangThai) {
    if (maLichKham == null || maLichKham.trim().isEmpty()) {
      return "Mã lịch khám không được để trống";
    }
    if (trangThai == null || trangThai.trim().isEmpty()) {
      return "Trạng thái không được để trống";
    }

    // Validate trạng thái hợp lệ
    if (
      !trangThai.equals("Đã đặt") &&
      !trangThai.equals("Đang khám") &&
      !trangThai.equals("Hoàn thành") &&
      !trangThai.equals("Đã hủy")
    ) {
      return "Trạng thái không hợp lệ (Đã đặt/Đang khám/Hoàn thành/Đã hủy)";
    }

    if (lichKhamDAO.getById(maLichKham) == null) {
      return "Lịch khám không tồn tại";
    }

    boolean result = lichKhamDAO.updateTrangThai(maLichKham, trangThai);
    if (result) {
      return "Cập nhật trạng thái thành công";
    }
    return "Cập nhật trạng thái thất bại";
  }

  // ================= TÌM THEO MÃ =================
  public LichKhamDTO getById(String ma) {
    if (ma == null || ma.trim().isEmpty()) {
      return null;
    }
    return lichKhamDAO.getById(ma);
  }

  // ================= TÌM THEO BÁC SĨ =================
  public ArrayList<LichKhamDTO> getByMaBacSi(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return lichKhamDAO.getByMaBacSi(maBacSi);
  }

  // ================= TÌM THEO GÓI DỊCH VỤ =================
  public ArrayList<LichKhamDTO> getByMaGoi(String maGoi) {
    if (maGoi == null || maGoi.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return lichKhamDAO.getByMaGoi(maGoi);
  }

  // ================= TÌM THEO MÃ ĐỊNH DANH =================
  public ArrayList<LichKhamDTO> getByMaDinhDanhTam(String MaDinhDanhTam) {
    if (MaDinhDanhTam == null || MaDinhDanhTam.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return lichKhamDAO.getByMaDinhDanhTam(MaDinhDanhTam);
  }

  // ================= TÌM THEO TRẠNG THÁI =================
  public ArrayList<LichKhamDTO> getByTrangThai(String trangThai) {
    if (trangThai == null || trangThai.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return lichKhamDAO.getByTrangThai(trangThai);
  }

  // ================= TÌM THEO NGÀY =================
  public ArrayList<LichKhamDTO> getByNgay(String ngay) {
    if (ngay == null || ngay.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return lichKhamDAO.getByNgay(ngay);
  }

  // ================= TÌM THEO BÁC SĨ VÀ NGÀY =================
  public ArrayList<LichKhamDTO> getByBacSiAndNgay(String maBacSi, String ngay) {
    if (
      maBacSi == null ||
      maBacSi.trim().isEmpty() ||
      ngay == null ||
      ngay.trim().isEmpty()
    ) {
      return new ArrayList<>();
    }
    return lichKhamDAO.getByBacSiAndNgay(maBacSi, ngay);
  }

  // ================= TÌM THEO KHOẢNG THỜI GIAN =================
  public ArrayList<LichKhamDTO> getByKhoangThoiGian(
    String tuNgay,
    String denNgay
  ) {
    if (
      tuNgay == null ||
      tuNgay.trim().isEmpty() ||
      denNgay == null ||
      denNgay.trim().isEmpty()
    ) {
      return new ArrayList<>();
    }

    if (tuNgay.compareTo(denNgay) > 0) {
      return new ArrayList<>();
    }

    return lichKhamDAO.getByKhoangThoiGian(tuNgay, denNgay);
  }

  // ================= ĐẾM LỊCH KHÁM THEO TRẠNG THÁI =================
  public int countByTrangThai(String trangThai) {
    if (trangThai == null || trangThai.trim().isEmpty()) {
      return 0;
    }
    return lichKhamDAO.countByTrangThai(trangThai);
  }

  // ================= TÌM KIẾM THEO NHIỀU TIÊU CHÍ =================
  public ArrayList<LichKhamDTO> search(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return lichKhamDAO.getAll();
    }
    return lichKhamDAO.search(keyword);
  }

  // ================= KIỂM TRA TRÙNG LỊCH =================
  public boolean kiemTraTrungLich(
    String maBacSi,
    String thoiGianBatDau,
    String thoiGianKetThuc
  ) {
    if (maBacSi == null || thoiGianBatDau == null || thoiGianKetThuc == null) {
      return false;
    }
    return lichKhamDAO.checkTrungLich(maBacSi, thoiGianBatDau, thoiGianKetThuc);
  }

  // ================= LẤY LỊCH KHÁM SẮP TỚI CỦA BÁC SĨ =================
  public ArrayList<LichKhamDTO> getLichKhamSapToi(String maBacSi) {
    ArrayList<LichKhamDTO> dsAll = lichKhamDAO.getByMaBacSi(maBacSi);
    ArrayList<LichKhamDTO> dsSapToi = new ArrayList<>();

    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss"
      );
      LocalDateTime now = LocalDateTime.now();

      for (LichKhamDTO lk : dsAll) {
        LocalDateTime tgBatDau = LocalDateTime.parse(
          lk.getThoiGianBatDau(),
          formatter
        );
        if (tgBatDau.isAfter(now) && !lk.getTrangThai().equals("Đã hủy")) {
          dsSapToi.add(lk);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return dsSapToi;
  }

  // ================= LẤY LỊCH KHÁM HÔM NAY =================
  public ArrayList<LichKhamDTO> getLichKhamHomNay() {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String homNay = LocalDateTime.now().format(formatter);
      return lichKhamDAO.getByNgay(homNay);
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // ================= THỐNG KÊ LỊCH KHÁM =================
  public String thongKeLichKham() {
    int soDaDat = countByTrangThai("Đã đặt");
    int soDangKham = countByTrangThai("Đang khám");
    int soHoanThanh = countByTrangThai("Hoàn thành");
    int soDaHuy = countByTrangThai("Đã hủy");
    int tongSo = soDaDat + soDangKham + soHoanThanh + soDaHuy;

    StringBuilder sb = new StringBuilder();
    sb.append("=== THỐNG KÊ LỊCH KHÁM ===\n");
    sb.append("Tổng số lịch khám: ").append(tongSo).append("\n");
    sb.append("Đã đặt: ").append(soDaDat).append("\n");
    sb.append("Đang khám: ").append(soDangKham).append("\n");
    sb.append("Hoàn thành: ").append(soHoanThanh).append("\n");
    sb.append("Đã hủy: ").append(soDaHuy).append("\n");

    return sb.toString();
  }

  // ================= TẠO MÃ LỊCH KHÁM TỰ ĐỘNG =================
  public String generateMaLichKham() {
    ArrayList<LichKhamDTO> dsAll = lichKhamDAO.getAll();
    int maxId = 0;

    for (LichKhamDTO lk : dsAll) {
      try {
        String ma = lk.getMaLichKham();
        if (ma.startsWith("LK")) {
          int id = Integer.parseInt(ma.substring(2));
          if (id > maxId) {
            maxId = id;
          }
        }
      } catch (Exception e) {
        // Bỏ qua các mã không đúng định dạng
      }
    }

    return String.format("LK%03d", maxId + 1);
  }

  // ================= VALIDATE FORMAT THỜI GIAN =================
  public boolean validateTimeFormat(String time) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss"
      );
      LocalDateTime.parse(time, formatter);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
