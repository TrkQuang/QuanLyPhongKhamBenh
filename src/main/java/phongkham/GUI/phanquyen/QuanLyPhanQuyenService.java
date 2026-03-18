package phongkham.gui.phanquyen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import phongkham.BUS.PermissionBUS;
import phongkham.BUS.RolePermissionsBUS;
import phongkham.BUS.RolesBUS;
import phongkham.BUS.UsersBUS;
import phongkham.BUS.UsersRolesBUS;
import phongkham.DTO.PermissionsDTO;
import phongkham.DTO.RolePermissionsDTO;
import phongkham.DTO.RolesDTO;
import phongkham.DTO.UsersDTO;
import phongkham.DTO.UsersRolesDTO;

public class QuanLyPhanQuyenService {

  private final RolesBUS rolesBUS;
  private final PermissionBUS permissionBUS;
  private final RolePermissionsBUS rolePermissionsBUS;
  private final UsersBUS usersBUS;
  private final UsersRolesBUS usersRolesBUS;

  public QuanLyPhanQuyenService() {
    this.rolesBUS = new RolesBUS();
    this.permissionBUS = new PermissionBUS();
    this.rolePermissionsBUS = new RolePermissionsBUS();
    this.usersBUS = new UsersBUS();
    this.usersRolesBUS = new UsersRolesBUS();
  }

  public ArrayList<RolesDTO> getAllRoles() {
    return rolesBUS.getAllRoles();
  }

  public RolesDTO getRoleById(String roleId) {
    return rolesBUS.getRoleById(roleId);
  }

  public boolean addRole(RolesDTO role) {
    return rolesBUS.addRole(role);
  }

  public boolean updateRole(RolesDTO role) {
    return rolesBUS.updateRole(role);
  }

  public boolean deleteRole(String roleId) {
    return rolesBUS.deleteRole(roleId);
  }

  public String generateNextRoleId() {
    return String.valueOf(getAllRoles().size() + 1);
  }

  public ArrayList<PermissionsDTO> getAllPermissions() {
    return permissionBUS.getAllPermissions();
  }

  public ArrayList<RolePermissionsDTO> getPermissionsByRole(String roleId) {
    return rolePermissionsBUS.getPermissionsByRole(roleId);
  }

  public Map<String, Boolean> getPermissionStateByRole(String roleId) {
    ArrayList<RolePermissionsDTO> rolePermissions = getPermissionsByRole(
      roleId
    );
    Map<String, Boolean> permissionMap = new HashMap<>();
    for (RolePermissionsDTO rp : rolePermissions) {
      permissionMap.put(String.valueOf(rp.getMaPermission()), rp.isActive());
    }
    return permissionMap;
  }

  public void savePermissionsForRole(
    String roleId,
    ArrayList<String> permissionIds
  ) throws Exception {
    rolePermissionsBUS.deleteAllPermissionsByRole(roleId);

    int maRole = Integer.parseInt(roleId);
    for (String permissionId : permissionIds) {
      int maPermission = Integer.parseInt(permissionId);
      RolePermissionsDTO rp = new RolePermissionsDTO(
        maRole,
        maPermission,
        "",
        true
      );
      rolePermissionsBUS.addRolePermission(rp);
    }
  }

  public ArrayList<UsersDTO> getAllUsers() {
    return usersBUS.getAllUsers();
  }

  public ArrayList<UsersRolesDTO> getRolesByUser(String userId) {
    return usersRolesBUS.getRolesByUser(userId);
  }

  public boolean hasUserRole(String userId, String roleId) {
    return usersRolesBUS.hasUserRole(userId, roleId);
  }

  public boolean addUserRole(String userId, String roleId) {
    return usersRolesBUS.addUserRole(new UsersRolesDTO(userId, roleId));
  }

  public boolean deleteUserRole(String userId, String roleId) {
    return usersRolesBUS.deleteUserRole(userId, roleId);
  }
}
