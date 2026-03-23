USE PhongKham;

-- =========================================================
-- SEED DU LIEU KHONG DUNG PROCEDURE
-- Muc tieu:
-- 1) Tao 10 benh nhan cho moi bac si
-- 2) Tao day du: LichLamViec -> LichKham -> HoSoBenhAn -> DonThuoc
--    -> CTDonThuoc -> HoaDonKham -> HoaDonThuoc -> CTHDThuoc
-- 3) Tuan thu FK/CHECK/trigger hien tai
-- =========================================================

START TRANSACTION;

-- 0) Dam bao moi khoa deu co it nhat 1 GoiDichVu de tao LichKham hop le
INSERT INTO GoiDichVu (MaGoi, TenGoi, GiaDichVu, ThoiGianKham, MoTa, MaKhoa)
SELECT
  CONCAT('GS', LPAD(ABS(CRC32(k.MaKhoa)) % 999999, 6, '0')) AS MaGoi,
  CONCAT('Goi seed cho khoa ', k.TenKhoa) AS TenGoi,
  200000 AS GiaDichVu,
  30 AS ThoiGianKham,
  'Auto seed de dam bao khoa co goi dich vu' AS MoTa,
  k.MaKhoa
FROM Khoa k
LEFT JOIN (
  SELECT MaKhoa, MIN(MaGoi) AS AnyGoi
  FROM GoiDichVu
  GROUP BY MaKhoa
) g ON g.MaKhoa = k.MaKhoa
WHERE g.AnyGoi IS NULL;

-- 1) Dam bao co thuoc active de tao DonThuoc/HoaDonThuoc
INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonVi, DonGiaBan, SoLuongTon, Active)
SELECT 'TSEED001', 'Paracetamol 500mg', 'Paracetamol', 'Vien', 'Hop', 1500, 5000, 1
WHERE NOT EXISTS (SELECT 1 FROM Thuoc WHERE Active = 1);

SET @seed_ts := UNIX_TIMESTAMP(NOW());
SET @seed_drug := (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1);
SET @seed_drug_price := (SELECT DonGiaBan FROM Thuoc WHERE MaThuoc = @seed_drug LIMIT 1);

-- 2) Tao tap du lieu trung gian 10 dong / moi bac si
DROP TEMPORARY TABLE IF EXISTS tmp_seed_rows;
CREATE TEMPORARY TABLE tmp_seed_rows AS
SELECT
  b.MaBacSi,
  b.MaKhoa,
  n.idx,
  ROW_NUMBER() OVER (ORDER BY b.MaBacSi, n.idx) AS seq,
  DATE_SUB(CURDATE(), INTERVAL (n.idx + 3) DAY) AS ngay_kham,
  TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (n.idx + 3) DAY), '09:00:00') AS tg_bat_dau,
  TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (n.idx + 3) DAY), '09:30:00') AS tg_ket_thuc
FROM BacSi b
CROSS JOIN (
  SELECT 1 AS idx UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
  UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
) n;

-- 3) Tao LichLamViec DA_DUYET (de trigger LichKham hop le)
-- Dung ON DUPLICATE KEY de tranh loi neu trung (MaBacSi, NgayLam, CaLam)
INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam, TrangThai)
SELECT
  CONCAT('LL', @seed_ts, LPAD(seq, 4, '0')),
  MaBacSi,
  ngay_kham,
  'Sang',
  'DA_DUYET'
FROM tmp_seed_rows
ON DUPLICATE KEY UPDATE TrangThai = 'DA_DUYET';

-- 4) Tao LichKham (trigger se check BacSi-Khoa-Goi va LichLamViec DA_DUYET)
INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, ThoiGianKetThuc, TrangThai, MaDinhDanhTam)
SELECT
  CONCAT('LK', @seed_ts, LPAD(r.seq, 4, '0')),
  g.MaGoi,
  r.MaBacSi,
  r.tg_bat_dau,
  r.tg_ket_thuc,
  'HOAN_THANH',
  CONCAT('TMP', LPAD(r.seq, 6, '0'))
FROM tmp_seed_rows r
JOIN (
  SELECT MaKhoa, MIN(MaGoi) AS MaGoi
  FROM GoiDichVu
  GROUP BY MaKhoa
) g ON g.MaKhoa = r.MaKhoa;

-- 5) Tao HoSoBenhAn
INSERT INTO HoSoBenhAn (
  MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi,
  NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai
)
SELECT
  CONCAT('HS', @seed_ts, LPAD(r.seq, 4, '0')),
  CONCAT('LK', @seed_ts, LPAD(r.seq, 4, '0')),
  CONCAT('BenhNhan_', r.MaBacSi, '_', LPAD(r.idx, 2, '0')),
  CONCAT('09', LPAD(r.seq, 8, '0')),
  CONCAT('1', LPAD(r.seq, 11, '0')),
  DATE_SUB(CURDATE(), INTERVAL (20 + (r.idx MOD 25)) YEAR),
  CASE WHEN (r.idx % 2) = 0 THEN 'Nam' ELSE 'Nu' END,
  CONCAT('Dia chi seed ', r.idx, ', Quan ', (r.idx % 12) + 1),
  DATE_ADD(r.tg_bat_dau, INTERVAL 15 MINUTE),
  CONCAT('Sot, ho nhe ngay ', r.idx),
  CASE WHEN (r.idx % 2) = 0 THEN 'Viem hong cap' ELSE 'Cam cum theo mua' END,
  'Theo doi ngoai tru',
  'Uong thuoc dung lieu, tai kham neu trieu chung keo dai',
  r.MaBacSi,
  'DA_KHAM'
FROM tmp_seed_rows r;

-- 6) Tao DonThuoc + CTDonThuoc
INSERT INTO DonThuoc (MaDonThuoc, MaHoSo, NgayKeDon, GhiChu)
SELECT
  CONCAT('DT', @seed_ts, LPAD(seq, 4, '0')),
  CONCAT('HS', @seed_ts, LPAD(seq, 4, '0')),
  DATE_ADD(tg_bat_dau, INTERVAL 20 MINUTE),
  'Don thuoc auto seed'
FROM tmp_seed_rows;

INSERT INTO CTDonThuoc (MaCTDonThuoc, MaDonThuoc, MaThuoc, SoLuong, LieuDung, CachDung)
SELECT
  CONCAT('CD', @seed_ts, LPAD(seq, 4, '0')),
  CONCAT('DT', @seed_ts, LPAD(seq, 4, '0')),
  @seed_drug,
  (idx % 3) + 1,
  '2 lan/ngay',
  'Sau an, sang va toi'
FROM tmp_seed_rows;

-- 7) Tao HoaDonKham
INSERT INTO HoaDonKham (MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai)
SELECT
  CONCAT('HK', @seed_ts, LPAD(r.seq, 4, '0')),
  CONCAT('HS', @seed_ts, LPAD(r.seq, 4, '0')),
  g.MaGoi,
  DATE_ADD(r.tg_bat_dau, INTERVAL 30 MINUTE),
  g.GiaDichVu,
  CASE WHEN (r.idx % 2) = 0 THEN 'TIEN_MAT' ELSE 'CHUYEN_KHOAN' END,
  'DA_THANH_TOAN'
FROM tmp_seed_rows r
JOIN (
  SELECT gdv.MaKhoa, gdv.MaGoi, gdv.GiaDichVu
  FROM GoiDichVu gdv
  JOIN (
    SELECT MaKhoa, MIN(MaGoi) AS MaGoi
    FROM GoiDichVu
    GROUP BY MaKhoa
  ) x ON x.MaKhoa = gdv.MaKhoa AND x.MaGoi = gdv.MaGoi
) g ON g.MaKhoa = r.MaKhoa;

-- 8) Tao HoaDonThuoc + CTHDThuoc
INSERT INTO HoaDonThuoc (
  MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu,
  TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc,
  TenBenhNhan, SdtBenhNhan, Active
)
SELECT
  CONCAT('HT', @seed_ts, LPAD(seq, 4, '0')),
  CONCAT('DT', @seed_ts, LPAD(seq, 4, '0')),
  DATE_ADD(tg_bat_dau, INTERVAL 35 MINUTE),
  ((idx % 3) + 1) * @seed_drug_price,
  'Hoa don thuoc auto seed',
  'DA_THANH_TOAN',
  DATE_ADD(tg_bat_dau, INTERVAL 40 MINUTE),
  'DA_HOAN_THANH',
  CONCAT('BenhNhan_', MaBacSi, '_', LPAD(idx, 2, '0')),
  CONCAT('09', LPAD(seq, 8, '0')),
  1
FROM tmp_seed_rows;

INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active)
SELECT
  CONCAT('CT', @seed_ts, LPAD(seq, 4, '0')),
  CONCAT('HT', @seed_ts, LPAD(seq, 4, '0')),
  @seed_drug,
  (idx % 3) + 1,
  @seed_drug_price,
  ((idx % 3) + 1) * @seed_drug_price,
  'Chi tiet thuoc auto seed',
  1
FROM tmp_seed_rows;

COMMIT;

-- =========================
-- QUICK CHECK
-- =========================
SELECT MaBacSi, COUNT(*) AS SoHoSoSeed
FROM HoSoBenhAn
WHERE HoTen LIKE 'BenhNhan_%'
GROUP BY MaBacSi
ORDER BY MaBacSi;
