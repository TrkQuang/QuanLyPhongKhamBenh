package phongkham.gui.datlich;

import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.time.LocalDate;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Helper gom logic validate/reset form đặt lịch khám để panel gọn hơn.
 */
public final class DatLichKhamFormHelper {

  private DatLichKhamFormHelper() {}

  public static ValidationResult validate(
    JTextField txtHoTen,
    JTextField txtSDT,
    JTextField txtCCCD,
    JDateChooser dateNgaySinh,
    JDateChooser dateNgayKham,
    JComboBox<String> goiDV,
    JComboBox<String> bacSi
  ) {
    if (txtHoTen.getText().trim().isEmpty()) {
      return ValidationResult.fail("Vui lòng nhập họ tên!", txtHoTen);
    }

    String sdt = txtSDT.getText().trim();
    if (sdt.isEmpty()) {
      return ValidationResult.fail("Vui lòng nhập số điện thoại!", txtSDT);
    }
    if (!sdt.matches("^0\\d{9}$")) {
      return ValidationResult.fail("Số điện thoại không hợp lệ!", txtSDT);
    }

    String cccd = txtCCCD.getText().trim();
    if (cccd.isEmpty()) {
      return ValidationResult.fail("Vui lòng nhập CCCD!", txtCCCD);
    }
    if (!cccd.matches("^\\d{9,12}$")) {
      return ValidationResult.fail("CCCD không hợp lệ!", txtCCCD);
    }

    if (dateNgaySinh.getDate() == null) {
      return ValidationResult.fail("Vui lòng chọn ngày sinh!", null);
    }

    Date ngayKhamDate = dateNgayKham.getDate();
    if (ngayKhamDate == null) {
      return ValidationResult.fail("Vui lòng chọn ngày khám!", null);
    }

    if (goiDV.getSelectedItem() == null) {
      return ValidationResult.fail("Vui lòng chọn gói dịch vụ!", null);
    }

    if (bacSi.getSelectedItem() == null) {
      return ValidationResult.fail("Vui lòng chọn bác sĩ!", null);
    }

    LocalDate ngayKham = new java.sql.Date(
      ngayKhamDate.getTime()
    ).toLocalDate();
    if (ngayKham.isBefore(LocalDate.now())) {
      return ValidationResult.fail("Ngày khám không được trong quá khứ!", null);
    }

    return ValidationResult.ok();
  }

  public static void resetForm(
    JTextField txtHoTen,
    JTextField txtSDT,
    JTextField txtCCCD,
    JTextField txtDiaChi,
    JDateChooser dateNgaySinh,
    JDateChooser dateNgayKham,
    JRadioButton radNam,
    JComboBox<String> goiDV,
    JComboBox<String> bacSi,
    JTextArea txtMoTaGoi
  ) {
    txtHoTen.setText("");
    txtSDT.setText("");
    txtCCCD.setText("");
    txtDiaChi.setText("");
    dateNgaySinh.setDate(null);
    dateNgayKham.setDate(new Date());
    radNam.setSelected(true);

    if (goiDV.getItemCount() > 0) {
      goiDV.setSelectedIndex(0);
    }
    if (bacSi.getItemCount() > 0) {
      bacSi.setSelectedIndex(0);
    }

    txtMoTaGoi.setText("");
  }

  public static String extractCode(String comboValue) {
    if (comboValue == null || comboValue.trim().isEmpty()) {
      return "";
    }
    String[] tokens = comboValue.split(" - ");
    return tokens.length > 0 ? tokens[0].trim() : "";
  }

  public static class ValidationResult {

    public final boolean valid;
    public final String message;
    public final Component focusTarget;

    private ValidationResult(
      boolean valid,
      String message,
      Component focusTarget
    ) {
      this.valid = valid;
      this.message = message;
      this.focusTarget = focusTarget;
    }

    public static ValidationResult ok() {
      return new ValidationResult(true, "", null);
    }

    public static ValidationResult fail(String message, Component focusTarget) {
      return new ValidationResult(false, message, focusTarget);
    }
  }
}
