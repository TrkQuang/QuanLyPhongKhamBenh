package phongkham.gui.taikhoan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.UsersDTO;

public class TaiKhoanDanhSachTabPanel extends JPanel {

  private final QuanLyTaiKhoanService service;
  private final Runnable onDataChanged;

  private JTable tableUsers;
  private DefaultTableModel usersModel;

  public TaiKhoanDanhSachTabPanel(
    QuanLyTaiKhoanService service,
    Runnable onDataChanged
  ) {
    this.service = service;
    this.onDataChanged = onDataChanged;

    setLayout(new BorderLayout(8, 8));
    setOpaque(false);

    initUi();
    refreshData();
  }

  private void initUi() {
    usersModel = new DefaultTableModel(
      new String[] { "UserID", "Username", "Email", "Vai tro", "Trang thai" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tableUsers = new JTable(usersModel);
    tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableUsers.setRowHeight(28);

    add(new JScrollPane(tableUsers), BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actions.setOpaque(false);

    JButton btnRefresh = createActionButton("Lam moi", new Color(59, 130, 246));
    JButton btnDisable = createActionButton(
      "Vo hieu hoa",
      new Color(239, 68, 68)
    );
    JButton btnEnable = createActionButton(
      "Kich hoat",
      new Color(16, 185, 129)
    );
    JButton btnResetPass = createActionButton(
      "Reset mat khau",
      new Color(245, 158, 11)
    );

    btnRefresh.addActionListener(e -> refreshData());
    btnDisable.addActionListener(e -> toggleUserActive(false));
    btnEnable.addActionListener(e -> toggleUserActive(true));
    btnResetPass.addActionListener(e -> resetPassword());

    actions.add(btnRefresh);
    actions.add(btnDisable);
    actions.add(btnEnable);
    actions.add(btnResetPass);

    add(actions, BorderLayout.SOUTH);
  }

  private JButton createActionButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    return button;
  }

  public void refreshData() {
    Map<Integer, String> roleMap = service.loadRoleMap();
    usersModel.setRowCount(0);

    for (UsersDTO user : service.getAllUsers()) {
      Integer roleId = user.getRoleID();
      String roleName =
        roleId == null ? "N/A" : roleMap.getOrDefault(roleId, "ROLE_" + roleId);
      usersModel.addRow(
        new Object[] {
          user.getUserID(),
          user.getUsername(),
          user.getEmail(),
          roleName,
          user.isActive() ? "Hoat dong" : "Da khoa",
        }
      );
    }
  }

  private String getSelectedUserId() {
    int row = tableUsers.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Vui long chon tai khoan.");
      return null;
    }
    int modelRow = tableUsers.convertRowIndexToModel(row);
    return usersModel.getValueAt(modelRow, 0).toString();
  }

  private void toggleUserActive(boolean enable) {
    String userId = getSelectedUserId();
    if (userId == null) {
      return;
    }

    String message = enable
      ? service.enableUser(userId)
      : service.disableUser(userId);
    JOptionPane.showMessageDialog(this, message);

    if (onDataChanged != null) {
      onDataChanged.run();
    }
  }

  private void resetPassword() {
    String userId = getSelectedUserId();
    if (userId == null) {
      return;
    }

    String newPassword = JOptionPane.showInputDialog(
      this,
      "Nhap mat khau moi (>= 6 ky tu):",
      "Reset mat khau",
      JOptionPane.QUESTION_MESSAGE
    );

    if (newPassword == null) {
      return;
    }

    String message = service.resetPassword(userId, newPassword);
    JOptionPane.showMessageDialog(this, message);
  }
}
