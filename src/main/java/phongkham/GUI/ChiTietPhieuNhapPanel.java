package phongkham.gui;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.phieunhap.ThuocNhapDialogHelper;

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
    ThuocNhapDialogHelper.Input input = hienThiDialogThuoc(null);
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

    ThuocNhapDialogHelper.Input input = hienThiDialogThuoc(
      new ThuocNhapDialogHelper.Input(
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

  private ThuocNhapDialogHelper.Input hienThiDialogThuoc(
    ThuocNhapDialogHelper.Input macDinh
  ) {
    return ThuocNhapDialogHelper.show(
      this,
      new phongkham.BUS.ThuocBUS().list(),
      macDinh,
      macDinh == null ? "Thêm thuốc vào phiếu" : "Sửa dòng thuốc"
    );
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
}
