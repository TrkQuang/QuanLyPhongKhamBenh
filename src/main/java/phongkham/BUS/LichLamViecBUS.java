package phongkham.BUS;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.dao.LichLamViecDAO;

public class LichLamViecBUS {

  private LichLamViecDAO dao = new LichLamViecDAO();
  private KhungGioLamViecBUS khungGioLamViecBUS = new KhungGioLamViecBUS();
  private static final List<String> LEGACY_CA = Arrays.asList(
    "Sang",
    "Chieu",
    "Toi"
  );
  private static final List<String> DEFAULT_SHIFT_RANGES = Arrays.asList(
    "08:00-12:00",
    "13:00-17:00",
    "17:00-21:00"
  );
  private static final List<String> VALID_TRANG_THAI = Arrays.asList(
    "CHO_DUYET",
    "DA_DUYET",
    "TU_CHOI"
  );
  private static final DateTimeFormatter DATE_FORMAT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMAT =
    DateTimeFormatter.ofPattern("HH:mm");

  public ArrayList<LichLamViecDTO> getAll() {
    return dao.getAll();
  }

  public boolean add(LichLamViecDTO llv) {
    if (llv != null && isEmpty(llv.getTrangThai())) {
      llv.setTrangThai("CHO_DUYET");
    }
    if (!validate(llv, false)) return false;
    if (checkConflict(llv.getMaBacSi(), llv.getNgayLam(), llv.getCaLam())) {
      System.out.println("Bac si da dang ky ca nay trong ngay da chon");
      return false;
    }
    boolean result = dao.insert(llv);
    if (result) System.out.println("Them lich lam viec: " + llv.getMaLichLam());
    return result;
  }

  public boolean update(LichLamViecDTO llv) {
    if (!validate(llv, true)) return false;
    boolean result = dao.update(llv);
    if (result) System.out.println(
      "Cap nhat lich lam viec: " + llv.getMaLichLam()
    );
    return result;
  }

  public boolean delete(String maLichLam) {
    if (isEmpty(maLichLam) || getById(maLichLam) == null) {
      System.out.println("Lich lam viec khong ton tai");
      return false;
    }
    boolean result = dao.delete(maLichLam);
    if (result) System.out.println("Xoa lich lam viec: " + maLichLam);
    return result;
  }

  public LichLamViecDTO getById(String maLichLam) {
    ArrayList<LichLamViecDTO> list = dao.getAll();
    for (LichLamViecDTO llv : list) {
      if (llv.getMaLichLam().equals(maLichLam)) return llv;
    }
    return null;
  }

  public ArrayList<LichLamViecDTO> getByBacSi(String maBacSi) {
    return isEmpty(maBacSi) ? new ArrayList<>() : dao.getByBacSi(maBacSi);
  }

  public ArrayList<LichLamViecDTO> getByNgay(String ngay) {
    if (isEmpty(ngay)) {
      return new ArrayList<>();
    }
    return filterFromAll(llv -> ngay.equals(llv.getNgayLam()));
  }

  public ArrayList<LichLamViecDTO> getByCa(String ca) {
    if (isEmpty(ca)) {
      return new ArrayList<>();
    }
    return filterFromAll(llv -> ca.equals(llv.getCaLam()));
  }

  public ArrayList<LichLamViecDTO> getByBacSiAndNgay(
    String maBacSi,
    String ngay
  ) {
    if (isEmpty(maBacSi) || isEmpty(ngay)) {
      return new ArrayList<>();
    }
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    for (LichLamViecDTO llv : dao.getByBacSi(maBacSi)) {
      if (ngay.equals(llv.getNgayLam())) result.add(llv);
    }
    return result;
  }

  public ArrayList<LichLamViecDTO> getSapToi(String maBacSi) {
    String today = LocalDate.now().format(DATE_FORMAT);
    return filterByNgayMoc(maBacSi, today, true);
  }

  public ArrayList<LichLamViecDTO> getQuaKhu(String maBacSi) {
    String today = LocalDate.now().format(DATE_FORMAT);
    return filterByNgayMoc(maBacSi, today, false);
  }

  private ArrayList<LichLamViecDTO> filterByNgayMoc(
    String maBacSi,
    String mocNgay,
    boolean layTuHienTai
  ) {
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    for (LichLamViecDTO llv : getByBacSi(maBacSi)) {
      boolean hopLe = layTuHienTai
        ? llv.getNgayLam().compareTo(mocNgay) >= 0
        : llv.getNgayLam().compareTo(mocNgay) < 0;
      if (hopLe) {
        result.add(llv);
      }
    }
    return result;
  }

  public boolean checkConflict(String maBacSi, String ngay, String ca) {
    if (isEmpty(maBacSi) || isEmpty(ngay) || isEmpty(ca)) {
      return false;
    }
    LocalTime[] candidateRange = parseShiftRange(ca);
    if (candidateRange == null) {
      return false;
    }
    ArrayList<LichLamViecDTO> list = getByBacSiAndNgay(maBacSi, ngay);
    for (LichLamViecDTO llv : list) {
      LocalTime[] existingRange = parseShiftRange(llv.getCaLam());
      if (existingRange == null) {
        continue;
      }
      if (
        isOverlap(
          candidateRange[0],
          candidateRange[1],
          existingRange[0],
          existingRange[1]
        ) &&
        !"TU_CHOI".equals(
          StatusNormalizer.normalizeLichLamViecStatus(llv.getTrangThai())
        )
      ) {
        System.out.println(
          "Xung dot: Bac si " +
            maBacSi +
            " da co lich lam viec trong ca " +
            ca +
            " ngay " +
            ngay
        );
        return true;
      }
    }
    return false;
  }

  public boolean duyetLich(String maLichLam) {
    return updateFromChoDuyet(maLichLam, "DA_DUYET");
  }

  public boolean tuChoiLich(String maLichLam) {
    return updateFromChoDuyet(maLichLam, "TU_CHOI");
  }

  private boolean updateFromChoDuyet(String maLichLam, String trangThaiMoi) {
    if (isEmpty(maLichLam)) {
      return false;
    }
    LichLamViecDTO llv = getById(maLichLam);
    if (llv == null) {
      return false;
    }
    String trangThai = StatusNormalizer.normalizeLichLamViecStatus(
      llv.getTrangThai()
    );
    if (!"CHO_DUYET".equals(trangThai)) {
      return false;
    }
    return dao.updateTrangThai(maLichLam, trangThaiMoi);
  }

  public int countAll() {
    return dao.getAll().size();
  }

  public int countByBacSi(String maBacSi) {
    return getByBacSi(maBacSi).size();
  }

  public boolean exists(String maLichLam) {
    return getById(maLichLam) != null;
  }

  public String generateMaLichLam() {
    ArrayList<LichLamViecDTO> list = dao.getAll();
    int maxId = 0;
    for (LichLamViecDTO llv : list) {
      try {
        if (llv.getMaLichLam().startsWith("LLV")) {
          int id = Integer.parseInt(llv.getMaLichLam().substring(3));
          maxId = Math.max(maxId, id);
        }
      } catch (Exception e) {}
    }
    return String.format("LLV%03d", maxId + 1);
  }

  public List<String> getValidCa() {
    List<String> ranges = khungGioLamViecBUS.getAllActiveRanges();
    if (ranges.isEmpty()) {
      return DEFAULT_SHIFT_RANGES;
    }
    return ranges;
  }

  public String thongKe() {
    int choDuyet = 0;
    int daDuyet = 0;
    int tuChoi = 0;
    for (LichLamViecDTO llv : dao.getAll()) {
      String status = StatusNormalizer.normalizeLichLamViecStatus(
        llv.getTrangThai()
      );
      if ("CHO_DUYET".equals(status)) {
        choDuyet++;
      } else if ("DA_DUYET".equals(status)) {
        daDuyet++;
      } else if ("TU_CHOI".equals(status)) {
        tuChoi++;
      }
    }
    return String.format(
      "Tong: %d (Cho duyet: %d, Da duyet: %d, Tu choi: %d)",
      choDuyet + daDuyet + tuChoi,
      choDuyet,
      daDuyet,
      tuChoi
    );
  }

  private boolean validate(LichLamViecDTO llv, boolean isUpdate) {
    if (
      llv == null || isEmpty(llv.getMaLichLam()) || isEmpty(llv.getMaBacSi())
    ) {
      System.out.println("Thong tin khong day du");
      return false;
    }
    if (isEmpty(llv.getNgayLam()) || !isValidDate(llv.getNgayLam())) {
      System.out.println("Ngay khong hop le (yyyy-MM-dd)");
      return false;
    }
    if (isEmpty(llv.getCaLam())) {
      System.out.println("Khung gio khong hop le (VD: 08:00-10:00)");
      return false;
    }

    String caNormalized = normalizeShiftInput(llv.getCaLam());
    if (parseShiftRange(caNormalized) == null) {
      System.out.println("Khung gio khong hop le (VD: 08:00-10:00)");
      return false;
    }
    if (!isConfiguredShift(caNormalized)) {
      System.out.println("Khung gio chua duoc admin cau hinh hoat dong");
      return false;
    }
    llv.setCaLam(caNormalized);

    if (
      !isEmpty(llv.getTrangThai()) &&
      !VALID_TRANG_THAI.contains(
        StatusNormalizer.normalizeLichLamViecStatus(llv.getTrangThai())
      )
    ) {
      System.out.println(
        "Trang thai khong hop le (CHO_DUYET/DA_DUYET/TU_CHOI)"
      );
      return false;
    }
    String trangThaiChuan = StatusNormalizer.normalizeLichLamViecStatus(
      llv.getTrangThai()
    );
    llv.setTrangThai(trangThaiChuan);

    boolean tonTai = exists(llv.getMaLichLam());
    if (!isUpdate && tonTai) {
      System.out.println("Ma da ton tai");
      return false;
    }
    if (isUpdate && !tonTai) {
      System.out.println("Ma khong ton tai");
      return false;
    }
    return true;
  }

  private ArrayList<LichLamViecDTO> filterFromAll(
    Predicate<LichLamViecDTO> predicate
  ) {
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    for (LichLamViecDTO llv : dao.getAll()) {
      if (predicate.test(llv)) {
        result.add(llv);
      }
    }
    return result;
  }

  private boolean isValidDate(String date) {
    try {
      LocalDate.parse(date, DATE_FORMAT);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  private String normalizeShiftInput(String value) {
    if (value == null) {
      return "";
    }
    String input = value.trim().replace(" ", "");
    if ("Sang".equalsIgnoreCase(input)) {
      return "08:00-12:00";
    }
    if ("Chieu".equalsIgnoreCase(input)) {
      return "13:00-17:00";
    }
    if ("Toi".equalsIgnoreCase(input) || "Tối".equalsIgnoreCase(input)) {
      return "17:00-21:00";
    }
    return input;
  }

  private boolean isConfiguredShift(String range) {
    for (String configured : getValidCa()) {
      if (configured.equalsIgnoreCase(range)) {
        return true;
      }
    }
    return false;
  }

  private LocalTime[] parseShiftRange(String rawRange) {
    if (rawRange == null) {
      return null;
    }

    String value = rawRange.trim().replace(" ", "");
    final String normalizedInput = value;
    if (
      LEGACY_CA
        .stream()
        .anyMatch(ca -> ca.equalsIgnoreCase(normalizedInput))
    ) {
      value = normalizeShiftInput(value);
    }

    String[] parts = value.split("-");
    if (parts.length != 2) {
      return null;
    }

    try {
      LocalTime start = LocalTime.parse(parts[0], TIME_FORMAT);
      LocalTime end = LocalTime.parse(parts[1], TIME_FORMAT);
      if (!start.isBefore(end)) {
        return null;
      }
      return new LocalTime[] { start, end };
    } catch (Exception ex) {
      return null;
    }
  }

  private boolean isOverlap(
    LocalTime startA,
    LocalTime endA,
    LocalTime startB,
    LocalTime endB
  ) {
    return startA.isBefore(endB) && endA.isAfter(startB);
  }
}
