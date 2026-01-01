package phongkham.dao;

import phongkham.model.User;

public interface DAO_auth {
  public User dangNhap(String user, String pass);

  public boolean dangKy(User tk);

  public boolean tonTaiTenDangNhap(String user);
}
