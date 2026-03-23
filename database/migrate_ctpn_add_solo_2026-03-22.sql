USE PhongKham;

-- 1) Thêm cột số lô cho chi tiết phiếu nhập
ALTER TABLE ChiTietPhieuNhap
  ADD COLUMN IF NOT EXISTS SoLo VARCHAR(50) NULL AFTER MaThuoc;

-- 2) Điền số lô cho dữ liệu cũ (nếu thiếu)
UPDATE ChiTietPhieuNhap
SET SoLo = CONCAT(
  'LO-',
  COALESCE(MaThuoc, 'UNK'),
  '-',
  DATE_FORMAT(COALESCE(HanSuDung, CURDATE()), '%Y%m%d'),
  '-',
  COALESCE(MaCTPN, 'NA')
)
WHERE SoLo IS NULL OR TRIM(SoLo) = '';

-- 3) Cột số lô là bắt buộc
ALTER TABLE ChiTietPhieuNhap
  MODIFY COLUMN SoLo VARCHAR(50) NOT NULL;

-- 4) Chống nhập trùng lô trong cùng phiếu
ALTER TABLE ChiTietPhieuNhap
  ADD UNIQUE INDEX IF NOT EXISTS uq_ctpn_lot (MaPhieuNhap, MaThuoc, SoLo, HanSuDung);

-- 5) Tối ưu truy vấn quản lý theo HSD/NCC
ALTER TABLE ChiTietPhieuNhap
  ADD INDEX IF NOT EXISTS idx_ctpn_hsd (HanSuDung),
  ADD INDEX IF NOT EXISTS idx_ctpn_thuoc_hsd (MaThuoc, HanSuDung),
  ADD INDEX IF NOT EXISTS idx_ctpn_lot_lookup (MaThuoc, SoLo, HanSuDung);

-- 6) View phục vụ lọc nhanh theo thuốc + NCC + HSD + lô
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
