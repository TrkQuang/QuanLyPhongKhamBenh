package phongkham.DTO;

public class CTHDThuocDTO {
    private int maCTHDThuoc;
    private int maHoaDon;
    private int maThuoc;
    private String tenThuoc;
    private String donVi;
    private int soLuong;
    private double donGia;
    private double thanhTien;
    private String ghiChu;
    private boolean active;

    // Constructor
    public CTHDThuocDTO() {
        this.active = true;
    }

    public CTHDThuocDTO(int maCTHDThuoc, int maHoaDon, int maThuoc, String tenThuoc, 
                        String donVi, int soLuong, double donGia, double thanhTien, String ghiChu, boolean active) {
        this.maCTHDThuoc = maCTHDThuoc;
        this.maHoaDon = maHoaDon;
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.donVi = donVi;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.ghiChu = ghiChu;
        this.active = active;
    }

    // Insert
    public CTHDThuocDTO(int maHoaDon, int maThuoc, int soLuong, double donGia) {
        this.maHoaDon = maHoaDon;
        this.maThuoc = maThuoc;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
        this.active = true;
    }

    // Getters và Setters
    public int getMaCTHDThuoc() {
        return maCTHDThuoc;
    }

    public void setMaCTHDThuoc(int maCTHDThuoc) {
        this.maCTHDThuoc = maCTHDThuoc;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public int getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(int maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        calculateThanhTien();
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
        calculateThanhTien();
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Tính thành tiền (logic đơn giản của entity)
    private void calculateThanhTien() {
        this.thanhTien = this.soLuong * this.donGia;
    }

    @Override
    public String toString() {
        return "CTHDThuocDTO{" +
                "maCTHDThuoc=" + maCTHDThuoc +
                ", maHoaDon=" + maHoaDon +
                ", tenThuoc='" + tenThuoc + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
