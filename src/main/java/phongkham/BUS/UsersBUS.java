package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DTO.BacSiDTO;
import phongkham.DTO.UsersDTO;
import phongkham.Utils.Session;
import phongkham.dao.UsersDAO;

public class UsersBUS {

  private UsersDAO userDAO = new UsersDAO();
  private static final int ROLE_BACSI = 2;
  private static final int ROLE_NHATHUOC = 3;

  //=========Login==========
  public UsersDTO login(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return null;
    }
    return userDAO.checkLogin(username, password);
  }

  //=======GET ALL=========
  public ArrayList<UsersDTO> getAllUsers() {
    return userDAO.getAll();
  }

  public ArrayList<UsersDTO> getUsersByRole(int roleId) {
    return userDAO.getByRole(roleId);
  }

  //===========Thêm USER==========
  public String insertUser(UsersDTO u) {
    if (u.getUsername().trim().isEmpty() || u.getPassword().trim().isEmpty()) {
      return "Tên TK và MK ko được trống!";
    }
    if (u.getUserID() == null || u.getUserID().trim().isEmpty()) {
      u.setUserID(userDAO.generateNextUserID());
    }
    if (userDAO.getUserByID(u.getUserID()) != null) {
      return "UserID đã tồn tại";
    }
    if (userDAO.existsUsername(u.getUsername().trim())) {
      return "Tên tài khoản đã tồn tại";
    }
    if (u.getEmail() != null && !u.getEmail().trim().isEmpty()) {
      if (userDAO.existsEmail(u.getEmail().trim())) {
        return "Email đã tồn tại";
      }
    }
    if (u.getRoleID() == null) {
      return "Vai trò không hợp lệ";
    }
    boolean result = userDAO.insertUser(u);
    if (result) return "Thêm thành công";
    else return "Thêm thất bại";
  }

  //=========CHỈNH SỬA USER===========
  public String updateUser(UsersDTO u) {
    if (u.getUsername().trim().isEmpty()) {
      return "Tên tài khoản ko được trống";
    }
    boolean result = userDAO.updateUser(u);
    if (result) return "Thêm thành công";
    else return "Thất bại!";
  }

  //==========XÓA USER==========
  public String deleteUser(int uID) {
    boolean rs = userDAO.deleteUser(uID);
    if (rs) return "Xóa thành công";
    else return "Xóa thất bại";
  }

  public String deleteUser(String userID) {
    if (userID == null || userID.trim().isEmpty()) {
      return "UserID không được để trống";
    }
    boolean rs = userDAO.disableUser(userID.trim());
    if (rs) return "Khóa tài khoản thành công";
    return "Khóa tài khoản thất bại";
  }

  /**
   * Xóa tài khoản theo nghiệp vụ:
   * - Nếu chưa phát sinh dữ liệu liên quan: xóa cứng.
   * - Nếu đã phát sinh dữ liệu liên quan: chuyển trạng thái xóa ẩn vĩnh viễn.
   */
  public String xoaTaiKhoanTheoNghiepVu(String userID) {
    if (userID == null || userID.trim().isEmpty()) {
      return "UserID không được để trống";
    }
    String normalizedUserId = userID.trim();

    String currentUser = Session.getCurrentUserID();
    if (
      currentUser != null &&
      !currentUser.trim().isEmpty() &&
      currentUser.trim().equalsIgnoreCase(normalizedUserId)
    ) {
      return "Không thể tự xóa chính tài khoản đang đăng nhập";
    }

    UsersDTO user = userDAO.getUserByID(normalizedUserId);
    if (user == null) {
      return "Tài khoản không tồn tại hoặc đã bị ẩn vĩnh viễn";
    }

    LienQuanTaiKhoan lienQuan = danhGiaLienQuanDuLieu(user);
    if (lienQuan.coLienQuan) {
      if (!userDAO.supportsArchiveDeletion()) {
        return (
          "Tài khoản có dữ liệu liên quan (" +
          lienQuan.moTa +
          ") nên cần xóa ẩn vĩnh viễn. " +
          "Vui lòng chạy migration database: migrate_users_archive_delete_2026-03-24.sql"
        );
      }

      boolean archived = userDAO.archiveUserPermanently(
        normalizedUserId,
        "AN_VINH_VIEN_DO_LIEN_QUAN_DU_LIEU"
      );
      if (!archived) {
        return "Không thể chuyển tài khoản sang trạng thái xóa ẩn";
      }
      return (
        "Tài khoản đã được chuyển sang xóa ẩn vĩnh viễn do có dữ liệu liên quan: " +
        lienQuan.moTa
      );
    }

    boolean deleted = userDAO.hardDeleteUser(normalizedUserId);
    if (!deleted) {
      return "Xóa tài khoản thất bại";
    }
    return "Xóa tài khoản thành công";
  }

  public String enableUser(String userID) {
    if (userID == null || userID.trim().isEmpty()) {
      return "UserID không được để trống";
    }
    boolean rs = userDAO.enableUser(userID.trim());
    if (rs) return "Mở khóa tài khoản thành công";
    return "Mở khóa tài khoản thất bại";
  }

  //=========LẤY THEO ID===========
  public UsersDTO getUserByID(String UID) {
    return userDAO.getUserByID(UID);
  }

  public String resetPassword(String userID, String newPassword) {
    if (userID == null || userID.trim().isEmpty()) {
      return "UserID không được để trống";
    }
    if (newPassword == null || newPassword.trim().length() < 6) {
      return "Mật khẩu mới phải có ít nhất 6 ký tự";
    }
    UsersDTO existing = userDAO.getUserByID(userID.trim());
    if (existing == null) {
      return "Tài khoản không tồn tại";
    }
    boolean ok = userDAO.resetPassword(userID.trim(), newPassword.trim());
    return ok ? "Đặt lại mật khẩu thành công" : "Đặt lại mật khẩu thất bại";
  }

  public String createDoctorAccountWithProfile(
    String maBacSi,
    String username,
    String password,
    String email,
    String hoTen,
    String soDienThoai,
    String chuyenKhoa,
    String maKhoa
  ) {
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      return "Mã bác sĩ không được để trống";
    }
    if (username == null || username.trim().isEmpty()) {
      return "Tên tài khoản không được để trống";
    }
    if (password == null || password.trim().length() < 6) {
      return "Mật khẩu phải có ít nhất 6 ký tự";
    }
    if (email == null || email.trim().isEmpty()) {
      return "Email không được để trống";
    }
    if (hoTen == null || hoTen.trim().isEmpty()) {
      return "Họ tên bác sĩ không được để trống";
    }
    if (maKhoa == null || maKhoa.trim().isEmpty()) {
      return "Mã khoa không được để trống";
    }
    if (userDAO.existsUsername(username.trim())) {
      return "Tên tài khoản đã tồn tại";
    }
    if (
      userDAO.existsEmail(email.trim()) ||
      userDAO.existsBacSiEmail(email.trim())
    ) {
      return "Email đã tồn tại";
    }
    if (userDAO.existsBacSiId(maBacSi.trim())) {
      return "Mã bác sĩ đã tồn tại";
    }

    UsersDTO user = new UsersDTO();
    user.setUserID(userDAO.generateNextUserID());
    user.setUsername(username.trim());
    user.setPassword(password.trim());
    user.setEmail(email.trim());
    user.setRoleID(ROLE_BACSI);
    user.setActive(true);

    BacSiDTO bacSi = new BacSiDTO();
    bacSi.setMaBacSi(maBacSi.trim());
    bacSi.setHoTen(hoTen.trim());
    bacSi.setSoDienThoai(soDienThoai == null ? "" : soDienThoai.trim());
    bacSi.setEmail(email.trim());
    bacSi.setMaKhoa(maKhoa.trim());
    bacSi.setChuyenKhoa(chuyenKhoa == null ? "" : chuyenKhoa.trim());

    boolean ok = userDAO.createDoctorAccountWithProfile(user, bacSi);
    return ok
      ? "Tạo tài khoản bác sĩ thành công (" +
        user.getUserID() +
        " / " +
        bacSi.getMaBacSi() +
        ")"
      : "Tạo tài khoản bác sĩ thất bại";
  }

  public String createDoctorAccountWithProfile(
    String username,
    String password,
    String email,
    String hoTen,
    String soDienThoai,
    String chuyenKhoa,
    String maKhoa
  ) {
    return createDoctorAccountWithProfile(
      userDAO.generateNextBacSiID(),
      username,
      password,
      email,
      hoTen,
      soDienThoai,
      chuyenKhoa,
      maKhoa
    );
  }

  public String createPharmacyAccount(
    String username,
    String password,
    String email
  ) {
    if (username == null || username.trim().isEmpty()) {
      return "Tên tài khoản không được để trống";
    }
    if (password == null || password.trim().length() < 6) {
      return "Mật khẩu phải có ít nhất 6 ký tự";
    }
    if (email == null || email.trim().isEmpty()) {
      return "Email không được để trống";
    }
    if (userDAO.existsUsername(username.trim())) {
      return "Tên tài khoản đã tồn tại";
    }
    if (userDAO.existsEmail(email.trim())) {
      return "Email đã tồn tại";
    }

    UsersDTO user = new UsersDTO();
    user.setUserID(userDAO.generateNextUserID());
    user.setUsername(username.trim());
    user.setPassword(password.trim());
    user.setEmail(email.trim());
    user.setRoleID(ROLE_NHATHUOC);
    user.setActive(true);

    boolean ok = userDAO.createPharmacyAccount(user);
    return ok
      ? "Tạo tài khoản nhà thuốc thành công (" + user.getUserID() + ")"
      : "Tạo tài khoản nhà thuốc thất bại";
  }

  private LienQuanTaiKhoan danhGiaLienQuanDuLieu(UsersDTO user) {
    LienQuanTaiKhoan ketQua = new LienQuanTaiKhoan();
    StringBuilder moTa = new StringBuilder();

    String userEmail = user.getEmail() == null ? "" : user.getEmail().trim();
    String username =
      user.getUsername() == null ? "" : user.getUsername().trim();

    int soBacSiTheoEmail = userDAO.countBacSiByEmail(userEmail);
    if (soBacSiTheoEmail > 0) {
      ketQua.coLienQuan = true;
      appendReason(moTa, "hồ sơ bác sĩ");
    }

    String maBacSi = userDAO.findDoctorIdByEmail(userEmail);
    if (maBacSi == null || maBacSi.trim().isEmpty()) {
      maBacSi = deriveDoctorIdFromUsername(username);
    }

    int soLichKham = userDAO.countLichKhamByBacSi(maBacSi);
    if (soLichKham > 0) {
      ketQua.coLienQuan = true;
      appendReason(moTa, "lịch khám");
    }

    int soLichLam = userDAO.countLichLamViecByBacSi(maBacSi);
    if (soLichLam > 0) {
      ketQua.coLienQuan = true;
      appendReason(moTa, "lịch làm việc");
    }

    int soHoSo = userDAO.countHoSoByBacSi(maBacSi);
    if (soHoSo > 0) {
      ketQua.coLienQuan = true;
      appendReason(moTa, "hồ sơ bệnh án");
    }

    int soBienDongLo = userDAO.countLoaiBienDongByNguoiThucHien(username);
    if (soBienDongLo > 0) {
      ketQua.coLienQuan = true;
      appendReason(moTa, "biến động lô thuốc");
    }

    int soTieuHuyLo = userDAO.countTieuHuyByNguoiThucHien(username);
    if (soTieuHuyLo > 0) {
      ketQua.coLienQuan = true;
      appendReason(moTa, "lịch sử tiêu hủy lô");
    }

    ketQua.moTa = moTa.toString();
    return ketQua;
  }

  private void appendReason(StringBuilder sb, String reason) {
    if (reason == null || reason.trim().isEmpty()) {
      return;
    }
    if (sb.length() > 0) {
      sb.append(", ");
    }
    sb.append(reason.trim());
  }

  private String deriveDoctorIdFromUsername(String username) {
    if (username == null) {
      return null;
    }
    String digits = username.replaceAll("\\D", "");
    if (digits.isEmpty()) {
      return null;
    }
    return "BS" + String.format("%03d", Integer.parseInt(digits));
  }

  private static class LienQuanTaiKhoan {

    private boolean coLienQuan;
    private String moTa;
  }
}
