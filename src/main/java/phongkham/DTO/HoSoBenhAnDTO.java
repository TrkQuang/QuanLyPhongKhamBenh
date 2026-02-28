package phongkham.DTO;

import java.sql.Date;

public class HoSoBenhAnDTO {

  private String MaHoSo;
  private String MaLichKham; // Liên kết với lịch khám
  private String HoTen; // Thêm: Họ tên bệnh nhân
  private String SoDienThoai; // Thêm: SĐT để tra cứu
  private String CCCD; // Thêm: CCCD để tra cứu
  private Date NgaySinh; // Thêm: Ngày sinh
  private String GioiTinh; // Thêm: Giới tính
  private String DiaChi; // Thêm: Địa chỉ (optional)
  private Date NgayKham;
  private String TrieuChung; // Thêm: Triệu chứng khi đến khám
  private String ChanDoan;
  private String KetLuan;
  private String LoiDan;
  private String MaBacSi; // Thêm: Bác sĩ khám
  private String TrangThai; // CHO_KHAM, DA_KHAM, HUY

  public HoSoBenhAnDTO() {
    MaHoSo = "";
    MaLichKham = "";
    HoTen = "";
    SoDienThoai = "";
    CCCD = "";
    NgaySinh = null;
    GioiTinh = "";
    DiaChi = "";
    NgayKham = null;
    TrieuChung = "";
    ChanDoan = "";
    KetLuan = "";
    LoiDan = "";
    MaBacSi = "";
    TrangThai = "CHO_KHAM"; // Default
  }

  public HoSoBenhAnDTO(
    String MaHoSo,
    String MaLichKham,
    String HoTen,
    String SoDienThoai,
    String CCCD,
    Date NgaySinh,
    String GioiTinh,
    String DiaChi,
    Date NgayKham,
    String TrieuChung,
    String ChanDoan,
    String KetLuan,
    String LoiDan,
    String MaBacSi,
    String TrangThai
  ) {
    this.MaHoSo = MaHoSo;
    this.MaLichKham = MaLichKham;
    this.HoTen = HoTen;
    this.SoDienThoai = SoDienThoai;
    this.CCCD = CCCD;
    this.NgaySinh = NgaySinh;
    this.GioiTinh = GioiTinh;
    this.DiaChi = DiaChi;
    this.NgayKham = NgayKham;
    this.TrieuChung = TrieuChung;
    this.ChanDoan = ChanDoan;
    this.KetLuan = KetLuan;
    this.LoiDan = LoiDan;
    this.MaBacSi = MaBacSi;
    this.TrangThai = TrangThai;
  }

  // Getters
  public String getMaHoSo() {
    return MaHoSo;
  }

  public String getMaLichKham() {
    return MaLichKham;
  }

  public String getHoTen() {
    return HoTen;
  }

  public String getSoDienThoai() {
    return SoDienThoai;
  }

  public String getCCCD() {
    return CCCD;
  }

  public Date getNgaySinh() {
    return NgaySinh;
  }

  public String getGioiTinh() {
    return GioiTinh;
  }

  public String getDiaChi() {
    return DiaChi;
  }

  public Date getNgayKham() {
    return NgayKham;
  }

  public String getTrieuChung() {
    return TrieuChung;
  }

  public String getChanDoan() {
    return ChanDoan;
  }

  public String getKetLuan() {
    return KetLuan;
  }

  public String getLoiDan() {
    return LoiDan;
  }

  public String getMaBacSi() {
    return MaBacSi;
  }

  public String getTrangThai() {
    return TrangThai;
  }

  // Setters
  public void setMaHoSo(String MaHoSo) {
    this.MaHoSo = MaHoSo;
  }

  public void setMaLichKham(String MaLichKham) {
    this.MaLichKham = MaLichKham;
  }

  public void setHoTen(String HoTen) {
    this.HoTen = HoTen;
  }

  public void setSoDienThoai(String SoDienThoai) {
    this.SoDienThoai = SoDienThoai;
  }

  public void setCCCD(String CCCD) {
    this.CCCD = CCCD;
  }

  public void setNgaySinh(Date NgaySinh) {
    this.NgaySinh = NgaySinh;
  }

  public void setGioiTinh(String GioiTinh) {
    this.GioiTinh = GioiTinh;
  }

  public void setDiaChi(String DiaChi) {
    this.DiaChi = DiaChi;
  }

  public void setNgayKham(Date NgayKham) {
    this.NgayKham = NgayKham;
  }

  public void setTrieuChung(String TrieuChung) {
    this.TrieuChung = TrieuChung;
  }

  public void setChanDoan(String ChanDoan) {
    this.ChanDoan = ChanDoan;
  }

  public void setKetLuan(String KetLuan) {
    this.KetLuan = KetLuan;
  }

  public void setLoiDan(String LoiDan) {
    this.LoiDan = LoiDan;
  }

  public void setMaBacSi(String MaBacSi) {
    this.MaBacSi = MaBacSi;
  }

  public void setTrangThai(String TrangThai) {
    this.TrangThai = TrangThai;
  }
}
