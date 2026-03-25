package phongkham.DTO;

import java.time.LocalDateTime;

public class UsersDTO {

  private String UserID;
  private String Username;
  private String Password;
  private String Email;
  private Integer RoleID;
  private boolean Active;
  private boolean Archived;
  private LocalDateTime DeletedAt;
  private String DeletedReason;

  public UsersDTO() {
    UserID = "";
    Username = "";
    Password = "";
    Email = "";
    RoleID = null;
    Active = true;
    Archived = false;
    DeletedAt = null;
    DeletedReason = "";
  }

  public UsersDTO(
    String UserID,
    String Username,
    String Password,
    String Email
  ) {
    this.UserID = UserID;
    this.Username = Username;
    this.Password = Password;
    this.Email = Email;
    this.RoleID = null;
    this.Active = true;
    this.Archived = false;
    this.DeletedAt = null;
    this.DeletedReason = "";
  }

  public UsersDTO(
    String UserID,
    String Username,
    String Password,
    String Email,
    Integer RoleID,
    boolean Active
  ) {
    this.UserID = UserID;
    this.Username = Username;
    this.Password = Password;
    this.Email = Email;
    this.RoleID = RoleID;
    this.Active = Active;
    this.Archived = false;
    this.DeletedAt = null;
    this.DeletedReason = "";
  }

  public UsersDTO(
    String UserID,
    String Username,
    String Password,
    String Email,
    boolean Active
  ) {
    this(UserID, Username, Password, Email, null, Active);
  }

  public UsersDTO(UsersDTO u) {
    this.UserID = u.UserID;
    this.Username = u.Username;
    this.Password = u.Password;
    this.Email = u.Email;
    this.RoleID = u.RoleID;
    this.Active = u.Active;
    this.Archived = u.Archived;
    this.DeletedAt = u.DeletedAt;
    this.DeletedReason = u.DeletedReason;
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

  public Integer getRoleID() {
    return RoleID;
  }

  public boolean isActive() {
    return Active;
  }

  public boolean isArchived() {
    return Archived;
  }

  public LocalDateTime getDeletedAt() {
    return DeletedAt;
  }

  public String getDeletedReason() {
    return DeletedReason;
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

  public void setRoleID(Integer RoleID) {
    this.RoleID = RoleID;
  }

  public void setActive(boolean Active) {
    this.Active = Active;
  }

  public void setArchived(boolean Archived) {
    this.Archived = Archived;
  }

  public void setDeletedAt(LocalDateTime DeletedAt) {
    this.DeletedAt = DeletedAt;
  }

  public void setDeletedReason(String DeletedReason) {
    this.DeletedReason = DeletedReason;
  }
}
