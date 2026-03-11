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

  // kiểm tra quyền
  public boolean hasPerm(String permission) {
    return listPerm.contains(permission);
  }

  public ArrayList<String> getListPermission() {
    return listPerm;
  }

  // Lấy tất cả Permission trong hệ thống
  public ArrayList<PermissionsDTO> getAllPermissions() {
    ArrayList<PermissionsDTO> list = permissionDAO.getAllPermissions();
    return (list != null) ? list : new ArrayList<>();
  }

  // Lấy Permission theo ID
  public PermissionsDTO getPermissionById(String permissionId) {
    return permissionDAO.getPermissionById(permissionId);
  }
}
