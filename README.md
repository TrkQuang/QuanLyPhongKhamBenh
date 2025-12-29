# ĐỒ ÁN JAVA: QUẢN LÝ PHÒNG KHÁM BỆNH

## 1. Thông tin dự án
- **Tên đồ án:** Quản Lý Phòng Khám Bệnh  
- **Ngôn ngữ lập trình:** Java  
- **Thư viện:** Swing, JDBC, MySQL Connector/J  
- **Cơ sở dữ liệu:** MySQL  
- **Mô hình kiến trúc:** 3 lớp (MVC đơn giản)

---

## 2. Mục tiêu
Xây dựng một hệ thống quản lý phòng khám bệnh cơ bản, cho phép:
- Quản lý bệnh nhân (thêm, sửa, xóa, tìm kiếm)
- Quản lý bác sĩ
- Quản lý lịch khám
- Quản lý hóa đơn  
- Đảm bảo phân tách rõ ràng **giao diện – nghiệp vụ – dữ liệu** theo mô hình 3 lớp

## 3. Cấu trúc thư mục
QuanLyPhongKham/
│
├── lib/ # Thư viện ngoài
│ └── mysql-connector-j.jar
│
├── src/main/java/phongkham/
│ ├── db/ # Kết nối database
│ │ └── DBConnection.java
│ │
│ ├── model/ # Lớp dữ liệu
│ │ ├── BenhNhan.java
│ │ ├── BacSi.java
│ │ ├── PhongKham.java
│ │ ├── LichKham.java
│ │ └── HoaDon.java
│ │
│ ├── dao/ # Truy cập dữ liệu
│ │ ├── BenhNhanDAO.java
│ │ ├── BacSiDAO.java
│ │ ├── LichKhamDAO.java
│ │ └── HoaDonDAO.java
│ │
│ ├── service/ # Xử lý nghiệp vụ
│ │ ├── BenhNhanService.java
│ │ ├── BacSiService.java
│ │ ├── LichKhamService.java
│ │ └── HoaDonService.java
│ │
│ ├── controller/ # Điều khiển
│ │ ├── BenhNhanController.java
│ │ ├── BacSiController.java
│ │ ├── LichKhamController.java
│ │ └── HoaDonController.java
│ │
│ ├── view/ # Giao diện Swing
│ │ ├── FrmDangNhap.java
│ │ ├── FrmMain.java
│ │ ├── FrmBenhNhan.java
│ │ ├── FrmBacSi.java
│ │ ├── FrmLichKham.java
│ │ └── FrmHoaDon.java
│ │
│ └── Main.java # Lớp khởi chạy chương trình
│
├── database/ # File SQL tạo database
│ └── phongkham.sql
│
└── README.md

---

## 4. Mô hình 3 lớp
1. **Presentation Layer (View + Controller)**
   - Hiển thị giao diện Swing
   - Nhận thao tác từ người dùng
   - Gửi yêu cầu xuống lớp nghiệp vụ

2. **Business Logic Layer (Service)**
   - Xử lý nghiệp vụ, kiểm tra dữ liệu
   - Không trực tiếp thao tác database
   - Là trung gian giữa Controller và DAO

3. **Data Access Layer (DAO + DB + Model)**
   - Kết nối database MySQL
   - Thực hiện SQL (SELECT, INSERT, UPDATE, DELETE)
   - Map dữ liệu từ database về đối tượng Java
---

## 5. Luồng hoạt động hệ thống
Người dùng
↓
UI (Swing) + Controller
↓
Service (xử lý nghiệp vụ)
↓
DAO + Database
---

## 6. Hướng dẫn cài đặt & chạy
1. **Chuẩn bị cơ sở dữ liệu**
   - Tạo database bằng file `database/phongkham.sql`
   - Cập nhật thông tin user/password trong `DBConnection.java`

2. **Chạy chương trình**
   - Mở project trong IDE (Eclipse, IntelliJ, NetBeans)
   - Thêm thư viện `mysql-connector-j.jar` vào Build Path
   - Chạy `Main.java`

3. **Giao diện**
   - Form đăng nhập
   - Form chính: quản lý Bệnh nhân, Bác sĩ, Lịch khám, Hóa đơn
   - Chức năng CRUD đầy đủ

---

## 7. Ghi chú
- Công nghệ dùng: Java Swing, Java JDBC, MySQL/SQl Server, Java GUI, Figma, ..
- Công nghệ teamwork: Github, Messengers
- Tuân thủ **MVC / 3 lớp**  
- Tách rõ **View – Service – DAO**  
- Dễ mở rộng: thêm module mới (thuốc, phòng khám…)  

---