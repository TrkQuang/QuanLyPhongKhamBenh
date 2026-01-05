```java
package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DAO.InterfaceDAO;
import phongkham.DTO.UserDTO;

// Ví dụ 1: BUS bọc nghiệp vụ CRUD (minh hoạ)
public class UserBUSExample {
  private final InterfaceDAO<UserDTO> userDAO;

  public UserBUSExample(InterfaceDAO<UserDTO> userDAO) {
    this.userDAO = userDAO;
  }

  public ArrayList<UserDTO> getAllUsers() {
    return userDAO.selectAll();
  }

  public int createUser(UserDTO user) {
    // validate cơ bản
    if (user == null) return 0;
    return userDAO.insert(user);
  }
}

// Ví dụ 2: nghiệp vụ đặc thù (minh hoạ)
// public boolean dangNhap(String username, String password) {
//   // gọi authDAO.dangNhap(...) và xử lý logic liên quan
// }
```

# BUS (Business Logic)

Thư mục `phongkham/BUS` chứa các lớp **xử lý nghiệp vụ** – là tầng trung gian giữa GUI và DAO.

## Vai trò

- Nhận dữ liệu từ GUI (thường là `DTO`).
- Validate dữ liệu theo nghiệp vụ (ràng buộc, điều kiện, kiểm tra trùng...).
- Điều phối thao tác dữ liệu thông qua `DAO` (không viết SQL trong BUS).
- Trả kết quả/DTO về cho GUI.

Luồng chuẩn:

- `GUI` → `BUS` → `DAO` → `DB`

## Quy ước khuyến nghị

- Package chuẩn: `package phongkham.BUS;`
- Mỗi module nghiệp vụ có 1 lớp BUS tương ứng: `BenhNhanBUS`, `BacSiBUS`, `HoaDonBUS`, ...
- BUS không phụ thuộc trực tiếp vào UI (Swing). BUS chỉ làm việc với DTO/primitive.
- BUS không thao tác trực tiếp với JDBC/SQL.

## Gợi ý nội dung lớp BUS

- Các hàm CRUD bọc lại DAO + validate:
  - `getAll*()`, `getById(...)`
  - `create(...)`, `update(...)`, `delete(...)`
- Các hàm nghiệp vụ riêng:
  - đăng nhập/đăng ký, phân quyền, thanh toán, xuất hoá đơn...

## Export

- `ExportService.java` được dùng cho nghiệp vụ xuất file (PDF/Excel).
- Nên để các hàm export ở đây hoặc tách theo module nếu lớn.
