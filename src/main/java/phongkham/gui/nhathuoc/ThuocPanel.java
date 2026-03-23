package phongkham.gui.nhathuoc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.ThuocDTO;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;

public class ThuocPanel extends BasePanel {

  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final ArrayList<ThuocDTO> allMedicines = new ArrayList<>();
  private final ArrayList<ThuocDTO> filteredMedicines = new ArrayList<>();
  private final Map<String, Boolean> activeByMedicineId = new HashMap<>();

  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã thuốc",
      "Tên thuốc",
      "Hoạt chất",
      "Đơn vị tính",
      "Đơn giá bán",
      "Số lượng tồn",
    },
    0
  ) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private JTable table;
  private CustomTextField txtSearch;
  private JComboBox<String> cbSort;

  @Override
  protected void init() {
    add(buildToolbar(), BorderLayout.NORTH);

    table = new JTable(model);
    UIUtils.styleTable(table);
    applyInactiveRowStyle();
    add(
      UIUtils.createSection("Quản lý thuốc", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    add(buildActions(), BorderLayout.SOUTH);
    reloadData();
  }

  private JPanel buildToolbar() {
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    toolbar.setOpaque(false);

    txtSearch = (CustomTextField) UIUtils.roundedTextField(
      "Tìm theo mã / tên / hoạt chất",
      22
    );
    cbSort = new JComboBox<>(
      new String[] { "Mặc định", "Giá tăng dần", "Giá giảm dần", "Tên A-Z" }
    );

    javax.swing.JButton btnSearch = UIUtils.primaryButton("Tìm kiếm");
    javax.swing.JButton btnRefresh = UIUtils.ghostButton("Làm mới");

    btnSearch.addActionListener(e -> applyFilters());
    btnRefresh.addActionListener(e -> resetFilters());
    cbSort.addActionListener(e -> applyFilters());
    txtSearch.addActionListener(e -> applyFilters());

    toolbar.add(txtSearch);
    toolbar.add(cbSort);
    toolbar.add(btnSearch);
    toolbar.add(btnRefresh);
    return toolbar;
  }

  private JPanel buildActions() {
    JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Thêm"),
      UIUtils.ghostButton("Sửa"),
      UIUtils.ghostButton("Vô hiệu hóa bán"),
      UIUtils.ghostButton("Kích hoạt bán lại"),
      UIUtils.ghostButton("Tải lại")
    );

    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      addMedicine()
    );
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      editMedicine()
    );
    ((javax.swing.JButton) actions.getComponent(2)).addActionListener(e ->
      disableMedicine()
    );
    ((javax.swing.JButton) actions.getComponent(3)).addActionListener(e ->
      reactivateMedicine()
    );
    ((javax.swing.JButton) actions.getComponent(4)).addActionListener(e ->
      reloadData()
    );
    return actions;
  }

  private void reloadData() {
    allMedicines.clear();
    allMedicines.addAll(thuocBUS.listForManagement());
    applyFilters();
  }

  private void resetFilters() {
    txtSearch.setText("");
    cbSort.setSelectedItem("Mặc định");
    applyFilters();
  }

  private void applyFilters() {
    filteredMedicines.clear();

    String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
    for (ThuocDTO medicine : allMedicines) {
      String ma = safe(medicine.getMaThuoc()).toLowerCase(Locale.ROOT);
      String ten = safe(medicine.getTenThuoc()).toLowerCase(Locale.ROOT);
      String hoatChat = safe(medicine.getHoatChat()).toLowerCase(Locale.ROOT);
      boolean match =
        keyword.isEmpty() ||
        ma.contains(keyword) ||
        ten.contains(keyword) ||
        hoatChat.contains(keyword);
      if (match) {
        filteredMedicines.add(medicine);
      }
    }

    String sort = String.valueOf(cbSort.getSelectedItem());
    if ("Giá tăng dần".equals(sort)) {
      filteredMedicines.sort(
        Comparator.comparingDouble(ThuocDTO::getDonGiaBan)
      );
    } else if ("Giá giảm dần".equals(sort)) {
      filteredMedicines.sort(
        Comparator.comparingDouble(ThuocDTO::getDonGiaBan).reversed()
      );
    } else if ("Tên A-Z".equals(sort)) {
      filteredMedicines.sort(
        Comparator.comparing(m ->
          safe(m.getTenThuoc()).toLowerCase(Locale.ROOT)
        )
      );
    }

    renderTable();
  }

  private void renderTable() {
    activeByMedicineId.clear();
    model.setRowCount(0);
    for (ThuocDTO medicine : filteredMedicines) {
      activeByMedicineId.put(medicine.getMaThuoc(), medicine.isActive());
      model.addRow(
        new Object[] {
          medicine.getMaThuoc(),
          medicine.getTenThuoc(),
          medicine.getHoatChat(),
          medicine.getDonViTinh(),
          medicine.getDonGiaBan(),
          medicine.getSoLuongTon(),
        }
      );
    }
    if (table != null) {
      table.repaint();
    }
  }

  private void addMedicine() {
    ThuocDTO input = showMedicineDialog(null, false);
    if (input == null) {
      return;
    }
    if (!thuocBUS.addThuoc(input)) {
      DialogHelper.error(
        this,
        "Thêm thuốc thất bại. Vui lòng kiểm tra dữ liệu."
      );
      return;
    }
    DialogHelper.info(this, "Thêm thuốc thành công.");
    reloadData();
  }

  private void editMedicine() {
    ThuocDTO selected = getSelectedMedicine();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn thuốc cần sửa.");
      return;
    }

    ThuocDTO updated = showMedicineDialog(selected, true);
    if (updated == null) {
      return;
    }

    if (!thuocBUS.UpdateThuoc(updated)) {
      DialogHelper.error(this, "Cập nhật thuốc thất bại.");
      return;
    }
    DialogHelper.info(this, "Cập nhật thuốc thành công.");
    reloadData();
  }

  private void disableMedicine() {
    ThuocDTO selected = getSelectedMedicine();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn thuốc cần vô hiệu hóa bán.");
      return;
    }

    if (!selected.isActive()) {
      DialogHelper.info(this, "Thuốc này đã ở trạng thái vô hiệu hóa bán.");
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Vô hiệu hóa bán thuốc " + selected.getTenThuoc() + "?"
      )
    ) {
      return;
    }

    if (!thuocBUS.deleteByMa(selected.getMaThuoc())) {
      DialogHelper.error(this, "Vô hiệu hóa bán thất bại.");
      return;
    }
    DialogHelper.info(this, "Đã vô hiệu hóa bán thuốc (Active = 0).");
    reloadData();
  }

  private void reactivateMedicine() {
    ThuocDTO selected = getSelectedMedicine();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn thuốc cần kích hoạt bán lại.");
      return;
    }

    if (selected.isActive()) {
      DialogHelper.info(this, "Thuốc này đang ở trạng thái bán.");
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Kích hoạt bán lại thuốc " + selected.getTenThuoc() + "?"
      )
    ) {
      return;
    }

    if (!thuocBUS.reactivateByMa(selected.getMaThuoc())) {
      DialogHelper.error(this, "Kích hoạt bán lại thất bại.");
      return;
    }
    DialogHelper.info(this, "Đã kích hoạt bán lại thuốc (Active = 1).");
    reloadData();
  }

  private ThuocDTO getSelectedMedicine() {
    if (table == null) {
      return null;
    }
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    int modelRow = table.convertRowIndexToModel(row);
    String maThuoc = String.valueOf(model.getValueAt(modelRow, 0));
    for (ThuocDTO medicine : filteredMedicines) {
      if (maThuoc.equals(medicine.getMaThuoc())) {
        return medicine;
      }
    }
    return null;
  }

  private ThuocDTO showMedicineDialog(ThuocDTO source, boolean isEdit) {
    JDialog dialog = new JDialog(
      javax.swing.SwingUtilities.getWindowAncestor(this),
      isEdit ? "Sửa thông tin thuốc" : "Thêm thuốc",
      java.awt.Dialog.ModalityType.APPLICATION_MODAL
    );
    dialog.setSize(520, 420);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(0, 10));

    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    JTextField txtMa = UIUtils.roundedTextField("Để trống để tự sinh", 18);
    JTextField txtTen = UIUtils.roundedTextField("Tên thuốc", 18);
    JTextField txtHoatChat = UIUtils.roundedTextField("Hoạt chất", 18);
    JTextField txtDonVi = UIUtils.roundedTextField("Đơn vị tính", 18);
    JTextField txtDonGia = UIUtils.roundedTextField("Đơn giá bán", 18);
    JTextField txtSoLuong = UIUtils.roundedTextField("Số lượng tồn", 18);

    if (source != null) {
      txtMa.setText(safe(source.getMaThuoc()));
      txtTen.setText(safe(source.getTenThuoc()));
      txtHoatChat.setText(safe(source.getHoatChat()));
      txtDonVi.setText(safe(source.getDonViTinh()));
      txtDonGia.setText(String.valueOf(source.getDonGiaBan()));
      txtSoLuong.setText(String.valueOf(source.getSoLuongTon()));
    }
    if (isEdit) {
      txtMa.setEditable(false);
    }

    int row = 0;
    addFormRow(form, gbc, row++, "Mã thuốc", txtMa);
    addFormRow(form, gbc, row++, "Tên thuốc", txtTen);
    addFormRow(form, gbc, row++, "Hoạt chất", txtHoatChat);
    addFormRow(form, gbc, row++, "Đơn vị tính", txtDonVi);
    addFormRow(form, gbc, row++, "Đơn giá bán", txtDonGia);
    addFormRow(form, gbc, row++, "Số lượng tồn", txtSoLuong);

    JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Lưu"),
      UIUtils.ghostButton("Hủy")
    );
    final ThuocDTO[] result = new ThuocDTO[] { null };

    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e -> {
      try {
        ThuocDTO medicine = new ThuocDTO();
        medicine.setMaThuoc(txtMa.getText().trim());
        medicine.setTenThuoc(txtTen.getText().trim());
        medicine.setHoatChat(txtHoatChat.getText().trim());
        medicine.setDonViTinh(txtDonVi.getText().trim());
        medicine.setDonGiaBan(Float.parseFloat(txtDonGia.getText().trim()));
        medicine.setSoLuongTon(Integer.parseInt(txtSoLuong.getText().trim()));

        String error = validateMedicine(medicine, isEdit);
        if (error != null) {
          DialogHelper.warn(dialog, error);
          return;
        }

        result[0] = medicine;
        dialog.dispose();
      } catch (Exception ex) {
        DialogHelper.warn(dialog, "Đơn giá và số lượng phải là số hợp lệ.");
      }
    });
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      dialog.dispose()
    );

    dialog.add(form, BorderLayout.CENTER);
    dialog.add(actions, BorderLayout.SOUTH);
    dialog.setVisible(true);

    return result[0];
  }

  private String validateMedicine(ThuocDTO medicine, boolean isEdit) {
    if (isEdit && safe(medicine.getMaThuoc()).isEmpty()) {
      return "Mã thuốc không được để trống khi sửa.";
    }
    if (safe(medicine.getTenThuoc()).isEmpty()) {
      return "Tên thuốc không được để trống.";
    }
    if (safe(medicine.getHoatChat()).isEmpty()) {
      return "Hoạt chất không được để trống.";
    }
    if (safe(medicine.getDonViTinh()).isEmpty()) {
      return "Đơn vị tính không được để trống.";
    }
    if (medicine.getDonGiaBan() <= 0) {
      return "Đơn giá bán phải lớn hơn 0.";
    }
    if (medicine.getSoLuongTon() <= 0) {
      return "Số lượng tồn phải lớn hơn 0.";
    }
    return null;
  }

  private void addFormRow(
    JPanel form,
    GridBagConstraints gbc,
    int row,
    String label,
    java.awt.Component field
  ) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    form.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    form.add(field, gbc);
  }

  private String safe(String text) {
    return text == null ? "" : text;
  }

  private void applyInactiveRowStyle() {
    DefaultTableCellRenderer inactiveAwareRenderer =
      new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
          JTable tbl,
          Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column
        ) {
          Component component = super.getTableCellRendererComponent(
            tbl,
            value,
            isSelected,
            hasFocus,
            row,
            column
          );

          int modelRow = tbl.convertRowIndexToModel(row);
          String medicineId = String.valueOf(model.getValueAt(modelRow, 0));
          boolean isActive = Boolean.TRUE.equals(
            activeByMedicineId.get(medicineId)
          );

          if (!isActive) {
            component.setBackground(
              isSelected ? new Color(226, 232, 240) : new Color(241, 245, 249)
            );
            component.setForeground(new Color(148, 163, 184));
          } else {
            component.setBackground(
              isSelected ? new Color(219, 234, 254) : Color.WHITE
            );
            component.setForeground(
              isSelected ? new Color(30, 64, 175) : new Color(15, 23, 42)
            );
          }
          return component;
        }
      };

    for (int i = 0; i < table.getColumnCount(); i++) {
      table
        .getColumnModel()
        .getColumn(i)
        .setCellRenderer(inactiveAwareRenderer);
    }
  }
}
