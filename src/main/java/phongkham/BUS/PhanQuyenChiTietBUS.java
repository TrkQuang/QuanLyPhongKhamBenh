package phongkham.BUS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import phongkham.DTO.RolesDTO;
import phongkham.dao.PhanQuyenChiTietDAO;

public class PhanQuyenChiTietBUS {

  private final PhanQuyenChiTietDAO dao = new PhanQuyenChiTietDAO();

  /**
   * Lay tat ca vai tro de do len ma tran.
   */
  public ArrayList<RolesDTO> layTatCaVaiTro() {
    return dao.layTatCaVaiTro();
  }

  /**
   * Lay tat ca ma quyen chi tiet (VD: BENHNHAN_XEM).
   */
  public ArrayList<String> layTatCaMaQuyenChiTiet() {
    return dao.layTatCaMaQuyenChiTiet();
  }

  /**
   * Lay map mo ta theo ma quyen de hien thi giao dien de hieu hon.
   */
  public Map<String, String> layMoTaTheoMaQuyen() {
    return dao.layMoTaTheoMaQuyen();
  }

  /**
   * Lay map role -> tap ma quyen da cap.
   */
  public Map<Integer, Set<String>> layMapQuyenTheoRole() {
    return dao.layMapQuyenTheoRole();
  }

  /**
   * Tick/bo tick 1 o trong ma tran quyen.
   */
  public boolean capNhatMotQuyenChoRole(
    int roleId,
    String maQuyen,
    boolean duocCap
  ) {
    if (roleId <= 0 || maQuyen == null || maQuyen.trim().isEmpty()) {
      return false;
    }
    return dao.capNhatMotQuyenChoRole(
      roleId,
      maQuyen.trim().toUpperCase(),
      duocCap
    );
  }

  /**
   * Ghi de toan bo quyen cua 1 role.
   */
  public boolean capNhatToanBoQuyenRole(
    int roleId,
    List<String> danhSachMaQuyen
  ) {
    if (roleId <= 0) {
      return false;
    }
    ArrayList<String> normalized = new ArrayList<>();
    if (danhSachMaQuyen != null) {
      for (String maQuyen : danhSachMaQuyen) {
        if (maQuyen != null && !maQuyen.trim().isEmpty()) {
          normalized.add(maQuyen.trim().toUpperCase());
        }
      }
    }
    return dao.capNhatToanBoQuyenRole(roleId, normalized);
  }

  /**
   * Kiem tra schema moi da ton tai chua.
   */
  public boolean daCoBangPhanQuyenChiTiet() {
    return dao.daCoBangPhanQuyenChiTiet();
  }

  /**
   * Dam bao 1 ma quyen chi tiet da ton tai trong schema moi.
   */
  public boolean damBaoQuyenChiTiet(
    String tenPermission,
    String hanhDong,
    String maQuyen,
    String moTaModule
  ) {
    if (
      tenPermission == null ||
      tenPermission.trim().isEmpty() ||
      hanhDong == null ||
      hanhDong.trim().isEmpty() ||
      maQuyen == null ||
      maQuyen.trim().isEmpty()
    ) {
      return false;
    }
    return dao.damBaoQuyenChiTiet(
      tenPermission.trim().toUpperCase(),
      hanhDong.trim().toUpperCase(),
      maQuyen.trim().toUpperCase(),
      moTaModule == null ? "" : moTaModule.trim()
    );
  }
}
