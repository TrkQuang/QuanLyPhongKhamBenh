package phongkham.BUS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.dao.CTPhieuNhapDAO;
import phongkham.dao.PhieuNhapDAO;
import phongkham.dao.ThuocDAO;

public class CTPhieuNhapBUS {

  private ThuocDAO thuocDAO;
  private PhieuNhapDAO phieuNhapDAO;
  private CTPhieuNhapDAO ctDAO;

  public CTPhieuNhapBUS() {
    ctDAO = new CTPhieuNhapDAO();
    thuocDAO = new ThuocDAO();
    phieuNhapDAO = new PhieuNhapDAO();
  }

  // ================= LOAD =================
  public ArrayList<CTPhieuNhapDTO> getByMaPhieuNhap(String maPN) {
    if (maPN == null || maPN.trim().isEmpty()) return new ArrayList<>();
    return ctDAO.getByMaPhieuNhap(maPN);
  }

  // ================= THÊM =================
  public boolean insert(
    String maCTPN,
    String maPN,
    String maThuoc,
    int soLuong,
    BigDecimal donGia,
    LocalDateTime hanSuDung
  ) {
    PhieuNhapDTO pn = phieuNhapDAO.getById(maPN);

    if (pn == null) return false;

    // chỉ cho thêm khi CHUA_DUYET
    if (!"CHUA_DUYET".equals(pn.getTrangThai())) return false;

    CTPhieuNhapDTO ct = new CTPhieuNhapDTO();

    ct.setMaCTPN(maCTPN);
    ct.SetMaPhieuNhap(maPN);
    ct.setMaThuoc(maThuoc);
    ct.setSoLuong(soLuong);
    ct.setDonGiaNhap(donGia);
    ct.setHanSuDung(hanSuDung);

    if (!validate(ct)) return false;

    boolean result = ctDAO.insert(ct);

    if (result) capNhatTongTien(maPN);

    return result;
  }

  // ================= SỬA =================
  public boolean update(String maCTPN, int soLuong, BigDecimal donGia) {
    CTPhieuNhapDTO ct = ctDAO.search(maCTPN);

    if (ct == null) return false;

    PhieuNhapDTO pn = phieuNhapDAO.getById(ct.getMaPhieuNhap());

    // Chỉ cho sửa khi CHUA_DUYET
    if (!"CHUA_DUYET".equals(pn.getTrangThai())) return false;

    ct.setSoLuong(soLuong);
    ct.setDonGiaNhap(donGia);

    if (!validate(ct)) return false;

    boolean result = ctDAO.update(ct);

    if (result) capNhatTongTien(ct.getMaPhieuNhap());

    return result;
  }

  // ================= XÓA =================
  public boolean delete(String maCTPN) {
    CTPhieuNhapDTO ct = ctDAO.search(maCTPN);

    if (ct == null) return false;

    PhieuNhapDTO pn = phieuNhapDAO.getById(ct.getMaPhieuNhap());

    // Chỉ cho xóa khi CHUA_DUYET
    if (!"CHUA_DUYET".equals(pn.getTrangThai())) return false;

    boolean result = ctDAO.delete(maCTPN);

    if (result) capNhatTongTien(ct.getMaPhieuNhap());

    return result;
  }

  // ================= TÍNH TỔNG =================
  public BigDecimal tinhTongTien(String maPN) {
    BigDecimal tong = BigDecimal.ZERO;

    for (CTPhieuNhapDTO ct : getByMaPhieuNhap(maPN)) {
      BigDecimal thanhTien = ct
        .getDonGiaNhap()
        .multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));

      tong = tong.add(thanhTien);
    }

    return tong;
  }

  // ================= CẬP NHẬT TỔNG TIỀN VỀ PHIẾU =================
  private void capNhatTongTien(String maPN) {
    BigDecimal tong = tinhTongTien(maPN);
    phieuNhapDAO.updateTongTien(maPN, tong);
  }

  // ================= NHẬP KHO =================
  public boolean xacNhanNhapKho(String maPN) {
    try {
      PhieuNhapDTO pn = phieuNhapDAO.getById(maPN);

      if (pn == null) return false;

      if ("DA_NHAP".equals(pn.getTrangThai())) return false;

      if (!"DA_DUYET".equals(pn.getTrangThai())) return false;

      ArrayList<CTPhieuNhapDTO> list = getByMaPhieuNhap(maPN);

      for (CTPhieuNhapDTO ct : list) {
        boolean updated = thuocDAO.updateSoLuong(
          ct.getMaThuoc(),
          ct.getSoLuongNhap()
        );

        if (!updated) return false;
      }

      return phieuNhapDAO.capNhatTrangThai(maPN, "DA_NHAP");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  // ================= VALIDATE =================
  private boolean validate(CTPhieuNhapDTO ct) {
    if (ct == null) return false;

    if (
      ct.getMaPhieuNhap() == null || ct.getMaPhieuNhap().trim().isEmpty()
    ) return false;

    if (
      ct.getMaThuoc() == null || ct.getMaThuoc().trim().isEmpty()
    ) return false;

    if (ct.getSoLuongNhap() <= 0) return false;

    if (
      ct.getDonGiaNhap() == null ||
      ct.getDonGiaNhap().compareTo(BigDecimal.ZERO) <= 0
    ) return false;

    return true;
  }
}
