package phongkham.gui.taikhoan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import phongkham.BUS.KhoaBUS;
import phongkham.BUS.RolesBUS;
import phongkham.BUS.UsersBUS;
import phongkham.DTO.KhoaDTO;
import phongkham.DTO.RolesDTO;
import phongkham.DTO.UsersDTO;

public class QuanLyTaiKhoanService {

  public static final int ROLE_NHATHUOC = 3;

  private final UsersBUS usersBUS;
  private final RolesBUS rolesBUS;
  private final KhoaBUS khoaBUS;

  public QuanLyTaiKhoanService() {
    this.usersBUS = new UsersBUS();
    this.rolesBUS = new RolesBUS();
    this.khoaBUS = new KhoaBUS();
  }

  public Map<Integer, String> loadRoleMap() {
    Map<Integer, String> roleMap = new HashMap<>();
    ArrayList<RolesDTO> roles = rolesBUS.getAllRoles();
    for (RolesDTO role : roles) {
      roleMap.put(Integer.parseInt(role.getSTT()), role.getTenVaiTro());
    }
    return roleMap;
  }

  public ArrayList<KhoaDTO> getAllKhoa() {
    return khoaBUS.getAll();
  }

  public ArrayList<UsersDTO> getAllUsers() {
    return usersBUS.getAllUsers();
  }

  public ArrayList<UsersDTO> getUsersByRole(int roleId) {
    return usersBUS.getUsersByRole(roleId);
  }

  public String enableUser(String userId) {
    return usersBUS.enableUser(userId);
  }

  public String disableUser(String userId) {
    return usersBUS.deleteUser(userId);
  }

  public String resetPassword(String userId, String newPassword) {
    return usersBUS.resetPassword(userId, newPassword);
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
    return usersBUS.createDoctorAccountWithProfile(
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
    return usersBUS.createPharmacyAccount(username, password, email);
  }
}
