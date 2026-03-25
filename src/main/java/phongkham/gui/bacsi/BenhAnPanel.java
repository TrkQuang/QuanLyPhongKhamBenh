package phongkham.gui.bacsi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.CTDonThuocBUS;
import phongkham.BUS.DonThuocBUS;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.CTDonThuocDTO;
import phongkham.DTO.DonThuocDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.LichKhamDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;

public class BenhAnPanel extends BasePanel {

  private static final String GHI_CHU_PLACEHOLDER = "Ghi chú";
  private static final String HO_SO_CHUA_TAO_PREFIX = "CHUA_TAO_";

  private final HoSoBenhAnBUS hoSoBenhAnBUS = new HoSoBenhAnBUS();
  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();
  private final DonThuocBUS donThuocBUS = new DonThuocBUS();
  private final CTDonThuocBUS ctDonThuocBUS = new CTDonThuocBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final BacSiBUS bacSiBUS = new BacSiBUS();

  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã hồ sơ",
      "Mã lịch",
      "Họ tên",
      "SĐT",
      "Ngày khám",
      "Mã bác sĩ",
      "Trạng thái",
      "Chẩn đoán",
    },
    0
  );
  private JTable table;
  private JButton btnKeHoSo;
  private JButton btnTaiLai;
  private boolean coQuyenXem = true;
  private boolean coQuyenKeHoSo = true;

  @Override
  protected void init() {
    add(
      UIUtils.createSection("Danh sách hồ sơ bệnh án", buildTable()),
      BorderLayout.CENTER
    );

    javax.swing.JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Kê hồ sơ bệnh án"),
      UIUtils.ghostButton("Tải lại")
    );
    btnKeHoSo = (JButton) actions.getComponent(0);
    btnTaiLai = (JButton) actions.getComponent(1);
    btnKeHoSo.addActionListener(e -> openExaminationDialog());
    btnTaiLai.addActionListener(e -> loadData());
    add(actions, BorderLayout.SOUTH);

    apDungPhanQuyenHanhDong();

    table
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
          updateKeHoSoButtonState();
        }
      });

    loadData();
  }

  private JScrollPane buildTable() {
    table = new JTable(model);
    UIUtils.styleTable(table);
    return new JScrollPane(table);
  }

  private void openExaminationDialog() {
    if (!coQuyenKeHoSo) {
      DialogHelper.warn(this, "Bạn không có quyền kê/cập nhật hồ sơ bệnh án.");
      return;
    }

    HoSoBenhAnDTO selected = getSelectedRecord();
    if (selected == null) {
      DialogHelper.warn(this, "Vui lòng chọn hồ sơ cần khám.");
      return;
    }

    boolean isPendingRecord =
      selected.getMaHoSo() == null ||
      selected.getMaHoSo().startsWith(HO_SO_CHUA_TAO_PREFIX);

    String trangThaiHoSo = StatusNormalizer.normalizeHoSoStatus(
      safe(selected.getTrangThai())
    );
    if (StatusNormalizer.DA_KHAM.equals(trangThaiHoSo)) {
      DialogHelper.warn(
        this,
        "Hồ sơ đã ở trạng thái ĐÃ KHÁM, không thể kê lại."
      );
      return;
    }

    String currentDoctorId = resolveCurrentDoctorId();
    if (currentDoctorId == null || currentDoctorId.trim().isEmpty()) {
      DialogHelper.error(this, "Không xác định được bác sĩ hiện tại.");
      return;
    }

    LichKhamDTO lich = lichKhamBUS.getById(selected.getMaLichKham());
    if (lich == null) {
      DialogHelper.error(this, "Không tìm thấy lịch khám tương ứng của hồ sơ.");
      return;
    }
    if (!currentDoctorId.equalsIgnoreCase(lich.getMaBacSi())) {
      DialogHelper.warn(
        this,
        "Chỉ bác sĩ được bệnh nhân đăng ký khám mới có thể kê hồ sơ bệnh án."
      );
      return;
    }

    JDialog dialog = new JDialog(
      javax.swing.SwingUtilities.getWindowAncestor(this),
      "Kê hồ sơ bệnh án",
      java.awt.Dialog.ModalityType.APPLICATION_MODAL
    );
    dialog.setSize(900, 700);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(0, 10));

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    CustomTextField txtMaHoSo = (CustomTextField) UIUtils.roundedTextField(
      "",
      18
    );
    txtMaHoSo.setText(
      selected.getMaHoSo() == null ||
        selected.getMaHoSo().trim().isEmpty() ||
        selected.getMaHoSo().startsWith(HO_SO_CHUA_TAO_PREFIX)
        ? generateMaHoSo()
        : selected.getMaHoSo()
    );
    txtMaHoSo.setEditable(false);
    applyReadOnlyStyle(txtMaHoSo);

    CustomTextField txtMaLich = (CustomTextField) UIUtils.roundedTextField(
      "",
      18
    );
    txtMaLich.setText(selected.getMaLichKham());
    txtMaLich.setEditable(false);
    applyReadOnlyStyle(txtMaLich);

    CustomTextField txtHoTen = (CustomTextField) UIUtils.roundedTextField(
      "Họ tên bệnh nhân",
      18
    );
    txtHoTen.setText(safe(selected.getHoTen()));
    txtHoTen.setEditable(isPendingRecord);
    if (!isPendingRecord) {
      applyReadOnlyStyle(txtHoTen);
    }

    CustomTextField txtSdt = (CustomTextField) UIUtils.roundedTextField(
      "Số điện thoại",
      18
    );
    txtSdt.setText(safe(selected.getSoDienThoai()));
    txtSdt.setEditable(isPendingRecord);
    if (!isPendingRecord) {
      applyReadOnlyStyle(txtSdt);
    }

    CustomTextField txtCCCD = (CustomTextField) UIUtils.roundedTextField(
      "CCCD",
      18
    );
    txtCCCD.setText(safe(selected.getCCCD()));
    txtCCCD.setEditable(isPendingRecord);
    if (!isPendingRecord) {
      applyReadOnlyStyle(txtCCCD);
    }

    JComboBox<String> cbGioiTinh = new JComboBox<>(
      new String[] { "Nam", "Nữ" }
    );
    cbGioiTinh.setSelectedItem(
      "Nu".equalsIgnoreCase(safe(selected.getGioiTinh())) ? "Nữ" : "Nam"
    );
    cbGioiTinh.setEnabled(isPendingRecord);

    JSpinner spNgaySinh = new JSpinner(
      new SpinnerDateModel(
        selected.getNgaySinh() == null
          ? new Date()
          : new Date(selected.getNgaySinh().getTime()),
        null,
        new Date(),
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));
    spNgaySinh.setEnabled(isPendingRecord);

    JSpinner spNgayKham = new JSpinner(
      new SpinnerDateModel(
        selected.getNgayKham() == null
          ? new Date()
          : new Date(selected.getNgayKham().getTime()),
        null,
        null,
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgayKham.setEditor(
      new JSpinner.DateEditor(spNgayKham, "dd/MM/yyyy HH:mm")
    );

    CustomTextField txtDiaChi = (CustomTextField) UIUtils.roundedTextField(
      "Địa chỉ",
      18
    );
    txtDiaChi.setText(safe(selected.getDiaChi()));
    txtDiaChi.setEditable(isPendingRecord);
    if (!isPendingRecord) {
      applyReadOnlyStyle(txtDiaChi);
    }

    JTextArea txtTrieuChung = new JTextArea(
      safe(selected.getTrieuChung()),
      3,
      20
    );
    JTextArea txtChanDoan = new JTextArea(safe(selected.getChanDoan()), 3, 20);
    JTextArea txtKetLuan = new JTextArea(safe(selected.getKetLuan()), 3, 20);
    JTextArea txtLoiDan = new JTextArea(safe(selected.getLoiDan()), 3, 20);
    wrapTextArea(txtTrieuChung);
    wrapTextArea(txtChanDoan);
    wrapTextArea(txtKetLuan);
    wrapTextArea(txtLoiDan);

    JComboBox<String> cbTrangThai = new JComboBox<>(
      new String[] {
        StatusNormalizer.CHO_KHAM,
        StatusNormalizer.DA_KHAM,
        "HUY",
      }
    );
    cbTrangThai.setSelectedItem(
      StatusNormalizer.normalizeHoSoStatus(safe(selected.getTrangThai()))
    );
    cbTrangThai.setEnabled(false);

    int row = 0;
    addFormRow(form, gbc, row++, "Mã hồ sơ", txtMaHoSo);
    addFormRow(form, gbc, row++, "Mã lịch khám", txtMaLich);
    addFormRow(form, gbc, row++, "Họ tên", txtHoTen);
    addFormRow(form, gbc, row++, "Số điện thoại", txtSdt);
    addFormRow(form, gbc, row++, "CCCD", txtCCCD);
    addFormRow(form, gbc, row++, "Ngày sinh", spNgaySinh);
    addFormRow(form, gbc, row++, "Giới tính", cbGioiTinh);
    addFormRow(form, gbc, row++, "Địa chỉ", txtDiaChi);
    addFormRow(form, gbc, row++, "Ngày khám", spNgayKham);
    addFormRow(form, gbc, row++, "Triệu chứng", new JScrollPane(txtTrieuChung));
    addFormRow(form, gbc, row++, "Chẩn đoán", new JScrollPane(txtChanDoan));
    addFormRow(form, gbc, row++, "Kết luận", new JScrollPane(txtKetLuan));
    addFormRow(form, gbc, row++, "Lời dặn", new JScrollPane(txtLoiDan));
    addFormRow(form, gbc, row++, "Trạng thái", cbTrangThai);

    JCheckBox chkKeDon = new JCheckBox("Kê thêm đơn thuốc");
    chkKeDon.setOpaque(false);
    JTextArea txtGhiChuDon = new JTextArea("", 2, 20);
    wrapTextArea(txtGhiChuDon);
    applyTextAreaPlaceholder(txtGhiChuDon, GHI_CHU_PLACEHOLDER);

    DefaultTableModel donModel = new DefaultTableModel(
      new Object[] {
        "Mã thuốc",
        "Tên thuốc",
        "Số lượng",
        "Liều dùng",
        "Cách dùng",
      },
      0
    );
    JTable tblDon = new JTable(donModel);
    UIUtils.styleTable(tblDon);
    JScrollPane donScroll = new JScrollPane(tblDon);
    donScroll.setPreferredSize(new Dimension(700, 150));

    JComboBox<String> cbThuoc = new JComboBox<>();
    for (ThuocDTO thuoc : thuocBUS.list()) {
      cbThuoc.addItem(thuoc.getMaThuoc() + " - " + thuoc.getTenThuoc());
    }
    CustomTextField txtSoLuongThuoc =
      (CustomTextField) UIUtils.roundedTextField("Số lượng", 8);
    txtSoLuongThuoc.setText("1");
    CustomTextField txtLieuDung = (CustomTextField) UIUtils.roundedTextField(
      "Liều dùng",
      10
    );
    CustomTextField txtCachDung = (CustomTextField) UIUtils.roundedTextField(
      "Cách dùng",
      12
    );

    JButton btnThemThuoc = UIUtils.ghostButton("Thêm thuốc vào đơn");
    btnThemThuoc.setBackground(new Color(219, 234, 254));
    btnThemThuoc.setForeground(new Color(29, 78, 216));
    btnThemThuoc.setBorder(
      javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new Color(147, 197, 253)),
        javax.swing.BorderFactory.createEmptyBorder(10, 18, 10, 18)
      )
    );
    btnThemThuoc.addActionListener(e -> {
      String thuocText = String.valueOf(cbThuoc.getSelectedItem());
      String maThuoc = extractDrugCode(thuocText);
      String tenThuoc = extractDrugName(thuocText);
      int soLuong;
      try {
        soLuong = Integer.parseInt(txtSoLuongThuoc.getText().trim());
      } catch (Exception ex) {
        DialogHelper.warn(dialog, "Số lượng thuốc không hợp lệ.");
        return;
      }
      if (soLuong <= 0) {
        DialogHelper.warn(dialog, "Số lượng thuốc phải lớn hơn 0.");
        return;
      }
      String ghiChu = getActualNoteText(txtGhiChuDon.getText());
      donModel.addRow(
        new Object[] {
          maThuoc,
          tenThuoc,
          soLuong,
          txtLieuDung.getText().trim(),
          txtCachDung.getText().trim(),
        }
      );
    });

    JPanel donInputRow = new JPanel(new GridLayout(1, 5, 6, 6));
    donInputRow.setOpaque(false);
    donInputRow.add(cbThuoc);
    donInputRow.add(txtSoLuongThuoc);
    donInputRow.add(txtLieuDung);
    donInputRow.add(txtCachDung);
    donInputRow.add(btnThemThuoc);

    JPanel donPanel = new JPanel(new BorderLayout(0, 6));
    donPanel.setOpaque(false);
    donPanel.add(chkKeDon, BorderLayout.NORTH);
    donPanel.add(donInputRow, BorderLayout.SOUTH);

    JPanel ghiChuPanel = new JPanel(new BorderLayout(0, 4));
    ghiChuPanel.setOpaque(false);
    ghiChuPanel.add(new JLabel("Ghi chú đơn thuốc"), BorderLayout.NORTH);
    JScrollPane ghiChuScroll = new JScrollPane(txtGhiChuDon);
    ghiChuScroll.setPreferredSize(new Dimension(700, 70));
    ghiChuPanel.add(ghiChuScroll, BorderLayout.CENTER);

    JPanel hoSoTab = new JPanel(new BorderLayout(0, 8));
    hoSoTab.setOpaque(false);
    JLabel lblHuongDan = new JLabel(
      "Thông tin bệnh nhân do hệ thống quản lý (chỉ đọc). Bác sĩ chỉ cập nhật nội dung khám và đơn thuốc."
    );
    lblHuongDan.setForeground(new Color(71, 85, 105));
    hoSoTab.add(lblHuongDan, BorderLayout.NORTH);
    hoSoTab.add(new JScrollPane(form), BorderLayout.CENTER);

    JPanel donWrap = new JPanel(new BorderLayout(0, 6));
    donWrap.setOpaque(false);
    donWrap.add(donPanel, BorderLayout.NORTH);
    donWrap.add(donScroll, BorderLayout.CENTER);
    donWrap.add(ghiChuPanel, BorderLayout.SOUTH);

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Hồ sơ bệnh án", hoSoTab);
    tabs.addTab(
      "Đơn thuốc",
      UIUtils.createSection("Thông tin đơn thuốc", donWrap)
    );

    JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Hoàn thành khám"),
      UIUtils.ghostButton("Đóng")
    );
    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e -> {
      String loi = validateMedicalForm(
        txtHoTen.getText().trim(),
        txtSdt.getText().trim(),
        txtCCCD.getText().trim(),
        (Date) spNgaySinh.getValue()
      );
      if (loi != null) {
        DialogHelper.warn(dialog, loi);
        return;
      }

      HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
      hs.setMaHoSo(txtMaHoSo.getText().trim());
      hs.setMaLichKham(txtMaLich.getText().trim());
      hs.setHoTen(txtHoTen.getText().trim());
      hs.setSoDienThoai(txtSdt.getText().trim());
      hs.setCCCD(txtCCCD.getText().trim());
      hs.setNgaySinh(
        new java.sql.Date(((Date) spNgaySinh.getValue()).getTime())
      );
      hs.setGioiTinh(
        "Nữ".equals(String.valueOf(cbGioiTinh.getSelectedItem())) ? "Nu" : "Nam"
      );
      hs.setDiaChi(txtDiaChi.getText().trim());
      hs.setNgayKham(
        new java.sql.Date(((Date) spNgayKham.getValue()).getTime())
      );
      hs.setTrieuChung(txtTrieuChung.getText().trim());
      hs.setChanDoan(txtChanDoan.getText().trim());
      hs.setKetLuan(txtKetLuan.getText().trim());
      hs.setLoiDan(txtLoiDan.getText().trim());
      hs.setMaBacSi(currentDoctorId);
      hs.setTrangThai(StatusNormalizer.DA_KHAM);

      HoSoBenhAnDTO existingByLich = hoSoBenhAnBUS.getByMaLichKham(
        hs.getMaLichKham()
      );
      boolean saved;
      if (existingByLich != null) {
        hs.setMaHoSo(existingByLich.getMaHoSo());
        saved = hoSoBenhAnBUS.updateHoSo(hs);
      } else {
        saved = hoSoBenhAnBUS.dangKyBenhNhan(hs);
      }
      if (!saved) {
        DialogHelper.error(dialog, "Cập nhật hồ sơ bệnh án thất bại.");
        return;
      }

      if (chkKeDon.isSelected()) {
        if (donModel.getRowCount() == 0) {
          DialogHelper.warn(
            dialog,
            "Đã chọn kê đơn nhưng chưa có thuốc trong đơn."
          );
          return;
        }

        DonThuocDTO don = new DonThuocDTO();
        don.setMaDonThuoc(donThuocBUS.generateMaDonThuoc());
        don.setMaHoSo(hs.getMaHoSo());
        don.setNgayKeDon(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        don.setGhiChu(getActualNoteText(txtGhiChuDon.getText()));
        if (!donThuocBUS.add(don)) {
          DialogHelper.error(dialog, "Không thể lưu đơn thuốc.");
          return;
        }

        for (int i = 0; i < donModel.getRowCount(); i++) {
          CTHDDonThuocSaved savedDetail = saveCtDonThuoc(don, donModel, i);
          if (!savedDetail.ok) {
            DialogHelper.error(dialog, savedDetail.message);
            return;
          }
        }
      }

      if (
        StatusNormalizer.DA_KHAM.equals(
          StatusNormalizer.normalizeHoSoStatus(hs.getTrangThai())
        )
      ) {
        lichKhamBUS.updateTrangThai(
          hs.getMaLichKham(),
          StatusNormalizer.HOAN_THANH
        );
      }

      DialogHelper.info(
        dialog,
        "Hoàn thành khám và cập nhật hồ sơ thành công."
      );
      dialog.dispose();
      loadData();
    });
    ((javax.swing.JButton) actions.getComponent(1)).addActionListener(e ->
      dialog.dispose()
    );

    dialog.add(tabs, BorderLayout.CENTER);
    dialog.add(actions, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  private CTHDDonThuocSaved saveCtDonThuoc(
    DonThuocDTO don,
    DefaultTableModel donModel,
    int row
  ) {
    CTHDDonThuocSaved result = new CTHDDonThuocSaved();
    try {
      String maThuoc = String.valueOf(donModel.getValueAt(row, 0));
      int soLuong = Integer.parseInt(
        String.valueOf(donModel.getValueAt(row, 2))
      );
      String lieuDung = String.valueOf(donModel.getValueAt(row, 3));
      String cachDung = String.valueOf(donModel.getValueAt(row, 4));

      CTDonThuocDTO ct = new CTDonThuocDTO();
      ct.setMaCTDonThuoc(generateMaCTDonThuoc(row));
      ct.setMaDonThuoc(don.getMaDonThuoc());
      ct.setMaThuoc(maThuoc);
      ct.setSoLuong(soLuong);
      ct.setLieuDung(lieuDung);
      ct.setCachDung(cachDung);

      if (!ctDonThuocBUS.add(ct)) {
        result.ok = false;
        result.message = "Lưu chi tiết đơn thuốc thất bại ở dòng " + (row + 1);
        return result;
      }
      result.ok = true;
      return result;
    } catch (Exception ex) {
      result.ok = false;
      result.message = "Dữ liệu thuốc không hợp lệ ở dòng " + (row + 1);
      return result;
    }
  }

  private void addFormRow(
    JPanel form,
    GridBagConstraints gbc,
    int row,
    String label,
    java.awt.Component field
  ) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    form.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    form.add(field, gbc);
  }

  private void wrapTextArea(JTextArea area) {
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
    area.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
  }

  private void applyTextAreaPlaceholder(JTextArea area, String placeholder) {
    area.setForeground(new Color(148, 163, 184));
    area.setText(placeholder);
    area.addFocusListener(
      new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
          if (placeholder.equals(area.getText())) {
            area.setText("");
            area.setForeground(new Color(15, 23, 42));
          }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
          if (area.getText() == null || area.getText().trim().isEmpty()) {
            area.setForeground(new Color(148, 163, 184));
            area.setText(placeholder);
          }
        }
      }
    );
  }

  private String getActualNoteText(String rawText) {
    if (rawText == null) {
      return "";
    }
    String value = rawText.trim();
    return GHI_CHU_PLACEHOLDER.equals(value) ? "" : value;
  }

  private void applyReadOnlyStyle(CustomTextField field) {
    field.setEditable(false);
    field.setEnabled(false);
    field.setBackground(new Color(241, 245, 249));
    field.setForeground(new Color(71, 85, 105));
  }

  private HoSoBenhAnDTO getSelectedRecord() {
    if (table == null) {
      return null;
    }
    int row = table.getSelectedRow();
    if (row < 0) {
      return null;
    }
    String maHoSo = String.valueOf(
      model.getValueAt(table.convertRowIndexToModel(row), 0)
    );
    if (maHoSo.startsWith(HO_SO_CHUA_TAO_PREFIX)) {
      HoSoBenhAnDTO pending = new HoSoBenhAnDTO();
      int modelRow = table.convertRowIndexToModel(row);
      pending.setMaHoSo("");
      pending.setMaLichKham(String.valueOf(model.getValueAt(modelRow, 1)));
      pending.setHoTen("");
      pending.setSoDienThoai("");
      pending.setCCCD("");
      pending.setDiaChi("");
      pending.setGioiTinh("Nam");
      pending.setNgaySinh(new java.sql.Date(new Date().getTime()));
      pending.setNgayKham(new java.sql.Date(new Date().getTime()));
      pending.setTrangThai(StatusNormalizer.CHO_KHAM);
      return pending;
    }
    return hoSoBenhAnBUS.getById(maHoSo);
  }

  private void loadData() {
    model.setRowCount(0);
    String maBacSi = resolveCurrentDoctorId();
    ArrayList<HoSoBenhAnDTO> dsHoSo = hoSoBenhAnBUS.getAll();
    Set<String> maLichDaCoHoSo = new HashSet<>();

    for (HoSoBenhAnDTO hoSo : dsHoSo) {
      if (hoSo.getMaLichKham() != null) {
        maLichDaCoHoSo.add(hoSo.getMaLichKham());
      }
      if (!isVisibleByLinkedScheduleStatus(hoSo)) {
        continue;
      }
      if (
        maBacSi != null &&
        !maBacSi.trim().isEmpty() &&
        hoSo.getMaBacSi() != null &&
        !maBacSi.equals(hoSo.getMaBacSi())
      ) {
        continue;
      }

      model.addRow(
        new Object[] {
          hoSo.getMaHoSo(),
          hoSo.getMaLichKham(),
          hoSo.getHoTen(),
          hoSo.getSoDienThoai(),
          hoSo.getNgayKham(),
          hoSo.getMaBacSi(),
          hoSo.getTrangThai(),
          hoSo.getChanDoan(),
        }
      );
    }

    ArrayList<LichKhamDTO> dsLich =
      maBacSi == null || maBacSi.trim().isEmpty()
        ? lichKhamBUS.getAll()
        : lichKhamBUS.getByMaBacSi(maBacSi);
    for (LichKhamDTO lich : dsLich) {
      if (lich == null || lich.getMaLichKham() == null) {
        continue;
      }
      String trangThaiLich = StatusNormalizer.normalizeLichKhamStatus(
        lich.getTrangThai()
      );
      if (!StatusNormalizer.DA_XAC_NHAN.equals(trangThaiLich)) {
        continue;
      }
      if (maLichDaCoHoSo.contains(lich.getMaLichKham())) {
        continue;
      }

      model.addRow(
        new Object[] {
          HO_SO_CHUA_TAO_PREFIX + lich.getMaLichKham(),
          lich.getMaLichKham(),
          "",
          "",
          lich.getThoiGianBatDau(),
          lich.getMaBacSi(),
          StatusNormalizer.CHO_KHAM,
          "",
        }
      );
    }
    updateKeHoSoButtonState();
  }

  private boolean isVisibleByLinkedScheduleStatus(HoSoBenhAnDTO hoSo) {
    if (hoSo == null) {
      return false;
    }
    String maLich = hoSo.getMaLichKham();
    if (maLich == null || maLich.trim().isEmpty()) {
      return true;
    }

    LichKhamDTO lich = lichKhamBUS.getById(maLich);
    if (lich == null) {
      return true;
    }

    String trangThaiLich = StatusNormalizer.normalizeLichKhamStatus(
      lich.getTrangThai()
    );
    return (
      StatusNormalizer.DA_XAC_NHAN.equals(trangThaiLich) ||
      StatusNormalizer.DANG_KHAM.equals(trangThaiLich) ||
      StatusNormalizer.HOAN_THANH.equals(trangThaiLich)
    );
  }

  private void updateKeHoSoButtonState() {
    if (btnKeHoSo == null) {
      return;
    }
    if (!coQuyenKeHoSo) {
      btnKeHoSo.setEnabled(false);
      btnKeHoSo.setToolTipText("Bạn không có quyền kê/cập nhật hồ sơ bệnh án");
      return;
    }
    int row = table == null ? -1 : table.getSelectedRow();
    if (row < 0) {
      btnKeHoSo.setEnabled(true);
      btnKeHoSo.setToolTipText("Kê hồ sơ bệnh án cho bệnh nhân được chọn");
      return;
    }
    int modelRow = table.convertRowIndexToModel(row);
    String trangThai = String.valueOf(model.getValueAt(modelRow, 6));
    boolean daKham = StatusNormalizer.DA_KHAM.equals(
      StatusNormalizer.normalizeHoSoStatus(trangThai)
    );
    btnKeHoSo.setEnabled(!daKham);
    btnKeHoSo.setToolTipText(
      daKham
        ? "Hồ sơ đã ĐÃ KHÁM, không thể kê lại"
        : "Kê hồ sơ bệnh án cho bệnh nhân được chọn"
    );
  }

  private String resolveCurrentDoctorId() {
    String maBacSi = Session.getCurrentBacSiID();
    if (maBacSi != null && !maBacSi.trim().isEmpty()) {
      return maBacSi;
    }
    String email = Session.getCurrentUserEmail();
    if (email != null && !email.trim().isEmpty()) {
      BacSiDTO bs = bacSiBUS.getByEmail(email);
      if (bs != null) {
        Session.setCurrentBacSiID(bs.getMaBacSi());
        return bs.getMaBacSi();
      }
    }
    return null;
  }

  private String generateMaHoSo() {
    return "HS" + System.currentTimeMillis();
  }

  private String generateMaCTDonThuoc(int row) {
    return "CTDT" + (System.currentTimeMillis() % 1000000000L) + row;
  }

  private String safe(String text) {
    return text == null ? "" : text;
  }

  private String extractDrugCode(String value) {
    if (value == null) {
      return "";
    }
    int idx = value.indexOf(" - ");
    return idx > 0 ? value.substring(0, idx).trim() : value.trim();
  }

  private String extractDrugName(String value) {
    if (value == null) {
      return "";
    }
    int idx = value.indexOf(" - ");
    return idx > 0 ? value.substring(idx + 3).trim() : "";
  }

  private String validateMedicalForm(
    String hoTen,
    String sdt,
    String cccd,
    Date ngaySinh
  ) {
    if (hoTen == null || hoTen.trim().isEmpty()) {
      return "Họ tên bệnh nhân không được để trống.";
    }
    if (sdt == null || sdt.trim().isEmpty()) {
      return "Số điện thoại không được để trống.";
    }
    if (!sdt.matches("^0\\d{9}$")) {
      return "Số điện thoại không hợp lệ. Vui lòng nhập đúng 10 số và bắt đầu bằng số 0.";
    }
    if (cccd == null || cccd.trim().isEmpty()) {
      return "CCCD không được để trống.";
    }
    if (!cccd.matches("^\\d{9,12}$")) {
      return "CCCD không hợp lệ. Vui lòng nhập từ 9 đến 12 chữ số.";
    }
    if (ngaySinh == null) {
      return "Ngày sinh không được để trống.";
    }
    Date homNay = new Date();
    if (ngaySinh.after(homNay)) {
      return "Ngày sinh không hợp lệ. Ngày sinh không được lớn hơn ngày hiện tại.";
    }
    return null;
  }

  private void apDungPhanQuyenHanhDong() {
    coQuyenXem = Session.coMotTrongCacQuyen("HOSO_XEM");
    coQuyenKeHoSo = Session.coMotTrongCacQuyen("HOSO_THEM", "HOSO_SUA");

    if (btnKeHoSo != null) btnKeHoSo.setVisible(coQuyenKeHoSo);
    if (btnTaiLai != null) btnTaiLai.setVisible(coQuyenXem);
    if (table != null) table.setEnabled(coQuyenXem);
  }

  private static class CTHDDonThuocSaved {

    private boolean ok;
    private String message;
  }
}
