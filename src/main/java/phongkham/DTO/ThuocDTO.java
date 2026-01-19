package phongkham.DTO;

public class ThuocDTO {
    private String MaThuoc;
    private String HoatChat;
    private String DonViTinh;
    private float DonGiaBan;
    private int SoLuongTon;

    public ThuocDTO(){
        MaThuoc = "";
        HoatChat = "";
        DonViTinh = "";
        DonGiaBan = 0;
        SoLuongTon = 0;
    }

    public ThuocDTO(String MaThuoc, String HoatChat, String DonViTinh, float DonGiaBan, int SoLuongTon){
        this.MaThuoc = MaThuoc;
        this.HoatChat = HoatChat;
        this.DonViTinh = DonViTinh;
        this.DonGiaBan = DonGiaBan;
        this.SoLuongTon = SoLuongTon;
    }

    public ThuocDTO(ThuocDTO t){
        this.MaThuoc = t.MaThuoc;
        this.HoatChat = t.HoatChat;
        this.DonViTinh = t.DonViTinh;
        this.DonGiaBan = t.DonGiaBan;
        this.SoLuongTon = t.SoLuongTon;
    }

    public String getMaThuoc() {return MaThuoc;}
    public String getHoatChat() {return HoatChat;}
    public String getDonViTinh() {return DonViTinh;}
    public float getDonGiaBan() {return DonGiaBan;}
    public int getSoLuongTon() {return SoLuongTon;}

    public void setMaThuoc(String MaThuoc) {this.MaThuoc = MaThuoc;}
    public void setHoatChat(String HoatChat) {this.HoatChat = HoatChat;}
    public void setDonViTinh(String DonViTinh) {this.DonViTinh = DonViTinh;}
    public void setDonGiaBan(float DonGiaBan) {this.DonGiaBan = DonGiaBan;}
    public void setSoLuongTon(int SoLuongTon) {this.SoLuongTon = SoLuongTon;}
}
