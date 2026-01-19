package phongkham.DTO;

public class CTDonThuocDTO {

    private String MaCTDT;
    private String MaDonThuoc;
    private String MaThuoc;
    private int SoLuong ;
    private String LieuDung;
    private String CachDung;
    
    public CTDonThuocDTO () {
        MaCTDT ="";
        MaDonThuoc ="";
        MaThuoc ="";
        SoLuong =0;
        LieuDung ="";
        CachDung ="";
    }

    public CTDonThuocDTO ( String MaCTDT,String MaDonThuoc,  String MaThuoc, int SoLuong, String LieuDung, String CachDung ) {
        this.MaCTDT=MaCTDT;
        this.MaDonThuoc = MaDonThuoc;
        this.MaThuoc = MaThuoc;
        this.Soluong = Soluong;
        this.LieuDung = LieuDung;
        this.CachDung=CachDung;
    }

    public String gettMaCTDT() {return MaCTDT;}
    public String getMaDonThuoc() {return MaDonThuoc;}
    public String getMaThuoc() {return MaThuoc;}
    public String getSoluong() {return SoLuong;}
    public String getLieuDung() {return LieuDung;}
    public String getCachDung() {return CachDung;}

    public void setMaCTDT(String MaCTDT) {this.MaCTDT=MaCTDT;}
    public void setMaDonThuoc(String MaDonThuoc) {this.MaDonThuoc=MaDonThuoc;}
    public void setMaThuoc(String MaThuoc) {this.MaThuoc=MaThuoc;}
    public void setSoLuong( int SoLuong) {this.SoLuong=Soluong;}
    public void setLieuDung( String LieuDung) {this.LieuDung=LieuDung;}
    public void setCachDung( String CachDung) {this.CachDung=CachDung;}
}

