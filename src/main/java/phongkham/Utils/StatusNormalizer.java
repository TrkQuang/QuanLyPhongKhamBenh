package phongkham.Utils;

import java.text.Normalizer;

public class StatusNormalizer {

  public static final String CHO_XAC_NHAN = "CHO_XAC_NHAN";
  public static final String DA_XAC_NHAN = "DA_XAC_NHAN";
  public static final String DANG_KHAM = "DANG_KHAM";
  public static final String HOAN_THANH = "HOAN_THANH";
  public static final String DA_HUY = "DA_HUY";

  public static final String CHO_DUYET = "CHO_DUYET";
  public static final String DA_DUYET = "DA_DUYET";
  public static final String TU_CHOI = "TU_CHOI";
  public static final String DA_NHAP = "DA_NHAP";

  public static final String CHUA_THANH_TOAN = "CHUA_THANH_TOAN";
  public static final String DA_THANH_TOAN = "DA_THANH_TOAN";
  public static final String HOAN_HOA_DON = "HOAN_HOA_DON";

  public static final String CHO_LAY = "CHO_LAY";
  public static final String DA_HOAN_THANH = "DA_HOAN_THANH";

  public static final String CHO_KHAM = "CHO_KHAM";
  public static final String DA_KHAM = "DA_KHAM";

  private StatusNormalizer() {}

  public static String normalizeToken(String raw) {
    if (raw == null) {
      return "";
    }
    String noAccent = Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll(
      "\\p{M}+",
      ""
    );
    return noAccent
      .trim()
      .toUpperCase()
      .replaceAll("[^A-Z0-9]+", "_")
      .replaceAll("_+", "_")
      .replaceAll("^_|_$", "");
  }

  public static String normalizeLichKhamStatus(String raw) {
    String token = normalizeToken(raw);
    switch (token) {
      case "DA_DAT":
      case "CHO_XAC_NHAN":
        return CHO_XAC_NHAN;
      case "DA_XAC_NHAN":
        return DA_XAC_NHAN;
      case "DANG_KHAM":
        return DANG_KHAM;
      case "HOAN_THANH":
        return HOAN_THANH;
      case "DA_HUY":
      case "HUY":
        return DA_HUY;
      default:
        return token;
    }
  }

  public static String normalizeLichLamViecStatus(String raw) {
    String token = normalizeToken(raw);
    if ("CHUA_DUYET".equals(token)) {
      return CHO_DUYET;
    }
    if ("DA_DUYET".equals(token)) {
      return DA_DUYET;
    }
    if ("TU_CHOI".equals(token)) {
      return TU_CHOI;
    }
    return token;
  }

  public static String normalizePaymentStatus(String raw) {
    String token = normalizeToken(raw);
    switch (token) {
      case "DA_THANH_TOAN":
        return DA_THANH_TOAN;
      case "CHUA_THANH_TOAN":
      case "CHO_THANH_TOAN":
        return CHUA_THANH_TOAN;
      case "HOAN_HOA_DON":
      case "HOAN_TIEN":
        return HOAN_HOA_DON;
      default:
        return token;
    }
  }

  public static String normalizePickupStatus(String raw) {
    String token = normalizeToken(raw);
    switch (token) {
      case "DANG_CHO_LAY":
      case "DANG_CHO_LAY_THUOC":
      case "CHO_LAY":
        return CHO_LAY;
      case "DA_HOAN_THANH":
      case "HOAN_THANH":
        return DA_HOAN_THANH;
      default:
        return token;
    }
  }

  public static String normalizePhieuNhapStatus(String raw) {
    String token = normalizeToken(raw);
    if ("CHUA_DUYET".equals(token)) {
      return CHO_DUYET;
    }
    if ("DA_DUYET".equals(token)) {
      return DA_DUYET;
    }
    if ("DA_NHAP".equals(token) || "DA_NHAP_KHO".equals(token)) {
      return DA_NHAP;
    }
    if ("DA_HUY".equals(token) || "HUY".equals(token)) {
      return DA_HUY;
    }
    return token;
  }

  public static String normalizeHoSoStatus(String raw) {
    String token = normalizeToken(raw);
    switch (token) {
      case "CHO_KHAM":
        return CHO_KHAM;
      case "DA_KHAM":
      case "HOAN_THANH":
        return DA_KHAM;
      case "DA_HUY":
      case "HUY":
        return DA_HUY;
      default:
        return token;
    }
  }
}
