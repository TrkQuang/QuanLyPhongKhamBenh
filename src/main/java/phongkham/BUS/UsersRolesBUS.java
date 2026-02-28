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
}
