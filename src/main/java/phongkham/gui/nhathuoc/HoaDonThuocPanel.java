package phongkham.gui.nhathuoc;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTDonThuocBUS;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.DonThuocBUS;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTDonThuocDTO;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.DonThuocDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.DTO.XuatThuocTheoLoDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;

public class HoaDonThuocPanel extends BasePanel {

  private static final int PAGE_SIZE = 10;
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private final HoaDonThuocBUS hoaDonThuocBUS = new HoaDonThuocBUS();
  private final CTHDThuocBUS cthdThuocBUS = new CTHDThuocBUS();
  private final DonThuocBUS donThuocBUS = new DonThuocBUS();
  private final CTDonThuocBUS ctDonThuocBUS = new CTDonThuocBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final HoSoBenhAnBUS hoSoBenhAnBUS = new HoSoBenhAnBUS();
  private final ArrayList<HoaDonThuocDTO> allInvoices = new ArrayList<>();
  private final ArrayList<HoaDonThuocDTO> filteredInvoices = new ArrayList<>();

  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã hóa đơn",
      "Mã đơn thuốc",
      "Ngày lập",
      "Tên bệnh nhân",
      "SĐT",
      "Tổng tiền",
      "TT thanh toán",
      "TT lấy thuốc",
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
  private JComboBox<String> cbStatusFilter;
  private JSpinner spFromDate;
  private JSpinner spToDate;
  private JButton btnPrev;
  private JButton btnNext;
  private JButton btnCreateFromPrescription;
  private JButton btnConfirmPayment;
  private JButton btnEditInvoice;
  private JButton btnConfirmPickup;
  private JButton btnCancelInvoice;
  private JButton btnViewLotIssueTrace;
  private JButton btnReload;
  private javax.swing.JLabel lblPageInfo;
  private int currentPage = 1;
  private boolean coQuyenXem;
  private boolean coQuyenThem;
  private boolean coQuyenSua;
  private boolean coQuyenXoa;
  private boolean coQuyenXacNhanThanhToan;
  private boolean coQuyenXacNhanGiaoThuoc;
  private boolean coQuyenXemXuatTheoLo;

  @Override
  protected void init() {
    add(buildTopFilter(), BorderLayout.NORTH);

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
      UIUtils.createSection(
        "Quản lý hóa đơn bán thuốc",
        new JScrollPane(table)
      ),
      BorderLayout.CENTER
    );

    add(buildBottomActions(), BorderLayout.SOUTH);
    reloadData();
  }

  private JPanel buildTopFilter() {
    JPanel wrapper = new JPanel(new BorderLayout(0, 6));
    wrapper.setOpaque(false);

    JPanel filterTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    filterTop.setOpaque(false);
    JPanel filterBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    filterBottom.setOpaque(false);

    txtSearch = (CustomTextField) UIUtils.roundedTextField(
      "Tìm mã hóa đơn / mã đơn thuốc / tên bệnh nhân / SĐT",
      28
    );
    cbSort = new JComboBox<>(
      new String[] { "Mặc định", "Giá tăng dần", "Giá giảm dần" }
    );
    cbStatusFilter = new JComboBox<>(
      new String[] {
        "Tất cả trạng thái",
        "CHO_XAC_NHAN_THANH_TOAN",
        "DA_THANH_TOAN_CHO_LAY",
        "DA_GIAO_THUOC",
        "DA_HUY_CHUA_THANH_TOAN",
        "DA_HOAN_TIEN",
      }
    );

    spFromDate = createDateSpinner();
    spToDate = createDateSpinner();
    applyDefaultDateRange();

    JButton btnFilter = UIUtils.primaryButton("Lọc");
    JButton btnReset = UIUtils.ghostButton("Xóa lọc");

    btnFilter.addActionListener(e -> {
      currentPage = 1;
      applyFiltersAndRender();
    });
    btnReset.addActionListener(e -> resetFilters());
    txtSearch.addActionListener(e -> {
      currentPage = 1;
      applyFiltersAndRender();
    });
    cbSort.addActionListener(e -> {
      currentPage = 1;
      applyFiltersAndRender();
    });
    cbStatusFilter.addActionListener(e -> {
      currentPage = 1;
      applyFiltersAndRender();
    });

    filterTop.add(txtSearch);
    filterTop.add(cbSort);
    filterTop.add(cbStatusFilter);
    filterTop.add(btnFilter);
    filterTop.add(btnReset);

    filterBottom.add(new javax.swing.JLabel("Từ ngày"));
    filterBottom.add(spFromDate);
    filterBottom.add(new javax.swing.JLabel("Đến ngày"));
    filterBottom.add(spToDate);

    wrapper.add(filterTop, BorderLayout.NORTH);
    wrapper.add(filterBottom, BorderLayout.SOUTH);
    return wrapper;
  }

  private JPanel buildBottomActions() {
    JPanel actions = new JPanel(new BorderLayout(0, 6));
    actions.setOpaque(false);

    JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actionRow.setOpaque(false);
    btnCreateFromPrescription = UIUtils.primaryButton("Tạo từ đơn thuốc");
    btnEditInvoice = UIUtils.ghostButton("Sửa chi tiết hóa đơn");
    btnConfirmPayment = UIUtils.primaryButton("Xác nhận thanh toán");
    btnConfirmPickup = UIUtils.ghostButton("Xác nhận giao thuốc");
    btnCancelInvoice = UIUtils.ghostButton("Hủy hóa đơn");
    btnViewLotIssueTrace = UIUtils.ghostButton("Xem xuất theo lô");
    btnReload = UIUtils.ghostButton("Tải lại");

    btnCreateFromPrescription.addActionListener(e -> openCreateInvoiceDialog());
    btnEditInvoice.addActionListener(e -> openEditInvoiceDialog());
    btnConfirmPayment.addActionListener(e -> confirmPayment());
    btnConfirmPickup.addActionListener(e -> confirmPickup());
    btnCancelInvoice.addActionListener(e -> cancelInvoice());
    btnViewLotIssueTrace.addActionListener(e -> openLotIssueTraceDialog());
    btnReload.addActionListener(e -> reloadData());

    actionRow.add(btnCreateFromPrescription);
    actionRow.add(btnEditInvoice);
    actionRow.add(btnConfirmPayment);
    actionRow.add(btnConfirmPickup);
    actionRow.add(btnCancelInvoice);
    actionRow.add(btnViewLotIssueTrace);
    actionRow.add(btnReload);

    apDungPhanQuyenHanhDong();

    JPanel pagingRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    pagingRow.setOpaque(false);
    lblPageInfo = new javax.swing.JLabel("Trang 1/1");
    btnPrev = UIUtils.ghostButton("< Trước");
    btnNext = UIUtils.ghostButton("Sau >");

    btnPrev.addActionListener(e -> {
      currentPage = Math.max(1, currentPage - 1);
      renderCurrentPage();
    });
    btnNext.addActionListener(e -> {
      currentPage++;
      renderCurrentPage();
    });

    pagingRow.add(lblPageInfo);
    pagingRow.add(btnPrev);
    pagingRow.add(btnNext);

    actions.add(actionRow, BorderLayout.NORTH);
    actions.add(pagingRow, BorderLayout.SOUTH);
    return actions;
  }

  private JSpinner createDateSpinner() {
    JSpinner spinner = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        null,
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
    return spinner;
  }

  private void reloadData() {
    allInvoices.clear();
    allInvoices.addAll(hoaDonThuocBUS.getAllHoaDonThuoc());
    currentPage = 1;
    applyFiltersAndRender();
  }

  private void resetFilters() {
    txtSearch.setText("");
    cbSort.setSelectedItem("Mặc định");
    cbStatusFilter.setSelectedItem("Tất cả trạng thái");
    applyDefaultDateRange();
    currentPage = 1;
    applyFiltersAndRender();
  }

  private void applyDefaultDateRange() {
    spFromDate.setValue(
      Date.from(
        LocalDate.now()
          .minusMonths(6)
          .atStartOfDay(ZoneId.systemDefault())
          .toInstant()
      )
    );
    spToDate.setValue(
      Date.from(
        LocalDate.now()
          .plusMonths(6)
          .atStartOfDay(ZoneId.systemDefault())
          .toInstant()
      )
    );
  }

  private void applyFiltersAndRender() {
    filteredInvoices.clear();

    String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
    LocalDate fromDate = toLocalDate((Date) spFromDate.getValue());
    LocalDate toDate = toLocalDate((Date) spToDate.getValue());
    String statusFilter = String.valueOf(cbStatusFilter.getSelectedItem());

    for (HoaDonThuocDTO invoice : allInvoices) {
      LocalDate invoiceDate =
        invoice.getNgayLap() == null
          ? null
          : invoice.getNgayLap().toLocalDate();
      boolean inDateRange =
        invoiceDate != null &&
        !invoiceDate.isBefore(fromDate) &&
        !invoiceDate.isAfter(toDate);
      if (!inDateRange) {
        continue;
      }

      String maHoaDon = safe(invoice.getMaHoaDon()).toLowerCase(Locale.ROOT);
      String maDonThuoc = safe(invoice.getMaDonThuoc()).toLowerCase(
        Locale.ROOT
      );
      String tenBenhNhan = safe(invoice.getTenBenhNhan()).toLowerCase(
        Locale.ROOT
      );
      String sdt = safe(invoice.getSdtBenhNhan()).toLowerCase(Locale.ROOT);

      boolean matchKeyword =
        keyword.isEmpty() ||
        maHoaDon.contains(keyword) ||
        maDonThuoc.contains(keyword) ||
        tenBenhNhan.contains(keyword) ||
        sdt.contains(keyword);

      if (matchKeyword) {
        if (isStatusMatch(invoice, statusFilter)) {
          filteredInvoices.add(invoice);
        }
      }
    }

    String sort = String.valueOf(cbSort.getSelectedItem());
    if ("Giá tăng dần".equals(sort)) {
      filteredInvoices.sort(
        Comparator.comparingDouble(HoaDonThuocDTO::getTongTien)
      );
    } else if ("Giá giảm dần".equals(sort)) {
      filteredInvoices.sort(
        Comparator.comparingDouble(HoaDonThuocDTO::getTongTien).reversed()
      );
    } else {
      filteredInvoices.sort(
        Comparator.comparing(
          HoaDonThuocDTO::getNgayLap,
          Comparator.nullsLast(Comparator.reverseOrder())
        )
      );
    }

    int totalPages = getTotalPages();
    if (currentPage > totalPages) {
      currentPage = totalPages;
    }
    currentPage = Math.max(1, currentPage);

    renderCurrentPage();
  }

  private void renderCurrentPage() {
    model.setRowCount(0);

    int totalPages = getTotalPages();
    int fromIndex = (currentPage - 1) * PAGE_SIZE;
    int toIndex = Math.min(fromIndex + PAGE_SIZE, filteredInvoices.size());

    if (fromIndex >= filteredInvoices.size()) {
      currentPage = 1;
      fromIndex = 0;
      toIndex = Math.min(PAGE_SIZE, filteredInvoices.size());
    }

    for (int i = fromIndex; i < toIndex; i++) {
      HoaDonThuocDTO invoice = filteredInvoices.get(i);
      model.addRow(
        new Object[] {
          invoice.getMaHoaDon(),
          invoice.getMaDonThuoc(),
          formatDateTime(invoice.getNgayLap()),
          invoice.getTenBenhNhan(),
          invoice.getSdtBenhNhan(),
          invoice.getTongTien(),
          normalizePaymentStatusLabel(invoice.getTrangThaiThanhToan()),
          normalizePickupStatusLabel(invoice.getTrangThaiLayThuoc()),
        }
      );
    }

    lblPageInfo.setText(
      "Trang " +
        currentPage +
        "/" +
        totalPages +
        " (" +
        filteredInvoices.size() +
        " hóa đơn)"
    );
    btnPrev.setEnabled(currentPage > 1);
    btnNext.setEnabled(currentPage < totalPages);
    updateActionButtons();
  }

  private int getTotalPages() {
    return Math.max(
      1,
      (int) Math.ceil(filteredInvoices.size() / (double) PAGE_SIZE)
    );
  }

  private LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  private HoaDonThuocDTO getSelectedInvoice() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    int modelRow = table.convertRowIndexToModel(row);
    String maHoaDon = String.valueOf(model.getValueAt(modelRow, 0));
    for (HoaDonThuocDTO invoice : filteredInvoices) {
      if (maHoaDon.equals(invoice.getMaHoaDon())) {
        return invoice;
      }
    }
    return null;
  }

  private void confirmPayment() {
    HoaDonThuocDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hóa đơn để xác nhận thanh toán.");
      return;
    }

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThaiThanhToan()
    );
    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      selected.getTrangThaiLayThuoc()
    );

    if (StatusNormalizer.DA_THANH_TOAN.equals(paymentStatus)) {
      DialogHelper.info(this, "Hóa đơn này đã thanh toán.");
      return;
    }
    if (StatusNormalizer.HOAN_HOA_DON.equals(paymentStatus)) {
      DialogHelper.warn(this, "Hóa đơn đã ở trạng thái hoàn/hủy.");
      return;
    }
    if (StatusNormalizer.DA_HUY.equals(pickupStatus)) {
      DialogHelper.warn(this, "Hóa đơn đã hủy, không thể xác nhận thanh toán.");
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Xác nhận thanh toán hóa đơn " + selected.getMaHoaDon() + "?"
      )
    ) {
      return;
    }

    String stockMessage = hoaDonThuocBUS.layThongBaoKiemTraTonKhoHoaDon(
      selected.getMaHoaDon()
    );
    if (stockMessage != null && !stockMessage.isBlank()) {
      DialogHelper.warn(
        this,
        stockMessage +
          "\nVui lòng sửa chi tiết hóa đơn trước khi xác nhận thanh toán."
      );
      return;
    }

    if (!hoaDonThuocBUS.xacNhanThanhToanHoaDon(selected.getMaHoaDon())) {
      DialogHelper.error(this, "Xác nhận thanh toán thất bại.");
      return;
    }

    exportInvoicePdfAfterPayment(selected.getMaHoaDon());
    DialogHelper.info(this, "Xác nhận thanh toán thành công.");
    reloadData();
  }

  private void openEditInvoiceDialog() {
    HoaDonThuocDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hóa đơn để chỉnh sửa chi tiết.");
      return;
    }

    if (!hoaDonThuocBUS.coTheSuaHoaDon(selected.getMaHoaDon())) {
      DialogHelper.warn(
        this,
        "Chỉ được sửa chi tiết khi hóa đơn chưa thanh toán và đang chờ lấy thuốc."
      );
      return;
    }

    java.util.List<CTHDThuocDTO> details = cthdThuocBUS.getDetailsByInvoice(
      selected.getMaHoaDon()
    );
    if (details == null || details.isEmpty()) {
      DialogHelper.warn(this, "Hóa đơn chưa có chi tiết thuốc để chỉnh sửa.");
      return;
    }

    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Sửa chi tiết hóa đơn " + selected.getMaHoaDon(),
      true
    );
    dialog.setLayout(new BorderLayout(8, 8));

    JLabel lblHint = new JLabel(
      "Có thể thêm thuốc mới, sửa SL dòng cũ, hoặc xóa dòng bất kỳ trước khi xác nhận thanh toán."
    );
    javax.swing.JTextArea txtNote = new javax.swing.JTextArea(3, 28);
    txtNote.setLineWrap(true);
    txtNote.setWrapStyleWord(true);
    txtNote.setText(safe(selected.getGhiChu()));

    DefaultTableModel editModel = new DefaultTableModel(
      new Object[] {
        "Mã CTHD",
        "Mã thuốc",
        "Tên thuốc",
        "Đơn giá",
        "SL",
        "Tồn hiện tại",
        "Thành tiền",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 4;
      }
    };

    for (CTHDThuocDTO detail : details) {
      ThuocDTO thuoc = thuocBUS.getByMa(detail.getMaThuoc());
      int tonHienTai = thuoc == null ? 0 : Math.max(0, thuoc.getSoLuongTon());
      editModel.addRow(
        new Object[] {
          detail.getMaCTHDThuoc(),
          detail.getMaThuoc(),
          safe(detail.getTenThuoc()),
          detail.getDonGia(),
          detail.getSoLuong(),
          tonHienTai,
          detail.getThanhTien(),
        }
      );
    }

    JTable editTable = new JTable(editModel);
    UIUtils.styleTable(editTable);
    editModel.addTableModelListener(e -> {
      if (e.getFirstRow() < 0 || e.getColumn() != 4) {
        return;
      }
      int row = e.getFirstRow();
      Object rawQty = editModel.getValueAt(row, 4);
      int qty;
      try {
        qty = Integer.parseInt(String.valueOf(rawQty).trim());
      } catch (Exception ex) {
        qty = 0;
      }
      if (qty < 0) {
        qty = 0;
      }
      double donGia = toDouble(editModel.getValueAt(row, 3));
      editModel.setValueAt(qty * donGia, row, 6);
    });

    JPanel top = new JPanel(new BorderLayout(0, 6));
    top.setOpaque(false);
    top.add(lblHint, BorderLayout.NORTH);
    top.add(new JScrollPane(txtNote), BorderLayout.SOUTH);

    JPanel midActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    midActions.setOpaque(false);
    JButton btnAddMedicine = UIUtils.ghostButton("Thêm thuốc mới");
    JButton btnRemoveRow = UIUtils.ghostButton("Xóa dòng đã chọn");
    midActions.add(btnAddMedicine);
    midActions.add(btnRemoveRow);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    bottom.setOpaque(false);
    JButton btnSave = UIUtils.primaryButton("Lưu chi tiết");
    JButton btnClose = UIUtils.ghostButton("Đóng");
    bottom.add(btnClose);
    bottom.add(btnSave);

    btnClose.addActionListener(e -> dialog.dispose());
    btnRemoveRow.addActionListener(e -> {
      int row = editTable.getSelectedRow();
      if (row < 0) {
        DialogHelper.warn(dialog, "Vui lòng chọn dòng cần xóa.");
        return;
      }
      int modelRow = editTable.convertRowIndexToModel(row);
      editModel.removeRow(modelRow);
    });

    btnAddMedicine.addActionListener(e -> {
      ArrayList<ThuocDTO> allMeds = thuocBUS.list();
      if (allMeds == null || allMeds.isEmpty()) {
        DialogHelper.warn(dialog, "Không có thuốc để thêm.");
        return;
      }

      JComboBox<String> cbThuoc = new JComboBox<>();
      for (ThuocDTO thuoc : allMeds) {
        cbThuoc.addItem(thuoc.getMaThuoc() + " - " + safe(thuoc.getTenThuoc()));
      }
      javax.swing.JTextField txtQty = new javax.swing.JTextField("1", 10);

      JPanel form = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(4, 4, 4, 4);
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;

      gbc.gridx = 0;
      gbc.gridy = 0;
      form.add(new JLabel("Thuốc"), gbc);
      gbc.gridx = 1;
      form.add(cbThuoc, gbc);
      gbc.gridx = 0;
      gbc.gridy = 1;
      form.add(new JLabel("Số lượng"), gbc);
      gbc.gridx = 1;
      form.add(txtQty, gbc);

      int result = JOptionPane.showConfirmDialog(
        dialog,
        form,
        "Thêm thuốc vào hóa đơn",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
      );
      if (result != JOptionPane.OK_OPTION) {
        return;
      }

      String selectedText = String.valueOf(cbThuoc.getSelectedItem());
      String maThuoc = extractIdFromLabel(selectedText);
      ThuocDTO thuoc = thuocBUS.getByMa(maThuoc);
      if (thuoc == null) {
        DialogHelper.warn(dialog, "Thuốc không tồn tại.");
        return;
      }

      int qty;
      try {
        qty = Integer.parseInt(txtQty.getText().trim());
      } catch (Exception ex) {
        DialogHelper.warn(dialog, "Số lượng không hợp lệ.");
        return;
      }
      if (qty <= 0) {
        DialogHelper.warn(dialog, "Số lượng phải lớn hơn 0.");
        return;
      }

      for (int i = 0; i < editModel.getRowCount(); i++) {
        String existingMaThuoc = safe(
          String.valueOf(editModel.getValueAt(i, 1))
        );
        if (maThuoc.equalsIgnoreCase(existingMaThuoc)) {
          int oldQty = toInt(editModel.getValueAt(i, 4));
          int newQty = oldQty + qty;
          editModel.setValueAt(newQty, i, 4);
          editModel.setValueAt(newQty * thuoc.getDonGiaBan(), i, 6);
          return;
        }
      }

      editModel.addRow(
        new Object[] {
          "",
          maThuoc,
          safe(thuoc.getTenThuoc()),
          thuoc.getDonGiaBan(),
          qty,
          Math.max(0, thuoc.getSoLuongTon()),
          qty * thuoc.getDonGiaBan(),
        }
      );
    });

    btnSave.addActionListener(e -> {
      ArrayList<CTHDThuocDTO> replacementDetails = new ArrayList<>();
      for (int i = 0; i < editModel.getRowCount(); i++) {
        String maCt = safe(String.valueOf(editModel.getValueAt(i, 0)));
        String maThuoc = safe(String.valueOf(editModel.getValueAt(i, 1)));
        int qty;
        try {
          qty = Integer.parseInt(
            String.valueOf(editModel.getValueAt(i, 4)).trim()
          );
        } catch (Exception ex) {
          DialogHelper.warn(
            dialog,
            "Số lượng tại dòng " + (i + 1) + " không hợp lệ."
          );
          return;
        }

        if (qty <= 0) {
          continue;
        }

        double donGia = toDouble(editModel.getValueAt(i, 3));
        if (maThuoc.isEmpty() || donGia <= 0) {
          DialogHelper.warn(
            dialog,
            "Thông tin thuốc tại dòng " + (i + 1) + " không hợp lệ."
          );
          return;
        }

        CTHDThuocDTO dto = new CTHDThuocDTO();
        dto.setMaCTHDThuoc(maCt.isEmpty() ? null : maCt);
        dto.setMaHoaDon(selected.getMaHoaDon());
        dto.setMaThuoc(maThuoc);
        dto.setSoLuong(qty);
        dto.setDonGia(donGia);
        dto.setThanhTien(qty * donGia);
        dto.setActive(true);
        replacementDetails.add(dto);
      }

      if (replacementDetails.isEmpty()) {
        DialogHelper.warn(dialog, "Hóa đơn phải còn ít nhất 1 dòng thuốc.");
        return;
      }

      if (
        !DialogHelper.confirm(
          dialog,
          "Lưu thay đổi chi tiết hóa đơn " + selected.getMaHoaDon() + "?"
        )
      ) {
        return;
      }

      boolean updated = hoaDonThuocBUS.thayTheChiTietHoaDonTruocThanhToan(
        selected.getMaHoaDon(),
        replacementDetails,
        txtNote.getText().trim()
      );
      if (!updated) {
        DialogHelper.error(
          dialog,
          "Cập nhật chi tiết thất bại. Vui lòng kiểm tra số lượng/tồn kho."
        );
        return;
      }

      DialogHelper.info(dialog, "Đã cập nhật chi tiết hóa đơn.");
      dialog.dispose();
      reloadData();
    });

    dialog.add(top, BorderLayout.NORTH);
    JPanel center = new JPanel(new BorderLayout(0, 8));
    center.setOpaque(false);
    center.add(midActions, BorderLayout.NORTH);
    center.add(new JScrollPane(editTable), BorderLayout.CENTER);
    dialog.add(center, BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setSize(900, 520);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void openCreateInvoiceDialog() {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Tạo hóa đơn từ đơn thuốc",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    top.setOpaque(false);
    CustomTextField txtMaDonThuoc = (CustomTextField) UIUtils.roundedTextField(
      "Nhập mã đơn thuốc",
      20
    );
    JButton btnLoad = UIUtils.ghostButton("Tải đơn thuốc");
    top.add(new JLabel("Mã đơn thuốc"));
    top.add(txtMaDonThuoc);
    top.add(btnLoad);

    JPanel info = new JPanel(new GridBagLayout());
    info.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;

    JLabel lblPatient = new JLabel("Bệnh nhân: ");
    JLabel lblPhone = new JLabel("SĐT: ");
    JLabel lblTotal = new JLabel("Tổng tiền: 0");
    JTextArea txtNote = new JTextArea(3, 30);
    txtNote.setLineWrap(true);
    txtNote.setWrapStyleWord(true);

    gbc.gridx = 0;
    gbc.gridy = 0;
    info.add(lblPatient, gbc);
    gbc.gridy = 1;
    info.add(lblPhone, gbc);
    gbc.gridy = 2;
    info.add(lblTotal, gbc);
    gbc.gridy = 3;
    info.add(new JLabel("Ghi chú hóa đơn"), gbc);
    gbc.gridy = 4;
    info.add(new JScrollPane(txtNote), gbc);

    DefaultTableModel detailModel = new DefaultTableModel(
      new Object[] {
        "Mã thuốc",
        "Tên thuốc",
        "Đơn vị",
        "Số lượng",
        "Đơn giá",
        "Thành tiền",
        "Hướng dẫn",
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

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    bottom.setOpaque(false);
    JButton btnCancel = UIUtils.ghostButton("Đóng");
    JButton btnCreate = UIUtils.primaryButton("Hoàn thành tạo hóa đơn");
    btnCreate.setEnabled(false);
    bottom.add(btnCancel);
    bottom.add(btnCreate);

    final PrescriptionPreview[] currentPreview = new PrescriptionPreview[] {
      null,
    };

    btnLoad.addActionListener(e -> {
      PrescriptionPreview preview = loadPrescriptionPreview(
        txtMaDonThuoc.getText().trim(),
        detailModel,
        lblPatient,
        lblPhone,
        lblTotal
      );
      currentPreview[0] = preview;
      btnCreate.setEnabled(preview != null);
    });

    btnCreate.addActionListener(e -> {
      PrescriptionPreview preview = currentPreview[0];
      if (preview == null) {
        DialogHelper.warn(
          dialog,
          "Vui lòng tải đơn thuốc trước khi tạo hóa đơn."
        );
        return;
      }

      if (hasActiveInvoiceForPrescription(preview.maDonThuoc)) {
        DialogHelper.warn(
          dialog,
          "Đơn thuốc này đã có hóa đơn đang hoạt động. Vui lòng kiểm tra lại."
        );
        return;
      }

      HoaDonThuocDTO hoaDon = new HoaDonThuocDTO();
      hoaDon.setMaDonThuoc(preview.maDonThuoc);
      hoaDon.setTenBenhNhan(preview.tenBenhNhan);
      hoaDon.setSdtBenhNhan(preview.sdtBenhNhan);
      hoaDon.setGhiChu(txtNote.getText().trim());

      if (!hoaDonThuocBUS.taoHoaDonTuDonThuoc(hoaDon, preview.details)) {
        DialogHelper.error(dialog, "Tạo hóa đơn từ đơn thuốc thất bại.");
        return;
      }

      DialogHelper.info(
        dialog,
        "Tạo hóa đơn thành công. Mặc định trạng thái: CHUA_THANH_TOAN / CHO_LAY."
      );
      dialog.dispose();
      reloadData();
    });

    btnCancel.addActionListener(e -> dialog.dispose());

    JPanel center = new JPanel(new BorderLayout(8, 8));
    center.setOpaque(false);
    center.add(info, BorderLayout.NORTH);
    center.add(new JScrollPane(detailTable), BorderLayout.CENTER);

    dialog.add(top, BorderLayout.NORTH);
    dialog.add(center, BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setSize(980, 620);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private PrescriptionPreview loadPrescriptionPreview(
    String maDonThuoc,
    DefaultTableModel detailModel,
    JLabel lblPatient,
    JLabel lblPhone,
    JLabel lblTotal
  ) {
    detailModel.setRowCount(0);
    lblPatient.setText("Bệnh nhân: ");
    lblPhone.setText("SĐT: ");
    lblTotal.setText("Tổng tiền: 0");

    if (maDonThuoc == null || maDonThuoc.isEmpty()) {
      DialogHelper.warn(this, "Vui lòng nhập mã đơn thuốc.");
      return null;
    }

    DonThuocDTO donThuoc = donThuocBUS.searchTheoMa(maDonThuoc);
    if (donThuoc == null) {
      DialogHelper.warn(this, "Không tìm thấy đơn thuốc " + maDonThuoc + ".");
      return null;
    }

    ArrayList<CTDonThuocDTO> chiTietDonThuoc = ctDonThuocBUS.getByMaDonThuoc(
      maDonThuoc
    );
    if (chiTietDonThuoc == null || chiTietDonThuoc.isEmpty()) {
      DialogHelper.warn(this, "Đơn thuốc chưa có chi tiết thuốc.");
      return null;
    }

    HoSoBenhAnDTO hoSo = hoSoBenhAnBUS.getByMaHoSo(donThuoc.getMaHoSo());
    String tenBenhNhan =
      hoSo == null || safe(hoSo.getHoTen()).isEmpty()
        ? "Bệnh nhân"
        : hoSo.getHoTen();
    String sdtBenhNhan = hoSo == null ? "" : safe(hoSo.getSoDienThoai());

    ArrayList<CTHDThuocDTO> details = new ArrayList<>();
    double tongTien = 0;

    for (CTDonThuocDTO ct : chiTietDonThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc == null) {
        continue;
      }
      double donGia = thuoc.getDonGiaBan();
      if (donGia <= 0) {
        DialogHelper.warn(
          this,
          "Thuốc " + thuoc.getMaThuoc() + " chưa có giá bán hợp lệ."
        );
        return null;
      }

      CTHDThuocDTO cthd = new CTHDThuocDTO();
      cthd.setMaThuoc(thuoc.getMaThuoc());
      cthd.setSoLuong(ct.getSoluong());
      cthd.setDonGia(donGia);
      cthd.setGhiChu(buildInstruction(ct));
      details.add(cthd);

      double thanhTien = ct.getSoluong() * donGia;
      tongTien += thanhTien;

      detailModel.addRow(
        new Object[] {
          thuoc.getMaThuoc(),
          thuoc.getTenThuoc(),
          thuoc.getDonViTinh(),
          ct.getSoluong(),
          donGia,
          thanhTien,
          buildInstruction(ct),
        }
      );
    }

    if (details.isEmpty()) {
      DialogHelper.warn(
        this,
        "Không tải được chi tiết thuốc hợp lệ từ đơn thuốc."
      );
      return null;
    }

    lblPatient.setText("Bệnh nhân: " + tenBenhNhan);
    lblPhone.setText("SĐT: " + sdtBenhNhan);
    lblTotal.setText("Tổng tiền: " + tongTien);

    PrescriptionPreview preview = new PrescriptionPreview();
    preview.maDonThuoc = maDonThuoc;
    preview.tenBenhNhan = tenBenhNhan;
    preview.sdtBenhNhan = sdtBenhNhan;
    preview.details = details;
    return preview;
  }

  private String buildInstruction(CTDonThuocDTO ct) {
    String lieuDung = safe(ct.getLieuDung());
    String cachDung = safe(ct.getCachDung());
    if (!lieuDung.isEmpty() && !cachDung.isEmpty()) {
      return lieuDung + " | " + cachDung;
    }
    if (!lieuDung.isEmpty()) {
      return lieuDung;
    }
    return cachDung;
  }

  private boolean hasActiveInvoiceForPrescription(String maDonThuoc) {
    for (HoaDonThuocDTO invoice : hoaDonThuocBUS.getAllHoaDonThuoc()) {
      if (!maDonThuoc.equalsIgnoreCase(safe(invoice.getMaDonThuoc()))) {
        continue;
      }
      String pickupStatus = StatusNormalizer.normalizePickupStatus(
        invoice.getTrangThaiLayThuoc()
      );
      if (!StatusNormalizer.DA_HUY.equals(pickupStatus)) {
        return true;
      }
    }
    return false;
  }

  private void confirmPickup() {
    HoaDonThuocDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hóa đơn để xác nhận giao thuốc.");
      return;
    }

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThaiThanhToan()
    );
    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      selected.getTrangThaiLayThuoc()
    );

    if (!StatusNormalizer.DA_THANH_TOAN.equals(paymentStatus)) {
      DialogHelper.warn(this, "Hóa đơn chưa thanh toán, không thể giao thuốc.");
      return;
    }
    if (!StatusNormalizer.CHO_LAY.equals(pickupStatus)) {
      DialogHelper.warn(
        this,
        "Chỉ giao thuốc cho hóa đơn ở trạng thái chờ lấy."
      );
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Xác nhận giao thuốc cho hóa đơn " + selected.getMaHoaDon() + "?"
      )
    ) {
      return;
    }

    if (!hoaDonThuocBUS.xacNhanGiaoThuoc(selected.getMaHoaDon())) {
      DialogHelper.error(
        this,
        "Xác nhận giao thuốc thất bại. Kiểm tra tồn kho và chi tiết hóa đơn."
      );
      return;
    }

    DialogHelper.info(this, "Xác nhận giao thuốc thành công và đã trừ kho.");
    reloadData();
  }

  private void cancelInvoice() {
    HoaDonThuocDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hóa đơn để hủy.");
      return;
    }

    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      selected.getTrangThaiLayThuoc()
    );
    if (StatusNormalizer.DA_HOAN_THANH.equals(pickupStatus)) {
      DialogHelper.warn(this, "Hóa đơn đã giao thuốc, không thể hủy.");
      return;
    }
    if (StatusNormalizer.DA_HUY.equals(pickupStatus)) {
      DialogHelper.info(this, "Hóa đơn đã hủy trước đó.");
      return;
    }

    if (
      !DialogHelper.confirm(this, "Hủy hóa đơn " + selected.getMaHoaDon() + "?")
    ) {
      return;
    }

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThaiThanhToan()
    );

    if (!hoaDonThuocBUS.huyHoaDonThuoc(selected.getMaHoaDon())) {
      DialogHelper.error(this, "Hủy hóa đơn thất bại.");
      return;
    }

    if (StatusNormalizer.DA_THANH_TOAN.equals(paymentStatus)) {
      DialogHelper.info(
        this,
        "Hủy hóa đơn thành công. Trạng thái: HOAN_HOA_DON + DA_HUY."
      );
    } else {
      DialogHelper.info(
        this,
        "Hủy hóa đơn thành công. Trạng thái: CHUA_THANH_TOAN + DA_HUY."
      );
    }
    reloadData();
  }

  private void updateActionButtons() {
    if (
      btnConfirmPayment == null ||
      btnEditInvoice == null ||
      btnConfirmPickup == null ||
      btnCancelInvoice == null ||
      btnViewLotIssueTrace == null
    ) {
      return;
    }

    HoaDonThuocDTO selected = getSelectedInvoice();
    if (selected == null) {
      btnConfirmPayment.setEnabled(false);
      btnEditInvoice.setEnabled(false);
      btnConfirmPickup.setEnabled(false);
      btnCancelInvoice.setEnabled(false);
      btnViewLotIssueTrace.setEnabled(false);
      return;
    }

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThaiThanhToan()
    );
    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      selected.getTrangThaiLayThuoc()
    );

    boolean canConfirmPayment =
      StatusNormalizer.CHUA_THANH_TOAN.equals(paymentStatus) &&
      !StatusNormalizer.DA_HUY.equals(pickupStatus);
    boolean canEdit =
      StatusNormalizer.CHUA_THANH_TOAN.equals(paymentStatus) &&
      StatusNormalizer.CHO_LAY.equals(pickupStatus);
    boolean canConfirmPickup =
      StatusNormalizer.DA_THANH_TOAN.equals(paymentStatus) &&
      StatusNormalizer.CHO_LAY.equals(pickupStatus);
    boolean canCancel =
      !StatusNormalizer.DA_HUY.equals(pickupStatus) &&
      !StatusNormalizer.DA_HOAN_THANH.equals(pickupStatus);

    btnConfirmPayment.setEnabled(canConfirmPayment);
    btnEditInvoice.setEnabled(canEdit);
    btnConfirmPickup.setEnabled(canConfirmPickup);
    btnCancelInvoice.setEnabled(canCancel);
    btnViewLotIssueTrace.setEnabled(true);

    if (!coQuyenSua) {
      btnEditInvoice.setEnabled(false);
    }
    if (!coQuyenXacNhanThanhToan) {
      btnConfirmPayment.setEnabled(false);
    }
    if (!coQuyenXacNhanGiaoThuoc) {
      btnConfirmPickup.setEnabled(false);
    }
    if (!coQuyenXoa) {
      btnCancelInvoice.setEnabled(false);
    }
    if (!coQuyenXem || !coQuyenXemXuatTheoLo) {
      btnViewLotIssueTrace.setEnabled(false);
    }
  }

  private void apDungPhanQuyenHanhDong() {
    coQuyenXem = Session.coMotTrongCacQuyen("HOADONTHUOC_XEM");
    coQuyenThem = Session.coMotTrongCacQuyen("HOADONTHUOC_THEM");
    coQuyenSua = Session.coMotTrongCacQuyen("HOADONTHUOC_SUA");
    coQuyenXoa = Session.coMotTrongCacQuyen("HOADONTHUOC_XOA");
    coQuyenXacNhanThanhToan = Session.coMotTrongCacQuyen(
      "HOADONTHUOC_XAC_NHAN_THANH_TOAN"
    );
    coQuyenXacNhanGiaoThuoc = Session.coMotTrongCacQuyen(
      "HOADONTHUOC_XAC_NHAN_GIAO_THUOC"
    );
    coQuyenXemXuatTheoLo = Session.coMotTrongCacQuyen(
      "HOADONTHUOC_XEM_XUAT_THEO_LO"
    );

    if (btnCreateFromPrescription != null) {
      btnCreateFromPrescription.setVisible(coQuyenThem);
    }
    if (btnConfirmPayment != null) {
      btnConfirmPayment.setVisible(coQuyenXacNhanThanhToan);
    }
    if (btnEditInvoice != null) {
      btnEditInvoice.setVisible(coQuyenSua);
    }
    if (btnConfirmPickup != null) {
      btnConfirmPickup.setVisible(coQuyenXacNhanGiaoThuoc);
    }
    if (btnCancelInvoice != null) {
      btnCancelInvoice.setVisible(coQuyenXoa);
    }
    if (btnViewLotIssueTrace != null) {
      btnViewLotIssueTrace.setVisible(coQuyenXem && coQuyenXemXuatTheoLo);
    }
    if (btnReload != null) {
      btnReload.setVisible(coQuyenXem);
    }
    if (table != null) {
      table.setEnabled(coQuyenXem);
    }
  }

  private void openLotIssueTraceDialog() {
    HoaDonThuocDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(
        this,
        "Vui lòng chọn hóa đơn để xem lịch sử xuất theo lô."
      );
      return;
    }

    java.util.List<XuatThuocTheoLoDTO> traceRows =
      hoaDonThuocBUS.layLichSuXuatTheoLoTheoHoaDon(selected.getMaHoaDon());
    if (traceRows == null || traceRows.isEmpty()) {
      DialogHelper.info(
        this,
        "Hóa đơn này chưa có dữ liệu xuất theo lô. Dữ liệu sẽ xuất hiện sau khi giao thuốc hoàn tất."
      );
      return;
    }

    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Chi tiết xuất theo lô - " + selected.getMaHoaDon(),
      true
    );
    dialog.setLayout(new BorderLayout(8, 8));

    JLabel lblSummary = new JLabel(
      "Hóa đơn: " +
        selected.getMaHoaDon() +
        " | Bệnh nhân: " +
        safe(selected.getTenBenhNhan()) +
        " | Số dòng: " +
        traceRows.size()
    );

    DefaultTableModel groupedModel = new DefaultTableModel(
      new Object[] {
        "Mã thuốc",
        "Tên thuốc",
        "Tổng SL xuất",
        "Số lô",
        "HSD gần nhất",
        "HSD xa nhất",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    Map<String, LotIssueGroupSummary> groupedByMedicine = new LinkedHashMap<>();
    for (XuatThuocTheoLoDTO row : traceRows) {
      String maThuoc = safe(row.getMaThuoc());
      LotIssueGroupSummary group = groupedByMedicine.computeIfAbsent(
        maThuoc,
        key -> {
          LotIssueGroupSummary summary = new LotIssueGroupSummary();
          summary.maThuoc = key;
          ThuocDTO thuoc = thuocBUS.getByMa(key);
          summary.tenThuoc = thuoc == null ? "" : safe(thuoc.getTenThuoc());
          return summary;
        }
      );
      group.totalQty += Math.max(0, row.getSoLuongXuat());
      group.lotCount++;
      LocalDate hsd = row.getHanSuDung();
      if (hsd != null) {
        if (group.nearestExpiry == null || hsd.isBefore(group.nearestExpiry)) {
          group.nearestExpiry = hsd;
        }
        if (group.farthestExpiry == null || hsd.isAfter(group.farthestExpiry)) {
          group.farthestExpiry = hsd;
        }
      }
    }

    for (LotIssueGroupSummary group : groupedByMedicine.values()) {
      groupedModel.addRow(
        new Object[] {
          group.maThuoc,
          group.tenThuoc,
          group.totalQty,
          group.lotCount,
          group.nearestExpiry == null ? "" : group.nearestExpiry.toString(),
          group.farthestExpiry == null ? "" : group.farthestExpiry.toString(),
        }
      );
    }

    DefaultTableModel traceModel = new DefaultTableModel(
      new Object[] {
        "Mã CTHD",
        "Mã thuốc",
        "Tên thuốc",
        "Mã CTPN",
        "Số lô",
        "HSD",
        "SL xuất",
        "Ngày xuất",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    int totalQty = 0;
    for (XuatThuocTheoLoDTO row : traceRows) {
      ThuocDTO thuoc = thuocBUS.getByMa(row.getMaThuoc());
      totalQty += Math.max(0, row.getSoLuongXuat());
      traceModel.addRow(
        new Object[] {
          row.getMaCTHDThuoc(),
          row.getMaThuoc(),
          thuoc == null ? "" : safe(thuoc.getTenThuoc()),
          row.getMaCTPN(),
          row.getSoLo(),
          row.getHanSuDung(),
          row.getSoLuongXuat(),
          formatDateTime(row.getNgayXuat()),
        }
      );
    }

    JTable groupedTable = new JTable(groupedModel);
    UIUtils.styleTable(groupedTable);

    JTable traceTable = new JTable(traceModel);
    UIUtils.styleTable(traceTable);
    traceTable.setDefaultRenderer(
      Object.class,
      new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
          JTable table,
          Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column
        ) {
          Component cell = super.getTableCellRendererComponent(
            table,
            value,
            isSelected,
            hasFocus,
            row,
            column
          );

          if (isSelected) {
            return cell;
          }

          LocalDate hsd = getTraceRowExpiryDate(table, row);
          if (hsd == null) {
            cell.setBackground(Color.WHITE);
            return cell;
          }

          LocalDate today = LocalDate.now();
          if (!hsd.isAfter(today)) {
            cell.setBackground(new Color(255, 217, 217));
          } else if (!hsd.isAfter(today.plusDays(30))) {
            cell.setBackground(new Color(255, 242, 204));
          } else {
            cell.setBackground(Color.WHITE);
          }
          return cell;
        }
      }
    );

    traceTable
      .getColumnModel()
      .getColumn(5)
      .setCellRenderer(
        new DefaultTableCellRenderer() {
          @Override
          public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
          ) {
            LocalDate hsd = getTraceRowExpiryDate(table, row);
            Object displayValue = value;
            if (hsd != null) {
              displayValue = hsd.toString();
            }
            return super.getTableCellRendererComponent(
              table,
              displayValue,
              isSelected,
              hasFocus,
              row,
              column
            );
          }
        }
      );

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);
    top.add(lblSummary, BorderLayout.WEST);
    top.add(new JLabel("Tổng SL xuất: " + totalQty), BorderLayout.EAST);

    JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    legend.setOpaque(false);
    JLabel lblExpired = new JLabel("Đỏ nhạt: đã hết hạn");
    lblExpired.setOpaque(true);
    lblExpired.setBackground(new Color(255, 217, 217));
    JLabel lblNearExpiry = new JLabel("Vàng nhạt: hết hạn trong 30 ngày");
    lblNearExpiry.setOpaque(true);
    lblNearExpiry.setBackground(new Color(255, 242, 204));
    legend.add(lblExpired);
    legend.add(lblNearExpiry);

    JPanel center = new JPanel(new BorderLayout(0, 8));
    center.setOpaque(false);
    center.add(legend, BorderLayout.NORTH);

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.VERTICAL_SPLIT,
      new JScrollPane(groupedTable),
      new JScrollPane(traceTable)
    );
    splitPane.setResizeWeight(0.3);
    splitPane.setContinuousLayout(true);
    center.add(splitPane, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    bottom.setOpaque(false);
    JButton btnExportCsv = UIUtils.primaryButton("Export CSV");
    btnExportCsv.addActionListener(e ->
      exportTraceTableToCsv(traceModel, selected.getMaHoaDon())
    );
    JButton btnClose = UIUtils.ghostButton("Đóng");
    btnClose.addActionListener(e -> dialog.dispose());
    bottom.add(btnExportCsv);
    bottom.add(btnClose);

    dialog.add(top, BorderLayout.NORTH);
    dialog.add(center, BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setSize(980, 620);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private LocalDate getTraceRowExpiryDate(JTable table, int viewRow) {
    int modelRow = table.convertRowIndexToModel(viewRow);
    Object raw = table.getModel().getValueAt(modelRow, 5);
    if (raw instanceof LocalDate) {
      return (LocalDate) raw;
    }
    if (raw == null) {
      return null;
    }
    String text = raw.toString().trim();
    if (text.isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(text);
    } catch (Exception ex) {
      return null;
    }
  }

  private void exportTraceTableToCsv(
    DefaultTableModel traceModel,
    String maHoaDon
  ) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Chọn nơi lưu file CSV");
    chooser.setFileFilter(
      new FileNameExtensionFilter("CSV files (*.csv)", "csv")
    );
    chooser.setSelectedFile(
      new File("xuat_theo_lo_" + safe(maHoaDon) + ".csv")
    );

    int option = chooser.showSaveDialog(this);
    if (option != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File file = chooser.getSelectedFile();
    if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
      file = new File(file.getAbsolutePath() + ".csv");
    }

    try (
      BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(file),
          StandardCharsets.UTF_8
        )
      )
    ) {
      writer.write("\uFEFF");

      for (int c = 0; c < traceModel.getColumnCount(); c++) {
        if (c > 0) {
          writer.write(',');
        }
        writer.write(escapeCsv(traceModel.getColumnName(c)));
      }
      writer.newLine();

      for (int r = 0; r < traceModel.getRowCount(); r++) {
        for (int c = 0; c < traceModel.getColumnCount(); c++) {
          if (c > 0) {
            writer.write(',');
          }
          Object value = traceModel.getValueAt(r, c);
          writer.write(escapeCsv(value == null ? "" : String.valueOf(value)));
        }
        writer.newLine();
      }
    } catch (IOException ex) {
      DialogHelper.error(this, "Xuất CSV thất bại: " + ex.getMessage());
      return;
    }

    DialogHelper.info(this, "Đã xuất CSV: " + file.getAbsolutePath());
  }

  private String escapeCsv(String text) {
    String normalized = text == null ? "" : text;
    String escaped = normalized.replace("\"", "\"\"");
    return '"' + escaped + '"';
  }

  private static class LotIssueGroupSummary {

    private String maThuoc;
    private String tenThuoc;
    private int totalQty;
    private int lotCount;
    private LocalDate nearestExpiry;
    private LocalDate farthestExpiry;
  }

  private boolean isStatusMatch(HoaDonThuocDTO invoice, String statusFilter) {
    if (
      statusFilter == null ||
      statusFilter.trim().isEmpty() ||
      "Tất cả trạng thái".equals(statusFilter)
    ) {
      return true;
    }

    String paymentStatus = StatusNormalizer.normalizePaymentStatus(
      invoice.getTrangThaiThanhToan()
    );
    String pickupStatus = StatusNormalizer.normalizePickupStatus(
      invoice.getTrangThaiLayThuoc()
    );

    if ("CHO_XAC_NHAN_THANH_TOAN".equals(statusFilter)) {
      return (
        StatusNormalizer.CHUA_THANH_TOAN.equals(paymentStatus) &&
        StatusNormalizer.CHO_LAY.equals(pickupStatus)
      );
    }
    if ("DA_THANH_TOAN_CHO_LAY".equals(statusFilter)) {
      return (
        StatusNormalizer.DA_THANH_TOAN.equals(paymentStatus) &&
        StatusNormalizer.CHO_LAY.equals(pickupStatus)
      );
    }
    if ("DA_GIAO_THUOC".equals(statusFilter)) {
      return StatusNormalizer.DA_HOAN_THANH.equals(pickupStatus);
    }
    if ("DA_HUY_CHUA_THANH_TOAN".equals(statusFilter)) {
      return (
        StatusNormalizer.DA_HUY.equals(pickupStatus) &&
        StatusNormalizer.CHUA_THANH_TOAN.equals(paymentStatus)
      );
    }
    if ("DA_HOAN_TIEN".equals(statusFilter)) {
      return (
        StatusNormalizer.DA_HUY.equals(pickupStatus) &&
        StatusNormalizer.HOAN_HOA_DON.equals(paymentStatus)
      );
    }

    return true;
  }

  private double toDouble(Object value) {
    if (value == null) {
      return 0;
    }
    try {
      return Double.parseDouble(String.valueOf(value).trim());
    } catch (Exception ex) {
      return 0;
    }
  }

  private int toInt(Object value) {
    if (value == null) {
      return 0;
    }
    try {
      return Integer.parseInt(String.valueOf(value).trim());
    } catch (Exception ex) {
      return 0;
    }
  }

  private String extractIdFromLabel(String text) {
    if (text == null) {
      return "";
    }
    int idx = text.indexOf(" - ");
    return idx > 0 ? text.substring(0, idx).trim() : text.trim();
  }

  private void exportInvoicePdfAfterPayment(String maHoaDon) {
    HoaDonThuocDTO invoice = hoaDonThuocBUS.getHoaDonThuocDetail(maHoaDon);
    if (invoice == null) {
      return;
    }
    java.util.List<CTHDThuocDTO> details = cthdThuocBUS.getDetailsByInvoice(
      maHoaDon
    );
    if (details == null || details.isEmpty()) {
      return;
    }

    File outputDir = new File("hoa_don_ban_thuoc");
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
    String timestamp = java.time.format.DateTimeFormatter.ofPattern(
      "yyyyMMdd_HHmmss"
    ).format(LocalDateTime.now());
    File file = new File(
      outputDir,
      "HoaDonThuoc_" + maHoaDon + "_" + timestamp + ".pdf"
    );

    try {
      Document document = new Document();
      PdfWriter.getInstance(document, new FileOutputStream(file));
      document.open();
      document.add(new Paragraph("HOA DON BAN THUOC"));
      document.add(new Paragraph("Ma hoa don: " + safe(invoice.getMaHoaDon())));
      document.add(
        new Paragraph("Ten benh nhan: " + safe(invoice.getTenBenhNhan()))
      );
      document.add(
        new Paragraph("So dien thoai: " + safe(invoice.getSdtBenhNhan()))
      );
      document.add(
        new Paragraph("Ngay lap: " + formatDateTime(invoice.getNgayLap()))
      );
      document.add(
        new Paragraph(
          "Trang thai thanh toan: " +
            normalizePaymentStatusLabel(invoice.getTrangThaiThanhToan())
        )
      );
      document.add(new Paragraph("----------------------------------------"));
      for (CTHDThuocDTO detail : details) {
        String line =
          safe(detail.getMaThuoc()) +
          " - " +
          safe(detail.getTenThuoc()) +
          " | Don gia: " +
          detail.getDonGia() +
          " | SL: " +
          detail.getSoLuong() +
          " | Thanh tien: " +
          detail.getThanhTien();
        document.add(new Paragraph(line));
      }
      document.add(new Paragraph("----------------------------------------"));
      document.add(new Paragraph("Tong tien: " + invoice.getTongTien()));
      document.close();
      DialogHelper.info(this, "Đã in lại hóa đơn: " + file.getAbsolutePath());
    } catch (Exception ex) {
      DialogHelper.error(this, "In lại hóa đơn thất bại: " + ex.getMessage());
    }
  }

  private String normalizePaymentStatusLabel(String raw) {
    String status = StatusNormalizer.normalizePaymentStatus(raw);
    if (StatusNormalizer.CHUA_THANH_TOAN.equals(status)) {
      return "CHUA_THANH_TOAN";
    }
    if (StatusNormalizer.DA_THANH_TOAN.equals(status)) {
      return "DA_THANH_TOAN";
    }
    if (StatusNormalizer.HOAN_HOA_DON.equals(status)) {
      return "HOAN_HOA_DON";
    }
    return status;
  }

  private String normalizePickupStatusLabel(String raw) {
    String status = StatusNormalizer.normalizePickupStatus(raw);
    if (StatusNormalizer.CHO_LAY.equals(status)) {
      return "CHO_LAY";
    }
    if (StatusNormalizer.DA_HOAN_THANH.equals(status)) {
      return "DA_HOAN_THANH";
    }
    if (StatusNormalizer.DA_HUY.equals(status)) {
      return "DA_HUY";
    }
    return status;
  }

  private String formatDateTime(LocalDateTime dateTime) {
    if (dateTime == null) {
      return "";
    }
    return DATE_TIME_FORMATTER.format(dateTime);
  }

  private String safe(String value) {
    return value == null ? "" : value;
  }

  private static class PrescriptionPreview {

    private String maDonThuoc;
    private String tenBenhNhan;
    private String sdtBenhNhan;
    private ArrayList<CTHDThuocDTO> details;
  }
}
