package phongkham.gui.main;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import phongkham.Utils.Session;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIConstants;

public class MainFrame extends JFrame {

  private final Sidebar sidebar;
  private final ContentPanel contentPanel;
  private final Header header;
  private String currentRoute = AppRoute.HOME;

  public MainFrame() {
    setTitle("Phòng khám đa khoa");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1320, 760);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    getContentPane().setBackground(UIConstants.BG_APP);

    header = new Header();
    contentPanel = new ContentPanel();
    sidebar = new Sidebar(this::onNavigate);

    JPanel shell = new JPanel(new BorderLayout());
    shell.setOpaque(false);
    shell.add(header, BorderLayout.NORTH);
    shell.add(contentPanel, BorderLayout.CENTER);

    add(sidebar, BorderLayout.WEST);
    add(shell, BorderLayout.CENTER);

    onNavigate(AppRoute.HOME);
  }

  private void onNavigate(String route) {
    if (!contentPanel.containsRoute(route)) {
      DialogHelper.warn(this, "Trang không tồn tại.");
      return;
    }
    if (!canAccess(route)) {
      DialogHelper.warn(this, "Bạn không có quyền truy cập chức năng này.");
      sidebar.setActiveRoute(AppRoute.HOME);
      contentPanel.showRoute(AppRoute.HOME);
      currentRoute = AppRoute.HOME;
      return;
    }
    sidebar.setActiveRoute(route);
    contentPanel.showRoute(route);
    header.setPageTitle(route);
    currentRoute = route;
  }

  private boolean canAccess(String route) {
    UserMode mode = RoleResolver.resolve();

    if (AppRoute.HOME.equals(route)) {
      return true;
    }

    if (mode == UserMode.GUEST) {
      return (
        AppRoute.DAT_LICH.equals(route) || AppRoute.MUA_THUOC.equals(route)
      );
    }

    if (AppRoute.DAT_LICH.equals(route) || AppRoute.MUA_THUOC.equals(route)) {
      return true;
    }

    if (AppRoute.BACSI_LICH_LAM_VIEC.equals(route)) {
      return hasAnyPermission("LICHLAMVIEC_XEM", "LICHLAMVIEC_THEM");
    }
    if (AppRoute.BACSI_LICH_KHAM.equals(route)) {
      return hasAnyPermission("LICHKHAM_XEM", "LICHKHAM_SUA", "LICHKHAM_HUY");
    }
    if (AppRoute.BACSI_HOA_DON_KHAM.equals(route)) {
      return hasAnyPermission(
        "HOADONKHAM_XEM",
        "HOADONKHAM_THEM",
        "HOADONKHAM_SUA",
        "HOADONKHAM_HUY"
      );
    }
    if (AppRoute.BACSI_BENH_AN.equals(route)) {
      return hasAnyPermission("HOSO_XEM", "HOSO_THEM", "HOSO_SUA");
    }
    if (AppRoute.BACSI_PROFILE.equals(route)) {
      return hasAnyPermission(
        "BACSI_PROFILE_XEM",
        "BACSI_PROFILE_DOI_MAT_KHAU"
      );
    }

    if (AppRoute.THUOC.equals(route)) {
      return hasAnyPermission(
        "THUOC_XEM",
        "THUOC_THEM",
        "THUOC_SUA",
        "THUOC_XOA",
        "THUOC_KICH_HOAT"
      );
    }
    if (AppRoute.NHA_CUNG_CAP.equals(route)) {
      return hasAnyPermission("NCC_XEM", "NCC_THEM", "NCC_SUA", "NCC_XOA");
    }
    if (AppRoute.PHIEU_NHAP.equals(route)) {
      return hasAnyPermission(
        "PHIEUNHAP_XEM",
        "PHIEUNHAP_THEM",
        "PHIEUNHAP_SUA",
        "PHIEUNHAP_XOA",
        "PHIEUNHAP_XAC_NHAN_NHAP_KHO",
        "PHIEUNHAP_XEM_LO_HSD"
      );
    }
    if (AppRoute.HOA_DON_THUOC.equals(route)) {
      return hasAnyPermission(
        "HOADONTHUOC_XEM",
        "HOADONTHUOC_THEM",
        "HOADONTHUOC_SUA",
        "HOADONTHUOC_XOA",
        "HOADONTHUOC_XAC_NHAN_THANH_TOAN",
        "HOADONTHUOC_XAC_NHAN_GIAO_THUOC",
        "HOADONTHUOC_XEM_XUAT_THEO_LO"
      );
    }

    if (AppRoute.DASHBOARD.equals(route)) {
      return hasAnyPermission("DASHBOARD_XEM");
    }
    if (AppRoute.QL_TAI_KHOAN.equals(route)) {
      return hasAnyPermission(
        "USER_XEM",
        "USER_THEM",
        "USER_SUA",
        "USER_XOA",
        "USER_RESET_MAT_KHAU",
        "USER_KICH_HOAT_VO_HIEU_HOA"
      );
    }
    if (AppRoute.QL_BAC_SI.equals(route)) {
      return hasAnyPermission(
        "BACSI_XEM",
        "BACSI_THEM",
        "BACSI_SUA",
        "BACSI_XOA",
        "BACSI_XEM_CHI_TIET"
      );
    }
    if (AppRoute.QL_DUYET_LICH_LAM.equals(route)) {
      return hasAnyPermission("DUYETLICHLAM_XEM");
    }
    if (AppRoute.QL_KHOA.equals(route)) {
      return hasAnyPermission("KHOA_XEM", "KHOA_THEM", "KHOA_SUA", "KHOA_XOA");
    }
    if (AppRoute.QL_GOI_DICH_VU.equals(route)) {
      return hasAnyPermission(
        "GOIDICHVU_XEM",
        "GOIDICHVU_THEM",
        "GOIDICHVU_SUA",
        "GOIDICHVU_XOA"
      );
    }
    if (AppRoute.QL_ROLE.equals(route)) {
      return hasAnyPermission("ROLE_XEM", "ROLE_THEM", "ROLE_SUA", "ROLE_XOA");
    }
    if (AppRoute.PHAN_QUYEN.equals(route)) {
      return hasAnyPermission("PHANQUYEN_XEM", "PHANQUYEN_CAP_NHAT");
    }

    return true;
  }

  private boolean hasAnyPermission(String... permissions) {
    if (permissions == null) {
      return false;
    }
    for (String permission : permissions) {
      if (Session.hasPermission(permission)) {
        return true;
      }
    }
    return false;
  }

  public void reloadLayoutAfterLogin() {
    sidebar.buildMenu();
    onNavigate(resolveFirstAccessibleRoute());
  }

  private String resolveFirstAccessibleRoute() {
    if (!Session.isLoggedIn()) {
      return AppRoute.DAT_LICH;
    }

    String[] routeOrder = new String[] {
      AppRoute.BACSI_LICH_LAM_VIEC,
      AppRoute.BACSI_LICH_KHAM,
      AppRoute.BACSI_HOA_DON_KHAM,
      AppRoute.BACSI_BENH_AN,
      AppRoute.BACSI_PROFILE,
      AppRoute.THUOC,
      AppRoute.NHA_CUNG_CAP,
      AppRoute.PHIEU_NHAP,
      AppRoute.HOA_DON_THUOC,
      AppRoute.DASHBOARD,
      AppRoute.QL_TAI_KHOAN,
      AppRoute.QL_BAC_SI,
      AppRoute.QL_DUYET_LICH_LAM,
      AppRoute.QL_KHOA,
      AppRoute.QL_GOI_DICH_VU,
      AppRoute.QL_ROLE,
      AppRoute.PHAN_QUYEN,
    };

    for (String route : routeOrder) {
      if (contentPanel.containsRoute(route) && canAccess(route)) {
        return route;
      }
    }
    return AppRoute.HOME;
  }

  public void refreshAllPanelsAfterPermissionSave() {
    sidebar.buildMenu();
    contentPanel.reloadAllPanels();

    String targetRoute = currentRoute;
    if (
      targetRoute == null ||
      !contentPanel.containsRoute(targetRoute) ||
      !canAccess(targetRoute)
    ) {
      targetRoute = AppRoute.HOME;
    }

    sidebar.setActiveRoute(targetRoute);
    contentPanel.showRoute(targetRoute);
    header.setPageTitle(targetRoute);
    currentRoute = targetRoute;
  }
}
