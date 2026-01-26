package phongkham.DTO;

public class UsersRolesDTO {
    private String user_id;
    private String role_id;

<<<<<<< HEAD
    public UsersRolesDTO() {
    }

    public UsersRolesDTO(RolesDTO rl, UsersDTO us) {
        this.role_id = rl.getSTT();
        this.user_id = us.getUserID();
    }

    public UsersRolesDTO(int user_id, int role_id) {
=======
    public UsersRolesDTO(){}

    public UsersRolesDTO(RolesDTO rl, UsersDTO us){
        this.role_id = rl.getSTT();
        this.user_id = us.getUserID();
    }

    public UsersRolesDTO(String user_id, String role_id){
>>>>>>> 0b75ce203a4e70e3bc5ce02c088aea616ad8545e
        this.role_id = user_id;
        this.user_id = user_id;
    }

<<<<<<< HEAD
    public int getRole_ID() {
        return this.role_id;
    }

    public int getUser_ID() {
        return this.user_id;
=======
    public String getRole_ID(){ return this.role_id;}
    public String getUser_ID(){ return this.user_id;}
   

    public void setRole_ID(String role_id){ 
        this.role_id = role_id; 
    }

    public void setUser_ID(String user_id){
         this.user_id = user_id; 
>>>>>>> 0b75ce203a4e70e3bc5ce02c088aea616ad8545e
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

<<<<<<< HEAD
    public void setUser_ID(UsersDTO us) {
=======
    public void setUser_ID(UsersDTO us){
>>>>>>> 0b75ce203a4e70e3bc5ce02c088aea616ad8545e
        this.user_id = us.getUserID();
    }

}
