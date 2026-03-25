package phongkham.DTO;

public class KhungGioLamViecDTO {

  private int maKhungGio;
  private String khungGio;
  private String moTa;
  private int active;

  public KhungGioLamViecDTO() {
    this.maKhungGio = 0;
    this.khungGio = "";
    this.moTa = "";
    this.active = 1;
  }

  public int getMaKhungGio() {
    return maKhungGio;
  }

  public void setMaKhungGio(int maKhungGio) {
    this.maKhungGio = maKhungGio;
  }

  public String getKhungGio() {
    return khungGio;
  }

  public void setKhungGio(String khungGio) {
    this.khungGio = khungGio;
  }

  public String getMoTa() {
    return moTa;
  }

  public void setMoTa(String moTa) {
    this.moTa = moTa;
  }

  public int getActive() {
    return active;
  }

  public void setActive(int active) {
    this.active = active;
  }
}
