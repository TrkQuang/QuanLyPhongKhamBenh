package phongkham.GUI;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.LichKhamDTO;

public class DatLichKhamPanel extends JPanel {

  private HoSoBenhAnBUS hsBUS = new HoSoBenhAnBUS();
  private LichKhamBUS lkBUS = new LichKhamBUS();

  //Thông tin cá nhân - components
  private JTextField txtHoTen, txtSDT, txtCCCD, txtDiaChi;
  private JDateChooser dateNgaySinh;
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
    dateNgaySinh.setDateFormatString("dd/mm/yyyy");
    center.add(dateNgaySinh);
    JLabel lblGioiTinh = createLabel("Giới tính");
    center.add(lblGioiTinh);
    radNam = new JRadioButton("Nam");
    radNu = new JRadioButton("Nữ");
    groupGioiTinh = new ButtonGroup();
    radNam.setSelected(true);
    JPanel GioiTinh = new JPanel();
    GioiTinh.setLayout(new FlowLayout(FlowLayout.LEFT));
    GioiTinh.add(radNam);
    GioiTinh.add(radNu);
    center.add(GioiTinh);
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
    loadBacSi();
  }

  //Sau khi có Dịch vụ bus thì bỏ comment cái này
  public void loadGoiDichVu() {
    GoiDV.removeAllItems();
    GoiDichVuBUS dvBUS = new GoiDichVuBUS();
    ArrayList<GoiDichVuDTO> list = new ArrayList<>();
    // list = dvBUS.getAll();
    if (list != null && !list.isEmpty()) {
      for (GoiDichVuDTO dichvu : list) {
        GoiDV.addItem(dichvu.getMaGoi() + "-" + dichvu.getTenGoi());
      }
    }
  }

  //Bác sĩ BUS xong thì bỏ comment cái này
  public void loadBacSi() {
    BacSi.removeAllItems();
    BacSiBUS bsBUS = new BacSiBUS();
    ArrayList<BacSiDTO> list = new ArrayList<>();
    // list = bsBUS.getAll();
    if (list != null && !list.isEmpty()) {
      for (BacSiDTO bs : list) {
        BacSi.addItem(bs.getHoTen());
      }
    }
  }

  //================XỬ LÝ SỰ KIỆN===================
  private void loadEvents() {
    //sự kiện thay đổi gói dịch vụ
    GoiDV.addActionListener(e -> onGoiDichVuChanged());
    btnDangky.addActionListener(e -> onDangKy());
    btnRefresh.addActionListener(e -> onRefresh());
    btnSearch.addActionListener(e -> onSearch());
  }

  private void onGoiDichVuChanged() {
    String selected = (String) GoiDV.getSelectedItem();
    if (selected != null && !selected.isEmpty()) {
      String MaGoi = selected.split("-")[0];
      // GoiDichVuDTO goi = GoiDVBUS.getByMaGoi(goi.getMaGoi());
      // if(goi != null) {
      //   txtMoTaGoi.setText(goi.getMoTa());
      //   lblGiaGoi.setText(goi.getGiaDichVu() + " VND");
      // }
    }
  }

  private void onDangKy() {
    try {
      if (!validateInput()) {
        return;
      }
      //Tạo hồ sơ trước
      String maHoSo = generateMaHoSo();
      HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
      hs.setMaHoSo(maHoSo);
      hs.setCCCD(txtCCCD.getText());
      hs.setHoTen(txtHoTen.getText());
      hs.setDiaChi(txtDiaChi.getText());
      hs.setGioiTinh(radNam.isSelected() ? "Nam" : "Nu");
      hs.setTrangThai("CHO_KHAM");
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      hs.setNgaySinh(java.sql.Date.valueOf(sdf.format(dateNgaySinh.getDate())));
      boolean insertHS = hsBUS.dangKyBenhNhan(hs);
      if (!insertHS) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tạo mới hồ sơ");
        return;
      }

      //Tạo lịch khám
      String maLichKham = lkBUS.generateMaLichKham();
      LichKhamDTO lichKham = new LichKhamDTO();
      lichKham.setMaLichKham(maLichKham);
      
      String maBacSi = ((String) BacSi.getSelectedItem()).split(" - ")[0];
      lichKham.setMaBacSi(maBacSi);
      
      String maGoi = ((String) GoiDV.getSelectedItem()).split(" - ")[0];
      lichKham.setMaGoi(maGoi);
      
      lichKham.setMaDinhDanhTam(maHoSo);

      String ngayKham = sdf.format(new Date());
      String gioKham = (String)LichKham.getSelectedItem();
      String[] gio = gioKham.split(" - ");
      
      lichKham.setThoiGianBatDau(ngayKham + " " + gio[0] + ":00");
      lichKham.setThoiGianKetThuc(ngayKham + " " + gio[1] + ":00");
      lichKham.setTrangThai("Chờ xác nhận");
      String result = lkBUS.insert(lichKham);
      
      if (result.contains("✅")) {
        JOptionPane.showMessageDialog(this, 
          "✅ Đặt lịch khám thành công!\nMã lịch khám: " + maLichKham,
          "Thành công", 
          JOptionPane.INFORMATION_MESSAGE);
        onRefresh();
      } else {
        JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
      }

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, 
        "❌ Lỗi: " + ex.getMessage(), 
        "Lỗi", 
        JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
    }

  private void onRefresh() {
    txtHoTen.setText("");
    txtSDT.setText("");
    txtCCCD.setText("");
    txtDiaChi.setText("");
    dateNgaySinh.setDate(null);
    radNam.setSelected(true);
    GoiDV.setSelectedIndex(0);
    BacSi.setSelectedIndex(0);
    txtMoTaGoi.setText("");
    lblGiaGoi.setText("0 VND");
  }

  private void onSearch() {
    String sdt = JOptionPane.showInputDialog(
      this,
      "Nhập số điện thoại của bạn: ",
      "Tìm kiếm hồ sơ",
      JOptionPane.QUESTION_MESSAGE
    );
    if (sdt == null || sdt.trim().isEmpty()) {
      return;
    }
    ArrayList<HoSoBenhAnDTO> list = new ArrayList<>();
    list = hsBUS.getBySDT(sdt);
    if (list == null || list.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Không tìm thấy hồ sơ nào!",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
    } else {
      showHoSo(list);
    }
  }

  private void showHoSo(ArrayList<HoSoBenhAnDTO> list) {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Danh sách hồ sơ",
      true
    );
    dialog.setSize(800, 400);
    dialog.setLocationRelativeTo(this);

    //tạo table
    String[] col = {
      "Mã hồ sơ",
      "Họ tên",
      "CCCD",
      "Ngày sinh",
      "Giới tính",
      "Ngày khám",
      "Bác sĩ khám",
      "Chẩn đoán",
      "Lời dặn",
      "Trạng thái",
    };
    DefaultTableModel model = new DefaultTableModel(col, 0);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    for (HoSoBenhAnDTO hs : list) {
      model.addRow(
        new Object[] {
          hs.getMaHoSo(),
          hs.getHoTen(),
          hs.getCCCD(),
          hs.getNgaySinh() != null ? sdf.format(hs.getNgaySinh()) : "",
          hs.getGioiTinh(),
          hs.getNgayKham() != null ? sdf.format(hs.getNgayKham()) : "",
          hs.getMaBacSi(),
          hs.getChanDoan(),
          hs.getLoiDan(),
          hs.getTrangThai(),
        }
      );
    }
    JTable table = new JTable(model);
    table.setRowHeight(30);
    JScrollPane scroll = new JScrollPane(table);

    dialog.add(scroll);
    dialog.setVisible(true);
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

    if (dateNgaySinh.getDate() == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!");
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

    return true;
  }

  private String generateMaHoSo() {
    return "HS" + System.currentTimeMillis();
  }
}
