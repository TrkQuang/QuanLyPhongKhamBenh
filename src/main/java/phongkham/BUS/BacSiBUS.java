package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.BacSiDTO;
import phongkham.dao.BacSiDAO;

public class BacSiBUS {

  private BacSiDAO dao = new BacSiDAO();

  // Lấy tất cả bác sĩ
  public ArrayList<BacSiDTO> getAll() {
    return dao.getAll();
  }

  // Thêm bác sĩ mới
  public boolean add(BacSiDTO bs) {
    if (bs == null || bs.getHoTen().trim().isEmpty()) {
      System.out.println("Tên bác sĩ không được để trống");
      return false;
    }
    if (bs.getMaKhoa().trim().isEmpty()) {
      System.out.println("Mã khoa không được để trống");
      return false;
    }
    return dao.insertBacSi(bs);
  }

  // Cập nhật thông tin bác sĩ
  public boolean update(BacSiDTO bs) {
    if (bs == null || bs.getHoTen().trim().isEmpty()) {
      System.out.println("Tên bác sĩ không được để trống");
      return false;
    }
    if (bs.getMaKhoa().trim().isEmpty()) {
      System.out.println("Mã khoa không được để trống");
      return false;
    }
    return dao.updateBacSi(bs);
  }

  // Xóa bác sĩ theo ID
  public boolean delete(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      System.out.println("Mã bác sĩ không được để trống");
      return false;
    }
    return dao.deleteMaBacSi(maBacSi);
  }

  // Tìm bác sĩ theo ID
  public BacSiDTO getById(String maBacSi) {
    ArrayList<BacSiDTO> list = dao.getAll();
    for (BacSiDTO bs : list) {
      if (bs.getMaBacSi().equals(maBacSi)) {
        return bs;
      }
    }
    return null;
  }

  // Tìm bác sĩ theo khoa
  public ArrayList<BacSiDTO> getByKhoa(String maKhoa) {
    ArrayList<BacSiDTO> result = new ArrayList<>();
    ArrayList<BacSiDTO> list = dao.getAll();

    for (BacSiDTO bs : list) {
      if (bs.getMaKhoa().equals(maKhoa)) {
        result.add(bs);
      }
    }
    return result;
  }

  // Tìm bác sĩ theo tên
  public ArrayList<BacSiDTO> searchByName(String keyword) {
    ArrayList<BacSiDTO> result = new ArrayList<>();
    ArrayList<BacSiDTO> list = dao.getAll();
    String search = keyword.toLowerCase();

    for (BacSiDTO bs : list) {
      if (bs.getHoTen().toLowerCase().contains(search)) {
        result.add(bs);
      }
    }
    return result;
  }

  // Đếm tổng số bác sĩ
  public int countAll() {
    return dao.getAll().size();
  }

  // Đếm bác sĩ theo khoa
  public int countByKhoa(String maKhoa) {
    return getByKhoa(maKhoa).size();
  }

  // Tìm bác sĩ theo email
  public BacSiDTO getByEmail(String email) {
    ArrayList<BacSiDTO> list = dao.getAll();
    for (BacSiDTO bs : list) {
      if (bs.getEmail() != null && bs.getEmail().equals(email)) {
        return bs;
      }
    }
    return null;
  }
}
