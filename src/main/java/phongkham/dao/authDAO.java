package phongkham.DAO;

import phongkham.DTO.UserDTO;

public interface authDAO {
  public UserDTO dangNhap(String user, String pass);

  public boolean dangKy(UserDTO tk);

  public boolean tonTaiTenDangNhap(String user);
}
