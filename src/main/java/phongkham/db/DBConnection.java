package phongkham.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

  private static final String URL =
    "jdbc:mysql://quanlyphongkham-doanquanlyphongkham.e.aivencloud.com:20567/PhongKham?sslMode=REQUIRED&allowPublicKeyRetrieval=true";
  private static final String USER = "avnadmin";
  private static final String PASS = "AVNS_P0GmFQ6e6gYGhk0ooIV";

  // Không cho tạo object
  private DBConnection() {}

  // Tạo kết nối mới mỗi lần gọi (không cache)
  public static Connection getConnection() {
    Connection conn = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      conn = DriverManager.getConnection(URL, USER, PASS);
      System.out.println("Kết nối vào database thành công!");
    } catch (SQLException e) {
      System.err.println("Lỗi kết nối database: " + e.getMessage());
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      System.err.println("Không tìm thấy MySQL Driver: " + e.getMessage());
      e.printStackTrace();
    }
    return conn;
  }

  // Đóng connection được truyền vào
  public static void closeConnection(Connection conn) {
    try {
      if (conn != null && !conn.isClosed()) {
        conn.close();
        System.out.println("Đã đóng kết nối DB");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
