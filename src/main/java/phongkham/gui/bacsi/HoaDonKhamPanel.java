package phongkham.gui.bacsi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.HoaDonKhamBUS;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;

public class HoaDonKhamPanel extends BasePanel {

  private final HoaDonKhamBUS hoaDonKhamBUS = new HoaDonKhamBUS();
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã hóa đơn",
      "Mã hồ sơ",
      "Mã gói",
      "Ngày thanh toán",
      "Tổng tiền",
      "Hình thức",
      "Trạng thái",
    },
    0
  );
  private final ArrayList<HoaDonKhamDTO> filteredInvoices = new ArrayList<>();

  private JTable table;
  private CustomTextField txtMaTim;
  private CustomTextField txtGoiTim;
  private JSpinner spFrom;
  private JSpinner spTo;
  private JComboBox<Integer> cbPageSize;
  private JButton btnPrev;
  private JButton btnNext;
  private JButton btnXacNhan;
  private JButton btnHuy;
  private JButton btnLoc;
  private JButton btnXoaLoc;
  private JButton btnTaiLai;
  private javax.swing.JLabel lblPageInfo;
  private int currentPage = 1;
  private boolean coQuyenXem = true;
  private boolean coQuyenSua = true;
  private boolean coQuyenHuy = true;

  @Override
  protected void init() {
    add(buildActionsTop(), BorderLayout.SOUTH);

    add(
      UIUtils.createSection("Danh sách hóa đơn khám", buildTable()),
      BorderLayout.CENTER
    );
    applyFilterAndRender();
  }

  private JScrollPane buildTable() {
    table = new JTable(model);
    UIUtils.styleTable(table);
    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
          updateActionButtons();
        }
      });
    return new JScrollPane(table);
  }

  private JPanel buildActionsTop() {
    JPanel wrapper = new JPanel(new BorderLayout(0, 8));
    wrapper.setOpaque(false);

    JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    filterRow.setOpaque(false);
    txtMaTim = (CustomTextField) UIUtils.roundedTextField(
      "Mã hóa đơn hoặc mã hồ sơ",
      16
    );
    txtGoiTim = (CustomTextField) UIUtils.roundedTextField("Mã gói", 10);
    spFrom = createDateSpinner();
    spTo = createDateSpinner();
    spFrom.setValue(
      Date.from(
        LocalDate.now()
          .minusYears(1)
          .atStartOfDay(ZoneId.systemDefault())
          .toInstant()
      )
    );
    spTo.setValue(new Date());
    cbPageSize = new JComboBox<>(new Integer[] { 10, 20, 30, 50 });
    btnLoc = UIUtils.primaryButton("Lọc");
    btnXoaLoc = UIUtils.ghostButton("Xóa lọc");

    btnLoc.addActionListener(e -> {
      currentPage = 1;
      applyFilterAndRender();
    });
    btnXoaLoc.addActionListener(e -> resetFilters());
    txtMaTim.addActionListener(e -> {
      currentPage = 1;
      applyFilterAndRender();
    });
    txtGoiTim.addActionListener(e -> {
      currentPage = 1;
      applyFilterAndRender();
    });
    cbPageSize.addActionListener(e -> {
      currentPage = 1;
      renderCurrentPage();
    });

    filterRow.add(txtMaTim);
    filterRow.add(txtGoiTim);
    filterRow.add(new javax.swing.JLabel("Từ"));
    filterRow.add(spFrom);
    filterRow.add(new javax.swing.JLabel("Đến"));
    filterRow.add(spTo);
    filterRow.add(new javax.swing.JLabel("/trang"));
    filterRow.add(cbPageSize);
    filterRow.add(btnLoc);
    filterRow.add(btnXoaLoc);

    JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    actionRow.setOpaque(false);
    btnXacNhan = UIUtils.primaryButton("Xác nhận thanh toán");
    btnHuy = UIUtils.ghostButton("Hủy hóa đơn");
    btnTaiLai = UIUtils.ghostButton("Tải lại");
    btnPrev = UIUtils.ghostButton("< Trước");
    btnNext = UIUtils.ghostButton("Sau >");
    lblPageInfo = new javax.swing.JLabel("Trang 1/1");

    btnXacNhan.addActionListener(e -> confirmSelectedInvoice());
    btnHuy.addActionListener(e -> cancelSelectedInvoice());
    btnTaiLai.addActionListener(e -> {
      currentPage = 1;
      applyFilterAndRender();
    });
    btnPrev.addActionListener(e -> {
      currentPage = Math.max(1, currentPage - 1);
      renderCurrentPage();
    });
    btnNext.addActionListener(e -> {
      currentPage++;
      renderCurrentPage();
    });

    actionRow.add(lblPageInfo);
    actionRow.add(btnPrev);
    actionRow.add(btnNext);
    actionRow.add(btnXacNhan);
    actionRow.add(btnHuy);
    actionRow.add(btnTaiLai);

    wrapper.add(filterRow, BorderLayout.NORTH);
    wrapper.add(actionRow, BorderLayout.SOUTH);

    apDungPhanQuyenHanhDong();
    return wrapper;
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

  private void applyFilterAndRender() {
    filteredInvoices.clear();
    filteredInvoices.addAll(
      hoaDonKhamBUS.filterForView(
        txtMaTim.getText(),
        txtGoiTim.getText(),
        toLocalDate((Date) spFrom.getValue()),
        toLocalDate((Date) spTo.getValue())
      )
    );

    int totalPages = getTotalPages();
    if (currentPage > totalPages) {
      currentPage = totalPages;
    }
    currentPage = Math.max(1, currentPage);
    renderCurrentPage();
  }

  private void renderCurrentPage() {
    model.setRowCount(0);

    int pageSize = getPageSize();
    int totalPages = getTotalPages();
    int fromIndex = (currentPage - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, filteredInvoices.size());

    if (fromIndex >= filteredInvoices.size()) {
      fromIndex = 0;
      toIndex = Math.min(pageSize, filteredInvoices.size());
      currentPage = 1;
    }

    for (int i = fromIndex; i < toIndex; i++) {
      HoaDonKhamDTO hoaDon = filteredInvoices.get(i);
      model.addRow(
        new Object[] {
          hoaDon.getMaHDKham(),
          hoaDon.getMaHoSo(),
          hoaDon.getMaGoi(),
          hoaDon.getNgayThanhToan(),
          hoaDon.getTongTien(),
          hoaDon.getHinhThucThanhToan(),
          hoaDon.getTrangThai(),
        }
      );
    }

    if (lblPageInfo != null) {
      lblPageInfo.setText(
        "Trang " +
          currentPage +
          "/" +
          totalPages +
          " (" +
          filteredInvoices.size() +
          " bản ghi)"
      );
    }
    if (btnPrev != null) {
      btnPrev.setEnabled(currentPage > 1);
    }
    if (btnNext != null) {
      btnNext.setEnabled(currentPage < totalPages);
    }
    updateActionButtons();
  }

  private int getPageSize() {
    Integer selected =
      cbPageSize == null ? null : (Integer) cbPageSize.getSelectedItem();
    return selected == null ? 10 : selected;
  }

  private int getTotalPages() {
    int size = filteredInvoices.size();
    int pageSize = getPageSize();
    return Math.max(1, (int) Math.ceil(size / (double) pageSize));
  }

  private LocalDate toLocalDate(Date date) {
    if (date == null) {
      return null;
    }
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  private void confirmSelectedInvoice() {
    HoaDonKhamDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hóa đơn cần xác nhận thanh toán.");
      return;
    }

    String trangThai = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThai()
    );
    if (StatusNormalizer.DA_THANH_TOAN.equals(trangThai)) {
      DialogHelper.info(this, "Hóa đơn này đã ở trạng thái đã thanh toán.");
      return;
    }
    if (StatusNormalizer.HOAN_HOA_DON.equals(trangThai)) {
      DialogHelper.warn(
        this,
        "Hóa đơn đã hủy/hoàn tiền, không thể xác nhận thanh toán."
      );
      return;
    }

    if (
      !DialogHelper.confirm(
        this,
        "Xác nhận thanh toán hóa đơn " + selected.getMaHDKham() + "?"
      )
    ) {
      return;
    }

    if (hoaDonKhamBUS.xacNhanThanhToan(selected.getMaHDKham())) {
      DialogHelper.info(this, "Xác nhận thanh toán thành công.");
      applyFilterAndRender();
      return;
    }
    DialogHelper.error(
      this,
      "Không thể xác nhận thanh toán. Vui lòng thử lại."
    );
  }

  private void cancelSelectedInvoice() {
    HoaDonKhamDTO selected = getSelectedInvoice();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hóa đơn cần hủy.");
      return;
    }

    String trangThai = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThai()
    );
    if (StatusNormalizer.HOAN_HOA_DON.equals(trangThai)) {
      DialogHelper.info(this, "Hóa đơn này đã hủy/hoàn tiền trước đó.");
      return;
    }
    if (StatusNormalizer.DA_THANH_TOAN.equals(trangThai)) {
      DialogHelper.warn(this, "Hóa đơn đã thanh toán, không thể hủy.");
      return;
    }

    if (
      !DialogHelper.confirm(this, "Hủy hóa đơn " + selected.getMaHDKham() + "?")
    ) {
      return;
    }

    if (hoaDonKhamBUS.huyHoaDon(selected.getMaHDKham())) {
      DialogHelper.info(this, "Hủy hóa đơn thành công.");
      applyFilterAndRender();
      return;
    }
    DialogHelper.error(this, "Không thể hủy hóa đơn. Vui lòng thử lại.");
  }

  private HoaDonKhamDTO getSelectedInvoice() {
    if (table == null) {
      return null;
    }
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    String maHD = String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 0)
    );
    for (HoaDonKhamDTO hd : filteredInvoices) {
      if (maHD.equals(hd.getMaHDKham())) {
        return hd;
      }
    }
    return null;
  }

  private void updateActionButtons() {
    if (btnXacNhan == null || btnHuy == null) {
      return;
    }

    HoaDonKhamDTO selected = getSelectedInvoice();
    if (selected == null) {
      btnXacNhan.setEnabled(false);
      btnHuy.setEnabled(false);
      return;
    }

    String trangThai = StatusNormalizer.normalizePaymentStatus(
      selected.getTrangThai()
    );
    btnXacNhan.setEnabled(
      coQuyenSua && !StatusNormalizer.HOAN_HOA_DON.equals(trangThai)
    );
    btnHuy.setEnabled(
      coQuyenHuy && !StatusNormalizer.DA_THANH_TOAN.equals(trangThai)
    );
  }

  private void resetFilters() {
    txtMaTim.setText("");
    txtGoiTim.setText("");
    spFrom.setValue(new Date());
    spTo.setValue(new Date());
    if (cbPageSize.getItemCount() > 0) {
      cbPageSize.setSelectedItem(10);
    }
    currentPage = 1;
    applyFilterAndRender();
  }

  private void apDungPhanQuyenHanhDong() {
    coQuyenXem = Session.coMotTrongCacQuyen("HOADONKHAM_XEM");
    coQuyenSua = Session.coMotTrongCacQuyen(
      "HOADONKHAM_SUA",
      "HOADONKHAM_THEM"
    );
    coQuyenHuy = Session.coMotTrongCacQuyen("HOADONKHAM_HUY");

    if (btnLoc != null) btnLoc.setVisible(coQuyenXem);
    if (btnXoaLoc != null) btnXoaLoc.setVisible(coQuyenXem);
    if (btnTaiLai != null) btnTaiLai.setVisible(coQuyenXem);
    if (btnPrev != null) btnPrev.setVisible(coQuyenXem);
    if (btnNext != null) btnNext.setVisible(coQuyenXem);
    if (btnXacNhan != null) btnXacNhan.setVisible(coQuyenSua);
    if (btnHuy != null) btnHuy.setVisible(coQuyenHuy);
    if (table != null) table.setEnabled(coQuyenXem);
    if (txtMaTim != null) txtMaTim.setEnabled(coQuyenXem);
    if (txtGoiTim != null) txtGoiTim.setEnabled(coQuyenXem);
    if (spFrom != null) spFrom.setEnabled(coQuyenXem);
    if (spTo != null) spTo.setEnabled(coQuyenXem);
    if (cbPageSize != null) cbPageSize.setEnabled(coQuyenXem);
  }
}
