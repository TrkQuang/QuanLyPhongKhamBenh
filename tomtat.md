# Tóm tắt luồng xử lý code (đọc để nắm nhanh toàn hệ thống)

Tài liệu này bám theo source code hiện tại để bạn hiểu rõ luồng: mở app -> login -> lưu session -> phân quyền -> điều hướng màn hình -> logout.

## 1) Kiến trúc tổng thể

Project đi theo mô hình:

- GUI: màn hình và sự kiện người dùng
- BUS: luật nghiệp vụ, validate input
- DAO: truy vấn SQL bằng PreparedStatement
- DB: MySQL + HikariCP pool

File tiêu biểu:

- [src/main/java/phongkham/Main.java](src/main/java/phongkham/Main.java)
- [src/main/java/phongkham/gui/auth/LoginForm.java](src/main/java/phongkham/gui/auth/LoginForm.java)
- [src/main/java/phongkham/BUS/UsersBUS.java](src/main/java/phongkham/BUS/UsersBUS.java)
- [src/main/java/phongkham/dao/UsersDAO.java](src/main/java/phongkham/dao/UsersDAO.java)
- [src/main/java/phongkham/Utils/Session.java](src/main/java/phongkham/Utils/Session.java)
- [src/main/java/phongkham/gui/main/MainFrame.java](src/main/java/phongkham/gui/main/MainFrame.java)

## 2) Luồng khởi động ứng dụng

1. `Main.main()` set LookAndFeel rồi mở `LoginForm`.
2. Người dùng mới bắt đầu ở form đăng nhập.

Mốc code:

- [src/main/java/phongkham/Main.java#L9](src/main/java/phongkham/Main.java#L9)
- [src/main/java/phongkham/Main.java#L19](src/main/java/phongkham/Main.java#L19)

## 3) Luồng đăng nhập chi tiết

### 3.1 Người dùng bấm nút Đăng nhập

`LoginForm.login()` lấy username/password từ UI, kiểm tra rỗng.

- [src/main/java/phongkham/gui/auth/LoginForm.java#L337](src/main/java/phongkham/gui/auth/LoginForm.java#L337)

### 3.2 Gọi BUS -> DAO để check tài khoản

Chuỗi gọi:

- `LoginForm.login()`
- `UsersBUS.login(username, password)`
- `UsersDAO.checkLogin(username, password)`

SQL thực thi trong DAO:

`SELECT * FROM Users WHERE Username = ? AND Password = ? AND Active = 1`

Mốc code:

- [src/main/java/phongkham/BUS/UsersBUS.java#L15](src/main/java/phongkham/BUS/UsersBUS.java#L15)
- [src/main/java/phongkham/dao/UsersDAO.java#L111](src/main/java/phongkham/dao/UsersDAO.java#L111)

### 3.3 Nếu login thành công thì lưu Session

`Session.login(user)` được gọi trong `LoginForm`.

Mốc code:

- [src/main/java/phongkham/gui/auth/LoginForm.java#L364](src/main/java/phongkham/gui/auth/LoginForm.java#L364)
- [src/main/java/phongkham/Utils/Session.java#L18](src/main/java/phongkham/Utils/Session.java#L18)

Session lưu 3 thứ chính (đều nằm trong RAM, static, không ghi DB):

1. `currentUser`: object `UsersDTO` user đang đăng nhập
2. `currentPermissions`: `Set<String>` quyền của user
3. `currentBacSiID`: mã bác sĩ đang login (nếu role bác sĩ)

Mốc code:

- [src/main/java/phongkham/Utils/Session.java#L11](src/main/java/phongkham/Utils/Session.java#L11)
- [src/main/java/phongkham/Utils/Session.java#L12](src/main/java/phongkham/Utils/Session.java#L12)

### 3.4 Quyền được nạp và lưu thế nào

Trong `Session.login(user)`:

1. Tạo `PermissionBUS`
2. Gọi `loadPermission(userId)`
3. `PermissionsDAO.getPermissionByUser(userId)` join các bảng `Users -> Roles -> RolePermissions -> Permissions`
4. Đưa toàn bộ quyền vào `currentPermissions` và chuẩn hóa chữ HOA để tránh lệch hoa thường

Mốc code:

- [src/main/java/phongkham/BUS/PermissionBUS.java#L18](src/main/java/phongkham/BUS/PermissionBUS.java#L18)
- [src/main/java/phongkham/dao/PermissionsDAO.java#L32](src/main/java/phongkham/dao/PermissionsDAO.java#L32)
- [src/main/java/phongkham/Utils/Session.java#L28](src/main/java/phongkham/Utils/Session.java#L28)

### 3.5 Bác sĩ thì map thêm `currentBacSiID`

Sau `Session.login`, `LoginForm` kiểm tra `roleId == 2`:

- Tìm bác sĩ theo email
- Nếu không ra thì suy luận từ username (ví dụ BS001)
- Gán vào `Session.setCurrentBacSiID(...)`

Mốc code:

- [src/main/java/phongkham/gui/auth/LoginForm.java#L370](src/main/java/phongkham/gui/auth/LoginForm.java#L370)

### 3.6 Mở MainFrame và dựng layout theo quyền

`openMainFrame()` tạo `MainFrame`, gọi `reloadLayoutAfterLogin()`.

Mốc code:

- [src/main/java/phongkham/gui/auth/LoginForm.java#L402](src/main/java/phongkham/gui/auth/LoginForm.java#L402)
- [src/main/java/phongkham/gui/main/MainFrame.java#L145](src/main/java/phongkham/gui/main/MainFrame.java#L145)

## 4) Sau login: menu và route được xử lý ở đâu

### 4.1 Xác định mode user

`RoleResolver.resolve()` phân mode dựa trên permission đang có trong Session:

- Guest: chưa đăng nhập
- Admin: có `ROLE_PERMISSION_MANAGE` hoặc `USER_MANAGE`
- Nhà thuốc: có quyền thuốc/phiếu nhập/hóa đơn thuốc
- Còn lại: bác sĩ

Mốc code:

- [src/main/java/phongkham/gui/main/RoleResolver.java#L9](src/main/java/phongkham/gui/main/RoleResolver.java#L9)

### 4.2 Dựng Sidebar theo permission

`Sidebar.buildMenu()` kiểm tra `Session.hasPermission(...)` để show/hide menu từng mục.

Mốc code:

- [src/main/java/phongkham/gui/main/Sidebar.java#L99](src/main/java/phongkham/gui/main/Sidebar.java#L99)

### 4.3 Chặn truy cập route trái phép

Mỗi lần navigate, `MainFrame.onNavigate(route)` gọi `canAccess(route)`.

Nếu không có quyền:

- hiện cảnh báo
- đưa user về HOME

Mốc code:

- [src/main/java/phongkham/gui/main/MainFrame.java#L44](src/main/java/phongkham/gui/main/MainFrame.java#L44)
- [src/main/java/phongkham/gui/main/MainFrame.java#L55](src/main/java/phongkham/gui/main/MainFrame.java#L55)

## 5) Guest mode khác gì login thường

`openGuestMode()` không đăng nhập DB:

1. `Session.logout()` để chắc chắn sạch session
2. vào `MainFrame`
3. chỉ cho 2 route: `DAT_LICH`, `MUA_THUOC`

Mốc code:

- [src/main/java/phongkham/gui/auth/LoginForm.java#L409](src/main/java/phongkham/gui/auth/LoginForm.java#L409)
- [src/main/java/phongkham/gui/main/MainFrame.java#L63](src/main/java/phongkham/gui/main/MainFrame.java#L63)

## 6) Logout xử lý ra sao

Trong Header, bấm Đăng xuất sẽ:

1. confirm dialog
2. `Session.logout()`
3. đóng MainFrame
4. mở lại `LoginForm`

Mốc code:

- [src/main/java/phongkham/gui/main/Header.java](src/main/java/phongkham/gui/main/Header.java)

## 7) Lưu session ở đâu? Có xuống DB không?

- Session nằm trong class static [src/main/java/phongkham/Utils/Session.java](src/main/java/phongkham/Utils/Session.java)
- Chỉ ở bộ nhớ RAM của app đang chạy
- Không có bảng riêng để persist session
- Tắt app là mất session

## 8) Sơ đồ gọi hàm nhanh

```text
Main.main
  -> new LoginForm
      -> login()
          -> UsersBUS.login
              -> UsersDAO.checkLogin (SELECT Users)
          -> Session.login(user)
              -> PermissionBUS.loadPermission
                  -> PermissionsDAO.getPermissionByUser (Users->Roles->RolePermissions->Permissions)
              -> currentUser/currentPermissions được set trong RAM
          -> openMainFrame
              -> MainFrame.reloadLayoutAfterLogin
                  -> Sidebar.buildMenu (ẩn/hiện menu theo quyền)
                  -> onNavigate(defaultRoute)
                      -> canAccess(route) (chặn route trái phép)
                      -> ContentPanel.showRoute(route)
```

## 9) Điểm bạn nên debug để hiểu sâu nhất

1. Đặt breakpoint ở `LoginForm.login()`.
2. Step vào `UsersDAO.checkLogin()` để thấy SQL check account.
3. Step vào `Session.login()` để thấy nạp quyền.
4. Step vào `MainFrame.reloadLayoutAfterLogin()` và `Sidebar.buildMenu()` để thấy menu đổi theo role.
5. Thử click route không có quyền để vào `MainFrame.canAccess()`.

## 10) Ghi chú kỹ thuật quan trọng

- Password hiện đang check trực tiếp dạng plain text trong query login.
- Phân quyền route làm 2 lớp: ẩn menu + chặn route.
- Role hiển thị UI thực tế suy ra từ permission trong session, không chỉ nhìn RoleID.

Nếu bạn muốn, mình có thể làm thêm bản `tomtat_flow_login_only.md` chỉ tập trung đúng login/session/phân quyền trong 1 trang A4 để ôn trước khi thuyết trình.

## 11) Luồng Dashboard (chi tiết theo code)

Phần Dashboard bạn chụp ảnh là route `DASHBOARD` trong hệ Admin.

Điểm vào route:

- [src/main/java/phongkham/gui/main/MainFrame.java#L111](src/main/java/phongkham/gui/main/MainFrame.java#L111)
- [src/main/java/phongkham/gui/main/MainFrame.java#L145](src/main/java/phongkham/gui/main/MainFrame.java#L145)
- [src/main/java/phongkham/gui/main/ContentPanel.java#L49](src/main/java/phongkham/gui/main/ContentPanel.java#L49)

Khi route mở, `DashboardPanel` được khởi tạo và chạy `init()`.

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L51](src/main/java/phongkham/gui/admin/DashboardPanel.java#L51)

### 11.1 Dashboard dựng layout như thế nào

Trong `init()`:

1. Gọi `collectMetrics()` gom toàn bộ dữ liệu thống kê.
2. Dựng 3 khối UI:

- hàng số liệu tổng quan (4 card)
- khối giữa gồm chart + trạng thái hệ thống (split 72/28)
- khối dưới là 2 bảng cảnh báo thuốc

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L52](src/main/java/phongkham/gui/admin/DashboardPanel.java#L52)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L68](src/main/java/phongkham/gui/admin/DashboardPanel.java#L68)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L87](src/main/java/phongkham/gui/admin/DashboardPanel.java#L87)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L116](src/main/java/phongkham/gui/admin/DashboardPanel.java#L116)

### 11.2 Các card số liệu lấy từ đâu

4 card ở đầu màn hình:

1. Số bác sĩ: `BacSiBUS.countAll()`
2. Số tài khoản: `UsersBUS.getAllUsers().size()`
3. Doanh thu: cộng `TongTien` của toàn bộ `HoaDonKham`
4. Lịch khám hôm nay: đếm `LichKham` có ngày bắt đầu bằng ngày hiện tại

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L198](src/main/java/phongkham/gui/admin/DashboardPanel.java#L198)
- [src/main/java/phongkham/BUS/BacSiBUS.java#L90](src/main/java/phongkham/BUS/BacSiBUS.java#L90)
- [src/main/java/phongkham/BUS/UsersBUS.java#L23](src/main/java/phongkham/BUS/UsersBUS.java#L23)
- [src/main/java/phongkham/BUS/HoaDonKhamBUS.java#L16](src/main/java/phongkham/BUS/HoaDonKhamBUS.java#L16)

Lưu ý quan trọng:

- Card doanh thu hiện tại chỉ cộng từ hóa đơn khám (`HoaDonKham`), chưa cộng doanh thu bán thuốc (`HoaDonThuoc`).

### 11.3 Biểu đồ Doanh thu theo tháng làm như nào

Biểu đồ không dùng thư viện chart ngoài. Team tự vẽ bằng Java2D trong `ChartPanel.ChartCanvas`.

Luồng dữ liệu:

1. `buildChartData()` tạo map 6 tháng gần nhất (T10, T11, ...).
2. Duyệt `HoaDonKhamDTO`:

- lấy `NgayThanhToan`
- group theo `YearMonth`
- cộng dồn `TongTien`

3. Đổ sang 2 mảng:

- `monthLabels[]`
- `monthValues[]`

4. `ChartPanel` nhận mảng và tự vẽ:

- trục X/Y
- cột bo góc
- đường line nối các điểm
- chấm tròn tại từng tháng

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L335](src/main/java/phongkham/gui/admin/DashboardPanel.java#L335)
- [src/main/java/phongkham/gui/admin/components/ChartPanel.java#L16](src/main/java/phongkham/gui/admin/components/ChartPanel.java#L16)
- [src/main/java/phongkham/gui/admin/components/ChartPanel.java#L83](src/main/java/phongkham/gui/admin/components/ChartPanel.java#L83)
- [src/main/java/phongkham/gui/admin/components/ChartPanel.java#L128](src/main/java/phongkham/gui/admin/components/ChartPanel.java#L128)

Chi tiết vẽ chart (để bạn giải thích khi thuyết trình):

- Chiều cao cột dùng công thức chuẩn hóa theo max:
  `barHeight = (value / max) * (chartHeight - 8)`
- Đường line đi qua tâm từng cột.
- Nhãn tháng in ở đáy chart.

Lưu ý thú vị trong code:

- Nếu toàn bộ 6 tháng doanh thu bằng 0, code tự gán dữ liệu giả `40 + i*16` để chart không phẳng hoàn toàn.
- Vì vậy nếu DB trống, vẫn thấy chart "nhấp nhô" dù doanh thu thật là 0.

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L378](src/main/java/phongkham/gui/admin/DashboardPanel.java#L378)

### 11.4 Khối Trạng thái hệ thống (2 progress + 3 badge)

Khối bên phải dùng component `Sidebar` (admin dashboard sidebar, không phải menu trái toàn app).

Nó nhận 5 chỉ số:

1. `confirmedRate` (Lịch đã xác nhận)
2. `stockSafetyRate` (Tồn kho an toàn)
3. `expiredQty` (Hết hạn)
4. `nearExpiryQty` (Sắp hết hạn <=30 ngày)
5. `lowStockCount` (Thuốc tồn thấp)

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L96](src/main/java/phongkham/gui/admin/DashboardPanel.java#L96)
- [src/main/java/phongkham/gui/admin/components/Sidebar.java#L20](src/main/java/phongkham/gui/admin/components/Sidebar.java#L20)

Cách tính từng chỉ số:

- `confirmedRate` = `count(DA_XAC_NHAN) / tongLich * 100`
- `stockSafetyRate` = `100 - (soThuocTonThap / tongSoThuoc * 100)`
- Thuốc tồn thấp: `SoLuongTon < 20`
- Hết hạn và sắp hết hạn lấy từ danh sách lô nhập (`ChiTietPhieuNhap`) có:
  - trạng thái phiếu nhập là `DA_NHAP` hoặc `DA_NHAP_KHO`
  - `SoLuongConLai > 0`
  - có `HanSuDung`

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L221](src/main/java/phongkham/gui/admin/DashboardPanel.java#L221)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L233](src/main/java/phongkham/gui/admin/DashboardPanel.java#L233)
- [src/main/java/phongkham/BUS/LichKhamBUS.java#L266](src/main/java/phongkham/BUS/LichKhamBUS.java#L266)
- [src/main/java/phongkham/BUS/CTPhieuNhapBUS.java#L79](src/main/java/phongkham/BUS/CTPhieuNhapBUS.java#L79)

UI progress có animation:

- ProgressBar chạy từ 0 lên target bằng `Timer` mỗi 14ms, tăng 2% mỗi nhịp.

Mốc code:

- [src/main/java/phongkham/gui/admin/components/Sidebar.java#L94](src/main/java/phongkham/gui/admin/components/Sidebar.java#L94)

### 11.5 Hai bảng cảnh báo dưới cùng

#### Bảng Thuốc sắp hết hạn

Nguồn: `metrics.nearExpiryLots` (đã lọc theo điều kiện ở trên).

Xử lý:

1. Tính `daysLeft = HanSuDung - today`.
2. Chỉ giữ các lô `daysLeft <= 30`.
3. Sort tăng dần theo `daysLeft` để lô gấp lên trước.
4. Cắt top 8 dòng cho gọn UI.

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L251](src/main/java/phongkham/gui/admin/DashboardPanel.java#L251)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L276](src/main/java/phongkham/gui/admin/DashboardPanel.java#L276)

#### Bảng Thuốc tồn thấp

Nguồn: danh sách `ThuocBUS.list()`.

Xử lý:

1. Sort tăng dần theo `SoLuongTon`.
2. Lấy thuốc có `SoLuongTon < 20`.
3. Cắt top 8 dòng.

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L283](src/main/java/phongkham/gui/admin/DashboardPanel.java#L283)

### 11.6 Vì sao ảnh bạn có "Lịch khám hôm nay = 0" nhưng vẫn có chart cao ở T2/T3

Theo code hiện tại, 2 chỉ số này độc lập:

- `Lịch khám hôm nay` đếm từ `LichKham` đúng ngày hiện tại.
- Chart doanh thu lại cộng `HoaDonKham` theo `NgayThanhToan` của 6 tháng.

Nên hoàn toàn có thể:

- hôm nay không có lịch mới (`0`)
- nhưng các tháng trước đã có hóa đơn thanh toán cao (chart vẫn cao).

### 11.7 Debug nhanh Dashboard (đúng chỗ cần đặt breakpoint)

1. `DashboardPanel.init()` để thấy thứ tự dựng UI.
2. `DashboardPanel.collectMetrics()` để theo dõi số liệu gốc.
3. `DashboardPanel.buildChartData()` để xem nhãn/tháng và value.
4. `ChartPanel.ChartCanvas.paintComponent()` để xem cách vẽ cột + line.
5. `Sidebar.animateBar()` để xem animation progress.

Mốc code:

- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L51](src/main/java/phongkham/gui/admin/DashboardPanel.java#L51)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L198](src/main/java/phongkham/gui/admin/DashboardPanel.java#L198)
- [src/main/java/phongkham/gui/admin/DashboardPanel.java#L335](src/main/java/phongkham/gui/admin/DashboardPanel.java#L335)
- [src/main/java/phongkham/gui/admin/components/ChartPanel.java#L77](src/main/java/phongkham/gui/admin/components/ChartPanel.java#L77)
- [src/main/java/phongkham/gui/admin/components/Sidebar.java#L94](src/main/java/phongkham/gui/admin/components/Sidebar.java#L94)

## 12) 3 ý ngắn để thuyết trình về Dashboard

1. Dashboard đang xử lý theo mô hình "gom metrics trước, render UI sau", giúp code rõ và tách phần tính toán khỏi phần hiển thị.
2. Biểu đồ là custom vẽ tay bằng Java2D nên nhẹ, chủ động style, không phụ thuộc lib chart ngoài.
3. Cảnh báo kho dùng dữ liệu lô thuốc thực tế (số lô, HSD, số lượng còn lại), nên có giá trị vận hành chứ không chỉ là chỉ số trang trí.
