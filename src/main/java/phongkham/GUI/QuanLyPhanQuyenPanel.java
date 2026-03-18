package phongkham.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import phongkham.gui.phanquyen.QuanLyPhanQuyenService;
import phongkham.gui.phanquyen.RoleManagementTabPanel;
import phongkham.gui.phanquyen.RolePermissionTabPanel;
import phongkham.gui.phanquyen.UserRoleTabPanel;

/**
 * Panel quan ly phan quyen (RBAC) - ban da tach service va cac tab con.
 */
public class QuanLyPhanQuyenPanel extends JPanel {

  private final QuanLyPhanQuyenService service;
  private RoleManagementTabPanel roleManagementTab;
  private RolePermissionTabPanel rolePermissionTab;
  private UserRoleTabPanel userRoleTab;

  public QuanLyPhanQuyenPanel() {
    this.service = new QuanLyPhanQuyenService();

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    initComponents();
    refreshAll();
  }

  private void initComponents() {
    JLabel lblTitle = new JLabel("QUAN LY PHAN QUYEN HE THONG", JLabel.CENTER);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitle.setBorder(new EmptyBorder(10, 10, 20, 10));
    add(lblTitle, BorderLayout.NORTH);

    JTabbedPane tabbedPane = new JTabbedPane();

    roleManagementTab = new RoleManagementTabPanel(service, this::refreshAll);
    rolePermissionTab = new RolePermissionTabPanel(service);
    userRoleTab = new UserRoleTabPanel(service);

    tabbedPane.addTab("Quan ly Role", roleManagementTab);
    tabbedPane.addTab("Phan quyen cho Role", rolePermissionTab);
    tabbedPane.addTab("Gan Role cho User", userRoleTab);

    add(tabbedPane, BorderLayout.CENTER);
  }

  public void refreshAll() {
    if (roleManagementTab != null) {
      roleManagementTab.refreshData();
      roleManagementTab.clearForm();
    }
    if (rolePermissionTab != null) {
      rolePermissionTab.refreshData();
      rolePermissionTab.clearSelection();
    }
    if (userRoleTab != null) {
      userRoleTab.refreshData();
      userRoleTab.clearSelection();
    }
  }
}
