# 📋 TÓM TẮT PHÂN CÔNG - HỆ THỐNG QUẢN LÝ PHÒNG KHÁM

## 🎯 TỔNG QUAN

**6 thành viên làm 21 module** - mỗi người làm đầy đủ **3 tầng**: DAO + BUS + GUI  
**Phân chia**: 3 người × 4 module (19%) + 3 người × 3 module (14%) = **Cân bằng tối ưu**

---

## 👥 PHÂN CÔNG CHI TIẾT

### 🔐👤 **THÀNH VIÊN 1** - User + Hệ thống Bác sĩ

**🎯 Tích hợp Authentication với Nghiệp vụ Bác sĩ**

- **Users** 🔐 - Tài khoản hệ thống, authentication
- **BacSi** 👨‍⚕️ - Thông tin bác sĩ, liên kết với Users
- **LichLamViec** 📅 - Lịch làm việc bác sĩ theo ca
- **Khoa** 🏥 - Khoa khám bệnh, chuyên môn

**💡 Vai trò**: Quản lý toàn bộ hệ thống bác sĩ từ authentication đến phân khoa

---

### 🏥⭐ **THÀNH VIÊN 2** - Roles + Core Workflo

**🎯 TRUNG TÂM HỆ THỐNG - Workflow khám bệnh chính**

- **Roles** 👥 - Vai trò/chức vụ trong hệ thống
- **LichKham** 📆 - Lịch hẹn khám từ khách hàng
- **PhieuKham** 📋 - Phiếu khám bệnh, kết quả khám
- **HoSoBenhAn** ⭐ - **CORE** - Hồ sơ bệnh án (trung tâm hệ thống)

**💡 Vai trò**: Luồng khám bệnh từ đặt lịch → khám → hồ sơ bệnh án

---

### 💊 **THÀNH VIÊN 3** - Permissions + Đơn thuốc

**🎯 Quyền hạn + Quản lý đơn thuốc hoàn chỉnh**

- **Permissions** 🔑 - Quyền hạn trong hệ thống
- **GoiDichVu** 📦 - Gói dịch vụ khám, combo
- **DonThuoc** 💊 - Đơn thuốc bác sĩ kê
- **CTDonThuoc** 📝 - Chi tiết từng thuốc trong đơn

**💡 Vai trò**: Quản lý quyền hạn và luồng đơn thuốc từ kê đơn đến chi tiết

---

### 🏪 **THÀNH VIÊN 4** - Quản lý Thuốc + Nhập kho

**🎯 Chuyên sâu nghiệp vụ kho thuốc**

- **NhaCungCap** 🏭 - Nhà cung cấp thuốc
- **Thuoc** 💊 - Danh mục thuốc, master data
- **PhieuNhap** 📥 - Phiếu nhập thuốc từ NCC

**💡 Vai trò**: Master data thuốc cho toàn hệ thống, quản lý nhập kho

---

### 💰 **THÀNH VIÊN 5** - UserRoles + Thanh toán

**🎯 Many-to-many + Thanh toán + Chi tiết nhập**

- **UserRoles** 🔗 - Bảng quan hệ user-role (many-to-many)
- **HoaDonKham** 💵 - Hóa đơn thanh toán khám bệnh
- **CTPhieuNhap** 📋 - Chi tiết thuốc trong phiếu nhập

**💡 Vai trò**: Tech Lead - Xử lý quan hệ phức tạp + thanh toán

---

### 🎨 **THÀNH VIÊN 6** - RolePermissions + Bán thuốc

**🎯 Many-to-many + Hóa đơn bán thuốc**

- **RolePermissions** 🔗 - Bảng quan hệ role-permission (many-to-many)
- **HoaDonThuoc** 💰 - Hóa đơn bán thuốc (có/không đơn - nullable FK)
- **CTHDThuoc** 📝 - Chi tiết thuốc trong hóa đơn

**💡 Vai trò**: UI Lead - Thiết kế giao diện + xử lý nullable FK

---

## 🔄 WORKFLOW CHÍNH

```
Guest/Khách hàng
      ↓
[TV2] LichKham → Đặt lịch hẹn khám
      ↓
[TV2] PhieuKham → Khám bệnh, chẩn đoán
      ↓
[TV2] HoSoBenhAn ⭐ → Lưu hồ sơ bệnh án (CORE)
      ↓
[TV3] DonThuoc → Bác sĩ kê đơn
      ↓
[TV3] CTDonThuoc → Chi tiết từng thuốc
      ↓
[TV6] HoaDonThuoc → Thanh toán mua thuốc
      ↓
[TV6] CTHDThuoc → Chi tiết hóa đơn

Luồng phụ:
[TV5] HoaDonKham → Thanh toán tiền khám
```

---

## 📊 THỐNG KÊ WORKLOAD

| Thành viên | Module | % Công việc | Chuyên môn                        | Module Auth        | Độ khó       |
| ---------- | ------ | ----------- | --------------------------------- | ------------------ | ------------ |
| **TV1**    | 4      | 19.05%      | 🔐 User + Hệ thống Bác sĩ         | Users              | ⭐⭐⭐⭐     |
| **TV2**    | 4      | 19.05%      | 🏥⭐ Roles + Core Workflow        | Roles              | ⭐⭐⭐⭐⭐   |
| **TV3**    | 4      | 19.05%      | 💊 Permissions + Đơn thuốc        | Permissions        | ⭐⭐⭐⭐     |
| **TV4**    | 3      | 14.29%      | 🏪 Thuốc + Nhập kho               | -                  | ⭐⭐⭐       |
| **TV5**    | 3      | 14.29%      | 💰 UserRoles + Thanh toán         | UserRoles          | ⭐⭐⭐       |
| **TV6**    | 3      | 14.29%      | 🎨 RolePermissions + Bán thuốc    | RolePermissions    | ⭐⭐⭐       |
| **TỔNG**   | **21** | **100%**    | **5/6 người có Auth + Nghiệp vụ** | **5 Auth modules** | **⭐⭐⭐⭐** |

---

### 🚀 **THỨ TỰ LÀM VIỆC KHUYẾN NGHỊ**

**GIAI ĐOẠN 1 - Foundation** (Tuần 1-2)

1. **TV1**: Users, BacSi, Khoa → Nền tảng authentication + entities cơ bản
2. **TV4**: NhaCungCap, Thuoc → Master data cho các module khác
3. **TV2**: Roles → Phân quyền cơ bản

**GIAI ĐOẠN 2 - Core Business** (Tuần 3-4) 4. **TV1**: LichLamViec → Lịch bác sĩ (phụ thuộc BacSi) 5. **TV2**: LichKham, PhieuKham, HoSoBenhAn → Core workflow 6. **TV3**: Permissions, GoiDichVu → Quyền hạn + gói dịch vụ

**GIAI ĐOẠN 3 - Details & Relations** (Tuần 5-6) 7. **TV3**: DonThuoc, CTDonThuoc → Đơn thuốc (phụ thuộc HoSoBenhAn + Thuoc) 8. **TV4**: PhieuNhap → Nhập kho (phụ thuộc NhaCungCap) 9. **TV5**: UserRoles, HoaDonKham → Many-to-many + Thanh toán khám 10. **TV5**: CTPhieuNhap → Chi tiết nhập (phụ thuộc PhieuNhap + Thuoc)

**GIAI ĐOẠN 4 - Advanced Features** (Tuần 7-8) 11. **TV6**: RolePermissions → Many-to-many auth (phụ thuộc Roles + Permissions) 12. **TV6**: HoaDonThuoc, CTHDThuoc → Bán thuốc (phụ thuộc DonThuoc - nullable)

---

### 🤝 **PHỐI HỢP QUAN TRỌNG**

**Dependencies chính**:

- **TV1 → TV2**: BacSi, Khoa → LichKham, HoSoBenhAn
- **TV1 ↔ TV2 ↔ TV3**: Users ↔ Roles ↔ Permissions (Tam giác phân quyền)
- **TV1 ↔ TV5**: Users + Roles → UserRoles (many-to-many)
- **TV2 ↔ TV3 ↔ TV6**: Roles + Permissions → RolePermissions (many-to-many)
- **TV2 → TV3**: HoSoBenhAn → DonThuoc
- **TV3 → TV6**: DonThuoc → HoaDonThuoc (nullable FK)
- **TV4 → All**: Thuoc → (CTDonThuoc, CTPhieuNhap, CTHDThuoc)

**Họp sync**:

- **Sprint 1**: TV1 + TV2 + TV4 (Foundation team)
- **Sprint 2**: TV2 + TV3 (Core workflow team)
- **Sprint 3**: TV3 + TV5 + TV6 (Details & Relations team)

--

### ⚠️ **LƯU Ý:**

⚠️ **TV2 workload cao nhất**: Core workflow + HoSoBenhAn trung tâm → Cần hỗ trợ  
⚠️ **Dependencies phức tạp**: Thuoc (TV4) bị nhiều module phụ thuộc → Ưu tiên sớm  
⚠️ **Many-to-many relationships**: TV5, TV6 cần hiểu rõ quan hệ nhiều-nhiều  
⚠️ **Nullable FK**: TV6 cần xử lý HoaDonThuoc.DonThuoc (có thể NULL)
