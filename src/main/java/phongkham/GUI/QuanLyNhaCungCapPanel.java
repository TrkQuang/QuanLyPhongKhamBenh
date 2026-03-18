package phongkham.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import phongkham.BUS.NhaCungCapBUS;
import phongkham.DTO.NhaCungCapDTO;

public class QuanLyNhaCungCapPanel extends JPanel {

  private JTextField txtTimKiem;
  private JPanel listContainer;
  private NhaCungCapBUS nhaCungCapBUS;

  public QuanLyNhaCungCapPanel() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    nhaCungCapBUS = new NhaCungCapBUS();

    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);

    loadData();
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    txtTimKiem = new JTextField(20);
    JButton btnLoc = new JButton("Lọc");
    left.add(new JLabel("Tìm nhà cung cấp:"));
    left.add(txtTimKiem);
    left.add(btnLoc);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnThem = new JButton("Thêm nhà cung cấp");
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
      BorderFactory.createTitledBorder("Danh sách nhà cung cấp")
    );
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    return scrollPane;
  }

  private void loadData() {
    showList(nhaCungCapBUS.list());
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
    showList(nhaCungCapBUS.timTheoTen(tuKhoa));
  }

  private void showList(ArrayList<NhaCungCapDTO> danhSach) {
    listContainer.removeAll();

    if (danhSach == null || danhSach.isEmpty()) {
      JLabel lbl = new JLabel("Không có dữ liệu");
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      listContainer.add(lbl);
    } else {
      for (NhaCungCapDTO nhaCungCap : danhSach) {
        listContainer.add(createItem(nhaCungCap));
      }
    }

    listContainer.revalidate();
    listContainer.repaint();
  }

  private JPanel createItem(NhaCungCapDTO nhaCungCap) {
    JPanel item = new JPanel(new BorderLayout(10, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
      )
    );
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

    item.add(createInfoPanel(nhaCungCap), BorderLayout.CENTER);
    item.add(createButtonPanel(nhaCungCap), BorderLayout.EAST);

    return item;
  }

  private JPanel createInfoPanel(NhaCungCapDTO nhaCungCap) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    JLabel lblDong1 = new JLabel(
      String.format(
        "Mã: %s  |  Tên: %s",
        nhaCungCap.getMaNhaCungCap(),
        nhaCungCap.getTenNhaCungCap()
      )
    );
    lblDong1.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblDong1.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel dong2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    dong2.setOpaque(false);
    dong2.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel lblTrangThai = new JLabel(
      nhaCungCap.isActive() ? "Đang hợp tác" : "Ngừng hợp tác"
    );
    lblTrangThai.setForeground(
      nhaCungCap.isActive() ? new Color(16, 185, 129) : new Color(220, 38, 38)
    );
    lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 12));

    dong2.add(new JLabel("Địa chỉ: " + nhaCungCap.getDiaChi()));
    dong2.add(new JLabel("|"));
    dong2.add(new JLabel("SĐT: " + nhaCungCap.getSDT()));
    dong2.add(new JLabel("|"));
    dong2.add(lblTrangThai);

    panel.add(lblDong1);
    panel.add(Box.createVerticalStrut(5));
    panel.add(dong2);

    return panel;
  }

  private JPanel createButtonPanel(NhaCungCapDTO nhaCungCap) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    panel.setOpaque(false);

    JButton btnSua = createButton("Sửa", new Color(255, 165, 0));
    btnSua.addActionListener(e -> moDialogSua(nhaCungCap));
    panel.add(btnSua);

    JButton btnTrangThai = createButton(
      nhaCungCap.isActive() ? "Ngừng hợp tác" : "Hợp tác lại",
      nhaCungCap.isActive() ? new Color(220, 38, 38) : new Color(16, 185, 129)
    );
    btnTrangThai.addActionListener(e -> doiTrangThaiHopTac(nhaCungCap));
    panel.add(btnTrangThai);

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
    JTextField txtMaNCC = new JTextField();
    JTextField txtTenNCC = new JTextField();
    JTextField txtDiaChi = new JTextField();
    JTextField txtSoDienThoai = new JTextField();

    JPanel panelNhap = new JPanel(new GridLayout(0, 2, 10, 10));
    panelNhap.add(new JLabel("Mã nhà cung cấp:"));
    panelNhap.add(txtMaNCC);
    panelNhap.add(new JLabel("Tên nhà cung cấp:"));
    panelNhap.add(txtTenNCC);
    panelNhap.add(new JLabel("Địa chỉ:"));
    panelNhap.add(txtDiaChi);
    panelNhap.add(new JLabel("Số điện thoại:"));
    panelNhap.add(txtSoDienThoai);

    int luaChon = JOptionPane.showConfirmDialog(
      this,
      panelNhap,
      "Thêm nhà cung cấp",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return;
    }

    NhaCungCapDTO nhaCungCapMoi = new NhaCungCapDTO();
    nhaCungCapMoi.setMaNhaCungCap(txtMaNCC.getText().trim());
    nhaCungCapMoi.setTenNhaCungCap(txtTenNCC.getText().trim());
    nhaCungCapMoi.setDiaChi(txtDiaChi.getText().trim());
    nhaCungCapMoi.setSDT(txtSoDienThoai.getText().trim());

    if (nhaCungCapBUS.addNCC(nhaCungCapMoi)) {
      JOptionPane.showMessageDialog(this, "✅ Thêm nhà cung cấp thành công");
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "❌ Không thể thêm nhà cung cấp");
    }
  }

  private void moDialogSua(NhaCungCapDTO nhaCungCap) {
    JTextField txtTenNCC = new JTextField(nhaCungCap.getTenNhaCungCap());
    JTextField txtDiaChi = new JTextField(nhaCungCap.getDiaChi());
    JTextField txtSoDienThoai = new JTextField(nhaCungCap.getSDT());

    JPanel panelNhap = new JPanel(new GridLayout(0, 2, 10, 10));
    panelNhap.add(new JLabel("Mã nhà cung cấp:"));
    panelNhap.add(new JLabel(nhaCungCap.getMaNhaCungCap()));
    panelNhap.add(new JLabel("Tên nhà cung cấp:"));
    panelNhap.add(txtTenNCC);
    panelNhap.add(new JLabel("Địa chỉ:"));
    panelNhap.add(txtDiaChi);
    panelNhap.add(new JLabel("Số điện thoại:"));
    panelNhap.add(txtSoDienThoai);

    int luaChon = JOptionPane.showConfirmDialog(
      this,
      panelNhap,
      "Sửa nhà cung cấp",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (luaChon != JOptionPane.OK_OPTION) {
      return;
    }

    NhaCungCapDTO capNhat = new NhaCungCapDTO(nhaCungCap);
    capNhat.setTenNhaCungCap(txtTenNCC.getText().trim());
    capNhat.setDiaChi(txtDiaChi.getText().trim());
    capNhat.setSDT(txtSoDienThoai.getText().trim());

    if (nhaCungCapBUS.updateNCC(capNhat)) {
      JOptionPane.showMessageDialog(
        this,
        "✅ Cập nhật nhà cung cấp thành công"
      );
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "❌ Không thể cập nhật nhà cung cấp");
    }
  }

  private void doiTrangThaiHopTac(NhaCungCapDTO nhaCungCap) {
    boolean ketQua = nhaCungCap.isActive()
      ? nhaCungCapBUS.ngungHopTac(nhaCungCap.getMaNhaCungCap())
      : nhaCungCapBUS.hopTacLai(nhaCungCap.getMaNhaCungCap());

    if (ketQua) {
      JOptionPane.showMessageDialog(
        this,
        "✅ Cập nhật trạng thái hợp tác thành công"
      );
      loadData();
    } else {
      JOptionPane.showMessageDialog(
        this,
        "❌ Không thể cập nhật trạng thái hợp tác"
      );
    }
  }
}
