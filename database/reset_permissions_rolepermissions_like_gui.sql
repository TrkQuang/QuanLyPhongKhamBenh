USE PhongKham;

START TRANSACTION;

-- 1) Xoa du lieu cu
DELETE FROM RolePermissions;
DELETE FROM Permissions;
ALTER TABLE RolePermissions AUTO_INCREMENT = 1;
ALTER TABLE Permissions AUTO_INCREMENT = 1;

-- 2) Insert bo permission moi theo GUI hien tai
INSERT INTO Permissions (TenPermission, MoTa, Active) VALUES
('HOME_VIEW', 'Xem trang chu he thong', 1),
('GUEST_BOOKING', 'Khach dat lich kham', 1),
('GUEST_BUY_THUOC', 'Khach mua thuoc', 1),

('LICHLAMVIEC_VIEW', 'Xem lich lam viec bac si', 1),
('LICHLAMVIEC_MANAGE', 'Dang ky/cap nhat lich lam viec', 1),
('LICHKHAM_VIEW', 'Xem lich kham', 1),
('LICHKHAM_MANAGE', 'Xac nhan/chuyen trang thai lich kham', 1),
('HOADONKHAM_VIEW', 'Xem hoa don kham', 1),
('HOADONKHAM_MANAGE', 'Quan ly hoa don kham', 1),
('HOSO_VIEW', 'Xem ho so benh an', 1),
('HOSO_MANAGE', 'Quan ly ho so benh an', 1),
('BACSI_PROFILE_VIEW', 'Xem profile bac si', 1),
('BACSI_PROFILE_UPDATE_PASSWORD', 'Bac si doi mat khau', 1),

('THUOC_VIEW', 'Xem danh sach thuoc', 1),
('THUOC_MANAGE', 'Them/sua/xoa thuoc', 1),
('NCC_VIEW', 'Xem nha cung cap', 1),
('NCC_MANAGE', 'Them/sua/xoa nha cung cap', 1),
('PHIEUNHAP_VIEW', 'Xem phieu nhap', 1),
('PHIEUNHAP_MANAGE', 'Them/sua/xoa phieu nhap', 1),
('HOADONTHUOC_VIEW', 'Xem hoa don thuoc', 1),
('HOADONTHUOC_CREATE', 'Tao hoa don thuoc', 1),
('HOADONTHUOC_MANAGE', 'Quan ly hoa don thuoc', 1),

('DASHBOARD_VIEW', 'Xem dashboard admin', 1),
('USER_VIEW', 'Xem danh sach tai khoan', 1),
('USER_MANAGE', 'Them/sua/khoa-mo tai khoan', 1),
('BACSI_VIEW', 'Xem danh sach bac si', 1),
('BACSI_MANAGE', 'Them/sua/xoa bac si', 1),
('KHOA_VIEW', 'Xem khoa', 1),
('KHOA_MANAGE', 'Them/sua/xoa khoa', 1),
('GOIDICHVU_VIEW', 'Xem goi dich vu', 1),
('GOIDICHVU_MANAGE', 'Them/sua/xoa goi dich vu', 1),
('ROLE_PERMISSION_VIEW', 'Xem phan quyen', 1),
('ROLE_PERMISSION_MANAGE', 'Quan ly role-permission va gan role cho account', 1);

-- 3) Gan permission cho Role 1 (Admin): toan bo
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, p.MaPermission, 1
FROM Permissions p
WHERE p.Active = 1;

-- 4) Gan permission cho Role 2 (BacSi)
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 2, p.MaPermission, 1
FROM Permissions p
WHERE p.TenPermission IN (
  'HOME_VIEW',
  'LICHLAMVIEC_VIEW', 'LICHLAMVIEC_MANAGE',
  'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
  'HOADONKHAM_VIEW', 'HOADONKHAM_MANAGE',
  'HOSO_VIEW', 'HOSO_MANAGE',
  'BACSI_PROFILE_VIEW', 'BACSI_PROFILE_UPDATE_PASSWORD'
);

-- 5) Gan permission cho Role 3 (NhaThuoc)
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 3, p.MaPermission, 1
FROM Permissions p
WHERE p.TenPermission IN (
  'HOME_VIEW',
  'THUOC_VIEW', 'THUOC_MANAGE',
  'NCC_VIEW', 'NCC_MANAGE',
  'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE',
  'HOADONTHUOC_VIEW', 'HOADONTHUOC_CREATE', 'HOADONTHUOC_MANAGE'
);

COMMIT;

-- Kiem tra nhanh
SELECT * FROM Permissions ORDER BY MaPermission;
SELECT MaRole, COUNT(*) AS SoPermission
FROM RolePermissions
GROUP BY MaRole
ORDER BY MaRole;
