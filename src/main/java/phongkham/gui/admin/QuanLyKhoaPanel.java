package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.KhoaBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.KhoaDTO;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyKhoaPanel extends BasePanel {

  private final KhoaBUS khoaBUS = new KhoaBUS();
  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private JTable table;
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] { "Mã khoa", "Tên khoa" },
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
      UIUtils.createSection("Danh sách khoa", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Thêm khoa"),
      UIUtils.ghostButton("Sửa khoa"),
      UIUtils.ghostButton("Xóa khoa"),
      UIUtils.ghostButton("Xem bác sĩ trong khoa"),
      UIUtils.ghostButton("Chuyển khoa bác sĩ"),
      UIUtils.ghostButton("Tải lại")
    );
    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      openKhoaDialog(null)
    );
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      editKhoa()
    );
    ((javax.swing.JButton) actions.getComponent(2)).addActionListener(e ->
      deleteKhoa()
    );
    ((javax.swing.JButton) actions.getComponent(3)).addActionListener(e ->
      showDoctorsBySelectedKhoa()
    );
    ((javax.swing.JButton) actions.getComponent(4)).addActionListener(e ->
      openTransferDoctorDialog()
    );
    ((javax.swing.JButton) actions.getComponent(5)).addActionListener(e ->
      loadData()
    );
    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private void loadData() {
    model.setRowCount(0);
    ArrayList<KhoaDTO> dsKhoa = khoaBUS.getAll();
    for (KhoaDTO khoa : dsKhoa) {
      model.addRow(new Object[] { khoa.getMaKhoa(), khoa.getTenKhoa() });
    }
  }

  private void editKhoa() {
    KhoaDTO selected = getSelectedKhoa();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn khoa để sửa.");
      return;
    }
    openKhoaDialog(selected);
  }

  private void openKhoaDialog(KhoaDTO source) {
    boolean isCreate = source == null;

    JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
    form.setOpaque(false);

    JTextField txtMa = new JTextField(isCreate ? "" : source.getMaKhoa());
    txtMa.setEditable(isCreate);
    JTextField txtTen = new JTextField(isCreate ? "" : source.getTenKhoa());

    form.add(new JLabel("Mã khoa"));
    form.add(txtMa);
    form.add(new JLabel("Tên khoa"));
    form.add(txtTen);

    AdminDialogs.showFormDialog(
      this,
      isCreate ? "Tạo khoa khám" : "Cập nhật khoa khám",
      form,
      () -> {
        KhoaDTO khoa = new KhoaDTO();
        khoa.setMaKhoa(txtMa.getText().trim());
        khoa.setTenKhoa(txtTen.getText().trim());

        boolean ok = isCreate ? khoaBUS.add(khoa) : khoaBUS.update(khoa);
        if (!ok) {
          DialogHelper.error(
            this,
            isCreate ? "Thêm khoa thất bại." : "Cập nhật khoa thất bại."
          );
          return false;
        }

        DialogHelper.info(
          this,
          isCreate ? "Đã thêm khoa mới." : "Đã cập nhật khoa."
        );
        loadData();
        return true;
      },
      520,
      260
    );
  }

  private void deleteKhoa() {
    KhoaDTO selected = getSelectedKhoa();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn khoa để xóa.");
      return;
    }

    if (!DialogHelper.confirm(this, "Xóa khoa " + selected.getMaKhoa() + "?")) {
      return;
    }

    if (!khoaBUS.delete(selected.getMaKhoa())) {
      DialogHelper.error(this, "Xóa khoa thất bại.");
      return;
    }

    DialogHelper.info(this, "Đã xóa khoa thành công.");
    loadData();
  }

  private void showDoctorsBySelectedKhoa() {
    KhoaDTO selected = getSelectedKhoa();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn khoa để xem danh sách bác sĩ.");
      return;
    }

    ArrayList<BacSiDTO> doctors = bacSiBUS.getByKhoa(selected.getMaKhoa());
    DefaultTableModel doctorModel = new DefaultTableModel(
      new Object[] { "Mã BS", "Họ tên", "Chuyên khoa", "SĐT", "Email" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    for (BacSiDTO doctor : doctors) {
      doctorModel.addRow(
        new Object[] {
          doctor.getMaBacSi(),
          doctor.getHoTen(),
          doctor.getChuyenKhoa(),
          doctor.getSoDienThoai(),
          doctor.getEmail(),
        }
      );
    }

    JTable doctorTable = new JTable(doctorModel);
    UIUtils.styleTable(doctorTable);

    JPanel body = new JPanel(new BorderLayout());
    body.setOpaque(false);
    body.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

    AdminDialogs.showViewDialog(
      this,
      "Bác sĩ trong khoa " + selected.getTenKhoa(),
      body,
      860,
      420
    );
  }

  private void openTransferDoctorDialog() {
    ArrayList<BacSiDTO> doctors = bacSiBUS.getAll();
    ArrayList<KhoaDTO> khoaList = khoaBUS.getAll();

    if (doctors.isEmpty() || khoaList.isEmpty()) {
      DialogHelper.warn(this, "Thiếu dữ liệu bác sĩ hoặc khoa để chuyển.");
      return;
    }

    JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
    form.setOpaque(false);

    JComboBox<String> cbDoctor = new JComboBox<>();
    for (BacSiDTO doctor : doctors) {
      cbDoctor.addItem(
        doctor.getMaBacSi() +
          " - " +
          doctor.getHoTen() +
          " (" +
          doctor.getMaKhoa() +
          ")"
      );
    }

    JComboBox<String> cbTargetKhoa = new JComboBox<>();
    for (KhoaDTO khoa : khoaList) {
      cbTargetKhoa.addItem(khoa.getMaKhoa() + " - " + khoa.getTenKhoa());
    }

    form.add(new JLabel("Chọn bác sĩ"));
    form.add(cbDoctor);
    form.add(new JLabel("Chuyển sang khoa"));
    form.add(cbTargetKhoa);

    AdminDialogs.showFormDialog(
      this,
      "Chuyển khoa bác sĩ",
      form,
      () -> {
        BacSiDTO doctor = findDoctorBySelection(
          doctors,
          String.valueOf(cbDoctor.getSelectedItem())
        );
        String maKhoaTarget = extractPrefix(
          String.valueOf(cbTargetKhoa.getSelectedItem())
        );

        if (doctor == null || maKhoaTarget.isEmpty()) {
          DialogHelper.warn(this, "Dữ liệu chọn không hợp lệ.");
          return false;
        }

        if (maKhoaTarget.equalsIgnoreCase(doctor.getMaKhoa())) {
          DialogHelper.warn(this, "Bác sĩ đang thuộc khoa này.");
          return false;
        }

        doctor.setMaKhoa(maKhoaTarget);
        if (!bacSiBUS.update(doctor)) {
          DialogHelper.error(this, "Chuyển khoa thất bại.");
          return false;
        }

        DialogHelper.info(this, "Đã chuyển khoa cho bác sĩ thành công.");
        loadData();
        return true;
      },
      620,
      280
    );
  }

  private KhoaDTO getSelectedKhoa() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    String ma = String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 0)
    );
    return khoaBUS.getById(ma);
  }

  private String extractPrefix(String text) {
    if (text == null || text.trim().isEmpty()) {
      return "";
    }
    return text.split(" - ")[0].trim();
  }

  private BacSiDTO findDoctorBySelection(
    ArrayList<BacSiDTO> list,
    String selection
  ) {
    String ma = extractPrefix(selection);
    for (BacSiDTO doctor : list) {
      if (doctor.getMaBacSi().equalsIgnoreCase(ma)) {
        return doctor;
      }
    }
    return null;
  }
}
