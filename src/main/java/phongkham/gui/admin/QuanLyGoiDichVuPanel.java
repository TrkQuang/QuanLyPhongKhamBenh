package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.KhoaBUS;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.KhoaDTO;
import phongkham.gui.admin.components.AdminDialogs;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyGoiDichVuPanel extends BasePanel {

  private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
  private final KhoaBUS khoaBUS = new KhoaBUS();
  private JTable table;
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] { "Mã gói", "Tên gói", "Giá", "Thời gian khám", "Mã khoa" },
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
      UIUtils.createSection("Danh sách gói dịch vụ", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Tạo gói"),
      UIUtils.ghostButton("Sửa gói"),
      UIUtils.ghostButton("Xóa gói"),
      UIUtils.ghostButton("Tải lại")
    );
    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      openPackageDialog(null)
    );
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      editPackage()
    );
    ((javax.swing.JButton) actions.getComponent(2)).addActionListener(e ->
      deletePackage()
    );
    ((javax.swing.JButton) actions.getComponent(3)).addActionListener(e ->
      loadData()
    );
    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private void loadData() {
    model.setRowCount(0);
    ArrayList<GoiDichVuDTO> dsGoi = goiDichVuBUS.getAll();
    for (GoiDichVuDTO goi : dsGoi) {
      model.addRow(
        new Object[] {
          goi.getMaGoi(),
          goi.getTenGoi(),
          goi.getGiaDichVu(),
          goi.getThoiGianKham(),
          goi.getMaKhoa(),
        }
      );
    }
  }

  private void editPackage() {
    GoiDichVuDTO current = getSelectedPackage();
    if (current == null) {
      DialogHelper.warn(this, "Vui lòng chọn gói dịch vụ để sửa.");
      return;
    }
    openPackageDialog(current);
  }

  private void deletePackage() {
    GoiDichVuDTO current = getSelectedPackage();
    if (current == null) {
      DialogHelper.warn(this, "Vui lòng chọn gói dịch vụ để xóa.");
      return;
    }
    if (
      !DialogHelper.confirm(this, "Xóa gói dịch vụ " + current.getMaGoi() + "?")
    ) {
      return;
    }
    if (!goiDichVuBUS.delete(current.getMaGoi())) {
      DialogHelper.error(this, "Xóa gói dịch vụ thất bại.");
      return;
    }
    DialogHelper.info(this, "Đã xóa gói dịch vụ.");
    loadData();
  }

  private void openPackageDialog(GoiDichVuDTO source) {
    boolean isCreate = source == null;

    javax.swing.JPanel form = new javax.swing.JPanel(
      new GridLayout(6, 2, 10, 10)
    );
    form.setOpaque(false);

    JTextField txtMa = new JTextField(
      isCreate ? goiDichVuBUS.generateNextMaGoi() : source.getMaGoi()
    );
    txtMa.setEditable(false);
    JTextField txtTen = new JTextField(isCreate ? "" : source.getTenGoi());
    JTextField txtGia = new JTextField(
      isCreate || source.getGiaDichVu() == null
        ? "0"
        : source.getGiaDichVu().toString()
    );
    JTextField txtThoiGian = new JTextField(
      isCreate ? "" : source.getThoiGianKham()
    );
    JTextField txtMoTa = new JTextField(isCreate ? "" : source.getMoTa());
    JComboBox<String> cbMaKhoa = new JComboBox<>();
    for (KhoaDTO khoa : khoaBUS.getAll()) {
      cbMaKhoa.addItem(khoa.getMaKhoa() + " - " + khoa.getTenKhoa());
    }
    if (!isCreate && source.getMaKhoa() != null) {
      selectKhoaInCombo(cbMaKhoa, source.getMaKhoa());
    }

    form.add(new JLabel("Mã gói"));
    form.add(txtMa);
    form.add(new JLabel("Tên gói"));
    form.add(txtTen);
    form.add(new JLabel("Giá dịch vụ"));
    form.add(txtGia);
    form.add(new JLabel("Thời gian khám"));
    form.add(txtThoiGian);
    form.add(new JLabel("Mô tả"));
    form.add(txtMoTa);
    form.add(new JLabel("Mã khoa"));
    form.add(cbMaKhoa);

    AdminDialogs.showFormDialog(
      this,
      isCreate ? "Tạo gói dịch vụ" : "Cập nhật gói dịch vụ",
      form,
      () -> {
        GoiDichVuDTO goi = new GoiDichVuDTO();
        try {
          goi.setMaGoi(txtMa.getText().trim());
          goi.setTenGoi(txtTen.getText().trim());
          goi.setGiaDichVu(new BigDecimal(txtGia.getText().trim()));
          goi.setThoiGianKham(txtThoiGian.getText().trim());
          goi.setMoTa(txtMoTa.getText().trim());
          goi.setMaKhoa(extractMaKhoa(String.valueOf(cbMaKhoa.getSelectedItem())));
        } catch (Exception ex) {
          DialogHelper.warn(this, "Giá dịch vụ không hợp lệ.");
          return false;
        }

        if (goi.getTenGoi() == null || goi.getTenGoi().trim().isEmpty()) {
          DialogHelper.warn(this, "Tên gói không được để trống.");
          return false;
        }
        if (goi.getThoiGianKham() == null || goi.getThoiGianKham().trim().isEmpty()) {
          DialogHelper.warn(this, "Thời gian khám không được để trống.");
          return false;
        }
        if (goi.getMaKhoa() == null || goi.getMaKhoa().isEmpty()) {
          DialogHelper.warn(this, "Vui lòng chọn khoa cho gói dịch vụ.");
          return false;
        }

        boolean ok = isCreate
          ? goiDichVuBUS.insert(goi)
          : goiDichVuBUS.update(goi);
        if (!ok) {
          DialogHelper.error(
            this,
            isCreate
              ? "Thêm gói dịch vụ thất bại."
              : "Cập nhật gói dịch vụ thất bại."
          );
          return false;
        }

        DialogHelper.info(
          this,
          isCreate ? "Đã tạo gói dịch vụ mới." : "Đã cập nhật gói dịch vụ."
        );
        loadData();
        return true;
      },
      620,
      380
    );
  }

  private GoiDichVuDTO getSelectedPackage() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    String maGoi = String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 0)
    );
    return goiDichVuBUS.getByMaGoi(maGoi);
  }

  private String extractMaKhoa(String comboText) {
    if (comboText == null) {
      return "";
    }
    String[] parts = comboText.split(" - ", 2);
    return parts.length == 0 ? "" : parts[0].trim();
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
}
