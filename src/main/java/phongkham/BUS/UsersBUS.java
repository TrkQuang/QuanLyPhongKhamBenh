package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.UsersDTO;
import phongkham.dao.UsersDAO;

public class UsersBUS {

  private UsersDAO userDAO = new UsersDAO();

  //=========Login==========
  public UsersDTO login(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return null;
    }
    return userDAO.checkLogin(username, password);
  }

  //=======GET ALL=========
  public ArrayList<UsersDTO> getAllUsers() {
    return userDAO.getAll();
  }

  //===========Thêm USER==========
  public String insertUser(UsersDTO u) {
    if (u.getUsername().trim().isEmpty() || u.getPassword().trim().isEmpty()) {
      return "Tên TK và MK ko được trống!";
    }
    for (UsersDTO a : userDAO.getAll()) {
      if (u.getUsername().equalsIgnoreCase(a.getUsername())) {
        return "Tên tài khoản đã tồn tại";
      }
    }
    boolean result = userDAO.insertUser(u);
    if (result) return "Thêm thành công";
    else return "Thêm thất bại";
  }

  //=========CHỈNH SỬA USER===========
  public String updateUser(UsersDTO u) {
    if (u.getUsername().trim().isEmpty()) {
      return "Tên tài khoản ko được trống";
    }
    boolean result = userDAO.updateUser(u);
    if (result) return "Thêm thành công";
    else return "Thất bại!";
  }

  //==========XÓA USER==========
  public String deleteUser(int uID) {
    boolean rs = userDAO.deleteUser(uID);
    if (rs) return "Xóa thành công";
    else return "Xóa thất bại";
  }

  //=========LẤY THEO ID===========
  public UsersDTO getUserByID(String UID) {
    return userDAO.getUserByID(UID);
  }
}
