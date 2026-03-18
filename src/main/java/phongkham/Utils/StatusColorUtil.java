package phongkham.Utils;

import java.awt.Color;

public class StatusColorUtil {

  private StatusColorUtil() {}

  private static final Color COLOR_SUCCESS = new Color(22, 163, 74);
  private static final Color COLOR_WARNING = new Color(217, 119, 6);
  private static final Color COLOR_DANGER = new Color(220, 38, 38);
  private static final Color COLOR_INFO = new Color(37, 99, 235);
  private static final Color COLOR_NEUTRAL = new Color(71, 85, 105);

  public static Color lichKham(String rawStatus) {
    String status = StatusNormalizer.normalizeLichKhamStatus(rawStatus);
    switch (status) {
      case "CHO_XAC_NHAN":
        return COLOR_WARNING;
      case "DA_XAC_NHAN":
        return COLOR_INFO;
      case "DANG_KHAM":
        return new Color(14, 116, 144);
      case "HOAN_THANH":
        return COLOR_SUCCESS;
      case "DA_HUY":
        return COLOR_DANGER;
      default:
        return COLOR_NEUTRAL;
    }
  }

  public static Color lichLamViec(String rawStatus) {
    String status = StatusNormalizer.normalizeLichLamViecStatus(rawStatus);
    switch (status) {
      case "CHO_DUYET":
        return COLOR_WARNING;
      case "DA_DUYET":
        return COLOR_SUCCESS;
      case "TU_CHOI":
        return COLOR_DANGER;
      default:
        return COLOR_NEUTRAL;
    }
  }

  public static Color thanhToan(String rawStatus) {
    String status = StatusNormalizer.normalizePaymentStatus(rawStatus);
    switch (status) {
      case "CHUA_THANH_TOAN":
        return COLOR_WARNING;
      case "DA_THANH_TOAN":
        return COLOR_SUCCESS;
      case "HOAN_HOA_DON":
        return COLOR_DANGER;
      default:
        return COLOR_NEUTRAL;
    }
  }

  public static Color layThuoc(String rawStatus) {
    String status = StatusNormalizer.normalizePickupStatus(rawStatus);
    switch (status) {
      case "CHO_LAY":
        return COLOR_INFO;
      case "DA_HOAN_THANH":
        return COLOR_SUCCESS;
      default:
        return COLOR_NEUTRAL;
    }
  }

  public static Color phieuNhap(String rawStatus) {
    String status = StatusNormalizer.normalizePhieuNhapStatus(rawStatus);
    switch (status) {
      case "CHO_DUYET":
        return COLOR_WARNING;
      case "DA_DUYET":
        return COLOR_INFO;
      case "DA_NHAP":
        return COLOR_SUCCESS;
      case "DA_HUY":
        return COLOR_DANGER;
      default:
        return COLOR_NEUTRAL;
    }
  }
}
