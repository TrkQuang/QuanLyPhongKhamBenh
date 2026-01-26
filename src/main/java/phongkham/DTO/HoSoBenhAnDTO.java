package phongkham.DTO;
public class HoSoBenhAnDTO {
    private String MaHoSo;
    private String MaPhieuKham;
    private String NgayKham;
    private String ChanDoan;
    private String KetLuan;
    private String LoiDan; 

    public HoSoBenhAnDTO(){
        MaHoSo = "";
        MaPhieuKham = "";
        NgayKham = "";
        ChanDoan = "";
        KetLuan = "";
        LoiDan = "";
    }

    public HoSoBenhAnDTO(String MaHoSo, String MaPhieuKham, String NgayKham, String ChanDoan, String KetLuan, String LoiDan){
        this.MaHoSo = MaHoSo;
        this.MaPhieuKham = MaPhieuKham;
        this.NgayKham = NgayKham;
        this.ChanDoan = ChanDoan;
        this.KetLuan = KetLuan;
        this.LoiDan = LoiDan;
    }

    public String getMaHoSo() {return MaHoSo;}
    public String getMaPhieuKham() {return MaPhieuKham;}
    public String getNgayKham() {return NgayKham;}
    public String getChanDoan() {return ChanDoan;}
    public String getKetLuan() {return KetLuan;}
    public String getLoiDan() {return LoiDan;}

    public void setMaHoSo(String MaHoSo) {this.MaHoSo = MaHoSo;}
    public void setMaPhieuKham(String MaPhieuKham) {this.MaPhieuKham = MaPhieuKham;}
    public void setNgayKham(String NgayKham) {this.NgayKham = NgayKham;}
    public void setChanDoan(String ChanDoan) {this.ChanDoan = ChanDoan;}
    public void setKetLuan(String KetLuan) {this.KetLuan = KetLuan;}
    public void setLoiDan(String LoiDan) {this.LoiDan = LoiDan;}
} 
