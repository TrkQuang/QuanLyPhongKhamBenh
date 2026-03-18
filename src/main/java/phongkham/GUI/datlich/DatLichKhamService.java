package phongkham.gui.datlich;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.DTO.LichKhamDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.db.DBConnection;

/**
 * Service màn đặt lịch khám: gom nghiệp vụ để panel chỉ xử lý UI.
 */
public class DatLichKhamService {

  private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();
  private final LichLamViecBUS lichLamViecBUS = new LichLamViecBUS();

  public static class DangKyInput {

    public final String hoTen;
    public final String soDienThoai;
    public final String cccd;
    public final String diaChi;
    public final Date ngaySinh;
    public final Date ngayKham;
    public final String gioiTinh;
    public final String maGoi;
    public final String maBacSi;
    public final String lichKhamSlot;
    public final String phuongThucThanhToan;

    public DangKyInput(
      String hoTen,
      String soDienThoai,
      String cccd,
      String diaChi,
      Date ngaySinh,
      Date ngayKham,
      String gioiTinh,
      String maGoi,
      String maBacSi,
      String lichKhamSlot,
      String phuongThucThanhToan
    ) {
      this.hoTen = hoTen;
      this.soDienThoai = soDienThoai;
      this.cccd = cccd;
      this.diaChi = diaChi;
      this.ngaySinh = ngaySinh;
      this.ngayKham = ngayKham;
      this.gioiTinh = gioiTinh;
      this.maGoi = maGoi;
      this.maBacSi = maBacSi;
      this.lichKhamSlot = lichKhamSlot;
      this.phuongThucThanhToan = phuongThucThanhToan;
    }
  }

  public static class DangKyResult {

    public final boolean thanhCong;
    public final String message;
    public final HoSoBenhAnDTO hoSo;
    public final LichKhamDTO lichKham;
    public final GoiDichVuDTO goiDichVu;
    public final HoaDonKhamDTO hoaDon;

    private DangKyResult(
      boolean thanhCong,
      String message,
      HoSoBenhAnDTO hoSo,
      LichKhamDTO lichKham,
      GoiDichVuDTO goiDichVu,
      HoaDonKhamDTO hoaDon
    ) {
      this.thanhCong = thanhCong;
      this.message = message;
      this.hoSo = hoSo;
      this.lichKham = lichKham;
      this.goiDichVu = goiDichVu;
      this.hoaDon = hoaDon;
    }

    public static DangKyResult fail(String message) {
      return new DangKyResult(false, message, null, null, null, null);
    }

    public static DangKyResult success(
      HoSoBenhAnDTO hoSo,
      LichKhamDTO lichKham,
      GoiDichVuDTO goiDichVu,
      HoaDonKhamDTO hoaDon
    ) {
      return new DangKyResult(true, "OK", hoSo, lichKham, goiDichVu, hoaDon);
    }
  }

  public ArrayList<GoiDichVuDTO> layDanhSachGoiDichVu() {
    return goiDichVuBUS.getAll();
  }

  public GoiDichVuDTO layGoiDichVuTheoMa(String maGoi) {
    return goiDichVuBUS.getByMaGoi(maGoi);
  }

  public ArrayList<BacSiDTO> layBacSiTheoKhoa(String maKhoa) {
    if (maKhoa == null || maKhoa.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return bacSiBUS.getByKhoa(maKhoa);
  }

  public ArrayList<BacSiDTO> layBacSiTheoKhoaVaLich(
    String maKhoa,
    Date selectedNgayKham,
    String selectedSlot
  ) {
    ArrayList<BacSiDTO> result = new ArrayList<>();
    if (maKhoa == null || maKhoa.trim().isEmpty()) {
      return result;
    }

    if (
      selectedNgayKham == null ||
      selectedSlot == null ||
      selectedSlot.trim().isEmpty()
    ) {
      return layBacSiTheoKhoa(maKhoa);
    }

    String ngayKham = new SimpleDateFormat("yyyy-MM-dd").format(
      selectedNgayKham
    );
    String gioBatDau = selectedSlot.split(" - ")[0].trim();
    String gioKetThuc = selectedSlot.split(" - ")[1].trim();

    ArrayList<BacSiDTO> danhSachTheoKhoa = bacSiBUS.getByKhoa(maKhoa);
    if (danhSachTheoKhoa == null || danhSachTheoKhoa.isEmpty()) {
      return result;
    }

    for (BacSiDTO bs : danhSachTheoKhoa) {
      String maBacSi = bs.getMaBacSi();
      if (!bacSiCoLichLamViec(maBacSi, ngayKham, gioBatDau)) {
        continue;
      }

      String thoiGianBatDau = ngayKham + " " + gioBatDau + ":00";
      String thoiGianKetThuc = ngayKham + " " + gioKetThuc + ":00";
      if (
        lichKhamBUS.kiemTraTrungLich(maBacSi, thoiGianBatDau, thoiGianKetThuc)
      ) {
        continue;
      }

      result.add(bs);
    }

    return result;
  }

  public DangKyResult dangKyKham(DangKyInput input) {
    GoiDichVuDTO goiDV = goiDichVuBUS.getByMaGoi(input.maGoi);
    if (goiDV == null) {
      return DangKyResult.fail("Không tìm thấy gói dịch vụ");
    }

    if (input.maBacSi == null || input.maBacSi.trim().isEmpty()) {
      return DangKyResult.fail("Vui lòng chọn bác sĩ");
    }

    String ngayKham = new SimpleDateFormat("yyyy-MM-dd").format(input.ngayKham);
    String[] gio = input.lichKhamSlot.split(" - ");
    String gioBatDau = gio[0].trim();
    String gioKetThuc = gio[1].trim();

    if (!bacSiCoLichLamViec(input.maBacSi, ngayKham, gioBatDau)) {
      return DangKyResult.fail(
        "Bác sĩ chưa có lịch làm việc đã duyệt ở khung giờ này"
      );
    }

    String thoiGianBatDau = ngayKham + " " + gioBatDau + ":00";
    String thoiGianKetThuc = ngayKham + " " + gioKetThuc + ":00";
    if (
      lichKhamBUS.kiemTraTrungLich(
        input.maBacSi,
        thoiGianBatDau,
        thoiGianKetThuc
      )
    ) {
      return DangKyResult.fail(
        "Bác sĩ đã có lịch khám trùng với khung giờ này"
      );
    }

    String maHoSo = generateMaHoSo();
    String maLichKham = lichKhamBUS.generateMaLichKham();

    LichKhamDTO lichKham = new LichKhamDTO();
    lichKham.setMaLichKham(maLichKham);
    lichKham.setMaBacSi(input.maBacSi);
    lichKham.setMaGoi(input.maGoi);
    lichKham.setMaDinhDanhTam(maHoSo);
    lichKham.setThoiGianBatDau(thoiGianBatDau);
    lichKham.setThoiGianKetThuc(thoiGianKetThuc);
    lichKham.setTrangThai("CHO_XAC_NHAN");

    HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
    hs.setMaHoSo(maHoSo);
    hs.setMaLichKham(maLichKham);
    hs.setMaBacSi(input.maBacSi);
    hs.setHoTen(input.hoTen);
    hs.setSoDienThoai(input.soDienThoai);
    hs.setGioiTinh(input.gioiTinh);
    hs.setDiaChi(input.diaChi);
    hs.setTrangThai("CHO_KHAM");
    hs.setCCCD(input.cccd);
    hs.setNgaySinh(new java.sql.Date(input.ngaySinh.getTime()));
    hs.setNgayKham(new java.sql.Date(input.ngayKham.getTime()));

    HoaDonKhamDTO hd = new HoaDonKhamDTO();
    hd.setMaHoaDonKham("HDK" + System.currentTimeMillis());
    hd.setTongTien(goiDV.getGiaDichVu());
    hd.setMaHoSo(maHoSo);
    hd.setMaGoi(input.maGoi);
    hd.setHinhThucThanhToan(input.phuongThucThanhToan);
    if (laChuyenKhoan(input.phuongThucThanhToan)) {
      hd.setTrangThai("DA_THANH_TOAN");
      hd.setNgayThanhToan(LocalDateTime.now());
    } else {
      hd.setTrangThai("CHUA_THANH_TOAN");
      hd.setNgayThanhToan(null);
    }

    if (!saveDangKyTransaction(lichKham, hs, hd)) {
      return DangKyResult.fail("Lỗi khi lưu đăng ký khám");
    }

    return DangKyResult.success(hs, lichKham, goiDV, hd);
  }

  private boolean saveDangKyTransaction(
    LichKhamDTO lichKham,
    HoSoBenhAnDTO hs,
    HoaDonKhamDTO hd
  ) {
    String insertLichKhamSql =
      "INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, ThoiGianKetThuc, TrangThai, MaDinhDanhTam) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String insertHoSoSql =
      "INSERT INTO HoSoBenhAn (MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi, NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String insertHoaDonSql =
      "INSERT INTO HoaDonKham (MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection()) {
      conn.setAutoCommit(false);
      try (
        PreparedStatement psLich = conn.prepareStatement(insertLichKhamSql);
        PreparedStatement psHoSo = conn.prepareStatement(insertHoSoSql);
        PreparedStatement psHoaDon = conn.prepareStatement(insertHoaDonSql)
      ) {
        psLich.setString(1, lichKham.getMaLichKham());
        psLich.setString(2, lichKham.getMaGoi());
        psLich.setString(3, lichKham.getMaBacSi());
        psLich.setString(4, lichKham.getThoiGianBatDau());
        psLich.setString(5, lichKham.getThoiGianKetThuc());
        psLich.setString(6, lichKham.getTrangThai());
        psLich.setString(7, lichKham.getMaDinhDanhTam());
        psLich.executeUpdate();

        psHoSo.setString(1, hs.getMaHoSo());
        psHoSo.setString(2, hs.getMaLichKham());
        psHoSo.setString(3, hs.getHoTen());
        psHoSo.setString(4, hs.getSoDienThoai());
        psHoSo.setString(5, hs.getCCCD());
        psHoSo.setDate(6, hs.getNgaySinh());
        psHoSo.setString(7, hs.getGioiTinh());
        psHoSo.setString(8, hs.getDiaChi());
        psHoSo.setDate(9, hs.getNgayKham());
        psHoSo.setString(10, hs.getTrieuChung());
        psHoSo.setString(11, hs.getChanDoan());
        psHoSo.setString(12, hs.getKetLuan());
        psHoSo.setString(13, hs.getLoiDan());
        psHoSo.setString(14, hs.getMaBacSi());
        psHoSo.setString(15, hs.getTrangThai());
        psHoSo.executeUpdate();

        psHoaDon.setString(1, hd.getMaHDKham());
        psHoaDon.setString(2, hd.getMaHoSo());
        psHoaDon.setString(3, hd.getMaGoi());
        psHoaDon.setObject(4, hd.getNgayThanhToan());
        psHoaDon.setBigDecimal(5, hd.getTongTien());
        psHoaDon.setString(6, hd.getHinhThucThanhToan());
        psHoaDon.setString(7, hd.getTrangThai());
        psHoaDon.executeUpdate();

        conn.commit();
        return true;
      } catch (SQLException ex) {
        conn.rollback();
        System.err.println("Lỗi transaction đăng ký khám: " + ex.getMessage());
        return false;
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      System.err.println(
        "Không thể mở transaction đăng ký khám: " + e.getMessage()
      );
      return false;
    }
  }

  private boolean laChuyenKhoan(String phuongThucThanhToan) {
    if (phuongThucThanhToan == null) {
      return false;
    }
    String phuongThucChuanHoa = phuongThucThanhToan
      .trim()
      .toUpperCase()
      .replace(" ", "_");
    return (
      "CHUYEN_KHOAN".equals(phuongThucChuanHoa) ||
      "BANK_TRANSFER".equals(phuongThucChuanHoa)
    );
  }

  private String generateMaHoSo() {
    return "HS" + System.currentTimeMillis();
  }

  private boolean bacSiCoLichLamViec(
    String maBacSi,
    String ngayKham,
    String gioBatDau
  ) {
    String caLam = xacDinhCaLam(gioBatDau);
    ArrayList<LichLamViecDTO> danhSach = lichLamViecBUS.getByBacSiAndNgay(
      maBacSi,
      ngayKham
    );

    for (LichLamViecDTO lich : danhSach) {
      String caLamTrongLich =
        lich.getCaLam() == null ? "" : lich.getCaLam().trim();
      String trangThai =
        lich.getTrangThai() == null
          ? ""
          : lich.getTrangThai().trim().toUpperCase();

      if (
        caLamTrongLich.equalsIgnoreCase(caLam) &&
        ("DA_DUYET".equals(trangThai) || "APPROVED".equals(trangThai))
      ) {
        return true;
      }
    }

    return false;
  }

  private String xacDinhCaLam(String gioBatDau) {
    try {
      int gio = Integer.parseInt(gioBatDau.split(":")[0]);
      if (gio < 12) {
        return "Sang";
      }
      if (gio < 17) {
        return "Chieu";
      }
      return "Toi";
    } catch (Exception ex) {
      return "Sang";
    }
  }
}
