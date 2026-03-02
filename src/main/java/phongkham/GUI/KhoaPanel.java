package phongkham.GUI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.KhoaDTO;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.KhoaBUS;
public class KhoaPanel extends JPanel {

    private final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private final Color DANGER_COLOR = new Color(220, 38, 38);
    private final Color BG_COLOR = new Color(245, 247, 250);

    private ArrayList<KhoaDTO> listKhoa;
    private ArrayList<BacSiDTO> listBacSi;
    private JPanel listContainer;
    private JTextField txtSearch;
    private KhoaBUS Khoabus = new KhoaBUS();
    private BacSiBUS BSBus = new BacSiBUS();

    public KhoaPanel() {
        initData(); // Luôn khởi tạo dữ liệu trước khi vẽ giao diện
        initComponents();
        renderList(listKhoa);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout(20, 10));
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ KHOA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolBar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 40));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchKhoa(txtSearch.getText());
            }
        });

        JButton btnThem = createButton("＋ Thêm Khoa", PRIMARY_COLOR);
        btnThem.addActionListener(e -> themKhoa());

        toolBar.add(txtSearch);
        toolBar.add(btnThem);
        topPanel.add(toolBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- LIST CONTAINER ---
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_COLOR);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void renderList(ArrayList<KhoaDTO> data) {
        listContainer.removeAll();
        for (KhoaDTO khoa : data) {
            // Thống kê số bác sĩ
            long count = listBacSi.stream()
                    .filter(bs -> bs.getMaKhoa() != null && bs.getMaKhoa().equals(khoa.getMaKhoa()))
                    .count();

            listContainer.add(createCard(khoa, (int) count));
            listContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createCard(KhoaDTO khoa, int sl) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setPreferredSize(new Dimension(0, 90));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        // Thông tin
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel lblTen = new JLabel(khoa.getTenKhoa());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        info.add(lblTen);
        info.add(new JLabel("Mã: " + khoa.getMaKhoa()));

        // Nút bấm
        JPanel action = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        action.setOpaque(false);

        JLabel lblCount = new JLabel(sl + " Bác sĩ");
        lblCount.setForeground(PRIMARY_COLOR);

        JButton btnView = new JButton("👁");
        styleMiniButton(btnView, PRIMARY_COLOR);
        btnView.addActionListener(e -> xemDanhSachBacSi(khoa));

        JButton btnDelete = new JButton("🗑");
        styleMiniButton(btnDelete, DANGER_COLOR);
        btnDelete.addActionListener(e -> xoaKhoa(khoa));

        action.add(lblCount);
        action.add(btnView);
        action.add(btnDelete);

        card.add(info, BorderLayout.WEST);
        card.add(action, BorderLayout.EAST);
        return card;
    }

    private void xemDanhSachBacSi(KhoaDTO khoa) {
        List<BacSiDTO> ds = listBacSi.stream()
                .filter(bs -> bs.getMaKhoa() != null && bs.getMaKhoa().equals(khoa.getMaKhoa()))
                .collect(Collectors.toList());

        String content = ds.isEmpty() ? "Chưa có bác sĩ"
                : ds.stream().map(b -> "- " + b.getHoTen()).collect(Collectors.joining("\n"));

        JOptionPane.showMessageDialog(this, content, "Bác sĩ khoa " + khoa.getTenKhoa(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void themKhoa() {
        String ten = JOptionPane.showInputDialog(this, "Nhập tên khoa mới:");
        if (ten != null && !ten.trim().isEmpty()) {
            listKhoa.add(new KhoaDTO("K" + (listKhoa.size() + 1), ten));
            renderList(listKhoa);
        }
    }

    private void xoaKhoa(KhoaDTO khoa) {
        if (JOptionPane.showConfirmDialog(this, "Xóa khoa " + khoa.getTenKhoa() + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            listKhoa.remove(khoa);
            renderList(listKhoa);
        }
    }

    private void searchKhoa(String key) {
        ArrayList<KhoaDTO> filtered = listKhoa.stream()
                .filter(k -> k.getTenKhoa().toLowerCase().contains(key.toLowerCase()) ||
                        k.getMaKhoa().toLowerCase().contains(key.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
        renderList(filtered);
    }

    private void initData() {
        loadDataKhoa();
        loadDataBacSi();
    }
    private void loadDataKhoa() {
        ArrayList<KhoaDTO> list = new ArrayList<>();
        list = Khoabus.getAll();
        listKhoa = list;
    }
    private void loadDataBacSi() {
        ArrayList<BacSiDTO> list = new ArrayList<>();
        list = BSBus.getAll();
        listBacSi = list;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    private void styleMiniButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(40, 35));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(color));
    }
}