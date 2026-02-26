package phongkham.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import phongkham.DTO.GoiDichVuDTO;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.DTO.HoaDonKhamDTO;
import phongkham.dao.GoiDichVuDAO;
import phongkham.dao.HoSoBenhAnDAO;
import phongkham.dao.HoaDonKhamDAO;

public class HoaDonPDF {

  // ✅ SỬA: Đường dẫn font
  private static final String FONT_PATH =
    "src/main/resources/fonts/vuArial.ttf";
  private static BaseFont baseFont;

  static {
    try {
      baseFont = BaseFont.createFont(
        FONT_PATH,
        BaseFont.IDENTITY_H,
        BaseFont.EMBEDDED
      );
      System.out.println("✅ Đã load font tiếng Việt: " + FONT_PATH);
    } catch (Exception e) {
      System.err.println("❌ Không tải được font: " + FONT_PATH);
      System.err.println(
        "⚠️ Sẽ dùng font mặc định (có thể không hiển thị tiếng Việt)"
      );
      try {
        // ✅ FALLBACK: Dùng font hệ thống
        baseFont = BaseFont.createFont(
          "c:/windows/fonts/arial.ttf", // Font Arial của Windows
          BaseFont.IDENTITY_H,
          BaseFont.EMBEDDED
        );
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public static boolean exportHoaDonToPDF(String maHDKham, String outputPath) {
    try {
      // 1. Lấy thông tin hóa đơn
      HoaDonKhamDAO hoaDonDAO = new HoaDonKhamDAO();
      HoaDonKhamDTO hoaDon = hoaDonDAO.Search(maHDKham);

      if (hoaDon == null) {
        System.err.println("❌ Không tìm thấy hóa đơn: " + maHDKham);
        return false;
      }

      // 2. Lấy thông tin hồ sơ bệnh án
      HoSoBenhAnDAO hoSoDAO = new HoSoBenhAnDAO();
      HoSoBenhAnDTO hoSo = hoSoDAO.getByMaHoSo(hoaDon.getMaHoSo());

      if (hoSo == null) {
        System.err.println("❌ Không tìm thấy hồ sơ: " + hoaDon.getMaHoSo());
        return false;
      }

      // 3. Lấy thông tin gói dịch vụ
      GoiDichVuDTO goiDV = null;
      if (hoaDon.getMaGoi() != null && !hoaDon.getMaGoi().isEmpty()) {
        GoiDichVuDAO goiDVDAO = new GoiDichVuDAO();
        goiDV = goiDVDAO.getByMaGoi(hoaDon.getMaGoi());
      }

      // 4. Tạo document PDF
      Document document = new Document(PageSize.A5);
      document.setMargins(20, 20, 20, 20); // ✅ THÊM: Margin
      PdfWriter.getInstance(document, new FileOutputStream(outputPath));
      document.open();

      // 5. Tạo fonts
      Font titleFont = new Font(baseFont, 18, Font.BOLD);
      Font headerFont = new Font(baseFont, 12, Font.BOLD);
      Font normalFont = new Font(baseFont, 10, Font.NORMAL);
      Font smallFont = new Font(baseFont, 8, Font.ITALIC);

      // 6. Header - Logo và tên phòng khám
      Paragraph title = new Paragraph("HÓA ĐƠN KHÁM BỆNH", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(10);
      document.add(title);

      Paragraph clinicName = new Paragraph(
        "PHÒNG KHÁM ĐA KHOA ABC",
        headerFont
      );
      clinicName.setAlignment(Element.ALIGN_CENTER);
      document.add(clinicName);

      Paragraph clinicInfo = new Paragraph(
        "Địa chỉ: 123 Nguyễn Huệ, Q1, TP.HCM\nĐiện thoại: 028-1234-5678",
        smallFont
      );
      clinicInfo.setAlignment(Element.ALIGN_CENTER);
      clinicInfo.setSpacingAfter(15);
      document.add(clinicInfo);

      // 7. Thông tin hóa đơn
      document.add(
        new Paragraph("Mã hóa đơn: " + hoaDon.getMaHDKham(), normalFont)
      );

      // ✅ FIX: Xử lý LocalDateTime
      String ngayThanhToan = "Chưa thanh toán";
      if (hoaDon.getNgayThanhToan() != null) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
          "dd/MM/yyyy HH:mm"
        );
        ngayThanhToan = hoaDon.getNgayThanhToan().format(dateFormatter);
      }
      document.add(
        new Paragraph("Ngày thanh toán: " + ngayThanhToan, normalFont)
      );

      // ✅ FIX: Format trạng thái đẹp hơn
      String trangThai = hoaDon.getTrangThai().replace("_", " ");
      document.add(new Paragraph("Trạng thái: " + trangThai, normalFont));

      // Đường kẻ ngang
      Paragraph line1 = new Paragraph(
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        normalFont
      );
      line1.setSpacingBefore(5);
      line1.setSpacingAfter(5);
      document.add(line1);

      // 8. Thông tin bệnh nhân
      Paragraph patientHeader = new Paragraph(
        "THÔNG TIN BỆNH NHÂN",
        headerFont
      );
      patientHeader.setSpacingBefore(5);
      document.add(patientHeader);

      document.add(new Paragraph("Họ tên: " + hoSo.getHoTen(), normalFont));
      document.add(
        new Paragraph("Số điện thoại: " + hoSo.getSoDienThoai(), normalFont)
      );
      document.add(
        new Paragraph(
          "CCCD: " + (hoSo.getCCCD() != null ? hoSo.getCCCD() : "N/A"),
          normalFont
        )
      );

      // ✅ FIX: Xử lý Date
      String ngaySinh = "N/A";
      if (hoSo.getNgaySinh() != null) {
        SimpleDateFormat dateOnlyFormatter = new SimpleDateFormat("dd/MM/yyyy");
        ngaySinh = dateOnlyFormatter.format(hoSo.getNgaySinh());
      }
      document.add(new Paragraph("Ngày sinh: " + ngaySinh, normalFont));
      document.add(
        new Paragraph("Giới tính: " + hoSo.getGioiTinh(), normalFont)
      );

      if (hoSo.getDiaChi() != null && !hoSo.getDiaChi().isEmpty()) {
        document.add(new Paragraph("Địa chỉ: " + hoSo.getDiaChi(), normalFont));
      }

      Paragraph line2 = new Paragraph(
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        normalFont
      );
      line2.setSpacingBefore(5);
      line2.setSpacingAfter(5);
      document.add(line2);

      // 9. Thông tin khám bệnh
      Paragraph examHeader = new Paragraph("THÔNG TIN KHÁM BỆNH", headerFont);
      examHeader.setSpacingBefore(5);
      document.add(examHeader);

      String ngayKham = "Chưa khám";
      if (hoSo.getNgayKham() != null) {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
          "dd/MM/yyyy HH:mm"
        );
        ngayKham = dateTimeFormatter.format(hoSo.getNgayKham());
      }
      document.add(new Paragraph("Ngày khám: " + ngayKham, normalFont));
      document.add(
        new Paragraph(
          "Triệu chứng: " +
            (hoSo.getTrieuChung() != null ? hoSo.getTrieuChung() : "N/A"),
          normalFont
        )
      );
      document.add(
        new Paragraph(
          "Chẩn đoán: " +
            (hoSo.getChanDoan() != null ? hoSo.getChanDoan() : "N/A"),
          normalFont
        )
      );
      document.add(
        new Paragraph(
          "Kết luận: " +
            (hoSo.getKetLuan() != null ? hoSo.getKetLuan() : "N/A"),
          normalFont
        )
      );

      if (hoSo.getLoiDan() != null && !hoSo.getLoiDan().isEmpty()) {
        document.add(new Paragraph("Lời dặn: " + hoSo.getLoiDan(), normalFont));
      }

      Paragraph line3 = new Paragraph(
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        normalFont
      );
      line3.setSpacingBefore(5);
      line3.setSpacingAfter(5);
      document.add(line3);

      // 10. Chi tiết thanh toán
      Paragraph paymentHeader = new Paragraph(
        "CHI TIẾT THANH TOÁN",
        headerFont
      );
      paymentHeader.setSpacingBefore(5);
      document.add(paymentHeader);

      // ✅ Tạo bảng đẹp hơn
      PdfPTable table = new PdfPTable(3);
      table.setWidthPercentage(100);
      table.setWidths(new float[] { 3, 1.5f, 1.5f }); // ✅ Tỷ lệ cột đẹp hơn
      table.setSpacingBefore(5);

      // Header bảng
      Font tableFontBold = new Font(baseFont, 10, Font.BOLD);

      PdfPCell headerCell1 = new PdfPCell(new Phrase("Dịch vụ", tableFontBold));
      headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
      headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
      headerCell1.setPadding(5);

      PdfPCell headerCell2 = new PdfPCell(new Phrase("Đơn giá", tableFontBold));
      headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
      headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
      headerCell2.setPadding(5);

      PdfPCell headerCell3 = new PdfPCell(
        new Phrase("Thành tiền", tableFontBold)
      );
      headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
      headerCell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
      headerCell3.setPadding(5);

      table.addCell(headerCell1);
      table.addCell(headerCell2);
      table.addCell(headerCell3);

      // Dữ liệu bảng
      NumberFormat currencyFormat = NumberFormat.getInstance(
        new Locale("vi", "VN")
      );

      String tenDichVu = goiDV != null ? goiDV.getTenGoi() : "Khám bệnh";
      PdfPCell dataCell1 = new PdfPCell(new Phrase(tenDichVu, normalFont));
      dataCell1.setPadding(5);

      PdfPCell dataCell2 = new PdfPCell(
        new Phrase(currencyFormat.format(hoaDon.getTongTien()), normalFont)
      );
      dataCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
      dataCell2.setPadding(5);

      PdfPCell dataCell3 = new PdfPCell(
        new Phrase(currencyFormat.format(hoaDon.getTongTien()), normalFont)
      );
      dataCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
      dataCell3.setPadding(5);

      table.addCell(dataCell1);
      table.addCell(dataCell2);
      table.addCell(dataCell3);

      document.add(table);

      // Tổng tiền
      Paragraph totalAmount = new Paragraph(
        "TỔNG CỘNG: " + currencyFormat.format(hoaDon.getTongTien()) + " VNĐ",
        headerFont
      );
      totalAmount.setAlignment(Element.ALIGN_RIGHT);
      totalAmount.setSpacingBefore(10);
      document.add(totalAmount);

      document.add(
        new Paragraph(
          "Hình thức thanh toán: " + hoaDon.getHinhThucThanhToan(),
          normalFont
        )
      );

      // 11. Chữ ký
      Paragraph signature = new Paragraph(
        "\n\n\nNgười lập phiếu\n(Ký, ghi rõ họ tên)",
        smallFont
      );
      signature.setAlignment(Element.ALIGN_RIGHT);
      signature.setSpacingBefore(20);
      document.add(signature);

      // 12. Footer
      Paragraph footer = new Paragraph(
        "Cảm ơn quý khách đã tin tưởng sử dụng dịch vụ!",
        smallFont
      );
      footer.setAlignment(Element.ALIGN_CENTER);
      footer.setSpacingBefore(20);
      document.add(footer);

      document.close();
      System.out.println("✅ Xuất PDF thành công: " + outputPath);
      return true;
    } catch (Exception e) {
      System.err.println("❌ Lỗi xuất PDF: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public static void main(String[] args) {
    String outputPath = "HoaDon_HDK001.pdf";
    boolean success = exportHoaDonToPDF("HDK001", outputPath);

    if (success) {
      System.out.println("✅ Hóa đơn đã được xuất thành công!");
    } else {
      System.out.println("❌ Xuất hóa đơn thất bại!");
    }
  }
}
