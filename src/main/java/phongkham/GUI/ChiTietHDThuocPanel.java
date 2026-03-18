package phongkham.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.hoadonthuoc.ChiTietHDThuocDialogs;
import phongkham.gui.hoadonthuoc.ChiTietHDThuocService;

public class ChiTietHDThuocPanel extends JPanel {

  private final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private final Color BG_COLOR = new Color(245, 247, 250);

  private String maHoaDon;
  private HoaDonThuocPanel parentPanel;

  private JTable table;
  private DefaultTableModel model;
  private JLabel lblTongTien, lblTrangThai;
  private ChiTietHDThuocService service;

  public ChiTietHDThuocPanel(String maHoaDon, HoaDonThuocPanel parentPanel) {
    this.maHoaDon = maHoaDon;
    this.parentPanel = parentPanel;
    initData();
    initComponents();
  }

  private void initData() {
    service = new ChiTietHDThuocService();
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
    HoaDonThuocDTO hd = service.layHoaDon(maHoaDon);

    if (hd != null) {
      String trangThai = hd.getTrangThaiThanhToan();
      String trangThaiLay =
        hd.getTrangThaiLayThuoc() != null
          ? hd.getTrangThaiLayThuoc()
          : "CHO_LAY";

      if (
        "CHUA_THANH_TOAN".equals(
          StatusNormalizer.normalizePaymentStatus(trangThai)
        )
      ) {
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
      if (
        "CHUA_THANH_TOAN".equals(
          StatusNormalizer.normalizePaymentStatus(trangThai)
        )
      ) {
        JButton btnThanhToan = createStyledButton(
          "Thanh toán",
          new Color(34, 197, 94),
          Color.WHITE
        );
        btnThanhToan.addActionListener(e -> thanhToanHoaDon());
        rightPanel.add(btnThanhToan);
      }

      // Nút hoàn thành lấy thuốc - chỉ hiển thị khi đã thanh toán và đang chờ lấy
      if (
        "DA_THANH_TOAN".equals(
          StatusNormalizer.normalizePaymentStatus(trangThai)
        ) &&
        "CHO_LAY".equals(StatusNormalizer.normalizePickupStatus(trangThaiLay))
      ) {
        JButton btnHoanThanhLayThuoc = createStyledButton(
          "Hoàn thành lấy thuốc",
          new Color(34, 197, 94),
          Color.WHITE
        );
        btnHoanThanhLayThuoc.addActionListener(e -> hoanThanhLayThuoc());
        rightPanel.add(btnHoanThanhLayThuoc);
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

    HoaDonThuocDTO hd = service.layHoaDon(maHoaDon);
    if (hd != null) {
      String trangThaiTT = hd.getTrangThaiThanhToan();
      String trangThaiLay =
        hd.getTrangThaiLayThuoc() != null
          ? hd.getTrangThaiLayThuoc()
          : "CHO_LAY";
      lblTrangThai.setText(
        "Thanh toán: " +
          StatusDisplayUtil.thanhToan(trangThaiTT) +
          " | Lấy thuốc: " +
          StatusDisplayUtil.layThuoc(trangThaiLay)
      );
      lblTrangThai.setForeground(StatusColorUtil.thanhToan(trangThaiTT));
    }

    List<CTHDThuocDTO> details = service.layChiTietHoaDon(maHoaDon);
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
    ArrayList<phongkham.DTO.ThuocDTO> dsThuoc = service.layDanhSachThuoc();
    ChiTietHDThuocDialogs.ThemInput input =
      ChiTietHDThuocDialogs.showThemDialog(this, dsThuoc);
    if (input == null) {
      return;
    }

    ChiTietHDThuocService.ActionResult result = service.themChiTiet(
      maHoaDon,
      input.maThuoc,
      input.soLuong,
      input.donGia,
      input.ghiChu
    );
    JOptionPane.showMessageDialog(this, result.getMessage());
    if (result.isThanhCong()) {
      loadData();
    }
  }

  private void suaDong() {
    int row = table.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
      return;
    }

    String maCTHD = (String) table.getValueAt(row, 0);
    CTHDThuocDTO cthd = service.layChiTietTheoMa(maCTHD);
    ChiTietHDThuocDialogs.SuaInput input = ChiTietHDThuocDialogs.showSuaDialog(
      this,
      cthd
    );
    if (input == null) {
      return;
    }

    ChiTietHDThuocService.ActionResult result = service.capNhatChiTiet(
      maHoaDon,
      cthd,
      input.soLuong,
      input.donGia,
      input.ghiChu
    );
    JOptionPane.showMessageDialog(this, result.getMessage());
    if (result.isThanhCong()) {
      loadData();
    }
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
      ChiTietHDThuocService.ActionResult result = service.xoaChiTiet(
        maHoaDon,
        maCTHD
      );
      JOptionPane.showMessageDialog(this, result.getMessage());
      if (result.isThanhCong()) {
        loadData();
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
      ChiTietHDThuocService.ActionResult result = service.thanhToanHoaDon(
        maHoaDon
      );
      JOptionPane.showMessageDialog(this, result.getMessage());
      if (result.isThanhCong()) {
        loadData();
      }
    }
  }

  private void hoanThanhLayThuoc() {
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận bệnh nhân đã lấy thuốc?\n" +
        "Số lượng thuốc sẽ được trừ khỏi kho.",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );
    if (confirm == JOptionPane.YES_OPTION) {
      ChiTietHDThuocService.ActionResult result = service.hoanThanhLayThuoc(
        maHoaDon
      );
      if (result.isThanhCong()) {
        JOptionPane.showMessageDialog(
          this,
          result.getMessage(),
          "Thành công",
          JOptionPane.INFORMATION_MESSAGE
        );
        loadData();
        // Refresh parent panel
        if (parentPanel != null) {
          parentPanel.refreshData();
        }
      } else {
        JOptionPane.showMessageDialog(
          this,
          result.getMessage(),
          "Lỗi",
          JOptionPane.ERROR_MESSAGE
        );
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
