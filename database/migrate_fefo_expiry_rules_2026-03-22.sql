USE PhongKham;

START TRANSACTION;

-- 1) Theo doi ton con lai theo lo de xuat FEFO va bao toan lich su nhap
SET @has_col_soluongconlai := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'ChiTietPhieuNhap'
    AND COLUMN_NAME = 'SoLuongConLai'
);

SET @sql_add_col_soluongconlai := IF(
  @has_col_soluongconlai = 0,
  'ALTER TABLE ChiTietPhieuNhap ADD COLUMN SoLuongConLai INT NULL AFTER SoLuongNhap',
  'SELECT ''skip add col SoLuongConLai'''
);

PREPARE stmt_add_col_soluongconlai FROM @sql_add_col_soluongconlai;
EXECUTE stmt_add_col_soluongconlai;
DEALLOCATE PREPARE stmt_add_col_soluongconlai;

UPDATE ChiTietPhieuNhap
SET SoLuongConLai = SoLuongNhap
WHERE SoLuongConLai IS NULL;

ALTER TABLE ChiTietPhieuNhap
  MODIFY COLUMN SoLuongConLai INT NOT NULL;

-- 2) Rang buoc so luong con lai
SET @has_chk := (
  SELECT COUNT(*)
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'ChiTietPhieuNhap'
    AND CONSTRAINT_NAME = 'chk_ctpn_soluong_con_lai'
);

SET @sql_chk := IF(
  @has_chk = 0,
  'ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT chk_ctpn_soluong_con_lai CHECK (SoLuongConLai >= 0 AND SoLuongConLai <= SoLuongNhap)',
  'SELECT ''skip chk_ctpn_soluong_con_lai'''
);

PREPARE stmt_chk FROM @sql_chk;
EXECUTE stmt_chk;
DEALLOCATE PREPARE stmt_chk;

SET @has_idx_ctpn_thuoc_hsd_conlai := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'ChiTietPhieuNhap'
    AND INDEX_NAME = 'idx_ctpn_thuoc_hsd_conlai'
);

SET @sql_idx_ctpn_thuoc_hsd_conlai := IF(
  @has_idx_ctpn_thuoc_hsd_conlai = 0,
  'CREATE INDEX idx_ctpn_thuoc_hsd_conlai ON ChiTietPhieuNhap (MaThuoc, HanSuDung, SoLuongConLai)',
  'SELECT ''skip idx_ctpn_thuoc_hsd_conlai'''
);

PREPARE stmt_idx_ctpn_thuoc_hsd_conlai FROM @sql_idx_ctpn_thuoc_hsd_conlai;
EXECUTE stmt_idx_ctpn_thuoc_hsd_conlai;
DEALLOCATE PREPARE stmt_idx_ctpn_thuoc_hsd_conlai;

-- 3) Lich su xuat theo lo (de truy vet FEFO)
CREATE TABLE IF NOT EXISTS XuatThuocTheoLo (
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

SET @has_idx_xuatlo_thuoc_ngay := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'XuatThuocTheoLo'
    AND INDEX_NAME = 'idx_xuatlo_thuoc_ngay'
);

SET @sql_idx_xuatlo_thuoc_ngay := IF(
  @has_idx_xuatlo_thuoc_ngay = 0,
  'CREATE INDEX idx_xuatlo_thuoc_ngay ON XuatThuocTheoLo (MaThuoc, NgayXuat)',
  'SELECT ''skip idx_xuatlo_thuoc_ngay'''
);

PREPARE stmt_idx_xuatlo_thuoc_ngay FROM @sql_idx_xuatlo_thuoc_ngay;
EXECUTE stmt_idx_xuatlo_thuoc_ngay;
DEALLOCATE PREPARE stmt_idx_xuatlo_thuoc_ngay;

SET @has_idx_xuatlo_hsd := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'XuatThuocTheoLo'
    AND INDEX_NAME = 'idx_xuatlo_hsd'
);

SET @sql_idx_xuatlo_hsd := IF(
  @has_idx_xuatlo_hsd = 0,
  'CREATE INDEX idx_xuatlo_hsd ON XuatThuocTheoLo (HanSuDung)',
  'SELECT ''skip idx_xuatlo_hsd'''
);

PREPARE stmt_idx_xuatlo_hsd FROM @sql_idx_xuatlo_hsd;
EXECUTE stmt_idx_xuatlo_hsd;
DEALLOCATE PREPARE stmt_idx_xuatlo_hsd;

-- 4) Nghiep vu tieu huy lo het han (khong xoa lich su nhap)
CREATE TABLE IF NOT EXISTS TieuHuyLoThuoc (
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

SET @has_idx_tieuhuy_thuoc_ngay := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'TieuHuyLoThuoc'
    AND INDEX_NAME = 'idx_tieuhuy_thuoc_ngay'
);

SET @sql_idx_tieuhuy_thuoc_ngay := IF(
  @has_idx_tieuhuy_thuoc_ngay = 0,
  'CREATE INDEX idx_tieuhuy_thuoc_ngay ON TieuHuyLoThuoc (MaThuoc, NgayTieuHuy)',
  'SELECT ''skip idx_tieuhuy_thuoc_ngay'''
);

PREPARE stmt_idx_tieuhuy_thuoc_ngay FROM @sql_idx_tieuhuy_thuoc_ngay;
EXECUTE stmt_idx_tieuhuy_thuoc_ngay;
DEALLOCATE PREPARE stmt_idx_tieuhuy_thuoc_ngay;

SET @has_idx_tieuhuy_hsd := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'TieuHuyLoThuoc'
    AND INDEX_NAME = 'idx_tieuhuy_hsd'
);

SET @sql_idx_tieuhuy_hsd := IF(
  @has_idx_tieuhuy_hsd = 0,
  'CREATE INDEX idx_tieuhuy_hsd ON TieuHuyLoThuoc (HanSuDung)',
  'SELECT ''skip idx_tieuhuy_hsd'''
);

PREPARE stmt_idx_tieuhuy_hsd FROM @sql_idx_tieuhuy_hsd;
EXECUTE stmt_idx_tieuhuy_hsd;
DEALLOCATE PREPARE stmt_idx_tieuhuy_hsd;

COMMIT;
