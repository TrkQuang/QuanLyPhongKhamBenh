package phongkham.gui;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;

public class ChiTietHDThuocPanel extends JPanel {

  private final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private final Color BG_COLOR = new Color(245, 247, 250);

  private String maHoaDon;
  private HoaDonThuocPanel parentPanel;

  private JTable table;
  private DefaultTableModel model;
  private JLabel lblTongTien, lblTrangThai;
  private HoaDonThuocBUS hdBUS;
  private CTHDThuocBUS bus;
  private ThuocBUS thuocBUS;

  public ChiTietHDThuocPanel(String maHoaDon, HoaDonThuocPanel parentPanel) {
    this.maHoaDon = maHoaDon;
    this.parentPanel = parentPanel;
    initData();
    initComponents();
  }

  private void initData() {
    hdBUS = new HoaDonThuocBUS();
    bus = new CTHDThuocBUS();
    thuocBUS = new ThuocBUS();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBackground(BG_COLOR);
    setBorder(new EmptyBorder(10, 10, 10, 10));

    add(createTopPanel(), BorderLayout.NORTH);
    add(createCenterPanel(), BorderLayout.CENTER);
    add(createBottomPanel(), BorderLayout.SOUTH);

    loadData();
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    JLabel lblTitle = new JLabel("CHI TIẾT HÓA ĐƠN BÁN THUỐC: " + maHoaDon);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

    lblTrangThai = new JLabel();
    lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setOpaque(false);
    leftPanel.add(lblTitle);
    leftPanel.add(lblTrangThai);

    JButton btnBack = createButton("← Quay lại", PRIMARY_COLOR);
    btnBack.addActionListener(e -> {
      if (parentPanel != null) {
        parentPanel.refreshData();
      }
    });

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(btnBack, BorderLayout.EAST);

    return panel;
  }

  private JPanel createCenterPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.add(createTablePanel(), BorderLayout.CENTER);
    return panel;
  }

  private JScrollPane createTablePanel() {
    String[] columns = {
      "Mã CTHD",
      "Mã thuốc",
      "Tên thuốc",
      "Đơn vị",
      "Số lượng",
      "Đơn giá",
      "Thành tiền",
    };

    model = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    table = new JTable(model);
    table.setRowHeight(30);

    DefaultTableCellRenderer center = new DefaultTableCellRenderer();
    center.setHorizontalAlignment(SwingConstants.CENTER);

    DefaultTableCellRenderer right = new DefaultTableCellRenderer();
    right.setHorizontalAlignment(SwingConstants.RIGHT);

    for (int i = 0; i < columns.length; i++) {
      if (i == 1 || i == 4) {
        table.getColumnModel().getColumn(i).setCellRenderer(center);
      } else if (i >= 5) {
        table.getColumnModel().getColumn(i).setCellRenderer(right);
      }
    }

    JScrollPane scroll = new JScrollPane(table);
    scroll.getViewport().setBackground(Color.WHITE);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

    return scroll;
  }

  private JPanel createBottomPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    lblTongTien = new JLabel("Tổng tiền: 0 đ");
    lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setOpaque(false);
    rightPanel.add(lblTongTien);
    rightPanel.add(Box.createHorizontalStrut(20));

    // Kiểm tra trạng thái thanh toán để show/hide nút chức năng
    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);

    if (hd != null) {
      String trangThai = hd.getTrangThaiThanhToan();

      if ("Chưa thanh toán".equals(trangThai)) {
        JButton btnThem = createStyledButton(
          "Thêm",
          new Color(34, 197, 94),
          Color.WHITE
        );
        JButton btnSua = createStyledButton(
          "Sửa",
          new Color(37, 99, 235),
          Color.WHITE
        );
        JButton btnXoa = createStyledButton(
          "Xóa dòng",
          new Color(220, 38, 38),
          Color.WHITE
        );

        btnThem.addActionListener(e -> themDong());
        btnSua.addActionListener(e -> suaDong());
        btnXoa.addActionListener(e -> xoaDong());

        rightPanel.add(btnThem);
        rightPanel.add(btnSua);
        rightPanel.add(btnXoa);
      }

      // Nút thanh toán
      if ("Chưa thanh toán".equals(trangThai)) {
        JButton btnThanhToan = createStyledButton(
          "Thanh toán",
          new Color(34, 197, 94),
          Color.WHITE
        );
        btnThanhToan.addActionListener(e -> thanhToanHoaDon());
        rightPanel.add(btnThanhToan);
      }
    }

    panel.add(rightPanel, BorderLayout.EAST);
    return panel;
  }

  private JButton createStyledButton(String text, Color bg, Color fg) {
    JButton btn = new JButton(text);
    btn.setBackground(bg);
    btn.setForeground(fg);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    return btn;
  }

  private void loadData() {
    model.setRowCount(0);

    HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);
    if (hd != null) {
      lblTrangThai.setText("Trạng thái: " + hd.getTrangThaiThanhToan());
    }

    List<CTHDThuocDTO> details = bus.getDetailsByInvoice(maHoaDon);
    double tongTien = 0;

    for (CTHDThuocDTO detail : details) {
      model.addRow(
        new Object[] {
          detail.getMaCTHDThuoc(),
          detail.getMaThuoc(),
          detail.getTenThuoc(),
          detail.getDonVi(),
          detail.getSoLuong(),
          String.format("%,.0f VNĐ", detail.getDonGia()),
          String.format("%,.0f VNĐ", detail.getThanhTien()),
        }
      );
      tongTien += detail.getThanhTien();
    }

    lblTongTien.setText(String.format("Tổng tiền: %,.0f VNĐ", tongTien));
  }

  private void themDong() {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Thêm chi tiết thuốc",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(500, 250);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Lấy danh sách thuốc
    java.util.ArrayList<ThuocDTO> dsThuoc = new java.util.ArrayList<>(
      thuocBUS.list()
    );
    JComboBox<String> cbThuoc = new JComboBox<>();
    for (ThuocDTO t : dsThuoc) {
      cbThuoc.addItem(t.getMaThuoc() + " - " + t.getTenThuoc());
    }

    JSpinner spinnerSoLuong = new JSpinner(
      new SpinnerNumberModel(1, 1, 1000, 1)
    );
    JTextField txtDonGia = new JTextField(10);
    JTextField txtGhiChu = new JTextField(30);

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Chọn thuốc:"), gbc);
    gbc.gridx = 1;
    panel.add(cbThuoc, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Số lượng:"), gbc);
    gbc.gridx = 1;
    panel.add(spinnerSoLuong, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Đơn giá:"), gbc);
    gbc.gridx = 1;
    panel.add(txtDonGia, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Ghi chú:"), gbc);
    gbc.gridx = 1;
    panel.add(txtGhiChu, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnLuu = new JButton("Lưu");
    JButton btnHuy = new JButton("Hủy");

    btnLuu.addActionListener(e -> {
      String maThuoc = cbThuoc.getSelectedItem().toString().split(" - ")[0];
      int soLuong = (int) spinnerSoLuong.getValue();
      double donGia = Double.parseDouble(txtDonGia.getText());

      CTHDThuocDTO cthd = new CTHDThuocDTO(maHoaDon, maThuoc, soLuong, donGia);
      cthd.setGhiChu(txtGhiChu.getText());

      if (bus.addDetailMedicine(cthd)) {
        // Cập nhật tổng tiền hóa đơn
        double totalAmount = bus.calculateInvoiceTotal(maHoaDon);
        HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);
        hd.setTongTien(totalAmount);
        hdBUS.updateHoaDonThuoc(hd);

        JOptionPane.showMessageDialog(dialog, "Thêm thành công!");
        loadData();
        dialog.dispose();
      } else {
        JOptionPane.showMessageDialog(dialog, "Thêm thất bại!");
      }
    });

    btnHuy.addActionListener(e -> dialog.dispose());

    buttonPanel.add(btnLuu);
    buttonPanel.add(btnHuy);

    dialog.add(panel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  private void suaDong() {
    int row = table.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
      return;
    }

    String maCTHD = (String) table.getValueAt(row, 0);
    CTHDThuocDTO cthd = bus.getDetailMedicine(maCTHD);

    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Sửa chi tiết thuốc",
      true
    );
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(500, 250);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JSpinner spinnerSoLuong = new JSpinner(
      new SpinnerNumberModel(cthd.getSoLuong(), 1, 1000, 1)
    );
    JTextField txtDonGia = new JTextField(String.valueOf(cthd.getDonGia()), 10);
    JTextField txtGhiChu = new JTextField(
      cthd.getGhiChu() != null ? cthd.getGhiChu() : "",
      30
    );

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Thuốc: " + cthd.getTenThuoc()), gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Số lượng:"), gbc);
    gbc.gridx = 1;
    panel.add(spinnerSoLuong, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Đơn giá:"), gbc);
    gbc.gridx = 1;
    panel.add(txtDonGia, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Ghi chú:"), gbc);
    gbc.gridx = 1;
    panel.add(txtGhiChu, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnLuu = new JButton("Lưu");
    JButton btnHuy = new JButton("Hủy");

    btnLuu.addActionListener(e -> {
      int soLuong = (int) spinnerSoLuong.getValue();
      double donGia = Double.parseDouble(txtDonGia.getText());

      cthd.setSoLuong(soLuong);
      cthd.setDonGia(donGia);
      cthd.setThanhTien(soLuong * donGia);
      cthd.setGhiChu(txtGhiChu.getText());

      if (bus.updateDetailMedicine(cthd)) {
        // Cập nhật tổng tiền hóa đơn
        double totalAmount = bus.calculateInvoiceTotal(maHoaDon);
        HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);
        hd.setTongTien(totalAmount);
        hdBUS.updateHoaDonThuoc(hd);

        JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
        loadData();
        dialog.dispose();
      } else {
        JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại!");
      }
    });

    btnHuy.addActionListener(e -> dialog.dispose());

    buttonPanel.add(btnLuu);
    buttonPanel.add(btnHuy);

    dialog.add(panel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  private void xoaDong() {
    int row = table.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
      return;
    }

    String maCTHD = (String) table.getValueAt(row, 0);
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Bạn có chắc chắn muốn xóa?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      if (bus.deleteDetailMedicine(maCTHD)) {
        // Cập nhật tổng tiền hóa đơn
        double totalAmount = bus.calculateInvoiceTotal(maHoaDon);
        HoaDonThuocDTO hd = hdBUS.getHoaDonThuocDetail(maHoaDon);
        hd.setTongTien(totalAmount);
        hdBUS.updateHoaDonThuoc(hd);

        JOptionPane.showMessageDialog(this, "Xóa thành công!");
        loadData();
      } else {
        JOptionPane.showMessageDialog(this, "Xóa thất bại!");
      }
    }
  }

  private void thanhToanHoaDon() {
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận thanh toán hóa đơn này?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );
    if (confirm == JOptionPane.YES_OPTION) {
      if (hdBUS.payInvoice(maHoaDon)) {
        JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
        loadData();
      } else {
        JOptionPane.showMessageDialog(this, "Thanh toán thất bại!");
      }
    }
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
}
