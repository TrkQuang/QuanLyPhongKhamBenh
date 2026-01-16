package phongkham.DTO;

public class PhieuNhapDTO {
    private String MaPhieuNhap;
    private String MaNhaCungCap;
    private String NgayNhap;
    private String NguoiGiao;
    private int TongTienNhap;

    public PhieuNhapDTO(){
        MaPhieuNhap = "";
        MaNhaCungCap = "";
        NgayNhap = "";
        NguoiGiao = "";
        TongTienNhap = 0;
    }

    public PhieuNhapDTO(String MaPhieuNhap, String MaNhaCungCap, String NgayNhap, String NguoiGiao, int TongTienNhap){
        this.MaPhieuNhap = MaPhieuNhap;
        this.MaNhaCungCap = MaNhaCungCap;
        this.NgayNhap = NgayNhap;
        this.NguoiGiao = NguoiGiao;
        this.TongTienNhap = TongTienNhap;
    }

    public PhieuNhapDTO(PhieuNhapDTO pn){
        this.MaPhieuNhap = pn.MaPhieuNhap;
        this.MaNhaCungCap = pn.MaNhaCungCap;
        this.NgayNhap = pn.NgayNhap;
        this.NguoiGiao = pn.NguoiGiao;
        this.TongTienNhap = pn.TongTienNhap;
    }

    public String getMaPhieuNhap() {return MaPhieuNhap;}
    public String getMaNhaCungCap() {return MaNhaCungCap;}
    public String getNgayNhap() {return NgayNhap;}
    public String getNguoiGiao() {return NguoiGiao;}
    public int getTongTienNhap() {return TongTienNhap;}

    public void setMaPhieuNhap(String MaPhieuNhap) {this.MaPhieuNhap = MaPhieuNhap;}
    public void setMaNhaCungCap(String MaNhaCungCap) {this.MaNhaCungCap = MaNhaCungCap;}
    public void setNgayNhap(String NgayNhap) {this.NgayNhap = NgayNhap;}
    public void setNguoiGiao(String NguoiGiao) {this.NguoiGiao = NguoiGiao;}
    public void setTongTienNhap(int TongTienNhap) {this.TongTienNhap = TongTienNhap;}
}
