package phongkham.gui;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.NhaCungCapBUS;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.ExcelExport;
import phongkham.Utils.PdfExport;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;

public class PhieuNhapPanel extends JPanel {

  private JTextField txtTimKiem;
  private JPanel listContainer;
  private PhieuNhapBUS bus;
  private CTPhieuNhapBUS ctPhieuNhapBUS;

  public PhieuNhapPanel() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    bus = new PhieuNhapBUS();
    ctPhieuNhapBUS = new CTPhieuNhapBUS();

    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);

    loadData();
  }

  // ===== PANEL TRÊN: tìm kiếm =====
  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    txtTimKiem = new JTextField(20);
    JButton btnLoc = new JButton("Lọc");

    left.add(new JLabel("Tìm kiếm:"));
    left.add(txtTimKiem);
    left.add(btnLoc);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnExcel = new JButton("Xuất Excel");
    JButton btnPdf = new JButton("Xuất PDF");
    JButton btnThem = new JButton("Thêm");
    right.add(btnLamMoi);
    right.add(btnExcel);
    right.add(btnPdf);
    right.add(btnThem);

    panel.add(left, BorderLayout.WEST);
    panel.add(right, BorderLayout.EAST);

    btnLoc.addActionListener(e -> searchData());
    btnLamMoi.addActionListener(e -> refreshData());
    btnExcel.addActionListener(e -> xuatBaoCaoExcel());
    btnPdf.addActionListener(e -> xuatBaoCaoPdf());
    btnThem.addActionListener(e -> moDialogThemPhieuNhap());

    return panel;
  }

  // ===== VÙNG DANH SÁCH =====
  private JScrollPane createListArea() {
    listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

    JScrollPane scrollPane = new JScrollPane(listContainer);
    scrollPane.setBorder(
      BorderFactory.createTitledBorder("Danh sách phiếu nhập")
    );
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    return scrollPane;
  }

  // ===== LOAD DỮ LIỆU =====
  private void loadData() {
    showList(bus.getAll());
  }

  public void refreshData() {
    txtTimKiem.setText("");
    loadData();
  }

  // ===== TÌM KIẾM =====
  private void searchData() {
    String keyword = txtTimKiem.getText().trim();
    if (keyword.isEmpty()) {
      loadData();
      return;
    }
    showList(bus.search(keyword));
  }

  // ✅ METHOD DÙNG CHUNG: Hiển thị danh sách
  private void showList(ArrayList<PhieuNhapDTO> list) {
    listContainer.removeAll();

    if (list.isEmpty()) {
      JLabel lblEmpty = new JLabel("Không có dữ liệu");
      lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
      listContainer.add(lblEmpty);
    } else {
      for (PhieuNhapDTO pn : list) {
        listContainer.add(createPhieuNhapItem(pn));
      }
    }

    listContainer.revalidate();
    listContainer.repaint();
  }

  // ===== TẠO ITEM PHIẾU NHẬP =====
  private JPanel createPhieuNhapItem(PhieuNhapDTO pn) {
    JPanel item = new JPanel(new BorderLayout(10, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
      )
    );
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

    // ✅ PANEL THÔNG TIN (KHÔNG HTML)
    JPanel infoPanel = createInfoPanel(pn);
    item.add(infoPanel, BorderLayout.CENTER);

    // ✅ PANEL NÚT BẤM
    JPanel btnPanel = createButtonPanel(pn);
    item.add(btnPanel, BorderLayout.EAST);

    return item;
  }

  // ✅ TẠO PANEL THÔNG TIN (Thay thế HTML)
  private JPanel createInfoPanel(PhieuNhapDTO pn) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    // Dòng 1: Mã + Ngày + Người giao
    JLabel lblDong1 = new JLabel(
      String.format(
        "Mã: %s  |  Ngày: %s  |  Người giao: %s",
        pn.getMaPhieuNhap(),
        new SimpleDateFormat("dd/MM/yyyy").format(pn.getNgayNhap()),
        pn.getNguoiGiao()
      )
    );
    lblDong1.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblDong1.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CĂN TRÁI

    // Dòng 2: Tổng tiền + Trạng thái
    JPanel dong2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    dong2Panel.setOpaque(false);
    dong2Panel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CĂN TRÁI

    JLabel lblTien = new JLabel(
      String.format("%.0f VNĐ", pn.getTongTienNhap())
    );
    lblTien.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblTien.setForeground(new Color(16, 185, 129));

    JLabel lblTrangThai = createTrangThaiLabel(pn.getTrangThai());

    dong2Panel.add(lblTien);
    dong2Panel.add(new JLabel("  |  "));
    dong2Panel.add(lblTrangThai);

    panel.add(lblDong1);
    panel.add(Box.createVerticalStrut(5));
    panel.add(dong2Panel);

    return panel;
  }

  // ✅ TẠO LABEL TRẠNG THÁI với màu sắc
  private JLabel createTrangThaiLabel(String trangThai) {
    String text;
    Color color;
    String trangThaiChuan = StatusNormalizer.normalizePhieuNhapStatus(
      trangThai
    );

    switch (trangThaiChuan) {
      case "DA_NHAP":
        text = StatusDisplayUtil.phieuNhap(trangThaiChuan);
        color = StatusColorUtil.phieuNhap(trangThaiChuan);
        break;
      case "DA_DUYET":
        text = StatusDisplayUtil.phieuNhap(trangThaiChuan);
        color = StatusColorUtil.phieuNhap(trangThaiChuan);
        break;
      case "DA_HUY":
        text = StatusDisplayUtil.phieuNhap(trangThaiChuan);
        color = StatusColorUtil.phieuNhap(trangThaiChuan);
        break;
      case "CHO_DUYET":
      default:
        text = StatusDisplayUtil.phieuNhap(trangThaiChuan);
        color = StatusColorUtil.phieuNhap(trangThaiChuan);
        break;
    }

    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lbl.setForeground(color);
    return lbl;
  }

  // ✅ TẠO PANEL NÚT BẤM
  private JPanel createButtonPanel(PhieuNhapDTO pn) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    panel.setOpaque(false);

    String trangThai = StatusNormalizer.normalizePhieuNhapStatus(
      pn.getTrangThai()
    );

    // Nút "Chi tiết"
    JButton btnChiTiet = createButton("Chi tiết", new Color(59, 130, 246));
    btnChiTiet.addActionListener(e -> {
      ChiTietPhieuNhapPanel detailPanel = new ChiTietPhieuNhapPanel(
        pn.getMaPhieuNhap(),
        this
      );
      removeAll();
      setLayout(new BorderLayout());
      add(detailPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
    });
    panel.add(btnChiTiet);

    // Nút "Duyệt" (chỉ hiện nếu CHO_DUYET)
    if ("CHUA_DUYET".equals(trangThai) || "CHO_DUYET".equals(trangThai)) {
      JButton btnDuyet = createButton("Duyệt", new Color(16, 185, 129));
      btnDuyet.addActionListener(e -> {
        if (confirm("Xác nhận duyệt phiếu nhập này?")) {
          if (bus.capNhatTrangThai(pn.getMaPhieuNhap(), "DA_DUYET")) {
            JOptionPane.showMessageDialog(
              this,
              "✅ Đã duyệt phiếu thành công!"
            );
            loadData();
          } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi duyệt phiếu!");
          }
        }
      });
      panel.add(btnDuyet);
    }

    // Nút "Xác nhận nhập kho" (chỉ hiện nếu DA_DUYET)
    // Tồn kho chỉ được cộng khi xác nhận nhập kho thành công.
    if ("DA_DUYET".equals(trangThai)) {
      JButton btnXacNhanNhapKho = createButton(
        "Xác nhận nhập kho",
        new Color(16, 185, 129)
      );
      btnXacNhanNhapKho.addActionListener(e -> {
        if (
          confirm(
            "Xác nhận nhập kho phiếu này?\nHệ thống sẽ cộng số lượng tồn kho thuốc."
          )
        ) {
          if (ctPhieuNhapBUS.xacNhanNhapKho(pn.getMaPhieuNhap())) {
            JOptionPane.showMessageDialog(this, "✅ Nhập kho thành công!");
            refreshData();
          } else {
            JOptionPane.showMessageDialog(
              this,
              "❌ Không thể nhập kho!\nVui lòng kiểm tra trạng thái phiếu và chi tiết nhập."
            );
          }
        }
      });
      panel.add(btnXacNhanNhapKho);
    }

    // Nút "Xóa" (chỉ hiện nếu KHÔNG PHẢI DA_DUYET)
    // Chỉ cho xóa khi chưa nhập và chưa hủy
    if (!"DA_NHAP".equals(trangThai) && !"DA_HUY".equals(trangThai)) {
      JButton btnXoa = createButton("Xóa", new Color(239, 68, 68));

      btnXoa.addActionListener(e -> {
        if (confirm("Xác nhận hủy phiếu nhập này?")) {
          if (bus.delete(pn.getMaPhieuNhap())) {
            JOptionPane.showMessageDialog(this, "✅ Đã hủy thành công!");
            loadData();
          } else {
            JOptionPane.showMessageDialog(this, "❌ Không thể hủy phiếu!");
          }
        }
      });

      panel.add(btnXoa);
    }

    return panel;
  }

  // ✅ TẠO NÚT BẤM với style giống nhau
  private JButton createButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return btn;
  }

  // ✅ Helper: Confirm dialog
  private boolean confirm(String message) {
    return (
      JOptionPane.showConfirmDialog(
        this,
        message,
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
      ) ==
      JOptionPane.YES_OPTION
    );
  }

  // ===== QUAY LẠI VIEW CHÍNH =====
  public void showMainView() {
    removeAll();
    setLayout(new BorderLayout(10, 10));
    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);
    loadData();
    revalidate();
    repaint();
  }

  private void moDialogThemPhieuNhap() {
    NhaCungCapBUS nhaCungCapBUS = new NhaCungCapBUS();
    ArrayList<NhaCungCapDTO> danhSachNCC = nhaCungCapBUS.listDangHopTac();
    ArrayList<ThuocDTO> danhSachThuoc = new ThuocBUS().list();

    if (danhSachNCC == null || danhSachNCC.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "❌ Chưa có nhà cung cấp đang hợp tác để tạo phiếu nhập"
      );
      return;
    }

    if (danhSachThuoc == null || danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "❌ Chưa có thuốc trong hệ thống để thêm vào phiếu nhập"
      );
      return;
    }

    JComboBox<String> cboNhaCungCap = new JComboBox<>();
    for (NhaCungCapDTO nhaCungCap : danhSachNCC) {
      cboNhaCungCap.addItem(
        nhaCungCap.getMaNhaCungCap() + " - " + nhaCungCap.getTenNhaCungCap()
      );
    }

    JTextField txtNguoiGiao = new JTextField();

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Nhà cung cấp:"));
    formPanel.add(cboNhaCungCap);
    formPanel.add(new JLabel("Người giao:"));
    formPanel.add(txtNguoiGiao);

    String[] cols = {
      "Mã thuốc",
      "Tên thuốc",
      "Số lượng",
      "Đơn giá nhập",
      "Hạn sử dụng",
      "Thành tiền",
    };
    javax.swing.table.DefaultTableModel model =
      new javax.swing.table.DefaultTableModel(cols, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };

    JTable table = new JTable(model);
    table.setRowHeight(26);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(760, 220));

    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    List<TempChiTietNhap> chiTietTam = new ArrayList<>();

    JLabel lblTongTien = new JLabel("Tổng tiền nhập: 0 đ");
    lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 13));

    Runnable refreshBangChiTiet = () -> {
      model.setRowCount(0);
      BigDecimal tong = BigDecimal.ZERO;
      for (TempChiTietNhap ct : chiTietTam) {
        BigDecimal thanhTien = ct.tinhThanhTien();
        tong = tong.add(thanhTien);
        model.addRow(
          new Object[] {
            ct.maThuoc,
            ct.tenThuoc,
            ct.soLuong,
            formatter.format(ct.donGiaNhap),
            ct.hanSuDung == null ? "" : ct.hanSuDung.format(dateFormatter),
            formatter.format(thanhTien),
          }
        );
      }
      lblTongTien.setText("Tổng tiền nhập: " + formatter.format(tong) + " đ");
    };

    JButton btnThemThuoc = new JButton("Thêm thuốc");
    JButton btnSuaThuoc = new JButton("Sửa dòng");
    JButton btnXoaThuoc = new JButton("Xóa dòng");

    btnSuaThuoc.setEnabled(false);
    btnXoaThuoc.setEnabled(false);

    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        boolean coChon = table.getSelectedRow() >= 0;
        btnSuaThuoc.setEnabled(coChon);
        btnXoaThuoc.setEnabled(coChon);
      });

    btnThemThuoc.addActionListener(e -> {
      TempChiTietNhap input = hienThiDialogChonThuocNhap(danhSachThuoc, null);
      if (input == null) {
        return;
      }
      chiTietTam.add(input);
      refreshBangChiTiet.run();
    });

    btnSuaThuoc.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0 || row >= chiTietTam.size()) {
        return;
      }
      TempChiTietNhap cu = chiTietTam.get(row);
      TempChiTietNhap moi = hienThiDialogChonThuocNhap(danhSachThuoc, cu);
      if (moi == null) {
        return;
      }
      chiTietTam.set(row, moi);
      refreshBangChiTiet.run();
      if (row < model.getRowCount()) {
        table.setRowSelectionInterval(row, row);
      }
    });

    btnXoaThuoc.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0 || row >= chiTietTam.size()) {
        return;
      }
      chiTietTam.remove(row);
      refreshBangChiTiet.run();
      btnSuaThuoc.setEnabled(false);
      btnXoaThuoc.setEnabled(false);
    });

    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actionsPanel.add(btnThemThuoc);
    actionsPanel.add(btnSuaThuoc);
    actionsPanel.add(btnXoaThuoc);

    JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
    tablePanel.setBorder(
      BorderFactory.createTitledBorder("Chi tiết thuốc nhập")
    );
    tablePanel.add(actionsPanel, BorderLayout.NORTH);
    tablePanel.add(scrollPane, BorderLayout.CENTER);
    tablePanel.add(lblTongTien, BorderLayout.SOUTH);

    JPanel panelNhap = new JPanel(new BorderLayout(0, 10));
    panelNhap.add(formPanel, BorderLayout.NORTH);
    panelNhap.add(tablePanel, BorderLayout.CENTER);

    int luaChon = JOptionPane.showConfirmDialog(
      this,
      panelNhap,
      "Thêm phiếu nhập",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return;
    }

    try {
      String nguoiGiao = txtNguoiGiao.getText().trim();
      if (nguoiGiao.isEmpty()) {
        JOptionPane.showMessageDialog(
          this,
          "❌ Người giao không được để trống"
        );
        return;
      }

      if (chiTietTam.isEmpty()) {
        JOptionPane.showMessageDialog(
          this,
          "❌ Cần thêm ít nhất một dòng thuốc vào phiếu nhập"
        );
        return;
      }

      String nhaCungCapDaChon = (String) cboNhaCungCap.getSelectedItem();
      String maNhaCungCap = nhaCungCapDaChon.split(" - ")[0].trim();
      String maPhieuNhap = "PN" + System.currentTimeMillis();

      BigDecimal tong = BigDecimal.ZERO;
      for (TempChiTietNhap ct : chiTietTam) {
        tong = tong.add(ct.tinhThanhTien());
      }

      PhieuNhapDTO phieuNhapMoi = new PhieuNhapDTO();
      phieuNhapMoi.setMaPhieuNhap(maPhieuNhap);
      phieuNhapMoi.setMaNCC(maNhaCungCap);
      phieuNhapMoi.setNgayNhap(new java.sql.Date(System.currentTimeMillis()));
      phieuNhapMoi.setNguoiGiao(nguoiGiao);
      phieuNhapMoi.setTongTienNhap(tong.floatValue());
      phieuNhapMoi.setTrangThai("CHO_DUYET");

      if (!bus.insert(phieuNhapMoi)) {
        JOptionPane.showMessageDialog(this, "❌ Không thể thêm phiếu nhập");
        return;
      }

      for (int i = 0; i < chiTietTam.size(); i++) {
        TempChiTietNhap ct = chiTietTam.get(i);
        boolean inserted = ctPhieuNhapBUS.insert(
          taoMaCTPN(maPhieuNhap, i),
          maPhieuNhap,
          ct.maThuoc,
          ct.soLuong,
          ct.donGiaNhap,
          ct.hanSuDung
        );
        if (!inserted) {
          JOptionPane.showMessageDialog(
            this,
            "❌ Tạo phiếu thành công nhưng thêm dòng thuốc thất bại ở dòng " +
              (i + 1) +
              ".\nVui lòng mở chi tiết phiếu để kiểm tra lại."
          );
          loadData();
          return;
        }
      }

      JOptionPane.showMessageDialog(this, "✅ Thêm phiếu nhập thành công");
      loadData();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "❌ Dữ liệu không hợp lệ");
    }
  }

  private String taoMaCTPN(String maPhieuNhap, int stt) {
    return "CT" + maPhieuNhap + "_" + (stt + 1);
  }

  private TempChiTietNhap hienThiDialogChonThuocNhap(
    List<ThuocDTO> danhSachThuoc,
    TempChiTietNhap macDinh
  ) {
    JComboBox<String> cboThuoc = new JComboBox<>();
    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    int selectedIndex = 0;
    for (int i = 0; i < danhSachThuoc.size(); i++) {
      ThuocDTO t = danhSachThuoc.get(i);
      cboThuoc.addItem(
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
    cboThuoc.setSelectedIndex(selectedIndex);

    JSpinner spSoLuong = new JSpinner(
      new SpinnerNumberModel(
        macDinh == null ? 1 : macDinh.soLuong,
        1,
        100000,
        1
      )
    );
    JTextField txtDonGia = new JTextField(
      macDinh == null
        ? String.valueOf(danhSachThuoc.get(selectedIndex).getDonGiaBan())
        : macDinh.donGiaNhap.toPlainString()
    );
    JTextField txtHanSuDung = new JTextField(
      macDinh == null || macDinh.hanSuDung == null
        ? ""
        : macDinh.hanSuDung
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    );

    cboThuoc.addActionListener(e -> {
      int idx = cboThuoc.getSelectedIndex();
      if (idx >= 0 && idx < danhSachThuoc.size()) {
        txtDonGia.setText(
          String.valueOf(danhSachThuoc.get(idx).getDonGiaBan())
        );
      }
    });

    Object[] message = {
      "Chọn thuốc:",
      cboThuoc,
      "Số lượng nhập:",
      spSoLuong,
      "Đơn giá nhập:",
      txtDonGia,
      "Hạn sử dụng (dd/MM/yyyy, để trống nếu chưa có):",
      txtHanSuDung,
    };

    int option = JOptionPane.showConfirmDialog(
      this,
      message,
      macDinh == null ? "Thêm thuốc vào phiếu nhập" : "Sửa dòng thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (option != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      int idx = cboThuoc.getSelectedIndex();
      if (idx < 0 || idx >= danhSachThuoc.size()) {
        JOptionPane.showMessageDialog(this, "❌ Vui lòng chọn thuốc");
        return null;
      }

      ThuocDTO thuoc = danhSachThuoc.get(idx);
      int soLuong = (int) spSoLuong.getValue();
      BigDecimal donGia = new BigDecimal(txtDonGia.getText().trim());
      if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
        JOptionPane.showMessageDialog(this, "❌ Đơn giá nhập phải lớn hơn 0");
        return null;
      }

      LocalDateTime hanSuDung = null;
      String hanText = txtHanSuDung.getText().trim();
      if (!hanText.isEmpty()) {
        LocalDate ngay = LocalDate.parse(
          hanText,
          DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        if (ngay.isBefore(LocalDate.now())) {
          JOptionPane.showMessageDialog(
            this,
            "❌ Hạn sử dụng phải từ hôm nay trở đi"
          );
          return null;
        }
        hanSuDung = ngay.atStartOfDay();
      }

      return new TempChiTietNhap(
        thuoc.getMaThuoc(),
        thuoc.getTenThuoc(),
        soLuong,
        donGia,
        hanSuDung
      );
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "❌ Dữ liệu dòng thuốc không hợp lệ");
      return null;
    }
  }

  private static class TempChiTietNhap {

    private final String maThuoc;
    private final String tenThuoc;
    private final int soLuong;
    private final BigDecimal donGiaNhap;
    private final LocalDateTime hanSuDung;

    private TempChiTietNhap(
      String maThuoc,
      String tenThuoc,
      int soLuong,
      BigDecimal donGiaNhap,
      LocalDateTime hanSuDung
    ) {
      this.maThuoc = maThuoc;
      this.tenThuoc = tenThuoc;
      this.soLuong = soLuong;
      this.donGiaNhap = donGiaNhap;
      this.hanSuDung = hanSuDung;
    }

    private BigDecimal tinhThanhTien() {
      return donGiaNhap.multiply(BigDecimal.valueOf(soLuong));
    }
  }

  private void xuatBaoCaoExcel() {
    JTable table = taoBangTamDanhSachPhieuNhap();
    String boLoc = txtTimKiem.getText().trim().isEmpty()
      ? "Toàn bộ"
      : "Từ khóa: " + txtTimKiem.getText().trim();
    ExcelExport.exportOperationalTableToCsv(table, "PhieuNhap", boLoc);
  }

  private void xuatBaoCaoPdf() {
    JTable table = taoBangTamDanhSachPhieuNhap();
    String boLoc = txtTimKiem.getText().trim().isEmpty()
      ? "Toàn bộ"
      : "Từ khóa: " + txtTimKiem.getText().trim();
    PdfExport.exportOperationalTable(table, "Phiếu nhập", boLoc);
  }

  private JTable taoBangTamDanhSachPhieuNhap() {
    String[] cols = {
      "Mã phiếu nhập",
      "Mã NCC",
      "Ngày nhập",
      "Người giao",
      "Tổng tiền",
      "Trạng thái",
    };
    javax.swing.table.DefaultTableModel model =
      new javax.swing.table.DefaultTableModel(cols, 0);

    java.util.ArrayList<PhieuNhapDTO> danhSach = txtTimKiem
      .getText()
      .trim()
      .isEmpty()
      ? bus.getAll()
      : bus.search(txtTimKiem.getText().trim());

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    for (PhieuNhapDTO pn : danhSach) {
      model.addRow(
        new Object[] {
          pn.getMaPhieuNhap(),
          pn.getMaNCC(),
          pn.getNgayNhap() == null ? "" : sdf.format(pn.getNgayNhap()),
          pn.getNguoiGiao(),
          String.format("%,.0f", pn.getTongTienNhap()),
          StatusDisplayUtil.phieuNhap(pn.getTrangThai()),
        }
      );
    }
    return new JTable(model);
  }
}
