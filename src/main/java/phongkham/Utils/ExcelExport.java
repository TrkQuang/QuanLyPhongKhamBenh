package phongkham.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import phongkham.DTO.ThuocDTO;

public class ExcelExport {

  private ExcelExport() {}

  /**
   * Xuất danh sách thuốc ra file CSV để mở bằng Excel.
   */
  public static boolean exportThuocToCsv(List<ThuocDTO> danhSachThuoc) {
    if (danhSachThuoc == null || danhSachThuoc.isEmpty()) {
      JOptionPane.showMessageDialog(
        null,
        "Không có dữ liệu thuốc để xuất.",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
      return false;
    }

    JFileChooser chonFile = new JFileChooser();
    chonFile.setDialogTitle("Chọn nơi lưu file Excel (CSV)");
    chonFile.setSelectedFile(new File("DanhSachThuoc.csv"));

    int luaChon = chonFile.showSaveDialog(null);
    if (luaChon != JFileChooser.APPROVE_OPTION) {
      return false;
    }

    File tepDich = chuanHoaDuongDanCsv(chonFile.getSelectedFile());

    try (
      BufferedWriter boGhi = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(tepDich),
          StandardCharsets.UTF_8
        )
      )
    ) {
      // Ghi BOM để Excel nhận UTF-8 đúng tiếng Việt.
      boGhi.write("\uFEFF");
      boGhi.write("MaThuoc,TenThuoc,HoatChat,DonViTinh,DonGiaBan,SoLuongTon");
      boGhi.newLine();

      for (ThuocDTO thuoc : danhSachThuoc) {
        String dong =
          escapeCsv(thuoc.getMaThuoc()) +
          "," +
          escapeCsv(thuoc.getTenThuoc()) +
          "," +
          escapeCsv(thuoc.getHoatChat()) +
          "," +
          escapeCsv(thuoc.getDonViTinh()) +
          "," +
          thuoc.getDonGiaBan() +
          "," +
          thuoc.getSoLuongTon();
        boGhi.write(dong);
        boGhi.newLine();
      }

      JOptionPane.showMessageDialog(
        null,
        "Xuất Excel thành công: " + tepDich.getAbsolutePath(),
        "Thành công",
        JOptionPane.INFORMATION_MESSAGE
      );
      return true;
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
        null,
        "Lỗi xuất Excel: " + ex.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      return false;
    }
  }

  /**
   * Xuất dữ liệu đang hiển thị trên JTable ra file CSV.
   */
  public static boolean exportTableToCsv(JTable bangDuLieu, String tenMacDinh) {
    if (bangDuLieu == null || bangDuLieu.getRowCount() == 0) {
      JOptionPane.showMessageDialog(
        null,
        "Bảng không có dữ liệu để xuất.",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
      return false;
    }

    JFileChooser chonFile = new JFileChooser();
    chonFile.setDialogTitle("Chọn nơi lưu file Excel (CSV)");
    chonFile.setSelectedFile(new File(tenMacDinh + ".csv"));

    int luaChon = chonFile.showSaveDialog(null);
    if (luaChon != JFileChooser.APPROVE_OPTION) {
      return false;
    }

    File tepDich = chuanHoaDuongDanCsv(chonFile.getSelectedFile());
    TableModel model = bangDuLieu.getModel();

    try (
      BufferedWriter boGhi = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(tepDich),
          StandardCharsets.UTF_8
        )
      )
    ) {
      boGhi.write("\uFEFF");

      for (int cot = 0; cot < model.getColumnCount(); cot++) {
        boGhi.write(escapeCsv(model.getColumnName(cot)));
        if (cot < model.getColumnCount() - 1) {
          boGhi.write(",");
        }
      }
      boGhi.newLine();

      for (int hang = 0; hang < model.getRowCount(); hang++) {
        for (int cot = 0; cot < model.getColumnCount(); cot++) {
          Object giaTri = model.getValueAt(hang, cot);
          boGhi.write(escapeCsv(giaTri == null ? "" : giaTri.toString()));
          if (cot < model.getColumnCount() - 1) {
            boGhi.write(",");
          }
        }
        boGhi.newLine();
      }

      JOptionPane.showMessageDialog(
        null,
        "Xuất Excel thành công: " + tepDich.getAbsolutePath(),
        "Thành công",
        JOptionPane.INFORMATION_MESSAGE
      );
      return true;
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
        null,
        "Lỗi xuất Excel: " + ex.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      return false;
    }
  }

  private static File chuanHoaDuongDanCsv(File tepDuocChon) {
    if (tepDuocChon.getName().toLowerCase().endsWith(".csv")) {
      return tepDuocChon;
    }
    return new File(tepDuocChon.getAbsolutePath() + ".csv");
  }

  private static String escapeCsv(String giaTri) {
    if (giaTri == null) {
      return "";
    }
    String ketQua = giaTri.replace("\"", "\"\"");
    if (
      ketQua.contains(",") ||
      ketQua.contains("\"") ||
      ketQua.contains("\n") ||
      ketQua.contains("\r")
    ) {
      return "\"" + ketQua + "\"";
    }
    return ketQua;
  }

  /**
   * Xuất bảng nghiệp vụ chuẩn vận hành (CSV mở bằng Excel) kèm metadata.
   */
  public static boolean exportOperationalTableToCsv(
    JTable bangDuLieu,
    String tenBaoCao,
    String boLoc
  ) {
    if (bangDuLieu == null || bangDuLieu.getRowCount() == 0) {
      JOptionPane.showMessageDialog(
        null,
        "Bảng không có dữ liệu để xuất.",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
      return false;
    }

    JFileChooser chonFile = new JFileChooser();
    chonFile.setDialogTitle("Chọn nơi lưu file Excel (CSV)");
    chonFile.setSelectedFile(new File(tenBaoCao + "_Operational.csv"));

    int luaChon = chonFile.showSaveDialog(null);
    if (luaChon != JFileChooser.APPROVE_OPTION) {
      return false;
    }

    File tepDich = chuanHoaDuongDanCsv(chonFile.getSelectedFile());
    TableModel model = bangDuLieu.getModel();
    String thoiGian = LocalDateTime.now().format(
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    );

    try (
      BufferedWriter boGhi = new BufferedWriter(
        new OutputStreamWriter(
          new FileOutputStream(tepDich),
          StandardCharsets.UTF_8
        )
      )
    ) {
      boGhi.write("\uFEFF");

      boGhi.write(escapeCsv("BAO CAO VAN HANH: " + tenBaoCao));
      boGhi.newLine();
      boGhi.write(escapeCsv("Thoi gian xuat: " + thoiGian));
      boGhi.newLine();
      boGhi.write(escapeCsv("Bo loc: " + (boLoc == null ? "Khong" : boLoc)));
      boGhi.newLine();
      boGhi.write(escapeCsv("So dong du lieu: " + model.getRowCount()));
      boGhi.newLine();
      boGhi.newLine();

      for (int cot = 0; cot < model.getColumnCount(); cot++) {
        boGhi.write(escapeCsv(model.getColumnName(cot)));
        if (cot < model.getColumnCount() - 1) {
          boGhi.write(",");
        }
      }
      boGhi.newLine();

      for (int hang = 0; hang < model.getRowCount(); hang++) {
        for (int cot = 0; cot < model.getColumnCount(); cot++) {
          Object giaTri = model.getValueAt(hang, cot);
          boGhi.write(escapeCsv(giaTri == null ? "" : giaTri.toString()));
          if (cot < model.getColumnCount() - 1) {
            boGhi.write(",");
          }
        }
        boGhi.newLine();
      }

      JOptionPane.showMessageDialog(
        null,
        "Xuất Excel thành công: " + tepDich.getAbsolutePath(),
        "Thành công",
        JOptionPane.INFORMATION_MESSAGE
      );
      return true;
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
        null,
        "Lỗi xuất Excel: " + ex.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      return false;
    }
  }
}
