package phongkham.DTO;

public class ThuocDTO {
    private String MaThuoc;
    private String HoaChat;
    private String DonViTinh;
    private int DonGiaBan;
    private int SoLuongTon;

    public ThuocDTO(){
        MaThuoc = "";
        HoaChat = "";
        DonViTinh = "";
        DonGiaBan = 0;
        SoLuongTon = 0;
    }

    public ThuocDTO(String MaThuoc, String HoaChat, String DonViTinh, int DonGiaBan, int SoLuongTon){
        this.MaThuoc = MaThuoc;
        this.HoaChat = HoaChat;
        this.DonViTinh = DonViTinh;
        this.DonGiaBan = DonGiaBan;
        this.SoLuongTon = SoLuongTon;
    }

    public ThuocDTO(ThuocDTO t){
        this.MaThuoc = t.MaThuoc;
        this.HoaChat = t.HoaChat;
        this.DonViTinh = t.DonViTinh;
        this.DonGiaBan = t.DonGiaBan;
        this.SoLuongTon = t.SoLuongTon;
    }

    public String getMaThuoc() {return MaThuoc;}
    public String getHoaChat() {return HoaChat;}
    public String getDonViTinh() {return DonViTinh;}
    public int getDonGiaBan() {return DonGiaBan;}
    public int getSoLuongTon() {return SoLuongTon;}

    public void setMaThuoc(String MaThuoc) {this.MaThuoc = MaThuoc;}
    public void setHoaChat(String HoaChat) {this.HoaChat = HoaChat;}
    public void setDonViTinh(String DonViTinh) {this.DonViTinh = DonViTinh;}
    public void setDonGiaBan(int DonGiaBan) {this.DonGiaBan = DonGiaBan;}
    public void setSoLuongTon(int SoLuongTon) {this.SoLuongTon = SoLuongTon;}
}
