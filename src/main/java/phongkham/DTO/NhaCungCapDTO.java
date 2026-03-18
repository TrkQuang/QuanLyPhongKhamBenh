package phongkham.DTO;

public class NhaCungCapDTO {

  private String MaNhaCungCap;
  private String TenNhaCungCap;
  private String DiaChi;
  private String SDT;
  private boolean Active;

  public NhaCungCapDTO() {
    MaNhaCungCap = "";
    TenNhaCungCap = "";
    DiaChi = "";
    SDT = "";
    Active = true;
  }

  public NhaCungCapDTO(
    String MaNhaCungCap,
    String TenNhaCungCap,
    String DiaChi,
    String SDT
  ) {
    this.MaNhaCungCap = MaNhaCungCap;
    this.TenNhaCungCap = TenNhaCungCap;
    this.DiaChi = DiaChi;
    this.SDT = SDT;
    this.Active = true;
  }

  public NhaCungCapDTO(
    String MaNhaCungCap,
    String TenNhaCungCap,
    String DiaChi,
    String SDT,
    boolean Active
  ) {
    this.MaNhaCungCap = MaNhaCungCap;
    this.TenNhaCungCap = TenNhaCungCap;
    this.DiaChi = DiaChi;
    this.SDT = SDT;
    this.Active = Active;
  }

  public NhaCungCapDTO(NhaCungCapDTO ncc) {
    this.MaNhaCungCap = ncc.MaNhaCungCap;
    this.TenNhaCungCap = ncc.TenNhaCungCap;
    this.DiaChi = ncc.DiaChi;
    this.SDT = ncc.SDT;
    this.Active = ncc.Active;
  }

  public String getMaNhaCungCap() {
    return MaNhaCungCap;
  }

  public String getTenNhaCungCap() {
    return TenNhaCungCap;
  }

  public String getDiaChi() {
    return DiaChi;
  }

  public String getSDT() {
    return SDT;
  }

  public boolean isActive() {
    return Active;
  }

  public void setMaNhaCungCap(String MaNhaCungCap) {
    this.MaNhaCungCap = MaNhaCungCap;
  }

  public void setTenNhaCungCap(String TenNhaCungCap) {
    this.TenNhaCungCap = TenNhaCungCap;
  }

  public void setDiaChi(String DiaChi) {
    this.DiaChi = DiaChi;
  }

  public void setSDT(String SDT) {
    this.SDT = SDT;
  }

  public void setActive(boolean Active) {
    this.Active = Active;
  }

  @Override
  public String toString() {
    return (
      "NhaCungCap{" +
      "MaNhaCungCap=" +
      MaNhaCungCap +
      ", TenNhaCungCap='" +
      TenNhaCungCap +
      '\'' +
      ", DiaChi='" +
      DiaChi +
      '\'' +
      ", SDT='" +
      SDT +
      '\'' +
      ", Active=" +
      Active +
      "}"
    );
  }
}
