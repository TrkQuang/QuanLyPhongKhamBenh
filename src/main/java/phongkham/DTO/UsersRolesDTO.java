package phongkham.DTO;

public class UsersRolesDTO {
    private int user_id;
    private int role_id;

    public UsersRolesDTO() {
    }

    public UsersRolesDTO(RolesDTO rl, UsersDTO us) {
        this.role_id = rl.getSTT();
        this.user_id = us.getUserID();
    }

    public UsersRolesDTO(int user_id, int role_id) {
        this.role_id = user_id;
        this.user_id = role_id;
    }

    public int getRole_ID() {
        return this.role_id;
    }

    public int getUser_ID() {
        return this.user_id;
    }

    public void setRole_ID(int role_id) {
        this.role_id = role_id;
    }

    public void setUser_ID(int user_id) {
        this.user_id = user_id;
    }

    public void setRole_ID(RolesDTO rl) {
        this.role_id = rl.getSTT();
    }

    public void setUser_ID(UsersDTO us) {
        this.user_id = us.getUserID();
    }

}
