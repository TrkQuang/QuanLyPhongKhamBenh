package phongkham.BUS;

import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.dao.CTPhieuNhapDAO;

import java.util.ArrayList;

public class CTPhieuNhapBUS {

    private CTPhieuNhapDAO ctDAO;
    private ArrayList<CTPhieuNhapDTO> list;

    public CTPhieuNhapBUS() {
        ctDAO = new CTPhieuNhapDAO();
        list = ctDAO.getAll();
    }

    // ===== LOAD DANH SÁCH =====
    public ArrayList<CTPhieuNhapDTO> getAll() {
        list = ctDAO.getAll();
        return list;
    }

    // ===== LẤY THEO MÃ PHIẾU NHẬP =====
    public ArrayList<CTPhieuNhapDTO> getByMaPhieuNhap(String maPN) {
        return ctDAO.getByMaPhieuNhap(maPN);
    }

    // ===== THÊM =====
    public boolean insert(CTPhieuNhapDTO ct) {
        if (ct == null) return false;
        if (ct.getSoLuongNhap() <= 0) return false;
        if (ct.getDonGiaNhap().doubleValue() <= 0) return false;

        boolean ok = ctDAO.Insert(ct);
        if (ok) list.add(ct);

        return ok;
    }

    // ===== XÓA =====
    public boolean delete(String maCTPN) {
        boolean ok = ctDAO.Delete(maCTPN, null);
        if (ok) {
            list.removeIf(ct -> ct.getMaCTPN().equals(maCTPN));
        }
        return ok;
    }

    // ===== CẬP NHẬT =====
    public boolean update(CTPhieuNhapDTO ct) {
        boolean ok = ctDAO.Update(ct);
        if (ok) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMaCTPN().equals(ct.getMaCTPN())) {
                    list.set(i, ct);
                    break;
                }
            }
        }
        return ok;
    }

    // ===== TÌM THEO MÃ =====
    public CTPhieuNhapDTO search(String maCTPN) {
        return ctDAO.Search(maCTPN);
    }

    // ===== TÍNH TỔNG TIỀN PHIẾU NHẬP =====
    public double tinhTongTien(String maPN) {
        double tong = 0;
        for (CTPhieuNhapDTO ct : getByMaPhieuNhap(maPN)) {
            tong += ct.getSoLuongNhap() * ct.getDonGiaNhap().doubleValue();
        }
        return tong;
    }
}
