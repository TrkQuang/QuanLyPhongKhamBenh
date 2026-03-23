package phongkham.gui.nhathuoc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.NhaCungCapBUS;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.LoThuocNhapDTO;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class PhieuNhapPanel extends BasePanel {

  private final PhieuNhapBUS phieuNhapBUS = new PhieuNhapBUS();
  private final CTPhieuNhapBUS ctPhieuNhapBUS = new CTPhieuNhapBUS();
  private final NhaCungCapBUS nhaCungCapBUS = new NhaCungCapBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();

  private JTable table;
  private JButton btnEdit;
  private JButton btnCancel;
  private JButton btnConfirmImport;
  private JButton btnViewDetail;
  private JButton btnLotOverview;
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã phiếu",
      "Nhà cung cấp",
      "Ngày nhập",
      "Người giao",
      "Tổng tiền",
      "Trạng thái",
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
    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
          updateActionButtons();
        }
      });
    add(
      UIUtils.createSection("Danh sách phiếu nhập", new JScrollPane(table)),
      BorderLayout.CENTER
    );

    JButton btnAdd = UIUtils.primaryButton("Tạo phiếu nhập");
    btnEdit = UIUtils.ghostButton("Sửa chi tiết");
    btnConfirmImport = UIUtils.ghostButton("Xác nhận nhập kho");
    btnCancel = UIUtils.ghostButton("Hủy phiếu");
    btnViewDetail = UIUtils.ghostButton("Xem chi tiết");
    btnLotOverview = UIUtils.ghostButton("Theo dõi lô/HSD");
    JButton btnReload = UIUtils.ghostButton("Tải lại");

    JPanel actions = UIUtils.row(
      btnAdd,
      btnEdit,
      btnConfirmImport,
      btnCancel,
      btnViewDetail,
      btnLotOverview,
      btnReload
    );

    btnAdd.addActionListener(e -> openReceiptEditor(null));
    btnEdit.addActionListener(e -> editSelectedReceipt());
    btnConfirmImport.addActionListener(e -> confirmImportSelectedReceipt());
    btnCancel.addActionListener(e -> cancelSelectedReceipt());
    btnViewDetail.addActionListener(e ->
      openDetailDialog(getSelectedReceipt())
    );
    btnLotOverview.addActionListener(e -> openLotOverviewDialog());
    btnReload.addActionListener(e -> loadData());

    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private void loadData() {
    model.setRowCount(0);
    ArrayList<PhieuNhapDTO> dsPhieuNhap = phieuNhapBUS.getAll();
    dsPhieuNhap.sort(
      Comparator.comparing(
        PhieuNhapDTO::getNgayNhap,
        Comparator.nullsLast(Comparator.reverseOrder())
      )
    );
    for (PhieuNhapDTO phieuNhap : dsPhieuNhap) {
      model.addRow(
        new Object[] {
          phieuNhap.getMaPhieuNhap(),
          formatSupplierLabel(phieuNhap.getMaNCC()),
          phieuNhap.getNgayNhap(),
          phieuNhap.getNguoiGiao(),
          phieuNhap.getTongTienNhap(),
          statusLabel(phieuNhap.getTrangThai()),
        }
      );
    }
    updateActionButtons();
  }

  private void openReceiptEditor(PhieuNhapDTO source) {
    boolean isCreate = source == null;
    String maPhieuNhap = isCreate
      ? generateNextMaPhieuNhap()
      : source.getMaPhieuNhap();

    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      isCreate ? "Tạo phiếu nhập" : "Sửa chi tiết phiếu nhập",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel headerForm = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    List<NhaCungCapDTO> suppliers = nhaCungCapBUS.listDangHopTac();
    if (suppliers == null || suppliers.isEmpty()) {
      DialogHelper.warn(this, "Không có nhà cung cấp đang hợp tác.");
      return;
    }

    JComboBox<String> cbSupplier = new JComboBox<>();
    for (NhaCungCapDTO ncc : suppliers) {
      cbSupplier.addItem(
        ncc.getMaNhaCungCap() + " - " + ncc.getTenNhaCungCap()
      );
    }

    if (!isCreate) {
      for (int i = 0; i < suppliers.size(); i++) {
        if (source.getMaNCC().equals(suppliers.get(i).getMaNhaCungCap())) {
          cbSupplier.setSelectedIndex(i);
          break;
        }
      }
    }

    JTextField txtMaPhieu = new JTextField(maPhieuNhap, 16);
    txtMaPhieu.setEditable(false);

    JSpinner spNgayNhap = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        null,
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgayNhap.setEditor(new JSpinner.DateEditor(spNgayNhap, "dd/MM/yyyy"));
    if (!isCreate && source.getNgayNhap() != null) {
      spNgayNhap.setValue(
        Date.from(
          source
            .getNgayNhap()
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
        )
      );
    }

    JTextField txtNguoiGiao = new JTextField(
      isCreate ? "" : source.getNguoiGiao(),
      24
    );
    JLabel lblTongTien = new JLabel("Tổng tiền: 0");
    final String receiptLotCode = generateReceiptLotCode(maPhieuNhap);

    gbc.gridx = 0;
    gbc.gridy = 0;
    headerForm.add(new JLabel("Mã phiếu"), gbc);
    gbc.gridx = 1;
    headerForm.add(txtMaPhieu, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    headerForm.add(new JLabel("Nhà cung cấp"), gbc);
    gbc.gridx = 1;
    headerForm.add(cbSupplier, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    headerForm.add(new JLabel("Ngày nhập"), gbc);
    gbc.gridx = 1;
    headerForm.add(spNgayNhap, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    headerForm.add(new JLabel("Người giao"), gbc);
    gbc.gridx = 1;
    headerForm.add(txtNguoiGiao, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    headerForm.add(lblTongTien, gbc);

    DefaultTableModel detailModel = new DefaultTableModel(
      new Object[] {
        "Mã CTPN",
        "Số lô",
        "Mã thuốc",
        "Tên thuốc",
        "Số lượng",
        "SL còn lại",
        "Giá nhập",
        "Hạn sử dụng",
        "Trạng thái HSD",
        "Thành tiền",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable detailTable = new JTable(detailModel);
    UIUtils.styleTable(detailTable);
    detailTable.setFillsViewportHeight(true);
    ArrayList<LineItem> lineItems = new ArrayList<>();

    if (!isCreate) {
      ArrayList<CTPhieuNhapDTO> oldDetails = ctPhieuNhapBUS.getByMaPhieuNhap(
        source.getMaPhieuNhap()
      );
      for (CTPhieuNhapDTO ct : oldDetails) {
        ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
        LineItem item = new LineItem();
        item.maCTPN = ct.getMaCTPN();
        item.soLo = ct.getSoLo();
        item.maThuoc = ct.getMaThuoc();
        item.tenThuoc = thuoc == null ? "" : thuoc.getTenThuoc();
        item.soLuong = ct.getSoLuongNhap();
        item.soLuongConLai = ct.getSoLuongConLai();
        item.donGiaNhap = ct.getDonGiaNhap();
        item.hanSuDung = ct.getHanSuDung();
        lineItems.add(item);
      }
    }

    refreshLineItemTable(detailModel, lineItems, lblTongTien);

    JButton btnAddLine = UIUtils.ghostButton("Thêm thuốc");
    JButton btnEditLine = UIUtils.ghostButton("Sửa dòng");
    JButton btnRemoveLine = UIUtils.ghostButton("Xóa dòng");

    btnAddLine.addActionListener(e -> {
      LineItem item = showLineItemDialog(dialog, null, receiptLotCode);
      if (item == null) {
        return;
      }
      item.maCTPN = generateNextMaCTPN();
      item.soLuongConLai = item.soLuong;
      lineItems.add(item);
      refreshLineItemTable(detailModel, lineItems, lblTongTien);
    });

    btnEditLine.addActionListener(e -> {
      int row = detailTable.getSelectedRow();
      if (row < 0) {
        DialogHelper.warn(dialog, "Vui lòng chọn dòng thuốc cần sửa.");
        return;
      }
      int modelRow = detailTable.convertRowIndexToModel(row);
      LineItem old = lineItems.get(modelRow);
      LineItem edited = showLineItemDialog(dialog, old, receiptLotCode);
      if (edited == null) {
        return;
      }
      edited.maCTPN = old.maCTPN;
      edited.soLuongConLai = old.soLuongConLai;
      lineItems.set(modelRow, edited);
      refreshLineItemTable(detailModel, lineItems, lblTongTien);
    });

    btnRemoveLine.addActionListener(e -> {
      int row = detailTable.getSelectedRow();
      if (row < 0) {
        DialogHelper.warn(dialog, "Vui lòng chọn dòng thuốc cần xóa.");
        return;
      }
      lineItems.remove(detailTable.convertRowIndexToModel(row));
      refreshLineItemTable(detailModel, lineItems, lblTongTien);
    });

    JPanel detailActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    detailActions.setOpaque(false);
    detailActions.add(btnAddLine);
    detailActions.add(btnEditLine);
    detailActions.add(btnRemoveLine);

    JPanel detailBlock = new JPanel(new BorderLayout(0, 8));
    detailBlock.setOpaque(false);
    detailBlock.add(detailActions, BorderLayout.NORTH);
    detailBlock.add(new JScrollPane(detailTable), BorderLayout.CENTER);

    JPanel center = new JPanel(new BorderLayout(8, 8));
    center.setOpaque(false);
    center.add(headerForm, BorderLayout.NORTH);
    center.add(detailBlock, BorderLayout.CENTER);

    JButton btnClose = UIUtils.ghostButton("Đóng");
    JButton btnSave = UIUtils.primaryButton(
      isCreate ? "Tạo phiếu nhập" : "Lưu thay đổi"
    );
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    bottom.setOpaque(false);
    bottom.add(btnClose);
    bottom.add(btnSave);

    btnClose.addActionListener(e -> dialog.dispose());
    btnSave.addActionListener(e -> {
      if (lineItems.isEmpty()) {
        DialogHelper.warn(dialog, "Phiếu nhập phải có ít nhất một thuốc.");
        return;
      }
      if (txtNguoiGiao.getText().trim().isEmpty()) {
        DialogHelper.warn(dialog, "Người giao không được để trống.");
        return;
      }

      int selectedSupplier = cbSupplier.getSelectedIndex();
      if (selectedSupplier < 0) {
        DialogHelper.warn(dialog, "Vui lòng chọn nhà cung cấp.");
        return;
      }

      NhaCungCapDTO ncc = suppliers.get(selectedSupplier);
      Date ngayNhap = new Date(((Date) spNgayNhap.getValue()).getTime());

      PhieuNhapDTO pn = new PhieuNhapDTO();
      pn.setMaPhieuNhap(maPhieuNhap);
      pn.setMaNCC(ncc.getMaNhaCungCap());
      pn.setNgayNhap(new java.sql.Date(ngayNhap.getTime()));
      pn.setNguoiGiao(txtNguoiGiao.getText().trim());
      pn.setTongTienNhap((float) computeTotal(lineItems));
      pn.setTrangThai(
        isCreate
          ? StatusNormalizer.CHO_DUYET
          : StatusNormalizer.normalizePhieuNhapStatus(source.getTrangThai())
      );

      boolean createdHeader = false;
      if (isCreate) {
        if (!phieuNhapBUS.insert(pn)) {
          DialogHelper.error(dialog, "Tạo phiếu nhập thất bại.");
          return;
        }
        createdHeader = true;
      } else {
        if (!phieuNhapBUS.update(pn)) {
          DialogHelper.error(dialog, "Cập nhật thông tin phiếu nhập thất bại.");
          return;
        }
        ArrayList<CTPhieuNhapDTO> oldDetails = ctPhieuNhapBUS.getByMaPhieuNhap(
          maPhieuNhap
        );
        for (CTPhieuNhapDTO old : oldDetails) {
          if (!ctPhieuNhapBUS.delete(old.getMaCTPN())) {
            DialogHelper.error(
              dialog,
              "Không thể thay thế toàn bộ chi tiết phiếu nhập."
            );
            return;
          }
        }
      }

      ArrayList<String> insertedDetailIds = new ArrayList<>();
      for (LineItem item : lineItems) {
        boolean ok = ctPhieuNhapBUS.insert(
          item.maCTPN,
          maPhieuNhap,
          item.maThuoc,
          item.soLo,
          item.soLuong,
          item.donGiaNhap,
          item.hanSuDung
        );
        if (!ok) {
          if (isCreate && createdHeader) {
            rollbackCreatedReceipt(maPhieuNhap, insertedDetailIds);
          }
          DialogHelper.error(
            dialog,
            "Lưu chi tiết thuốc thất bại tại mã thuốc " +
              item.maThuoc +
              ". Vui lòng kiểm tra hạn sử dụng, số lô và dữ liệu thuốc."
          );
          return;
        }
        insertedDetailIds.add(item.maCTPN);
      }

      DialogHelper.info(
        dialog,
        isCreate
          ? "Tạo phiếu nhập thành công. Trạng thái mặc định: CHUA_XAC_NHAN."
          : "Cập nhật phiếu nhập thành công."
      );
      dialog.dispose();
      loadData();
    });

    dialog.add(center, BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setMinimumSize(new Dimension(900, 640));
    dialog.setSize(980, 700);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void editSelectedReceipt() {
    PhieuNhapDTO selected = getSelectedReceipt();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn phiếu nhập để sửa.");
      return;
    }

    String status = StatusNormalizer.normalizePhieuNhapStatus(
      selected.getTrangThai()
    );
    if (!StatusNormalizer.CHO_DUYET.equals(status)) {
      DialogHelper.warn(this, "Chỉ sửa được phiếu ở trạng thái CHUA_XAC_NHAN.");
      return;
    }

    openReceiptEditor(selected);
  }

  private void confirmImportSelectedReceipt() {
    PhieuNhapDTO selected = getSelectedReceipt();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn phiếu nhập để xác nhận nhập kho.");
      return;
    }

    String status = StatusNormalizer.normalizePhieuNhapStatus(
      selected.getTrangThai()
    );
    if (StatusNormalizer.DA_NHAP.equals(status)) {
      DialogHelper.info(this, "Phiếu này đã nhập kho.");
      return;
    }
    if (StatusNormalizer.DA_HUY.equals(status)) {
      DialogHelper.warn(this, "Phiếu đã hủy, không thể xác nhận nhập kho.");
      return;
    }

    ArrayList<CTPhieuNhapDTO> details = ctPhieuNhapBUS.getByMaPhieuNhap(
      selected.getMaPhieuNhap()
    );
    if (details.isEmpty()) {
      DialogHelper.warn(this, "Phiếu chưa có chi tiết thuốc.");
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Xác nhận nhập kho phiếu " + selected.getMaPhieuNhap() + "?"
      )
    ) {
      return;
    }

    if (!ctPhieuNhapBUS.xacNhanNhapKho(selected.getMaPhieuNhap())) {
      DialogHelper.error(this, "Xác nhận nhập kho thất bại.");
      return;
    }

    DialogHelper.info(this, "Đã xác nhận nhập kho và cộng tồn kho thành công.");
    loadData();
  }

  private void cancelSelectedReceipt() {
    PhieuNhapDTO selected = getSelectedReceipt();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn phiếu nhập để hủy.");
      return;
    }

    String status = StatusNormalizer.normalizePhieuNhapStatus(
      selected.getTrangThai()
    );
    if (StatusNormalizer.DA_NHAP.equals(status)) {
      DialogHelper.warn(this, "Phiếu đã nhập kho, không thể hủy.");
      return;
    }
    if (StatusNormalizer.DA_HUY.equals(status)) {
      DialogHelper.info(this, "Phiếu đã hủy trước đó.");
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Hủy phiếu nhập " + selected.getMaPhieuNhap() + "?"
      )
    ) {
      return;
    }

    if (!phieuNhapBUS.delete(selected.getMaPhieuNhap())) {
      DialogHelper.error(this, "Hủy phiếu nhập thất bại.");
      return;
    }

    DialogHelper.info(
      this,
      "Đã hủy phiếu nhập. Các thao tác chỉnh sửa/xác nhận đã bị khóa."
    );
    loadData();
  }

  private void openDetailDialog(PhieuNhapDTO pn) {
    if (pn == null) {
      DialogHelper.warn(this, "Vui lòng chọn phiếu nhập để xem chi tiết.");
      return;
    }

    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Chi tiết phiếu nhập " + pn.getMaPhieuNhap(),
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel info = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    addInfoRow(info, gbc, 0, "Mã phiếu", pn.getMaPhieuNhap());
    addInfoRow(
      info,
      gbc,
      1,
      "Nhà cung cấp",
      formatSupplierLabel(pn.getMaNCC())
    );
    addInfoRow(info, gbc, 2, "Ngày nhập", String.valueOf(pn.getNgayNhap()));
    addInfoRow(info, gbc, 3, "Người giao", pn.getNguoiGiao());
    addInfoRow(info, gbc, 4, "Trạng thái", statusLabel(pn.getTrangThai()));
    addInfoRow(info, gbc, 5, "Tổng tiền", String.valueOf(pn.getTongTienNhap()));

    DefaultTableModel detailModel = new DefaultTableModel(
      new Object[] {
        "Mã CTPN",
        "Số lô",
        "Mã thuốc",
        "Tên thuốc",
        "Số lượng",
        "SL còn lại",
        "Giá nhập",
        "Hạn sử dụng",
        "Trạng thái HSD",
        "Thành tiền",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    ArrayList<CTPhieuNhapDTO> details = ctPhieuNhapBUS.getByMaPhieuNhap(
      pn.getMaPhieuNhap()
    );
    for (CTPhieuNhapDTO ct : details) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      detailModel.addRow(
        new Object[] {
          ct.getMaCTPN(),
          ct.getSoLo(),
          ct.getMaThuoc(),
          thuoc == null ? "" : thuoc.getTenThuoc(),
          ct.getSoLuongNhap(),
          ct.getSoLuongConLai(),
          ct.getDonGiaNhap(),
          ct.getHanSuDung(),
          getHsdStateText(
            ct.getHanSuDung() == null ? null : ct.getHanSuDung().toLocalDate()
          ),
          ct.getDonGiaNhap().multiply(BigDecimal.valueOf(ct.getSoLuongNhap())),
        }
      );
    }

    JTable detailTable = new JTable(detailModel);
    UIUtils.styleTable(detailTable);

    JButton btnClose = UIUtils.ghostButton("Đóng");
    btnClose.addActionListener(e -> dialog.dispose());
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    bottom.setOpaque(false);
    bottom.add(btnClose);

    dialog.add(info, BorderLayout.NORTH);
    dialog.add(new JScrollPane(detailTable), BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setSize(950, 620);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private LineItem showLineItemDialog(
    JDialog owner,
    LineItem source,
    String receiptLotCode
  ) {
    List<ThuocDTO> medicines = new ArrayList<>();
    for (ThuocDTO t : thuocBUS.list()) {
      if (t != null && t.isActive()) {
        medicines.add(t);
      }
    }
    if (medicines == null || medicines.isEmpty()) {
      DialogHelper.warn(owner, "Không có thuốc đang hoạt động để chọn.");
      return null;
    }

    JComboBox<String> cbThuoc = new JComboBox<>();
    for (ThuocDTO t : medicines) {
      cbThuoc.addItem(t.getMaThuoc() + " - " + t.getTenThuoc());
    }

    JSpinner spSoLuong = new JSpinner(
      new javax.swing.SpinnerNumberModel(
        source == null ? 1 : source.soLuong,
        1,
        100000,
        1
      )
    );
    JTextField txtSoLo = new JTextField(22);
    txtSoLo.setEditable(false);
    txtSoLo.setToolTipText(
      "Số lô dùng chung theo phiếu nhập để đồng nhất quản lý"
    );
    JSpinner spDonGia = new JSpinner(
      new javax.swing.SpinnerNumberModel(
        source == null ? 1000.0 : source.donGiaNhap.doubleValue(),
        1.0,
        1000000000.0,
        1000.0
      )
    );
    JSpinner spHsd = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        null,
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spHsd.setEditor(new JSpinner.DateEditor(spHsd, "dd/MM/yyyy"));
    if (source != null && source.hanSuDung != null) {
      spHsd.setValue(
        Date.from(source.hanSuDung.atZone(ZoneId.systemDefault()).toInstant())
      );
    }

    if (source != null) {
      for (int i = 0; i < medicines.size(); i++) {
        if (source.maThuoc.equals(medicines.get(i).getMaThuoc())) {
          cbThuoc.setSelectedIndex(i);
          break;
        }
      }
      txtSoLo.setText(
        receiptLotCode == null || receiptLotCode.trim().isEmpty()
          ? ""
          : receiptLotCode
      );
    } else {
      txtSoLo.setText(
        receiptLotCode == null || receiptLotCode.trim().isEmpty()
          ? ""
          : receiptLotCode
      );
    }

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Thuốc"), gbc);
    gbc.gridx = 1;
    panel.add(cbThuoc, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Số lô"), gbc);
    gbc.gridx = 1;
    panel.add(txtSoLo, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Số lượng"), gbc);
    gbc.gridx = 1;
    panel.add(spSoLuong, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Giá nhập"), gbc);
    gbc.gridx = 1;
    panel.add(spDonGia, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(new JLabel("Hạn sử dụng"), gbc);
    gbc.gridx = 1;
    panel.add(spHsd, gbc);

    int result = JOptionPane.showConfirmDialog(
      owner,
      panel,
      source == null ? "Thêm thuốc nhập" : "Sửa dòng thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );
    if (result != JOptionPane.OK_OPTION) {
      return null;
    }

    int index = cbThuoc.getSelectedIndex();
    if (index < 0) {
      return null;
    }
    ThuocDTO selected = medicines.get(index);
    String soLo = txtSoLo.getText() == null ? "" : txtSoLo.getText().trim();
    if (soLo.isEmpty()) {
      soLo = receiptLotCode == null ? "" : receiptLotCode.trim();
    }
    if (soLo.isEmpty()) {
      DialogHelper.warn(owner, "Không thể sinh số lô cho phiếu nhập này.");
      return null;
    }

    LineItem item = new LineItem();
    item.soLo = soLo;
    item.maThuoc = selected.getMaThuoc();
    item.tenThuoc = selected.getTenThuoc();
    item.soLuong = (Integer) spSoLuong.getValue();
    item.donGiaNhap = BigDecimal.valueOf(
      ((Number) spDonGia.getValue()).doubleValue()
    );
    item.hanSuDung = LocalDateTime.ofInstant(
      ((Date) spHsd.getValue()).toInstant(),
      ZoneId.systemDefault()
    );
    return item;
  }

  private void refreshLineItemTable(
    DefaultTableModel detailModel,
    ArrayList<LineItem> lineItems,
    JLabel lblTongTien
  ) {
    detailModel.setRowCount(0);
    for (LineItem item : lineItems) {
      detailModel.addRow(
        new Object[] {
          item.maCTPN,
          item.soLo,
          item.maThuoc,
          item.tenThuoc,
          item.soLuong,
          item.soLuongConLai,
          item.donGiaNhap,
          item.hanSuDung,
          getHsdStateText(
            item.hanSuDung == null ? null : item.hanSuDung.toLocalDate()
          ),
          item.getThanhTien(),
        }
      );
    }
    lblTongTien.setText("Tổng tiền: " + computeTotal(lineItems));
  }

  private double computeTotal(ArrayList<LineItem> lineItems) {
    double total = 0;
    for (LineItem item : lineItems) {
      total += item.getThanhTien().doubleValue();
    }
    return total;
  }

  private void rollbackCreatedReceipt(
    String maPhieuNhap,
    ArrayList<String> insertedDetailIds
  ) {
    if (insertedDetailIds != null) {
      for (String maCTPN : insertedDetailIds) {
        if (maCTPN == null || maCTPN.trim().isEmpty()) {
          continue;
        }
        ctPhieuNhapBUS.delete(maCTPN);
      }
    }
    phieuNhapBUS.hardDeleteForRollback(maPhieuNhap);
  }

  private void updateActionButtons() {
    PhieuNhapDTO selected = getSelectedReceipt();
    if (selected == null) {
      btnEdit.setEnabled(false);
      btnConfirmImport.setEnabled(false);
      btnCancel.setEnabled(false);
      btnViewDetail.setEnabled(false);
      return;
    }

    String status = StatusNormalizer.normalizePhieuNhapStatus(
      selected.getTrangThai()
    );
    boolean isPending =
      StatusNormalizer.CHO_DUYET.equals(status) ||
      StatusNormalizer.DA_DUYET.equals(status);
    boolean isDone =
      StatusNormalizer.DA_NHAP.equals(status) ||
      StatusNormalizer.DA_HUY.equals(status);

    btnEdit.setEnabled(StatusNormalizer.CHO_DUYET.equals(status));
    btnConfirmImport.setEnabled(isPending);
    btnCancel.setEnabled(isPending);
    btnViewDetail.setEnabled(true);

    if (isDone) {
      btnEdit.setEnabled(false);
      btnConfirmImport.setEnabled(false);
      btnCancel.setEnabled(false);
    }
  }

  private PhieuNhapDTO getSelectedReceipt() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    int modelRow = table.convertRowIndexToModel(row);
    String maPN = String.valueOf(model.getValueAt(modelRow, 0));
    return phieuNhapBUS.getById(maPN);
  }

  private String statusLabel(String statusRaw) {
    String status = StatusNormalizer.normalizePhieuNhapStatus(statusRaw);
    if (StatusNormalizer.CHO_DUYET.equals(status)) {
      return "CHUA_XAC_NHAN";
    }
    if (StatusNormalizer.DA_DUYET.equals(status)) {
      return "DA_DUYET";
    }
    if (StatusNormalizer.DA_NHAP.equals(status)) {
      return "DA_NHAP";
    }
    if (StatusNormalizer.DA_HUY.equals(status)) {
      return "DA_HUY";
    }
    return status;
  }

  private String generateNextMaPhieuNhap() {
    int max = 0;
    for (PhieuNhapDTO pn : phieuNhapBUS.getAll()) {
      String ma = pn.getMaPhieuNhap();
      if (ma == null || !ma.startsWith("PN")) {
        continue;
      }
      try {
        max = Math.max(max, Integer.parseInt(ma.substring(2)));
      } catch (NumberFormatException ignored) {}
    }
    return String.format("PN%03d", max + 1);
  }

  private String generateNextMaCTPN() {
    // Dùng timestamp + random để tránh trùng khi thêm nhiều dòng trước khi lưu DB.
    long ts = System.currentTimeMillis() % 1000000000L;
    int rand = (int) (Math.random() * 1000);
    return String.format("CTPN%09d%03d", ts, rand);
  }

  private void addInfoRow(
    JPanel panel,
    GridBagConstraints gbc,
    int row,
    String label,
    String value
  ) {
    gbc.gridx = 0;
    gbc.gridy = row;
    panel.add(new JLabel(label + ":"), gbc);
    gbc.gridx = 1;
    panel.add(new JLabel(value == null ? "" : value), gbc);
  }

  private void openLotOverviewDialog() {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Theo dõi lô nhập theo HSD/NCC",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    top.setOpaque(false);
    JTextField txtKeyword = new JTextField(22);
    JComboBox<String> cbNcc = new JComboBox<>();
    JCheckBox chkOnlyRemain = new JCheckBox("Chỉ lô còn tồn");
    chkOnlyRemain.setSelected(true);
    JComboBox<String> cbHsdFilter = new JComboBox<>(
      new String[] {
        "Tất cả",
        "Còn hạn",
        "Sắp hết hạn 30 ngày",
        "Sắp hết hạn 60 ngày",
        "Sắp hết hạn 90 ngày",
        "Hết hạn",
      }
    );

    cbNcc.addItem("Tất cả NCC");
    List<NhaCungCapDTO> suppliers = nhaCungCapBUS.list();
    if (suppliers != null) {
      for (NhaCungCapDTO ncc : suppliers) {
        cbNcc.addItem(ncc.getMaNhaCungCap() + " - " + ncc.getTenNhaCungCap());
      }
    }

    JButton btnFilter = UIUtils.primaryButton("Lọc");
    JButton btnQuickReports = UIUtils.ghostButton("Báo cáo nhanh");
    JButton btnDisposeExpired = UIUtils.ghostButton("Tiêu hủy lô hết hạn");
    JButton btnClose = UIUtils.ghostButton("Đóng");

    top.add(new JLabel("Từ khóa"));
    top.add(txtKeyword);
    top.add(new JLabel("NCC"));
    top.add(cbNcc);
    top.add(new JLabel("Trạng thái HSD"));
    top.add(cbHsdFilter);
    top.add(chkOnlyRemain);
    top.add(btnFilter);
    top.add(btnQuickReports);
    top.add(btnDisposeExpired);
    top.add(btnClose);

    DefaultTableModel lotModel = new DefaultTableModel(
      new Object[] {
        "Mã phiếu",
        "NCC",
        "Mã thuốc",
        "Tên thuốc",
        "Số lô",
        "Hạn sử dụng",
        "Còn hạn (ngày)",
        "SL nhập",
        "SL còn lại",
        "Đơn giá nhập",
        "Trạng thái HSD",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable lotTable = new JTable(lotModel);
    UIUtils.styleTable(lotTable);
    JLabel lblSummary = new JLabel(" ");

    Runnable reloadLots = () -> {
      lotModel.setRowCount(0);
      String keyword =
        txtKeyword.getText() == null ? "" : txtKeyword.getText().trim();
      String selectedNcc = String.valueOf(cbNcc.getSelectedItem());
      String maNccFilter = "";
      if (selectedNcc != null && !"Tất cả NCC".equals(selectedNcc)) {
        maNccFilter = selectedNcc.split(" - ")[0].trim();
      }

      String hsdFilter = mapHsdFilter(
        String.valueOf(cbHsdFilter.getSelectedItem())
      );
      boolean onlyRemain = chkOnlyRemain.isSelected();
      ArrayList<LoThuocNhapDTO> lots = ctPhieuNhapBUS.getLotOverview(
        keyword,
        maNccFilter,
        hsdFilter
      );
      int totalRemain = 0;
      int expiredRemain = 0;
      int near30 = 0;
      int near60 = 0;
      int near90 = 0;
      for (LoThuocNhapDTO lot : lots) {
        if (onlyRemain && lot.getSoLuongConLai() <= 0) {
          continue;
        }
        long daysRemaining = getRemainingDays(lot.getHanSuDung());
        int remain = Math.max(0, lot.getSoLuongConLai());
        totalRemain += remain;
        if (daysRemaining <= 0) {
          expiredRemain += remain;
        } else {
          if (daysRemaining <= 30) {
            near30 += remain;
          }
          if (daysRemaining <= 60) {
            near60 += remain;
          }
          if (daysRemaining <= 90) {
            near90 += remain;
          }
        }
        lotModel.addRow(
          new Object[] {
            lot.getMaPhieuNhap(),
            lot.getMaNCC() + " - " + safe(lot.getTenNhaCungCap()),
            lot.getMaThuoc(),
            lot.getTenThuoc(),
            lot.getSoLo(),
            lot.getHanSuDung(),
            daysRemaining == Long.MAX_VALUE ? "" : daysRemaining,
            lot.getSoLuongNhap(),
            lot.getSoLuongConLai(),
            lot.getDonGiaNhap(),
            getHsdStateText(lot.getHanSuDung()),
          }
        );
      }
      lblSummary.setText(
        "Tồn còn lại: " +
          totalRemain +
          " | Hết hạn: " +
          expiredRemain +
          " | <=30 ngày: " +
          near30 +
          " | <=60 ngày: " +
          near60 +
          " | <=90 ngày: " +
          near90
      );
    };

    btnFilter.addActionListener(e -> reloadLots.run());
    btnQuickReports.addActionListener(e -> openQuickReportDialog());
    btnDisposeExpired.addActionListener(e -> {
      int confirmed = JOptionPane.showConfirmDialog(
        dialog,
        "Tạo nghiệp vụ tiêu hủy cho toàn bộ lô đã hết hạn (HSD <= hôm nay)?",
        "Xác nhận tiêu hủy",
        JOptionPane.YES_NO_OPTION
      );
      if (confirmed != JOptionPane.YES_OPTION) {
        return;
      }
      int disposed = ctPhieuNhapBUS.createDisposalForExpiredLots(
        "HET_HAN",
        "NHATHUOC"
      );
      if (disposed < 0) {
        DialogHelper.error(dialog, "Tạo nghiệp vụ tiêu hủy thất bại.");
        return;
      }
      DialogHelper.info(
        dialog,
        disposed == 0
          ? "Không có lô hết hạn cần tiêu hủy."
          : "Đã tạo nghiệp vụ tiêu hủy cho " + disposed + " lô hết hạn."
      );
      reloadLots.run();
      loadData();
    });
    txtKeyword.addActionListener(e -> reloadLots.run());
    cbNcc.addActionListener(e -> reloadLots.run());
    cbHsdFilter.addActionListener(e -> reloadLots.run());
    chkOnlyRemain.addActionListener(e -> reloadLots.run());
    btnClose.addActionListener(e -> dialog.dispose());

    reloadLots.run();

    dialog.add(top, BorderLayout.NORTH);
    JPanel center = new JPanel(new BorderLayout(0, 8));
    center.setOpaque(false);
    center.add(lblSummary, BorderLayout.NORTH);
    center.add(new JScrollPane(lotTable), BorderLayout.CENTER);
    dialog.add(center, BorderLayout.CENTER);
    dialog.setSize(1200, 620);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private String mapHsdFilter(String selected) {
    if (selected == null) {
      return "TAT_CA";
    }
    String normalized = selected.trim().toLowerCase(Locale.ROOT);
    if (normalized.contains("còn hạn") || normalized.contains("con han")) {
      return "CON_HAN";
    }
    if (normalized.contains("sắp hết") || normalized.contains("sap het")) {
      if (normalized.contains("90")) {
        return "SAP_HET_HAN_90";
      }
      if (normalized.contains("60")) {
        return "SAP_HET_HAN_60";
      }
      return "SAP_HET_HAN_30";
    }
    if (normalized.contains("hết hạn") || normalized.contains("het han")) {
      return "HET_HAN";
    }
    return "TAT_CA";
  }

  private long getRemainingDays(LocalDate hsd) {
    if (hsd == null) {
      return Long.MAX_VALUE;
    }
    return ChronoUnit.DAYS.between(LocalDate.now(), hsd);
  }

  private String getHsdStateText(LocalDate hsd) {
    if (hsd == null) {
      return "KHONG_CO_HSD";
    }
    long days = getRemainingDays(hsd);
    if (days < 0) {
      return "HET_HAN";
    }
    if (days <= 30) {
      return "SAP_HET_HAN";
    }
    return "CON_HAN";
  }

  private String safe(String text) {
    return text == null ? "" : text;
  }

  private String generateReceiptLotCode(String maPhieuNhap) {
    String ma =
      maPhieuNhap == null ? "PN" : maPhieuNhap.trim().toUpperCase(Locale.ROOT);
    String datePart = LocalDate.now().format(
      java.time.format.DateTimeFormatter.BASIC_ISO_DATE
    );
    return "LO-" + ma + "-" + datePart;
  }

  private void openQuickReportDialog() {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Báo cáo nhanh tồn kho theo HSD/NCC",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    ArrayList<LoThuocNhapDTO> allLots =
      ctPhieuNhapBUS.getAllLotsForMonitoring();
    ArrayList<LoThuocNhapDTO> activeLots = new ArrayList<>();
    LocalDate today = LocalDate.now();
    for (LoThuocNhapDTO lot : allLots) {
      String statusPn = safe(lot.getTrangThaiPhieuNhap())
        .trim()
        .toUpperCase(Locale.ROOT);
      boolean daNhap =
        "DA_NHAP".equals(statusPn) || "DA_NHAP_KHO".equals(statusPn);
      if (!daNhap) {
        continue;
      }
      if (lot.getSoLuongConLai() <= 0) {
        continue;
      }
      activeLots.add(lot);
    }

    Map<String, Integer> tonTheoHsd = new java.util.TreeMap<>();
    Map<String, Integer> tonTheoNcc = new java.util.TreeMap<>();
    Map<String, Integer> sapHetTheoNcc30 = new HashMap<>();
    Map<String, Integer> sapHetTheoNcc60 = new HashMap<>();
    Map<String, Integer> sapHetTheoNcc90 = new HashMap<>();
    int expiredCount = 0;
    int exp30 = 0;
    int exp60 = 0;
    int exp90 = 0;

    for (LoThuocNhapDTO lot : activeLots) {
      LocalDate hsd = lot.getHanSuDung();
      String hsdKey = hsd == null ? "KHONG_CO_HSD" : hsd.toString();
      String nccKey = lot.getMaNCC() + " - " + safe(lot.getTenNhaCungCap());

      tonTheoHsd.put(
        hsdKey,
        tonTheoHsd.getOrDefault(hsdKey, 0) + lot.getSoLuongConLai()
      );
      tonTheoNcc.put(
        nccKey,
        tonTheoNcc.getOrDefault(nccKey, 0) + lot.getSoLuongConLai()
      );

      if (hsd != null) {
        long days = ChronoUnit.DAYS.between(today, hsd);
        if (days <= 0) {
          expiredCount += lot.getSoLuongConLai();
        } else {
          if (days <= 30) {
            exp30 += lot.getSoLuongConLai();
            sapHetTheoNcc30.put(
              nccKey,
              sapHetTheoNcc30.getOrDefault(nccKey, 0) + lot.getSoLuongConLai()
            );
          }
          if (days <= 60) {
            exp60 += lot.getSoLuongConLai();
            sapHetTheoNcc60.put(
              nccKey,
              sapHetTheoNcc60.getOrDefault(nccKey, 0) + lot.getSoLuongConLai()
            );
          }
          if (days <= 90) {
            exp90 += lot.getSoLuongConLai();
            sapHetTheoNcc90.put(
              nccKey,
              sapHetTheoNcc90.getOrDefault(nccKey, 0) + lot.getSoLuongConLai()
            );
          }
        }
      }
    }

    DefaultTableModel modelHsd = new DefaultTableModel(
      new Object[] { "HSD", "Tồn còn lại" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    for (Map.Entry<String, Integer> entry : tonTheoHsd.entrySet()) {
      modelHsd.addRow(new Object[] { entry.getKey(), entry.getValue() });
    }

    DefaultTableModel modelNcc = new DefaultTableModel(
      new Object[] { "NCC", "Tồn còn lại" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    for (Map.Entry<String, Integer> entry : tonTheoNcc.entrySet()) {
      modelNcc.addRow(new Object[] { entry.getKey(), entry.getValue() });
    }

    DefaultTableModel modelSapHet = new DefaultTableModel(
      new Object[] { "NCC", "SL sắp hết hạn" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JComboBox<String> cbNguong = new JComboBox<>(
      new String[] { "30 ngày", "60 ngày", "90 ngày" }
    );
    Runnable reloadSapHet = () -> {
      modelSapHet.setRowCount(0);
      Map<String, Integer> source = sapHetTheoNcc30;
      String selected = String.valueOf(cbNguong.getSelectedItem());
      if (selected.contains("60")) {
        source = sapHetTheoNcc60;
      } else if (selected.contains("90")) {
        source = sapHetTheoNcc90;
      }
      java.util.List<Map.Entry<String, Integer>> rows = new ArrayList<>(
        source.entrySet()
      );
      rows.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
      for (Map.Entry<String, Integer> entry : rows) {
        modelSapHet.addRow(new Object[] { entry.getKey(), entry.getValue() });
      }
    };
    cbNguong.addActionListener(e -> reloadSapHet.run());
    reloadSapHet.run();

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    top.setOpaque(false);
    top.add(new JLabel("Hết hạn: " + expiredCount));
    top.add(new JLabel("Sắp hết hạn <=30 ngày: " + exp30));
    top.add(new JLabel("Sắp hết hạn <=60 ngày: " + exp60));
    top.add(new JLabel("Sắp hết hạn <=90 ngày: " + exp90));

    JTable tableHsd = new JTable(modelHsd);
    JTable tableNcc = new JTable(modelNcc);
    JTable tableSapHet = new JTable(modelSapHet);
    UIUtils.styleTable(tableHsd);
    UIUtils.styleTable(tableNcc);
    UIUtils.styleTable(tableSapHet);

    JPanel center = new JPanel(new java.awt.GridLayout(1, 3, 8, 8));
    center.setOpaque(false);
    center.add(
      UIUtils.createSection("Tồn theo HSD", new JScrollPane(tableHsd))
    );
    center.add(
      UIUtils.createSection("Tồn theo NCC", new JScrollPane(tableNcc))
    );

    JPanel sapHetSection = new JPanel(new BorderLayout(0, 6));
    sapHetSection.setOpaque(false);
    JPanel sapHetTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    sapHetTop.setOpaque(false);
    sapHetTop.add(new JLabel("Ngưỡng"));
    sapHetTop.add(cbNguong);
    sapHetSection.add(sapHetTop, BorderLayout.NORTH);
    sapHetSection.add(new JScrollPane(tableSapHet), BorderLayout.CENTER);
    center.add(
      UIUtils.createSection("Sắp hết hạn theo NCC (đổi/trả)", sapHetSection)
    );

    JButton btnClose = UIUtils.ghostButton("Đóng");
    btnClose.addActionListener(e -> dialog.dispose());
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    bottom.setOpaque(false);
    bottom.add(btnClose);

    dialog.add(top, BorderLayout.NORTH);
    dialog.add(center, BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setSize(1280, 620);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private String formatSupplierLabel(String maNCC) {
    if (maNCC == null || maNCC.trim().isEmpty()) {
      return "";
    }
    NhaCungCapDTO ncc = nhaCungCapBUS.layTheoMa(maNCC.trim());
    if (ncc == null || ncc.getTenNhaCungCap() == null) {
      return maNCC;
    }
    return maNCC + " - " + ncc.getTenNhaCungCap();
  }

  private static class LineItem {

    private String maCTPN;
    private String soLo;
    private String maThuoc;
    private String tenThuoc;
    private int soLuong;
    private int soLuongConLai;
    private BigDecimal donGiaNhap;
    private LocalDateTime hanSuDung;

    private BigDecimal getThanhTien() {
      return donGiaNhap.multiply(BigDecimal.valueOf(soLuong));
    }
  }
}
