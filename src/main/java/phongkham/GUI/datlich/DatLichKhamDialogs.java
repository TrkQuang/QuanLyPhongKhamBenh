package phongkham.gui.datlich;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.DTO.LichKhamDTO;

public class DatLichKhamDialogs {

  private DatLichKhamDialogs() {}

  public static String showThanhToanDialog(
    java.awt.Component parent,
    BigDecimal tongTien
  ) {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(parent),
      "Thanh toan",
      true
    );
    dialog.setLayout(new BorderLayout());
    dialog.setSize(400, 250);
    dialog.setLocationRelativeTo(parent);

    JPanel info = new JPanel(new GridLayout(2, 1, 5, 5));
    JLabel lblTitle = createLabel("XAC NHAN THANH TOAN");
    JLabel lblMoney = new JLabel(
      String.format("Tong tien: %,.0f VNĐ", tongTien),
      JLabel.CENTER
    );
    lblMoney.setFont(new Font("Segoe UI", Font.ITALIC, 16));
    info.add(lblTitle);
    info.add(lblMoney);

    JPanel pick = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    ButtonGroup gr = new ButtonGroup();
    JRadioButton cash = new JRadioButton("Tien mat");
    JRadioButton banking = new JRadioButton("Chuyen khoan");
    gr.add(cash);
    gr.add(banking);
    pick.add(cash);
    pick.add(banking);

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    JButton btnConfirm = createButton("Xac nhan");
    JButton btnExit = createButton("Huy");
    String[] kq = { null };

    btnConfirm.addActionListener(e -> {
      if (!cash.isSelected() && !banking.isSelected()) {
        JOptionPane.showMessageDialog(
          dialog,
          "Vui long chon phuong thuc thanh toan"
        );
        return;
      }
      kq[0] = cash.isSelected() ? "Tien mat" : "Chuyen khoan";
      dialog.dispose();
    });
    btnExit.addActionListener(e -> {
      kq[0] = null;
      dialog.dispose();
    });

    btnPanel.add(btnConfirm);
    btnPanel.add(btnExit);

    dialog.add(info, BorderLayout.NORTH);
    dialog.add(pick, BorderLayout.CENTER);
    dialog.add(btnPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);

    return kq[0];
  }

  public static void showHoSoDialog(
    java.awt.Component parent,
    ArrayList<HoSoBenhAnDTO> list
  ) {
    JDialog dialog = new JDialog(
      (Frame) SwingUtilities.getWindowAncestor(parent),
      "Danh sach ho so",
      true
    );
    dialog.setSize(800, 400);
    dialog.setLocationRelativeTo(parent);

    String[] col = {
      "Ma ho so",
      "Ho ten",
      "CCCD",
      "Ngay sinh",
      "Gioi tinh",
      "Ngay kham",
      "Bac si kham",
      "Chan doan",
      "Loi dan",
      "Trang thai",
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
    dialog.add(new JScrollPane(table));
    dialog.setVisible(true);
  }

  public static void printPhieuDangKy(
    HoSoBenhAnDTO hs,
    LichKhamDTO lk,
    GoiDichVuDTO goi,
    HoaDonKhamDTO hd,
    String phuongThuc
  ) {
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    String content = String.format(
      "=== PHIEU DANG KY KHAM BENH ===\n\n" +
        "--- THONG TIN BENH NHAN ---\n" +
        "Ma ho so: %s\n" +
        "Ho ten: %s\n" +
        "CCCD: %s\n" +
        "So dien thoai: %s\n" +
        "Ngay sinh: %s\n" +
        "Gioi tinh: %s\n" +
        "Dia chi: %s\n\n" +
        "--- THONG TIN LICH KHAM ---\n" +
        "Ma lich kham: %s\n" +
        "Bac si: %s\n" +
        "Goi dich vu: %s\n" +
        "Thoi gian: %s - %s\n\n" +
        "--- THONG TIN THANH TOAN ---\n" +
        "Ma hoa don: %s\n" +
        "Tong tien: %,d VNĐ\n" +
        "Phuong thuc: %s\n" +
        "Ngay thanh toan: %s\n" +
        "Trang thai: %s\n\n" +
        "=== CAM ON QUY KHACH ===\n" +
        "Vui long den dung gio hen va mang theo CCCD!",
      hs.getMaHoSo(),
      hs.getHoTen(),
      hs.getCCCD(),
      hs.getSoDienThoai(),
      df.format(hs.getNgaySinh()),
      hs.getGioiTinh(),
      hs.getDiaChi(),
      lk.getMaLichKham(),
      lk.getMaBacSi(),
      goi.getTenGoi(),
      lk.getThoiGianBatDau(),
      lk.getThoiGianKetThuc(),
      hd.getMaHDKham(),
      goi.getGiaDichVu().intValue(),
      phuongThuc,
      hd.getNgayThanhToan() != null
        ? hd
            .getNgayThanhToan()
            .format(
              java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
        : "Chua thanh toan",
      hd.getTrangThai()
    );

    String fileName = "PhieuDangKy_" + hd.getMaHDKham();
    phongkham.Utils.PdfExport.exportText(content, fileName);
  }

  private static JLabel createLabel(String txt) {
    JLabel lbl = new JLabel(txt, JLabel.CENTER);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
    return lbl;
  }

  private static JButton createButton(String txt) {
    JButton btn = new JButton(txt);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
    btn.setPreferredSize(new Dimension(140, 40));
    return btn;
  }
}
