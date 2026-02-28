package phongkham.BUS;

import java.util.ArrayList;

import phongkham.dao.PermissionsDAO;

public class PermissionBUS {
    private ArrayList<String> listPerm;
    private PermissionsDAO permissionDAO;

    public PermissionBUS() {
        listPerm = new ArrayList<>();
        permissionDAO = new PermissionsDAO();
    }
    //load quyền khi login vào
    public void loadPermission(String UserID) {
        listPerm = permissionDAO.getPermissionByUser(UserID);
    }
    //kiểm tra quyền
    public boolean hasPerm(String Permission) {
        for(String p : listPerm) {
            if(p.equals(Permission)) {
                return true;
            }
        }
        return false;
    }
    public ArrayList<String> getListPermission() {
        return listPerm;
    }
}
