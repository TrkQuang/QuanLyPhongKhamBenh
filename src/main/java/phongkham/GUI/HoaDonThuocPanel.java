package phongkham.gui;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.*;
import phongkham.DTO.*;
import phongkham.Utils.ExcelExport;
import phongkham.Utils.PdfExport;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.components.StatusCellRenderer;
import phongkham.gui.hoadonthuoc.HoaDonThuocPanelService;
import phongkham.gui.hoadonthuoc.TaoHoaDonTuDonThuocDialog;

public class HoaDonThuocPanel extends JPanel {

  private final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private final Color DANGER_COLOR = new Color(220, 38, 38);
  private final Color SUCCESS_COLOR = new Color(34, 197, 94);
  private final Color TEXT_COLOR = new Color(107, 114, 128);
  private final Color BG_COLOR = new Color(245, 247, 250);

  private JTextField txtTimKiem;
  private JButton btFind, btReload, btExport, btExportExcel, btView, btXacNhanThanhToan, btXacNhanGiao, btTaoDonThuoc;
  private JTable dataTable;
  private DefaultTableModel tableModel;
  private JLabel lbInfo;
  private JDateChooser dateFrom, dateTo;

  private ArrayList<HoaDonThuocDTO> fullList;
  private HoaDonThuocBUS hdBUS;
  private DonThuocBUS donThuocBUS;
  private CTDonThuocBUS ctDonThuocBUS;
  private ThuocBUS thuocBUS;
  private CTHDThuocBUS cthdBUS;
  private HoaDonThuocPanelService panelService;

  private int currentPage = 1;
  private static final int ROWS_PER_PAGE = 10;

  public HoaDonThuocPanel() {
    initData();
    initComponents();
  }

  private void initData() {
    hdBUS = new HoaDonThuocBUS();
    donThuocBUS = new DonThuocBUS();
    ctDonThuocBUS = new CTDonThuocBUS();
    thuocBUS = new ThuocBUS();
    cthdBUS = new CTHDThuocBUS();
    panelService = new HoaDonThuocPanelService();
    fullList = new ArrayList<>();
    reloadFullList();
  }

  private void initComponents() {
    setLayout(new BorderLayout(0, 10));
    setBackground(BG_COLOR);
    setBorder(new EmptyBorder(20, 20, 20, 20));

    add(createMasterTopPanel(), BorderLayout.NORTH);
    add(createTablePanel(), BorderLayout.CENTER);

    loadDataToTable();
  }

  private JPanel createMasterTopPanel() {
    JPanel master = new JPanel();
    master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));
    master.setOpaque(false);

    master.add(createTitlePanel());
    master.add(Box.createVerticalStrut(15));
    master.add(createSearchPanel());
    master.add(Box.createVerticalStrut(10));
    master.add(createPaginationPanel());

    return master;
  }

  private JPanel createTitlePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.setOpaque(false);
    JLabel title = new JLabel("QUẢN LÝ HÓA ĐƠN BÁN THUỐC");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    panel.add(title);
    return panel;
  }

  private JPanel createSearchPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

    JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    filterRow.setOpaque(false);

    JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
    actionRow.setOpaque(false);

    txtTimKiem = new JTextField(12);

    dateFrom = new JDateChooser();
    dateFrom.setDateFormatString("dd/MM/yyyy");
    dateFrom.setPreferredSize(new Dimension(120, 25));

    dateTo = new JDateChooser();
    dateTo.setDateFormatString("dd/MM/yyyy");
    dateTo.setPreferredSize(new Dimension(120, 25));

    btFind = createButton("Tìm kiếm", PRIMARY_COLOR);
    btReload = createButton("Làm mới", TEXT_COLOR);
    btTaoDonThuoc = createButton(
      "Tạo HĐ từ đơn thuốc",
      new Color(168, 85, 247)
    );
    btView = createButton("Xem chi tiết", SUCCESS_COLOR);
    btXacNhanThanhToan = createButton(
      "Xác nhận thanh toán",
      new Color(245, 158, 11)
    );
    btXacNhanGiao = createButton(
      "Xác nhận giao thuốc",
      new Color(16, 185, 129)
    );
    btExportExcel = createButton("Xuất Excel", new Color(6, 95, 70));
    btExport = createButton("Xuất PDF", DANGER_COLOR);

    filterRow.add(new JLabel("Tìm mã:"));
    filterRow.add(txtTimKiem);
    filterRow.add(new JLabel("Từ ngày:"));
    filterRow.add(dateFrom);
    filterRow.add(new JLabel("Đến ngày:"));
    filterRow.add(dateTo);
    filterRow.add(btFind);
    filterRow.add(btReload);

    actionRow.add(btTaoDonThuoc);
    actionRow.add(btView);
    actionRow.add(btXacNhanThanhToan);
    actionRow.add(btXacNhanGiao);
    actionRow.add(btExportExcel);
    actionRow.add(btExport);

    btFind.addActionListener(this::btFindAction);
    btReload.addActionListener(this::btReloadAction);
    btTaoDonThuoc.addActionListener(this::btTaoDonThuocAction);
    btView.addActionListener(this::btViewAction);
    btXacNhanThanhToan.addActionListener(this::btXacNhanThanhToanAction);
    btXacNhanGiao.addActionListener(this::btXacNhanGiaoAction);
    btExportExcel.addActionListener(e -> xuatBaoCaoExcel());
    btExport.addActionListener(e -> xuatBaoCaoPdf());

    panel.add(filterRow);
    panel.add(actionRow);

    return panel;
  }

  private JScrollPane createTablePanel() {
    String[] cols = {
      "Mã hóa đơn",
      "Mã đơn thuốc",
      "Ngày lập",
      "Tên bệnh nhân",
      "SĐT",
      "Tổng tiền",
      "Trạng thái TT",
      "Trạng thái lấy",
    };
    tableModel = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    dataTable = new JTable(tableModel);
    dataTable.setRowHeight(40);
    dataTable.setGridColor(new Color(240, 240, 240));
    dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    dataTable.getTableHeader().setBackground(new Color(248, 249, 250));

    DefaultTableCellRenderer center = new DefaultTableCellRenderer();
    center.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < cols.length; i++) {
      dataTable.getColumnModel().getColumn(i).setCellRenderer(center);
    }
    dataTable
      .getColumnModel()
      .getColumn(6)
      .setCellRenderer(new StatusCellRenderer(StatusColorUtil::thanhToan));
    dataTable
      .getColumnModel()
      .getColumn(7)
      .setCellRenderer(new StatusCellRenderer(StatusColorUtil::layThuoc));

    dataTable
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
          updateActionButtonsState();
        }
      });

    JScrollPane scroll = new JScrollPane(dataTable);
    scroll.getViewport().setBackground(Color.WHITE);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

    return scroll;
  }

  private JPanel createPaginationPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    lbInfo = new JLabel();
    lbInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));

    JButton btnPrev = new JButton("<");
    JButton btnNext = new JButton(">");

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    right.setOpaque(false);
    right.add(btnPrev);
    right.add(btnNext);

    panel.add(lbInfo, BorderLayout.WEST);
    panel.add(right, BorderLayout.EAST);

    btnPrev.addActionListener(e -> {
      if (currentPage > 1) {
        currentPage--;
        loadDataToTable();
      }
    });

    btnNext.addActionListener(e -> {
      if (currentPage * ROWS_PER_PAGE < fullList.size()) {
        currentPage++;
        loadDataToTable();
      }
    });

    return panel;
  }

  private JButton createButton(String text, Color bg) {
    JButton btn = new JButton(text);
    btn.setBackground(bg);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    return btn;
  }

  private void loadDataToTable() {
    tableModel.setRowCount(0);
    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    int total = fullList.size();
    int start = (currentPage - 1) * ROWS_PER_PAGE;
    int end = Math.min(start + ROWS_PER_PAGE, total);

    for (int i = start; i < end; i++) {
      HoaDonThuocDTO hd = fullList.get(i);
      tableModel.addRow(
        new Object[] {
          hd.getMaHoaDon(),
          hd.getMaDonThuoc() != null ? hd.getMaDonThuoc() : "-",
          hd.getNgayLap() != null ? hd.getNgayLap().format(f) : "",
          hd.getTenBenhNhan(),
          hd.getSdtBenhNhan(),
          String.format("%,.0f VNĐ", hd.getTongTien()),
          StatusDisplayUtil.thanhToan(hd.getTrangThaiThanhToan()),
          StatusDisplayUtil.layThuoc(
            hd.getTrangThaiLayThuoc() == null
              ? "CHO_LAY"
              : hd.getTrangThaiLayThuoc()
          ),
        }
      );
    }
    lbInfo.setText(
      "Trang " +
        currentPage +
        " / " +
        Math.max(1, (int) Math.ceil((double) total / ROWS_PER_PAGE))
    );

    updateActionButtonsState();
  }

  private void btFindAction(ActionEvent e) {
    String key = txtTimKiem.getText().trim();
    Date dFrom = dateFrom.getDate();
    Date dTo = dateTo.getDate();

    LocalDate fromDate = null;
    LocalDate toDate = null;

    if (dFrom != null) {
      fromDate = dFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    if (dTo != null) {
      toDate = dTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    List<HoaDonThuocDTO> source = hdBUS.getAllHoaDonThuoc();
    fullList = panelService.locHoaDon(
      source != null ? source : new ArrayList<>(),
      key,
      fromDate,
      toDate
    );

    if (fullList.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!");
    }

    currentPage = 1;
    loadDataToTable();
  }

  private void btReloadAction(ActionEvent e) {
    txtTimKiem.setText("");
    dateFrom.setDate(null);
    dateTo.setDate(null);

    reloadFullList();
    currentPage = 1;
    loadDataToTable();
  }

  private void btViewAction(ActionEvent e) {
    int row = dataTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn một hóa đơn để xem chi tiết!"
      );
      return;
    }

    String maHoaDon = (String) dataTable.getValueAt(row, 0);
    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);

    if (hd != null) {
      JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
      ChiTietHDThuocDialog dialog = new ChiTietHDThuocDialog(
        parentFrame,
        hd,
        this
      );
      dialog.setVisible(true);
    }
  }

  private void btXacNhanGiaoAction(ActionEvent e) {
    int row = dataTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn một hóa đơn để xác nhận giao thuốc!"
      );
      return;
    }

    String maHoaDon = (String) dataTable.getValueAt(row, 0);
    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);

    if (hd == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
      return;
    }

    // Confirm
    int confirm = JOptionPane.showConfirmDialog(
      this,
      String.format(
        "Xác nhận giao thuốc cho hóa đơn?\n\nMã HD: %s\nKhách: %s\nTổng tiền: %,.0f VNĐ\n\nHệ thống sẽ trừ tồn kho!",
        hd.getMaHoaDon(),
        hd.getTenBenhNhan(),
        hd.getTongTien()
      ),
      "Xác nhận giao thuốc",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    HoaDonThuocPanelService.ActionResult result = panelService.xacNhanGiaoThuoc(
      hdBUS,
      hd
    );
    if (result.isThanhCong()) {
      JOptionPane.showMessageDialog(this, result.getMessage());
      refreshData();
    } else {
      JOptionPane.showMessageDialog(
        this,
        result.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void btXacNhanThanhToanAction(ActionEvent e) {
    int row = dataTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn một hóa đơn để xác nhận thanh toán!"
      );
      return;
    }

    String maHoaDon = (String) dataTable.getValueAt(row, 0);
    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);

    if (hd == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
      return;
    }

    String trangThaiThanhToan = StatusNormalizer.normalizePaymentStatus(
      hd.getTrangThaiThanhToan()
    );
    if (StatusNormalizer.DA_THANH_TOAN.equals(trangThaiThanhToan)) {
      JOptionPane.showMessageDialog(
        this,
        "Hóa đơn này đã thanh toán trước đó."
      );
      return;
    }
    if (StatusNormalizer.HOAN_HOA_DON.equals(trangThaiThanhToan)) {
      JOptionPane.showMessageDialog(
        this,
        "Hóa đơn đã hoàn, không thể xác nhận thanh toán."
      );
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      this,
      String.format(
        "Xác nhận đã thanh toán hóa đơn?\n\nMã HD: %s\nKhách: %s\nTổng tiền: %,.0f VNĐ",
        hd.getMaHoaDon(),
        hd.getTenBenhNhan(),
        hd.getTongTien()
      ),
      "Xác nhận thanh toán",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    if (hdBUS.payInvoice(maHoaDon)) {
      JOptionPane.showMessageDialog(this, "✅ Xác nhận thanh toán thành công!");
      refreshData();
    } else {
      JOptionPane.showMessageDialog(
        this,
        "❌ Không thể xác nhận thanh toán.",
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void btTaoDonThuocAction(ActionEvent e) {
    TaoHoaDonTuDonThuocDialog dialog = new TaoHoaDonTuDonThuocDialog(
      hdBUS,
      donThuocBUS,
      ctDonThuocBUS,
      thuocBUS,
      cthdBUS,
      this::refreshData
    );
    dialog.show(this);
  }

  public void refreshData() {
    btReloadAction(null);
  }

  private void updateActionButtonsState() {
    if (
      btXacNhanGiao == null || btXacNhanThanhToan == null || dataTable == null
    ) {
      return;
    }

    int row = dataTable.getSelectedRow();
    if (row < 0) {
      btXacNhanGiao.setEnabled(false);
      btXacNhanThanhToan.setEnabled(false);
      return;
    }

    String maHoaDon = (String) dataTable.getValueAt(row, 0);
    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);
    if (hd == null) {
      btXacNhanGiao.setEnabled(false);
      btXacNhanThanhToan.setEnabled(false);
      return;
    }

    String ttThanhToan = StatusNormalizer.normalizePaymentStatus(
      hd.getTrangThaiThanhToan()
    );
    boolean daThanhToan = StatusNormalizer.DA_THANH_TOAN.equals(ttThanhToan);
    boolean hoanHoaDon = StatusNormalizer.HOAN_HOA_DON.equals(ttThanhToan);

    btXacNhanGiao.setEnabled(daThanhToan);
    btXacNhanThanhToan.setEnabled(!daThanhToan && !hoanHoaDon);
  }

  private void reloadFullList() {
    List<HoaDonThuocDTO> data = hdBUS.getAllHoaDonThuoc();
    fullList = new ArrayList<>(data != null ? data : new ArrayList<>());
  }

  private void xuatBaoCaoExcel() {
    ExcelExport.exportOperationalTableToCsv(
      dataTable,
      "DoanhThuHoaDonThuoc",
      taoBoLocBaoCao()
    );
  }

  private void xuatBaoCaoPdf() {
    PdfExport.exportOperationalTable(
      dataTable,
      "Doanh thu hóa đơn thuốc",
      taoBoLocBaoCao()
    );
  }

  private String taoBoLocBaoCao() {
    String key = txtTimKiem.getText().trim();
    String from =
      dateFrom.getDate() == null
        ? ""
        : new java.text.SimpleDateFormat("dd/MM/yyyy").format(
            dateFrom.getDate()
          );
    String to =
      dateTo.getDate() == null
        ? ""
        : new java.text.SimpleDateFormat("dd/MM/yyyy").format(dateTo.getDate());

    if (key.isEmpty() && from.isEmpty() && to.isEmpty()) {
      return "Toàn bộ";
    }
    return (
      "Từ khóa: " +
      (key.isEmpty() ? "(không)" : key) +
      " | Từ ngày: " +
      (from.isEmpty() ? "(không)" : from) +
      " | Đến ngày: " +
      (to.isEmpty() ? "(không)" : to)
    );
  }
}
