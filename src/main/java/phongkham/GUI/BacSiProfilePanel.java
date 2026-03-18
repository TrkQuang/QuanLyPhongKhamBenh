package phongkham.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;

public class BacSiProfilePanel extends JPanel {

  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final UsersBUS usersBUS = new UsersBUS();

  private JTextField txtMaBacSi;
  private JTextField txtHoTen;
  private JTextField txtSoDienThoai;
  private JTextField txtEmail;
  private JTextField txtChuyenKhoa;
  private JTextField txtMaKhoa;

  private JPasswordField txtMatKhauCu;
  private JPasswordField txtMatKhauMoi;
  private JPasswordField txtNhapLaiMatKhauMoi;

  private JButton btnCapNhat;
  private JButton btnDoiMatKhau;
  private JButton btnLamMoi;

  public BacSiProfilePanel() {
    setLayout(new BorderLayout(10, 10));
    setBackground(new Color(245, 247, 250));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    add(createHeader(), BorderLayout.NORTH);
    add(createMainContent(), BorderLayout.CENTER);

    refreshData();
  }

  private JPanel createHeader() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    JLabel title = new JLabel("Hồ Sơ Bác Sĩ");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(new Color(30, 41, 59));

    panel.add(title, BorderLayout.WEST);
    return panel;
  }

  private JPanel createMainContent() {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
        BorderFactory.createEmptyBorder(12, 12, 12, 12)
      )
    );

    JPanel formInfo = new JPanel(new GridLayout(0, 2, 8, 8));
    formInfo.setOpaque(false);

    txtMaBacSi = new JTextField();
    txtMaBacSi.setEditable(false);
    txtHoTen = new JTextField();
    txtSoDienThoai = new JTextField();
    txtEmail = new JTextField();
    txtChuyenKhoa = new JTextField();
    txtChuyenKhoa.setEditable(false);
    txtMaKhoa = new JTextField();
    txtMaKhoa.setEditable(false);

    addField(formInfo, "Mã bác sĩ", txtMaBacSi);
    addField(formInfo, "Họ tên", txtHoTen);
    addField(formInfo, "Số điện thoại", txtSoDienThoai);
    addField(formInfo, "Email", txtEmail);
    addField(formInfo, "Chuyên khoa", txtChuyenKhoa);
    addField(formInfo, "Mã khoa", txtMaKhoa);

    panel.add(formInfo);
    panel.add(Box.createVerticalStrut(12));

    JLabel lblMatKhau = new JLabel("Đổi mật khẩu");
    lblMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblMatKhau.setForeground(new Color(51, 65, 85));
    panel.add(lblMatKhau);
    panel.add(Box.createVerticalStrut(8));

    JPanel formPassword = new JPanel(new GridLayout(0, 2, 8, 8));
    formPassword.setOpaque(false);

    txtMatKhauCu = new JPasswordField();
    txtMatKhauMoi = new JPasswordField();
    txtNhapLaiMatKhauMoi = new JPasswordField();

    addField(formPassword, "Mật khẩu cũ", txtMatKhauCu);
    addField(formPassword, "Mật khẩu mới", txtMatKhauMoi);
    addField(formPassword, "Nhập lại mật khẩu mới", txtNhapLaiMatKhauMoi);

    panel.add(formPassword);
    panel.add(Box.createVerticalStrut(12));

    JPanel actions = new JPanel(
      new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0)
    );
    actions.setOpaque(false);

    btnCapNhat = createButton("Cập nhật", new Color(37, 99, 235));
    btnDoiMatKhau = createButton("Đổi mật khẩu", new Color(16, 185, 129));
    btnLamMoi = createButton("Làm mới", new Color(100, 116, 139));

    btnCapNhat.addActionListener(e -> handleUpdate());
    btnDoiMatKhau.addActionListener(e -> handleChangePassword());
    btnLamMoi.addActionListener(e -> refreshData());

    actions.add(btnCapNhat);
    actions.add(btnDoiMatKhau);
    actions.add(btnLamMoi);

    panel.add(actions);
    wrapper.add(panel, BorderLayout.CENTER);

    return wrapper;
  }

  private void addField(
    JPanel form,
    String label,
    javax.swing.JComponent field
  ) {
    JLabel lbl = new JLabel(label + ":");
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    form.add(lbl);
    form.add(field);
  }

  private JButton createButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    return button;
  }

  private void refreshData() {
    String currentDoctorId = resolveCurrentDoctorId();
    if (currentDoctorId == null) {
      disableAllActions();
      clearDoctorForm();
      return;
    }

    BacSiDTO dto = bacSiBUS.getById(currentDoctorId);
    if (dto == null) {
      disableAllActions();
      clearDoctorForm();
      return;
    }

    txtMaBacSi.setText(dto.getMaBacSi());
    txtHoTen.setText(dto.getHoTen());
    txtSoDienThoai.setText(dto.getSoDienThoai());
    txtEmail.setText(dto.getEmail());
    txtChuyenKhoa.setText(dto.getChuyenKhoa());
    txtMaKhoa.setText(dto.getMaKhoa());

    clearPasswordForm();
    btnCapNhat.setEnabled(true);
    btnDoiMatKhau.setEnabled(true);
  }

  private void handleUpdate() {
    String currentDoctorId = resolveCurrentDoctorId();
    if (currentDoctorId == null) {
      JOptionPane.showMessageDialog(
        this,
        "Không xác định được bác sĩ đang đăng nhập"
      );
      return;
    }

    BacSiDTO dto = new BacSiDTO();
    dto.setMaBacSi(currentDoctorId);
    dto.setHoTen(txtHoTen.getText().trim());
    dto.setSoDienThoai(txtSoDienThoai.getText().trim());
    dto.setEmail(txtEmail.getText().trim());
    dto.setChuyenKhoa(txtChuyenKhoa.getText().trim());
    dto.setMaKhoa(txtMaKhoa.getText().trim());

    if (dto.getHoTen().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Họ tên không được để trống");
      return;
    }

    if (bacSiBUS.update(dto)) {
      JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ thành công");
      refreshData();
    } else {
      JOptionPane.showMessageDialog(this, "Không thể cập nhật hồ sơ");
    }
  }

  private void handleChangePassword() {
    UsersDTO currentUser = Session.currentUser;
    if (currentUser == null) {
      JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập");
      return;
    }

    String matKhauCu = new String(txtMatKhauCu.getPassword()).trim();
    String matKhauMoi = new String(txtMatKhauMoi.getPassword()).trim();
    String nhapLai = new String(txtNhapLaiMatKhauMoi.getPassword()).trim();

    if (matKhauCu.isEmpty() || matKhauMoi.isEmpty() || nhapLai.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Vui lòng nhập đầy đủ thông tin mật khẩu"
      );
      return;
    }

    if (!matKhauMoi.equals(nhapLai)) {
      JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp");
      return;
    }

    if (matKhauMoi.length() < 6) {
      JOptionPane.showMessageDialog(
        this,
        "Mật khẩu mới phải có ít nhất 6 ký tự"
      );
      return;
    }

    UsersDTO checkLogin = usersBUS.login(currentUser.getUsername(), matKhauCu);
    if (checkLogin == null) {
      JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng");
      return;
    }

    String result = usersBUS.resetPassword(currentUser.getUserID(), matKhauMoi);
    if (result != null && result.toLowerCase().contains("thành công")) {
      JOptionPane.showMessageDialog(this, result);
      clearPasswordForm();
    } else {
      JOptionPane.showMessageDialog(
        this,
        result == null ? "Đổi mật khẩu thất bại" : result
      );
    }
  }

  private void disableAllActions() {
    btnCapNhat.setEnabled(false);
    btnDoiMatKhau.setEnabled(false);
  }

  private void clearDoctorForm() {
    txtMaBacSi.setText("");
    txtHoTen.setText("");
    txtSoDienThoai.setText("");
    txtEmail.setText("");
    txtChuyenKhoa.setText("");
    txtMaKhoa.setText("");
    clearPasswordForm();
  }

  private void clearPasswordForm() {
    txtMatKhauCu.setText("");
    txtMatKhauMoi.setText("");
    txtNhapLaiMatKhauMoi.setText("");
  }

  private String resolveCurrentDoctorId() {
    if (!Session.isLoggedIn() || Session.currentUser == null) {
      return null;
    }

    String currentDoctorId = Session.getCurrentBacSiID();
    if (currentDoctorId != null && !currentDoctorId.trim().isEmpty()) {
      return currentDoctorId.trim();
    }

    BacSiDTO byEmail = bacSiBUS.getByEmail(Session.currentUser.getEmail());
    if (byEmail != null && byEmail.getMaBacSi() != null) {
      Session.setCurrentBacSiID(byEmail.getMaBacSi());
      return byEmail.getMaBacSi();
    }

    String inferred = inferDoctorIdFromUsername(
      Session.currentUser.getUsername()
    );
    if (inferred != null && bacSiBUS.getById(inferred) != null) {
      Session.setCurrentBacSiID(inferred);
      return inferred;
    }

    return null;
  }

  private String inferDoctorIdFromUsername(String username) {
    if (username == null) {
      return null;
    }

    Matcher matcher = Pattern.compile("(\\d+)$").matcher(username.trim());
    if (!matcher.find()) {
      return null;
    }

    int index;
    try {
      index = Integer.parseInt(matcher.group(1));
    } catch (NumberFormatException ex) {
      return null;
    }

    return String.format("BS%03d", index);
  }
}
