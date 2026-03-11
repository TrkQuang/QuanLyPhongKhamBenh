package phongkham.BUS;

import java.math.BigDecimal;
import java.util.ArrayList;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.dao.GoiDichVuDAO;

public class GoiDichVuBUS {

  private GoiDichVuDAO dao;

  public GoiDichVuBUS() {
    dao = new GoiDichVuDAO();
  }

  public ArrayList<GoiDichVuDTO> getAll() {
    return dao.getAll();
  }

  public boolean insert(GoiDichVuDTO g) {
    if (g.getMaGoi().trim().isEmpty() || g.getTenGoi().trim().isEmpty()) {
      System.out.println("Không được để trống dữ liệu!");
      return false;
    }
    BigDecimal zero = new BigDecimal(0);
    if (g.getGiaDichVu().compareTo(zero) < 0) {
      System.out.println("Giá dịch vụ phải lớn hơn 0!");
      return false;
    }

    if (dao.existsMaGoi(g.getMaGoi())) {
      System.out.println("Mã gói đã tồn tại!");
      return false;
    }

    return dao.insertGoiDichVu(g);
  }

  public boolean update(GoiDichVuDTO g) {
    if (!dao.existsMaGoi(g.getMaGoi())) {
      System.out.println("Không tìm thấy mã để cập nhật!");
      return false;
    }

    return dao.updateGoiDichVu(g);
  }

  public boolean delete(String maGoi) {
    if (!dao.existsMaGoi(maGoi)) {
      System.out.println("Mã không tồn tại!");
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
    if (maGoi == null || maGoi.trim().isEmpty()) return null;
    return dao.getByMaGoi(maGoi);
  }
}
