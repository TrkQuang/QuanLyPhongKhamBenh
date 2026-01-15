package phongkham.db;

public class TESTConnection {

  public static void main(String[] args) {
    if (DBConnection.getConnection() != null) {
      System.out.println("Kết nối OK");
      DBConnection.closeConnection();
    } else {
      System.out.println("Kết nối FAIL");
    }
  }
}
