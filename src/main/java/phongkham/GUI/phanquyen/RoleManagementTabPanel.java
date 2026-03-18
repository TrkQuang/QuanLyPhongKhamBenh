package phongkham.gui.phanquyen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.RolesDTO;

public class RoleManagementTabPanel extends JPanel {

  private final QuanLyPhanQuyenService service;
  private final Runnable onDataChanged;

  private JTable roleTable;
  private DefaultTableModel roleTableModel;
  private JTextField txtRoleName;
  private JTextField txtRoleDesc;
  private String selectedRoleId;

  public RoleManagementTabPanel(
    QuanLyPhanQuyenService service,
    Runnable onDataChanged
  ) {
    this.service = service;
    this.onDataChanged = onDataChanged;

    setLayout(new BorderLayout(10, 10));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    initUi();
    refreshData();
  }

  private void initUi() {
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Danh sach Role",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    String[] columns = { "Ma Role", "Ten Role", "Mo ta" };
    roleTableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    roleTable = new JTable(roleTableModel);
    roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    roleTable.setRowHeight(25);
    roleTable.getTableHeader().setReorderingAllowed(false);

    roleTable
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        int row = roleTable.getSelectedRow();
        if (row >= 0) {
          selectedRoleId = roleTableModel.getValueAt(row, 0).toString();
          txtRoleName.setText(roleTableModel.getValueAt(row, 1).toString());
          txtRoleDesc.setText(roleTableModel.getValueAt(row, 2).toString());
        }
      });

    tablePanel.add(new JScrollPane(roleTable), BorderLayout.CENTER);

    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Thong tin Role",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    inputPanel.add(new JLabel("Ten Role:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtRoleName = new JTextField(20);
    inputPanel.add(txtRoleName, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    inputPanel.add(new JLabel("Mo ta:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtRoleDesc = new JTextField(20);
    inputPanel.add(txtRoleDesc, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton btnAddRole = createButton("Them Role", new Color(46, 204, 113));
    JButton btnEditRole = createButton("Sua Role", new Color(52, 152, 219));
    JButton btnDeleteRole = createButton("Xoa Role", new Color(231, 76, 60));
    JButton btnClearRole = createButton("Lam moi", new Color(149, 165, 166));

    btnAddRole.addActionListener(e -> addRole());
    btnEditRole.addActionListener(e -> editRole());
    btnDeleteRole.addActionListener(e -> deleteRole());
    btnClearRole.addActionListener(e -> clearForm());

    buttonPanel.add(btnAddRole);
    buttonPanel.add(btnEditRole);
    buttonPanel.add(btnDeleteRole);
    buttonPanel.add(btnClearRole);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    inputPanel.add(buttonPanel, gbc);

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.VERTICAL_SPLIT,
      tablePanel,
      inputPanel
    );
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.6);

    add(splitPane, BorderLayout.CENTER);
  }

  private JButton createButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    return button;
  }

  public void refreshData() {
    roleTableModel.setRowCount(0);
    for (RolesDTO role : service.getAllRoles()) {
      roleTableModel.addRow(
        new Object[] { role.getSTT(), role.getTenVaiTro(), role.getMoTa() }
      );
    }
  }

  private void addRole() {
    String roleName = txtRoleName.getText().trim();
    String roleDesc = txtRoleDesc.getText().trim();

    if (roleName.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long nhap ten Role!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    RolesDTO newRole = new RolesDTO(
      service.generateNextRoleId(),
      roleName,
      roleDesc
    );

    if (service.addRole(newRole)) {
      JOptionPane.showMessageDialog(
        this,
        "Them Role thanh cong!",
        "Thanh cong",
        JOptionPane.INFORMATION_MESSAGE
      );
      clearForm();
      if (onDataChanged != null) {
        onDataChanged.run();
      }
    } else {
      JOptionPane.showMessageDialog(
        this,
        "Them Role that bai!",
        "Loi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void editRole() {
    if (selectedRoleId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon Role can sua!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    String roleName = txtRoleName.getText().trim();
    String roleDesc = txtRoleDesc.getText().trim();

    if (roleName.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long nhap ten Role!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    RolesDTO role = new RolesDTO(selectedRoleId, roleName, roleDesc);

    if (service.updateRole(role)) {
      JOptionPane.showMessageDialog(
        this,
        "Cap nhat Role thanh cong!",
        "Thanh cong",
        JOptionPane.INFORMATION_MESSAGE
      );
      clearForm();
      if (onDataChanged != null) {
        onDataChanged.run();
      }
    } else {
      JOptionPane.showMessageDialog(
        this,
        "Cap nhat Role that bai!",
        "Loi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void deleteRole() {
    if (selectedRoleId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon Role can xoa!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Ban co chac chan muon xoa Role nay?\nTat ca quyen va User gan voi Role nay se bi xoa!",
      "Xac nhan xoa",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    if (service.deleteRole(selectedRoleId)) {
      JOptionPane.showMessageDialog(
        this,
        "Xoa Role thanh cong!",
        "Thanh cong",
        JOptionPane.INFORMATION_MESSAGE
      );
      clearForm();
      if (onDataChanged != null) {
        onDataChanged.run();
      }
    } else {
      JOptionPane.showMessageDialog(
        this,
        "Xoa Role that bai!",
        "Loi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  public void clearForm() {
    txtRoleName.setText("");
    txtRoleDesc.setText("");
    selectedRoleId = null;
    roleTable.clearSelection();
  }
}
