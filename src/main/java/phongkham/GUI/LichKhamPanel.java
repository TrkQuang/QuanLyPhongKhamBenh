package phongkham.GUI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import phongkham.BUS.LichKhamBUS;
import phongkham.DTO.LichKhamDTO;

public class LichKhamPanel extends JPanel {

  private JTextField txtTimKiem;
  private JComboBox<String> cboTrangThai;
  private JTable tableLichKham;
  private DefaultTableModel tableModel;
  private LichKhamBUS lichKhamBUS;

  public LichKhamPanel() {
    lichKhamBUS = new LichKhamBUS();
    initComponents();
    loadData();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title
    JLabel lblTitle = new JLabel("QUẢN LÝ LỊCH KHÁM");
    lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

    // Search panel
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    searchPanel.add(new JLabel("Tìm kiếm:"));
    txtTimKiem = new JTextField(20);
    searchPanel.add(txtTimKiem);

    searchPanel.add(new JLabel("Trạng thái:"));
    cboTrangThai = new JComboBox<>(
      new String[] { "Tất cả", "Đã đặt", "Đang khám", "Hoàn thành", "Đã hủy" }
    );
    searchPanel.add(cboTrangThai);

    JButton btnTimKiem = new JButton("Tìm kiếm");
    JButton btnLamMoi = new JButton("Làm mới");
    JButton btnThongKe = new JButton("Thống kê");

    btnTimKiem.addActionListener(e -> timKiem());
    btnLamMoi.addActionListener(e -> loadData());
    btnThongKe.addActionListener(e -> showThongKe());

    searchPanel.add(btnTimKiem);
    searchPanel.add(btnLamMoi);
    searchPanel.add(btnThongKe);

    // Table
    String[] columns = {
      "Mã lịch khám",
      "Mã gói",
      "Mã bác sĩ",
      "Thời gian bắt đầu",
      "Thời gian kết thúc",
      "Trạng thái",
      "Mã định danh",
    };

    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tableLichKham = new JTable(tableModel);
    tableLichKham.setRowHeight(25);
    tableLichKham.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(tableLichKham);

    // Button panel
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton btnChiTiet = new JButton("Chi tiết");
    JButton btnXacNhan = new JButton("Xác nhận");
    JButton btnBatDau = new JButton("Bắt đầu khám");
    JButton btnHoanThanh = new JButton("Hoàn thành");
    JButton btnHuy = new JButton("Hủy lịch");

    btnChiTiet.addActionListener(e -> showChiTiet());
    btnXacNhan.addActionListener(e -> xacNhanLichKham());
    btnBatDau.addActionListener(e -> batDauKham());
    btnHoanThanh.addActionListener(e -> hoanThanhKham());
    btnHuy.addActionListener(e -> huyLichKham());

    btnPanel.add(btnChiTiet);
    btnPanel.add(btnXacNhan);
    btnPanel.add(btnBatDau);
    btnPanel.add(btnHoanThanh);
    btnPanel.add(btnHuy);

    // Key listener for search
    txtTimKiem.addKeyListener(
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            timKiem();
          }
        }
      }
    );

    cboTrangThai.addActionListener(e -> timKiem());

    // Layout
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(lblTitle, BorderLayout.NORTH);
    topPanel.add(searchPanel, BorderLayout.CENTER);

    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(scrollPane, BorderLayout.CENTER);
    centerPanel.add(btnPanel, BorderLayout.SOUTH);

    add(topPanel, BorderLayout.NORTH);
    add(centerPanel, BorderLayout.CENTER);
  }

  private void loadData() {
    tableModel.setRowCount(0);
    ArrayList<LichKhamDTO> danhSach = lichKhamBUS.getAll();

    for (LichKhamDTO lk : danhSach) {
      Object[] row = {
        lk.getMaLichKham(),
        lk.getMaGoi(),
        lk.getMaBacSi(),
        lk.getThoiGianBatDau(),
        lk.getThoiGianKetThuc(),
        lk.getTrangThai(),
        lk.getMaDinhDanhTam(),
      };
      tableModel.addRow(row);
    }
  }

  private void timKiem() {
    tableModel.setRowCount(0);
    String keyword = txtTimKiem.getText().trim();
    String trangThai = (String) cboTrangThai.getSelectedItem();

    ArrayList<LichKhamDTO> danhSach;

    if (trangThai.equals("Tất cả")) {
      if (keyword.isEmpty()) {
        danhSach = lichKhamBUS.getAll();
      } else {
        danhSach = lichKhamBUS.search(keyword);
      }
    } else {
      danhSach = lichKhamBUS.getByTrangThai(trangThai);
      if (!keyword.isEmpty()) {
        ArrayList<LichKhamDTO> filtered = new ArrayList<>();
        for (LichKhamDTO lk : danhSach) {
          if (
            lk.getMaLichKham().toLowerCase().contains(keyword.toLowerCase()) ||
            lk.getMaBacSi().toLowerCase().contains(keyword.toLowerCase()) ||
            lk.getMaGoi().toLowerCase().contains(keyword.toLowerCase())
          ) {
            filtered.add(lk);
          }
        }
        danhSach = filtered;
      }
    }

    for (LichKhamDTO lk : danhSach) {
      Object[] row = {
        lk.getMaLichKham(),
        lk.getMaGoi(),
        lk.getMaBacSi(),
        lk.getThoiGianBatDau(),
        lk.getThoiGianKetThuc(),
        lk.getTrangThai(),
        lk.getMaDinhDanhTam(),
      };
      tableModel.addRow(row);
    }
  }

  private void showChiTiet() {
    int selectedRow = tableLichKham.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn lịch khám cần xem chi tiết!"
      );
      return;
    }

    String maLichKham = tableModel.getValueAt(selectedRow, 0).toString();
    LichKhamDTO lk = lichKhamBUS.getById(maLichKham);

    if (lk == null) {
      JOptionPane.showMessageDialog(this, "Không tìm thấy lịch khám!");
      return;
    }

    String info =
      "Mã lịch khám: " +
      lk.getMaLichKham() +
      "\n" +
      "Mã gói: " +
      lk.getMaGoi() +
      "\n" +
      "Mã bác sĩ: " +
      lk.getMaBacSi() +
      "\n" +
      "Thời gian: " +
      lk.getThoiGianBatDau() +
      " - " +
      lk.getThoiGianKetThuc() +
      "\n" +
      "Trạng thái: " +
      lk.getTrangThai() +
      "\n" +
      "Mã định danh: " +
      lk.getMaDinhDanhTam();

    JOptionPane.showMessageDialog(
      this,
      info,
      "Chi tiết lịch khám",
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  private void huyLichKham() {
    int selectedRow = tableLichKham.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch khám cần hủy!");
      return;
    }

    String maLichKham = tableModel.getValueAt(selectedRow, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Bạn có chắc muốn hủy lịch khám " + maLichKham + "?",
      "Xác nhận hủy",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      String result = lichKhamBUS.huyLichKham(maLichKham);
      JOptionPane.showMessageDialog(this, result);

      if (result.contains("thành công")) {
        loadData();
      }
    }
  }

  private void xacNhanLichKham() {
    int selectedRow = tableLichKham.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng chọn lịch khám cần xác nhận!"
      );
      return;
    }

    String maLichKham = tableModel.getValueAt(selectedRow, 0).toString();
    LichKhamDTO lk = lichKhamBUS.getById(maLichKham);

    if (!lk.getTrangThai().equals("Đã đặt")) {
      JOptionPane.showMessageDialog(
        this,
        "Chỉ có thể xác nhận lịch ở trạng thái 'Đã đặt'!"
      );
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Xác nhận lịch khám " + maLichKham + "?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      lichKhamBUS.updateTrangThai(maLichKham, "Đã đặt");
      JOptionPane.showMessageDialog(this, "Đã xác nhận lịch khám thành công!");
      loadData();
    }
  }

  private void batDauKham() {
    int selectedRow = tableLichKham.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch khám!");
      return;
    }

    String maLichKham = tableModel.getValueAt(selectedRow, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Bắt đầu khám cho lịch " + maLichKham + "?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      String result = lichKhamBUS.updateTrangThai(maLichKham, "Đang khám");
      JOptionPane.showMessageDialog(this, result);
      if (result.contains("thành công")) {
        loadData();
      }
    }
  }

  private void hoanThanhKham() {
    int selectedRow = tableLichKham.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch khám!");
      return;
    }

    String maLichKham = tableModel.getValueAt(selectedRow, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Hoàn thành khám cho lịch " + maLichKham + "?",
      "Xác nhận",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      String result = lichKhamBUS.updateTrangThai(maLichKham, "Hoàn thành");
      JOptionPane.showMessageDialog(this, result);

      if (result.contains("thành công")) {
        loadData();
      }
    }
  }

  private void showThongKe() {
    String thongKe = lichKhamBUS.thongKeLichKham();
    JOptionPane.showMessageDialog(
      this,
      thongKe,
      "Thống kê lịch khám",
      JOptionPane.INFORMATION_MESSAGE
    );
  }
}
