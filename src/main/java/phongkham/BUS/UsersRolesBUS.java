package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.UsersRolesDTO;
import phongkham.dao.UsersRolesDAO;

public class UsersRolesBUS {

  private UsersRolesDAO urlDAO = new UsersRolesDAO();

  //gán roles cho user
  public String assignRole(String userID, String roleID) {
    if (userID.trim().isEmpty() || roleID.trim().isEmpty()) {
      return "Dữ liệu không được để trống";
    }
    UsersRolesDTO url = new UsersRolesDTO(userID, roleID);
    boolean rs = urlDAO.insert(url);
    if (rs) return "Gán vai trò thành công";
    else return "Gán vai trò thất bại";
  }

  //xóa roles khỏi user
  public String deleteRole(String userID, String roleID) {
    if (userID.trim().isEmpty() || roleID.trim().isEmpty()) {
      return "Dữ liệu không được để trống";
    }
    boolean rs = urlDAO.delete(userID, roleID);
    if (rs) return "Xóa vai trò thành công";
    else return "Xóa vai trò thất bại";
  }

  // Lấy role theo user
  public ArrayList<UsersRolesDTO> getRolesByUser(String userID) {
    return urlDAO.getRolesByUser(userID);
  }

  // ========== WRAPPER METHODS CHO QuanLyPhanQuyenPanel ==========

  public boolean hasUserRole(String userId, String roleId) {
    ArrayList<UsersRolesDTO> roles = getRolesByUser(userId);
    for (UsersRolesDTO ur : roles) {
      if (ur.getRole_ID().equals(roleId)) return true;
    }
    return false;
  }

  public boolean addUserRole(UsersRolesDTO userRole) {
    return assignRole(userRole.getUser_ID(), userRole.getRole_ID()).equals(
      "Gán vai trò thành công"
    );
  }

  public boolean deleteUserRole(String userId, String roleId) {
    return deleteRole(userId, roleId).equals("Xóa vai trò thành công");
  }
}
