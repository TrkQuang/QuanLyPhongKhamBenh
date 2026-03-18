SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS PhongKham;
CREATE DATABASE PhongKham CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE PhongKham;

-- =========================
-- RBAC + USERS
-- =========================
CREATE TABLE Users (
  UserID VARCHAR(20) PRIMARY KEY,
  Username VARCHAR(50) NOT NULL UNIQUE,
  Password VARCHAR(255) NOT NULL,
  Email VARCHAR(120),
  RoleID INT,
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Roles (
  STT INT PRIMARY KEY,
  TenVaiTro VARCHAR(60) NOT NULL UNIQUE,
  MoTa VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Users
  ADD CONSTRAINT fk_users_role FOREIGN KEY (RoleID) REFERENCES Roles(STT);

CREATE TABLE Permissions (
  MaPermission INT AUTO_INCREMENT PRIMARY KEY,
  TenPermission VARCHAR(100) NOT NULL UNIQUE,
  MoTa VARCHAR(255),
  Active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE RolePermissions (
  MaRolePermissions INT AUTO_INCREMENT PRIMARY KEY,
  MaRole INT NOT NULL,
  MaPermission INT NOT NULL,
  Active TINYINT(1) NOT NULL DEFAULT 1,
  UNIQUE KEY uq_role_permission (MaRole, MaPermission),
  CONSTRAINT fk_rolepermissions_role FOREIGN KEY (MaRole) REFERENCES Roles(STT),
  CONSTRAINT fk_rolepermissions_permission FOREIGN KEY (MaPermission) REFERENCES Permissions(MaPermission)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- KHOA + BACSI
-- =========================
CREATE TABLE Khoa (
  MaKhoa VARCHAR(20) PRIMARY KEY,
  TenKhoa VARCHAR(120) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE BacSi (
  MaBacSi VARCHAR(20) PRIMARY KEY,
  HoTen VARCHAR(120) NOT NULL,
  ChuyenKhoa VARCHAR(120),
  SoDienThoai VARCHAR(20),
  Email VARCHAR(120),
  MaKhoa VARCHAR(20) NOT NULL,
  CONSTRAINT fk_bacsi_khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- GOI DICH VU + LICH
-- =========================
CREATE TABLE GoiDichVu (
  MaGoi VARCHAR(20) PRIMARY KEY,
  TenGoi VARCHAR(120) NOT NULL,
  GiaDichVu DECIMAL(18,2) NOT NULL,
  ThoiGianKham INT,
  MoTa TEXT,
  MaKhoa VARCHAR(20) NOT NULL,
  CONSTRAINT fk_goidichvu_khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE LichLamViec (
  MaLichLam VARCHAR(20) PRIMARY KEY,
  MaBacSi VARCHAR(20) NOT NULL,
  NgayLam DATE NOT NULL,
  CaLam VARCHAR(30) NOT NULL,
  TrangThai VARCHAR(30) NOT NULL,
  CONSTRAINT fk_lichlamviec_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE LichKham (
  MaLichKham VARCHAR(20) PRIMARY KEY,
  MaGoi VARCHAR(20),
  MaBacSi VARCHAR(20) NOT NULL,
  ThoiGianBatDau DATETIME NOT NULL,
  ThoiGianKetThuc DATETIME,
  TrangThai VARCHAR(40),
  MaDinhDanhTam VARCHAR(40),
  CONSTRAINT fk_lichkham_goi FOREIGN KEY (MaGoi) REFERENCES GoiDichVu(MaGoi),
  CONSTRAINT fk_lichkham_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- HOSO + DONTHUOC
-- =========================
CREATE TABLE HoSoBenhAn (
  MaHoSo VARCHAR(20) PRIMARY KEY,
  MaLichKham VARCHAR(20),
  HoTen VARCHAR(120) NOT NULL,
  SoDienThoai VARCHAR(20),
  CCCD VARCHAR(20),
  NgaySinh DATE,
  GioiTinh VARCHAR(10),
  DiaChi VARCHAR(255),
  NgayKham DATETIME,
  TrieuChung TEXT,
  ChanDoan TEXT,
  KetLuan TEXT,
  LoiDan TEXT,
  MaBacSi VARCHAR(20),
  TrangThai VARCHAR(30),
  CONSTRAINT fk_hoso_lichkham FOREIGN KEY (MaLichKham) REFERENCES LichKham(MaLichKham),
  CONSTRAINT fk_hoso_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE DonThuoc (
  MaDonThuoc VARCHAR(20) PRIMARY KEY,
  MaHoSo VARCHAR(20) NOT NULL,
  NgayKeDon DATETIME,
  GhiChu TEXT,
  CONSTRAINT fk_donthuoc_hoso FOREIGN KEY (MaHoSo) REFERENCES HoSoBenhAn(MaHoSo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- THUOC + TON KHO
-- =========================
CREATE TABLE Thuoc (
  MaThuoc VARCHAR(20) PRIMARY KEY,
  TenThuoc VARCHAR(120) NOT NULL,
  HoatChat VARCHAR(120),
  DonViTinh VARCHAR(30),
  DonVi VARCHAR(30),
  DonGiaBan DECIMAL(18,2) NOT NULL,
  SoLuongTon INT NOT NULL DEFAULT 0,
  Active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CTDonThuoc (
  MaCTDonThuoc VARCHAR(20) PRIMARY KEY,
  MaDonThuoc VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLuong INT NOT NULL,
  LieuDung VARCHAR(120),
  CachDung VARCHAR(255),
  CONSTRAINT fk_ctdonthuoc_donthuoc FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc(MaDonThuoc),
  CONSTRAINT fk_ctdonthuoc_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- NHA CUNG CAP + PHIEU NHAP
-- =========================
CREATE TABLE NhaCungCap (
  MaNhaCungCap VARCHAR(20) PRIMARY KEY,
  TenNhaCungCap VARCHAR(120) NOT NULL,
  DiaChi VARCHAR(255),
  SDT VARCHAR(20),
  Active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PhieuNhap (
  MaPhieuNhap VARCHAR(20) PRIMARY KEY,
  MaNCC VARCHAR(20) NOT NULL,
  NgayNhap DATETIME NOT NULL,
  NguoiGiao VARCHAR(120),
  TongTienNhap DECIMAL(18,2) NOT NULL DEFAULT 0,
  TrangThai VARCHAR(30) NOT NULL,
  CONSTRAINT fk_phieunhap_ncc FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNhaCungCap)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ChiTietPhieuNhap (
  MaCTPN VARCHAR(20) PRIMARY KEY,
  MaPhieuNhap VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLuongNhap INT NOT NULL,
  DonGiaNhap DECIMAL(18,2) NOT NULL,
  HanSuDung DATE,
  CONSTRAINT fk_ctpn_phieunhap FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
  CONSTRAINT fk_ctpn_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- HOA DON THUOC
-- =========================
CREATE TABLE HoaDonThuoc (
  MaHoaDon VARCHAR(20) PRIMARY KEY,
  MaDonThuoc VARCHAR(20),
  MaNCC VARCHAR(20),
  NgayLap DATETIME NOT NULL,
  TongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
  GhiChu TEXT,
  TrangThaiThanhToan VARCHAR(30) NOT NULL,
  NgayThanhToan DATETIME,
  TrangThaiLayThuoc VARCHAR(30),
  TenBenhNhan VARCHAR(120),
  SdtBenhNhan VARCHAR(20),
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_hdthuoc_donthuoc FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc(MaDonThuoc),
  CONSTRAINT fk_hdthuoc_ncc FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNhaCungCap)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CTHDThuoc (
  MaCTHDThuoc VARCHAR(20) PRIMARY KEY,
  MaHoaDon VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLuong INT NOT NULL,
  DonGia DECIMAL(18,2) NOT NULL,
  ThanhTien DECIMAL(18,2) NOT NULL,
  GhiChu VARCHAR(255),
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_cthd_hdthuoc FOREIGN KEY (MaHoaDon) REFERENCES HoaDonThuoc(MaHoaDon),
  CONSTRAINT fk_cthd_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- HOA DON KHAM
-- =========================
CREATE TABLE HoaDonKham (
  MaHDKham VARCHAR(20) PRIMARY KEY,
  MaHoSo VARCHAR(20) NOT NULL,
  MaGoi VARCHAR(20),
  NgayThanhToan DATETIME,
  TongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
  HinhThucThanhToan VARCHAR(30),
  TrangThai VARCHAR(30) NOT NULL,
  CONSTRAINT fk_hdk_hoso FOREIGN KEY (MaHoSo) REFERENCES HoSoBenhAn(MaHoSo),
  CONSTRAINT fk_hdk_goi FOREIGN KEY (MaGoi) REFERENCES GoiDichVu(MaGoi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- INDEX TOI UU TRUY VAN
-- =========================
CREATE INDEX idx_users_username_active ON Users (Username, Active);
CREATE INDEX idx_users_role ON Users (RoleID);
CREATE INDEX idx_lichkham_bacsi_time ON LichKham (MaBacSi, ThoiGianBatDau, ThoiGianKetThuc);
CREATE INDEX idx_lichkham_status ON LichKham (TrangThai);
CREATE INDEX idx_lichlamviec_bacsi_ngay_ca ON LichLamViec (MaBacSi, NgayLam, CaLam);
CREATE INDEX idx_hosobenhan_phone ON HoSoBenhAn (SoDienThoai);
CREATE INDEX idx_hosobenhan_cccd ON HoSoBenhAn (CCCD);
CREATE INDEX idx_thuoc_active_ten ON Thuoc (Active, TenThuoc);
CREATE INDEX idx_goidv_khoa ON GoiDichVu (MaKhoa);
CREATE INDEX idx_phieunhap_trangthai_ngay ON PhieuNhap (TrangThai, NgayNhap);
CREATE INDEX idx_hdthuoc_ngay_status ON HoaDonThuoc (NgayLap, TrangThaiThanhToan, Active);
CREATE INDEX idx_hdthuoc_pickup_status ON HoaDonThuoc (TrangThaiLayThuoc);
CREATE INDEX idx_hdthuoc_ncc ON HoaDonThuoc (MaNCC);
CREATE INDEX idx_hdkham_ngay_status ON HoaDonKham (NgayThanhToan, TrangThai);

-- =========================
-- SEED DATA
-- =========================

INSERT INTO Roles (STT, TenVaiTro, MoTa) VALUES
(1, 'ADMIN', 'Quan tri he thong'),
(2, 'BACSI', 'Bac si kham benh'),
(3, 'NHATHUOC', 'Nhan vien nha thuoc'),
(4, 'GUEST', 'Khach chua dang nhap');

INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('DASHBOARD_VIEW', 'Xem dashboard', 1),
('KHOA_VIEW', 'Xem va quan ly khoa', 1),
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
('PHANQUYEN_VIEW', 'Quan ly phan quyen', 1),
('USER_MANAGE', 'Quan ly tai khoan', 1),
('ROLE_PERMISSION_MANAGE', 'Quan ly vai tro va quyen', 1),
('BACSI_MANAGE', 'Quan ly bac si', 1),
('BACSI_PROFILE_VIEW', 'Xem ho so bac si', 1),
('LICHLAMVIEC_VIEW', 'Xem lich lam viec', 1),
('LICHLAMVIEC_APPROVE', 'Duyet lich lam viec bac si', 1),
('LICHKHAM_VIEW', 'Xem lich kham', 1),
('LICHKHAM_MANAGE', 'Quan ly lich kham', 1),
('KHAMBENH_CREATE', 'Thuc hien kham benh', 1),
('HOSO_MANAGE', 'Quan ly ho so benh an', 1),
('DONTHUOC_MANAGE', 'Quan ly don thuoc', 1),
('GUEST_BOOKING', 'Dat lich kham guest', 1),
('GUEST_BUY_THUOC', 'Mua thuoc guest', 1);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, MaPermission, 1 FROM Permissions;

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 2, MaPermission, 1 FROM Permissions
WHERE TenPermission IN (
  'GOIDICHVU_VIEW',
  'LICHLAMVIEC_VIEW',
  'LICHKHAM_VIEW',
  'LICHKHAM_MANAGE',
  'KHAMBENH_CREATE',
  'HOSO_MANAGE',
  'DONTHUOC_MANAGE',
  'BACSI_PROFILE_VIEW',
  'HOADONKHAM_VIEW'
);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 3, MaPermission, 1 FROM Permissions
WHERE TenPermission IN (
  'THUOC_VIEW',
  'THUOC_MANAGE',
  'GOIDICHVU_VIEW',
  'NCC_VIEW',
  'NCC_MANAGE',
  'PHIEUNHAP_VIEW',
  'PHIEUNHAP_MANAGE',
  'HOADONTHUOC_VIEW',
  'HOADONTHUOC_CREATE',
  'HOADONTHUOC_MANAGE'
);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 4, MaPermission, 1 FROM Permissions
WHERE TenPermission IN ('GUEST_BOOKING', 'GUEST_BUY_THUOC');

INSERT INTO Users (UserID, Username, Password, Email, RoleID, Active) VALUES
('U001', 'admin', 'admin123', 'admin@phongkham.vn', 1, 1),
('U002', 'bacsi01', '123456', 'bacsi01@phongkham.vn', 2, 1),
('U003', 'nhathuoc01', '123456', 'nhathuoc01@phongkham.vn', 3, 1),
('U004', 'bacsi02', '123456', 'bacsi02@phongkham.vn', 2, 1);

INSERT INTO Khoa (MaKhoa, TenKhoa) VALUES
('K01', 'Noi tong quat'),
('K02', 'Nhi khoa'),
('K03', 'Tai mui hong'),
('K04', 'Da lieu');

INSERT INTO BacSi (MaBacSi, HoTen, ChuyenKhoa, SoDienThoai, Email, MaKhoa) VALUES
('BS01', 'Nguyen Van Minh', 'Noi tong quat', '0901000001', 'bacsi01@phongkham.vn', 'K01'),
('BS02', 'Tran Thi Lan', 'Nhi khoa', '0901000002', 'bacsi02@phongkham.vn', 'K02'),
('BS03', 'Le Hoang Nam', 'Tai mui hong', '0901000003', 'bacsi03@phongkham.vn', 'K03');

INSERT INTO GoiDichVu (MaGoi, TenGoi, GiaDichVu, ThoiGianKham, MoTa, MaKhoa) VALUES
('DV01', 'Kham tong quat co ban', 150000, 20, 'Kham tong quat co ban', 'K01'),
('DV02', 'Kham nhi', 180000, 25, 'Kham danh cho tre em', 'K02'),
('DV03', 'Kham tai mui hong', 200000, 30, 'Kham chuyen khoa TMH', 'K03');

INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonVi, DonGiaBan, SoLuongTon, Active) VALUES
('T10', 'Paracetamol 500mg', 'Paracetamol', 'Vien', 'Vien', 1500, 500, 1),
('T11', 'Amoxicillin 500mg', 'Amoxicillin', 'Vien', 'Vien', 3500, 320, 1),
('T12', 'Vitamin C 1000mg', 'Ascorbic Acid', 'Vien', 'Vien', 2000, 280, 1),
('T13', 'Oresol', 'Glucose + Dien giai', 'Goi', 'Goi', 4500, 150, 1),
('T14', 'Siro ho Prospan', 'La thuong xuan', 'Chai', 'Chai', 75000, 80, 1),
('T15', 'Natri Clorid 0.9%', 'NaCl', 'Chai', 'Chai', 12000, 120, 1);

INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SDT, Active) VALUES
('NCC01', 'Duoc pham An Khang', 'Quan 1, TP.HCM', '02811112222', 1),
('NCC02', 'Duoc pham Minh Chau', 'Quan 3, TP.HCM', '02833334444', 1),
('NCC03', 'Duoc pham Viet Duc', 'Thu Duc, TP.HCM', '02855556666', 0);

INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam, TrangThai) VALUES
('LLV001', 'BS01', '2026-03-17', 'Sang', 'DA_DUYET'),
('LLV002', 'BS01', '2026-03-17', 'Chieu', 'DA_DUYET'),
('LLV003', 'BS02', '2026-03-17', 'Sang', 'CHO_DUYET'),
('LLV004', 'BS02', '2026-03-18', 'Toi', 'DA_DUYET'),
('LLV005', 'BS03', '2026-03-18', 'Chieu', 'TU_CHOI');

INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, ThoiGianKetThuc, TrangThai, MaDinhDanhTam) VALUES
('LK001', 'DV01', 'BS01', '2026-03-17 08:00:00', '2026-03-17 08:20:00', 'CHO_XAC_NHAN', 'GUEST001'),
('LK002', 'DV01', 'BS01', '2026-03-17 08:30:00', '2026-03-17 08:50:00', 'DA_XAC_NHAN', 'GUEST002'),
('LK003', 'DV02', 'BS02', '2026-03-18 18:30:00', '2026-03-18 18:55:00', 'CHO_XAC_NHAN', 'GUEST003');

INSERT INTO HoSoBenhAn (MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi, NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai) VALUES
('HS001', 'LK002', 'Pham Thi Hoa', '0912345678', '079204000111', '1992-07-12', 'NU', 'Go Vap, TP.HCM', '2026-03-17 08:35:00', 'Ho, sot nhe', 'Viem hong cap', 'Dieu tri ngoai tru', 'Uong nhieu nuoc, tai kham sau 3 ngay', 'BS01', 'DA_KHAM'),
('HS002', 'LK003', 'Nguyen Van Nam', '0909123456', '079204000222', '1988-03-05', 'NAM', 'Binh Thanh, TP.HCM', NULL, NULL, NULL, NULL, NULL, NULL, 'CHO_KHAM');

INSERT INTO DonThuoc (MaDonThuoc, MaHoSo, NgayKeDon, GhiChu) VALUES
('DT001', 'HS001', '2026-03-17 08:45:00', 'Dung thuoc sau an'),
('DT002', 'HS001', '2026-03-17 08:46:00', 'Bo sung vitamin');

INSERT INTO CTDonThuoc (MaCTDonThuoc, MaDonThuoc, MaThuoc, SoLuong, LieuDung, CachDung) VALUES
('CTDT001', 'DT001', 'T10', 10, '2 vien/ngay', 'Sang 1 vien, toi 1 vien'),
('CTDT002', 'DT001', 'T11', 14, '2 vien/ngay', 'Sau an sang va toi'),
('CTDT003', 'DT002', 'T12', 20, '1 vien/ngay', 'Uong sau an trua');

INSERT INTO PhieuNhap (MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai) VALUES
('PN001', 'NCC01', '2026-03-15 09:00:00', 'Le Van Giao', 8500000, 'DA_DUYET'),
('PN002', 'NCC02', '2026-03-16 10:00:00', 'Tran Quoc Dat', 6200000, 'CHO_DUYET');

INSERT INTO ChiTietPhieuNhap (MaCTPN, MaPhieuNhap, MaThuoc, SoLuongNhap, DonGiaNhap, HanSuDung) VALUES
('CTPN001', 'PN001', 'T10', 300, 1000, '2027-12-31'),
('CTPN002', 'PN001', 'T11', 200, 2500, '2027-10-31'),
('CTPN003', 'PN001', 'T14', 50, 62000, '2027-08-30'),
('CTPN004', 'PN002', 'T12', 150, 1400, '2027-11-15');

INSERT INTO HoaDonThuoc (MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu, TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc, TenBenhNhan, SdtBenhNhan, Active) VALUES
('HDT001', 'DT001', '2026-03-17 09:00:00', 64000, 'Ban theo don thuoc', 'Đã thanh toán', '2026-03-17 09:02:00', 'ĐÃ HOÀN THÀNH', 'Pham Thi Hoa', '0912345678', 1),
('HDT002', NULL, '2026-03-17 11:20:00', 9000, 'Khach mua le', 'Chưa thanh toán', NULL, 'ĐANG CHỜ LẤY', 'Le Thi Mai', '0909333444', 1);

INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active) VALUES
('CTHD001', 'HDT001', 'T10', 10, 1500, 15000, 'Theo don DT001', 1),
('CTHD002', 'HDT001', 'T11', 14, 3500, 49000, 'Theo don DT001', 1),
('CTHD003', 'HDT002', 'T13', 2, 4500, 9000, 'Khach mua le', 1);

INSERT INTO HoaDonKham (MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai) VALUES
('HDK001', 'HS001', 'DV01', '2026-03-17 08:50:00', 150000, 'CASH', 'DA_THANH_TOAN'),
('HDK002', 'HS002', 'DV02', NULL, 180000, 'BANK_TRANSFER', 'CHO_THANH_TOAN');

SET FOREIGN_KEY_CHECKS = 1;
