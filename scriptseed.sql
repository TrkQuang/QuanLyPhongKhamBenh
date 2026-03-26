-- Seed phieu nhap moi tu PN026 den PN030
-- Yeu cau:
-- 1) Tat ca phieu co TrangThai = CHO_DUYET (UI hien thi la CHUA_XAC_NHAN)
-- 2) Du lieu hop le FK, lay NCC/Thuoc dang ton tai trong DB
-- 3) Co chi tiet phieu nhap de hien thi va thao tac trong man nha thuoc

USE PhongKham;
SET NAMES utf8mb4;

START TRANSACTION;

-- Don du lieu mau neu da tung seed truoc do
DELETE FROM ChiTietPhieuNhap
WHERE MaPhieuNhap IN ('PN026', 'PN027', 'PN028', 'PN029', 'PN030');

DELETE FROM PhieuNhap
WHERE MaPhieuNhap IN ('PN026', 'PN027', 'PN028', 'PN029', 'PN030');

-- Header phieu nhap: trang thai CHO_DUYET
INSERT INTO PhieuNhap (MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai)
SELECT *
FROM (
  SELECT
    'PN026' AS MaPhieuNhap,
    (SELECT MaNhaCungCap FROM NhaCungCap WHERE Active = 1 ORDER BY MaNhaCungCap LIMIT 1 OFFSET 0) AS MaNCC,
    '2026-03-26 08:30:00' AS NgayNhap,
    'Nguyen Van Giao' AS NguoiGiao,
    0 AS TongTienNhap,
    'CHO_DUYET' AS TrangThai
  UNION ALL
  SELECT
    'PN027',
    (SELECT MaNhaCungCap FROM NhaCungCap WHERE Active = 1 ORDER BY MaNhaCungCap LIMIT 1 OFFSET 1),
    '2026-03-26 09:15:00',
    'Tran Thi Giao',
    0,
    'CHO_DUYET'
  UNION ALL
  SELECT
    'PN028',
    (SELECT MaNhaCungCap FROM NhaCungCap WHERE Active = 1 ORDER BY MaNhaCungCap LIMIT 1 OFFSET 2),
    '2026-03-26 10:00:00',
    'Le Van Giao',
    0,
    'CHO_DUYET'
  UNION ALL
  SELECT
    'PN029',
    (SELECT MaNhaCungCap FROM NhaCungCap WHERE Active = 1 ORDER BY MaNhaCungCap LIMIT 1 OFFSET 3),
    '2026-03-26 10:45:00',
    'Pham Van Giao',
    0,
    'CHO_DUYET'
  UNION ALL
  SELECT
    'PN030',
    (SELECT MaNhaCungCap FROM NhaCungCap WHERE Active = 1 ORDER BY MaNhaCungCap LIMIT 1 OFFSET 4),
    '2026-03-26 11:30:00',
    'Hoang Van Giao',
    0,
    'CHO_DUYET'
) seed
WHERE seed.MaNCC IS NOT NULL;

-- Chi tiet cho PN026
INSERT INTO ChiTietPhieuNhap (
  MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung
)
SELECT *
FROM (
  SELECT
    'CTPN02601' AS MaCTPN,
    'PN026' AS MaPhieuNhap,
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 0) AS MaThuoc,
    'LO-PN026-01' AS SoLo,
    120 AS SoLuongNhap,
    120 AS SoLuongConLai,
    1800 AS DonGiaNhap,
    '2027-06-30' AS HanSuDung
  UNION ALL
  SELECT
    'CTPN02602',
    'PN026',
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 1),
    'LO-PN026-02',
    80,
    80,
    2600,
    '2027-09-30'
) s
WHERE EXISTS (SELECT 1 FROM PhieuNhap WHERE MaPhieuNhap = 'PN026')
  AND s.MaThuoc IS NOT NULL;

-- Chi tiet cho PN027
INSERT INTO ChiTietPhieuNhap (
  MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung
)
SELECT *
FROM (
  SELECT
    'CTPN02701' AS MaCTPN,
    'PN027' AS MaPhieuNhap,
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 2) AS MaThuoc,
    'LO-PN027-01' AS SoLo,
    150 AS SoLuongNhap,
    150 AS SoLuongConLai,
    2100 AS DonGiaNhap,
    '2027-08-31' AS HanSuDung
  UNION ALL
  SELECT
    'CTPN02702',
    'PN027',
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 3),
    'LO-PN027-02',
    60,
    60,
    3400,
    '2028-01-31'
) s
WHERE EXISTS (SELECT 1 FROM PhieuNhap WHERE MaPhieuNhap = 'PN027')
  AND s.MaThuoc IS NOT NULL;

-- Chi tiet cho PN028
INSERT INTO ChiTietPhieuNhap (
  MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung
)
SELECT *
FROM (
  SELECT
    'CTPN02801' AS MaCTPN,
    'PN028' AS MaPhieuNhap,
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 4) AS MaThuoc,
    'LO-PN028-01' AS SoLo,
    90 AS SoLuongNhap,
    90 AS SoLuongConLai,
    4200 AS DonGiaNhap,
    '2027-12-31' AS HanSuDung
  UNION ALL
  SELECT
    'CTPN02802',
    'PN028',
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 5),
    'LO-PN028-02',
    70,
    70,
    3900,
    '2027-10-31'
) s
WHERE EXISTS (SELECT 1 FROM PhieuNhap WHERE MaPhieuNhap = 'PN028')
  AND s.MaThuoc IS NOT NULL;

-- Chi tiet cho PN029
INSERT INTO ChiTietPhieuNhap (
  MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung
)
SELECT *
FROM (
  SELECT
    'CTPN02901' AS MaCTPN,
    'PN029' AS MaPhieuNhap,
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 6) AS MaThuoc,
    'LO-PN029-01' AS SoLo,
    100 AS SoLuongNhap,
    100 AS SoLuongConLai,
    5200 AS DonGiaNhap,
    '2028-03-31' AS HanSuDung
  UNION ALL
  SELECT
    'CTPN02902',
    'PN029',
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 7),
    'LO-PN029-02',
    55,
    55,
    6100,
    '2028-05-31'
) s
WHERE EXISTS (SELECT 1 FROM PhieuNhap WHERE MaPhieuNhap = 'PN029')
  AND s.MaThuoc IS NOT NULL;

-- Chi tiet cho PN030
INSERT INTO ChiTietPhieuNhap (
  MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung
)
SELECT *
FROM (
  SELECT
    'CTPN03001' AS MaCTPN,
    'PN030' AS MaPhieuNhap,
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 8) AS MaThuoc,
    'LO-PN030-01' AS SoLo,
    130 AS SoLuongNhap,
    130 AS SoLuongConLai,
    2900 AS DonGiaNhap,
    '2027-11-30' AS HanSuDung
  UNION ALL
  SELECT
    'CTPN03002',
    'PN030',
    (SELECT MaThuoc FROM Thuoc WHERE Active = 1 ORDER BY MaThuoc LIMIT 1 OFFSET 9),
    'LO-PN030-02',
    65,
    65,
    4700,
    '2028-02-29'
) s
WHERE EXISTS (SELECT 1 FROM PhieuNhap WHERE MaPhieuNhap = 'PN030')
  AND s.MaThuoc IS NOT NULL;

-- Cap nhat tong tien cho tung phieu
UPDATE PhieuNhap pn
JOIN (
  SELECT MaPhieuNhap, SUM(SoLuongNhap * DonGiaNhap) AS TongTien
  FROM ChiTietPhieuNhap
  WHERE MaPhieuNhap IN ('PN026', 'PN027', 'PN028', 'PN029', 'PN030')
  GROUP BY MaPhieuNhap
) x ON x.MaPhieuNhap = pn.MaPhieuNhap
SET pn.TongTienNhap = x.TongTien,
    pn.TrangThai = 'CHO_DUYET';

COMMIT;

-- Kiem tra nhanh
SELECT MaPhieuNhap, MaNCC, NgayNhap, NguoiGiao, TongTienNhap, TrangThai
FROM PhieuNhap
WHERE MaPhieuNhap IN ('PN026', 'PN027', 'PN028', 'PN029', 'PN030')
ORDER BY MaPhieuNhap;

SELECT MaCTPN, MaPhieuNhap, MaThuoc, SoLo, SoLuongNhap, SoLuongConLai, DonGiaNhap, HanSuDung
FROM ChiTietPhieuNhap
WHERE MaPhieuNhap IN ('PN026', 'PN027', 'PN028', 'PN029', 'PN030')
ORDER BY MaPhieuNhap, MaCTPN;
