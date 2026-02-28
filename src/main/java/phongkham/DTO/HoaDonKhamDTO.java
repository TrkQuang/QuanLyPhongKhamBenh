package phongkham.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDonKhamDTO {

  private String MaHDKham;
  private String MaHoSo; // Đổi: MaPhieuKham → MaHoSo
  private String MaGoi;
  private LocalDateTime NgayThanhToan;
  private BigDecimal TongTien = BigDecimal.ZERO;
  private String HinhThucThanhToan;
  private String TrangThai; // CHO_THANH_TOAN, DA_THANH_TOAN

  public HoaDonKhamDTO() {
    this.TrangThai = "CHO_THANH_TOAN"; // Default
  }

  public HoaDonKhamDTO(
    String MaHDKham,
    String MaHoSo,
    String MaGoi,
    LocalDateTime NgayThanhToan,
    BigDecimal TongTien,
    String HinhThucThanhToan,
    String TrangThai
  ) {
    this.MaHDKham = MaHDKham;
    this.MaHoSo = MaHoSo;
    this.MaGoi = MaGoi;
    this.NgayThanhToan = NgayThanhToan;
    this.TongTien = TongTien;
    this.HinhThucThanhToan = HinhThucThanhToan;
    this.TrangThai = TrangThai;
  }

  public String getMaHDKham() {
    return this.MaHDKham;
  }

  public String getMaHoSo() {
    return this.MaHoSo;
  }

  public String getMaGoi() {
    return this.MaGoi;
  }

  public LocalDateTime getNgayThanhToan() {
    return this.NgayThanhToan;
  }

  public BigDecimal getTongTien() {
    return this.TongTien;
  }

  public String getHinhThucThanhToan() {
    return this.HinhThucThanhToan;
  }

  public String getTrangThai() {
    return this.TrangThai;
  }

  public void setMaHoaDonKham(String MaHDKham) {
    this.MaHDKham = MaHDKham;
  }

  public void setMaHoSo(String MaHoSo) {
    this.MaHoSo = MaHoSo;
  }

  public void setMaGoi(String MaGoi) {
    this.MaGoi = MaGoi;
  }

  public void setNgayThanhToan(LocalDateTime NgayThanhToan) {
    this.NgayThanhToan = NgayThanhToan;
  }

  public void setTongTien(BigDecimal TongTien) {
    this.TongTien = TongTien;
  }

  public void setHinhThucThanhToan(String HinhThucThanhToan) {
    this.HinhThucThanhToan = HinhThucThanhToan;
  }

  public void setTrangThai(String TrangThai) {
    this.TrangThai = TrangThai;
  }
}
