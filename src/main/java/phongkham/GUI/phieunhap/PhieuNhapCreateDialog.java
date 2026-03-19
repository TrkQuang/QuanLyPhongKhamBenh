package phongkham.gui.phieunhap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.NhaCungCapBUS;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.NhaCungCapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.DTO.ThuocDTO;

public final class PhieuNhapCreateDialog {

  private PhieuNhapCreateDialog() {}

  public static boolean show(
    JPanel parent,
    PhieuNhapBUS phieuNhapBUS,
    CTPHieuNhapBUSAdapter ctAdapter
  ) {
    ArrayList<NhaCungCapDTO> danhSachNCC = new NhaCungCapBUS().listDangHopTac();
    ArrayList<ThuocDTO> danhSachThuoc = new ThuocBUS().list();

    if (danhSachNCC == null || danhSachNCC.isEmpty()) {
      JOptionPane.showMessageDialog(
        parent,
        "❌ Chưa có nhà cung cấp đang hợp tác để tạo phiếu nhập"
      );
      return false;
    }

    if (danhSachThuoc == null || danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(
        parent,
        "❌ Chưa có thuốc trong hệ thống để thêm vào phiếu nhập"
      );
      return false;
    }

    JComboBox<String> cboNhaCungCap = new JComboBox<>();
    for (NhaCungCapDTO nhaCungCap : danhSachNCC) {
      cboNhaCungCap.addItem(
        nhaCungCap.getMaNhaCungCap() + " - " + nhaCungCap.getTenNhaCungCap()
      );
    }

    JTextField txtNguoiGiao = new JTextField();
    JPanel formPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Nhà cung cấp:"));
    formPanel.add(cboNhaCungCap);
    formPanel.add(new JLabel("Người giao:"));
    formPanel.add(txtNguoiGiao);

    String[] cols = {
      "Mã thuốc",
      "Tên thuốc",
      "Số lượng",
      "Đơn giá nhập",
      "Hạn sử dụng",
      "Thành tiền",
    };
    DefaultTableModel model = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable table = new JTable(model);
    table.setRowHeight(26);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(760, 220));

    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    List<RowItem> chiTietTam = new ArrayList<>();

    JLabel lblTongTien = new JLabel("Tổng tiền nhập: 0 đ");
    lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 13));

    Runnable refreshBangChiTiet = () -> {
      model.setRowCount(0);
      BigDecimal tong = BigDecimal.ZERO;
      for (RowItem ct : chiTietTam) {
        BigDecimal thanhTien = ct.input.tinhThanhTien();
        tong = tong.add(thanhTien);
        model.addRow(
          new Object[] {
            ct.input.maThuoc,
            ct.tenThuoc,
            ct.input.soLuong,
            formatter.format(ct.input.donGia),
            ct.input.hanSuDung == null
              ? ""
              : ct.input.hanSuDung.format(dateFormatter),
            formatter.format(thanhTien),
          }
        );
      }
      lblTongTien.setText("Tổng tiền nhập: " + formatter.format(tong) + " đ");
    };

    javax.swing.JButton btnThemThuoc = new javax.swing.JButton("Thêm thuốc");
    javax.swing.JButton btnSuaThuoc = new javax.swing.JButton("Sửa dòng");
    javax.swing.JButton btnXoaThuoc = new javax.swing.JButton("Xóa dòng");

    btnSuaThuoc.setEnabled(false);
    btnXoaThuoc.setEnabled(false);

    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        boolean coChon = table.getSelectedRow() >= 0;
        btnSuaThuoc.setEnabled(coChon);
        btnXoaThuoc.setEnabled(coChon);
      });

    btnThemThuoc.addActionListener(e -> {
      RowItem input = showRowDialog(parent, danhSachThuoc, null);
      if (input == null) {
        return;
      }
      chiTietTam.add(input);
      refreshBangChiTiet.run();
    });

    btnSuaThuoc.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0 || row >= chiTietTam.size()) {
        return;
      }
      RowItem cu = chiTietTam.get(row);
      RowItem moi = showRowDialog(parent, danhSachThuoc, cu);
      if (moi == null) {
        return;
      }
      chiTietTam.set(row, moi);
      refreshBangChiTiet.run();
      if (row < model.getRowCount()) {
        table.setRowSelectionInterval(row, row);
      }
    });

    btnXoaThuoc.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0 || row >= chiTietTam.size()) {
        return;
      }
      chiTietTam.remove(row);
      refreshBangChiTiet.run();
      btnSuaThuoc.setEnabled(false);
      btnXoaThuoc.setEnabled(false);
    });

    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actionsPanel.add(btnThemThuoc);
    actionsPanel.add(btnSuaThuoc);
    actionsPanel.add(btnXoaThuoc);

    JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
    tablePanel.setBorder(
      BorderFactory.createTitledBorder("Chi tiết thuốc nhập")
    );
    tablePanel.add(actionsPanel, BorderLayout.NORTH);
    tablePanel.add(scrollPane, BorderLayout.CENTER);
    tablePanel.add(lblTongTien, BorderLayout.SOUTH);

    JPanel panelNhap = new JPanel(new BorderLayout(0, 10));
    panelNhap.add(formPanel, BorderLayout.NORTH);
    panelNhap.add(tablePanel, BorderLayout.CENTER);

    int luaChon = JOptionPane.showConfirmDialog(
      parent,
      panelNhap,
      "Thêm phiếu nhập",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return false;
    }

    return luuPhieuNhap(
      parent,
      phieuNhapBUS,
      ctAdapter,
      cboNhaCungCap,
      txtNguoiGiao,
      chiTietTam
    );
  }

  private static boolean luuPhieuNhap(
    JPanel parent,
    PhieuNhapBUS phieuNhapBUS,
    CTPHieuNhapBUSAdapter ctAdapter,
    JComboBox<String> cboNhaCungCap,
    JTextField txtNguoiGiao,
    List<RowItem> chiTietTam
  ) {
    try {
      String nguoiGiao = txtNguoiGiao.getText().trim();
      if (nguoiGiao.isEmpty()) {
        JOptionPane.showMessageDialog(
          parent,
          "❌ Người giao không được để trống"
        );
        return false;
      }

      if (chiTietTam.isEmpty()) {
        JOptionPane.showMessageDialog(
          parent,
          "❌ Cần thêm ít nhất một dòng thuốc vào phiếu nhập"
        );
        return false;
      }

      String nhaCungCapDaChon = (String) cboNhaCungCap.getSelectedItem();
      String maNhaCungCap = nhaCungCapDaChon.split(" - ")[0].trim();
      String maPhieuNhap = "PN" + System.currentTimeMillis();

      BigDecimal tong = BigDecimal.ZERO;
      for (RowItem ct : chiTietTam) {
        tong = tong.add(ct.input.tinhThanhTien());
      }

      PhieuNhapDTO phieuNhapMoi = new PhieuNhapDTO();
      phieuNhapMoi.setMaPhieuNhap(maPhieuNhap);
      phieuNhapMoi.setMaNCC(maNhaCungCap);
      phieuNhapMoi.setNgayNhap(new java.sql.Date(System.currentTimeMillis()));
      phieuNhapMoi.setNguoiGiao(nguoiGiao);
      phieuNhapMoi.setTongTienNhap(tong.floatValue());
      phieuNhapMoi.setTrangThai("CHO_DUYET");

      if (!phieuNhapBUS.insert(phieuNhapMoi)) {
        JOptionPane.showMessageDialog(parent, "❌ Không thể thêm phiếu nhập");
        return false;
      }

      for (int i = 0; i < chiTietTam.size(); i++) {
        RowItem ct = chiTietTam.get(i);
        boolean inserted = ctAdapter.insert(
          taoMaCTPN(maPhieuNhap, i),
          maPhieuNhap,
          ct.input.maThuoc,
          ct.input.soLuong,
          ct.input.donGia,
          ct.input.hanSuDung
        );
        if (!inserted) {
          JOptionPane.showMessageDialog(
            parent,
            "❌ Tạo phiếu thành công nhưng thêm dòng thuốc thất bại ở dòng " +
              (i + 1) +
              ".\nVui lòng mở chi tiết phiếu để kiểm tra lại."
          );
          return false;
        }
      }

      JOptionPane.showMessageDialog(parent, "✅ Thêm phiếu nhập thành công");
      return true;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(parent, "❌ Dữ liệu không hợp lệ");
      return false;
    }
  }

  private static RowItem showRowDialog(
    JPanel parent,
    List<ThuocDTO> danhSachThuoc,
    RowItem macDinh
  ) {
    ThuocNhapDialogHelper.Input input = ThuocNhapDialogHelper.show(
      parent,
      danhSachThuoc,
      macDinh == null ? null : macDinh.input,
      macDinh == null ? "Thêm thuốc vào phiếu nhập" : "Sửa dòng thuốc"
    );
    if (input == null) {
      return null;
    }

    String tenThuoc = "";
    for (ThuocDTO thuoc : danhSachThuoc) {
      if (thuoc.getMaThuoc().equals(input.maThuoc)) {
        tenThuoc = thuoc.getTenThuoc();
        break;
      }
    }
    return new RowItem(input, tenThuoc);
  }

  private static String taoMaCTPN(String maPhieuNhap, int stt) {
    return "CT" + maPhieuNhap + "_" + (stt + 1);
  }

  private static class RowItem {

    private final ThuocNhapDialogHelper.Input input;
    private final String tenThuoc;

    private RowItem(ThuocNhapDialogHelper.Input input, String tenThuoc) {
      this.input = input;
      this.tenThuoc = tenThuoc;
    }
  }

  @FunctionalInterface
  public interface CTPHieuNhapBUSAdapter {
    boolean insert(
      String maCTPN,
      String maPhieuNhap,
      String maThuoc,
      int soLuong,
      BigDecimal donGia,
      java.time.LocalDateTime hanSuDung
    );
  }
}
