package phongkham.DTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CTPhieuNhapDTO {
    private String MaCTPN;
    private String MaPhieuNhap;
    private String MaThuoc;
    private int SoLuongNhap;
    private BigDecimal DonGiaNhap = BigDecimal.ZERO;
    private LocalDateTime HanSuDung;

    public CTPhieuNhapDTO(){
    }

    public CTPhieuNhapDTO(String MaCTPN, String MaPhieuNhap,
                        String MaThuoc, int SoLuongNhap, 
                        BigDecimal DonGiaNhap, LocalDateTime HanSuDung){
        this.MaCTPN = MaCTPN;
        this.MaPhieuNhap = MaPhieuNhap;
        this.MaThuoc = MaThuoc;
        this.SoLuongNhap = SoLuongNhap;
        this.DonGiaNhap = DonGiaNhap;  
        this.HanSuDung = HanSuDung;              
    }

    public String getMaCTPN(){ return this.MaCTPN;}
    public String getMaPhieuNhap(){ return this.MaPhieuNhap;}
    public String getMaThuoc(){ return this.MaThuoc;}
    public int getSoLuongNhap(){ return this.SoLuongNhap;}
    public BigDecimal getDonGiaNhap(){ return this.DonGiaNhap;}
    public LocalDateTime getHanSuDung(){ return this.HanSuDung;}

    public void setMaCTPN(String MaCTPN){
        this.MaCTPN = MaCTPN;
    }
    
    public void SetMaPhieuNhap(String MaPhieuNhap){
        this.MaPhieuNhap = MaPhieuNhap;
    }

    public void setMaThuoc(String MaThuoc){
        this.MaThuoc = MaThuoc;
    }

    public void setSoLuong(int SoLuongNhap){
        this.SoLuongNhap = SoLuongNhap;
    }

    public void setDonGiaNhap(BigDecimal DonGiaNhap){
        this.DonGiaNhap = DonGiaNhap;
    }
    
    public void setHanSuDung(LocalDateTime HanSuDung){
        this.HanSuDung = HanSuDung;
    }

}
