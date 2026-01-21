package phongkham.DTO;

public class LichLamViecDTO {
    private String MaLichLam;
    private String MaBacSi;
    private String NgayLam;
    private String CaLam;
    private String GhiChu;

    public LichLamViecDTO() {
        MaLichLam = "";
        MaBacSi = "";
        NgayLam = "";
        CaLam = "";
        GhiChu = "";
    }

    public LichLamViecDTO(String MaLichLam, String MaBacSi, String NgayLam, String CaLam, String GhiChu) {
        this.MaLichLam = MaLichLam;
        this.MaBacSi = MaBacSi;
        this.NgayLam = NgayLam;
        this.CaLam = CaLam;
        this.GhiChu = GhiChu;
    }

    public LichLamViecDTO(LichLamViecDTO llv) {
        this.MaLichLam = llv.MaLichLam;
        this.MaBacSi = llv.MaBacSi;
        this.NgayLam = llv.NgayLam;
        this.CaLam = llv.CaLam;
        this.GhiChu = llv.GhiChu;
    }

    public String getMaLichLam() {
        return MaLichLam;
    }

    public String getMaBacSi() {
        return MaBacSi;
    }

    public String getNgayLam() {
        return NgayLam;
    }

    public String getCaLam() {
        return CaLam;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setMaLichLam(String MaLichLam) {
        this.MaLichLam = MaLichLam;
    }

    public void setMaBacSi(String MaBacSi) {
        this.MaBacSi = MaBacSi;
    }

    public void setNgayLam(String NgayLam) {
        this.NgayLam = NgayLam;
    }

    public void setCaLam(String CaLam) {
        this.CaLam = CaLam;
    }

    public void setGhiChu(String GhiChu) {
        this.GhiChu = GhiChu;
    }

}
