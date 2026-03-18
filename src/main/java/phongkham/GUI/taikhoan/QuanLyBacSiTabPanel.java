package phongkham.gui.taikhoan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.KhoaBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.KhoaDTO;

public class QuanLyBacSiTabPanel extends JPanel {

  private final Runnable onDataChanged;
  private final BacSiBUS bacSiBUS;
  private final KhoaBUS khoaBUS;

  private JTable table;
  private DefaultTableModel model;
  private TableRowSorter<DefaultTableModel> sorter;

  private JTextField txtSearch;
  private JComboBox<String> cbKhoaFilter;

  private JTextField txtMaBacSi;
  private JTextField txtHoTen;
  private JTextField txtChuyenKhoa;
  private JTextField txtSoDienThoai;
  private JTextField txtEmail;
  private JComboBox<String> cbMaKhoa;

  public QuanLyBacSiTabPanel(Runnable onDataChanged) {
    this.onDataChanged = onDataChanged;
    this.bacSiBUS = new BacSiBUS();
    this.khoaBUS = new KhoaBUS();

    setLayout(new BorderLayout(10, 10));
    setOpaque(false);

    add(createTopPanel(), BorderLayout.NORTH);
    add(createTablePanel(), BorderLayout.CENTER);
    add(createEditorPanel(), BorderLayout.SOUTH);

    bindEvents();
    loadKhoaFilters();
    refreshData();
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    panel.setOpaque(false);

    txtSearch = new JTextField(20);
    cbKhoaFilter = new JComboBox<>();

    panel.add(new JLabel("Tim nhanh:"));
    panel.add(txtSearch);
    panel.add(new JLabel("Khoa:"));
    panel.add(cbKhoaFilter);

    return panel;
  }

  private JScrollPane createTablePanel() {
    model = new DefaultTableModel(
      new String[] {
        "Ma bac si",
        "Ho ten",
        "Chuyen khoa",
        "So dien thoai",
        "Email",
        "Ma khoa",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    table = new JTable(model);
    table.setRowHeight(30);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

    sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);

    return new JScrollPane(table);
  }

  private JPanel createEditorPanel() {
    JPanel panel = new JPanel(new BorderLayout(8, 8));
    panel.setOpaque(false);
    panel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
      )
    );

    JLabel title = new JLabel("Cap nhat ho so bac si");
    title.setFont(new Font("Segoe UI", Font.BOLD, 14));

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);

    txtMaBacSi = new JTextField();
    txtMaBacSi.setEditable(false);
    txtHoTen = new JTextField();
    txtChuyenKhoa = new JTextField();
    txtSoDienThoai = new JTextField();
    txtEmail = new JTextField();
    cbMaKhoa = new JComboBox<>();

    addFormRow(form, 0, "Ma bac si", txtMaBacSi);
    addFormRow(form, 1, "Ho ten", txtHoTen);
    addFormRow(form, 2, "Chuyen khoa", txtChuyenKhoa);
    addFormRow(form, 3, "So dien thoai", txtSoDienThoai);
    addFormRow(form, 4, "Email", txtEmail);
    addFormRow(form, 5, "Ma khoa", cbMaKhoa);

    JButton btnCapNhat = createButton("Cap nhat", new Color(37, 99, 235));
    JButton btnLamMoi = createButton("Lam moi", new Color(100, 116, 139));

    btnCapNhat.addActionListener(e -> capNhatBacSi());
    btnLamMoi.addActionListener(e -> refreshData());

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actions.setOpaque(false);
    actions.add(btnCapNhat);
    actions.add(btnLamMoi);

    panel.add(title, BorderLayout.NORTH);
    panel.add(form, BorderLayout.CENTER);
    panel.add(actions, BorderLayout.SOUTH);

    return panel;
  }

  private void addFormRow(
    JPanel panel,
    int row,
    String label,
    java.awt.Component input
  ) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    panel.add(new JLabel(label + ":"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(input, gbc);
  }

  private JButton createButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    return button;
  }

  private void bindEvents() {
    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
          return;
        }
        fillFormFromSelection();
      });

    txtSearch
      .getDocument()
      .addDocumentListener(
        new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            applyFilter();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            applyFilter();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            applyFilter();
          }
        }
      );

    cbKhoaFilter.addActionListener(e -> applyFilter());
  }

  private void loadKhoaFilters() {
    cbKhoaFilter.removeAllItems();
    cbKhoaFilter.addItem("Tat ca");

    cbMaKhoa.removeAllItems();

    for (KhoaDTO khoa : khoaBUS.getAll()) {
      String item = khoa.getMaKhoa() + " - " + khoa.getTenKhoa();
      cbKhoaFilter.addItem(item);
      cbMaKhoa.addItem(item);
    }
  }

  public void refreshData() {
    model.setRowCount(0);

    ArrayList<BacSiDTO> list = bacSiBUS.getAll();
    for (BacSiDTO bs : list) {
      model.addRow(
        new Object[] {
          bs.getMaBacSi(),
          bs.getHoTen(),
          bs.getChuyenKhoa(),
          bs.getSoDienThoai(),
          bs.getEmail(),
          bs.getMaKhoa(),
        }
      );
    }

    clearForm();
    applyFilter();
  }

  private void applyFilter() {
    if (sorter == null) {
      return;
    }

    final String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
    final String khoaSelected =
      cbKhoaFilter.getSelectedItem() == null
        ? "Tat ca"
        : cbKhoaFilter.getSelectedItem().toString();
    final String maKhoaFilter = "Tat ca".equals(khoaSelected)
      ? ""
      : khoaSelected.split(" - ")[0].trim();

    sorter.setRowFilter(
      new RowFilter<DefaultTableModel, Integer>() {
        @Override
        public boolean include(
          Entry<? extends DefaultTableModel, ? extends Integer> entry
        ) {
          String ma = String.valueOf(entry.getValue(0)).toLowerCase(
            Locale.ROOT
          );
          String ten = String.valueOf(entry.getValue(1)).toLowerCase(
            Locale.ROOT
          );
          String ck = String.valueOf(entry.getValue(2)).toLowerCase(
            Locale.ROOT
          );
          String sdt = String.valueOf(entry.getValue(3)).toLowerCase(
            Locale.ROOT
          );
          String email = String.valueOf(entry.getValue(4)).toLowerCase(
            Locale.ROOT
          );
          String maKhoa = String.valueOf(entry.getValue(5));

          boolean matchKeyword =
            keyword.isEmpty() ||
            ma.contains(keyword) ||
            ten.contains(keyword) ||
            ck.contains(keyword) ||
            sdt.contains(keyword) ||
            email.contains(keyword);

          boolean matchKhoa =
            maKhoaFilter.isEmpty() || maKhoaFilter.equalsIgnoreCase(maKhoa);

          return matchKeyword && matchKhoa;
        }
      }
    );
  }

  private void fillFormFromSelection() {
    int row = table.getSelectedRow();
    if (row < 0) {
      return;
    }

    int modelRow = table.convertRowIndexToModel(row);
    txtMaBacSi.setText(String.valueOf(model.getValueAt(modelRow, 0)));
    txtHoTen.setText(String.valueOf(model.getValueAt(modelRow, 1)));
    txtChuyenKhoa.setText(String.valueOf(model.getValueAt(modelRow, 2)));
    txtSoDienThoai.setText(String.valueOf(model.getValueAt(modelRow, 3)));
    txtEmail.setText(String.valueOf(model.getValueAt(modelRow, 4)));

    String maKhoa = String.valueOf(model.getValueAt(modelRow, 5));
    for (int i = 0; i < cbMaKhoa.getItemCount(); i++) {
      String item = cbMaKhoa.getItemAt(i);
      if (item.startsWith(maKhoa + " ")) {
        cbMaKhoa.setSelectedIndex(i);
        break;
      }
    }
  }

  private void capNhatBacSi() {
    String maBacSi = txtMaBacSi.getText().trim();
    if (maBacSi.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui long chon bac si trong bang truoc."
      );
      return;
    }

    String hoTen = txtHoTen.getText().trim();
    String soDienThoai = txtSoDienThoai.getText().trim();
    String email = txtEmail.getText().trim();
    String chuyenKhoa = txtChuyenKhoa.getText().trim();
    String khoaItem =
      cbMaKhoa.getSelectedItem() == null
        ? ""
        : cbMaKhoa.getSelectedItem().toString();
    String maKhoa = khoaItem.contains(" - ")
      ? khoaItem.split(" - ")[0].trim()
      : "";

    if (hoTen.isEmpty() || maKhoa.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Ho ten va khoa khong duoc de trong."
      );
      return;
    }

    BacSiDTO dto = new BacSiDTO();
    dto.setMaBacSi(maBacSi);
    dto.setHoTen(hoTen);
    dto.setSoDienThoai(soDienThoai);
    dto.setEmail(email);
    dto.setChuyenKhoa(chuyenKhoa);
    dto.setMaKhoa(maKhoa);

    if (bacSiBUS.update(dto)) {
      JOptionPane.showMessageDialog(this, "Cap nhat bac si thanh cong.");
      refreshData();
      if (onDataChanged != null) {
        onDataChanged.run();
      }
    } else {
      JOptionPane.showMessageDialog(this, "Khong the cap nhat bac si.");
    }
  }

  private void clearForm() {
    txtMaBacSi.setText("");
    txtHoTen.setText("");
    txtChuyenKhoa.setText("");
    txtSoDienThoai.setText("");
    txtEmail.setText("");
    if (cbMaKhoa.getItemCount() > 0) {
      cbMaKhoa.setSelectedIndex(0);
    }
  }
}
