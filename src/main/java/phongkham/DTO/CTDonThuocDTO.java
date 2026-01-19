package phongkham.DTO;

public class CTDonThuocDTO {

    private String MaCTDonThuoc;
    private String MaDonThuoc;
    private String MaThuoc;
    private int SoLuong ;
    private String LieuDung;
    private String CachDung;
    
    public CTDonThuocDTO () {
        MaCTDonThuoc ="";
        MaDonThuoc ="";
        MaThuoc ="";
        SoLuong =0;
        LieuDung ="";
        CachDung ="";
    }

    public CTDonThuocDTO ( String MaCTDT,String MaDonThuoc,  String MaThuoc, int SoLuong, String LieuDung, String CachDung ) {
        this.MaCTDonThuoc=MaCTDonThuoc;
        this.MaDonThuoc = MaDonThuoc;
        this.MaThuoc = MaThuoc;
        this.Soluong = Soluong;
        this.LieuDung = LieuDung;
        this.CachDung=CachDung;
    }

    public String gettMaCTDonThuoc() {return MaCTDonThuoc;}
    public String getMaDonThuoc() {return MaDonThuoc;}
    public String getMaThuoc() {return MaThuoc;}
    public String getSoluong() {return SoLuong;}
    public String getLieuDung() {return LieuDung;}
    public String getCachDung() {return CachDung;}

    public void setMaCTDonThuoc(String MaCTDonTthuoc) {this.MaCTDonThuoc=MaCTDonThuoc;}
    public void setMaDonThuoc(String MaDonThuoc) {this.MaDonThuoc=MaDonThuoc;}
    public void setMaThuoc(String MaThuoc) {this.MaThuoc=MaThuoc;}
    public void setSoLuong( int SoLuong) {this.SoLuong=Soluong;}
    public void setLieuDung( String LieuDung) {this.LieuDung=LieuDung;}
    public void setCachDung( String CachDung) {this.CachDung=CachDung;}
}

