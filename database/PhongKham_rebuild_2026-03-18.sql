SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS PhongKham;
CREATE DATABASE PhongKham CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE PhongKham;

-- =========================
-- RBAC + USERS
-- =========================
CREATE TABLE Roles (
  STT INT PRIMARY KEY,
  TenVaiTro VARCHAR(60) NOT NULL UNIQUE,
  MoTa VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Users (
  UserID VARCHAR(20) PRIMARY KEY,
  Username VARCHAR(50) NOT NULL UNIQUE,
  Password VARCHAR(255) NOT NULL,
  Email VARCHAR(120),
  RoleID INT,
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_users_role FOREIGN KEY (RoleID) REFERENCES Roles(STT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
-- MASTER DATA
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
  Email VARCHAR(120) UNIQUE,
  MaKhoa VARCHAR(20) NOT NULL,
  CONSTRAINT fk_bacsi_khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE GoiDichVu (
  MaGoi VARCHAR(20) PRIMARY KEY,
  TenGoi VARCHAR(120) NOT NULL,
  GiaDichVu DECIMAL(18,2) NOT NULL,
  ThoiGianKham INT,
  MoTa TEXT,
  MaKhoa VARCHAR(20) NOT NULL,
  CONSTRAINT fk_goidichvu_khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Thuoc (
  MaThuoc VARCHAR(20) PRIMARY KEY,
  TenThuoc VARCHAR(120) NOT NULL,
  HoatChat VARCHAR(120),
  DonViTinh VARCHAR(30),
  DonVi VARCHAR(30),
  DonGiaBan DECIMAL(18,2) NOT NULL,
  SoLuongTon INT NOT NULL DEFAULT 0,
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT chk_thuoc_soluong CHECK (SoLuongTon >= 0),
  CONSTRAINT chk_thuoc_dongia CHECK (DonGiaBan >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE NhaCungCap (
  MaNhaCungCap VARCHAR(20) PRIMARY KEY,
  TenNhaCungCap VARCHAR(120) NOT NULL,
  DiaChi VARCHAR(255),
  SDT VARCHAR(20),
  Active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- SCHEDULE + MEDICAL RECORDS
-- =========================
CREATE TABLE LichLamViec (
  MaLichLam VARCHAR(20) PRIMARY KEY,
  MaBacSi VARCHAR(20) NOT NULL,
  NgayLam DATE NOT NULL,
  CaLam VARCHAR(30) NOT NULL,
  TrangThai VARCHAR(30) NOT NULL,
  CONSTRAINT fk_lichlamviec_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi),
  CONSTRAINT chk_lichlamviec_ca CHECK (CaLam IN ('Sang', 'Chieu', 'Toi')),
  CONSTRAINT chk_lichlamviec_trangthai CHECK (TrangThai IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI')),
  UNIQUE KEY uq_lichlamviec_bacsi_ngay_ca (MaBacSi, NgayLam, CaLam)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE LichKham (
  MaLichKham VARCHAR(20) PRIMARY KEY,
  MaGoi VARCHAR(20) NOT NULL,
  MaBacSi VARCHAR(20) NOT NULL,
  ThoiGianBatDau DATETIME NOT NULL,
  ThoiGianKetThuc DATETIME NOT NULL,
  TrangThai VARCHAR(40) NOT NULL DEFAULT 'CHO_XAC_NHAN',
  MaDinhDanhTam VARCHAR(40),
  CONSTRAINT fk_lichkham_goi FOREIGN KEY (MaGoi) REFERENCES GoiDichVu(MaGoi),
  CONSTRAINT fk_lichkham_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi),
  CONSTRAINT chk_lichkham_trangthai CHECK (TrangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DANG_KHAM', 'HOAN_THANH', 'DA_HUY')),
  CONSTRAINT chk_lichkham_time CHECK (ThoiGianKetThuc > ThoiGianBatDau)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
  TrangThai VARCHAR(30) NOT NULL DEFAULT 'CHO_KHAM',
  CONSTRAINT fk_hoso_lichkham FOREIGN KEY (MaLichKham) REFERENCES LichKham(MaLichKham),
  CONSTRAINT fk_hoso_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi),
  CONSTRAINT chk_hoso_trangthai CHECK (TrangThai IN ('CHO_KHAM', 'DA_KHAM', 'HUY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE DonThuoc (
  MaDonThuoc VARCHAR(20) PRIMARY KEY,
  MaHoSo VARCHAR(20) NOT NULL,
  NgayKeDon DATETIME,
  GhiChu TEXT,
  CONSTRAINT fk_donthuoc_hoso FOREIGN KEY (MaHoSo) REFERENCES HoSoBenhAn(MaHoSo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CTDonThuoc (
  MaCTDonThuoc VARCHAR(20) PRIMARY KEY,
  MaDonThuoc VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLuong INT NOT NULL,
  LieuDung VARCHAR(120),
  CachDung VARCHAR(255),
  CONSTRAINT fk_ctdonthuoc_donthuoc FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc(MaDonThuoc),
  CONSTRAINT fk_ctdonthuoc_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc),
  CONSTRAINT chk_ctdonthuoc_soluong CHECK (SoLuong > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- PURCHASE + WAREHOUSE
-- =========================
CREATE TABLE PhieuNhap (
  MaPhieuNhap VARCHAR(20) PRIMARY KEY,
  MaNCC VARCHAR(20) NOT NULL,
  NgayNhap DATETIME NOT NULL,
  NguoiGiao VARCHAR(120),
  TongTienNhap DECIMAL(18,2) NOT NULL DEFAULT 0,
  TrangThai VARCHAR(30) NOT NULL DEFAULT 'CHO_DUYET',
  CONSTRAINT fk_phieunhap_ncc FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNhaCungCap),
  CONSTRAINT chk_phieunhap_trangthai CHECK (TrangThai IN ('CHO_DUYET', 'DA_DUYET', 'DA_NHAP', 'DA_HUY')),
  CONSTRAINT chk_phieunhap_tongtien CHECK (TongTienNhap >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ChiTietPhieuNhap (
  MaCTPN VARCHAR(20) PRIMARY KEY,
  MaPhieuNhap VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLo VARCHAR(50) NOT NULL,
  SoLuongNhap INT NOT NULL,
  SoLuongConLai INT NOT NULL,
  DonGiaNhap DECIMAL(18,2) NOT NULL,
  HanSuDung DATE,
  CONSTRAINT fk_ctpn_phieunhap FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
  CONSTRAINT fk_ctpn_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc),
  CONSTRAINT chk_ctpn_soluong CHECK (SoLuongNhap > 0),
  CONSTRAINT chk_ctpn_soluong_con_lai CHECK (SoLuongConLai >= 0 AND SoLuongConLai <= SoLuongNhap),
  CONSTRAINT chk_ctpn_dongia CHECK (DonGiaNhap > 0),
  UNIQUE KEY uq_ctpn_lot (MaPhieuNhap, MaThuoc, SoLo, HanSuDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE HoaDonThuoc (
  MaHoaDon VARCHAR(20) PRIMARY KEY,
  MaDonThuoc VARCHAR(20),
  NgayLap DATETIME NOT NULL,
  TongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
  GhiChu TEXT,
  TrangThaiThanhToan VARCHAR(30) NOT NULL DEFAULT 'CHUA_THANH_TOAN',
  NgayThanhToan DATETIME,
  TrangThaiLayThuoc VARCHAR(30) NOT NULL DEFAULT 'CHO_LAY',
  TenBenhNhan VARCHAR(120),
  SdtBenhNhan VARCHAR(20),
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_hdthuoc_donthuoc FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc(MaDonThuoc),
  CONSTRAINT chk_hdthuoc_trangthai_tt CHECK (TrangThaiThanhToan IN ('CHUA_THANH_TOAN', 'DA_THANH_TOAN', 'HOAN_HOA_DON')),
  CONSTRAINT chk_hdthuoc_trangthai_lay CHECK (TrangThaiLayThuoc IN ('CHO_LAY', 'DA_HOAN_THANH', 'DA_HUY')),
  CONSTRAINT chk_hdthuoc_tongtien CHECK (TongTien >= 0)
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
  CONSTRAINT fk_cthd_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc),
  CONSTRAINT chk_cthd_soluong CHECK (SoLuong > 0),
  CONSTRAINT chk_cthd_dongia CHECK (DonGia >= 0),
  CONSTRAINT chk_cthd_thanhtien CHECK (ThanhTien >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE XuatThuocTheoLo (
  MaXuatLo BIGINT PRIMARY KEY AUTO_INCREMENT,
  MaHoaDon VARCHAR(20) NOT NULL,
  MaCTHDThuoc VARCHAR(20) NOT NULL,
  MaCTPN VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLo VARCHAR(50),
  HanSuDung DATE,
  SoLuongXuat INT NOT NULL,
  NgayXuat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_xuatlo_hd FOREIGN KEY (MaHoaDon) REFERENCES HoaDonThuoc(MaHoaDon),
  CONSTRAINT fk_xuatlo_cthd FOREIGN KEY (MaCTHDThuoc) REFERENCES CTHDThuoc(MaCTHDThuoc),
  CONSTRAINT fk_xuatlo_ctpn FOREIGN KEY (MaCTPN) REFERENCES ChiTietPhieuNhap(MaCTPN),
  CONSTRAINT chk_xuatlo_soluong CHECK (SoLuongXuat > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE TieuHuyLoThuoc (
  MaTieuHuy BIGINT PRIMARY KEY AUTO_INCREMENT,
  MaCTPN VARCHAR(20) NOT NULL,
  MaPhieuNhap VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLo VARCHAR(50),
  SoLuongTieuHuy INT NOT NULL,
  HanSuDung DATETIME,
  NgayTieuHuy DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  LyDo VARCHAR(255) NOT NULL,
  NguoiThucHien VARCHAR(120) NOT NULL,
  CONSTRAINT fk_tieuhuy_ctpn FOREIGN KEY (MaCTPN) REFERENCES ChiTietPhieuNhap(MaCTPN),
  CONSTRAINT chk_tieuhuy_soluong CHECK (SoLuongTieuHuy > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE HoaDonKham (
  MaHDKham VARCHAR(20) PRIMARY KEY,
  MaHoSo VARCHAR(20) NOT NULL,
  MaGoi VARCHAR(20),
  NgayThanhToan DATETIME,
  TongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
  HinhThucThanhToan VARCHAR(30),
  TrangThai VARCHAR(30) NOT NULL,
  CONSTRAINT fk_hdk_hoso FOREIGN KEY (MaHoSo) REFERENCES HoSoBenhAn(MaHoSo),
  CONSTRAINT fk_hdk_goi FOREIGN KEY (MaGoi) REFERENCES GoiDichVu(MaGoi),
  CONSTRAINT chk_hdkham_trangthai CHECK (TrangThai IN ('CHO_THANH_TOAN', 'DA_THANH_TOAN', 'HOAN_TIEN')),
  CONSTRAINT chk_hdkham_tongtien CHECK (TongTien >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- INDEXES
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
CREATE INDEX idx_ctpn_hsd ON ChiTietPhieuNhap (HanSuDung);
CREATE INDEX idx_ctpn_thuoc_hsd ON ChiTietPhieuNhap (MaThuoc, HanSuDung);
CREATE INDEX idx_ctpn_lot_lookup ON ChiTietPhieuNhap (MaThuoc, SoLo, HanSuDung);
CREATE INDEX idx_hdthuoc_ngay_status ON HoaDonThuoc (NgayLap, TrangThaiThanhToan, Active);
CREATE INDEX idx_hdthuoc_pickup_status ON HoaDonThuoc (TrangThaiLayThuoc);
CREATE INDEX idx_hdkham_ngay_status ON HoaDonKham (NgayThanhToan, TrangThai);

-- View quản lý theo NCC + HSD + lô hàng
CREATE OR REPLACE VIEW vw_ThuocNhapTheoNCC_HSD AS
SELECT
  ctpn.MaCTPN,
  ctpn.MaPhieuNhap,
  pn.MaNCC,
  ncc.TenNhaCungCap,
  ctpn.MaThuoc,
  t.TenThuoc,
  ctpn.SoLo,
  ctpn.HanSuDung,
  ctpn.SoLuongNhap,
  ctpn.DonGiaNhap,
  (ctpn.SoLuongNhap * ctpn.DonGiaNhap) AS ThanhTienNhap,
  pn.NgayNhap,
  pn.TrangThai
FROM ChiTietPhieuNhap ctpn
JOIN PhieuNhap pn ON pn.MaPhieuNhap = ctpn.MaPhieuNhap
JOIN NhaCungCap ncc ON ncc.MaNhaCungCap = pn.MaNCC
JOIN Thuoc t ON t.MaThuoc = ctpn.MaThuoc;

-- =========================
-- TRIGGERS / PROCEDURES
-- =========================
DELIMITER $$

CREATE TRIGGER trg_phieunhap_check_ncc_active_before_insert
BEFORE INSERT ON PhieuNhap
FOR EACH ROW
BEGIN
  DECLARE v_active TINYINT;
  SELECT Active INTO v_active FROM NhaCungCap WHERE MaNhaCungCap = NEW.MaNCC;
  IF v_active IS NULL OR v_active = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Chi duoc tao PhieuNhap voi NhaCungCap dang hop tac';
  END IF;
END$$

CREATE TRIGGER trg_lichkham_validate_before_insert
BEFORE INSERT ON LichKham
FOR EACH ROW
BEGIN
  DECLARE v_khoa_bacsi VARCHAR(20);
  DECLARE v_khoa_goi VARCHAR(20);
  DECLARE v_date DATE;
  DECLARE v_hour INT;
  DECLARE v_ca VARCHAR(10);
  DECLARE v_approved INT;

  SELECT MaKhoa INTO v_khoa_bacsi FROM BacSi WHERE MaBacSi = NEW.MaBacSi;
  SELECT MaKhoa INTO v_khoa_goi FROM GoiDichVu WHERE MaGoi = NEW.MaGoi;

  IF v_khoa_bacsi IS NULL OR v_khoa_goi IS NULL OR v_khoa_bacsi <> v_khoa_goi THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'BacSi khong thuoc Khoa cua GoiDichVu';
  END IF;

  SET v_date = DATE(NEW.ThoiGianBatDau);
  SET v_hour = HOUR(NEW.ThoiGianBatDau);
  SET v_ca = CASE
    WHEN v_hour < 12 THEN 'Sang'
    WHEN v_hour < 17 THEN 'Chieu'
    ELSE 'Toi'
  END;

  SELECT COUNT(*) INTO v_approved
  FROM LichLamViec
  WHERE MaBacSi = NEW.MaBacSi
    AND NgayLam = v_date
    AND CaLam = v_ca
    AND TrangThai = 'DA_DUYET';

  IF v_approved = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'BacSi chua co lich lam viec DA_DUYET trong ca nay';
  END IF;
END$$

CREATE PROCEDURE sp_confirm_phieunhap(IN p_maPhieuNhap VARCHAR(20))
BEGIN
  DECLARE v_status VARCHAR(30);
  DECLARE done INT DEFAULT 0;
  DECLARE v_maThuoc VARCHAR(20);
  DECLARE v_soLuong INT;

  DECLARE cur CURSOR FOR
    SELECT MaThuoc, SoLuongNhap FROM ChiTietPhieuNhap WHERE MaPhieuNhap = p_maPhieuNhap;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  START TRANSACTION;

  SELECT TrangThai INTO v_status FROM PhieuNhap WHERE MaPhieuNhap = p_maPhieuNhap FOR UPDATE;

  IF v_status <> 'DA_DUYET' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Chi xac nhan nhap kho khi phieu o trang thai DA_DUYET';
  END IF;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO v_maThuoc, v_soLuong;
    IF done = 1 THEN
      LEAVE read_loop;
    END IF;

    UPDATE Thuoc
      SET SoLuongTon = SoLuongTon + v_soLuong
      WHERE MaThuoc = v_maThuoc;
  END LOOP;
  CLOSE cur;

  UPDATE PhieuNhap
    SET TrangThai = 'DA_NHAP'
    WHERE MaPhieuNhap = p_maPhieuNhap;

  COMMIT;
END$$

CREATE PROCEDURE sp_complete_hoa_don_thuoc(IN p_maHoaDon VARCHAR(20))
BEGIN
  DECLARE v_tt VARCHAR(30);
  DECLARE v_lay VARCHAR(30);
  DECLARE done INT DEFAULT 0;
  DECLARE v_maThuoc VARCHAR(20);
  DECLARE v_soLuong INT;

  DECLARE cur CURSOR FOR
    SELECT MaThuoc, SoLuong FROM CTHDThuoc WHERE MaHoaDon = p_maHoaDon;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  START TRANSACTION;

  SELECT TrangThaiThanhToan, TrangThaiLayThuoc
    INTO v_tt, v_lay
  FROM HoaDonThuoc
  WHERE MaHoaDon = p_maHoaDon
  FOR UPDATE;

  IF v_tt <> 'DA_THANH_TOAN' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'HoaDonThuoc chua thanh toan';
  END IF;

  IF v_lay <> 'CHO_LAY' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Trang thai lay thuoc khong hop le';
  END IF;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO v_maThuoc, v_soLuong;
    IF done = 1 THEN
      LEAVE read_loop;
    END IF;

    IF (SELECT SoLuongTon FROM Thuoc WHERE MaThuoc = v_maThuoc FOR UPDATE) < v_soLuong THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Khong du ton kho de xuat thuoc';
    END IF;

    UPDATE Thuoc
      SET SoLuongTon = SoLuongTon - v_soLuong
      WHERE MaThuoc = v_maThuoc;
  END LOOP;
  CLOSE cur;

  UPDATE HoaDonThuoc
    SET TrangThaiLayThuoc = 'DA_HOAN_THANH'
    WHERE MaHoaDon = p_maHoaDon;

  COMMIT;
END$$

DELIMITER ;

-- =========================
-- MINIMUM SEED
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
('GUEST_BUY_THUOC', 'Mua thuoc guest', 1),
('GUEST_HISTORY_LOOKUP', 'Tra cuu ho so theo CCCD', 1);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, MaPermission, 1 FROM Permissions;

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 2, MaPermission, 1 FROM Permissions
WHERE TenPermission IN (
  'GOIDICHVU_VIEW', 'LICHLAMVIEC_VIEW', 'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
  'KHAMBENH_CREATE', 'HOSO_MANAGE', 'DONTHUOC_MANAGE', 'BACSI_PROFILE_VIEW', 'HOADONKHAM_VIEW'
);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 3, MaPermission, 1 FROM Permissions
WHERE TenPermission IN (
  'THUOC_VIEW', 'THUOC_MANAGE', 'NCC_VIEW', 'NCC_MANAGE',
  'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE', 'HOADONTHUOC_VIEW',
  'HOADONTHUOC_CREATE', 'HOADONTHUOC_MANAGE'
);

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 4, MaPermission, 1 FROM Permissions
WHERE TenPermission IN ('GUEST_BOOKING', 'GUEST_BUY_THUOC', 'GUEST_HISTORY_LOOKUP');

SET FOREIGN_KEY_CHECKS = 1;
