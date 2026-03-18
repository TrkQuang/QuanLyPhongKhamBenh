package phongkham.gui.hoadonthuoc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.Utils.StatusNormalizer;

public class HoaDonThuocPanelService {

  public ArrayList<HoaDonThuocDTO> locHoaDon(
    List<HoaDonThuocDTO> source,
    String keyword,
    LocalDate fromDate,
    LocalDate toDate
  ) {
    ArrayList<HoaDonThuocDTO> ketQua = new ArrayList<>();
    String key = keyword == null ? "" : keyword.trim().toLowerCase();

    for (HoaDonThuocDTO hd : source) {
      boolean match = true;

      if (!key.isEmpty()) {
        String tenBenhNhan =
          hd.getTenBenhNhan() == null ? "" : hd.getTenBenhNhan().toLowerCase();
        match =
          String.valueOf(hd.getMaHoaDon()).contains(key) ||
          tenBenhNhan.contains(key);
      }

      if (match && fromDate != null && toDate != null && hd.getNgayLap() != null) {
        LocalDate hoaDonDate = hd.getNgayLap().toLocalDate();
        match = !hoaDonDate.isBefore(fromDate) && !hoaDonDate.isAfter(toDate);
      }

      if (match) {
        ketQua.add(hd);
      }
    }

    return ketQua;
  }

  public ActionResult xacNhanGiaoThuoc(HoaDonThuocBUS hdBUS, HoaDonThuocDTO hd) {
    if (hd == null) {
      return ActionResult.thatBai("Không tìm thấy hóa đơn!");
    }

    if (
      !"DA_THANH_TOAN".equals(
        StatusNormalizer.normalizePaymentStatus(hd.getTrangThaiThanhToan())
      )
    ) {
      return ActionResult.thatBai(
        "Hóa đơn chưa thanh toán, không thể giao thuốc!"
      );
    }

    if (
      !"CHO_LAY".equals(
        StatusNormalizer.normalizePickupStatus(hd.getTrangThaiLayThuoc())
      )
    ) {
      return ActionResult.thatBai("Hóa đơn đã được xử lý hoặc đã hủy!");
    }

    boolean success = hdBUS.completePickup(hd.getMaHoaDon());
    return success
      ? ActionResult.thanhCong("Xác nhận giao thuốc thành công!\nĐã trừ tồn kho.")
      : ActionResult.thatBai("Giao thuốc thất bại! Vui lòng kiểm tra lại.");
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
