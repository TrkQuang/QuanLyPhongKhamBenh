package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.UsersDTO;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyTaiKhoanPanel extends BasePanel {

  private final UsersBUS usersBUS = new UsersBUS();
  private JTable table;
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] { "UserID", "Username", "Email", "RoleID", "Trạng thái" },
    0
  ) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  @Override
  protected void init() {
    table = new JTable(model);
    UIUtils.styleTable(table);
    add(
      UIUtils.createSection("Danh sách tài khoản", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Thêm"),
      UIUtils.ghostButton("Sửa"),
      UIUtils.ghostButton("Kích hoạt/Vô hiệu hóa"),
      UIUtils.ghostButton("Reset mật khẩu 123456"),
      UIUtils.ghostButton("Tải lại")
    );
    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      openUserDialog(null)
    );
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      editUser()
    );
    ((javax.swing.JButton) actions.getComponent(2)).addActionListener(e ->
      toggleActive()
    );
    ((javax.swing.JButton) actions.getComponent(3)).addActionListener(e ->
      resetPasswordDefault()
    );
    ((javax.swing.JButton) actions.getComponent(4)).addActionListener(e ->
      loadData()
    );
    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private void loadData() {
    model.setRowCount(0);
    ArrayList<UsersDTO> dsUsers = usersBUS.getAllUsers();
    for (UsersDTO user : dsUsers) {
      model.addRow(
        new Object[] {
          user.getUserID(),
          user.getUsername(),
          user.getEmail(),
          user.getRoleID(),
          user.isActive() ? "Active" : "Disabled",
        }
      );
    }
  }

  private void editUser() {
    UsersDTO current = getSelectedUser();
    if (current == null) {
      DialogHelper.warn(this, "Vui lòng chọn tài khoản để sửa.");
      return;
    }
    openUserDialog(current);
  }

  private void openUserDialog(UsersDTO source) {
    boolean isCreate = source == null;

    JPanel form = new JPanel(new GridLayout(isCreate ? 10 : 5, 2, 10, 10));
    form.setOpaque(false);

    JTextField txtUserId = new JTextField(isCreate ? "" : source.getUserID());
    txtUserId.setEditable(false);
    txtUserId.setText(isCreate ? "(Tự động)" : source.getUserID());

    JTextField txtUsername = new JTextField(
      isCreate ? "" : source.getUsername()
    );
    JTextField txtPassword = new JTextField(
      isCreate ? "" : source.getPassword()
    );
    JTextField txtEmail = new JTextField(isCreate ? "" : source.getEmail());

    JComboBox<String> cbRole = new JComboBox<>(
      new String[] { "1 - Admin", "2 - Bác sĩ", "3 - Nhà thuốc" }
    );
    if (!isCreate && source.getRoleID() != null) {
      int roleId = source.getRoleID();
      if (roleId >= 1 && roleId <= 3) {
        cbRole.setSelectedIndex(roleId - 1);
      }
    }

    JLabel lblMaBacSi = new JLabel("Mã bác sĩ");
    JTextField txtMaBacSi = new JTextField();
    JLabel lblHoTen = new JLabel("Họ tên bác sĩ");
    JTextField txtHoTen = new JTextField();
    JLabel lblChuyenKhoa = new JLabel("Chuyên khoa");
    JTextField txtChuyenKhoa = new JTextField();
    JLabel lblSdt = new JLabel("SĐT bác sĩ");
    JTextField txtSdt = new JTextField();
    JLabel lblMaKhoa = new JLabel("Mã khoa");
    JTextField txtMaKhoa = new JTextField();

    form.add(new JLabel("UserID"));
    form.add(txtUserId);
    form.add(new JLabel("Username"));
    form.add(txtUsername);
    form.add(new JLabel("Password"));
    form.add(txtPassword);
    form.add(new JLabel("Email"));
    form.add(txtEmail);
    form.add(new JLabel("Role"));
    form.add(cbRole);

    if (isCreate) {
      form.add(lblMaBacSi);
      form.add(txtMaBacSi);
      form.add(lblHoTen);
      form.add(txtHoTen);
      form.add(lblChuyenKhoa);
      form.add(txtChuyenKhoa);
      form.add(lblSdt);
      form.add(txtSdt);
      form.add(lblMaKhoa);
      form.add(txtMaKhoa);

      Runnable syncDoctorFields = () -> {
        boolean isDoctor =
          parseRoleId(String.valueOf(cbRole.getSelectedItem())) == 2;
        setComponentVisibility(lblMaBacSi, isDoctor);
        setComponentVisibility(txtMaBacSi, isDoctor);
        setComponentVisibility(lblHoTen, isDoctor);
        setComponentVisibility(txtHoTen, isDoctor);
        setComponentVisibility(lblChuyenKhoa, isDoctor);
        setComponentVisibility(txtChuyenKhoa, isDoctor);
        setComponentVisibility(lblSdt, isDoctor);
        setComponentVisibility(txtSdt, isDoctor);
        setComponentVisibility(lblMaKhoa, isDoctor);
        setComponentVisibility(txtMaKhoa, isDoctor);
        form.revalidate();
        form.repaint();
      };

      cbRole.addActionListener(e -> syncDoctorFields.run());
      syncDoctorFields.run();
    }

    AdminDialogs.showFormDialog(
      this,
      isCreate ? "Tạo tài khoản" : "Cập nhật tài khoản",
      form,
      () -> {
        int roleId = parseRoleId(String.valueOf(cbRole.getSelectedItem()));
        String message;

        if (isCreate && roleId == 2) {
          message = usersBUS.createDoctorAccountWithProfile(
            txtMaBacSi.getText().trim(),
            txtUsername.getText().trim(),
            txtPassword.getText().trim(),
            txtEmail.getText().trim(),
            txtHoTen.getText().trim(),
            txtSdt.getText().trim(),
            txtChuyenKhoa.getText().trim(),
            txtMaKhoa.getText().trim()
          );
        } else {
          UsersDTO user = new UsersDTO();
          user.setUserID(isCreate ? "" : txtUserId.getText().trim());
          user.setUsername(txtUsername.getText().trim());
          user.setPassword(txtPassword.getText().trim());
          user.setEmail(txtEmail.getText().trim());
          user.setRoleID(roleId);
          user.setActive(isCreate || source.isActive());
          message = isCreate
            ? usersBUS.insertUser(user)
            : usersBUS.updateUser(user);
        }

        if (
          !message.toLowerCase().contains("thành công") &&
          !message.toLowerCase().contains("thanh cong")
        ) {
          DialogHelper.warn(this, message);
          return false;
        }
        DialogHelper.info(this, message);
        loadData();
        return true;
      },
      620,
      isCreate ? 500 : 340
    );
  }

  private void setComponentVisibility(Component component, boolean visible) {
    component.setVisible(visible);
    component.setEnabled(visible);
  }

  private void toggleActive() {
    UsersDTO selected = getSelectedUser();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn tài khoản.");
      return;
    }

    String state = getSelectedState();
    String message;
    if ("Active".equalsIgnoreCase(state)) {
      message = usersBUS.deleteUser(selected.getUserID());
    } else {
      message = usersBUS.enableUser(selected.getUserID());
    }

    if (
      !message.toLowerCase().contains("thành công") &&
      !message.toLowerCase().contains("thanh cong")
    ) {
      DialogHelper.warn(this, message);
      return;
    }

    DialogHelper.info(this, message);
    loadData();
  }

  private void resetPasswordDefault() {
    UsersDTO selected = getSelectedUser();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn tài khoản để reset mật khẩu.");
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Reset mật khẩu của " + selected.getUsername() + " về mặc định 123456?"
      )
    ) {
      return;
    }

    String message = usersBUS.resetPassword(selected.getUserID(), "123456");
    if (
      !message.toLowerCase().contains("thành công") &&
      !message.toLowerCase().contains("thanh cong")
    ) {
      DialogHelper.error(this, message);
      return;
    }

    DialogHelper.info(this, "Đã reset mật khẩu mặc định: 123456");
  }

  private UsersDTO getSelectedUser() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    String userId = String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 0)
    );
    return usersBUS.getUserByID(userId);
  }

  private String getSelectedState() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return "";
    }
    return String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 4)
    );
  }

  private int parseRoleId(String roleText) {
    try {
      return Integer.parseInt(roleText.split(" - ")[0].trim());
    } catch (Exception ex) {
      return 3;
    }
  }
}
