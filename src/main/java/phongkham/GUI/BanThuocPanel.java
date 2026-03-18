package phongkham.gui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.ThuocDTO;
import phongkham.gui.banthuoc.BanThuocService;

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

  private BanThuocService banThuocService;

  private ArrayList<ThuocDTO> dsThuoc;
  private ArrayList<BanThuocService.CartItem> gioHang; // Lưu tạm giỏ hàng

  public BanThuocPanel() {
    initData();
    initComponents();
    loadDanhSachThuoc();
  }

  private void initData() {
    banThuocService = new BanThuocService();
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
    dsThuoc = banThuocService.layThuocConTon(); // Chỉ lấy thuốc còn tồn

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
    ArrayList<ThuocDTO> result = banThuocService.timThuocTheoTen(key);

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
      ThuocDTO thuoc = banThuocService.layThuocTheoMa(maThuoc);
      if (thuoc == null) {
        JOptionPane.showMessageDialog(this, "Lỗi tìm thuốc!");
        return;
      }

      banThuocService.themHoacCongDonVaoGio(
        gioHang,
        maThuoc,
        tenThuoc,
        thuoc.getDonGiaBan(),
        soLuong
      );

      updateGioHangTable();
      txtSoLuong.setText("1");
      JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ!");
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
    }
  }

  private void updateGioHangTable() {
    modelGioHang.setRowCount(0);
    double tongTien = banThuocService.tinhTongTien(gioHang);

    for (BanThuocService.CartItem item : gioHang) {
      double thanhTien = item.getDonGia() * item.getSoLuong();
      modelGioHang.addRow(
        new Object[] {
          item.getTenThuoc(),
          item.getSoLuong(),
          String.format("%,.0f VNĐ", item.getDonGia()),
          String.format("%,.0f VNĐ", thanhTien),
        }
      );
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

    double tongTien = banThuocService.tinhTongTien(gioHang);

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

    BanThuocService.ThanhToanResult result = banThuocService.thanhToan(
      tenKhach,
      sdt,
      phuongThuc,
      gioHang
    );
    if (!result.isThanhCong()) {
      JOptionPane.showMessageDialog(this, result.getMessage());
      return;
    }

    JOptionPane.showMessageDialog(
      this,
      String.format(
        "Thanh toán thành công!\n\nMã hóa đơn: %s\nTổng tiền: %,.0f VNĐ\n\nTrạng thái: ĐANG CHỜ LẤY THUỐC",
        result.getMaHoaDon(),
        result.getTongTien()
      )
    );

    banThuocService.xuatHoaDonPdf(
      result.getMaHoaDon(),
      tenKhach,
      sdt,
      phuongThuc,
      gioHang,
      result.getTongTien()
    );

    gioHang.clear();
    updateGioHangTable();
    txtTenKhach.setText("");
    txtSDT.setText("");
    loadDanhSachThuoc();
  }
}
