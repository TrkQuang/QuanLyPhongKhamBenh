package main.java.phongkham.dao;
import main.java.phongkham.model.User;

public interface DAO_auth {
    public User dangNhap(String user, String pass);
    public boolean dangKy(User tk);
    public boolean tonTaiTenDangNhap(String user);
}
