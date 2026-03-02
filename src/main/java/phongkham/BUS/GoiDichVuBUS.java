package phongkham.BUS;

import java.math.BigDecimal;
import java.util.ArrayList;
import phongkham.dao.GoiDichVuDAO;
import phongkham.DTO.GoiDichVuDTO;

public class GoiDichVuBUS {

    private GoiDichVuDAO dao;

    public GoiDichVuBUS() {
        dao = new GoiDichVuDAO();
    }

    public ArrayList<GoiDichVuDTO> getAll() {
        return dao.getAll();
    }

    public boolean insert(GoiDichVuDTO g) {

        if (g.getMaGoi().trim().isEmpty() ||
            g.getTenGoi().trim().isEmpty()) {
            System.out.println("Khong duoc de trong du lieu!");
            return false;
        }
        BigDecimal zero = new BigDecimal(0);
        if (g.getGiaDichVu().compareTo(zero) < 0) {
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

        if (!dao.existsMaGoi(g.getMaGoi())) {
            System.out.println("Khong tim thay ma de cap nhat!");
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
}
