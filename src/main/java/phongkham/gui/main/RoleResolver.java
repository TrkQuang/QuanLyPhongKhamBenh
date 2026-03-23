package phongkham.gui.main;

import phongkham.Utils.Session;

public final class RoleResolver {

  private RoleResolver() {}

  public static UserMode resolve() {
    if (!Session.isLoggedIn()) {
      return UserMode.GUEST;
    }
    if (
      Session.hasPermission("ROLE_PERMISSION_MANAGE") ||
      Session.hasPermission("USER_MANAGE")
    ) {
      return UserMode.ADMIN;
    }
    if (
      Session.hasPermission("THUOC_VIEW") ||
      Session.hasPermission("PHIEUNHAP_VIEW") ||
      Session.hasPermission("HOADONTHUOC_VIEW")
    ) {
      return UserMode.NHATHUOC;
    }
    return UserMode.BACSI;
  }
}
