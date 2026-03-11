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

public class HoaDonThuocPanel extends JPanel {

  private final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private final Color DANGER_COLOR = new Color(220, 38, 38);
  private final Color SUCCESS_COLOR = new Color(34, 197, 94);
  private final Color TEXT_COLOR = new Color(107, 114, 128);
  private final Color BG_COLOR = new Color(245, 247, 250);

  private JTextField txtTimKiem;
  private JButton btFind, btReload, btExport, btView, btXacNhanGiao, btTaoDonThuoc;
  private JTable dataTable;
  private DefaultTableModel tableModel;
  private JLabel lbInfo;
  private JDateChooser dateFrom, dateTo;

  private ArrayList<HoaDonThuocDTO> fullList;
  private HoaDonThuocBUS hdBUS;

  private int currentPage = 1;
  private static final int ROWS_PER_PAGE = 10;

  public HoaDonThuocPanel() {
    initData();
    initComponents();
  }

  private void initData() {
    hdBUS = new HoaDonThuocBUS();
    fullList = new ArrayList<>();
    List<HoaDonThuocDTO> data = hdBUS.getAllHoaDonThuoc();
    if (data != null) {
      fullList.addAll(data);
    }
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
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

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
      "📋 Tạo HĐ từ đơn thuốc",
      new Color(168, 85, 247)
    );
    btView = createButton("Xem chi tiết", SUCCESS_COLOR);
    btXacNhanGiao = createButton(
      "Xác nhận giao thuốc",
      new Color(16, 185, 129)
    );
    btExport = createButton("Xuất PDF", DANGER_COLOR);

    panel.add(new JLabel("Tìm mã:"));
    panel.add(txtTimKiem);
    panel.add(new JLabel("Từ ngày:"));
    panel.add(dateFrom);
    panel.add(new JLabel("Đến ngày:"));
    panel.add(dateTo);
    panel.add(btFind);
    panel.add(btReload);
    panel.add(btTaoDonThuoc);
    panel.add(btView);
    panel.add(btXacNhanGiao);
    panel.add(btExport);

    btFind.addActionListener(this::btFindAction);
    btReload.addActionListener(this::btReloadAction);
    btTaoDonThuoc.addActionListener(this::btTaoDonThuocAction);
    btView.addActionListener(this::btViewAction);
    btXacNhanGiao.addActionListener(this::btXacNhanGiaoAction);
    btExport.addActionListener(e ->
      phongkham.Utils.PdfExport.exportTable(dataTable, "Hóa đơn bán thuốc")
    );

    panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 70));
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

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
          hd.getTrangThaiThanhToan(),
          hd.getTrangThaiLayThuoc() != null
            ? hd.getTrangThaiLayThuoc()
            : "ĐANG CHỜ LẤY",
        }
      );
    }
    lbInfo.setText(
      "Trang " +
        currentPage +
        " / " +
        Math.max(1, (int) Math.ceil((double) total / ROWS_PER_PAGE))
    );
  }

  private void btFindAction(ActionEvent e) {
    String key = txtTimKiem.getText().trim();
    Date dFrom = dateFrom.getDate();
    Date dTo = dateTo.getDate();

    LocalDate fromDate =
      dFrom != null
        ? dFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        : null;
    LocalDate toDate =
      dTo != null
        ? dTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        : null;

    fullList = hdBUS.searchAndFilter(key, fromDate, toDate);

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

    List<HoaDonThuocDTO> data = hdBUS.getAllHoaDonThuoc();
    fullList = new ArrayList<>(data != null ? data : new ArrayList<>());
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

    // Kiểm tra trạng thái
    if (!"Đã thanh toán".equals(hd.getTrangThaiThanhToan())) {
      JOptionPane.showMessageDialog(
        this,
        "Hóa đơn chưa thanh toán, không thể giao thuốc!"
      );
      return;
    }

    if (!"ĐANG CHỜ LẤY".equals(hd.getTrangThaiLayThuoc())) {
      JOptionPane.showMessageDialog(this, "Hóa đơn đã được xử lý hoặc đã hủy!");
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

    // Gọi BUS để xử lý
    boolean success = hdBUS.completePickup(maHoaDon);

    if (success) {
      JOptionPane.showMessageDialog(
        this,
        "Xác nhận giao thuốc thành công!\nĐã trừ tồn kho."
      );
      refreshData();
    } else {
      JOptionPane.showMessageDialog(
        this,
        "Giao thuốc thất bại! Vui lòng kiểm tra lại.",
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void btTaoDonThuocAction(ActionEvent e) {
    TaoDonThuocDialog dialog = new TaoDonThuocDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      this
    );
    dialog.setVisible(true);
  }

  public void refreshData() {
    btReloadAction(null);
  }
}
