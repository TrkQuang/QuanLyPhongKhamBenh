-- MySQL Compatible Dump (Safe for MySQL 5.7 / MariaDB / Railway)
-- Database: PhongKham
-- Charset: utf8mb4_unicode_ci

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS PhongKham;
CREATE DATABASE PhongKham
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE PhongKham;

-- =========================
-- KHOA
-- =========================
CREATE TABLE Khoa (
  MaKhoa INT AUTO_INCREMENT PRIMARY KEY,
  TenKhoa VARCHAR(191) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- BAC SI
-- =========================
CREATE TABLE BacSi (
  MaBacSi INT AUTO_INCREMENT PRIMARY KEY,
  HoTen VARCHAR(191) NOT NULL,
  MaKhoa INT NOT NULL,
  SoDienThoai VARCHAR(20),
  Email VARCHAR(191),
  KEY MaKhoa (MaKhoa),
  CONSTRAINT fk_bacsi_khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- GOI DICH VU
-- =========================
CREATE TABLE GoiDichVu (
  MaGoi VARCHAR(20) PRIMARY KEY,
  TenGoi VARCHAR(191) NOT NULL,
  GiaDichVu DECIMAL(18,2) NOT NULL,
  ThoiGianKham INT,
  MoTa TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- LICH KHAM
-- =========================
CREATE TABLE LichKham (
  MaLichKham INT AUTO_INCREMENT PRIMARY KEY,
  MaGoi VARCHAR(20),
  MaBacSi INT NOT NULL,
  ThoiGianBatDau DATETIME NOT NULL,
  ThoiGianKetThuc DATETIME,
  TrangThai VARCHAR(100),
  MaDinhDanhTam VARCHAR(100),
  KEY MaGoi (MaGoi),
  KEY MaBacSi (MaBacSi),
  CONSTRAINT fk_lichkham_goi FOREIGN KEY (MaGoi) REFERENCES GoiDichVu(MaGoi),
  CONSTRAINT fk_lichkham_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- PHIEU KHAM
-- =========================
CREATE TABLE PhieuKham (
  MaPhieuKham VARCHAR(20) PRIMARY KEY,
  MaLichKham INT NOT NULL,
  MaBacSi INT NOT NULL,
  ThoiGianVao DATETIME,
  TrieuChungSoBo TEXT,
  KEY MaLichKham (MaLichKham),
  KEY MaBacSi (MaBacSi),
  CONSTRAINT fk_phieukham_lich FOREIGN KEY (MaLichKham) REFERENCES LichKham(MaLichKham),
  CONSTRAINT fk_phieukham_bacsi FOREIGN KEY (MaBacSi) REFERENCES BacSi(MaBacSi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- HO SO BENH AN
-- =========================
CREATE TABLE HoSoBenhAn (
  MaHoSo INT AUTO_INCREMENT PRIMARY KEY,
  MaPhieuKham VARCHAR(20) NOT NULL,
  NgayKham DATETIME,
  ChanDoan TEXT,
  KetLuan TEXT,
  LoiDan TEXT,
  KEY MaPhieuKham (MaPhieuKham),
  CONSTRAINT fk_hsba_phieukham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- DON THUOC
-- =========================
CREATE TABLE DonThuoc (
  MaDonThuoc INT AUTO_INCREMENT PRIMARY KEY,
  MaHoSo INT NOT NULL,
  NgayKeDon DATETIME,
  GhiChu TEXT,
  KEY MaHoSo (MaHoSo),
  CONSTRAINT fk_donthuoc_hoso FOREIGN KEY (MaHoSo) REFERENCES HoSoBenhAn(MaHoSo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- THUOC
-- =========================
CREATE TABLE Thuoc (
  MaThuoc VARCHAR(20) PRIMARY KEY,
  TenThuoc VARCHAR(191) NOT NULL,
  HoatChat VARCHAR(191),
  DonViTinh VARCHAR(50),
  DonGiaBan DECIMAL(18,2),
  SoLuongTon INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- CHI TIET DON THUOC
-- =========================
CREATE TABLE ChiTietDonThuoc (
  MaCTDT INT AUTO_INCREMENT PRIMARY KEY,
  MaDonThuoc INT NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLuong INT,
  LieuDung VARCHAR(191),
  CachDung VARCHAR(191),
  KEY MaDonThuoc (MaDonThuoc),
  KEY MaThuoc (MaThuoc),
  CONSTRAINT fk_ctdt_donthuoc FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc(MaDonThuoc),
  CONSTRAINT fk_ctdt_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- HOA DON THUOC
-- =========================
CREATE TABLE HoaDonThuoc (
  MaHDThuoc INT AUTO_INCREMENT PRIMARY KEY,
  MaDonThuoc INT NOT NULL,
  NgayMua DATETIME,
  TongTien DECIMAL(18,2),
  HinhThucThanhToan VARCHAR(100),
  KEY MaDonThuoc (MaDonThuoc),
  CONSTRAINT fk_hdthuoc_donthuoc FOREIGN KEY (MaDonThuoc) REFERENCES DonThuoc(MaDonThuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- HOA DON KHAM
-- =========================
CREATE TABLE HoaDonKham (
  MaHDKham VARCHAR(20) PRIMARY KEY,
  MaPhieuKham VARCHAR(20) NOT NULL,
  MaGoi VARCHAR(20),
  NgayThanhToan DATETIME,
  TongTien DECIMAL(18,2),
  HinhThucThanhToan VARCHAR(100),
  KEY MaPhieuKham (MaPhieuKham),
  KEY MaGoi (MaGoi),
  CONSTRAINT fk_hdk_phieukham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
  CONSTRAINT fk_hdk_goi FOREIGN KEY (MaGoi) REFERENCES GoiDichVu(MaGoi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- NHA CUNG CAP
-- =========================
CREATE TABLE NhaCungCap (
  MaNCC INT AUTO_INCREMENT PRIMARY KEY,
  TenNCC VARCHAR(191),
  DiaChi VARCHAR(191),
  SoDienThoai VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- PHIEU NHAP
-- =========================
CREATE TABLE PhieuNhap (
  MaPhieuNhap VARCHAR(20) PRIMARY KEY,
  MaNCC INT NOT NULL,
  NgayNhap DATETIME,
  NguoiNhap VARCHAR(191),
  TongTienNhap DECIMAL(18,2),
  KEY MaNCC (MaNCC),
  CONSTRAINT fk_phieunhap_ncc FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- CHI TIET PHIEU NHAP
-- =========================
CREATE TABLE ChiTietPhieuNhap (
  MaCTPN VARCHAR(20) PRIMARY KEY,
  MaPhieuNhap VARCHAR(20) NOT NULL,
  MaThuoc VARCHAR(20) NOT NULL,
  SoLuongNhap INT,
  DonGiaNhap DECIMAL(18,2),
  HanSuDung DATE,
  KEY MaPhieuNhap (MaPhieuNhap),
  KEY MaThuoc (MaThuoc),
  CONSTRAINT fk_ctpn_phieunhap FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
  CONSTRAINT fk_ctpn_thuoc FOREIGN KEY (MaThuoc) REFERENCES Thuoc(MaThuoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
