package phongkham.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfExport {

    private static final String FONT_PATH = "C:\\Windows\\Fonts\\arial.ttf";

    public static void exportTable(JTable table, String titleName) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một hóa đơn!");
            return;
        }

        String maHD = table.getValueAt(row, 0).toString();
        String maPhieu = table.getValueAt(row, 1).toString();
        String maGoi = table.getValueAt(row, 2).toString();
        String ngay = table.getValueAt(row, 3).toString();
        String tongTien = table.getValueAt(row, 4).toString();
        String hinhThuc = table.getValueAt(row, 5).toString();

        Document document = new Document(PageSize.A5, 30, 30, 20, 20);

        try {
            String fileName = "HoaDon_" + maHD + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            BaseFont bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font fTitle = new Font(bf, 16, Font.BOLD);
            Font fHeader = new Font(bf, 12, Font.BOLD);
            Font fNormal = new Font(bf, 11);
            Font fSmall = new Font(bf, 10);

            // ===== HEADER =====
            Paragraph clinic = new Paragraph("PHÒNG KHÁM ĐA KHOA", fHeader);
            clinic.setAlignment(Element.ALIGN_CENTER);
            document.add(clinic);

            Paragraph info = new Paragraph(
                    "Địa chỉ: 123 Nguyễn Trãi, Q1, TP.HCM\n" +
                    "Hotline: 1900-8888  |  Email: contact@phongkham.vn",
                    fSmall
            );
            info.setAlignment(Element.ALIGN_CENTER);
            document.add(info);

            addLine(document);

            // ===== TITLE =====
            Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", fTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // ===== INFO =====
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.3f, 2.7f});

            addInfoRow(infoTable, "Mã hóa đơn:", maHD, fNormal);
            addInfoRow(infoTable, "Mã phiếu khám:", maPhieu, fNormal);
            addInfoRow(infoTable, "Ngày:", ngay, fNormal);
            addInfoRow(infoTable, "Thanh toán:", hinhThuc, fNormal);

            document.add(infoTable);

            addLine(document);

            // ===== TABLE =====
            PdfPTable tablePDF = new PdfPTable(2);
            tablePDF.setWidthPercentage(100);
            tablePDF.setWidths(new float[]{2.5f, 1.5f});

            addHeaderCell(tablePDF, "Dịch vụ", fHeader);
            addHeaderCell(tablePDF, "Thành tiền", fHeader);

            addCell(tablePDF, "Gói khám: " + maGoi, fNormal, Element.ALIGN_LEFT);
            addCell(tablePDF, tongTien, fNormal, Element.ALIGN_RIGHT);

            document.add(tablePDF);

            addLine(document);

            // ===== TOTAL =====
            Paragraph total = new Paragraph("TỔNG CỘNG: " + tongTien, fHeader);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph(" "));

            // ===== DATE =====
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Paragraph date = new Paragraph("Ngày in: " + LocalDateTime.now().format(dtf), fSmall);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            document.add(new Paragraph("\n\n"));

            // ===== SIGN =====
            PdfPTable sign = new PdfPTable(2);
            sign.setWidthPercentage(100);

            addSignCell(sign, "Khách hàng\n(Ký, ghi rõ họ tên)", fSmall);
            addSignCell(sign, "Thu ngân\n(Ký, ghi rõ họ tên)", fSmall);

            document.add(sign);

            document.close();
            Desktop.getDesktop().open(new File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== UTILS =====
    private static void addLine(Document doc) throws Exception {
        doc.add(new Paragraph(" "));
        LineSeparator ls = new LineSeparator(1, 100, BaseColor.GRAY, Element.ALIGN_CENTER, 0);
        doc.add(ls);
        doc.add(new Paragraph(" "));
    }

    private static void addInfoRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, font));
        PdfPCell c2 = new PdfPCell(new Phrase(value, font));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);
        table.addCell(c2);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(230,230,230));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void addCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private static void addSignCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}


// Hàm phụ trợ để thêm dòng thông tin không viền
// private static void addInfoRow(PdfPTable table, String label, String value, Font font) {
//     PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
//     cellLabel.setBorder(Rectangle.NO_BORDER);
//     cellLabel.setPaddingBottom(5);
    
//     PdfPCell cellValue = new PdfPCell(new Phrase(value, font));
//     cellValue.setBorder(Rectangle.NO_BORDER);
//     cellValue.setPaddingBottom(5);
    
//     table.addCell(cellLabel);
//     table.addCell(cellValue);
// }
