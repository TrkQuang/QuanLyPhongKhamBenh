package phongkham.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

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
        HoaDonKhamDAO hoaDonDAO = new HoaDonKhamDAO();
        HoaDonKhamDTO hoaDon = hoaDonDAO.Search(maHD);

        if (hoaDon == null) {
            return false;
        }

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        // ===== FONT TIẾNG VIỆT =====
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

        // ===== HEADER PHÒNG KHÁM =====
        Paragraph clinic = new Paragraph("PHÒNG KHÁM ĐA KHOA", headerFont);
        clinic.setAlignment(Element.ALIGN_CENTER);
        document.add(clinic);

        Paragraph hotline = new Paragraph("Hotline: 1900-8888", normalFont);
        hotline.setAlignment(Element.ALIGN_CENTER);
        hotline.setSpacingAfter(10);
        document.add(hotline);

        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);

        // ===== TIÊU ĐỀ =====
        Paragraph title = new Paragraph("HÓA ĐƠN KHÁM BỆNH", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // ===== BẢNG THÔNG TIN =====
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{2, 3});

        infoTable.addCell(createCell("Mã hóa đơn:", headerFont));
        infoTable.addCell(createCell(hoaDon.getMaHDKham(), normalFont));

        infoTable.addCell(createCell("Mã gói dịch vụ:", headerFont));
        infoTable.addCell(createCell(hoaDon.getMaGoi(), normalFont));

        if (hoaDon.getNgayThanhToan() != null) {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            infoTable.addCell(createCell("Ngày thanh toán:", headerFont));
            infoTable.addCell(createCell(
                    hoaDon.getNgayThanhToan().format(formatter),
                    normalFont
            ));
        }

        String hinhThuc = hoaDon.getHinhThucThanhToan() != null
                ? hoaDon.getHinhThucThanhToan()
                : "Chưa thanh toán";

        infoTable.addCell(createCell("Hình thức thanh toán:", headerFont));
        infoTable.addCell(createCell(hinhThuc, normalFont));

        infoTable.addCell(createCell("Trạng thái:", headerFont));
        infoTable.addCell(createCell(hoaDon.getTrangThai(), normalFont));

        document.add(infoTable);

        // ===== TỔNG TIỀN NỔI BẬT =====
        NumberFormat currencyFormat =
                NumberFormat.getInstance(new Locale("vi", "VN"));
        String tongTienStr =
                currencyFormat.format(hoaDon.getTongTien()) + " VNĐ";

        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);

        PdfPCell empty = new PdfPCell(new Phrase(""));
        empty.setBorder(Rectangle.NO_BORDER);
        totalTable.addCell(empty);

        PdfPCell totalCell =
                new PdfPCell(new Phrase("TỔNG TIỀN: " + tongTienStr, headerFont));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setBorder(Rectangle.NO_BORDER);
        totalCell.setPadding(10);
        totalTable.addCell(totalCell);

        document.add(totalTable);

        document.add(Chunk.NEWLINE);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);

        // ===== FOOTER =====
        Paragraph thanks = new Paragraph(
                "Cảm ơn quý khách đã sử dụng dịch vụ!",
                normalFont
        );
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);

        document.close();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
  private static PdfPCell createCell(String text, Font font) {
      PdfPCell cell = new PdfPCell(new Phrase(text, font));
      cell.setPadding(8);
      return cell;
  }
}
