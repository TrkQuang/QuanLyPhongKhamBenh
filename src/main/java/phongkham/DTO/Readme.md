# DTO (Data Transfer Object)

## Ví dụ ngắn (một file DTO trông thường sẽ như vầy)

> Mục tiêu: DTO là “gói dữ liệu” dùng để truyền giữa GUI ↔ BUS ↔ DAO. DTO không chứa SQL và cũng không chứa nghiệp vụ phức tạp.

```java
package phongkham.DTO;

public class ThuocDTO {
    private String maThuoc;
    private String tenThuoc;
    private int soLuongTon;

    public ThuocDTO() {}

    public ThuocDTO(String maThuoc, String tenThuoc, int soLuongTon) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.soLuongTon = soLuongTon;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
}
```

---

## DTO là gì?

`DTO` (Data Transfer Object) là tầng mô hình dữ liệu đơn giản, dùng để:

- Biểu diễn dữ liệu của 1 “thực thể” (Bác sĩ, Thuốc, Phiếu khám, …)
- Truyền dữ liệu qua lại giữa các tầng `GUI` ↔ `BUS` ↔ `DAO`
- Giúp code rõ ràng hơn thay vì truyền nhiều tham số rời rạc

## Quy ước đặt tên

- Lớp: `*DTO` (vd: `ThuocDTO`, `BacSiDTO`, `CTPhieuNhapDTO`…)
- Thường sẽ đi theo bộ 3: `XDTO` ↔ `XDAO` ↔ `XBUS`

## DTO nên chứa gì?

- Fields (private)
- Constructor rỗng + constructor đầy đủ (tuỳ nhu cầu)
- Getter/Setter
- (Tuỳ chọn) `toString()` để debug/log

## DTO KHÔNG nên chứa gì?

- SQL / thao tác DB (để `DAO`)
- Logic nghiệp vụ phức tạp (để `BUS`)
- Code UI (để `GUI`)

## Gợi ý cách map giữa DAO và DTO

- Ở `DAO`: đọc `ResultSet` → set vào DTO
- Ở `DAO`: khi insert/update: lấy DTO → set vào `PreparedStatement`

Ví dụ mapping (pseudo):

- `ThuocDTO t = new ThuocDTO();`
- `t.setMaThuoc(rs.getString("ma_thuoc"));`
- `t.setTenThuoc(rs.getString("ten_thuoc"));`

## Trạng thái hiện tại của thư mục

Nếu nhiều file `*DTO.java` đang trống (`{}`), đó là khung (scaffold). Khi bạn bắt đầu implement CRUD ở `DAO/BUS`, bạn nên bổ sung fields + getter/setter cho DTO tương ứng để mapping dữ liệu dễ và đúng.
