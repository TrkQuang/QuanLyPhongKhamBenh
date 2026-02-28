package phongkham.GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import phongkham.DTO.BacSiDTO;

public class BacSiProfilePanel extends JPanel {

    private final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private final Color DANGER_COLOR = new Color(220, 38, 38);
    private final Color SUCCESS_COLOR = new Color(22, 163, 74);
    private final Color BG_COLOR = new Color(245, 247, 250);

    private JTable tableBacSi;
    private DefaultTableModel tableModel;
    private JLabel lblMaBacSi;
    private JTextField txtHoTen, txtSDT, txtEmail, txtChuyenKhoa, txtMaKhoa;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private ArrayList<BacSiDTO> listBacSi;

    public BacSiProfilePanel() {
        initData(); // Khoi tao du lieu truoc
        initComponents();
        loadTableData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(400, 0));

        String[] columns = { "Mã BS", "Họ Tên", "Chuyên Khoa" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBacSi = new JTable(tableModel);
        tableBacSi.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableBacSi.getSelectedRow();
                if (row >= 0) {
                    fillForm(row);
                    updateButtonState(true);
                }
            }
        });

        leftPanel.add(new JScrollPane(tableBacSi), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel cardPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);

        lblMaBacSi = new JLabel("Auto", SwingConstants.CENTER);
        lblMaBacSi.setBounds(550, 30, 100, 45);
        cardPanel.add(lblMaBacSi);

        int startX = 180, startY = 100; // Da xoa gapY thua

        addLabel(cardPanel, "Họ và tên", startX, startY - 25);
        txtHoTen = createTextField();
        txtHoTen.setBounds(startX, startY, 470, 40);
        cardPanel.add(txtHoTen);

        addLabel(cardPanel, "Số điện thoại", startX, startY + 80 - 25);
        txtSDT = createTextField();
        txtSDT.setBounds(startX, startY + 80, 220, 40);
        cardPanel.add(txtSDT);

        addLabel(cardPanel, "Email", startX + 250, startY + 80 - 25);
        txtEmail = createTextField();
        txtEmail.setBounds(startX + 250, startY + 80, 220, 40);
        cardPanel.add(txtEmail);

        addLabel(cardPanel, "Chuyên khoa", startX, startY + 160 - 25);
        txtChuyenKhoa = createTextField();
        txtChuyenKhoa.setBounds(startX, startY + 160, 220, 40);
        cardPanel.add(txtChuyenKhoa);

        addLabel(cardPanel, "Mã khoa", startX + 250, startY + 160 - 25);
        txtMaKhoa = createTextField();
        txtMaKhoa.setBounds(startX + 250, startY + 160, 220, 40);
        cardPanel.add(txtMaKhoa);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 480, 700, 80);

        btnThem = createButton("Thêm Mới", SUCCESS_COLOR);
        btnSua = createButton("Cập Nhật", PRIMARY_COLOR);
        btnXoa = createButton("Xóa", DANGER_COLOR);
        btnLamMoi = createButton("Làm Mới", Color.GRAY);

        btnThem.addActionListener(e -> handleAdd());
        btnSua.addActionListener(e -> handleUpdate());
        btnXoa.addActionListener(e -> handleDelete());
        btnLamMoi.addActionListener(e -> resetForm());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        cardPanel.add(buttonPanel);
        rightPanel.add(cardPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
        resetForm();
    }

    private void fillForm(int row) {
        BacSiDTO bs = listBacSi.get(row);
        lblMaBacSi.setText(bs.getMaBacSi());
        txtHoTen.setText(bs.getHoTen());
        txtSDT.setText(bs.getSoDienThoai());
        txtEmail.setText(bs.getEmail());
        txtChuyenKhoa.setText(bs.getChuyenKhoa());
        txtMaKhoa.setText(bs.getMaKhoa());
    }

    private void handleAdd() {
        if (!validateInput())
            return;
        String newID = "BS00" + (listBacSi.size() + 1);
        listBacSi.add(new BacSiDTO(newID, txtHoTen.getText(), txtChuyenKhoa.getText(), txtSDT.getText(),
                txtEmail.getText(), txtMaKhoa.getText()));
        loadTableData();
        resetForm();
    }

    private void handleUpdate() {
        int row = tableBacSi.getSelectedRow();
        if (row == -1 || !validateInput())
            return;
        BacSiDTO bs = listBacSi.get(row);
        listBacSi.set(row, new BacSiDTO(bs.getMaBacSi(), txtHoTen.getText(), txtChuyenKhoa.getText(), txtSDT.getText(),
                txtEmail.getText(), txtMaKhoa.getText()));
        loadTableData();
    }

    private void handleDelete() {
        int row = tableBacSi.getSelectedRow();
        if (row == -1)
            return;
        if (JOptionPane.showConfirmDialog(this, "Xóa bác sĩ này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            listBacSi.remove(row);
            loadTableData();
            resetForm();
        }
    }

    private void resetForm() {
        lblMaBacSi.setText("Auto");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtChuyenKhoa.setText("");
        txtMaKhoa.setText("");
        tableBacSi.clearSelection();
        updateButtonState(false);
    }

    private void updateButtonState(boolean isEditing) {
        btnThem.setEnabled(!isEditing);
        btnSua.setEnabled(isEditing);
        btnXoa.setEnabled(isEditing);
    }

    private boolean validateInput() {
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được trống!");
            return false;
        }
        return true;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        for (BacSiDTO bs : listBacSi)
            tableModel.addRow(new Object[] { bs.getMaBacSi(), bs.getHoTen(), bs.getChuyenKhoa() });
    }

    private void initData() {
        listBacSi = new ArrayList<>();
        listBacSi.add(new BacSiDTO("BS001", "Trần Quang Hữu", "Nội Khoa", "0987654321", "huu@pk.com", "MK01"));
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setPreferredSize(new Dimension(110, 35));
        return btn;
    }

    private void addLabel(JPanel p, String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setBounds(x, y, 200, 20);
        p.add(l);
    }
}