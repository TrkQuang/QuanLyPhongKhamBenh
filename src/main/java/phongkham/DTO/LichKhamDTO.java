package phongkham.DTO;

public class LichKhamDTO {
    private String MaLichKham;
    private String MaGoi;
    private String MaBacSi;
    private String ThoiGianBatDau;
    private String ThoiGianKetThuc;
    private String TrangThai;
    private String MaDinhDanhTam;

    public LichKhamDTO(){
        MaLichKham = "";
        MaGoi = "";
        MaBacSi = "";
        ThoiGianBatDau = "";
        ThoiGianKetThuc = "";
        TrangThai = "";
        MaDinhDanhTam = "";
    }

    public LichKhamDTO(String MaLichKham, String MaGoi, String MaBacSi, String ThoiGianBatDau, String ThoiGianKetThuc, String TrangThai, String MaDinhDanhTam){
        this.MaLichKham = MaLichKham;
        this.MaGoi = MaGoi;
        this.MaBacSi = MaBacSi;
        this.ThoiGianBatDau = ThoiGianBatDau;
        this.ThoiGianKetThuc = ThoiGianKetThuc;
        this.TrangThai = TrangThai;
        this.MaDinhDanhTam = MaDinhDanhTam;
    }

    public LichKhamDTO(LichKhamDTO lk){
        this.MaLichKham = lk.MaLichKham;
        this.MaGoi = lk.MaGoi;
        this.MaBacSi = lk.MaBacSi;
        this.ThoiGianBatDau = lk.ThoiGianBatDau;
        this.ThoiGianKetThuc = lk.ThoiGianKetThuc;
        this.TrangThai = lk.TrangThai;
        this.MaDinhDanhTam = lk.MaDinhDanhTam;
    }

    public String getMaLichKham() {return MaLichKham;}
    public String getMaGoi() {return MaGoi;}
    public String getMaBacSi() {return MaBacSi;}
    public String getThoiGianBatDau() {return ThoiGianBatDau;}
    public String getThoiGianKetThuc() {return ThoiGianKetThuc;}
    public String getTrangThai() {return TrangThai;}
    public String getMaDinhDanhTam() {return MaDinhDanhTam;}

    public void setMaLichKham(String MaLichKham) {this.MaLichKham = MaLichKham;}
    public void setMaGoi(String MaGoi) {this.MaGoi = MaGoi;}
    public void setMaBacSi(String MaBacSi) {this.MaBacSi = MaBacSi;}
    public void setThoiGianBatDau(String ThoiGianBatDau) {this.ThoiGianBatDau = ThoiGianBatDau;}
    public void setThoiGianKetThuc(String ThoiGianKetThuc) {this.ThoiGianKetThuc = ThoiGianKetThuc;}
    public void setTrangThai(String TrangThai) {this.TrangThai = TrangThai;}
    public void setMaDinhDanhTam(String MaDinhDanhTam) {this.MaDinhDanhTam = MaDinhDanhTam;}
}
