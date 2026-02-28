package phongkham.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import phongkham.BUS.PermissionBUS; // ✅ Import BUS
import phongkham.DTO.UsersDTO;

public class Session {

  public static UsersDTO currentUser = null;
  private static Set<String> currentPermissions = null; // ✅ Đổi sang Set

  /**
   * Đăng nhập: lưu user + TỰ ĐỘNG load permissions
   */
  public static void login(UsersDTO user) {
    currentUser = user;

    // ✅ TỰ ĐỘNG LOAD PERMISSIONS QUA BUS
    PermissionBUS permBUS = new PermissionBUS();
    permBUS.loadPermission(user.getUserID());

    // ✅ Convert sang Set (lowercase để dễ compare)
    ArrayList<String> perms = permBUS.getListPermission();
    currentPermissions = new HashSet<>();
    for (String p : perms) {
      currentPermissions.add(p.toLowerCase());
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
    currentPermissions = null;
  }

  public static boolean isLoggedIn() {
    return currentUser != null;
  }

  public static String getCurrentUserID() {
    return currentUser != null ? currentUser.getUserID() : null;
  }

  public static String getCurrentUsername() {
    return currentUser != null ? currentUser.getUsername() : null;
  }

  /**
   * Kiểm tra quyền
   */
  public static boolean hasPermission(String permissionName) {
    if (currentPermissions == null || permissionName == null) {
      return false;
    }
    return currentPermissions.contains(permissionName.toLowerCase());
  }

  /**
   * Debug: In thông tin session
   */
  public static void printInfo() {
    if (!isLoggedIn()) {
      System.out.println("❌ Chưa đăng nhập");
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
