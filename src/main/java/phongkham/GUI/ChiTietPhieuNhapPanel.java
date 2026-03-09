package phongkham.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import phongkham.DTO.CTPhieuNhapDTO;
import phongkham.DTO.PhieuNhapDTO;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.PhieuNhapBUS;

public class ChiTietPhieuNhapPanel extends JPanel {

    private String maPN;
    private PhieuNhapPanel parentPanel;

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongTien;
    private JLabel lblTrangThai;
    private PhieuNhapBUS pnBUS = new PhieuNhapBUS();
    private CTPhieuNhapBUS bus = new CTPhieuNhapBUS();

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

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("CHI TIẾT PHIẾU NHẬP: " + maPN);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        lblTrangThai = new JLabel();
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(lblTitle);
        leftPanel.add(lblTrangThai);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.addActionListener(e -> parentPanel.showMainView());

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(btnBack, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createTablePanel(), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createTablePanel() {

        String[] columns = {
                "Mã CTPN",
                "Mã thuốc",
                "Số lượng",
                "Đơn giá",
                "Hạn sử dụng",
                "Thành tiền"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(right);
        }

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);

        return new JScrollPane(table);
    }

    private JPanel createBottomPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        lblTongTien = new JLabel("Tổng tiền: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(lblTongTien);
        rightPanel.add(Box.createHorizontalStrut(20));

        
        PhieuNhapDTO pn = pnBUS.getById(maPN);

        if (pn != null) {

            String trangThai = pn.getTrangThai();

            if ("CHUA_DUYET".equals(trangThai)) {

                JButton btnThem = new JButton("Thêm");
                JButton btnSua = new JButton("Sửa");
                JButton btnXoa = new JButton("Xóa dòng");

                btnThem.addActionListener(e -> themDong());
                btnSua.addActionListener(e -> suaDong());
                btnXoa.addActionListener(e -> xoaDong());

                rightPanel.add(btnThem);
                rightPanel.add(btnSua);
                rightPanel.add(btnXoa);
            }

            if ("DA_DUYET".equals(trangThai)) {
                JButton btnNhapKho = new JButton("Nhập kho");
                btnNhapKho.addActionListener(e -> xacNhanNhapKho());
                rightPanel.add(btnNhapKho);
            }
        }

        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private void themDong() {
        String maCTPN = JOptionPane.showInputDialog(this, "Nhập mã CTPN:");
        String maThuoc = JOptionPane.showInputDialog(this, "Nhập mã thuốc:");
        String soLuongStr = JOptionPane.showInputDialog(this, "Nhập số lượng:");
        String donGiaStr = JOptionPane.showInputDialog(this, "Nhập đơn giá:");
        String hanStr = JOptionPane.showInputDialog(this, "Nhập hạn sử dụng:");

        LocalDateTime hanSuDung = null;

        if (hanStr != null && !hanStr.trim().isEmpty()) {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(hanStr, formatter);
            hanSuDung = date.atStartOfDay(); // 00:00:00
        }
        try {
            int soLuong = Integer.parseInt(soLuongStr);
            BigDecimal donGia = new BigDecimal(donGiaStr);
            boolean success = bus.insert(
                    maCTPN,
                    maPN,
                    maThuoc,
                    soLuong,
                    donGia,
                    hanSuDung
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
        }
    }

    private void suaDong() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa!");
            return;
        }
        String maCTPN = model.getValueAt(row, 0).toString();
        String soLuongStr = JOptionPane.showInputDialog(this, "Nhập số lượng mới:");
        String donGiaStr = JOptionPane.showInputDialog(this, "Nhập đơn giá mới:");

        try {
            int soLuong = Integer.parseInt(soLuongStr);
            BigDecimal donGia = new BigDecimal(donGiaStr);
            boolean success = bus.update(maCTPN, soLuong, donGia);

            if (success) {
                JOptionPane.showMessageDialog(this, "Sửa thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể sửa!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
        }
        loadData();
    }

    private void xoaDong() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa!");
            return;
        }
        String maCTPN = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận xóa?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bus.delete(maCTPN);
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã xóa!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa!");
            }
        }
        loadData();
    }

    private void xacNhanNhapKho() {
        boolean success = bus.xacNhanNhapKho(maPN);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nhập kho thành công!");
            parentPanel.showMainView();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể nhập kho!\n" +
                            "- Phiếu đã nhập\n" +
                            "- Hoặc chưa được duyệt");
        }
    }

    private void loadData() {
        List<CTPhieuNhapDTO> list = bus.getByMaPhieuNhap(maPN);
        model.setRowCount(0);
        BigDecimal tong = BigDecimal.ZERO;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (CTPhieuNhapDTO ct : list) {
            BigDecimal thanhTien =
                    ct.getDonGiaNhap().multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));

            tong = tong.add(thanhTien);

            model.addRow(new Object[]{
                    ct.getMaCTPN(),
                    ct.getMaThuoc(),
                    ct.getSoLuongNhap(),
                    formatter.format(ct.getDonGiaNhap()),
                    ct.getHanSuDung() == null ? "" : ct.getHanSuDung().format(dtf),
                    formatter.format(thanhTien)
            });
        }

        lblTongTien.setText("Tổng tiền: " + formatter.format(tong) + " đ");
        PhieuNhapDTO pn = pnBUS.getById(maPN);

        if (pn != null) {
            lblTrangThai.setText("Trạng thái: " + pn.getTrangThai());

            switch (pn.getTrangThai()) {
                case "DA_NHAP":
                    lblTrangThai.setForeground(new Color(0, 128, 0));
                    break;
                case "DA_DUYET":
                    lblTrangThai.setForeground(new Color(255, 140, 0));
                    break;
                default:
                    lblTrangThai.setForeground(Color.RED);
            }
        }
    }
}