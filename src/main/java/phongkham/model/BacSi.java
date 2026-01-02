package main.java.phongkham.model;

public class BacSi {
    private String maBS;
    private String hoTen;
    private String chuyenKhoa;
    private String sdt;
    private String chucVu;
    private String hocVi;
    private Khoa khoa;


    public BacSi(String maBS, String hoTen, String chuyenKhoa,
                 String sdt, String chucVu, String hocVi, Khoa khoa) {
        this.maBS = maBS;
        this.hoTen = hoTen;
        this.chuyenKhoa = chuyenKhoa;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.hocVi = hocVi;
        this.khoa = khoa;
    }
        public BacSi() {
        this.maBS = null;
        this.hoTen = null;
        this.chuyenKhoa = null;
        this.sdt = null;
        this.chucVu = null;
        this.hocVi = null;
        this.khoa = null;
    }

    public String getMaBS() {
        return maBS;
    }
    public void setMaBS(String maBS) {
        this.maBS = maBS;
    }
    
    @Override
    public String toString() {
        return this.maBS+" - "+this.hoTen+" - "+this.chuyenKhoa+" - "+this.sdt+" - "+
               this.chucVu+" - "+this.hocVi+" - "+(khoa!=null?khoa.getMaKhoa():"-");
    }

}
