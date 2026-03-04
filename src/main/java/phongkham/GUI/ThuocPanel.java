package phongkham.GUI;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.ThuocDTO;

public class ThuocPanel extends JPanel {

  private final Color PRIMARY_COLOR = new Color(37, 99, 235);
  private final Color SUCCESS_COLOR = new Color(34, 197, 94);
  private final Color TEXT_COLOR = new Color(107, 114, 128);
  private final Color BG_COLOR = new Color(245, 247, 250);
  private final Color STOCK_HIGH = new Color(34, 197, 94);
  private final Color STOCK_MEDIUM = new Color(202, 138, 4);
  private final Color STOCK_LOW = new Color(220, 38, 38);

  private JTextField txtTimKiem;
  private JPanel listContainer;
  private ThuocBUS bus;

  public ThuocPanel() {
    initData();
    initComponents();
  }

  private void initData() {
    bus = new ThuocBUS();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBackground(BG_COLOR);
    setBorder(new EmptyBorder(10, 10, 10, 10));

    add(createTopPanel(), BorderLayout.NORTH);
    add(createListArea(), BorderLayout.CENTER);

    loadData();
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new BorderLayout(20, 10));
    panel.setOpaque(false);

    JLabel title = new JLabel("DANH SÁCH THUỐC BÁN");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    toolbar.setOpaque(false);

    txtTimKiem = new JTextField();
    txtTimKiem.setPreferredSize(new Dimension(200, 40));
    txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.LIGHT_GRAY),
      BorderFactory.createEmptyBorder(5, 10, 5, 10)));

    JButton btnSearch = createButton("Tìm kiếm", PRIMARY_COLOR);
    JButton btnReload = createButton("Làm mới", TEXT_COLOR);
    JButton btnAdd = createButton("＋ Thêm", SUCCESS_COLOR);

    toolbar.add(txtTimKiem);
    toolbar.add(btnSearch);
    toolbar.add(btnReload);
    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(btnAdd);

    panel.add(toolbar, BorderLayout.CENTER);

    btnSearch.addActionListener(e -> searchData());
    btnReload.addActionListener(e -> reloadData());
    btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng đang phát triển"));

    return panel;
  }

  private JScrollPane createListArea() {
    listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
    listContainer.setBackground(BG_COLOR);

    JScrollPane scrollPane = new JScrollPane(listContainer);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.getViewport().setBackground(BG_COLOR);

    return scrollPane;
  }

  private void loadData() {
    showList(new ArrayList<>(bus.list()));
  }

  private void reloadData() {
    txtTimKiem.setText("");
    loadData();
  }

  private void searchData() {
    String keyword = txtTimKiem.getText().trim();
    ArrayList<ThuocDTO> allThuoc = new ArrayList<>(bus.list());

    if (keyword.isEmpty()) {
      showList(allThuoc);
      return;
    }

    ArrayList<ThuocDTO> filtered = new ArrayList<>();
    for (ThuocDTO thuoc : allThuoc) {
      if (thuoc.getMaThuoc().toLowerCase().contains(keyword.toLowerCase()) ||
          thuoc.getTenThuoc().toLowerCase().contains(keyword.toLowerCase())) {
        filtered.add(thuoc);
      }
    }
    showList(filtered);
  }

  private void showList(ArrayList<ThuocDTO> list) {
    listContainer.removeAll();

    if (list.isEmpty()) {
      JLabel lblEmpty = new JLabel("Không có dữ liệu");
      lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
      lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
      listContainer.add(lblEmpty);
    } else {
      for (ThuocDTO thuoc : list) {
        listContainer.add(createThuocItem(thuoc));
      }
    }

    listContainer.revalidate();
    listContainer.repaint();
  }

  private JPanel createThuocItem(ThuocDTO thuoc) {
    JPanel item = new JPanel(new BorderLayout(15, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
      )
    );
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

    item.add(createInfoPanel(thuoc), BorderLayout.CENTER);
    item.add(createButtonPanel(thuoc), BorderLayout.EAST);

    return item;
  }

  private JPanel createInfoPanel(ThuocDTO thuoc) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    JLabel line1 = new JLabel(String.format("Mã: %s | Tên: %s", thuoc.getMaThuoc(), thuoc.getTenThuoc()));
    line1.setFont(new Font("Segoe UI", Font.BOLD, 13));
    line1.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel line2 = new JLabel(String.format("Hoạt chất: %s | Đơn vị: %s", thuoc.getHoatChat(), thuoc.getDonViTinh()));
    line2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    line2.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
    line3.setOpaque(false);
    line3.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel lblPrice = new JLabel(String.format("Đơn giá: %,.0f VNĐ", thuoc.getDonGiaBan()));
    lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblPrice.setForeground(new Color(220, 38, 38));

    JLabel lblStock = new JLabel(String.format("Tồn: %d", thuoc.getSoLuongTon()));
    lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    if (thuoc.getSoLuongTon() > 10) {
      lblStock.setForeground(STOCK_HIGH);
    } else if (thuoc.getSoLuongTon() > 0) {
      lblStock.setForeground(STOCK_MEDIUM);
    } else {
      lblStock.setForeground(STOCK_LOW);
    }

    line3.add(lblPrice);
    line3.add(lblStock);

    panel.add(line1);
    panel.add(Box.createVerticalStrut(5));
    panel.add(line2);
    panel.add(Box.createVerticalStrut(5));
    panel.add(line3);

    return panel;
  }

  private JPanel createButtonPanel(ThuocDTO thuoc) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    panel.setOpaque(false);

    JButton btnEdit = createButton("Sửa", PRIMARY_COLOR);
    JButton btnDelete = createButton("Xóa", new Color(220, 38, 38));

    btnEdit.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sửa: " + thuoc.getMaThuoc()));
    btnDelete.addActionListener(e -> JOptionPane.showMessageDialog(this, "Xóa: " + thuoc.getMaThuoc()));

    panel.add(btnEdit);
    panel.add(btnDelete);

    return panel;
  }

  private JButton createButton(String text, Color bg) {
    JButton btn = new JButton(text);
    btn.setBackground(bg);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    return btn;
  }
}
