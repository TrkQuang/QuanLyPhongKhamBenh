package phongkham.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class XuatThuocTheoLoDTO {

  private long maXuatLo;
  private String maHoaDon;
  private String maCTHDThuoc;
  private String maCTPN;
  private String maThuoc;
  private String soLo;
  private LocalDate hanSuDung;
  private int soLuongXuat;
  private LocalDateTime ngayXuat;

  public long getMaXuatLo() {
    return maXuatLo;
  }

  public void setMaXuatLo(long maXuatLo) {
    this.maXuatLo = maXuatLo;
  }

  public String getMaHoaDon() {
    return maHoaDon;
  }

  public void setMaHoaDon(String maHoaDon) {
    this.maHoaDon = maHoaDon;
  }

  public String getMaCTHDThuoc() {
    return maCTHDThuoc;
  }

  public void setMaCTHDThuoc(String maCTHDThuoc) {
    this.maCTHDThuoc = maCTHDThuoc;
  }

  public String getMaCTPN() {
    return maCTPN;
  }

  public void setMaCTPN(String maCTPN) {
    this.maCTPN = maCTPN;
  }

  public String getMaThuoc() {
    return maThuoc;
  }

  public void setMaThuoc(String maThuoc) {
    this.maThuoc = maThuoc;
  }

  public String getSoLo() {
    return soLo;
  }

  public void setSoLo(String soLo) {
    this.soLo = soLo;
  }

  public LocalDate getHanSuDung() {
    return hanSuDung;
  }

  public void setHanSuDung(LocalDate hanSuDung) {
    this.hanSuDung = hanSuDung;
  }

  public int getSoLuongXuat() {
    return soLuongXuat;
  }

  public void setSoLuongXuat(int soLuongXuat) {
    this.soLuongXuat = soLuongXuat;
  }

  public LocalDateTime getNgayXuat() {
    return ngayXuat;
  }

  public void setNgayXuat(LocalDateTime ngayXuat) {
    this.ngayXuat = ngayXuat;
  }
}
