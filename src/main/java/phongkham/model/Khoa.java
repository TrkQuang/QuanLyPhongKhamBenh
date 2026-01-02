package main.java.phongkham.model;

public class Khoa {
    private String maKhoa;
    private String tenKhoa;

    public Khoa(String maKhoa, String tenKhoa){
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public String getMaKhoa() {
        return maKhoa;
    }
    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }
    public String getTenKhoa() {
        return tenKhoa;
    }
    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }
}
