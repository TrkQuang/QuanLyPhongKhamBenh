# BUS (Business Layer)

## Ví dụ ngắn (một file BUS trông thường sẽ như vầy)

> Mục tiêu: BUS đứng giữa GUI ↔ DAO/DB, xử lý nghiệp vụ/validate và gọi DAO để thao tác dữ liệu.

```java
package phongkham.BUS;

import java.util.ArrayList;
import phongkham.DAO.ThuocDAO;
import phongkham.DTO.ThuocDTO;

public class ThuocBUS {
    private final ThuocDAO thuocDAO = new ThuocDAO();

    public ArrayList<ThuocDTO> getAll() {
        return thuocDAO.getAll();
    }

    public boolean add(ThuocDTO t) {
        if (t == null) return false;
        if (t.getMaThuoc() == null || t.getMaThuoc().isBlank()) return false;
        if (t.getTenThuoc() == null || t.getTenThuoc().isBlank()) return false;
        return thuocDAO.insert(t);
    }
}
```

---

## BUS là gì?

`BUS` (Business) là tầng nghiệp vụ của ứng dụng. Tầng này:

- Nhận dữ liệu từ `GUI` (hoặc `Main`) và chuẩn hoá/kiểm tra (validation).
- Thực thi logic nghiệp vụ (quy tắc, ràng buộc, tính toán, trạng thái).
- Gọi `DAO` để truy xuất/ghi dữ liệu xuống DB.
- Trả kết quả về cho `GUI` (dữ liệu, boolean, message tuỳ thiết kế).

Trong project này, cấu trúc thường là:

- `DTO`: đối tượng dữ liệu (model) dùng để truyền qua các tầng
- `DAO`: làm việc trực tiếp với DB/SQL (CRUD)
- `BUS`: kiểm tra nghiệp vụ + điều phối DAO

## Quy ước đặt tên

- Lớp: `*BUS` (vd: `BacSiBUS`, `ThuocBUS`, `HoaDonThuocBUS`…)
- Tương ứng với DAO/DTO: thường sẽ có `XBUS` ↔ `XDAO` ↔ `XDTO`

## Những việc nên đặt trong BUS

- Validate dữ liệu đầu vào trước khi gọi DAO
  - bắt buộc nhập (not null/blank)
  - ràng buộc số lượng/đơn giá > 0
  - ràng buộc ngày tháng (ngày khám >= hôm nay, v.v.)
- Quy tắc nghiệp vụ
  - tính tổng tiền hoá đơn, giảm giá, cập nhật tồn kho
  - kiểm tra trùng lịch
- Gom nhiều thao tác DAO thành 1 thao tác nghiệp vụ (nếu cần)

## Những việc KHÔNG nên đặt trong BUS

- Viết câu SQL trực tiếp (nên nằm trong `DAO`)
- Code giao diện (JTable, JFrame, JOptionPane…) (nên nằm trong `GUI`)

## Gợi ý chữ ký hàm thường gặp

Tuỳ file hiện tại đang viết theo kiểu nào, nhưng thường BUS sẽ có:

- Lấy danh sách:
  - `ArrayList<XDTO> getAll()`
- Thêm/sửa/xoá:
  - `boolean insert(XDTO x)` / `boolean add(XDTO x)`
  - `boolean update(XDTO x)`
  - `boolean delete(String id)`
- Tìm kiếm:
  - `ArrayList<XDTO> search(String keyword)`
  - hoặc `XDTO findById(String id)`

## Luồng gọi điển hình

1. GUI tạo `DTO` từ dữ liệu người dùng nhập
2. GUI gọi `BUS.add(dto)`
3. BUS validate + xử lý logic
4. BUS gọi `DAO.insert(dto)`
5. DAO chạy SQL qua `DBConnection` và trả kết quả
6. BUS trả kết quả về GUI

## Note

- Một số BUS trong project có thể đang viết theo style khác (static method, trả về int, v.v.). README này mô tả chuẩn chung; bạn có thể chỉnh lại cho khớp codebase hiện tại.
