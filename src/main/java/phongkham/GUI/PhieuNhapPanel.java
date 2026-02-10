package phongkham.GUI;

import java.awt.*;
import javax.swing.*;

public class PhieuNhapPanel extends JPanel {
     private JTextField txtTimKiem;
    private JButton btnLoc, btnThem;

    private JPanel listContainer; // vùng danh sách phiếu nhập 

    public PhieuNhapPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createListArea(), BorderLayout.CENTER);

    }

    // ===== PANEL TRÊN: tìm kiếm =====
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtTimKiem = new JTextField(20);
        btnLoc = new JButton("Lọc");

        left.add(new JLabel("Tìm kiếm:"));
        left.add(txtTimKiem);
        left.add(btnLoc);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnThem = new JButton("Thêm");
        right.add(btnThem);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
            btnLoc.addActionListener(e -> loadFakeData());

        return panel;
    }

    // ===== VÙNG DANH SÁCH RỖNG =====
    private JScrollPane createListArea() {
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

        // KHÔNG thêm dữ liệu demo theo yêu cầu

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));

        return scrollPane;
    }


    // ===== HÀM CHO PHÉP THÊM 1 DÒNG PHIẾU NHẬP =====
    public void addPhieuNhapItem(JPanel itemPanel) {
        listContainer.add(itemPanel);
        listContainer.revalidate();
        listContainer.repaint();
    }
    private JPanel createPhieuNhapItem(String maPN) {
    JPanel item = new JPanel(new BorderLayout());
    item.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    item.setBackground(Color.WHITE);

    // ===== Mã phiếu =====
    JLabel lblMa = new JLabel("Mã phiếu: " + maPN);
    lblMa.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    item.add(lblMa, BorderLayout.WEST);

    // ===== Panel nút bên phải =====
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    btnPanel.setOpaque(false);

    JButton btnXacNhan = new JButton("Xác nhận");
    JButton btnXoa = new JButton("Xóa");
    JButton btnChiTiet = new JButton("Chi tiết");



    btnChiTiet.addActionListener(e ->
        JOptionPane.showMessageDialog(this, "Xem chi tiết " + maPN)
    );

    btnPanel.add(btnXacNhan);
    btnPanel.add(btnXoa);
    btnPanel.add(btnChiTiet);

    item.add(btnPanel, BorderLayout.EAST);

    return item;
}
private void loadFakeData() {
    listContainer.removeAll();

    // dữ liệu giả
    listContainer.add(createPhieuNhapItem("PN001"));
    listContainer.add(createPhieuNhapItem("PN002"));
    listContainer.add(createPhieuNhapItem("PN003"));
    listContainer.add(createPhieuNhapItem("PN004"));

    listContainer.revalidate();
    listContainer.repaint();
}

}
