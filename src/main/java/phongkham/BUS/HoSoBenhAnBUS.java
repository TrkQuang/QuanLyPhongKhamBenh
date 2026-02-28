package phongkham.BUS;

import java.sql.Date;
import java.util.ArrayList;
import phongkham.DTO.HoSoBenhAnDTO;
import phongkham.dao.HoSoBenhAnDAO;

public class HoSoBenhAnBUS {

  private HoSoBenhAnDAO hsDAO = new HoSoBenhAnDAO();

  // ===== VALIDATION =====

  // Validate khi đăng ký (thông tin cơ bản)
  private String validateInsert(HoSoBenhAnDTO hs) {
    if (hs == null) return "Hồ sơ không được null";
    if (hs.getMaHoSo() == null || hs.getMaHoSo().trim().isEmpty()) {
      return "Mã hồ sơ không được để trống";
    }
    if (hs.getHoTen() == null || hs.getHoTen().trim().isEmpty()) {
      return "Họ tên không được để trống";
    }
    if (hs.getSoDienThoai() == null || hs.getSoDienThoai().trim().isEmpty()) {
      return "Số điện thoại không được để trống";
    }
    if (!isValidPhoneNumber(hs.getSoDienThoai())) {
      return "Số điện thoại không hợp lệ";
    }
    if (hs.getNgaySinh() != null) {
      if (hs.getNgaySinh().after(new Date(System.currentTimeMillis()))) {
        return "Ngày sinh không được lớn hơn ngày hiện tại";
      }
    }
    if (hs.getGioiTinh() != null && !hs.getGioiTinh().isEmpty()) {
      if (!hs.getGioiTinh().equals("Nam") && !hs.getGioiTinh().equals("Nữ")) {
        return "Giới tính chỉ được là 'Nam' hoặc 'Nữ'";
      }
    }
    return null; // Hợp lệ
  }

  // Kiểm tra trạng thái hợp lệ
  private boolean isValidTrangThai(String trangThai) {
    if (trangThai == null || trangThai.trim().isEmpty()) {
      return false;
    }
    return (
      trangThai.equals("CHO_KHAM") ||
      trangThai.equals("DA_KHAM") ||
      trangThai.equals("HUY")
    );
  }

  // Kiểm tra số điện thoại hợp lệ
  private boolean isValidPhoneNumber(String sdt) {
    if (sdt == null) return false;
    // Số điện thoại Việt Nam: 10 số, bắt đầu bằng 0
    return sdt.matches("^0\\d{9}$");
  }

  //Lấy tất cả hồ sơ
  public ArrayList<HoSoBenhAnDTO> getAll() {
    return hsDAO.getAll();
  }

  public HoSoBenhAnDTO getByMaHoSo(String MaHS) {
    if (MaHS == null || MaHS.trim().isEmpty()) {
      System.out.println("Không được rỗng dữ liệu");
      return null;
    }
    return hsDAO.getByMaHoSo(MaHS);
  }

  public boolean dangKyBenhNhan(HoSoBenhAnDTO hs) {
    String valid = validateInsert(hs);
    if (valid != null) return false;
    return hsDAO.insert(hs);
  }

  public boolean updateHoSo(HoSoBenhAnDTO hs) {
    String valid = validateInsert(hs);
    if (valid != null) return false;
    return true;
  }

  // Cập nhật kết quả khám (bác sĩ sử dụng)
  public boolean capNhatKetQuaKham(
    String maHoSo,
    String maBacSi,
    String trieuChung,
    String chanDoan,
    String ketLuan,
    String loiDan
  ) {
    // Validate
    if (maHoSo == null || maHoSo.trim().isEmpty()) {
      System.err.println("Mã hồ sơ không được để trống");
      return false;
    }
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      System.err.println("Mã bác sĩ không được để trống");
      return false;
    }
    if (chanDoan == null || chanDoan.trim().isEmpty()) {
      System.err.println("Chẩn đoán không được để trống");
      return false;
    }

    return hsDAO.updateKetQuaKham(
      maHoSo,
      trieuChung,
      chanDoan,
      ketLuan,
      loiDan,
      maBacSi
    );
  }
  
  public boolean updateTrangThai(String MaHS, String trangThai) {
    if(MaHS == null || MaHS.trim().isEmpty()) {
        System.out.println("Mã hồ sơ k đc trống");
        return false;
    }
    if(!isValidTrangThai(trangThai)) {
        System.out.println("Trạng thái không hợp lệ!");
        return false;
    }
    return hsDAO.updateTrangThai(MaHS, trangThai);
  }

  public boolean delete(String maHS) {
    if(maHS == null || maHS.trim().isEmpty()) {
        System.out.println("Mã hồ sơ không được rỗng");
        return false;
    }
    return hsDAO.delete(maHS);
  }

  //các hàm tìm kiếm, lọc
  public HoSoBenhAnDTO getByMaLichKham(String MaLichKham) {
    if(MaLichKham == null || MaLichKham.trim().isEmpty()) {
        return null;
    }
    return hsDAO.getByMaLichKham(MaLichKham);
  }

  public ArrayList<HoSoBenhAnDTO> getBySDT(String sdt) {
    if(sdt == null || sdt.trim().isEmpty()) {
      System.out.println("Số điện thoại không được rỗng");
      return null;
    }
    boolean valid = isValidPhoneNumber(sdt);
    if(valid) {
        ArrayList<HoSoBenhAnDTO> list = hsDAO.getBySoDienThoai(sdt);
        return list;
    }
    return null;
  }
}
