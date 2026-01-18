package phongkham.DTO;
import java.time.LocalDateTime;
public class HoaDonKhamDTO {
    private String MaHoaDonKham;
    private String MaPhieuKham;
    private float TongTien;
    private LocalDateTime NgayThanhToan;
    private String TrangThai;

    public HoaDonKhamDTO(){
        this.MaHoaDonKham = "";
        this.MaPhieuKham = "";
        this.TongTien = 0;
        this.NgayThanhToan = null;
        this.TrangThai = "";
    }

    public HoaDonKhamDTO(String MaHoaDonKham, String MaPhieuKham, 
                        float TongTien, LocalDateTime NgayThanhToan, String TrangThai){
        this.MaHoaDonKham = MaHoaDonKham;
        this.MaPhieuKham = MaPhieuKham;
        this.TongTien = TongTien;
        this.NgayThanhToan = NgayThanhToan;
        this.TrangThai = TrangThai;
    }

    public String getMaHoaDonKham(){ return MaHoaDonKham;}
    public String getMaPhieuKham(){ return MaPhieuKham;}
    public float getTongTien(){ return TongTien;}
    public LocalDateTime getNgayThanhToan(){ return NgayThanhToan;}
    public String getTrangThai(){ return TrangThai;}

    public void setMaHoaDonKham(String MaHoaDonKham){
        this.MaHoaDonKham = MaHoaDonKham;
    }

    public void setMaPhieuKham(String MaPhieuKham){
        this.MaPhieuKham = MaPhieuKham;
    }

    public void setTongTien(float TongTien){
        this.TongTien = TongTien;
    }

    public void setNgayThanhToan(LocalDateTime NgayThanhToan){
        this.NgayThanhToan = NgayThanhToan;
    }

    public void setTrangThai(String TrangThai){
        this.TrangThai = TrangThai;
    }

}
