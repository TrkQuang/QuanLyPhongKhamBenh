package phongkham.GUI;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import phongkham.BUS.LichKhamBUS;
import phongkham.DTO.LichKhamDTO;

public class DatLichKhamPanel extends JPanel {

  private LichKhamBUS lichKhamBUS;
  private JTextField txtHoTen, txtSoDienThoai, txtEmail;
  private JComboBox<String> cboBacSi, cboGoiDichVu, cboNgay, cboGio;
  private JTextArea txtGhiChu;

  public DatLichKhamPanel() {
    lichKhamBUS = new LichKhamBUS();
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout(15, 15));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title
    JLabel lblTitle = new JLabel("ĐẶT LỊCH KHÁM BỆNH");
    lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

    // Form
    JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

    formPanel.add(new JLabel("Họ và tên: *"));
    txtHoTen = new JTextField();
    formPanel.add(txtHoTen);

    formPanel.add(new JLabel("Số điện thoại: *"));
    txtSoDienThoai = new JTextField();
    formPanel.add(txtSoDienThoai);

    formPanel.add(new JLabel("Email:"));
    txtEmail = new JTextField();
    formPanel.add(txtEmail);

    formPanel.add(new JLabel("Gói dịch vụ: *"));
    cboGoiDichVu = new JComboBox<>(
      new String[] { "GOI001", "GOI002", "GOI003" }
    );
    formPanel.add(cboGoiDichVu);

    formPanel.add(new JLabel("Bác sĩ: *"));
    cboBacSi = new JComboBox<>(
      new String[] { "BS001", "BS002", "BS003", "BS004" }
    );
    formPanel.add(cboBacSi);

    formPanel.add(new JLabel("Ngày khám: *"));
    cboNgay = new JComboBox<>(generateNextDays(14));
    formPanel.add(cboNgay);

    formPanel.add(new JLabel("Giờ khám: *"));
    cboGio = new JComboBox<>(
      new String[] {
        "08:00",
        "08:30",
        "09:00",
        "09:30",
        "10:00",
        "10:30",
        "13:00",
        "13:30",
        "14:00",
        "14:30",
        "15:00",
        "15:30",
      }
    );
    formPanel.add(cboGio);

    formPanel.add(new JLabel("Ghi chú:"));
    txtGhiChu = new JTextArea(3, 20);
    JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
    formPanel.add(scrollGhiChu);

    // Buttons
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    JButton btnDatLich = new JButton("Đặt lịch khám");
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnXemLich = new JButton("Xem lịch của tôi");

    btnDatLich.addActionListener(e -> datLichKham());
    btnLamMoi.addActionListener(e -> lamMoi());
    btnXemLich.addActionListener(e -> xemLichCuaToi());

    btnPanel.add(btnDatLich);
    btnPanel.add(btnLamMoi);
    btnPanel.add(btnXemLich);

    add(lblTitle, BorderLayout.NORTH);
    add(formPanel, BorderLayout.CENTER);
    add(btnPanel, BorderLayout.SOUTH);
  }

  private String[] generateNextDays(int days) {
    String[] result = new String[days];
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    for (int i = 0; i < days; i++) {
      LocalDateTime date = LocalDateTime.now().plusDays(i);
      result[i] = date.format(formatter);
    }

    return result;
  }

  private void datLichKham() {
    // Validate
    if (txtHoTen.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập họ tên!",
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    if (txtSoDienThoai.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập số điện thoại!",
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    try {
      // Lấy thông tin
      String ngay = cboNgay.getSelectedItem().toString();
      String gio = cboGio.getSelectedItem().toString();
      String maBacSi = cboBacSi.getSelectedItem().toString();
      String maGoi = cboGoiDichVu.getSelectedItem().toString();

      String thoiGianBatDau = ngay + " " + gio + ":00";

      // Tính thời gian kết thúc (+ 30 phút)
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss"
      );
      LocalDateTime batDau = LocalDateTime.parse(thoiGianBatDau, formatter);
      LocalDateTime ketThuc = batDau.plusMinutes(30);
      String thoiGianKetThuc = ketThuc.format(formatter);

      // Tạo DTO
      LichKhamDTO lk = new LichKhamDTO();
      lk.setMaLichKham(lichKhamBUS.generateMaLichKham());
      lk.setMaGoi(maGoi);
      lk.setMaBacSi(maBacSi);
      lk.setThoiGianBatDau(thoiGianBatDau);
      lk.setThoiGianKetThuc(thoiGianKetThuc);
      lk.setTrangThai("Đã đặt");
      lk.setMaDinhDanhTam(txtSoDienThoai.getText().trim());

      // Insert
      String result = lichKhamBUS.insert(lk);

      if (result.contains("thành công")) {
        JOptionPane.showMessageDialog(
          this,
          "Đặt lịch thành công!\n\n" +
            "Mã lịch khám: " +
            lk.getMaLichKham() +
            "\n" +
            "Bác sĩ: " +
            maBacSi +
            "\n" +
            "Thời gian: " +
            thoiGianBatDau +
            "\n\n" +
            "Vui lòng đến đúng giờ!",
          "Thành công",
          JOptionPane.INFORMATION_MESSAGE
        );
        lamMoi();
      } else {
        JOptionPane.showMessageDialog(
          this,
          "Lỗi: " + result,
          "Lỗi",
          JOptionPane.ERROR_MESSAGE
        );
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        this,
        "Lỗi: " + ex.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      ex.printStackTrace();
    }
  }

  private void lamMoi() {
    txtHoTen.setText("");
    txtSoDienThoai.setText("");
    txtEmail.setText("");
    txtGhiChu.setText("");
    cboGoiDichVu.setSelectedIndex(0);
    cboBacSi.setSelectedIndex(0);
    cboNgay.setSelectedIndex(0);
    cboGio.setSelectedIndex(0);
  }

  private void xemLichCuaToi() {
    String sdt = JOptionPane.showInputDialog(
      this,
      "Nhập số điện thoại để tra cứu lịch khám:"
    );

    if (sdt != null && !sdt.trim().isEmpty()) {
      ArrayList<LichKhamDTO> danhSach = lichKhamBUS.getByMaDinhDanhTam(sdt.trim());

      if (danhSach.isEmpty()) {
        JOptionPane.showMessageDialog(
          this,
          "Không tìm thấy lịch khám với số điện thoại này!"
        );
      } else {
        showLichKhamDialog(danhSach);
      }
    }
  }

  private void showLichKhamDialog(ArrayList<LichKhamDTO> danhSach) {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(this),
      "Lịch khám của tôi",
      true
    );
    dialog.setSize(700, 400);
    dialog.setLocationRelativeTo(this);

    String[] columns = { "Mã lịch", "Bác sĩ", "Thời gian", "Trạng thái" };
    Object[][] data = new Object[danhSach.size()][4];

    for (int i = 0; i < danhSach.size(); i++) {
      LichKhamDTO lk = danhSach.get(i);
      data[i][0] = lk.getMaLichKham();
      data[i][1] = lk.getMaBacSi();
      data[i][2] = lk.getThoiGianBatDau();
      data[i][3] = lk.getTrangThai();
    }

    JTable table = new JTable(data, columns);
    table.setRowHeight(30);
    JScrollPane scrollPane = new JScrollPane(table);

    JButton btnDong = new JButton("Đóng");
    btnDong.addActionListener(e -> dialog.dispose());

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(btnDong);

    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
  }
}
