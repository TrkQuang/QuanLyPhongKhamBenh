package phongkham.BUS;

import phongkham.dao.BacSiDAO;
import phongkham.DTO.BacSiDTO;
import java.util.ArrayList;

public class BacSiBUS {
    private BacSiDAO dao = new BacSiDAO();

    // Lay danh sach tat ca bac si
    public ArrayList<BacSiDTO> getAll() {
        return dao.getAll();
    }

    // Them bac si moi
    public boolean add(BacSiDTO bs) {
        if (bs == null || bs.getHoTen().trim().isEmpty()) {
            System.out.println("Ten bac si khong duoc rong");
            return false;
        }

        if (bs.getMaKhoa().trim().isEmpty()) {
            System.out.println("Ma khoa khong duoc rong");
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
        if (bs == null || bs.getHoTen().trim().isEmpty()) {
            System.out.println("Ten bac si khong duoc rong");
            return false;
        }

        if (bs.getMaKhoa().trim().isEmpty()) {
            System.out.println("Ma khoa khong duoc rong");
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
        ArrayList<BacSiDTO> list = dao.getAll();
        for (BacSiDTO bs : list) {
            if (bs.getMaBacSi().equals(maBacSi)) {
                return bs;
            }
        }
        return null;
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
        String search = keyword.toLowerCase();

        for (BacSiDTO bs : list) {
            if (bs.getHoTen().toLowerCase().contains(search)) {
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
        ArrayList<BacSiDTO> list = dao.getAll();
        for (BacSiDTO bs : list) {
            if (bs.getEmail() != null && bs.getEmail().equals(email)) {
                return bs;
            }
        }
        return null;
    }
}