package phongkham.DTO;

public class DonThuocDTO {
    private String MaDonThuoc;
    private String MaHoSo;
    private String NgayKeDon;
    private String GhiChu;
    
    public DonThuocDTO {
        MaDonThuoc =" ";
        MaHoSo =" ";
        NgayKeDon =" ";
        GhiChu =" ";
    }

    public DonThuocDTO ( String MaDonThuoc, String MaHoSo, String NgayKeDon, String GhiChu) {
        This.MaDonThuoc = MaDonThuoc;
        This.MaHoSo = MaHoSo;
        This.NgayKeDon = NgayKeDon;
        This.GhiChu = GhiChu;
    }

    public String getMaDonThuoc() {return MaDonThuoc;}
    public String getMaHoSo() {return MaHoSo;}
    public String getNgayKeDon() {return NgayKeDon;}
    public String getGhiChu() {return GhiChu;}

    public void setMaDonThuoc(String MaDonThuoc) {this.MaDonThuoc=MaDonThuoc;}
    public void setMaHoSo(String MaHoSo) {this.MaHoSo=MaHoSo;}
    public void setNgayKeDon( String NgayKeDon) {this.NgayKeDon=NgayKeDon;}
    public void setGhiChu( String GhiChu) {this.GhiChu=GhiChu;}
}


