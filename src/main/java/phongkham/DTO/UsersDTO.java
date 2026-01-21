package phongkham.DTO;

public class UsersDTO {
    private String UserID;
    private String Username;
    private String Password;
    private String Email;

    public UsersDTO() {
        UserID = "";
        Username = "";
        Password = "";
        Email = "";
    }

    public UsersDTO(String UserID, String Username, String Password, String Email) {
        this.UserID = UserID;
        this.Username = Username;
        this.Password = Password;
        this.Email = Email;
    }

    public UsersDTO(UsersDTO u) {
        this.UserID = u.UserID;
        this.Username = u.Username;
        this.Password = u.Password;
        this.Email = u.Email;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
}