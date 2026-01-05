```java
package phongkham.DTO;

// Ví dụ DTO (Data Transfer Object)
public class BacSiDTO {
  private int maBacSi;
  private String hoTen;
  private String sdt;

  public BacSiDTO() {}

  public BacSiDTO(int maBacSi, String hoTen, String sdt) {
    this.maBacSi = maBacSi;
    this.hoTen = hoTen;
    this.sdt = sdt;
  }

  public int getMaBacSi() {
    return maBacSi;
  }

  public void setMaBacSi(int maBacSi) {
    this.maBacSi = maBacSi;
  }

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String hoTen) {
    this.hoTen = hoTen;
  }

  public String getSdt() {
    return sdt;
  }

  public void setSdt(String sdt) {
    this.sdt = sdt;
  }
}
```

# DTO (Data Transfer Object)

Thư mục `phongkham/DTO` chứa các lớp **DTO** dùng để **đóng gói dữ liệu** khi truyền qua lại giữa các tầng của ứng dụng (GUI/BUS/DAO).

## Mục tiêu

- Chuẩn hoá dữ liệu trao đổi giữa các tầng, tránh phụ thuộc trực tiếp vào DB.
- Dễ validate/format dữ liệu trước khi hiển thị lên GUI.
- Dễ mở rộng khi thêm field mà không ảnh hưởng quá nhiều phần còn lại.

## Quy ước đặt tên

- Mỗi đối tượng nghiệp vụ có 1 DTO tương ứng: `BacSiDTO`, `BenhNhanDTO`, `HoaDonDTO`, ...
- Tên file và tên `public class` phải trùng nhau (theo Java convention).
- Package chuẩn: `package phongkham.DTO;`

## Cách dùng (luồng khuyến nghị)

- GUI nhận input → tạo DTO → gọi `BUS`.
- `BUS` xử lý nghiệp vụ → gọi `DAO` để đọc/ghi dữ liệu.
- `DAO` trả dữ liệu về dưới dạng DTO (hoặc list DTO).

Luồng:

- `GUI` → `BUS` → `DAO` → `DB`

## Gợi ý nội dung DTO

- Field nên bám theo dữ liệu cần hiển thị / trao đổi (không nhất thiết giống 100% bảng DB).
- Có constructor rỗng + constructor đầy đủ (tuỳ nhu cầu).
- Có getter/setter.

Nếu bạn muốn, mình có thể chuẩn hoá các DTO hiện tại (thêm field/getter/setter theo schema trong `database/qlpk_db.sql`).
