package phongkham.gui.guest;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.toedter.calendar.IDateEvaluator;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.GoiDichVuBUS;
import phongkham.BUS.HoSoBenhAnBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.LichKhamDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.CustomTextField;
import phongkham.gui.common.components.RoundedPanel;

public class DatLichPanel extends BasePanel {

  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();
  private final LichLamViecBUS lichLamViecBUS = new LichLamViecBUS();
  private final HoSoBenhAnBUS hoSoBenhAnBUS = new HoSoBenhAnBUS();

  private final ArrayList<BacSiDTO> allDoctors = new ArrayList<>();
  private final ArrayList<GoiDichVuDTO> allPackages = new ArrayList<>();
  private final Map<String, BacSiDTO> visibleDoctors = new LinkedHashMap<>();
  private final Map<String, Boolean> packageAvailabilityByLabel =
    new LinkedHashMap<>();
  private final Map<String, SlotState> slotStateByLabel = new LinkedHashMap<>();
  private final Map<String, String> slotHintByLabel = new LinkedHashMap<>();
  private final Map<String, List<TimeRange>> approvedShiftCache =
    new HashMap<>();
  private final Map<String, List<TimeRange>> bookedRangesCache =
    new HashMap<>();
  private final Map<String, Boolean> hasAvailableSlotCache = new HashMap<>();
  private final Map<String, List<String>> validDatesByDoctorCache =
    new HashMap<>();
  private final List<String> currentValidExamDates = new ArrayList<>();
  private final DoctorScheduleDateEvaluator examDateEvaluator =
    new DoctorScheduleDateEvaluator();

  private static final int DEFAULT_GOI_DURATION_MINUTES = 30;
  private static final int SLOT_STEP_MINUTES = 30;
  private static final DateTimeFormatter TIME_FORMATTER =
    DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter DATETIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private JComboBox<String> cbBacSi;
  private JComboBox<String> cbKhungGio;
  private JComboBox<String> cbGoiKham;
  private JComboBox<String> cbThanhToan;
  private CustomTextField txtHoTen;
  private CustomTextField txtSoDienThoai;
  private CustomTextField txtCCCD;
  private JSpinner spNgaySinh;
  private JDateChooser spNgayKham;
  private JButton btnTraCuu;
  private JButton btnLamMoi;
  private JButton btnXacNhanDatLich;
  private BookingReceipt lastReceipt;
  private boolean isRefreshingUI = false;
  private String lastSelectableSlotLabel;
  private String lastSelectablePackageLabel;
  private boolean coQuyenDatLich = true;
  private boolean coQuyenTraCuu = true;

  @Override
  protected void init() {
    add(createLayout(), BorderLayout.CENTER);
  }

  private JPanel createLayout() {
    JPanel wrap = new JPanel(new GridLayout(1, 2, 14, 14));
    wrap.setOpaque(false);

    RoundedPanel info = new RoundedPanel(20);
    info.setLayout(new BorderLayout());
    info.setBackground(UIConstants.BG_SURFACE);
    info.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
    info.add(
      new JLabel(
        "<html><span style='font-size:18px;font-weight:bold;'>Hướng dẫn quy trình 3 bước & Thông tin liên hệ</span><br/><span style='color:#64748B'>Thực hiện theo các bước bên dưới để đặt lịch nhanh và chính xác.</span></html>"
      ),
      BorderLayout.NORTH
    );

    info.add(buildGuideAndContactPanel(), BorderLayout.CENTER);

    wrap.add(info);
    wrap.add(UIUtils.createSection("Form đặt lịch", createForm()));
    return wrap;
  }

  private JPanel createForm() {
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    txtHoTen = (CustomTextField) UIUtils.roundedTextField("Nhập họ tên", 18);
    txtSoDienThoai = (CustomTextField) UIUtils.roundedTextField(
      "Ví dụ: 09xxxxxxxx",
      18
    );
    txtCCCD = (CustomTextField) UIUtils.roundedTextField(
      "Nhập CCCD (9-12 số)",
      18
    );
    spNgaySinh = new JSpinner(
      new SpinnerDateModel(
        new Date(),
        null,
        new Date(),
        java.util.Calendar.DAY_OF_MONTH
      )
    );
    spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));
    cbBacSi = new JComboBox<>();
    spNgayKham = new JDateChooser(new Date());
    spNgayKham.setDateFormatString("dd/MM/yyyy");
    spNgayKham
      .getJCalendar()
      .getDayChooser()
      .addDateEvaluator(examDateEvaluator);
    cbKhungGio = new JComboBox<>();
    cbKhungGio.setRenderer(
      new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(
          JList<?> list,
          Object value,
          int index,
          boolean isSelected,
          boolean cellHasFocus
        ) {
          JLabel label = (JLabel) super.getListCellRendererComponent(
            list,
            value,
            index,
            isSelected,
            cellHasFocus
          );
          String text = value == null ? "" : String.valueOf(value);
          SlotState state = slotStateByLabel.get(text);
          if (!isSelected && state != null && state != SlotState.AVAILABLE) {
            label.setForeground(new Color(148, 163, 184));
          }
          return label;
        }
      }
    );

    cbGoiKham = new JComboBox<>();
    cbGoiKham.setRenderer(
      new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(
          JList<?> list,
          Object value,
          int index,
          boolean isSelected,
          boolean cellHasFocus
        ) {
          JLabel label = (JLabel) super.getListCellRendererComponent(
            list,
            value,
            index,
            isSelected,
            cellHasFocus
          );
          String text = value == null ? "" : String.valueOf(value);
          Boolean available = packageAvailabilityByLabel.get(text);
          if (!isSelected && Boolean.FALSE.equals(available)) {
            label.setForeground(new Color(148, 163, 184));
          }
          return label;
        }
      }
    );
    cbThanhToan = new JComboBox<>(new String[] { "Tiền mặt", "Chuyển khoản" });
    cbBacSi.setToolTipText(
      "Chỉ hiển thị bác sĩ theo khoa gói khám, có lịch làm việc DA_DUYET và còn slot trống."
    );

    int row = 0;
    addRow(form, gbc, row++, "Họ và tên", txtHoTen);
    addRow(form, gbc, row++, "Số điện thoại", txtSoDienThoai);
    addRow(form, gbc, row++, "CCCD", txtCCCD);
    addRow(form, gbc, row++, "Ngày sinh", spNgaySinh);
    addRow(form, gbc, row++, "Bác sĩ", cbBacSi);
    addRow(form, gbc, row++, "Ngày khám", spNgayKham);
    addRow(form, gbc, row++, "Khung giờ", cbKhungGio);
    addRow(form, gbc, row++, "Gói khám", cbGoiKham);
    addRow(form, gbc, row++, "Thanh toán", cbThanhToan);

    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;

    JPanel actions = new JPanel(new BorderLayout(0, 8));
    actions.setOpaque(false);

    btnTraCuu = UIUtils.ghostButton("Tra cứu");
    btnLamMoi = UIUtils.ghostButton("Làm mới");
    btnXacNhanDatLich = UIUtils.primaryButton("Xác nhận đặt lịch");

    JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    bottomRow.setOpaque(false);
    bottomRow.add(btnLamMoi);
    bottomRow.add(btnXacNhanDatLich);

    actions.add(btnTraCuu, BorderLayout.NORTH);
    actions.add(bottomRow, BorderLayout.SOUTH);

    btnTraCuu.addActionListener(e -> openLookupCccdDialog());
    btnLamMoi.addActionListener(e -> resetForm());
    btnXacNhanDatLich.addActionListener(e -> createBookingStub());

    apDungPhanQuyenHanhDong();

    // Load data after action components are initialized to avoid early null access.
    loadComboboxData();
    bindFilterEvents();

    form.add(actions, gbc);
    return form;
  }

  private JPanel buildGuideAndContactPanel() {
    JPanel wrapper = new JPanel(new BorderLayout(0, 12));
    wrapper.setOpaque(false);

    RoundedPanel guide = new RoundedPanel(16);
    guide.setBackground(new Color(219, 234, 254));
    guide.setLayout(new BorderLayout(0, 8));
    guide.setBorder(
      javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
    );
    guide.add(
      new JLabel("<html><b>Quy trình 3 bước</b></html>"),
      BorderLayout.NORTH
    );

    JPanel guideRows = new JPanel(new GridLayout(3, 1, 0, 8));
    guideRows.setOpaque(false);
    guideRows.add(
      new JLabel(
        "<html><b>Bước 1:</b> Điền đầy đủ thông tin đặt lịch ở form bên phải.</html>"
      )
    );
    guideRows.add(
      new JLabel(
        "<html><b>Bước 2:</b> Bấm nút Xác nhận đặt lịch để tạo mã lịch.</html>"
      )
    );
    guideRows.add(
      new JLabel(
        "<html><b>Bước 3:</b> Đến phòng khám và đọc mã lịch tại quầy lễ tân.</html>"
      )
    );
    guide.add(guideRows, BorderLayout.CENTER);

    RoundedPanel contact = new RoundedPanel(16);
    contact.setBackground(new Color(241, 245, 249));
    contact.setLayout(new BorderLayout(0, 8));
    contact.setBorder(
      javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
    );
    contact.add(
      new JLabel("<html><b>Thông tin liên hệ</b></html>"),
      BorderLayout.NORTH
    );

    JPanel contactRows = new JPanel(new GridLayout(4, 1, 0, 6));
    contactRows.setOpaque(false);
    contactRows.add(new JLabel("Hotline hỗ trợ: 1900 6868"));
    contactRows.add(
      new JLabel("Giờ làm việc: 07:00 - 17:30 (Thứ 2 - Chủ nhật)")
    );
    contactRows.add(new JLabel("Địa chỉ: 123 Nguyễn Trãi, Quận 5, TP.HCM"));
    contactRows.add(new JLabel("Email: hotro@phongkhamdakhoa.vn"));
    contact.add(contactRows, BorderLayout.CENTER);

    wrapper.add(guide, BorderLayout.NORTH);
    wrapper.add(contact, BorderLayout.CENTER);
    return wrapper;
  }

  private void addRow(
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

  private void loadComboboxData() {
    clearSlotCaches();
    isRefreshingUI = true;
    cbGoiKham.removeAllItems();
    cbKhungGio.removeAllItems();
    cbBacSi.removeAllItems();
    packageAvailabilityByLabel.clear();
    visibleDoctors.clear();
    allDoctors.clear();
    allPackages.clear();

    ArrayList<BacSiDTO> dsBacSi = bacSiBUS.getAll();
    for (BacSiDTO bacSi : dsBacSi) {
      allDoctors.add(bacSi);
      String label = bacSi.getMaBacSi() + " - " + bacSi.getHoTen();
      visibleDoctors.put(label, bacSi);
      cbBacSi.addItem(label);
    }

    ArrayList<GoiDichVuDTO> dsGoi = goiDichVuBUS.getAll();
    for (GoiDichVuDTO goi : dsGoi) {
      allPackages.add(goi);
      String label = goi.getMaGoi() + " - " + goi.getTenGoi();
      cbGoiKham.addItem(label);
      packageAvailabilityByLabel.put(label, Boolean.TRUE);
    }

    if (cbBacSi.getItemCount() == 0) {
      cbBacSi.addItem("-- Chưa có bác sĩ --");
      updateDoctorSelectionUX(false, "");
      isRefreshingUI = false;
      return;
    }

    cbBacSi.setSelectedIndex(0);
    isRefreshingUI = false;
    refreshSelectionState();
  }

  private void bindFilterEvents() {
    cbGoiKham.addActionListener(e -> {
      if (isRefreshingUI) {
        return;
      }
      if (!enforceSelectablePackage()) {
        return;
      }
      refreshTimeSlotsForSelectedDoctor();
    });
    cbBacSi.addActionListener(e -> {
      if (isRefreshingUI) {
        return;
      }
      clearSlotCaches();
      refreshValidExamDatesForSelectedDoctor();
      refreshPackageAvailabilityForSelectedDoctor();
      refreshTimeSlotsForSelectedDoctor();
    });
    cbKhungGio.addActionListener(e -> {
      if (isRefreshingUI) {
        return;
      }
      if (!enforceSelectableSlot()) {
        return;
      }
      updateSlotSelectionUX();
    });
    spNgayKham.addPropertyChangeListener("date", e -> {
      if (isRefreshingUI) {
        return;
      }
      enforceValidExamDateSelection();
      clearSlotCaches();
      refreshPackageAvailabilityForSelectedDoctor();
      refreshTimeSlotsForSelectedDoctor();
    });
  }

  private void refreshSelectionState() {
    refreshValidExamDatesForSelectedDoctor();
    refreshPackageAvailabilityForSelectedDoctor();
    refreshTimeSlotsForSelectedDoctor();
  }

  private void updateDoctorSelectionUX(
    boolean hasMatchingDoctor,
    String ngayKham
  ) {
    String ngayHienThi = (ngayKham == null || ngayKham.isBlank())
      ? "(chưa rõ ngày)"
      : ngayKham;

    String tooltip =
      "<html>Danh sách bác sĩ hiện có.<br/>" +
      "- Sau khi chọn bác sĩ, hệ thống sẽ tải ngày khám hợp lệ.<br/>" +
      "- Ngày đang xét: " +
      ngayHienThi +
      "<br/>- Khung giờ được tải theo lịch DA_DUYET và slot trống" +
      (!hasMatchingDoctor
        ? "<br/><br/><b>Hiện không có bác sĩ, vui lòng kiểm tra dữ liệu.</b>"
        : "") +
      "</html>";

    cbBacSi.setToolTipText(tooltip);
    if (btnXacNhanDatLich != null) {
      btnXacNhanDatLich.setEnabled(hasMatchingDoctor);
      btnXacNhanDatLich.setToolTipText(
        hasMatchingDoctor
          ? "Xác nhận đặt lịch với bác sĩ hiện tại"
          : "Không thể đặt lịch vì chưa có bác sĩ phù hợp"
      );
    }
  }

  private GoiDichVuDTO getSelectedPackage() {
    String maGoi = extractId(String.valueOf(cbGoiKham.getSelectedItem()));
    for (GoiDichVuDTO goi : allPackages) {
      if (equalsIgnoreCase(goi.getMaGoi(), maGoi)) {
        return goi;
      }
    }
    return null;
  }

  private boolean equalsIgnoreCase(String a, String b) {
    if (a == null || b == null) {
      return false;
    }
    return normalize(a).equals(normalize(b));
  }

  private void refreshTimeSlotsForSelectedDoctor() {
    isRefreshingUI = true;
    try {
      cbKhungGio.removeAllItems();
      slotStateByLabel.clear();
      slotHintByLabel.clear();
      lastSelectableSlotLabel = null;

      GoiDichVuDTO goi = getSelectedPackage();
      BacSiDTO bacSi = getSelectedDoctor();
      if (goi == null || bacSi == null) {
        cbKhungGio.addItem("-- Chọn bác sĩ và gói khám để xem khung giờ --");
        if (btnXacNhanDatLich != null) {
          btnXacNhanDatLich.setEnabled(false);
        }
        cbKhungGio.setToolTipText("Chưa đủ thông tin để sinh khung giờ.");
        return;
      }

      int durationMinutes = parseDurationMinutes(goi);
      Date examDate = getSelectedExamDate();
      if (examDate == null) {
        cbKhungGio.addItem("-- Vui lòng chọn ngày khám hợp lệ --");
        if (btnXacNhanDatLich != null) {
          btnXacNhanDatLich.setEnabled(false);
        }
        return;
      }
      String ngayKham = new SimpleDateFormat("yyyy-MM-dd").format(examDate);

      if (!currentValidExamDates.contains(ngayKham)) {
        cbKhungGio.addItem("-- Ngày khám không hợp lệ với bác sĩ đã chọn --");
        if (btnXacNhanDatLich != null) {
          btnXacNhanDatLich.setEnabled(false);
        }
        return;
      }

      List<SlotOption> options = buildSlotsForDoctor(
        bacSi.getMaBacSi(),
        ngayKham,
        durationMinutes
      );

      if (options.isEmpty()) {
        cbKhungGio.addItem("-- Không có khung giờ trong ngày --");
        if (btnXacNhanDatLich != null) {
          btnXacNhanDatLich.setEnabled(false);
        }
        return;
      }

      int selectedIndex = -1;
      for (int i = 0; i < options.size(); i++) {
        SlotOption option = options.get(i);
        String display = option.toDisplayText();
        cbKhungGio.addItem(display);
        slotStateByLabel.put(display, option.state);
        slotHintByLabel.put(display, option.hint);
        if (selectedIndex < 0 && option.state == SlotState.AVAILABLE) {
          selectedIndex = i;
        }
      }

      cbKhungGio.setSelectedIndex(selectedIndex >= 0 ? selectedIndex : 0);
      if (selectedIndex >= 0) {
        lastSelectableSlotLabel = String.valueOf(cbKhungGio.getSelectedItem());
      }
      updateSlotSelectionUX();
      cbKhungGio.setToolTipText(
        "<html>Màu xám: slot không khả dụng.<br/>Lý do gồm: ngoài ca làm việc hoặc đã có lịch khám.</html>"
      );
    } finally {
      isRefreshingUI = false;
    }
  }

  private void updateSlotSelectionUX() {
    String label = String.valueOf(cbKhungGio.getSelectedItem());
    SlotState state = slotStateByLabel.get(label);
    boolean available =
      state == SlotState.AVAILABLE && getSelectedDoctor() != null;
    if (btnXacNhanDatLich == null) {
      return;
    }
    btnXacNhanDatLich.setEnabled(available);

    String hint = slotHintByLabel.get(label);
    if (hint == null || hint.isBlank()) {
      hint = available
        ? "Xác nhận đặt lịch với khung giờ đã chọn"
        : "Khung giờ hiện tại không khả dụng";
    }
    btnXacNhanDatLich.setToolTipText(hint);
  }

  private BacSiDTO getSelectedDoctor() {
    String key = String.valueOf(cbBacSi.getSelectedItem());
    return visibleDoctors.get(key);
  }

  private void refreshPackageAvailabilityForSelectedDoctor() {
    BacSiDTO doctor = getSelectedDoctor();
    Date examDate = getSelectedExamDate();
    String ngayKham =
      examDate == null
        ? ""
        : new SimpleDateFormat("yyyy-MM-dd").format(examDate);

    if (doctor == null) {
      return;
    }

    isRefreshingUI = true;
    try {
      cbGoiKham.removeAllItems();
      packageAvailabilityByLabel.clear();
      lastSelectablePackageLabel = null;
      int selectedIndex = -1;
      int currentIndex = 0;

      for (GoiDichVuDTO goi : allPackages) {
        if (!equalsIgnoreCase(doctor.getMaKhoa(), goi.getMaKhoa())) {
          continue;
        }

        String label = goi.getMaGoi() + " - " + goi.getTenGoi();
        int duration = parseDurationMinutes(goi);
        boolean available =
          currentValidExamDates.contains(ngayKham) &&
          hasAnyAvailableSlot(doctor.getMaBacSi(), ngayKham, duration);

        cbGoiKham.addItem(label);
        packageAvailabilityByLabel.put(label, available);
        if (selectedIndex < 0 && available) {
          selectedIndex = currentIndex;
          lastSelectablePackageLabel = label;
        }
        currentIndex++;
      }

      if (cbGoiKham.getItemCount() == 0) {
        cbGoiKham.addItem("-- Không có gói dịch vụ phù hợp khoa bác sĩ --");
        packageAvailabilityByLabel.put(
          "-- Không có gói dịch vụ phù hợp khoa bác sĩ --",
          Boolean.FALSE
        );
      } else {
        cbGoiKham.setSelectedIndex(selectedIndex >= 0 ? selectedIndex : 0);
        if (selectedIndex < 0) {
          lastSelectablePackageLabel = null;
        }
      }
    } finally {
      isRefreshingUI = false;
    }

    cbGoiKham.repaint();
  }

  private void refreshValidExamDatesForSelectedDoctor() {
    currentValidExamDates.clear();
    BacSiDTO doctor = getSelectedDoctor();
    if (doctor == null) {
      updateDoctorSelectionUX(false, "");
      return;
    }

    List<String> validDates = getValidExamDatesByDoctor(doctor.getMaBacSi());
    currentValidExamDates.addAll(validDates);
    applyValidDatesToChooser();
    enforceValidExamDateSelection();
    String selectedNgay = new SimpleDateFormat("yyyy-MM-dd").format(
      getSelectedExamDateOrToday()
    );
    updateDoctorSelectionUX(!validDates.isEmpty(), selectedNgay);
  }

  private List<String> getValidExamDatesByDoctor(String maBacSi) {
    List<String> cached = validDatesByDoctorCache.get(maBacSi);
    if (cached != null) {
      return cached;
    }

    TreeSet<String> dates = new TreeSet<>();
    ArrayList<LichLamViecDTO> schedules = lichLamViecBUS.getByBacSi(maBacSi);
    for (LichLamViecDTO llv : schedules) {
      String status = StatusNormalizer.normalizeLichLamViecStatus(
        llv.getTrangThai()
      );
      if (!StatusNormalizer.DA_DUYET.equals(status)) {
        continue;
      }
      if (llv.getNgayLam() != null && !llv.getNgayLam().isBlank()) {
        dates.add(llv.getNgayLam());
      }
    }

    List<String> ketQua = new ArrayList<>(dates);
    validDatesByDoctorCache.put(maBacSi, ketQua);
    return ketQua;
  }

  private void enforceValidExamDateSelection() {
    if (currentValidExamDates.isEmpty()) {
      return;
    }

    String selectedNgay = new SimpleDateFormat("yyyy-MM-dd").format(
      getSelectedExamDateOrToday()
    );
    if (currentValidExamDates.contains(selectedNgay)) {
      return;
    }

    String ngayGanNhat = pickPreferredValidDate();
    try {
      Date date = new SimpleDateFormat("yyyy-MM-dd").parse(ngayGanNhat);
      isRefreshingUI = true;
      spNgayKham.setDate(date);
    } catch (Exception ignored) {
    } finally {
      isRefreshingUI = false;
    }
  }

  private String pickPreferredValidDate() {
    if (currentValidExamDates.isEmpty()) {
      return null;
    }
    String homNay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    for (String ngay : currentValidExamDates) {
      if (ngay.compareTo(homNay) >= 0) {
        return ngay;
      }
    }
    return currentValidExamDates.get(currentValidExamDates.size() - 1);
  }

  private void applyValidDatesToChooser() {
    examDateEvaluator.setValidDateKeys(currentValidExamDates);
    spNgayKham.getJCalendar().getDayChooser().repaint();
  }

  private boolean enforceSelectableSlot() {
    String selectedLabel = String.valueOf(cbKhungGio.getSelectedItem());
    SlotState state = slotStateByLabel.get(selectedLabel);
    if (state == SlotState.AVAILABLE) {
      lastSelectableSlotLabel = selectedLabel;
      return true;
    }

    String fallback = findFirstAvailableSlotLabel();
    if (fallback == null) {
      return false;
    }

    isRefreshingUI = true;
    try {
      cbKhungGio.setSelectedItem(fallback);
      lastSelectableSlotLabel = fallback;
    } finally {
      isRefreshingUI = false;
    }
    return false;
  }

  private String findFirstAvailableSlotLabel() {
    if (lastSelectableSlotLabel != null) {
      SlotState prevState = slotStateByLabel.get(lastSelectableSlotLabel);
      if (prevState == SlotState.AVAILABLE) {
        return lastSelectableSlotLabel;
      }
    }
    for (int i = 0; i < cbKhungGio.getItemCount(); i++) {
      String label = String.valueOf(cbKhungGio.getItemAt(i));
      if (slotStateByLabel.get(label) == SlotState.AVAILABLE) {
        return label;
      }
    }
    return null;
  }

  private boolean enforceSelectablePackage() {
    String selectedLabel = String.valueOf(cbGoiKham.getSelectedItem());
    Boolean available = packageAvailabilityByLabel.get(selectedLabel);
    if (!Boolean.FALSE.equals(available)) {
      lastSelectablePackageLabel = selectedLabel;
      return true;
    }

    String fallback = findFirstAvailablePackageLabel();
    if (fallback == null) {
      return false;
    }

    isRefreshingUI = true;
    try {
      cbGoiKham.setSelectedItem(fallback);
      lastSelectablePackageLabel = fallback;
    } finally {
      isRefreshingUI = false;
    }
    return false;
  }

  private String findFirstAvailablePackageLabel() {
    if (lastSelectablePackageLabel != null) {
      Boolean prevAvailable = packageAvailabilityByLabel.get(
        lastSelectablePackageLabel
      );
      if (!Boolean.FALSE.equals(prevAvailable)) {
        return lastSelectablePackageLabel;
      }
    }
    for (int i = 0; i < cbGoiKham.getItemCount(); i++) {
      String label = String.valueOf(cbGoiKham.getItemAt(i));
      Boolean available = packageAvailabilityByLabel.get(label);
      if (!Boolean.FALSE.equals(available)) {
        return label;
      }
    }
    return null;
  }

  private GoiDichVuDTO findPackageByLabel(String label) {
    String maGoi = extractId(label);
    for (GoiDichVuDTO goi : allPackages) {
      if (equalsIgnoreCase(goi.getMaGoi(), maGoi)) {
        return goi;
      }
    }
    return null;
  }

  private List<SlotOption> buildSlotsForDoctor(
    String maBacSi,
    String ngayKham,
    int durationMinutes
  ) {
    List<TimeRange> approvedShiftRanges = getApprovedShiftRanges(
      maBacSi,
      ngayKham
    );
    List<TimeRange> bookedRanges = getBookedRanges(maBacSi, ngayKham);
    List<SlotOption> slots = new ArrayList<>();

    LocalTime displayStart = LocalTime.of(7, 0);
    LocalTime displayEnd = LocalTime.of(21, 0);
    if (!approvedShiftRanges.isEmpty()) {
      displayStart = approvedShiftRanges.get(0).start;
      displayEnd = approvedShiftRanges.get(0).end;
      for (TimeRange shift : approvedShiftRanges) {
        if (shift.start.isBefore(displayStart)) {
          displayStart = shift.start;
        }
        if (shift.end.isAfter(displayEnd)) {
          displayEnd = shift.end;
        }
      }
    }

    LocalTime cursor = displayStart;
    while (!cursor.plusMinutes(durationMinutes).isAfter(displayEnd)) {
      LocalTime slotEnd = cursor.plusMinutes(durationMinutes);

      if (!isCoveredByApprovedShift(cursor, slotEnd, approvedShiftRanges)) {
        slots.add(
          new SlotOption(
            cursor,
            slotEnd,
            SlotState.OUT_OF_SHIFT,
            "Bác sĩ không có ca làm trong khung giờ này"
          )
        );
      } else if (isOverlappedWithBooked(cursor, slotEnd, bookedRanges)) {
        slots.add(
          new SlotOption(
            cursor,
            slotEnd,
            SlotState.BOOKED,
            "Khung giờ này đã có bệnh nhân đăng ký"
          )
        );
      } else {
        slots.add(
          new SlotOption(
            cursor,
            slotEnd,
            SlotState.AVAILABLE,
            "Khung giờ còn trống"
          )
        );
      }

      cursor = cursor.plusMinutes(SLOT_STEP_MINUTES);
    }

    return slots;
  }

  private boolean hasAnyAvailableSlot(
    String maBacSi,
    String ngayKham,
    int durationMinutes
  ) {
    String cacheKey = maBacSi + "|" + ngayKham + "|" + durationMinutes;
    Boolean cached = hasAvailableSlotCache.get(cacheKey);
    if (cached != null) {
      return cached;
    }

    List<SlotOption> slots = buildSlotsForDoctor(
      maBacSi,
      ngayKham,
      durationMinutes
    );
    for (SlotOption option : slots) {
      if (option.state == SlotState.AVAILABLE) {
        hasAvailableSlotCache.put(cacheKey, Boolean.TRUE);
        return true;
      }
    }
    hasAvailableSlotCache.put(cacheKey, Boolean.FALSE);
    return false;
  }

  private List<TimeRange> getApprovedShiftRanges(
    String maBacSi,
    String ngayKham
  ) {
    String cacheKey = maBacSi + "|" + ngayKham;
    List<TimeRange> cached = approvedShiftCache.get(cacheKey);
    if (cached != null) {
      return cached;
    }

    ArrayList<LichLamViecDTO> schedules = lichLamViecBUS.getByBacSiAndNgay(
      maBacSi,
      ngayKham
    );
    List<TimeRange> ranges = new ArrayList<>();

    for (LichLamViecDTO llv : schedules) {
      String status = StatusNormalizer.normalizeLichLamViecStatus(
        llv.getTrangThai()
      );
      if (!StatusNormalizer.DA_DUYET.equals(status)) {
        continue;
      }
      TimeRange parsed = parseShiftRange(llv.getCaLam());
      if (parsed != null) {
        ranges.add(parsed);
      }
    }

    approvedShiftCache.put(cacheKey, ranges);
    return ranges;
  }

  private List<TimeRange> getBookedRanges(String maBacSi, String ngayKham) {
    String cacheKey = maBacSi + "|" + ngayKham;
    List<TimeRange> cached = bookedRangesCache.get(cacheKey);
    if (cached != null) {
      return cached;
    }

    List<TimeRange> booked = new ArrayList<>();
    ArrayList<LichKhamDTO> lichKhams = lichKhamBUS.getByBacSiAndNgay(
      maBacSi,
      ngayKham
    );
    for (LichKhamDTO lk : lichKhams) {
      String status = StatusNormalizer.normalizeLichKhamStatus(
        lk.getTrangThai()
      );
      if (StatusNormalizer.DA_HUY.equals(status)) {
        continue;
      }
      TimeRange range = parseAppointmentRange(
        lk.getThoiGianBatDau(),
        lk.getThoiGianKetThuc()
      );
      if (range != null) {
        booked.add(range);
      }
    }
    bookedRangesCache.put(cacheKey, booked);
    return booked;
  }

  private TimeRange parseShiftRange(String rawShift) {
    if (rawShift == null) {
      return null;
    }
    String value = rawShift.trim().replace(" ", "");
    if ("Sang".equalsIgnoreCase(value)) {
      value = "08:00-12:00";
    } else if ("Chieu".equalsIgnoreCase(value)) {
      value = "13:00-17:00";
    } else if ("Toi".equalsIgnoreCase(value) || "Tối".equalsIgnoreCase(value)) {
      value = "17:00-21:00";
    }

    String[] parts = value.split("-");
    if (parts.length != 2) {
      return null;
    }

    try {
      LocalTime start = LocalTime.parse(parts[0], TIME_FORMATTER);
      LocalTime end = LocalTime.parse(parts[1], TIME_FORMATTER);
      if (!start.isBefore(end)) {
        return null;
      }
      return new TimeRange(start, end);
    } catch (Exception ex) {
      return null;
    }
  }

  private TimeRange parseAppointmentRange(String start, String end) {
    if (start == null || end == null) {
      return null;
    }
    try {
      LocalDateTime startTime = LocalDateTime.parse(start, DATETIME_FORMATTER);
      LocalDateTime endTime = LocalDateTime.parse(end, DATETIME_FORMATTER);
      return new TimeRange(startTime.toLocalTime(), endTime.toLocalTime());
    } catch (Exception ex) {
      return null;
    }
  }

  private boolean isCoveredByApprovedShift(
    LocalTime slotStart,
    LocalTime slotEnd,
    List<TimeRange> approvedShiftRanges
  ) {
    for (TimeRange shift : approvedShiftRanges) {
      if (!slotStart.isBefore(shift.start) && !slotEnd.isAfter(shift.end)) {
        return true;
      }
    }
    return false;
  }

  private boolean isOverlappedWithBooked(
    LocalTime slotStart,
    LocalTime slotEnd,
    List<TimeRange> bookedRanges
  ) {
    for (TimeRange booked : bookedRanges) {
      if (slotStart.isBefore(booked.end) && slotEnd.isAfter(booked.start)) {
        return true;
      }
    }
    return false;
  }

  private int parseDurationMinutes(GoiDichVuDTO goi) {
    if (goi == null || goi.getThoiGianKham() == null) {
      return DEFAULT_GOI_DURATION_MINUTES;
    }

    String raw = goi.getThoiGianKham().trim();
    if (raw.isEmpty()) {
      return DEFAULT_GOI_DURATION_MINUTES;
    }

    String digitsOnly = raw.replaceAll("\\D", "");
    if (digitsOnly.isEmpty()) {
      return DEFAULT_GOI_DURATION_MINUTES;
    }

    try {
      int minutes = Integer.parseInt(digitsOnly);
      return minutes > 0 ? minutes : DEFAULT_GOI_DURATION_MINUTES;
    } catch (NumberFormatException ex) {
      return DEFAULT_GOI_DURATION_MINUTES;
    }
  }

  private void createBookingStub() {
    if (!coQuyenDatLich) {
      DialogHelper.warn(this, "Bạn không có quyền đặt lịch khám.");
      return;
    }

    if (
      txtHoTen.getText().trim().isEmpty() ||
      txtSoDienThoai.getText().trim().isEmpty()
    ) {
      DialogHelper.warn(this, "Vui lòng nhập đầy đủ họ tên và số điện thoại.");
      return;
    }

    String cccd = txtCCCD.getText().trim();
    if (!isValidCCCD(cccd)) {
      DialogHelper.warn(this, "CCCD không hợp lệ. Vui lòng nhập 9-12 chữ số.");
      return;
    }

    Date ngaySinh = (Date) spNgaySinh.getValue();
    if (!isValidBirthDate(ngaySinh)) {
      DialogHelper.warn(
        this,
        "Ngày sinh không hợp lệ. Vui lòng chọn ngày không vượt quá hiện tại."
      );
      return;
    }

    String maBacSi = extractId(String.valueOf(cbBacSi.getSelectedItem()));
    String maGoi = extractId(String.valueOf(cbGoiKham.getSelectedItem()));

    GoiDichVuDTO goiDaChon = getSelectedPackage();
    BacSiDTO bacSiDaChon = visibleDoctors.get(
      String.valueOf(cbBacSi.getSelectedItem())
    );

    if (goiDaChon == null) {
      DialogHelper.warn(this, "Vui lòng chọn gói khám hợp lệ.");
      return;
    }
    Boolean packageAvailable = packageAvailabilityByLabel.get(
      String.valueOf(cbGoiKham.getSelectedItem())
    );
    if (Boolean.FALSE.equals(packageAvailable)) {
      DialogHelper.warn(
        this,
        "Gói khám đang chọn không còn slot phù hợp với bác sĩ/ngày hiện tại."
      );
      return;
    }
    if (visibleDoctors.isEmpty() || bacSiDaChon == null) {
      DialogHelper.error(
        this,
        "Không có bác sĩ phù hợp với khoa của gói khám và ngày đã chọn."
      );
      return;
    }

    if (maBacSi.isEmpty() || maGoi.isEmpty()) {
      DialogHelper.warn(this, "Vui lòng chọn bác sĩ và gói khám hợp lệ.");
      return;
    }

    String khung = String.valueOf(cbKhungGio.getSelectedItem());
    String[] parts = khung.split("-");
    if (parts.length < 2) {
      DialogHelper.warn(this, "Khung giờ không hợp lệ.");
      return;
    }

    SlotState selectedState = slotStateByLabel.get(khung);
    if (selectedState != SlotState.AVAILABLE) {
      DialogHelper.warn(
        this,
        "Khung giờ đã chọn hiện không khả dụng. Vui lòng chọn khung giờ khác."
      );
      return;
    }

    Date ngay = getSelectedExamDate();
    if (ngay == null) {
      DialogHelper.warn(this, "Vui lòng chọn ngày khám hợp lệ.");
      return;
    }
    if (isDateBeforeToday(ngay)) {
      DialogHelper.warn(this, "Không thể đặt lịch cho ngày trong quá khứ.");
      return;
    }
    SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
    String ngayKham = dateOnly.format(ngay);

    if (!equalsIgnoreCase(bacSiDaChon.getMaKhoa(), goiDaChon.getMaKhoa())) {
      DialogHelper.error(
        this,
        "Bác sĩ không thuộc khoa của gói dịch vụ đã chọn. Vui lòng chọn lại."
      );
      return;
    }

    String batDau = ngayKham + " " + parts[0].trim() + ":00";
    String ketThuc = ngayKham + " " + parts[1].trim() + ":00";

    LichKhamDTO lich = new LichKhamDTO();
    lich.setMaLichKham(lichKhamBUS.generateMaLichKham());
    lich.setMaBacSi(maBacSi);
    lich.setMaGoi(maGoi);
    lich.setThoiGianBatDau(batDau);
    lich.setThoiGianKetThuc(ketThuc);
    lich.setTrangThai(StatusNormalizer.CHO_XAC_NHAN);
    lich.setMaDinhDanhTam(generateGuestCode());

    String result = lichKhamBUS.insert(lich);
    if (!normalize(result).contains("thanh cong")) {
      DialogHelper.error(this, result);
      return;
    }

    boolean taoHoSoCoBan = createInitialMedicalRecord(lich, ngaySinh);
    if (!taoHoSoCoBan) {
      lichKhamBUS.delete(lich.getMaLichKham());
      DialogHelper.error(
        this,
        "Đặt lịch thất bại do không thể tạo hồ sơ bệnh án ban đầu. Vui lòng thử lại."
      );
      return;
    }

    lastReceipt = new BookingReceipt(
      lich.getMaLichKham(),
      txtHoTen.getText().trim(),
      txtSoDienThoai.getText().trim(),
      cccd,
      new SimpleDateFormat("dd/MM/yyyy").format(ngaySinh),
      String.valueOf(cbBacSi.getSelectedItem()),
      String.valueOf(cbGoiKham.getSelectedItem()),
      batDau,
      String.valueOf(cbThanhToan.getSelectedItem())
    );

    File invoiceFile = createAutoInvoiceFile(lastReceipt.maLich);
    boolean exportOk = exportReceiptPdf(lastReceipt, invoiceFile);

    String message =
      "Đặt lịch thành công. Mã lịch: " +
      lich.getMaLichKham() +
      (exportOk
        ? "\nĐã tự động xuất hóa đơn: " + invoiceFile.getAbsolutePath()
        : "\nĐặt lịch thành công nhưng xuất hóa đơn tự động thất bại.");

    DialogHelper.info(this, message);
    resetForm();
    loadComboboxData();
  }

  private boolean createInitialMedicalRecord(
    LichKhamDTO lich,
    java.util.Date ngaySinh
  ) {
    if (lich == null || lich.getMaLichKham() == null) {
      return false;
    }

    HoSoBenhAnDTO existing = hoSoBenhAnBUS.getByMaLichKham(
      lich.getMaLichKham()
    );
    if (existing != null) {
      return true;
    }

    HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
    hs.setMaHoSo(generateUniqueMaHoSo());
    hs.setMaLichKham(lich.getMaLichKham());
    hs.setHoTen(txtHoTen.getText().trim());
    hs.setSoDienThoai(txtSoDienThoai.getText().trim());
    hs.setCCCD(txtCCCD.getText().trim());
    hs.setNgaySinh(
      ngaySinh == null ? null : new java.sql.Date(ngaySinh.getTime())
    );
    hs.setGioiTinh("Nam");
    hs.setDiaChi("");
    hs.setNgayKham(new java.sql.Date(getSelectedExamDateOrToday().getTime()));
    hs.setTrieuChung("");
    hs.setChanDoan("");
    hs.setKetLuan("");
    hs.setLoiDan("");
    hs.setMaBacSi(lich.getMaBacSi());
    hs.setTrangThai(StatusNormalizer.CHO_KHAM);

    return hoSoBenhAnBUS.dangKyBenhNhan(hs);
  }

  private String generateUniqueMaHoSo() {
    String ma;
    do {
      ma = "HS" + System.currentTimeMillis();
    } while (hoSoBenhAnBUS.getById(ma) != null);
    return ma;
  }

  private boolean exportReceiptPdf(BookingReceipt receipt, File file) {
    if (receipt == null || file == null) {
      return false;
    }
    try {
      Document document = new Document();
      PdfWriter.getInstance(document, new FileOutputStream(file));
      document.open();
      document.add(new Paragraph("PHIEU DAT LICH KHAM"));
      document.add(new Paragraph("----------------------------------------"));
      document.add(new Paragraph("Ma lich: " + receipt.maLich));
      document.add(new Paragraph("Ho ten: " + receipt.hoTen));
      document.add(new Paragraph("So dien thoai: " + receipt.soDienThoai));
      document.add(new Paragraph("CCCD: " + receipt.cccd));
      document.add(new Paragraph("Ngay sinh: " + receipt.ngaySinh));
      document.add(new Paragraph("Bac si: " + receipt.bacSi));
      document.add(new Paragraph("Goi kham: " + receipt.goiKham));
      document.add(new Paragraph("Thoi gian: " + receipt.thoiGianBatDau));
      document.add(new Paragraph("Thanh toan: " + receipt.hinhThucThanhToan));
      document.add(new Paragraph("----------------------------------------"));
      document.add(new Paragraph("Cam on quy khach da su dung dich vu."));
      document.close();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  private File createAutoInvoiceFile(String maLich) {
    File dir = new File("hoa_don_dat_lich");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    return new File(dir, "HoaDonDatLich_" + maLich + "_" + time + ".pdf");
  }

  private void resetForm() {
    clearSlotCaches();
    txtHoTen.setText("");
    txtSoDienThoai.setText("");
    txtCCCD.setText("");
    spNgaySinh.setValue(new Date());
    spNgayKham.setDate(new Date());
    if (cbKhungGio.getItemCount() > 0) {
      cbKhungGio.setSelectedIndex(0);
    }
    if (cbGoiKham.getItemCount() > 0) {
      cbGoiKham.setSelectedIndex(0);
    }
    if (cbThanhToan.getItemCount() > 0) {
      cbThanhToan.setSelectedIndex(0);
    }
    lastReceipt = null;
    refreshSelectionState();
  }

  private void clearSlotCaches() {
    approvedShiftCache.clear();
    bookedRangesCache.clear();
    hasAvailableSlotCache.clear();
    validDatesByDoctorCache.clear();
  }

  private Date getSelectedExamDate() {
    return spNgayKham == null ? null : spNgayKham.getDate();
  }

  private Date getSelectedExamDateOrToday() {
    Date selected = getSelectedExamDate();
    return selected == null ? new Date() : selected;
  }

  private boolean isDateBeforeToday(Date date) {
    if (date == null) {
      return false;
    }
    LocalDate selected = new java.sql.Date(date.getTime()).toLocalDate();
    return selected.isBefore(LocalDate.now());
  }

  private static class DoctorScheduleDateEvaluator implements IDateEvaluator {

    private final Set<String> validDateKeys = new HashSet<>();
    private final SimpleDateFormat keyFormatter = new SimpleDateFormat(
      "yyyy-MM-dd"
    );

    private void setValidDateKeys(List<String> keys) {
      validDateKeys.clear();
      if (keys != null) {
        validDateKeys.addAll(keys);
      }
    }

    @Override
    public boolean isSpecial(Date date) {
      return false;
    }

    @Override
    public Color getSpecialForegroundColor() {
      return null;
    }

    @Override
    public Color getSpecialBackroundColor() {
      return null;
    }

    @Override
    public String getSpecialTooltip() {
      return null;
    }

    @Override
    public boolean isInvalid(Date date) {
      if (date == null || validDateKeys.isEmpty()) {
        return true;
      }
      String key = keyFormatter.format(date);
      return !validDateKeys.contains(key);
    }

    @Override
    public Color getInvalidForegroundColor() {
      return new Color(148, 163, 184);
    }

    @Override
    public Color getInvalidBackroundColor() {
      return new Color(241, 245, 249);
    }

    @Override
    public String getInvalidTooltip() {
      return "Bác sĩ không có lịch làm việc ngày này";
    }
  }

  private void openLookupCccdDialog() {
    if (!coQuyenTraCuu) {
      DialogHelper.warn(this, "Bạn không có quyền tra cứu hồ sơ.");
      return;
    }

    javax.swing.JTextField txtLookup = new javax.swing.JTextField(18);
    JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setOpaque(false);
    panel.add(
      new JLabel("Nhập CCCD để tra cứu hồ sơ đã khám"),
      BorderLayout.NORTH
    );
    panel.add(txtLookup, BorderLayout.CENTER);

    int result = JOptionPane.showConfirmDialog(
      this,
      panel,
      "Tra cứu hồ sơ theo CCCD",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );
    if (result != JOptionPane.OK_OPTION) {
      return;
    }

    String cccd = txtLookup.getText() == null ? "" : txtLookup.getText().trim();
    searchMedicalRecordsByCCCD(cccd);
  }

  private void searchMedicalRecordsByCCCD(String cccd) {
    if (!isValidCCCD(cccd)) {
      DialogHelper.warn(
        this,
        "Vui lòng nhập CCCD hợp lệ (9-12 chữ số) để tra cứu."
      );
      return;
    }

    ArrayList<HoSoBenhAnDTO> records = hoSoBenhAnBUS.getByCCCD(cccd);
    ArrayList<HoSoBenhAnDTO> daKham = new ArrayList<>();
    if (records != null) {
      for (HoSoBenhAnDTO hs : records) {
        if (
          StatusNormalizer.DA_KHAM.equals(
            StatusNormalizer.normalizeHoSoStatus(hs.getTrangThai())
          )
        ) {
          daKham.add(hs);
        }
      }
    }

    if (daKham.isEmpty()) {
      DialogHelper.info(
        this,
        "Không tìm thấy hồ sơ bệnh án đã khám theo CCCD này."
      );
      return;
    }

    DefaultTableModel model = new DefaultTableModel(
      new Object[] {
        "Mã hồ sơ",
        "Ngày khám",
        "Họ tên",
        "SĐT",
        "Chẩn đoán",
        "Kết luận",
        "Mã bác sĩ",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    for (HoSoBenhAnDTO hs : daKham) {
      model.addRow(
        new Object[] {
          hs.getMaHoSo(),
          hs.getNgayKham(),
          hs.getHoTen(),
          hs.getSoDienThoai(),
          hs.getChanDoan(),
          hs.getKetLuan(),
          hs.getMaBacSi(),
        }
      );
    }

    JTable table = new JTable(model);
    UIUtils.styleTable(table);
    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new java.awt.Dimension(900, 280));

    JOptionPane.showMessageDialog(
      this,
      scroll,
      "Danh sách hồ sơ đã khám theo CCCD: " + cccd,
      JOptionPane.INFORMATION_MESSAGE
    );
  }

  private boolean isValidCCCD(String cccd) {
    return cccd != null && cccd.matches("^\\d{9,12}$");
  }

  private boolean isValidBirthDate(Date ngaySinh) {
    return ngaySinh != null && !ngaySinh.after(new Date());
  }

  private String extractId(String text) {
    if (text == null) {
      return "";
    }
    int idx = text.indexOf(" - ");
    return idx > 0 ? text.substring(0, idx).trim() : text.trim();
  }

  private String generateGuestCode() {
    String phone = txtSoDienThoai.getText().replaceAll("\\D", "");
    String suffix =
      phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
    return "GUEST-" + suffix + "-" + System.currentTimeMillis();
  }

  private String normalize(String text) {
    if (text == null) {
      return "";
    }
    String lowered = text
      .toLowerCase(Locale.ROOT)
      .replace('á', 'a')
      .replace('à', 'a')
      .replace('ả', 'a')
      .replace('ã', 'a')
      .replace('ạ', 'a')
      .replace('ă', 'a')
      .replace('ắ', 'a')
      .replace('ằ', 'a')
      .replace('ẳ', 'a')
      .replace('ẵ', 'a')
      .replace('ặ', 'a')
      .replace('â', 'a')
      .replace('ấ', 'a')
      .replace('ầ', 'a')
      .replace('ẩ', 'a')
      .replace('ẫ', 'a')
      .replace('ậ', 'a')
      .replace('é', 'e')
      .replace('è', 'e')
      .replace('ẻ', 'e')
      .replace('ẽ', 'e')
      .replace('ẹ', 'e')
      .replace('ê', 'e')
      .replace('ế', 'e')
      .replace('ề', 'e')
      .replace('ể', 'e')
      .replace('ễ', 'e')
      .replace('ệ', 'e')
      .replace('í', 'i')
      .replace('ì', 'i')
      .replace('ỉ', 'i')
      .replace('ĩ', 'i')
      .replace('ị', 'i')
      .replace('ó', 'o')
      .replace('ò', 'o')
      .replace('ỏ', 'o')
      .replace('õ', 'o')
      .replace('ọ', 'o')
      .replace('ô', 'o')
      .replace('ố', 'o')
      .replace('ồ', 'o')
      .replace('ổ', 'o')
      .replace('ỗ', 'o')
      .replace('ộ', 'o')
      .replace('ơ', 'o')
      .replace('ớ', 'o')
      .replace('ờ', 'o')
      .replace('ở', 'o')
      .replace('ỡ', 'o')
      .replace('ợ', 'o')
      .replace('ú', 'u')
      .replace('ù', 'u')
      .replace('ủ', 'u')
      .replace('ũ', 'u')
      .replace('ụ', 'u')
      .replace('ư', 'u')
      .replace('ứ', 'u')
      .replace('ừ', 'u')
      .replace('ử', 'u')
      .replace('ữ', 'u')
      .replace('ự', 'u')
      .replace('ý', 'y')
      .replace('ỳ', 'y')
      .replace('ỷ', 'y')
      .replace('ỹ', 'y')
      .replace('ỵ', 'y')
      .replace('đ', 'd');
    return lowered;
  }

  private void apDungPhanQuyenHanhDong() {
    coQuyenDatLich = Session.coMotTrongCacQuyen("GUEST_DAT_LICH");
    coQuyenTraCuu = Session.coMotTrongCacQuyen("GUEST_TRA_CUU_HO_SO");

    if (btnXacNhanDatLich != null) btnXacNhanDatLich.setVisible(coQuyenDatLich);
    if (btnLamMoi != null) btnLamMoi.setVisible(coQuyenDatLich);
    if (btnTraCuu != null) btnTraCuu.setVisible(coQuyenTraCuu);

    if (txtHoTen != null) txtHoTen.setEnabled(coQuyenDatLich);
    if (txtSoDienThoai != null) txtSoDienThoai.setEnabled(coQuyenDatLich);
    if (txtCCCD != null) txtCCCD.setEnabled(coQuyenDatLich);
    if (spNgaySinh != null) spNgaySinh.setEnabled(coQuyenDatLich);
    if (spNgayKham != null) spNgayKham.setEnabled(coQuyenDatLich);
    if (cbBacSi != null) cbBacSi.setEnabled(coQuyenDatLich);
    if (cbKhungGio != null) cbKhungGio.setEnabled(coQuyenDatLich);
    if (cbGoiKham != null) cbGoiKham.setEnabled(coQuyenDatLich);
    if (cbThanhToan != null) cbThanhToan.setEnabled(coQuyenDatLich);
  }

  private static class BookingReceipt {

    private final String maLich;
    private final String hoTen;
    private final String soDienThoai;
    private final String cccd;
    private final String ngaySinh;
    private final String bacSi;
    private final String goiKham;
    private final String thoiGianBatDau;
    private final String hinhThucThanhToan;

    private BookingReceipt(
      String maLich,
      String hoTen,
      String soDienThoai,
      String cccd,
      String ngaySinh,
      String bacSi,
      String goiKham,
      String thoiGianBatDau,
      String hinhThucThanhToan
    ) {
      this.maLich = maLich;
      this.hoTen = hoTen;
      this.soDienThoai = soDienThoai;
      this.cccd = cccd;
      this.ngaySinh = ngaySinh;
      this.bacSi = bacSi;
      this.goiKham = goiKham;
      this.thoiGianBatDau = thoiGianBatDau;
      this.hinhThucThanhToan = hinhThucThanhToan;
    }
  }

  private enum SlotState {
    AVAILABLE,
    OUT_OF_SHIFT,
    BOOKED,
  }

  private static class TimeRange {

    private final LocalTime start;
    private final LocalTime end;

    private TimeRange(LocalTime start, LocalTime end) {
      this.start = start;
      this.end = end;
    }
  }

  private static class SlotOption {

    private final TimeRange range;
    private final SlotState state;
    private final String hint;

    private SlotOption(
      LocalTime start,
      LocalTime end,
      SlotState state,
      String hint
    ) {
      this.range = new TimeRange(start, end);
      this.state = state;
      this.hint = hint;
    }

    private String toDisplayText() {
      String base =
        TIME_FORMATTER.format(range.start) +
        " - " +
        TIME_FORMATTER.format(range.end);
      if (state == SlotState.BOOKED) {
        return base + " (đã bận)";
      }
      if (state == SlotState.OUT_OF_SHIFT) {
        return base + " (ngoài ca)";
      }
      return base;
    }
  }
}
