# DB (Kết nối Database)

Thư mục `phongkham/DB` chứa phần **kết nối JDBC** đến MySQL để các lớp `DAO` sử dụng khi thao tác dữ liệu.

## Thành phần

- `DBConnection.java`: tạo và quản lý kết nối `java.sql.Connection`.
- `getConnection()`: trả về kết nối dùng chung (singleton).
- `closeConnection()`: đóng kết nối.
- `TESTConnection.java`: chương trình nhỏ để test kết nối DB.

## Cách dùng

- Trong các lớp `DAO`, lấy kết nối bằng:

```java
import java.sql.Connection;
import phongkham.DB.DBConnection;

Connection conn = DBConnection.getConnection();
```

- Khi thoát ứng dụng (ví dụ ở `Main`), nên đóng kết nối:

```java
DBConnection.closeConnection();
```

## Cấu hình kết nối

Các thông số nằm trực tiếp trong `DBConnection.java`:

- `URL`: JDBC URL (host/port/database)
- `USER`: username
- `PASS`: password

Gợi ý: với dự án thật, nên **tách cấu hình** ra file cấu hình (hoặc biến môi trường) để tránh hard-code mật khẩu trong code.

## Chạy test kết nối

- Chạy class `TESTConnection`.
- Nếu kết nối thành công sẽ in:
  - `Kết nối vào database thành công!`
  - `Kết nối OK`

## Lưu ý

- Hiện tại code dùng **1 Connection dùng chung** (singleton). Cách này phù hợp app Swing nhỏ, nhưng không phải connection pool.
- Nếu sau này chạy đa luồng nhiều, cân nhắc dùng connection pool (HikariCP) hoặc tạo/đóng connection theo từng thao tác.                                                                                                                      