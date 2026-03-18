package phongkham.gui.banthuoc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.PdfExport;
import phongkham.Utils.StatusNormalizer;

public class BanThuocService {

  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final HoaDonThuocBUS hoaDonBUS = new HoaDonThuocBUS();
  private final CTHDThuocBUS cthdBUS = new CTHDThuocBUS();

  public ArrayList<ThuocDTO> layThuocConTon() {
    return thuocBUS.getThuocConTon();
  }

  public ArrayList<ThuocDTO> timThuocTheoTen(String keyword) {
    return thuocBUS.timTheoTen(keyword);
  }

  public ThuocDTO layThuocTheoMa(String maThuoc) {
    return thuocBUS.getByMa(maThuoc);
  }

  public void themHoacCongDonVaoGio(
    List<CartItem> gioHang,
    String maThuoc,
    String tenThuoc,
    float donGia,
    int soLuong
  ) {
    for (CartItem item : gioHang) {
      if (item.getMaThuoc().equals(maThuoc)) {
        item.setSoLuong(item.getSoLuong() + soLuong);
        return;
      }
    }
    gioHang.add(new CartItem(maThuoc, tenThuoc, donGia, soLuong));
  }

  public double tinhTongTien(List<CartItem> gioHang) {
    double tongTien = 0;
    for (CartItem item : gioHang) {
      tongTien += item.getDonGia() * item.getSoLuong();
    }
    return tongTien;
  }

  public ThanhToanResult thanhToan(
    String tenKhach,
    String sdt,
    String phuongThuc,
    List<CartItem> gioHang
  ) {
    if (gioHang == null || gioHang.isEmpty()) {
      return ThanhToanResult.thatBai("Giỏ hàng trống!");
    }

    double tongTien = tinhTongTien(gioHang);

    try {
      HoaDonThuocDTO hoaDon = new HoaDonThuocDTO(null, tenKhach, sdt);
      hoaDon.setTongTien(tongTien);
      hoaDon.setTrangThaiThanhToan(StatusNormalizer.DA_THANH_TOAN);
      hoaDon.setNgayThanhToan(LocalDateTime.now());
      hoaDon.setTrangThaiLayThuoc(StatusNormalizer.CHO_LAY);
      hoaDon.setGhiChu("Thanh toán: " + phuongThuc);

      boolean insertHD = hoaDonBUS.addHoaDonThuoc(hoaDon);
      if (!insertHD) {
        return ThanhToanResult.thatBai("Lỗi tạo hóa đơn!");
      }

      String maHoaDon = hoaDon.getMaHoaDon();
      if (maHoaDon == null || maHoaDon.isEmpty()) {
        return ThanhToanResult.thatBai("Lỗi lấy mã hóa đơn!");
      }

      for (CartItem item : gioHang) {
        CTHDThuocDTO cthd = new CTHDThuocDTO(
          maHoaDon,
          item.getMaThuoc(),
          item.getSoLuong(),
          item.getDonGia()
        );
        cthdBUS.addDetailMedicine(cthd);
      }

      HoaDonThuocDTO hdUpdate = hoaDonBUS.getHoaDonThuocDetail(maHoaDon);
      if (hdUpdate != null) {
        hdUpdate.setTongTien(tongTien);
        hoaDonBUS.updateHoaDonThuoc(hdUpdate);
      }

      return ThanhToanResult.thanhCong(maHoaDon, tongTien);
    } catch (Exception ex) {
      return ThanhToanResult.thatBai("Lỗi thanh toán: " + ex.getMessage());
    }
  }

  public void xuatHoaDonPdf(
    String maHoaDon,
    String tenKhach,
    String sdt,
    String phuongThuc,
    List<CartItem> gioHang,
    double tongTien
  ) {
    StringBuilder chiTiet = new StringBuilder();
    for (CartItem item : gioHang) {
      chiTiet.append(
        String.format(
          "- %s | SL: %d | Đơn giá: %,.0f | Thành tiền: %,.0f\n",
          item.getTenThuoc(),
          item.getSoLuong(),
          (double) item.getDonGia(),
          item.getSoLuong() * (double) item.getDonGia()
        )
      );
    }

    String content = String.format(
      "=== HÓA ĐƠN BÁN THUỐC ===\n\n" +
        "--- THÔNG TIN CHUNG ---\n" +
        "Mã hóa đơn: %s\n" +
        "Khách hàng: %s\n" +
        "SĐT: %s\n" +
        "Thời gian: %s\n\n" +
        "--- CHI TIẾT THUỐC ---\n" +
        "%s\n" +
        "--- THANH TOÁN ---\n" +
        "Tổng tiền: %,.0f VNĐ\n" +
        "Phương thức: %s\n" +
        "Trạng thái thanh toán: ĐÃ THANH TOÁN\n" +
        "Trạng thái lấy thuốc: ĐANG CHỜ LẤY\n\n" +
        "=== VUI LÒNG ĐƯA HÓA ĐƠN CHO NHÀ THUỐC ĐỂ LẤY THUỐC ===",
      maHoaDon,
      tenKhach,
      sdt,
      LocalDateTime.now(),
      chiTiet.toString(),
      tongTien,
      phuongThuc
    );

    PdfExport.exportText(content, "HoaDonThuoc_" + maHoaDon);
  }

  public static class CartItem {

    private final String maThuoc;
    private final String tenThuoc;
    private final float donGia;
    private int soLuong;

    public CartItem(
      String maThuoc,
      String tenThuoc,
      float donGia,
      int soLuong
    ) {
      this.maThuoc = maThuoc;
      this.tenThuoc = tenThuoc;
      this.donGia = donGia;
      this.soLuong = soLuong;
    }

    public String getMaThuoc() {
      return maThuoc;
    }

    public String getTenThuoc() {
      return tenThuoc;
    }

    public float getDonGia() {
      return donGia;
    }

    public int getSoLuong() {
      return soLuong;
    }

    public void setSoLuong(int soLuong) {
      this.soLuong = soLuong;
    }
  }

  public static class ThanhToanResult {

    private final boolean thanhCong;
    private final String message;
    private final String maHoaDon;
    private final double tongTien;

    private ThanhToanResult(
      boolean thanhCong,
      String message,
      String maHoaDon,
      double tongTien
    ) {
      this.thanhCong = thanhCong;
      this.message = message;
      this.maHoaDon = maHoaDon;
      this.tongTien = tongTien;
    }

    public static ThanhToanResult thanhCong(String maHoaDon, double tongTien) {
      return new ThanhToanResult(
        true,
        "Thanh toán thành công!",
        maHoaDon,
        tongTien
      );
    }

    public static ThanhToanResult thatBai(String message) {
      return new ThanhToanResult(false, message, null, 0);
    }

    public boolean isThanhCong() {
      return thanhCong;
    }

    public String getMessage() {
      return message;
    }

    public String getMaHoaDon() {
      return maHoaDon;
    }

    public double getTongTien() {
      return tongTien;
    }
  }
}
