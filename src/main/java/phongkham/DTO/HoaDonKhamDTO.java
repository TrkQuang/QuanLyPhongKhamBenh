package phongkham.DTO;
import java.time.LocalDateTime;
import java.math.BigDecimal;
public class HoaDonKhamDTO {
    private String MaHDKham;
    private String MaPhieuKham;
    private String MaGoi;
    private LocalDateTime NgayThanhToan;
    private BigDecimal TongTien = BigDecimal.ZERO;
    private String HinhThucThanhToan;

    public HoaDonKhamDTO(){
    }

    public HoaDonKhamDTO(String MaHDKham, String MaPhieuKham, String MaGoi, LocalDateTime NgayThanhToan,
                        BigDecimal TongTien, String HinhThucThanhToan){
        this.MaHDKham = MaHDKham;
        this.MaPhieuKham = MaPhieuKham;
        this.MaGoi = MaGoi;
        this.NgayThanhToan = NgayThanhToan;
        this.TongTien = TongTien;
        this.HinhThucThanhToan = HinhThucThanhToan;
    }

    public String getMaHDKham(){ return this.MaHDKham;}
    public String getMaPhieuKham(){ return this.MaPhieuKham;}
    public String getMaGoi(){ return this.MaGoi;}
    public LocalDateTime getNgayThanhToan(){ return this.NgayThanhToan;}
    public BigDecimal getTongTien(){ return this.TongTien;}
    public String getHinhThucThanhToan(){ return this.HinhThucThanhToan;}

    public void setMaHoaDonKham(String MaHDKham){
        this.MaHDKham = MaHDKham;
    }

    public void setMaPhieuKham(String MaPhieuKham){
        this.MaPhieuKham = MaPhieuKham;
    }

    public void setMaGoi(String MaGoi){
        this.MaGoi = MaGoi;
    }

     public void setNgayThanhToan(LocalDateTime NgayThanhToan){
        this.NgayThanhToan = NgayThanhToan;
    }

    public void setTongTien(BigDecimal TongTien){
        this.TongTien = TongTien;
    }

   
    public void setHinhThucThanhToan(String HinhThucThanhToan){
        this.HinhThucThanhToan = HinhThucThanhToan;
    }

}
