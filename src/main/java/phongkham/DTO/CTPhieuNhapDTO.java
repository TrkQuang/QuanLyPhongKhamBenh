package phongkham.DTO;

public class CTPhieuNhapDTO {
    private String MaCTPhieuNhap;
    private String MaPhieuNhap;
    private String MaThuoc;
    private int SoLuong;
    private float DonGiaNhap;

    public CTPhieuNhapDTO(){
        this.MaCTPhieuNhap = "";
        this.MaPhieuNhap = "";
        this.MaThuoc = "";
        this.SoLuong = 0;
        this.DonGiaNhap = 0;
    }

    public CTPhieuNhapDTO(String MaCTPhieuNhap, String MaPhieuNhap,
                        String MaThuoc, int SoLuong, float DonGiaNhap){
        this.MaCTPhieuNhap = MaCTPhieuNhap;
        this.MaPhieuNhap = MaPhieuNhap;
        this.MaThuoc = MaThuoc;
        this.SoLuong = SoLuong;
        this.DonGiaNhap = DonGiaNhap;                
    }

    public String getMaCTPhieuNhap(){ return this.MaCTPhieuNhap;}
    public String getMaPhieuNhap(){ return this.MaPhieuNhap;}
    public String getMaThuoc(){ return this.MaThuoc;}
    public int getSoLuong(){ return this.SoLuong;}
    public float getDonGianNhap(){ return this.DonGiaNhap;}

    public void setMaCTPhieuNhap(String MaCTPhieuNhap){
        this.MaCTPhieuNhap = MaCTPhieuNhap;
    }
    
    public void SetMaPhieuNhap(String MaPhieuNhap){
        this.MaPhieuNhap = MaPhieuNhap;
    }

    public void setMaThuoc(String MaThuoc){
        this.MaThuoc = MaThuoc;
    }

    public void setSoLuong(int SoLuong){
        this.SoLuong = SoLuong;
    }

    public void setDonGiaNhap(float DonGiaNhap){
        this.DonGiaNhap = DonGiaNhap;
    }

}
