package phongkham.gui.nhathuoc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.NhaCungCapBUS;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;

public class NhaCungCapPanel extends BasePanel {

  private final NhaCungCapBUS nhaCungCapBUS = new NhaCungCapBUS();
  private final ArrayList<NhaCungCapDTO> allSuppliers = new ArrayList<>();
  private final ArrayList<NhaCungCapDTO> filteredSuppliers = new ArrayList<>();

  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã NCC",
      "Tên nhà cung cấp",
      "Địa chỉ",
      "SĐT",
      "Trạng thái",
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

  @Override
  protected void init() {
    add(buildToolbar(), BorderLayout.NORTH);

    table = new JTable(model);
    UIUtils.styleTable(table);
    add(
      UIUtils.createSection("Danh sách nhà cung cấp", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    add(buildActions(), BorderLayout.SOUTH);
    reloadData();
  }

  private JPanel buildToolbar() {
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    toolbar.setOpaque(false);

    txtSearch = (CustomTextField) UIUtils.roundedTextField(
      "Tìm theo mã hoặc tên NCC",
      24
    );

    javax.swing.JButton btnSearch = UIUtils.primaryButton("Tìm kiếm");
    javax.swing.JButton btnReset = UIUtils.ghostButton("Làm mới");
    btnSearch.addActionListener(e -> applyFilters());
    btnReset.addActionListener(e -> {
      txtSearch.setText("");
      applyFilters();
    });
    txtSearch.addActionListener(e -> applyFilters());

    toolbar.add(txtSearch);
    toolbar.add(btnSearch);
    toolbar.add(btnReset);
    return toolbar;
  }

  private JPanel buildActions() {
    JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Thêm"),
      UIUtils.ghostButton("Sửa"),
      UIUtils.ghostButton("Ngừng hợp tác/Hợp tác lại"),
      UIUtils.ghostButton("Tải lại")
    );

    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      addSupplier()
    );
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      editSupplier()
    );
    ((javax.swing.JButton) actions.getComponent(2)).addActionListener(e ->
      toggleCooperation()
    );
    ((javax.swing.JButton) actions.getComponent(3)).addActionListener(e ->
      reloadData()
    );
    return actions;
  }

  private void reloadData() {
    allSuppliers.clear();
    allSuppliers.addAll(nhaCungCapBUS.list());
    applyFilters();
  }

  private void applyFilters() {
    filteredSuppliers.clear();
    String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);

    for (NhaCungCapDTO supplier : allSuppliers) {
      String ma = safe(supplier.getMaNhaCungCap()).toLowerCase(Locale.ROOT);
      String ten = safe(supplier.getTenNhaCungCap()).toLowerCase(Locale.ROOT);
      boolean match =
        keyword.isEmpty() || ma.contains(keyword) || ten.contains(keyword);
      if (match) {
        filteredSuppliers.add(supplier);
      }
    }
    renderTable();
  }

  private void renderTable() {
    model.setRowCount(0);
    for (NhaCungCapDTO supplier : filteredSuppliers) {
      model.addRow(
        new Object[] {
          supplier.getMaNhaCungCap(),
          supplier.getTenNhaCungCap(),
          supplier.getDiaChi(),
          supplier.getSDT(),
          supplier.isActive() ? "Đang hợp tác" : "Ngừng hợp tác",
        }
      );
    }
  }

  private void addSupplier() {
    NhaCungCapDTO input = showSupplierDialog(null, false);
    if (input == null) {
      return;
    }
    if (!nhaCungCapBUS.addNCC(input)) {
      DialogHelper.error(
        this,
        "Thêm nhà cung cấp thất bại. Vui lòng kiểm tra dữ liệu."
      );
      return;
    }
    DialogHelper.info(this, "Thêm nhà cung cấp thành công.");
    reloadData();
  }

  private void editSupplier() {
    NhaCungCapDTO selected = getSelectedSupplier();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn nhà cung cấp cần sửa.");
      return;
    }

    NhaCungCapDTO updated = showSupplierDialog(selected, true);
    if (updated == null) {
      return;
    }
    if (!nhaCungCapBUS.updateNCC(updated)) {
      DialogHelper.error(this, "Cập nhật nhà cung cấp thất bại.");
      return;
    }
    DialogHelper.info(this, "Cập nhật nhà cung cấp thành công.");
    reloadData();
  }

  private void toggleCooperation() {
    NhaCungCapDTO selected = getSelectedSupplier();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn nhà cung cấp.");
      return;
    }

    boolean targetActive = !selected.isActive();
    String actionText = targetActive ? "hợp tác lại" : "ngừng hợp tác";

    if (
      !DialogHelper.confirm(
        this,
        "Bạn có muốn " +
          actionText +
          " với NCC " +
          selected.getMaNhaCungCap() +
          "?"
      )
    ) {
      return;
    }

    boolean ok = targetActive
      ? nhaCungCapBUS.hopTacLai(selected.getMaNhaCungCap())
      : nhaCungCapBUS.ngungHopTac(selected.getMaNhaCungCap());

    if (!ok) {
      DialogHelper.error(this, "Cập nhật trạng thái hợp tác thất bại.");
      return;
    }

    DialogHelper.info(this, "Cập nhật trạng thái hợp tác thành công.");
    reloadData();
  }

  private NhaCungCapDTO getSelectedSupplier() {
    if (table == null) {
      return null;
    }
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    int modelRow = table.convertRowIndexToModel(row);
    String maNcc = String.valueOf(model.getValueAt(modelRow, 0));
    for (NhaCungCapDTO supplier : filteredSuppliers) {
      if (maNcc.equals(supplier.getMaNhaCungCap())) {
        return supplier;
      }
    }
    return null;
  }

  private NhaCungCapDTO showSupplierDialog(
    NhaCungCapDTO source,
    boolean isEdit
  ) {
    JDialog dialog = new JDialog(
      javax.swing.SwingUtilities.getWindowAncestor(this),
      isEdit ? "Sửa nhà cung cấp" : "Thêm nhà cung cấp",
      java.awt.Dialog.ModalityType.APPLICATION_MODAL
    );
    dialog.setSize(540, 380);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(0, 10));

    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    JTextField txtMa = UIUtils.roundedTextField("Mã nhà cung cấp", 20);
    JTextField txtTen = UIUtils.roundedTextField("Tên nhà cung cấp", 20);
    JTextField txtDiaChi = UIUtils.roundedTextField("Địa chỉ", 20);
    JTextField txtSdt = UIUtils.roundedTextField("SĐT (10 số)", 20);

    if (source != null) {
      txtMa.setText(safe(source.getMaNhaCungCap()));
      txtTen.setText(safe(source.getTenNhaCungCap()));
      txtDiaChi.setText(safe(source.getDiaChi()));
      txtSdt.setText(safe(source.getSDT()));
    }
    if (isEdit) {
      txtMa.setEditable(false);
    }

    int row = 0;
    addFormRow(form, gbc, row++, "Mã NCC", txtMa);
    addFormRow(form, gbc, row++, "Tên nhà cung cấp", txtTen);
    addFormRow(form, gbc, row++, "Địa chỉ", txtDiaChi);
    addFormRow(form, gbc, row++, "SĐT", txtSdt);
    if (!isEdit) {
      addFormRow(
        form,
        gbc,
        row++,
        "Trạng thái",
        new JLabel("Đang hợp tác (mặc định)")
      );
    }

    JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Lưu"),
      UIUtils.ghostButton("Hủy")
    );
    final NhaCungCapDTO[] result = new NhaCungCapDTO[] { null };

    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e -> {
      NhaCungCapDTO supplier = new NhaCungCapDTO();
      supplier.setMaNhaCungCap(txtMa.getText().trim());
      supplier.setTenNhaCungCap(txtTen.getText().trim());
      supplier.setDiaChi(txtDiaChi.getText().trim());
      supplier.setSDT(txtSdt.getText().trim());
      supplier.setActive(source == null || source.isActive());

      String error = validateSupplier(supplier, isEdit);
      if (error != null) {
        DialogHelper.warn(dialog, error);
        return;
      }

      result[0] = supplier;
      dialog.dispose();
    });
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      dialog.dispose()
    );

    dialog.add(form, BorderLayout.CENTER);
    dialog.add(actions, BorderLayout.SOUTH);
    dialog.setVisible(true);
    return result[0];
  }

  private String validateSupplier(NhaCungCapDTO supplier, boolean isEdit) {
    if (isEdit && safe(supplier.getMaNhaCungCap()).isEmpty()) {
      return "Mã NCC không được để trống khi sửa.";
    }
    if (safe(supplier.getMaNhaCungCap()).isEmpty()) {
      return "Mã NCC không được để trống.";
    }
    if (safe(supplier.getTenNhaCungCap()).isEmpty()) {
      return "Tên nhà cung cấp không được để trống.";
    }
    if (safe(supplier.getDiaChi()).isEmpty()) {
      return "Địa chỉ không được để trống.";
    }
    if (!safe(supplier.getSDT()).matches("^(03|05|07|08|09)\\d{8}$")) {
      return "SĐT không hợp lệ (đầu số 03/05/07/08/09 và đủ 10 số).";
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
}
