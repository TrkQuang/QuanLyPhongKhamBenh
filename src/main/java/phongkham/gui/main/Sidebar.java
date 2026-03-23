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
        hasAnyPermission("LICHLAMVIEC_VIEW", "LICHLAMVIEC_MANAGE") ||
        hasAnyPermission("LICHKHAM_VIEW", "LICHKHAM_MANAGE") ||
        hasAnyPermission("HOADONKHAM_VIEW", "HOADONKHAM_MANAGE") ||
        hasAnyPermission("HOSO_VIEW", "HOSO_MANAGE") ||
        hasAnyPermission("BACSI_PROFILE_VIEW", "BACSI_PROFILE_UPDATE_PASSWORD");
      if (hasBacSiSection) {
        menuContainer.add(Box.createVerticalStrut(20));
        sectionTitle("BÁC SĨ");
        if (hasAnyPermission("LICHLAMVIEC_VIEW", "LICHLAMVIEC_MANAGE")) {
          addMenu("Lịch làm việc", AppRoute.BACSI_LICH_LAM_VIEC);
        }
        if (hasAnyPermission("LICHKHAM_VIEW", "LICHKHAM_MANAGE")) {
          addMenu("Lịch khám", AppRoute.BACSI_LICH_KHAM);
        }
        if (hasAnyPermission("HOADONKHAM_VIEW", "HOADONKHAM_MANAGE")) {
          addMenu("Hóa đơn khám", AppRoute.BACSI_HOA_DON_KHAM);
        }
        if (hasAnyPermission("HOSO_VIEW", "HOSO_MANAGE")) {
          addMenu("Bệnh án", AppRoute.BACSI_BENH_AN);
        }
        if (
          hasAnyPermission(
            "BACSI_PROFILE_VIEW",
            "BACSI_PROFILE_UPDATE_PASSWORD"
          )
        ) {
          addMenu("Profile", AppRoute.BACSI_PROFILE);
        }
      }

      boolean hasNhaThuocSection =
        hasAnyPermission("THUOC_VIEW", "THUOC_MANAGE") ||
        hasAnyPermission("NCC_VIEW", "NCC_MANAGE") ||
        hasAnyPermission("PHIEUNHAP_VIEW", "PHIEUNHAP_MANAGE") ||
        hasAnyPermission(
          "HOADONTHUOC_VIEW",
          "HOADONTHUOC_CREATE",
          "HOADONTHUOC_MANAGE"
        );
      if (hasNhaThuocSection) {
        menuContainer.add(Box.createVerticalStrut(20));
        sectionTitle("NHÀ THUỐC");
        if (hasAnyPermission("THUOC_VIEW", "THUOC_MANAGE")) {
          addMenu("Quản lý thuốc", AppRoute.THUOC);
        }
        if (hasAnyPermission("NCC_VIEW", "NCC_MANAGE")) {
          addMenu("Nhà cung cấp", AppRoute.NHA_CUNG_CAP);
        }
        if (hasAnyPermission("PHIEUNHAP_VIEW", "PHIEUNHAP_MANAGE")) {
          addMenu("Phiếu nhập", AppRoute.PHIEU_NHAP);
        }
        if (
          hasAnyPermission(
            "HOADONTHUOC_VIEW",
            "HOADONTHUOC_CREATE",
            "HOADONTHUOC_MANAGE"
          )
        ) {
          addMenu("Hóa đơn bán thuốc", AppRoute.HOA_DON_THUOC);
        }
      }

      boolean hasQuanTriSection =
        hasAnyPermission("DASHBOARD_VIEW") ||
        hasAnyPermission("USER_VIEW", "USER_MANAGE") ||
        hasAnyPermission("BACSI_VIEW", "BACSI_MANAGE") ||
        hasAnyPermission("KHOA_VIEW", "KHOA_MANAGE") ||
        hasAnyPermission("GOIDICHVU_VIEW", "GOIDICHVU_MANAGE") ||
        hasAnyPermission("ROLE_PERMISSION_VIEW", "ROLE_PERMISSION_MANAGE");
      if (hasQuanTriSection) {
        menuContainer.add(Box.createVerticalStrut(20));
        sectionTitle("QUẢN TRỊ");
        if (hasAnyPermission("DASHBOARD_VIEW")) {
          addMenu("Dashboard", AppRoute.DASHBOARD);
        }
        if (hasAnyPermission("USER_VIEW", "USER_MANAGE")) {
          addMenu("Quản lý tài khoản", AppRoute.QL_TAI_KHOAN);
        }
        if (hasAnyPermission("BACSI_VIEW", "BACSI_MANAGE")) {
          addMenu("Quản lý bác sĩ", AppRoute.QL_BAC_SI);
        }
        if (mode == UserMode.ADMIN) {
          addMenu("Duyệt lịch làm", AppRoute.QL_DUYET_LICH_LAM);
        }
        if (hasAnyPermission("KHOA_VIEW", "KHOA_MANAGE")) {
          addMenu("Quản lý khoa", AppRoute.QL_KHOA);
        }
        if (hasAnyPermission("GOIDICHVU_VIEW", "GOIDICHVU_MANAGE")) {
          addMenu("Quản lý gói dịch vụ", AppRoute.QL_GOI_DICH_VU);
        }
        if (
          hasAnyPermission("ROLE_PERMISSION_VIEW", "ROLE_PERMISSION_MANAGE")
        ) {
          addMenu("Phân quyền", AppRoute.PHAN_QUYEN);
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
