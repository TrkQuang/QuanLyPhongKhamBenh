# 📋 HƯỚNG DẪN SỬ DỤNG HỒ SƠ BỆNH ÁN MỚI

## 🎯 Mục tiêu cập nhật

- **Đơn giản hóa logic**: 1 bảng HoSoBenhAn chứa đầy đủ thông tin từ đăng ký → kết quả khám
- **Phân tách rõ ràng**: Thông tin cá nhân (lúc đăng ký) vs Kết quả khám (bác sĩ nhập)
- **Linh hoạt**: Hỗ trợ cả đặt lịch trước và khám walk-in

---

## 🗄️ Cấu trúc Database

### Bảng HoSoBenhAn

```sql
CREATE TABLE HoSoBenhAn (
    MaHoSo VARCHAR(20) PRIMARY KEY,
    MaLichKham VARCHAR(20),  -- Nullable (có thể khám không cần đặt lịch)

    -- ✅ Thông tin cá nhân (nhập khi đăng ký)
    HoTen VARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL,
    CCCD VARCHAR(20),
    NgaySinh DATE,
    GioiTinh VARCHAR(10),
    DiaChi VARCHAR(200),

    -- ✅ Thông tin khám (bác sĩ nhập sau)
    NgayKham DATETIME,
    MaBacSi VARCHAR(20),
    TrieuChung TEXT,
    ChanDoan TEXT,
    KetLuan TEXT,
    LoiDan TEXT,

    -- ✅ Trạng thái
    TrangThai ENUM('CHO_KHAM', 'DA_KHAM', 'HUY')
);
```

---

## 🔄 Luồng xử lý

### 1. Bệnh nhân đăng ký (Lễ tân/Bệnh nhân)

```java
HoSoBenhAnDTO hs = new HoSoBenhAnDTO();
hs.setMaHoSo("HS001");
hs.setHoTen("Nguyen Van A");
hs.setSoDienThoai("0901234567");
hs.setCCCD("001234567890");
hs.setNgaySinh(Date.valueOf("1990-05-15"));
hs.setGioiTinh("Nam");
hs.setDiaChi("123 Le Loi, Q1");
hs.setTrangThai("CHO_KHAM"); // Default

// ❌ KHÔNG nhập: ChanDoan, KetLuan, LoiDan (null)
hoSoDAO.insert(hs);
```

**Trong Database:**
| MaHoSo | HoTen | SoDienThoai | ChanDoan | TrangThai |
|--------|-------|-------------|----------|-----------|
| HS001 | Nguyen Van A | 0901234567 | `null` | CHO_KHAM |

---

### 2. Đặt lịch khám (Optional)

```java
// Cập nhật MaLichKham nếu đặt lịch
hs.setMaLichKham("LK001");
hoSoDAO.update(hs);
```

**Lưu ý:** Có thể bỏ qua bước này nếu khám walk-in (MaLichKham = null)

---

### 3. Bác sĩ khám bệnh và lưu kết quả

```java
// Method 1: Update toàn bộ
HoSoBenhAnDTO hs = hoSoDAO.getByMaHoSo("HS001");
hs.setTrieuChung("Dau dau, chong mat");
hs.setChanDoan("Viem da day");
hs.setKetLuan("Can dieu tri");
hs.setLoiDan("An uong dieu do");
hs.setMaBacSi("BS01");
hs.setNgayKham(new Date());
hs.setTrangThai("DA_KHAM");
hoSoDAO.update(hs);

// Method 2: Dùng method chuyên dụng (Recommended)
hoSoDAO.updateKetQuaKham(
    "HS001",
    "Dau dau, chong mat",
    "Viem da day",
    "Can dieu tri",
    "An uong dieu do",
    "BS01"
);
```

**Trong Database:**
| MaHoSo | HoTen | ChanDoan | KetLuan | TrangThai |
|--------|-------|----------|---------|-----------|
| HS001 | Nguyen Van A | Viem da day | Can dieu tri | DA_KHAM |

---

## 🔍 Tra cứu hồ sơ

### Tìm theo số điện thoại (lịch sử khám)

```java
ArrayList<HoSoBenhAnDTO> list = hoSoDAO.getBySoDienThoai("0901234567");
// → Trả về TẤT CẢ lần khám của số điện thoại này
```

### Tìm theo CCCD

```java
ArrayList<HoSoBenhAnDTO> list = hoSoDAO.getByCCCD("001234567890");
```

### Tìm theo trạng thái

```java
// Danh sách chờ khám
ArrayList<HoSoBenhAnDTO> choKham = hoSoDAO.getByTrangThai("CHO_KHAM");

// Đã khám
ArrayList<HoSoBenhAnDTO> daKham = hoSoDAO.getByTrangThai("DA_KHAM");

// Đã hủy
ArrayList<HoSoBenhAnDTO> huy = hoSoDAO.getByTrangThai("HUY");
```

### Tìm theo Mã lịch khám

```java
HoSoBenhAnDTO hs = hoSoDAO.getByMaLichKham("LK001");
// → Lấy hồ sơ của bệnh nhân đặt lịch LK001
```

---

## 🎨 Giao diện UI

### Form đăng ký (Tab 1)

```
┌─────────────────────────────────────┐
│  📋 THÔNG TIN BỆNH NHÂN             │
├─────────────────────────────────────┤
│  Họ tên:     [__________________]  │
│  SĐT:        [__________________]  │
│  CCCD:       [__________________]  │
│  Ngày sinh:  [____/____/____]      │
│  Giới tính:  ⚪ Nam  ⚪ Nữ         │
│  Địa chỉ:    [__________________]  │
│                                     │
│  ❌ ChanDoan, KetLuan, LoiDan      │
│     → DISABLED (bác sĩ mới nhập)   │
│                                     │
│       [Hủy]  [Tiếp theo →]         │
└─────────────────────────────────────┘
```

### Form khám bệnh (Bác sĩ)

```
┌─────────────────────────────────────┐
│  🩺 KHÁM BỆNH                       │
├─────────────────────────────────────┤
│  Bệnh nhân: Nguyen Van A            │
│  SĐT: 0901234567                    │
│  CCCD: 001234567890                 │
├─────────────────────────────────────┤
│  Triệu chứng:                       │
│  [_____________________________]    │
│                                     │
│  Chẩn đoán:                         │
│  [_____________________________]    │
│                                     │
│  Kết luận:                          │
│  [_____________________________]    │
│                                     │
│  Lời dặn:                           │
│  [_____________________________]    │
│                                     │
│  [Lưu kết quả]  [Kê đơn thuốc]    │
└─────────────────────────────────────┘
```

---

## 🔐 Phân quyền

| Chức năng                        | Lễ tân | Bệnh nhân     | Bác sĩ |
| -------------------------------- | ------ | ------------- | ------ |
| Đăng ký (nhập thông tin cá nhân) | ✅     | ✅            | ✅     |
| Đặt lịch khám                    | ✅     | ✅            | ❌     |
| Nhập chẩn đoán, kết luận         | ❌     | ❌            | ✅     |
| Xem lịch sử khám                 | ✅     | ✅ (của mình) | ✅     |
| Hủy lịch                         | ✅     | ✅ (của mình) | ❌     |

---

## 📊 Truy vấn SQL hữu ích

### Danh sách chờ khám hôm nay

```sql
SELECT hs.MaHoSo, hs.HoTen, hs.SoDienThoai, lk.ThoiGianBatDau
FROM HoSoBenhAn hs
JOIN LichKham lk ON hs.MaLichKham = lk.MaLichKham
WHERE hs.TrangThai = 'CHO_KHAM'
AND DATE(lk.ThoiGianBatDau) = CURDATE()
ORDER BY lk.ThoiGianBatDau;
```

### Lịch sử khám của bệnh nhân

```sql
SELECT MaHoSo, NgayKham, ChanDoan, KetLuan
FROM HoSoBenhAn
WHERE SoDienThoai = '0901234567'
AND TrangThai = 'DA_KHAM'
ORDER BY NgayKham DESC;
```

### Thống kê theo trạng thái

```sql
SELECT TrangThai, COUNT(*) as SoLuong
FROM HoSoBenhAn
GROUP BY TrangThai;
```

---

## ✅ Ưu điểm của cấu trúc mới

1. ✅ **Đơn giản**: 1 bảng duy nhất, không cần JOIN phức tạp
2. ✅ **Phân tách rõ**: Thông tin cá nhân vs Kết quả khám
3. ✅ **Linh hoạt**: Hỗ trợ cả đặt lịch và walk-in
4. ✅ **Dễ tra cứu**: WHERE SoDienThoai, CCCD, TrangThai
5. ✅ **Lịch sử đơn giản**: Tất cả lần khám của 1 người = 1 query

---

## 🚀 Cài đặt

1. **Chạy SQL script:**

   ```bash
   mysql -u root -p PhongKham < database/UPDATE_HoSoBenhAn.sql
   ```

2. **Code đã cập nhật:**
   - ✅ HoSoBenhAnDTO (+ TrangThai)
   - ✅ HoSoBenhAnDAO (+ methods mới)
   - ✅ LichKhamDTO (giữ nguyên - đơn giản)

3. **Xem ví dụ:**
   ```bash
   src/main/java/phongkham/example/HoSoBenhAnExample.java
   ```

---
