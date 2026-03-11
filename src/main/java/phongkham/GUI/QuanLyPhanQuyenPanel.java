package phongkham.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.*;
import phongkham.DTO.*;

/**
 * Panel quản lý phân quyền (RBAC - Role-Based Access Control)
 * Bao gồm 3 chức năng chính:
 * 1. Quản lý Role (Vai trò)
 * 2. Phân quyền cho Role
 * 3. Gán Role cho User
 */
public class QuanLyPhanQuyenPanel extends JPanel {

  // Business Logic Layer
  private RolesBUS rolesBUS;
  private PermissionBUS permissionBUS;
  private RolePermissionsBUS rolePermissionsBUS;
  private UsersBUS usersBUS;
  private UsersRolesBUS usersRolesBUS;

  // Main Components
  private JTabbedPane tabbedPane;

  // Tab 1: Quản lý Role
  private JTable roleTable;
  private DefaultTableModel roleTableModel;
  private JTextField txtRoleName, txtRoleDesc;
  private JButton btnAddRole, btnEditRole, btnDeleteRole, btnClearRole;
  private String selectedRoleId = null;

  // Tab 2: Phân quyền cho Role
  private JTable roleListTable;
  private DefaultTableModel roleListTableModel;
  private JPanel permissionCheckboxPanel;
  private JButton btnSavePermissions;
  private Map<String, JCheckBox> permissionCheckboxMap; // Map: permissionId -> checkbox
  private String selectedRoleIdForPermission = null;

  // Tab 3: Gán Role cho User
  private JTable userTable;
  private DefaultTableModel userTableModel;
  private JList<String> roleList;
  private DefaultListModel<String> roleListModel;
  private JButton btnAssignRole, btnRemoveRole;
  private String selectedUserId = null;
  private Map<String, String> roleIdMap; // Map: displayName -> roleId

  public QuanLyPhanQuyenPanel() {
    initBUS();
    initComponents();
    loadData();
  }

  /**
   * Khởi tạo các BUS layer
   */
  private void initBUS() {
    rolesBUS = new RolesBUS();
    permissionBUS = new PermissionBUS();
    rolePermissionsBUS = new RolePermissionsBUS();
    usersBUS = new UsersBUS();
    usersRolesBUS = new UsersRolesBUS();
  }

  /**
   * Khởi tạo giao diện
   */
  private void initComponents() {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // Title
    JLabel lblTitle = new JLabel("QUẢN LÝ PHÂN QUYỀN HỆ THỐNG", JLabel.CENTER);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitle.setBorder(new EmptyBorder(10, 10, 20, 10));
    add(lblTitle, BorderLayout.NORTH);

    // Tabbed Pane
    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Quản lý Role", createRoleManagementPanel());
    tabbedPane.addTab("Phân quyền cho Role", createRolePermissionPanel());
    tabbedPane.addTab("Gán Role cho User", createUserRolePanel());

    add(tabbedPane, BorderLayout.CENTER);
  }

  // ==================== TAB 1: QUẢN LÝ ROLE ====================

  /**
   * Tạo panel quản lý Role (CRUD)
   */
  private JPanel createRoleManagementPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Bảng hiển thị danh sách Role
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Danh sách Role",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    String[] columns = { "Mã Role", "Tên Role", "Mô tả" };
    roleTableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Không cho chỉnh sửa trực tiếp
      }
    };
    roleTable = new JTable(roleTableModel);
    roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    roleTable.setRowHeight(25);
    roleTable.getTableHeader().setReorderingAllowed(false);

    // Sự kiện chọn Role
    roleTable.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int row = roleTable.getSelectedRow();
          if (row >= 0) {
            selectedRoleId = roleTableModel.getValueAt(row, 0).toString();
            txtRoleName.setText(roleTableModel.getValueAt(row, 1).toString());
            txtRoleDesc.setText(roleTableModel.getValueAt(row, 2).toString());
          }
        }
      }
    );

    JScrollPane scrollPane = new JScrollPane(roleTable);
    tablePanel.add(scrollPane, BorderLayout.CENTER);

    // Panel nhập liệu
    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Thông tin Role",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Tên Role
    gbc.gridx = 0;
    gbc.gridy = 0;
    inputPanel.add(new JLabel("Tên Role:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtRoleName = new JTextField(20);
    inputPanel.add(txtRoleName, gbc);

    // Mô tả
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    inputPanel.add(new JLabel("Mô tả:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtRoleDesc = new JTextField(20);
    inputPanel.add(txtRoleDesc, gbc);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    btnAddRole = new JButton("➕ Thêm Role");
    btnAddRole.setBackground(new Color(46, 204, 113));
    btnAddRole.setForeground(Color.WHITE);
    btnAddRole.setFocusPainted(false);
    btnAddRole.addActionListener(e -> addRole());

    btnEditRole = new JButton("✏️ Sửa Role");
    btnEditRole.setBackground(new Color(52, 152, 219));
    btnEditRole.setForeground(Color.WHITE);
    btnEditRole.setFocusPainted(false);
    btnEditRole.addActionListener(e -> editRole());

    btnDeleteRole = new JButton("🗑️ Xóa Role");
    btnDeleteRole.setBackground(new Color(231, 76, 60));
    btnDeleteRole.setForeground(Color.WHITE);
    btnDeleteRole.setFocusPainted(false);
    btnDeleteRole.addActionListener(e -> deleteRole());

    btnClearRole = new JButton("🔄 Làm mới");
    btnClearRole.setBackground(new Color(149, 165, 166));
    btnClearRole.setForeground(Color.WHITE);
    btnClearRole.setFocusPainted(false);
    btnClearRole.addActionListener(e -> clearRoleForm());

    buttonPanel.add(btnAddRole);
    buttonPanel.add(btnEditRole);
    buttonPanel.add(btnDeleteRole);
    buttonPanel.add(btnClearRole);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    inputPanel.add(buttonPanel, gbc);

    // Layout chính
    JSplitPane splitPane = new JSplitPane(
      JSplitPane.VERTICAL_SPLIT,
      tablePanel,
      inputPanel
    );
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.6);

    panel.add(splitPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Thêm Role mới
   */
  private void addRole() {
    String roleName = txtRoleName.getText().trim();
    String roleDesc = txtRoleDesc.getText().trim();

    if (roleName.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập tên Role!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    // Tạo mã Role tự động (lấy số lượng + 1)
    String roleId = String.valueOf(rolesBUS.getAllRoles().size() + 1);

    RolesDTO newRole = new RolesDTO(roleId, roleName, roleDesc);

    if (rolesBUS.addRole(newRole)) {
      JOptionPane.showMessageDialog(
        this,
        "✅ Thêm Role thành công!",
        "Thành công",
        JOptionPane.INFORMATION_MESSAGE
      );
      loadRoleTable();
      clearRoleForm();
    } else {
      JOptionPane.showMessageDialog(
        this,
        "❌ Thêm Role thất bại!",
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  /**
   * Sửa Role
   */
  private void editRole() {
    if (selectedRoleId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn Role cần sửa!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    String roleName = txtRoleName.getText().trim();
    String roleDesc = txtRoleDesc.getText().trim();

    if (roleName.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập tên Role!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    RolesDTO role = new RolesDTO(selectedRoleId, roleName, roleDesc);

    if (rolesBUS.updateRole(role)) {
      JOptionPane.showMessageDialog(
        this,
        "✅ Cập nhật Role thành công!",
        "Thành công",
        JOptionPane.INFORMATION_MESSAGE
      );
      loadRoleTable();
      clearRoleForm();
    } else {
      JOptionPane.showMessageDialog(
        this,
        "❌ Cập nhật Role thất bại!",
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  /**
   * Xóa Role
   */
  private void deleteRole() {
    if (selectedRoleId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn Role cần xóa!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Bạn có chắc chắn muốn xóa Role này?\nLưu ý: Tất cả quyền và User gắn với Role này sẽ bị xóa!",
      "Xác nhận xóa",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
      if (rolesBUS.deleteRole(selectedRoleId)) {
        JOptionPane.showMessageDialog(
          this,
          "✅ Xóa Role thành công!",
          "Thành công",
          JOptionPane.INFORMATION_MESSAGE
        );
        loadRoleTable();
        clearRoleForm();
      } else {
        JOptionPane.showMessageDialog(
          this,
          "❌ Xóa Role thất bại!",
          "Lỗi",
          JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }

  /**
   * Xóa form nhập Role
   */
  private void clearRoleForm() {
    txtRoleName.setText("");
    txtRoleDesc.setText("");
    selectedRoleId = null;
    roleTable.clearSelection();
  }

  /**
   * Load danh sách Role vào bảng
   */
  private void loadRoleTable() {
    roleTableModel.setRowCount(0);
    ArrayList<RolesDTO> roles = rolesBUS.getAllRoles();

    for (RolesDTO role : roles) {
      Object[] row = { role.getSTT(), role.getTenVaiTro(), role.getMoTa() };
      roleTableModel.addRow(row);
    }
  }

  // ==================== TAB 2: PHÂN QUYỀN CHO ROLE ====================

  /**
   * Tạo panel phân quyền cho Role
   */
  private JPanel createRolePermissionPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Panel chọn Role
    JPanel roleSelectionPanel = new JPanel(new BorderLayout());
    roleSelectionPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Chọn Role để phân quyền",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    String[] columns = { "Mã Role", "Tên Role" };
    roleListTableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    roleListTable = new JTable(roleListTableModel);
    roleListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    roleListTable.setRowHeight(25);

    // Sự kiện chọn Role để load quyền
    roleListTable.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int row = roleListTable.getSelectedRow();
          if (row >= 0) {
            selectedRoleIdForPermission = roleListTableModel
              .getValueAt(row, 0)
              .toString();
            loadPermissionsForRole(selectedRoleIdForPermission);
          }
        }
      }
    );

    JScrollPane roleScrollPane = new JScrollPane(roleListTable);
    roleScrollPane.setPreferredSize(new Dimension(300, 0));
    roleSelectionPanel.add(roleScrollPane, BorderLayout.CENTER);

    // Panel hiển thị danh sách Permission với checkbox
    JPanel permissionPanel = new JPanel(new BorderLayout());
    permissionPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Danh sách quyền (Permission)",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    // Panel chứa các checkbox
    permissionCheckboxPanel = new JPanel();
    permissionCheckboxPanel.setLayout(
      new BoxLayout(permissionCheckboxPanel, BoxLayout.Y_AXIS)
    );
    JScrollPane permissionScrollPane = new JScrollPane(permissionCheckboxPanel);
    permissionPanel.add(permissionScrollPane, BorderLayout.CENTER);

    // Nút lưu
    btnSavePermissions = new JButton("💾 Lưu phân quyền");
    btnSavePermissions.setBackground(new Color(46, 204, 113));
    btnSavePermissions.setForeground(Color.WHITE);
    btnSavePermissions.setFont(new Font("Arial", Font.BOLD, 14));
    btnSavePermissions.setFocusPainted(false);
    btnSavePermissions.addActionListener(e -> savePermissionsForRole());

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnPanel.add(btnSavePermissions);
    permissionPanel.add(btnPanel, BorderLayout.SOUTH);

    // Split pane
    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      roleSelectionPanel,
      permissionPanel
    );
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.3);

    panel.add(splitPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Load tất cả Permission và đánh dấu checkbox cho Role
   */
  private void loadPermissionsForRole(String roleId) {
    permissionCheckboxPanel.removeAll();
    permissionCheckboxMap = new HashMap<>();

    // Lấy tất cả permission trong hệ thống
    ArrayList<PermissionsDTO> allPermissions =
      permissionBUS.getAllPermissions();

    // Lấy các permission mà role này có
    ArrayList<RolePermissionsDTO> rolePermissions =
      rolePermissionsBUS.getPermissionsByRole(roleId);

    // Tạo Set để tra cứu nhanh
    Map<String, Boolean> permissionMap = new HashMap<>();
    for (RolePermissionsDTO rp : rolePermissions) {
      permissionMap.put(String.valueOf(rp.getMaPermission()), rp.isActive());
    }

    // Tạo checkbox cho mỗi permission
    for (PermissionsDTO permission : allPermissions) {
      String permId = String.valueOf(permission.getMaPermission());
      JCheckBox checkbox = new JCheckBox(
        permission.getTenPermission() + " - " + permission.getMoTa()
      );
      checkbox.setFont(new Font("Arial", Font.PLAIN, 12));
      checkbox.setBorder(new EmptyBorder(5, 10, 5, 10));

      // Đánh dấu checkbox nếu role có permission này
      if (permissionMap.containsKey(permId) && permissionMap.get(permId)) {
        checkbox.setSelected(true);
      }

      permissionCheckboxMap.put(permId, checkbox);
      permissionCheckboxPanel.add(checkbox);
    }

    permissionCheckboxPanel.revalidate();
    permissionCheckboxPanel.repaint();
  }

  /**
   * Lưu phân quyền cho Role
   */
  private void savePermissionsForRole() {
    if (selectedRoleIdForPermission == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn Role!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    // Disable button để tránh click nhiều lần
    btnSavePermissions.setEnabled(false);
    btnSavePermissions.setText("⏳ Đang lưu...");

    // Chạy trong background thread
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        // Xóa tất cả permission cũ của role này
        rolePermissionsBUS.deleteAllPermissionsByRole(
          selectedRoleIdForPermission
        );

        // Thêm lại các permission được chọn
        int maRole = Integer.parseInt(selectedRoleIdForPermission);
        for (Map.Entry<
          String,
          JCheckBox
        > entry : permissionCheckboxMap.entrySet()) {
          if (entry.getValue().isSelected()) {
            int maPermission = Integer.parseInt(entry.getKey());
            RolePermissionsDTO rp = new RolePermissionsDTO(
              maRole,
              maPermission,
              "",
              true
            );
            rolePermissionsBUS.addRolePermission(rp);
          }
        }
        return true;
      }

      @Override
      protected void done() {
        // Re-enable button
        btnSavePermissions.setEnabled(true);
        btnSavePermissions.setText("💾 Lưu phân quyền");

        try {
          if (get()) {
            JOptionPane.showMessageDialog(
              QuanLyPhanQuyenPanel.this,
              "✅ Lưu phân quyền thành công!",
              "Thành công",
              JOptionPane.INFORMATION_MESSAGE
            );
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
            QuanLyPhanQuyenPanel.this,
            "❌ Lỗi khi lưu phân quyền: " + ex.getMessage(),
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
          );
          ex.printStackTrace();
        }
      }
    };
    worker.execute();
  }

  /**
   * Load danh sách Role vào bảng chọn
   */
  private void loadRoleListTable() {
    roleListTableModel.setRowCount(0);
    ArrayList<RolesDTO> roles = rolesBUS.getAllRoles();

    for (RolesDTO role : roles) {
      Object[] row = { role.getSTT(), role.getTenVaiTro() };
      roleListTableModel.addRow(row);
    }
  }

  // ==================== TAB 3: GÁN ROLE CHO USER ====================

  /**
   * Tạo panel gán Role cho User
   */
  private JPanel createUserRolePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Panel danh sách User
    JPanel userPanel = new JPanel(new BorderLayout());
    userPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Danh sách User",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    String[] columns = { "User ID", "Username", "Email" };
    userTableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    userTable = new JTable(userTableModel);
    userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userTable.setRowHeight(25);

    // Sự kiện chọn User
    userTable.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int row = userTable.getSelectedRow();
          if (row >= 0) {
            selectedUserId = userTableModel.getValueAt(row, 0).toString();
            loadRolesForUser(selectedUserId);
          }
        }
      }
    );

    JScrollPane userScrollPane = new JScrollPane(userTable);
    userScrollPane.setPreferredSize(new Dimension(400, 0));
    userPanel.add(userScrollPane, BorderLayout.CENTER);

    // Panel hiển thị Role của User
    JPanel rolePanel = new JPanel(new BorderLayout());
    rolePanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Role của User",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    roleListModel = new DefaultListModel<>();
    roleList = new JList<>(roleListModel);
    roleList.setFont(new Font("Arial", Font.PLAIN, 13));
    JScrollPane roleScrollPane = new JScrollPane(roleList);
    rolePanel.add(roleScrollPane, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    btnAssignRole = new JButton("➕ Gán Role");
    btnAssignRole.setBackground(new Color(46, 204, 113));
    btnAssignRole.setForeground(Color.WHITE);
    btnAssignRole.setFocusPainted(false);
    btnAssignRole.addActionListener(e -> assignRoleToUser());

    btnRemoveRole = new JButton("➖ Gỡ Role");
    btnRemoveRole.setBackground(new Color(231, 76, 60));
    btnRemoveRole.setForeground(Color.WHITE);
    btnRemoveRole.setFocusPainted(false);
    btnRemoveRole.addActionListener(e -> removeRoleFromUser());

    buttonPanel.add(btnAssignRole);
    buttonPanel.add(btnRemoveRole);

    rolePanel.add(buttonPanel, BorderLayout.SOUTH);

    // Split pane
    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      userPanel,
      rolePanel
    );
    splitPane.setDividerLocation(400);
    splitPane.setResizeWeight(0.5);

    panel.add(splitPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Load danh sách Role của User
   */
  private void loadRolesForUser(String userId) {
    roleListModel.clear();
    ArrayList<UsersRolesDTO> userRoles = usersRolesBUS.getRolesByUser(userId);

    for (UsersRolesDTO ur : userRoles) {
      String roleId = ur.getRole_ID();

      // Lấy thông tin Role
      RolesDTO role = rolesBUS.getRoleById(roleId);

      if (role != null) {
        String display = role.getSTT() + " - " + role.getTenVaiTro();
        roleListModel.addElement(display);
      }
    }
  }

  /**
   * Gán Role cho User
   */
  private void assignRoleToUser() {
    if (selectedUserId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn User!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    // Hiển thị dialog chọn Role
    ArrayList<RolesDTO> allRoles = rolesBUS.getAllRoles();
    String[] roleNames = new String[allRoles.size()];
    roleIdMap = new HashMap<>();

    for (int i = 0; i < allRoles.size(); i++) {
      RolesDTO role = allRoles.get(i);
      String display = role.getSTT() + " - " + role.getTenVaiTro();
      roleNames[i] = display;
      roleIdMap.put(display, role.getSTT());
    }

    String selected = (String) JOptionPane.showInputDialog(
      this,
      "Chọn Role để gán:",
      "Gán Role cho User",
      JOptionPane.QUESTION_MESSAGE,
      null,
      roleNames,
      roleNames[0]
    );

    if (selected != null) {
      String roleId = roleIdMap.get(selected);
      String userId = selectedUserId;

      // Chạy trong background thread
      SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
          // Kiểm tra User đã có Role này chưa
          if (usersRolesBUS.hasUserRole(userId, roleId)) {
            return null; // Trả về null để biết là đã có role
          }

          UsersRolesDTO userRole = new UsersRolesDTO(userId, roleId);
          return usersRolesBUS.addUserRole(userRole);
        }

        @Override
        protected void done() {
          try {
            Boolean result = get();
            if (result == null) {
              JOptionPane.showMessageDialog(
                QuanLyPhanQuyenPanel.this,
                "User đã có Role này rồi!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
              );
            } else if (result) {
              JOptionPane.showMessageDialog(
                QuanLyPhanQuyenPanel.this,
                "✅ Gán Role thành công!",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
              );
              loadRolesForUser(userId);
            } else {
              JOptionPane.showMessageDialog(
                QuanLyPhanQuyenPanel.this,
                "❌ Gán Role thất bại!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
              );
            }
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
              QuanLyPhanQuyenPanel.this,
              "❌ Lỗi: " + ex.getMessage(),
              "Lỗi",
              JOptionPane.ERROR_MESSAGE
            );
          }
        }
      };
      worker.execute();
    }
  }

  /**
   * Gỡ Role khỏi User
   */
  private void removeRoleFromUser() {
    if (selectedUserId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn User!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    String selectedRole = roleList.getSelectedValue();
    if (selectedRole == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn Role cần gỡ!",
        "Cảnh báo",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    // Lấy roleId từ chuỗi "1 - Admin"
    String roleId = selectedRole.split(" - ")[0];

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Bạn có chắc chắn muốn gỡ Role này khỏi User?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      String userId = selectedUserId;

      // Chạy trong background thread
      SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
          return usersRolesBUS.deleteUserRole(userId, roleId);
        }

        @Override
        protected void done() {
          try {
            Boolean result = get();
            if (result) {
              JOptionPane.showMessageDialog(
                QuanLyPhanQuyenPanel.this,
                "✅ Gỡ Role thành công!",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
              );
              loadRolesForUser(userId);
            } else {
              JOptionPane.showMessageDialog(
                QuanLyPhanQuyenPanel.this,
                "❌ Gỡ Role thất bại!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
              );
            }
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
              QuanLyPhanQuyenPanel.this,
              "❌ Lỗi: " + ex.getMessage(),
              "Lỗi",
              JOptionPane.ERROR_MESSAGE
            );
          }
        }
      };
      worker.execute();
    }
  }

  /**
   * Load danh sách User vào bảng
   */
  private void loadUserTable() {
    userTableModel.setRowCount(0);
    ArrayList<UsersDTO> users = usersBUS.getAllUsers();

    for (UsersDTO user : users) {
      Object[] row = { user.getUserID(), user.getUsername(), user.getEmail() };
      userTableModel.addRow(row);
    }
  }

  // ==================== LOAD DỮ LIỆU BAN ĐẦU ====================

  /**
   * Load tất cả dữ liệu khi khởi tạo panel
   */
  private void loadData() {
    loadRoleTable();
    loadRoleListTable();
    loadUserTable();
  }

  /**
   * Refresh toàn bộ dữ liệu
   */
  public void refreshAll() {
    loadData();
    clearRoleForm();
    permissionCheckboxPanel.removeAll();
    permissionCheckboxPanel.revalidate();
    permissionCheckboxPanel.repaint();
    roleListModel.clear();
  }
}
