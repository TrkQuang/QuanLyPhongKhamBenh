```java
package phongkham.DAO;

import java.util.ArrayList;
import phongkham.DTO.BacSiDTO;

// Ví dụ 1: Interface CRUD chung
public interface InterfaceDAO<T> {
  int insert(T t);
  int update(T t);
  int delete(T t);
  ArrayList<T> selectAll();
  T selectById(int id);
}

// Ví dụ 2: DAO theo nghiệp vụ (chỉ minh hoạ chữ ký method)
public interface BacSiDAOExample {
  ArrayList<BacSiDTO> selectAll();
  BacSiDTO selectById(int maBacSi);
  int insert(BacSiDTO bacSi);
}
```

# DAO (Data Access Object)

Thư mục `phongkham/DAO` chứa các lớp **DAO** dùng để **truy cập dữ liệu** (SELECT/INSERT/UPDATE/DELETE) từ cơ sở dữ liệu.

## Vai trò

- Nhận yêu cầu từ tầng `BUS` (nghiệp vụ).
- Thực thi câu lệnh SQL qua JDBC.
- Map dữ liệu DB ↔ DTO (trả về `DTO` hoặc `ArrayList<DTO>`).

Luồng chuẩn:

- `GUI` → `BUS` → `DAO` → `DB`

## Các file chính

- `InterfaceDAO.java`: interface CRUD chung cho các DAO.
- `authDAO.java`: interface cho nghiệp vụ đăng nhập/đăng ký.
- Các DAO theo module: `BacSiDAO`, `BenhNhanDAO`, `HoaDonDAO`, `UserDAO`, ...

## Quy ước khuyến nghị

- Package chuẩn: `package phongkham.DAO;`
- DAO chỉ làm việc với dữ liệu (SQL/JDBC), không chứa logic nghiệp vụ phức tạp.
- Ưu tiên nhận/trả kiểu `DTO` (vd: `UserDTO`, `BacSiDTO`).
- Mỗi module nên có:
  - `selectAll`, `selectById`
  - `insert`, `update`, `delete` (nếu module hỗ trợ CRUD)

## Kết nối DB

- Sử dụng `phongkham.DB.DBConnection.getConnection()` để lấy `Connection`.

Ví dụ (pattern JDBC tối thiểu):

```java
// Pseudo-code
// Connection conn = DBConnection.getConnection();
// PreparedStatement ps = conn.prepareStatement("SELECT ... WHERE id = ?");
// ps.setInt(1, id);
// ResultSet rs = ps.executeQuery();
```

## Lưu ý

- Nên dùng `PreparedStatement` (tránh SQL injection, dễ set tham số).
- Đóng tài nguyên JDBC (`ResultSet`, `Statement`) sau khi dùng (try-with-resources là tốt nhất).
- Nếu DTO/class đổi tên hoặc đổi package, cần update lại import trong DAO tương ứng.
