SET NAMES utf8mb4;
USE PhongKham;

SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 1) XÓA TOÀN BỘ DỮ LIỆU
-- =========================
DELETE FROM XuatThuocTheoLo;
DELETE FROM TieuHuyLoThuoc;
DELETE FROM CTHDThuoc;
DELETE FROM HoaDonThuoc;
DELETE FROM HoaDonKham;

DELETE FROM CTDonThuoc;
DELETE FROM DonThuoc;
DELETE FROM HoSoBenhAn;
DELETE FROM LichKham;
DELETE FROM LichLamViec;

DELETE FROM ChiTietPhieuNhap;
DELETE FROM PhieuNhap;

DELETE FROM BacSi;
DELETE FROM GoiDichVu;
DELETE FROM Thuoc;
DELETE FROM Khoa;
DELETE FROM NhaCungCap;

DELETE FROM RolePermissions;
DELETE FROM Permissions;
DELETE FROM Users;
DELETE FROM Roles;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- 2) RBAC + USERS
-- =========================
INSERT INTO Roles (STT, TenVaiTro, MoTa) VALUES
(1, 'ADMIN', 'Quản trị hệ thống'),
(2, 'BACSI', 'Bác sĩ khám chữa bệnh'),
(3, 'NHATHUOC', 'Nhân viên nhà thuốc'),
(4, 'GUEST', 'Khách sử dụng dịch vụ');

INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('DASHBOARD_VIEW', 'Xem bảng điều khiển', 1),
('USER_VIEW', 'Xem danh sách tài khoản', 1),
('USER_MANAGE', 'Quản lý tài khoản người dùng', 1),
('BACSI_VIEW', 'Xem danh sách bác sĩ', 1),
('BACSI_MANAGE', 'Quản lý thông tin bác sĩ', 1),
('KHOA_VIEW', 'Xem khoa khám bệnh', 1),
('KHOA_MANAGE', 'Quản lý khoa khám bệnh', 1),
('GOIDICHVU_VIEW', 'Xem gói dịch vụ', 1),
('GOIDICHVU_MANAGE', 'Quản lý gói dịch vụ', 1),
('ROLE_PERMISSION_VIEW', 'Xem phân quyền', 1),
('ROLE_PERMISSION_MANAGE', 'Quản lý phân quyền', 1),
('LICHLAMVIEC_VIEW', 'Xem lịch làm việc', 1),
('LICHLAMVIEC_MANAGE', 'Quản lý lịch làm việc', 1),
('LICHKHAM_VIEW', 'Xem lịch khám', 1),
('LICHKHAM_MANAGE', 'Quản lý lịch khám', 1),
('HOADONKHAM_VIEW', 'Xem hóa đơn khám', 1),
('HOADONKHAM_MANAGE', 'Quản lý hóa đơn khám', 1),
('HOSO_VIEW', 'Xem hồ sơ bệnh án', 1),
('HOSO_MANAGE', 'Quản lý hồ sơ bệnh án', 1),
('BACSI_PROFILE_VIEW', 'Xem hồ sơ bác sĩ', 1),
('BACSI_PROFILE_UPDATE_PASSWORD', 'Đổi mật khẩu bác sĩ', 1),
('THUOC_VIEW', 'Xem thuốc', 1),
('THUOC_MANAGE', 'Quản lý thuốc', 1),
('NCC_VIEW', 'Xem nhà cung cấp', 1),
('NCC_MANAGE', 'Quản lý nhà cung cấp', 1),
('PHIEUNHAP_VIEW', 'Xem phiếu nhập', 1),
('PHIEUNHAP_MANAGE', 'Quản lý phiếu nhập', 1),
('HOADONTHUOC_VIEW', 'Xem hóa đơn thuốc', 1),
('HOADONTHUOC_CREATE', 'Tạo hóa đơn thuốc', 1),
('HOADONTHUOC_MANAGE', 'Quản lý hóa đơn thuốc', 1),
('DATLICH_ACCESS', 'Truy cập đặt lịch khám', 1),
('MUATHUOC_ACCESS', 'Truy cập mua thuốc', 1),
('GUEST_LOOKUP_CCCD', 'Tra cứu hồ sơ theo CCCD', 1);

-- Admin toàn quyền
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, MaPermission, 1 FROM Permissions;

-- Bác sĩ: lịch làm việc, lịch khám, hóa đơn khám, hồ sơ
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 2, MaPermission, 1 FROM Permissions
WHERE TenPermission IN (
	'LICHLAMVIEC_VIEW', 'LICHLAMVIEC_MANAGE',
	'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
	'HOADONKHAM_VIEW', 'HOADONKHAM_MANAGE',
	'HOSO_VIEW', 'HOSO_MANAGE',
	'BACSI_PROFILE_VIEW', 'BACSI_PROFILE_UPDATE_PASSWORD'
);

-- Nhà thuốc: thuốc, NCC, phiếu nhập, hóa đơn thuốc
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 3, MaPermission, 1 FROM Permissions
WHERE TenPermission IN (
	'THUOC_VIEW', 'THUOC_MANAGE',
	'NCC_VIEW', 'NCC_MANAGE',
	'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE',
	'HOADONTHUOC_VIEW', 'HOADONTHUOC_CREATE', 'HOADONTHUOC_MANAGE'
);

-- Guest: chỉ đặt lịch và mua thuốc
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 4, MaPermission, 1 FROM Permissions
WHERE TenPermission IN ('DATLICH_ACCESS', 'MUATHUOC_ACCESS', 'GUEST_LOOKUP_CCCD');

INSERT INTO Users (UserID, Username, Password, Email, RoleID, Active, CreatedAt) VALUES
('U001', 'admin', '123456', 'admin@phongkham.vn', 1, 1, '2026-01-01 08:00:00'),
('U002', 'bacsi.timmanh', '123456', 'tim.manh@phongkham.vn', 2, 1, '2026-01-02 08:00:00'),
('U003', 'bacsi.thuylinh', '123456', 'thuy.linh@phongkham.vn', 2, 1, '2026-01-02 08:05:00'),
('U004', 'bacsi.hoangminh', '123456', 'hoang.minh@phongkham.vn', 2, 1, '2026-01-02 08:10:00'),
('U005', 'bacsi.phuonganh', '123456', 'phuong.anh@phongkham.vn', 2, 1, '2026-01-02 08:15:00'),
('U006', 'bacsi.ngocson', '123456', 'ngoc.son@phongkham.vn', 2, 1, '2026-01-02 08:20:00'),
('U007', 'bacsi.minhchau', '123456', 'minh.chau@phongkham.vn', 2, 1, '2026-01-02 08:25:00'),
('U008', 'bacsi.thanhha', '123456', 'thanh.ha@phongkham.vn', 2, 1, '2026-01-02 08:30:00'),
('U009', 'bacsi.dinhan', '123456', 'dinh.an@phongkham.vn', 2, 1, '2026-01-02 08:35:00'),
('U010', 'bacsi.yennhi', '123456', 'yen.nhi@phongkham.vn', 2, 1, '2026-01-02 08:40:00'),
('U011', 'bacsi.quanghuy', '123456', 'quang.huy@phongkham.vn', 2, 1, '2026-01-02 08:45:00'),
('U012', 'nhathuoc.anhthu', '123456', 'anh.thu@phongkham.vn', 3, 1, '2026-01-03 08:00:00'),
('U013', 'nhathuoc.khanhvy', '123456', 'khanh.vy@phongkham.vn', 3, 1, '2026-01-03 08:05:00'),
('U014', 'nhathuoc.hongnhung', '123456', 'hong.nhung@phongkham.vn', 3, 1, '2026-01-03 08:10:00'),
('U015', 'nhathuoc.vantrang', '123456', 'van.trang@phongkham.vn', 3, 1, '2026-01-03 08:15:00'),
('U016', 'nhathuoc.tuananh', '123456', 'tuan.anh@phongkham.vn', 3, 1, '2026-01-03 08:20:00'),
('U017', 'nhathuoc.nhatlinh', '123456', 'nhat.linh@phongkham.vn', 3, 1, '2026-01-03 08:25:00'),
('U018', 'nhathuoc.baotram', '123456', 'bao.tram@phongkham.vn', 3, 1, '2026-01-03 08:30:00'),
('U019', 'guest.huongmai', '123456', 'huong.mai@gmail.com', 4, 1, '2026-01-04 08:00:00'),
('U020', 'guest.binhminh', '123456', 'binh.minh@gmail.com', 4, 1, '2026-01-04 08:05:00'),
('U021', 'guest.thaovy', '123456', 'thao.vy@gmail.com', 4, 1, '2026-01-04 08:10:00'),
('U022', 'guest.ngoclan', '123456', 'ngoc.lan@gmail.com', 4, 1, '2026-01-04 08:15:00'),
('U023', 'guest.ducphat', '123456', 'duc.phat@gmail.com', 4, 0, '2026-01-04 08:20:00'),
('U024', 'guest.kimchi', '123456', 'kim.chi@gmail.com', 4, 1, '2026-01-04 08:25:00');

-- =========================
-- 3) MASTER DATA
-- =========================
INSERT INTO Khoa (MaKhoa, TenKhoa) VALUES
('K001', 'Nội tổng quát'),
('K002', 'Tim mạch'),
('K003', 'Thần kinh'),
('K004', 'Tiêu hóa'),
('K005', 'Hô hấp'),
('K006', 'Nội tiết'),
('K007', 'Cơ xương khớp'),
('K008', 'Da liễu'),
('K009', 'Tai mũi họng'),
('K010', 'Mắt'),
('K011', 'Răng hàm mặt'),
('K012', 'Nhi khoa'),
('K013', 'Sản phụ khoa'),
('K014', 'Ngoại tổng quát'),
('K015', 'Tiết niệu'),
('K016', 'Ung bướu'),
('K017', 'Dinh dưỡng'),
('K018', 'Vật lý trị liệu'),
('K019', 'Y học gia đình'),
('K020', 'Khám sức khỏe tổng quát');

INSERT INTO BacSi (MaBacSi, HoTen, ChuyenKhoa, SoDienThoai, Email, MaKhoa) VALUES
('BS001', 'Trần Hữu Mạnh', 'Nội tổng quát', '0903123456', 'tim.manh@phongkham.vn', 'K001'),
('BS002', 'Lê Thùy Linh', 'Tim mạch', '0903223456', 'thuy.linh@phongkham.vn', 'K002'),
('BS003', 'Nguyễn Hoàng Minh', 'Thần kinh', '0903323456', 'hoang.minh@phongkham.vn', 'K003'),
('BS004', 'Phạm Phương Anh', 'Tiêu hóa', '0903423456', 'phuong.anh@phongkham.vn', 'K004'),
('BS005', 'Đỗ Ngọc Sơn', 'Hô hấp', '0903523456', 'ngoc.son@phongkham.vn', 'K005'),
('BS006', 'Võ Minh Châu', 'Nội tiết', '0903623456', 'minh.chau@phongkham.vn', 'K006'),
('BS007', 'Bùi Thanh Hà', 'Cơ xương khớp', '0903723456', 'thanh.ha@phongkham.vn', 'K007'),
('BS008', 'Huỳnh Đình An', 'Da liễu', '0903823456', 'dinh.an@phongkham.vn', 'K008'),
('BS009', 'Đặng Yến Nhi', 'Tai mũi họng', '0903923456', 'yen.nhi@phongkham.vn', 'K009'),
('BS010', 'Phan Quang Huy', 'Mắt', '0903023456', 'quang.huy@phongkham.vn', 'K010'),
('BS011', 'Ngô Gia Bảo', 'Răng hàm mặt', '0911123456', 'gia.bao@phongkham.vn', 'K011'),
('BS012', 'Trịnh Minh Khang', 'Nhi khoa', '0911223456', 'minh.khang@phongkham.vn', 'K012'),
('BS013', 'Lý Ngọc Diệp', 'Sản phụ khoa', '0911323456', 'ngoc.diep@phongkham.vn', 'K013'),
('BS014', 'Mai Quốc Cường', 'Ngoại tổng quát', '0911423456', 'quoc.cuong@phongkham.vn', 'K014'),
('BS015', 'Đinh Hải Nam', 'Tiết niệu', '0911523456', 'hai.nam@phongkham.vn', 'K015'),
('BS016', 'Tạ Bích Ngân', 'Ung bướu', '0911623456', 'bich.ngan@phongkham.vn', 'K016'),
('BS017', 'Cao Minh Trí', 'Dinh dưỡng', '0911723456', 'minh.tri@phongkham.vn', 'K017'),
('BS018', 'Vũ Khánh Vân', 'Vật lý trị liệu', '0911823456', 'khanh.van@phongkham.vn', 'K018'),
('BS019', 'Tôn Hồng Phúc', 'Y học gia đình', '0911923456', 'hong.phuc@phongkham.vn', 'K019'),
('BS020', 'La Thu Hằng', 'Khám sức khỏe tổng quát', '0911023456', 'thu.hang@phongkham.vn', 'K020'),
('BS021', 'Phùng Tuấn Kiệt', 'Nội tổng quát', '0922123456', 'tuan.kiet@phongkham.vn', 'K001'),
('BS022', 'Kiều Mỹ Hạnh', 'Tim mạch', '0922223456', 'my.hanh@phongkham.vn', 'K002');

INSERT INTO GoiDichVu (MaGoi, TenGoi, GiaDichVu, ThoiGianKham, MoTa, MaKhoa) VALUES
('G001', 'Khám nội tổng quát cơ bản', 180000, 30, 'Đánh giá tổng quát sức khỏe người lớn', 'K001'),
('G002', 'Khám chuyên sâu tim mạch', 350000, 40, 'Điện tim, tư vấn nguy cơ tim mạch', 'K002'),
('G003', 'Khám thần kinh chuyên khoa', 320000, 40, 'Đánh giá đau đầu, chóng mặt, tê yếu', 'K003'),
('G004', 'Khám tiêu hóa tổng quát', 280000, 35, 'Đau bụng, trào ngược, rối loạn tiêu hóa', 'K004'),
('G005', 'Khám hô hấp', 260000, 30, 'Ho kéo dài, viêm phế quản, hen', 'K005'),
('G006', 'Khám nội tiết', 300000, 35, 'Đái tháo đường, tuyến giáp, rối loạn nội tiết', 'K006'),
('G007', 'Khám cơ xương khớp', 280000, 35, 'Đau lưng, đau khớp, thoái hóa', 'K007'),
('G008', 'Khám da liễu', 250000, 30, 'Mụn, viêm da, dị ứng da', 'K008'),
('G009', 'Khám tai mũi họng', 230000, 30, 'Viêm mũi, viêm họng, ù tai', 'K009'),
('G010', 'Khám mắt tổng quát', 220000, 30, 'Tật khúc xạ, khô mắt, viêm kết mạc', 'K010'),
('G011', 'Khám răng hàm mặt', 240000, 30, 'Đau răng, sâu răng, viêm nướu', 'K011'),
('G012', 'Khám nhi khoa', 210000, 30, 'Khám bệnh trẻ em theo triệu chứng', 'K012'),
('G013', 'Khám sản phụ khoa', 330000, 40, 'Khám phụ khoa định kỳ và tư vấn', 'K013'),
('G014', 'Khám ngoại tổng quát', 300000, 35, 'Thoát vị, khối u phần mềm, hậu phẫu', 'K014'),
('G015', 'Khám tiết niệu', 290000, 35, 'Tiểu buốt, sỏi, viêm đường tiết niệu', 'K015'),
('G016', 'Khám ung bướu tầm soát', 450000, 45, 'Tư vấn tầm soát ung thư sớm', 'K016'),
('G017', 'Tư vấn dinh dưỡng lâm sàng', 260000, 30, 'Chế độ ăn cho từng bệnh lý', 'K017'),
('G018', 'Vật lý trị liệu phục hồi', 280000, 45, 'Phục hồi vận động sau chấn thương', 'K018'),
('G019', 'Khám y học gia đình', 200000, 30, 'Theo dõi bệnh mạn tính dài hạn', 'K019'),
('G020', 'Gói khám sức khỏe tổng quát nâng cao', 690000, 60, 'Khám sức khỏe định kỳ toàn diện', 'K020');

INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonVi, DonGiaBan, SoLuongTon, Active) VALUES
('T001', 'Paracetamol 500mg', 'Paracetamol', 'Viên', 'Hộp', 3500, 420, 1),
('T002', 'Amoxicillin 500mg', 'Amoxicillin', 'Viên', 'Hộp', 5200, 310, 1),
('T003', 'Cefuroxim 500mg', 'Cefuroxim', 'Viên', 'Hộp', 9800, 250, 1),
('T004', 'Omeprazol 20mg', 'Omeprazol', 'Viên', 'Hộp', 4300, 380, 1),
('T005', 'Esomeprazol 40mg', 'Esomeprazol', 'Viên', 'Hộp', 11200, 220, 1),
('T006', 'Loratadin 10mg', 'Loratadin', 'Viên', 'Hộp', 3900, 270, 1),
('T007', 'Cetirizin 10mg', 'Cetirizin', 'Viên', 'Hộp', 4100, 290, 1),
('T008', 'Vitamin C 500mg', 'Acid ascorbic', 'Viên', 'Hộp', 2600, 500, 1),
('T009', 'Calci D3', 'Calci carbonat + Vitamin D3', 'Viên', 'Hộp', 5800, 330, 1),
('T010', 'Bổ gan Silymarin', 'Silymarin', 'Viên', 'Hộp', 7200, 240, 1),
('T011', 'Amlodipin 5mg', 'Amlodipin', 'Viên', 'Hộp', 6400, 210, 1),
('T012', 'Losartan 50mg', 'Losartan', 'Viên', 'Hộp', 6900, 190, 1),
('T013', 'Metformin 500mg', 'Metformin', 'Viên', 'Hộp', 4700, 260, 1),
('T014', 'Gliclazid 30mg MR', 'Gliclazid', 'Viên', 'Hộp', 7500, 160, 1),
('T015', 'Atorvastatin 20mg', 'Atorvastatin', 'Viên', 'Hộp', 8300, 180, 1),
('T016', 'Bisoprolol 5mg', 'Bisoprolol', 'Viên', 'Hộp', 7900, 175, 1),
('T017', 'Salbutamol xịt định liều', 'Salbutamol', 'Lọ', 'Lọ', 98000, 85, 1),
('T018', 'Budesonid xịt mũi', 'Budesonid', 'Lọ', 'Lọ', 76000, 95, 1),
('T019', 'Natri clorid 0.9%', 'Natri clorid', 'Chai', 'Chai', 12000, 300, 1),
('T020', 'Nước súc miệng sát khuẩn', 'Chlorhexidin', 'Chai', 'Chai', 45000, 140, 1),
('T021', 'Men vi sinh đường ruột', 'Bacillus clausii', 'Ống', 'Hộp', 13800, 205, 1),
('T022', 'Kẽm gluconat', 'Kẽm gluconat', 'Viên', 'Hộp', 5300, 280, 1),
('T023', 'Magie B6', 'Magie lactat + Pyridoxin', 'Viên', 'Hộp', 6100, 155, 1),
('T024', 'Diclofenac gel bôi', 'Diclofenac', 'Tuýp', 'Tuýp', 48000, 120, 1),
('T025', 'Methylprednisolon 16mg', 'Methylprednisolon', 'Viên', 'Hộp', 8900, 90, 1);

INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SDT, Active) VALUES
('NCC001', 'Công ty Dược An Khang', '12 Trần Bình Trọng, Quận 5, TP.HCM', '02838381234', 1),
('NCC002', 'Dược phẩm Minh Tâm', '98 Nguyễn Văn Cừ, Quận 1, TP.HCM', '02839222345', 1),
('NCC003', 'Phân phối Y Tế Hưng Thịnh', '45 Lê Hồng Phong, Quận 10, TP.HCM', '02838663456', 1),
('NCC004', 'Dược Nam Việt', '220 Hoàng Hoa Thám, Bình Thạnh, TP.HCM', '02835114567', 1),
('NCC005', 'Thiết bị và Dược phẩm Ánh Dương', '77 Võ Văn Tần, Quận 3, TP.HCM', '02839385678', 1),
('NCC006', 'Dược phẩm An Bình', '14 Pasteur, Quận 3, TP.HCM', '02838226789', 1),
('NCC007', 'Dược Phúc Lộc', '120 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM', '02838457890', 1),
('NCC008', 'Công ty MedCare Việt', '64 Lý Chính Thắng, Quận 3, TP.HCM', '02839328901', 1),
('NCC009', 'Dược phẩm Tâm Đức', '39 Âu Cơ, Quận Tân Bình, TP.HCM', '02838119012', 1),
('NCC010', 'Pharma One Việt Nam', '188 Cách Mạng Tháng 8, Quận 10, TP.HCM', '02838640123', 1),
('NCC011', 'Dược Gia Khang', '28 Nguyễn Thị Minh Khai, Quận 1, TP.HCM', '02838251234', 1),
('NCC012', 'Y Dược Tín Phát', '310 Nguyễn Oanh, Quận Gò Vấp, TP.HCM', '02838962345', 1),
('NCC013', 'Dược phẩm Hoàng Quân', '90 Lạc Long Quân, Quận 11, TP.HCM', '02839673456', 1),
('NCC014', 'Công ty Dược Long Châu', '16 Quang Trung, Quận Gò Vấp, TP.HCM', '02839884567', 1),
('NCC015', 'Phân phối Dược Thành Công', '130 Trường Chinh, Quận Tân Bình, TP.HCM', '02839095678', 1),
('NCC016', 'Dược phẩm Kim Ngân', '52 Hùng Vương, Quận 5, TP.HCM', '02839206789', 1),
('NCC017', 'Dược Sài Gòn Xanh', '104 Nguyễn Trãi, Quận 1, TP.HCM', '02839417890', 1),
('NCC018', 'Thiên Phúc Pharma', '71 Phạm Văn Đồng, TP. Thủ Đức, TP.HCM', '02839628901', 1),
('NCC019', 'An Tín Medical', '56 Nguyễn Duy Trinh, TP. Thủ Đức, TP.HCM', '02839839012', 1),
('NCC020', 'Dược phẩm Việt Tín', '208 Lê Văn Việt, TP. Thủ Đức, TP.HCM', '02839940123', 1);

-- =========================
-- 4) SCHEDULE + MEDICAL
-- =========================
INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam, TrangThai) VALUES
('LLV001', 'BS001', '2026-03-24', 'Sang', 'DA_DUYET'),
('LLV002', 'BS002', '2026-03-24', 'Chieu', 'DA_DUYET'),
('LLV003', 'BS003', '2026-03-24', 'Sang', 'DA_DUYET'),
('LLV004', 'BS004', '2026-03-24', 'Chieu', 'DA_DUYET'),
('LLV005', 'BS005', '2026-03-25', 'Sang', 'DA_DUYET'),
('LLV006', 'BS006', '2026-03-25', 'Chieu', 'DA_DUYET'),
('LLV007', 'BS007', '2026-03-25', 'Sang', 'DA_DUYET'),
('LLV008', 'BS008', '2026-03-25', 'Chieu', 'DA_DUYET'),
('LLV009', 'BS009', '2026-03-26', 'Sang', 'DA_DUYET'),
('LLV010', 'BS010', '2026-03-26', 'Chieu', 'DA_DUYET'),
('LLV011', 'BS011', '2026-03-26', 'Sang', 'DA_DUYET'),
('LLV012', 'BS012', '2026-03-26', 'Chieu', 'DA_DUYET'),
('LLV013', 'BS013', '2026-03-27', 'Sang', 'DA_DUYET'),
('LLV014', 'BS014', '2026-03-27', 'Chieu', 'DA_DUYET'),
('LLV015', 'BS015', '2026-03-27', 'Sang', 'DA_DUYET'),
('LLV016', 'BS016', '2026-03-27', 'Chieu', 'DA_DUYET'),
('LLV017', 'BS017', '2026-03-28', 'Sang', 'DA_DUYET'),
('LLV018', 'BS018', '2026-03-28', 'Chieu', 'DA_DUYET'),
('LLV019', 'BS019', '2026-03-28', 'Sang', 'DA_DUYET'),
('LLV020', 'BS020', '2026-03-28', 'Chieu', 'DA_DUYET'),
('LLV021', 'BS021', '2026-03-29', 'Sang', 'DA_DUYET'),
('LLV022', 'BS022', '2026-03-29', 'Chieu', 'DA_DUYET'),
('LLV023', 'BS001', '2026-03-30', 'Toi', 'CHO_DUYET'),
('LLV024', 'BS002', '2026-03-30', 'Toi', 'TU_CHOI'),
('LLV025', 'BS003', '2026-03-31', 'Sang', 'DA_DUYET');

INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, ThoiGianKetThuc, TrangThai, MaDinhDanhTam) VALUES
('LK001', 'G001', 'BS001', '2026-03-24 08:00:00', '2026-03-24 08:30:00', 'DA_XAC_NHAN', 'GD-001'),
('LK002', 'G002', 'BS002', '2026-03-24 14:00:00', '2026-03-24 14:30:00', 'CHO_XAC_NHAN', 'GD-002'),
('LK003', 'G003', 'BS003', '2026-03-24 09:00:00', '2026-03-24 09:30:00', 'DANG_KHAM', 'GD-003'),
('LK004', 'G004', 'BS004', '2026-03-24 15:00:00', '2026-03-24 15:30:00', 'HOAN_THANH', 'GD-004'),
('LK005', 'G005', 'BS005', '2026-03-25 08:15:00', '2026-03-25 08:45:00', 'DA_XAC_NHAN', 'GD-005'),
('LK006', 'G006', 'BS006', '2026-03-25 14:30:00', '2026-03-25 15:00:00', 'DA_HUY', 'GD-006'),
('LK007', 'G007', 'BS007', '2026-03-25 09:00:00', '2026-03-25 09:30:00', 'CHO_XAC_NHAN', 'GD-007'),
('LK008', 'G008', 'BS008', '2026-03-25 15:15:00', '2026-03-25 15:45:00', 'DA_XAC_NHAN', 'GD-008'),
('LK009', 'G009', 'BS009', '2026-03-26 08:30:00', '2026-03-26 09:00:00', 'HOAN_THANH', 'GD-009'),
('LK010', 'G010', 'BS010', '2026-03-26 14:10:00', '2026-03-26 14:40:00', 'DANG_KHAM', 'GD-010'),
('LK011', 'G011', 'BS011', '2026-03-26 09:10:00', '2026-03-26 09:40:00', 'CHO_XAC_NHAN', 'GD-011'),
('LK012', 'G012', 'BS012', '2026-03-26 15:10:00', '2026-03-26 15:40:00', 'DA_XAC_NHAN', 'GD-012'),
('LK013', 'G013', 'BS013', '2026-03-27 08:20:00', '2026-03-27 08:50:00', 'HOAN_THANH', 'GD-013'),
('LK014', 'G014', 'BS014', '2026-03-27 14:20:00', '2026-03-27 14:50:00', 'DA_HUY', 'GD-014'),
('LK015', 'G015', 'BS015', '2026-03-27 09:20:00', '2026-03-27 09:50:00', 'DA_XAC_NHAN', 'GD-015'),
('LK016', 'G016', 'BS016', '2026-03-27 15:20:00', '2026-03-27 15:50:00', 'CHO_XAC_NHAN', 'GD-016'),
('LK017', 'G017', 'BS017', '2026-03-28 08:40:00', '2026-03-28 09:10:00', 'DANG_KHAM', 'GD-017'),
('LK018', 'G018', 'BS018', '2026-03-28 14:40:00', '2026-03-28 15:10:00', 'DA_XAC_NHAN', 'GD-018'),
('LK019', 'G019', 'BS019', '2026-03-28 09:30:00', '2026-03-28 10:00:00', 'HOAN_THANH', 'GD-019'),
('LK020', 'G020', 'BS020', '2026-03-28 15:30:00', '2026-03-28 16:00:00', 'CHO_XAC_NHAN', 'GD-020'),
('LK021', 'G001', 'BS021', '2026-03-29 08:00:00', '2026-03-29 08:30:00', 'DA_XAC_NHAN', 'GD-021'),
('LK022', 'G002', 'BS022', '2026-03-29 14:00:00', '2026-03-29 14:30:00', 'DA_XAC_NHAN', 'GD-022'),
('LK023', 'G003', 'BS003', '2026-03-31 08:30:00', '2026-03-31 09:00:00', 'CHO_XAC_NHAN', 'GD-023');

INSERT INTO HoSoBenhAn (
	MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi,
	NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai
) VALUES
('HS001', 'LK001', 'Nguyễn Văn Hào', '0913001001', '079301001111', '1992-03-10', 'Nam', 'Quận 10, TP.HCM', '2026-03-24 08:00:00', 'Đau đầu nhẹ', 'Rối loạn tiền đình nhẹ', 'Theo dõi ngoại trú', 'Uống thuốc đủ liều, ngủ sớm', 'BS001', 'DA_KHAM'),
('HS002', 'LK002', 'Trần Thị Ánh', '0913001002', '079301002222', '1988-07-21', 'Nu', 'Quận 3, TP.HCM', '2026-03-24 14:00:00', 'Đau ngực thoáng qua', 'Nghi tăng huyết áp', 'Cần theo dõi thêm', 'Đo huyết áp sáng tối', 'BS002', 'CHO_KHAM'),
('HS003', 'LK003', 'Lê Quốc Duy', '0913001003', '079301003333', '1985-11-05', 'Nam', 'Quận 5, TP.HCM', '2026-03-24 09:00:00', 'Tê tay trái', 'Thiếu ngủ kéo dài', 'Điều trị ngoại trú', 'Tập vận động cổ vai gáy', 'BS003', 'DA_KHAM'),
('HS004', 'LK004', 'Phạm Thu Trang', '0913001004', '079301004444', '1994-01-18', 'Nu', 'Quận 1, TP.HCM', '2026-03-24 15:00:00', 'Đau bụng âm ỉ', 'Viêm dạ dày', 'Ổn định', 'Ăn uống đúng giờ', 'BS004', 'DA_KHAM'),
('HS005', 'LK005', 'Võ Minh Khôi', '0913001005', '079301005555', '1990-04-01', 'Nam', 'Quận 7, TP.HCM', '2026-03-25 08:15:00', 'Ho khan', 'Viêm họng cấp', 'Theo dõi 5 ngày', 'Uống nhiều nước ấm', 'BS005', 'DA_KHAM'),
('HS006', 'LK006', 'Đỗ Ngọc Bích', '0913001006', '079301006666', '1996-12-19', 'Nu', 'Quận Tân Bình, TP.HCM', '2026-03-25 14:30:00', 'Mệt mỏi', 'Lịch khám đã hủy', 'Không khám', 'Đặt lại lịch mới', 'BS006', 'HUY'),
('HS007', 'LK007', 'Bùi Thanh Tâm', '0913001007', '079301007777', '1982-02-09', 'Nam', 'Quận 4, TP.HCM', '2026-03-25 09:00:00', 'Đau lưng', 'Thoái hóa cột sống nhẹ', 'Điều trị ngoại trú', 'Vật lý trị liệu', 'BS007', 'DA_KHAM'),
('HS008', 'LK008', 'Huỳnh Thảo Vy', '0913001008', '079301008888', '1998-09-15', 'Nu', 'TP. Thủ Đức, TP.HCM', '2026-03-25 15:15:00', 'Nổi mẩn ngứa', 'Viêm da dị ứng', 'Ổn định', 'Tránh dị nguyên', 'BS008', 'DA_KHAM'),
('HS009', 'LK009', 'Đặng Gia Phúc', '0913001009', '079301009999', '1979-05-12', 'Nam', 'Quận 11, TP.HCM', '2026-03-26 08:30:00', 'Ù tai', 'Viêm tai giữa', 'Theo dõi', 'Tái khám sau 7 ngày', 'BS009', 'DA_KHAM'),
('HS010', 'LK010', 'Phan Mỹ Linh', '0913001010', '079301010101', '1993-08-08', 'Nu', 'Quận 8, TP.HCM', '2026-03-26 14:10:00', 'Mờ mắt', 'Khô mắt', 'Điều trị ngoại trú', 'Nghỉ màn hình 20-20-20', 'BS010', 'DA_KHAM'),
('HS011', 'LK011', 'Ngô Hải Đăng', '0913001011', '079301011111', '1987-06-30', 'Nam', 'Quận Bình Tân, TP.HCM', '2026-03-26 09:10:00', 'Đau răng', 'Viêm tủy răng', 'Can thiệp nha khoa', 'Giữ vệ sinh răng miệng', 'BS011', 'CHO_KHAM'),
('HS012', 'LK012', 'Trịnh Hoài An', '0913001012', '079301012121', '2018-10-02', 'Nu', 'Quận 6, TP.HCM', '2026-03-26 15:10:00', 'Sốt nhẹ', 'Nhiễm siêu vi', 'Theo dõi tại nhà', 'Bổ sung nước', 'BS012', 'DA_KHAM'),
('HS013', 'LK013', 'Lý Minh Tâm', '0913001013', '079301013131', '1991-01-20', 'Nu', 'Quận Phú Nhuận, TP.HCM', '2026-03-27 08:20:00', 'Rối loạn kinh nguyệt', 'Rối loạn nội tiết', 'Điều trị nội khoa', 'Tái khám sau 1 tháng', 'BS013', 'DA_KHAM'),
('HS014', 'LK014', 'Mai Quốc Bảo', '0913001014', '079301014141', '1984-07-03', 'Nam', 'Quận 12, TP.HCM', '2026-03-27 14:20:00', 'Đau bụng dưới', 'Lịch khám hủy', 'Không khám', 'Đổi khung giờ khác', 'BS014', 'HUY'),
('HS015', 'LK015', 'Đinh Nhã Trúc', '0913001015', '079301015151', '1995-03-28', 'Nu', 'Hóc Môn, TP.HCM', '2026-03-27 09:20:00', 'Tiểu buốt', 'Viêm đường tiết niệu', 'Điều trị ngoại trú', 'Uống nhiều nước', 'BS015', 'DA_KHAM'),
('HS016', 'LK016', 'Tạ Công Thành', '0913001016', '079301016161', '1976-09-17', 'Nam', 'Bình Chánh, TP.HCM', '2026-03-27 15:20:00', 'Sụt cân', 'Nghi suy kiệt', 'Theo dõi chuyên sâu', 'Làm thêm xét nghiệm', 'BS016', 'CHO_KHAM'),
('HS017', 'LK017', 'Cao Khánh Huyền', '0913001017', '079301017171', '1999-12-11', 'Nu', 'Quận Gò Vấp, TP.HCM', '2026-03-28 08:40:00', 'Ăn uống kém', 'Thiếu vi chất nhẹ', 'Tư vấn dinh dưỡng', 'Tăng rau xanh', 'BS017', 'DA_KHAM'),
('HS018', 'LK018', 'Vũ Anh Khoa', '0913001018', '079301018181', '1983-04-14', 'Nam', 'Quận 2, TP.HCM', '2026-03-28 14:40:00', 'Đau vai gáy', 'Co cứng cơ', 'Vật lý trị liệu', 'Tập kéo giãn cơ', 'BS018', 'DA_KHAM'),
('HS019', 'LK019', 'Tôn Gia Hân', '0913001019', '079301019191', '1997-05-24', 'Nu', 'Quận 9, TP.HCM', '2026-03-28 09:30:00', 'Mệt kéo dài', 'Rối loạn nhịp sinh học', 'Theo dõi', 'Ngủ trước 23h', 'BS019', 'DA_KHAM'),
('HS020', 'LK020', 'La Đức Long', '0913001020', '079301020202', '1980-08-19', 'Nam', 'Quận 7, TP.HCM', '2026-03-28 15:30:00', 'Khám tổng quát', 'Sức khỏe ổn định', 'Duy trì theo dõi định kỳ', 'Tập thể dục 150 phút/tuần', 'BS020', 'CHO_KHAM'),
('HS021', 'LK021', 'Phùng Trúc Quỳnh', '0913001021', '079301021212', '1992-11-22', 'Nu', 'Quận 1, TP.HCM', '2026-03-29 08:00:00', 'Đau đầu căng cơ', 'Căng thẳng công việc', 'Ổn định', 'Giảm caffeine', 'BS021', 'DA_KHAM'),
('HS022', 'LK022', 'Kiều Thành Nhân', '0913001022', '079301022222', '1978-02-16', 'Nam', 'Quận 3, TP.HCM', '2026-03-29 14:00:00', 'Hồi hộp', 'Rối loạn thần kinh tim', 'Theo dõi', 'Tập thở sâu', 'BS022', 'DA_KHAM'),
('HS023', 'LK023', 'Nguyễn Bảo Trâm', '0913001023', '079301023232', '2000-01-01', 'Nu', 'Quận 5, TP.HCM', '2026-03-31 08:30:00', 'Đau nửa đầu', 'Migraine', 'Điều trị triệu chứng', 'Tái khám nếu tăng nặng', 'BS003', 'CHO_KHAM');

INSERT INTO DonThuoc (MaDonThuoc, MaHoSo, NgayKeDon, GhiChu) VALUES
('DT001', 'HS001', '2026-03-24 08:20:00', 'Đơn điều trị đau đầu nhẹ'),
('DT002', 'HS003', '2026-03-24 09:20:00', 'Đơn giảm đau và vitamin thần kinh'),
('DT003', 'HS004', '2026-03-24 15:20:00', 'Đơn điều trị viêm dạ dày'),
('DT004', 'HS005', '2026-03-25 08:35:00', 'Đơn hô hấp nhẹ'),
('DT005', 'HS007', '2026-03-25 09:25:00', 'Đơn cơ xương khớp'),
('DT006', 'HS008', '2026-03-25 15:30:00', 'Đơn da liễu'),
('DT007', 'HS009', '2026-03-26 08:50:00', 'Đơn tai mũi họng'),
('DT008', 'HS010', '2026-03-26 14:30:00', 'Đơn mắt khô'),
('DT009', 'HS012', '2026-03-26 15:30:00', 'Đơn nhi khoa'),
('DT010', 'HS013', '2026-03-27 08:40:00', 'Đơn nội tiết phụ khoa'),
('DT011', 'HS015', '2026-03-27 09:40:00', 'Đơn tiết niệu'),
('DT012', 'HS017', '2026-03-28 09:00:00', 'Đơn dinh dưỡng'),
('DT013', 'HS018', '2026-03-28 15:00:00', 'Đơn giảm đau cơ'),
('DT014', 'HS019', '2026-03-28 09:50:00', 'Đơn điều chỉnh giấc ngủ'),
('DT015', 'HS021', '2026-03-29 08:20:00', 'Đơn giảm đau đầu'),
('DT016', 'HS022', '2026-03-29 14:20:00', 'Đơn tim mạch theo dõi'),
('DT017', 'HS005', '2026-03-25 08:40:00', 'Đơn bổ sung lần 2'),
('DT018', 'HS010', '2026-03-26 14:35:00', 'Đơn nhỏ mắt bổ sung'),
('DT019', 'HS001', '2026-03-24 08:25:00', 'Đơn vitamin bổ trợ'),
('DT020', 'HS019', '2026-03-28 09:55:00', 'Đơn hỗ trợ thần kinh');

INSERT INTO CTDonThuoc (MaCTDonThuoc, MaDonThuoc, MaThuoc, SoLuong, LieuDung, CachDung) VALUES
('CTDT001', 'DT001', 'T001', 10, '2 viên/ngày', 'Sau ăn sáng và tối'),
('CTDT002', 'DT001', 'T008', 10, '1 viên/ngày', 'Sau ăn trưa'),
('CTDT003', 'DT002', 'T001', 12, '2 viên/ngày', 'Sau ăn'),
('CTDT004', 'DT002', 'T023', 8, '1 viên/ngày', 'Buổi tối'),
('CTDT005', 'DT003', 'T004', 14, '1 viên/ngày', 'Trước ăn sáng 30 phút'),
('CTDT006', 'DT003', 'T005', 7, '1 viên/ngày', 'Buổi tối'),
('CTDT007', 'DT004', 'T002', 10, '2 viên/ngày', 'Sau ăn'),
('CTDT008', 'DT004', 'T006', 10, '1 viên/ngày', 'Buổi tối'),
('CTDT009', 'DT005', 'T024', 2, '2 lần/ngày', 'Bôi tại chỗ đau'),
('CTDT010', 'DT005', 'T023', 10, '1 viên/ngày', 'Sau ăn'),
('CTDT011', 'DT006', 'T006', 10, '1 viên/ngày', 'Buổi tối'),
('CTDT012', 'DT006', 'T025', 6, '1 viên/ngày', 'Sau ăn trưa'),
('CTDT013', 'DT007', 'T003', 10, '2 viên/ngày', 'Sau ăn'),
('CTDT014', 'DT007', 'T018', 1, '2 nhát/ngày', 'Xịt mũi sáng tối'),
('CTDT015', 'DT008', 'T019', 2, '2 giọt/lần', 'Rửa mắt ngày 2 lần'),
('CTDT016', 'DT008', 'T018', 1, '1 nhát/ngày', 'Trước ngủ'),
('CTDT017', 'DT009', 'T001', 8, '1 viên/lần', 'Khi sốt trên 38.5 độ'),
('CTDT018', 'DT009', 'T021', 6, '1 ống/ngày', 'Sau ăn sáng'),
('CTDT019', 'DT010', 'T013', 14, '2 viên/ngày', 'Sau ăn sáng tối'),
('CTDT020', 'DT011', 'T003', 10, '2 viên/ngày', 'Sau ăn'),
('CTDT021', 'DT012', 'T022', 10, '1 viên/ngày', 'Sau ăn trưa'),
('CTDT022', 'DT013', 'T024', 2, '2 lần/ngày', 'Bôi vùng vai gáy'),
('CTDT023', 'DT014', 'T008', 10, '1 viên/ngày', 'Buổi sáng'),
('CTDT024', 'DT015', 'T001', 10, '2 viên/ngày', 'Sau ăn'),
('CTDT025', 'DT016', 'T011', 14, '1 viên/ngày', 'Buổi sáng');

-- =========================
-- 5) PURCHASE + WAREHOUSE
-- =========================
INSERT INTO PhieuNhap (MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai) VALUES
('PN001', 'NCC001', '2026-03-01 09:00:00', 'Nguyễn Văn Nam', 3250000, 'DA_NHAP'),
('PN002', 'NCC002', '2026-03-01 10:00:00', 'Trần Thị Hòa', 4180000, 'DA_NHAP'),
('PN003', 'NCC003', '2026-03-02 09:20:00', 'Lê Anh Tuấn', 2850000, 'DA_DUYET'),
('PN004', 'NCC004', '2026-03-02 11:30:00', 'Phạm Đức Hiếu', 3720000, 'CHO_DUYET'),
('PN005', 'NCC005', '2026-03-03 08:40:00', 'Vũ Quang Minh', 2640000, 'DA_HUY'),
('PN006', 'NCC006', '2026-03-03 10:10:00', 'Đỗ Hồng Hà', 3180000, 'DA_NHAP'),
('PN007', 'NCC007', '2026-03-04 09:00:00', 'Ngô Văn Khang', 2950000, 'DA_DUYET'),
('PN008', 'NCC008', '2026-03-04 14:00:00', 'Bùi Gia Hưng', 3410000, 'DA_NHAP'),
('PN009', 'NCC009', '2026-03-05 09:45:00', 'Huỳnh Mai An', 2890000, 'CHO_DUYET'),
('PN010', 'NCC010', '2026-03-05 15:30:00', 'Đặng Hữu Tài', 2740000, 'DA_NHAP'),
('PN011', 'NCC011', '2026-03-06 08:30:00', 'Nguyễn Khánh Duy', 3660000, 'DA_DUYET'),
('PN012', 'NCC012', '2026-03-06 10:20:00', 'Trịnh Hoàng Sơn', 2550000, 'DA_NHAP'),
('PN013', 'NCC013', '2026-03-07 09:10:00', 'Lâm Quốc Việt', 2260000, 'DA_HUY'),
('PN014', 'NCC014', '2026-03-07 11:00:00', 'Cao Văn Nhật', 4120000, 'DA_NHAP'),
('PN015', 'NCC015', '2026-03-08 08:50:00', 'Phạm Thanh Bình', 3010000, 'CHO_DUYET'),
('PN016', 'NCC016', '2026-03-08 13:40:00', 'Tạ Kim Long', 2790000, 'DA_DUYET'),
('PN017', 'NCC017', '2026-03-09 09:30:00', 'Võ Anh Dũng', 3180000, 'DA_NHAP'),
('PN018', 'NCC018', '2026-03-09 10:50:00', 'Nguyễn Hoàng Vũ', 2930000, 'DA_NHAP'),
('PN019', 'NCC019', '2026-03-10 08:45:00', 'Lê Văn Trực', 2670000, 'DA_DUYET'),
('PN020', 'NCC020', '2026-03-10 15:00:00', 'Phan Quốc Nam', 3550000, 'DA_NHAP');

INSERT INTO ChiTietPhieuNhap (MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung) VALUES
('CTPN001', 'PN001', 'T001', 'LO-PARA-2601', 200, 150, 2200, '2027-03-01'),
('CTPN002', 'PN001', 'T008', 'LO-VITC-2601', 150, 120, 1500, '2027-05-01'),
('CTPN003', 'PN002', 'T002', 'LO-AMOX-2602', 180, 140, 3200, '2027-04-15'),
('CTPN004', 'PN002', 'T006', 'LO-LORA-2602', 120, 90, 2300, '2027-06-20'),
('CTPN005', 'PN003', 'T003', 'LO-CEFU-2603', 130, 130, 6500, '2027-08-01'),
('CTPN006', 'PN003', 'T018', 'LO-BUD-2603', 60, 60, 52000, '2026-12-10'),
('CTPN007', 'PN004', 'T004', 'LO-OMEP-2604', 170, 170, 2800, '2027-03-20'),
('CTPN008', 'PN004', 'T005', 'LO-ESOM-2604', 95, 95, 7600, '2027-02-28'),
('CTPN009', 'PN005', 'T007', 'LO-CETI-2605', 150, 150, 2600, '2027-01-15'),
('CTPN010', 'PN005', 'T022', 'LO-KEM-2605', 140, 140, 3100, '2026-10-15'),
('CTPN011', 'PN006', 'T009', 'LO-CALD-2606', 180, 150, 3900, '2027-07-07'),
('CTPN012', 'PN006', 'T010', 'LO-SILY-2606', 120, 90, 5200, '2027-06-18'),
('CTPN013', 'PN007', 'T011', 'LO-AMLO-2607', 130, 130, 4500, '2027-09-09'),
('CTPN014', 'PN007', 'T012', 'LO-LOSA-2607', 110, 110, 4800, '2027-09-30'),
('CTPN015', 'PN008', 'T013', 'LO-METF-2608', 160, 130, 2900, '2027-11-11'),
('CTPN016', 'PN008', 'T014', 'LO-GLIC-2608', 120, 80, 4700, '2027-12-01'),
('CTPN017', 'PN009', 'T015', 'LO-ATOR-2609', 140, 140, 5500, '2027-06-06'),
('CTPN018', 'PN010', 'T016', 'LO-BISO-2610', 130, 90, 5100, '2027-05-25'),
('CTPN019', 'PN011', 'T017', 'LO-SALB-2611', 70, 70, 65000, '2026-11-20'),
('CTPN020', 'PN012', 'T019', 'LO-NaCl-2612', 220, 180, 8000, '2027-04-04'),
('CTPN021', 'PN013', 'T020', 'LO-CHLO-2613', 90, 90, 29000, '2026-09-09'),
('CTPN022', 'PN014', 'T021', 'LO-PROB-2614', 150, 110, 9200, '2027-03-03'),
('CTPN023', 'PN015', 'T023', 'LO-MGB6-2615', 110, 110, 3600, '2026-08-20'),
('CTPN024', 'PN016', 'T024', 'LO-DICG-2616', 85, 85, 29000, '2026-12-25'),
('CTPN025', 'PN020', 'T025', 'LO-MP16-2620', 75, 55, 6200, '2026-07-30');

INSERT INTO HoaDonThuoc (
	MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan,
	TrangThaiLayThuoc, TenBenhNhan, SdtBenhNhan, Active
) VALUES
('HDT001', 'DT001', '2026-03-24 09:00:00', 61000, 'Thu ngân quầy 1', 'DA_THANH_TOAN', '2026-03-24 09:05:00', 'DA_HOAN_THANH', 'Nguyễn Văn Hào', '0913001001', 1),
('HDT002', 'DT002', '2026-03-24 10:00:00', 73000, 'Thu ngân quầy 1', 'DA_THANH_TOAN', '2026-03-24 10:05:00', 'CHO_LAY', 'Lê Quốc Duy', '0913001003', 1),
('HDT003', 'DT003', '2026-03-24 16:00:00', 98000, 'Thu ngân quầy 2', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Phạm Thu Trang', '0913001004', 1),
('HDT004', 'DT004', '2026-03-25 09:10:00', 68000, 'Thanh toán tiền mặt', 'DA_THANH_TOAN', '2026-03-25 09:15:00', 'DA_HOAN_THANH', 'Võ Minh Khôi', '0913001005', 1),
('HDT005', 'DT005', '2026-03-25 10:20:00', 54000, 'Khách chờ lấy thuốc', 'DA_THANH_TOAN', '2026-03-25 10:25:00', 'CHO_LAY', 'Bùi Thanh Tâm', '0913001007', 1),
('HDT006', 'DT006', '2026-03-25 16:10:00', 76000, 'Đơn da liễu', 'HOAN_HOA_DON', '2026-03-25 16:20:00', 'DA_HUY', 'Huỳnh Thảo Vy', '0913001008', 1),
('HDT007', 'DT007', '2026-03-26 09:30:00', 84000, 'Quầy 3', 'DA_THANH_TOAN', '2026-03-26 09:35:00', 'DA_HOAN_THANH', 'Đặng Gia Phúc', '0913001009', 1),
('HDT008', 'DT008', '2026-03-26 15:00:00', 69000, 'Quầy 2', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Phan Mỹ Linh', '0913001010', 1),
('HDT009', 'DT009', '2026-03-26 16:00:00', 52000, 'Đơn nhi khoa', 'DA_THANH_TOAN', '2026-03-26 16:02:00', 'DA_HOAN_THANH', 'Trịnh Hoài An', '0913001012', 1),
('HDT010', 'DT010', '2026-03-27 09:10:00', 78000, 'Đơn phụ khoa', 'DA_THANH_TOAN', '2026-03-27 09:15:00', 'CHO_LAY', 'Lý Minh Tâm', '0913001013', 1),
('HDT011', 'DT011', '2026-03-27 10:30:00', 64000, 'Đơn tiết niệu', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Đinh Nhã Trúc', '0913001015', 1),
('HDT012', 'DT012', '2026-03-28 09:20:00', 47000, 'Đơn vi chất', 'DA_THANH_TOAN', '2026-03-28 09:22:00', 'DA_HOAN_THANH', 'Cao Khánh Huyền', '0913001017', 1),
('HDT013', 'DT013', '2026-03-28 15:20:00', 83000, 'Đơn phục hồi', 'DA_THANH_TOAN', '2026-03-28 15:25:00', 'CHO_LAY', 'Vũ Anh Khoa', '0913001018', 1),
('HDT014', 'DT014', '2026-03-28 10:10:00', 35000, 'Đơn bổ trợ', 'DA_THANH_TOAN', '2026-03-28 10:15:00', 'DA_HOAN_THANH', 'Tôn Gia Hân', '0913001019', 1),
('HDT015', 'DT015', '2026-03-29 08:40:00', 45000, 'Đơn đau đầu', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Phùng Trúc Quỳnh', '0913001021', 1),
('HDT016', 'DT016', '2026-03-29 14:40:00', 98000, 'Đơn tim mạch', 'DA_THANH_TOAN', '2026-03-29 14:45:00', 'CHO_LAY', 'Kiều Thành Nhân', '0913001022', 1),
('HDT017', NULL, '2026-03-20 08:30:00', 125000, 'Mua lẻ tại quầy', 'DA_THANH_TOAN', '2026-03-20 08:35:00', 'DA_HOAN_THANH', 'Nguyễn Thị Bích', '0909111222', 1),
('HDT018', NULL, '2026-03-21 11:00:00', 86000, 'Mua lẻ tại quầy', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Trần Minh Tài', '0908222333', 1),
('HDT019', NULL, '2026-03-22 16:30:00', 93000, 'Mua lẻ tại quầy', 'HOAN_HOA_DON', '2026-03-22 16:40:00', 'DA_HUY', 'Lê Kim Oanh', '0907333444', 1),
('HDT020', NULL, '2026-03-23 09:45:00', 154000, 'Mua lẻ tại quầy', 'DA_THANH_TOAN', '2026-03-23 09:50:00', 'CHO_LAY', 'Phạm Văn Đạt', '0906444555', 1);

INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) VALUES
('CTHD001', 'HDT001', 'T001', 10, 3500, 35000, 'Sau ăn', 1),
('CTHD002', 'HDT001', 'T008', 10, 2600, 26000, 'Buổi trưa', 1),
('CTHD003', 'HDT002', 'T001', 12, 3500, 42000, 'Sau ăn', 1),
('CTHD004', 'HDT002', 'T023', 5, 6100, 30500, 'Buổi tối', 1),
('CTHD005', 'HDT003', 'T004', 14, 4300, 60200, 'Trước ăn', 1),
('CTHD006', 'HDT003', 'T005', 3, 11200, 33600, 'Buổi tối', 1),
('CTHD007', 'HDT004', 'T002', 10, 5200, 52000, 'Sau ăn', 1),
('CTHD008', 'HDT004', 'T006', 4, 3900, 15600, 'Buổi tối', 1),
('CTHD009', 'HDT005', 'T024', 1, 48000, 48000, 'Bôi ngoài da', 1),
('CTHD010', 'HDT005', 'T023', 1, 6100, 6100, 'Buổi tối', 1),
('CTHD011', 'HDT006', 'T006', 10, 3900, 39000, 'Dị ứng', 1),
('CTHD012', 'HDT007', 'T003', 8, 9800, 78400, 'Sau ăn', 1),
('CTHD013', 'HDT008', 'T019', 2, 12000, 24000, 'Rửa mắt', 1),
('CTHD014', 'HDT008', 'T018', 1, 76000, 76000, 'Xịt mũi', 1),
('CTHD015', 'HDT009', 'T001', 8, 3500, 28000, 'Hạ sốt', 1),
('CTHD016', 'HDT009', 'T021', 2, 13800, 27600, 'Men vi sinh', 1),
('CTHD017', 'HDT010', 'T013', 10, 4700, 47000, 'Sau ăn', 1),
('CTHD018', 'HDT011', 'T003', 6, 9800, 58800, 'Sau ăn', 1),
('CTHD019', 'HDT012', 'T022', 8, 5300, 42400, 'Vi chất', 1),
('CTHD020', 'HDT013', 'T024', 1, 48000, 48000, 'Bôi đau cơ', 1),
('CTHD021', 'HDT014', 'T008', 10, 2600, 26000, 'Buổi sáng', 1),
('CTHD022', 'HDT015', 'T001', 10, 3500, 35000, 'Sau ăn', 1),
('CTHD023', 'HDT016', 'T011', 14, 6400, 89600, 'Buổi sáng', 1),
('CTHD024', 'HDT017', 'T020', 2, 45000, 90000, 'Súc miệng', 1),
('CTHD025', 'HDT020', 'T017', 1, 98000, 98000, 'Xịt định liều', 1);

INSERT INTO XuatThuocTheoLo (MaHoaDon, MaCTHDThuoc, MaCTPN, MaThuoc, SoLo, HanSuDung, SoLuongXuat, NgayXuat) VALUES
('HDT001', 'CTHD001', 'CTPN001', 'T001', 'LO-PARA-2601', '2027-03-01', 10, '2026-03-24 09:06:00'),
('HDT001', 'CTHD002', 'CTPN002', 'T008', 'LO-VITC-2601', '2027-05-01', 10, '2026-03-24 09:06:30'),
('HDT004', 'CTHD007', 'CTPN003', 'T002', 'LO-AMOX-2602', '2027-04-15', 10, '2026-03-25 09:16:00'),
('HDT004', 'CTHD008', 'CTPN004', 'T006', 'LO-LORA-2602', '2027-06-20', 4, '2026-03-25 09:16:20'),
('HDT007', 'CTHD012', 'CTPN005', 'T003', 'LO-CEFU-2603', '2027-08-01', 8, '2026-03-26 09:36:00'),
('HDT009', 'CTHD015', 'CTPN001', 'T001', 'LO-PARA-2601', '2027-03-01', 8, '2026-03-26 16:03:00'),
('HDT009', 'CTHD016', 'CTPN022', 'T021', 'LO-PROB-2614', '2027-03-03', 2, '2026-03-26 16:03:10'),
('HDT012', 'CTHD019', 'CTPN010', 'T022', 'LO-KEM-2605', '2026-10-15', 8, '2026-03-28 09:23:00'),
('HDT014', 'CTHD021', 'CTPN002', 'T008', 'LO-VITC-2601', '2027-05-01', 10, '2026-03-28 10:16:00'),
('HDT017', 'CTHD024', 'CTPN021', 'T020', 'LO-CHLO-2613', '2026-09-09', 2, '2026-03-20 08:36:00'),
('HDT002', 'CTHD003', 'CTPN001', 'T001', 'LO-PARA-2601', '2027-03-01', 12, '2026-03-24 10:06:00'),
('HDT002', 'CTHD004', 'CTPN023', 'T023', 'LO-MGB6-2615', '2026-08-20', 5, '2026-03-24 10:06:10'),
('HDT005', 'CTHD009', 'CTPN024', 'T024', 'LO-DICG-2616', '2026-12-25', 1, '2026-03-25 10:26:00'),
('HDT005', 'CTHD010', 'CTPN023', 'T023', 'LO-MGB6-2615', '2026-08-20', 1, '2026-03-25 10:26:10'),
('HDT016', 'CTHD023', 'CTPN013', 'T011', 'LO-AMLO-2607', '2027-09-09', 14, '2026-03-29 14:46:00'),
('HDT013', 'CTHD020', 'CTPN024', 'T024', 'LO-DICG-2616', '2026-12-25', 1, '2026-03-28 15:26:00'),
('HDT020', 'CTHD025', 'CTPN019', 'T017', 'LO-SALB-2611', '2026-11-20', 1, '2026-03-23 09:51:00'),
('HDT010', 'CTHD017', 'CTPN015', 'T013', 'LO-METF-2608', '2027-11-11', 10, '2026-03-27 09:16:00'),
('HDT011', 'CTHD018', 'CTPN005', 'T003', 'LO-CEFU-2603', '2027-08-01', 6, '2026-03-27 10:31:00'),
('HDT015', 'CTHD022', 'CTPN001', 'T001', 'LO-PARA-2601', '2027-03-01', 10, '2026-03-29 08:41:00');

INSERT INTO TieuHuyLoThuoc (MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongTieuHuy, HanSuDung, NgayTieuHuy, LyDo, NguoiThucHien) VALUES
('CTPN021', 'PN013', 'T020', 'LO-CHLO-2613', 3, '2026-09-09 00:00:00', '2026-10-10 10:00:00', 'Hết hạn theo quy định', 'Nguyễn Anh Thư'),
('CTPN023', 'PN015', 'T023', 'LO-MGB6-2615', 2, '2026-08-20 00:00:00', '2026-08-25 09:00:00', 'Bao bì hư hỏng', 'Khánh Vy'),
('CTPN025', 'PN020', 'T025', 'LO-MP16-2620', 1, '2026-07-30 00:00:00', '2026-08-01 14:00:00', 'Hàng cận date không đạt chuẩn', 'Hồng Nhung'),
('CTPN010', 'PN005', 'T022', 'LO-KEM-2605', 2, '2026-10-15 00:00:00', '2026-10-18 11:00:00', 'Rách vỏ hộp', 'Văn Trang'),
('CTPN024', 'PN016', 'T024', 'LO-DICG-2616', 1, '2026-12-25 00:00:00', '2026-12-30 15:20:00', 'Móp méo bao bì', 'Tuấn Anh'),
('CTPN019', 'PN011', 'T017', 'LO-SALB-2611', 1, '2026-11-20 00:00:00', '2026-11-28 13:00:00', 'Lọ xịt rò rỉ', 'Nhật Linh'),
('CTPN018', 'PN010', 'T016', 'LO-BISO-2610', 1, '2027-05-25 00:00:00', '2026-12-01 09:30:00', 'Vỡ viên', 'Bảo Trâm'),
('CTPN017', 'PN009', 'T015', 'LO-ATOR-2609', 1, '2027-06-06 00:00:00', '2026-12-02 09:50:00', 'Hộp méo góc', 'Nguyễn Anh Thư'),
('CTPN016', 'PN008', 'T014', 'LO-GLIC-2608', 1, '2027-12-01 00:00:00', '2026-12-03 10:15:00', 'Mất tem phụ', 'Khánh Vy'),
('CTPN015', 'PN008', 'T013', 'LO-METF-2608', 1, '2027-11-11 00:00:00', '2026-12-03 10:30:00', 'Bể lọ trong quá trình vận chuyển', 'Hồng Nhung'),
('CTPN014', 'PN007', 'T012', 'LO-LOSA-2607', 1, '2027-09-30 00:00:00', '2026-12-04 11:20:00', 'Kiểm kê thiếu chuẩn', 'Văn Trang'),
('CTPN013', 'PN007', 'T011', 'LO-AMLO-2607', 1, '2027-09-09 00:00:00', '2026-12-04 11:35:00', 'Vỡ hộp', 'Tuấn Anh'),
('CTPN012', 'PN006', 'T010', 'LO-SILY-2606', 1, '2027-06-18 00:00:00', '2026-12-05 14:00:00', 'Nhiễm ẩm', 'Nhật Linh'),
('CTPN011', 'PN006', 'T009', 'LO-CALD-2606', 1, '2027-07-07 00:00:00', '2026-12-05 14:30:00', 'Đổi mẫu mã', 'Bảo Trâm'),
('CTPN009', 'PN005', 'T007', 'LO-CETI-2605', 1, '2027-01-15 00:00:00', '2026-12-06 08:40:00', 'Sai nhiệt độ bảo quản', 'Nguyễn Anh Thư'),
('CTPN008', 'PN004', 'T005', 'LO-ESOM-2604', 1, '2027-02-28 00:00:00', '2026-12-06 09:00:00', 'Mất nhãn phụ', 'Khánh Vy'),
('CTPN007', 'PN004', 'T004', 'LO-OMEP-2604', 1, '2027-03-20 00:00:00', '2026-12-06 09:15:00', 'Tem mờ', 'Hồng Nhung'),
('CTPN006', 'PN003', 'T018', 'LO-BUD-2603', 1, '2026-12-10 00:00:00', '2026-12-11 10:10:00', 'Rò khí đầu xịt', 'Văn Trang'),
('CTPN005', 'PN003', 'T003', 'LO-CEFU-2603', 1, '2027-08-01 00:00:00', '2026-12-07 11:00:00', 'Thùng hàng móp nặng', 'Tuấn Anh'),
('CTPN004', 'PN002', 'T006', 'LO-LORA-2602', 1, '2027-06-20 00:00:00', '2026-12-07 11:20:00', 'Đổi lô theo NCC', 'Nhật Linh');

INSERT INTO HoaDonKham (MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai) VALUES
('HDK001', 'HS001', 'G001', '2026-03-24 08:40:00', 180000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK002', 'HS003', 'G003', '2026-03-24 09:40:00', 320000, 'Chuyển khoản', 'DA_THANH_TOAN'),
('HDK003', 'HS004', 'G004', '2026-03-24 15:45:00', 280000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK004', 'HS005', 'G005', '2026-03-25 08:55:00', 260000, 'Tiền mặt', 'CHO_THANH_TOAN'),
('HDK005', 'HS006', 'G006', '2026-03-25 14:40:00', 300000, 'Tiền mặt', 'HOAN_TIEN'),
('HDK006', 'HS007', 'G007', '2026-03-25 09:40:00', 280000, 'Chuyển khoản', 'DA_THANH_TOAN'),
('HDK007', 'HS008', 'G008', '2026-03-25 15:50:00', 250000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK008', 'HS009', 'G009', '2026-03-26 09:10:00', 230000, 'Chuyển khoản', 'DA_THANH_TOAN'),
('HDK009', 'HS010', 'G010', '2026-03-26 14:55:00', 220000, 'Tiền mặt', 'CHO_THANH_TOAN'),
('HDK010', 'HS011', 'G011', '2026-03-26 09:45:00', 240000, 'Tiền mặt', 'CHO_THANH_TOAN'),
('HDK011', 'HS012', 'G012', '2026-03-26 15:50:00', 210000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK012', 'HS013', 'G013', '2026-03-27 08:55:00', 330000, 'Chuyển khoản', 'DA_THANH_TOAN'),
('HDK013', 'HS014', 'G014', '2026-03-27 14:40:00', 300000, 'Tiền mặt', 'HOAN_TIEN'),
('HDK014', 'HS015', 'G015', '2026-03-27 10:00:00', 290000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK015', 'HS016', 'G016', '2026-03-27 16:00:00', 450000, 'Chuyển khoản', 'CHO_THANH_TOAN'),
('HDK016', 'HS017', 'G017', '2026-03-28 09:20:00', 260000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK017', 'HS018', 'G018', '2026-03-28 15:20:00', 280000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK018', 'HS019', 'G019', '2026-03-28 10:10:00', 200000, 'Chuyển khoản', 'DA_THANH_TOAN'),
('HDK019', 'HS021', 'G001', '2026-03-29 08:40:00', 180000, 'Tiền mặt', 'DA_THANH_TOAN'),
('HDK020', 'HS022', 'G002', '2026-03-29 14:40:00', 350000, 'Chuyển khoản', 'CHO_THANH_TOAN');

-- =========================
-- 6) CẬP NHẬT TỒN KHO HỢP LÝ SAU SEED
-- (đảm bảo số lượng tồn không âm và phản ánh dữ liệu mẫu)
-- =========================
UPDATE Thuoc t
SET SoLuongTon = GREATEST(
	0,
	COALESCE((
		SELECT SUM(ctpn.SoLuongNhap)
		FROM ChiTietPhieuNhap ctpn
		WHERE ctpn.MaThuoc = t.MaThuoc
			AND ctpn.MaPhieuNhap IN (
				SELECT MaPhieuNhap FROM PhieuNhap WHERE TrangThai IN ('DA_DUYET', 'DA_NHAP')
			)
	), 0)
	- COALESCE((
		SELECT SUM(cthd.SoLuong)
		FROM CTHDThuoc cthd
		JOIN HoaDonThuoc hdt ON hdt.MaHoaDon = cthd.MaHoaDon
		WHERE cthd.MaThuoc = t.MaThuoc
			AND hdt.TrangThaiLayThuoc = 'DA_HOAN_THANH'
			AND hdt.Active = 1
	), 0)
	- COALESCE((
		SELECT SUM(th.SoLuongTieuHuy)
		FROM TieuHuyLoThuoc th
		WHERE th.MaThuoc = t.MaThuoc
	), 0)
)
WHERE t.Active = 1;

SELECT 'Seed reset_full_vi_2026-03-22 đã hoàn tất.' AS Message;
