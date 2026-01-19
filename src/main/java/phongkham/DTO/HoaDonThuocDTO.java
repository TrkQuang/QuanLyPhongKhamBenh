package phongkham.DTO;

import java.time.LocalDateTime;

public class HoaDonThuocDTO {
    private int maHoaDon;
    private Integer maDonThuoc; 
    private LocalDateTime ngayLap;
    private double tongTien;
    private String ghiChu;
    private String trangThaiThanhToan;
    private LocalDateTime ngayThanhToan;
    private String tenBenhNhan;
    private String sdtBenhNhan;
    private boolean active;

    // Constructor 
    public HoaDonThuocDTO() {
        this.ngayLap = LocalDateTime.now();
        this.trangThaiThanhToan = "Chưa thanh toán";
        this.active = true;
    }

    public HoaDonThuocDTO(int maHoaDon, Integer maDonThuoc, LocalDateTime ngayLap, 
                          double tongTien, String ghiChu, String trangThaiThanhToan, 
                          LocalDateTime ngayThanhToan, String tenBenhNhan, String sdtBenhNhan, boolean active) {
        this.maHoaDon = maHoaDon;
        this.maDonThuoc = maDonThuoc;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.ghiChu = ghiChu;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.ngayThanhToan = ngayThanhToan;
        this.tenBenhNhan = tenBenhNhan;
        this.sdtBenhNhan = sdtBenhNhan;
        this.active = active;
    }

    // Insert
    public HoaDonThuocDTO(Integer maDonThuoc, String tenBenhNhan, String sdtBenhNhan) {
        this.maDonThuoc = maDonThuoc;
        this.tenBenhNhan = tenBenhNhan;
        this.sdtBenhNhan = sdtBenhNhan;
        this.ngayLap = LocalDateTime.now();
        this.tongTien = 0;
        this.trangThaiThanhToan = "Chưa thanh toán";
        this.active = true;
    }

    // Getters và Setters
    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public Integer getMaDonThuoc() {
        return maDonThuoc;
    }

    public void setMaDonThuoc(Integer maDonThuoc) {
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

    @Override
    public String toString() {
        return "HoaDonThuocDTO{" +
                "maHoaDon=" + maHoaDon +
                ", maDonThuoc=" + maDonThuoc +
                ", ngayLap=" + ngayLap +
                ", tongTien=" + tongTien +
                ", tenBenhNhan='" + tenBenhNhan + '\'' +
                ", trangThaiThanhToan='" + trangThaiThanhToan + '\'' +
                ", active=" + active +
                '}';
    }
}
