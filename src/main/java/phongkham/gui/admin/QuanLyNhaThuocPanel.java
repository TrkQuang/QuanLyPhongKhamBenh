package phongkham.gui.admin;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import phongkham.BUS.PhieuNhapBUS;
import phongkham.BUS.ThuocBUS;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.UIUtils;

public class QuanLyNhaThuocPanel extends BasePanel {

  private final ThuocBUS thuocBUS = new ThuocBUS();
  private final PhieuNhapBUS phieuNhapBUS = new PhieuNhapBUS();
  private JLabel lblTongThuoc;
  private JLabel lblTongPhieuNhap;

  @Override
  protected void init() {

    javax.swing.JPanel grid = new javax.swing.JPanel(
      new java.awt.GridLayout(1, 2, 12, 12)
    );
    grid.setOpaque(false);
    lblTongThuoc = metric("0");
    lblTongPhieuNhap = metric("0");
    grid.add(UIUtils.createSection("Tổng thuốc", lblTongThuoc));
    grid.add(UIUtils.createSection("Tổng phiếu nhập", lblTongPhieuNhap));
    add(grid, BorderLayout.CENTER);

    javax.swing.JPanel actions = UIUtils.row(UIUtils.primaryButton("Tải lại"));
    ((javax.swing.JButton) actions.getComponent(0)).addActionListener(e ->
      loadData()
    );
    add(actions, BorderLayout.SOUTH);

    loadData();
  }

  private JLabel metric(String text) {
    JLabel label = new JLabel(text, javax.swing.SwingConstants.CENTER);
    label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
    return label;
  }

  private void loadData() {
    if (lblTongThuoc != null) {
      lblTongThuoc.setText(String.valueOf(thuocBUS.list().size()));
    }
    if (lblTongPhieuNhap != null) {
      lblTongPhieuNhap.setText(String.valueOf(phieuNhapBUS.getAll().size()));
    }
  }
}
