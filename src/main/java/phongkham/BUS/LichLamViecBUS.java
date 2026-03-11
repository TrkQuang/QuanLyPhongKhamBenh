package phongkham.BUS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import phongkham.DTO.LichLamViecDTO;
import phongkham.dao.LichLamViecDAO;

public class LichLamViecBUS {

  private LichLamViecDAO dao = new LichLamViecDAO();
  private static final List<String> VALID_CA = Arrays.asList(
    "Sang",
    "Chieu",
    "Toi"
  );
  private static final DateTimeFormatter DATE_FORMAT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public ArrayList<LichLamViecDTO> getAll() {
    return dao.getAll();
  }

  public boolean add(LichLamViecDTO llv) {
    if (!validate(llv, false)) return false;
    return dao.insert(llv);
  }

  public boolean update(LichLamViecDTO llv) {
    if (!validate(llv, true)) return false;
    return dao.update(llv);
  }

  public boolean delete(String maLichLam) {
    if (isEmpty(maLichLam) || getById(maLichLam) == null) return false;
    return dao.delete(maLichLam);
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
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    if (!isEmpty(ngay)) {
      for (LichLamViecDTO llv : dao.getAll()) {
        if (llv.getNgayLam().equals(ngay)) result.add(llv);
      }
    }
    return result;
  }

  public ArrayList<LichLamViecDTO> getByCa(String ca) {
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    if (!isEmpty(ca)) {
      for (LichLamViecDTO llv : dao.getAll()) {
        if (llv.getCaLam().equals(ca)) result.add(llv);
      }
    }
    return result;
  }

  public ArrayList<LichLamViecDTO> getByBacSiAndNgay(
    String maBacSi,
    String ngay
  ) {
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    if (!isEmpty(maBacSi) && !isEmpty(ngay)) {
      for (LichLamViecDTO llv : dao.getByBacSi(maBacSi)) {
        if (llv.getNgayLam().equals(ngay)) result.add(llv);
      }
    }
    return result;
  }

  public ArrayList<LichLamViecDTO> getSapToi(String maBacSi) {
    String today = LocalDate.now().format(DATE_FORMAT);
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    for (LichLamViecDTO llv : getByBacSi(maBacSi)) {
      if (llv.getNgayLam().compareTo(today) >= 0) result.add(llv);
    }
    return result;
  }

  public ArrayList<LichLamViecDTO> getQuaKhu(String maBacSi) {
    String today = LocalDate.now().format(DATE_FORMAT);
    ArrayList<LichLamViecDTO> result = new ArrayList<>();
    for (LichLamViecDTO llv : getByBacSi(maBacSi)) {
      if (llv.getNgayLam().compareTo(today) < 0) result.add(llv);
    }
    return result;
  }

  public boolean checkConflict(String maBacSi, String ngay, String ca) {
    if (isEmpty(maBacSi) || isEmpty(ngay) || isEmpty(ca)) {
      return false;
    }
    ArrayList<LichLamViecDTO> list = getByBacSiAndNgay(maBacSi, ngay);
    for (LichLamViecDTO llv : list) {
      if (llv.getCaLam().equals(ca)) return true;
    }
    return false;
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
    return VALID_CA;
  }

  public String thongKe() {
    int sang = getByCa("Sang").size();
    int chieu = getByCa("Chieu").size();
    int toi = getByCa("Toi").size();
    return String.format(
      "Tổng: %d (Sáng: %d, Chiều: %d, Tối: %d)",
      sang + chieu + toi,
      sang,
      chieu,
      toi
    );
  }

  private boolean validate(LichLamViecDTO llv, boolean isUpdate) {
    if (
      llv == null || isEmpty(llv.getMaLichLam()) || isEmpty(llv.getMaBacSi())
    ) return false;
    if (
      isEmpty(llv.getNgayLam()) || !isValidDate(llv.getNgayLam())
    ) return false;
    if (
      isEmpty(llv.getCaLam()) || !VALID_CA.contains(llv.getCaLam())
    ) return false;
    if (!isUpdate && exists(llv.getMaLichLam())) return false;
    if (isUpdate && !exists(llv.getMaLichLam())) return false;
    return true;
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
}
