# 🔑 PERMISSIONS STRUCTURE - Hệ thống Phòng khám

## 📋 Tổng quan Permissions

### 🏥 **Nhóm BÁC SĨ** (BACSI_VIEW)

```
BACSI_VIEW          - Xem menu bác sĩ
KHAMBENH_VIEW       - Xem chức năng khám bệnh
KHAMBENH_CREATE     - Khám bệnh và kê đơn
HOSOBENHAN_VIEW     - Xem hồ sơ bệnh án
HOSOBENHAN_EDIT     - Cập nhật hồ sơ
DONTHUOC_VIEW       - Xem đơn thuốc
DONTHUOC_CREATE     - Kê đơn thuốc
LICHKHAM_VIEW       - Xem lịch khám
HOADONKHAM_VIEW     - Xem hóa đơn khám
LICHLAMVIEC_VIEW    - Xem lịch làm việc
LICHLAMVIEC_ADD     - Đăng ký ca làm
LICHLAMVIEC_EDIT    - Sửa lịch làm việc
```

### 💊 **Nhóm NHÀ THUỐC** (NHATHUOC_VIEW)

```
NHATHUOC_VIEW       - Xem menu nhà thuốc
THUOC_VIEW          - Xem danh sách thuốc
THUOC_ADD           - Thêm thuốc mới
THUOC_EDIT          - Sửa thông tin thuốc
THUOC_DELETE        - Xóa thuốc
PHIEUNHAP_VIEW      - Xem phiếu nhập
PHIEUNHAP_ADD       - Tạo phiếu nhập
PHIEUNHAP_EDIT      - Sửa phiếu nhập
PHIEUNHAP_DELETE    - Xóa phiếu nhập
HOADONTHUOC_VIEW    - Xem hóa đơn bán thuốc
HOADONTHUOC_ADD     - Tạo hóa đơn bán thuốc
```

### ⚙️ **Nhóm ADMIN** (ADMIN_VIEW)

```
ADMIN_VIEW          - Xem menu quản trị
BACSI_VIEW          - Xem bác sĩ
BACSI_ADD           - Thêm bác sĩ
BACSI_EDIT          - Sửa bác sĩ
BACSI_DELETE        - Xóa bác sĩ
BENHNHAN_VIEW       - Xem bệnh nhân
BENHNHAN_ADD        - Thêm bệnh nhân
BENHNHAN_EDIT       - Sửa bệnh nhân
BENHNHAN_DELETE     - Xóa bệnh nhân
KHOA_VIEW           - Xem khoa
KHOA_ADD            - Thêm khoa
KHOA_EDIT           - Sửa khoa
KHOA_DELETE         - Xóa khoa
USER_VIEW           - Xem user
USER_ADD            - Thêm user
USER_EDIT           - Sửa user
USER_DELETE         - Xóa user
PHANQUYEN_VIEW      - Xem phân quyền
PHANQUYEN_EDIT      - Sửa phân quyền
```

## 👥 Phân quyền theo Role

### 🔴 **ROLE 1: ADMIN**

- ✅ Toàn bộ quyền quản trị
- ✅ Quản lý bác sĩ, bệnh nhân, user
- ✅ Phân quyền hệ thống
- ✅ Quản lý thuốc, phiếu nhập

### 🟢 **ROLE 2: BÁC SĨ**

- ✅ Khám bệnh, kê đơn thuốc
- ✅ Xem/cập nhật hồ sơ bệnh án
- ✅ Xem lịch khám
- ✅ Đăng ký ca làm việc
- ❌ Không có quyền quản trị

### 🟡 **ROLE 3: NHÀ THUỐC**

- ✅ Quản lý thuốc (CRUD)
- ✅ Quản lý phiếu nhập (CRUD)
- ✅ Tạo hóa đơn bán thuốc
- ❌ Không xem hồ sơ bệnh án

---

## 🚀 Cách sử dụng

### 1. Chạy SQL Script

```sql
-- Trong SQL Server Management Studio hoặc Azure Data Studio
-- Mở file: database/setup_permissions.sql
-- Chạy toàn bộ script
```

### 2. Kiểm tra trong code

```java
// Trong SidePanel.java
if (Session.hasPermission("BACSI_VIEW")) {
    addMenuItem("🩺 Khám bệnh", ...);
}

if (Session.hasPermission("NHATHUOC_VIEW")) {
    addMenuItem("💊 Quản lý thuốc", ...);
}

if (Session.hasPermission("ADMIN_VIEW")) {
    addMenuItem("⚙️ Quản trị", ...);
}
```

### 3. Test

- Login với user có role khác nhau
- Kiểm tra menu hiển thị đúng theo quyền
- Debug bằng `Session.printInfo()` để xem permissions

---

## 📝 Lưu ý

- ✅ **BACSI_VIEW** là permission chính để hiển thị menu bác sĩ
- ✅ **NHATHUOC_VIEW** là permission chính để hiển thị menu nhà thuốc
- ✅ **ADMIN_VIEW** là permission chính để hiển thị menu admin
- ⚠️ Các permission khác (\_ADD, \_EDIT, \_DELETE) dùng để kiểm tra quyền thực hiện hành động cụ thể
- 🔒 Guest không có permission nào (chưa login)

---

## 🎯 Mapping với Menu

| Menu Item            | Permission cần                | Role có quyền    |
| -------------------- | ----------------------------- | ---------------- |
| 🩺 Khám bệnh         | BACSI_VIEW                    | Bác sĩ           |
| 📋 Lịch khám         | BACSI_VIEW hoặc LICHKHAM_VIEW | Bác sĩ  |
| 💊 Quản lý thuốc     | NHATHUOC_VIEW                 | Nhà thuốc, Admin |
| 📦 Phiếu nhập        | NHATHUOC_VIEW                 | Nhà thuốc, Admin |
| 🧾 Hóa đơn bán thuốc | NHATHUOC_VIEW                 | Nhà thuốc        |
| 👨‍⚕️ Quản lý bác sĩ    | ADMIN_VIEW                    | Admin            |
| 🔒 Phân quyền        | ADMIN_VIEW                    | Admin            |
