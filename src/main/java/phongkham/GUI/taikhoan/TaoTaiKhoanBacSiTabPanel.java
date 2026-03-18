package phongkham.gui.taikhoan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import phongkham.DTO.KhoaDTO;

public class TaoTaiKhoanBacSiTabPanel extends JPanel {

  private final QuanLyTaiKhoanService service;
  private final Runnable onDataChanged;

  private JTextField txtUsername;
  private JTextField txtEmail;
  private JTextField txtPassword;
  private JTextField txtHoTen;
  private JTextField txtSoDienThoai;
  private JTextField txtChuyenKhoa;
  private JComboBox<String> cbKhoa;

  public TaoTaiKhoanBacSiTabPanel(
    QuanLyTaiKhoanService service,
    Runnable onDataChanged
  ) {
    this.service = service;
    this.onDataChanged = onDataChanged;

    setLayout(new BorderLayout(8, 8));
    setOpaque(false);

    initUi();
    loadKhoa();
  }

  private void initUi() {
    JLabel title = new JLabel("Tao tai khoan bac si + ho so");
    title.setFont(new Font("Segoe UI", Font.BOLD, 18));
    title.setForeground(new Color(30, 41, 59));
    add(title, BorderLayout.NORTH);

    JPanel card = new JPanel(new BorderLayout(10, 10));
    card.setOpaque(false);
    card.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(12, 12, 12, 12)
      )
    );

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);

    txtUsername = new JTextField();
    txtEmail = new JTextField();
    txtPassword = new JTextField();
    txtHoTen = new JTextField();
    txtSoDienThoai = new JTextField();
    txtChuyenKhoa = new JTextField();
    cbKhoa = new JComboBox<>();

    addFormField(form, 0, "Username", txtUsername);
    addFormField(form, 1, "Email", txtEmail);
    addFormField(form, 2, "Mat khau", txtPassword);
    addFormField(form, 3, "Ho ten bac si", txtHoTen);
    addFormField(form, 4, "So dien thoai", txtSoDienThoai);
    addFormField(form, 5, "Chuyen khoa", txtChuyenKhoa);
    addFormField(form, 6, "Khoa", cbKhoa);

    JButton btnCreate = new JButton("Tao tai khoan bac si");
    btnCreate.setBackground(new Color(37, 99, 235));
    btnCreate.setForeground(Color.WHITE);
    btnCreate.setFocusPainted(false);
    btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnCreate.addActionListener(e -> createDoctorAccount());

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    actionPanel.setOpaque(false);
    actionPanel.add(btnCreate);

    card.add(form, BorderLayout.CENTER);
    card.add(actionPanel, BorderLayout.SOUTH);

    add(card, BorderLayout.CENTER);
  }

  private void addFormField(
    JPanel form,
    int row,
    String label,
    Component input
  ) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;

    JLabel lbl = new JLabel(label + ":");
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    form.add(lbl, gbc);

    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    form.add(input, gbc);
  }

  public void loadKhoa() {
    cbKhoa.removeAllItems();
    for (KhoaDTO khoa : service.getAllKhoa()) {
      cbKhoa.addItem(khoa.getMaKhoa() + " - " + khoa.getTenKhoa());
    }
  }

  private void createDoctorAccount() {
    String khoaItem =
      cbKhoa.getSelectedItem() == null
        ? ""
        : cbKhoa.getSelectedItem().toString();
    String maKhoa = khoaItem.contains(" - ")
      ? khoaItem.split(" - ")[0].trim()
      : "";

    String result = service.createDoctorAccountWithProfile(
      txtUsername.getText(),
      txtPassword.getText(),
      txtEmail.getText(),
      txtHoTen.getText(),
      txtSoDienThoai.getText(),
      txtChuyenKhoa.getText(),
      maKhoa
    );

    JOptionPane.showMessageDialog(this, result);
    if (result.startsWith("Tạo tài khoản bác sĩ thành công")) {
      txtUsername.setText("");
      txtPassword.setText("");
      txtEmail.setText("");
      txtHoTen.setText("");
      txtSoDienThoai.setText("");
      txtChuyenKhoa.setText("");
      if (onDataChanged != null) {
        onDataChanged.run();
      }
    }
  }
}
