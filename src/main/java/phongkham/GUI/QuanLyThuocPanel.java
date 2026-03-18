package phongkham.gui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.ExcelExport;

public class QuanLyThuocPanel extends JPanel {

  private JTextField txtTimKiem;
  private JPanel listContainer;
  private ThuocBUS bus;

  public QuanLyThuocPanel() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    bus = new ThuocBUS();

    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);

    loadData();
  }

  // ===== PANEL TRÊN =====
  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    txtTimKiem = new JTextField(20);
    JButton btnLoc = new JButton("Lọc");

    left.add(new JLabel("Tìm kiếm:"));
    left.add(txtTimKiem);
    left.add(btnLoc);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnXuatExcel = new JButton("Xuất Excel");
    JButton btnThem = new JButton("Thêm");
    right.add(btnLamMoi);
    right.add(btnXuatExcel);
    right.add(btnThem);

    panel.add(left, BorderLayout.WEST);
    panel.add(right, BorderLayout.EAST);

    btnLoc.addActionListener(e -> searchData());
    btnLamMoi.addActionListener(e -> refreshData());
    btnXuatExcel.addActionListener(e -> xuatDanhSachThuocExcel());
    btnThem.addActionListener(e -> moDialogThemThuoc());

    return panel;
  }

  // ===== DANH SÁCH =====
  private JScrollPane createListArea() {
    listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

    JScrollPane scrollPane = new JScrollPane(listContainer);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách thuốc"));
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    return scrollPane;
  }

  private void loadData() {
    showList(bus.list());
  }

  private void refreshData() {
    txtTimKiem.setText("");
    loadData();
  }

  private void searchData() {
    String keyword = txtTimKiem.getText().trim();
    if (keyword.isEmpty()) {
      loadData();
      return;
    }
    showList(bus.timTheoTen(keyword));
  }

  // ===== HIỂN THỊ DANH SÁCH =====
  private void showList(ArrayList<ThuocDTO> list) {
    listContainer.removeAll();

    if (list.isEmpty()) {
      JLabel lbl = new JLabel("Không có dữ liệu");
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      listContainer.add(lbl);
    } else {
      for (ThuocDTO t : list) {
        listContainer.add(createThuocItem(t));
      }
    }

    listContainer.revalidate();
    listContainer.repaint();
  }

  // ===== ITEM THUỐC =====
  private JPanel createThuocItem(ThuocDTO t) {
    JPanel item = new JPanel(new BorderLayout(10, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
      )
    );
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

    item.add(createInfoPanel(t), BorderLayout.CENTER);
    item.add(createButtonPanel(t), BorderLayout.EAST);

    return item;
  }

  // ===== THÔNG TIN =====
  private JPanel createInfoPanel(ThuocDTO t) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    JLabel lblDong1 = new JLabel(
      String.format(
        "Mã thuốc: %s  |  Tên thuốc: %s  |  Đơn vị tính: %s",
        t.getMaThuoc(),
        t.getTenThuoc(),
        t.getDonViTinh()
      )
    );
    lblDong1.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblDong1.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel dong2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    dong2.setOpaque(false);
    dong2.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel lblGia = new JLabel(
      String.format("Đơn giá bán: %.0f VNĐ", t.getDonGiaBan())
    );
    lblGia.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblGia.setForeground(new Color(16, 185, 129));

    JLabel lblSoLuong = new JLabel("Số lượng tồn: " + t.getSoLuongTon());

    dong2.add(lblGia);
    dong2.add(new JLabel("|"));
    dong2.add(lblSoLuong);

    panel.add(lblDong1);
    panel.add(Box.createVerticalStrut(5));
    panel.add(dong2);

    return panel;
  }

  // ===== NÚT =====
  private JPanel createButtonPanel(ThuocDTO t) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    panel.setOpaque(false);

    JButton btnSua = createButton("Sửa", new Color(255, 165, 0));
    btnSua.addActionListener(e -> moDialogSuaThuoc(t));
    panel.add(btnSua);

    JButton btnXoa = createButton("Xóa", new Color(239, 68, 68));
    btnXoa.addActionListener(e -> {
      if (confirm("Xác nhận xóa thuốc này?")) {
        if (bus.deleteByMa(t.getMaThuoc())) {
          JOptionPane.showMessageDialog(this, "✅ Đã xóa thành công!");
          loadData();
        } else {
          JOptionPane.showMessageDialog(this, "❌ Không thể xóa!");
        }
      }
    });
    panel.add(btnXoa);

    return panel;
  }

  private JButton createButton(String text, Color color) {
    JButton btn = new JButton(text);
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return btn;
  }

  private boolean confirm(String message) {
    return (
      JOptionPane.showConfirmDialog(
        this,
        message,
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
      ) ==
      JOptionPane.YES_OPTION
    );
  }

  private void xuatDanhSachThuocExcel() {
    ArrayList<ThuocDTO> danhSachThuoc = bus.list();
    ExcelExport.exportThuocToCsv(danhSachThuoc);
  }

  private void moDialogThemThuoc() {
    JTextField txtTenThuoc = new JTextField();
    JTextField txtHoatChat = new JTextField();
    JTextField txtDonViTinh = new JTextField();
    JTextField txtDonGiaBan = new JTextField();
    JTextField txtSoLuongTon = new JTextField();

    JPanel panelNhap = new JPanel(new GridLayout(0, 2, 10, 10));
    panelNhap.add(new JLabel("Tên thuốc:"));
    panelNhap.add(txtTenThuoc);
    panelNhap.add(new JLabel("Hoạt chất:"));
    panelNhap.add(txtHoatChat);
    panelNhap.add(new JLabel("Đơn vị tính:"));
    panelNhap.add(txtDonViTinh);
    panelNhap.add(new JLabel("Đơn giá bán:"));
    panelNhap.add(txtDonGiaBan);
    panelNhap.add(new JLabel("Số lượng tồn:"));
    panelNhap.add(txtSoLuongTon);

    int luaChon = JOptionPane.showConfirmDialog(
      this,
      panelNhap,
      "Thêm thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return;
    }

    try {
      ThuocDTO thuocMoi = new ThuocDTO();
      thuocMoi.setTenThuoc(txtTenThuoc.getText().trim());
      thuocMoi.setHoatChat(txtHoatChat.getText().trim());
      thuocMoi.setDonViTinh(txtDonViTinh.getText().trim());
      thuocMoi.setDonGiaBan(Float.parseFloat(txtDonGiaBan.getText().trim()));
      thuocMoi.setSoLuongTon(Integer.parseInt(txtSoLuongTon.getText().trim()));

      if (bus.addThuoc(thuocMoi)) {
        JOptionPane.showMessageDialog(this, "✅ Thêm thuốc thành công");
        loadData();
      } else {
        JOptionPane.showMessageDialog(this, "❌ Không thể thêm thuốc");
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "❌ Dữ liệu không hợp lệ");
    }
  }

  private void moDialogSuaThuoc(ThuocDTO thuocCanSua) {
    JTextField txtTenThuoc = new JTextField(thuocCanSua.getTenThuoc());
    JTextField txtHoatChat = new JTextField(thuocCanSua.getHoatChat());
    JTextField txtDonViTinh = new JTextField(thuocCanSua.getDonViTinh());
    JTextField txtDonGiaBan = new JTextField(
      String.valueOf(thuocCanSua.getDonGiaBan())
    );
    JTextField txtSoLuongTon = new JTextField(
      String.valueOf(thuocCanSua.getSoLuongTon())
    );

    JPanel panelNhap = new JPanel(new GridLayout(0, 2, 10, 10));
    panelNhap.add(new JLabel("Mã thuốc:"));
    panelNhap.add(new JLabel(thuocCanSua.getMaThuoc()));
    panelNhap.add(new JLabel("Tên thuốc:"));
    panelNhap.add(txtTenThuoc);
    panelNhap.add(new JLabel("Hoạt chất:"));
    panelNhap.add(txtHoatChat);
    panelNhap.add(new JLabel("Đơn vị tính:"));
    panelNhap.add(txtDonViTinh);
    panelNhap.add(new JLabel("Đơn giá bán:"));
    panelNhap.add(txtDonGiaBan);
    panelNhap.add(new JLabel("Số lượng tồn:"));
    panelNhap.add(txtSoLuongTon);

    int luaChon = JOptionPane.showConfirmDialog(
      this,
      panelNhap,
      "Sửa thuốc",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return;
    }

    try {
      ThuocDTO thuocSua = new ThuocDTO();
      thuocSua.setMaThuoc(thuocCanSua.getMaThuoc());
      thuocSua.setTenThuoc(txtTenThuoc.getText().trim());
      thuocSua.setHoatChat(txtHoatChat.getText().trim());
      thuocSua.setDonViTinh(txtDonViTinh.getText().trim());
      thuocSua.setDonGiaBan(Float.parseFloat(txtDonGiaBan.getText().trim()));
      thuocSua.setSoLuongTon(Integer.parseInt(txtSoLuongTon.getText().trim()));

      if (bus.UpdateThuoc(thuocSua)) {
        JOptionPane.showMessageDialog(this, "✅ Cập nhật thuốc thành công");
        loadData();
      } else {
        JOptionPane.showMessageDialog(this, "❌ Không thể cập nhật thuốc");
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "❌ Dữ liệu không hợp lệ");
    }
  }
}
