package phongkham.BUS;

import java.sql.Date;
import java.util.ArrayList;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.dao.HoSoBenhAnDAO;

public class HoSoBenhAnBUS {

  private HoSoBenhAnDAO hsDAO = new HoSoBenhAnDAO();

  // ===== VALIDATION =====

  private String normalizeGender(String gioiTinh) {
    if (gioiTinh == null) {
      return "";
    }
    String raw = gioiTinh.trim();
    if (raw.isEmpty()) {
      return "";
    }
    String upper = raw.toUpperCase();
    if (
      "NAM".equals(upper) ||
      "MALE".equals(upper) ||
      "M".equals(upper) ||
      "1".equals(upper)
    ) {
      return "Nam";
    }
    if (
      "NU".equals(upper) ||
      "NỮ".equals(upper) ||
      "FEMALE".equals(upper) ||
      "F".equals(upper) ||
      "0".equals(upper)
    ) {
      return "Nu";
    }
    return raw;
  }

  // Validate khi đăng ký (thông tin cơ bản)
  private String validateInsert(HoSoBenhAnDTO hs) {
    return validateHoSo(hs, true);
  }

  private String validateUpdate(HoSoBenhAnDTO hs) {
    return validateHoSo(hs, false);
  }

  private String validateHoSo(
    HoSoBenhAnDTO hs,
    boolean checkNgaySinhKhongVuotHienTai
  ) {
    if (hs == null) return "Hồ sơ không được null";
    if (isBlank(hs.getMaHoSo())) {
      return "Mã hồ sơ không được để trống";
    }
    if (isBlank(hs.getHoTen())) {
      return "Họ tên không được để trống";
    }
    if (isBlank(hs.getSoDienThoai())) {
      return "Số điện thoại không được để trống";
    }
    if (!isValidPhoneNumber(hs.getSoDienThoai())) {
      return "Số điện thoại không hợp lệ";
    }

    if (
      checkNgaySinhKhongVuotHienTai &&
      hs.getNgaySinh() != null &&
      hs.getNgaySinh().after(new Date(System.currentTimeMillis()))
    ) {
      return "Ngày sinh không được lớn hơn ngày hiện tại";
    }

    String gioiTinhChuan = normalizeGender(hs.getGioiTinh());
    if (!gioiTinhChuan.isEmpty()) {
      if (!"Nam".equals(gioiTinhChuan) && !"Nu".equals(gioiTinhChuan)) {
        return "Giới tính không hợp lệ";
      }
      hs.setGioiTinh(gioiTinhChuan);
    }

    return null;
  }

  // Kiểm tra trạng thái hợp lệ
  private boolean isValidTrangThai(String trangThai) {
    if (trangThai == null || trangThai.trim().isEmpty()) {
      return false;
    }
    return (
      trangThai.equals("CHO_KHAM") ||
      trangThai.equals("DA_KHAM") ||
      trangThai.equals("HUY")
    );
  }

  // Kiểm tra số điện thoại hợp lệ
  private boolean isValidPhoneNumber(String sdt) {
    if (sdt == null) return false;
    // Số điện thoại Việt Nam: 10 số, bắt đầu bằng 0
    return sdt.matches("^0\\d{9}$");
  }

  private boolean isValidCCCD(String cccd) {
    if (cccd == null) return false;
    return cccd.trim().matches("^\\d{9,12}$");
  }

  //Lấy tất cả hồ sơ
  public ArrayList<HoSoBenhAnDTO> getAll() {
    return hsDAO.getAll();
  }

  public HoSoBenhAnDTO getByMaHoSo(String MaHS) {
    if (isBlank(MaHS)) {
      System.out.println("Không được rỗng dữ liệu");
      return null;
    }
    return hsDAO.getByMaHoSo(MaHS);
  }

  public boolean dangKyBenhNhan(HoSoBenhAnDTO hs) {
    String valid = validateInsert(hs);
    if (valid != null) return false;
    return hsDAO.insert(hs);
  }

  public boolean updateHoSo(HoSoBenhAnDTO hs) {
    String valid = validateUpdate(hs);
    if (valid != null) return false;
    return hsDAO.update(hs);
  }

  // Cập nhật kết quả khám (bác sĩ sử dụng)
  public boolean capNhatKetQuaKham(
    String maHoSo,
    String maBacSi,
    String trieuChung,
    String chanDoan,
    String ketLuan,
    String loiDan
  ) {
    // Validate
    if (isBlank(maHoSo)) {
      System.err.println("Mã hồ sơ không được để trống");
      return false;
    }
    if (isBlank(maBacSi)) {
      System.err.println("Mã bác sĩ không được để trống");
      return false;
    }
    if (isBlank(chanDoan)) {
      System.err.println("Chẩn đoán không được để trống");
      return false;
    }

    return hsDAO.updateKetQuaKham(
      maHoSo,
      trieuChung,
      chanDoan,
      ketLuan,
      loiDan,
      maBacSi
    );
  }

  public boolean updateTrangThai(String MaHS, String trangThai) {
    if (isBlank(MaHS)) {
      System.out.println("Mã hồ sơ k đc trống");
      return false;
    }
    if (!isValidTrangThai(trangThai)) {
      System.out.println("Trạng thái không hợp lệ!");
      return false;
    }
    return hsDAO.updateTrangThai(MaHS, trangThai);
  }

  public boolean delete(String maHS) {
    if (isBlank(maHS)) {
      System.out.println("Mã hồ sơ không được rỗng");
      return false;
    }
    return hsDAO.delete(maHS);
  }

  //các hàm tìm kiếm, lọc
  public HoSoBenhAnDTO getByMaLichKham(String MaLichKham) {
    if (isBlank(MaLichKham)) {
      return null;
    }
    return hsDAO.getByMaLichKham(MaLichKham);
  }

  public ArrayList<HoSoBenhAnDTO> getBySDT(String sdt) {
    if (isBlank(sdt)) {
      System.out.println("Số điện thoại không được rỗng");
      return null;
    }
    boolean valid = isValidPhoneNumber(sdt);
    if (valid) {
      ArrayList<HoSoBenhAnDTO> list = hsDAO.getBySoDienThoai(sdt);
      return list;
    }
    return null;
  }

  public ArrayList<HoSoBenhAnDTO> getByCCCD(String cccd) {
    if (isBlank(cccd)) {
      System.out.println("CCCD không được rỗng");
      return null;
    }
    if (!isValidCCCD(cccd)) {
      System.out.println("CCCD không hợp lệ");
      return null;
    }
    return hsDAO.getByCCCD(cccd.trim());
  }

  // Lấy hồ sơ theo trạng thái
  public ArrayList<HoSoBenhAnDTO> getByTrangThai(String trangThai) {
    if (!isValidTrangThai(trangThai)) {
      System.out.println("Trạng thái không hợp lệ: " + trangThai);
      return new ArrayList<>();
    }
    return hsDAO.getByTrangThai(trangThai);
  }

  // Alias cho getByMaHoSo để phù hợp với cách gọi trong GUI
  public HoSoBenhAnDTO getById(String maHS) {
    return getByMaHoSo(maHS);
  }

  // Cập nhật toàn bộ hồ sơ
  public boolean update(HoSoBenhAnDTO hs) {
    String valid = validateUpdate(hs);
    if (valid != null) {
      System.out.println("Validation error: " + valid);
      return false;
    }
    return hsDAO.update(hs);
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
