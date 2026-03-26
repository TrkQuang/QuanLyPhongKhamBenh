package phongkham.gui.admin;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import phongkham.Utils.Session;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.components.RoundedPanel;

public class QuanLyTongHopPanel extends BasePanel {

  @Override
  protected void init() {
    JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

    if (
      Session.coMotTrongCacQuyen(
        "USER_XEM",
        "USER_THEM",
        "USER_SUA",
        "USER_XOA",
        "USER_RESET_MAT_KHAU",
        "USER_KICH_HOAT_VO_HIEU_HOA"
      )
    ) {
      tabs.addTab("Tài khoản", new QuanLyTaiKhoanPanel());
    }

    if (
      Session.coMotTrongCacQuyen(
        "ROLE_XEM",
        "ROLE_THEM",
        "ROLE_SUA",
        "ROLE_XOA"
      )
    ) {
      tabs.addTab("Role", new QuanLyRolePanel());
    }

    if (Session.coMotTrongCacQuyen("PHANQUYEN_XEM", "PHANQUYEN_CAP_NHAT")) {
      tabs.addTab("Phân quyền chi tiết", new QuanLyPhanQuyenPanel());
    }

    if (tabs.getTabCount() == 0) {
      JPanel empty = new JPanel(new BorderLayout());
      empty.setOpaque(false);
      empty.add(
        new JLabel(
          "Bạn không có quyền truy cập các tab quản lý.",
          SwingConstants.CENTER
        ),
        BorderLayout.CENTER
      );
      return;
    }

    RoundedPanel wrapper = new RoundedPanel(18);
    wrapper.setLayout(new BorderLayout());
    wrapper.setBackground(UIConstants.BG_SURFACE);
    wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    wrapper.add(tabs, BorderLayout.CENTER);

    add(wrapper, BorderLayout.CENTER);
  }
}
