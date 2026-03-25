package phongkham.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import phongkham.BUS.PermissionBUS;
import phongkham.DTO.UsersDTO;

public class Session {

  public static UsersDTO currentUser = null;
  private static Set<String> currentPermissions = null;
  public static String currentBacSiID = null;

  /**
   * Đăng nhập: lưu user + TỰ ĐỘNG load permissions
   */
  public static void login(UsersDTO user) {
    if (user == null) {
      return;
    }
    currentUser = user;

    // TỰ ĐỘNG LOAD PERMISSIONS QUA BUS
    PermissionBUS permBUS = new PermissionBUS();
    permBUS.loadPermission(user.getUserID());

    // Chuẩn hóa quyền theo uppercase để so sánh không phân biệt hoa thường.
    ArrayList<String> perms = permBUS.getListPermission();
    currentPermissions = new HashSet<>();
    for (String p : perms) {
      if (p != null) {
        currentPermissions.add(p.trim().toUpperCase());
      }
    }

    System.out.println(
      "✓ Loaded " +
        currentPermissions.size() +
        " permissions for " +
        user.getUsername()
    );
  }

  public static void logout() {
    currentUser = null;
    currentBacSiID = null;
    currentPermissions = null;
  }

  public static void refreshPermissions() {
    if (currentUser == null) {
      return;
    }
    login(currentUser);
  }

  public static boolean isLoggedIn() {
    return currentUser != null;
  }

  public static String getCurrentUserID() {
    return currentUser != null ? currentUser.getUserID() : null;
  }

  public static void setCurrentBacSiID(String maBacSi) {
    currentBacSiID = maBacSi;
  }

  public static String getCurrentBacSiID() {
    return currentBacSiID;
  }

  public static String getCurrentUsername() {
    return currentUser != null ? currentUser.getUsername() : null;
  }

  public static String getCurrentUserEmail() {
    return currentUser != null ? currentUser.getEmail() : null;
  }

  /**
   * Kiểm tra quyền
   */
  public static boolean hasPermission(String permissionName) {
    if (permissionName == null) {
      return false;
    }

    String normalized = permissionName.trim().toUpperCase();

    // Guest khong can login van duoc dung cac tinh nang co ban.
    if (!isLoggedIn() && isDefaultGuestPermission(normalized)) {
      return true;
    }

    if (currentPermissions == null) {
      return false;
    }

    return currentPermissions.contains(normalized);
  }

  /**
   * Ham kiem tra quyen theo ten ma quyen chi tiet (khuyen nghi su dung cho code moi).
   */
  public static boolean kiemTraQuyen(String maQuyen) {
    return hasPermission(maQuyen);
  }

  /**
   * Kiem tra user hien tai co it nhat mot quyen trong danh sach hay khong.
   */
  public static boolean coMotTrongCacQuyen(String... dsMaQuyen) {
    if (dsMaQuyen == null) {
      return false;
    }
    for (String maQuyen : dsMaQuyen) {
      if (kiemTraQuyen(maQuyen)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isDefaultGuestPermission(String permissionName) {
    return (
      "GUEST_DAT_LICH".equals(permissionName) ||
      "GUEST_MUA_THUOC".equals(permissionName) ||
      "GUEST_TRA_CUU_HO_SO".equals(permissionName)
    );
  }

  /**
   * Debug: In thông tin session
   */
  public static void printInfo() {
    if (!isLoggedIn()) {
      System.out.println("Chưa đăng nhập");
      return;
    }

    System.out.println("========== SESSION INFO ==========");
    System.out.println("User: " + currentUser.getUsername());
    System.out.println(
      "Permissions (" + currentPermissions.size() + "): " + currentPermissions
    );
    System.out.println("==================================");
  }
}
