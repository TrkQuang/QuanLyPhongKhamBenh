package phongkham.BUS;

import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.dao.CTPhieuNhapDAO;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CTPhieuNhapBUS {

    private CTPhieuNhapDAO ctDAO;

    public CTPhieuNhapBUS() {
        ctDAO = new CTPhieuNhapDAO();
    }

    // ================= LOAD TẤT CẢ =================
    public ArrayList<CTPhieuNhapDTO> getAll() {
        return ctDAO.getAll();
    }

    // ================= LẤY THEO MÃ PHIẾU NHẬP =================
    public ArrayList<CTPhieuNhapDTO> getByMaPhieuNhap(String maPN) {
        if (maPN == null || maPN.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return ctDAO.getByMaPhieuNhap(maPN);
    }

    // ================= THÊM =================
    public boolean insert(CTPhieuNhapDTO ct) {

        if (!validate(ct)) return false;

        return ctDAO.Insert(ct);
    }

    // ================= CẬP NHẬT =================
    public boolean update(CTPhieuNhapDTO ct) {

        if (!validate(ct)) return false;

        return ctDAO.Update(ct);
    }

    // ================= XÓA =================
    public boolean delete(String maCTPN) {

        if (maCTPN == null || maCTPN.trim().isEmpty()) {
            return false;
        }

        return ctDAO.Delete(maCTPN);
    }

    // ================= TÌM THEO MÃ =================
    public CTPhieuNhapDTO search(String maCTPN) {

        if (maCTPN == null || maCTPN.trim().isEmpty()) {
            return null;
        }

        return ctDAO.Search(maCTPN);
    }

    // ================= TÍNH TỔNG TIỀN PHIẾU NHẬP =================
    public BigDecimal tinhTongTien(String maPN) {

        BigDecimal tong = BigDecimal.ZERO;

        for (CTPhieuNhapDTO ct : getByMaPhieuNhap(maPN)) {

            if (ct.getDonGiaNhap() != null) {

                BigDecimal thanhTien = ct.getDonGiaNhap()
                        .multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));

                tong = tong.add(thanhTien);
            }
        }

        return tong;
    }

    // ================= VALIDATE =================
    private boolean validate(CTPhieuNhapDTO ct) {

        if (ct == null) return false;

        if (ct.getMaPhieuNhap() == null || ct.getMaPhieuNhap().trim().isEmpty())
            return false;

        if (ct.getSoLuongNhap() <= 0)
            return false;

        if (ct.getDonGiaNhap() == null 
                || ct.getDonGiaNhap().compareTo(BigDecimal.ZERO) <= 0)
            return false;

        return true;
    }
}