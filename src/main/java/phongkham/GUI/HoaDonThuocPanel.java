package phongkham.gui;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private DonThuocBUS donThuocBUS;
  private CTDonThuocBUS ctDonThuocBUS;
  private ThuocBUS thuocBUS;
  private CTHDThuocBUS cthdBUS;

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

    LocalDate fromDate = null;
    LocalDate toDate = null;

    if (dFrom != null) {
      fromDate = dFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    if (dTo != null) {
      toDate = dTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    fullList = new ArrayList<>();
    for (HoaDonThuocDTO hd : hdBUS.getAllHoaDonThuoc()) {
      boolean match = true;

      if (!key.isEmpty()) {
        match =
          String.valueOf(hd.getMaHoaDon()).contains(key) ||
          hd.getTenBenhNhan().toLowerCase().contains(key.toLowerCase());
      }

      if (match && fromDate != null && toDate != null) {
        LocalDate hoaDonDate = hd.getNgayLap().toLocalDate();
        match = !hoaDonDate.isBefore(fromDate) && !hoaDonDate.isAfter(toDate);
      }

      if (match) {
        fullList.add(hd);
      }
    }

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
    // Hiển thị dialog nhập mã đơn thuốc
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Tạo hóa đơn từ đơn thuốc",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(600, 500);
    dialog.setLocationRelativeTo(this);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Panel nhập mã đơn
    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    inputPanel.setBorder(
      BorderFactory.createTitledBorder("Thông tin đơn thuốc")
    );
    JTextField txtMaDon = new JTextField(15);
    JButton btnTimDon = createButton("Tìm đơn", PRIMARY_COLOR);
    inputPanel.add(new JLabel("Mã đơn thuốc:"));
    inputPanel.add(txtMaDon);
    inputPanel.add(btnTimDon);

    // Panel hiển thị thông tin đơn
    JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin"));
    JLabel lblNgayKe = new JLabel("-");
    JLabel lblMaHS = new JLabel("-");
    JLabel lblSoLuongThuoc = new JLabel("-");
    infoPanel.add(new JLabel("Ngày kê:"));
    infoPanel.add(lblNgayKe);
    infoPanel.add(new JLabel("Mã hồ sơ:"));
    infoPanel.add(lblMaHS);
    infoPanel.add(new JLabel("Số loại thuốc:"));
    infoPanel.add(lblSoLuongThuoc);

    // Table thuốc
    String[] cols = { "Mã thuốc", "Tên thuốc", "SL", "Đơn giá", "Thành tiền" };
    DefaultTableModel modelThuoc = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable tableThuoc = new JTable(modelThuoc);
    tableThuoc.setRowHeight(30);
    JScrollPane scrollThuoc = new JScrollPane(tableThuoc);

    // Panel khách hàng
    JPanel khachPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    khachPanel.setBorder(
      BorderFactory.createTitledBorder("Thông tin khách hàng")
    );
    JTextField txtTenKhach = new JTextField(15);
    JTextField txtSDT = new JTextField(15);
    khachPanel.add(new JLabel("Tên khách:"));
    khachPanel.add(txtTenKhach);
    khachPanel.add(new JLabel("SĐT:"));
    khachPanel.add(txtSDT);

    // Panel tổng tiền và nút
    JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
    JLabel lblTongTien = new JLabel("TỔNG TIỀN: 0 VNĐ");
    lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblTongTien.setForeground(DANGER_COLOR);
    JButton btnThanhToan = createButton(
      "💰 THANH TOÁN TIỀN MẶT",
      SUCCESS_COLOR
    );
    btnThanhToan.setEnabled(false);

    bottomPanel.add(lblTongTien, BorderLayout.WEST);
    bottomPanel.add(btnThanhToan, BorderLayout.EAST);

    // Add to main panel
    mainPanel.add(inputPanel);
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(infoPanel);
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(scrollThuoc);
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(khachPanel);
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(bottomPanel);

    dialog.add(mainPanel);

    // Biến lưu đơn thuốc hiện tại
    final DonThuocDTO[] donThuocHienTai = { null };
    final ArrayList<CTDonThuocDTO>[] danhSachThuoc = new ArrayList[] {
      new ArrayList<>(),
    };

    // Action tìm đơn
    btnTimDon.addActionListener(ev -> {
      String maDon = txtMaDon.getText().trim();
      if (maDon.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã đơn thuốc!");
        return;
      }

      // Tìm đơn thuốc
      donThuocHienTai[0] = donThuocBUS.searchTheoMa(maDon);
      if (donThuocHienTai[0] == null) {
        JOptionPane.showMessageDialog(
          dialog,
          "❌ Không tìm thấy đơn thuốc: " + maDon
        );
        return;
      }

      // Kiểm tra đơn đã có hóa đơn chưa
      for (HoaDonThuocDTO hd : hdBUS.getAllHoaDonThuoc()) {
        if (hd.getMaDonThuoc() != null && hd.getMaDonThuoc().equals(maDon)) {
          JOptionPane.showMessageDialog(
            dialog,
            "⚠️ Đơn thuốc này đã có hóa đơn!\nMã hóa đơn: " + hd.getMaHoaDon()
          );
          return;
        }
      }

      // Lấy chi tiết đơn
      danhSachThuoc[0] = ctDonThuocBUS.getByMaDonThuoc(maDon);
      if (danhSachThuoc[0].isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Đơn thuốc không có thuốc nào!");
        return;
      }

      // Hiển thị thông tin
      lblNgayKe.setText(donThuocHienTai[0].getNgayKeDon());
      lblMaHS.setText(donThuocHienTai[0].getMaHoSo());
      lblSoLuongThuoc.setText(
        String.valueOf(danhSachThuoc[0].size()) + " loại"
      );

      // Hiển thị danh sách thuốc
      modelThuoc.setRowCount(0);
      double tongTien = 0;

      for (CTDonThuocDTO ct : danhSachThuoc[0]) {
        ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
        if (thuoc != null) {
          double thanhTien = thuoc.getDonGiaBan() * ct.getSoluong();
          tongTien += thanhTien;

          modelThuoc.addRow(
            new Object[] {
              thuoc.getMaThuoc(),
              thuoc.getTenThuoc(),
              ct.getSoluong(),
              String.format("%,.0f", thuoc.getDonGiaBan()),
              String.format("%,.0f", thanhTien),
            }
          );
        }
      }

      lblTongTien.setText(String.format("TỔNG TIỀN: %,.0f VNĐ", tongTien));
      btnThanhToan.setEnabled(true);

      JOptionPane.showMessageDialog(
        dialog,
        "✅ Tìm thấy đơn thuốc!\nSố loại thuốc: " + danhSachThuoc[0].size()
      );
    });

    // Action thanh toán
    btnThanhToan.addActionListener(ev -> {
      if (donThuocHienTai[0] == null || danhSachThuoc[0].isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Vui lòng tìm đơn thuốc trước!");
        return;
      }

      String tenKhach = txtTenKhach.getText().trim();
      String sdt = txtSDT.getText().trim();

      if (tenKhach.isEmpty() || sdt.isEmpty()) {
        JOptionPane.showMessageDialog(
          dialog,
          "Vui lòng nhập đầy đủ thông tin khách hàng!"
        );
        return;
      }

      // Kiểm tra tồn kho
      for (CTDonThuocDTO ct : danhSachThuoc[0]) {
        ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
        if (thuoc == null) {
          JOptionPane.showMessageDialog(
            dialog,
            "Không tìm thấy thuốc: " + ct.getMaThuoc()
          );
          return;
        }
        if (thuoc.getSoLuongTon() < ct.getSoluong()) {
          JOptionPane.showMessageDialog(
            dialog,
            "Thuốc " +
              thuoc.getTenThuoc() +
              " không đủ tồn kho!\nYêu cầu: " +
              ct.getSoluong() +
              ", Tồn: " +
              thuoc.getSoLuongTon()
          );
          return;
        }
      }

      int confirm = JOptionPane.showConfirmDialog(
        dialog,
        "Xác nhận thanh toán bằng TIỀN MẶT?\nTổng tiền: " +
          lblTongTien.getText().replace("TỔNG TIỀN: ", ""),
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
      );

      if (confirm != JOptionPane.YES_OPTION) return;

      try {
        // Tạo hóa đơn
        HoaDonThuocDTO hoaDon = new HoaDonThuocDTO();
        hoaDon.setMaHoaDon(""); // Auto-generate
        hoaDon.setMaDonThuoc(donThuocHienTai[0].getMaDonThuoc());
        hoaDon.setTenBenhNhan(tenKhach);
        hoaDon.setSdtBenhNhan(sdt);
        hoaDon.setNgayLap(LocalDateTime.now());

        // Tính tổng tiền
        double tongTien = 0;
        for (CTDonThuocDTO ct : danhSachThuoc[0]) {
          ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
          if (thuoc != null) {
            tongTien += thuoc.getDonGiaBan() * ct.getSoluong();
          }
        }

        hoaDon.setTongTien(tongTien);
        hoaDon.setTrangThaiThanhToan("Đã thanh toán");
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setTrangThaiLayThuoc("ĐÃ HOÀN THÀNH");
        hoaDon.setGhiChu("Thanh toán tiền mặt");

        boolean insertHD = hdBUS.addHoaDonThuoc(hoaDon);
        if (!insertHD) {
          JOptionPane.showMessageDialog(dialog, "Lỗi tạo hóa đơn!");
          return;
        }

        // Lấy mã hóa đơn vừa tạo
        List<HoaDonThuocDTO> dsHD = hdBUS.getAllHoaDonThuoc();
        String maHoaDon = dsHD.get(dsHD.size() - 1).getMaHoaDon();

        // Insert chi tiết và trừ tồn kho
        for (CTDonThuocDTO ct : danhSachThuoc[0]) {
          ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());

          CTHDThuocDTO ctHD = new CTHDThuocDTO();
          ctHD.setMaCTHDThuoc(""); // Auto-generate
          ctHD.setMaHoaDon(maHoaDon);
          ctHD.setMaThuoc(ct.getMaThuoc());
          ctHD.setSoLuong(ct.getSoluong());
          ctHD.setDonGia(thuoc.getDonGiaBan());
          ctHD.setThanhTien(thuoc.getDonGiaBan() * ct.getSoluong());

          boolean insertCT = cthdBUS.addDetailMedicine(ctHD);
          if (!insertCT) {
            JOptionPane.showMessageDialog(dialog, "Lỗi thêm chi tiết hóa đơn!");
            return;
          }

          // Trừ tồn kho
          boolean truKho = thuocBUS.truSoLuongTon(
            ct.getMaThuoc(),
            ct.getSoluong()
          );
          if (!truKho) {
            JOptionPane.showMessageDialog(
              dialog,
              "Lỗi trừ tồn kho: " + thuoc.getTenThuoc()
            );
            return;
          }
        }

        String message = String.format(
          "✅ THANH TOÁN THÀNH CÔNG!\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📋 Mã hóa đơn: %s\n" +
            "📋 Mã đơn thuốc: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━\n" +
            "💰 Tổng tiền: %,.0f VNĐ\n" +
            "💵 Hình thức: TIỀN MẶT\n" +
            "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Đã trừ tồn kho!\nCảm ơn quý khách!",
          maHoaDon,
          donThuocHienTai[0].getMaDonThuoc(),
          tongTien
        );

        JOptionPane.showMessageDialog(dialog, message);
        dialog.dispose();
        refreshData();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
        ex.printStackTrace();
      }
    });

    dialog.setVisible(true);
  }

  public void refreshData() {
    btReloadAction(null);
  }
}
