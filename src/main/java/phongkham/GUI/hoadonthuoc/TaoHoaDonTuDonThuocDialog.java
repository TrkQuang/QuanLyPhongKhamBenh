package phongkham.gui.hoadonthuoc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTDonThuocBUS;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.DonThuocBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTDonThuocDTO;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.DonThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.StatusNormalizer;

/**
 * Dialog tách riêng cho luồng tạo hóa đơn từ đơn thuốc.
 */
public class TaoHoaDonTuDonThuocDialog {

  private final HoaDonThuocBUS hoaDonBUS;
  private final DonThuocBUS donThuocBUS;
  private final CTDonThuocBUS ctDonThuocBUS;
  private final ThuocBUS thuocBUS;
  private final CTHDThuocBUS cthdBUS;
  private final Runnable onSuccess;

  private final Color mauChinh = new Color(37, 99, 235);
  private final Color mauThanhCong = new Color(34, 197, 94);
  private final Color mauNguyHiem = new Color(220, 38, 38);

  private JDialog dialog;
  private JTextField txtMaDon;
  private JTextField txtTenKhach;
  private JTextField txtSdt;
  private JLabel lblNgayKe;
  private JLabel lblMaHs;
  private JLabel lblSoLoaiThuoc;
  private JLabel lblTongTien;
  private JButton btnThanhToan;

  private DefaultTableModel modelThuoc;

  private DonThuocDTO donThuocHienTai;
  private final ArrayList<CTDonThuocDTO> danhSachThuoc = new ArrayList<>();

  public TaoHoaDonTuDonThuocDialog(
    HoaDonThuocBUS hoaDonBUS,
    DonThuocBUS donThuocBUS,
    CTDonThuocBUS ctDonThuocBUS,
    ThuocBUS thuocBUS,
    CTHDThuocBUS cthdBUS,
    Runnable onSuccess
  ) {
    this.hoaDonBUS = hoaDonBUS;
    this.donThuocBUS = donThuocBUS;
    this.ctDonThuocBUS = ctDonThuocBUS;
    this.thuocBUS = thuocBUS;
    this.cthdBUS = cthdBUS;
    this.onSuccess = onSuccess;
  }

  public void show(Component parent) {
    dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(parent),
      "Tạo hóa đơn từ đơn thuốc",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(640, 560);
    dialog.setLocationRelativeTo(parent);
    dialog.add(buildMainPanel());
    dialog.setVisible(true);
  }

  private JPanel buildMainPanel() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    mainPanel.add(buildInputPanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(buildInfoPanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(buildThuocTablePanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(buildKhachHangPanel());
    mainPanel.add(Box.createVerticalStrut(10));
    mainPanel.add(buildBottomPanel());

    return mainPanel;
  }

  private JPanel buildInputPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn thuốc"));

    txtMaDon = new JTextField(15);
    JButton btnTimDon = createButton("Tìm đơn", mauChinh);
    btnTimDon.addActionListener(e -> timDonThuoc());

    panel.add(new JLabel("Mã đơn thuốc:"));
    panel.add(txtMaDon);
    panel.add(btnTimDon);
    return panel;
  }

  private JPanel buildInfoPanel() {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn"));

    lblNgayKe = new JLabel("-");
    lblMaHs = new JLabel("-");
    lblSoLoaiThuoc = new JLabel("-");

    panel.add(new JLabel("Ngày kê:"));
    panel.add(lblNgayKe);
    panel.add(new JLabel("Mã hồ sơ:"));
    panel.add(lblMaHs);
    panel.add(new JLabel("Số loại thuốc:"));
    panel.add(lblSoLoaiThuoc);

    return panel;
  }

  private JScrollPane buildThuocTablePanel() {
    String[] cols = { "Mã thuốc", "Tên thuốc", "SL", "Đơn giá", "Thành tiền" };
    modelThuoc = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable tableThuoc = new JTable(modelThuoc);
    tableThuoc.setRowHeight(30);
    return new JScrollPane(tableThuoc);
  }

  private JPanel buildKhachHangPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

    txtTenKhach = new JTextField(15);
    txtSdt = new JTextField(15);

    panel.add(new JLabel("Tên khách:"));
    panel.add(txtTenKhach);
    panel.add(new JLabel("SĐT:"));
    panel.add(txtSdt);

    return panel;
  }

  private JPanel buildBottomPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    lblTongTien = new JLabel("TỔNG TIỀN: 0 VNĐ");
    lblTongTien.setForeground(mauNguyHiem);

    btnThanhToan = createButton("TẠO HÓA ĐƠN", mauThanhCong);
    btnThanhToan.setEnabled(false);
    btnThanhToan.setPreferredSize(new Dimension(240, 36));
    btnThanhToan.addActionListener(e -> taoHoaDonTuDonThuoc());

    panel.add(lblTongTien, BorderLayout.WEST);
    panel.add(btnThanhToan, BorderLayout.EAST);
    return panel;
  }

  private JButton createButton(String text, Color bg) {
    JButton btn = new JButton(text);
    btn.setBackground(bg);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    return btn;
  }

  private void timDonThuoc() {
    String maDonNhap = txtMaDon.getText().trim();
    String maDon = chuanHoaMaDonThuoc(maDonNhap);
    if (maDon.isEmpty()) {
      JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã đơn thuốc!");
      return;
    }

    txtMaDon.setText(maDon);

    donThuocBUS.loadData();
    donThuocHienTai = donThuocBUS.searchTheoMa(maDon);
    if (donThuocHienTai == null) {
      JOptionPane.showMessageDialog(
        dialog,
        "Không tìm thấy đơn thuốc: " + maDonNhap
      );
      return;
    }

    for (HoaDonThuocDTO hd : hoaDonBUS.getAllHoaDonThuoc()) {
      if (hd.getMaDonThuoc() != null && hd.getMaDonThuoc().equals(maDon)) {
        JOptionPane.showMessageDialog(
          dialog,
          "Đơn thuốc này đã có hóa đơn!\nMã hóa đơn: " + hd.getMaHoaDon()
        );
        return;
      }
    }

    danhSachThuoc.clear();
    danhSachThuoc.addAll(ctDonThuocBUS.getByMaDonThuoc(maDon));
    if (danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(dialog, "Đơn thuốc không có thuốc nào!");
      return;
    }

    hienThiDonThuocVaTongTien();
  }

  private void hienThiDonThuocVaTongTien() {
    lblNgayKe.setText(donThuocHienTai.getNgayKeDon());
    lblMaHs.setText(donThuocHienTai.getMaHoSo());
    lblSoLoaiThuoc.setText(danhSachThuoc.size() + " loại");

    modelThuoc.setRowCount(0);
    double tongTien = 0;

    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc == null) {
        continue;
      }
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

    lblTongTien.setText(String.format("TỔNG TIỀN: %,.0f VNĐ", tongTien));
    btnThanhToan.setEnabled(true);

    JOptionPane.showMessageDialog(
      dialog,
      "Tìm thấy đơn thuốc!\nSố loại thuốc: " + danhSachThuoc.size()
    );
  }

  private void taoHoaDonTuDonThuoc() {
    if (donThuocHienTai == null || danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(dialog, "Vui lòng tìm đơn thuốc trước!");
      return;
    }

    String tenKhach = txtTenKhach.getText().trim();
    String sdt = txtSdt.getText().trim();
    if (tenKhach.isEmpty() || sdt.isEmpty()) {
      JOptionPane.showMessageDialog(
        dialog,
        "Vui lòng nhập đầy đủ thông tin khách hàng!"
      );
      return;
    }

    if (!kiemTraTonKhoDayDu()) {
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      dialog,
      "Xác nhận tạo hóa đơn từ đơn thuốc này?\nTổng tiền: " +
        lblTongTien.getText().replace("TỔNG TIỀN: ", ""),
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    try {
      HoaDonThuocDTO hoaDon = taoHoaDon(tenKhach, sdt);
      boolean insertHd = hoaDonBUS.addHoaDonThuoc(hoaDon);
      if (!insertHd) {
        JOptionPane.showMessageDialog(dialog, "Lỗi tạo hóa đơn!");
        return;
      }

      String maHoaDon = hoaDon.getMaHoaDon();
      if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
        JOptionPane.showMessageDialog(
          dialog,
          "Không lấy được mã hóa đơn vừa tạo!"
        );
        return;
      }

      if (!themChiTietHoaDon(maHoaDon)) {
        JOptionPane.showMessageDialog(dialog, "Lỗi thêm chi tiết hóa đơn!");
        return;
      }

      JOptionPane.showMessageDialog(
        dialog,
        String.format(
          "TẠO HÓA ĐƠN THÀNH CÔNG!\n\nMã hóa đơn: %s\nMã đơn thuốc: %s\n%s\nTrạng thái: Chưa thanh toán / Chờ lấy",
          maHoaDon,
          donThuocHienTai.getMaDonThuoc(),
          lblTongTien.getText()
        )
      );

      dialog.dispose();
      if (onSuccess != null) {
        onSuccess.run();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private HoaDonThuocDTO taoHoaDon(String tenKhach, String sdt) {
    HoaDonThuocDTO hoaDon = new HoaDonThuocDTO();
    hoaDon.setMaHoaDon("");
    hoaDon.setMaDonThuoc(donThuocHienTai.getMaDonThuoc());
    hoaDon.setTenBenhNhan(tenKhach);
    hoaDon.setSdtBenhNhan(sdt);
    hoaDon.setNgayLap(LocalDateTime.now());
    hoaDon.setTongTien(tinhTongTienDonThuoc());
    hoaDon.setTrangThaiThanhToan(StatusNormalizer.CHUA_THANH_TOAN);
    hoaDon.setNgayThanhToan(null);
    hoaDon.setTrangThaiLayThuoc(StatusNormalizer.CHO_LAY);
    hoaDon.setGhiChu("Tạo từ đơn thuốc");
    return hoaDon;
  }

  private String chuanHoaMaDonThuoc(String raw) {
    if (raw == null) {
      return "";
    }
    String value = raw.trim().toUpperCase();
    if (value.isEmpty()) {
      return "";
    }
    if (!value.startsWith("DT")) {
      value = "DT" + value;
    }
    return value;
  }

  private double tinhTongTienDonThuoc() {
    double tongTien = 0;
    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc != null) {
        tongTien += thuoc.getDonGiaBan() * ct.getSoluong();
      }
    }
    return tongTien;
  }

  private boolean themChiTietHoaDon(String maHoaDon) {
    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc == null) {
        return false;
      }

      CTHDThuocDTO ctHd = new CTHDThuocDTO();
      ctHd.setMaCTHDThuoc("");
      ctHd.setMaHoaDon(maHoaDon);
      ctHd.setMaThuoc(ct.getMaThuoc());
      ctHd.setSoLuong(ct.getSoluong());
      ctHd.setDonGia(thuoc.getDonGiaBan());
      ctHd.setThanhTien(thuoc.getDonGiaBan() * ct.getSoluong());

      if (!cthdBUS.addDetailMedicine(ctHd)) {
        return false;
      }
    }
    return true;
  }

  private boolean kiemTraTonKhoDayDu() {
    for (CTDonThuocDTO ct : danhSachThuoc) {
      ThuocDTO thuoc = thuocBUS.getByMa(ct.getMaThuoc());
      if (thuoc == null) {
        JOptionPane.showMessageDialog(
          dialog,
          "Không tìm thấy thuốc: " + ct.getMaThuoc()
        );
        return false;
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
        return false;
      }
    }
    return true;
  }
}
