package phongkham.gui.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import phongkham.Utils.Session;
import phongkham.gui.auth.LoginForm;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.RoundedPanel;

public class Header extends JPanel {

  private final JLabel pageTitleLabel;

  public Header() {
    setLayout(new BorderLayout());
    setBackground(UIConstants.HEADER_BG);
    setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 18));

    pageTitleLabel = new JLabel("TRANG CHỦ");
    pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    pageTitleLabel.setForeground(UIConstants.TEXT_MAIN);

    JPanel left = new JPanel(new BorderLayout());
    left.setOpaque(false);
    left.add(pageTitleLabel, BorderLayout.WEST);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    actions.setOpaque(false);

    String username = Session.isLoggedIn()
      ? Session.getCurrentUsername()
      : "Khách";
    JLabel userInfo = new JLabel("Xin chào, " + username + "  ");
    userInfo.setFont(UIConstants.FONT_BODY_BOLD);

    RoundedPanel avatar = new RoundedPanel(24);
    avatar.setDrawShadow(false);
    avatar.setBackground(new java.awt.Color(219, 234, 254));
    avatar.setLayout(new BorderLayout());
    avatar.setPreferredSize(new java.awt.Dimension(32, 32));
    JLabel lblAvatar = new JLabel("U", SwingConstants.CENTER);
    lblAvatar.setForeground(UIConstants.PRIMARY_DARK);
    lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
    avatar.add(lblAvatar, BorderLayout.CENTER);

    JButton btnLogout = UIUtils.primaryButton("Đăng xuất");
    btnLogout.addActionListener(e -> logout());

    actions.add(avatar);
    actions.add(userInfo);
    actions.add(btnLogout);

    add(left, BorderLayout.WEST);
    add(actions, BorderLayout.EAST);
  }

  public void setPageTitle(String route) {
    pageTitleLabel.setText(resolveTitle(route));
  }

  private String resolveTitle(String route) {
    if (route == null) {
      return "TRANG CHỦ";
    }

    switch (route) {
      case AppRoute.HOME:
        return "TRANG CHỦ";
      case AppRoute.DAT_LICH:
        return "LỊCH KHÁM";
      case AppRoute.MUA_THUOC:
        return "MUA THUỐC";
      case AppRoute.BACSI_LICH_LAM_VIEC:
        return "LỊCH LÀM VIỆC";
      case AppRoute.BACSI_LICH_KHAM:
        return "LỊCH KHÁM";
      case AppRoute.BACSI_HOA_DON_KHAM:
        return "HÓA ĐƠN KHÁM";
      case AppRoute.BACSI_BENH_AN:
        return "HỒ SƠ BỆNH ÁN";
      case AppRoute.BACSI_PROFILE:
        return "THÔNG TIN BÁC SĨ";
      case AppRoute.THUOC:
        return "QUẢN LÝ THUỐC";
      case AppRoute.NHA_CUNG_CAP:
        return "NHÀ CUNG CẤP";
      case AppRoute.PHIEU_NHAP:
        return "PHIẾU NHẬP";
      case AppRoute.HOA_DON_THUOC:
        return "HÓA ĐƠN BÁN THUỐC";
      case AppRoute.DASHBOARD:
        return "BẢNG ĐIỀU KHIỂN";
      case AppRoute.QL_QUAN_LY:
        return "QUẢN LÝ";
      case AppRoute.QL_TAI_KHOAN:
        return "QUẢN LÝ TÀI KHOẢN";
      case AppRoute.QL_BAC_SI:
        return "QUẢN LÝ BÁC SĨ";
      case AppRoute.QL_DUYET_LICH_LAM:
        return "DUYỆT LỊCH LÀM";
      case AppRoute.QL_KHOA:
        return "QUẢN LÝ KHOA";
      case AppRoute.QL_GOI_DICH_VU:
        return "QUẢN LÝ GÓI DỊCH VỤ";
      case AppRoute.PHAN_QUYEN:
        return "PHÂN QUYỀN";
      default:
        return "TRANG CHỦ";
    }
  }

  private void logout() {
    java.awt.Window window = SwingUtilities.getWindowAncestor(this);
    java.awt.Component dialogParent = window != null ? window : this;

    if (
      !DialogHelper.confirm(dialogParent, "Bạn có chắc chắn muốn đăng xuất?")
    ) {
      return;
    }
    Session.logout();
    if (window != null) {
      window.dispose();
    }
    new LoginForm().setVisible(true);
  }
}
