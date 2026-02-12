package phongkham.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class TESTConnection {

  public static void main(String[] args) {
    System.out.println("========== TEST KẾT NỐI DATABASE ==========\n");

    Connection conn = DBConnection.getConnection();

    if (conn != null) {
      System.out.println("✅ KẾT NỐI THÀNH CÔNG!\n");

      try {
        // Lấy thông tin database
        DatabaseMetaData metaData = conn.getMetaData();
        System.out.println("📌 Database: " + metaData.getDatabaseProductName());
        System.out.println(
          "📌 Version: " + metaData.getDatabaseProductVersion()
        );
        System.out.println("📌 URL: " + metaData.getURL());
        System.out.println("📌 User: " + metaData.getUserName());

        // Test query đơn giản
        System.out.println("\n🔍 Test query: SELECT 1...");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1 as test");
        if (rs.next()) {
          System.out.println(
            "✅ Query test thành công! Result: " + rs.getInt("test")
          );
        }
        rs.close();
        stmt.close();

        // Kiểm tra bảng LichKham
        System.out.println("\n🔍 Kiểm tra bảng LichKham...");
        ResultSet tables = metaData.getTables(null, null, "LichKham", null);
        if (tables.next()) {
          System.out.println("✅ Bảng LichKham TỒN TẠI");
        } else {
          System.out.println("❌ Bảng LichKham KHÔNG TỒN TẠI!");
          System.out.println("   Bạn cần import file SQL vào database.");
        }
        tables.close();
      } catch (Exception e) {
        System.err.println("❌ Lỗi khi test: " + e.getMessage());
        e.printStackTrace();
      }

      DBConnection.closeConnection(conn);
      System.out.println("\n========== KẾT THÚC TEST ==========");
    } else {
      System.out.println("❌ KẾT NỐI THẤT BẠI!");
      System.out.println("Kiểm tra lại:");
      System.out.println("- URL, username, password");
      System.out.println("- MySQL driver trong lib/");
      System.out.println("- Firewall/network");
    }
  }
}
