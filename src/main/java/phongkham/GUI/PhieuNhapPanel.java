package phongkham.gui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.Utils.ExcelExport;
import phongkham.Utils.PdfExport;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.phieunhap.PhieuNhapCreateDialog;

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

    if (list == null || list.isEmpty()) {
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

  // ✅ TẠO PANEL THÔNG TIN
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
    String trangThaiChuan = StatusNormalizer.normalizePhieuNhapStatus(
      trangThai
    );

    JLabel lbl = new JLabel(StatusDisplayUtil.phieuNhap(trangThaiChuan));
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lbl.setForeground(StatusColorUtil.phieuNhap(trangThaiChuan));
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
    btnChiTiet.addActionListener(e -> moChiTietPhieuNhap(pn.getMaPhieuNhap()));
    panel.add(btnChiTiet);

    // Nút "Duyệt" (chỉ hiện nếu CHO_DUYET)
    if (coTheDuyet(trangThai)) {
      JButton btnDuyet = createButton("Duyệt", new Color(16, 185, 129));
      btnDuyet.addActionListener(e -> duyetPhieuNhap(pn.getMaPhieuNhap()));
      panel.add(btnDuyet);
    }

    // Nút "Xác nhận nhập kho" (chỉ hiện nếu DA_DUYET)
    // Tồn kho chỉ được cộng khi xác nhận nhập kho thành công.
    if ("DA_DUYET".equals(trangThai)) {
      JButton btnXacNhanNhapKho = createButton(
        "Xác nhận nhập kho",
        new Color(16, 185, 129)
      );
      btnXacNhanNhapKho.addActionListener(e ->
        xacNhanNhapKho(pn.getMaPhieuNhap())
      );
      panel.add(btnXacNhanNhapKho);
    }

    // Nút "Xóa" (chỉ hiện nếu KHÔNG PHẢI DA_DUYET)
    // Chỉ cho xóa khi chưa nhập và chưa hủy
    if (coTheXoa(trangThai)) {
      JButton btnXoa = createButton("Xóa", new Color(239, 68, 68));
      btnXoa.addActionListener(e -> huyPhieuNhap(pn.getMaPhieuNhap()));
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
    boolean success = PhieuNhapCreateDialog.show(
      this,
      bus,
      (maCTPN, maPhieuNhap, maThuoc, soLuong, donGia, hanSuDung) ->
        ctPhieuNhapBUS.insert(
          maCTPN,
          maPhieuNhap,
          maThuoc,
          soLuong,
          donGia,
          hanSuDung
        )
    );
    if (success) {
      loadData();
    }
  }

  private void moChiTietPhieuNhap(String maPhieuNhap) {
    ChiTietPhieuNhapPanel detailPanel = new ChiTietPhieuNhapPanel(
      maPhieuNhap,
      this
    );
    removeAll();
    setLayout(new BorderLayout());
    add(detailPanel, BorderLayout.CENTER);
    revalidate();
    repaint();
  }

  private boolean coTheDuyet(String trangThai) {
    return "CHUA_DUYET".equals(trangThai) || "CHO_DUYET".equals(trangThai);
  }

  private boolean coTheXoa(String trangThai) {
    return !"DA_NHAP".equals(trangThai) && !"DA_HUY".equals(trangThai);
  }

  private void duyetPhieuNhap(String maPhieuNhap) {
    if (!confirm("Xác nhận duyệt phiếu nhập này?")) {
      return;
    }

    if (bus.capNhatTrangThai(maPhieuNhap, "DA_DUYET")) {
      JOptionPane.showMessageDialog(this, "✅ Đã duyệt phiếu thành công!");
      loadData();
      return;
    }
    JOptionPane.showMessageDialog(this, "❌ Lỗi khi duyệt phiếu!");
  }

  private void xacNhanNhapKho(String maPhieuNhap) {
    if (
      !confirm(
        "Xác nhận nhập kho phiếu này?\nHệ thống sẽ cộng số lượng tồn kho thuốc."
      )
    ) {
      return;
    }

    if (ctPhieuNhapBUS.xacNhanNhapKho(maPhieuNhap)) {
      JOptionPane.showMessageDialog(this, "✅ Nhập kho thành công!");
      refreshData();
      return;
    }
    JOptionPane.showMessageDialog(
      this,
      "❌ Không thể nhập kho!\nVui lòng kiểm tra trạng thái phiếu và chi tiết nhập."
    );
  }

  private void huyPhieuNhap(String maPhieuNhap) {
    if (!confirm("Xác nhận hủy phiếu nhập này?")) {
      return;
    }

    if (bus.delete(maPhieuNhap)) {
      JOptionPane.showMessageDialog(this, "✅ Đã hủy thành công!");
      loadData();
      return;
    }
    JOptionPane.showMessageDialog(this, "❌ Không thể hủy phiếu!");
  }

  private void xuatBaoCaoExcel() {
    JTable table = taoBangTamDanhSachPhieuNhap();
    ExcelExport.exportOperationalTableToCsv(table, "PhieuNhap", taoBoLocText());
  }

  private void xuatBaoCaoPdf() {
    JTable table = taoBangTamDanhSachPhieuNhap();
    PdfExport.exportOperationalTable(table, "Phiếu nhập", taoBoLocText());
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

    java.util.ArrayList<PhieuNhapDTO> danhSach = layDanhSachTheoBoLoc();

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

  private String taoBoLocText() {
    String keyword = txtTimKiem.getText().trim();
    return keyword.isEmpty() ? "Toàn bộ" : "Từ khóa: " + keyword;
  }

  private ArrayList<PhieuNhapDTO> layDanhSachTheoBoLoc() {
    String keyword = txtTimKiem.getText().trim();
    return keyword.isEmpty() ? bus.getAll() : bus.search(keyword);
  }
}
