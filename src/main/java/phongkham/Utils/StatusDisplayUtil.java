package phongkham.Utils;

public class StatusDisplayUtil {

  private StatusDisplayUtil() {}

  public static String lichKham(String rawStatus) {
    String status = StatusNormalizer.normalizeLichKhamStatus(rawStatus);
    switch (status) {
      case "CHO_XAC_NHAN":
        return "Chờ xác nhận";
      case "DA_XAC_NHAN":
        return "Đã xác nhận";
      case "DANG_KHAM":
        return "Đang khám";
      case "HOAN_THANH":
        return "Hoàn thành";
      case "DA_HUY":
        return "Đã hủy";
      default:
        return rawStatus == null ? "" : rawStatus;
    }
  }

  public static String lichLamViec(String rawStatus) {
    String status = StatusNormalizer.normalizeLichLamViecStatus(rawStatus);
    switch (status) {
      case "CHO_DUYET":
        return "Chờ duyệt";
      case "DA_DUYET":
        return "Đã duyệt";
      case "TU_CHOI":
        return "Từ chối";
      default:
        return rawStatus == null ? "" : rawStatus;
    }
  }

  public static String thanhToan(String rawStatus) {
    String status = StatusNormalizer.normalizePaymentStatus(rawStatus);
    switch (status) {
      case "CHUA_THANH_TOAN":
        return "Chưa thanh toán";
      case "DA_THANH_TOAN":
        return "Đã thanh toán";
      case "HOAN_HOA_DON":
        return "Hoàn hóa đơn";
      default:
        return rawStatus == null ? "" : rawStatus;
    }
  }

  public static String layThuoc(String rawStatus) {
    String status = StatusNormalizer.normalizePickupStatus(rawStatus);
    switch (status) {
      case "CHO_LAY":
        return "Đang chờ lấy";
      case "DA_HOAN_THANH":
        return "Đã hoàn thành";
      default:
        return rawStatus == null ? "" : rawStatus;
    }
  }

  public static String phieuNhap(String rawStatus) {
    String status = StatusNormalizer.normalizePhieuNhapStatus(rawStatus);
    switch (status) {
      case "CHO_DUYET":
        return "Chờ duyệt";
      case "DA_DUYET":
        return "Đã duyệt";
      case "DA_NHAP":
        return "Đã nhập";
      case "DA_HUY":
        return "Đã hủy";
      default:
        return rawStatus == null ? "" : rawStatus;
    }
  }
}
