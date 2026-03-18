-- Drop MaNCC from HoaDonThuoc safely (MySQL 8+)
-- Run this once on existing databases.

SET @schema_name := DATABASE();

-- Drop FK if it exists.
SET @fk_exists := (
  SELECT COUNT(*)
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = @schema_name
    AND TABLE_NAME = 'HoaDonThuoc'
    AND CONSTRAINT_NAME = 'fk_hdthuoc_ncc'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(
  @fk_exists > 0,
  'ALTER TABLE HoaDonThuoc DROP FOREIGN KEY fk_hdthuoc_ncc',
  'SELECT "fk_hdthuoc_ncc not found"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop index on MaNCC if present.
SET @idx_exists := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'HoaDonThuoc'
    AND INDEX_NAME = 'idx_hdthuoc_ncc'
);
SET @sql := IF(
  @idx_exists > 0,
  'ALTER TABLE HoaDonThuoc DROP INDEX idx_hdthuoc_ncc',
  'SELECT "idx_hdthuoc_ncc not found"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop MaNCC column if present.
SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'HoaDonThuoc'
    AND COLUMN_NAME = 'MaNCC'
);
SET @sql := IF(
  @col_exists > 0,
  'ALTER TABLE HoaDonThuoc DROP COLUMN MaNCC',
  'SELECT "MaNCC column not found"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'drop_manhacc_from_hoadonthuoc completed' AS message;
