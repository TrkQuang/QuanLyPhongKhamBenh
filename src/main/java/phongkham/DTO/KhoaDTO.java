package phongkham.DTO;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.itextpdf.text.pdf.security.MakeSignature;

public class KhoaDTO {
    private String MaKhoa;
    private String TenKhoa;

    public KhoaDTO() {
        MaKhoa = "";
        TenKhoa = "";
    }

    public KhoaDTO(String MaKhoa, String TenKhoa) {
        this.MaKhoa = MaKhoa;
        this.TenKhoa = TenKhoa;
    }

    public KhoaDTO(KhoaDTO k) {
        this.MaKhoa = k.MaKhoa;
        this.TenKhoa = k.TenKhoa;
    }

    public String getMaKhoa() {
        return MaKhoa;
    }

    public String getTenKhoa() {
        return TenKhoa;
    }

    public void setMaKhoa(String MaKhoa) {
        this.MaKhoa = MaKhoa;
    }

    public void setTenKhoa(String TenKhoa) {
        this.TenKhoa = TenKhoa;
    }
}
