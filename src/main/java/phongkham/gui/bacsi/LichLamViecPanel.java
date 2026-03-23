package phongkham.gui.bacsi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class LichLamViecPanel extends BasePanel {

  private final LichLamViecBUS lichLamViecBUS = new LichLamViecBUS();
  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] { "Mã lịch", "Ngày làm", "Ca", "Trạng thái" },
    0
  );

  private JSpinner spNgayLam;
  private JComboBox<String> cbCa;
  private JButton btnDangKy;
  private JButton btnTaiLai;

  @Override
  protected void init() {
    add(
      UIUtils.createSection("Lịch làm việc", buildTableSection()),
      BorderLayout.CENTER
    );

    loadData();
  }

  private JPanel buildTableSection() {
    JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setOpaque(false);

    JPanel topBar = new JPanel(new BorderLayout(8, 0));
    topBar.setOpaque(false);

    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    form.setOpaque(false);

    spNgayLam = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        null,
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgayLam.setEditor(new JSpinner.DateEditor(spNgayLam, "yyyy-MM-dd"));
    UIUtils.fixedSize(spNgayLam, 130, 32);
    cbCa = new JComboBox<>(new String[] { "Sang", "Chieu", "Toi" });
    UIUtils.fixedSize(cbCa, 90, 32);

    btnDangKy = UIUtils.primaryButton("Đăng ký ca");
    btnTaiLai = UIUtils.ghostButton("Tải lại");
    btnDangKy.addActionListener(e -> registerShift());
    btnTaiLai.addActionListener(e -> loadData());

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    actions.setOpaque(false);
    actions.add(btnTaiLai);
    actions.add(btnDangKy);

    form.add(new javax.swing.JLabel("Ngày làm"));
    form.add(spNgayLam);
    form.add(new javax.swing.JLabel("Ca"));
    form.add(cbCa);

    topBar.add(form, BorderLayout.WEST);
    topBar.add(actions, BorderLayout.EAST);

    JTable table = new JTable(model);
    UIUtils.styleTable(table);

    panel.add(topBar, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  private void registerShift() {
    String maBacSi = resolveCurrentDoctorId();
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      DialogHelper.warn(this, "Không xác định được bác sĩ hiện tại.");
      return;
    }

    String ngay = new SimpleDateFormat("yyyy-MM-dd").format(
      (Date) spNgayLam.getValue()
    );
    String ca = normalizeCa(String.valueOf(cbCa.getSelectedItem()));

    LichLamViecDTO lich = new LichLamViecDTO();
    lich.setMaLichLam(lichLamViecBUS.generateMaLichLam());
    lich.setMaBacSi(maBacSi);
    lich.setNgayLam(ngay);
    lich.setCaLam(ca);
    lich.setTrangThai(StatusNormalizer.CHO_DUYET);

    if (!lichLamViecBUS.add(lich)) {
      DialogHelper.error(
        this,
        "Đăng ký lịch làm việc thất bại (có thể bị trùng ca)."
      );
      return;
    }

    DialogHelper.info(this, "Đăng ký lịch làm việc thành công.");
    loadData();
  }

  private void loadData() {
    model.setRowCount(0);
    String maBacSi = resolveCurrentDoctorId();
    ArrayList<LichLamViecDTO> dsLich = (maBacSi == null ||
      maBacSi.trim().isEmpty())
      ? lichLamViecBUS.getAll()
      : lichLamViecBUS.getByBacSi(maBacSi);

    for (LichLamViecDTO lich : dsLich) {
      model.addRow(
        new Object[] {
          lich.getMaLichLam(),
          lich.getNgayLam(),
          toDisplayCa(lich.getCaLam()),
          lich.getTrangThai(),
        }
      );
    }
  }

  private String normalizeCa(String caDisplay) {
    if (caDisplay == null) {
      return "";
    }
    if ("Tối".equalsIgnoreCase(caDisplay)) {
      return "Toi";
    }
    return caDisplay;
  }

  private String toDisplayCa(String caDb) {
    if ("Toi".equalsIgnoreCase(caDb)) {
      return "Tối";
    }
    return caDb;
  }

  private String resolveCurrentDoctorId() {
    String maBacSi = Session.getCurrentBacSiID();
    if (maBacSi != null && !maBacSi.trim().isEmpty()) {
      return maBacSi;
    }

    String email = Session.getCurrentUserEmail();
    if (email != null && !email.trim().isEmpty()) {
      BacSiDTO byEmail = bacSiBUS.getByEmail(email);
      if (byEmail != null) {
        Session.setCurrentBacSiID(byEmail.getMaBacSi());
        return byEmail.getMaBacSi();
      }
    }

    String username = Session.getCurrentUsername();
    if (username != null) {
      String digits = username.replaceAll("\\D", "");
      if (!digits.isEmpty()) {
        try {
          String candidate = String.format("BS%03d", Integer.parseInt(digits));
          BacSiDTO byId = bacSiBUS.getById(candidate);
          if (byId != null) {
            Session.setCurrentBacSiID(byId.getMaBacSi());
            return byId.getMaBacSi();
          }
        } catch (NumberFormatException ex) {}
      }
    }

    return null;
  }
}
