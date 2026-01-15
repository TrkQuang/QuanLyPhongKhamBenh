# 📋 TÓM TẮT PHÂN CÔNG - HỆ THỐNG QUẢN LÝ PHÒNG KHÁM

## 🎯 TỔNG QUAN

**6 thành viên làm 21 module** - mỗi người làm đầy đủ **3 tầng**: DAO + BUS + GUI  
**Phân chia**: 3 người × 4 module (19%) + 3 người × 3 module (14%) = **Cân bằng tối ưu**

---

## 👥 PHÂN CÔNG CHI TIẾT

### 🔐👤 **QUANG HỮU** - User + Hệ thống Bác sĩ

**🎯 Tích hợp Authentication với Nghiệp vụ Bác sĩ**

- **Users** 🔐 - Tài khoản hệ thống, authentication
- **BacSi** 👨‍⚕️ - Thông tin bác sĩ, liên kết với Users
- **LichLamViec** 📅 - Lịch làm việc bác sĩ theo ca
- **Khoa** 🏥 - Khoa khám bệnh, chuyên môn

**💡 Vai trò**: Quản lý toàn bộ hệ thống bác sĩ từ authentication đến phân khoa

---

### 🏥⭐ **KỲ QUANG** - Roles + Core Workflo

**🎯 TRUNG TÂM HỆ THỐNG - Workflow khám bệnh chính**

- **Roles** 👥 - Vai trò/chức vụ trong hệ thống
- **LichKham** 📆 - Lịch hẹn khám từ khách hàng
- **PhieuKham** 📋 - Phiếu khám bệnh, kết quả khám
- **HoSoBenhAn** ⭐ - **CORE** - Hồ sơ bệnh án (trung tâm hệ thống)

**💡 Vai trò**: Luồng khám bệnh từ đặt lịch → khám → hồ sơ bệnh án

---

### 💊 **BẢO TRÍ** - Permissions + Đơn thuốc

**🎯 Quyền hạn + Quản lý đơn thuốc hoàn chỉnh**

- **Permissions** 🔑 - Quyền hạn trong hệ thống
- **GoiDichVu** 📦 - Gói dịch vụ khám, combo
- **DonThuoc** 💊 - Đơn thuốc bác sĩ kê
- **CTDonThuoc** 📝 - Chi tiết từng thuốc trong đơn

**💡 Vai trò**: Quản lý quyền hạn và luồng đơn thuốc từ kê đơn đến chi tiết

---

### 🏪 **NHƯ QUỲNH** - Quản lý Thuốc + Nhập kho

**🎯 Chuyên sâu nghiệp vụ kho thuốc**

- **NhaCungCap** 🏭 - Nhà cung cấp thuốc
- **Thuoc** 💊 - Danh mục thuốc, master data
- **PhieuNhap** 📥 - Phiếu nhập thuốc từ NCC

**💡 Vai trò**: Master data thuốc cho toàn hệ thống, quản lý nhập kho

---

### 💰 **THÀNH NHÂN** - UserRoles + Thanh toán

**🎯 Many-to-many + Thanh toán + Chi tiết nhập**

- **UserRoles** 🔗 - Bảng quan hệ user-role (many-to-many)
- **HoaDonKham** 💵 - Hóa đơn thanh toán khám bệnh
- **CTPhieuNhap** 📋 - Chi tiết thuốc trong phiếu nhập

**💡 Vai trò**: Tech Lead - Xử lý quan hệ phức tạp + thanh toán

---

### 🎨 **MINH TRIẾT** - RolePermissions + Bán thuốc

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
[KQ] LichKham → Đặt lịch hẹn khám
      ↓
[KQ] PhieuKham → Khám bệnh, chẩn đoán
      ↓
[KQ] HoSoBenhAn ⭐ → Lưu hồ sơ bệnh án (CORE)
      ↓
[BT] DonThuoc → Bác sĩ kê đơn
      ↓
[BT] CTDonThuoc → Chi tiết từng thuốc
      ↓
[MT] HoaDonThuoc → Thanh toán mua thuốc
      ↓
[MT] CTHDThuoc → Chi tiết hóa đơn

Luồng phụ:
[TN] HoaDonKham → Thanh toán tiền khám
```

---

## 📊 THỐNG KÊ WORKLOAD

| Thành viên     | Module | % Công việc | Chuyên môn                        | Module Auth        | Độ khó       |
| -------------- | ------ | ----------- | --------------------------------- | ------------------ | ------------ |
| **Quang Hữu**  | 4      | 19.05%      | 🔐 User + Hệ thống Bác sĩ         | Users              | ⭐⭐⭐⭐     |
| **Kỳ Quang**   | 4      | 19.05%      | 🏥⭐ Roles + Core Workflow        | Roles              | ⭐⭐⭐⭐⭐   |
| **Bảo Trí**    | 4      | 19.05%      | 💊 Permissions + Đơn thuốc        | Permissions        | ⭐⭐⭐⭐     |
| **Như Quỳnh**  | 3      | 14.29%      | 🏪 Thuốc + Nhập kho               | -                  | ⭐⭐⭐       |
| **Thành Nhân** | 3      | 14.29%      | 💰 UserRoles + Thanh toán         | UserRoles          | ⭐⭐⭐       |
| **Minh Triết** | 3      | 14.29%      | 🎨 RolePermissions + Bán thuốc    | RolePermissions    | ⭐⭐⭐       |
| **TỔNG**       | **21** | **100%**    | **5/6 người có Auth + Nghiệp vụ** | **5 Auth modules** | **⭐⭐⭐⭐** |

---

### 🚀 **THỨ TỰ LÀM VIỆC KHUYẾN NGHỊ**

**GIAI ĐOẠN 1 - Foundation** (Tuần 1-2)

1. **Quang Hữu**: Users, BacSi, Khoa → Nền tảng authentication + entities cơ bản
2. **Như Quỳnh**: NhaCungCap, Thuoc → Master data cho các module khác
3. **Kỳ Quang**: Roles → Phân quyền cơ bản

**GIAI ĐOẠN 2 - Core Business** (Tuần 3-4) 4. **Quang Hữu**: LichLamViec → Lịch bác sĩ (phụ thuộc BacSi) 5. **Kỳ Quang**: LichKham, PhieuKham, HoSoBenhAn → Core workflow 6. **Bảo Trí**: Permissions, GoiDichVu → Quyền hạn + gói dịch vụ

**GIAI ĐOẠN 3 - Details & Relations** (Tuần 5-6) 7. **Bảo Trí**: DonThuoc, CTDonThuoc → Đơn thuốc (phụ thuộc HoSoBenhAn + Thuoc) 8. **Như Quỳnh**: PhieuNhap → Nhập kho (phụ thuộc NhaCungCap) 9. **Thành Nhân**: UserRoles, HoaDonKham → Many-to-many + Thanh toán khám 10. **Thành Nhân**: CTPhieuNhap → Chi tiết nhập (phụ thuộc PhieuNhap + Thuoc)

**GIAI ĐOẠN 4 - Advanced Features** (Tuần 7-8) 11. **Minh Triết**: RolePermissions → Many-to-many auth (phụ thuộc Roles + Permissions) 12. **Minh Triết**: HoaDonThuoc, CTHDThuoc → Bán thuốc (phụ thuộc DonThuoc - nullable)

---

### 🤝 **PHỐI HỢP QUAN TRỌNG**

**Dependencies chính**:

- **Quang Hữu → Kỳ Quang**: BacSi, Khoa → LichKham, HoSoBenhAn
- **Quang Hữu ↔ Kỳ Quang ↔ Bảo Trí**: Users ↔ Roles ↔ Permissions (Tam giác phân quyền)
- **Quang Hữu ↔ Thành Nhân**: Users + Roles → UserRoles (many-to-many)
- **Kỳ Quang ↔ Bảo Trí ↔ Minh Triết**: Roles + Permissions → RolePermissions (many-to-many)
- **Kỳ Quang → Bảo Trí**: HoSoBenhAn → DonThuoc
- **Bảo Trí → Minh Triết**: DonThuoc → HoaDonThuoc (nullable FK)
- **Như Quỳnh → All**: Thuoc → (CTDonThuoc, CTPhieuNhap, CTHDThuoc)

**Họp sync**:

- **Sprint 1**: Quang Hữu + Kỳ Quang + Như Quỳnh (Foundation team)
- **Sprint 2**: Kỳ Quang + Bảo Trí (Core workflow team)
- **Sprint 3**: Bảo Trí + Thành Nhân + Minh Triết (Details & Relations team)

--

### ⚠️ **LƯU Ý:**

⚠️ **Kỳ Quang workload cao nhất**: Core workflow + HoSoBenhAn trung tâm → Cần hỗ trợ  
⚠️ **Dependencies phức tạp**: Thuoc (Như Quỳnh) bị nhiều module phụ thuộc → Ưu tiên sớm  
⚠️ **Many-to-many relationships**: Thành Nhân, Minh Triết cần hiểu rõ quan hệ nhiều-nhiều  
⚠️ **Nullable FK**: Minh Triết cần xử lý HoaDonThuoc.DonThuoc (có thể NULL)
