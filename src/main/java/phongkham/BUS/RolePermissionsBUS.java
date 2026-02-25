package phongkham.BUS;

import java.util.List;
import phongkham.DTO.RolePermissionsDTO;
import phongkham.dao.RolePermissionsDAO;

public class RolePermissionsBUS {

  private RolePermissionsDAO rolePermissionsDAO = new RolePermissionsDAO();

  // Thêm RolePermissions
  public boolean addRolePermission(RolePermissionsDTO rolePermissions) {
    if (rolePermissions == null) {
      System.err.println("RolePermissions không được null");
      return false;
    }
    if (
      rolePermissions.getMaRole() <= 0 || rolePermissions.getMaPermission() <= 0
    ) {
      System.err.println("Role ID và Permission ID phải > 0");
      return false;
    }

    // Kiểm tra trùng lặp
    if (
      isPermissionExists(
        rolePermissions.getMaRole(),
        rolePermissions.getMaPermission()
      )
    ) {
      System.err.println("Permission này đã gán cho role!");
      return false;
    }

    return rolePermissionsDAO.insert(rolePermissions);
  }

  // Cập nhật RolePermissions
  public boolean updateRolePermission(RolePermissionsDTO rolePermissions) {
    if (rolePermissions == null) {
      System.err.println("RolePermissions không được null");
      return false;
    }
    if (rolePermissions.getMaRolePermissions() <= 0) {
      System.err.println("Mã RolePermissions phải > 0");
      return false;
    }
    if (
      rolePermissions.getMaRole() <= 0 || rolePermissions.getMaPermission() <= 0
    ) {
      System.err.println("Role ID và Permission ID phải > 0");
      return false;
    }
    return rolePermissionsDAO.update(rolePermissions);
  }

  // Xóa RolePermissions
  public boolean deleteRolePermission(int maRolePermissions) {
    return rolePermissionsDAO.delete(maRolePermissions);
  }

  // Lấy chi tiết RolePermissions
  public RolePermissionsDTO getRolePermissionDetail(int maRolePermissions) {
    return rolePermissionsDAO.getById(maRolePermissions);
  }

  // Lấy tất cả RolePermissions
  public List<RolePermissionsDTO> getAllRolePermissions() {
    return rolePermissionsDAO.getAll();
  }

  // Lấy permissions theo role
  public List<RolePermissionsDTO> getPermissionsByRole(int maRole) {
    return rolePermissionsDAO.getByRole(maRole);
  }

  // Kiểm tra role có permission không
  public boolean checkPermission(int maRole, int maPermission) {
    return rolePermissionsDAO.hasPermission(maRole, maPermission);
  }

  // Kiểm tra permission đã tồn tại cho role chưa
  private boolean isPermissionExists(int maRole, int maPermission) {
    List<RolePermissionsDTO> permissions = rolePermissionsDAO.getByRole(maRole);
    for (RolePermissionsDTO rp : permissions) {
      if (rp.getMaPermission() == maPermission) {
        return true;
      }
    }
    return false;
  }

  // Gán tất cả permissions cho role
  public boolean assignAllPermissionsToRole(
    int maRole,
    List<Integer> permissionIds
  ) {
    // Validate
    if (maRole <= 0) {
      System.err.println("Role ID không hợp lệ");
      return false;
    }
    if (permissionIds == null || permissionIds.isEmpty()) {
      System.err.println("Danh sách quyền trống");
      return false;
    }

    // ✅ Gọi 1 hàm DAO duy nhất xử lý toàn bộ logic
    return rolePermissionsDAO.replacePermissionsForRole(maRole, permissionIds);
  }
}
