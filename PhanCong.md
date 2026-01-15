# PHÂN CÔNG CÔNG VIỆC - HỆ THỐNG QUẢN LÝ PHÒNG KHÁM BỆNH

## 🎯 THÔNG TIN CHUNG

- **Số thành viên team**: 6 người
- **Tổng số module**: 21 module (16 cũ + 5 phân quyền)
- **Phân chia công việc**: Mỗi thành viên làm **đầy đủ 3 tầng (Gui bus dao )** (DTO-DAO-BUS-GUI) cho các module được giao
- **Phân chia mới**: 3 người x 4 module + 3 người x 3 module = 21 module

## 📋 DANH SÁCH CÁC MODULE THEO DATABASE SCHEMA

**📊 Dựa trên Database Diagram thực tế (21 module):**

### 🏥 **MODULE NGHIỆP VỤ CHÍNH (16 module)**

1. **BacSi** - Quản lý bác sĩ (có FK đến Khoa)
2. **Khoa** - Khoa khám bệnh
3. **LichLamViec** - Lịch làm việc bác sĩ (FK đến BacSi)
4. **LichKham** - Lịch hẹn khám (FK đến BacSi, Khoa)
5. **PhieuKham** - Phiếu khám bệnh (FK đến LichKham, GoiDichVu)
6. **HoSoBenhAn** - Hồ sơ bệnh án **[CORE]** (FK đến PhieuKham, BacSi)
7. **DonThuoc** - Đơn thuốc (FK đến HoSoBenhAn)
8. **CTDonThuoc** - Chi tiết đơn thuốc (FK đến DonThuoc, Thuoc)
9. **Thuoc** - Danh mục thuốc (FK đến NhaCungCap)
10. **NhaCungCap** - Nhà cung cấp thuốc
11. **PhieuNhap** - Phiếu nhập thuốc (FK đến NhaCungCap)
12. **CTPhieuNhap** - Chi tiết phiếu nhập (FK đến PhieuNhap, Thuoc)
13. **HoaDonThuoc** - Hóa đơn bán thuốc (FK đến DonThuoc - nullable)
14. **CTHDThuoc** - Chi tiết hóa đơn thuốc (FK đến HoaDonThuoc, Thuoc)
15. **HoaDonKham** - Hóa đơn khám bệnh (FK đến PhieuKham)
16. **GoiDichVu** - Gói dịch vụ khám

### 🔐 **MODULE PHÂN QUYỀN MỚI (5 module)**

17. **Users** - Người dùng hệ thống (username, password, email, status)
18. **Roles** - Vai trò/chức vụ (role_name, description)
19. **Permissions** - Quyền hạn (permission_key, description)
20. **UserRoles** - Phân quyền user-role (FK đến Users, Roles)
21. **RolePermissions** - Phân quyền role-permission (FK đến Roles, Permissions)

---

## 👥 PHÂN CHIA THÀNH VIÊN (DỰA TRÊN DATABASE SCHEMA)

### 🧑‍💻 **QUANG HỮU** (4 module - 19.05%)

**Chuyên trách**: User & Lịch làm việc bác sĩ

| Module          | Mức độ | Mối quan hệ       | Mô tả                               |
| --------------- | ------ | ----------------- | ----------------------------------- |
| **Users**       | Core   | ← UserRoles       | Người dùng hệ thống, authentication |
| **BacSi**       | Core   | → Khoa (FK)       | Thông tin bác sĩ, liên kết Users    |
| **LichLamViec** | Medium | → BacSi (FK)      | Lịch làm việc bác sĩ theo ca        |
| **Khoa**        | Core   | ← BacSi, LichKham | Khoa khám bệnh, chuyên khoa         |

**Công việc cần làm**:

- `UsersDTO.java` - `UsersDAO.java` - `UsersBUS.java` - `UsersGUI.java`
- `BacSiDTO.java` - `BacSiDAO.java` - `BacSiBUS.java` - `BacSiGUI.java`
- `LichLamViecDTO.java` - `LichLamViecDAO.java` - `LichLamViecBUS.java` - `LichLamViecGUI.java`
- `KhoaDTO.java` - `KhoaDAO.java` - `KhoaBUS.java` - `KhoaGUI.java`

---

### 🧑‍💻 **KỲ QUANG** (4 module - 19.05%)

**Chuyên trách**: Luồng khám bệnh chính & Roles

| Module         | Mức độ | Mối quan hệ                  | Mô tả                          |
| -------------- | ------ | ---------------------------- | ------------------------------ |
| **Roles**      | Core   | ← UserRoles, RolePermissions | Vai trò/chức vụ trong hệ thống |
| **LichKham**   | Core   | → BacSi, Khoa (FK)           | Lịch hẹn khám từ Guest         |
| **PhieuKham**  | Core   | → LichKham, GoiDichVu (FK)   | Phiếu khám bệnh, kết quả       |
| **HoSoBenhAn** | Core   | → PhieuKham, BacSi (FK)      | **TRUNG TÂM** - Hồ sơ bệnh án  |

**Công việc cần làm**:

- `RolesDTO.java` - `RolesDAO.java` - `RolesBUS.java` - `RolesGUI.java`
- `LichKhamDTO.java` - `LichKhamDAO.java` - `LichKhamBUS.java` - `LichKhamGUI.java`
- `PhieuKhamDTO.java` - `PhieuKhamDAO.java` - `PhieuKhamBUS.java` - `PhieuKhamGUI.java`
- `HoSoBenhAnDTO.java` - `HoSoBenhAnDAO.java` - `HoSoBenhAnBUS.java` - `HoSoBenhAnGUI.java`

---

### 🧑‍💻 **BẢO TRÍ** (4 module - 19.05%)

**Chuyên trách**: Đơn thuốc & Permissions

| Module          | Mức độ | Mối quan hệ            | Mô tả                         |
| --------------- | ------ | ---------------------- | ----------------------------- |
| **Permissions** | Medium | ← RolePermissions      | Quyền hạn hệ thống            |
| **GoiDichVu**   | Medium | ← PhieuKham            | Gói dịch vụ khám, combo       |
| **DonThuoc**    | Core   | → HoSoBenhAn (FK)      | Đơn thuốc bác sĩ kê           |
| **CTDonThuoc**  | Medium | → DonThuoc, Thuoc (FK) | Chi tiết từng thuốc trong đơn |

**Công việc cần làm**:

- `PermissionsDTO.java` - `PermissionsDAO.java` - `PermissionsBUS.java` - `PermissionsGUI.java`
- `GoiDichVuDTO.java` - `GoiDichVuDAO.java` - `GoiDichVuBUS.java` - `GoiDichVuGUI.java`
- `DonThuocDTO.java` - `DonThuocDAO.java` - `DonThuocBUS.java` - `DonThuocGUI.java`
- `CTDonThuocDTO.java` - `CTDonThuocDAO.java` - `CTDonThuocBUS.java` - `CTDonThuocGUI.java`

---

### 🧑‍💻 **NHƯ QUỲNH** (3 module - 14.29%)

**Chuyên trách**: Quản lý thuốc và nhập kho

| Module         | Mức độ | Mối quan hệ        | Mô tả                     |
| -------------- | ------ | ------------------ | ------------------------- |
| **NhaCungCap** | Medium | ← Thuoc, PhieuNhap | Nhà cung cấp thuốc        |
| **Thuoc**      | Core   | → NhaCungCap (FK)  | Danh mục thuốc, thông tin |
| **PhieuNhap**  | Core   | → NhaCungCap (FK)  | Phiếu nhập thuốc từ NCC   |

**Công việc cần làm**:

- `NhaCungCapDTO.java` - `NhaCungCapDAO.java` - `NhaCungCapBUS.java` - `NhaCungCapGUI.java`
- `ThuocDTO.java` - `ThuocDAO.java` - `ThuocBUS.java` - `ThuocGUI.java`
- `PhieuNhapDTO.java` - `PhieuNhapDAO.java` - `PhieuNhapBUS.java` - `PhieuNhapGUI.java`

---

### 🧑‍💻 **THÀNH NHÂN** (3 module - 14.29%)

**Chuyên trách**: Quan hệ nhiều-nhiều & Thanh toán

| Module          | Mức độ | Mối quan hệ             | Mô tả                                 |
| --------------- | ------ | ----------------------- | ------------------------------------- |
| **UserRoles**   | Medium | → Users, Roles (FK)     | Bảng quan hệ user-role (many-to-many) |
| **HoaDonKham**  | Medium | → PhieuKham (FK)        | Hóa đơn thanh toán khám               |
| **CTPhieuNhap** | Medium | → PhieuNhap, Thuoc (FK) | Chi tiết thuốc trong phiếu nhập       |

**Công việc cần làm**:

- `UserRolesDTO.java` - `UserRolesDAO.java` - `UserRolesBUS.java` - `UserRolesGUI.java`
- `HoaDonKhamDTO.java` - `HoaDonKhamDAO.java` - `HoaDonKhamBUS.java` - `HoaDonKhamGUI.java`
- `CTPhieuNhapDTO.java` - `CTPhieuNhapDAO.java` - `CTPhieuNhapBUS.java` - `CTPhieuNhapGUI.java`

---

### 🧑‍💻 **MINH TRIẾT** (3 module - 14.29%)

**Chuyên trách**: Hóa đơn bán thuốc và bảng quan hệ phân quyền

| Module              | Mức độ | Mối quan hệ                | Mô tả                                       |
| ------------------- | ------ | -------------------------- | ------------------------------------------- |
| **RolePermissions** | Medium | → Roles, Permissions (FK)  | Bảng quan hệ role-permission (many-to-many) |
| **HoaDonThuoc**     | Core   | → DonThuoc (FK - nullable) | Hóa đơn bán thuốc (có/không đơn)            |
| **CTHDThuoc**       | Medium | → HoaDonThuoc, Thuoc (FK)  | Chi tiết thuốc trong hóa đơn                |

**Công việc cần làm**:

- `RolePermissionsDTO.java` - `RolePermissionsDAO.java` - `RolePermissionsBUS.java` - `RolePermissionsGUI.java`
- `HoaDonThuocDTO.java` - `HoaDonThuocDAO.java` - `HoaDonThuocBUS.java` - `HoaDonThuocGUI.java`
- `CTHDThuocDTO.java` - `CTHDThuocDAO.java` - `CTHDThuocBUS.java` - `CTHDThuocGUI.java`

---

## 📊 THỐNG KÊ CÔNG VIỆC ( DỰ KIẾN )

| Thành viên | Số module | % Công việc | Vai trò chính                              | Module phân quyền  | Độ phức tạp  |
| ---------- | --------- | ----------- | ------------------------------------------ | ------------------ | ------------ |
| Quang Hữu  | 4         | 19.05%      | Users + Bác sĩ + Khoa + Lịch làm việc      | Users              | High         |
| Kỳ Quang   | 4         | 19.05%      | Roles + Luồng khám chính (CORE)            | Roles              | **HIGHEST**  |
| Bảo Trí    | 4         | 19.05%      | Permissions + Đơn thuốc + Gói dịch vụ      | Permissions        | High         |
| Như Quỳnh  | 3         | 14.29%      | Thuốc + Nhà cung cấp + Phiếu nhập          | -                  | Medium       |
| Thành Nhân | 3         | 14.29%      | UserRoles + Thanh toán + Chi tiết nhập     | UserRoles          | Medium-High  |
| Minh Triết | 3         | 14.29%      | RolePermissions + Hóa đơn thuốc            | RolePermissions    | Medium-High  |
| **TỔNG**   | **21**    | **100%**    | **Mỗi người có 1 module Auth + Nghiệp vụ** | **5 Auth modules** | **Balanced** |

---

## 🎯 NGUYÊN TẮC PHÂN CÔNG

### ✅ **Tích hợp Phân quyền vào Nghiệp vụ**

- **5 trong 6 thành viên** đều có 1 module phân quyền + các module nghiệp vụ liên quan
- **Không tách biệt**: Mỗi người hiểu cả authentication lẫn business logic
- **Thành viên 4**: Chuyên sâu nghiệp vụ thuốc (không phân quyền)

### ✅ **Cân bằng workload theo Logic nghiệp vụ**

- **4 người**: 3 module (18.75%) - Handle complex relationships
- **2 người**: 2 module (12.5%) + Leadership roles

### ✅ **Chuyên môn hóa theo Business Logic**

- **Quang Hữu**: Hệ thống bác sĩ (BacSi → Khoa ← LichLamViec)
- **Kỳ Quang**: **CORE WORKFLOW** (LichKham → PhieuKham → HoSoBenhAn)
- **Bảo Trí**: Đơn thuốc (GoiDichVu → DonThuoc → CTDonThuoc)
- **Như Quỳnh**: Thuốc & Thanh toán (NhaCungCap → Thuoc, HoaDonKham)
- **Thành Nhân**: Nhập kho (PhieuNhap → CTPhieuNhap) + Tech Lead
- **Minh Triết**: Bán thuốc (HoaDonThuoc → CTHDThuoc) + UI Lead

### ✅ **Phụ thuộc module tối thiểu theo FK**

- **Workflow chính**: LichKham → PhieuKham → HoSoBenhAn (cùng Kỳ Quang)
- **Đơn thuốc**: DonThuoc → CTDonThuoc (cùng Bảo Trí)
- **Nhập kho**: PhieuNhap → CTPhieuNhap (cùng Thành Nhân)
- **Bán thuốc**: HoaDonThuoc → CTHDThuoc (cùng Minh Triết)
- **Master-Detail khác**: Các thành viên khác xử lý FK đơn giản

## 🔗 MỐI QUAN HỆ QUAN TRỌNG TRONG DATABASE

### 🎯 **CORE ENTITIES (Cần chú ý đặc biệt)**:

1. **HoSoBenhAn** (Kỳ Quang) - **TRUNG TÂM HỆ THỐNG**
2. **BacSi** (Quang Hữu) - Authentication & Authorization
3. **Thuoc** (Như Quỳnh) - Master data cho hầu hết workflow

### 🔄 **WORKFLOW DEPENDENCIES**:

```
Guest → LichKham (KQ) → PhieuKham (KQ) → HoSoBenhAn (KQ)
                                                    ↓
                                              DonThuoc (BT)
                                                    ↓
                                            CTDonThuoc (BT)
                                                    ↓
                               HoaDonThuoc (MT) ←───────┐
                                     ↓                     │
                               CTHDThuoc (MT)           Thuoc (NQ)
```

### 🚨 **NULLABLE FOREIGN KEYS CẦN ĐặC BIỆT XỬ LÝ**:

- **HoaDonThuoc.MaDonThuoc** - Cho phép NULL (mua thuốc tự do)
- **PhieuKham.MaGoiDichVu** - Cho phép NULL (khám lẻ)

---

## 📝 CHI TIẾT CÔNG VIỆC MỖI THÀNH VIÊN

### 🔧 **Tất cả thành viên đều làm 4 tầng**:

1. **DTO** (Data Transfer Object)

   - Định nghĩa thuộc tính, constructor, getter/setter
   - Validation cơ bản (nếu cần)

2. **DAO** (Data Access Object)

   - Kết nối database, CRUD operations
   - PreparedStatement, handle SQLException

3. **BUS** (Business Logic)

   - Xử lý nghiệp vụ, validation phức tạp
   - Gọi DAO, trả kết quả cho GUI

4. **GUI** (Graphical User Interface)
   - Thiết kế giao diện Swing/JavaFX
   - Kết nối với BUS, xử lý sự kiện

---

## 🚀 LỘ TRÌNH THỰC HIỆN

### **GIAI ĐOẠN 1 (Tuần 1-2): Phát triển tầng DTO**

- Tất cả thành viên hoàn thành DTO cho module của mình
- Review chéo, thống nhất chuẩn code

### **GIAI ĐOẠN 2 (Tuần 3-4): Phát triển tầng DAO**

- Hoàn thành DAO, test kết nối database
- Thống nhất schema database cuối cùng

### **GIAI ĐOẠN 3 (Tuần 5-6): Phát triển tầng BUS**

- Hoàn thành logic nghiệp vụ
- Test tích hợp DTO-DAO-BUS

### **GIAI ĐOẠN 4 (Tuần 7-8): Phát triển tầng GUI**

- Thiết kế giao diện, kết nối BUS
- Test chức năng end-to-end

### **GIAI ĐOẠN 5 (Tuần 9): Tích hợp & Test tổng thể**

- Tích hợp tất cả module
- Test workflow nghiệp vụ
- Bug fixing, hoàn thiện

---

## 📞 PHÂN CÔNG LIÊN LẠC & HỖ TRỢ

### 🤝 **Nhóm hỗ trợ chéo**:

- **Nhóm A** (Quang Hữu, Kỳ Quang): Quản lý bác sĩ & khám bệnh
- **Nhóm B** (Bảo Trí, Như Quỳnh): Quản lý thuốc & kho
- **Nhóm C** (Thành Nhân, Minh Triết): Thanh toán & tích hợp

### 📋 **Cân bằng công việc**:

- **3 thành viên** (Quang Hữu, Kỳ Quang, Bảo Trí) làm **4 module** = 57.15% tổng công việc
- **3 thành viên** (Như Quỳnh, Thành Nhân, Minh Triết) làm **3 module** = 42.85% tổng công việc
- Tất cả đều tham gia đều các tầng DTO-DAO-BUS-GUI

---

## ⚠️ LƯU Ý QUAN TRỌNG (DỰA TRÊN DATABASE SCHEMA)

### 🐎 **DATABASE CONSTRAINTS**:

1. **HoSoBenhAn** (Thành viên 2) là **CORE TABLE** - ưu tiên cao nhất
2. **Foreign Key Dependencies**: Phải hoàn thành table cha trước table con
3. **Nullable FK**: `HoaDonThuoc.MaDonThuoc`, `PhieuKham.MaGoiDichVu`
4. **Cascade Operations**: Cần xử lý delete/update cascade

### 📊 **WORKFLOW BUSINESS LOGIC**:

1. **Không có thực thể BenhNhan** - Tất cả qua HoSoBenhAn
2. **Guest workflow**: LichKham → PhieuKham → HoSoBenhAn → DonThuoc
3. **Nhà thuốc workflow**: HoaDonThuoc có thể không liên kết DonThuoc
4. **Inventory management**: CTPhieuNhap → Thuoc stock, CTHDThuoc → Thuoc stock

### 🔧 **TECHNICAL REQUIREMENTS**:

1. **Database connection**: Sử dụng `phongkham.DB.DBConnection`
2. **Transaction handling**: Đặc biệt quan trọng cho Master-Detail operations
3. **Error handling**: Validate FK constraints trước khi insert/update
4. **Data integrity**: Check references trước khi delete

### 🔄 **COORDINATION POINTS**:

- **Kỳ Quang** (HoSoBenhAn) cần hoàn thành trước cho các thành viên khác test
- **Như Quỳnh** (Thuoc) cần hoàn thành sớm vì nhiều module khác phụ thuộc
- **Thành Nhân** (Technical Lead) hỗ trợ giải quyết FK conflicts
- **Minh Triết** (UI Lead) thiết kế UI pattern cho nullable FK

---

## 🔐 **CẬP NHẬT MỚI: HỆ THỐNG PHÂN QUYỀN**

### 📊 **TỔNG KẾT PHÂN CHIA MỚI (21 MODULE)**:

- **3 thành viên** x 4 module = 12 module (19.05% mỗi người)
- **3 thành viên** x 3 module = 9 module (14.29% mỗi người)
- **Tổng**: 21 module = 100%

### 🔗 **DEPENDENCIES PHÂN QUYỀN MỚI**:

1. **Users** (QH) → **UserRoles** (TN) → **Roles** (QH)
2. **Roles** (QH) → **RolePermissions** (MT) → **Permissions** (KQ)
3. **BacSi** (QH) tích hợp **Users** cho authentication
4. **Many-to-many UI patterns** (TN, MT) với leadership roles

### 🎯 **ƯU TIÊN THỨ TỰ PHÁT TRIỂN**:

1. **Users + Roles** (QH) - Authentication cơ bản
2. **Permissions** (KQ) - Authorization framework
3. **BacSi integration** (QH) - Link business với auth
4. **UserRoles + RolePermissions** (TN, MT) - Many-to-many
5. **Các module nghiệp vụ khác** - Business logic

---
