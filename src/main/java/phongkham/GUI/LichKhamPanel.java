package phongkham.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import phongkham.BUS.LichKhamBUS;
import phongkham.DTO.LichKhamDTO;
import phongkham.Utils.ExcelExport;
import phongkham.Utils.PdfExport;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;

public class LichKhamPanel extends JPanel {

  private JTextField txtTimKiem;
  private JComboBox<String> cboTrangThai;
  private JTable tableLichKham;
  private DefaultTableModel tableModel;
  private LichKhamBUS lichKhamBUS;

  public LichKhamPanel() {
    lichKhamBUS = new LichKhamBUS();
    initComponents();
    loadData();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title
    JLabel lblTitle = new JLabel("QUẢN LÝ LỊCH KHÁM");
    lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

    // Search panel
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    searchPanel.add(new JLabel("Tìm kiếm:"));
    txtTimKiem = new JTextField(20);
    searchPanel.add(txtTimKiem);

    searchPanel.add(new JLabel("Trạng thái:"));
    cboTrangThai = new JComboBox<>(
      new String[] {
        "Tất cả",
        "Chờ xác nhận",
        "Đã xác nhận",
        "Đang khám",
        "Hoàn thành",
        "Đã hủy",
      }
    );
    searchPanel.add(cboTrangThai);

    JButton btnTimKiem = new JButton("Tìm kiếm");
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnThongKe = new JButton("Thống kê");
    JButton btnXuatExcel = new JButton("Xuất Excel");
    JButton btnXuatPDF = new JButton("Xuất PDF");

    btnTimKiem.addActionListener(e -> timKiem());
    btnLamMoi.addActionListener(e -> loadData());
    btnThongKe.addActionListener(e -> showThongKe());

    searchPanel.add(btnTimKiem);
    searchPanel.add(btnLamMoi);
    searchPanel.add(btnThongKe);
    searchPanel.add(btnXuatExcel);
    searchPanel.add(btnXuatPDF);

    btnXuatExcel.addActionListener(e -> xuatBaoCaoExcel());
    btnXuatPDF.addActionListener(e -> xuatBaoCaoPdf());

    // Table
    String[] columns = {
      "Mã lịch khám",
      "Mã gói",
      "Mã bác sĩ",
      "Thời gian bắt đầu",
      "Thời gian kết thúc",
      "Trạng thái",
      "Mã định danh",
    };

    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tableLichKham = new JTable(tableModel);
    tableLichKham.setRowHeight(25);
    tableLichKham.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableLichKham
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
            Component c = super.getTableCellRendererComponent(
              table,
              value,
              isSelected,
              hasFocus,
              row,
              column
            );
            if (!isSelected) {
              c.setForeground(StatusColorUtil.lichKham(String.valueOf(value)));
            }
            return c;
          }
        }
      );

    JScrollPane scrollPane = new JScrollPane(tableLichKham);

    // Button panel
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton btnXacNhan = new JButton("Xác nhận");
    JButton btnTuChoi = new JButton("Từ chối lịch");
    JButton btnBatDau = new JButton("Bắt đầu khám");
    JButton btnHuy = new JButton("Hủy lịch");

    btnXacNhan.addActionListener(e -> xacNhanLichKham());
    btnTuChoi.addActionListener(e -> tuChoiLichKham());
    btnBatDau.addActionListener(e -> batDauKham());
    btnHuy.addActionListener(e -> huyLichKham());

    btnPanel.add(btnXacNhan);
    btnPanel.add(btnTuChoi);
    btnPanel.add(btnBatDau);
    btnPanel.add(btnHuy);

    // Key listener for search
    txtTimKiem.addKeyListener(
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            timKiem();
          }
        }
      }
    );

    cboTrangThai.addActionListener(e -> timKiem());

    // Layout
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(lblTitle, BorderLayout.NORTH);
    topPanel.add(searchPanel, BorderLayout.CENTER);

    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(scrollPane, BorderLayout.CENTER);
    centerPanel.add(btnPanel, BorderLayout.SOUTH);

    add(topPanel, BorderLayout.NORTH);
    add(centerPanel, BorderLayout.CENTER);
  }

  private void loadData() {
    hienThiDanhSach(lichKhamBUS.getAll());
  }

  private void timKiem() {
    String keyword = txtTimKiem.getText().trim();
    String trangThai = (String) cboTrangThai.getSelectedItem();
    hienThiDanhSach(layDanhSachTheoBoLoc(keyword, trangThai));
  }

  private void huyLichKham() {
    String maLichKham = layMaLichDangChon("Vui lòng chọn lịch khám cần hủy!");
    if (maLichKham == null) {
      return;
    }

    if (
      xacNhanHanhDong(
        "Bạn có chắc muốn hủy lịch khám " + maLichKham + "?",
        "Xác nhận hủy"
      )
    ) {
      String result = lichKhamBUS.huyLichKham(maLichKham);
      JOptionPane.showMessageDialog(this, result);

      if (result.contains("thành công")) {
        loadData();
      }
    }
  }

  private void xacNhanLichKham() {
    String maLichKham = layMaLichDangChon(
      "Vui lòng chọn lịch khám cần xác nhận!"
    );
    if (maLichKham == null) {
      return;
    }
    LichKhamDTO lk = lichKhamBUS.getById(maLichKham);
    if (lk == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy lịch khám!");
      return;
    }
    String trangThaiHienTai = StatusNormalizer.normalizeLichKhamStatus(
      lk.getTrangThai()
    );

    if (!("CHO_XAC_NHAN".equals(trangThaiHienTai))) {
      JOptionPane.showMessageDialog(
        this,
        "Chỉ có thể xác nhận lịch ở trạng thái chờ xác nhận!"
      );
      return;
    }

    if (xacNhanHanhDong("Xác nhận lịch khám " + maLichKham + "?", "Xác nhận")) {
      lichKhamBUS.updateTrangThai(maLichKham, "DA_XAC_NHAN");
      JOptionPane.showMessageDialog(this, "Đã xác nhận lịch khám thành công!");
      loadData();
    }
  }

  private void tuChoiLichKham() {
    String maLichKham = layMaLichDangChon(
      "Vui lòng chọn lịch khám cần từ chối!"
    );
    if (maLichKham == null) {
      return;
    }
    LichKhamDTO lk = lichKhamBUS.getById(maLichKham);
    if (lk == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy lịch khám!");
      return;
    }
    String trangThaiHienTai = StatusNormalizer.normalizeLichKhamStatus(
      lk.getTrangThai()
    );
    if (!("CHO_XAC_NHAN".equals(trangThaiHienTai))) {
      JOptionPane.showMessageDialog(
        this,
        "Chỉ có thể từ chối lịch đang chờ xác nhận."
      );
      return;
    }

    if (
      xacNhanHanhDong(
        "Xác nhận từ chối lịch khám " + maLichKham + "?",
        "Xác nhận"
      )
    ) {
      String result = lichKhamBUS.updateTrangThai(maLichKham, "DA_HUY");
      JOptionPane.showMessageDialog(
        this,
        result.contains("thành công") ? "Đã từ chối lịch khám." : result
      );
      if (result.contains("thành công")) {
        loadData();
      }
    }
  }

  private void batDauKham() {
    String maLichKham = layMaLichDangChon("Vui lòng chọn lịch khám!");
    if (maLichKham == null) {
      return;
    }
    LichKhamDTO lk = lichKhamBUS.getById(maLichKham);
    if (lk == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy lịch khám!");
      return;
    }
    String trangThaiHienTai = StatusNormalizer.normalizeLichKhamStatus(
      lk.getTrangThai()
    );

    if (!("DA_XAC_NHAN".equals(trangThaiHienTai))) {
      JOptionPane.showMessageDialog(
        this,
        "Chỉ bắt đầu khám khi lịch đã được xác nhận."
      );
      return;
    }

    if (
      xacNhanHanhDong("Bắt đầu khám cho lịch " + maLichKham + "?", "Xác nhận")
    ) {
      String result = lichKhamBUS.updateTrangThai(maLichKham, "DANG_KHAM");
      JOptionPane.showMessageDialog(this, result);
      if (result.contains("thành công")) {
        loadData();
      }
    }
  }

  private ArrayList<LichKhamDTO> layDanhSachTheoBoLoc(
    String keyword,
    String trangThaiHienThi
  ) {
    if ("Tất cả".equals(trangThaiHienThi)) {
      return keyword.isEmpty()
        ? lichKhamBUS.getAll()
        : lichKhamBUS.search(keyword);
    }

    String trangThaiTimKiem = mapTrangThaiLoc(trangThaiHienThi);
    ArrayList<LichKhamDTO> danhSachTheoTrangThai = lichKhamBUS.getByTrangThai(
      trangThaiTimKiem
    );
    if (keyword.isEmpty()) {
      return danhSachTheoTrangThai;
    }

    ArrayList<LichKhamDTO> ketQua = new ArrayList<>();
    String keywordLower = keyword.toLowerCase();
    for (LichKhamDTO lichKham : danhSachTheoTrangThai) {
      if (
        lichKham.getMaLichKham().toLowerCase().contains(keywordLower) ||
        lichKham.getMaBacSi().toLowerCase().contains(keywordLower) ||
        lichKham.getMaGoi().toLowerCase().contains(keywordLower)
      ) {
        ketQua.add(lichKham);
      }
    }
    return ketQua;
  }

  private void hienThiDanhSach(ArrayList<LichKhamDTO> danhSach) {
    tableModel.setRowCount(0);
    for (LichKhamDTO lichKham : danhSach) {
      tableModel.addRow(
        new Object[] {
          lichKham.getMaLichKham(),
          lichKham.getMaGoi(),
          lichKham.getMaBacSi(),
          lichKham.getThoiGianBatDau(),
          lichKham.getThoiGianKetThuc(),
          StatusDisplayUtil.lichKham(lichKham.getTrangThai()),
          lichKham.getMaDinhDanhTam(),
        }
      );
    }
  }

  private String layMaLichDangChon(String messageNeuChuaChon) {
    int selectedRow = tableLichKham.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, messageNeuChuaChon);
      return null;
    }
    return tableModel.getValueAt(selectedRow, 0).toString();
  }

  private boolean xacNhanHanhDong(String message, String title) {
    return (
      JOptionPane.showConfirmDialog(
        this,
        message,
        title,
        JOptionPane.YES_NO_OPTION
      ) ==
      JOptionPane.YES_OPTION
    );
  }

  private void showThongKe() {
    String thongKe = lichKhamBUS.thongKeLichKham();
    JOptionPane.showMessageDialog(
      this,
      thongKe,
      "Thống kê lịch khám",
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  private String mapTrangThaiLoc(String trangThaiHienThi) {
    switch (trangThaiHienThi) {
      case "Chờ xác nhận":
        return "CHO_XAC_NHAN";
      case "Đã xác nhận":
        return "DA_XAC_NHAN";
      case "Đang khám":
        return "DANG_KHAM";
      case "Hoàn thành":
        return "HOAN_THANH";
      case "Đã hủy":
        return "DA_HUY";
      default:
        return trangThaiHienThi;
    }
  }

  private void xuatBaoCaoExcel() {
    String boLoc = taoBoLocText();
    ExcelExport.exportOperationalTableToCsv(tableLichKham, "LichKham", boLoc);
  }

  private void xuatBaoCaoPdf() {
    String boLoc = taoBoLocText();
    PdfExport.exportOperationalTable(tableLichKham, "Lịch khám", boLoc);
  }

  private String taoBoLocText() {
    String keyword = txtTimKiem.getText().trim();
    String trangThai = String.valueOf(cboTrangThai.getSelectedItem());
    if (keyword.isEmpty()) {
      return "Trạng thái: " + trangThai;
    }
    return "Từ khóa: " + keyword + " | Trạng thái: " + trangThai;
  }
}
