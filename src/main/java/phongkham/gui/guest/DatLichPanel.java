package phongkham.gui.guest;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.LichKhamDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;
import phongkham.gui.common.components.RoundedPanel;

public class DatLichPanel extends BasePanel {

  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();
  private final LichLamViecBUS lichLamViecBUS = new LichLamViecBUS();
  private final HoSoBenhAnBUS hoSoBenhAnBUS = new HoSoBenhAnBUS();

  private final ArrayList<BacSiDTO> allDoctors = new ArrayList<>();
  private final ArrayList<GoiDichVuDTO> allPackages = new ArrayList<>();
  private final Map<String, BacSiDTO> visibleDoctors = new LinkedHashMap<>();

  private JComboBox<String> cbBacSi;
  private JComboBox<String> cbKhungGio;
  private JComboBox<String> cbGoiKham;
  private JComboBox<String> cbThanhToan;
  private CustomTextField txtHoTen;
  private CustomTextField txtSoDienThoai;
  private CustomTextField txtCCCD;
  private JSpinner spNgaySinh;
  private JSpinner spNgayKham;
  private JButton btnXacNhanDatLich;
  private BookingReceipt lastReceipt;

  @Override
  protected void init() {
    add(createLayout(), BorderLayout.CENTER);
  }

  private JPanel createLayout() {
    JPanel wrap = new JPanel(new GridLayout(1, 2, 14, 14));
    wrap.setOpaque(false);

    RoundedPanel info = new RoundedPanel(20);
    info.setLayout(new BorderLayout());
    info.setBackground(UIConstants.BG_SURFACE);
    info.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
    info.add(
      new JLabel(
        "<html><span style='font-size:18px;font-weight:bold;'>Hướng dẫn quy trình 3 bước & Thông tin liên hệ</span><br/><span style='color:#64748B'>Thực hiện theo các bước bên dưới để đặt lịch nhanh và chính xác.</span></html>"
      ),
      BorderLayout.NORTH
    );

    info.add(buildGuideAndContactPanel(), BorderLayout.CENTER);

    wrap.add(info);
    wrap.add(UIUtils.createSection("Form đặt lịch", createForm()));
    return wrap;
  }

  private JPanel createForm() {
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    txtHoTen = (CustomTextField) UIUtils.roundedTextField("Nhập họ tên", 18);
    txtSoDienThoai = (CustomTextField) UIUtils.roundedTextField(
      "Ví dụ: 09xxxxxxxx",
      18
    );
    txtCCCD = (CustomTextField) UIUtils.roundedTextField(
      "Nhập CCCD (9-12 số)",
      18
    );
    spNgaySinh = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        new Date(),
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));
    cbBacSi = new JComboBox<>();
    spNgayKham = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        null,
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgayKham.setEditor(new JSpinner.DateEditor(spNgayKham, "dd/MM/yyyy"));
    cbKhungGio = new JComboBox<>(
      new String[] {
        "08:00 - 09:00",
        "09:00 - 10:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
      }
    );
    cbGoiKham = new JComboBox<>();
    cbThanhToan = new JComboBox<>(new String[] { "Tiền mặt", "Chuyển khoản" });
    cbBacSi.setToolTipText(
      "Chỉ hiển thị bác sĩ theo khoa gói khám, đúng ca/ngày đã chọn và có lịch DA_DUYET."
    );

    loadComboboxData();
    bindFilterEvents();

    int row = 0;
    addRow(form, gbc, row++, "Họ và tên", txtHoTen);
    addRow(form, gbc, row++, "Số điện thoại", txtSoDienThoai);
    addRow(form, gbc, row++, "CCCD", txtCCCD);
    addRow(form, gbc, row++, "Ngày sinh", spNgaySinh);
    addRow(form, gbc, row++, "Bác sĩ", cbBacSi);
    addRow(form, gbc, row++, "Ngày khám", spNgayKham);
    addRow(form, gbc, row++, "Khung giờ", cbKhungGio);
    addRow(form, gbc, row++, "Gói khám", cbGoiKham);
    addRow(form, gbc, row++, "Thanh toán", cbThanhToan);

    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;

    JPanel actions = new JPanel(new BorderLayout(0, 8));
    actions.setOpaque(false);

    JButton btnTraCuu = UIUtils.ghostButton("Tra cứu");
    JButton btnLamMoi = UIUtils.ghostButton("Làm mới");
    btnXacNhanDatLich = UIUtils.primaryButton("Xác nhận đặt lịch");

    JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    bottomRow.setOpaque(false);
    bottomRow.add(btnLamMoi);
    bottomRow.add(btnXacNhanDatLich);

    actions.add(btnTraCuu, BorderLayout.NORTH);
    actions.add(bottomRow, BorderLayout.SOUTH);

    btnTraCuu.addActionListener(e -> openLookupCccdDialog());
    btnLamMoi.addActionListener(e -> resetForm());
    btnXacNhanDatLich.addActionListener(e -> createBookingStub());

    form.add(actions, gbc);
    return form;
  }

  private JPanel buildGuideAndContactPanel() {
    JPanel wrapper = new JPanel(new BorderLayout(0, 12));
    wrapper.setOpaque(false);

    RoundedPanel guide = new RoundedPanel(16);
    guide.setBackground(new Color(219, 234, 254));
    guide.setLayout(new BorderLayout(0, 8));
    guide.setBorder(
      javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
    );
    guide.add(
      new JLabel("<html><b>Quy trình 3 bước</b></html>"),
      BorderLayout.NORTH
    );

    JPanel guideRows = new JPanel(new GridLayout(3, 1, 0, 8));
    guideRows.setOpaque(false);
    guideRows.add(
      new JLabel(
        "<html><b>Bước 1:</b> Điền đầy đủ thông tin đặt lịch ở form bên phải.</html>"
      )
    );
    guideRows.add(
      new JLabel(
        "<html><b>Bước 2:</b> Bấm nút Xác nhận đặt lịch để tạo mã lịch.</html>"
      )
    );
    guideRows.add(
      new JLabel(
        "<html><b>Bước 3:</b> Đến phòng khám và đọc mã lịch tại quầy lễ tân.</html>"
      )
    );
    guide.add(guideRows, BorderLayout.CENTER);

    RoundedPanel contact = new RoundedPanel(16);
    contact.setBackground(new Color(241, 245, 249));
    contact.setLayout(new BorderLayout(0, 8));
    contact.setBorder(
      javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
    );
    contact.add(
      new JLabel("<html><b>Thông tin liên hệ</b></html>"),
      BorderLayout.NORTH
    );

    JPanel contactRows = new JPanel(new GridLayout(4, 1, 0, 6));
    contactRows.setOpaque(false);
    contactRows.add(new JLabel("Hotline hỗ trợ: 1900 6868"));
    contactRows.add(
      new JLabel("Giờ làm việc: 07:00 - 17:30 (Thứ 2 - Chủ nhật)")
    );
    contactRows.add(new JLabel("Địa chỉ: 123 Nguyễn Trãi, Quận 5, TP.HCM"));
    contactRows.add(new JLabel("Email: hotro@phongkhamdakhoa.vn"));
    contact.add(contactRows, BorderLayout.CENTER);

    wrapper.add(guide, BorderLayout.NORTH);
    wrapper.add(contact, BorderLayout.CENTER);
    return wrapper;
  }

  private void addRow(
    JPanel form,
    GridBagConstraints gbc,
    int row,
    String label,
    java.awt.Component field
  ) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    form.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    form.add(field, gbc);
  }

  private void loadComboboxData() {
    cbGoiKham.removeAllItems();
    allDoctors.clear();
    allPackages.clear();

    ArrayList<BacSiDTO> dsBacSi = bacSiBUS.getAll();
    for (BacSiDTO bacSi : dsBacSi) {
      allDoctors.add(bacSi);
    }

    ArrayList<GoiDichVuDTO> dsGoi = goiDichVuBUS.getAll();
    for (GoiDichVuDTO goi : dsGoi) {
      allPackages.add(goi);
      cbGoiKham.addItem(goi.getMaGoi() + " - " + goi.getTenGoi());
    }

    refreshAvailableDoctors();
  }

  private void bindFilterEvents() {
    cbGoiKham.addActionListener(e -> refreshAvailableDoctors());
    cbKhungGio.addActionListener(e -> refreshAvailableDoctors());
    spNgayKham.addChangeListener(e -> refreshAvailableDoctors());
  }

  private void refreshAvailableDoctors() {
    cbBacSi.removeAllItems();
    visibleDoctors.clear();

    GoiDichVuDTO goi = getSelectedPackage();
    if (goi == null) {
      cbBacSi.addItem("-- Chưa có gói khám hợp lệ --");
      updateDoctorSelectionUX(false, "", "");
      return;
    }

    String ngayKham = new SimpleDateFormat("yyyy-MM-dd").format(
      (Date) spNgayKham.getValue()
    );
    String caLam = mapKhungGioToCa(
      String.valueOf(cbKhungGio.getSelectedItem())
    );

    for (BacSiDTO bacSi : allDoctors) {
      if (!equalsIgnoreCase(bacSi.getMaKhoa(), goi.getMaKhoa())) {
        continue;
      }
      if (!hasApprovedShiftInSlot(bacSi.getMaBacSi(), ngayKham, caLam)) {
        continue;
      }

      String label = bacSi.getMaBacSi() + " - " + bacSi.getHoTen();
      visibleDoctors.put(label, bacSi);
      cbBacSi.addItem(label);
    }

    if (cbBacSi.getItemCount() == 0) {
      cbBacSi.addItem("-- Không có bác sĩ phù hợp ca/khoa --");
      updateDoctorSelectionUX(false, caLam, ngayKham);
      return;
    }

    updateDoctorSelectionUX(true, caLam, ngayKham);
  }

  private void updateDoctorSelectionUX(
    boolean hasMatchingDoctor,
    String caLam,
    String ngayKham
  ) {
    String caHienThi = (caLam == null || caLam.isBlank())
      ? "(chưa rõ ca)"
      : caLam;
    String ngayHienThi = (ngayKham == null || ngayKham.isBlank())
      ? "(chưa rõ ngày)"
      : ngayKham;

    String tooltip =
      "<html>Danh sách bác sĩ được lọc theo:<br/>" +
      "- Thuộc khoa của gói dịch vụ đã chọn<br/>" +
      "- Có lịch làm việc ca " +
      caHienThi +
      " ngày " +
      ngayHienThi +
      "<br/>" +
      "- Trạng thái lịch làm việc: DA_DUYET" +
      (!hasMatchingDoctor
        ? "<br/><br/><b>Hiện không có bác sĩ phù hợp, vui lòng đổi ngày/ca/gói khám.</b>"
        : "") +
      "</html>";

    cbBacSi.setToolTipText(tooltip);
    if (btnXacNhanDatLich != null) {
      btnXacNhanDatLich.setEnabled(hasMatchingDoctor);
      btnXacNhanDatLich.setToolTipText(
        hasMatchingDoctor
          ? "Xác nhận đặt lịch với bác sĩ hiện tại"
          : "Không thể đặt lịch vì chưa có bác sĩ phù hợp với bộ lọc"
      );
    }
  }

  private GoiDichVuDTO getSelectedPackage() {
    String maGoi = extractId(String.valueOf(cbGoiKham.getSelectedItem()));
    for (GoiDichVuDTO goi : allPackages) {
      if (equalsIgnoreCase(goi.getMaGoi(), maGoi)) {
        return goi;
      }
    }
    return null;
  }

  private boolean hasApprovedShiftInSlot(
    String maBacSi,
    String ngayKham,
    String caLam
  ) {
    ArrayList<LichLamViecDTO> schedules = lichLamViecBUS.getByBacSiAndNgay(
      maBacSi,
      ngayKham
    );
    for (LichLamViecDTO llv : schedules) {
      String status = StatusNormalizer.normalizeLichLamViecStatus(
        llv.getTrangThai()
      );
      if (!StatusNormalizer.DA_DUYET.equals(status)) {
        continue;
      }
      if (equalsIgnoreCase(llv.getCaLam(), caLam)) {
        return true;
      }
    }
    return false;
  }

  private String mapKhungGioToCa(String khung) {
    if (khung == null) {
      return "";
    }
    String value = khung.trim();
    if (value.startsWith("08:") || value.startsWith("09:")) {
      return "Sang";
    }
    if (value.startsWith("14:") || value.startsWith("15:")) {
      return "Chieu";
    }
    return "Toi";
  }

  private boolean equalsIgnoreCase(String a, String b) {
    if (a == null || b == null) {
      return false;
    }
    return normalize(a).equals(normalize(b));
  }

  private void createBookingStub() {
    if (
      txtHoTen.getText().trim().isEmpty() ||
      txtSoDienThoai.getText().trim().isEmpty()
    ) {
      DialogHelper.warn(this, "Vui lòng nhập đầy đủ họ tên và số điện thoại.");
      return;
    }

    String cccd = txtCCCD.getText().trim();
    if (!isValidCCCD(cccd)) {
      DialogHelper.warn(this, "CCCD không hợp lệ. Vui lòng nhập 9-12 chữ số.");
      return;
    }

    Date ngaySinh = (Date) spNgaySinh.getValue();
    if (!isValidBirthDate(ngaySinh)) {
      DialogHelper.warn(
        this,
        "Ngày sinh không hợp lệ. Vui lòng chọn ngày không vượt quá hiện tại."
      );
      return;
    }

    String maBacSi = extractId(String.valueOf(cbBacSi.getSelectedItem()));
    String maGoi = extractId(String.valueOf(cbGoiKham.getSelectedItem()));

    GoiDichVuDTO goiDaChon = getSelectedPackage();
    BacSiDTO bacSiDaChon = visibleDoctors.get(
      String.valueOf(cbBacSi.getSelectedItem())
    );

    if (goiDaChon == null) {
      DialogHelper.warn(this, "Vui lòng chọn gói khám hợp lệ.");
      return;
    }
    if (visibleDoctors.isEmpty() || bacSiDaChon == null) {
      DialogHelper.error(
        this,
        "Không có bác sĩ phù hợp với khoa của gói khám và ca đã chọn. Vui lòng đổi ngày/ca hoặc gói khám."
      );
      return;
    }

    if (maBacSi.isEmpty() || maGoi.isEmpty()) {
      DialogHelper.warn(this, "Vui lòng chọn bác sĩ và gói khám hợp lệ.");
      return;
    }

    String khung = String.valueOf(cbKhungGio.getSelectedItem());
    String[] parts = khung.split("-");
    if (parts.length < 2) {
      DialogHelper.warn(this, "Khung giờ không hợp lệ.");
      return;
    }

    Date ngay = (Date) spNgayKham.getValue();
    SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
    String ngayKham = dateOnly.format(ngay);
    String caLam = mapKhungGioToCa(khung);

    if (!equalsIgnoreCase(bacSiDaChon.getMaKhoa(), goiDaChon.getMaKhoa())) {
      DialogHelper.error(
        this,
        "Bác sĩ không thuộc khoa của gói dịch vụ đã chọn. Vui lòng chọn lại."
      );
      return;
    }

    if (!hasApprovedShiftInSlot(maBacSi, ngayKham, caLam)) {
      DialogHelper.error(
        this,
        "Bác sĩ chưa có lịch làm việc đã duyệt trong ca " +
          caLam +
          " ngày " +
          ngayKham +
          "."
      );
      return;
    }

    String batDau = ngayKham + " " + parts[0].trim() + ":00";
    String ketThuc = ngayKham + " " + parts[1].trim() + ":00";

    LichKhamDTO lich = new LichKhamDTO();
    lich.setMaLichKham(lichKhamBUS.generateMaLichKham());
    lich.setMaBacSi(maBacSi);
    lich.setMaGoi(maGoi);
    lich.setThoiGianBatDau(batDau);
    lich.setThoiGianKetThuc(ketThuc);
    lich.setTrangThai(StatusNormalizer.CHO_XAC_NHAN);
    lich.setMaDinhDanhTam(generateGuestCode());

    String result = lichKhamBUS.insert(lich);
    if (!normalize(result).contains("thanh cong")) {
      DialogHelper.error(this, result);
      return;
    }

    boolean taoHoSoCoBan = createInitialMedicalRecord(lich, ngaySinh);
    if (!taoHoSoCoBan) {
      lichKhamBUS.delete(lich.getMaLichKham());
      DialogHelper.error(
        this,
        "Đặt lịch thất bại do không thể tạo hồ sơ bệnh án ban đầu. Vui lòng thử lại."
      );
      return;
    }

    lastReceipt = new BookingReceipt(
      lich.getMaLichKham(),
      txtHoTen.getText().trim(),
      txtSoDienThoai.getText().trim(),
      cccd,
      new SimpleDateFormat("dd/MM/yyyy").format(ngaySinh),
      String.valueOf(cbBacSi.getSelectedItem()),
      String.valueOf(cbGoiKham.getSelectedItem()),
      batDau,
      String.valueOf(cbThanhToan.getSelectedItem())
    );

    File invoiceFile = createAutoInvoiceFile(lastReceipt.maLich);
    boolean exportOk = exportReceiptPdf(lastReceipt, invoiceFile);

    String message =
      "Đặt lịch thành công. Mã lịch: " +
      lich.getMaLichKham() +
      (exportOk
        ? "\nĐã tự động xuất hóa đơn: " + invoiceFile.getAbsolutePath()
        : "\nĐặt lịch thành công nhưng xuất hóa đơn tự động thất bại.");

    DialogHelper.info(this, message);
    resetForm();
    loadComboboxData();
  }

  private boolean createInitialMedicalRecord(LichKhamDTO lich, java.util.Date ngaySinh) {
    if (lich == null || lich.getMaLichKham() == null) {
      return false;
    }

    HoSoBenhAnDTO existing = hoSoBenhAnBUS.getByMaLichKham(lich.getMaLichKham());
    if (existing != null) {
      return true;
    }

    HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
    hs.setMaHoSo(generateUniqueMaHoSo());
    hs.setMaLichKham(lich.getMaLichKham());
    hs.setHoTen(txtHoTen.getText().trim());
    hs.setSoDienThoai(txtSoDienThoai.getText().trim());
    hs.setCCCD(txtCCCD.getText().trim());
    hs.setNgaySinh(ngaySinh == null ? null : new Date(ngaySinh.getTime()));
    hs.setGioiTinh("Nam");
    hs.setDiaChi("");
    hs.setNgayKham(new Date(((java.util.Date) spNgayKham.getValue()).getTime()));
    hs.setTrieuChung("");
    hs.setChanDoan("");
    hs.setKetLuan("");
    hs.setLoiDan("");
    hs.setMaBacSi(lich.getMaBacSi());
    hs.setTrangThai(StatusNormalizer.CHO_KHAM);

    return hoSoBenhAnBUS.dangKyBenhNhan(hs);
  }

  private String generateUniqueMaHoSo() {
    String ma;
    do {
      ma = "HS" + System.currentTimeMillis();
    } while (hoSoBenhAnBUS.getById(ma) != null);
    return ma;
  }

  private boolean exportReceiptPdf(BookingReceipt receipt, File file) {
    if (receipt == null || file == null) {
      return false;
    }
    try {
      Document document = new Document();
      PdfWriter.getInstance(document, new FileOutputStream(file));
      document.open();
      document.add(new Paragraph("PHIEU DAT LICH KHAM"));
      document.add(new Paragraph("----------------------------------------"));
      document.add(new Paragraph("Ma lich: " + receipt.maLich));
      document.add(new Paragraph("Ho ten: " + receipt.hoTen));
      document.add(new Paragraph("So dien thoai: " + receipt.soDienThoai));
      document.add(new Paragraph("CCCD: " + receipt.cccd));
      document.add(new Paragraph("Ngay sinh: " + receipt.ngaySinh));
      document.add(new Paragraph("Bac si: " + receipt.bacSi));
      document.add(new Paragraph("Goi kham: " + receipt.goiKham));
      document.add(new Paragraph("Thoi gian: " + receipt.thoiGianBatDau));
      document.add(new Paragraph("Thanh toan: " + receipt.hinhThucThanhToan));
      document.add(new Paragraph("----------------------------------------"));
      document.add(new Paragraph("Cam on quy khach da su dung dich vu."));
      document.close();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  private File createAutoInvoiceFile(String maLich) {
    File dir = new File("hoa_don_dat_lich");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    return new File(dir, "HoaDonDatLich_" + maLich + "_" + time + ".pdf");
  }

  private void resetForm() {
    txtHoTen.setText("");
    txtSoDienThoai.setText("");
    txtCCCD.setText("");
    spNgaySinh.setValue(new Date());
    spNgayKham.setValue(new Date());
    if (cbKhungGio.getItemCount() > 0) {
      cbKhungGio.setSelectedIndex(0);
    }
    if (cbGoiKham.getItemCount() > 0) {
      cbGoiKham.setSelectedIndex(0);
    }
    if (cbThanhToan.getItemCount() > 0) {
      cbThanhToan.setSelectedIndex(0);
    }
    lastReceipt = null;
    refreshAvailableDoctors();
  }

  private void openLookupCccdDialog() {
    javax.swing.JTextField txtLookup = new javax.swing.JTextField(18);
    JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setOpaque(false);
    panel.add(
      new JLabel("Nhập CCCD để tra cứu hồ sơ đã khám"),
      BorderLayout.NORTH
    );
    panel.add(txtLookup, BorderLayout.CENTER);

    int result = JOptionPane.showConfirmDialog(
      this,
      panel,
      "Tra cứu hồ sơ theo CCCD",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );
    if (result != JOptionPane.OK_OPTION) {
      return;
    }

    String cccd = txtLookup.getText() == null ? "" : txtLookup.getText().trim();
    searchMedicalRecordsByCCCD(cccd);
  }

  private void searchMedicalRecordsByCCCD(String cccd) {
    if (!isValidCCCD(cccd)) {
      DialogHelper.warn(
        this,
        "Vui lòng nhập CCCD hợp lệ (9-12 chữ số) để tra cứu."
      );
      return;
    }

    ArrayList<HoSoBenhAnDTO> records = hoSoBenhAnBUS.getByCCCD(cccd);
    ArrayList<HoSoBenhAnDTO> daKham = new ArrayList<>();
    if (records != null) {
      for (HoSoBenhAnDTO hs : records) {
        if (
          StatusNormalizer.DA_KHAM.equals(
            StatusNormalizer.normalizeHoSoStatus(hs.getTrangThai())
          )
        ) {
          daKham.add(hs);
        }
      }
    }

    if (daKham.isEmpty()) {
      DialogHelper.info(
        this,
        "Không tìm thấy hồ sơ bệnh án đã khám theo CCCD này."
      );
      return;
    }

    DefaultTableModel model = new DefaultTableModel(
      new Object[] {
        "Mã hồ sơ",
        "Ngày khám",
        "Họ tên",
        "SĐT",
        "Chẩn đoán",
        "Kết luận",
        "Mã bác sĩ",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    for (HoSoBenhAnDTO hs : daKham) {
      model.addRow(
        new Object[] {
          hs.getMaHoSo(),
          hs.getNgayKham(),
          hs.getHoTen(),
          hs.getSoDienThoai(),
          hs.getChanDoan(),
          hs.getKetLuan(),
          hs.getMaBacSi(),
        }
      );
    }

    JTable table = new JTable(model);
    UIUtils.styleTable(table);
    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new java.awt.Dimension(900, 280));

    JOptionPane.showMessageDialog(
      this,
      scroll,
      "Danh sách hồ sơ đã khám theo CCCD: " + cccd,
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  private boolean isValidCCCD(String cccd) {
    return cccd != null && cccd.matches("^\\d{9,12}$");
  }

  private boolean isValidBirthDate(Date ngaySinh) {
    return ngaySinh != null && !ngaySinh.after(new Date());
  }

  private String extractId(String text) {
    if (text == null) {
      return "";
    }
    int idx = text.indexOf(" - ");
    return idx > 0 ? text.substring(0, idx).trim() : text.trim();
  }

  private String generateGuestCode() {
    String phone = txtSoDienThoai.getText().replaceAll("\\D", "");
    String suffix =
      phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
    return "GUEST-" + suffix + "-" + System.currentTimeMillis();
  }

  private String normalize(String text) {
    if (text == null) {
      return "";
    }
    String lowered = text
      .toLowerCase(Locale.ROOT)
      .replace('á', 'a')
      .replace('à', 'a')
      .replace('ả', 'a')
      .replace('ã', 'a')
      .replace('ạ', 'a')
      .replace('ă', 'a')
      .replace('ắ', 'a')
      .replace('ằ', 'a')
      .replace('ẳ', 'a')
      .replace('ẵ', 'a')
      .replace('ặ', 'a')
      .replace('â', 'a')
      .replace('ấ', 'a')
      .replace('ầ', 'a')
      .replace('ẩ', 'a')
      .replace('ẫ', 'a')
      .replace('ậ', 'a')
      .replace('é', 'e')
      .replace('è', 'e')
      .replace('ẻ', 'e')
      .replace('ẽ', 'e')
      .replace('ẹ', 'e')
      .replace('ê', 'e')
      .replace('ế', 'e')
      .replace('ề', 'e')
      .replace('ể', 'e')
      .replace('ễ', 'e')
      .replace('ệ', 'e')
      .replace('í', 'i')
      .replace('ì', 'i')
      .replace('ỉ', 'i')
      .replace('ĩ', 'i')
      .replace('ị', 'i')
      .replace('ó', 'o')
      .replace('ò', 'o')
      .replace('ỏ', 'o')
      .replace('õ', 'o')
      .replace('ọ', 'o')
      .replace('ô', 'o')
      .replace('ố', 'o')
      .replace('ồ', 'o')
      .replace('ổ', 'o')
      .replace('ỗ', 'o')
      .replace('ộ', 'o')
      .replace('ơ', 'o')
      .replace('ớ', 'o')
      .replace('ờ', 'o')
      .replace('ở', 'o')
      .replace('ỡ', 'o')
      .replace('ợ', 'o')
      .replace('ú', 'u')
      .replace('ù', 'u')
      .replace('ủ', 'u')
      .replace('ũ', 'u')
      .replace('ụ', 'u')
      .replace('ư', 'u')
      .replace('ứ', 'u')
      .replace('ừ', 'u')
      .replace('ử', 'u')
      .replace('ữ', 'u')
      .replace('ự', 'u')
      .replace('ý', 'y')
      .replace('ỳ', 'y')
      .replace('ỷ', 'y')
      .replace('ỹ', 'y')
      .replace('ỵ', 'y')
      .replace('đ', 'd');
    return lowered;
  }

  private static class BookingReceipt {

    private final String maLich;
    private final String hoTen;
    private final String soDienThoai;
    private final String cccd;
    private final String ngaySinh;
    private final String bacSi;
    private final String goiKham;
    private final String thoiGianBatDau;
    private final String hinhThucThanhToan;

    private BookingReceipt(
      String maLich,
      String hoTen,
      String soDienThoai,
      String cccd,
      String ngaySinh,
      String bacSi,
      String goiKham,
      String thoiGianBatDau,
      String hinhThucThanhToan
    ) {
      this.maLich = maLich;
      this.hoTen = hoTen;
      this.soDienThoai = soDienThoai;
      this.cccd = cccd;
      this.ngaySinh = ngaySinh;
      this.bacSi = bacSi;
      this.goiKham = goiKham;
      this.thoiGianBatDau = thoiGianBatDau;
      this.hinhThucThanhToan = hinhThucThanhToan;
    }
  }
}
