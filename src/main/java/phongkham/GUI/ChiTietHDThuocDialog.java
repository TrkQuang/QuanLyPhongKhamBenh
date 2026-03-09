package phongkham.gui;
import javax.swing.*;
import java.awt.*;
import phongkham.DTO.HoaDonThuocDTO;

public class ChiTietHDThuocDialog extends JDialog {

  private HoaDonThuocDTO hoaDon;
  private HoaDonThuocPanel parentPanel;

  public ChiTietHDThuocDialog(Frame parentFrame, HoaDonThuocDTO hoaDon, HoaDonThuocPanel parentPanel) {
    super(parentFrame, "Chi tiết hóa đơn bán thuốc", true);
    this.hoaDon = hoaDon;
    this.parentPanel = parentPanel;
    initComponents();
  }

  private void initComponents() {
    setSize(1000, 700);
    setLocationRelativeTo(getParent());
    setResizable(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    ChiTietHDThuocPanel chiTietPanel = new ChiTietHDThuocPanel(hoaDon.getMaHoaDon(), parentPanel);
    add(chiTietPanel);
  }
}
