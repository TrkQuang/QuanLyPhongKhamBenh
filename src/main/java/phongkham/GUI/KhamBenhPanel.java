package phongkham.gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.*;
import phongkham.DTO.*;

public class KhamBenhPanel extends JPanel {

  // BUS
  private HoSoBenhAnBUS hoSoBUS = new HoSoBenhAnBUS();
  private ThuocBUS thuocBUS = new ThuocBUS();
  private DonThuocBUS donThuocBUS = new DonThuocBUS();
  private CTDonThuocBUS ctDonThuocBUS = new CTDonThuocBUS();

  // Components - Danh sách hồ sơ chờ khám
  private JTable tableDanhSachHS;
  private DefaultTableModel modelDanhSachHS;

  // Components - Thông tin bệnh nhân
  private JLabel lblMaHS, lblHoTen, lblSDT, lblNgaySinh, lblGioiTinh, lblDiaChi;

  // Components - Thông tin khám
  private JTextArea txtTrieuChung, txtChanDoan, txtKetLuan, txtLoiDan;

  // Components - Kê đơn thuốc
  private JComboBox<String> cboThuoc;
  private JTextField txtSoLuong, txtLieuDung, txtCachDung;
  private JTable tableDonThuoc;
  private DefaultTableModel modelDonThuoc;
  private JButton btnThemThuoc, btnXoaThuoc;

  // Components - Buttons
  private JButton btnLuuKham, btnLamMoi;

  // Current data
  private String maHoSoHienTai = null;
  private ArrayList<ThuocDTO> danhSachThuoc = new ArrayList<>();

  public KhamBenhPanel() {
    initComponents();
    loadDanhSachHoSoChoKham();
    loadDanhSachThuoc();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title
    JLabel lblTitle = new JLabel("KHÁM BỆNH", JLabel.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    add(lblTitle, BorderLayout.NORTH);

    // Main Panel
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setDividerLocation(400);

    // Left: Danh sách hồ sơ chờ khám
    splitPane.setLeftComponent(createDanhSachHoSoPanel());

    // Right: Form khám bệnh
    splitPane.setRightComponent(createFormKhamPanel());

    add(splitPane, BorderLayout.CENTER);
  }

  // ==================== PANEL: DANH SÁCH HỒ SƠ CHỜ KHÁM ====================
  private JPanel createDanhSachHoSoPanel() {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createTitledBorder("Danh sách chờ khám"));

    String[] columns = { "Mã HS", "Họ tên", "SĐT", "Ngày khám" };
    modelDanhSachHS = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tableDanhSachHS = new JTable(modelDanhSachHS);
    tableDanhSachHS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableDanhSachHS.setRowHeight(25);

    // Khi chọn hồ sơ -> hiển thị thông tin
    tableDanhSachHS
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
          onChonHoSo();
        }
      });

    JScrollPane scroll = new JScrollPane(tableDanhSachHS);
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  // ==================== PANEL: FORM KHÁM BỆNH ====================
  private JPanel createFormKhamPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Thông tin khám", createThongTinKhamPanel());
    tabbedPane.addTab("Kê đơn thuốc", createKeDonThuocPanel());

    panel.add(tabbedPane, BorderLayout.CENTER);

    // Buttons
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    btnLuuKham = createButton("💾 Lưu khám bệnh", new Color(34, 197, 94));
    btnLamMoi = createButton("🔄 Làm mới", new Color(59, 130, 246));

    btnLuuKham.addActionListener(e -> luuKhamBenh());
    btnLamMoi.addActionListener(e -> lamMoi());

    btnPanel.add(btnLuuKham);
    btnPanel.add(btnLamMoi);

    panel.add(btnPanel, BorderLayout.SOUTH);

    return panel;
  }

  // ==================== TAB: THÔNG TIN KHÁM ====================
  private JPanel createThongTinKhamPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Thông tin bệnh nhân
    JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
    infoPanel.setBorder(
      BorderFactory.createTitledBorder("Thông tin bệnh nhân")
    );

    infoPanel.add(new JLabel("Mã hồ sơ:"));
    lblMaHS = new JLabel("-");
    infoPanel.add(lblMaHS);

    infoPanel.add(new JLabel("Họ tên:"));
    lblHoTen = new JLabel("-");
    infoPanel.add(lblHoTen);

    infoPanel.add(new JLabel("SĐT:"));
    lblSDT = new JLabel("-");
    infoPanel.add(lblSDT);

    infoPanel.add(new JLabel("Ngày sinh:"));
    lblNgaySinh = new JLabel("-");
    infoPanel.add(lblNgaySinh);

    infoPanel.add(new JLabel("Giới tính:"));
    lblGioiTinh = new JLabel("-");
    infoPanel.add(lblGioiTinh);

    infoPanel.add(new JLabel("Địa chỉ:"));
    lblDiaChi = new JLabel("-");
    infoPanel.add(lblDiaChi);

    panel.add(infoPanel, BorderLayout.NORTH);

    // Form khám
    JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
    formPanel.setBorder(
      BorderFactory.createTitledBorder("Thông tin khám bệnh")
    );

    formPanel.add(
      createTextAreaPanel("Triệu chứng:", txtTrieuChung = new JTextArea(3, 20))
    );
    formPanel.add(
      createTextAreaPanel("Chẩn đoán:", txtChanDoan = new JTextArea(3, 20))
    );
    formPanel.add(
      createTextAreaPanel("Kết luận:", txtKetLuan = new JTextArea(3, 20))
    );
    formPanel.add(
      createTextAreaPanel("Lời dặn:", txtLoiDan = new JTextArea(3, 20))
    );

    panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

    return panel;
  }

  // ==================== TAB: KÊ ĐƠN THUỐC ====================
  private JPanel createKeDonThuocPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Form thêm thuốc
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createTitledBorder("Thêm thuốc vào đơn"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Chọn thuốc
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(new JLabel("Chọn thuốc:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    cboThuoc = new JComboBox<>();
    cboThuoc.setPreferredSize(new Dimension(300, 30));
    formPanel.add(cboThuoc, gbc);

    // Số lượng
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Số lượng:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtSoLuong = new JTextField(10);
    formPanel.add(txtSoLuong, gbc);

    // Liều dùng
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Liều dùng:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtLieuDung = new JTextField(10);
    txtLieuDung.setToolTipText("VD: 1 viên x 2 lần/ngày");
    formPanel.add(txtLieuDung, gbc);

    // Cách dùng
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Cách dùng:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtCachDung = new JTextField(10);
    txtCachDung.setToolTipText("VD: Uống sau ăn");
    formPanel.add(txtCachDung, gbc);

    // Buttons
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JPanel btnPanelThuoc = new JPanel(
      new FlowLayout(FlowLayout.CENTER, 10, 10)
    );

    btnThemThuoc = createButton("➕ Thêm thuốc", new Color(34, 197, 94));
    btnXoaThuoc = createButton("❌ Xóa thuốc", new Color(239, 68, 68));

    btnThemThuoc.addActionListener(e -> themThuocVaoDon());
    btnXoaThuoc.addActionListener(e -> xoaThuocKhoiDon());

    btnPanelThuoc.add(btnThemThuoc);
    btnPanelThuoc.add(btnXoaThuoc);
    formPanel.add(btnPanelThuoc, gbc);

    panel.add(formPanel, BorderLayout.NORTH);

    // Table đơn thuốc
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.setBorder(
      BorderFactory.createTitledBorder("Danh sách thuốc trong đơn")
    );

    String[] columns = {
      "Mã thuốc",
      "Tên thuốc",
      "Số lượng",
      "Liều dùng",
      "Cách dùng",
    };
    modelDonThuoc = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tableDonThuoc = new JTable(modelDonThuoc);
    tableDonThuoc.setRowHeight(25);

    JScrollPane scroll = new JScrollPane(tableDonThuoc);
    tablePanel.add(scroll, BorderLayout.CENTER);

    panel.add(tablePanel, BorderLayout.CENTER);

    return panel;
  }

  // ==================== HELPER METHODS ====================
  private JPanel createTextAreaPanel(String label, JTextArea textArea) {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.add(new JLabel(label), BorderLayout.NORTH);

    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    JScrollPane scroll = new JScrollPane(textArea);
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  private JButton createButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(180, 40));
    return btn;
  }

  // ==================== LOAD DATA ====================
  private void loadDanhSachHoSoChoKham() {
    modelDanhSachHS.setRowCount(0);
    ArrayList<HoSoBenhAnDTO> list = hoSoBUS.getByTrangThai("CHO_KHAM");

    for (HoSoBenhAnDTO hs : list) {
      modelDanhSachHS.addRow(
        new Object[] {
          hs.getMaHoSo(),
          hs.getHoTen(),
          hs.getSoDienThoai(),
          hs.getNgayKham() != null ? hs.getNgayKham().toString() : "",
        }
      );
    }
  }

  private void loadDanhSachThuoc() {
    cboThuoc.removeAllItems();
    danhSachThuoc = thuocBUS.list();

    for (ThuocDTO thuoc : danhSachThuoc) {
      cboThuoc.addItem(thuoc.getMaThuoc() + " - " + thuoc.getTenThuoc());
    }
  }

  // ==================== EVENT HANDLERS ====================
  private void onChonHoSo() {
    int row = tableDanhSachHS.getSelectedRow();
    if (row == -1) return;

    String maHS = (String) modelDanhSachHS.getValueAt(row, 0);
    maHoSoHienTai = maHS;

    HoSoBenhAnDTO hs = hoSoBUS.getById(maHS);
    if (hs == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ!");
      return;
    }

    // Hiển thị thông tin bệnh nhân
    lblMaHS.setText(hs.getMaHoSo());
    lblHoTen.setText(hs.getHoTen());
    lblSDT.setText(hs.getSoDienThoai());
    lblNgaySinh.setText(
      hs.getNgaySinh() != null ? hs.getNgaySinh().toString() : ""
    );
    lblGioiTinh.setText(hs.getGioiTinh());
    lblDiaChi.setText(hs.getDiaChi());

    // Clear form
    txtTrieuChung.setText("");
    txtChanDoan.setText("");
    txtKetLuan.setText("");
    txtLoiDan.setText("");
    modelDonThuoc.setRowCount(0);
  }

  private void themThuocVaoDon() {
    if (cboThuoc.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc!");
      return;
    }

    String soLuongStr = txtSoLuong.getText().trim();
    if (soLuongStr.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng!");
      txtSoLuong.requestFocus();
      return;
    }

    int soLuong;
    try {
      soLuong = Integer.parseInt(soLuongStr);
      if (soLuong <= 0) {
        JOptionPane.showMessageDialog(this, "Số lượng phải > 0!");
        return;
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
      return;
    }

    String selected = (String) cboThuoc.getSelectedItem();
    String maThuoc = selected.split(" - ")[0];
    String tenThuoc = selected.split(" - ")[1];

    String lieuDung = txtLieuDung.getText().trim();
    String cachDung = txtCachDung.getText().trim();

    // Thêm vào table
    modelDonThuoc.addRow(
      new Object[] { maThuoc, tenThuoc, soLuong, lieuDung, cachDung }
    );

    // Clear form
    txtSoLuong.setText("");
    txtLieuDung.setText("");
    txtCachDung.setText("");
    cboThuoc.setSelectedIndex(0);

    JOptionPane.showMessageDialog(this, "Đã thêm thuốc vào đơn!");
  }

  private void xoaThuocKhoiDon() {
    int row = tableDonThuoc.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần xóa!");
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xóa thuốc khỏi đơn?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      modelDonThuoc.removeRow(row);
    }
  }

  private void luuKhamBenh() {
    if (maHoSoHienTai == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn hồ sơ bệnh nhân!");
      return;
    }

    // Validate
    if (txtTrieuChung.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập triệu chứng!");
      return;
    }

    if (txtChanDoan.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập chẩn đoán!");
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận lưu thông tin khám bệnh?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
      // 1. Update HoSoBenhAn
      HoSoBenhAnDTO hs = hoSoBUS.getById(maHoSoHienTai);
      hs.setTrieuChung(txtTrieuChung.getText().trim());
      hs.setChanDoan(txtChanDoan.getText().trim());
      hs.setKetLuan(txtKetLuan.getText().trim());
      hs.setLoiDan(txtLoiDan.getText().trim());
      hs.setTrangThai("DA_KHAM");

      boolean updateHS = hoSoBUS.update(hs);
      if (!updateHS) {
        JOptionPane.showMessageDialog(this, "Lỗi cập nhật hồ sơ!");
        return;
      }

      // 2. Nếu có thuốc -> Insert DonThuoc và ChiTietDonThuoc
      if (modelDonThuoc.getRowCount() > 0) {
        String maDonThuoc = generateMaDonThuoc();

        // Insert DonThuoc
        DonThuocDTO donThuoc = new DonThuocDTO();
        donThuoc.setMaDonThuoc(maDonThuoc);
        donThuoc.setMaHoSo(maHoSoHienTai);
        donThuoc.setNgayKeDon(
          LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
          )
        );
        donThuoc.setGhiChu("");

        boolean insertDon = donThuocBUS.add(donThuoc);
        if (!insertDon) {
          JOptionPane.showMessageDialog(this, "Lỗi tạo đơn thuốc!");
          return;
        }

        // Insert ChiTietDonThuoc
        for (int i = 0; i < modelDonThuoc.getRowCount(); i++) {
          String maThuoc = (String) modelDonThuoc.getValueAt(i, 0);
          int soLuong = (int) modelDonThuoc.getValueAt(i, 2);
          String lieuDung = (String) modelDonThuoc.getValueAt(i, 3);
          String cachDung = (String) modelDonThuoc.getValueAt(i, 4);

          String maCTDT = "CTDT" + System.currentTimeMillis() + "_" + i;

          CTDonThuocDTO ctDon = new CTDonThuocDTO(
            maCTDT,
            maDonThuoc,
            maThuoc,
            soLuong,
            lieuDung,
            cachDung
          );

          boolean insertCT = ctDonThuocBUS.add(ctDon);
          if (!insertCT) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm chi tiết đơn thuốc!");
            return;
          }
        }
      }

      JOptionPane.showMessageDialog(this, "✅ Lưu khám bệnh thành công!");

      // Refresh
      loadDanhSachHoSoChoKham();
      lamMoi();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void lamMoi() {
    maHoSoHienTai = null;

    lblMaHS.setText("-");
    lblHoTen.setText("-");
    lblSDT.setText("-");
    lblNgaySinh.setText("-");
    lblGioiTinh.setText("-");
    lblDiaChi.setText("-");

    txtTrieuChung.setText("");
    txtChanDoan.setText("");
    txtKetLuan.setText("");
    txtLoiDan.setText("");

    txtSoLuong.setText("");
    txtLieuDung.setText("");
    txtCachDung.setText("");

    modelDonThuoc.setRowCount(0);

    tableDanhSachHS.clearSelection();
  }

  private String generateMaDonThuoc() {
    return "DT" + System.currentTimeMillis();
  }
}
