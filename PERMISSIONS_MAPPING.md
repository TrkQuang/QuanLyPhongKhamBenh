# 🔐 DANH SÁCH PERMISSIONS CHO HỆ THỐNG

## 📋 MỤC LỤC

1. [Tổng quan](#tổng-quan)
2. [Danh sách Permissions](#danh-sách-permissions)
3. [Mapping Permissions với Role](#mapping-permissions-với-role)
4. [SQL Scripts](#sql-scripts)
5. [Cách sử dụng](#cách-sử-dụng)

---

## 🎯 TỔNG QUAN

Hệ thống đã được **refactor từ Role-Based sang Permission-Based**.

**Trước đây:**

```java
if (Session.hasPermission("ADMIN_VIEW")) {
    // Show menu
}
```

**Bây giờ:**

```java
if (Session.hasPermission("THUOC_VIEW")) {
    addMenuItem("💊 Quản lý thuốc", ...);
}
```

**Lợi ích:**

- ✅ **Linh hoạt hơn**: Có thể gán permission riêng lẻ cho user
- ✅ **Dễ bảo trì**: Thêm/xóa permission không ảnh hưởng toàn bộ role
- ✅ **Phân quyền chi tiết**: View/Create/Edit/Delete riêng biệt

---

## 📜 DANH SÁCH PERMISSIONS

### 1️⃣ **Quản trị hệ thống**

| Permission         | Mô tả                     | Panel             |
| ------------------ | ------------------------- | ----------------- |
| `KHOA_VIEW`        | Xem và quản lý khoa       | QUANLYKHOA        |
| `THUOC_VIEW`       | Xem và quản lý thuốc      | QUANLYTHUOC       |
| `PHIEUNHAP_VIEW`   | Xem và quản lý phiếu nhập | PHIEUNHAP         |
| `HOADONTHUOC_VIEW` | Xem hóa đơn thuốc         | HOADONTHUOC       |
| `HOADONKHAM_VIEW`  | Xem hóa đơn khám          | HOADONKHAM        |
| `PHANQUYEN_VIEW`   | Quản lý phân quyền        | PHANQUYEN         |
| `BAOCAO_VIEW`      | Xem báo cáo tổng hợp      | (Đang phát triển) |

---

### 2️⃣ **Bác sĩ**

| Permission           | Mô tả                        | Panel             |
| -------------------- | ---------------------------- | ----------------- |
| `LICHLAMVIEC_VIEW`   | Xem lịch làm việc của bác sĩ | LICHLAMVIEC       |
| `LICHKHAM_VIEW`      | Xem lịch khám bệnh           | QUANLYLICHKHAM    |
| `KHAMBENH_CREATE`    | Khám bệnh và kê đơn          | KHAMBENH          |
| `HSBA_VIEW`          | Xem hồ sơ bệnh án            | (Đang phát triển) |
| `BACSI_PROFILE_VIEW` | Xem hồ sơ cá nhân bác sĩ     | BACSI_PROFILE     |

---

### 3️⃣ **Nhà thuốc**

| Permission           | Mô tả                 | Panel       |
| -------------------- | --------------------- | ----------- |
| `THUOC_VIEW`         | Xem và quản lý thuốc  | QUANLYTHUOC |
| `PHIEUNHAP_VIEW`     | Xem và tạo phiếu nhập | PHIEUNHAP   |
| `HOADONTHUOC_VIEW`   | Xem hóa đơn thuốc     | HOADONTHUOC |
| `HOADONTHUOC_CREATE` | Bán thuốc trực tiếp   | MUATHUOC    |

---

### 4️⃣ **Lễ tân**

| Permission           | Mô tả                       | Panel          |
| -------------------- | --------------------------- | -------------- |
| `DATLICHKHAM_CREATE` | Đặt lịch khám cho bệnh nhân | DATLICHKHAM    |
| `LICHKHAM_VIEW`      | Quản lý lịch khám           | QUANLYLICHKHAM |
| `HOADONKHAM_VIEW`    | Xem hóa đơn khám            | HOADONKHAM     |

---

### 5️⃣ **Tiện ích (Guest & User)**

| Permission             | Mô tả      | Panel   |
| ---------------------- | ---------- | ------- |
| (Không cần permission) | Trang chủ  | HOME    |
| (Không cần permission) | Dịch vụ    | SERVICE |
| (Không cần permission) | Giới thiệu | ABOUT   |
| (Không cần permission) | Liên hệ    | CONTACT |

---

## 🔗 MAPPING PERMISSIONS VỚI ROLE

### **Admin** (Quản trị viên)

```
✅ KHOA_VIEW
✅ THUOC_VIEW
✅ PHIEUNHAP_VIEW
✅ HOADONTHUOC_VIEW
✅ HOADONKHAM_VIEW
✅ PHANQUYEN_VIEW
✅ BAOCAO_VIEW
✅ LICHKHAM_VIEW
✅ HSBA_VIEW
```

### **Bác sĩ** (Doctor)

```
✅ LICHLAMVIEC_VIEW
✅ LICHKHAM_VIEW
✅ KHAMBENH_CREATE
✅ HSBA_VIEW
✅ HOADONKHAM_VIEW
✅ BACSI_PROFILE_VIEW
```

### **Nhà thuốc** (Pharmacist)

```
✅ THUOC_VIEW
✅ PHIEUNHAP_VIEW
✅ HOADONTHUOC_VIEW
✅ HOADONTHUOC_CREATE
```

### **Lễ tân** (Receptionist)

```
✅ DATLICHKHAM_CREATE
✅ LICHKHAM_VIEW
✅ HOADONKHAM_VIEW
```

### **Khách** (Guest)

```
(Không có permission, chỉ truy cập trang chủ, đặt lịch, dịch vụ, giới thiệu, liên hệ)
```

---

## 💾 SQL SCRIPTS

### **1. Thêm Permissions vào database**

```sql
-- Xóa permissions cũ (nếu có)
DELETE FROM Permissions WHERE TenPermission LIKE '%_VIEW' OR TenPermission LIKE '%_CREATE';

-- ========== QUẢN TRỊ ==========
INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('KHOA_VIEW', 'Xem và quản lý khoa', 1),
('THUOC_VIEW', 'Xem và quản lý thuốc', 1),
('PHIEUNHAP_VIEW', 'Xem và quản lý phiếu nhập', 1),
('HOADONTHUOC_VIEW', 'Xem hóa đơn thuốc', 1),
('HOADONKHAM_VIEW', 'Xem hóa đơn khám', 1),
('PHANQUYEN_VIEW', 'Quản lý phân quyền', 1),
('BAOCAO_VIEW', 'Xem báo cáo tổng hợp', 1);

-- ========== BÁC SĨ ==========
INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('LICHLAMVIEC_VIEW', 'Xem lịch làm việc', 1),
('LICHKHAM_VIEW', 'Xem lịch khám bệnh', 1),
('KHAMBENH_CREATE', 'Khám bệnh và kê đơn', 1),
('HSBA_VIEW', 'Xem hồ sơ bệnh án', 1),
('BACSI_PROFILE_VIEW', 'Xem hồ sơ cá nhân bác sĩ', 1);

-- ========== NHÀ THUỐC ==========
INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('HOADONTHUOC_CREATE', 'Bán thuốc trực tiếp', 1);

-- ========== LỄ TÂN ==========
INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('DATLICHKHAM_CREATE', 'Đặt lịch khám cho bệnh nhân', 1);
```

---

### **2. Gán permissions cho Role Admin (giả sử Role Admin có STT = '1')**

```sql
-- Lấy ID của permissions vừa tạo
SET @khoa_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'KHOA_VIEW');
SET @thuoc_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'THUOC_VIEW');
SET @phieunhap_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'PHIEUNHAP_VIEW');
SET @hoadonthuoc_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HOADONTHUOC_VIEW');
SET @hoadonkham_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HOADONKHAM_VIEW');
SET @phanquyen_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'PHANQUYEN_VIEW');
SET @baocao_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'BAOCAO_VIEW');
SET @lichkham_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'LICHKHAM_VIEW');
SET @hsba_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HSBA_VIEW');

-- Gán cho Admin (STT = '1')
INSERT INTO RolePermissions (MaRole, MaPermission, Active) VALUES
(1, @khoa_view, 1),
(1, @thuoc_view, 1),
(1, @phieunhap_view, 1),
(1, @hoadonthuoc_view, 1),
(1, @hoadonkham_view, 1),
(1, @phanquyen_view, 1),
(1, @baocao_view, 1),
(1, @lichkham_view, 1),
(1, @hsba_view, 1);
```

---

### **3. Gán permissions cho Role Bác sĩ (giả sử Role BacSi có STT = '2')**

```sql
SET @lichlamviec_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'LICHLAMVIEC_VIEW');
SET @lichkham_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'LICHKHAM_VIEW');
SET @khambenh_create = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'KHAMBENH_CREATE');
SET @hsba_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HSBA_VIEW');
SET @hoadonkham_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HOADONKHAM_VIEW');
SET @bacsi_profile_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'BACSI_PROFILE_VIEW');

INSERT INTO RolePermissions (MaRole, MaPermission, Active) VALUES
(2, @lichlamviec_view, 1),
(2, @lichkham_view, 1),
(2, @khambenh_create, 1),
(2, @hsba_view, 1),
(2, @hoadonkham_view, 1),
(2, @bacsi_profile_view, 1);
```

---

### **4. Gán permissions cho Role Nhà thuốc (giả sử Role NhaThuoc có STT = '3')**

```sql
SET @thuoc_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'THUOC_VIEW');
SET @phieunhap_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'PHIEUNHAP_VIEW');
SET @hoadonthuoc_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HOADONTHUOC_VIEW');
SET @hoadonthuoc_create = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HOADONTHUOC_CREATE');

INSERT INTO RolePermissions (MaRole, MaPermission, Active) VALUES
(3, @thuoc_view, 1),
(3, @phieunhap_view, 1),
(3, @hoadonthuoc_view, 1),
(3, @hoadonthuoc_create, 1);
```

---

### **5. Gán permissions cho Role Lễ tân (giả sử Role LeTan có STT = '4')**

```sql
SET @datlichkham_create = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'DATLICHKHAM_CREATE');
SET @lichkham_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'LICHKHAM_VIEW');
SET @hoadonkham_view = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'HOADONKHAM_VIEW');

INSERT INTO RolePermissions (MaRole, MaPermission, Active) VALUES
(4, @datlichkham_create, 1),
(4, @lichkham_view, 1),
(4, @hoadonkham_view, 1);
```

---

## 🔍 CÁCH SỬ DỤNG

### **1. Kiểm tra permission trong code**

```java
if (Session.hasPermission("THUOC_VIEW")) {
    // User có quyền xem thuốc
    mainFrame.showPanel("QUANLYTHUOC");
}
```

### **2. Kiểm tra permission trong SidePanel**

File `SidePanel.java` đã được refactor:

```java
private void loadUserMenu() {
    // Quản trị hệ thống
    if (Session.hasPermission("KHOA_VIEW")) {
        addMenuItem("🏢 Quản lý khoa", e -> mainFrame.showPanel("QUANLYKHOA"));
    }

    if (Session.hasPermission("THUOC_VIEW")) {
        addMenuItem("💊 Quản lý thuốc", e -> mainFrame.showPanel("QUANLYTHUOC"));
    }

    // ... các permission khác
}
```

### **3. Thêm permission mới**

**Bước 1:** Thêm vào database

```sql
INSERT INTO Permissions (TenPermission, MoTa, Active)
VALUES ('BENHVIEN_EDIT', 'Sửa thông tin bệnh viện', 1);
```

**Bước 2:** Gán cho Role

```sql
SET @benhvien_edit = (SELECT MaPermission FROM Permissions WHERE TenPermission = 'BENHVIEN_EDIT');
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
VALUES (1, @benhvien_edit, 1); -- Admin
```

**Bước 3:** Sử dụng trong code

```java
if (Session.hasPermission("BENHVIEN_EDIT")) {
    addMenuItem("🏥 Sửa thông tin bệnh viện", e -> mainFrame.showPanel("BENHVIEN"));
}
```

---

## 🎯 QUY TẮC ĐẶT TÊN PERMISSION

### **Format:**

```
<RESOURCE>_<ACTION>
```

### **Action:**

- `VIEW` - Xem (đọc)
- `CREATE` - Tạo mới
- `EDIT` - Sửa
- `DELETE` - Xóa

### **Ví dụ:**

```
THUOC_VIEW       → Xem thuốc
THUOC_CREATE     → Thêm thuốc
THUOC_EDIT       → Sửa thuốc
THUOC_DELETE     → Xóa thuốc

KHAMBENH_VIEW    → Xem khám bệnh
KHAMBENH_CREATE  → Khám bệnh mới
```

---

## 📊 BẢNG TỔNG HỢP

| Role          | Số Permissions | Permissions                                                                                                                     |
| ------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| **Admin**     | 9              | KHOA_VIEW, THUOC_VIEW, PHIEUNHAP_VIEW, HOADONTHUOC_VIEW, HOADONKHAM_VIEW, PHANQUYEN_VIEW, BAOCAO_VIEW, LICHKHAM_VIEW, HSBA_VIEW |
| **Bác sĩ**    | 6              | LICHLAMVIEC_VIEW, LICHKHAM_VIEW, KHAMBENH_CREATE, HSBA_VIEW, HOADONKHAM_VIEW, BACSI_PROFILE_VIEW                                |
| **Nhà thuốc** | 4              | THUOC_VIEW, PHIEUNHAP_VIEW, HOADONTHUOC_VIEW, HOADONTHUOC_CREATE                                                                |
| **Lễ tân**    | 3              | DATLICHKHAM_CREATE, LICHKHAM_VIEW, HOADONKHAM_VIEW                                                                              |

---

## 🚨 LƯU Ý

### ⚠️ **Migration từ Role-Based**

Nếu hệ thống cũ dùng:

```
ADMIN_VIEW
BACSI_VIEW
NHATHUOC_VIEW
LETAN_VIEW
```

Bạn có 2 lựa chọn:

**Option 1: Giữ cả 2 (backward compatible)**

```java
if (Session.hasPermission("ADMIN_VIEW") || Session.hasPermission("KHOA_VIEW")) {
    // Show menu
}
```

**Option 2: Migrate hoàn toàn (khuyến nghị)**

- Xóa các permission cũ
- Chạy SQL scripts trên
- Cập nhật code sử dụng permission mới

---

### ⚠️ **Xử lý trùng lặp**

Một số permission như `THUOC_VIEW` có thể xuất hiện ở cả Admin và Nhà thuốc.

**Giải pháp trong SidePanel:**

```java
// Hiển thị ở Admin
if (Session.hasPermission("THUOC_VIEW")) {
    addMenuItem("💊 Quản lý thuốc", ...);
}

// Tránh duplicate ở Nhà thuốc
if (Session.hasPermission("THUOC_VIEW") && !Session.hasPermission("KHOA_VIEW")) {
    addMenuItem("📋 Quản lý thuốc", ...);
}
```

---

### ⚠️ **Session reload**

Sau khi thay đổi permission, user cần:

1. Đăng xuất
2. Đăng nhập lại

Hoặc reload permission:

```java
Session.permissionBUS.loadPermission(Session.getCurrentUserId());
```

---

## 📚 TÀI LIỆU LIÊN QUAN

- **[SidePanel.java](src/main/java/phongkham/gui/SidePanel.java)** - Menu sidebar permission-based
- **[QuanLyPhanQuyenPanel.java](src/main/java/phongkham/gui/QuanLyPhanQuyenPanel.java)** - Panel quản lý phân quyền
- **[Session.java](src/main/java/phongkham/Utils/Session.java)** - Session management
- **[PermissionBUS.java](src/main/java/phongkham/BUS/PermissionBUS.java)** - Business logic phân quyền

---

**Cập nhật:** 09/03/2026  
**Phiên bản:** 2.0 - Permission-Based Access Control
