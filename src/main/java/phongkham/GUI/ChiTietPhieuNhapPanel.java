package phongkham.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import phongkham.dao.CTPhieuNhapDAO;
import phongkham.dao.PhieuNhapDAO;
import phongkham.dao.ThuocDAO;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.DTO.CTPhieuNhapDTO;

public class ChiTietPhieuNhapPanel extends JPanel {

    private String maPN;
    private PhieuNhapPanel parentPanel;

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongTien;

    public ChiTietPhieuNhapPanel(String maPN, PhieuNhapPanel parentPanel) {
        this.maPN = maPN;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        loadData();
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
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ===== TẮT AUTO RESIZE (để cột không giãn full) =====
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // ===== SET ĐỘ RỘNG CỘT =====
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        // ===== CĂN PHẢI SỐ =====
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // ===== HEADER STYLE =====
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        // ===== CĂN GIỮA HEADER =====
        DefaultTableCellRenderer centerHeader =
                (DefaultTableCellRenderer) header.getDefaultRenderer();
        centerHeader.setHorizontalAlignment(SwingConstants.CENTER);

        model.addTableModelListener(e -> tinhTongTien());

        return new JScrollPane(table);
    }

    // ===== PANEL DƯỚI =====
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        lblTongTien = new JLabel("Tổng tiền: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.addActionListener(e -> xacNhanNhapKho());

        rightPanel.add(lblTongTien);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(btnXacNhan);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // ===== TÍNH TỔNG =====
    private void tinhTongTien() {
        BigDecimal tong = BigDecimal.ZERO;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int sl = Integer.parseInt(model.getValueAt(i, 1).toString());
                BigDecimal gia = new BigDecimal(model.getValueAt(i, 2).toString());
                BigDecimal thanhTien = gia.multiply(BigDecimal.valueOf(sl));
                tong = tong.add(thanhTien);
            } catch (Exception ignored) {}
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblTongTien.setText("Tổng tiền: " + formatter.format(tong) + " đ");
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
            CTPhieuNhapBUS bus = new CTPhieuNhapBUS();
            ThuocDAO tDAO = new ThuocDAO();
            PhieuNhapDAO pnDAO = new PhieuNhapDAO();
            List<CTPhieuNhapDTO> list = bus.getByMaPhieuNhap(maPN);

            for (CTPhieuNhapDTO ct : list) {
                tDAO.updateSoLuong(ct.getMaThuoc(), ct.getSoLuongNhap());
            }

            pnDAO.capNhatTrangThai(maPN, "Đã nhập");

            JOptionPane.showMessageDialog(this, "Nhập kho thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xác nhận: " + e.getMessage());
        }
    }
    private void loadData() {
        try {
            CTPhieuNhapBUS bus = new CTPhieuNhapBUS();
            List<CTPhieuNhapDTO> list = bus.getByMaPhieuNhap(maPN);

            BigDecimal tong = BigDecimal.ZERO;
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            for (CTPhieuNhapDTO ct : list) {

                BigDecimal thanhTien = ct.getDonGiaNhap()
                        .multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));

                tong = tong.add(thanhTien);   // ✅ tính tổng tại đây

                model.addRow(new Object[]{
                        ct.getMaThuoc(),
                        ct.getSoLuongNhap(),
                        formatter.format(ct.getDonGiaNhap()),
                        formatter.format(thanhTien)
                });
            }

            lblTongTien.setText("Tổng tiền: " + formatter.format(tong) + " đ");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu: " + e.getMessage());
        }
    }
}
