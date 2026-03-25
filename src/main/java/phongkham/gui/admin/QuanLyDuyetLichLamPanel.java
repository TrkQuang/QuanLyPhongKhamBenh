package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.KhungGioLamViecBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.KhungGioLamViecDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class QuanLyDuyetLichLamPanel extends BasePanel {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final LichLamViecBUS lichLamViecBUS = new LichLamViecBUS();
  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final KhungGioLamViecBUS khungGioLamViecBUS =
    new KhungGioLamViecBUS();

  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã lịch",
      "Mã bác sĩ",
      "Tên bác sĩ",
      "Ngày làm",
      "Ca làm",
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
  private JTextField txtSearch;
  private JTextField txtFromDate;
  private JTextField txtToDate;
  private JComboBox<DoctorOption> cbDoctor;
  private JComboBox<String> cbStatus;
  private JLabel lblPage;
  private JButton btnFilter;
  private JButton btnClear;
  private JButton btnApprove;
  private JButton btnReject;
  private JButton btnManageRanges;
  private JButton btnExport;
  private JButton btnReload;
  private JButton btnPrev;
  private JButton btnNext;

  private List<LichLamViecDTO> filteredRows = new ArrayList<>();
  private Map<String, String> doctorNameMap = new HashMap<>();
  private int currentPage = 1;
  private int pageSize = DEFAULT_PAGE_SIZE;

  @Override
  protected void init() {
    add(buildTopArea(), BorderLayout.NORTH);

    table = new JTable(model);
    UIUtils.styleTable(table);
    applyStatusRowRenderer();
    add(
      UIUtils.createSection(
        "Danh sách lịch làm cần duyệt",
        new JScrollPane(table)
      ),
      BorderLayout.CENTER
    );

    add(buildBottomArea(), BorderLayout.SOUTH);

    apDungPhanQuyenHanhDong();

    loadDoctors();
    refreshData();
  }

  private JPanel buildTopArea() {
    JPanel top = new JPanel(new BorderLayout(0, 12));
    top.setOpaque(false);

    JPanel filterContent = new JPanel(new BorderLayout(0, 8));
    filterContent.setOpaque(false);

    JPanel filterRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    filterRow1.setOpaque(false);
    JPanel filterRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    filterRow2.setOpaque(false);

    txtSearch = UIUtils.roundedTextField(
      "Mã lịch / mã bác sĩ / tên bác sĩ",
      18
    );
    txtFromDate = UIUtils.roundedTextField("Từ ngày (yyyy-MM-dd)", 12);
    txtToDate = UIUtils.roundedTextField("Đến ngày (yyyy-MM-dd)", 12);
    UIUtils.fixedSize(txtSearch, 270, 36);
    UIUtils.fixedSize(txtFromDate, 190, 36);
    UIUtils.fixedSize(txtToDate, 190, 36);

    cbDoctor = new JComboBox<>();
    UIUtils.fixedSize(cbDoctor, 190, 36);

    cbStatus = new JComboBox<>(
      new String[] { "Tất cả", "Chờ duyệt", "Đã duyệt", "Từ chối" }
    );
    UIUtils.fixedSize(cbStatus, 130, 36);

    btnFilter = UIUtils.primaryButton("Lọc");
    btnClear = UIUtils.ghostButton("Xóa lọc");

    btnFilter.addActionListener(e -> applyFilterAndRender(1));
    btnClear.addActionListener(e -> clearFilter());

    filterRow1.add(txtSearch);
    filterRow1.add(cbDoctor);
    filterRow1.add(cbStatus);

    filterRow2.add(txtFromDate);
    filterRow2.add(txtToDate);
    filterRow2.add(btnFilter);
    filterRow2.add(btnClear);

    filterContent.add(filterRow1, BorderLayout.NORTH);
    filterContent.add(filterRow2, BorderLayout.CENTER);

    top.add(UIUtils.createSection("Bộ lọc", filterContent), BorderLayout.NORTH);

    btnApprove = UIUtils.primaryButton("Duyệt lịch đã chọn");
    btnReject = UIUtils.ghostButton("Từ chối lịch đã chọn");
    btnManageRanges = UIUtils.ghostButton("Khung giờ đăng ký");
    btnExport = UIUtils.ghostButton("Xuất CSV");
    btnReload = UIUtils.ghostButton("Tải lại");

    JPanel actions = UIUtils.row(
      btnApprove,
      btnReject,
      btnManageRanges,
      btnExport,
      btnReload
    );

    btnApprove.addActionListener(e -> approveSelected());
    btnReject.addActionListener(e -> rejectSelected());
    btnManageRanges.addActionListener(e -> openShiftTemplateDialog());
    btnExport.addActionListener(e -> exportFilteredCsv());
    btnReload.addActionListener(e -> refreshData());

    top.add(actions, BorderLayout.SOUTH);
    return top;
  }

  private JPanel buildBottomArea() {
    JPanel wrap = new JPanel(new BorderLayout(0, 10));
    wrap.setOpaque(false);

    JPanel paging = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    paging.setOpaque(false);

    btnPrev = UIUtils.ghostButton("Trang trước");
    btnNext = UIUtils.ghostButton("Trang sau");
    lblPage = new JLabel("Trang 1/1");

    JComboBox<Integer> cbPageSize = new JComboBox<>(
      new Integer[] { 10, 20, 50 }
    );
    cbPageSize.setSelectedItem(DEFAULT_PAGE_SIZE);
    UIUtils.fixedSize(cbPageSize, 80, 36);

    btnPrev.addActionListener(e -> {
      if (currentPage > 1) {
        renderPage(currentPage - 1);
      }
    });

    btnNext.addActionListener(e -> {
      int totalPages = getTotalPages();
      if (currentPage < totalPages) {
        renderPage(currentPage + 1);
      }
    });

    cbPageSize.addActionListener(e -> {
      Integer selected = (Integer) cbPageSize.getSelectedItem();
      if (selected != null && selected > 0) {
        pageSize = selected;
        renderPage(1);
      }
    });

    paging.add(new JLabel("Hiển thị"));
    paging.add(cbPageSize);
    paging.add(new JLabel("dòng/trang"));
    paging.add(btnPrev);
    paging.add(lblPage);
    paging.add(btnNext);

    wrap.add(paging, BorderLayout.EAST);
    return wrap;
  }

  private void loadDoctors() {
    doctorNameMap.clear();
    cbDoctor.removeAllItems();
    cbDoctor.addItem(new DoctorOption("", "Tất cả bác sĩ"));

    for (BacSiDTO doctor : bacSiBUS.getAll()) {
      doctorNameMap.put(doctor.getMaBacSi(), safe(doctor.getHoTen()));
      cbDoctor.addItem(
        new DoctorOption(
          doctor.getMaBacSi(),
          doctor.getMaBacSi() + " - " + safe(doctor.getHoTen())
        )
      );
    }
  }

  private void refreshData() {
    applyFilterAndRender(1);
  }

  private void clearFilter() {
    txtSearch.setText("");
    txtFromDate.setText("");
    txtToDate.setText("");
    cbDoctor.setSelectedIndex(0);
    cbStatus.setSelectedIndex(0);
    applyFilterAndRender(1);
  }

  private void applyFilterAndRender(int page) {
    LocalDate fromDate;
    LocalDate toDate;
    try {
      fromDate = parseDateOrNull(txtFromDate.getText());
      toDate = parseDateOrNull(txtToDate.getText());
    } catch (DateTimeParseException ex) {
      DialogHelper.warn(
        this,
        "Ngày lọc không hợp lệ. Định dạng đúng: yyyy-MM-dd"
      );
      return;
    }

    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      DialogHelper.warn(this, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày.");
      return;
    }

    String keyword = safe(txtSearch.getText()).trim().toLowerCase();
    DoctorOption selectedDoctor = (DoctorOption) cbDoctor.getSelectedItem();
    String selectedDoctorId = selectedDoctor == null ? "" : selectedDoctor.id;
    String selectedStatusToken = mapStatusToToken(
      (String) cbStatus.getSelectedItem()
    );

    ArrayList<LichLamViecDTO> all = lichLamViecBUS.getAll();
    List<LichLamViecDTO> result = new ArrayList<>();

    for (LichLamViecDTO item : all) {
      String maLich = safe(item.getMaLichLam());
      String maBacSi = safe(item.getMaBacSi());
      String tenBacSi = getDoctorName(maBacSi);
      String statusToken = normalizeStatus(item.getTrangThai());

      if (!selectedDoctorId.isEmpty() && !selectedDoctorId.equals(maBacSi)) {
        continue;
      }

      if (
        !selectedStatusToken.isEmpty() &&
        !selectedStatusToken.equals(statusToken)
      ) {
        continue;
      }

      if (!keyword.isEmpty()) {
        String haystack = (
          maLich +
          " " +
          maBacSi +
          " " +
          tenBacSi
        ).toLowerCase();
        if (!haystack.contains(keyword)) {
          continue;
        }
      }

      if (!matchDateRange(item.getNgayLam(), fromDate, toDate)) {
        continue;
      }

      result.add(item);
    }

    filteredRows = result;
    renderPage(page);
  }

  private void renderPage(int targetPage) {
    model.setRowCount(0);

    int totalPages = getTotalPages();
    currentPage = Math.max(1, Math.min(targetPage, totalPages));

    int fromIndex = (currentPage - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, filteredRows.size());

    for (int i = fromIndex; i < toIndex; i++) {
      LichLamViecDTO item = filteredRows.get(i);
      String statusToken = normalizeStatus(item.getTrangThai());
      model.addRow(
        new Object[] {
          item.getMaLichLam(),
          item.getMaBacSi(),
          getDoctorName(item.getMaBacSi()),
          item.getNgayLam(),
          item.getCaLam(),
          toDisplayStatus(statusToken),
        }
      );
    }

    lblPage.setText("Trang " + currentPage + "/" + totalPages);
  }

  private int getTotalPages() {
    if (filteredRows.isEmpty()) {
      return 1;
    }
    return (int) Math.ceil(
      (double) filteredRows.size() / Math.max(1, pageSize)
    );
  }

  private void approveSelected() {
    String maLich = getSelectedScheduleId();
    if (maLich == null) {
      DialogHelper.warn(this, "Vui lòng chọn lịch làm để duyệt.");
      return;
    }

    LichLamViecDTO schedule = lichLamViecBUS.getById(maLich);
    if (schedule == null) {
      DialogHelper.warn(this, "Không tìm thấy lịch làm đã chọn.");
      return;
    }

    String status = normalizeStatus(schedule.getTrangThai());
    if (!StatusNormalizer.CHO_DUYET.equals(status)) {
      DialogHelper.warn(this, "Chỉ có thể duyệt lịch ở trạng thái Chờ duyệt.");
      return;
    }

    if (!DialogHelper.confirm(this, "Xác nhận duyệt lịch " + maLich + "?")) {
      return;
    }

    if (!lichLamViecBUS.duyetLich(maLich)) {
      DialogHelper.error(this, "Duyệt lịch thất bại.");
      return;
    }

    DialogHelper.info(this, "Đã duyệt lịch thành công.");
    applyFilterAndRender(currentPage);
  }

  private void rejectSelected() {
    String maLich = getSelectedScheduleId();
    if (maLich == null) {
      DialogHelper.warn(this, "Vui lòng chọn lịch làm để từ chối.");
      return;
    }

    LichLamViecDTO schedule = lichLamViecBUS.getById(maLich);
    if (schedule == null) {
      DialogHelper.warn(this, "Không tìm thấy lịch làm đã chọn.");
      return;
    }

    String status = normalizeStatus(schedule.getTrangThai());
    if (!StatusNormalizer.CHO_DUYET.equals(status)) {
      DialogHelper.warn(
        this,
        "Chỉ có thể từ chối lịch ở trạng thái Chờ duyệt."
      );
      return;
    }

    if (!DialogHelper.confirm(this, "Xác nhận từ chối lịch " + maLich + "?")) {
      return;
    }

    if (!lichLamViecBUS.tuChoiLich(maLich)) {
      DialogHelper.error(this, "Từ chối lịch thất bại.");
      return;
    }

    DialogHelper.info(this, "Đã từ chối lịch thành công.");
    applyFilterAndRender(currentPage);
  }

  private String getSelectedScheduleId() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    int modelRow = table.convertRowIndexToModel(row);
    return String.valueOf(model.getValueAt(modelRow, 0));
  }

  private boolean matchDateRange(
    String rawDate,
    LocalDate fromDate,
    LocalDate toDate
  ) {
    LocalDate value;
    try {
      value = LocalDate.parse(safe(rawDate));
    } catch (DateTimeParseException ex) {
      return false;
    }

    if (fromDate != null && value.isBefore(fromDate)) {
      return false;
    }
    if (toDate != null && value.isAfter(toDate)) {
      return false;
    }
    return true;
  }

  private LocalDate parseDateOrNull(String text) {
    String value = safe(text).trim();
    if (value.isEmpty()) {
      return null;
    }
    return LocalDate.parse(value);
  }

  private String mapStatusToToken(String display) {
    if (display == null || "Tất cả".equalsIgnoreCase(display)) {
      return "";
    }
    if ("Chờ duyệt".equalsIgnoreCase(display)) {
      return StatusNormalizer.CHO_DUYET;
    }
    if ("Đã duyệt".equalsIgnoreCase(display)) {
      return StatusNormalizer.DA_DUYET;
    }
    if ("Từ chối".equalsIgnoreCase(display)) {
      return StatusNormalizer.TU_CHOI;
    }
    return "";
  }

  private String toDisplayStatus(String token) {
    if (StatusNormalizer.CHO_DUYET.equals(token)) {
      return "Chờ duyệt";
    }
    if (StatusNormalizer.DA_DUYET.equals(token)) {
      return "Đã duyệt";
    }
    if (StatusNormalizer.TU_CHOI.equals(token)) {
      return "Từ chối";
    }
    return token;
  }

  private String normalizeStatus(String rawStatus) {
    return StatusNormalizer.normalizeLichLamViecStatus(rawStatus);
  }

  private void applyStatusRowRenderer() {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
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

        int modelRow = table.convertRowIndexToModel(row);
        String statusText = String.valueOf(model.getValueAt(modelRow, 5));
        String token = normalizeStatus(statusText);

        if (StatusNormalizer.CHO_DUYET.equals(token)) {
          cell.setBackground(new Color(255, 247, 214));
        } else if (StatusNormalizer.DA_DUYET.equals(token)) {
          cell.setBackground(new Color(220, 252, 231));
        } else if (StatusNormalizer.TU_CHOI.equals(token)) {
          cell.setBackground(new Color(254, 226, 226));
        } else {
          cell.setBackground(Color.WHITE);
        }
        return cell;
      }
    };

    for (int i = 0; i < table.getColumnCount(); i++) {
      table.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }
  }

  private void exportFilteredCsv() {
    if (filteredRows.isEmpty()) {
      DialogHelper.warn(this, "Không có dữ liệu để xuất CSV.");
      return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Chọn nơi lưu file CSV");
    chooser.setFileFilter(
      new FileNameExtensionFilter("CSV files (*.csv)", "csv")
    );
    chooser.setSelectedFile(new File("duyet_lich_lam.csv"));

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
      writer.write("Ma lich,Ma bac si,Ten bac si,Ngay lam,Ca lam,Trang thai");
      writer.newLine();

      for (LichLamViecDTO row : filteredRows) {
        String line =
          escapeCsv(safe(row.getMaLichLam())) +
          "," +
          escapeCsv(safe(row.getMaBacSi())) +
          "," +
          escapeCsv(getDoctorName(row.getMaBacSi())) +
          "," +
          escapeCsv(safe(row.getNgayLam())) +
          "," +
          escapeCsv(safe(row.getCaLam())) +
          "," +
          escapeCsv(toDisplayStatus(normalizeStatus(row.getTrangThai())));
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException ex) {
      DialogHelper.error(this, "Xuất CSV thất bại: " + ex.getMessage());
      return;
    }

    DialogHelper.info(
      this,
      "Đã xuất CSV thành công: " + file.getAbsolutePath()
    );
  }

  private String escapeCsv(String value) {
    String text = value == null ? "" : value.replace("\"", "\"\"");
    return "\"" + text + "\"";
  }

  private String getDoctorName(String maBacSi) {
    String name = doctorNameMap.get(maBacSi);
    return name == null || name.trim().isEmpty() ? "Không rõ" : name;
  }

  private String safe(String text) {
    return text == null ? "" : text;
  }

  private void openShiftTemplateDialog() {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Quản lý khung giờ đăng ký lịch",
      true
    );
    dialog.setLayout(new BorderLayout(8, 8));

    DefaultTableModel rangeModel = new DefaultTableModel(
      new Object[] { "Mã", "Khung giờ", "Mô tả", "Trạng thái" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable rangeTable = new JTable(rangeModel);
    UIUtils.styleTable(rangeTable);
    reloadShiftTemplateTable(rangeModel);

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    top.setOpaque(false);
    JTextField txtRange = UIUtils.roundedTextField("VD: 07:30-09:30", 14);
    JTextField txtDesc = UIUtils.roundedTextField("Mô tả (tuỳ chọn)", 18);
    UIUtils.fixedSize(txtRange, 150, 34);
    UIUtils.fixedSize(txtDesc, 220, 34);
    javax.swing.JButton btnAdd = UIUtils.primaryButton("Thêm khung giờ");
    javax.swing.JButton btnToggle = UIUtils.ghostButton(
      "Bật/Tắt khung đã chọn"
    );
    javax.swing.JButton btnRefresh = UIUtils.ghostButton("Làm mới");

    btnAdd.addActionListener(e -> {
      String range = safe(txtRange.getText()).trim();
      String desc = safe(txtDesc.getText()).trim();
      if (range.isEmpty()) {
        DialogHelper.warn(dialog, "Vui lòng nhập khung giờ (HH:mm-HH:mm).");
        return;
      }
      if (!khungGioLamViecBUS.addRange(range, desc)) {
        DialogHelper.warn(
          dialog,
          "Không thể thêm khung giờ. Kiểm tra định dạng hoặc trùng dữ liệu."
        );
        return;
      }
      txtRange.setText("");
      txtDesc.setText("");
      reloadShiftTemplateTable(rangeModel);
      DialogHelper.info(dialog, "Đã thêm khung giờ đăng ký.");
    });

    btnToggle.addActionListener(e -> {
      int row = rangeTable.getSelectedRow();
      if (row < 0) {
        DialogHelper.warn(dialog, "Vui lòng chọn khung giờ cần bật/tắt.");
        return;
      }
      int modelRow = rangeTable.convertRowIndexToModel(row);
      int maKhung = Integer.parseInt(
        String.valueOf(rangeModel.getValueAt(modelRow, 0))
      );
      String status = String.valueOf(rangeModel.getValueAt(modelRow, 3));
      boolean nextActive = !"Hoạt động".equalsIgnoreCase(status);
      if (!khungGioLamViecBUS.toggleActive(maKhung, nextActive)) {
        DialogHelper.error(dialog, "Cập nhật trạng thái khung giờ thất bại.");
        return;
      }
      reloadShiftTemplateTable(rangeModel);
    });

    btnRefresh.addActionListener(e -> reloadShiftTemplateTable(rangeModel));

    top.add(txtRange);
    top.add(txtDesc);
    top.add(btnAdd);
    top.add(btnToggle);
    top.add(btnRefresh);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    bottom.setOpaque(false);
    javax.swing.JButton btnClose = UIUtils.ghostButton("Đóng");
    btnClose.addActionListener(e -> dialog.dispose());
    bottom.add(btnClose);

    dialog.add(
      UIUtils.createSection("Danh mục khung giờ", top),
      BorderLayout.NORTH
    );
    dialog.add(new JScrollPane(rangeTable), BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);
    dialog.setSize(900, 460);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void reloadShiftTemplateTable(DefaultTableModel model) {
    model.setRowCount(0);
    for (KhungGioLamViecDTO item : khungGioLamViecBUS.getAll()) {
      model.addRow(
        new Object[] {
          item.getMaKhungGio(),
          item.getKhungGio(),
          safe(item.getMoTa()),
          item.getActive() == 1 ? "Hoạt động" : "Tạm tắt",
        }
      );
    }
  }

  private static class DoctorOption {

    private final String id;
    private final String label;

    private DoctorOption(String id, String label) {
      this.id = id;
      this.label = label;
    }

    @Override
    public String toString() {
      return label;
    }
  }

  private void apDungPhanQuyenHanhDong() {
    boolean coQuyenXem = Session.coMotTrongCacQuyen("DUYETLICHLAM_XEM");
    boolean coQuyenDuyet = Session.coMotTrongCacQuyen("LICHLAMVIEC_DUYET");
    boolean coQuyenTuChoi = Session.coMotTrongCacQuyen("LICHLAMVIEC_TU_CHOI");
    boolean coQuyenQuanLyKhung = Session.coMotTrongCacQuyen("LICHLAMVIEC_THEM");

    if (btnFilter != null) btnFilter.setVisible(coQuyenXem);
    if (btnClear != null) btnClear.setVisible(coQuyenXem);
    if (btnApprove != null) btnApprove.setVisible(coQuyenDuyet);
    if (btnReject != null) btnReject.setVisible(coQuyenTuChoi);
    if (btnManageRanges != null) btnManageRanges.setVisible(coQuyenQuanLyKhung);
    if (btnExport != null) btnExport.setVisible(coQuyenXem);
    if (btnReload != null) btnReload.setVisible(coQuyenXem);
    if (btnPrev != null) btnPrev.setVisible(coQuyenXem);
    if (btnNext != null) btnNext.setVisible(coQuyenXem);

    if (txtSearch != null) txtSearch.setEnabled(coQuyenXem);
    if (txtFromDate != null) txtFromDate.setEnabled(coQuyenXem);
    if (txtToDate != null) txtToDate.setEnabled(coQuyenXem);
    if (cbDoctor != null) cbDoctor.setEnabled(coQuyenXem);
    if (cbStatus != null) cbStatus.setEnabled(coQuyenXem);
    if (table != null) table.setEnabled(coQuyenXem);
  }
}
