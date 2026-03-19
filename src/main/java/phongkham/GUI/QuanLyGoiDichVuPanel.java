package phongkham.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.KhoaBUS;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.gui.goidichvu.GoiDichVuDialogHelper;

public class QuanLyGoiDichVuPanel extends JPanel {

  private JTextField txtTimKiem;
  private JPanel listContainer;
  private GoiDichVuBUS goiDichVuBUS;
  private KhoaBUS khoaBUS;

  public QuanLyGoiDichVuPanel() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    goiDichVuBUS = new GoiDichVuBUS();
    khoaBUS = new KhoaBUS();

    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);

    loadData();
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    txtTimKiem = new JTextField(20);
    JButton btnLoc = new JButton("Lọc");
    left.add(new JLabel("Tìm gói dịch vụ:"));
    left.add(txtTimKiem);
    left.add(btnLoc);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnThem = new JButton("Thêm gói dịch vụ");
    right.add(btnLamMoi);
    right.add(btnThem);

    panel.add(left, BorderLayout.WEST);
    panel.add(right, BorderLayout.EAST);

    btnLoc.addActionListener(e -> timKiem());
    btnLamMoi.addActionListener(e -> lamMoi());
    btnThem.addActionListener(e -> moDialogThem());

    return panel;
  }

  private JScrollPane createListArea() {
    listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

    JScrollPane scrollPane = new JScrollPane(listContainer);
    scrollPane.setBorder(
      BorderFactory.createTitledBorder("Danh sách gói dịch vụ")
    );
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    return scrollPane;
  }

  private void loadData() {
    showList(goiDichVuBUS.getAll());
  }

  private void lamMoi() {
    txtTimKiem.setText("");
    loadData();
  }

  private void timKiem() {
    String tuKhoa = txtTimKiem.getText().trim();
    if (tuKhoa.isEmpty()) {
      loadData();
      return;
    }
    showList(goiDichVuBUS.searchByTen(tuKhoa));
  }

  private void showList(ArrayList<GoiDichVuDTO> danhSach) {
    listContainer.removeAll();

    if (danhSach == null || danhSach.isEmpty()) {
      JLabel lbl = new JLabel("Không có dữ liệu");
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      listContainer.add(lbl);
    } else {
      for (GoiDichVuDTO goiDichVu : danhSach) {
        listContainer.add(createItem(goiDichVu));
      }
    }

    listContainer.revalidate();
    listContainer.repaint();
  }

  private JPanel createItem(GoiDichVuDTO goiDichVu) {
    JPanel item = new JPanel(new BorderLayout(10, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
      )
    );
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

    item.add(createInfoPanel(goiDichVu), BorderLayout.CENTER);
    item.add(createButtonPanel(goiDichVu), BorderLayout.EAST);

    return item;
  }

  private JPanel createInfoPanel(GoiDichVuDTO goiDichVu) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    JLabel lblDong1 = new JLabel(
      String.format(
        "Mã: %s  |  Tên: %s  |  Khoa: %s",
        goiDichVu.getMaGoi(),
        goiDichVu.getTenGoi(),
        goiDichVu.getMaKhoa()
      )
    );
    lblDong1.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblDong1.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel dong2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    dong2.setOpaque(false);
    dong2.setAlignmentX(Component.LEFT_ALIGNMENT);

    dong2.add(
      new JLabel("Giá: " + String.format("%,.0f VNĐ", goiDichVu.getGiaDichVu()))
    );
    dong2.add(new JLabel("|"));
    dong2.add(
      new JLabel("Thời gian khám: " + goiDichVu.getThoiGianKham() + " phút")
    );
    dong2.add(new JLabel("|"));
    dong2.add(new JLabel("Mô tả: " + goiDichVu.getMoTa()));

    panel.add(lblDong1);
    panel.add(Box.createVerticalStrut(5));
    panel.add(dong2);

    return panel;
  }

  private JPanel createButtonPanel(GoiDichVuDTO goiDichVu) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    panel.setOpaque(false);

    JButton btnSua = createButton("Sửa", new Color(255, 165, 0));
    btnSua.addActionListener(e -> moDialogSua(goiDichVu));
    panel.add(btnSua);

    JButton btnXoa = createButton("Xóa", new Color(220, 38, 38));
    btnXoa.addActionListener(e -> xoaGoiDichVu(goiDichVu));
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

  private void moDialogThem() {
    GoiDichVuDTO goiMoi = GoiDichVuDialogHelper.showCreateDialog(this, khoaBUS);
    if (goiMoi == null) {
      return;
    }

    if (goiDichVuBUS.insert(goiMoi)) {
      JOptionPane.showMessageDialog(this, "✅ Thêm gói dịch vụ thành công");
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "❌ Không thể thêm gói dịch vụ");
    }
  }

  private void moDialogSua(GoiDichVuDTO goiDichVu) {
    GoiDichVuDTO capNhat = GoiDichVuDialogHelper.showEditDialog(
      this,
      khoaBUS,
      goiDichVu
    );
    if (capNhat == null) {
      return;
    }

    if (goiDichVuBUS.update(capNhat)) {
      JOptionPane.showMessageDialog(this, "✅ Cập nhật gói dịch vụ thành công");
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "❌ Không thể cập nhật gói dịch vụ");
    }
  }

  private void xoaGoiDichVu(GoiDichVuDTO goiDichVu) {
    int luaChon = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận xóa gói dịch vụ này?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (luaChon != JOptionPane.YES_OPTION) {
      return;
    }

    if (goiDichVuBUS.delete(goiDichVu.getMaGoi())) {
      JOptionPane.showMessageDialog(this, "✅ Xóa gói dịch vụ thành công");
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "❌ Không thể xóa gói dịch vụ");
    }
  }
}
