package phongkham.gui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;
import phongkham.Utils.Session;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.components.SidebarItem;

public class Sidebar extends JPanel {

  private final NavigationHandler navigationHandler;
  private final Map<String, SidebarItem> routeButtons = new HashMap<>();
  private final JPanel menuContainer = new JPanel();
  private final JScrollPane scrollPane;

  public Sidebar(NavigationHandler navigationHandler) {
    this.navigationHandler = navigationHandler;
    setLayout(new BorderLayout());
    setBackground(UIConstants.SIDEBAR_BG);
    setBorder(BorderFactory.createEmptyBorder(16, 10, 16, 10));
    setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
    setMinimumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));

    menuContainer.setOpaque(false);
    menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));

    JPanel pinnedTop = new JPanel();
    pinnedTop.setOpaque(false);
    pinnedTop.setLayout(new BoxLayout(pinnedTop, BoxLayout.Y_AXIS));
    pinnedTop.add(buildBrand());
    pinnedTop.add(Box.createVerticalStrut(10));
    pinnedTop.add(buildTopSectionTitle("MENU"));
    add(pinnedTop, BorderLayout.NORTH);

    scrollPane = new JScrollPane(menuContainer);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setHorizontalScrollBarPolicy(
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    );
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
    );
    scrollPane.getVerticalScrollBar().setUI(new SidebarScrollBarUI());
    scrollPane.getVerticalScrollBar().setOpaque(false);
    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
    scrollPane
      .getVerticalScrollBar()
      .setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getVerticalScrollBar().setUnitIncrement(14);

    java.awt.event.MouseAdapter hoverHandler =
      new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
          showScrollbar();
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
          SwingUtilities.invokeLater(() -> {
            if (!isMouseInsideSidebar()) {
              hideScrollbar();
            }
          });
        }
      };
    addMouseListener(hoverHandler);
    scrollPane.addMouseListener(hoverHandler);
    scrollPane.getViewport().addMouseListener(hoverHandler);
    menuContainer.addMouseListener(hoverHandler);

    add(scrollPane, BorderLayout.CENTER);

    buildMenu();
  }

  public final void buildMenu() {
    menuContainer.removeAll();
    routeButtons.clear();

    UserMode mode = RoleResolver.resolve();

    if (mode == UserMode.GUEST) {
      menuContainer.add(Box.createVerticalStrut(20));
      sectionTitle("KHÁCH");
      addMenu("Đặt lịch khám", AppRoute.DAT_LICH);
      addMenu("Mua thuốc", AppRoute.MUA_THUOC);
    } else {
      boolean hasBacSiSection =
        hasAnyPermission("LICHLAMVIEC_XEM", "LICHLAMVIEC_THEM") ||
        hasAnyPermission("LICHKHAM_XEM", "LICHKHAM_SUA", "LICHKHAM_HUY") ||
        hasAnyPermission(
          "HOADONKHAM_XEM",
          "HOADONKHAM_THEM",
          "HOADONKHAM_SUA",
          "HOADONKHAM_HUY"
        ) ||
        hasAnyPermission("HOSO_XEM", "HOSO_THEM", "HOSO_SUA") ||
        hasAnyPermission("BACSI_PROFILE_XEM", "BACSI_PROFILE_DOI_MAT_KHAU");
      if (hasBacSiSection) {
        menuContainer.add(Box.createVerticalStrut(20));
        sectionTitle("BÁC SĨ");
        if (hasAnyPermission("LICHLAMVIEC_XEM", "LICHLAMVIEC_THEM")) {
          addMenu("Lịch làm việc", AppRoute.BACSI_LICH_LAM_VIEC);
        }
        if (hasAnyPermission("LICHKHAM_XEM", "LICHKHAM_SUA", "LICHKHAM_HUY")) {
          addMenu("Lịch khám", AppRoute.BACSI_LICH_KHAM);
        }
        if (
          hasAnyPermission(
            "HOADONKHAM_XEM",
            "HOADONKHAM_THEM",
            "HOADONKHAM_SUA",
            "HOADONKHAM_HUY"
          )
        ) {
          addMenu("Hóa đơn khám", AppRoute.BACSI_HOA_DON_KHAM);
        }
        if (hasAnyPermission("HOSO_XEM", "HOSO_THEM", "HOSO_SUA")) {
          addMenu("Bệnh án", AppRoute.BACSI_BENH_AN);
        }
        if (
          hasAnyPermission("BACSI_PROFILE_XEM", "BACSI_PROFILE_DOI_MAT_KHAU")
        ) {
          addMenu("Profile", AppRoute.BACSI_PROFILE);
        }
      }

      boolean hasNhaThuocSection =
        hasAnyPermission(
          "THUOC_XEM",
          "THUOC_THEM",
          "THUOC_SUA",
          "THUOC_XOA",
          "THUOC_KICH_HOAT"
        ) ||
        hasAnyPermission("NCC_XEM", "NCC_THEM", "NCC_SUA", "NCC_XOA") ||
        hasAnyPermission(
          "PHIEUNHAP_XEM",
          "PHIEUNHAP_THEM",
          "PHIEUNHAP_SUA",
          "PHIEUNHAP_XOA",
          "PHIEUNHAP_XAC_NHAN_NHAP_KHO",
          "PHIEUNHAP_XEM_LO_HSD"
        ) ||
        hasAnyPermission(
          "HOADONTHUOC_XEM",
          "HOADONTHUOC_THEM",
          "HOADONTHUOC_SUA",
          "HOADONTHUOC_XOA",
          "HOADONTHUOC_XAC_NHAN_THANH_TOAN",
          "HOADONTHUOC_XAC_NHAN_GIAO_THUOC",
          "HOADONTHUOC_XEM_XUAT_THEO_LO"
        );
      if (hasNhaThuocSection) {
        menuContainer.add(Box.createVerticalStrut(20));
        sectionTitle("NHÀ THUỐC");
        if (
          hasAnyPermission(
            "THUOC_XEM",
            "THUOC_THEM",
            "THUOC_SUA",
            "THUOC_XOA",
            "THUOC_KICH_HOAT"
          )
        ) {
          addMenu("Quản lý thuốc", AppRoute.THUOC);
        }
        if (hasAnyPermission("NCC_XEM", "NCC_THEM", "NCC_SUA", "NCC_XOA")) {
          addMenu("Nhà cung cấp", AppRoute.NHA_CUNG_CAP);
        }
        if (
          hasAnyPermission(
            "PHIEUNHAP_XEM",
            "PHIEUNHAP_THEM",
            "PHIEUNHAP_SUA",
            "PHIEUNHAP_XOA",
            "PHIEUNHAP_XAC_NHAN_NHAP_KHO",
            "PHIEUNHAP_XEM_LO_HSD"
          )
        ) {
          addMenu("Phiếu nhập", AppRoute.PHIEU_NHAP);
        }
        if (
          hasAnyPermission(
            "HOADONTHUOC_XEM",
            "HOADONTHUOC_THEM",
            "HOADONTHUOC_SUA",
            "HOADONTHUOC_XOA",
            "HOADONTHUOC_XAC_NHAN_THANH_TOAN",
            "HOADONTHUOC_XAC_NHAN_GIAO_THUOC",
            "HOADONTHUOC_XEM_XUAT_THEO_LO"
          )
        ) {
          addMenu("Hóa đơn bán thuốc", AppRoute.HOA_DON_THUOC);
        }
      }

      boolean hasQuanTriSection =
        hasAnyPermission("DASHBOARD_XEM") ||
        hasAnyPermission(
          "USER_XEM",
          "USER_THEM",
          "USER_SUA",
          "USER_XOA",
          "USER_RESET_MAT_KHAU",
          "USER_KICH_HOAT_VO_HIEU_HOA"
        ) ||
        hasAnyPermission(
          "BACSI_XEM",
          "BACSI_THEM",
          "BACSI_SUA",
          "BACSI_XOA",
          "BACSI_XEM_CHI_TIET"
        ) ||
        hasAnyPermission("DUYETLICHLAM_XEM") ||
        hasAnyPermission("KHOA_XEM", "KHOA_THEM", "KHOA_SUA", "KHOA_XOA") ||
        hasAnyPermission(
          "GOIDICHVU_XEM",
          "GOIDICHVU_THEM",
          "GOIDICHVU_SUA",
          "GOIDICHVU_XOA"
        ) ||
        hasAnyPermission("ROLE_XEM", "ROLE_THEM", "ROLE_SUA", "ROLE_XOA") ||
        hasAnyPermission("PHANQUYEN_XEM", "PHANQUYEN_CAP_NHAT");
      if (hasQuanTriSection) {
        menuContainer.add(Box.createVerticalStrut(20));
        sectionTitle("QUẢN TRỊ");
        if (hasAnyPermission("DASHBOARD_XEM")) {
          addMenu("Dashboard", AppRoute.DASHBOARD);
        }
        if (
          hasAnyPermission(
            "USER_XEM",
            "USER_THEM",
            "USER_SUA",
            "USER_XOA",
            "USER_RESET_MAT_KHAU",
            "USER_KICH_HOAT_VO_HIEU_HOA"
          )
        ) {
          addMenu("Quản lý tài khoản", AppRoute.QL_TAI_KHOAN);
        }
        if (
          hasAnyPermission(
            "BACSI_XEM",
            "BACSI_THEM",
            "BACSI_SUA",
            "BACSI_XOA",
            "BACSI_XEM_CHI_TIET"
          )
        ) {
          addMenu("Quản lý bác sĩ", AppRoute.QL_BAC_SI);
        }
        if (hasAnyPermission("DUYETLICHLAM_XEM")) {
          addMenu("Duyệt lịch làm", AppRoute.QL_DUYET_LICH_LAM);
        }
        if (hasAnyPermission("KHOA_XEM", "KHOA_THEM", "KHOA_SUA", "KHOA_XOA")) {
          addMenu("Quản lý khoa", AppRoute.QL_KHOA);
        }
        if (
          hasAnyPermission(
            "GOIDICHVU_XEM",
            "GOIDICHVU_THEM",
            "GOIDICHVU_SUA",
            "GOIDICHVU_XOA"
          )
        ) {
          addMenu("Quản lý gói dịch vụ", AppRoute.QL_GOI_DICH_VU);
        }
        if (hasAnyPermission("ROLE_XEM", "ROLE_THEM", "ROLE_SUA", "ROLE_XOA")) {
          addMenu("Quản lý role", AppRoute.QL_ROLE);
        }
        if (hasAnyPermission("PHANQUYEN_XEM", "PHANQUYEN_CAP_NHAT")) {
          addMenu("Phân quyền chi tiết", AppRoute.PHAN_QUYEN);
        }
      }
    }

    menuContainer.revalidate();
    menuContainer.repaint();
    revalidate();
    repaint();
  }

  private JPanel buildBrand() {
    JPanel brand = new JPanel();
    brand.setOpaque(false);
    brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("PHÒNG KHÁM");
    title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
    title.setForeground(new java.awt.Color(191, 219, 254));
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitle = new JLabel("UY TÍN VÀ HIỆU QUẢ");
    subtitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
    subtitle.setForeground(new java.awt.Color(148, 163, 184));
    subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    brand.add(title);
    brand.add(Box.createVerticalStrut(2));
    brand.add(subtitle);
    brand.setAlignmentX(Component.LEFT_ALIGNMENT);
    brand.setMaximumSize(
      new Dimension(Integer.MAX_VALUE, brand.getPreferredSize().height)
    );
    return brand;
  }

  private JPanel buildTopSectionTitle(String text) {
    JPanel row = new JPanel(
      new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 8)
    );
    row.setOpaque(false);

    JLabel label = new JLabel(text);
    label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
    label.setForeground(new java.awt.Color(148, 163, 184));
    row.add(label);

    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    return row;
  }

  private void sectionTitle(String text) {
    JPanel row = new JPanel(
      new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 8)
    );
    row.setOpaque(false);

    JLabel label = new JLabel(text);
    label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
    label.setForeground(new java.awt.Color(148, 163, 184));
    row.add(label);

    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

    menuContainer.add(row);
    menuContainer.add(Box.createVerticalStrut(6));
  }

  private void addMenu(String text, String route) {
    SidebarItem item = new SidebarItem(text);
    item.setAlignmentX(Component.LEFT_ALIGNMENT);
    item.addActionListener(e -> {
      setActiveRoute(route);
      navigationHandler.navigate(route);
    });
    routeButtons.put(route, item);

    menuContainer.add(item);
    menuContainer.add(Box.createVerticalStrut(8));
  }

  public void setActiveRoute(String route) {
    for (Map.Entry<String, SidebarItem> entry : routeButtons.entrySet()) {
      entry.getValue().setActive(entry.getKey().equals(route));
    }
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

  private void showScrollbar() {
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
    );
  }

  private void hideScrollbar() {
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
    );
  }

  private boolean isMouseInsideSidebar() {
    Point pointer =
      MouseInfo.getPointerInfo() == null
        ? null
        : MouseInfo.getPointerInfo().getLocation();
    if (pointer == null) {
      return false;
    }
    SwingUtilities.convertPointFromScreen(pointer, this);
    return (
      pointer.x >= 0 &&
      pointer.y >= 0 &&
      pointer.x < getWidth() &&
      pointer.y < getHeight()
    );
  }

  private static class SidebarScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
      this.trackColor = UIConstants.SIDEBAR_BG;
      this.thumbColor = new Color(100, 116, 139, 130);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
      return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
      return createZeroButton();
    }

    private JButton createZeroButton() {
      JButton button = new JButton();
      button.setPreferredSize(new Dimension(0, 0));
      button.setMinimumSize(new Dimension(0, 0));
      button.setMaximumSize(new Dimension(0, 0));
      button.setBorder(BorderFactory.createEmptyBorder());
      button.setContentAreaFilled(false);
      button.setFocusable(false);
      return button;
    }

    @Override
    protected void paintTrack(
      Graphics g,
      javax.swing.JComponent c,
      Rectangle trackBounds
    ) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setColor(UIConstants.SIDEBAR_BG);
      g2.fillRect(
        trackBounds.x,
        trackBounds.y,
        trackBounds.width,
        trackBounds.height
      );
      g2.dispose();
    }

    @Override
    protected void paintThumb(
      Graphics g,
      javax.swing.JComponent c,
      Rectangle thumbBounds
    ) {
      if (
        !scrollbar.isEnabled() ||
        thumbBounds.width <= 0 ||
        thumbBounds.height <= 0
      ) {
        return;
      }
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      );
      g2.setColor(new Color(120, 136, 153, 120));
      int x = thumbBounds.x + 2;
      int y = thumbBounds.y + 2;
      int w = Math.max(4, thumbBounds.width - 4);
      int h = Math.max(14, thumbBounds.height - 4);
      g2.fillRoundRect(x, y, w, h, 8, 8);
      g2.dispose();
    }
  }
}
