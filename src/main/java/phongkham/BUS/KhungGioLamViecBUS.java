package phongkham.BUS;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import phongkham.DTO.KhungGioLamViecDTO;
import phongkham.dao.KhungGioLamViecDAO;

public class KhungGioLamViecBUS {

  private final KhungGioLamViecDAO dao = new KhungGioLamViecDAO();
  private static final DateTimeFormatter TIME_FORMAT =
    DateTimeFormatter.ofPattern("HH:mm");

  public ArrayList<KhungGioLamViecDTO> getAll() {
    return dao.getAll();
  }

  public ArrayList<KhungGioLamViecDTO> getAllActive() {
    return dao.getAllActive();
  }

  public List<String> getAllActiveRanges() {
    ArrayList<KhungGioLamViecDTO> rows = dao.getAllActive();
    ArrayList<String> ranges = new ArrayList<>();
    for (KhungGioLamViecDTO row : rows) {
      ranges.add(row.getKhungGio());
    }
    return ranges;
  }

  public boolean addRange(String rawRange, String moTa) {
    String normalized = normalizeRange(rawRange);
    if (normalized == null) {
      return false;
    }
    if (dao.existsByRange(normalized)) {
      return false;
    }
    return dao.insert(normalized, moTa == null ? "" : moTa.trim());
  }

  public boolean toggleActive(int maKhungGio, boolean active) {
    return dao.updateActive(maKhungGio, active ? 1 : 0);
  }

  public boolean isConfiguredRange(String rawRange) {
    String normalized = normalizeRange(rawRange);
    if (normalized == null) {
      return false;
    }
    for (KhungGioLamViecDTO item : dao.getAllActive()) {
      if (normalized.equals(item.getKhungGio())) {
        return true;
      }
    }
    return false;
  }

  public String normalizeRange(String rawRange) {
    if (rawRange == null) {
      return null;
    }
    String value = rawRange.trim().replace(" ", "");
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
      return TIME_FORMAT.format(start) + "-" + TIME_FORMAT.format(end);
    } catch (Exception ex) {
      return null;
    }
  }
}
