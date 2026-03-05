package phongkham.BUS;

import phongkham.DTO.KhoaDTO;
import phongkham.dao.KhoaDAO;
import java.util.ArrayList;

public class KhoaBUS {
    private KhoaDAO dao = new KhoaDAO();

    // Lay danh sach tat ca khoa
    public ArrayList<KhoaDTO> getAll() {
        return dao.getAll();
    }

    // Them khoa moi
    public boolean add(KhoaDTO khoa) {
        if (khoa == null || khoa.getTenKhoa().trim().isEmpty()) {
            System.out.println("Ten khoa khong duoc rong");
            return false;
        }

        if (khoa.getMaKhoa().trim().isEmpty()) {
            System.out.println("Ma khoa khong duoc rong");
            return false;
        }

        if (getById(khoa.getMaKhoa()) != null) {
            System.out.println("Ma khoa da ton tai: " + khoa.getMaKhoa());
            return false;
        }

        boolean result = dao.insertKhoa(khoa);
        if (result) {
            System.out.println("Them khoa thanh cong: " + khoa.getTenKhoa());
        }
        return result;
    }

    // Cap nhat thong tin khoa
    public boolean update(KhoaDTO khoa) {
        if (khoa == null || khoa.getTenKhoa().trim().isEmpty()) {
            System.out.println("Ten khoa khong duoc rong");
            return false;
        }

        if (khoa.getMaKhoa().trim().isEmpty()) {
            System.out.println("Ma khoa khong duoc rong");
            return false;
        }

        if (getById(khoa.getMaKhoa()) == null) {
            System.out.println("Khoa khong ton tai: " + khoa.getMaKhoa());
            return false;
        }

        boolean result = dao.updateKhoa(khoa);
        if (result) {
            System.out.println("Cap nhat khoa thanh cong: " + khoa.getTenKhoa());
        }
        return result;
    }

    // Xoa khoa theo ma
    public boolean delete(String maKhoa) {
        if (maKhoa == null || maKhoa.trim().isEmpty()) {
            System.out.println("Ma khoa khong duoc rong");
            return false;
        }

        KhoaDTO khoa = getById(maKhoa);
        if (khoa == null) {
            System.out.println("Khoa khong ton tai: " + maKhoa);
            return false;
        }

        boolean result = dao.deleteKhoa(maKhoa);
        if (result) {
            System.out.println("Xoa khoa thanh cong: " + khoa.getTenKhoa());
        }
        return result;
    }

    // Tim khoa theo ma
    public KhoaDTO getById(String maKhoa) {
        ArrayList<KhoaDTO> list = dao.getAll();
        for (KhoaDTO khoa : list) {
            if (khoa.getMaKhoa().equals(maKhoa)) {
                return khoa;
            }
        }
        return null;
    }

    // Tim khoa theo ten (chua tu khoa)
    public ArrayList<KhoaDTO> searchByName(String keyword) {
        ArrayList<KhoaDTO> result = new ArrayList<>();
        ArrayList<KhoaDTO> list = dao.getAll();
        String search = keyword.toLowerCase();

        for (KhoaDTO khoa : list) {
            if (khoa.getTenKhoa().toLowerCase().contains(search)) {
                result.add(khoa);
            }
        }
        return result;
    }

    // Dem tong so khoa
    public int countAll() {
        return dao.getAll().size();
    }

    // Kiem tra khoa co ton tai khong
    public boolean exists(String maKhoa) {
        return getById(maKhoa) != null;
    }
}