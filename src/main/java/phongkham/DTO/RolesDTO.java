package phongkham.DTO;

public class RolesDTO{
    private int STT;
    private String TenVaiTro;
    private String MoTa;

    public RolesDTO(){
        STT = 0;
        TenVaiTro = "";
        MoTa = "";
    }

    public RolesDTO(int STT, String TenVaiTro, String MoTa){
        this.STT = STT;
        this.TenVaiTro = TenVaiTro;
        this.MoTa = MoTa;
    }

    public RolesDTO(RolesDTO rl){
        this.STT = rl.STT;
        this.TenVaiTro = rl.TenVaiTro;
        this.MoTa = rl.MoTa;
    }

    public int getSTT() {return STT;}
    public String getTenVaiTro() {return TenVaiTro;}
    public String getMoTa() {return MoTa;}

    public void setSTT(int STT) {this.STT = STT;}
    public void setTenVaiTro(String TenVaiTro) {this.TenVaiTro = TenVaiTro;}
    public void setMoTa(String MoTa) {this.MoTa = MoTa;}
}