package phongkham.DTO;

public class PermissionsDTO {
    private int maPermission;
    private String tenPermission;
    private String moTa;
    private boolean active;

    public PermissionsDTO() {
    }

    public PermissionsDTO(int maPermission, String tenPermission, String moTa, boolean active) {
        this.maPermission = maPermission;
        this.tenPermission = tenPermission;
        this.moTa = moTa;
        this.active = active;
    }

    public int getMaPermission() {
        return maPermission;
    }

    public void setMaPermission(int maPermission) {
        this.maPermission = maPermission;
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
}