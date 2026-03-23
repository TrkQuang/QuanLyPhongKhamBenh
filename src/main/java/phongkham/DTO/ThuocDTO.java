package phongkham.DTO;

public class ThuocDTO {

  private String MaThuoc;
  private String TenThuoc;
  private String HoatChat;
  private String DonViTinh;
  private float DonGiaBan;
  private int SoLuongTon;
  private boolean Active;

  public ThuocDTO() {
    TenThuoc = "";
    MaThuoc = "";
    HoatChat = "";
    DonViTinh = "";
    DonGiaBan = 0;
    SoLuongTon = 0;
    Active = true;
  }

  public ThuocDTO(
    String MaThuoc,
    String TenThuoc,
    String HoatChat,
    String DonViTinh,
    float DonGiaBan,
    int SoLuongTon
  ) {
    this.MaThuoc = MaThuoc;
    this.TenThuoc = TenThuoc;
    this.HoatChat = HoatChat;
    this.DonViTinh = DonViTinh;
    this.DonGiaBan = DonGiaBan;
    this.SoLuongTon = SoLuongTon;
    this.Active = true;
  }

  public ThuocDTO(
    String MaThuoc,
    String TenThuoc,
    String HoatChat,
    String DonViTinh,
    float DonGiaBan,
    int SoLuongTon,
    boolean Active
  ) {
    this.MaThuoc = MaThuoc;
    this.TenThuoc = TenThuoc;
    this.HoatChat = HoatChat;
    this.DonViTinh = DonViTinh;
    this.DonGiaBan = DonGiaBan;
    this.SoLuongTon = SoLuongTon;
    this.Active = Active;
  }

  public ThuocDTO(ThuocDTO t) {
    this.MaThuoc = t.MaThuoc;
    this.TenThuoc = t.TenThuoc;
    this.HoatChat = t.HoatChat;
    this.DonViTinh = t.DonViTinh;
    this.DonGiaBan = t.DonGiaBan;
    this.SoLuongTon = t.SoLuongTon;
    this.Active = t.Active;
  }

  public String getMaThuoc() {
    return MaThuoc;
  }

  public String getTenThuoc() {
    return TenThuoc;
  }

  public String getHoatChat() {
    return HoatChat;
  }

  public String getDonViTinh() {
    return DonViTinh;
  }

  public float getDonGiaBan() {
    return DonGiaBan;
  }

  public int getSoLuongTon() {
    return SoLuongTon;
  }

  public boolean isActive() {
    return Active;
  }

  public void setMaThuoc(String MaThuoc) {
    this.MaThuoc = MaThuoc;
  }

  public void setTenThuoc(String TenThuoc) {
    this.TenThuoc = TenThuoc;
  }

  public void setHoatChat(String HoatChat) {
    this.HoatChat = HoatChat;
  }

  public void setDonViTinh(String DonViTinh) {
    this.DonViTinh = DonViTinh;
  }

  public void setDonGiaBan(float DonGiaBan) {
    this.DonGiaBan = DonGiaBan;
  }

  public void setSoLuongTon(int SoLuongTon) {
    this.SoLuongTon = SoLuongTon;
  }

  public void setActive(boolean Active) {
    this.Active = Active;
  }

  @Override
  public String toString() {
    return (
      "Thuoc{" +
      "MaThuoc=" +
      MaThuoc +
      ", TenThuoc='" +
      TenThuoc +
      '\'' +
      ", HoatChat='" +
      HoatChat +
      '\'' +
      ", DonViTinh=" +
      DonViTinh +
      '\'' +
      ", DonGiaBan=" +
      DonGiaBan +
      ", SoLuongTon=" +
      SoLuongTon +
      ", Active=" +
      Active +
      "}"
    );
  }
}
