package phongkham.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

  private static final String URL =
    "jdbc:mysql://quanlyphongkham-doanquanlyphongkham.e.aivencloud.com:20567/PhongKham?ssl-mode=REQUIRED";
  private static final String USER = "avnadmin";
  private static final String PASS = "AVNS_P0GmFQ6e6gYGhk0ooIV";

  private static Connection connection = null;

  // Không cho tạo object
  private DBConnection() {}

  // tạo kết nối đến DB
  public static Connection getConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("Kết nối vào database thành công!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return connection;
  }

  //ngắt kết nối đến DB
  public static void closeConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        System.out.println("Đã đóng kết nối DB");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
