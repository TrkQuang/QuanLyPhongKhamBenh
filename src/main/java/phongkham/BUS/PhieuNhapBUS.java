package phongkham.BUS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.Utils.StatusNormalizer;
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
    if (pn.getTrangThai() == null || pn.getTrangThai().trim().isEmpty()) {
      pn.setTrangThai(StatusNormalizer.CHO_DUYET);
    } else {
      pn.setTrangThai(
        StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai())
      );
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
    pn.setTrangThai(
      StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai())
    );
    return dao.update(pn);
  }

  public boolean delete(String maPhieuNhap) {
    PhieuNhapDTO pn = dao.getById(maPhieuNhap);

    if (pn == null) {
      System.err.println("❌ Không tìm thấy phiếu nhập!");
      return false;
    }

    // ❌ Không cho hủy nếu đã nhập kho
    if (
      StatusNormalizer.DA_NHAP.equals(
        StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai())
      )
    ) {
      System.err.println("❌ Không thể hủy phiếu đã nhập!");
      return false;
    }

    // ❌ Không hủy nếu đã hủy rồi
    if (
      StatusNormalizer.DA_HUY.equals(
        StatusNormalizer.normalizePhieuNhapStatus(pn.getTrangThai())
      )
    ) {
      System.err.println("⚠️ Phiếu đã bị hủy trước đó!");
      return false;
    }

    // ✅ Cập nhật trạng thái thành DA_HUY
    return dao.capNhatTrangThai(maPhieuNhap, StatusNormalizer.DA_HUY);
  }

  public boolean capNhatTrangThai(String maPhieuNhap, String trangThaiMoi) {
    return dao.capNhatTrangThai(
      maPhieuNhap,
      StatusNormalizer.normalizePhieuNhapStatus(trangThaiMoi)
    );
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
    return dao.getByTrangThai(
      StatusNormalizer.normalizePhieuNhapStatus(trangThai)
    );
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
    return dao.getTongTienByTrangThai(
      StatusNormalizer.normalizePhieuNhapStatus(trangThai)
    );
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
