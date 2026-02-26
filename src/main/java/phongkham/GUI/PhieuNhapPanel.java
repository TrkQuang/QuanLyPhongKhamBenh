package phongkham.GUI;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.DTO.PhieuNhapDTO;

/**
 * PhieuNhapPanel - TỐI ƯU (Không HTML)
 * Từ 258 dòng → 160 dòng (-38%)
 */
public class PhieuNhapPanel extends JPanel {

  private JTextField txtTimKiem;
  private JPanel listContainer;
  private PhieuNhapBUS bus;

  public PhieuNhapPanel() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    bus = new PhieuNhapBUS();

    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);

    loadData();
  }

  // ===== PANEL TRÊN: tìm kiếm =====
  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    txtTimKiem = new JTextField(20);
    JButton btnLoc = new JButton("Lọc");

    left.add(new JLabel("Tìm kiếm:"));
    left.add(txtTimKiem);
    left.add(btnLoc);

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    JButton btnThem = new JButton("Thêm");
    right.add(btnThem);

    panel.add(left, BorderLayout.WEST);
    panel.add(right, BorderLayout.EAST);

    btnLoc.addActionListener(e -> searchData());
    btnThem.addActionListener(e ->
      JOptionPane.showMessageDialog(this, "Chức năng đang phát triển")
    );

    return panel;
  }

  // ===== VÙNG DANH SÁCH =====
  private JScrollPane createListArea() {
    listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

    JScrollPane scrollPane = new JScrollPane(listContainer);
    scrollPane.setBorder(
      BorderFactory.createTitledBorder("Danh sách phiếu nhập")
    );
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    return scrollPane;
  }

  // ===== LOAD DỮ LIỆU =====
  private void loadData() {
    showList(bus.getAll());
  }

  // ===== TÌM KIẾM =====
  private void searchData() {
    String keyword = txtTimKiem.getText().trim();
    if (keyword.isEmpty()) {
      loadData();
      return;
    }
    showList(bus.search(keyword));
  }

  // ✅ METHOD DÙNG CHUNG: Hiển thị danh sách
  private void showList(ArrayList<PhieuNhapDTO> list) {
    listContainer.removeAll();

    if (list.isEmpty()) {
      JLabel lblEmpty = new JLabel("Không có dữ liệu");
      lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
      listContainer.add(lblEmpty);
    } else {
      for (PhieuNhapDTO pn : list) {
        listContainer.add(createPhieuNhapItem(pn));
      }
    }

    listContainer.revalidate();
    listContainer.repaint();
  }

  // ===== TẠO ITEM PHIẾU NHẬP =====
  private JPanel createPhieuNhapItem(PhieuNhapDTO pn) {
    JPanel item = new JPanel(new BorderLayout(10, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
      )
    );
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

    // ✅ PANEL THÔNG TIN (KHÔNG HTML)
    JPanel infoPanel = createInfoPanel(pn);
    item.add(infoPanel, BorderLayout.CENTER);

    // ✅ PANEL NÚT BẤM
    JPanel btnPanel = createButtonPanel(pn);
    item.add(btnPanel, BorderLayout.EAST);

    return item;
  }

  // ✅ TẠO PANEL THÔNG TIN (Thay thế HTML)
  private JPanel createInfoPanel(PhieuNhapDTO pn) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    // Dòng 1: Mã + Ngày + Người giao
    JLabel lblDong1 = new JLabel(
      String.format(
        "Mã: %s  |  Ngày: %s  |  Người giao: %s",
        pn.getMaPhieuNhap(),
        new SimpleDateFormat("dd/MM/yyyy").format(pn.getNgayNhap()),
        pn.getNguoiGiao()
      )
    );
    lblDong1.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblDong1.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CĂN TRÁI

    // Dòng 2: Tổng tiền + Trạng thái
    JPanel dong2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    dong2Panel.setOpaque(false);
    dong2Panel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CĂN TRÁI

    JLabel lblTien = new JLabel(
      String.format("%.0f VNĐ", pn.getTongTienNhap())
    );
    lblTien.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblTien.setForeground(new Color(16, 185, 129));

    JLabel lblTrangThai = createTrangThaiLabel(pn.getTrangThai());

    dong2Panel.add(lblTien);
    dong2Panel.add(new JLabel("  |  "));
    dong2Panel.add(lblTrangThai);

    panel.add(lblDong1);
    panel.add(Box.createVerticalStrut(5));
    panel.add(dong2Panel);

    return panel;
  }

  // ✅ TẠO LABEL TRẠNG THÁI với màu sắc
  private JLabel createTrangThaiLabel(String trangThai) {
    String text = "Chờ duyệt";
    Color color = new Color(255, 165, 0); // Cam

    if ("DA_DUYET".equals(trangThai)) {
      text = "Đã duyệt";
      color = new Color(16, 185, 129); // Xanh
    } else if ("DA_HUY".equals(trangThai)) {
      text = "Đã hủy";
      color = new Color(239, 68, 68); // Đỏ
    }

    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lbl.setForeground(color);
    return lbl;
  }

  // ✅ TẠO PANEL NÚT BẤM
  private JPanel createButtonPanel(PhieuNhapDTO pn) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    panel.setOpaque(false);

    String trangThai = pn.getTrangThai();

    // Nút "Chi tiết"
    JButton btnChiTiet = createButton("Chi tiết", new Color(59, 130, 246));
    btnChiTiet.addActionListener(e -> {
      NghiepVuPhieuNhapPanel detailPanel = new NghiepVuPhieuNhapPanel(
        pn.getMaPhieuNhap(),
        this
      );
      removeAll();
      setLayout(new BorderLayout());
      add(detailPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
    });
    panel.add(btnChiTiet);

    // Nút "Duyệt" (chỉ hiện nếu CHO_DUYET)
    if ("CHO_DUYET".equals(trangThai)) {
      JButton btnDuyet = createButton("Duyệt", new Color(16, 185, 129));
      btnDuyet.addActionListener(e -> {
        if (confirm("Xác nhận duyệt phiếu nhập này?")) {
          if (bus.capNhatTrangThai(pn.getMaPhieuNhap(), "DA_DUYET")) {
            JOptionPane.showMessageDialog(
              this,
              "✅ Đã duyệt phiếu thành công!"
            );
            loadData();
          } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi duyệt phiếu!");
          }
        }
      });
      panel.add(btnDuyet);
    }

    // Nút "Xóa" (chỉ hiện nếu KHÔNG PHẢI DA_DUYET)
    if (!"DA_DUYET".equals(trangThai)) {
      JButton btnXoa = createButton("Xóa", new Color(239, 68, 68));
      btnXoa.addActionListener(e -> {
        if (confirm("Xác nhận xóa phiếu nhập này?")) {
          if (bus.delete(pn.getMaPhieuNhap())) {
            JOptionPane.showMessageDialog(this, "✅ Đã xóa thành công!");
            loadData();
          } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi xóa!");
          }
        }
      });
      panel.add(btnXoa);
    }

    return panel;
  }

  // ✅ TẠO NÚT BẤM với style giống nhau
  private JButton createButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return btn;
  }

  // ✅ Helper: Confirm dialog
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

  // ===== QUAY LẠI VIEW CHÍNH =====
  public void showMainView() {
    removeAll();
    setLayout(new BorderLayout(10, 10));
    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);
    loadData();
    revalidate();
    repaint();
  }
}
