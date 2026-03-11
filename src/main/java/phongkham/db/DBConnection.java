package phongkham.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

  //(Pool) dùng chung cho cả chương trình
  private static final HikariDataSource dataSource;

  private static final String URL =
    "jdbc:mysql://quanlyphongkham-doanquanlyphongkham.e.aivencloud.com:20567/PhongKham?sslMode=REQUIRED&allowPublicKeyRetrieval=true";
  private static final String USER = "avnadmin";
  private static final String PASS = "AVNS_P0GmFQ6e6gYGhk0ooIV";

  // Khối static này sẽ tự động chạy ĐÚNG 1 LẦN duy nhất khi phần mềm bật lên
  static {
    try {
      HikariConfig config = new HikariConfig();
      config.setDriverClassName("com.mysql.cj.jdbc.Driver");
      config.setJdbcUrl(URL);
      config.setUsername(USER);
      config.setPassword(PASS);

      // --- CẤU HÌNH POOL ---
      config.setMaximumPoolSize(10); // Chỉ mở tối đa 10 kết nối
      config.setMinimumIdle(2); // Lúc nào cũng giữ sẵn 2 kết nối ngầm để chờ
      config.setIdleTimeout(30000); // Nếu timeoutt 30 giây thì dọn bớt cho nhẹ máy
      config.setConnectionTimeout(10000); // Chờ tối đa 10 giây để lấy kết nối

      // Tối ưu hóa tốc độ cho MySQL
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

      // khởi tạo
      dataSource = new HikariDataSource(config);
      System.out.println("Đã khởi tạo HikariCP thành công! Sẵn sàng kết nối.");
    } catch (Exception e) {
      System.err.println("Lỗi khởi tạo HikariCP: " + e.getMessage());
      throw new RuntimeException("Không thể khởi tạo Database Pool", e);
    }
  }

  // Khóa constructor lại, không cho tạo object lung tung bằng từ khóa `new`
  private DBConnection() {}

  // Hàm lấy kết nối
  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
