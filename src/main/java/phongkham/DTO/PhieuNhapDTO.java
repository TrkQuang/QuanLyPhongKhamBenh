package phongkham.DTO;

import java.sql.Date;

public class PhieuNhapDTO {
    private String MaPhieuNhap;
    private String MaNCC;
    private Date NgayNhap;
    private String NguoiGiao;
    private float TongTienNhap;
    private String trangThai;

    public PhieuNhapDTO(){
        MaPhieuNhap = "";
        MaNCC = "";
        NgayNhap = null;
        NguoiGiao = "";
        TongTienNhap = 0;
    }

    public PhieuNhapDTO(String MaPhieuNhap, String MaNCC, Date NgayNhap, String NguoiGiao, float TongTienNhap, String trangThai){
        this.MaPhieuNhap = MaPhieuNhap;
        this.MaNCC = MaNCC;
        this.NgayNhap = NgayNhap;
        this.NguoiGiao = NguoiGiao;
        this.TongTienNhap = TongTienNhap;
        this.trangThai = trangThai;
    }

    public PhieuNhapDTO(PhieuNhapDTO pn){
        this.MaPhieuNhap = pn.MaPhieuNhap;
        this.MaNCC = pn.MaNCC;
        this.NgayNhap = pn.NgayNhap;
        this.NguoiGiao = pn.NguoiGiao;
        this.TongTienNhap = pn.TongTienNhap;
        this.trangThai = pn.trangThai;
    }

    public String getMaPhieuNhap() {return MaPhieuNhap;}
    public String getMaNCC() {return MaNCC;}
    public Date getNgayNhap() {return NgayNhap;}
    public String getNguoiGiao() {return NguoiGiao;}
    public float getTongTienNhap() {return TongTienNhap;}
    public String getTrangThai() { return trangThai; }


    public void setMaPhieuNhap(String MaPhieuNhap) {this.MaPhieuNhap = MaPhieuNhap;}
    public void setMaNCC(String MaNCC) {this.MaNCC = MaNCC;}
    public void setNgayNhap(Date NgayNhap) {this.NgayNhap = NgayNhap;}
    public void setNguoiGiao(String NguoiGiao) {this.NguoiGiao = NguoiGiao;}
    public void setTongTienNhap(float TongTienNhap) {this.TongTienNhap = TongTienNhap;}
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    @Override 
    public String toString(){
        return "PhieuNhap{" + "MaPhieuNhap=" +MaPhieuNhap+ ", MaNCC=" +MaNCC+ ", NgayNhap='" +NgayNhap+ '\'' + ", NguoiGiao='" +NguoiGiao+ '\'' +", TongTienNhap" +TongTienNhap+ '}';
    }
}
