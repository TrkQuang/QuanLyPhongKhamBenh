package phongkham.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import java.util.Date;

import phongkham.DTO.HoaDonKhamDTO;
import phongkham.BUS.HoaDonKhamBUS;
import phongkham.dao.HoaDonKhamDAO;

public class HoaDonKhamPanel extends JPanel {

  private JTextField txtTimKiem, txtTuNgay, txtDenNgay;
  private JButton btFind, btReload, btExport;
  private JTable dataTable;
  private DefaultTableModel tableModel;
  private JLabel lbInfo;
  private JDateChooser dateFrom;
  private JDateChooser dateTo;

  private ArrayList<HoaDonKhamDTO> fullList = new ArrayList<>();
  private HoaDonKhamBUS hdBUS = new HoaDonKhamBUS();

  private int currentPage = 1;
  private final int rowsPerPage = 10;

  public HoaDonKhamPanel() {
    setLayout(new BorderLayout(0, 10)); // Khoảng cách giữa Top và Center
    setBackground(new Color(245, 247, 250));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // KHỐI TOP: Chứa tiêu đề, tìm kiếm và phân trang
    add(createMasterTopPanel(), BorderLayout.NORTH);

    // KHỐI CENTER: Chứa bảng dữ liệu
    add(createTablePanel(), BorderLayout.CENTER);

    fullList = hdBUS.getAll();
    loadDataToTable();
  }

  // --- PANEL TỔNG HỢP PHÍA BẮC ---
  private JPanel createMasterTopPanel() {
    JPanel master = new JPanel();
    master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));
    master.setOpaque(false);

    // Dòng 1: Tiêu đề
    master.add(createTitlePanel());
    master.add(Box.createVerticalStrut(15));

    // Dòng 2: Thanh tìm kiếm (Màu trắng, bo góc nhẹ nếu cần)
    master.add(createSearchPanel());
    master.add(Box.createVerticalStrut(10));

    // Dòng 3: Thông tin phân trang
    master.add(createPaginationPanel());

    return master;
  }

  private JPanel createTitlePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.setOpaque(false);
    JLabel title = new JLabel("QUẢN LÝ HÓA ĐƠN");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    panel.add(title);
    return panel;
  }

  private JPanel createSearchPanel() {

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

    txtTimKiem = new JTextField(12);

    // Dùng JDateChooser thay vì JTextField
    dateFrom = new JDateChooser();
    dateFrom.setDateFormatString("dd/MM/yyyy");
    dateFrom.setPreferredSize(new Dimension(120, 25));

    dateTo = new JDateChooser();
    dateTo.setDateFormatString("dd/MM/yyyy");
    dateTo.setPreferredSize(new Dimension(120, 25));

    btFind = createStyledButton(
            "Tìm kiếm",
            new Color(37, 99, 235),
            Color.WHITE
    );

    btReload = createStyledButton(
            "Làm mới",
            new Color(107, 114, 128),
            Color.WHITE
    );

    btExport = createStyledButton(
            "Xuất PDF",
            new Color(220, 38, 38),
            Color.WHITE
    );

    panel.add(new JLabel("Tìm mã:"));
    panel.add(txtTimKiem);

    panel.add(new JLabel("Từ ngày:"));
    panel.add(dateFrom);

    panel.add(new JLabel("Đến ngày:"));
    panel.add(dateTo);

    panel.add(btFind);
    panel.add(btReload);
    panel.add(btExport);

    btFind.addActionListener(this::btFindAction);
    btReload.addActionListener(this::btReloadAction);

    btExport.addActionListener(e -> {
        phongkham.Utils.PdfExport.exportTable(dataTable, "Hóa đơn khám");
    });

    panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 70));
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

    return panel;
}

  private JScrollPane createTablePanel() {
    String[] cols = {
      "Mã hóa đơn",
      "Mã hồ sơ",
      "Mã gói",
      "Ngày thanh toán",
      "Tổng tiền",
      "Hình thức",
    };
    tableModel = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    dataTable = new JTable(tableModel);
    dataTable.setRowHeight(40);
    dataTable.setGridColor(new Color(240, 240, 240));
    dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    dataTable.getTableHeader().setBackground(new Color(248, 249, 250));

    DefaultTableCellRenderer center = new DefaultTableCellRenderer();
    center.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < cols.length; i++) {
      dataTable.getColumnModel().getColumn(i).setCellRenderer(center);
    }

    JScrollPane scroll = new JScrollPane(dataTable);
    scroll.getViewport().setBackground(Color.WHITE);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

    return scroll;
  }

  private JPanel createPaginationPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    lbInfo = new JLabel();
    lbInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));

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

  // Hàm tiện ích để tạo nút bấm đẹp
  private JButton createStyledButton(String text, Color bg, Color fg) {
    JButton btn = new JButton(text);
    btn.setBackground(bg);
    btn.setForeground(fg);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
      tableModel.addRow(
        new Object[] {
          hd.getMaHDKham(),
          hd.getMaHoSo(),
          hd.getMaGoi(),
          hd.getNgayThanhToan() == null ? "" : hd.getNgayThanhToan().format(f),
          String.format("%,.0f VNĐ", hd.getTongTien()),
          hd.getHinhThucThanhToan(),
        }
      );
    }
    lbInfo.setText(
      "Trang " +
        currentPage +
        " / " +
        Math.max(1, (int) Math.ceil((double) total / rowsPerPage))
    );
  }

  private void btFindAction(ActionEvent e) {

    String key = txtTimKiem.getText().trim();

    Date dFrom = dateFrom.getDate();
    Date dTo = dateTo.getDate();

    LocalDate fromDate = null;
    LocalDate toDate = null;

    if (dFrom != null) {
        fromDate = dFrom.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    if (dTo != null) {
        toDate = dTo.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // Gọi BUS (không gọi DAO trực tiếp nữa)
    fullList = hdBUS.searchAndFilter(key, fromDate, toDate);

    if (fullList.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!");
    }

    currentPage = 1;
    loadDataToTable();
}
  private void btReloadAction(ActionEvent e) {

    txtTimKiem.setText("");
    dateFrom.setDate(null);
    dateTo.setDate(null);

    fullList = hdBUS.getAll();

    currentPage = 1;
    loadDataToTable();
}
}
