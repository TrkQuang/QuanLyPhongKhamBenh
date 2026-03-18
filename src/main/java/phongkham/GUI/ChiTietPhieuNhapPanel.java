package phongkham.gui;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;

public class ChiTietPhieuNhapPanel extends JPanel {

  private String maPN;
  private PhieuNhapPanel parentPanel;

  private JTable table;
  private DefaultTableModel model;
  private JLabel lblTongTien;
  private JLabel lblTrangThai;
  private JButton btnThemThuoc;
  private JButton btnSuaDong;
  private JButton btnXoaDong;
  private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
  private CTPhieuNhapBUS bus = new CTPhieuNhapBUS();

  public ChiTietPhieuNhapPanel(String maPN, PhieuNhapPanel parentPanel) {
    this.maPN = maPN;
    this.parentPanel = parentPanel;

    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(createTopPanel(), BorderLayout.NORTH);
    add(createCenterPanel(), BorderLayout.CENTER);
    add(createBottomPanel(), BorderLayout.SOUTH);

    loadData();
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel lblTitle = new JLabel("CHI TIẾT PHIẾU NHẬP: " + maPN);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

    lblTrangThai = new JLabel();
    lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.add(lblTitle);
    leftPanel.add(lblTrangThai);

    JButton btnBack = new JButton("← Quay lại");
    btnBack.addActionListener(e -> parentPanel.showMainView());

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(btnBack, BorderLayout.EAST);

    return panel;
  }

  private JPanel createCenterPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createTablePanel(), BorderLayout.CENTER);
    return panel;
  }

  private JScrollPane createTablePanel() {
    String[] columns = {
      "Mã CTPN",
      "Mã thuốc",
      "Số lượng",
      "Đơn giá",
      "Hạn sử dụng",
      "Thành tiền",
    };

    model = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    table = new JTable(model);
    table.setRowHeight(28);
    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        capNhatTrangThaiNutDong();
      });

    DefaultTableCellRenderer right = new DefaultTableCellRenderer();
    right.setHorizontalAlignment(SwingConstants.RIGHT);
    for (int i = 0; i < columns.length; i++) {
      table.getColumnModel().getColumn(i).setCellRenderer(right);
    }

    JTableHeader header = table.getTableHeader();
    header.setReorderingAllowed(false);

    return new JScrollPane(table);
  }

  private JPanel createBottomPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    lblTongTien = new JLabel("Tổng tiền: 0 đ");
    lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.add(lblTongTien);
    rightPanel.add(Box.createHorizontalStrut(20));

    PhieuNhapDTO pn = pnBUS.getById(maPN);

    if (pn != null) {
      String trangThai = StatusNormalizer.normalizePhieuNhapStatus(
        pn.getTrangThai()
      );

      if ("CHO_DUYET".equals(trangThai)) {
        btnThemThuoc = new JButton("Thêm thuốc");
        btnSuaDong = new JButton("Sửa dòng");
        btnXoaDong = new JButton("Xóa dòng");

        btnThemThuoc.addActionListener(e -> themDong());
        btnSuaDong.addActionListener(e -> suaDong());
        btnXoaDong.addActionListener(e -> xoaDong());

        rightPanel.add(btnThemThuoc);
        rightPanel.add(btnSuaDong);
        rightPanel.add(btnXoaDong);
        capNhatTrangThaiNutDong();
      }

      if ("DA_DUYET".equals(trangThai)) {
        JButton btnNhapKho = new JButton("Nhập kho");
        btnNhapKho.addActionListener(e -> xacNhanNhapKho());
        rightPanel.add(btnNhapKho);
      }
    }

    panel.add(rightPanel, BorderLayout.EAST);
    return panel;
  }

  private void themDong() {
    ThuocNhapDialogInput input = hienThiDialogThuoc(null);
    if (input == null) {
      return;
    }

    boolean success = bus.insert(
      taoMaCTPN(),
      maPN,
      input.maThuoc,
      input.soLuong,
      input.donGia,
      input.hanSuDung
    );

    if (success) {
      JOptionPane.showMessageDialog(this, "Thêm thành công!");
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "Không thể thêm!");
    }
  }

  private void suaDong() {
    int row = table.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa!");
      return;
    }
    String maCTPN = model.getValueAt(row, 0).toString();

    ThuocNhapDialogInput input = hienThiDialogThuoc(
      new ThuocNhapDialogInput(
        model.getValueAt(row, 1).toString(),
        Integer.parseInt(model.getValueAt(row, 2).toString()),
        parseBigDecimalFromFormatted(model.getValueAt(row, 3).toString()),
        parseHanSuDung(model.getValueAt(row, 4).toString())
      )
    );
    if (input == null) {
      return;
    }

    boolean success = bus.update(maCTPN, input.soLuong, input.donGia);

    if (success) {
      JOptionPane.showMessageDialog(this, "Sửa thành công!");
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "Không thể sửa!");
    }
  }

  private void xoaDong() {
    int row = table.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa!");
      return;
    }
    String maCTPN = model.getValueAt(row, 0).toString();
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận xóa?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      boolean success = bus.delete(maCTPN);
      if (success) {
        JOptionPane.showMessageDialog(this, "Đã xóa!");
        loadData();
      } else {
        JOptionPane.showMessageDialog(this, "Không thể xóa!");
      }
    }
  }

  private void xacNhanNhapKho() {
    boolean success = bus.xacNhanNhapKho(maPN);

    if (success) {
      JOptionPane.showMessageDialog(this, "Nhập kho thành công!");
      parentPanel.showMainView();
    } else {
      JOptionPane.showMessageDialog(
        this,
        "Không thể nhập kho!\n" + "- Phiếu đã nhập\n" + "- Hoặc chưa được duyệt"
      );
    }
  }

  private void loadData() {
    List<CTPhieuNhapDTO> list = bus.getByMaPhieuNhap(maPN);
    model.setRowCount(0);
    BigDecimal tong = BigDecimal.ZERO;
    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    for (CTPhieuNhapDTO ct : list) {
      BigDecimal thanhTien = ct
        .getDonGiaNhap()
        .multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));

      tong = tong.add(thanhTien);

      model.addRow(
        new Object[] {
          ct.getMaCTPN(),
          ct.getMaThuoc(),
          ct.getSoLuongNhap(),
          formatter.format(ct.getDonGiaNhap()),
          ct.getHanSuDung() == null ? "" : ct.getHanSuDung().format(dtf),
          formatter.format(thanhTien),
        }
      );
    }

    lblTongTien.setText("Tổng tiền: " + formatter.format(tong) + " đ");
    PhieuNhapDTO pn = pnBUS.getById(maPN);

    if (pn != null) {
      String trangThai = StatusNormalizer.normalizePhieuNhapStatus(
        pn.getTrangThai()
      );
      lblTrangThai.setText(
        "Trạng thái: " + StatusDisplayUtil.phieuNhap(trangThai)
      );

      switch (trangThai) {
        case "DA_NHAP":
          lblTrangThai.setForeground(StatusColorUtil.phieuNhap(trangThai));
          break;
        case "DA_DUYET":
          lblTrangThai.setForeground(StatusColorUtil.phieuNhap(trangThai));
          break;
        default:
          lblTrangThai.setForeground(StatusColorUtil.phieuNhap(trangThai));
      }
    }

    capNhatTrangThaiNutDong();
  }

  private void capNhatTrangThaiNutDong() {
    if (btnSuaDong == null || btnXoaDong == null) {
      return;
    }
    boolean coDongDuocChon = table != null && table.getSelectedRow() >= 0;
    btnSuaDong.setEnabled(coDongDuocChon);
    btnXoaDong.setEnabled(coDongDuocChon);
  }

  private String taoMaCTPN() {
    return "CTPN" + System.currentTimeMillis();
  }

  private ThuocNhapDialogInput hienThiDialogThuoc(
    ThuocNhapDialogInput macDinh
  ) {
    ArrayList<ThuocDTO> dsThuoc = new ThuocBUS().list();
    if (dsThuoc == null || dsThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Không có thuốc để thêm!");
      return null;
    }

    JComboBox<String> cbThuoc = new JComboBox<>();
    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    int selectedIndex = 0;
    for (int i = 0; i < dsThuoc.size(); i++) {
      ThuocDTO t = dsThuoc.get(i);
      cbThuoc.addItem(
        t.getMaThuoc() +
          " - " +
          t.getTenThuoc() +
          " (Giá bán: " +
          formatter.format(t.getDonGiaBan()) +
          " đ)"
      );
      if (macDinh != null && t.getMaThuoc().equals(macDinh.maThuoc)) {
        selectedIndex = i;
      }
    }
    cbThuoc.setSelectedIndex(selectedIndex);

    JSpinner spinnerSoLuong = new JSpinner(
      new SpinnerNumberModel(
        macDinh == null ? 1 : macDinh.soLuong,
        1,
        100000,
        1
      )
    );
    JTextField txtDonGia = new JTextField(
      macDinh == null
        ? String.valueOf(dsThuoc.get(selectedIndex).getDonGiaBan())
        : macDinh.donGia.toPlainString()
    );
    JTextField txtHanSuDung = new JTextField(
      macDinh == null || macDinh.hanSuDung == null
        ? ""
        : macDinh.hanSuDung
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    );

    cbThuoc.addActionListener(e -> {
      int idx = cbThuoc.getSelectedIndex();
      if (idx >= 0 && idx < dsThuoc.size()) {
        txtDonGia.setText(String.valueOf(dsThuoc.get(idx).getDonGiaBan()));
      }
    });

    Object[] message = {
      "Chọn thuốc:",
      cbThuoc,
      "Số lượng:",
      spinnerSoLuong,
      "Đơn giá nhập:",
      txtDonGia,
      "Hạn sử dụng (dd/MM/yyyy, tùy chọn):",
      txtHanSuDung,
    };

    int option = JOptionPane.showConfirmDialog(
      this,
      message,
      macDinh == null ? "Thêm thuốc vào phiếu" : "Sửa dòng thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (option != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      int idx = cbThuoc.getSelectedIndex();
      if (idx < 0 || idx >= dsThuoc.size()) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc!");
        return null;
      }

      int soLuong = (int) spinnerSoLuong.getValue();
      BigDecimal donGia = new BigDecimal(txtDonGia.getText().trim());
      if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
        JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!");
        return null;
      }

      LocalDateTime hanSuDung = parseHanSuDung(txtHanSuDung.getText().trim());
      if (
        hanSuDung != null && hanSuDung.toLocalDate().isBefore(LocalDate.now())
      ) {
        JOptionPane.showMessageDialog(
          this,
          "Hạn sử dụng phải từ hôm nay trở đi!"
        );
        return null;
      }

      return new ThuocNhapDialogInput(
        dsThuoc.get(idx).getMaThuoc(),
        soLuong,
        donGia,
        hanSuDung
      );
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
      return null;
    }
  }

  private BigDecimal parseBigDecimalFromFormatted(String text) {
    String cleaned = text == null ? "" : text.replaceAll("[^0-9.]", "");
    if (cleaned.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(cleaned);
  }

  private LocalDateTime parseHanSuDung(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    LocalDate date = LocalDate.parse(
      value.trim(),
      DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );
    return date.atStartOfDay();
  }

  private static class ThuocNhapDialogInput {

    private final String maThuoc;
    private final int soLuong;
    private final BigDecimal donGia;
    private final LocalDateTime hanSuDung;

    private ThuocNhapDialogInput(
      String maThuoc,
      int soLuong,
      BigDecimal donGia,
      LocalDateTime hanSuDung
    ) {
      this.maThuoc = maThuoc;
      this.soLuong = soLuong;
      this.donGia = donGia;
      this.hanSuDung = hanSuDung;
    }
  }
}
