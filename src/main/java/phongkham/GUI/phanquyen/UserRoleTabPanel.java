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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
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
import phongkham.DTO.RolesDTO;
import phongkham.DTO.UsersDTO;
import phongkham.DTO.UsersRolesDTO;

public class UserRoleTabPanel extends JPanel {

  private final QuanLyPhanQuyenService service;

  private JTable userTable;
  private DefaultTableModel userTableModel;
  private JList<String> roleList;
  private DefaultListModel<String> roleListModel;

  private String selectedUserId;
  private Map<String, String> roleIdMap;

  public UserRoleTabPanel(QuanLyPhanQuyenService service) {
    this.service = service;

    setLayout(new BorderLayout(10, 10));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    initUi();
    refreshData();
  }

  private void initUi() {
    JPanel userPanel = new JPanel(new BorderLayout());
    userPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Danh sach User",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    userTableModel = new DefaultTableModel(
      new String[] { "User ID", "Username", "Email" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    userTable = new JTable(userTableModel);
    userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userTable.setRowHeight(25);
    userTable
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        int row = userTable.getSelectedRow();
        if (row >= 0) {
          selectedUserId = userTableModel.getValueAt(row, 0).toString();
          loadRolesForUser(selectedUserId);
        }
      });

    JScrollPane userScrollPane = new JScrollPane(userTable);
    userScrollPane.setPreferredSize(new Dimension(400, 0));
    userPanel.add(userScrollPane, BorderLayout.CENTER);

    JPanel rolePanel = new JPanel(new BorderLayout());
    rolePanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "Role cua User",
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Arial", Font.BOLD, 14)
      )
    );

    roleListModel = new DefaultListModel<>();
    roleList = new JList<>(roleListModel);
    roleList.setFont(new Font("Arial", Font.PLAIN, 13));
    rolePanel.add(new JScrollPane(roleList), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton btnAssignRole = createButton("Gan Role", new Color(46, 204, 113));
    JButton btnRemoveRole = createButton("Go Role", new Color(231, 76, 60));

    btnAssignRole.addActionListener(e -> assignRoleToUser());
    btnRemoveRole.addActionListener(e -> removeRoleFromUser());

    buttonPanel.add(btnAssignRole);
    buttonPanel.add(btnRemoveRole);

    rolePanel.add(buttonPanel, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      userPanel,
      rolePanel
    );
    splitPane.setDividerLocation(400);
    splitPane.setResizeWeight(0.5);

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
    userTableModel.setRowCount(0);
    for (UsersDTO user : service.getAllUsers()) {
      userTableModel.addRow(
        new Object[] { user.getUserID(), user.getUsername(), user.getEmail() }
      );
    }
  }

  private void loadRolesForUser(String userId) {
    roleListModel.clear();

    ArrayList<UsersRolesDTO> userRoles = service.getRolesByUser(userId);
    for (UsersRolesDTO ur : userRoles) {
      String roleId = ur.getRole_ID();
      RolesDTO role = service.getRoleById(roleId);
      if (role != null) {
        roleListModel.addElement(role.getSTT() + " - " + role.getTenVaiTro());
      }
    }
  }

  private void assignRoleToUser() {
    if (selectedUserId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon User!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    ArrayList<RolesDTO> allRoles = service.getAllRoles();
    if (allRoles.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Khong co role nao de gan!",
        "Thong bao",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

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
      "Chon Role de gan:",
      "Gan Role cho User",
      JOptionPane.QUESTION_MESSAGE,
      null,
      roleNames,
      roleNames[0]
    );

    if (selected == null) {
      return;
    }

    String roleId = roleIdMap.get(selected);
    String userId = selectedUserId;

    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        if (service.hasUserRole(userId, roleId)) {
          return null;
        }
        return service.addUserRole(userId, roleId);
      }

      @Override
      protected void done() {
        try {
          Boolean result = get();
          if (result == null) {
            JOptionPane.showMessageDialog(
              UserRoleTabPanel.this,
              "User da co Role nay roi!",
              "Canh bao",
              JOptionPane.WARNING_MESSAGE
            );
          } else if (result) {
            JOptionPane.showMessageDialog(
              UserRoleTabPanel.this,
              "Gan Role thanh cong!",
              "Thanh cong",
              JOptionPane.INFORMATION_MESSAGE
            );
            loadRolesForUser(userId);
          } else {
            JOptionPane.showMessageDialog(
              UserRoleTabPanel.this,
              "Gan Role that bai!",
              "Loi",
              JOptionPane.ERROR_MESSAGE
            );
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
            UserRoleTabPanel.this,
            "Loi: " + ex.getMessage(),
            "Loi",
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    };

    worker.execute();
  }

  private void removeRoleFromUser() {
    if (selectedUserId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon User!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    String selectedRole = roleList.getSelectedValue();
    if (selectedRole == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon Role can go!",
        "Canh bao",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    String roleId = selectedRole.split(" - ")[0];

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Ban co chac chan muon go Role nay khoi User?",
      "Xac nhan",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    String userId = selectedUserId;
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        return service.deleteUserRole(userId, roleId);
      }

      @Override
      protected void done() {
        try {
          Boolean result = get();
          if (result) {
            JOptionPane.showMessageDialog(
              UserRoleTabPanel.this,
              "Go Role thanh cong!",
              "Thanh cong",
              JOptionPane.INFORMATION_MESSAGE
            );
            loadRolesForUser(userId);
          } else {
            JOptionPane.showMessageDialog(
              UserRoleTabPanel.this,
              "Go Role that bai!",
              "Loi",
              JOptionPane.ERROR_MESSAGE
            );
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
            UserRoleTabPanel.this,
            "Loi: " + ex.getMessage(),
            "Loi",
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    };

    worker.execute();
  }

  public void clearSelection() {
    selectedUserId = null;
    userTable.clearSelection();
    roleListModel.clear();
  }
}
