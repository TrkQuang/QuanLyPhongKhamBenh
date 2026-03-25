package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.KhoaBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.KhoaDTO;
import phongkham.Utils.Session;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyBacSiPanel extends BasePanel {

  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final KhoaBUS khoaBUS = new KhoaBUS();
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

    int rows = isCreate ? 8 : 6;
    JPanel form = new JPanel(new GridLayout(rows, 2, 10, 10));
    form.setOpaque(false);

    JTextField txtMa = new JTextField(isCreate ? "" : source.getMaBacSi());
    txtMa.setEditable(isCreate);
    JTextField txtHoTen = new JTextField(isCreate ? "" : source.getHoTen());
    JTextField txtChuyenKhoa = new JTextField(
      isCreate ? "" : source.getChuyenKhoa()
    );
    JTextField txtSdt = new JTextField(isCreate ? "" : source.getSoDienThoai());
    JTextField txtEmail = new JTextField(isCreate ? "" : source.getEmail());
    JTextField txtMaKhoa = new JTextField(isCreate ? "" : source.getMaKhoa());
    JTextField txtUsername = new JTextField();
    JPasswordField txtPassword = new JPasswordField();

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
    form.add(txtMaKhoa);
    if (isCreate) {
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
        bs.setMaKhoa(txtMaKhoa.getText().trim());

        if (isCreate) {
          String result = usersBUS.createDoctorAccountWithProfile(
            bs.getMaBacSi(),
            txtUsername.getText().trim(),
            new String(txtPassword.getPassword()),
            bs.getEmail(),
            bs.getHoTen(),
            bs.getSoDienThoai(),
            bs.getChuyenKhoa(),
            bs.getMaKhoa()
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
