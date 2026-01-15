# DAO (Data Access Layer)

## Ví dụ ngắn (một file DAO trông thường sẽ như vầy)

> Mục tiêu: DAO chỉ tập trung truy xuất dữ liệu (CRUD), dùng `DBConnection.getConnection()` và `PreparedStatement` để chạy SQL an toàn.

```java
package phongkham.DAO;

import phongkham.DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ThuocDAO {

    public ArrayList<Object> getAll() {
        ArrayList<Object> list = new ArrayList<>();
        String sql = "SELECT * FROM thuoc";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // TODO: map ResultSet -> ThuocDTO (khi DTO có fields)
                // Ví dụ: ThuocDTO t = new ThuocDTO(...);
                // list.add(t);
                list.add(rs.getObject(1)); // minh hoạ ngắn
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(/*ThuocDTO t*/) {
        String sql = "INSERT INTO thuoc(ma_thuoc, ten_thuoc) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // ps.setString(1, t.getMaThuoc());
            // ps.setString(2, t.getTenThuoc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

---

## DAO là gì?

`DAO` (Data Access Object) là tầng truy cập dữ liệu. Tầng này:

- Chứa các hàm thao tác DB: `SELECT/INSERT/UPDATE/DELETE`.
- Map dữ liệu từ `ResultSet` → `DTO` (và ngược lại).
- Không chứa logic nghiệp vụ (logic nghiệp vụ nằm ở `BUS`).

Trong project này, `DAO` thường làm việc với:

- `phongkham.DB.DBConnection` để lấy `Connection`
- `phongkham.DTO.*` để đóng gói dữ liệu

## Quy ước đặt tên

- Lớp: `*DAO` (vd: `ThuocDAO`, `BacSiDAO`, `HoaDonThuocDAO`…)
- Thường đi theo bộ 3: `XDTO` ↔ `XDAO` ↔ `XBUS`

## Những việc nên đặt trong DAO

- Viết câu SQL và chạy query/update
- Map `ResultSet` thành `DTO`
- Trả về dữ liệu thô phục vụ nghiệp vụ (list DTO, 1 DTO, boolean, int…)

## Những việc KHÔNG nên đặt trong DAO

- Validate nghiệp vụ (vd: “tồn kho phải đủ”, “không được trùng lịch”) → để `BUS`
- Hiển thị UI / thông báo (JOptionPane, JTable, …) → để `GUI`

## Best practices (nên theo để code sạch và an toàn)

- Ưu tiên `PreparedStatement` (tránh SQL Injection)
- Dùng `try-with-resources` để auto close `PreparedStatement`/`ResultSet`
- Không `System.out.println` data nhạy cảm (credentials, thông tin bệnh nhân…)
- Nếu cần transaction (nhiều query phải cùng thành công): xử lý ở mức `Connection` (có thể do BUS điều phối, DAO cung cấp hàm hỗ trợ)

## Gợi ý chữ ký hàm thường gặp

- Lấy danh sách:
  - `ArrayList<XDTO> getAll()`
- Lấy theo khoá:
  - `XDTO findById(String id)`
- Thêm/sửa/xoá:
  - `boolean insert(XDTO x)`
  - `boolean update(XDTO x)`
  - `boolean delete(String id)`

## Trạng thái hiện tại của thư mục

Nếu bạn thấy nhiều file `*DAO.java` đang trống (`{}`), đó là khung (scaffold) để hoàn thiện dần. README này mô tả chuẩn triển khai khi bạn bắt đầu viết CRUD thật.
