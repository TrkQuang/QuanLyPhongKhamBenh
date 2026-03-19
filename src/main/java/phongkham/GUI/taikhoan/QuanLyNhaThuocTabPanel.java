package phongkham.gui.taikhoan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import phongkham.DTO.UsersDTO;

public class QuanLyNhaThuocTabPanel extends JPanel {

  private final QuanLyTaiKhoanService service;
  private final Runnable onDataChanged;

  private JTextField txtNhaThuocUsername;
  private JTextField txtNhaThuocEmail;
  private JTextField txtNhaThuocPassword;

  private JTextField txtPharmacySearch;
  private JComboBox<String> cbPharmacyStatus;

  private JTable tablePharmacyUsers;
  private DefaultTableModel pharmacyModel;
  private TableRowSorter<DefaultTableModel> pharmacySorter;

  public QuanLyNhaThuocTabPanel(
    QuanLyTaiKhoanService service,
    Runnable onDataChanged
  ) {
    this.service = service;
    this.onDataChanged = onDataChanged;

    setLayout(new BorderLayout(8, 8));
    setOpaque(false);

    initUi();
    refreshData();
  }

  private void initUi() {
    txtNhaThuocUsername = new JTextField();
    txtNhaThuocEmail = new JTextField();
    txtNhaThuocPassword = new JTextField();
    txtPharmacySearch = new JTextField(18);
    cbPharmacyStatus = new JComboBox<>(
      new String[] { "Tat ca", "Hoat dong", "Da khoa" }
    );

    JPanel northWrap = new JPanel();
    northWrap.setLayout(new BoxLayout(northWrap, BoxLayout.Y_AXIS));
    northWrap.setOpaque(false);

    JPanel createPanel = new JPanel(new BorderLayout(8, 8));
    createPanel.setOpaque(false);
    createPanel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
      )
    );

    JLabel lblCreateTitle = new JLabel("Tao tai khoan nha thuoc");
    lblCreateTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

    JPanel createForm = new JPanel(new GridBagLayout());
    createForm.setOpaque(false);
    addFormGridRow(createForm, 0, "Username", txtNhaThuocUsername);
    addFormGridRow(createForm, 1, "Email", txtNhaThuocEmail);
    addFormGridRow(createForm, 2, "Mat khau", txtNhaThuocPassword);

    JButton btnCreatePharmacy = createActionButton(
      "Tao tai khoan nha thuoc",
      new Color(124, 58, 237)
    );
    btnCreatePharmacy.addActionListener(e -> createPharmacyAccount());

    JPanel createAction = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    createAction.setOpaque(false);
    createAction.add(btnCreatePharmacy);

    createPanel.add(lblCreateTitle, BorderLayout.NORTH);
    createPanel.add(createForm, BorderLayout.CENTER);
    createPanel.add(createAction, BorderLayout.SOUTH);

    JPanel filterPanel = new JPanel(new GridBagLayout());
    filterPanel.setOpaque(false);
    filterPanel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
      )
    );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    filterPanel.add(new JLabel("Tim nhanh:"), gbc);

    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    filterPanel.add(txtPharmacySearch, gbc);

    gbc.gridx = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    filterPanel.add(new JLabel("Trang thai:"), gbc);

    gbc.gridx = 3;
    filterPanel.add(cbPharmacyStatus, gbc);

    northWrap.add(createPanel);
    northWrap.add(Box.createVerticalStrut(8));
    northWrap.add(filterPanel);

    add(northWrap, BorderLayout.NORTH);

    pharmacyModel = new DefaultTableModel(
      new String[] { "UserID", "Username", "Email", "Trang thai" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tablePharmacyUsers = new JTable(pharmacyModel);
    tablePharmacyUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tablePharmacyUsers.setRowHeight(28);
    pharmacySorter = new TableRowSorter<>(pharmacyModel);
    tablePharmacyUsers.setRowSorter(pharmacySorter);
    tablePharmacyUsers
      .getColumnModel()
      .getColumn(3)
      .setCellRenderer(
        new DefaultTableCellRenderer() {
          @Override
          public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
          ) {
            Component c = super.getTableCellRendererComponent(
              table,
              value,
              isSelected,
              hasFocus,
              row,
              column
            );
            String status = value == null ? "" : value.toString();
            setHorizontalAlignment(JLabel.CENTER);
            if (!isSelected) {
              if ("Hoat dong".equalsIgnoreCase(status)) {
                c.setForeground(new Color(22, 163, 74));
              } else {
                c.setForeground(new Color(220, 38, 38));
              }
            }
            setText(
              "Hoat dong".equalsIgnoreCase(status) ? "● Hoat dong" : "● Da khoa"
            );
            return c;
          }
        }
      );

    installQuickFilters();

    add(new JScrollPane(tablePharmacyUsers), BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actions.setOpaque(false);
    actions.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

    JButton btnRefresh = createActionButton("Lam moi", new Color(59, 130, 246));
    JButton btnDisable = createActionButton(
      "Vo hieu hoa",
      new Color(239, 68, 68)
    );
    JButton btnEnable = createActionButton(
      "Kich hoat",
      new Color(16, 185, 129)
    );
    JButton btnReset = createActionButton(
      "Reset mat khau",
      new Color(245, 158, 11)
    );

    btnRefresh.addActionListener(e -> refreshData());
    btnDisable.addActionListener(e -> toggleUserActive(false));
    btnEnable.addActionListener(e -> toggleUserActive(true));
    btnReset.addActionListener(e -> resetPassword());

    actions.add(btnRefresh);
    actions.add(btnDisable);
    actions.add(btnEnable);
    actions.add(btnReset);

    add(actions, BorderLayout.SOUTH);
  }

  private void addFormGridRow(
    JPanel panel,
    int row,
    String label,
    Component input
  ) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    panel.add(new JLabel(label + ":"), gbc);

    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add(input, gbc);
  }

  private JButton createActionButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    return button;
  }

  public void refreshData() {
    pharmacyModel.setRowCount(0);
    for (UsersDTO user : service.getUsersByRole(
      QuanLyTaiKhoanService.ROLE_NHATHUOC
    )) {
      pharmacyModel.addRow(
        new Object[] {
          user.getUserID(),
          user.getUsername(),
          user.getEmail(),
          user.isActive() ? "Hoat dong" : "Da khoa",
        }
      );
    }
    applyFilter();
  }

  private void createPharmacyAccount() {
    String result = service.createPharmacyAccount(
      txtNhaThuocUsername.getText(),
      txtNhaThuocPassword.getText(),
      txtNhaThuocEmail.getText()
    );

    JOptionPane.showMessageDialog(this, result);
    if (result.startsWith("Tạo tài khoản nhà thuốc thành công")) {
      txtNhaThuocUsername.setText("");
      txtNhaThuocPassword.setText("");
      txtNhaThuocEmail.setText("");
      if (onDataChanged != null) {
        onDataChanged.run();
      }
    }
  }

  private String getSelectedUserId() {
    int row = tablePharmacyUsers.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Vui long chon tai khoan.");
      return null;
    }
    int modelRow = tablePharmacyUsers.convertRowIndexToModel(row);
    return pharmacyModel.getValueAt(modelRow, 0).toString();
  }

  private void toggleUserActive(boolean enable) {
    String userId = getSelectedUserId();
    if (userId == null) {
      return;
    }

    String message = enable
      ? service.enableUser(userId)
      : service.disableUser(userId);
    JOptionPane.showMessageDialog(this, message);

    if (onDataChanged != null) {
      onDataChanged.run();
    }
  }

  private void resetPassword() {
    String userId = getSelectedUserId();
    if (userId == null) {
      return;
    }

    String newPassword = JOptionPane.showInputDialog(
      this,
      "Nhap mat khau moi (>= 6 ky tu):",
      "Reset mat khau",
      JOptionPane.QUESTION_MESSAGE
    );

    if (newPassword == null) {
      return;
    }

    String message = service.resetPassword(userId, newPassword);
    JOptionPane.showMessageDialog(this, message);
  }

  private void installQuickFilters() {
    txtPharmacySearch
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

    cbPharmacyStatus.addActionListener(e -> applyFilter());
  }

  private void applyFilter() {
    if (pharmacySorter == null) {
      return;
    }

    final String keyword =
      txtPharmacySearch == null
        ? ""
        : txtPharmacySearch.getText().trim().toLowerCase(Locale.ROOT);
    final String status =
      cbPharmacyStatus == null
        ? "Tat ca"
        : cbPharmacyStatus.getSelectedItem().toString();

    pharmacySorter.setRowFilter(
      new RowFilter<DefaultTableModel, Integer>() {
        @Override
        public boolean include(
          Entry<? extends DefaultTableModel, ? extends Integer> entry
        ) {
          String userId = String.valueOf(entry.getValue(0)).toLowerCase(
            Locale.ROOT
          );
          String username = String.valueOf(entry.getValue(1)).toLowerCase(
            Locale.ROOT
          );
          String email = String.valueOf(entry.getValue(2)).toLowerCase(
            Locale.ROOT
          );
          String rowStatus = String.valueOf(entry.getValue(3));

          boolean matchesKeyword =
            keyword.isEmpty() ||
            userId.contains(keyword) ||
            username.contains(keyword) ||
            email.contains(keyword);

          boolean matchesStatus =
            "Tat ca".equals(status) ||
            ("Hoat dong".equals(status) &&
              "Hoat dong".equalsIgnoreCase(rowStatus)) ||
            ("Da khoa".equals(status) && "Da khoa".equalsIgnoreCase(rowStatus));

          return matchesKeyword && matchesStatus;
        }
      }
    );
  }
}
