package phongkham.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import phongkham.DTO.HoaDonKhamDTO;
import phongkham.BUS.HoaDonKhamBUS;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HoaDonKhamPanel extends JPanel {

    private JTextField txtTimKiem, txtTuNgay, txtDenNgay;
    private JButton btFind, btReload, btExport;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JLabel lbInfo;

    private HoaDonKhamBUS hdBUS = new HoaDonKhamBUS();
    private ArrayList<HoaDonKhamDTO> fullList = new ArrayList<>();

    private int currentPage = 1;
    private final int rowsPerPage = 10;

    public HoaDonKhamPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createMasterTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        fullList = hdBUS.getAll();
        loadDataToTable();
    }

    private JPanel createMasterTopPanel() {
        JPanel master = new JPanel();
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));
        master.setOpaque(false);

        master.add(createTitlePanel());
        master.add(Box.createVerticalStrut(15));
        master.add(createSearchPanel());
        master.add(Box.createVerticalStrut(10));
        master.add(createPaginationPanel());

        return master;
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        JLabel title = new JLabel("QUẢN LÝ HÓA ĐƠN KHÁM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(title);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        txtTimKiem = new JTextField(12);
        txtTuNgay = new JTextField(8);
        txtDenNgay = new JTextField(8);

        btFind = createStyledButton("Tìm", new Color(37, 99, 235), Color.WHITE);
        btReload = createStyledButton("Làm mới", new Color(107, 114, 128), Color.WHITE);
        btExport = createStyledButton("Xuất PDF", new Color(220, 38, 38), Color.WHITE);

        panel.add(new JLabel("Tìm mã:"));
        panel.add(txtTimKiem);
        panel.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        panel.add(txtTuNgay);
        panel.add(new JLabel("Đến ngày:"));
        panel.add(txtDenNgay);
        panel.add(btFind);
        panel.add(btReload);
        panel.add(btExport);

        btFind.addActionListener(this::btFindAction);
        btReload.addActionListener(this::btReloadAction);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] cols = {"Mã HĐ", "Mã phiếu", "Mã gói", "Ngày TT", "Tổng tiền", "Hình thức"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        dataTable = new JTable(tableModel);
        dataTable.setRowHeight(35);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < cols.length; i++)
            dataTable.getColumnModel().getColumn(i).setCellRenderer(center);

        return new JScrollPane(dataTable);
    }

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        lbInfo = new JLabel();

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        right.setOpaque(false);
        right.add(btnPrev);
        right.add(btnNext);

        panel.add(lbInfo, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadDataToTable();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage * rowsPerPage < fullList.size()) {
                currentPage++;
                loadDataToTable();
            }
        });

        return panel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int total = fullList.size();
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, total);

        for (int i = start; i < end; i++) {
            HoaDonKhamDTO hd = fullList.get(i);
            tableModel.addRow(new Object[]{
                    hd.getMaHDKham(),
                    hd.getMaPhieuKham(),
                    hd.getMaGoi(),
                    hd.getNgayThanhToan() == null ? "" : hd.getNgayThanhToan().format(f),
                    String.format("%,.0f VNĐ", hd.getTongTien()),
                    hd.getHinhThucThanhToan()
            });
        }
        lbInfo.setText("Trang " + currentPage + " / " + Math.max(1, (int) Math.ceil((double) total / rowsPerPage)));
    }

    private void btFindAction(ActionEvent e) {
        try {
            if (!txtTuNgay.getText().isEmpty() && !txtDenNgay.getText().isEmpty()) {
                LocalDate from = LocalDate.parse(txtTuNgay.getText());
                LocalDate to = LocalDate.parse(txtDenNgay.getText());
                fullList = hdBUS.filterByDate(from, to);
            } else {
                String key = txtTimKiem.getText().trim();
                if (key.isEmpty()) fullList = hdBUS.getAll();
                else {
                    fullList.clear();
                    HoaDonKhamDTO hd = hdBUS.search(key);
                    if (hd != null) fullList.add(hd);
                }
            }
            currentPage = 1;
            loadDataToTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Sai định dạng ngày! yyyy-MM-dd");
        }
    }

    private void btReloadAction(ActionEvent e) {
        txtTimKiem.setText("");
        txtTuNgay.setText("");
        txtDenNgay.setText("");
        fullList = hdBUS.getAll();
        currentPage = 1;
        loadDataToTable();
    }
}
