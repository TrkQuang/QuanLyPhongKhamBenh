package phongkham.Utils;

import java.util.ArrayList;
import phongkham.DTO.UsersDTO;

/**
 * Class quản lý phiên đăng nhập của user hiện tại
 * Lưu trữ thông tin user và permissions sau khi đăng nhập thành công
 */
public class Session {

  // User hiện tại đang đăng nhập
  public static UsersDTO currentUser = null;

  // Danh sách quyền hạn của user hiện tại (tên quyền)
  public static ArrayList<String> currentPermissions = null;

  /**
   * Đăng nhập: lưu thông tin user vào session
   */
  public static void login(UsersDTO user) {
    currentUser = user;
    currentPermissions = new ArrayList<>();
  }

  /**
   * Đăng xuất: xóa toàn bộ thông tin session
   */
  public static void logout() {
    currentUser = null;
    currentPermissions = null;
  }

  /**
   * Kiểm tra user đã đăng nhập chưa
   */
  public static boolean isLoggedIn() {
    return currentUser != null;
  }

  /**
   * Lấy UserID của user hiện tại
   */
  public static String getCurrentUserID() {
    return currentUser != null ? currentUser.getUserID() : null;
  }

  /**
   * Lấy Username của user hiện tại
   */
  public static String getCurrentUsername() {
    return currentUser != null ? currentUser.getUsername() : null;
  }

  /**
   * Kiểm tra user có quyền hạn cụ thể không
   * (Sử dụng sau khi đã load permissions)
   */
  public static boolean hasPermission(String permissionName) {
    if (currentPermissions == null) return false;

    for (String perm : currentPermissions) {
      if (perm.equalsIgnoreCase(permissionName)) {
        return true;
      }
    }
    return false;
  }
}
