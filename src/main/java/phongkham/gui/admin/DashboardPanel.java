package phongkham.gui.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.CTPhieuNhapBUS;
import phongkham.BUS.HoaDonKhamBUS;
import phongkham.BUS.LichKhamBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.DTO.LichKhamDTO;
import phongkham.DTO.LoThuocNhapDTO;
import phongkham.DTO.ThuocDTO;
import phongkham.gui.admin.components.ChartPanel;
import phongkham.gui.admin.components.Sidebar;
import phongkham.gui.admin.components.StatCard;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.UIConstants;
import phongkham.gui.common.UIUtils;
import phongkham.gui.common.components.ShadowPanel;

public class DashboardPanel extends BasePanel {

  private static final DateTimeFormatter DATETIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final BacSiBUS bacSiBUS = new BacSiBUS();
  private final UsersBUS usersBUS = new UsersBUS();
  private final HoaDonKhamBUS hoaDonKhamBUS = new HoaDonKhamBUS();
  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();
  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final CTPhieuNhapBUS ctPhieuNhapBUS = new CTPhieuNhapBUS();

  @Override
  protected void init() {
    DashboardMetrics metrics = collectMetrics();

    JPanel page = new JPanel(new BorderLayout(0, 14));
    page.setOpaque(false);

    JPanel content = new JPanel(new BorderLayout(0, 12));
    content.setOpaque(false);

    content.add(buildStatsRow(metrics), BorderLayout.NORTH);
    content.add(buildMainSplit(metrics), BorderLayout.CENTER);
    content.add(buildAlertPanel(metrics), BorderLayout.SOUTH);

    page.add(content, BorderLayout.CENTER);
    add(page, BorderLayout.CENTER);
  }

  private JPanel buildStatsRow(DashboardMetrics metrics) {
    JPanel row = new JPanel(new java.awt.GridLayout(1, 4, 10, 0));
    row.setOpaque(false);

    row.add(new StatCard("Số bác sĩ", String.valueOf(metrics.soBacSi)));
    row.add(new StatCard("Số tài khoản", String.valueOf(metrics.soTaiKhoan)));
    row.add(
      new StatCard("Doanh thu", formatCurrency(metrics.tongDoanhThu) + " VND")
    );
    row.add(
      new StatCard("Lịch khám hôm nay", String.valueOf(metrics.lichHomNay))
    );

    return row;
  }

  private JSplitPane buildMainSplit(DashboardMetrics metrics) {
    String subtitle = "6 Tháng gần nhất";
    ChartPanel chartPanel = new ChartPanel(
      "Doanh thu theo tháng",
      subtitle,
      metrics.monthLabels,
      metrics.monthValues
    );

    Sidebar sidebar = new Sidebar(
      metrics.tyLeLich,
      metrics.tyLeTonAnToan,
      metrics.hetHan,
      metrics.sapHet30,
      metrics.thuocTonThap
    );

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      chartPanel,
      sidebar
    );
    splitPane.setOpaque(false);
    splitPane.setBorder(null);
    splitPane.setResizeWeight(0.72);
    splitPane.setContinuousLayout(true);
    splitPane.setDividerSize(8);

    return splitPane;
  }

  private JPanel buildAlertPanel(DashboardMetrics metrics) {
    ShadowPanel panel = new ShadowPanel(20);
    panel.setLayout(new BorderLayout(0, 10));
    panel.setBorder(
      javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
    );

    JLabel title = new JLabel("Cảnh báo thuốc");
    title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
    title.setForeground(UIConstants.TEXT_MAIN);

    JTable nearExpiryTable = createNearExpiryTable(metrics.nearExpiryLots);
    JTable lowStockTable = createLowStockTable(metrics.lowStockMedicines);

    JPanel tables = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
    tables.setOpaque(false);
    tables.add(
      UIUtils.createSection(
        "Thuoc sap het han",
        new JScrollPane(nearExpiryTable)
      )
    );
    tables.add(
      UIUtils.createSection("Thuốc tồn thấp", new JScrollPane(lowStockTable))
    );

    panel.add(title, BorderLayout.NORTH);
    panel.add(tables, BorderLayout.CENTER);
    panel.setPreferredSize(new Dimension(0, 220));

    return panel;
  }

  private JTable createNearExpiryTable(List<LotAlertItem> rows) {
    DefaultTableModel model = new DefaultTableModel(
      new Object[] { "Thuốc", "Số lô", "HSD", "Còn lại", "Còn (ngày)" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    if (rows.isEmpty()) {
      model.addRow(new Object[] { "-", "-", "-", 0, "-" });
    } else {
      for (LotAlertItem row : rows) {
        model.addRow(
          new Object[] {
            row.tenThuoc,
            row.soLo,
            row.hanSuDung,
            row.soLuongConLai,
            row.daysLeft,
          }
        );
      }
    }

    JTable table = new JTable(model);
    UIUtils.styleTable(table);
    return table;
  }

  private JTable createLowStockTable(List<ThuocDTO> rows) {
    DefaultTableModel model = new DefaultTableModel(
      new Object[] { "Mã", "Tên thuốc", "tồn" },
      0
    ) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    if (rows.isEmpty()) {
      model.addRow(new Object[] { "-", "Không có dữ liệu", 0 });
    } else {
      for (ThuocDTO thuoc : rows) {
        model.addRow(
          new Object[] {
            thuoc.getMaThuoc(),
            thuoc.getTenThuoc(),
            thuoc.getSoLuongTon(),
          }
        );
      }
    }

    JTable table = new JTable(model);
    UIUtils.styleTable(table);
    return table;
  }

  private DashboardMetrics collectMetrics() {
    DashboardMetrics metrics = new DashboardMetrics();

    metrics.soBacSi = bacSiBUS.countAll();
    metrics.soTaiKhoan = usersBUS.getAllUsers().size();

    ArrayList<HoaDonKhamDTO> hoaDonKhams = hoaDonKhamBUS.getAll();
    for (HoaDonKhamDTO hoaDon : hoaDonKhams) {
      if (hoaDon.getTongTien() != null) {
        metrics.tongDoanhThu = metrics.tongDoanhThu.add(hoaDon.getTongTien());
      }
    }

    ArrayList<LichKhamDTO> lichKhams = lichKhamBUS.getAll();
    int tongLich = lichKhams.size();
    int lichDaXacNhan = lichKhamBUS.countByTrangThai("DA_XAC_NHAN");
    metrics.lichHomNay = countTodayAppointments(lichKhams);

    ArrayList<ThuocDTO> dsThuoc = thuocBUS.list();
    for (ThuocDTO thuoc : dsThuoc) {
      if (thuoc.getSoLuongTon() < 20) {
        metrics.thuocTonThap++;
      }
    }

    metrics.tyLeLich = tongLich == 0 ? 0 : (lichDaXacNhan * 100) / tongLich;
    metrics.tyLeTonAnToan = dsThuoc.isEmpty()
      ? 100
      : Math.max(0, 100 - (metrics.thuocTonThap * 100) / dsThuoc.size());

    ArrayList<LoThuocNhapDTO> lots = ctPhieuNhapBUS.getAllLotsForMonitoring();
    LocalDate today = LocalDate.now();

    for (LoThuocNhapDTO lot : lots) {
      String status = normalize(lot.getTrangThaiPhieuNhap());
      boolean daNhap = "DA_NHAP".equals(status) || "DA_NHAP_KHO".equals(status);
      if (!daNhap || lot.getSoLuongConLai() <= 0) {
        continue;
      }

      if (lot.getHanSuDung() == null) {
        continue;
      }

      long days = java.time.temporal.ChronoUnit.DAYS.between(
        today,
        lot.getHanSuDung()
      );
      if (days <= 0) {
        metrics.hetHan += lot.getSoLuongConLai();
      } else {
        if (days <= 30) {
          metrics.sapHet30 += lot.getSoLuongConLai();
        }
      }

      if (days <= 30) {
        LotAlertItem item = new LotAlertItem();
        item.tenThuoc = safe(lot.getTenThuoc());
        item.soLo = safe(lot.getSoLo());
        item.hanSuDung = lot.getHanSuDung().toString();
        item.soLuongConLai = lot.getSoLuongConLai();
        item.daysLeft = days;
        metrics.nearExpiryLots.add(item);
      }
    }

    metrics.nearExpiryLots.sort(Comparator.comparingLong(a -> a.daysLeft));
    if (metrics.nearExpiryLots.size() > 8) {
      metrics.nearExpiryLots = new ArrayList<>(
        metrics.nearExpiryLots.subList(0, 8)
      );
    }

    dsThuoc.sort(Comparator.comparingInt(ThuocDTO::getSoLuongTon));
    for (ThuocDTO thuoc : dsThuoc) {
      if (thuoc.getSoLuongTon() >= 20) {
        continue;
      }
      metrics.lowStockMedicines.add(thuoc);
      if (metrics.lowStockMedicines.size() >= 8) {
        break;
      }
    }

    buildChartData(metrics, hoaDonKhams);
    return metrics;
  }

  private int countTodayAppointments(List<LichKhamDTO> lichKhams) {
    LocalDate today = LocalDate.now();
    int count = 0;

    for (LichKhamDTO lich : lichKhams) {
      String raw = safe(lich.getThoiGianBatDau());
      if (raw.isEmpty()) {
        continue;
      }

      LocalDate parsed = parseDate(raw);
      if (parsed != null && parsed.equals(today)) {
        count++;
      }
    }

    return count;
  }

  private LocalDate parseDate(String raw) {
    try {
      return LocalDateTime.parse(raw, DATETIME_FORMATTER).toLocalDate();
    } catch (Exception ignored) {}

    try {
      if (raw.length() >= 10) {
        return LocalDate.parse(raw.substring(0, 10));
      }
    } catch (Exception ignored) {}

    return null;
  }

  private void buildChartData(
    DashboardMetrics metrics,
    ArrayList<HoaDonKhamDTO> hoaDonKhams
  ) {
    Map<YearMonth, Double> revenueByMonth = new LinkedHashMap<>();
    YearMonth currentMonth = YearMonth.now();

    for (int i = 5; i >= 0; i--) {
      YearMonth ym = currentMonth.minusMonths(i);
      revenueByMonth.put(ym, 0d);
    }

    for (HoaDonKhamDTO hoaDon : hoaDonKhams) {
      if (hoaDon.getNgayThanhToan() == null || hoaDon.getTongTien() == null) {
        continue;
      }

      YearMonth ym = YearMonth.from(hoaDon.getNgayThanhToan());
      if (!revenueByMonth.containsKey(ym)) {
        continue;
      }

      revenueByMonth.put(
        ym,
        revenueByMonth.get(ym) + hoaDon.getTongTien().doubleValue()
      );
    }

    metrics.monthLabels = new String[revenueByMonth.size()];
    metrics.monthValues = new double[revenueByMonth.size()];

    int idx = 0;
    double max = 0;
    for (Map.Entry<YearMonth, Double> entry : revenueByMonth.entrySet()) {
      metrics.monthLabels[idx] = "T" + entry.getKey().getMonthValue();
      metrics.monthValues[idx] = Math.max(0, entry.getValue());
      max = Math.max(max, metrics.monthValues[idx]);
      idx++;
    }

    if (max <= 0) {
      for (int i = 0; i < metrics.monthValues.length; i++) {
        metrics.monthValues[i] = 40 + (i * 16);
      }
    }
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
  }

  private String safe(String value) {
    return value == null ? "" : value;
  }

  private String formatCurrency(BigDecimal value) {
    return new DecimalFormat("#,###").format(
      value == null ? BigDecimal.ZERO : value
    );
  }

  private static class LotAlertItem {

    private String tenThuoc;
    private String soLo;
    private String hanSuDung;
    private int soLuongConLai;
    private long daysLeft;
  }

  private static class DashboardMetrics {

    private int soBacSi;
    private int soTaiKhoan;
    private int lichHomNay;
    private int tyLeLich;
    private int tyLeTonAnToan;
    private int thuocTonThap;
    private int hetHan;
    private int sapHet30;
    private BigDecimal tongDoanhThu = BigDecimal.ZERO;
    private String[] monthLabels = new String[0];
    private double[] monthValues = new double[0];
    private ArrayList<LotAlertItem> nearExpiryLots = new ArrayList<>();
    private ArrayList<ThuocDTO> lowStockMedicines = new ArrayList<>();
  }
}
