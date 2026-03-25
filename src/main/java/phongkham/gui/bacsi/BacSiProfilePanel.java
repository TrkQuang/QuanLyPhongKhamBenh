package phongkham.gui.bacsi;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.UIUtils;

public class BacSiProfilePanel extends BasePanel {

  private final UsersBUS usersBUS = new UsersBUS();
  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private JButton btnDoiMatKhau;

  @Override
  protected void init() {
    add(
      UIUtils.createSection("Hồ sơ bác sĩ", buildProfileRows()),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Đổi mật khẩu")
    );
    btnDoiMatKhau = (JButton) actions.getComponent(0);
    btnDoiMatKhau.addActionListener(e -> changePassword());
    add(actions, BorderLayout.SOUTH);

    apDungPhanQuyenHanhDong();
  }

  private javax.swing.JPanel buildProfileRows() {
    UsersDTO user = usersBUS.getUserByID(Session.getCurrentUserID());
    BacSiDTO bacSi = null;
    String email = Session.getCurrentUserEmail();
    if (email != null && !email.trim().isEmpty()) {
      bacSi = bacSiBUS.getByEmail(email);
    }
    if (bacSi == null) {
      String maBacSi = Session.getCurrentBacSiID();
      if (maBacSi != null && !maBacSi.trim().isEmpty()) {
        bacSi = bacSiBUS.getById(maBacSi);
      }
    }

    javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
    panel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    int row = 0;
    addInfoRow(panel, gbc, row++, "UserID", value(Session.getCurrentUserID()));
    addInfoRow(
      panel,
      gbc,
      row++,
      "Username",
      value(Session.getCurrentUsername())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "Email",
      value(Session.getCurrentUserEmail())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "RoleID",
      user == null ? "-" : String.valueOf(user.getRoleID())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "Mã bác sĩ",
      bacSi == null ? "-" : value(bacSi.getMaBacSi())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "Họ tên",
      bacSi == null ? "-" : value(bacSi.getHoTen())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "Số điện thoại",
      bacSi == null ? "-" : value(bacSi.getSoDienThoai())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "Chuyên khoa",
      bacSi == null ? "-" : value(bacSi.getChuyenKhoa())
    );
    addInfoRow(
      panel,
      gbc,
      row++,
      "Mã khoa",
      bacSi == null ? "-" : value(bacSi.getMaKhoa())
    );

    return panel;
  }

  private void addInfoRow(
    javax.swing.JPanel panel,
    GridBagConstraints gbc,
    int row,
    String label,
    String value
  ) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    panel.add(new JLabel(label + ":"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(new JLabel(value(value)), gbc);
  }

  private void changePassword() {
    String userId = Session.getCurrentUserID();
    if (userId == null || userId.trim().isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Không xác định được tài khoản hiện tại."
      );
      return;
    }

    String newPassword = JOptionPane.showInputDialog(
      this,
      "Nhập mật khẩu mới (>=6 ký tự):"
    );
    if (newPassword == null || newPassword.trim().isEmpty()) return;

    String message = usersBUS.resetPassword(userId, newPassword.trim());
    JOptionPane.showMessageDialog(this, message);
  }

  private String value(String text) {
    return text == null || text.trim().isEmpty() ? "-" : text;
  }

  private void apDungPhanQuyenHanhDong() {
    boolean coQuyenXem = Session.coMotTrongCacQuyen("BACSI_PROFILE_XEM");
    boolean coQuyenDoiMatKhau = Session.coMotTrongCacQuyen(
      "BACSI_PROFILE_DOI_MAT_KHAU"
    );

    if (btnDoiMatKhau != null) {
      btnDoiMatKhau.setVisible(coQuyenDoiMatKhau && coQuyenXem);
    }
  }
}
