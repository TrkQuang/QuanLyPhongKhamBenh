package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.BacSiDTO;
import phongkham.dao.BacSiDAO;

public class BacSiBUS {

  private BacSiDAO dao = new BacSiDAO();

  // Lay danh sach tat ca bac si
  public ArrayList<BacSiDTO> getAll() {
    return dao.getAll();
  }

  // Them bac si moi
  public boolean add(BacSiDTO bs) {
    if (!isValidBacSi(bs)) {
      return false;
    }

    boolean result = dao.insertBacSi(bs);
    if (result) {
      System.out.println("Them bac si thanh cong: " + bs.getHoTen());
    }
    return result;
  }

  // Cap nhat thong tin bac si
  public boolean update(BacSiDTO bs) {
    if (!isValidBacSi(bs)) {
      return false;
    }

    boolean result = dao.updateBacSi(bs);
    if (result) {
      System.out.println("Cap nhat bac si thanh cong: " + bs.getHoTen());
    }
    return result;
  }

  // Xoa bac si theo ID
  public boolean delete(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      System.out.println("Ma bac si khong duoc rong");
      return false;
    }

    boolean result = dao.deleteMaBacSi(maBacSi);
    if (result) {
      System.out.println("Xoa bac si thanh cong");
    }
    return result;
  }

  // Tim bac si theo ID
  public BacSiDTO getById(String maBacSi) {
    return findFirstByMaBacSi(maBacSi);
  }

  // Tim bac si theo khoa
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

  // Tim bac si theo ten
  public ArrayList<BacSiDTO> searchByName(String keyword) {
    ArrayList<BacSiDTO> result = new ArrayList<>();
    ArrayList<BacSiDTO> list = dao.getAll();
    String search = keyword == null ? "" : keyword.toLowerCase();

    for (BacSiDTO bs : list) {
      String hoTen = bs.getHoTen() == null ? "" : bs.getHoTen().toLowerCase();
      if (hoTen.contains(search)) {
        result.add(bs);
      }
    }
    return result;
  }

  // Dem tong so bac si
  public int countAll() {
    return dao.getAll().size();
  }

  // Dem bac si theo khoa
  public int countByKhoa(String maKhoa) {
    return getByKhoa(maKhoa).size();
  }

  // Tim bac si theo email
  public BacSiDTO getByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }
    ArrayList<BacSiDTO> list = dao.getAll();
    for (BacSiDTO bs : list) {
      if (email.equals(bs.getEmail())) {
        return bs;
      }
    }
    return null;
  }

  private boolean isValidBacSi(BacSiDTO bs) {
    if (bs == null || bs.getHoTen() == null || bs.getHoTen().trim().isEmpty()) {
      System.out.println("Ten bac si khong duoc rong");
      return false;
    }
    if (bs.getMaKhoa() == null || bs.getMaKhoa().trim().isEmpty()) {
      System.out.println("Ma khoa khong duoc rong");
      return false;
    }
    return true;
  }

  private BacSiDTO findFirstByMaBacSi(String maBacSi) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      return null;
    }
    ArrayList<BacSiDTO> list = dao.getAll();
    for (BacSiDTO bs : list) {
      if (maBacSi.equals(bs.getMaBacSi())) {
        return bs;
      }
    }
    return null;
  }
}
