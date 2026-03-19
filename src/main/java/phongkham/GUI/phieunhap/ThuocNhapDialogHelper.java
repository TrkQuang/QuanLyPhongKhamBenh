package phongkham.gui.phieunhap;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import phongkham.DTO.ThuocDTO;

public final class ThuocNhapDialogHelper {

  private ThuocNhapDialogHelper() {}

  public static Input show(
    JComponent parent,
    List<ThuocDTO> danhSachThuoc,
    Input macDinh,
    String title
  ) {
    if (danhSachThuoc == null || danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(parent, "Không có thuốc để thêm!");
      return null;
    }

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

    JSpinner spinnerSoLuong = new JSpinner(
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
        : macDinh.donGia.toPlainString()
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
      "Số lượng:",
      spinnerSoLuong,
      "Đơn giá nhập:",
      txtDonGia,
      "Hạn sử dụng (dd/MM/yyyy, tùy chọn):",
      txtHanSuDung,
    };

    int option = JOptionPane.showConfirmDialog(
      parent,
      message,
      title,
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (option != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      int idx = cboThuoc.getSelectedIndex();
      if (idx < 0 || idx >= danhSachThuoc.size()) {
        JOptionPane.showMessageDialog(parent, "Vui lòng chọn thuốc!");
        return null;
      }

      int soLuong = (int) spinnerSoLuong.getValue();
      BigDecimal donGia = new BigDecimal(txtDonGia.getText().trim());
      if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
        JOptionPane.showMessageDialog(parent, "Đơn giá phải lớn hơn 0!");
        return null;
      }

      LocalDateTime hanSuDung = parseOptionalHanSuDung(
        parent,
        txtHanSuDung.getText()
      );
      if (hanSuDung == null && !txtHanSuDung.getText().trim().isEmpty()) {
        return null;
      }

      return new Input(
        dsThuocMa(danhSachThuoc, idx),
        soLuong,
        donGia,
        hanSuDung
      );
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(parent, "Dữ liệu không hợp lệ!");
      return null;
    }
  }

  private static String dsThuocMa(List<ThuocDTO> danhSachThuoc, int idx) {
    return danhSachThuoc.get(idx).getMaThuoc();
  }

  private static LocalDateTime parseOptionalHanSuDung(
    JComponent parent,
    String raw
  ) {
    String value = raw == null ? "" : raw.trim();
    if (value.isEmpty()) {
      return null;
    }
    try {
      LocalDate date = LocalDate.parse(
        value,
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
      );
      if (date.isBefore(LocalDate.now())) {
        JOptionPane.showMessageDialog(
          parent,
          "Hạn sử dụng phải từ hôm nay trở đi!"
        );
        return null;
      }
      return date.atStartOfDay();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        parent,
        "Hạn sử dụng không đúng định dạng dd/MM/yyyy!"
      );
      return null;
    }
  }

  public static class Input {

    public final String maThuoc;
    public final int soLuong;
    public final BigDecimal donGia;
    public final LocalDateTime hanSuDung;

    public Input(
      String maThuoc,
      int soLuong,
      BigDecimal donGia,
      LocalDateTime hanSuDung
    ) {
      this.maThuoc = maThuoc;
      this.soLuong = soLuong;
      this.donGia = donGia;
      this.hanSuDung = hanSuDung;
    }

    public BigDecimal tinhThanhTien() {
      return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
  }
}
