package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.PermissionsDTO;
import phongkham.dao.PermissionsDAO;

public class PermissionBUS {

  private ArrayList<String> listPerm;
  private PermissionsDAO permissionDAO;

  public PermissionBUS() {
    listPerm = new ArrayList<>();
    permissionDAO = new PermissionsDAO();
  }

  //load quyền khi login vào
  public void loadPermission(String UserID) {
    listPerm = permissionDAO.getPermissionByUser(UserID);
  }

  //kiểm tra quyền
  public boolean hasPerm(String Permission) {
    for (String p : listPerm) {
      if (p.equals(Permission)) {
        return true;
      }
    }
    return false;
  }

  public ArrayList<String> getListPermission() {
    return listPerm;
  }

  // ========== METHODS CHO QuanLyPhanQuyenPanel ==========

  /**
   * Lấy tất cả Permission trong hệ thống
   * @return ArrayList<PermissionsDTO>
   */
  public ArrayList<PermissionsDTO> getAllPermissions() {
    ArrayList<PermissionsDTO> list = permissionDAO.getAllPermissions();
    return (list != null) ? list : new ArrayList<>();
  }

  /**
   * Lấy Permission theo ID
   * @param permissionId - Mã Permission
   * @return PermissionsDTO hoặc null
   */
  public PermissionsDTO getPermissionById(String permissionId) {
    return permissionDAO.getPermissionById(permissionId);
  }
}
