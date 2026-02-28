package phongkham.BUS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.dao.PhieuNhapDAO;

public class PhieuNhapBUS {

  private PhieuNhapDAO dao = new PhieuNhapDAO();

  // ===== CRUD OPERATIONS =====

  public ArrayList<PhieuNhapDTO> getAll() {
    return dao.getAll();
  }

  public PhieuNhapDTO getById(String maPhieuNhap) {
    return dao.getById(maPhieuNhap);
  }

  public boolean insert(PhieuNhapDTO pn) {
    // ✅ Validate
    String error = validate(pn);
    if (error != null) {
      System.err.println("❌ " + error);
      return false;
    }
    return dao.insert(pn);
  }

  public boolean update(PhieuNhapDTO pn) {
    // ✅ Validate
    String error = validate(pn);
    if (error != null) {
      System.err.println("❌ " + error);
      return false;
    }
    return dao.update(pn);
  }

  public boolean delete(String maPhieuNhap) {
    return dao.delete(maPhieuNhap);
  }

  public boolean capNhatTrangThai(String maPhieuNhap, String trangThaiMoi) {
    return dao.capNhatTrangThai(maPhieuNhap, trangThaiMoi);
  }

  // ===== SEARCH OPERATIONS =====

  public ArrayList<PhieuNhapDTO> getByDate(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return dao.getByDate(startDate, endDate);
  }

  public ArrayList<PhieuNhapDTO> getByMaNCC(String maNCC) {
    return dao.getByMaNCC(maNCC);
  }

  public ArrayList<PhieuNhapDTO> getByNguoiGiao(String nguoiGiao) {
    return dao.getByNguoiGiao(nguoiGiao);
  }

  public ArrayList<PhieuNhapDTO> getByTrangThai(String trangThai) {
    return dao.getByTrangThai(trangThai); // ✅ Gọi thẳng DAO, không loop
  }

  public ArrayList<PhieuNhapDTO> search(String keyword) {
    return dao.search(keyword);
  }

  // ===== UTILITY OPERATIONS =====

  public boolean deleteByMaNCC(String maNCC) {
    return dao.deleteByNCC(maNCC);
  }

  public boolean hasPhieuNhap(String maNCC) {
    return dao.hasPhieuNhap(maNCC);
  }

  public double getTongTienByTrangThai(String trangThai) {
    return dao.getTongTienByTrangThai(trangThai);
  }

  // ===== VALIDATION =====

  // ✅ METHOD DUY NHẤT: Validate tất cả
  private String validate(PhieuNhapDTO pn) {
    if (pn == null) return "Phiếu nhập không được null";

    if (
      pn.getMaPhieuNhap() == null || pn.getMaPhieuNhap().trim().isEmpty()
    ) return "Mã phiếu nhập không được trống";

    if (
      pn.getMaNCC() == null || pn.getMaNCC().trim().isEmpty()
    ) return "Mã nhà cung cấp không được trống";

    if (pn.getNgayNhap() == null) return "Ngày nhập không được trống";

    if (
      pn.getNguoiGiao() == null || pn.getNguoiGiao().trim().isEmpty()
    ) return "Người giao không được trống";

    if (pn.getTongTienNhap() < 0) return "Tổng tiền nhập không được âm";

    return null; // ✅ Hợp lệ
  }
}
