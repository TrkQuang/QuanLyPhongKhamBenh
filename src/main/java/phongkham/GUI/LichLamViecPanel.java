package phongkham.gui;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import phongkham.BUS.BacSiBUS;
import phongkham.BUS.LichLamViecBUS;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.LichLamViecDTO;
import phongkham.Utils.Session;

public class LichLamViecPanel extends JPanel {

  private final Color PRIMARY = new Color(37, 99, 235);
  private final Color BG = new Color(245, 247, 250);
  private final Color SUCCESS = new Color(34, 197, 94);
  private final Color DANGER = new Color(239, 68, 68);

  private JDateChooser dateChooser;
  private JPanel pnlSang, pnlChieu, pnlToi;
  private LichLamViecBUS llvBUS = new LichLamViecBUS();
  private BacSiBUS bsBUS = new BacSiBUS();

  public LichLamViecPanel() {
    initComponents();
    loadData();
  }

  private void initComponents() {
    setLayout(new BorderLayout(20, 20));
    setBackground(BG);
    setBorder(new EmptyBorder(25, 25, 25, 25));

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);

    JLabel lblTitle = new JLabel("Lịch Làm Việc");
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

    JButton btnAdd = new JButton("+ Đăng ký ca " + title);
    styleButton(btnAdd, PRIMARY);
    btnAdd.addActionListener(e -> dangKy(title));
    col.add(btnAdd, BorderLayout.SOUTH);

    return col;
  }

  private void loadData() {
    if (dateChooser.getDate() == null) return;

    String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(
      dateChooser.getDate()
    );
    clearColumns();

    for (LichLamViecDTO llv : llvBUS.getAll()) {
      if (llv.getNgayLam() != null && llv.getNgayLam().startsWith(dateStr)) {
        JPanel col = llv.getCaLam().equalsIgnoreCase("Sang")
          ? pnlSang
          : llv.getCaLam().equalsIgnoreCase("Chieu")
            ? pnlChieu
            : llv.getCaLam().equalsIgnoreCase("Toi")
              ? pnlToi
              : null;
        if (col != null) addToColumn(llv, col);
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
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    BacSiDTO bs = bsBUS.getById(llv.getMaBacSi());
    String tenBS = (bs != null) ? bs.getHoTen() : "Bác sĩ";

    JLabel lblName = new JLabel(
      "<html><b>BS. " +
        tenBS +
        "</b><br><font color='gray'>Mã ca: " +
        llv.getMaLichLam() +
        "</font></html>"
    );
    lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    card.add(lblName, BorderLayout.CENTER);

    JPanel actions = new JPanel(new GridLayout(2, 1, 0, 5));
    actions.setOpaque(false);

    JButton btnDuyet = new JButton("Duyệt");
    JButton btnHuy = new JButton("Hủy");
    styleMiniBtn(btnDuyet, SUCCESS);
    styleMiniBtn(btnHuy, DANGER);

    btnDuyet.addActionListener(e ->
      JOptionPane.showMessageDialog(this, "Đã duyệt lịch của " + tenBS)
    );

    btnHuy.addActionListener(e -> {
      if (llvBUS.delete(llv.getMaLichLam())) {
        JOptionPane.showMessageDialog(this, "Đã hủy ca làm.");
        loadData();
      }
    });

    actions.add(btnDuyet);
    actions.add(btnHuy);
    card.add(actions, BorderLayout.EAST);

    return card;
  }

  private void addToColumn(LichLamViecDTO llv, JPanel col) {
    getListPnl(col).add(createCard(llv));
    JPanel spacer = new JPanel();
    spacer.setMaximumSize(new Dimension(0, 10));
    spacer.setOpaque(false);
    getListPnl(col).add(spacer);
  }

  private void dangKy(String ca) {
    String caDb = ca.equals("Sáng")
      ? "Sang"
      : (ca.equals("Chiều") ? "Chieu" : "Toi");

    String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(
      dateChooser.getDate()
    );
    String maBacSi = Session.getCurrentBacSiID();
    if (maBacSi == null) return;

    LichLamViecDTO llv = new LichLamViecDTO();
    llv.setMaLichLam("L" + (System.currentTimeMillis() % 10000));
    llv.setMaBacSi(maBacSi);
    llv.setNgayLam(dateStr);
    llv.setCaLam(caDb);

    if (llvBUS.add(llv)) {
      loadData();
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

  private JPanel getListPnl(JPanel col) {
    return (JPanel) ((JScrollPane) col.getComponent(1)).getViewport().getView();
  }

  private void clearColumns() {
    getListPnl(pnlSang).removeAll();
    getListPnl(pnlChieu).removeAll();
    getListPnl(pnlToi).removeAll();
  }
}
