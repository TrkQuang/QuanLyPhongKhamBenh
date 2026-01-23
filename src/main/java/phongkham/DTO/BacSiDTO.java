package phongkham.DTO;

public class BacSiDTO {
    private String MaBacSi;
    private String HoTen;
    private String ChuyenKhoa;
    private String SoDienThoai;
    private String Email;
    private String MaKhoa;

    public BacSiDTO() {
        MaBacSi = "";
        HoTen = "";
        ChuyenKhoa = "";
        SoDienThoai = "";
        Email = "";
        MaKhoa = "";
    }

    public BacSiDTO(String MaBacSi, String HoTen, String ChuyenKhoa, String SoDienThoai, String Email, String MaKhoa) {
        this.MaBacSi = MaBacSi;
        this.HoTen = HoTen;
        this.ChuyenKhoa = ChuyenKhoa;
        this.SoDienThoai = SoDienThoai;
        this.Email = Email;
        this.MaKhoa = MaKhoa;
    }

    public BacSiDTO(BacSiDTO bs) {
        this.MaBacSi = bs.MaBacSi;
        this.HoTen = bs.HoTen;
        this.ChuyenKhoa = bs.ChuyenKhoa;
        this.SoDienThoai = bs.SoDienThoai;
        this.Email = bs.Email;
        this.MaKhoa = bs.MaKhoa;
    }

    public String getMaBacSi() {
        return MaBacSi;
    }

    public String getHoTen() {
        return HoTen;
    }

    public String getChuyenKhoa() {
        return ChuyenKhoa;
    }

    public String getSoDienThoai() {
        return SoDienThoai;
    }

    public String getEmail() {
        return Email;
    }

    public String getMaKhoa() {
        return MaKhoa;
    }

    public void setMaBacSi(String MaBacSi) {
        this.MaBacSi = MaBacSi;
    }

    public void setHoTen(String HoTen) {
        this.HoTen = HoTen;
    }

    public void setChuyenKhoa(String ChuyenKhoa) {
        this.ChuyenKhoa = ChuyenKhoa;
    }

    public void setSoDienThoai(String SoDienThoai) {
        this.SoDienThoai = SoDienThoai;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setMaKhoa(String MaKhoa) {
        this.MaKhoa = MaKhoa;
    }

}
