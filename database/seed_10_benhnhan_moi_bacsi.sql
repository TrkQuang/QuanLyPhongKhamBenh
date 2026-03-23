USE PhongKham;

-- Seed du lieu hop le theo rang buoc hien tai:
-- - 10 benh nhan cho moi BacSi
-- - Tao LichLamViec DA_DUYET truoc khi tao LichKham (de qua trigger)
-- - Tao day du HoSoBenhAn, DonThuoc, CTDonThuoc, HoaDonKham, HoaDonThuoc, CTHDThuoc
-- Luu y: Script co the chay nhieu lan, ma du lieu moi duoc sinh voi ID moi.

SET @old_sql_safe_updates := @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_seed_10_benhnhan_moi_bacsi $$
CREATE PROCEDURE sp_seed_10_benhnhan_moi_bacsi()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE v_maBacSi VARCHAR(20);
  DECLARE v_maKhoa VARCHAR(20);

  DECLARE v_maGoi VARCHAR(20);
  DECLARE v_maThuoc VARCHAR(20);
  DECLARE v_giaGoi DECIMAL(18,2);
  DECLARE v_donGiaThuoc DECIMAL(18,2);

  DECLARE v_i INT;
  DECLARE v_doc_seq INT DEFAULT 0;

  DECLARE v_ngay DATE;
  DECLARE v_start DATETIME;
  DECLARE v_end DATETIME;

  DECLARE v_maLichLam VARCHAR(20);
  DECLARE v_maLichKham VARCHAR(20);
  DECLARE v_maHoSo VARCHAR(20);
  DECLARE v_maDonThuoc VARCHAR(20);
  DECLARE v_maCTDonThuoc VARCHAR(20);
  DECLARE v_maHDKham VARCHAR(20);
  DECLARE v_maHoaDonThuoc VARCHAR(20);
  DECLARE v_maCTHDThuoc VARCHAR(20);

  DECLARE v_soLuongThuoc INT;
  DECLARE v_thanhTienThuoc DECIMAL(18,2);
  DECLARE v_hoTen VARCHAR(120);
  DECLARE v_sdt VARCHAR(20);
  DECLARE v_cccd VARCHAR(20);

  DECLARE cur_bacsi CURSOR FOR
    SELECT MaBacSi, MaKhoa
    FROM BacSi
    ORDER BY MaBacSi;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  START TRANSACTION;

  -- Neu khong co goi dich vu cho khoa cua BacSi thi tao tam de dam bao FK/trigger hop le.
  -- Neu khong co thuoc thi tao bo thuoc mau de tao CTDonThuoc va CTHDThuoc.
  IF (SELECT COUNT(*) FROM Thuoc) = 0 THEN
    INSERT INTO Thuoc (MaThuoc, TenThuoc, HoatChat, DonViTinh, DonVi, DonGiaBan, SoLuongTon, Active)
    VALUES
      ('TSEED001', 'Paracetamol 500mg', 'Paracetamol', 'Vien', 'Hop', 1500, 500, 1),
      ('TSEED002', 'Amoxicillin 500mg', 'Amoxicillin', 'Vien', 'Hop', 2500, 500, 1),
      ('TSEED003', 'Vitamin C 500mg', 'Ascorbic Acid', 'Vien', 'Hop', 1200, 500, 1),
      ('TSEED004', 'Loratadine 10mg', 'Loratadine', 'Vien', 'Hop', 2200, 500, 1),
      ('TSEED005', 'Omeprazole 20mg', 'Omeprazole', 'Vien', 'Hop', 2800, 500, 1);
  END IF;

  OPEN cur_bacsi;

  bacsi_loop: LOOP
    FETCH cur_bacsi INTO v_maBacSi, v_maKhoa;
    IF done = 1 THEN
      LEAVE bacsi_loop;
    END IF;

    SET v_doc_seq = v_doc_seq + 1;

    SELECT MaGoi, GiaDichVu
      INTO v_maGoi, v_giaGoi
    FROM GoiDichVu
    WHERE MaKhoa = v_maKhoa
    ORDER BY MaGoi
    LIMIT 1;

    IF v_maGoi IS NULL THEN
      SET v_maGoi = CONCAT('GOI', LPAD(v_doc_seq, 4, '0'));
      INSERT INTO GoiDichVu (MaGoi, TenGoi, GiaDichVu, ThoiGianKham, MoTa, MaKhoa)
      VALUES (v_maGoi, CONCAT('Goi kham co ban ', v_maKhoa), 200000, 30, 'Auto seed for testing', v_maKhoa);
      SET v_giaGoi = 200000;
    END IF;

    SET v_i = 1;
    per_doc_loop: LOOP
      IF v_i > 10 THEN
        LEAVE per_doc_loop;
      END IF;

      -- Chon ngay trong qua khu de du lieu hop ly va de demo lich su.
      SET v_ngay = DATE_SUB(CURDATE(), INTERVAL (v_i + v_doc_seq * 2) DAY);
      SET v_start = TIMESTAMP(v_ngay, '09:00:00');
      SET v_end = TIMESTAMP(v_ngay, '09:30:00');

      -- Tao lich lam viec DA_DUYET (bat buoc de trigger LichKham pass).
      SET v_maLichLam = CONCAT('LL', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 1000 + v_i), 12, '0'));
      INSERT INTO LichLamViec (MaLichLam, MaBacSi, NgayLam, CaLam, TrangThai)
      VALUES (v_maLichLam, v_maBacSi, v_ngay, 'Sang', 'DA_DUYET')
      ON DUPLICATE KEY UPDATE TrangThai = 'DA_DUYET';

      -- ID dong bo theo tung lan tao.
      SET v_maLichKham = CONCAT('LK', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 10000 + v_i), 12, '0'));
      SET v_maHoSo = CONCAT('HS', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 20000 + v_i), 12, '0'));
      SET v_maDonThuoc = CONCAT('DT', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 30000 + v_i), 12, '0'));
      SET v_maCTDonThuoc = CONCAT('CD', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 40000 + v_i), 12, '0'));
      SET v_maHDKham = CONCAT('HK', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 50000 + v_i), 12, '0'));
      SET v_maHoaDonThuoc = CONCAT('HT', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 60000 + v_i), 12, '0'));
      SET v_maCTHDThuoc = CONCAT('CT', LPAD((UNIX_TIMESTAMP(NOW(6)) + v_doc_seq * 70000 + v_i), 12, '0'));

      SET v_hoTen = CONCAT('BenhNhan_', v_doc_seq, '_', LPAD(v_i, 2, '0'));
      SET v_sdt = CONCAT('09', LPAD(10000000 + (v_doc_seq * 100 + v_i), 8, '0'));
      SET v_cccd = CAST((100000000000 + v_doc_seq * 100 + v_i) AS CHAR);

      -- Chon 1 thuoc ngau nhien tu danh muc dang active.
      SELECT MaThuoc, DonGiaBan
        INTO v_maThuoc, v_donGiaThuoc
      FROM Thuoc
      WHERE Active = 1
      ORDER BY RAND()
      LIMIT 1;

      IF v_maThuoc IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Khong co Thuoc active de tao du lieu DonThuoc/HoaDonThuoc';
      END IF;

      SET v_soLuongThuoc = (v_i MOD 3) + 1;
      SET v_thanhTienThuoc = v_soLuongThuoc * v_donGiaThuoc;

      INSERT INTO LichKham (MaLichKham, MaGoi, MaBacSi, ThoiGianBatDau, ThoiGianKetThuc, TrangThai, MaDinhDanhTam)
      VALUES (v_maLichKham, v_maGoi, v_maBacSi, v_start, v_end, 'HOAN_THANH', CONCAT('TAM', v_doc_seq, LPAD(v_i, 2, '0')));

      INSERT INTO HoSoBenhAn (
        MaHoSo, MaLichKham, HoTen, SoDienThoai, CCCD, NgaySinh, GioiTinh, DiaChi,
        NgayKham, TrieuChung, ChanDoan, KetLuan, LoiDan, MaBacSi, TrangThai
      )
      VALUES (
        v_maHoSo,
        v_maLichKham,
        v_hoTen,
        v_sdt,
        v_cccd,
        DATE_SUB(CURDATE(), INTERVAL (20 + v_i + v_doc_seq) YEAR),
        IF(v_i % 2 = 0, 'Nam', 'Nu'),
        CONCAT('So ', v_i, ' Duong Kham Benh, Quan ', v_doc_seq),
        DATE_ADD(v_start, INTERVAL 15 MINUTE),
        CONCAT('Sot nhe va ho ngay thu ', v_i),
        IF(v_i % 2 = 0, 'Viem hong cap', 'Cam cum theo mua'),
        'Theo doi ngoai tru',
        'Uong thuoc dung lieu, tai kham sau 3-5 ngay neu khong giam',
        v_maBacSi,
        'DA_KHAM'
      );

      INSERT INTO DonThuoc (MaDonThuoc, MaHoSo, NgayKeDon, GhiChu)
      VALUES (v_maDonThuoc, v_maHoSo, DATE_ADD(v_start, INTERVAL 20 MINUTE), 'Don thuoc seed hop le');

      INSERT INTO CTDonThuoc (MaCTDonThuoc, MaDonThuoc, MaThuoc, SoLuong, LieuDung, CachDung)
      VALUES (
        v_maCTDonThuoc,
        v_maDonThuoc,
        v_maThuoc,
        v_soLuongThuoc,
        '2 lan/ngay',
        'Sau an, sang va toi'
      );

      INSERT INTO HoaDonKham (MaHDKham, MaHoSo, MaGoi, NgayThanhToan, TongTien, HinhThucThanhToan, TrangThai)
      VALUES (
        v_maHDKham,
        v_maHoSo,
        v_maGoi,
        DATE_ADD(v_start, INTERVAL 30 MINUTE),
        IFNULL(v_giaGoi, 0),
        IF(v_i % 2 = 0, 'TIEN_MAT', 'CHUYEN_KHOAN'),
        'DA_THANH_TOAN'
      );

      INSERT INTO HoaDonThuoc (
        MaHoaDon, MaDonThuoc, NgayLap, TongTien, GhiChu,
        TrangThaiThanhToan, NgayThanhToan, TrangThaiLayThuoc,
        TenBenhNhan, SdtBenhNhan, Active
      )
      VALUES (
        v_maHoaDonThuoc,
        v_maDonThuoc,
        DATE_ADD(v_start, INTERVAL 35 MINUTE),
        v_thanhTienThuoc,
        'Hoa don thuoc seed',
        'DA_THANH_TOAN',
        DATE_ADD(v_start, INTERVAL 40 MINUTE),
        'DA_HOAN_THANH',
        v_hoTen,
        v_sdt,
        1
      );

      INSERT INTO CTHDThuoc (MaCTHDThuoc, MaHoaDon, MaThuoc, SoLuong, DonGia, ThanhTien, GhiChu, Active)
      VALUES (
        v_maCTHDThuoc,
        v_maHoaDonThuoc,
        v_maThuoc,
        v_soLuongThuoc,
        v_donGiaThuoc,
        v_thanhTienThuoc,
        'Chi tiet thuoc seed',
        1
      );

      SET v_i = v_i + 1;
    END LOOP;
  END LOOP;

  CLOSE cur_bacsi;
  COMMIT;
END $$

DELIMITER ;

CALL sp_seed_10_benhnhan_moi_bacsi();
DROP PROCEDURE IF EXISTS sp_seed_10_benhnhan_moi_bacsi;

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;

-- Kiem tra nhanh ket qua seed:
-- 1) So luong HoSoBenhAn moi theo BacSi
-- SELECT MaBacSi, COUNT(*) AS SoHoSoMoi
-- FROM HoSoBenhAn
-- WHERE HoTen LIKE 'BenhNhan_%'
-- GROUP BY MaBacSi
-- ORDER BY MaBacSi;
