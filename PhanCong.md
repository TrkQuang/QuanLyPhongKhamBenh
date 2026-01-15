# PHÂN CÔNG CÔNG VIỆC - HỆ THỐNG QUẢN LÝ PHÒNG KHÁM (21 MODULE)

## 🎯 THÔNG TIN TỔNG QUAN

- **Team size**: 6 thành viên
- **Tổng module**: 21 module (16 nghiệp vụ + 5 phân quyền)
- **Phân chia**: Mỗi người làm **đầy đủ 4 tầng** (DTO-DAO-BUS-GUI)
- **Mô hình**: Desktop Application - LAN - Internal Database Server

---

## 📋 DANH SÁCH MODULE THEO DATABASE SCHEMA

### 🏥 **MODULE NGHIỆP VỤ CHÍNH (16 module)**

1. **BacSi** - Quản lý bác sĩ (liên kết Users)
2. **Khoa** - Khoa khám bệnh
3. **LichLamViec** - Lịch làm việc bác sĩ
4. **LichKham** - Lịch hẹn khám (từ Guest)
5. **PhieuKham** - Phiếu khám bệnh
6. **HoSoBenhAn** - **[CORE]** Hồ sơ bệnh án (thay BenhNhan)
7. **DonThuoc** - Đơn thuốc bác sĩ kê
8. **CTDonThuoc** - Chi tiết đơn thuốc
9. **GoiDichVu** - Gói dịch vụ khám
10. **Thuoc** - Danh mục thuốc
11. **NhaCungCap** - Nhà cung cấp thuốc
12. **PhieuNhap** - Phiếu nhập kho thuốc
13. **CTPhieuNhap** - Chi tiết phiếu nhập
14. **HoaDonThuoc** - Hóa đơn bán thuốc (MaDonThuoc nullable)
15. **CTHDThuoc** - Chi tiết hóa đơn thuốc
16. **HoaDonKham** - Hóa đơn khám bệnh

### 🔐 **MODULE PHÂN QUYỀN (5 module)**

17. **Users** - Tài khoản người dùng (username, password, email, status)
18. **Roles** - Vai trò/chức vụ (Bác sĩ, Nhà thuốc, Admin)
19. **Permissions** - Quyền hạn cụ thể
20. **UserRoles** - Many-to-many: User ↔ Role
21. **RolePermissions** - Many-to-many: Role ↔ Permission

---

## 👥 PHÂN CHIA TEAM (CÂN BẰNG WORKLOAD)

### 🔐 **THÀNH VIÊN 1** - Authentication System (4 module - 19.05%)

**Chuyên trách**: Hệ thống đăng nhập và phân quyền

| Module          | Loại    | Mối quan hệ                  | Mô tả              |
| --------------- | ------- | ---------------------------- | ------------------ |
| **Users**       | Core    | ↔ UserRoles                  | Tài khoản hệ thống |
| **Roles**       | Core    | ↔ UserRoles, RolePermissions | Vai trò người dùng |
| **Permissions** | Medium  | ↔ RolePermissions            | Quyền hạn chi tiết |
| **UserRoles**   | Complex | Many-to-many bridge          | Gán role cho user  |

**Workflow**: Guest (không cần auth) → Bác sĩ/Nhà thuốc (cần đăng nhập)

---

### 👨‍⚕️ **THÀNH VIÊN 2** - Doctor Management (4 module - 19.05%)

**Chuyên trách**: Quản lý bác sĩ và khoa phòng

| Module              | Loại    | Mối quan hệ         | Mô tả                      |
| ------------------- | ------- | ------------------- | -------------------------- |
| **BacSi**           | Core    | → Khoa, Users       | Hồ sơ bác sĩ               |
| **Khoa**            | Core    | ← BacSi             | Khoa chuyên môn            |
| **LichLamViec**     | Medium  | → BacSi             | Lịch trực của bác sĩ       |
| **RolePermissions** | Complex | Many-to-many bridge | Phân quyền role-permission |

**Workflow**: BacSi tạo lịch làm việc → xác nhận lịch khám → khám bệnh

---

### 🏥 **THÀNH VIÊN 3** - Medical Workflow (3 module - 14.29%)

**Chuyên trách**: Luồng khám bệnh cốt lõi

| Module         | Loại     | Mối quan hệ           | Mô tả                         |
| -------------- | -------- | --------------------- | ----------------------------- |
| **LichKham**   | Core     | → BacSi, Khoa         | Lịch hẹn từ Guest             |
| **PhieuKham**  | Core     | → LichKham, GoiDichVu | Phiếu khám bệnh               |
| **HoSoBenhAn** | **CORE** | → PhieuKham, BacSi    | **TRUNG TÂM** - Thay BenhNhan |

**Workflow**: Guest đặt lịch → Bác sĩ khám → Tạo hồ sơ bệnh án

---

### 💊 **THÀNH VIÊN 4** - Medicine & Prescription (4 module - 19.05%)

**Chuyên trách**: Thuốc và đơn thuốc

| Module         | Loại   | Mối quan hệ       | Mô tả               |
| -------------- | ------ | ----------------- | ------------------- |
| **Thuoc**      | Core   | → NhaCungCap      | Master data thuốc   |
| **DonThuoc**   | Core   | → HoSoBenhAn      | Đơn thuốc bác sĩ kê |
| **CTDonThuoc** | Medium | → DonThuoc, Thuoc | Chi tiết từng thuốc |
| **GoiDichVu**  | Medium | ← PhieuKham       | Combo dịch vụ khám  |

**Workflow**: Bác sĩ kê đơn → Chi tiết thuốc → Guest mua thuốc

---

### 🏪 **THÀNH VIÊN 5** - Inventory Management (3 module - 14.29%)

**Chuyên trách**: Kho thuốc và nhập hàng

| Module          | Loại   | Mối quan hệ        | Mô tả             |
| --------------- | ------ | ------------------ | ----------------- |
| **NhaCungCap**  | Medium | ← Thuoc, PhieuNhap | Nhà cung cấp      |
| **PhieuNhap**   | Core   | → NhaCungCap       | Phiếu nhập kho    |
| **CTPhieuNhap** | Medium | → PhieuNhap, Thuoc | Chi tiết nhập kho |

**Workflow**: Nhập thuốc từ NCC → Cập nhật tồn kho → Quản lý inventory

---

### 💰 **THÀNH VIÊN 6** - Sales & Payment (3 module - 14.29%)

**Chuyên trách**: Bán thuốc và thanh toán

| Module          | Loại   | Mối quan hệ           | Mô tả                   |
| --------------- | ------ | --------------------- | ----------------------- |
| **HoaDonThuoc** | Core   | → DonThuoc (nullable) | Bán thuốc có/không đơn  |
| **CTHDThuoc**   | Medium | → HoaDonThuoc, Thuoc  | Chi tiết hóa đơn thuốc  |
| **HoaDonKham**  | Medium | → PhieuKham           | Hóa đơn thanh toán khám |

**Workflow**: Nhà thuốc bán thuốc → Lập hóa đơn → Trừ tồn kho

---

## 📊 THỐNG KÊ PHÂN CHIA

| Thành viên | Số module | % Workload | Chuyên môn           | Độ phức tạp             |
| ---------- | --------- | ---------- | -------------------- | ----------------------- |
| **TV1**    | 4         | 19.05%     | 🔐 Authentication    | Many-to-many + Auth     |
| **TV2**    | 4         | 19.05%     | 👨‍⚕️ Doctor Management | Core entities           |
| **TV3**    | 3         | 14.29%     | 🏥 Medical Workflow  | **CORE SYSTEM**         |
| **TV4**    | 4         | 19.05%     | 💊 Medicine System   | Business logic          |
| **TV5**    | 3         | 14.29%     | 🏪 Inventory         | Data management         |
| **TV6**    | 3         | 14.29%     | 💰 Sales & Payment   | Financial + nullable FK |
| **TỔNG**   | **21**    | **100%**   | **Balanced**         | **Optimized**           |

---

## 🔗 LUỒNG NGHIỆP VỤ VÀ DEPENDENCIES

### 🎯 **CORE WORKFLOW (Ưu tiên cao nhất)**

```
Guest (không auth) → LichKham (TV3) → PhieuKham (TV3) → HoSoBenhAn (TV3)
                                                              ↓
                                                         DonThuoc (TV4)
                                                              ↓
                                                     HoaDonThuoc (TV6)
```

### 🔐 **AUTHENTICATION FLOW**

```
BacSi/NhaThuoc → Users (TV1) → UserRoles (TV1) → Roles (TV1)
                                                       ↓
                                              RolePermissions (TV2) → Permissions (TV1)
```

### 📦 **INVENTORY FLOW**

```
NhaCungCap (TV5) → PhieuNhap (TV5) → CTPhieuNhap (TV5) → Thuoc (TV4)
                                                              ↓
                                                    CTDonThuoc (TV4)
                                                              ↓
                                                     CTHDThuoc (TV6)
```

---

## 🚀 TIMELINE PHÁT TRIỂN (10 TUẦN)

### **GIAI ĐOẠN 1 (Tuần 1-2): DTO Layer**

- Tất cả thành viên: Thiết kế DTO cho module của mình
- **Focus**: Data structure, relationships, validation rules

### **GIAI ĐOẠN 2 (Tuần 3-4): DAO Layer**

- **Ưu tiên**: TV1 (Users, Roles) → TV3 (HoSoBenhAn) → TV4 (Thuoc)
- Setup database connections, CRUD operations

### **GIAI ĐOẠN 3 (Tuần 5-6): BUS Layer**

- **Business logic**: Authentication (TV1), Medical workflow (TV3)
- **Many-to-many logic**: UserRoles (TV1), RolePermissions (TV2)

### **GIAI ĐOẠN 4 (Tuần 7-8): GUI Layer**

- **Guest workflows**: Kiosk interfaces cho đặt lịch, mua thuốc
- **Staff workflows**: Bác sĩ và Nhà thuốc interfaces

### **GIAI ĐOẠN 5 (Tuần 9-10): Integration & Testing**

- **End-to-end testing**: Toàn bộ luồng nghiệp vụ
- **User acceptance testing**: 3 nhóm người dùng (Guest, Bác sĩ, Nhà thuốc)

---

## ⚠️ LƯU Ý QUAN TRỌNG

### 🎯 **CORE PRINCIPLES**

1. **HoSoBenhAn là trung tâm** - KHÔNG có BenhNhan entity
2. **Guest không cần authentication** - Chỉ Bác sĩ và Nhà thuốc cần đăng nhập
3. **HoaDonThuoc.MaDonThuoc nullable** - Cho phép mua thuốc tự do
4. **Desktop app trên LAN** - Không phải web application

### 🔄 **COORDINATION POINTS**

- **TV3** (HoSoBenhAn) hoàn thành trước → các module khác test
- **TV1** (Authentication) hoàn thành sớm → TV2 tích hợp Users với BacSi
- **TV4** (Thuoc) hoàn thành sớm → TV5, TV6 phụ thuộc
- **Weekly sync meetings** để đồng bộ progress

### 🔧 **TECHNICAL STACK**

- **Database**: MySQL/PostgreSQL với connection pooling
- **GUI**: Java Swing/JavaFX cho desktop app
- **Architecture**: Layered (DTO-DAO-BUS-GUI)
- **Authentication**: Role-based access control (RBAC)

---

## 📝 DELIVERABLES MỖI THÀNH VIÊN

### **Mỗi module cần hoàn thành:**

- ✅ **DTO**: Data model với validation
- ✅ **DAO**: CRUD operations + specific queries
- ✅ **BUS**: Business logic + workflow rules
- ✅ **GUI**: User interface + event handlers

### **Shared responsibilities:**

- **TV1**: Authentication framework cho toàn hệ thống
- **TV3**: Core workflow cho medical processes
- **Documentation**: Mỗi người document module của mình

---
