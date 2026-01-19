package phongkham.DTO;

public class RolePermissionsDTO {
    private int maRolePermissions;
    private int maRole;
    private int maPermission;
    private String tenRole;
    private String tenPermission;
    private String moTa;
    private boolean active;

    // Constructor
    public RolePermissionsDTO() {
    }

    public RolePermissionsDTO(int maRolePermissions, int maRole, int maPermission,
                              String tenRole, String tenPermission, String moTa, boolean active) {
        this.maRolePermissions = maRolePermissions;
        this.maRole = maRole;
        this.maPermission = maPermission;
        this.tenRole = tenRole;
        this.tenPermission = tenPermission;
        this.moTa = moTa;
        this.active = active;
    }

    // Insert
    public RolePermissionsDTO(int maRole, int maPermission, String moTa, boolean active) {
        this.maRole = maRole;
        this.maPermission = maPermission;
        this.moTa = moTa;
        this.active = active;
    }

    // Getters và Setters
    public int getMaRolePermissions() {
        return maRolePermissions;
    }

    public void setMaRolePermissions(int maRolePermissions) {
        this.maRolePermissions = maRolePermissions;
    }

    public int getMaRole() {
        return maRole;
    }

    public void setMaRole(int maRole) {
        this.maRole = maRole;
    }

    public int getMaPermission() {
        return maPermission;
    }

    public void setMaPermission(int maPermission) {
        this.maPermission = maPermission;
    }

    public String getTenRole() {
        return tenRole;
    }

    public void setTenRole(String tenRole) {
        this.tenRole = tenRole;
    }

    public String getTenPermission() {
        return tenPermission;
    }

    public void setTenPermission(String tenPermission) {
        this.tenPermission = tenPermission;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "RolePermissionsDTO{" +
                "maRolePermissions=" + maRolePermissions +
                ", maRole=" + maRole +
                ", tenRole='" + tenRole + '\'' +
                ", tenPermission='" + tenPermission + '\'' +
                ", moTa='" + moTa + '\'' +
                ", active=" + active +
                '}';
    }
}
