USE PhongKham;

START TRANSACTION;

DELETE FROM RolePermissions
WHERE MaRole = 1;

INSERT INTO RolePermissions (MaRole, MaPermission, Active)
SELECT 1, p.MaPermission, 1
FROM Permissions p
WHERE p.Active = 1;

COMMIT;

SELECT COUNT(*) AS admin_permission_count
FROM RolePermissions
WHERE MaRole = 1 AND Active = 1;
