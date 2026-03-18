package phongkham.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import phongkham.gui.taikhoan.QuanLyBacSiTabPanel;
import phongkham.gui.taikhoan.QuanLyNhaThuocTabPanel;
import phongkham.gui.taikhoan.QuanLyTaiKhoanService;
import phongkham.gui.taikhoan.TaiKhoanDanhSachTabPanel;
import phongkham.gui.taikhoan.TaoTaiKhoanBacSiTabPanel;

public class QuanLyTaiKhoanPanel extends JPanel {

  private final QuanLyTaiKhoanService service;
  private TaiKhoanDanhSachTabPanel taiKhoanTab;
  private TaoTaiKhoanBacSiTabPanel taoBacSiTab;
  private QuanLyBacSiTabPanel bacSiTab;
  private QuanLyNhaThuocTabPanel nhaThuocTab;

  public QuanLyTaiKhoanPanel() {
    service = new QuanLyTaiKhoanService();

    setLayout(new BorderLayout(10, 10));
    setBackground(new Color(245, 247, 250));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    add(createHeader(), BorderLayout.NORTH);
    add(createTabs(), BorderLayout.CENTER);
  }

  private JPanel createHeader() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    JLabel title = new JLabel("Quản Lý Tài Khoản");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(new Color(30, 41, 59));
    panel.add(title, BorderLayout.WEST);

    return panel;
  }

  private JTabbedPane createTabs() {
    JTabbedPane tabs = new JTabbedPane();

    taiKhoanTab = new TaiKhoanDanhSachTabPanel(service, this::refreshAll);
    taoBacSiTab = new TaoTaiKhoanBacSiTabPanel(service, this::refreshAll);
    bacSiTab = new QuanLyBacSiTabPanel(this::refreshAll);
    nhaThuocTab = new QuanLyNhaThuocTabPanel(service, this::refreshAll);

    tabs.addTab("Tài khoản", taiKhoanTab);
    tabs.addTab("Tạo tài khoản bác sĩ", taoBacSiTab);
    tabs.addTab("Quản lý bác sĩ", bacSiTab);
    tabs.addTab("Quản lý nhà thuốc", nhaThuocTab);

    return tabs;
  }

  public void refreshAll() {
    if (taiKhoanTab != null) {
      taiKhoanTab.refreshData();
    }
    if (taoBacSiTab != null) {
      taoBacSiTab.loadKhoa();
    }
    if (bacSiTab != null) {
      bacSiTab.refreshData();
    }
    if (nhaThuocTab != null) {
      nhaThuocTab.refreshData();
    }
  }
}
