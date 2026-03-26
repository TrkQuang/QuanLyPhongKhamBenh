package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.KhoaBUS;
import phongkham.BUS.RolesBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.KhoaDTO;
import phongkham.DTO.RolesDTO;
import phongkham.Utils.Session;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyBacSiPanel extends BasePanel {

  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final KhoaBUS khoaBUS = new KhoaBUS();
  private final RolesBUS rolesBUS = new RolesBUS();
  private final UsersBUS usersBUS = new UsersBUS();
  private JTable table;
  private JButton btnThem;
  private JButton btnSua;
  private JButton btnXemChiTiet;
  private JButton btnXoa;
  private JButton btnTaiLai;
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã bác sĩ",
      "Họ tên",
      "Chuyên khoa",
      "SĐT",
      "Email",
      "Mã khoa",
    },
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
      UIUtils.createSection("Danh sách bác sĩ", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Thêm bác sĩ"),
      UIUtils.ghostButton("Sửa thông tin"),
      UIUtils.ghostButton("Xem chi tiết"),
      UIUtils.ghostButton("Xóa"),
      UIUtils.ghostButton("Tải lại")
    );
    btnThem = (JButton) actions.getComponent(0);
    btnSua = (JButton) actions.getComponent(1);
    btnXemChiTiet = (JButton) actions.getComponent(2);
    btnXoa = (JButton) actions.getComponent(3);
    btnTaiLai = (JButton) actions.getComponent(4);

    btnThem.addActionListener(e -> openDoctorDialog(null));
    btnSua.addActionListener(e -> editDoctor());
    btnXemChiTiet.addActionListener(e -> showDoctorDetail());
    btnXoa.addActionListener(e -> deleteDoctor());
    btnTaiLai.addActionListener(e -> loadData());

    apDungPhanQuyenHanhDong();
    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private void loadData() {
    model.setRowCount(0);
    ArrayList<BacSiDTO> dsBacSi = bacSiBUS.getAll();
    for (BacSiDTO bacSi : dsBacSi) {
      model.addRow(
        new Object[] {
          bacSi.getMaBacSi(),
          bacSi.getHoTen(),
          bacSi.getChuyenKhoa(),
          bacSi.getSoDienThoai(),
          bacSi.getEmail(),
          bacSi.getMaKhoa(),
        }
      );
    }
  }

  private void editDoctor() {
    BacSiDTO selected = getSelectedDoctor();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn bác sĩ để sửa.");
      return;
    }
    openDoctorDialog(selected);
  }

  private void openDoctorDialog(BacSiDTO source) {
    boolean isCreate = source == null;

    int rows = isCreate ? 9 : 6;
    JPanel form = new JPanel(new GridLayout(rows, 2, 10, 10));
    form.setOpaque(false);

    JTextField txtMa = new JTextField(
      isCreate ? usersBUS.generateNextDoctorId() : source.getMaBacSi()
    );
    txtMa.setEditable(false);
    JTextField txtHoTen = new JTextField(isCreate ? "" : source.getHoTen());
    JTextField txtChuyenKhoa = new JTextField(
      isCreate ? "" : source.getChuyenKhoa()
    );
    JTextField txtSdt = new JTextField(isCreate ? "" : source.getSoDienThoai());
    JTextField txtEmail = new JTextField(isCreate ? "" : source.getEmail());
    JComboBox<String> cbMaKhoa = new JComboBox<>();
    for (KhoaDTO khoa : khoaBUS.getAll()) {
      cbMaKhoa.addItem(khoa.getMaKhoa() + " - " + khoa.getTenKhoa());
    }
    if (!isCreate && source.getMaKhoa() != null) {
      selectKhoaInCombo(cbMaKhoa, source.getMaKhoa());
    }
    JTextField txtUsername = new JTextField();
    JPasswordField txtPassword = new JPasswordField();
    JComboBox<String> cbRole = new JComboBox<>();
    if (isCreate) {
      loadRolesToCombo(cbRole);
      selectDoctorRoleInCombo(cbRole);
    }

    form.add(new JLabel("Mã bác sĩ"));
    form.add(txtMa);
    form.add(new JLabel("Họ tên"));
    form.add(txtHoTen);
    form.add(new JLabel("Chuyên khoa"));
    form.add(txtChuyenKhoa);
    form.add(new JLabel("SĐT"));
    form.add(txtSdt);
    form.add(new JLabel("Email"));
    form.add(txtEmail);
    form.add(new JLabel("Mã khoa"));
    form.add(cbMaKhoa);
    if (isCreate) {
      form.add(new JLabel("Role"));
      form.add(cbRole);
      form.add(new JLabel("Tên tài khoản"));
      form.add(txtUsername);
      form.add(new JLabel("Mật khẩu"));
      form.add(txtPassword);
    }

    AdminDialogs.showFormDialog(
      this,
      isCreate ? "Thêm bác sĩ" : "Cập nhật bác sĩ",
      form,
      () -> {
        BacSiDTO bs = new BacSiDTO();
        bs.setMaBacSi(txtMa.getText().trim());
        bs.setHoTen(txtHoTen.getText().trim());
        bs.setChuyenKhoa(txtChuyenKhoa.getText().trim());
        bs.setSoDienThoai(txtSdt.getText().trim());
        bs.setEmail(txtEmail.getText().trim());
        bs.setMaKhoa(extractMaKhoa(String.valueOf(cbMaKhoa.getSelectedItem())));

        if (isCreate) {
          String result = usersBUS.createDoctorAccountWithProfile(
            bs.getMaBacSi(),
            txtUsername.getText().trim(),
            new String(txtPassword.getPassword()),
            bs.getEmail(),
            bs.getHoTen(),
            bs.getSoDienThoai(),
            bs.getChuyenKhoa(),
            bs.getMaKhoa(),
            parseRoleId(String.valueOf(cbRole.getSelectedItem()))
          );
          if (!result.startsWith("Tạo tài khoản bác sĩ thành công")) {
            DialogHelper.error(this, result);
            return false;
          }
          DialogHelper.info(this, result);
        } else {
          boolean ok = bacSiBUS.update(bs);
          if (!ok) {
            DialogHelper.error(this, "Cập nhật bác sĩ thất bại.");
            return false;
          }
          DialogHelper.info(this, "Đã cập nhật bác sĩ.");
        }

        loadData();
        return true;
      },
      620,
      380
    );
  }

  private String extractMaKhoa(String comboText) {
    if (comboText == null) {
      return "";
    }
    int idx = comboText.indexOf(" - ");
    return (idx > 0 ? comboText.substring(0, idx) : comboText).trim();
  }

  private void selectKhoaInCombo(JComboBox<String> combo, String maKhoa) {
    for (int i = 0; i < combo.getItemCount(); i++) {
      String item = combo.getItemAt(i);
      if (maKhoa.equalsIgnoreCase(extractMaKhoa(item))) {
        combo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void loadRolesToCombo(JComboBox<String> cbRole) {
    cbRole.removeAllItems();
    for (RolesDTO role : rolesBUS.getAllRoles()) {
      cbRole.addItem(role.getSTT() + " - " + role.getTenVaiTro());
    }
  }

  private int parseRoleId(String roleText) {
    try {
      return Integer.parseInt(roleText.split(" - ")[0].trim());
    } catch (Exception ex) {
      return 0;
    }
  }

  private void selectDoctorRoleInCombo(JComboBox<String> combo) {
    for (int i = 0; i < combo.getItemCount(); i++) {
      String item = String.valueOf(combo.getItemAt(i)).toLowerCase();
      if (item.contains("bác sĩ") || item.contains("bac si")) {
        combo.setSelectedIndex(i);
        return;
      }
    }
    if (combo.getItemCount() > 0) {
      combo.setSelectedIndex(0);
    }
  }

  private void showDoctorDetail() {
    BacSiDTO doctor = getSelectedDoctor();
    if (doctor == null) {
      DialogHelper.warn(this, "Vui lòng chọn bác sĩ để xem chi tiết.");
      return;
    }

    KhoaDTO khoa = khoaBUS.getById(doctor.getMaKhoa());
    String tenKhoa = khoa == null ? "Không xác định" : khoa.getTenKhoa();

    JPanel detail = new JPanel(new GridLayout(7, 2, 10, 8));
    detail.setOpaque(false);

    detail.add(new JLabel("Mã bác sĩ"));
    detail.add(new JLabel(doctor.getMaBacSi()));
    detail.add(new JLabel("Họ tên"));
    detail.add(new JLabel(doctor.getHoTen()));
    detail.add(new JLabel("Chuyên khoa"));
    detail.add(new JLabel(doctor.getChuyenKhoa()));
    detail.add(new JLabel("SĐT"));
    detail.add(new JLabel(doctor.getSoDienThoai()));
    detail.add(new JLabel("Email"));
    detail.add(new JLabel(doctor.getEmail()));
    detail.add(new JLabel("Mã khoa"));
    detail.add(new JLabel(doctor.getMaKhoa()));
    detail.add(new JLabel("Tên khoa"));
    detail.add(new JLabel(tenKhoa));

    AdminDialogs.showViewDialog(this, "Chi tiết bác sĩ", detail, 560, 360);
  }

  private void deleteDoctor() {
    BacSiDTO doctor = getSelectedDoctor();
    if (doctor == null) {
      DialogHelper.warn(this, "Vui lòng chọn bác sĩ để xóa.");
      return;
    }
    if (
      !DialogHelper.confirm(this, "Xóa bác sĩ " + doctor.getMaBacSi() + "?")
    ) {
      return;
    }

    String ketQua = bacSiBUS.xoaBacSiTheoNghiepVu(doctor.getMaBacSi());
    if (ketQua.startsWith("Đã xóa")) {
      DialogHelper.info(this, ketQua);
    } else if (ketQua.startsWith("Không thể xóa")) {
      DialogHelper.warn(this, ketQua);
    } else {
      DialogHelper.error(this, ketQua);
      return;
    }
    loadData();
  }

  private BacSiDTO getSelectedDoctor() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    String maBs = String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 0)
    );
    return bacSiBUS.getById(maBs);
  }

  /**
   * Ap dung phan quyen chi tiet theo hanh dong cua module BACSI.
   */
  private void apDungPhanQuyenHanhDong() {
    boolean coQuyenXem = Session.coMotTrongCacQuyen("BACSI_XEM");
    boolean coQuyenThem = Session.coMotTrongCacQuyen("BACSI_THEM");
    boolean coQuyenSua = Session.coMotTrongCacQuyen("BACSI_SUA");
    boolean coQuyenXoa = Session.coMotTrongCacQuyen("BACSI_XOA");
    boolean coQuyenXemChiTiet = Session.coMotTrongCacQuyen(
      "BACSI_XEM_CHI_TIET",
      "BACSI_XEM"
    );

    if (btnThem != null) {
      btnThem.setVisible(coQuyenThem);
    }
    if (btnSua != null) {
      btnSua.setVisible(coQuyenSua);
    }
    if (btnXemChiTiet != null) {
      btnXemChiTiet.setVisible(coQuyenXemChiTiet);
    }
    if (btnXoa != null) {
      btnXoa.setVisible(coQuyenXoa);
    }
    if (btnTaiLai != null) {
      btnTaiLai.setVisible(coQuyenXem);
    }

    if (table != null) {
      table.setEnabled(coQuyenXem);
    }
  }
}
