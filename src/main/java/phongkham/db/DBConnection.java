package main.java.phongkham.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/clinic_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "qlpk";
    private static final String PASS = "123456";
    private static Connection connection = null;
    // Ngăn tạo object mới (chỉ dùng static)
    private DBConnection() {}
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Đã kết nối vào DATABASE");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("❌ Kết nối database thất bại ");
            e.printStackTrace();
        }
        return connection;
    }
}








