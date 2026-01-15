# 📋 TÓM TẮT PHÂN CÔNG - HỆ THỐNG QUẢN LÝ PHÒNG KHÁM

> **6 thành viên** | **21 mô-đun** | **10 tuần** | **Ứng dụng Desktop**

---

## 👥 PHÂN CHIA NHÓM

| TV    | Mô-đun | Tên Mô-đun                                  | Chuyên Môn                 |
| ----- | ------ | ------------------------------------------- | -------------------------- |
| **1** | 4      | TaiKhoan, VaiTro, Quyen, TaiKhoanVaiTro     | 🔐 **Hệ thống Xác thực**   |
| **2** | 4      | BacSi, Khoa, LichLamViec, VaiTroQuyen       | 👨‍⚕️ **Quản lý Bác sĩ**      |
| **3** | 3      | LichKham, PhieuKham, HoSoBenhAn             | 🏥 **Quy trình Khám bệnh** |
| **4** | 4      | Thuoc, DonThuoc, ChiTietDonThuoc, GoiDichVu | 💊 **Quản lý Thuốc**       |
| **5** | 3      | NhaCungCap, PhieuNhap, ChiTietPhieuNhap     | 🏪 **Quản lý Kho**         |
| **6** | 3      | HoaDonThuoc, ChiTietHoaDonThuoc, HoaDonKham | 💰 **Thanh toán**          |

---

## ⚡ QUY TRÌNH CHÍNH

```
Khách đặt lịch → Bác sĩ khám → Kê đơn thuốc → Bán thuốc → Thanh toán
      (TV3)         (TV3)         (TV4)         (TV6)        (TV6)
```

## 🔐 NHÓM NGƯỜI DÙNG

- **Khách**: Đặt lịch, mua thuốc _(không cần đăng nhập)_
- **Bác sĩ**: Khám bệnh, kê đơn _(cần đăng nhập)_
- **Nhà thuốc**: Bán thuốc, nhập kho _(cần đăng nhập)_

---

## 📅 LỊCH TRÌNH (10 TUẦN)

| Tuần     | Nhiệm vụ     | Trọng tâm                |
| -------- | ------------ | ------------------------ |
| **1-2**  | **Tầng DTO** | Thiết kế mô hình dữ liệu |
| **3-4**  | **Tầng DAO** | Thao tác cơ sở dữ liệu   |
| **5-6**  | **Tầng BUS** | Logic nghiệp vụ          |
| **7-8**  | **Tầng GUI** | Giao diện người dùng     |
| **9-10** | **Kiểm thử** | Kiểm thử tích hợp        |

---

## 🎯 THỨ TỰ ƯU TIÊN

1. **TV1** (Xác thực) → hoàn thành sớm nhất
2. **TV3** (Quy trình cốt lõi) → HoSoBenhAn là trung tâm
3. **TV4** (Thuốc) → các mô-đun khác phụ thuộc
4. **TV5, TV6** → thực hiện sau khi có Thuốc

---

## 🔧 CÔNG NGHỆ SỬ DỤNG

- **Cơ sở dữ liệu**: MySQL/PostgreSQL
- **Giao diện**: Java Swing/JavaFX
- **Kiến trúc**: DTO → DAO → BUS → GUI
- **Mạng**: Ứng dụng Desktop trên mạng LAN

---

## ⚠️ LƯU Ý QUAN TRỌNG

- **HoaDonThuoc có thể null** → cho phép mua thuốc tự do
- **Mỗi người làm đủ 4 tầng** cho mô-đun của mìn

---
