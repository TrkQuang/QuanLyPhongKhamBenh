package phongkham.BUS;

import phongkham.DTO.UsersRolesDTO;
import phongkham.dao.UsersRolesDAO;

import java.util.ArrayList;

public class UsersRolesBUS {

    private UsersRolesDAO dao;
    private ArrayList<UsersRolesDTO> list;

    public UsersRolesBUS() {
        dao = new UsersRolesDAO();
    }

    // ===== LẤY DANH SÁCH ROLE THEO USER =====
    public ArrayList<UsersRolesDTO> getAll(String user_id) {
        list = dao.getALL(user_id);
        return list;
    }

    // ===== THÊM ROLE CHO USER =====
    public boolean insert(UsersRolesDTO ur) {
        if (ur == null) return false;
        if (ur.getUser_ID() == null || ur.getRole_ID() == null) return false;

        return dao.Insert(ur);
    }

    // ===== XÓA ROLE CỦA USER =====
    public boolean delete(String user_id, String role_id) {
        if (user_id == null || role_id == null) return false;

        return dao.Delete(user_id, role_id);
    }

    // ===== KIỂM TRA USER CÓ ROLE KHÔNG =====
    public boolean hasRole(String user_id, String role_id) {
        for (UsersRolesDTO ur : getAll(user_id)) {
            if (ur.getRole_ID().equals(role_id)) {
                return true;
            }
        }
        return false;
    }

    // ===== KIỂM TRA USER CÓ PHẢI ADMIN KHÔNG =====
    public boolean isAdmin(String user_id) {
        return hasRole(user_id, "ADMIN");
    }
}
