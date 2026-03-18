SET NAMES utf8mb4;
USE PhongKham;

-- Reset all data
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE RolePermissions;
TRUNCATE TABLE CTHDThuoc;
TRUNCATE TABLE HoaDonThuoc;
TRUNCATE TABLE ChiTietPhieuNhap;
TRUNCATE TABLE PhieuNhap;
TRUNCATE TABLE CTDonThuoc;
TRUNCATE TABLE DonThuoc;
TRUNCATE TABLE HoaDonKham;
TRUNCATE TABLE HoSoBenhAn;
TRUNCATE TABLE LichKham;
TRUNCATE TABLE LichLamViec;
TRUNCATE TABLE GoiDichVu;
TRUNCATE TABLE BacSi;
TRUNCATE TABLE Thuoc;
TRUNCATE TABLE NhaCungCap;
TRUNCATE TABLE Users;
TRUNCATE TABLE Permissions;
TRUNCATE TABLE Khoa;
TRUNCATE TABLE Roles;

SET FOREIGN_KEY_CHECKS = 1;

-- Roles: only 4 rows by request
INSERT INTO Roles (STT, TenVaiTro, MoTa) VALUES
(1, 'Admin', 'Quan tri he thong'),
(2, 'BacSi', 'Bac si kham benh'),
(3, 'NhaThuoc', 'Nhan vien nha thuoc'),
(4, 'Guest', 'Khach chua dang nhap');

-- 10-12 rows each remaining table
INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('DASHBOARD_VIEW', 'Xem dashboard', 1),
('KHOA_VIEW', 'Xem khoa', 1),
('THUOC_VIEW', 'Xem thuoc', 1),
('THUOC_MANAGE', 'Quan ly thuoc', 1),
('GOIDICHVU_VIEW', 'Xem goi dich vu', 1),
('GOIDICHVU_MANAGE', 'Quan ly goi dich vu', 1),
('NCC_VIEW', 'Xem nha cung cap', 1),
('NCC_MANAGE', 'Quan ly nha cung cap', 1),
('PHIEUNHAP_VIEW', 'Xem phieu nhap', 1),
('PHIEUNHAP_MANAGE', 'Quan ly phieu nhap', 1),
('HOADONTHUOC_VIEW', 'Xem hoa don thuoc', 1),
('HOADONTHUOC_CREATE', 'Tao hoa don thuoc', 1),
('HOADONTHUOC_MANAGE', 'Quan ly hoa don thuoc', 1),
('HOADONKHAM_VIEW', 'Xem hoa don kham', 1),
('HOADONKHAM_MANAGE', 'Quan ly hoa don kham', 1),
('PHANQUYEN_VIEW', 'Xem phan quyen', 1),
('ROLE_PERMISSION_MANAGE', 'Quan ly role va permission', 1),
('USER_MANAGE', 'Quan ly nguoi dung', 1),
('LICHLAMVIEC_VIEW', 'Xem lich lam viec', 1),
('LICHLAMVIEC_APPROVE', 'Duyet lich lam viec', 1),
('LICHKHAM_VIEW', 'Xem lich kham', 1),
('LICHKHAM_MANAGE', 'Quan ly lich kham', 1),
('KHAMBENH_CREATE', 'Thuc hien kham benh', 1),
('HOSO_MANAGE', 'Quan ly ho so benh an', 1),
('BACSI_PROFILE_VIEW', 'Xem ho so bac si', 1),
('GUEST_BOOKING', 'Dat lich kham guest', 1),
('GUEST_BUY_THUOC', 'Mua thuoc guest', 1);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, p.MaPermission, 1
FROM Permissions p
WHERE p.Active = 1;

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 2, p.MaPermission, 1
FROM Permissions p
WHERE p.TenPermission IN (
  'LICHLAMVIEC_VIEW',
  'LICHKHAM_VIEW',
  'LICHKHAM_MANAGE',
  'KHAMBENH_CREATE',
  'HOSO_MANAGE',
  'BACSI_PROFILE_VIEW',
  'HOADONKHAM_VIEW'
);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 3, p.MaPermission, 1
FROM Permissions p
WHERE p.TenPermission IN (
  'THUOC_VIEW',
  'THUOC_MANAGE',
  'NCC_VIEW',
  'NCC_MANAGE',
  'PHIEUNHAP_VIEW',
  'PHIEUNHAP_MANAGE',
  'HOADONTHUOC_VIEW',
  'HOADONTHUOC_CREATE',
  'HOADONTHUOC_MANAGE'
);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 4, p.MaPermission, 1
FROM Permissions p
WHERE p.TenPermission IN ('GUEST_BOOKING', 'GUEST_BUY_THUOC');

INSERT INTO Users (UserID, Username, Password, Email, RoleID, Active, CreatedAt) VALUES
('U001', 'admin', '123456', 'admin@pk.com', 1, 1, '2026-03-01 08:00:00'),
('U002', 'bacsi01', '123456', 'bs01@pk.com', 2, 1, '2026-03-01 08:05:00'),
('U003', 'bacsi02', '123456', 'bs02@pk.com', 2, 1, '2026-03-01 08:10:00'),
('U004', 'bacsi03', '123456', 'bs03@pk.com', 2, 1, '2026-03-01 08:15:00'),
('U005', 'bacsi04', '123456', 'bs04@pk.com', 2, 1, '2026-03-01 08:20:00'),
('U006', 'nhathuoc01', '123456', 'nt01@pk.com', 3, 1, '2026-03-01 08:25:00'),
('U007', 'nhathuoc02', '123456', 'nt02@pk.com', 3, 1, '2026-03-01 08:30:00'),
('U008', 'nhathuoc03', '123456', 'nt03@pk.com', 3, 1, '2026-03-01 08:35:00'),
('U009', 'guest01', '123456', 'guest01@pk.com', 4, 1, '2026-03-01 08:40:00'),
('U010', 'guest02', '123456', 'guest02@pk.com', 4, 1, '2026-03-01 08:45:00');

INSERT INTO Khoa (MaKhoa, TenKhoa) VALUES
('K001', 'Noi tong quat'),
('K002', 'Ngoai tong quat'),
('K003', 'Nhi khoa'),
('K004', 'Tai mui hong'),
('K005', 'Da lieu'),
('K006', 'Than kinh'),
('K007', 'Tim mach'),
('K008', 'Noi tiet'),
('K009', 'Mat'),
('K010', 'Rang ham mat');

INSERT INTO BacSi (MaBacSi, HoTen, ChuyenKhoa, SoDienThoai, Email, MaKhoa) VALUES
('BS001', 'Nguyen Van An', 'Noi tong quat', '0901000001', 'bs001@pk.com', 'K001'),
('BS002', 'Tran Thi Binh', 'Ngoai tong quat', '0901000002', 'bs002@pk.com', 'K002'),
('BS003', 'Le Minh Chau', 'Nhi khoa', '0901000003', 'bs003@pk.com', 'K003'),
('BS004', 'Pham Ngoc Dung', 'Tai mui hong', '0901000004', 'bs004@pk.com', 'K004'),
('BS005', 'Vo Thanh Hai', 'Da lieu', '0901000005', 'bs005@pk.com', 'K005'),
('BS006', 'Do Khanh Linh', 'Than kinh', '0901000006', 'bs006@pk.com', 'K006'),
('BS007', 'Bui Tuan Minh', 'Tim mach', '0901000007', 'bs007@pk.com', 'K007'),
('BS008', 'Hoang Thu Nga', 'Noi tiet', '0901000008', 'bs008@pk.com', 'K008'),
('BS009', 'Dang Quoc Phong', 'Mat', '0901000009', 'bs009@pk.com', 'K009'),
('BS010', 'Nguyen Thi Quynh', 'Rang ham mat', '0901000010', 'bs010@pk.com', 'K010');

INSERT INTO GoiDichVu (MaGoi, TenGoi, GiaDichVu, ThoiGianKham, MoTa, MaKhoa) VALUES
('GOI001', 'Kham tong quat co ban', 150000, 20, 'Kham tong quat va tu van', 'K001'),
('GOI002', 'Kham ngoai tong quat', 180000, 25, 'Kham chan thuong co ban', 'K002'),
('GOI003', 'Kham nhi dinh ky', 160000, 20, 'Danh gia phat trien tre', 'K003'),
('GOI004', 'Kham tai mui hong', 170000, 20, 'Noi soi va danh gia TMH', 'K004'),
('GOI005', 'Kham da lieu', 190000, 25, 'Tu van va dieu tri da lieu', 'K005'),
('GOI006', 'Kham than kinh', 220000, 30, 'Danh gia benh ly than kinh', 'K006'),
('GOI007', 'Kham tim mach', 250000, 30, 'Do ECG va tu van tim mach', 'K007'),
('GOI008', 'Kham noi tiet', 230000, 30, 'Danh gia duong huyet va noi tiet', 'K008'),
('GOI009', 'Kham mat', 200000, 25, 'Do thi luc va soi day mat', 'K009'),
('GOI010', 'Kham rang ham mat', 180000, 25, 'Kham va tu van suc khoe rang', 'K010');

INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonVi, DonGiaBan, SoLuongTon, Active) VALUES
('T001', 'Paracetamol 500mg', 'Paracetamol', 'Vien', 'Hop', 2500, 500, 1),
('T002', 'Amoxicillin 500mg', 'Amoxicillin', 'Vien', 'Hop', 3500, 420, 1),
('T003', 'Cetirizine 10mg', 'Cetirizine', 'Vien', 'Hop', 2200, 380, 1),
('T004', 'Omeprazole 20mg', 'Omeprazole', 'Vien', 'Hop', 3000, 340, 1),
('T005', 'Vitamin C 500mg', 'Acid Ascorbic', 'Vien', 'Hop', 1500, 600, 1),
('T006', 'Alpha Choay', 'Alphachymotrypsin', 'Vien', 'Hop', 2800, 300, 1),
('T007', 'Efferalgan 500mg', 'Paracetamol', 'Vien', 'Hop', 2600, 450, 1),
('T008', 'Medrol 16mg', 'Methylprednisolone', 'Vien', 'Hop', 4800, 260, 1),
('T009', 'Bromhexin 8mg', 'Bromhexin', 'Vien', 'Hop', 2100, 390, 1),
('T010', 'Smecta', 'Diosmectite', 'Goi', 'Hop', 3300, 410, 1);

INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SDT, Active) VALUES
('NCC001', 'Duoc Pham A Chau', 'Quan 1, TP.HCM', '0281000001', 1),
('NCC002', 'Y Te Minh Khang', 'Quan 3, TP.HCM', '0281000002', 1),
('NCC003', 'Thiet Bi Y Te Viet', 'Quan 5, TP.HCM', '0281000003', 1),
('NCC004', 'Duoc Pham Hoang Long', 'Quan 7, TP.HCM', '0281000004', 1),
('NCC005', 'An Khang Pharma', 'Thu Duc, TP.HCM', '0281000005', 1),
('NCC006', 'Tam Duc Medical', 'Go Vap, TP.HCM', '0281000006', 1),
('NCC007', 'Hanh Phuc Pharma', 'Tan Binh, TP.HCM', '0281000007', 1),
('NCC008', 'BlueCare Medical', 'Binh Thanh, TP.HCM', '0281000008', 1),
('NCC009', 'Sai Gon Med Supply', 'Phu Nhuan, TP.HCM', '0281000009', 1),
('NCC010', 'Mekong Pharma', 'Quan 10, TP.HCM', '0281000010', 1);

INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam, TrangThai) VALUES
('LLV001', 'BS001', '2026-03-20', 'Sang', 'DA_DUYET'),
('LLV002', 'BS002', '2026-03-20', 'Chieu', 'DA_DUYET'),
('LLV003', 'BS003', '2026-03-21', 'Sang', 'DA_DUYET'),
('LLV004', 'BS004', '2026-03-21', 'Chieu', 'DA_DUYET'),
('LLV005', 'BS005', '2026-03-22', 'Sang', 'DA_DUYET'),
('LLV006', 'BS006', '2026-03-22', 'Chieu', 'DA_DUYET'),
('LLV007', 'BS007', '2026-03-23', 'Sang', 'DA_DUYET'),
('LLV008', 'BS008', '2026-03-23', 'Chieu', 'DA_DUYET'),
('LLV009', 'BS009', '2026-03-24', 'Sang', 'DA_DUYET'),
('LLV010', 'BS010', '2026-03-24', 'Chieu', 'DA_DUYET');

INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, ThoiGianKetThuc, TrangThai, MaDinhDanhTam) VALUES
('LK001', 'GOI001', 'BS001', '2026-03-20 08:00:00', '2026-03-20 08:20:00', 'HOAN_THANH', 'TAM001'),
('LK002', 'GOI002', 'BS002', '2026-03-20 14:00:00', '2026-03-20 14:25:00', 'HOAN_THANH', 'TAM002'),
('LK003', 'GOI003', 'BS003', '2026-03-21 08:30:00', '2026-03-21 08:50:00', 'HOAN_THANH', 'TAM003'),
('LK004', 'GOI004', 'BS004', '2026-03-21 14:30:00', '2026-03-21 14:50:00', 'HOAN_THANH', 'TAM004'),
('LK005', 'GOI005', 'BS005', '2026-03-22 09:00:00', '2026-03-22 09:25:00', 'HOAN_THANH', 'TAM005'),
('LK006', 'GOI006', 'BS006', '2026-03-22 15:00:00', '2026-03-22 15:30:00', 'HOAN_THANH', 'TAM006'),
('LK007', 'GOI007', 'BS007', '2026-03-23 08:10:00', '2026-03-23 08:40:00', 'HOAN_THANH', 'TAM007'),
('LK008', 'GOI008', 'BS008', '2026-03-23 14:10:00', '2026-03-23 14:40:00', 'HOAN_THANH', 'TAM008'),
('LK009', 'GOI009', 'BS009', '2026-03-24 08:15:00', '2026-03-24 08:40:00', 'HOAN_THANH', 'TAM009'),
('LK010', 'GOI010', 'BS010', '2026-03-24 14:15:00', '2026-03-24 14:40:00', 'HOAN_THANH', 'TAM010');

INSERT INTO HoSoBenhAn (
  MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi,
  NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai
) VALUES
('HS001', 'LK001', 'Pham Gia Bao', '0902000001', '079001000001', '1995-03-12', 'NAM', 'Quan 1', '2026-03-20 08:00:00', 'Sot nhe', 'Viem hong', 'On dinh', 'Uong nhieu nuoc', 'BS001', 'DA_KHAM'),
('HS002', 'LK002', 'Le Thi My', '0902000002', '079001000002', '1992-07-22', 'NU', 'Quan 3', '2026-03-20 14:00:00', 'Dau bung', 'Roi loan tieu hoa', 'On dinh', 'An nhe', 'BS002', 'DA_KHAM'),
('HS003', 'LK003', 'Tran Minh Khoa', '0902000003', '079001000003', '2018-01-11', 'NAM', 'Quan 5', '2026-03-21 08:30:00', 'Ho', 'Viem hong cap', 'On dinh', 'Dung thuoc dung lieu', 'BS003', 'DA_KHAM'),
('HS004', 'LK004', 'Nguyen Kim Anh', '0902000004', '079001000004', '1988-11-09', 'NU', 'Quan 7', '2026-03-21 14:30:00', 'Dau tai', 'Viem tai giua', 'Can theo doi', 'Tai kham 5 ngay', 'BS004', 'DA_KHAM'),
('HS005', 'LK005', 'Bui Hong Son', '0902000005', '079001000005', '1985-05-18', 'NAM', 'Thu Duc', '2026-03-22 09:00:00', 'Ngua da', 'Viem da co dia', 'On dinh', 'Tranh di ung', 'BS005', 'DA_KHAM'),
('HS006', 'LK006', 'Vo Minh Trang', '0902000006', '079001000006', '1990-09-30', 'NU', 'Go Vap', '2026-03-22 15:00:00', 'Dau dau', 'Dau nua dau', 'On dinh', 'Nghi ngoi hop ly', 'BS006', 'DA_KHAM'),
('HS007', 'LK007', 'Do Thanh Dat', '0902000007', '079001000007', '1979-06-14', 'NAM', 'Tan Binh', '2026-03-23 08:10:00', 'Hoi hop', 'Tang huyet ap nhe', 'Can theo doi', 'Giam muoi', 'BS007', 'DA_KHAM'),
('HS008', 'LK008', 'Hoang Thu Ha', '0902000008', '079001000008', '1987-10-02', 'NU', 'Binh Thanh', '2026-03-23 14:10:00', 'Khat nuoc', 'Roi loan duong huyet', 'Can theo doi', 'Kiem tra duong huyet', 'BS008', 'DA_KHAM'),
('HS009', 'LK009', 'Phan Quoc Nam', '0902000009', '079001000009', '1993-12-25', 'NAM', 'Phu Nhuan', '2026-03-24 08:15:00', 'Mo mat', 'Can thi nhe', 'On dinh', 'Gioi han man hinh', 'BS009', 'DA_KHAM'),
('HS010', 'LK010', 'Nguyen Bao Chau', '0902000010', '079001000010', '1998-04-08', 'NU', 'Quan 10', '2026-03-24 14:15:00', 'Dau rang', 'Sau rang nhe', 'On dinh', 'Ve sinh rang mieng', 'BS010', 'DA_KHAM');

INSERT INTO DonThuoc (MaDonThuoc, MaHoSo, NgayKeDon, GhiChu) VALUES
('DT001', 'HS001', '2026-03-20 08:10:00', 'Don thong thuong'),
('DT002', 'HS002', '2026-03-20 14:10:00', 'Theo doi tieu hoa'),
('DT003', 'HS003', '2026-03-21 08:40:00', 'Tre em lieu thap'),
('DT004', 'HS004', '2026-03-21 14:40:00', 'Khang sinh theo lieu'),
('DT005', 'HS005', '2026-03-22 09:10:00', 'Boi ngoai da'),
('DT006', 'HS006', '2026-03-22 15:10:00', 'Giam dau than kinh'),
('DT007', 'HS007', '2026-03-23 08:20:00', 'Theo doi tim mach'),
('DT008', 'HS008', '2026-03-23 14:20:00', 'Theo doi duong huyet'),
('DT009', 'HS009', '2026-03-24 08:25:00', 'Bo sung vitamin'),
('DT010', 'HS010', '2026-03-24 14:25:00', 'Dieu tri rang mieng');

INSERT INTO CTDonThuoc (MaCTDonThuoc, MaDonThuoc, MaThuoc, SoLuong, LieuDung, CachDung) VALUES
('CTDT001', 'DT001', 'T001', 10, '2 vien/ngay', 'Sau an'),
('CTDT002', 'DT002', 'T004', 14, '1 vien/ngay', 'Truoc an sang'),
('CTDT003', 'DT003', 'T007', 10, '2 vien/ngay', 'Khi sot'),
('CTDT004', 'DT004', 'T002', 14, '2 vien/ngay', 'Sau an'),
('CTDT005', 'DT005', 'T006', 10, '2 vien/ngay', 'Sau an'),
('CTDT006', 'DT006', 'T008', 10, '1 vien/ngay', 'Buoi sang'),
('CTDT007', 'DT007', 'T005', 20, '2 vien/ngay', 'Sau an'),
('CTDT008', 'DT008', 'T010', 10, '2 goi/ngay', 'Pha voi nuoc'),
('CTDT009', 'DT009', 'T003', 10, '1 vien/ngay', 'Buoi toi'),
('CTDT010', 'DT010', 'T009', 12, '2 vien/ngay', 'Sau an');

INSERT INTO PhieuNhap (MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai) VALUES
('PN001', 'NCC001', '2026-03-10 09:00:00', 'Nguyen Giao 1', 150000, 'DA_NHAP'),
('PN002', 'NCC002', '2026-03-10 10:00:00', 'Nguyen Giao 2', 198000, 'DA_NHAP'),
('PN003', 'NCC003', '2026-03-11 09:00:00', 'Nguyen Giao 3', 162000, 'DA_NHAP'),
('PN004', 'NCC004', '2026-03-11 10:00:00', 'Nguyen Giao 4', 225000, 'DA_NHAP'),
('PN005', 'NCC005', '2026-03-12 09:00:00', 'Nguyen Giao 5', 120000, 'DA_DUYET'),
('PN006', 'NCC006', '2026-03-12 10:00:00', 'Nguyen Giao 6', 168000, 'CHO_DUYET'),
('PN007', 'NCC007', '2026-03-13 09:00:00', 'Nguyen Giao 7', 176000, 'DA_DUYET'),
('PN008', 'NCC008', '2026-03-13 10:00:00', 'Nguyen Giao 8', 160000, 'CHO_DUYET'),
('PN009', 'NCC009', '2026-03-14 09:00:00', 'Nguyen Giao 9', 207000, 'DA_NHAP'),
('PN010', 'NCC010', '2026-03-14 10:00:00', 'Nguyen Giao 10', 180000, 'DA_DUYET');

INSERT INTO ChiTietPhieuNhap (MaCTPN, MaPhieuNhap, MaThuoc, SoLuongNhap, DonGiaNhap, HanSuDung) VALUES
('CTPN001', 'PN001', 'T001', 100, 1500, '2028-01-01'),
('CTPN002', 'PN002', 'T002', 90, 2200, '2028-02-01'),
('CTPN003', 'PN003', 'T003', 120, 1350, '2028-03-01'),
('CTPN004', 'PN004', 'T004', 125, 1800, '2028-04-01'),
('CTPN005', 'PN005', 'T005', 150, 800, '2028-05-01'),
('CTPN006', 'PN006', 'T006', 80, 2100, '2028-06-01'),
('CTPN007', 'PN007', 'T007', 110, 1600, '2028-07-01'),
('CTPN008', 'PN008', 'T008', 50, 3200, '2028-08-01'),
('CTPN009', 'PN009', 'T009', 138, 1500, '2028-09-01'),
('CTPN010', 'PN010', 'T010', 100, 1800, '2028-10-01');

INSERT INTO HoaDonThuoc (
  MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu,
  TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc,
  TenBenhNhan, SdtBenhNhan, Active
) VALUES
('HDT001', 'DT001', '2026-03-20 08:20:00', 25000, 'Thanh toan tai quay', 'DA_THANH_TOAN', '2026-03-20 08:25:00', 'DA_HOAN_THANH', 'Pham Gia Bao', '0902000001', 1),
('HDT002', 'DT002', '2026-03-20 14:20:00', 42000, 'Thanh toan chuyen khoan', 'DA_THANH_TOAN', '2026-03-20 14:25:00', 'DA_HOAN_THANH', 'Le Thi My', '0902000002', 1),
('HDT003', 'DT003', '2026-03-21 08:50:00', 26000, 'Uu tien tre em', 'DA_THANH_TOAN', '2026-03-21 08:55:00', 'DA_HOAN_THANH', 'Tran Minh Khoa', '0902000003', 1),
('HDT004', 'DT004', '2026-03-21 14:50:00', 49000, 'Theo doi tai nha', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Nguyen Kim Anh', '0902000004', 1),
('HDT005', 'DT005', '2026-03-22 09:20:00', 28000, 'Ban theo don', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Bui Hong Son', '0902000005', 1),
('HDT006', 'DT006', '2026-03-22 15:20:00', 48000, 'Da doi lieu', 'DA_THANH_TOAN', '2026-03-22 15:25:00', 'DA_HOAN_THANH', 'Vo Minh Trang', '0902000006', 1),
('HDT007', 'DT007', '2026-03-23 08:20:00', 30000, 'Theo doi tim mach', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Do Thanh Dat', '0902000007', 1),
('HDT008', 'DT008', '2026-03-23 14:20:00', 33000, 'Huong dan su dung', 'DA_THANH_TOAN', '2026-03-23 14:25:00', 'DA_HOAN_THANH', 'Hoang Thu Ha', '0902000008', 1),
('HDT009', 'DT009', '2026-03-24 08:35:00', 22000, 'Ban bo sung vitamin', 'CHUA_THANH_TOAN', NULL, 'CHO_LAY', 'Phan Quoc Nam', '0902000009', 1),
('HDT010', 'DT010', '2026-03-24 14:35:00', 25200, 'Thuoc dieu tri rang', 'DA_THANH_TOAN', '2026-03-24 14:40:00', 'DA_HOAN_THANH', 'Nguyen Bao Chau', '0902000010', 1);

INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) VALUES
('CTHD001', 'HDT001', 'T001', 10, 2500, 25000, 'Theo don', 1),
('CTHD002', 'HDT002', 'T004', 14, 3000, 42000, 'Theo don', 1),
('CTHD003', 'HDT003', 'T007', 10, 2600, 26000, 'Theo don', 1),
('CTHD004', 'HDT004', 'T002', 14, 3500, 49000, 'Theo don', 1),
('CTHD005', 'HDT005', 'T006', 10, 2800, 28000, 'Theo don', 1),
('CTHD006', 'HDT006', 'T008', 10, 4800, 48000, 'Theo don', 1),
('CTHD007', 'HDT007', 'T005', 20, 1500, 30000, 'Theo don', 1),
('CTHD008', 'HDT008', 'T010', 10, 3300, 33000, 'Theo don', 1),
('CTHD009', 'HDT009', 'T003', 10, 2200, 22000, 'Theo don', 1),
('CTHD010', 'HDT010', 'T009', 12, 2100, 25200, 'Theo don', 1);

INSERT INTO HoaDonKham (MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai) VALUES
('HDK001', 'HS001', 'GOI001', '2026-03-20 08:22:00', 150000, 'TIEN_MAT', 'DA_THANH_TOAN'),
('HDK002', 'HS002', 'GOI002', '2026-03-20 14:22:00', 180000, 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
('HDK003', 'HS003', 'GOI003', '2026-03-21 08:52:00', 160000, 'TIEN_MAT', 'DA_THANH_TOAN'),
('HDK004', 'HS004', 'GOI004', '2026-03-21 14:52:00', 170000, 'THE', 'DA_THANH_TOAN'),
('HDK005', 'HS005', 'GOI005', NULL, 190000, 'TIEN_MAT', 'CHO_THANH_TOAN'),
('HDK006', 'HS006', 'GOI006', '2026-03-22 15:22:00', 220000, 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
('HDK007', 'HS007', 'GOI007', '2026-03-23 08:22:00', 250000, 'TIEN_MAT', 'DA_THANH_TOAN'),
('HDK008', 'HS008', 'GOI008', NULL, 230000, 'THE', 'CHO_THANH_TOAN'),
('HDK009', 'HS009', 'GOI009', '2026-03-24 08:37:00', 200000, 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
('HDK010', 'HS010', 'GOI010', NULL, 180000, 'TIEN_MAT', 'CHO_THANH_TOAN');

-- Done
SELECT 'reset_reseed_10_12 completed' AS message;
