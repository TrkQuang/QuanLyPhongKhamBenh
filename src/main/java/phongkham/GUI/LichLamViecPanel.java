package phongkham.gui;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusColorUtil;
import phongkham.Utils.StatusDisplayUtil;
import phongkham.Utils.StatusNormalizer;

public class LichLamViecPanel extends JPanel {

  private final Color PRIMARY = new Color(37, 99, 235);
  private final Color BG = new Color(245, 247, 250);
  private final Color SUCCESS = new Color(34, 197, 94);
  private final Color DANGER = new Color(239, 68, 68);
  private static final DateTimeFormatter DATE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private JDateChooser dateChooser;
  private JPanel pnlSang, pnlChieu, pnlToi;
  private LichLamViecBUS llvBUS = new LichLamViecBUS();
  private BacSiBUS bsBUS = new BacSiBUS();

  private String maBacSiDangNhap = "BS001";
  private boolean laNguoiDuyet = false;

  public LichLamViecPanel() {
    capNhatThongTinPhien();
    initComponents();
    loadData();
  }

  private void capNhatThongTinPhien() {
    laNguoiDuyet =
      Session.hasPermission("LICHLAMVIEC_APPROVE") ||
      Session.hasPermission("PHANQUYEN_VIEW");
    String maBacSi = Session.getCurrentBacSiID();
    if (maBacSi != null && !maBacSi.trim().isEmpty()) {
      maBacSiDangNhap = maBacSi.trim();
    }
  }

  private void initComponents() {
    setLayout(new BorderLayout(20, 20));
    setBackground(BG);
    setBorder(new EmptyBorder(25, 25, 25, 25));

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);

    JLabel lblTitle = new JLabel(
      laNguoiDuyet ? "Duyệt lịch làm việc bác sĩ" : "Đăng ký lịch làm việc"
    );
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
    lblTitle.setForeground(new Color(30, 41, 59));
    topPanel.add(lblTitle, BorderLayout.WEST);

    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    filterPanel.setOpaque(false);

    JLabel lblDate = new JLabel("Ngày:");
    lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    filterPanel.add(lblDate);

    dateChooser = new JDateChooser(new Date());
    dateChooser.setDateFormatString("yyyy-MM-dd");
    dateChooser.setPreferredSize(new Dimension(160, 35));
    dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    dateChooser.addPropertyChangeListener("date", e -> loadData());
    filterPanel.add(dateChooser);

    topPanel.add(filterPanel, BorderLayout.EAST);
    add(topPanel, BorderLayout.NORTH);

    JPanel gridPanel = new JPanel(new GridLayout(1, 3, 20, 0));
    gridPanel.setOpaque(false);

    pnlSang = createShiftColumn("Sáng");
    pnlChieu = createShiftColumn("Chiều");
    pnlToi = createShiftColumn("Tối");

    gridPanel.add(pnlSang);
    gridPanel.add(pnlChieu);
    gridPanel.add(pnlToi);

    add(gridPanel, BorderLayout.CENTER);
  }

  private JPanel createShiftColumn(String title) {
    JPanel col = new JPanel(new BorderLayout());
    col.setBackground(Color.WHITE);
    col.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));

    JLabel lblHeader = new JLabel(
      "CA " + title.toUpperCase(),
      SwingConstants.CENTER
    );
    lblHeader.setOpaque(true);
    lblHeader.setBackground(new Color(248, 250, 252));
    lblHeader.setForeground(new Color(71, 85, 105));
    lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblHeader.setPreferredSize(new Dimension(0, 50));
    lblHeader.setBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240))
    );
    col.add(lblHeader, BorderLayout.NORTH);

    JPanel list = new JPanel();
    list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
    list.setBackground(Color.WHITE);
    list.setBorder(new EmptyBorder(10, 10, 10, 10));

    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(null);
    col.add(scroll, BorderLayout.CENTER);

    JButton btnAdd = new JButton(
      laNguoiDuyet ? "Làm mới" : "+ Đăng ký ca " + title
    );
    styleButton(btnAdd, PRIMARY);
    if (laNguoiDuyet) {
      btnAdd.addActionListener(e -> loadData());
    } else {
      btnAdd.addActionListener(e -> dangKy(title));
    }
    col.add(btnAdd, BorderLayout.SOUTH);

    return col;
  }

  private void loadData() {
    if (dateChooser.getDate() == null) return;

    String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(
      dateChooser.getDate()
    );
    clearColumns();

    java.util.List<LichLamViecDTO> danhSachNguon = laNguoiDuyet
      ? llvBUS.getAll()
      : llvBUS.getByBacSi(maBacSiDangNhap);

    for (LichLamViecDTO llv : danhSachNguon) {
      if (llv.getNgayLam() != null && llv.getNgayLam().startsWith(dateStr)) {
        JPanel card = createCard(llv);

        if (llv.getCaLam().equalsIgnoreCase("Sang")) getListPnl(pnlSang).add(
          card
        );
        else if (llv.getCaLam().equalsIgnoreCase("Chieu")) getListPnl(
          pnlChieu
        ).add(card);
        else if (llv.getCaLam().equalsIgnoreCase("Toi")) getListPnl(pnlToi).add(
          card
        );

        JPanel spacer = new JPanel();
        spacer.setMaximumSize(new Dimension(0, 10));
        spacer.setOpaque(false);

        if (llv.getCaLam().equalsIgnoreCase("Sang")) getListPnl(pnlSang).add(
          spacer
        );
        else if (llv.getCaLam().equalsIgnoreCase("Chieu")) getListPnl(
          pnlChieu
        ).add(spacer);
        else if (llv.getCaLam().equalsIgnoreCase("Toi")) getListPnl(pnlToi).add(
          spacer
        );
      }
    }

    repaint();
    revalidate();
  }

  private JPanel createCard(LichLamViecDTO llv) {
    JPanel card = new JPanel(new BorderLayout(10, 0));
    card.setBackground(new Color(248, 250, 252));
    card.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(new Color(226, 232, 240), 1, true),
        new EmptyBorder(12, 12, 12, 12)
      )
    );
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

    BacSiDTO bs = bsBUS.getById(llv.getMaBacSi());
    String tenBS = (bs != null) ? bs.getHoTen() : "Bác sĩ";
    String trangThaiChuan = StatusNormalizer.normalizeLichLamViecStatus(
      llv.getTrangThai()
    );
    String trangThaiHienThi = StatusDisplayUtil.lichLamViec(trangThaiChuan);

    JLabel lblName = new JLabel(
      "<html><b>BS. " +
        tenBS +
        "</b><br><font color='gray'>Mã ca: " +
        llv.getMaLichLam() +
        "</font><br><font color='" +
        toHtmlColor(StatusColorUtil.lichLamViec(trangThaiChuan)) +
        "'>Trạng thái: " +
        trangThaiHienThi +
        "</font></html>"
    );
    lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    card.add(lblName, BorderLayout.CENTER);

    JPanel actions = new JPanel(new GridLayout(2, 1, 0, 5));
    actions.setOpaque(false);

    if (laNguoiDuyet && "CHO_DUYET".equals(trangThaiChuan)) {
      JButton btnDuyet = new JButton("Duyệt");
      JButton btnTuChoi = new JButton("Từ chối");
      styleMiniBtn(btnDuyet, SUCCESS);
      styleMiniBtn(btnTuChoi, DANGER);

      btnDuyet.addActionListener(e -> {
        if (llvBUS.duyetLich(llv.getMaLichLam())) {
          JOptionPane.showMessageDialog(this, "Đã duyệt lịch làm việc.");
          loadData();
        } else {
          JOptionPane.showMessageDialog(this, "Không thể duyệt lịch này.");
        }
      });

      btnTuChoi.addActionListener(e -> {
        if (llvBUS.tuChoiLich(llv.getMaLichLam())) {
          JOptionPane.showMessageDialog(this, "Đã từ chối lịch làm việc.");
          loadData();
        } else {
          JOptionPane.showMessageDialog(this, "Không thể từ chối lịch này.");
        }
      });

      actions.add(btnDuyet);
      actions.add(btnTuChoi);
      card.add(actions, BorderLayout.EAST);
    } else if (!laNguoiDuyet && coTheHuyDangKy(llv, trangThaiChuan)) {
      JButton btnHuyDangKy = new JButton("Hủy đăng ký");
      styleMiniBtn(btnHuyDangKy, DANGER);
      btnHuyDangKy.addActionListener(e -> {
        if (llvBUS.delete(llv.getMaLichLam())) {
          JOptionPane.showMessageDialog(this, "Đã hủy đăng ký ca làm.");
          loadData();
        } else {
          JOptionPane.showMessageDialog(this, "Không thể hủy đăng ký.");
        }
      });
      actions.add(btnHuyDangKy);
      actions.add(new JLabel(""));
      card.add(actions, BorderLayout.EAST);
    }

    return card;
  }

  private boolean coTheHuyDangKy(LichLamViecDTO llv, String trangThaiChuan) {
    if ("CHO_DUYET".equals(trangThaiChuan)) {
      return true;
    }
    try {
      LocalDate ngayLam = LocalDate.parse(llv.getNgayLam(), DATE_FORMATTER);
      return ngayLam.isBefore(LocalDate.now());
    } catch (Exception ex) {
      return false;
    }
  }

  private void dangKy(String ca) {
    if (maBacSiDangNhap == null || maBacSiDangNhap.trim().isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Không xác định được bác sĩ đăng nhập."
      );
      return;
    }

    String caDb = ca.equals("Sáng")
      ? "Sang"
      : (ca.equals("Chiều") ? "Chieu" : "Toi");

    String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(
      dateChooser.getDate()
    );

    if (llvBUS.checkConflict(maBacSiDangNhap, dateStr, caDb)) {
      JOptionPane.showMessageDialog(
        this,
        "Bạn đã đăng ký ca này trong ngày đã chọn."
      );
      return;
    }

    LichLamViecDTO llv = new LichLamViecDTO();
    llv.setMaLichLam(llvBUS.generateMaLichLam());
    llv.setMaBacSi(maBacSiDangNhap);
    llv.setNgayLam(dateStr);
    llv.setCaLam(caDb);
    llv.setTrangThai("CHO_DUYET");

    if (llvBUS.add(llv)) {
      JOptionPane.showMessageDialog(
        this,
        "Đăng ký ca làm thành công. Trạng thái: Chờ duyệt."
      );
      loadData();
    } else {
      JOptionPane.showMessageDialog(this, "Đăng ký ca làm thất bại.");
    }
  }

  private void styleButton(JButton btn, Color bg) {
    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btn.setForeground(Color.WHITE);
    btn.setBackground(bg);
    btn.setFocusPainted(false);
    btn.setPreferredSize(new Dimension(0, 45));
    btn.setBorderPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private void styleMiniBtn(JButton btn, Color fg) {
    btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
    btn.setForeground(fg);
    btn.setBackground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setBorder(new LineBorder(fg, 1, true));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private String toHtmlColor(Color color) {
    return String.format(
      "#%02x%02x%02x",
      color.getRed(),
      color.getGreen(),
      color.getBlue()
    );
  }

  private JPanel getListPnl(JPanel col) {
    return (JPanel) ((JScrollPane) col.getComponent(1)).getViewport().getView();
  }

  private void clearColumns() {
    getListPnl(pnlSang).removeAll();
    getListPnl(pnlChieu).removeAll();
    getListPnl(pnlToi).removeAll();
  }
}
