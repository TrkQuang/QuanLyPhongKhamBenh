package phongkham.gui.phanquyen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.PermissionsDTO;
import phongkham.DTO.RolesDTO;

public class RolePermissionTabPanel extends JPanel {

  private final QuanLyPhanQuyenService service;

  private JTable roleListTable;
  private DefaultTableModel roleListTableModel;
  private JPanel permissionCheckboxPanel;
  private JButton btnSavePermissions;
  private Map<String, JCheckBox> permissionCheckboxMap;
  private String selectedRoleId;

  public RolePermissionTabPanel(QuanLyPhanQuyenService service) {
    this.service = service;

    setLayout(new BorderLayout(10, 10));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    initUi();
    refreshData();
  }

  private void initUi() {
    JPanel roleSelectionPanel = new JPanel(new BorderLayout());
    roleSelectionPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Chon Role de phan quyen",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    roleListTableModel = new DefaultTableModel(
      new String[] { "Ma Role", "Ten Role" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    roleListTable = new JTable(roleListTableModel);
    roleListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    roleListTable.setRowHeight(25);
    roleListTable
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        int row = roleListTable.getSelectedRow();
        if (row >= 0) {
          selectedRoleId = roleListTableModel.getValueAt(row, 0).toString();
          loadPermissionsForRole(selectedRoleId);
        }
      });

    JScrollPane roleScrollPane = new JScrollPane(roleListTable);
    roleScrollPane.setPreferredSize(new Dimension(300, 0));
    roleSelectionPanel.add(roleScrollPane, BorderLayout.CENTER);

    JPanel permissionPanel = new JPanel(new BorderLayout());
    permissionPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Danh sach quyen",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    permissionCheckboxPanel = new JPanel();
    permissionCheckboxPanel.setLayout(
      new BoxLayout(permissionCheckboxPanel, BoxLayout.Y_AXIS)
    );
    permissionPanel.add(
      new JScrollPane(permissionCheckboxPanel),
      BorderLayout.CENTER
    );

    btnSavePermissions = new JButton("Luu phan quyen");
    btnSavePermissions.setBackground(new Color(46, 204, 113));
    btnSavePermissions.setForeground(Color.WHITE);
    btnSavePermissions.setFocusPainted(false);
    btnSavePermissions.addActionListener(e -> savePermissionsForRole());

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnPanel.add(btnSavePermissions);
    permissionPanel.add(btnPanel, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      roleSelectionPanel,
      permissionPanel
    );
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.3);

    add(splitPane, BorderLayout.CENTER);
  }

  public void refreshData() {
    roleListTableModel.setRowCount(0);
    for (RolesDTO role : service.getAllRoles()) {
      roleListTableModel.addRow(
        new Object[] { role.getSTT(), role.getTenVaiTro() }
      );
    }
  }

  private void loadPermissionsForRole(String roleId) {
    permissionCheckboxPanel.removeAll();
    permissionCheckboxMap = new HashMap<>();

    ArrayList<PermissionsDTO> allPermissions = service.getAllPermissions();
    Map<String, Boolean> selectedMap = service.getPermissionStateByRole(roleId);

    for (PermissionsDTO permission : allPermissions) {
      String permId = String.valueOf(permission.getMaPermission());
      JCheckBox checkbox = new JCheckBox(
        permission.getTenPermission() + " - " + permission.getMoTa()
      );
      checkbox.setFont(new Font("Arial", Font.PLAIN, 12));
      checkbox.setBorder(new EmptyBorder(5, 10, 5, 10));
      checkbox.setSelected(Boolean.TRUE.equals(selectedMap.get(permId)));

      permissionCheckboxMap.put(permId, checkbox);
      permissionCheckboxPanel.add(checkbox);
    }

    permissionCheckboxPanel.revalidate();
    permissionCheckboxPanel.repaint();
  }

  private void savePermissionsForRole() {
    if (selectedRoleId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon Role!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    btnSavePermissions.setEnabled(false);
    btnSavePermissions.setText("Dang luu...");

    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        ArrayList<String> selectedPermissionIds = new ArrayList<>();
        for (Map.Entry<
          String,
          JCheckBox
        > entry : permissionCheckboxMap.entrySet()) {
          if (entry.getValue().isSelected()) {
            selectedPermissionIds.add(entry.getKey());
          }
        }
        service.savePermissionsForRole(selectedRoleId, selectedPermissionIds);
        return true;
      }

      @Override
      protected void done() {
        btnSavePermissions.setEnabled(true);
        btnSavePermissions.setText("Luu phan quyen");
        try {
          if (get()) {
            JOptionPane.showMessageDialog(
              RolePermissionTabPanel.this,
              "Luu phan quyen thanh cong!",
              "Thanh cong",
              JOptionPane.INFORMATION_MESSAGE
            );
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
            RolePermissionTabPanel.this,
            "Loi khi luu phan quyen: " + ex.getMessage(),
            "Loi",
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    };

    worker.execute();
  }

  public void clearSelection() {
    selectedRoleId = null;
    roleListTable.clearSelection();
    permissionCheckboxPanel.removeAll();
    permissionCheckboxPanel.revalidate();
    permissionCheckboxPanel.repaint();
  }
}
