package phongkham.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DBConnection {

  //(Pool) dùng chung cho cả chương trình
  private static final HikariDataSource dataSource;

  private static final String DEFAULT_URL =
    "jdbc:mysql://quanlyphongkham-doanquanlyphongkham.e.aivencloud.com:20567/PhongKham?sslMode=REQUIRED&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
  private static final String DEFAULT_USER = "avnadmin";
  private static final String DEFAULT_PASS = "AVNS_P0GmFQ6e6gYGhk0ooIV";

  private static String getConfigValue(
    String envKey,
    String sysKey,
    String fallback
  ) {
    String envValue = System.getenv(envKey);
    if (envValue != null && !envValue.trim().isEmpty()) {
      return envValue.trim();
    }
    String sysValue = System.getProperty(sysKey);
    if (sysValue != null && !sysValue.trim().isEmpty()) {
      return sysValue.trim();
    }
    return fallback;
  }

  // Khối static này sẽ tự động chạy ĐÚNG 1 LẦN duy nhất khi phần mềm bật lên
  static {
    try {
      String url = getConfigValue("DB_URL", "db.url", DEFAULT_URL);
      String user = getConfigValue("DB_USER", "db.user", DEFAULT_USER);
      String pass = getConfigValue("DB_PASS", "db.pass", DEFAULT_PASS);

      HikariConfig config = new HikariConfig();
      config.setDriverClassName("com.mysql.cj.jdbc.Driver");
      config.setJdbcUrl(url);
      config.setUsername(user);
      config.setPassword(pass);
      config.setPoolName("PhongKhamHikariPool");

      // --- CẤU HÌNH POOL ---
      config.setMaximumPoolSize(12); // Chỉ mở tối đa 12 kết nối
      config.setMinimumIdle(2); // Lúc nào cũng giữ sẵn 2 kết nối ngầm để chờ
      config.setIdleTimeout(TimeUnit.MINUTES.toMillis(3)); // Dọn kết nối rỗi sau 3 phút
      config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15)); // Chờ tối đa 15 giây để lấy kết nối
      config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5));
      config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
      config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(20));
      config.setKeepaliveTime(TimeUnit.MINUTES.toMillis(2));
      config.setConnectionTestQuery("SELECT 1");
      config.setInitializationFailTimeout(TimeUnit.SECONDS.toMillis(10));

      // Tối ưu hóa tốc độ cho MySQL
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.addDataSourceProperty("useServerPrepStmts", "true");
      config.addDataSourceProperty("rewriteBatchedStatements", "true");
      config.addDataSourceProperty("tcpKeepAlive", "true");

      // khởi tạo
      dataSource = new HikariDataSource(config);
      Runtime.getRuntime().addShutdownHook(
        new Thread(() -> {
          try {
            if (dataSource != null && !dataSource.isClosed()) {
              dataSource.close();
            }
          } catch (Exception ignored) {}
        })
      );

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
