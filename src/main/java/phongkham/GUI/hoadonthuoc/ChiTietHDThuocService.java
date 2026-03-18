package phongkham.gui.hoadonthuoc;

import java.util.ArrayList;
import java.util.List;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;

public class ChiTietHDThuocService {

  private final HoaDonThuocBUS hdBUS = new HoaDonThuocBUS();
  private final CTHDThuocBUS cthdBUS = new CTHDThuocBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();

  public HoaDonThuocDTO layHoaDon(String maHoaDon) {
    return hdBUS.getHoaDonThuocDetail(maHoaDon);
  }

  public List<CTHDThuocDTO> layChiTietHoaDon(String maHoaDon) {
    return cthdBUS.getDetailsByInvoice(maHoaDon);
  }

  public CTHDThuocDTO layChiTietTheoMa(String maCTHD) {
    return cthdBUS.getDetailMedicine(maCTHD);
  }

  public ArrayList<ThuocDTO> layDanhSachThuoc() {
    return new ArrayList<>(thuocBUS.list());
  }

  public ActionResult themChiTiet(
    String maHoaDon,
    String maThuoc,
    int soLuong,
    double donGia,
    String ghiChu
  ) {
    CTHDThuocDTO cthd = new CTHDThuocDTO(maHoaDon, maThuoc, soLuong, donGia);
    cthd.setGhiChu(ghiChu);

    if (!cthdBUS.addDetailMedicine(cthd)) {
      return ActionResult.thatBai("Thêm thất bại!");
    }

    capNhatTongTienHoaDon(maHoaDon);
    return ActionResult.thanhCong("Thêm thành công!");
  }

  public ActionResult capNhatChiTiet(
    String maHoaDon,
    CTHDThuocDTO cthd,
    int soLuong,
    double donGia,
    String ghiChu
  ) {
    cthd.setSoLuong(soLuong);
    cthd.setDonGia(donGia);
    cthd.setThanhTien(soLuong * donGia);
    cthd.setGhiChu(ghiChu);

    if (!cthdBUS.updateDetailMedicine(cthd)) {
      return ActionResult.thatBai("Cập nhật thất bại!");
    }

    capNhatTongTienHoaDon(maHoaDon);
    return ActionResult.thanhCong("Cập nhật thành công!");
  }

  public ActionResult xoaChiTiet(String maHoaDon, String maCTHD) {
    if (!cthdBUS.deleteDetailMedicine(maCTHD)) {
      return ActionResult.thatBai("Xóa thất bại!");
    }

    capNhatTongTienHoaDon(maHoaDon);
    return ActionResult.thanhCong("Xóa thành công!");
  }

  public ActionResult thanhToanHoaDon(String maHoaDon) {
    return hdBUS.payInvoice(maHoaDon)
      ? ActionResult.thanhCong("Thanh toán thành công!")
      : ActionResult.thatBai("Thanh toán thất bại!");
  }

  public ActionResult hoanThanhLayThuoc(String maHoaDon) {
    return hdBUS.completePickup(maHoaDon)
      ? ActionResult.thanhCong("Hoàn thành lấy thuốc và trừ kho thành công!")
      : ActionResult.thatBai(
          "Không thể hoàn thành lấy thuốc!\nKiểm tra số lượng thuốc tồn kho."
        );
  }

  private void capNhatTongTienHoaDon(String maHoaDon) {
    double totalAmount = cthdBUS.calculateInvoiceTotal(maHoaDon);
    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);
    if (hd != null) {
      hd.setTongTien(totalAmount);
      hdBUS.updateHoaDonThuoc(hd);
    }
  }

  public static class ActionResult {

    private final boolean thanhCong;
    private final String message;

    private ActionResult(boolean thanhCong, String message) {
      this.thanhCong = thanhCong;
      this.message = message;
    }

    public static ActionResult thanhCong(String message) {
      return new ActionResult(true, message);
    }

    public static ActionResult thatBai(String message) {
      return new ActionResult(false, message);
    }

    public boolean isThanhCong() {
      return thanhCong;
    }

    public String getMessage() {
      return message;
    }
  }
}
