package phongkham.DTO;

public class PhieuNhapDTO {
    private String MaPhieuNhap;
    private String NgayNhap;
    private String NguoiGiao;
    private float TongTienNhap;

    public PhieuNhapDTO(){
        MaPhieuNhap = "";
        NgayNhap = "";
        NguoiGiao = "";
        TongTienNhap = 0;
    }

    public PhieuNhapDTO(String MaPhieuNhap, String NgayNhap, String NguoiGiao, float TongTienNhap){
        this.MaPhieuNhap = MaPhieuNhap;
        this.NgayNhap = NgayNhap;
        this.NguoiGiao = NguoiGiao;
        this.TongTienNhap = TongTienNhap;
    }

    public PhieuNhapDTO(PhieuNhapDTO pn){
        this.MaPhieuNhap = pn.MaPhieuNhap;
        this.NgayNhap = pn.NgayNhap;
        this.NguoiGiao = pn.NguoiGiao;
        this.TongTienNhap = pn.TongTienNhap;
    }

    public String getMaPhieuNhap() {return MaPhieuNhap;}
    public String getNgayNhap() {return NgayNhap;}
    public String getNguoiGiao() {return NguoiGiao;}
    public float getTongTienNhap() {return TongTienNhap;}

    public void setMaPhieuNhap(String MaPhieuNhap) {this.MaPhieuNhap = MaPhieuNhap;}
    public void setNgayNhap(String NgayNhap) {this.NgayNhap = NgayNhap;}
    public void setNguoiGiao(String NguoiGiao) {this.NguoiGiao = NguoiGiao;}
    public void setTongTienNhap(float TongTienNhap) {this.TongTienNhap = TongTienNhap;}
}
