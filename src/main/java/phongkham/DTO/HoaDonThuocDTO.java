package phongkham.DTO;

import java.time.LocalDateTime;

public class HoaDonThuocDTO {

  private String maHoaDon;
  private String maDonThuoc;
  private LocalDateTime ngayLap;
  private double tongTien;
  private String ghiChu;
  private String trangThaiThanhToan;
  private LocalDateTime ngayThanhToan;
  private String trangThaiLayThuoc; // ĐANG CHỜ LẤY, ĐÃ HOÀN THÀNH
  private String tenBenhNhan;
  private String sdtBenhNhan;
  private boolean active;

  // Constructor
  public HoaDonThuocDTO() {
    this.ngayLap = LocalDateTime.now();
    this.trangThaiThanhToan = "Chưa thanh toán";
    this.trangThaiLayThuoc = "ĐANG CHỜ LẤY";
    this.active = true;
  }

  public HoaDonThuocDTO(
    String maHoaDon,
    String maDonThuoc,
    LocalDateTime ngayLap,
    double tongTien,
    String ghiChu,
    String trangThaiThanhToan,
    LocalDateTime ngayThanhToan,
    String trangThaiLayThuoc,
    String tenBenhNhan,
    String sdtBenhNhan,
    boolean active
  ) {
    this.maHoaDon = maHoaDon;
    this.maDonThuoc = maDonThuoc;
    this.ngayLap = ngayLap;
    this.tongTien = tongTien;
    this.ghiChu = ghiChu;
    this.trangThaiThanhToan = trangThaiThanhToan;
    this.ngayThanhToan = ngayThanhToan;
    this.trangThaiLayThuoc = trangThaiLayThuoc;
    this.tenBenhNhan = tenBenhNhan;
    this.sdtBenhNhan = sdtBenhNhan;
    this.active = active;
  }

  // Insert
  public HoaDonThuocDTO(
    String maDonThuoc,
    String tenBenhNhan,
    String sdtBenhNhan
  ) {
    this.maDonThuoc = maDonThuoc;
    this.tenBenhNhan = tenBenhNhan;
    this.sdtBenhNhan = sdtBenhNhan;
    this.ngayLap = LocalDateTime.now();
    this.tongTien = 0;
    this.trangThaiThanhToan = "Chưa thanh toán";
    this.trangThaiLayThuoc = "ĐANG CHỜ LẤY";
    this.active = true;
  }

  // Getters và Setters
  public String getMaHoaDon() {
    return maHoaDon;
  }

  public void setMaHoaDon(String maHoaDon) {
    this.maHoaDon = maHoaDon;
  }

  public String getMaDonThuoc() {
    return maDonThuoc;
  }

  public void setMaDonThuoc(String maDonThuoc) {
    this.maDonThuoc = maDonThuoc;
  }

  public LocalDateTime getNgayLap() {
    return ngayLap;
  }

  public void setNgayLap(LocalDateTime ngayLap) {
    this.ngayLap = ngayLap;
  }

  public double getTongTien() {
    return tongTien;
  }

  public void setTongTien(double tongTien) {
    this.tongTien = tongTien;
  }

  public String getGhiChu() {
    return ghiChu;
  }

  public void setGhiChu(String ghiChu) {
    this.ghiChu = ghiChu;
  }

  public String getTrangThaiThanhToan() {
    return trangThaiThanhToan;
  }

  public void setTrangThaiThanhToan(String trangThaiThanhToan) {
    this.trangThaiThanhToan = trangThaiThanhToan;
  }

  public LocalDateTime getNgayThanhToan() {
    return ngayThanhToan;
  }

  public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
    this.ngayThanhToan = ngayThanhToan;
  }

  public String getTenBenhNhan() {
    return tenBenhNhan;
  }

  public void setTenBenhNhan(String tenBenhNhan) {
    this.tenBenhNhan = tenBenhNhan;
  }

  public String getSdtBenhNhan() {
    return sdtBenhNhan;
  }

  public void setSdtBenhNhan(String sdtBenhNhan) {
    this.sdtBenhNhan = sdtBenhNhan;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getTrangThaiLayThuoc() {
    return trangThaiLayThuoc;
  }

  public void setTrangThaiLayThuoc(String trangThaiLayThuoc) {
    this.trangThaiLayThuoc = trangThaiLayThuoc;
  }

  @Override
  public String toString() {
    return (
      "HoaDonThuocDTO{" +
      "maHoaDon=" +
      maHoaDon +
      ", maDonThuoc=" +
      maDonThuoc +
      ", ngayLap=" +
      ngayLap +
      ", tongTien=" +
      tongTien +
      ", tenBenhNhan='" +
      tenBenhNhan +
      '\'' +
      ", trangThaiThanhToan='" +
      trangThaiThanhToan +
      '\'' +
      ", active=" +
      active +
      '}'
    );
  }
}
