package phongkham.gui.goidichvu;

import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import phongkham.BUS.KhoaBUS;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.KhoaDTO;

public final class GoiDichVuDialogHelper {

  private GoiDichVuDialogHelper() {}

  public static GoiDichVuDTO showCreateDialog(
    JComponent parent,
    KhoaBUS khoaBUS
  ) {
    JTextField txtMaGoi = new JTextField();
    JTextField txtTenGoi = new JTextField();
    JTextField txtGia = new JTextField();
    JTextField txtThoiGian = new JTextField();
    JTextField txtMoTa = new JTextField();
    JComboBox<String> cboKhoa = taoComboKhoa(khoaBUS, null);

    JPanel panelNhap = taoFormPanel(
      txtMaGoi,
      txtTenGoi,
      txtGia,
      txtThoiGian,
      txtMoTa,
      cboKhoa,
      null
    );

    int luaChon = JOptionPane.showConfirmDialog(
      parent,
      panelNhap,
      "Thêm gói dịch vụ",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      GoiDichVuDTO goiMoi = new GoiDichVuDTO();
      goiMoi.setMaGoi(txtMaGoi.getText().trim());
      goiMoi.setTenGoi(txtTenGoi.getText().trim());
      goiMoi.setGiaDichVu(new BigDecimal(txtGia.getText().trim()));
      goiMoi.setThoiGianKham(txtThoiGian.getText().trim());
      goiMoi.setMoTa(txtMoTa.getText().trim());
      goiMoi.setMaKhoa(extractMaKhoa(cboKhoa));
      return goiMoi;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(parent, "❌ Dữ liệu không hợp lệ");
      return null;
    }
  }

  public static GoiDichVuDTO showEditDialog(
    JComponent parent,
    KhoaBUS khoaBUS,
    GoiDichVuDTO hienTai
  ) {
    JTextField txtTenGoi = new JTextField(hienTai.getTenGoi());
    JTextField txtGia = new JTextField(String.valueOf(hienTai.getGiaDichVu()));
    JTextField txtThoiGian = new JTextField(hienTai.getThoiGianKham());
    JTextField txtMoTa = new JTextField(hienTai.getMoTa());
    JComboBox<String> cboKhoa = taoComboKhoa(khoaBUS, hienTai.getMaKhoa());

    JPanel panelNhap = taoFormPanel(
      null,
      txtTenGoi,
      txtGia,
      txtThoiGian,
      txtMoTa,
      cboKhoa,
      hienTai.getMaGoi()
    );

    int luaChon = JOptionPane.showConfirmDialog(
      parent,
      panelNhap,
      "Sửa gói dịch vụ",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return null;
    }

    try {
      GoiDichVuDTO capNhat = new GoiDichVuDTO();
      capNhat.setMaGoi(hienTai.getMaGoi());
      capNhat.setTenGoi(txtTenGoi.getText().trim());
      capNhat.setGiaDichVu(new BigDecimal(txtGia.getText().trim()));
      capNhat.setThoiGianKham(txtThoiGian.getText().trim());
      capNhat.setMoTa(txtMoTa.getText().trim());
      capNhat.setMaKhoa(extractMaKhoa(cboKhoa));
      return capNhat;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(parent, "❌ Dữ liệu không hợp lệ");
      return null;
    }
  }

  private static JPanel taoFormPanel(
    JTextField txtMaGoi,
    JTextField txtTenGoi,
    JTextField txtGia,
    JTextField txtThoiGian,
    JTextField txtMoTa,
    JComboBox<String> cboKhoa,
    String maGoiLabel
  ) {
    JPanel panelNhap = new JPanel(new GridLayout(0, 2, 10, 10));

    panelNhap.add(new JLabel("Mã gói:"));
    if (maGoiLabel == null) {
      panelNhap.add(txtMaGoi);
    } else {
      panelNhap.add(new JLabel(maGoiLabel));
    }

    panelNhap.add(new JLabel("Tên gói:"));
    panelNhap.add(txtTenGoi);

    panelNhap.add(new JLabel("Giá dịch vụ:"));
    panelNhap.add(txtGia);

    panelNhap.add(new JLabel("Thời gian khám (phút):"));
    panelNhap.add(txtThoiGian);

    panelNhap.add(new JLabel("Khoa phụ trách:"));
    panelNhap.add(cboKhoa);

    panelNhap.add(new JLabel("Mô tả:"));
    panelNhap.add(txtMoTa);

    return panelNhap;
  }

  private static JComboBox<String> taoComboKhoa(
    KhoaBUS khoaBUS,
    String maKhoaMacDinh
  ) {
    JComboBox<String> cboKhoa = new JComboBox<>();
    ArrayList<KhoaDTO> danhSachKhoa = khoaBUS.getAll();
    for (KhoaDTO khoa : danhSachKhoa) {
      String giaTri = khoa.getMaKhoa() + " - " + khoa.getTenKhoa();
      cboKhoa.addItem(giaTri);
      if (
        maKhoaMacDinh != null &&
        maKhoaMacDinh.equalsIgnoreCase(khoa.getMaKhoa())
      ) {
        cboKhoa.setSelectedItem(giaTri);
      }
    }
    return cboKhoa;
  }

  private static String extractMaKhoa(JComboBox<String> cboKhoa) {
    String selected = (String) cboKhoa.getSelectedItem();
    if (selected == null || selected.trim().isEmpty()) {
      return "";
    }
    String[] tokens = selected.split(" - ");
    return tokens.length > 0 ? tokens[0].trim() : "";
  }
}
