-- REBUILD ESSENTIAL ONLY
-- Generated: 2026-03-25
-- Includes only required blocks for current app code
-- ============================================================

-- ===================== BEGIN: PhongKham_rebuild_2026-03-18.sql =====================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Portable bootstrap for MySQL 8.0.45+ (including managed services like Aiven).
-- 1) On managed MySQL, create/select database outside this script if needed.
-- 2) Ensure the correct schema is selected before running this file:
--      USE your_database_name;
-- 3) If you run on local MySQL with full privileges, you may uncomment:
--      DROP DATABASE IF EXISTS PhongKham;
--      CREATE DATABASE PhongKham CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--      USE PhongKham;

-- =========================
-- RBAC + USERS
-- =========================
Use PhongKham;
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

CREATE TABLE KhungGioLamViec (
  MaKhungGio INT AUTO_INCREMENT PRIMARY KEY,
  KhungGio VARCHAR(30) NOT NULL UNIQUE,
  MoTa VARCHAR(255),
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT chk_khunggio_format CHECK (
    KhungGio REGEXP '^[0-2][0-9]:[0-5][0-9]-[0-2][0-9]:[0-5][0-9]$'
  )
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
  CONSTRAINT chk_lichlamviec_ca CHECK (
    CaLam IN ('Sang', 'Chieu', 'Toi')
    OR CaLam REGEXP '^[0-2][0-9]:[0-5][0-9]-[0-2][0-9]:[0-5][0-9]$'
  ),
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
DROP TRIGGER IF EXISTS trg_phieunhap_check_ncc_active_before_insert;
DROP TRIGGER IF EXISTS trg_lichkham_validate_before_insert;
DROP PROCEDURE IF EXISTS sp_confirm_phieunhap;
DROP PROCEDURE IF EXISTS sp_complete_hoa_don_thuoc;

DELIMITER $$

CREATE TRIGGER trg_phieunhap_check_ncc_active_before_insert
BEFORE INSERT ON PhieuNhap
FOR EACH ROW
BEGIN
    DECLARE v_active TINYINT;

    SELECT Active
    INTO v_active
    FROM NhaCungCap
    WHERE MaNhaCungCap = NEW.MaNCC;

    IF v_active IS NULL OR v_active = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Chi duoc tao PhieuNhap voi NhaCungCap dang hop tac';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_lichkham_validate_before_insert
BEFORE INSERT ON LichKham
FOR EACH ROW
BEGIN
    DECLARE v_khoa_bacsi VARCHAR(20);
    DECLARE v_khoa_goi VARCHAR(20);
    DECLARE v_approved INT;

    -- Lấy khoa bác sĩ
    SELECT MaKhoa
    INTO v_khoa_bacsi
    FROM BacSi
    WHERE MaBacSi = NEW.MaBacSi;

    -- Lấy khoa gói dịch vụ
    SELECT MaKhoa
    INTO v_khoa_goi
    FROM GoiDichVu
    WHERE MaGoi = NEW.MaGoi;

    -- Check cùng khoa
    IF v_khoa_bacsi IS NULL OR v_khoa_goi IS NULL OR v_khoa_bacsi <> v_khoa_goi THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'BacSi khong thuoc Khoa cua GoiDichVu';
    END IF;

    -- Check có lịch làm việc đã duyệt không
    SELECT COUNT(*)
    INTO v_approved
    FROM LichLamViec
    WHERE MaBacSi = NEW.MaBacSi
      AND NgayLam = DATE(NEW.ThoiGianBatDau)
      AND TrangThai = 'DA_DUYET';

    IF v_approved > 0 THEN
        -- Check đúng khung giờ
        SELECT COUNT(*)
        INTO v_approved
        FROM LichLamViec
        WHERE MaBacSi = NEW.MaBacSi
          AND NgayLam = DATE(NEW.ThoiGianBatDau)
          AND TrangThai = 'DA_DUYET'
          AND (
                (CaLam = 'Sang' 
                    AND TIME(NEW.ThoiGianBatDau) >= '08:00:00' 
                    AND TIME(NEW.ThoiGianKetThuc) <= '12:00:00')

             OR (CaLam = 'Chieu' 
                    AND TIME(NEW.ThoiGianBatDau) >= '13:00:00' 
                    AND TIME(NEW.ThoiGianKetThuc) <= '17:00:00')

             OR (CaLam = 'Toi' 
                    AND TIME(NEW.ThoiGianBatDau) >= '17:00:00' 
                    AND TIME(NEW.ThoiGianKetThuc) <= '21:00:00')

             OR (CaLam REGEXP '^[0-2][0-9]:[0-5][0-9]-[0-2][0-9]:[0-5][0-9]$'
                    AND NEW.ThoiGianBatDau >= STR_TO_DATE(CONCAT(NgayLam, ' ', SUBSTRING_INDEX(CaLam, '-', 1)), '%Y-%m-%d %H:%i')
                    AND NEW.ThoiGianKetThuc <= STR_TO_DATE(CONCAT(NgayLam, ' ', SUBSTRING_INDEX(CaLam, '-', -1)), '%Y-%m-%d %H:%i')
                )
          );
    END IF;

    -- Nếu không hợp lệ
    IF v_approved = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'BacSi chua co lich lam viec DA_DUYET phu hop khung gio nay';
    END IF;

END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_confirm_phieunhap(IN p_maPhieuNhap VARCHAR(20))
BEGIN
    DECLARE v_status VARCHAR(30);
    DECLARE done INT DEFAULT 0;
    DECLARE v_maThuoc VARCHAR(20);
    DECLARE v_soLuong INT;

    DECLARE cur CURSOR FOR
        SELECT MaThuoc, SoLuongNhap
        FROM ChiTietPhieuNhap
        WHERE MaPhieuNhap = p_maPhieuNhap;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    START TRANSACTION;

    -- Lock phiếu nhập
    SELECT TrangThai
    INTO v_status
    FROM PhieuNhap
    WHERE MaPhieuNhap = p_maPhieuNhap
    FOR UPDATE;

    -- Check trạng thái
    IF v_status <> 'DA_DUYET' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Chi xac nhan nhap kho khi phieu o trang thai DA_DUYET';
    END IF;

    -- Duyệt từng thuốc
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

    -- Update trạng thái phiếu
    UPDATE PhieuNhap
    SET TrangThai = 'DA_NHAP'
    WHERE MaPhieuNhap = p_maPhieuNhap;

    COMMIT;

END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_complete_hoa_don_thuoc(IN p_maHoaDon VARCHAR(20))
BEGIN
    DECLARE v_tt VARCHAR(30);
    DECLARE v_lay VARCHAR(30);
    DECLARE done INT DEFAULT 0;
    DECLARE v_maThuoc VARCHAR(20);
    DECLARE v_soLuong INT;

    DECLARE cur CURSOR FOR
        SELECT MaThuoc, SoLuong
        FROM CTHDThuoc
        WHERE MaHoaDon = p_maHoaDon;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;

    -- Lock hóa đơn
    SELECT TrangThaiThanhToan, TrangThaiLayThuoc
    INTO v_tt, v_lay
    FROM HoaDonThuoc
    WHERE MaHoaDon = p_maHoaDon
    FOR UPDATE;

    IF v_tt <> 'DA_THANH_TOAN' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'HoaDonThuoc chua thanh toan';
    END IF;

    IF v_lay <> 'CHO_LAY' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Trang thai lay thuoc khong hop le';
    END IF;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_maThuoc, v_soLuong;

        IF done = 1 THEN
            LEAVE read_loop;
        END IF;

        -- Lock tồn kho từng thuốc
        IF (SELECT SoLuongTon FROM Thuoc 
            WHERE MaThuoc = v_maThuoc 
            FOR UPDATE) < v_soLuong THEN

            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Khong du ton kho de xuat thuoc';
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

INSERT INTO KhungGioLamViec (KhungGio, MoTa, Active) VALUES
('08:00-12:00', 'Ca sang mac dinh', 1),
('13:00-17:00', 'Ca chieu mac dinh', 1),
('17:00-21:00', 'Ca toi mac dinh', 1);

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

-- ====================== END: PhongKham_rebuild_2026-03-18.sql ======================

-- ===================== BEGIN: migrate_users_archive_delete_2026-03-24.sql =====================
-- ============================================================
-- Users archive-delete migration
-- Date: 2026-03-24
--
-- Mục tiêu:
-- 1) Bổ sung trạng thái xóa ẩn vĩnh viễn cho tài khoản
-- 2) Tách biệt với trạng thái vô hiệu hóa (Active=0)
--
-- Quy ước:
-- - Active = 0, IsArchived = 0: tài khoản bị vô hiệu hóa tạm thời (có thể mở lại)
-- - Active = 0, IsArchived = 1: tài khoản đã xóa ẩn vĩnh viễn (không hiển thị, không mở lại)
-- ============================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

SET @has_is_archived := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'Users'
    AND COLUMN_NAME = 'IsArchived'
);
SET @sql := IF(
  @has_is_archived = 0,
  'ALTER TABLE Users ADD COLUMN IsArchived TINYINT(1) NOT NULL DEFAULT 0 AFTER Active',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_deleted_at := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'Users'
    AND COLUMN_NAME = 'DeletedAt'
);
SET @sql := IF(
  @has_deleted_at = 0,
  'ALTER TABLE Users ADD COLUMN DeletedAt DATETIME NULL AFTER IsArchived',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_deleted_reason := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'Users'
    AND COLUMN_NAME = 'DeletedReason'
);
SET @sql := IF(
  @has_deleted_reason = 0,
  'ALTER TABLE Users ADD COLUMN DeletedReason VARCHAR(255) NULL AFTER DeletedAt',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_index_archived := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'Users'
    AND INDEX_NAME = 'idx_users_archived_active'
);
SET @sql := IF(
  @has_index_archived = 0,
  'CREATE INDEX idx_users_archived_active ON Users (IsArchived, Active)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Rà soát nhanh
SELECT
  SUM(CASE WHEN Active = 1 AND IsArchived = 0 THEN 1 ELSE 0 END) AS SoTaiKhoanDangHoatDong,
  SUM(CASE WHEN Active = 0 AND IsArchived = 0 THEN 1 ELSE 0 END) AS SoTaiKhoanVoHieuHoa,
  SUM(CASE WHEN IsArchived = 1 THEN 1 ELSE 0 END) AS SoTaiKhoanDaXoaAn
FROM Users;

-- ====================== END: migrate_users_archive_delete_2026-03-24.sql ======================

-- ===================== BEGIN: migrate_rbac_chitiet_2026-03-24.sql =====================
-- Migration RBAC chi tiet theo module + hanh dong
-- Muc tieu:
-- 1) Tao bo bang moi: Permission, ChiTiet_Permission, Role_Permission
-- 2) Seed quyen hanh dong mac dinh: XEM/THEM/SUA/XOA
-- 3) Migrate du lieu tu RBAC cu (Permissions, RolePermissions)
-- 4) Khong pha vo he thong cu: giu nguyen bang cu de fallback

USE PhongKham;

SET @schema_name = DATABASE();

-- =========================
-- 1) TAO BANG MOI (NEU CHUA TON TAI)
-- =========================
CREATE TABLE IF NOT EXISTS Permission (
  id INT AUTO_INCREMENT PRIMARY KEY,
  TenPermission VARCHAR(100) NOT NULL UNIQUE,
  MoTa VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ChiTiet_Permission (
  id INT AUTO_INCREMENT PRIMARY KEY,
  PermissionID INT NOT NULL,
  HanhDong VARCHAR(30) NOT NULL,
  MaQuyen VARCHAR(160) NOT NULL UNIQUE,
  CONSTRAINT fk_ctperm_permission FOREIGN KEY (PermissionID) REFERENCES Permission(id),
  CONSTRAINT uq_ctperm_permission_hanhdong UNIQUE (PermissionID, HanhDong)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Role_Permission (
  RoleID INT NOT NULL,
  ChiTietPermissionID INT NOT NULL,
  PRIMARY KEY (RoleID, ChiTietPermissionID),
  CONSTRAINT fk_roleperm_role FOREIGN KEY (RoleID) REFERENCES Roles(STT),
  CONSTRAINT fk_roleperm_ctpermission FOREIGN KEY (ChiTietPermissionID) REFERENCES ChiTiet_Permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tao index co dieu kien de script chay lai an toan
SELECT IF(
  EXISTS (
    SELECT 1
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'ChiTiet_Permission'
      AND index_name = 'idx_ctperm_maquyen'
  ),
  'SELECT 1',
  'CREATE INDEX idx_ctperm_maquyen ON ChiTiet_Permission (MaQuyen)'
) INTO @sql_idx_ctperm;
PREPARE stmt_idx_ctperm FROM @sql_idx_ctperm;
EXECUTE stmt_idx_ctperm;
DEALLOCATE PREPARE stmt_idx_ctperm;

SELECT IF(
  EXISTS (
    SELECT 1
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'Role_Permission'
      AND index_name = 'idx_roleperm_role'
  ),
  'SELECT 1',
  'CREATE INDEX idx_roleperm_role ON Role_Permission (RoleID)'
) INTO @sql_idx_roleperm;
PREPARE stmt_idx_roleperm FROM @sql_idx_roleperm;
EXECUTE stmt_idx_roleperm;
DEALLOCATE PREPARE stmt_idx_roleperm;

-- =========================
-- 2) MIGRATE CAC MODULE TU BANG Permissions CU
-- =========================
-- Quy uoc parse ten quyen cu:
--  BENHNHAN_VIEW -> module BENHNHAN
--  THUOC_MANAGE  -> module THUOC
--  ROLE_PERMISSION_MANAGE -> module ROLE_PERMISSION

INSERT IGNORE INTO Permission (TenPermission, MoTa)
SELECT DISTINCT
  UPPER(
    TRIM(
      CASE
        WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
        ELSE p.TenPermission
      END
    )
  ) AS TenPermission,
  CONCAT('Module migrate tu permission cu: ', p.TenPermission) AS MoTa
FROM Permissions p
WHERE p.TenPermission IS NOT NULL
  AND TRIM(p.TenPermission) <> '';

-- Cap nhat mo ta cho cac module da ton tai (neu co)
UPDATE Permission pm
JOIN (
  SELECT DISTINCT
    UPPER(
      TRIM(
        CASE
          WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
          ELSE p.TenPermission
        END
      )
    ) AS TenPermission,
    MAX(CONCAT('Module migrate tu permission cu: ', p.TenPermission)) AS MoTa
  FROM Permissions p
  WHERE p.TenPermission IS NOT NULL
    AND TRIM(p.TenPermission) <> ''
  GROUP BY 1
) src ON src.TenPermission = pm.TenPermission
SET pm.MoTa = src.MoTa;

-- =========================
-- 3) SEED HANH DONG MAC DINH CHO MOI MODULE
-- =========================
INSERT IGNORE INTO ChiTiet_Permission (PermissionID, HanhDong, MaQuyen)
SELECT pm.id, act.HanhDong, CONCAT(pm.TenPermission, '_', act.HanhDong)
FROM Permission pm
CROSS JOIN (
  SELECT 'XEM' AS HanhDong
  UNION ALL SELECT 'THEM'
  UNION ALL SELECT 'SUA'
  UNION ALL SELECT 'XOA'
) act;

-- =========================
-- 4) GAN DU LIEU ROLE TU BANG CU SANG BANG MOI
-- =========================
-- Quy tac map hanh dong:
--  *_VIEW   -> _XEM
--  *_CREATE -> _THEM
--  *_ADD    -> _THEM
--  *_UPDATE -> _SUA
--  *_EDIT   -> _SUA
--  *_MANAGE -> _XEM + _THEM + _SUA + _XOA
--  *_DELETE -> _XOA
--  khac     -> _XEM

-- 4.1 Map *_VIEW
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT rp.MaRole, ctp.id
FROM RolePermissions rp
JOIN Permissions p ON p.MaPermission = rp.MaPermission AND p.Active = 1
JOIN ChiTiet_Permission ctp ON ctp.MaQuyen = CONCAT(
  UPPER(
    CASE
      WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
      ELSE p.TenPermission
    END
  ),
  '_XEM'
)
WHERE rp.Active = 1
  AND UPPER(SUBSTRING_INDEX(p.TenPermission, '_', -1)) IN ('VIEW');

-- 4.2 Map *_CREATE, *_ADD
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT rp.MaRole, ctp.id
FROM RolePermissions rp
JOIN Permissions p ON p.MaPermission = rp.MaPermission AND p.Active = 1
JOIN ChiTiet_Permission ctp ON ctp.MaQuyen = CONCAT(
  UPPER(
    CASE
      WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
      ELSE p.TenPermission
    END
  ),
  '_THEM'
)
WHERE rp.Active = 1
  AND UPPER(SUBSTRING_INDEX(p.TenPermission, '_', -1)) IN ('CREATE', 'ADD');

-- 4.3 Map *_UPDATE, *_EDIT
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT rp.MaRole, ctp.id
FROM RolePermissions rp
JOIN Permissions p ON p.MaPermission = rp.MaPermission AND p.Active = 1
JOIN ChiTiet_Permission ctp ON ctp.MaQuyen = CONCAT(
  UPPER(
    CASE
      WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
      ELSE p.TenPermission
    END
  ),
  '_SUA'
)
WHERE rp.Active = 1
  AND UPPER(SUBSTRING_INDEX(p.TenPermission, '_', -1)) IN ('UPDATE', 'EDIT');

-- 4.4 Map *_DELETE
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT rp.MaRole, ctp.id
FROM RolePermissions rp
JOIN Permissions p ON p.MaPermission = rp.MaPermission AND p.Active = 1
JOIN ChiTiet_Permission ctp ON ctp.MaQuyen = CONCAT(
  UPPER(
    CASE
      WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
      ELSE p.TenPermission
    END
  ),
  '_XOA'
)
WHERE rp.Active = 1
  AND UPPER(SUBSTRING_INDEX(p.TenPermission, '_', -1)) IN ('DELETE');

-- 4.5 Map *_MANAGE -> full CRUD
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT rp.MaRole, ctp.id
FROM RolePermissions rp
JOIN Permissions p ON p.MaPermission = rp.MaPermission AND p.Active = 1
JOIN ChiTiet_Permission ctp ON ctp.MaQuyen IN (
  CONCAT(
    UPPER(
      CASE
        WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
        ELSE p.TenPermission
      END
    ), '_XEM'
  ),
  CONCAT(
    UPPER(
      CASE
        WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
        ELSE p.TenPermission
      END
    ), '_THEM'
  ),
  CONCAT(
    UPPER(
      CASE
        WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
        ELSE p.TenPermission
      END
    ), '_SUA'
  ),
  CONCAT(
    UPPER(
      CASE
        WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
        ELSE p.TenPermission
      END
    ), '_XOA'
  )
)
WHERE rp.Active = 1
  AND UPPER(SUBSTRING_INDEX(p.TenPermission, '_', -1)) = 'MANAGE';

-- 4.6 Truong hop khac -> map toi XEM
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT rp.MaRole, ctp.id
FROM RolePermissions rp
JOIN Permissions p ON p.MaPermission = rp.MaPermission AND p.Active = 1
JOIN ChiTiet_Permission ctp ON ctp.MaQuyen = CONCAT(
  UPPER(
    CASE
      WHEN LOCATE('_', p.TenPermission) > 0 THEN LEFT(p.TenPermission, LENGTH(p.TenPermission) - LENGTH(SUBSTRING_INDEX(p.TenPermission, '_', -1)) - 1)
      ELSE p.TenPermission
    END
  ),
  '_XEM'
)
WHERE rp.Active = 1
  AND UPPER(SUBSTRING_INDEX(p.TenPermission, '_', -1)) NOT IN ('VIEW', 'CREATE', 'ADD', 'UPDATE', 'EDIT', 'DELETE', 'MANAGE');

-- =========================
-- 5) DU LIEU MAU TRUC TIEP (NEU MUON THEM NHANH)
-- =========================
INSERT IGNORE INTO Permission (TenPermission, MoTa)
VALUES
  ('BENHNHAN', 'Quan ly benh nhan'),
  ('LICHKHAM', 'Quan ly lich kham'),
  ('THUOC', 'Quan ly thuoc');

INSERT IGNORE INTO ChiTiet_Permission (PermissionID, HanhDong, MaQuyen)
SELECT pm.id, act.HanhDong, CONCAT(pm.TenPermission, '_', act.HanhDong)
FROM Permission pm
JOIN (
  SELECT 'BENHNHAN' AS TenPermission
  UNION ALL SELECT 'LICHKHAM'
  UNION ALL SELECT 'THUOC'
) sample ON sample.TenPermission = pm.TenPermission
CROSS JOIN (
  SELECT 'XEM' AS HanhDong
  UNION ALL SELECT 'THEM'
  UNION ALL SELECT 'SUA'
  UNION ALL SELECT 'XOA'
) act;

-- =========================
-- 6) BAO CAO KIEM TRA
-- =========================
SELECT COUNT(*) AS TongModule FROM Permission;
SELECT COUNT(*) AS TongQuyenChiTiet FROM ChiTiet_Permission;
SELECT COUNT(*) AS TongGanQuyenRole FROM Role_Permission;

SELECT r.STT AS RoleID, r.TenVaiTro, COUNT(rp.ChiTietPermissionID) AS SoQuyen
FROM Roles r
LEFT JOIN Role_Permission rp ON rp.RoleID = r.STT
GROUP BY r.STT, r.TenVaiTro
ORDER BY r.STT;

-- ====================== END: migrate_rbac_chitiet_2026-03-24.sql ======================

-- ===================== BEGIN: seed_rbac_chitiet_roles_mau_2026-03-24.sql =====================
-- Seed role mau theo RBAC chi tiet (module + hanh dong)
-- Muc tieu:
-- 1) Gan toan bo quyen chi tiet cho ADMIN
-- 2) Gan bo quyen gioi han cho BACSI
-- 3) Gan bo quyen gioi han cho NHATHUOC
--
-- Luu y:
-- - Script nay se REPLACE role assignment trong bang Role_Permission
--   cho 3 role: ADMIN, BACSI, NHATHUOC.
-- - Khong dong den bang RolePermissions cu.

USE PhongKham;

SET @role_admin = (
  SELECT STT FROM Roles WHERE UPPER(TenVaiTro) = 'ADMIN' LIMIT 1
);
SET @role_bacsi = (
  SELECT STT FROM Roles WHERE UPPER(TenVaiTro) = 'BACSI' LIMIT 1
);
SET @role_nhathuoc = (
  SELECT STT FROM Roles WHERE UPPER(TenVaiTro) = 'NHATHUOC' LIMIT 1
);

-- Xoa role assignment cu trong Role_Permission cho 3 role mau
DELETE FROM Role_Permission WHERE RoleID = @role_admin;
DELETE FROM Role_Permission WHERE RoleID = @role_bacsi;
DELETE FROM Role_Permission WHERE RoleID = @role_nhathuoc;

-- 1) ADMIN: full quyen chi tiet
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT @role_admin, ctp.id
FROM ChiTiet_Permission ctp
WHERE @role_admin IS NOT NULL;

-- 2) BACSI: quyen gioi han cho quy trinh kham benh
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT @role_bacsi, ctp.id
FROM ChiTiet_Permission ctp
JOIN (
  SELECT 'BACSI_PROFILE_XEM' AS MaQuyen
  UNION ALL SELECT 'GOIDICHVU_XEM'
  UNION ALL SELECT 'BENHNHAN_XEM'
  UNION ALL SELECT 'LICHLAMVIEC_XEM'
  UNION ALL SELECT 'LICHKHAM_XEM'
  UNION ALL SELECT 'LICHKHAM_SUA'
  UNION ALL SELECT 'KHAMBENH_THEM'
  UNION ALL SELECT 'HOSO_XEM'
  UNION ALL SELECT 'HOSO_THEM'
  UNION ALL SELECT 'HOSO_SUA'
  UNION ALL SELECT 'DONTHUOC_XEM'
  UNION ALL SELECT 'DONTHUOC_THEM'
  UNION ALL SELECT 'DONTHUOC_SUA'
  UNION ALL SELECT 'HOADONKHAM_XEM'
) bacsi ON bacsi.MaQuyen = ctp.MaQuyen
WHERE @role_bacsi IS NOT NULL;

-- 3) NHATHUOC: quyen gioi han cho quy trinh ban thuoc / nhap thuoc
INSERT IGNORE INTO Role_Permission (RoleID, ChiTietPermissionID)
SELECT @role_nhathuoc, ctp.id
FROM ChiTiet_Permission ctp
JOIN (
  SELECT 'THUOC_XEM' AS MaQuyen
  UNION ALL SELECT 'THUOC_THEM'
  UNION ALL SELECT 'THUOC_SUA'
  UNION ALL SELECT 'THUOC_XOA'
  UNION ALL SELECT 'NCC_XEM'
  UNION ALL SELECT 'NCC_THEM'
  UNION ALL SELECT 'NCC_SUA'
  UNION ALL SELECT 'PHIEUNHAP_XEM'
  UNION ALL SELECT 'PHIEUNHAP_THEM'
  UNION ALL SELECT 'PHIEUNHAP_SUA'
  UNION ALL SELECT 'HOADONTHUOC_XEM'
  UNION ALL SELECT 'HOADONTHUOC_THEM'
  UNION ALL SELECT 'HOADONTHUOC_SUA'
) nhathuoc ON nhathuoc.MaQuyen = ctp.MaQuyen
WHERE @role_nhathuoc IS NOT NULL;

-- Bao cao nhanh
SELECT r.STT AS RoleID, r.TenVaiTro, COUNT(rp.ChiTietPermissionID) AS SoQuyenChiTiet
FROM Roles r
LEFT JOIN Role_Permission rp ON rp.RoleID = r.STT
WHERE UPPER(r.TenVaiTro) IN ('ADMIN', 'BACSI', 'NHATHUOC')
GROUP BY r.STT, r.TenVaiTro
ORDER BY r.STT;

-- ====================== END: seed_rbac_chitiet_roles_mau_2026-03-24.sql ======================

-- ===================== BEGIN: migrate_lothuoc_refactor_2026-03-24.sql =====================
-- ============================================================
-- LoThuoc refactor migration (Phase 1 - additive, safe)
-- Date: 2026-03-24
-- Target: MySQL 8.x (Aiven compatible)
--
-- Goal:
-- 1) Introduce normalized lot master table: LoThuoc
-- 2) Introduce unified lot movement ledger: LoThuocBienDong
-- 3) Backfill data from existing tables:
--    - ChiTietPhieuNhap        -> LoThuoc + IMPORT movements
--    - XuatThuocTheoLo         -> ISSUE movements
--    - TieuHuyLoThuoc          -> DISPOSE movements
--
-- NOTE:
-- - This script does NOT drop legacy tables.
-- - Run this first, then migrate application read/write gradually.
-- ============================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- ------------------------------------------------------------
-- 0) Create new lot master table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LoThuoc (
  MaLo BIGINT PRIMARY KEY AUTO_INCREMENT,
  MaCTPN VARCHAR(20) NOT NULL,
  MaPhieuNhap VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLo VARCHAR(50) NOT NULL,
  HanSuDung DATE,
  SoLuongNhap INT NOT NULL,
  SoLuongConLai INT NOT NULL,
  DonGiaNhap DECIMAL(18,2) NOT NULL,
  NgayNhap DATETIME,
  TrangThai VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  Active TINYINT(1) NOT NULL DEFAULT 1,
  CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UpdatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_lothuoc_ctpn FOREIGN KEY (MaCTPN) REFERENCES ChiTietPhieuNhap(MaCTPN),
  CONSTRAINT fk_lothuoc_phieunhap FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
  CONSTRAINT fk_lothuoc_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc),
  CONSTRAINT chk_lothuoc_slnhap CHECK (SoLuongNhap > 0),
  CONSTRAINT chk_lothuoc_slconlai CHECK (SoLuongConLai >= 0 AND SoLuongConLai <= SoLuongNhap),
  CONSTRAINT chk_lothuoc_dongia CHECK (DonGiaNhap > 0),
  CONSTRAINT chk_lothuoc_trangthai CHECK (TrangThai IN ('ACTIVE', 'EXPIRED', 'DEPLETED', 'DISPOSED')),
  UNIQUE KEY uq_lothuoc_ctpn (MaCTPN),
  UNIQUE KEY uq_lothuoc_logic (MaPhieuNhap, MaThuoc, SoLo, HanSuDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_lothuoc_thuoc_hsd ON LoThuoc (MaThuoc, HanSuDung);
CREATE INDEX idx_lothuoc_trangthai ON LoThuoc (TrangThai, Active);
CREATE INDEX idx_lothuoc_solo ON LoThuoc (MaThuoc, SoLo);

-- ------------------------------------------------------------
-- 1) Create unified lot movement table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LoThuocBienDong (
  MaBienDong BIGINT PRIMARY KEY AUTO_INCREMENT,
  MaLo BIGINT NOT NULL,
  LoaiBienDong VARCHAR(30) NOT NULL,
  SoLuong INT NOT NULL,
  ThoiDiem DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  NguonChungTuLoai VARCHAR(30),
  NguonChungTuMa VARCHAR(30),
  MaHoaDon VARCHAR(20),
  MaCTHDThuoc VARCHAR(20),
  MaCTPN VARCHAR(20),
  GhiChu VARCHAR(255),
  NguoiThucHien VARCHAR(120),
  CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_biendong_malo FOREIGN KEY (MaLo) REFERENCES LoThuoc(MaLo),
  CONSTRAINT fk_biendong_hoadon FOREIGN KEY (MaHoaDon) REFERENCES HoaDonThuoc(MaHoaDon),
  CONSTRAINT fk_biendong_cthd FOREIGN KEY (MaCTHDThuoc) REFERENCES CTHDThuoc(MaCTHDThuoc),
  CONSTRAINT fk_biendong_ctpn FOREIGN KEY (MaCTPN) REFERENCES ChiTietPhieuNhap(MaCTPN),
  CONSTRAINT chk_biendong_loai CHECK (LoaiBienDong IN ('IMPORT', 'ISSUE', 'DISPOSE', 'ADJUST', 'RETURN', 'REFUND_CANCEL')),
  CONSTRAINT chk_biendong_soluong CHECK (SoLuong > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_biendong_malo_time ON LoThuocBienDong (MaLo, ThoiDiem);
CREATE INDEX idx_biendong_hoadon ON LoThuocBienDong (MaHoaDon, MaCTHDThuoc);
CREATE INDEX idx_biendong_ctpn ON LoThuocBienDong (MaCTPN);
CREATE INDEX idx_biendong_loai_time ON LoThuocBienDong (LoaiBienDong, ThoiDiem);

-- ------------------------------------------------------------
-- 2) Backfill LoThuoc from ChiTietPhieuNhap
-- ------------------------------------------------------------
INSERT INTO LoThuoc (
  MaCTPN,
  MaPhieuNhap,
  MaThuoc,
  SoLo,
  HanSuDung,
  SoLuongNhap,
  SoLuongConLai,
  DonGiaNhap,
  NgayNhap,
  TrangThai,
  Active
)
SELECT
  ctpn.MaCTPN,
  ctpn.MaPhieuNhap,
  ctpn.MaThuoc,
  ctpn.SoLo,
  ctpn.HanSuDung,
  ctpn.SoLuongNhap,
  ctpn.SoLuongConLai,
  ctpn.DonGiaNhap,
  pn.NgayNhap,
  CASE
    WHEN ctpn.SoLuongConLai = 0 THEN 'DEPLETED'
    WHEN ctpn.HanSuDung IS NOT NULL AND DATE(ctpn.HanSuDung) <= CURDATE() THEN 'EXPIRED'
    ELSE 'ACTIVE'
  END AS TrangThai,
  1 AS Active
FROM ChiTietPhieuNhap ctpn
JOIN PhieuNhap pn ON pn.MaPhieuNhap = ctpn.MaPhieuNhap
LEFT JOIN LoThuoc lt ON lt.MaCTPN = ctpn.MaCTPN
WHERE lt.MaLo IS NULL;

-- ------------------------------------------------------------
-- 3) Backfill IMPORT movements
-- ------------------------------------------------------------
INSERT INTO LoThuocBienDong (
  MaLo,
  LoaiBienDong,
  SoLuong,
  ThoiDiem,
  NguonChungTuLoai,
  NguonChungTuMa,
  MaCTPN,
  GhiChu,
  NguoiThucHien
)
SELECT
  lt.MaLo,
  'IMPORT' AS LoaiBienDong,
  lt.SoLuongNhap,
  COALESCE(lt.NgayNhap, lt.CreatedAt) AS ThoiDiem,
  'PHIEU_NHAP' AS NguonChungTuLoai,
  lt.MaPhieuNhap AS NguonChungTuMa,
  lt.MaCTPN,
  'Backfill import from ChiTietPhieuNhap' AS GhiChu,
  'MIGRATION_2026_03_24' AS NguoiThucHien
FROM LoThuoc lt
LEFT JOIN LoThuocBienDong bd
  ON bd.MaLo = lt.MaLo
 AND bd.LoaiBienDong = 'IMPORT'
 AND bd.MaCTPN = lt.MaCTPN
WHERE bd.MaBienDong IS NULL;

-- ------------------------------------------------------------
-- 4) Backfill ISSUE movements from XuatThuocTheoLo
-- ------------------------------------------------------------
INSERT INTO LoThuocBienDong (
  MaLo,
  LoaiBienDong,
  SoLuong,
  ThoiDiem,
  NguonChungTuLoai,
  NguonChungTuMa,
  MaHoaDon,
  MaCTHDThuoc,
  MaCTPN,
  GhiChu,
  NguoiThucHien
)
SELECT
  lt.MaLo,
  'ISSUE' AS LoaiBienDong,
  xtl.SoLuongXuat,
  COALESCE(xtl.NgayXuat, CURRENT_TIMESTAMP) AS ThoiDiem,
  'HOA_DON_THUOC' AS NguonChungTuLoai,
  xtl.MaHoaDon AS NguonChungTuMa,
  xtl.MaHoaDon,
  xtl.MaCTHDThuoc,
  xtl.MaCTPN,
  CONCAT('Backfill issue from XuatThuocTheoLo#', xtl.MaXuatLo) AS GhiChu,
  'MIGRATION_2026_03_24' AS NguoiThucHien
FROM XuatThuocTheoLo xtl
JOIN LoThuoc lt ON lt.MaCTPN = xtl.MaCTPN
LEFT JOIN LoThuocBienDong bd
  ON bd.LoaiBienDong = 'ISSUE'
 AND bd.MaCTPN = xtl.MaCTPN
 AND bd.MaHoaDon = xtl.MaHoaDon
 AND bd.MaCTHDThuoc = xtl.MaCTHDThuoc
 AND bd.SoLuong = xtl.SoLuongXuat
 AND bd.ThoiDiem = COALESCE(xtl.NgayXuat, CURRENT_TIMESTAMP)
WHERE bd.MaBienDong IS NULL;

-- ------------------------------------------------------------
-- 5) Backfill DISPOSE movements from TieuHuyLoThuoc
-- ------------------------------------------------------------
INSERT INTO LoThuocBienDong (
  MaLo,
  LoaiBienDong,
  SoLuong,
  ThoiDiem,
  NguonChungTuLoai,
  NguonChungTuMa,
  MaCTPN,
  GhiChu,
  NguoiThucHien
)
SELECT
  lt.MaLo,
  'DISPOSE' AS LoaiBienDong,
  th.SoLuongTieuHuy,
  COALESCE(th.NgayTieuHuy, CURRENT_TIMESTAMP) AS ThoiDiem,
  'TIEU_HUY_LO' AS NguonChungTuLoai,
  CONCAT('TIEUHUY#', th.MaTieuHuy) AS NguonChungTuMa,
  th.MaCTPN,
  COALESCE(th.LyDo, 'Backfill dispose from TieuHuyLoThuoc') AS GhiChu,
  COALESCE(th.NguoiThucHien, 'MIGRATION_2026_03_24') AS NguoiThucHien
FROM TieuHuyLoThuoc th
JOIN LoThuoc lt ON lt.MaCTPN = th.MaCTPN
LEFT JOIN LoThuocBienDong bd
  ON bd.LoaiBienDong = 'DISPOSE'
 AND bd.MaCTPN = th.MaCTPN
 AND bd.SoLuong = th.SoLuongTieuHuy
 AND bd.ThoiDiem = COALESCE(th.NgayTieuHuy, CURRENT_TIMESTAMP)
WHERE bd.MaBienDong IS NULL;

-- ------------------------------------------------------------
-- 6) Sync lot status after backfill
-- ------------------------------------------------------------
UPDATE LoThuoc lt
SET lt.TrangThai = CASE
  WHEN lt.SoLuongConLai = 0 THEN 'DEPLETED'
  WHEN lt.HanSuDung IS NOT NULL AND DATE(lt.HanSuDung) <= CURDATE() THEN 'EXPIRED'
  ELSE 'ACTIVE'
END;

-- ------------------------------------------------------------
-- 7) Convenience view for lot remaining stock
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW vw_lo_thuoc_ton AS
SELECT
  lt.MaLo,
  lt.MaThuoc,
  t.TenThuoc,
  lt.MaPhieuNhap,
  lt.MaCTPN,
  lt.SoLo,
  lt.HanSuDung,
  lt.SoLuongNhap,
  lt.SoLuongConLai,
  lt.DonGiaNhap,
  lt.NgayNhap,
  lt.TrangThai
FROM LoThuoc lt
LEFT JOIN Thuoc t ON t.MaThuoc = lt.MaThuoc
WHERE lt.Active = 1;

-- ------------------------------------------------------------
-- 8) Sanity checks
-- ------------------------------------------------------------
SELECT 'LoThuoc count' AS CheckName, COUNT(*) AS ValueCount FROM LoThuoc;
SELECT 'LoThuocBienDong count' AS CheckName, COUNT(*) AS ValueCount FROM LoThuocBienDong;
SELECT 'Issue movement count' AS CheckName, COUNT(*) AS ValueCount FROM LoThuocBienDong WHERE LoaiBienDong = 'ISSUE';
SELECT 'Dispose movement count' AS CheckName, COUNT(*) AS ValueCount FROM LoThuocBienDong WHERE LoaiBienDong = 'DISPOSE';

-- ====================== END: migrate_lothuoc_refactor_2026-03-24.sql ======================

