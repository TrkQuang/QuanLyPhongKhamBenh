package phongkham.GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LichLamViecPanel extends JPanel {

    private final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private final Color BG_COLOR = new Color(245, 247, 250);

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cbThu, cbCa;
    private String tenBacSiDangNhap = "Trần Quang Hữu";

    public LichLamViecPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("ĐĂNG KÝ CA LÀM VIỆC - BS: " + tenBacSiDangNhap);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        cbThu = new JComboBox<>(new String[] { "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu" });
        cbCa = new JComboBox<>(new String[] { "Ca 1", "Ca 2" });
        JButton btnDangKy = new JButton("Gửi Đăng Ký");
        btnDangKy.addActionListener(e -> dangKyCa());

        inputPanel.add(cbThu);
        inputPanel.add(cbCa);
        inputPanel.add(btnDangKy);

        model = new DefaultTableModel(new String[] { "Thứ", "Ca", "Trạng thái" }, 0);
        table = new JTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.add(inputPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void dangKyCa() {
        model.addRow(new Object[] { cbThu.getSelectedItem(), cbCa.getSelectedItem(), "Chờ duyệt" });
    }
}