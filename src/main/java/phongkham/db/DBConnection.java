package phongkham.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

  private static final String URL =
    "jdbc:mysql://tramway.proxy.rlwy.net:57864/PhongKham";
  private static final String USER = "root";
  private static final String PASS = "RSfJaDbgzwfGdkHBtEGwLPhFyXVemcGZ";

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
