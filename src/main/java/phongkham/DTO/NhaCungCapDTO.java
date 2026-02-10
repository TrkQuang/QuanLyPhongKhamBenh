package phongkham.DTO;

public class NhaCungCapDTO {
    private String MaNhaCungCap;
    private String TenNhaCungCap;
    private String DiaChi;
    private String SDT; 

    public NhaCungCapDTO(){
        MaNhaCungCap = "";
        TenNhaCungCap = "";
        DiaChi = "";
        SDT = "";
    }

    public NhaCungCapDTO(String MaNhaCungCap, String TenNhaCungCap, String DiaChi, String SDT){
        this.MaNhaCungCap = MaNhaCungCap;
        this.TenNhaCungCap = TenNhaCungCap;
        this.DiaChi = DiaChi;
        this.SDT = SDT;
    }

    public NhaCungCapDTO(NhaCungCapDTO ncc){
        this.MaNhaCungCap = ncc.MaNhaCungCap;
        this.TenNhaCungCap = ncc.TenNhaCungCap;
        this.DiaChi = ncc.DiaChi;
        this.SDT = ncc.SDT;
    }

    public String getMaNhaCungCap() {return MaNhaCungCap;}
    public String getTenNhaCungCap() {return TenNhaCungCap;}
    public String getDiaChi() {return DiaChi;}
    public String getSDT() {return SDT;}

    public void setMaNhaCungCap(String MaNhaCungCap) {this.MaNhaCungCap = MaNhaCungCap;}
    public void setTenNhaCungCap(String TenNhaCungCap) {this.TenNhaCungCap = TenNhaCungCap;}
    public void setDiaChi(String DiaChi) {this.DiaChi = DiaChi;}
    public void setSDT(String SDT) {this.SDT = SDT;}

    @Override
    public String toString(){
        return "NhaCungCap{" + "MaNhaCungCap=" +MaNhaCungCap+ ", TenNhaCungCap='" +TenNhaCungCap+ '\''+ ", DiaChi='" +DiaChi+ '\''+ ", SDT='" +SDT+ '\''+ "}";
    }
}
