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
      return;
    }
    sidebar.setActiveRoute(route);
    contentPanel.showRoute(route);
    header.setPageTitle(route);
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
      return hasAnyPermission("LICHLAMVIEC_VIEW", "LICHLAMVIEC_MANAGE");
    }
    if (AppRoute.BACSI_LICH_KHAM.equals(route)) {
      return hasAnyPermission("LICHKHAM_VIEW", "LICHKHAM_MANAGE");
    }
    if (AppRoute.BACSI_HOA_DON_KHAM.equals(route)) {
      return hasAnyPermission("HOADONKHAM_VIEW", "HOADONKHAM_MANAGE");
    }
    if (AppRoute.BACSI_BENH_AN.equals(route)) {
      return hasAnyPermission("HOSO_VIEW", "HOSO_MANAGE");
    }
    if (AppRoute.BACSI_PROFILE.equals(route)) {
      return hasAnyPermission(
        "BACSI_PROFILE_VIEW",
        "BACSI_PROFILE_UPDATE_PASSWORD"
      );
    }

    if (AppRoute.THUOC.equals(route)) {
      return hasAnyPermission("THUOC_VIEW", "THUOC_MANAGE");
    }
    if (AppRoute.NHA_CUNG_CAP.equals(route)) {
      return hasAnyPermission("NCC_VIEW", "NCC_MANAGE");
    }
    if (AppRoute.PHIEU_NHAP.equals(route)) {
      return hasAnyPermission("PHIEUNHAP_VIEW", "PHIEUNHAP_MANAGE");
    }
    if (AppRoute.HOA_DON_THUOC.equals(route)) {
      return hasAnyPermission(
        "HOADONTHUOC_VIEW",
        "HOADONTHUOC_CREATE",
        "HOADONTHUOC_MANAGE"
      );
    }

    if (AppRoute.DASHBOARD.equals(route)) {
      return hasAnyPermission("DASHBOARD_VIEW");
    }
    if (AppRoute.QL_TAI_KHOAN.equals(route)) {
      return hasAnyPermission("USER_VIEW", "USER_MANAGE");
    }
    if (AppRoute.QL_BAC_SI.equals(route)) {
      return hasAnyPermission("BACSI_VIEW", "BACSI_MANAGE");
    }
    if (AppRoute.QL_DUYET_LICH_LAM.equals(route)) {
      return mode == UserMode.ADMIN;
    }
    if (AppRoute.QL_KHOA.equals(route)) {
      return hasAnyPermission("KHOA_VIEW", "KHOA_MANAGE");
    }
    if (AppRoute.QL_GOI_DICH_VU.equals(route)) {
      return hasAnyPermission("GOIDICHVU_VIEW", "GOIDICHVU_MANAGE");
    }
    if (AppRoute.PHAN_QUYEN.equals(route)) {
      return hasAnyPermission("ROLE_PERMISSION_VIEW", "ROLE_PERMISSION_MANAGE");
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
    if (Session.isLoggedIn()) {
      if (RoleResolver.resolve() == UserMode.ADMIN) {
        onNavigate(AppRoute.DASHBOARD);
      } else if (RoleResolver.resolve() == UserMode.NHATHUOC) {
        onNavigate(AppRoute.THUOC);
      } else {
        onNavigate(AppRoute.BACSI_LICH_KHAM);
      }
    } else {
      onNavigate(AppRoute.DAT_LICH);
    }
  }
}
