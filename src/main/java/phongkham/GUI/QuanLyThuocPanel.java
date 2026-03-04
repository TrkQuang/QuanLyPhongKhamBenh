package phongkham.GUI;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.ThuocDTO;

public class QuanLyThuocPanel extends JPanel{
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
        JButton btnThem = new JButton("Thêm");
        right.add(btnThem);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        btnLoc.addActionListener(e -> searchData());
        btnThem.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Chức năng thêm đang phát triển")
        );

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
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
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
                String.format("Mã thuốc: %s  |  Tên thuốc: %s  |  Đơn vị tính: %s",
                        t.getMaThuoc(),
                        t.getTenThuoc(),
                        t.getDonViTinh())
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

        JButton btnChiTiet = createButton("Chi tiết", new Color(59, 130, 246));
        btnChiTiet.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Xem chi tiết thuốc: " + t.getTenThuoc())
        );
        panel.add(btnChiTiet);

        JButton btnSua = createButton("Sửa", new Color(255, 165, 0));
        btnSua.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Chức năng sửa đang phát triển")
        );
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
        return JOptionPane.showConfirmDialog(
                this,
                message,
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
}
