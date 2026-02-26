package phongkham.example;

import java.sql.Date;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.dao.HoSoBenhAnDAO;

/**
 * VÍ DỤ SỬ DỤNG HỒ SƠ BỆNH ÁN MỚI
 * Luồng: Đăng ký → Đặt lịch → Khám bệnh → Cập nhật kết quả
 */
public class HoSoBenhAnExample {

  private HoSoBenhAnDAO hoSoDAO = new HoSoBenhAnDAO();

  // ========================================
  // BƯỚC 1: BỆNH NHÂN ĐĂNG KÝ (Lễ tân/Bệnh nhân)
  // ========================================
  public String dangKyBenhNhan(
    String hoTen,
    String sdt,
    String cccd,
    Date ngaySinh,
    String gioiTinh,
    String diaChi
  ) {
    // Tạo hồ sơ mới với THÔNG TIN CÁ NHÂN
    HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
    hs.setMaHoSo(generateMaHoSo()); // Auto-generate
    hs.setHoTen(hoTen);
    hs.setSoDienThoai(sdt);
    hs.setCCCD(cccd);
    hs.setNgaySinh(ngaySinh);
    hs.setGioiTinh(gioiTinh);
    hs.setDiaChi(diaChi);

    // ❌ KHÔNG nhập các thông tin khám bệnh
    // ChanDoan, KetLuan, LoiDan = null
    hs.setTrangThai("CHO_KHAM"); // Mặc định chờ khám

    if (hoSoDAO.insert(hs)) {
      System.out.println("✅ Đăng ký thành công! Mã hồ sơ: " + hs.getMaHoSo());
      return hs.getMaHoSo();
    }

    System.out.println("❌ Đăng ký thất bại!");
    return null;
  }

  // ========================================
  // BƯỚC 2: ĐẶT LỊCH KHÁM (Optional - có thể khám walk-in)
  // ========================================
  public boolean datLichKham(String maHoSo, String maLichKham) {
    // Cập nhật MaLichKham vào hồ sơ
    HoSoBenhAnDTO hs = hoSoDAO.getByMaHoSo(maHoSo);
    if (hs != null) {
      hs.setMaLichKham(maLichKham);
      if (hoSoDAO.update(hs)) {
        System.out.println("✅ Đặt lịch thành công!");
        return true;
      }
    }

    System.out.println("❌ Đặt lịch thất bại!");
    return false;
  }

  // ========================================
  // BƯỚC 3: BÁC SĨ KHÁM BỆNH VÀ LƯU KẾT QUẢ
  // ========================================
  public boolean capNhatKetQuaKham(
    String maHoSo,
    String maBacSi,
    String trieuChung,
    String chanDoan,
    String ketLuan,
    String loiDan
  ) {
    // Sử dụng method updateKetQuaKham() - tự động set TrangThai = DA_KHAM
    if (
      hoSoDAO.updateKetQuaKham(
        maHoSo,
        trieuChung,
        chanDoan,
        ketLuan,
        loiDan,
        maBacSi
      )
    ) {
      System.out.println("✅ Lưu kết quả khám thành công!");
      return true;
    }

    System.out.println("❌ Lưu kết quả khám thất bại!");
    return false;
  }

  // ========================================
  // TRA CỨU HỒ SƠ
  // ========================================

  // Tìm theo số điện thoại (lịch sử khám)
  public void timTheoSDT(String sdt) {
    var list = hoSoDAO.getBySoDienThoai(sdt);
    System.out.println("\n📋 Lịch sử khám của SĐT: " + sdt);
    for (HoSoBenhAnDTO hs : list) {
      System.out.println(
        "  - " +
          hs.getMaHoSo() +
          ": " +
          hs.getChanDoan() +
          " (" +
          hs.getTrangThai() +
          ")"
      );
    }
  }

  // Xem danh sách chờ khám
  public void danhSachChoKham() {
    var list = hoSoDAO.getByTrangThai("CHO_KHAM");
    System.out.println(
      "\n⏳ Danh sách chờ khám: " + list.size() + " bệnh nhân"
    );
    for (HoSoBenhAnDTO hs : list) {
      System.out.println(
        "  - " +
          hs.getHoTen() +
          " (" +
          hs.getSoDienThoai() +
          ") - " +
          hs.getMaHoSo()
      );
    }
  }

  // Xem danh sách đã khám
  public void danhSachDaKham() {
    var list = hoSoDAO.getByTrangThai("DA_KHAM");
    System.out.println("\n✅ Đã khám: " + list.size() + " hồ sơ");
    for (HoSoBenhAnDTO hs : list) {
      System.out.println(
        "  - " +
          hs.getHoTen() +
          ": " +
          hs.getChanDoan() +
          " - " +
          hs.getNgayKham()
      );
    }
  }

  // ========================================
  // HỦY HỒ SƠ / LỊCH KHÁM
  // ========================================
  public boolean huyLichKham(String maHoSo) {
    if (hoSoDAO.updateTrangThai(maHoSo, "HUY")) {
      System.out.println("✅ Đã hủy lịch khám!");
      return true;
    }
    System.out.println("❌ Hủy thất bại!");
    return false;
  }

  // ========================================
  // UTILITY
  // ========================================
  private String generateMaHoSo() {
    // Logic tự động sinh mã (ví dụ: HS + timestamp)
    return "HS" + System.currentTimeMillis();
  }

  // ========================================
  // MAIN - VÍ DỤ SỬ DỤNG
  // ========================================
  public static void main(String[] args) {
    HoSoBenhAnExample example = new HoSoBenhAnExample();

    // 1. Đăng ký bệnh nhân mới
    String maHoSo = example.dangKyBenhNhan(
      "Nguyen Van X",
      "0999888777",
      "001999888777",
      Date.valueOf("1995-06-15"),
      "Nam",
      "123 ABC, Q1, HCM"
    );

    // 2. Đặt lịch (optional)
    if (maHoSo != null) {
      example.datLichKham(maHoSo, "LK999");
    }

    // 3. Bác sĩ khám và lưu kết quả
    if (maHoSo != null) {
      example.capNhatKetQuaKham(
        maHoSo,
        "BS01",
        "Ho, sot",
        "Cam cum",
        "Nghi ngoi 3 ngay",
        "Uong nhieu nuoc"
      );
    }

    // 4. Tra cứu
    example.timTheoSDT("0999888777");
    example.danhSachChoKham();
    example.danhSachDaKham();
  }
}
