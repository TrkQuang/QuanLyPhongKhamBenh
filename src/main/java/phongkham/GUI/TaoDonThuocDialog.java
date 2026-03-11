package phongkham.gui;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.*;
import phongkham.DTO.*;

public class TaoDonThuocDialog extends JDialog {

  // ==================== CONSTANTS ====================
  private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private static final Color DANGER_COLOR = new Color(220, 38, 38);
  private static final Color SUCCESS_COLOR = new Color(34, 197, 94);

  // ==================== DEPENDENCIES ====================
  private final HoaDonThuocPanel parentPanel;
  private final HoaDonThuocBUS hdBUS = new HoaDonThuocBUS();
  private final DonThuocBUS donThuocBUS = new DonThuocBUS();
  private final CTDonThuocBUS ctDonThuocBUS = new CTDonThuocBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final CTHDThuocBUS cthdBUS = new CTHDThuocBUS();

  // ==================== UI COMPONENTS ====================
  private JTextField txtMaDon, txtTenKhach, txtSDT;
  private JLabel lblNgayKe, lblMaHS, lblSoLuongThuoc, lblTongTien;
  private DefaultTableModel modelThuoc;
  private JButton btnThanhToan;

  // ==================== STATE ====================
  private DonThuocDTO donThuocHienTai = null;
  private ArrayList<CTDonThuocDTO> danhSachThuoc = new ArrayList<>();

  // ==================== CONSTRUCTOR ====================
  public TaoDonThuocDialog(Frame parentFrame, HoaDonThuocPanel parentPanel) {
    super(parentFrame, "Tạo hóa đơn từ đơn thuốc", true);
    this.parentPanel = parentPanel;
    initComponents();
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  // ==================== INIT ====================
  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setSize(600, 500);
    setLocationRelativeTo(getParent());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    mainPanel.add(createInputPanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(createInfoPanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(createThuocTablePanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(createKhachHangPanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(createBottomPanel());

    add(mainPanel);
  }

  // ==================== UI BUILDERS ====================
  private JPanel createInputPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn thuốc"));
    txtMaDon = new JTextField(15);
    JButton btnTimDon = createButton("Tìm đơn", PRIMARY_COLOR);
    btnTimDon.addActionListener(e -> timDonThuoc());
    panel.add(new JLabel("Mã đơn thuốc:"));
    panel.add(txtMaDon);
    panel.add(btnTimDon);
    return panel;
  }

  private JPanel createInfoPanel() {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Thông tin"));
    lblNgayKe = new JLabel("-");
    lblMaHS = new JLabel("-");
    lblSoLuongThuoc = new JLabel("-");
    panel.add(new JLabel("Ngày kê:"));
    panel.add(lblNgayKe);
    panel.add(new JLabel("Mã hồ sơ:"));
    panel.add(lblMaHS);
    panel.add(new JLabel("Số loại thuốc:"));
    panel.add(lblSoLuongThuoc);
    return panel;
  }

  private JScrollPane createThuocTablePanel() {
    String[] cols = { "Mã thuốc", "Tên thuốc", "SL", "Đơn giá", "Thành tiền" };
    modelThuoc = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    JTable tableThuoc = new JTable(modelThuoc);
    tableThuoc.setRowHeight(30);
    return new JScrollPane(tableThuoc);
  }

  private JPanel createKhachHangPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
    txtTenKhach = new JTextField(15);
    txtSDT = new JTextField(15);
    panel.add(new JLabel("Tên khách:"));
    panel.add(txtTenKhach);
    panel.add(new JLabel("SĐT:"));
    panel.add(txtSDT);
    return panel;
  }

  private JPanel createBottomPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    lblTongTien = new JLabel("TỔNG TIỀN: 0 VNĐ");
    lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblTongTien.setForeground(DANGER_COLOR);
    btnThanhToan = createButton("💰 THANH TOÁN TIỀN MẶT", SUCCESS_COLOR);
    btnThanhToan.setEnabled(false);
    btnThanhToan.addActionListener(e -> thanhToan());
    panel.add(lblTongTien, BorderLayout.WEST);
    panel.add(btnThanhToan, BorderLayout.EAST);
    return panel;
  }

  // ==================== LOGIC ====================
  private void timDonThuoc() {
    String maDon = txtMaDon.getText().trim();
    if (maDon.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đơn thuốc!");
      return;
    }

    donThuocHienTai = donThuocBUS.searchTheoMa(maDon);
    if (donThuocHienTai == null) {
      JOptionPane.showMessageDialog(
        this,
        "❌ Không tìm thấy đơn thuốc: " + maDon
      );
      return;
    }

    // Kiểm tra đơn đã có hóa đơn chưa
    for (HoaDonThuocDTO hd : hdBUS.getAllHoaDonThuoc()) {
      if (hd.getMaDonThuoc() != null && hd.getMaDonThuoc().equals(maDon)) {
        JOptionPane.showMessageDialog(
          this,
          "⚠️ Đơn thuốc này đã có hóa đơn!\nMã hóa đơn: " + hd.getMaHoaDon()
        );
        return;
      }
    }

    danhSachThuoc = ctDonThuocBUS.getByMaDonThuoc(maDon);
    if (danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Đơn thuốc không có thuốc nào!");
      return;
    }

    lblNgayKe.setText(donThuocHienTai.getNgayKeDon());
    lblMaHS.setText(donThuocHienTai.getMaHoSo());
    lblSoLuongThuoc.setText(danhSachThuoc.size() + " loại");

    hienThiDanhSachThuoc();
    btnThanhToan.setEnabled(true);
    JOptionPane.showMessageDialog(
      this,
      "✅ Tìm thấy đơn thuốc!\nSố loại thuốc: " + danhSachThuoc.size()
    );
  }

  private void hienThiDanhSachThuoc() {
    modelThuoc.setRowCount(0);
    double tongTien = 0;
    for (CTDonThuocDTO ct : danhSachThuoc) {
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
  }

  private void thanhToan() {
    if (donThuocHienTai == null || danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng tìm đơn thuốc trước!");
      return;
    }
    String tenKhach = txtTenKhach.getText().trim();
    String sdt = txtSDT.getText().trim();
    if (tenKhach.isEmpty() || sdt.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập đầy đủ thông tin khách hàng!"
      );
      return;
    }
    if (!kiemTraTonKho()) return;

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận thanh toán bằng TIỀN MẶT?\nTổng tiền: " +
        lblTongTien.getText().replace("TỔNG TIỀN: ", ""),
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );
    if (confirm != JOptionPane.YES_OPTION) return;

    try {
      xuLyThanhToan(tenKhach, sdt);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private boolean kiemTraTonKho() {
    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc == null) {
        JOptionPane.showMessageDialog(
          this,
          "Không tìm thấy thuốc: " + ct.getMaThuoc()
        );
        return false;
      }
      if (thuoc.getSoLuongTon() < ct.getSoluong()) {
        JOptionPane.showMessageDialog(
          this,
          "Thuốc " +
            thuoc.getTenThuoc() +
            " không đủ tồn kho!\n" +
            "Yêu cầu: " +
            ct.getSoluong() +
            ", Tồn: " +
            thuoc.getSoLuongTon()
        );
        return false;
      }
    }
    return true;
  }

  private void xuLyThanhToan(String tenKhach, String sdt) {
    double tongTien = tinhTongTien();

    HoaDonThuocDTO hoaDon = new HoaDonThuocDTO();
    hoaDon.setMaHoaDon("");
    hoaDon.setMaDonThuoc(donThuocHienTai.getMaDonThuoc());
    hoaDon.setTenBenhNhan(tenKhach);
    hoaDon.setSdtBenhNhan(sdt);
    hoaDon.setNgayLap(LocalDateTime.now());
    hoaDon.setTongTien(tongTien);
    hoaDon.setTrangThaiThanhToan("Đã thanh toán");
    hoaDon.setNgayThanhToan(LocalDateTime.now());
    hoaDon.setTrangThaiLayThuoc("ĐÃ HOÀN THÀNH");
    hoaDon.setGhiChu("Thanh toán tiền mặt");

    if (!hdBUS.addHoaDonThuoc(hoaDon)) {
      JOptionPane.showMessageDialog(this, "Lỗi tạo hóa đơn!");
      return;
    }

    List<HoaDonThuocDTO> dsHD = hdBUS.getAllHoaDonThuoc();
    String maHoaDon = dsHD.get(dsHD.size() - 1).getMaHoaDon();

    if (!insertChiTietHoaDon(maHoaDon)) return;

    JOptionPane.showMessageDialog(
      this,
      String.format(
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
        donThuocHienTai.getMaDonThuoc(),
        tongTien
      )
    );

    dispose();
    parentPanel.refreshData();
  }

  private double tinhTongTien() {
    double total = 0;
    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc != null) total += thuoc.getDonGiaBan() * ct.getSoluong();
    }
    return total;
  }

  private boolean insertChiTietHoaDon(String maHoaDon) {
    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      CTHDThuocDTO ctHD = new CTHDThuocDTO();
      ctHD.setMaCTHDThuoc("");
      ctHD.setMaHoaDon(maHoaDon);
      ctHD.setMaThuoc(ct.getMaThuoc());
      ctHD.setSoLuong(ct.getSoluong());
      ctHD.setDonGia(thuoc.getDonGiaBan());
      ctHD.setThanhTien(thuoc.getDonGiaBan() * ct.getSoluong());
      if (!cthdBUS.addDetailMedicine(ctHD)) {
        JOptionPane.showMessageDialog(this, "Lỗi thêm chi tiết hóa đơn!");
        return false;
      }
      if (!thuocBUS.truSoLuongTon(ct.getMaThuoc(), ct.getSoluong())) {
        JOptionPane.showMessageDialog(
          this,
          "Lỗi trừ tồn kho: " + thuoc.getTenThuoc()
        );
        return false;
      }
    }
    return true;
  }

  // ==================== HELPERS ====================
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
}
