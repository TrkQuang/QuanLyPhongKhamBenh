package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.PermissionBUS;
import phongkham.BUS.RolePermissionsBUS;
import phongkham.BUS.RolesBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.PermissionsDTO;
import phongkham.DTO.RolePermissionsDTO;
import phongkham.DTO.RolesDTO;
import phongkham.DTO.UsersDTO;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class PhanQuyenPanel extends BasePanel {

  private final RolesBUS rolesBUS = new RolesBUS();
  private final PermissionBUS permissionBUS = new PermissionBUS();
  private final RolePermissionsBUS rolePermissionsBUS =
    new RolePermissionsBUS();
  private final UsersBUS usersBUS = new UsersBUS();

  private final DefaultTableModel roleModel = new DefaultTableModel(
    new Object[] { "RoleID", "Tên vai trò", "Mô tả" },
    0
  );
  private final DefaultTableModel userModel = new DefaultTableModel(
    new Object[] { "UserID", "Username", "RoleID", "Active" },
    0
  );
  private final DefaultTableModel permissionPickModel = new DefaultTableModel(
    new Object[] { "Chọn", "PermissionID", "Permission", "Mô tả" },
    0
  ) {
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return columnIndex == 0 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return column == 0;
    }
  };

  private final List<RolesDTO> roles = new ArrayList<>();
  private final List<PermissionsDTO> permissions = new ArrayList<>();
  private final List<UsersDTO> users = new ArrayList<>();

  private JComboBox<String> cbRoleAssignPermission;
  private JTable roleTable;
  private JTable userTable;
  private JTable permissionTable;

  @Override
  protected void init() {
    add(buildBody(), BorderLayout.CENTER);

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Tạo role"),
      UIUtils.ghostButton("Xóa role"),
      UIUtils.ghostButton("Tải lại")
    );
    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      openCreateRoleDialog()
    );
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      deleteSelectedRole()
    );
    ((javax.swing.JButton) actions.getComponent(2)).addActionListener(e ->
      loadData()
    );
    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private javax.swing.JPanel buildBody() {
    javax.swing.JPanel body = new javax.swing.JPanel(new BorderLayout(0, 10));
    body.setOpaque(false);

    cbRoleAssignPermission = new JComboBox<>();
    cbRoleAssignPermission.addActionListener(e ->
      loadPermissionMatrixForSelectedRole()
    );

    javax.swing.JPanel roleToolbar = new javax.swing.JPanel(
      new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0)
    );
    roleToolbar.setOpaque(false);
    roleToolbar.add(new JLabel("Role để phân quyền:"));
    roleToolbar.add(cbRoleAssignPermission);
    javax.swing.JButton btnSavePermission = UIUtils.primaryButton(
      "Lưu quyền theo Role"
    );
    btnSavePermission.addActionListener(e -> savePermissionsForRole());
    roleToolbar.add(btnSavePermission);

    javax.swing.JPanel userToolbar = new javax.swing.JPanel(
      new FlowLayout(FlowLayout.LEFT, 8, 0)
    );
    userToolbar.setOpaque(false);
    userToolbar.add(new JLabel("Chọn account trong bảng"));
    javax.swing.JButton btnEditRole = UIUtils.ghostButton("Sửa role");
    btnEditRole.addActionListener(e -> openEditRoleForSelectedUserDialog());
    userToolbar.add(btnEditRole);

    userTable = new JTable(userModel);
    UIUtils.styleTable(userTable);

    permissionTable = new JTable(permissionPickModel);
    UIUtils.styleTable(permissionTable);
    configurePermissionCheckboxColumn();

    javax.swing.JPanel tables = new javax.swing.JPanel(
      new GridLayout(1, 2, 12, 0)
    );
    tables.setOpaque(false);

    javax.swing.JPanel left = new javax.swing.JPanel(new BorderLayout(0, 8));
    left.setOpaque(false);
    left.add(roleToolbar, BorderLayout.NORTH);
    left.add(new JScrollPane(permissionTable), BorderLayout.CENTER);

    javax.swing.JPanel right = new javax.swing.JPanel(new BorderLayout(0, 8));
    right.setOpaque(false);
    right.add(userToolbar, BorderLayout.NORTH);
    right.add(new JScrollPane(userTable), BorderLayout.CENTER);

    tables.add(UIUtils.createSection("Tick quyền theo Role", left));
    tables.add(UIUtils.createSection("Gán Role cho Account", right));

    body.add(tables, BorderLayout.CENTER);
    return body;
  }

  private void loadData() {
    roleModel.setRowCount(0);
    userModel.setRowCount(0);
    permissionPickModel.setRowCount(0);

    roles.clear();
    roles.addAll(rolesBUS.getAllRoles());
    permissions.clear();
    permissions.addAll(permissionBUS.getAllPermissions());
    users.clear();
    users.addAll(usersBUS.getAllUsers());

    cbRoleAssignPermission.removeAllItems();

    for (RolesDTO role : roles) {
      roleModel.addRow(
        new Object[] { role.getSTT(), role.getTenVaiTro(), role.getMoTa() }
      );
      cbRoleAssignPermission.addItem(
        role.getSTT() + " - " + role.getTenVaiTro()
      );
    }

    for (UsersDTO user : users) {
      userModel.addRow(
        new Object[] {
          user.getUserID(),
          user.getUsername(),
          user.getRoleID(),
          user.isActive() ? "Yes" : "No",
        }
      );
    }

    loadPermissionMatrixForSelectedRole();
  }

  private void loadPermissionMatrixForSelectedRole() {
    permissionPickModel.setRowCount(0);
    int roleId = extractSelectedRoleId(cbRoleAssignPermission);
    if (roleId < 0) {
      return;
    }

    Set<Integer> selectedPermissionIds = new HashSet<>();
    for (RolePermissionsDTO rp : rolePermissionsBUS.getPermissionsByRole(
      roleId
    )) {
      selectedPermissionIds.add(rp.getMaPermission());
    }

    for (PermissionsDTO permission : permissions) {
      permissionPickModel.addRow(
        new Object[] {
          selectedPermissionIds.contains(permission.getMaPermission()),
          String.valueOf(permission.getMaPermission()),
          permission.getTenPermission(),
          permission.getMoTa(),
        }
      );
    }
  }

  private void savePermissionsForRole() {
    int roleId = extractSelectedRoleId(cbRoleAssignPermission);
    if (roleId < 0) {
      DialogHelper.warn(this, "Vui lòng chọn role.");
      return;
    }

    ArrayList<Integer> permissionIds = new ArrayList<>();
    for (int row = 0; row < permissionPickModel.getRowCount(); row++) {
      Object selected = permissionPickModel.getValueAt(row, 0);
      if (Boolean.TRUE.equals(selected)) {
        permissionIds.add(
          Integer.parseInt(
            String.valueOf(permissionPickModel.getValueAt(row, 1))
          )
        );
      }
    }

    if (!rolePermissionsBUS.assignAllPermissionsToRole(roleId, permissionIds)) {
      DialogHelper.error(this, "Lưu phân quyền thất bại.");
      return;
    }
    DialogHelper.info(this, "Đã lưu phân quyền cho role thành công.");
    loadPermissionMatrixForSelectedRole();
  }

  private void openEditRoleForSelectedUserDialog() {
    int selectedRow = userTable == null ? -1 : userTable.getSelectedRow();
    if (selectedRow < 0) {
      DialogHelper.warn(this, "Vui lòng chọn account trong bảng để sửa role.");
      return;
    }

    int modelRow = userTable.convertRowIndexToModel(selectedRow);
    String userId = String.valueOf(userModel.getValueAt(modelRow, 0));
    UsersDTO user = usersBUS.getUserByID(userId);
    if (user == null) {
      DialogHelper.error(this, "Không tìm thấy account.");
      return;
    }

    JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
    form.setOpaque(false);
    form.add(new JLabel("Account"));
    form.add(new JLabel(user.getUserID() + " - " + user.getUsername()));

    JComboBox<String> cbRole = new JComboBox<>();
    for (RolesDTO role : roles) {
      cbRole.addItem(role.getSTT() + " - " + role.getTenVaiTro());
    }
    selectRoleInCombo(cbRole, String.valueOf(user.getRoleID()));

    form.add(new JLabel("Role mới"));
    form.add(cbRole);

    AdminDialogs.showFormDialog(
      this,
      "Sửa role cho account",
      form,
      () -> updateRoleForUser(user, cbRole),
      520,
      250
    );
  }

  private boolean updateRoleForUser(UsersDTO user, JComboBox<String> cbRole) {
    int roleId = extractSelectedRoleId(cbRole);
    if (roleId < 0) {
      DialogHelper.warn(this, "Vui lòng chọn role hợp lệ.");
      return false;
    }

    user.setRoleID(roleId);
    String message = usersBUS.updateUser(user);
    if (
      !message.toLowerCase().contains("thành công") &&
      !message.toLowerCase().contains("thanh cong")
    ) {
      DialogHelper.error(this, "Gán role thất bại: " + message);
      return false;
    }
    DialogHelper.info(this, "Đã gán role cho account thành công.");
    loadData();
    return true;
  }

  private void openCreateRoleDialog() {
    JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
    form.setOpaque(false);

    JTextField txtRoleId = new JTextField();
    JTextField txtRoleName = new JTextField();
    JTextArea txtDescription = new JTextArea(3, 20);
    txtDescription.setLineWrap(true);
    txtDescription.setWrapStyleWord(true);

    form.add(new JLabel("Role ID"));
    form.add(txtRoleId);
    form.add(new JLabel("Tên vai trò"));
    form.add(txtRoleName);
    form.add(new JLabel("Mô tả"));
    form.add(new JScrollPane(txtDescription));

    AdminDialogs.showFormDialog(
      this,
      "Tạo role mới",
      form,
      () -> {
        RolesDTO role = new RolesDTO();
        role.setSTT(txtRoleId.getText().trim());
        role.setTenVaiTro(txtRoleName.getText().trim());
        role.setMoTa(txtDescription.getText().trim());

        String result = rolesBUS.insertRoles(role);
        if (!result.toLowerCase().contains("thành công")) {
          DialogHelper.warn(this, result);
          return false;
        }
        DialogHelper.info(this, "Tạo role thành công.");
        loadData();
        selectRoleInCombo(cbRoleAssignPermission, role.getSTT());
        return true;
      },
      560,
      320
    );
  }

  private void deleteSelectedRole() {
    String roleId = getSelectedRoleId();
    if (roleId == null || roleId.isEmpty()) {
      DialogHelper.warn(this, "Vui lòng chọn role để xóa.");
      return;
    }

    if (!DialogHelper.confirm(this, "Xóa role " + roleId + "?")) {
      return;
    }

    rolePermissionsBUS.deleteAllPermissionsByRole(roleId);
    String result = rolesBUS.deleteRoles(roleId);
    if (!result.toLowerCase().contains("thành công")) {
      DialogHelper.error(this, "Xóa role thất bại: " + result);
      return;
    }
    DialogHelper.info(this, "Đã xóa role thành công.");
    loadData();
  }

  private String getSelectedRoleId() {
    int row = roleTable == null ? -1 : roleTable.getSelectedRow();
    if (row >= 0) {
      return String.valueOf(
        roleModel.getValueAt(roleTable.convertRowIndexToModel(row), 0)
      );
    }
    if (
      cbRoleAssignPermission != null &&
      cbRoleAssignPermission.getSelectedItem() != null
    ) {
      return String.valueOf(cbRoleAssignPermission.getSelectedItem())
        .split(" - ")[0].trim();
    }
    return "";
  }

  private void selectRoleInCombo(JComboBox<String> comboBox, String roleId) {
    if (comboBox == null || roleId == null) {
      return;
    }
    for (int i = 0; i < comboBox.getItemCount(); i++) {
      String text = String.valueOf(comboBox.getItemAt(i));
      if (text.startsWith(roleId + " ")) {
        comboBox.setSelectedIndex(i);
        return;
      }
    }
  }

  private int extractSelectedRoleId(JComboBox<String> comboBox) {
    if (comboBox == null || comboBox.getSelectedItem() == null) {
      return -1;
    }
    String text = String.valueOf(comboBox.getSelectedItem());
    try {
      return Integer.parseInt(text.split(" - ")[0].trim());
    } catch (Exception ex) {
      return -1;
    }
  }

  private void configurePermissionCheckboxColumn() {
    if (
      permissionTable == null ||
      permissionTable.getColumnModel().getColumnCount() == 0
    ) {
      return;
    }

    DefaultTableCellRenderer checkboxRenderer = new DefaultTableCellRenderer() {
      private final JCheckBox checkBox = new JCheckBox();

      @Override
      public java.awt.Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
      ) {
        checkBox.setHorizontalAlignment(JLabel.CENTER);
        checkBox.setSelected(Boolean.TRUE.equals(value));
        checkBox.setOpaque(true);
        if (isSelected) {
          checkBox.setBackground(table.getSelectionBackground());
        } else {
          checkBox.setBackground(table.getBackground());
        }
        return checkBox;
      }
    };

    permissionTable.getColumnModel().getColumn(0).setMinWidth(56);
    permissionTable.getColumnModel().getColumn(0).setMaxWidth(70);
    permissionTable
      .getColumnModel()
      .getColumn(0)
      .setCellRenderer(checkboxRenderer);
    permissionTable
      .getColumnModel()
      .getColumn(0)
      .setCellEditor(new DefaultCellEditor(new JCheckBox()));
  }
}
