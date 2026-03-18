package phongkham.gui.hoadonthuoc;

import java.awt.Component;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import phongkham.DTO.CTHDThuocDTO;
import phongkham.DTO.ThuocDTO;

public final class ChiTietHDThuocDialogs {

  private ChiTietHDThuocDialogs() {}

  public static ThemInput showThemDialog(
    Component parent,
    List<ThuocDTO> dsThuoc
  ) {
    if (dsThuoc == null || dsThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(parent, "Không có thuốc để thêm!");
      return null;
    }

    JComboBox<String> cbThuoc = new JComboBox<>();
    for (ThuocDTO t : dsThuoc) {
      cbThuoc.addItem(t.getMaThuoc() + " - " + t.getTenThuoc());
    }

    JSpinner spinnerSoLuong = new JSpinner(
      new SpinnerNumberModel(1, 1, 1000, 1)
    );
    JTextField txtDonGia = new JTextField(10);
    JTextField txtGhiChu = new JTextField(30);

    Object[] message = {
      "Chọn thuốc:",
      cbThuoc,
      "Số lượng:",
      spinnerSoLuong,
      "Đơn giá:",
      txtDonGia,
      "Ghi chú:",
      txtGhiChu,
    };

    int option = JOptionPane.showConfirmDialog(
      parent,
      message,
      "Thêm chi tiết thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (option != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      String maThuoc = cbThuoc.getSelectedItem().toString().split(" - ")[0];
      int soLuong = (int) spinnerSoLuong.getValue();
      double donGia = Double.parseDouble(txtDonGia.getText().trim());
      return new ThemInput(
        maThuoc,
        soLuong,
        donGia,
        txtGhiChu.getText().trim()
      );
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(parent, "Đơn giá không hợp lệ!");
      return null;
    }
  }

  public static SuaInput showSuaDialog(Component parent, CTHDThuocDTO cthd) {
    if (cthd == null) {
      return null;
    }

    JSpinner spinnerSoLuong = new JSpinner(
      new SpinnerNumberModel(cthd.getSoLuong(), 1, 1000, 1)
    );
    JTextField txtDonGia = new JTextField(String.valueOf(cthd.getDonGia()), 10);
    JTextField txtGhiChu = new JTextField(
      cthd.getGhiChu() != null ? cthd.getGhiChu() : "",
      30
    );

    Object[] message = {
      "Thuốc:",
      cthd.getTenThuoc(),
      "Số lượng:",
      spinnerSoLuong,
      "Đơn giá:",
      txtDonGia,
      "Ghi chú:",
      txtGhiChu,
    };

    int option = JOptionPane.showConfirmDialog(
      parent,
      message,
      "Sửa chi tiết thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (option != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      int soLuong = (int) spinnerSoLuong.getValue();
      double donGia = Double.parseDouble(txtDonGia.getText().trim());
      return new SuaInput(soLuong, donGia, txtGhiChu.getText().trim());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(parent, "Đơn giá không hợp lệ!");
      return null;
    }
  }

  public static class ThemInput {

    public final String maThuoc;
    public final int soLuong;
    public final double donGia;
    public final String ghiChu;

    public ThemInput(
      String maThuoc,
      int soLuong,
      double donGia,
      String ghiChu
    ) {
      this.maThuoc = maThuoc;
      this.soLuong = soLuong;
      this.donGia = donGia;
      this.ghiChu = ghiChu;
    }
  }

  public static class SuaInput {

    public final int soLuong;
    public final double donGia;
    public final String ghiChu;

    public SuaInput(int soLuong, double donGia, String ghiChu) {
      this.soLuong = soLuong;
      this.donGia = donGia;
      this.ghiChu = ghiChu;
    }
  }
}
