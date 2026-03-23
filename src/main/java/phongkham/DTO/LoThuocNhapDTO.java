package phongkham.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoThuocNhapDTO {

  private String maCTPN;
  private String maPhieuNhap;
  private String maNCC;
  private String tenNhaCungCap;
  private String maThuoc;
  private String tenThuoc;
  private String soLo;
  private LocalDate hanSuDung;
  private int soLuongNhap;
  private int soLuongConLai;
  private BigDecimal donGiaNhap = BigDecimal.ZERO;
  private LocalDateTime ngayNhap;
  private String trangThaiPhieuNhap;

  public String getMaCTPN() {
    return maCTPN;
  }

  public void setMaCTPN(String maCTPN) {
    this.maCTPN = maCTPN;
  }

  public String getMaPhieuNhap() {
    return maPhieuNhap;
  }

  public void setMaPhieuNhap(String maPhieuNhap) {
    this.maPhieuNhap = maPhieuNhap;
  }

  public String getMaNCC() {
    return maNCC;
  }

  public void setMaNCC(String maNCC) {
    this.maNCC = maNCC;
  }

  public String getTenNhaCungCap() {
    return tenNhaCungCap;
  }

  public void setTenNhaCungCap(String tenNhaCungCap) {
    this.tenNhaCungCap = tenNhaCungCap;
  }

  public String getMaThuoc() {
    return maThuoc;
  }

  public void setMaThuoc(String maThuoc) {
    this.maThuoc = maThuoc;
  }

  public String getTenThuoc() {
    return tenThuoc;
  }

  public void setTenThuoc(String tenThuoc) {
    this.tenThuoc = tenThuoc;
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

  public int getSoLuongNhap() {
    return soLuongNhap;
  }

  public void setSoLuongNhap(int soLuongNhap) {
    this.soLuongNhap = soLuongNhap;
  }

  public int getSoLuongConLai() {
    return soLuongConLai;
  }

  public void setSoLuongConLai(int soLuongConLai) {
    this.soLuongConLai = soLuongConLai;
  }

  public BigDecimal getDonGiaNhap() {
    return donGiaNhap;
  }

  public void setDonGiaNhap(BigDecimal donGiaNhap) {
    this.donGiaNhap = donGiaNhap;
  }

  public LocalDateTime getNgayNhap() {
    return ngayNhap;
  }

  public void setNgayNhap(LocalDateTime ngayNhap) {
    this.ngayNhap = ngayNhap;
  }

  public String getTrangThaiPhieuNhap() {
    return trangThaiPhieuNhap;
  }

  public void setTrangThaiPhieuNhap(String trangThaiPhieuNhap) {
    this.trangThaiPhieuNhap = trangThaiPhieuNhap;
  }

  public BigDecimal getThanhTienNhap() {
    if (donGiaNhap == null) {
      return BigDecimal.ZERO;
    }
    return donGiaNhap.multiply(BigDecimal.valueOf(soLuongNhap));
  }
}
