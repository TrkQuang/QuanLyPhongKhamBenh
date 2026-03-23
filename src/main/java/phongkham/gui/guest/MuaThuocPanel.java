package phongkham.gui.guest;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTHDThuocBUS;
import phongkham.BUS.HoaDonThuocBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.HoaDonThuocDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;

public class MuaThuocPanel extends BasePanel {

  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final HoaDonThuocBUS hoaDonThuocBUS = new HoaDonThuocBUS();
  private final CTHDThuocBUS cthdThuocBUS = new CTHDThuocBUS();

  private final ArrayList<ThuocDTO> allMedicines = new ArrayList<>();
  private final Map<String, CartItem> cartItems = new LinkedHashMap<>();
  private final Map<String, Integer> stockByMedicine = new HashMap<>();

  private JTable tblThuoc;
  private JTable tblCart;
  private DefaultTableModel medicineModel;
  private DefaultTableModel cartModel;

  private CustomTextField txtSearch;
  private JComboBox<String> cbSort;
  private CustomTextField txtTenKhach;
  private CustomTextField txtSdt;
  private JComboBox<String> cbThanhToan;
  private JLabel lblCartNotice;
  private boolean isUpdatingCartModel = false;

  private static final DecimalFormat MONEY = new DecimalFormat("#,##0");

  @Override
  protected void init() {
    JSplitPane split = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      buildMedicineSection(),
      buildCartSection()
    );
    split.setResizeWeight(0.6);
    split.setDividerSize(8);
    split.setBorder(null);

    add(split, BorderLayout.CENTER);

    loadDataFromBus();
    refreshMedicineTable();
    refreshCartTable();
  }

  private JPanel buildMedicineSection() {
    JPanel body = new JPanel(new BorderLayout(0, 10));
    body.setOpaque(false);

    JPanel toolbar = new JPanel(new BorderLayout(8, 0));
    toolbar.setOpaque(false);

    JPanel toolbarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    toolbarLeft.setOpaque(false);
    JPanel toolbarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    toolbarRight.setOpaque(false);

    txtSearch = (CustomTextField) UIUtils.roundedTextField(
      "Tìm theo tên thuốc",
      18
    );
    cbSort = new JComboBox<>(
      new String[] { "Mặc định", "Giá tăng", "Giá giảm", "Tên A-Z" }
    );

    javax.swing.JButton btnLoc = UIUtils.primaryButton("Lọc");
    btnLoc.addActionListener(e -> refreshMedicineTable());
    txtSearch.addActionListener(e -> refreshMedicineTable());

    toolbarLeft.add(txtSearch);
    toolbarLeft.add(cbSort);
    toolbarRight.add(btnLoc);
    toolbar.add(toolbarLeft, BorderLayout.CENTER);
    toolbar.add(toolbarRight, BorderLayout.EAST);

    medicineModel = new DefaultTableModel(
      new Object[] {
        "Mã",
        "Tên thuốc",
        "Hoạt chất",
        "Đơn giá",
        "Tồn",
        "SL mua",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 5;
      }
    };
    tblThuoc = new JTable(medicineModel);
    UIUtils.styleTable(tblThuoc);

    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    footer.setOpaque(false);
    javax.swing.JButton btnThemNoiBat = UIUtils.primaryButton(
      "Thêm vào giỏ hàng đã chọn"
    );
    btnThemNoiBat.addActionListener(e -> addSelectedMedicineToCart());
    footer.add(btnThemNoiBat);

    body.add(toolbar, BorderLayout.NORTH);
    body.add(new JScrollPane(tblThuoc), BorderLayout.CENTER);
    body.add(footer, BorderLayout.SOUTH);
    return UIUtils.createSection("Danh sách thuốc", body);
  }

  private JPanel buildCartSection() {
    JPanel body = new JPanel(new BorderLayout(0, 10));
    body.setOpaque(false);

    cartModel = new DefaultTableModel(
      new Object[] { "Mã", "Tên thuốc", "Đơn giá", "SL", "Thành tiền" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 3;
      }
    };
    tblCart = new JTable(cartModel);
    UIUtils.styleTable(tblCart);
    cartModel.addTableModelListener(e -> {
      if (isUpdatingCartModel || e.getFirstRow() < 0 || e.getColumn() != 3) {
        return;
      }
      updateCartQuantityFromTable(e.getFirstRow());
    });

    JPanel customerForm = new JPanel(new BorderLayout(0, 8));
    customerForm.setOpaque(false);
    txtTenKhach = (CustomTextField) UIUtils.roundedTextField(
      "Tên khách hàng",
      18
    );
    txtSdt = (CustomTextField) UIUtils.roundedTextField("SĐT khách hàng", 18);
    cbThanhToan = new JComboBox<>(new String[] { "Tiền mặt", "Chuyển khoản" });

    JPanel formRow1 = new JPanel(new BorderLayout(8, 0));
    formRow1.setOpaque(false);
    formRow1.add(txtTenKhach, BorderLayout.CENTER);
    formRow1.add(cbThanhToan, BorderLayout.EAST);

    customerForm.add(formRow1, BorderLayout.NORTH);
    customerForm.add(txtSdt, BorderLayout.SOUTH);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    actions.setOpaque(false);
    lblCartNotice = new JLabel(" ");
    lblCartNotice.setForeground(new java.awt.Color(34, 197, 94));
    lblCartNotice.setFont(
      new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12)
    );
    JButtonWithText lblTong = new JButtonWithText();
    javax.swing.JButton btnXoaDong = UIUtils.ghostButton("Xóa dòng");
    javax.swing.JButton btnXoaGio = UIUtils.ghostButton("Xóa giỏ");
    javax.swing.JButton btnThanhToan = UIUtils.primaryButton(
      "Thanh toán + In PDF"
    );

    btnXoaDong.addActionListener(e -> removeSelectedCartItem());
    btnXoaGio.addActionListener(e -> clearCart());
    btnThanhToan.addActionListener(e -> checkoutAndExport(lblTong));

    actions.add(lblCartNotice);
    actions.add(lblTong);
    actions.add(btnXoaDong);
    actions.add(btnXoaGio);
    actions.add(btnThanhToan);

    body.add(new JScrollPane(tblCart), BorderLayout.CENTER);
    body.add(customerForm, BorderLayout.NORTH);
    body.add(actions, BorderLayout.SOUTH);
    return UIUtils.createSection("Giỏ hàng hiện tại", body);
  }

  private void loadDataFromBus() {
    allMedicines.clear();
    stockByMedicine.clear();
    for (ThuocDTO thuoc : thuocBUS.list()) {
      allMedicines.add(thuoc);
      stockByMedicine.put(thuoc.getMaThuoc(), thuoc.getSoLuongTon());
    }
  }

  private void refreshMedicineTable() {
    medicineModel.setRowCount(0);

    String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
    String sort = String.valueOf(cbSort.getSelectedItem());

    ArrayList<ThuocDTO> filtered = new ArrayList<>();
    for (ThuocDTO thuoc : allMedicines) {
      if (
        keyword.isEmpty() ||
        (thuoc.getTenThuoc() != null &&
          thuoc.getTenThuoc().toLowerCase(Locale.ROOT).contains(keyword))
      ) {
        filtered.add(thuoc);
      }
    }

    if ("Giá tăng".equals(sort)) {
      filtered.sort(
        java.util.Comparator.comparingDouble(ThuocDTO::getDonGiaBan)
      );
    } else if ("Giá giảm".equals(sort)) {
      filtered.sort(
        java.util.Comparator.comparingDouble(ThuocDTO::getDonGiaBan).reversed()
      );
    } else if ("Tên A-Z".equals(sort)) {
      filtered.sort(
        java.util.Comparator.comparing(
          ThuocDTO::getTenThuoc,
          String.CASE_INSENSITIVE_ORDER
        )
      );
    }

    for (ThuocDTO thuoc : filtered) {
      medicineModel.addRow(
        new Object[] {
          thuoc.getMaThuoc(),
          thuoc.getTenThuoc(),
          thuoc.getHoatChat(),
          thuoc.getDonGiaBan(),
          thuoc.getSoLuongTon(),
          1,
        }
      );
    }
  }

  private void addSelectedMedicineToCart() {
    int row = tblThuoc.getSelectedRow();
    if (row < 0) {
      DialogHelper.warn(this, "Vui lòng chọn thuốc để thêm vào giỏ.");
      return;
    }

    int modelRow = tblThuoc.convertRowIndexToModel(row);
    String maThuoc = String.valueOf(medicineModel.getValueAt(modelRow, 0));
    String tenThuoc = String.valueOf(medicineModel.getValueAt(modelRow, 1));
    double donGia = Double.parseDouble(
      String.valueOf(medicineModel.getValueAt(modelRow, 3))
    );
    int tonKho = Integer.parseInt(
      String.valueOf(medicineModel.getValueAt(modelRow, 4))
    );

    int soLuongMua;
    try {
      soLuongMua = Integer.parseInt(
        String.valueOf(medicineModel.getValueAt(modelRow, 5))
      );
    } catch (Exception ex) {
      DialogHelper.warn(this, "Số lượng mua không hợp lệ.");
      return;
    }

    if (soLuongMua <= 0) {
      DialogHelper.warn(this, "Số lượng mua phải lớn hơn 0.");
      return;
    }

    CartItem current = cartItems.get(maThuoc);
    int newQty = (current == null ? 0 : current.soLuong) + soLuongMua;
    if (newQty > tonKho) {
      DialogHelper.warn(this, "Số lượng vượt tồn kho.");
      return;
    }

    cartItems.put(maThuoc, new CartItem(maThuoc, tenThuoc, donGia, newQty));
    refreshCartTable();
    showCartNotice("Da them " + soLuongMua + " " + tenThuoc + " vao gio hang");
  }

  private void refreshCartTable() {
    isUpdatingCartModel = true;
    cartModel.setRowCount(0);
    for (CartItem item : cartItems.values()) {
      double thanhTien = item.donGia * item.soLuong;
      cartModel.addRow(
        new Object[] {
          item.maThuoc,
          item.tenThuoc,
          item.donGia,
          item.soLuong,
          thanhTien,
        }
      );
    }
    isUpdatingCartModel = false;
    if (tblCart.getParent() != null) {
      tblCart.getParent().repaint();
    }
  }

  private void updateCartQuantityFromTable(int modelRow) {
    String maThuoc = String.valueOf(cartModel.getValueAt(modelRow, 0));
    CartItem current = cartItems.get(maThuoc);
    if (current == null) {
      return;
    }

    int soLuongMoi;
    try {
      soLuongMoi = Integer.parseInt(
        String.valueOf(cartModel.getValueAt(modelRow, 3)).trim()
      );
    } catch (Exception ex) {
      DialogHelper.warn(this, "So luong phai la so nguyen hop le.");
      refreshCartTable();
      return;
    }

    if (soLuongMoi <= 0) {
      DialogHelper.warn(this, "So luong phai lon hon 0.");
      refreshCartTable();
      return;
    }

    int tonKho = stockByMedicine.getOrDefault(maThuoc, Integer.MAX_VALUE);
    if (soLuongMoi > tonKho) {
      DialogHelper.warn(this, "So luong vuot ton kho hien tai.");
      refreshCartTable();
      return;
    }

    cartItems.put(
      maThuoc,
      new CartItem(
        current.maThuoc,
        current.tenThuoc,
        current.donGia,
        soLuongMoi
      )
    );
    refreshCartTable();
    showCartNotice(
      "Da cap nhat so luong: " + current.tenThuoc + " x" + soLuongMoi
    );
  }

  private void showCartNotice(String text) {
    if (lblCartNotice == null) {
      return;
    }
    lblCartNotice.setText(text);
    Timer timer = new Timer(2200, e -> lblCartNotice.setText(" "));
    timer.setRepeats(false);
    timer.start();
  }

  private void removeSelectedCartItem() {
    int row = tblCart.getSelectedRow();
    if (row < 0) {
      DialogHelper.warn(this, "Vui lòng chọn dòng trong giỏ hàng để xóa.");
      return;
    }
    String maThuoc = String.valueOf(
      cartModel.getValueAt(tblCart.convertRowIndexToModel(row), 0)
    );
    cartItems.remove(maThuoc);
    refreshCartTable();
  }

  private void clearCart() {
    cartItems.clear();
    refreshCartTable();
  }

  private void checkoutAndExport(JButtonWithText lblTong) {
    if (cartItems.isEmpty()) {
      DialogHelper.warn(this, "Giỏ hàng đang trống.");
      return;
    }

    String tenKhach = txtTenKhach.getText().trim();
    String sdt = txtSdt.getText().trim();
    if (tenKhach.isEmpty() || sdt.isEmpty()) {
      DialogHelper.warn(this, "Vui lòng nhập tên khách hàng và số điện thoại.");
      return;
    }

    double tongTien = cartItems
      .values()
      .stream()
      .mapToDouble(i -> i.donGia * i.soLuong)
      .sum();
    String hinhThuc = String.valueOf(cbThanhToan.getSelectedItem());

    // Validate tồn kho trước khi tạo hóa đơn
    for (CartItem item : cartItems.values()) {
      ThuocDTO thuoc = thuocBUS.getByMa(item.maThuoc);
      if (thuoc == null || thuoc.getSoLuongTon() < item.soLuong) {
        DialogHelper.error(
          this,
          "Thuốc " + item.tenThuoc + " không đủ tồn kho để thanh toán."
        );
        return;
      }
    }

    HoaDonThuocDTO hoaDon = new HoaDonThuocDTO();
    hoaDon.setMaDonThuoc(null);
    hoaDon.setNgayLap(LocalDateTime.now());
    hoaDon.setTongTien(tongTien);
    hoaDon.setGhiChu("Guest checkout - HinhThucThanhToan=" + hinhThuc);
    hoaDon.setTrangThaiThanhToan(StatusNormalizer.DA_THANH_TOAN);
    hoaDon.setNgayThanhToan(LocalDateTime.now());
    hoaDon.setTrangThaiLayThuoc(StatusNormalizer.CHO_LAY);
    hoaDon.setTenBenhNhan(tenKhach);
    hoaDon.setSdtBenhNhan(sdt);
    hoaDon.setActive(true);

    if (!hoaDonThuocBUS.addHoaDonThuoc(hoaDon)) {
      DialogHelper.error(this, "Không thể tạo hóa đơn thuốc.");
      return;
    }

    for (CartItem item : cartItems.values()) {
      CTHDThuocDTO ct = new CTHDThuocDTO();
      ct.setMaHoaDon(hoaDon.getMaHoaDon());
      ct.setMaThuoc(item.maThuoc);
      ct.setSoLuong(item.soLuong);
      ct.setDonGia(item.donGia);
      ct.setThanhTien(item.donGia * item.soLuong);
      ct.setActive(true);

      if (!cthdThuocBUS.addDetailMedicine(ct)) {
        DialogHelper.error(
          this,
          "Lỗi thêm chi tiết hóa đơn cho thuốc: " + item.tenThuoc
        );
        return;
      }

      if (!thuocBUS.truSoLuongTon(item.maThuoc, item.soLuong)) {
        DialogHelper.error(
          this,
          "Không thể cập nhật tồn kho cho thuốc: " + item.tenThuoc
        );
        return;
      }
    }

    File pdf = choosePdfFile(hoaDon.getMaHoaDon());
    if (pdf != null) {
      exportInvoicePdf(pdf, hoaDon, hinhThuc);
    }

    DialogHelper.info(
      this,
      "Thanh toán thành công. Mã hóa đơn: " + hoaDon.getMaHoaDon()
    );
    lblTong.setText("Tổng: 0 VND");
    clearCart();
    txtTenKhach.setText("");
    txtSdt.setText("");
    loadDataFromBus();
    refreshMedicineTable();
  }

  private File choosePdfFile(String maHoaDon) {
    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("HoaDonThuoc_" + maHoaDon + ".pdf"));
    int result = chooser.showSaveDialog(this);
    return result == JFileChooser.APPROVE_OPTION
      ? chooser.getSelectedFile()
      : null;
  }

  private void exportInvoicePdf(
    File file,
    HoaDonThuocDTO hoaDon,
    String hinhThuc
  ) {
    try {
      Document document = new Document();
      PdfWriter.getInstance(document, new FileOutputStream(file));
      document.open();

      document.add(new Paragraph("HOA DON THUOC"));
      document.add(new Paragraph("Ma hoa don: " + hoaDon.getMaHoaDon()));
      document.add(new Paragraph("Khach hang: " + hoaDon.getTenBenhNhan()));
      document.add(new Paragraph("So dien thoai: " + hoaDon.getSdtBenhNhan()));
      document.add(new Paragraph("Hinh thuc thanh toan: " + hinhThuc));
      document.add(
        new Paragraph(
          "Trang thai thanh toan: " + StatusNormalizer.DA_THANH_TOAN
        )
      );
      document.add(new Paragraph("----------------------------------------"));

      for (CartItem item : cartItems.values()) {
        document.add(
          new Paragraph(
            item.tenThuoc +
              " | Don gia: " +
              MONEY.format(item.donGia) +
              " | SL: " +
              item.soLuong +
              " | Thanh tien: " +
              MONEY.format(item.donGia * item.soLuong)
          )
        );
      }

      document.add(new Paragraph("----------------------------------------"));
      document.add(
        new Paragraph(
          "Tong tien: " + MONEY.format(hoaDon.getTongTien()) + " VND"
        )
      );
      document.close();
    } catch (Exception ex) {
      DialogHelper.error(this, "Xuất hóa đơn PDF thất bại: " + ex.getMessage());
    }
  }

  private static class CartItem {

    private final String maThuoc;
    private final String tenThuoc;
    private final double donGia;
    private final int soLuong;

    private CartItem(
      String maThuoc,
      String tenThuoc,
      double donGia,
      int soLuong
    ) {
      this.maThuoc = maThuoc;
      this.tenThuoc = tenThuoc;
      this.donGia = donGia;
      this.soLuong = soLuong;
    }
  }

  private class JButtonWithText extends JLabel {

    private JButtonWithText() {
      super("Tổng: 0 VND");
      setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
      refreshAmount();
    }

    private void refreshAmount() {
      double tong = cartItems
        .values()
        .stream()
        .mapToDouble(i -> i.donGia * i.soLuong)
        .sum();
      setText("Tổng: " + MONEY.format(tong) + " VND");
    }

    @Override
    public void repaint() {
      refreshAmount();
      super.repaint();
    }
  }
}
