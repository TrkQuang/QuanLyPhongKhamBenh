package phongkham.gui.bacsi;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import phongkham.BUS.LichKhamBUS;
import phongkham.DTO.LichKhamDTO;
import phongkham.Utils.Session;
import phongkham.Utils.StatusNormalizer;
import phongkham.gui.common.BasePanel;
import phongkham.gui.common.DialogHelper;
import phongkham.gui.common.UIUtils;

public class LichKhamPanel extends BasePanel {

  private final LichKhamBUS lichKhamBUS = new LichKhamBUS();
  private final DefaultTableModel model = new DefaultTableModel(
    new Object[] {
      "Mã lịch",
      "Mã gói",
      "Thời gian bắt đầu",
      "Thời gian kết thúc",
      "Trạng thái",
    },
    0
  );

  private JTable table;
  private JButton btnTaiLai;
  private JButton btnXacNhan;
  private JButton btnHuy;

  @Override
  protected void init() {
    JPanel center = new JPanel(new BorderLayout(0, 10));
    center.setOpaque(false);

    table = new JTable(model);
    UIUtils.styleTable(table);
    center.add(
      UIUtils.createSection("Lịch khám hôm nay", new JScrollPane(table)),
      BorderLayout.CENTER
    );
    add(center, BorderLayout.CENTER);

    JPanel actions = UIUtils.row(
      UIUtils.primaryButton("Tải lại"),
      UIUtils.ghostButton("Xác nhận"),
      UIUtils.ghostButton("Hủy")
    );
    add(actions, BorderLayout.SOUTH);
    btnTaiLai = (JButton) actions.getComponent(0);
    btnXacNhan = (JButton) actions.getComponent(1);
    btnHuy = (JButton) actions.getComponent(2);

    btnTaiLai.addActionListener(e -> loadData());
    btnXacNhan.addActionListener(e ->
      updateSelectedStatus(StatusNormalizer.DA_XAC_NHAN)
    );
    btnHuy.addActionListener(e ->
      updateSelectedStatus(StatusNormalizer.DA_HUY)
    );

    apDungPhanQuyenHanhDong();

    loadData();
  }

  private void updateSelectedStatus(String status) {
    int row = table.getSelectedRow();
    if (row < 0) {
      DialogHelper.warn(this, "Vui lòng chọn một lịch khám.");
      return;
    }
    int modelRow = table.convertRowIndexToModel(row);
    String maLich = String.valueOf(model.getValueAt(modelRow, 0));
    String trangThaiHienTai = String.valueOf(model.getValueAt(modelRow, 4));
    String trangThaiChuan = StatusNormalizer.normalizeLichKhamStatus(
      trangThaiHienTai
    );

    if (
      StatusNormalizer.DA_XAC_NHAN.equals(status) &&
      StatusNormalizer.DA_HUY.equals(trangThaiChuan)
    ) {
      DialogHelper.warn(this, "Lịch khám đã hủy thì không thể xác nhận lại.");
      return;
    }

    String message = lichKhamBUS.updateTrangThai(maLich, status);
    if (message.contains("thành công") || message.contains("thanh cong")) {
      DialogHelper.info(this, "Cập nhật trạng thái thành công.");
      loadData();
      return;
    }
    DialogHelper.warn(this, message);
  }

  private void loadData() {
    model.setRowCount(0);

    String maBacSi = Session.getCurrentBacSiID();
    ArrayList<LichKhamDTO> data =
      maBacSi == null
        ? lichKhamBUS.getAll()
        : lichKhamBUS.getByMaBacSi(maBacSi);

    for (LichKhamDTO row : data) {
      model.addRow(
        new Object[] {
          row.getMaLichKham(),
          row.getMaGoi(),
          row.getThoiGianBatDau(),
          row.getThoiGianKetThuc(),
          row.getTrangThai(),
        }
      );
    }
  }

  private void apDungPhanQuyenHanhDong() {
    boolean coQuyenXem = Session.coMotTrongCacQuyen("LICHKHAM_XEM");
    boolean coQuyenCapNhat = Session.coMotTrongCacQuyen("LICHKHAM_SUA");
    boolean coQuyenHuy = Session.coMotTrongCacQuyen("LICHKHAM_HUY");

    if (btnTaiLai != null) btnTaiLai.setVisible(coQuyenXem);
    if (btnXacNhan != null) btnXacNhan.setVisible(coQuyenCapNhat);
    if (btnHuy != null) btnHuy.setVisible(coQuyenHuy);
    if (table != null) table.setEnabled(coQuyenXem);
  }
}
