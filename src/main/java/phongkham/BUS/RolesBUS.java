package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.RolesDTO;
import phongkham.dao.RolesDAO;

public class RolesBUS {

  private RolesDAO rolesDAO = new RolesDAO();

  public ArrayList<RolesDTO> getAllRoles() {
    return rolesDAO.getAllRoles();
  }

  public String insertRoles(RolesDTO rl) {
    if (rl.getSTT().trim().isEmpty() || rl.getTenVaiTro().trim().isEmpty()) {
      return "Không được để trống dữ liệu";
    }
    // Kiểm tra trùng STT
    if (rolesDAO.getById(rl.getSTT()) != null) {
      return "STT đã tồn tại";
    }
    boolean result = rolesDAO.insertRoles(rl);
    if (result) {
      return "Thêm thành công";
    }
    return "Thêm thất bại";
  }

  public String updateRoles(RolesDTO rl) {
    if (rl.getTenVaiTro().trim().isEmpty()) {
      return "Tên vai trò không được để trống";
    }
    if (rolesDAO.getById(rl.getSTT()) == null) {
      return "Vai trò không tồn tại";
    }
    boolean result = rolesDAO.updateRoles(rl);
    if (result) {
      return "Cập nhật thành công";
    }
    return "Cập nhật thất bại";
  }

  public String deleteRoles(String STT) {
    RolesDTO rl = rolesDAO.getById(STT);
    if (rl == null) {
      return "Vai trò không tồn tại";
    }
    boolean result = rolesDAO.deleteRoles(rl);
    if (result) {
      return "Xóa thành công";
    }
    return "Xóa thất bại";
  }

  public RolesDTO getById(String STT) {
    return rolesDAO.getById(STT);
  }

  // ========== WRAPPER METHODS CHO QuanLyPhanQuyenPanel ==========

  /**
   * Lấy Role theo ID (alias cho getById)
   */
  public RolesDTO getRoleById(String roleId) {
    return getById(roleId);
  }

  /**
   * Thêm Role - trả về boolean
   */
  public boolean addRole(RolesDTO role) {
    String result = insertRoles(role);
    return result.equals("Thêm thành công");
  }

  /**
   * Cập nhật Role - trả về boolean
   */
  public boolean updateRole(RolesDTO role) {
    String result = updateRoles(role);
    return result.equals("Cập nhật thành công");
  }

  /**
   * Xóa Role - trả về boolean
   */
  public boolean deleteRole(String roleId) {
    String result = deleteRoles(roleId);
    return result.equals("Xóa thành công");
  }
}
