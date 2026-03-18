package phongkham.gui;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.gui.datlich.DatLichKhamDialogs;
import phongkham.gui.datlich.DatLichKhamService;

public class DatLichKhamPanel extends JPanel {

  private HoSoBenhAnBUS hsBUS = new HoSoBenhAnBUS();
  private DatLichKhamService datLichService = new DatLichKhamService();

  //Thông tin cá nhân - components
  private JTextField txtHoTen, txtSDT, txtCCCD, txtDiaChi;
  private JDateChooser dateNgaySinh, dateNgayKham;
  private JRadioButton radNam, radNu;
  private ButtonGroup groupGioiTinh;

  //Thông tin đặt khám
  private JComboBox<String> GoiDV, LichKham, BacSi;
  private JTextArea txtMoTaGoi;
  private JLabel lblGiaGoi;

  //Table & Button
  private JButton btnDangky, btnRefresh, btnSearch;

  public DatLichKhamPanel() {
    initComponents();
    loadData();
    loadEvents();
  }

  public void initComponents() {
    this.setLayout(new BorderLayout());

    // Header
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
    JLabel header = new JLabel("ĐĂNG KÝ HỒ SƠ KHÁM", JLabel.CENTER);
    header.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerPanel.add(header);
    this.add(headerPanel, BorderLayout.NORTH);

    // Center - căn trái sát với sidebar
    JPanel centerPanel = createCenter();
    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
    wrapper.add(centerPanel);

    JScrollPane center = new JScrollPane(wrapper);
    center.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    center.getVerticalScrollBar().setBlockIncrement(100);
    this.add(center, BorderLayout.CENTER);

    // Footer
    JPanel Footer = createFooter();
    this.add(Footer, BorderLayout.SOUTH);
  }

  public JPanel createCenter() {
    JPanel center = new JPanel();
    center.setPreferredSize(new Dimension(650, 800));
    center.setLayout(new GridLayout(0, 2, 15, 15));
    center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JLabel lblHoten = createLabel("Họ và tên");
    center.add(lblHoten);
    txtHoTen = new JTextField(20);
    center.add(txtHoTen);
    JLabel lblSDT = createLabel("Số điện thoại");
    center.add(lblSDT);
    txtSDT = new JTextField(20);
    center.add(txtSDT);
    JLabel lblCCCD = createLabel("Căn cước công dân");
    center.add(lblCCCD);
    txtCCCD = new JTextField(20);
    center.add(txtCCCD);
    JLabel lblDiaChi = createLabel("Địa chỉ");
    center.add(lblDiaChi);
    txtDiaChi = new JTextField(20);
    center.add(txtDiaChi);
    JLabel lblNgaySinh = createLabel("Ngày sinh");
    center.add(lblNgaySinh);
    dateNgaySinh = new JDateChooser();
    dateNgaySinh.setDateFormatString("dd/MM/yyyy");
    center.add(dateNgaySinh);
    JLabel lblGioiTinh = createLabel("Giới tính");
    center.add(lblGioiTinh);
    radNam = new JRadioButton("Nam");
    radNu = new JRadioButton("Nữ");
    groupGioiTinh = new ButtonGroup();
    groupGioiTinh.add(radNam);
    groupGioiTinh.add(radNu);
    radNam.setSelected(true);
    JPanel GioiTinh = new JPanel();
    GioiTinh.setLayout(new FlowLayout(FlowLayout.LEFT));
    GioiTinh.add(radNam);
    GioiTinh.add(radNu);
    center.add(GioiTinh);

    JLabel lblNgayKham = createLabel("Ngày khám");
    center.add(lblNgayKham);
    dateNgayKham = new JDateChooser();
    dateNgayKham.setDateFormatString("yyyy-MM-dd");
    dateNgayKham.setDate(new Date());
    center.add(dateNgayKham);

    JLabel lblGoiDV = createLabel("Gói dịch vụ");
    GoiDV = new JComboBox<>();
    center.add(lblGoiDV);
    center.add(GoiDV);
    JLabel lblMoTaGoi = createLabel("Mô tả gói dịch vụ");
    center.add(lblMoTaGoi);
    txtMoTaGoi = new JTextArea(3, 20);
    txtMoTaGoi.setLineWrap(true);
    txtMoTaGoi.setWrapStyleWord(true);
    txtMoTaGoi.setEditable(false);
    center.add(txtMoTaGoi);
    JLabel lblGia = createLabel("Giá dịch vụ");
    center.add(lblGia);
    lblGiaGoi = new JLabel("0 VNĐ");
    lblGiaGoi.setFont(new Font("Segoe UI", Font.ITALIC, 15));
    lblGiaGoi.setForeground(Color.GREEN);
    lblGiaGoi.setHorizontalAlignment(SwingConstants.LEFT);
    center.add(lblGiaGoi);
    JLabel lblLichKham = createLabel("Lịch khám");
    LichKham = new JComboBox<>(
      new String[] {
        "08:00 - 8:30",
        "08:30 - 9:00",
        "09:00 - 9:30",
        "09:30 - 10:00",
        "10:00 - 10:30",
        "10:30 - 11:00",
        "13:00 - 13:30",
        "13:30 - 14:00",
        "14:00 - 14:30",
        "14:30 - 15:00",
        "15:00 - 15:30",
        "15:30 - 16:00",
        "16:00 - 16:30",
      }
    );
    center.add(lblLichKham);
    center.add(LichKham);
    JLabel lblBacSi = createLabel("Bác sĩ");
    BacSi = new JComboBox<>();
    center.add(lblBacSi);
    center.add(BacSi);
    return center;
  }

  public JPanel createFooter() {
    JPanel footer = new JPanel();
    footer.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
    btnDangky = createButton("Đăng ký lịch khám");
    btnRefresh = createButton("Làm mới");
    btnSearch = createButton("Tìm kiếm hồ sơ khám");
    footer.add(btnDangky);
    footer.add(btnRefresh);
    footer.add(btnSearch);
    return footer;
  }

  public JLabel createLabel(String txt) {
    JLabel lbl = new JLabel(txt);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
    return lbl;
  }

  public JButton createButton(String txt) {
    JButton btn = new JButton(txt);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
    btn.setPreferredSize(new Dimension(200, 50));
    return btn;
  }

  //===================XỬ LÝ DATA===============
  private void loadData() {
    loadGoiDichVu();
    onGoiDichVuChanged();
  }

  //Sau khi có Dịch vụ bus thì bỏ comment cái này
  public void loadGoiDichVu() {
    GoiDV.removeAllItems();
    ArrayList<GoiDichVuDTO> list = datLichService.layDanhSachGoiDichVu();
    if (list != null && !list.isEmpty()) {
      for (GoiDichVuDTO dichvu : list) {
        GoiDV.addItem(dichvu.getMaGoi() + " - " + dichvu.getTenGoi());
      }
    }
  }

  private void loadBacSiTheoKhoaVaLich(String maKhoa) {
    BacSi.removeAllItems();
    ArrayList<BacSiDTO> list = datLichService.layBacSiTheoKhoaVaLich(
      maKhoa,
      dateNgayKham.getDate(),
      (String) LichKham.getSelectedItem()
    );
    if (list == null || list.isEmpty()) {
      return;
    }

    for (BacSiDTO bs : list) {
      BacSi.addItem(bs.getMaBacSi() + " - " + bs.getHoTen());
    }
  }

  //================XỬ LÝ SỰ KIỆN===================
  private void loadEvents() {
    //sự kiện thay đổi gói dịch vụ
    GoiDV.addActionListener(e -> onGoiDichVuChanged());
    LichKham.addActionListener(e -> onGoiDichVuChanged());
    dateNgayKham
      .getDateEditor()
      .addPropertyChangeListener("date", e -> onGoiDichVuChanged());
    btnDangky.addActionListener(e -> onDangKy());
    btnRefresh.addActionListener(e -> onRefresh());
    btnSearch.addActionListener(e -> onSearch());
  }

  private void onGoiDichVuChanged() {
    String selected = (String) GoiDV.getSelectedItem();
    if (selected != null && !selected.isEmpty()) {
      String MaGoi = selected.split(" - ")[0].trim();
      GoiDichVuDTO goi = datLichService.layGoiDichVuTheoMa(MaGoi);
      if (goi != null) {
        txtMoTaGoi.setText(goi.getMoTa());
        lblGiaGoi.setText(goi.getGiaDichVu() + " VND");
        loadBacSiTheoKhoaVaLich(goi.getMaKhoa());
      }
    }
  }

  private void onDangKy() {
    if (!validateInput()) return;

    String selectedGoi = (String) GoiDV.getSelectedItem();
    String maGoi = selectedGoi.split(" - ")[0].trim();
    GoiDichVuDTO goiDV = datLichService.layGoiDichVuTheoMa(maGoi);

    if (goiDV == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy gói dịch vụ");
      return;
    }
    String phuongThucThanhToan = DatLichKhamDialogs.showThanhToanDialog(
      this,
      goiDV.getGiaDichVu()
    );
    if (phuongThucThanhToan == null) {
      return;
    }

    String maBS = (String) BacSi.getSelectedItem();
    if (maBS == null || maBS.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ");
      return;
    }
    String maBacSi = maBS.split(" - ")[0];

    DatLichKhamService.DangKyInput input = new DatLichKhamService.DangKyInput(
      txtHoTen.getText().trim(),
      txtSDT.getText().trim(),
      txtCCCD.getText().trim(),
      txtDiaChi.getText().trim(),
      dateNgaySinh.getDate(),
      dateNgayKham.getDate(),
      radNam.isSelected() ? "Nam" : "Nữ",
      maGoi,
      maBacSi,
      (String) LichKham.getSelectedItem(),
      phuongThucThanhToan
    );

    DatLichKhamService.DangKyResult result = datLichService.dangKyKham(input);
    if (!result.thanhCong) {
      JOptionPane.showMessageDialog(this, result.message);
      return;
    }

    DatLichKhamDialogs.printPhieuDangKy(
      result.hoSo,
      result.lichKham,
      result.goiDichVu,
      result.hoaDon,
      phuongThucThanhToan
    );
    JOptionPane.showMessageDialog(this, "ĐĂNG KÝ THÀNH CÔNG");
    onRefresh();
  }

  private void onRefresh() {
    txtHoTen.setText("");
    txtSDT.setText("");
    txtCCCD.setText("");
    txtDiaChi.setText("");
    dateNgaySinh.setDate(null);
    dateNgayKham.setDate(new Date());
    radNam.setSelected(true);
    GoiDV.setSelectedIndex(0);
    BacSi.setSelectedIndex(0);
    txtMoTaGoi.setText("");
    lblGiaGoi.setText("0 VND");
  }

  private void onSearch() {
    String cccd = JOptionPane.showInputDialog(
      this,
      "Nhập CCCD của bạn: ",
      "Tìm kiếm hồ sơ",
      JOptionPane.QUESTION_MESSAGE
    );
    if (cccd == null || cccd.trim().isEmpty()) {
      return;
    }
    ArrayList<HoSoBenhAnDTO> list = new ArrayList<>();
    list = hsBUS.getByCCCD(cccd.trim());
    if (list == null || list.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Không tìm thấy hồ sơ nào!",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
    } else {
      DatLichKhamDialogs.showHoSoDialog(this, list);
    }
  }

  private boolean validateInput() {
    if (txtHoTen.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
      txtHoTen.requestFocus();
      return false;
    }

    if (txtSDT.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
      txtSDT.requestFocus();
      return false;
    }

    if (!txtSDT.getText().matches("^0\\d{9}$")) {
      JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!");
      txtSDT.requestFocus();
      return false;
    }

    if (txtCCCD.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD!");
      txtCCCD.requestFocus();
      return false;
    }

    if (!txtCCCD.getText().trim().matches("^\\d{9,12}$")) {
      JOptionPane.showMessageDialog(this, "CCCD không hợp lệ!");
      txtCCCD.requestFocus();
      return false;
    }

    if (dateNgaySinh.getDate() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!");
      return false;
    }

    if (dateNgayKham.getDate() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày khám!");
      return false;
    }

    if (GoiDV.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn gói dịch vụ!");
      return false;
    }

    if (BacSi.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!");
      return false;
    }

    if (dateNgayKham.getDate() != null) {
      LocalDate ngayKham = new java.sql.Date(
        dateNgayKham.getDate().getTime()
      ).toLocalDate();
      if (ngayKham.isBefore(LocalDate.now())) {
        JOptionPane.showMessageDialog(
          this,
          "Ngày khám không được trong quá khứ!"
        );
        return false;
      }
    }

    return true;
  }
}
