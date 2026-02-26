package phongkham.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.dao.HoaDonKhamDAO;

/**
 * Class xuất PDF đơn giản cho hóa đơn khám bệnh
 */
public class PdfExport {

  /**
   * Xuất hóa đơn đã chọn trong JTable ra PDF
   * @param table Bảng chứa danh sách hóa đơn
   * @param titleName Tên tiêu đề (không sử dụng, giữ để tương thích)
   */
  public static void exportTable(JTable table, String titleName) {
    int row = table.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(
        null,
        "Vui lòng chọn một hóa đơn để xuất PDF!",
        "Thông báo",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }

    // Lấy mã hóa đơn từ cột đầu tiên
    String maHD = table.getValueAt(row, 0).toString();

    try {
      // Tên file PDF
      String fileName = "HoaDon_" + maHD + ".pdf";

      // Xuất PDF đơn giản
      boolean success = exportSimplePDF(maHD, fileName);

      if (success) {
        // Hỏi người dùng có muốn mở file không
        int option = JOptionPane.showConfirmDialog(
          null,
          "Xuất hóa đơn PDF thành công!\nBạn có muốn mở file không?",
          "Thành công",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.INFORMATION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
          File pdfFile = new File(fileName);
          if (pdfFile.exists()) {
            Desktop.getDesktop().open(pdfFile);
          }
        }
      } else {
        JOptionPane.showMessageDialog(
          null,
          "Xuất hóa đơn PDF thất bại!\nVui lòng kiểm tra lại dữ liệu.",
          "Lỗi",
          JOptionPane.ERROR_MESSAGE
        );
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
        null,
        "Lỗi khi xuất PDF: " + e.getMessage(),
        "Lỗi",
        JOptionPane.ERROR_MESSAGE
      );
      e.printStackTrace();
    }
  }

  /**
   * Xuất PDF đơn giản với thông tin cơ bản của hóa đơn
   */
  private static boolean exportSimplePDF(String maHD, String outputPath) {
    try {
      // Lấy thông tin hóa đơn từ database
      HoaDonKhamDAO hoaDonDAO = new HoaDonKhamDAO();
      HoaDonKhamDTO hoaDon = hoaDonDAO.Search(maHD);

      if (hoaDon == null) {
        return false;
      }

      // Tạo document PDF
      Document document = new Document(PageSize.A4);
      PdfWriter.getInstance(document, new FileOutputStream(outputPath));
      document.open();

      // Font tiếng Việt
      BaseFont baseFont;
      try {
        baseFont = BaseFont.createFont(
          "c:/windows/fonts/arial.ttf",
          BaseFont.IDENTITY_H,
          BaseFont.EMBEDDED
        );
      } catch (Exception e) {
        baseFont = BaseFont.createFont(
          BaseFont.HELVETICA,
          BaseFont.CP1252,
          BaseFont.EMBEDDED
        );
      }

      Font titleFont = new Font(baseFont, 20, Font.BOLD);
      Font headerFont = new Font(baseFont, 14, Font.BOLD);
      Font normalFont = new Font(baseFont, 12, Font.NORMAL);

      // Tiêu đề
      Paragraph title = new Paragraph("HÓA ĐƠN KHÁM BỆNH", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(30);
      document.add(title);

      // Thông tin hóa đơn
      document.add(new Paragraph("THÔNG TIN HÓA ĐƠN", headerFont));
      document.add(new Paragraph(" ", normalFont)); // Spacing

      // Mã hóa đơn
      Paragraph maHoaDon = new Paragraph(
        "Mã hóa đơn: " + hoaDon.getMaHDKham(),
        normalFont
      );
      document.add(maHoaDon);
      document.add(new Paragraph(" ", normalFont));

      // Mã gói dịch vụ
      Paragraph maGoi = new Paragraph(
        "Mã gói dịch vụ: " + hoaDon.getMaGoi(),
        normalFont
      );
      document.add(maGoi);
      document.add(new Paragraph(" ", normalFont));

      // Ngày thanh toán
      if (hoaDon.getNgayThanhToan() != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
          "dd/MM/yyyy HH:mm"
        );
        String ngayThanhToan = hoaDon.getNgayThanhToan().format(formatter);
        Paragraph ngay = new Paragraph(
          "Ngày thanh toán: " + ngayThanhToan,
          normalFont
        );
        document.add(ngay);
        document.add(new Paragraph(" ", normalFont));
      }

      // Tổng tiền
      NumberFormat currencyFormat = NumberFormat.getInstance(
        new Locale("vi", "VN")
      );
      String tongTienStr = currencyFormat.format(hoaDon.getTongTien()) + " VNĐ";
      Paragraph tongTien = new Paragraph(
        "Tổng tiền: " + tongTienStr,
        normalFont
      );
      document.add(tongTien);
      document.add(new Paragraph(" ", normalFont));

      // Hình thức thanh toán
      String hinhThuc =
        hoaDon.getHinhThucThanhToan() != null
          ? hoaDon.getHinhThucThanhToan()
          : "Chưa thanh toán";
      Paragraph hinhThucTT = new Paragraph(
        "Hình thức thanh toán: " + hinhThuc,
        normalFont
      );
      document.add(hinhThucTT);
      document.add(new Paragraph(" ", normalFont));

      // Trạng thái
      Paragraph trangThai = new Paragraph(
        "Trạng thái: " + hoaDon.getTrangThai(),
        normalFont
      );
      document.add(trangThai);

      // Đóng document
      document.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
