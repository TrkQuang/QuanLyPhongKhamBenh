# Giải thích luồng hoạt động các Panel (bản dễ hiểu)

Tài liệu này viết theo kiểu dễ đọc cho người mới: panel làm gì, gọi BUS thế nào, dữ liệu đi theo luồng nào, trả về `List` hay `ArrayList` ra sao.

## 1) Big Picture: app chạy theo luồng nào

Luồng chuẩn của project:

1. UI Panel nhận thao tác người dùng.
2. Panel gọi BUS để xử lý nghiệp vụ.
3. BUS validate, chuẩn hóa trạng thái, rồi gọi DAO.
4. DAO chạy SQL với `PreparedStatement`.
5. DAO trả DTO list về BUS.
6. BUS trả data về Panel.
7. Panel đổ vào `JTable`/combo box để hiển thị.

Đọc nhanh các file gốc:

- [src/main/java/phongkham/Main.java](src/main/java/phongkham/Main.java)
- [src/main/java/phongkham/gui/main/MainFrame.java](src/main/java/phongkham/gui/main/MainFrame.java)
- [src/main/java/phongkham/gui/main/ContentPanel.java](src/main/java/phongkham/gui/main/ContentPanel.java)

## 2) Mẫu chuẩn của đa số Panel

Mẫu hay gặp nhất:

```text
init()
  -> tạo table/model, nút, filter
  -> gắn action listener
  -> gọi loadData() hoặc reloadData()

loadData()/reloadData()
  -> BUS.getAll()/BUS.list()/BUS.search(...)
  -> nhận ArrayList/List<DTO>
  -> for (...) addRow vào DefaultTableModel

Nút Thêm/Sửa/Xóa
  -> mở dialog lấy input
  -> gọi BUS.insert/update/delete
  -> nhận boolean hoặc String message
  -> thông báo + reloadData()
```

Panel tiêu biểu đúng pattern:

- [src/main/java/phongkham/gui/admin/QuanLyTaiKhoanPanel.java](src/main/java/phongkham/gui/admin/QuanLyTaiKhoanPanel.java)
- [src/main/java/phongkham/gui/admin/QuanLyBacSiPanel.java](src/main/java/phongkham/gui/admin/QuanLyBacSiPanel.java)
- [src/main/java/phongkham/gui/nhathuoc/ThuocPanel.java](src/main/java/phongkham/gui/nhathuoc/ThuocPanel.java)
- [src/main/java/phongkham/gui/nhathuoc/NhaCungCapPanel.java](src/main/java/phongkham/gui/nhathuoc/NhaCungCapPanel.java)

## 3) Giải thích từng cụm Panel chính

## 3.1 Login + MainFrame + phân quyền

### LoginForm

File: [src/main/java/phongkham/gui/auth/LoginForm.java](src/main/java/phongkham/gui/auth/LoginForm.java)

Luồng:

1. Bấm đăng nhập -> `login()`.
2. Gọi `UsersBUS.login(username, password)`.
3. Nếu thành công -> `Session.login(user)` để nạp quyền.
4. Mở `MainFrame` -> `reloadLayoutAfterLogin()`.

BUS/DAO liên quan:

- [src/main/java/phongkham/BUS/UsersBUS.java](src/main/java/phongkham/BUS/UsersBUS.java)
- [src/main/java/phongkham/dao/UsersDAO.java](src/main/java/phongkham/dao/UsersDAO.java)
- [src/main/java/phongkham/Utils/Session.java](src/main/java/phongkham/Utils/Session.java)

### MainFrame / Sidebar / route guard

Files:

- [src/main/java/phongkham/gui/main/MainFrame.java](src/main/java/phongkham/gui/main/MainFrame.java)
- [src/main/java/phongkham/gui/main/Sidebar.java](src/main/java/phongkham/gui/main/Sidebar.java)
- [src/main/java/phongkham/gui/main/RoleResolver.java](src/main/java/phongkham/gui/main/RoleResolver.java)

Luồng:

1. `Sidebar.buildMenu()` kiểm tra quyền từ session để hiện menu.
2. Khi click route -> `MainFrame.onNavigate(route)`.
3. `canAccess(route)` chặn route không đúng quyền.

## 3.2 DashboardPanel (admin)

File: [src/main/java/phongkham/gui/admin/DashboardPanel.java](src/main/java/phongkham/gui/admin/DashboardPanel.java)

Luồng:

1. `init()` gọi `collectMetrics()`.
2. `collectMetrics()` gọi nhiều BUS để gom dữ liệu:
- bác sĩ
- user
- hóa đơn khám
- lịch khám
- thuốc
- lô thuốc nhập
3. Dữ liệu được gói vào `DashboardMetrics`.
4. Render:
- stat cards
- chart
- thanh trạng thái
- bảng cảnh báo

BUS gọi trong dashboard:

- `BacSiBUS.countAll()`
- `UsersBUS.getAllUsers()`
- `HoaDonKhamBUS.getAll()`
- `LichKhamBUS.getAll()`
- `ThuocBUS.list()`
- `CTPhieuNhapBUS.getAllLotsForMonitoring()`

Chart là custom Java2D, không dùng thư viện chart ngoài:

- [src/main/java/phongkham/gui/admin/components/ChartPanel.java](src/main/java/phongkham/gui/admin/components/ChartPanel.java)

## 3.3 Guest: DatLichPanel

File: [src/main/java/phongkham/gui/guest/DatLichPanel.java](src/main/java/phongkham/gui/guest/DatLichPanel.java)

Luồng chính:

1. Mở panel -> `loadComboboxData()`.
2. Gọi BUS lấy bác sĩ + gói dịch vụ.
3. Khi đổi ngày/ca/gói -> `refreshAvailableDoctors()`.
4. Bấm đặt lịch -> validate form -> gọi `LichKhamBUS.insert(...)`.

Dữ liệu trả về thường là `ArrayList`:

- bác sĩ: `ArrayList<BacSiDTO>`
- gói khám: `ArrayList<GoiDichVuDTO>`
- lịch làm: `ArrayList<LichLamViecDTO>`

## 3.4 Guest: MuaThuocPanel

File: [src/main/java/phongkham/gui/guest/MuaThuocPanel.java](src/main/java/phongkham/gui/guest/MuaThuocPanel.java)

Luồng:

1. `loadDataFromBus()` lấy danh sách thuốc.
2. `refreshMedicineTable()` lọc theo search/filter.
3. Add vào giỏ -> cập nhật table giỏ.
4. Thanh toán -> gọi BUS hóa đơn thuốc.

## 3.5 Bác sĩ: LichKhamPanel / BenhAnPanel / HoaDonKhamPanel

Files:

- [src/main/java/phongkham/gui/bacsi/LichKhamPanel.java](src/main/java/phongkham/gui/bacsi/LichKhamPanel.java)
- [src/main/java/phongkham/gui/bacsi/BenhAnPanel.java](src/main/java/phongkham/gui/bacsi/BenhAnPanel.java)
- [src/main/java/phongkham/gui/bacsi/HoaDonKhamPanel.java](src/main/java/phongkham/gui/bacsi/HoaDonKhamPanel.java)

Luồng chung:

1. `loadData()` theo role bác sĩ hiện tại.
2. Gọi BUS tương ứng (`LichKhamBUS`, `HoSoBenhAnBUS`, `HoaDonKhamBUS`).
3. Trước khi đổi trạng thái luôn có validate trong BUS.
4. Thành công -> reload dữ liệu panel.

## 3.6 Nhà thuốc: PhieuNhapPanel

File: [src/main/java/phongkham/gui/nhathuoc/PhieuNhapPanel.java](src/main/java/phongkham/gui/nhathuoc/PhieuNhapPanel.java)

Luồng tạo phiếu:

1. `loadData()` lấy list phiếu từ `PhieuNhapBUS.getAll()`.
2. Mở popup tạo phiếu.
3. Thêm nhiều line item thuốc vào bảng tạm.
4. Bấm lưu:
- lưu header phiếu nhập
- lưu chi tiết từng dòng qua `CTPhieuNhapBUS.insert(...)`
- lỗi giữa chừng có rollback cleanup
5. Sau đó `reloadData()`.

Các BUS gọi:

- `PhieuNhapBUS`
- `CTPhieuNhapBUS`
- `NhaCungCapBUS`
- `ThuocBUS`

## 3.7 Nhà thuốc: HoaDonThuocPanel

File: [src/main/java/phongkham/gui/nhathuoc/HoaDonThuocPanel.java](src/main/java/phongkham/gui/nhathuoc/HoaDonThuocPanel.java)

Luồng:

1. `reloadData()` lấy tất cả hóa đơn thuốc.
2. Lọc/search tại UI.
3. Khi xác nhận trạng thái thanh toán/lấy thuốc -> BUS xử lý nghiệp vụ FEFO, tồn kho, trace theo lô.

BUS trả về có chỗ dùng `List` thay vì `ArrayList`:

- `HoaDonThuocBUS.getAllHoaDonThuoc()` trả `List<HoaDonThuocDTO>`.

## 4) Vì sao có chỗ dùng ArrayList, có chỗ dùng List?

### Quy tắc trong codebase hiện tại

1. Rất nhiều BUS cũ trả thẳng `ArrayList<DTO>`.
2. Một số BUS mới/đã refactor trả `List<DTO>` để mềm dẻo hơn.

Ví dụ:

- `ArrayList`: [src/main/java/phongkham/BUS/ThuocBUS.java](src/main/java/phongkham/BUS/ThuocBUS.java), [src/main/java/phongkham/BUS/LichKhamBUS.java](src/main/java/phongkham/BUS/LichKhamBUS.java)
- `List`: [src/main/java/phongkham/BUS/HoaDonThuocBUS.java](src/main/java/phongkham/BUS/HoaDonThuocBUS.java), [src/main/java/phongkham/BUS/RolePermissionsBUS.java](src/main/java/phongkham/BUS/RolePermissionsBUS.java)

### Hiểu đơn giản

- `List` là interface (dùng để code linh hoạt).
- `ArrayList` là implementation cụ thể (dễ dùng, random access nhanh).

Trong project này, panel có thể dùng cả hai.

Nếu method trả `List`, bạn vẫn có thể loop bình thường:

```java
List<ThuocDTO> rows = hoaDonThuocBUS.getAllHoaDonThuoc();
for (ThuocDTO x : rows) {
  // render table
}
```

Nếu cần thao tác kiểu `ArrayList`:

```java
ArrayList<ThuocDTO> rows = new ArrayList<>(hoaDonThuocBUS.getAllHoaDonThuoc());
```

## 5) Panel gọi BUS kiểu nào là phổ biến nhất

3 kiểu return hay gặp:

1. `boolean`
- cho thao tác insert/update/delete.
- panel tự quyết định show thông báo success/fail.

2. `String` message
- BUS trả thẳng message nghiệp vụ.
- panel show message đó luôn.

3. `ArrayList/ List<DTO>`
- để đổ vào bảng/filter/sort.

Ví dụ thực tế:

- `UsersBUS.insertUser(...)` trả `String`.
- `PhieuNhapBUS.insert(...)` trả `boolean`.
- `BacSiBUS.getAll()` trả `ArrayList<BacSiDTO>`.

## 6) Mẹo đọc code panel nhanh nhất

1. Mở file panel, tìm `init()` trước.
2. Tìm `loadData()/reloadData()` để biết data source.
3. Tìm các `btn...addActionListener` để biết flow nghiệp vụ.
4. Từ mỗi action, lần theo method BUS được gọi.
5. Qua BUS xem validate nào có thể làm fail.
6. Qua DAO xem SQL thật sự chạy gì.

## 7) Sơ đồ ngắn gọn để nhớ

```text
Panel (UI)
  -> BUS (validate + rule)
      -> DAO (SQL)
          -> DB
      <- DTO list / boolean / message
  <- render JTable / Dialog
```

## 8) Kết luận ngắn

- Các panel trong project khá đồng nhất: mở lên là load data từ BUS.
- `ArrayList` dùng nhiều vì codebase cũ theo kiểu Java Swing truyền thống.
- Một số module mới đã dùng `List` để linh hoạt hơn.
- Đọc flow theo hướng event của button là nhanh và đúng nhất.

Nếu cần, mình có thể viết thêm bản `giaithich_theo_tung_panel.md` liệt kê riêng từng panel một (admin/bacsi/guest/nhathuoc), mỗi panel 5-7 dòng “mở panel -> gọi BUS nào -> return kiểu gì -> update UI ở đâu”.
