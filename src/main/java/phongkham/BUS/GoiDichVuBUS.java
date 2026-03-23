package phongkham.BUS;

import java.math.BigDecimal;
import java.util.ArrayList;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.dao.GoiDichVuDAO;

public class GoiDichVuBUS {

  private GoiDichVuDAO dao;
  private KhoaBUS khoaBUS;

  public GoiDichVuBUS() {
    dao = new GoiDichVuDAO();
    khoaBUS = new KhoaBUS();
  }

  public ArrayList<GoiDichVuDTO> getAll() {
    return dao.getAll();
  }

  public boolean insert(GoiDichVuDTO g) {
    if (g == null) {
      System.out.println("Goi dich vu khong hop le!");
      return false;
    }
    if (g.getTenGoi() == null || g.getTenGoi().trim().isEmpty()) {
      System.out.println("Khong duoc de trong du lieu!");
      return false;
    }
    if (g.getMaGoi() == null || g.getMaGoi().trim().isEmpty()) {
      g.setMaGoi(generateNextMaGoi());
    }
    if (g.getMaKhoa() == null || g.getMaKhoa().trim().isEmpty()) {
      System.out.println("Goi dich vu phai thuoc mot khoa");
      return false;
    }
    if (!khoaBUS.exists(g.getMaKhoa().trim())) {
      System.out.println("Ma khoa khong ton tai");
      return false;
    }
    if (g.getGiaDichVu() == null) {
      System.out.println("Gia dich vu khong hop le!");
      return false;
    }
    BigDecimal zero = new BigDecimal(0);
    if (g.getGiaDichVu().compareTo(zero) <= 0) {
      System.out.println("Gia dich vu phai lon hon 0!");
      return false;
    }

    if (dao.existsMaGoi(g.getMaGoi())) {
      System.out.println("Ma goi da ton tai!");
      return false;
    }

    return dao.insertGoiDichVu(g);
  }

  public boolean update(GoiDichVuDTO g) {
    if (g == null || g.getMaGoi() == null || g.getMaGoi().trim().isEmpty()) {
      System.out.println("Thong tin goi dich vu khong hop le");
      return false;
    }
    if (!dao.existsMaGoi(g.getMaGoi())) {
      System.out.println("Khong tim thay ma de cap nhat!");
      return false;
    }
    if (g.getMaKhoa() == null || g.getMaKhoa().trim().isEmpty()) {
      System.out.println("Goi dich vu phai thuoc mot khoa");
      return false;
    }
    if (!khoaBUS.exists(g.getMaKhoa().trim())) {
      System.out.println("Ma khoa khong ton tai");
      return false;
    }

    return dao.updateGoiDichVu(g);
  }

  public boolean delete(String maGoi) {
    if (!dao.existsMaGoi(maGoi)) {
      System.out.println("Ma khong ton tai!");
      return false;
    }

    return dao.deleteMaGoi(maGoi);
  }

  public ArrayList<GoiDichVuDTO> searchByTen(String ten) {
    ArrayList<GoiDichVuDTO> result = new ArrayList<>();

    for (GoiDichVuDTO g : dao.getAll()) {
      if (g.getTenGoi().toLowerCase().contains(ten.toLowerCase())) {
        result.add(g);
      }
    }
    return result;
  }

  public GoiDichVuDTO getByMaGoi(String maGoi) {
    GoiDichVuDTO g = new GoiDichVuDTO();
    if (maGoi == null || maGoi.trim().isEmpty()) {
      return null;
    }
    g = dao.getByMaGoi(maGoi);
    return g;
  }

  public ArrayList<GoiDichVuDTO> getByMaKhoa(String maKhoa) {
    ArrayList<GoiDichVuDTO> ketQua = new ArrayList<>();
    if (maKhoa == null || maKhoa.trim().isEmpty()) {
      return ketQua;
    }
    for (GoiDichVuDTO goiDichVu : dao.getAll()) {
      if (maKhoa.equalsIgnoreCase(goiDichVu.getMaKhoa())) {
        ketQua.add(goiDichVu);
      }
    }
    return ketQua;
  }

  public String generateNextMaGoi() {
    int maxNumber = 0;
    for (GoiDichVuDTO goi : dao.getAll()) {
      if (goi == null || goi.getMaGoi() == null) {
        continue;
      }
      String ma = goi.getMaGoi().trim().toUpperCase();
      if (!ma.startsWith("G")) {
        continue;
      }
      try {
        int number = Integer.parseInt(ma.substring(1));
        if (number > maxNumber) {
          maxNumber = number;
        }
      } catch (NumberFormatException ignored) {}
    }
    return String.format("G%03d", maxNumber + 1);
  }
}
