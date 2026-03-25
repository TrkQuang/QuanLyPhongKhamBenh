package phongkham.gui.main;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import phongkham.gui.admin.DashboardPanel;
import phongkham.gui.admin.QuanLyBacSiPanel;
import phongkham.gui.admin.QuanLyDuyetLichLamPanel;
import phongkham.gui.admin.QuanLyGoiDichVuPanel;
import phongkham.gui.admin.QuanLyKhoaPanel;
import phongkham.gui.admin.QuanLyPhanQuyenPanel;
import phongkham.gui.admin.QuanLyRolePanel;
import phongkham.gui.admin.QuanLyTaiKhoanPanel;
import phongkham.gui.bacsi.BacSiProfilePanel;
import phongkham.gui.bacsi.BenhAnPanel;
import phongkham.gui.bacsi.HoaDonKhamPanel;
import phongkham.gui.bacsi.LichKhamPanel;
import phongkham.gui.bacsi.LichLamViecPanel;
import phongkham.gui.guest.DatLichPanel;
import phongkham.gui.guest.MuaThuocPanel;
import phongkham.gui.nhathuoc.HoaDonThuocPanel;
import phongkham.gui.nhathuoc.NhaCungCapPanel;
import phongkham.gui.nhathuoc.PhieuNhapPanel;
import phongkham.gui.nhathuoc.ThuocPanel;

public class ContentPanel extends JPanel {

  private final CardLayout cardLayout = new CardLayout();
  private final Map<String, JPanel> routeMap = new HashMap<>();

  public ContentPanel() {
    setLayout(cardLayout);
    registerRoutes();
  }

  private void registerRoutes() {
    register(AppRoute.HOME, new HomePanel());
    register(AppRoute.DAT_LICH, new DatLichPanel());
    register(AppRoute.MUA_THUOC, new MuaThuocPanel());

    register(AppRoute.BACSI_LICH_LAM_VIEC, new LichLamViecPanel());
    register(AppRoute.BACSI_LICH_KHAM, new LichKhamPanel());
    register(AppRoute.BACSI_HOA_DON_KHAM, new HoaDonKhamPanel());
    register(AppRoute.BACSI_BENH_AN, new BenhAnPanel());
    register(AppRoute.BACSI_PROFILE, new BacSiProfilePanel());

    register(AppRoute.THUOC, new ThuocPanel());
    register(AppRoute.NHA_CUNG_CAP, new NhaCungCapPanel());
    register(AppRoute.PHIEU_NHAP, new PhieuNhapPanel());
    register(AppRoute.HOA_DON_THUOC, new HoaDonThuocPanel());

    register(AppRoute.DASHBOARD, new DashboardPanel());
    register(AppRoute.QL_TAI_KHOAN, new QuanLyTaiKhoanPanel());
    register(AppRoute.QL_BAC_SI, new QuanLyBacSiPanel());
    register(AppRoute.QL_DUYET_LICH_LAM, new QuanLyDuyetLichLamPanel());
    register(AppRoute.QL_KHOA, new QuanLyKhoaPanel());
    register(AppRoute.QL_GOI_DICH_VU, new QuanLyGoiDichVuPanel());
    register(AppRoute.QL_ROLE, new QuanLyRolePanel());
    register(AppRoute.PHAN_QUYEN, new QuanLyPhanQuyenPanel());
  }

  private void register(String route, JPanel panel) {
    routeMap.put(route, panel);
    add(panel, route);
  }

  public boolean containsRoute(String route) {
    return routeMap.containsKey(route);
  }

  public void showRoute(String route) {
    cardLayout.show(this, route);
  }

  public void reloadAllPanels() {
    removeAll();
    routeMap.clear();
    registerRoutes();
    revalidate();
    repaint();
  }
}
