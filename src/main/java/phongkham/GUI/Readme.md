# gui (Giao diện)

Thư mục `phongkham/gui` dùng để chứa **toàn bộ mã nguồn giao diện người dùng** (màn hình, form, dialog, component) cho dự án Quản Lý Phòng Khám.

## Mục tiêu

- Tách biệt phần **hiển thị/nhập liệu** (gui) khỏi phần **nghiệp vụ** (`BUS`) và **truy cập dữ liệu** (`DAO`, `DB`).
- gui chỉ nên xử lý: render UI, validate cơ bản, bắt sự kiện (click/submit), gọi tầng `BUS` để thực thi nghiệp vụ.

## Quy ước đề xuất

- Đặt tên lớp theo màn hình/chức năng:
  - `LoginFrame`, `MainFrame`, `BenhNhanForm`, `BacSiDialog`, `HoaDonPanel`...
- Mỗi màn hình nên có 1 lớp chính (Frame/Dialog/Panel) + (tuỳ chọn) lớp helper:
  - `*Controller` (bắt sự kiện, điều hướng)
  - `*ViewModel` hoặc `*State` (trạng thái hiển thị)
- Không gọi thẳng `DB` trong gui. Luồng chuẩn:
  - `gui` → `BUS` → `DAO` → `DB`

## Cấu trúc gợi ý (có thể tạo dần)

- `phongkham/gui/auth/` : đăng nhập, đổi mật khẩu
- `phongkham/gui/benhnhan/` : quản lý bệnh nhân
- `phongkham/gui/bacsi/` : quản lý bác sĩ
- `phongkham/gui/lichkham/` : lịch khám
- `phongkham/gui/hoadon/` : hóa đơn / thanh toán
- `phongkham/gui/common/` : component dùng chung (table model, validator, notify...)

## Dữ liệu trao đổi

- Ưu tiên dùng các lớp trong `phongkham/DTO` để trao đổi dữ liệu giữa gui và BUS/DAO.
- Tránh để gui phụ thuộc vào chi tiết bảng DB.

## Cách chạy (tạm thời)

- Entry point hiện nằm ở `phongkham.Main` nhưng đang rỗng. Khi có gui, bạn có thể:
  - Tạo `MainFrame`/`App` trong thư mục này.
  - Trong `Main.main(...)` khởi tạo và hiển thị màn hình chính.

## Ghi chú

- README này là khung định hướng. Khi bạn bắt đầu thêm màn hình vào `gui`, mình có thể cập nhật README theo đúng cấu trúc thực tế của dự án.
