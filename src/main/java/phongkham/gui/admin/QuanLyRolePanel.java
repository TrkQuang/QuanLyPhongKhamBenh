package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.RolesBUS;
import phongkham.DTO.RolesDTO;
import phongkham.Utils.Session;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyRolePanel extends BasePanel {

  private final RolesBUS rolesBUS = new RolesBUS();
  private JTable table;
  private JButton btnThem;
  private JButton btnSua;
  private JButton btnXoa;
  private JButton btnTaiLai;
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] { "RoleID", "Ten vai tro", "Mo ta" },
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
      UIUtils.createSection("Danh sach vai tro", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Them role"),
      UIUtils.ghostButton("Sua role"),
      UIUtils.ghostButton("Xoa role"),
      UIUtils.ghostButton("Tai lai")
    );

    btnThem = (JButton) actions.getComponent(0);
    btnSua = (JButton) actions.getComponent(1);
    btnXoa = (JButton) actions.getComponent(2);
    btnTaiLai = (JButton) actions.getComponent(3);

    btnThem.addActionListener(e -> moDialogRole(null));
    btnSua.addActionListener(e -> suaRole());
    btnXoa.addActionListener(e -> xoaRole());
    btnTaiLai.addActionListener(e -> taiDuLieu());

    add(actions, BorderLayout.SOUTH);
    apDungPhanQuyenHanhDong();
    taiDuLieu();
  }

  /**
   * Nap danh sach role len bang.
   */
  private void taiDuLieu() {
    model.setRowCount(0);
    ArrayList<RolesDTO> dsRole = rolesBUS.getAllRoles();
    for (RolesDTO role : dsRole) {
      model.addRow(
        new Object[] { role.getSTT(), role.getTenVaiTro(), role.getMoTa() }
      );
    }
  }

  /**
   * Mo form them/sua role.
   */
  private void moDialogRole(RolesDTO source) {
    boolean laThemMoi = source == null;

    JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
    form.setOpaque(false);

    JTextField txtRoleId = new JTextField(
      laThemMoi ? rolesBUS.generateNextRoleId() : source.getSTT()
    );
    txtRoleId.setEditable(false);
    JTextField txtTenVaiTro = new JTextField(
      laThemMoi ? "" : source.getTenVaiTro()
    );
    JTextField txtMoTa = new JTextField(laThemMoi ? "" : source.getMoTa());

    form.add(new JLabel("Role ID"));
    form.add(txtRoleId);
    form.add(new JLabel("Ten vai tro"));
    form.add(txtTenVaiTro);
    form.add(new JLabel("Mo ta"));
    form.add(txtMoTa);

    AdminDialogs.showFormDialog(
      this,
      laThemMoi ? "Them role" : "Sua role",
      form,
      () -> {
        RolesDTO role = new RolesDTO();
        role.setSTT(txtRoleId.getText().trim());
        role.setTenVaiTro(txtTenVaiTro.getText().trim());
        role.setMoTa(txtMoTa.getText().trim());

        String ketQua = laThemMoi
          ? rolesBUS.insertRoles(role)
          : rolesBUS.updateRoles(role);
        String normalized = ketQua == null ? "" : ketQua.toLowerCase();
        if (
          !normalized.contains("thanh cong") &&
          !normalized.contains("thành công")
        ) {
          DialogHelper.warn(this, ketQua);
          return false;
        }

        DialogHelper.info(this, ketQua);
        taiDuLieu();
        return true;
      },
      560,
      280
    );
  }

  /**
   * Lay role dang chon tu bang.
   */
  private RolesDTO layRoleDangChon() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }

    int modelRow = table.convertRowIndexToModel(row);
    String roleId = String.valueOf(model.getValueAt(modelRow, 0));
    return rolesBUS.getById(roleId);
  }

  /**
   * Sua role duoc chon.
   */
  private void suaRole() {
    RolesDTO role = layRoleDangChon();
    if (role == null) {
      DialogHelper.warn(this, "Vui long chon role de sua.");
      return;
    }
    moDialogRole(role);
  }

  /**
   * Xoa role duoc chon.
   */
  private void xoaRole() {
    RolesDTO role = layRoleDangChon();
    if (role == null) {
      DialogHelper.warn(this, "Vui long chon role de xoa.");
      return;
    }

    if (!DialogHelper.confirm(this, "Xoa role " + role.getSTT() + "?")) {
      return;
    }

    String ketQua = rolesBUS.deleteRoles(role.getSTT());
    String normalized = ketQua == null ? "" : ketQua.toLowerCase();
    if (
      !normalized.contains("thanh cong") && !normalized.contains("thành công")
    ) {
      DialogHelper.error(this, ketQua);
      return;
    }

    DialogHelper.info(this, ketQua);
    taiDuLieu();
  }

  private void apDungPhanQuyenHanhDong() {
    boolean coQuyenXem = Session.coMotTrongCacQuyen("ROLE_XEM");
    boolean coQuyenThem = Session.coMotTrongCacQuyen("ROLE_THEM");
    boolean coQuyenSua = Session.coMotTrongCacQuyen("ROLE_SUA");
    boolean coQuyenXoa = Session.coMotTrongCacQuyen("ROLE_XOA");

    if (btnThem != null) btnThem.setVisible(coQuyenThem);
    if (btnSua != null) btnSua.setVisible(coQuyenSua);
    if (btnXoa != null) btnXoa.setVisible(coQuyenXoa);
    if (btnTaiLai != null) btnTaiLai.setVisible(coQuyenXem);
    if (table != null) table.setEnabled(coQuyenXem);
  }
}
