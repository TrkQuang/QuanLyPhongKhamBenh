package phongkham.gui;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;

public class BanThuocPanel extends JPanel {

  private final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private final Color SUCCESS_COLOR = new Color(34, 197, 94);
  private final Color DANGER_COLOR = new Color(220, 38, 38);
  private final Color BG_COLOR = new Color(245, 247, 250);

  private JTable tableThuoc; // Bảng danh sách thuốc
  private DefaultTableModel modelThuoc;

  private JTable tableGioHang; // Bảng giỏ hàng
  private DefaultTableModel modelGioHang;

  private JTextField txtTimKiem, txtSoLuong, txtTenKhach, txtSDT;
  private JLabel lbTongTien;
  private JComboBox<String> cbPhuongThucTT;

  private ThuocBUS thuocBUS;
  private HoaDonThuocBUS hoaDonBUS;
  private CTHDThuocBUS cthdBUS;

  private ArrayList<ThuocDTO> dsThuoc;
  private ArrayList<GioHangItem> gioHang; // Lưu tạm giỏ hàng

  public BanThuocPanel() {
    initData();
    initComponents();
    loadDanhSachThuoc();
  }

  private void initData() {
    thuocBUS = new ThuocBUS();
    hoaDonBUS = new HoaDonThuocBUS();
    cthdBUS = new CTHDThuocBUS();
    dsThuoc = new ArrayList<>();
    gioHang = new ArrayList<>();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBackground(BG_COLOR);
    setBorder(new EmptyBorder(20, 20, 20, 20));

    // Top: Title
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    topPanel.setOpaque(false);
    JLabel title = new JLabel("BÁN THUỐC");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    topPanel.add(title);

    // Left: Danh sách thuốc
    JPanel leftPanel = createLeftPanel();

    // Right: Giỏ hàng
    JPanel rightPanel = createRightPanel();

    // Split
    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      leftPanel,
      rightPanel
    );
    splitPane.setDividerLocation(500);
    splitPane.setResizeWeight(0.5);

    add(topPanel, BorderLayout.NORTH);
    add(splitPane, BorderLayout.CENTER);
  }

  private JPanel createLeftPanel() {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createTitledBorder("Danh sách thuốc"));

    // Search bar
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.setBackground(Color.WHITE);
    txtTimKiem = new JTextField(20);
    JButton btTimKiem = createButton("Tìm kiếm", PRIMARY_COLOR);
    JButton btReload = createButton("Làm mới", SUCCESS_COLOR);

    searchPanel.add(new JLabel("Tìm thuốc:"));
    searchPanel.add(txtTimKiem);
    searchPanel.add(btTimKiem);
    searchPanel.add(btReload);

    btTimKiem.addActionListener(e -> timKiemThuoc());
    btReload.addActionListener(e -> loadDanhSachThuoc());

    // Table thuốc
    String[] colsThuoc = {
      "Mã thuốc",
      "Tên thuốc",
      "Đơn vị",
      "Đơn giá",
      "Tồn kho",
    };
    modelThuoc = new DefaultTableModel(colsThuoc, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    tableThuoc = new JTable(modelThuoc);
    tableThuoc.setRowHeight(35);
    tableThuoc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Panel thêm vào giỏ
    JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    addPanel.setBackground(Color.WHITE);
    txtSoLuong = new JTextField(5);
    txtSoLuong.setText("1");
    JButton btThemGio = createButton("Thêm vào giỏ", SUCCESS_COLOR);

    addPanel.add(new JLabel("Số lượng:"));
    addPanel.add(txtSoLuong);
    addPanel.add(btThemGio);

    btThemGio.addActionListener(e -> themVaoGio());

    panel.add(searchPanel, BorderLayout.NORTH);
    panel.add(new JScrollPane(tableThuoc), BorderLayout.CENTER);
    panel.add(addPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createRightPanel() {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createTitledBorder("Giỏ hàng"));

    // Table giỏ hàng
    String[] colsGio = { "Tên thuốc", "Số lượng", "Đơn giá", "Thành tiền" };
    modelGioHang = new DefaultTableModel(colsGio, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    tableGioHang = new JTable(modelGioHang);
    tableGioHang.setRowHeight(35);

    // Bottom panel
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
    bottomPanel.setBackground(Color.WHITE);
    bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Thông tin khách hàng
    JPanel khachPanel = new JPanel(new GridLayout(2, 2, 5, 5));
    khachPanel.setBackground(Color.WHITE);
    txtTenKhach = new JTextField(15);
    txtSDT = new JTextField(15);
    khachPanel.add(new JLabel("Tên khách:"));
    khachPanel.add(txtTenKhach);
    khachPanel.add(new JLabel("SĐT:"));
    khachPanel.add(txtSDT);

    // Phương thức thanh toán
    JPanel ttPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ttPanel.setBackground(Color.WHITE);
    cbPhuongThucTT = new JComboBox<>(
      new String[] { "Tiền mặt", "Chuyển khoản" }
    );
    ttPanel.add(new JLabel("Thanh toán:"));
    ttPanel.add(cbPhuongThucTT);

    // Tổng tiền
    JPanel tongTienPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    tongTienPanel.setBackground(Color.WHITE);
    lbTongTien = new JLabel("0 VNĐ");
    lbTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
    lbTongTien.setForeground(DANGER_COLOR);
    tongTienPanel.add(new JLabel("TỔNG TIỀN: "));
    tongTienPanel.add(lbTongTien);

    // Buttons
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    btnPanel.setBackground(Color.WHITE);
    JButton btXoaItem = createButton("Xóa thuốc", DANGER_COLOR);
    JButton btXoaGio = createButton("Xóa giỏ", new Color(239, 68, 68));
    JButton btThanhToan = createButton("THANH TOÁN", PRIMARY_COLOR);
    btThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 14));

    btnPanel.add(btXoaItem);
    btnPanel.add(btXoaGio);
    btnPanel.add(btThanhToan);

    btXoaItem.addActionListener(e -> xoaThuocKhoiGio());
    btXoaGio.addActionListener(e -> xoaGioHang());
    btThanhToan.addActionListener(e -> thanhToan());

    bottomPanel.add(khachPanel);
    bottomPanel.add(Box.createVerticalStrut(10));
    bottomPanel.add(ttPanel);
    bottomPanel.add(Box.createVerticalStrut(10));
    bottomPanel.add(tongTienPanel);
    bottomPanel.add(Box.createVerticalStrut(10));
    bottomPanel.add(btnPanel);

    panel.add(new JScrollPane(tableGioHang), BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);

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

  private void loadDanhSachThuoc() {
    modelThuoc.setRowCount(0);
    dsThuoc = thuocBUS.getThuocConTon(); // Chỉ lấy thuốc còn tồn

    for (ThuocDTO t : dsThuoc) {
      modelThuoc.addRow(
        new Object[] {
          t.getMaThuoc(),
          t.getTenThuoc(),
          t.getDonViTinh(),
          String.format("%,.0f VNĐ", t.getDonGiaBan()),
          t.getSoLuongTon(),
        }
      );
    }
  }

  private void timKiemThuoc() {
    String key = txtTimKiem.getText().trim();
    if (key.isEmpty()) {
      loadDanhSachThuoc();
      return;
    }

    modelThuoc.setRowCount(0);
    ArrayList<ThuocDTO> result = thuocBUS.timTheoTen(key);

    for (ThuocDTO t : result) {
      if (t.getSoLuongTon() > 0) {
        modelThuoc.addRow(
          new Object[] {
            t.getMaThuoc(),
            t.getTenThuoc(),
            t.getDonViTinh(),
            String.format("%,.0f VNĐ", t.getDonGiaBan()),
            t.getSoLuongTon(),
          }
        );
      }
    }
  }

  private void themVaoGio() {
    int row = tableThuoc.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc!");
      return;
    }

    try {
      int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
      if (soLuong <= 0) {
        JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
        return;
      }

      String maThuoc = (String) tableThuoc.getValueAt(row, 0);
      String tenThuoc = (String) tableThuoc.getValueAt(row, 1);
      int tonKho = (int) tableThuoc.getValueAt(row, 4);

      if (soLuong > tonKho) {
        JOptionPane.showMessageDialog(this, "Không đủ tồn kho! Tồn: " + tonKho);
        return;
      }

      // Tìm thuốc để lấy đơn giá
      ThuocDTO thuoc = thuocBUS.getByMa(maThuoc);
      if (thuoc == null) {
        JOptionPane.showMessageDialog(this, "Lỗi tìm thuốc!");
        return;
      }

      // Kiểm tra xem thuốc đã có trong giỏ chưa
      boolean found = false;
      for (GioHangItem item : gioHang) {
        if (item.maThuoc.equals(maThuoc)) {
          item.soLuong += soLuong;
          found = true;
          break;
        }
      }

      if (!found) {
        gioHang.add(
          new GioHangItem(maThuoc, tenThuoc, thuoc.getDonGiaBan(), soLuong)
        );
      }

      updateGioHangTable();
      txtSoLuong.setText("1");
      JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ!");
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
    }
  }

  private void updateGioHangTable() {
    modelGioHang.setRowCount(0);
    double tongTien = 0;

    for (GioHangItem item : gioHang) {
      double thanhTien = item.donGia * item.soLuong;
      modelGioHang.addRow(
        new Object[] {
          item.tenThuoc,
          item.soLuong,
          String.format("%,.0f VNĐ", item.donGia),
          String.format("%,.0f VNĐ", thanhTien),
        }
      );
      tongTien += thanhTien;
    }

    lbTongTien.setText(String.format("%,.0f VNĐ", tongTien));
  }

  private void xoaThuocKhoiGio() {
    int row = tableGioHang.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Chọn thuốc cần xóa!");
      return;
    }

    gioHang.remove(row);
    updateGioHangTable();
  }

  private void xoaGioHang() {
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xóa toàn bộ giỏ hàng?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      gioHang.clear();
      updateGioHangTable();
    }
  }

  private void thanhToan() {
    if (gioHang.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
      return;
    }

    String tenKhach = txtTenKhach.getText().trim();
    String sdt = txtSDT.getText().trim();

    if (tenKhach.isEmpty() || sdt.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập tên và SĐT khách hàng!"
      );
      return;
    }

    // Tính tổng tiền
    double tongTien = 0;
    for (GioHangItem item : gioHang) {
      tongTien += item.donGia * item.soLuong;
    }

    // Confirm
    String phuongThuc = (String) cbPhuongThucTT.getSelectedItem();
    int confirm = JOptionPane.showConfirmDialog(
      this,
      String.format(
        "Xác nhận thanh toán?\n\nKhách: %s - %s\nTổng tiền: %,.0f VNĐ\nPhương thức: %s",
        tenKhach,
        sdt,
        tongTien,
        phuongThuc
      ),
      "Xác nhận thanh toán",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    try {
      // 1. Tạo hóa đơn
      HoaDonThuocDTO hoaDon = new HoaDonThuocDTO(null, tenKhach, sdt);
      hoaDon.setTongTien(tongTien);
      hoaDon.setTrangThaiThanhToan("Đã thanh toán");
      hoaDon.setNgayThanhToan(LocalDateTime.now());
      hoaDon.setTrangThaiLayThuoc("ĐANG CHỜ LẤY");
      hoaDon.setGhiChu("Thanh toán: " + phuongThuc);

      boolean insertHD = hoaDonBUS.addHoaDonThuoc(hoaDon);
      if (!insertHD) {
        JOptionPane.showMessageDialog(this, "Lỗi tạo hóa đơn!");
        return;
      }

      // 2. Lấy mã hóa đơn từ DTO (đã được tự động sinh trong DAO)
      String maHoaDon = hoaDon.getMaHoaDon();

      if (maHoaDon == null || maHoaDon.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Lỗi lấy mã hóa đơn!");
        return;
      }

      // 3. Thêm chi tiết hóa đơn
      for (GioHangItem item : gioHang) {
        CTHDThuocDTO cthd = new CTHDThuocDTO(
          maHoaDon,
          item.maThuoc,
          item.soLuong,
          item.donGia
        );
        cthdBUS.addDetailMedicine(cthd);
      }

      // 4. Cập nhật lại tổng tiền (đảm bảo chính xác)
      HoaDonThuocDTO hdUpdate = hoaDonBUS.getHoaDonThuocDetail(maHoaDon);
      if (hdUpdate != null) {
        hdUpdate.setTongTien(tongTien);
        hoaDonBUS.updateHoaDonThuoc(hdUpdate);
      }

      JOptionPane.showMessageDialog(
        this,
        String.format(
          "Thanh toán thành công!\n\nMã hóa đơn: %s\nTổng tiền: %,.0f VNĐ\n\nTrạng thái: ĐANG CHỜ LẤY THUỐC",
          maHoaDon,
          tongTien
        )
      );

      // Reset
      gioHang.clear();
      updateGioHangTable();
      txtTenKhach.setText("");
      txtSDT.setText("");
      loadDanhSachThuoc();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // Inner class để lưu item trong giỏ hàng
  private class GioHangItem {

    String maThuoc;
    String tenThuoc;
    float donGia;
    int soLuong;

    public GioHangItem(
      String maThuoc,
      String tenThuoc,
      float donGia,
      int soLuong
    ) {
      this.maThuoc = maThuoc;
      this.tenThuoc = tenThuoc;
      this.donGia = donGia;
      this.soLuong = soLuong;
    }
  }
}
