package phongkham.gui.khambenh;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.CTDonThuocBUS;
import phongkham.BUS.DonThuocBUS;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.CTDonThuocDTO;
import phongkham.DTO.DonThuocDTO;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.LichKhamDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;

/**
 * Service màn khám bệnh: gom nghiệp vụ để UI dễ đọc hơn.
 */
public class KhamBenhService {

  private final HoSoBenhAnBUS hoSoBUS = new HoSoBenhAnBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final DonThuocBUS donThuocBUS = new DonThuocBUS();
  private final CTDonThuocBUS ctDonThuocBUS = new CTDonThuocBUS();
  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();

  public static class DonThuocRow {

    public final String maThuoc;
    public final int soLuong;
    public final String lieuDung;
    public final String cachDung;

    public DonThuocRow(
      String maThuoc,
      int soLuong,
      String lieuDung,
      String cachDung
    ) {
      this.maThuoc = maThuoc;
      this.soLuong = soLuong;
      this.lieuDung = lieuDung;
      this.cachDung = cachDung;
    }
  }

  public static class LuuKhamResult {

    public final boolean thanhCong;
    public final String message;
    public final String maDonThuoc;

    private LuuKhamResult(
      boolean thanhCong,
      String message,
      String maDonThuoc
    ) {
      this.thanhCong = thanhCong;
      this.message = message;
      this.maDonThuoc = maDonThuoc;
    }

    public static LuuKhamResult success(String message, String maDonThuoc) {
      return new LuuKhamResult(true, message, maDonThuoc);
    }

    public static LuuKhamResult fail(String message) {
      return new LuuKhamResult(false, message, null);
    }
  }

  public ArrayList<HoSoBenhAnDTO> layDanhSachHoSoChoKham() {
    ArrayList<HoSoBenhAnDTO> result = new ArrayList<>();
    ArrayList<HoSoBenhAnDTO> list = hoSoBUS.getAll();

    String currentDoctorId = Session.getCurrentBacSiID();
    for (HoSoBenhAnDTO hs : list) {
      String trangThaiHoSo = StatusNormalizer.normalizeHoSoStatus(
        hs.getTrangThai()
      );
      if (
        StatusNormalizer.DA_KHAM.equals(trangThaiHoSo) ||
        StatusNormalizer.DA_HUY.equals(trangThaiHoSo)
      ) {
        continue;
      }

      if (laHoSoKhacBacSi(currentDoctorId, hs.getMaBacSi())) {
        continue;
      }
      if (!isHoSoHienThiTrongDanhSachChoKham(hs)) {
        continue;
      }
      result.add(hs);
    }

    return result;
  }

  public ArrayList<ThuocDTO> layDanhSachThuoc() {
    return thuocBUS.list();
  }

  public HoSoBenhAnDTO layHoSoById(String maHoSo) {
    return hoSoBUS.getById(maHoSo);
  }

  public LuuKhamResult luuKhamBenh(
    String maHoSo,
    String trieuChung,
    String chanDoan,
    String ketLuan,
    String loiDan,
    List<DonThuocRow> rowsDonThuoc
  ) {
    HoSoBenhAnDTO hs = hoSoBUS.getById(maHoSo);
    if (hs == null) {
      return LuuKhamResult.fail("Không tìm thấy hồ sơ bệnh án!");
    }

    String currentDoctorId = Session.getCurrentBacSiID();
    if (laHoSoKhacBacSi(currentDoctorId, hs.getMaBacSi())) {
      return LuuKhamResult.fail(
        "Bạn không có quyền khám hồ sơ không thuộc lịch của mình!"
      );
    }

    if (!isDoctorKhoaCompatibleWithService(hs, currentDoctorId)) {
      return LuuKhamResult.fail(
        "Bác sĩ chỉ được khám hồ sơ thuộc gói dịch vụ của khoa mình!"
      );
    }

    String maLichKham = hs.getMaLichKham();
    if (maLichKham == null || maLichKham.trim().isEmpty()) {
      return LuuKhamResult.fail("Hồ sơ chưa liên kết lịch khám hợp lệ!");
    }

    LichKhamDTO lichKham = lichKhamBUS.getById(maLichKham);
    if (lichKham == null) {
      return LuuKhamResult.fail("Không tìm thấy lịch khám của hồ sơ này!");
    }

    hs.setTrieuChung(trieuChung);
    hs.setChanDoan(chanDoan);
    hs.setKetLuan(ketLuan);
    hs.setLoiDan(loiDan);
    hs.setTrangThai(StatusNormalizer.DA_KHAM);

    boolean updateHS = hoSoBUS.update(hs);
    if (!updateHS) {
      return LuuKhamResult.fail("Lỗi cập nhật hồ sơ!");
    }

    String maDonThuoc = null;
    if (rowsDonThuoc != null && !rowsDonThuoc.isEmpty()) {
      maDonThuoc = donThuocBUS.generateMaDonThuoc();

      DonThuocDTO donThuoc = new DonThuocDTO();
      donThuoc.setMaDonThuoc(maDonThuoc);
      donThuoc.setMaHoSo(maHoSo);
      donThuoc.setNgayKeDon(LocalDate.now().toString());
      donThuoc.setGhiChu("");

      if (!donThuocBUS.add(donThuoc)) {
        return LuuKhamResult.fail("Lỗi tạo đơn thuốc!");
      }

      int i = 0;
      for (DonThuocRow row : rowsDonThuoc) {
        String maCTDT = "CTDT" + System.currentTimeMillis() + "_" + i;
        CTDonThuocDTO ctDon = new CTDonThuocDTO(
          maCTDT,
          maDonThuoc,
          row.maThuoc,
          row.soLuong,
          row.lieuDung,
          row.cachDung
        );

        if (!ctDonThuocBUS.add(ctDon)) {
          return LuuKhamResult.fail("Lỗi thêm chi tiết đơn thuốc!");
        }
        i++;
      }
    }

    String capNhatLich = lichKhamBUS.updateTrangThai(
      maLichKham,
      StatusNormalizer.HOAN_THANH
    );
    if (!capNhatLich.contains("thành công")) {
      return LuuKhamResult.fail(
        "Đã lưu hồ sơ nhưng lỗi chuyển trạng thái lịch khám!"
      );
    }

    String message = "✅ Lưu khám bệnh thành công!";
    if (maDonThuoc != null) {
      message += "\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━";
      message += "\n📋 MÃ ĐƠN THUỐC: " + maDonThuoc;
      message += "\n━━━━━━━━━━━━━━━━━━━━━━━━━━";
      message += "\n\n📢 Vui lòng ra quầy thuốc để:";
      message += "\n   • Xuất trình mã đơn thuốc";
      message += "\n   • Lấy thuốc theo đơn";
      message += "\n   • Thanh toán bằng TIỀN MẶT";
    }

    return LuuKhamResult.success(message, maDonThuoc);
  }

  public String taoNoiDungHoSoPdf(HoSoBenhAnDTO hs) {
    return String.format(
      "=== HỒ SƠ BỆNH ÁN ===\n\n" +
        "--- THÔNG TIN BỆNH NHÂN ---\n" +
        "Mã hồ sơ: %s\n" +
        "Họ tên: %s\n" +
        "SĐT: %s\n" +
        "CCCD: %s\n" +
        "Ngày sinh: %s\n" +
        "Giới tính: %s\n" +
        "Địa chỉ: %s\n\n" +
        "--- KẾT QUẢ KHÁM ---\n" +
        "Triệu chứng: %s\n" +
        "Chẩn đoán: %s\n" +
        "Kết luận: %s\n" +
        "Lời dặn: %s\n" +
        "Trạng thái: %s\n",
      hs.getMaHoSo(),
      hs.getHoTen(),
      hs.getSoDienThoai(),
      hs.getCCCD() == null ? "" : hs.getCCCD(),
      hs.getNgaySinh() == null ? "" : hs.getNgaySinh().toString(),
      hs.getGioiTinh(),
      hs.getDiaChi(),
      hs.getTrieuChung() == null ? "" : hs.getTrieuChung(),
      hs.getChanDoan() == null ? "" : hs.getChanDoan(),
      hs.getKetLuan() == null ? "" : hs.getKetLuan(),
      hs.getLoiDan() == null ? "" : hs.getLoiDan(),
      StatusNormalizer.normalizeHoSoStatus(hs.getTrangThai())
    );
  }

  private boolean laHoSoKhacBacSi(
    String currentDoctorId,
    String maBacSiCuaHoSo
  ) {
    if (currentDoctorId == null || currentDoctorId.trim().isEmpty()) {
      return false;
    }
    if (maBacSiCuaHoSo == null || maBacSiCuaHoSo.trim().isEmpty()) {
      return false;
    }
    return !isSameDoctorId(currentDoctorId, maBacSiCuaHoSo);
  }

  private boolean isSameDoctorId(String a, String b) {
    String left = a == null ? "" : a.trim().toUpperCase();
    String right = b == null ? "" : b.trim().toUpperCase();
    if (left.isEmpty() || right.isEmpty()) {
      return false;
    }
    if (left.equals(right)) {
      return true;
    }

    String digitsLeft = left.replaceAll("\\D+", "");
    String digitsRight = right.replaceAll("\\D+", "");
    if (!digitsLeft.isEmpty() && !digitsRight.isEmpty()) {
      try {
        return Integer.parseInt(digitsLeft) == Integer.parseInt(digitsRight);
      } catch (NumberFormatException ignored) {}
    }

    return false;
  }

  private boolean isDoctorKhoaCompatibleWithService(
    HoSoBenhAnDTO hs,
    String currentDoctorId
  ) {
    if (
      hs == null ||
      hs.getMaLichKham() == null ||
      hs.getMaLichKham().trim().isEmpty() ||
      currentDoctorId == null ||
      currentDoctorId.trim().isEmpty()
    ) {
      return true;
    }

    GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
    BacSiBUS bacSiBUS = new BacSiBUS();

    LichKhamDTO lichKham = lichKhamBUS.getById(hs.getMaLichKham());
    if (
      lichKham == null ||
      lichKham.getMaGoi() == null ||
      lichKham.getMaGoi().trim().isEmpty()
    ) {
      return true;
    }

    GoiDichVuDTO goiDichVu = goiDichVuBUS.getByMaGoi(lichKham.getMaGoi());
    BacSiDTO bacSi = bacSiBUS.getById(currentDoctorId);
    if (goiDichVu == null || bacSi == null) {
      return true;
    }

    if (goiDichVu.getMaKhoa() == null || bacSi.getMaKhoa() == null) {
      return true;
    }

    return goiDichVu
      .getMaKhoa()
      .trim()
      .equalsIgnoreCase(bacSi.getMaKhoa().trim());
  }

  private boolean isHoSoHienThiTrongDanhSachChoKham(HoSoBenhAnDTO hs) {
    if (
      hs == null ||
      hs.getMaLichKham() == null ||
      hs.getMaLichKham().trim().isEmpty()
    ) {
      return false;
    }

    LichKhamDTO lichKham = lichKhamBUS.getById(hs.getMaLichKham());
    if (lichKham == null) {
      return false;
    }

    String trangThai = StatusNormalizer.normalizeLichKhamStatus(
      lichKham.getTrangThai()
    );
    return (
      StatusNormalizer.DA_XAC_NHAN.equals(trangThai) ||
      StatusNormalizer.DANG_KHAM.equals(trangThai)
    );
  }
}
