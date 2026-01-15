# Quản Lý Phòng Khám Bệnh (Java)

Dự án Java quản lý phòng khám theo mô hình phân lớp (GUI → BUS → DAO → DB), sử dụng JDBC + MySQL.

## Công nghệ

- **Java**
- **MySQL** + **JDBC** (MySQL Connector/J)
- **UI**: Swing + FlatLaf (theme)
- **Xuất file**: Apache POI (Excel), iText (PDF)

Thư viện nằm trong thư mục `lib/`:

- `mysql-connector-j-9.5.0.jar`
- `flatlaf-3.7.jar`
- `poi-5.2.3.jar`, `poi-ooxml-5.2.3.jar`
- `itextpdf-5.5.13.4.jar`

## Kiến trúc (3 lớp)

- **GUI** (`phongkham.GUI`): giao diện (Frame/Dialog/Panel), bắt sự kiện, validate cơ bản.
- **BUS** (`phongkham.BUS`): xử lý nghiệp vụ, điều phối luồng, không viết SQL.
- **DAO** (`phongkham.DAO`): truy cập dữ liệu (CRUD) qua JDBC.
- **DB** (`phongkham.DB`): quản lý kết nối DB.
- **DTO** (`phongkham.DTO`): đối tượng truyền dữ liệu giữa các tầng.

Luồng chuẩn:

- `GUI` → `BUS` → `DAO` → `DB`

## Cấu trúc thư mục

```
QuanLyPhongKhamBenh/
├── database/
│   └── qlpk_db.sql              # script tạo CSDL
├── lib/                         # các file .jar
├── src/main/java/phongkham/
│   ├── GUI/                     # giao diện
│   ├── BUS/                     # nghiệp vụ
│   ├── DAO/                     # truy cập dữ liệu
│   ├── DB/                      # kết nối DB
│   ├── DTO/                     # DTO
│   └── Main.java                # entry point
└── .vscode/ (tuỳ máy)
```

Tài liệu chi tiết theo từng tầng:

- GUI: `src/main/java/phongkham/GUI/Readme.md`
- BUS: `src/main/java/phongkham/BUS/Readme.md`
- DAO: `src/main/java/phongkham/DAO/Readme.md`
- DB: `src/main/java/phongkham/DB/Readme.md`
- DTO: `src/main/java/phongkham/DTO/Readme.md`

## Cài đặt & chạy

### 1) Tạo database

1. Cài MySQL (hoặc dùng MySQL remote nếu có).
2. Tạo schema và bảng bằng script:
   - `database/qlpk_db.sql`

### 2) Cấu hình kết nối DB

Chỉnh trong `src/main/java/phongkham/DB/DBConnection.java`:

- `URL` (jdbc url)
- `USER`
- `PASS`

> Lưu ý: không khuyến nghị hard-code mật khẩu trong code khi làm dự án thật.

### 3) Add thư viện (JAR)

Thêm các file trong `lib/` vào classpath/build path của IDE.

- IntelliJ: File → Project Structure → Modules → Dependencies → `+` JARs
- Eclipse: Build Path → Add External Archives...
- NetBeans: Libraries → Add JAR/Folder...

### 4) Chạy chương trình

Chạy class `phongkham.Main`.

> Hiện tại `Main.java` đang là khung trống; khi thêm GUI, `Main` sẽ là nơi khởi tạo và hiển thị màn hình chính.

## Gợi ý đóng góp / phát triển tiếp

- Hoàn thiện `GUI/` (tạo các màn hình, điều hướng)
- Implement các lớp `DAO` (hiện có nhiều file khung)
- Implement logic trong các lớp `BUS`
- Tách cấu hình DB ra file `.properties` hoặc biến môi trường

## Ghi chú bảo mật

Trong `DBConnection.java` hiện có thông tin kết nối DB. Nếu repo public, nên:

- Xoá credential khỏi code
- Dùng file cấu hình không commit hoặc biến môi trường
