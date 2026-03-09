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
    boolean rs = urlDAO.Insert(url);
    if (rs) return "Gán vai trò thành công";
    else return "Gán vai trò thất bại";
  }

  //xóa roles khỏi user
  public String deleteRole(String userID, String roleID) {
    if (userID.trim().isEmpty() || roleID.trim().isEmpty()) {
      return "Dữ liệu không được để trống";
    }
    boolean rs = urlDAO.Delete(userID, roleID);
    if (rs) return "Xóa vai trò thành công";
    else return "Xóa vai trò thất bại";
  }

  // Lấy role theo user
  public ArrayList<UsersRolesDTO> getRolesByUser(String userID) {
    return urlDAO.getRolesByUser(userID);
  }

  // ========== WRAPPER METHODS CHO QuanLyPhanQuyenPanel ==========

  /**
   * Kiểm tra User đã có Role chưa
   * @param userId - Mã User
   * @param roleId - Mã Role
   * @return true nếu đã có
   */
  public boolean hasUserRole(String userId, String roleId) {
    ArrayList<UsersRolesDTO> roles = getRolesByUser(userId);
    for (UsersRolesDTO ur : roles) {
      if (ur.getRole_ID().equals(roleId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gán Role cho User - trả về boolean
   * @param userRole - UsersRolesDTO
   * @return true nếu thành công
   */
  public boolean addUserRole(UsersRolesDTO userRole) {
    String result = assignRole(userRole.getUser_ID(), userRole.getRole_ID());
    return result.equals("Gán vai trò thành công");
  }

  /**
   * Xóa Role khỏi User - trả về boolean
   * @param userId - Mã User
   * @param roleId - Mã Role
   * @return true nếu thành công
   */
  public boolean deleteUserRole(String userId, String roleId) {
    String result = deleteRole(userId, roleId);
    return result.equals("Xóa vai trò thành công");
  }
}
