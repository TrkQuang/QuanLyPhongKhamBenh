package phongkham.DTO;

import java.math.BigDecimal;

public class GoiDichVuDTO {
    private String MaGoi;
    private String TenGoi;
    private BigDecimal GiaDichVu = BigDecimal.ZERO;
    private String ThoiGianKham;
    private String MoTa;

    public GoiDichVuDTO () {
        MaGoi =" ";
        TenGoi =" ";
        ThoiGianKham =" ";
        MoTa =" ";
    }

    public GoiDichVuDTO (String MaGoi, String TenGoi, BigDecimal GiaDichVu, String ThoiGianKham, String MoTa) {
        this.MaGoi=MaGoi;
        this.TenGoi=TenGoi;
        this.GiaDichVu=GiaDichVu;
        this.ThoiGianKham=ThoiGianKham;
        this.MoTa=MoTa;
    }

    public String getMaGoi() {return MaGoi;}
    public String getTenGoi() {return TenGoi;}
    public BigDecimal getGiaDichVu() {return GiaDichVu;}
    public String getThoiGianKham() {return ThoiGianKham;}
    public String getMoTa() {return MoTa;}

    public void setMaGoi(String MaGoi ) {this.MaGoi=MaGoi;}
    public void setTenGoi(String TenGoi ) {this.TenGoi=TenGoi;}
    public void setGiaDichVu(BigDecimal GiaDichVu ) {this.GiaDichVu=GiaDichVu;}
    public void setThoiGianKham(String ThoiGianKham ) {this.ThoiGianKham=ThoiGianKham;}
    public void setMoTa(String MoTa ) {this.MoTa=MoTa;}
}
