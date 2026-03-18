USE PhongKham;

START TRANSACTION;

-- 1) Ensure all permission keys used by MainFrame + SidePanel exist
INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'DASHBOARD_VIEW', 'Xem dashboard', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'DASHBOARD_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'KHOA_VIEW', 'Xem khoa', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'KHOA_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'THUOC_VIEW', 'Xem thuoc', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'THUOC_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'THUOC_MANAGE', 'Quan ly thuoc', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'THUOC_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'GOIDICHVU_VIEW', 'Xem goi dich vu', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'GOIDICHVU_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'GOIDICHVU_MANAGE', 'Quan ly goi dich vu', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'GOIDICHVU_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'NCC_VIEW', 'Xem nha cung cap', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'NCC_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'NCC_MANAGE', 'Quan ly nha cung cap', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'NCC_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'PHIEUNHAP_VIEW', 'Xem phieu nhap', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'PHIEUNHAP_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'PHIEUNHAP_MANAGE', 'Quan ly phieu nhap', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'PHIEUNHAP_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'HOADONTHUOC_VIEW', 'Xem hoa don thuoc', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'HOADONTHUOC_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'HOADONTHUOC_CREATE', 'Tao hoa don thuoc', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'HOADONTHUOC_CREATE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'HOADONTHUOC_MANAGE', 'Quan ly hoa don thuoc', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'HOADONTHUOC_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'HOADONKHAM_VIEW', 'Xem hoa don kham', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'HOADONKHAM_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'HOADONKHAM_MANAGE', 'Quan ly hoa don kham', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'HOADONKHAM_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'PHANQUYEN_VIEW', 'Xem phan quyen', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'PHANQUYEN_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'ROLE_PERMISSION_MANAGE', 'Quan ly role va permission', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'ROLE_PERMISSION_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'USER_MANAGE', 'Quan ly nguoi dung', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'USER_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'LICHLAMVIEC_VIEW', 'Xem lich lam viec', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'LICHLAMVIEC_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'LICHLAMVIEC_APPROVE', 'Duyet lich lam viec', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'LICHLAMVIEC_APPROVE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'LICHKHAM_VIEW', 'Xem lich kham', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'LICHKHAM_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'LICHKHAM_MANAGE', 'Quan ly lich kham', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'LICHKHAM_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'KHAMBENH_CREATE', 'Thuc hien kham benh', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'KHAMBENH_CREATE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'HOSO_MANAGE', 'Quan ly ho so benh an', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'HOSO_MANAGE');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'BACSI_PROFILE_VIEW', 'Xem ho so bac si', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'BACSI_PROFILE_VIEW');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'GUEST_BOOKING', 'Dat lich kham guest', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'GUEST_BOOKING');

INSERT INTO Permissions (TenPermission, MoTa, Active)
SELECT 'GUEST_BUY_THUOC', 'Mua thuoc guest', 1
WHERE NOT EXISTS (SELECT 1 FROM Permissions WHERE UPPER(TenPermission) = 'GUEST_BUY_THUOC');

-- 2) Normalize to active for all UI permissions
UPDATE Permissions
SET Active = 1
WHERE UPPER(TenPermission) IN (
  'DASHBOARD_VIEW', 'KHOA_VIEW', 'THUOC_VIEW', 'THUOC_MANAGE',
  'GOIDICHVU_VIEW', 'GOIDICHVU_MANAGE', 'NCC_VIEW', 'NCC_MANAGE',
  'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE', 'HOADONTHUOC_VIEW', 'HOADONTHUOC_CREATE',
  'HOADONTHUOC_MANAGE', 'HOADONKHAM_VIEW', 'HOADONKHAM_MANAGE',
  'PHANQUYEN_VIEW', 'ROLE_PERMISSION_MANAGE', 'USER_MANAGE',
  'LICHLAMVIEC_VIEW', 'LICHLAMVIEC_APPROVE', 'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
  'KHAMBENH_CREATE', 'HOSO_MANAGE', 'BACSI_PROFILE_VIEW', 'GUEST_BOOKING', 'GUEST_BUY_THUOC'
);

-- 2.1) Remove permissions not used by MainFrame/SidePanel
DELETE rp
FROM RolePermissions rp
LEFT JOIN Permissions p ON p.MaPermission = rp.MaPermission
WHERE p.MaPermission IS NULL
   OR UPPER(p.TenPermission) NOT IN (
    'DASHBOARD_VIEW', 'KHOA_VIEW', 'THUOC_VIEW', 'THUOC_MANAGE',
    'GOIDICHVU_VIEW', 'GOIDICHVU_MANAGE', 'NCC_VIEW', 'NCC_MANAGE',
    'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE', 'HOADONTHUOC_VIEW', 'HOADONTHUOC_CREATE',
    'HOADONTHUOC_MANAGE', 'HOADONKHAM_VIEW', 'HOADONKHAM_MANAGE',
    'PHANQUYEN_VIEW', 'ROLE_PERMISSION_MANAGE', 'USER_MANAGE',
    'LICHLAMVIEC_VIEW', 'LICHLAMVIEC_APPROVE', 'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
    'KHAMBENH_CREATE', 'HOSO_MANAGE', 'BACSI_PROFILE_VIEW', 'GUEST_BOOKING', 'GUEST_BUY_THUOC'
  );

DELETE FROM Permissions
WHERE UPPER(TenPermission) NOT IN (
  'DASHBOARD_VIEW', 'KHOA_VIEW', 'THUOC_VIEW', 'THUOC_MANAGE',
  'GOIDICHVU_VIEW', 'GOIDICHVU_MANAGE', 'NCC_VIEW', 'NCC_MANAGE',
  'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE', 'HOADONTHUOC_VIEW', 'HOADONTHUOC_CREATE',
  'HOADONTHUOC_MANAGE', 'HOADONKHAM_VIEW', 'HOADONKHAM_MANAGE',
  'PHANQUYEN_VIEW', 'ROLE_PERMISSION_MANAGE', 'USER_MANAGE',
  'LICHLAMVIEC_VIEW', 'LICHLAMVIEC_APPROVE', 'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
  'KHAMBENH_CREATE', 'HOSO_MANAGE', 'BACSI_PROFILE_VIEW', 'GUEST_BOOKING', 'GUEST_BUY_THUOC'
);

-- 3) Admin (RoleID=1) gets all active permissions
DELETE FROM RolePermissions WHERE MaRole = 1;
INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, p.MaPermission, 1
FROM Permissions p
WHERE p.Active = 1;

COMMIT;

SELECT p.TenPermission
FROM Permissions p
WHERE UPPER(p.TenPermission) IN (
  'DASHBOARD_VIEW', 'KHOA_VIEW', 'THUOC_VIEW', 'THUOC_MANAGE',
  'GOIDICHVU_VIEW', 'GOIDICHVU_MANAGE', 'NCC_VIEW', 'NCC_MANAGE',
  'PHIEUNHAP_VIEW', 'PHIEUNHAP_MANAGE', 'HOADONTHUOC_VIEW', 'HOADONTHUOC_CREATE',
  'HOADONTHUOC_MANAGE', 'HOADONKHAM_VIEW', 'HOADONKHAM_MANAGE',
  'PHANQUYEN_VIEW', 'ROLE_PERMISSION_MANAGE', 'USER_MANAGE',
  'LICHLAMVIEC_VIEW', 'LICHLAMVIEC_APPROVE', 'LICHKHAM_VIEW', 'LICHKHAM_MANAGE',
  'KHAMBENH_CREATE', 'HOSO_MANAGE', 'BACSI_PROFILE_VIEW', 'GUEST_BOOKING', 'GUEST_BUY_THUOC'
)
ORDER BY p.TenPermission;

SELECT COUNT(*) AS admin_permission_count
FROM RolePermissions
WHERE MaRole = 1 AND Active = 1;
