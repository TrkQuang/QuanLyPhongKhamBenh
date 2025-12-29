package main.java.phongkham.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

  private static final String URL =
    "jdbc:mysql://tramway.proxy.rlwy.net:57864/qlpk_db?useSSL=false&serverTimezone=UTC";
  private static final String USER = "root";
  private static final String PASS = "RSFJaDbgzwfGdkHBtEGwLPhFyXvemcGZ";

  private static Connection connection = null;

  // Không cho tạo object
  private DBConnection() {}

  public static Connection getConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("✅ Kết nối MySQL Railway thành công");
      }
    } catch (ClassNotFoundException e) {
      System.out.println("❌ Không tìm thấy MySQL Driver");
      e.printStackTrace();
    } catch (SQLException e) {
      System.out.println("❌ Kết nối database thất bại");
      e.printStackTrace();
    }
    return connection;
  }
}
