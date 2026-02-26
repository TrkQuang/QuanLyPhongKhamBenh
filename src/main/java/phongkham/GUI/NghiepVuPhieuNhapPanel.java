package phongkham.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import phongkham.dao.CTPhieuNhapDAO;
import phongkham.dao.PhieuNhapDAO;
import phongkham.dao.ThuocDAO;
import phongkham.DTO.CTPhieuNhapDTO;

public class NghiepVuPhieuNhapPanel extends JPanel {

    private String maPN;
    private PhieuNhapPanel parentPanel;

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongTien;

    public NghiepVuPhieuNhapPanel(String maPN, PhieuNhapPanel parentPanel) {
        this.maPN = maPN;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // ===== PANEL TRÊN =====
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("CHI TIẾT PHIẾU NHẬP: " + maPN);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnBack = new JButton("← Quay lại");
        btnBack.addActionListener(e -> parentPanel.showMainView());

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(btnBack, BorderLayout.EAST);

        return panel;
    }

    // ===== PANEL GIỮA =====
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(createTablePanel(), BorderLayout.CENTER);
        return panel;
    }

    // ===== TABLE =====
    private JScrollPane createTablePanel() {
        String[] columns = {"Mã thuốc", "Số lượng", "Đơn giá", "Thành tiền"};
        model = new DefaultTableModel(columns, 0);

        table = new JTable(model);
        table.setRowHeight(28);

        model.addTableModelListener(e -> tinhTongTien());

        return new JScrollPane(table);
    }

    // ===== PANEL DƯỚI =====
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        lblTongTien = new JLabel("Tổng tiền: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.addActionListener(e -> xacNhanNhapKho());

        btnPanel.add(btnXacNhan);

        panel.add(lblTongTien, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    // ===== TÍNH TỔNG =====
    private void tinhTongTien() {
        double tong = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int sl = Integer.parseInt(model.getValueAt(i, 1).toString());
                double gia = Double.parseDouble(model.getValueAt(i, 2).toString());
                double thanhTien = sl * gia;

                model.setValueAt(thanhTien, i, 3);
                tong += thanhTien;
            } catch (Exception ignored) {}
        }

        lblTongTien.setText("Tổng tiền: " + String.format("%,.0f", tong) + " đ");
    }

    // ===== XÁC NHẬN NHẬP KHO =====
    private void xacNhanNhapKho() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận nhập kho phiếu " + maPN + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            CTPhieuNhapDAO ctDAO = new CTPhieuNhapDAO();
            ThuocDAO tDAO = new ThuocDAO();
            PhieuNhapDAO pnDAO = new PhieuNhapDAO();

            List<CTPhieuNhapDTO> list = ctDAO.getByMaPhieuNhap(maPN);

            for (CTPhieuNhapDTO ct : list) {
                tDAO.updateSoLuong(ct.getMaThuoc(), ct.getSoLuongNhap());
            }

            pnDAO.capNhatTrangThai(maPN, "Đã nhập");

            JOptionPane.showMessageDialog(this, "Nhập kho thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xác nhận: " + e.getMessage());
        }
    }
}
