package phongkham.BUS;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import phongkham.DTO.DonThuocDTO;
import phongkham.dao.DonThuocDAO;

public class DonThuocBUS {

  private DonThuocDAO donThuocDAO;
  private ArrayList<DonThuocDTO> dsDonThuoc;

  public DonThuocBUS() {
    donThuocDAO = new DonThuocDAO();
    loadData();
  }

  // ================= LOAD DATA =================
  public void loadData() {
    dsDonThuoc = donThuocDAO.getAll();
  }

  public ArrayList<DonThuocDTO> getAll() {
    return dsDonThuoc;
  }

  // ================= TU SINH MA =================
  public String generateMaDonThuoc() {
    int max = 0;

    for (DonThuocDTO dt : dsDonThuoc) {
      try {
        int so = Integer.parseInt(dt.getMaDonThuoc().replace("DT", ""));
        if (so > max) max = so;
      } catch (Exception e) {}
    }

    return String.format("DT%03d", max + 1);
  }

  // ================= VALIDATE =================
  private String validate(DonThuocDTO dt) {
    if (
      dt.getMaHoSo() == null || dt.getMaHoSo().trim().isEmpty()
    ) return "Ma ho so khong duoc de trong!";

    if (
      dt.getNgayKeDon() == null || dt.getNgayKeDon().trim().isEmpty()
    ) return "Ngay ke don khong duoc de trong!";

    // Kiem tra dinh dang ngay yyyy-MM-dd
    try {
      LocalDate.parse(dt.getNgayKeDon());
    } catch (DateTimeParseException e) {
      return "Ngay ke don phai dung dinh dang yyyy-MM-dd!";
    }

    return null;
  }

  // ================= THEM =================
  public String insert(DonThuocDTO dt) {
    String error = validate(dt);
    if (error != null) return error;

    if (
      donThuocDAO.exists(dt.getMaDonThuoc())
    ) return "Ma don thuoc da ton tai!";

    boolean result = donThuocDAO.insertDonThuoc(dt);

    if (result) {
      dsDonThuoc.add(dt);
      return "Them thanh cong!";
    }

    return "Them that bai!";
  }

  // Method add() trả về boolean cho GUI (wrapper của insert)
  public boolean add(DonThuocDTO dt) {
    String result = insert(dt);
    return result != null && result.contains("thanh cong");
  }

  // ================= CAP NHAT =================
  public String update(DonThuocDTO dt) {
    String error = validate(dt);
    if (error != null) return error;

    if (
      !donThuocDAO.exists(dt.getMaDonThuoc())
    ) return "Ma don thuoc khong ton tai!";

    boolean result = donThuocDAO.updateDonThuoc(dt);

    if (result) {
      loadData(); // reload lai cache
      return "Cap nhat thanh cong!";
    }

    return "Cap nhat that bai!";
  }

  // ================= XOA =================
  public String delete(String maDonThuoc) {
    if (!donThuocDAO.exists(maDonThuoc)) return "Ma don thuoc khong ton tai!";

    boolean result = donThuocDAO.deleteMaDonThuoc(maDonThuoc);

    if (result) {
      loadData();
      return "Xoa thanh cong!";
    }

    return "Xoa that bai!";
  }

  // ================= TIM THEO MA =================
  public DonThuocDTO searchTheoMa(String maDonThuoc) {
    return donThuocDAO.searchTheoMa(maDonThuoc);
  }

  // ================= TIM THEO MA HO SO =================
  public ArrayList<DonThuocDTO> searchTheoMaHoSo(String maHoSo) {
    ArrayList<DonThuocDTO> result = new ArrayList<>();

    for (DonThuocDTO dt : dsDonThuoc) {
      if (dt.getMaHoSo().equalsIgnoreCase(maHoSo)) {
        result.add(dt);
      }
    }
    return result;
  }
}
