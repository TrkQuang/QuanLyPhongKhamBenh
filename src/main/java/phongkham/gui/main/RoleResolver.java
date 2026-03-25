package phongkham.gui.main;

import phongkham.Utils.Session;

public final class RoleResolver {

  private RoleResolver() {}

  public static UserMode resolve() {
    if (!Session.isLoggedIn()) {
      return UserMode.GUEST;
    }
    if (
      Session.hasPermission("DASHBOARD_XEM") ||
      Session.hasPermission("USER_XEM") ||
      Session.hasPermission("BACSI_XEM") ||
      Session.hasPermission("KHOA_XEM") ||
      Session.hasPermission("GOIDICHVU_XEM") ||
      Session.hasPermission("ROLE_XEM") ||
      Session.hasPermission("PHANQUYEN_XEM")
    ) {
      return UserMode.ADMIN;
    }
    if (
      Session.hasPermission("THUOC_XEM") ||
      Session.hasPermission("NCC_XEM") ||
      Session.hasPermission("PHIEUNHAP_XEM") ||
      Session.hasPermission("HOADONTHUOC_XEM")
    ) {
      return UserMode.NHATHUOC;
    }
    return UserMode.BACSI;
  }
}
