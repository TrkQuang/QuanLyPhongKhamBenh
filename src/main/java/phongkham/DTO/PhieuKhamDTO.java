package phongkham.DTO;

public class PhieuKhamDTO {
    private String MaPhieuKham;
    private String MaLichKham;
    private String MaBacSi;
    private String ThoiGianTao;
    private String TrieuChungSoBo;

    public PhieuKhamDTO(){
        MaPhieuKham = "";
        MaLichKham = "";
        MaBacSi = "";
        ThoiGianTao = "";
        TrieuChungSoBo = "";
    }

    public PhieuKhamDTO(String MaPhieuKham, String MaLichKham, String MaBacSi, String ThoiGianTao, String TrieuChungSoBo){
        this.MaPhieuKham = MaPhieuKham;
        this.MaLichKham = MaLichKham;
        this.MaBacSi = MaBacSi; 
        this.ThoiGianTao = ThoiGianTao;
        this.TrieuChungSoBo = TrieuChungSoBo;
    }

    public PhieuKhamDTO(PhieuKhamDTO pk){
        this.MaPhieuKham = pk.MaPhieuKham;
        this.MaLichKham = pk.MaLichKham;
        this.MaBacSi = pk.MaBacSi; 
        this.ThoiGianTao = pk.ThoiGianTao;
        this.TrieuChungSoBo = pk.TrieuChungSoBo;
    }

    public String getMaPhieuKham() {return MaPhieuKham;}
    public String getMaLichKham() {return MaLichKham;}
    public String getMaBacSi() {return MaBacSi;}
    public String getThoiGianTao() {return ThoiGianTao;}
    public String getTrieuChungSoBo() {return TrieuChungSoBo;}

    public void setMaPhieuKham(String MaPhieuKham) {this.MaPhieuKham =  MaPhieuKham;}
    public void setMaLichKham(String MaLichKham) {this.MaLichKham = MaLichKham;}
    public void setMaBacSi(String MaBacSi) {this.MaBacSi = MaBacSi;}
    public void setThoiGianTao(String ThoiGianTao) {this.ThoiGianTao = ThoiGianTao;}
    public void setTrieuChungSoBo(String TrieuChungSoBo) {this.TrieuChungSoBo = TrieuChungSoBo;}
} 
