-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: tramway.proxy.rlwy.net    Database: PhongKham
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `BacSi`
--

DROP TABLE IF EXISTS `BacSi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BacSi` (
  `MaBacSi` int NOT NULL AUTO_INCREMENT,
  `HoTen` varchar(255) NOT NULL,
  `MaKhoa` int NOT NULL,
  `SoDienThoai` varchar(20) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaBacSi`),
  KEY `MaKhoa` (`MaKhoa`),
  CONSTRAINT `BacSi_ibfk_1` FOREIGN KEY (`MaKhoa`) REFERENCES `Khoa` (`MaKhoa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BacSi`
--

/*!40000 ALTER TABLE `BacSi` DISABLE KEYS */;
/*!40000 ALTER TABLE `BacSi` ENABLE KEYS */;

--
-- Table structure for table `ChiTietDonThuoc`
--

DROP TABLE IF EXISTS `ChiTietDonThuoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChiTietDonThuoc` (
  `MaCTDT` int NOT NULL AUTO_INCREMENT,
  `MaDonThuoc` int NOT NULL,
  `MaThuoc` varchar(20) NOT NULL,
  `SoLuong` int DEFAULT NULL,
  `LieuDung` varchar(255) DEFAULT NULL,
  `CachDung` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MaCTDT`),
  KEY `MaDonThuoc` (`MaDonThuoc`),
  KEY `ChiTietDonThuoc_ibfk_2` (`MaThuoc`),
  CONSTRAINT `ChiTietDonThuoc_ibfk_1` FOREIGN KEY (`MaDonThuoc`) REFERENCES `DonThuoc` (`MaDonThuoc`),
  CONSTRAINT `ChiTietDonThuoc_ibfk_2` FOREIGN KEY (`MaThuoc`) REFERENCES `Thuoc` (`MaThuoc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChiTietDonThuoc`
--

/*!40000 ALTER TABLE `ChiTietDonThuoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChiTietDonThuoc` ENABLE KEYS */;

--
-- Table structure for table `ChiTietHoaDonThuoc`
--

DROP TABLE IF EXISTS `ChiTietHoaDonThuoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChiTietHoaDonThuoc` (
  `MaCTHD` int NOT NULL AUTO_INCREMENT,
  `MaHDThuoc` int NOT NULL,
  `MaThuoc` varchar(20) NOT NULL,
  `SoLuongMua` int DEFAULT NULL,
  `DonGiaBan` decimal(18,2) DEFAULT NULL,
  `ThanhTien` decimal(18,2) DEFAULT NULL,
  PRIMARY KEY (`MaCTHD`),
  KEY `MaHDThuoc` (`MaHDThuoc`),
  KEY `ChiTietHoaDonThuoc_ibfk_2` (`MaThuoc`),
  CONSTRAINT `ChiTietHoaDonThuoc_ibfk_1` FOREIGN KEY (`MaHDThuoc`) REFERENCES `HoaDonThuoc` (`MaHDThuoc`),
  CONSTRAINT `ChiTietHoaDonThuoc_ibfk_2` FOREIGN KEY (`MaThuoc`) REFERENCES `Thuoc` (`MaThuoc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChiTietHoaDonThuoc`
--

/*!40000 ALTER TABLE `ChiTietHoaDonThuoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChiTietHoaDonThuoc` ENABLE KEYS */;

--
-- Table structure for table `ChiTietPhieuNhap`
--

DROP TABLE IF EXISTS `ChiTietPhieuNhap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChiTietPhieuNhap` (
  `MaCTPN` varchar(20) NOT NULL,
  `MaPhieuNhap` varchar(20) NOT NULL,
  `MaThuoc` varchar(20) NOT NULL,
  `SoLuongNhap` int DEFAULT NULL,
  `DonGiaNhap` decimal(18,2) DEFAULT NULL,
  `HanSuDung` date DEFAULT NULL,
  PRIMARY KEY (`MaCTPN`),
  KEY `fk_ctpn_phieunhap` (`MaPhieuNhap`),
  KEY `fk_ctpn_thuoc` (`MaThuoc`),
  CONSTRAINT `fk_ctpn_phieunhap` FOREIGN KEY (`MaPhieuNhap`) REFERENCES `PhieuNhap` (`MaPhieuNhap`),
  CONSTRAINT `fk_ctpn_thuoc` FOREIGN KEY (`MaThuoc`) REFERENCES `Thuoc` (`MaThuoc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChiTietPhieuNhap`
--

/*!40000 ALTER TABLE `ChiTietPhieuNhap` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChiTietPhieuNhap` ENABLE KEYS */;

--
-- Table structure for table `DonThuoc`
--

DROP TABLE IF EXISTS `DonThuoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DonThuoc` (
  `MaDonThuoc` int NOT NULL AUTO_INCREMENT,
  `MaHoSo` int NOT NULL,
  `NgayKeDon` datetime DEFAULT NULL,
  `GhiChu` text,
  PRIMARY KEY (`MaDonThuoc`),
  KEY `MaHoSo` (`MaHoSo`),
  CONSTRAINT `DonThuoc_ibfk_1` FOREIGN KEY (`MaHoSo`) REFERENCES `HoSoBenhAn` (`MaHoSo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DonThuoc`
--

/*!40000 ALTER TABLE `DonThuoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `DonThuoc` ENABLE KEYS */;

--
-- Table structure for table `GoiDichVu`
--

DROP TABLE IF EXISTS `GoiDichVu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `GoiDichVu` (
  `MaGoi` varchar(20) NOT NULL,
  `TenGoi` varchar(255) NOT NULL,
  `GiaDichVu` decimal(18,2) NOT NULL,
  `ThoiGianKham` int DEFAULT NULL,
  `MoTa` text,
  PRIMARY KEY (`MaGoi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GoiDichVu`
--

/*!40000 ALTER TABLE `GoiDichVu` DISABLE KEYS */;
/*!40000 ALTER TABLE `GoiDichVu` ENABLE KEYS */;

--
-- Table structure for table `HoSoBenhAn`
--

DROP TABLE IF EXISTS `HoSoBenhAn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HoSoBenhAn` (
  `MaHoSo` int NOT NULL AUTO_INCREMENT,
  `MaPhieuKham` varchar(20) NOT NULL,
  `NgayKham` datetime DEFAULT NULL,
  `ChanDoan` text,
  `KetLuan` text,
  `LoiDan` text,
  PRIMARY KEY (`MaHoSo`),
  KEY `fk_hsba_phieukham` (`MaPhieuKham`),
  CONSTRAINT `fk_hsba_phieukham` FOREIGN KEY (`MaPhieuKham`) REFERENCES `PhieuKham` (`MaPhieuKham`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HoSoBenhAn`
--

/*!40000 ALTER TABLE `HoSoBenhAn` DISABLE KEYS */;
/*!40000 ALTER TABLE `HoSoBenhAn` ENABLE KEYS */;

--
-- Table structure for table `HoaDonKham`
--

DROP TABLE IF EXISTS `HoaDonKham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HoaDonKham` (
  `MaHDKham` varchar(20) NOT NULL,
  `MaPhieuKham` varchar(20) NOT NULL,
  `MaGoi` varchar(20) DEFAULT NULL,
  `NgayThanhToan` datetime DEFAULT NULL,
  `TongTien` decimal(18,2) DEFAULT NULL,
  `HinhThucThanhToan` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`MaHDKham`),
  KEY `fk_hdk_phieukham` (`MaPhieuKham`),
  KEY `HoaDonKham_ibfk_2` (`MaGoi`),
  CONSTRAINT `fk_hdk_phieukham` FOREIGN KEY (`MaPhieuKham`) REFERENCES `PhieuKham` (`MaPhieuKham`),
  CONSTRAINT `HoaDonKham_ibfk_2` FOREIGN KEY (`MaGoi`) REFERENCES `GoiDichVu` (`MaGoi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HoaDonKham`
--

/*!40000 ALTER TABLE `HoaDonKham` DISABLE KEYS */;
/*!40000 ALTER TABLE `HoaDonKham` ENABLE KEYS */;

--
-- Table structure for table `HoaDonThuoc`
--

DROP TABLE IF EXISTS `HoaDonThuoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HoaDonThuoc` (
  `MaHDThuoc` int NOT NULL AUTO_INCREMENT,
  `MaDonThuoc` int NOT NULL,
  `NgayMua` datetime DEFAULT NULL,
  `TongTien` decimal(18,2) DEFAULT NULL,
  `HinhThucThanhToan` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`MaHDThuoc`),
  KEY `MaDonThuoc` (`MaDonThuoc`),
  CONSTRAINT `HoaDonThuoc_ibfk_1` FOREIGN KEY (`MaDonThuoc`) REFERENCES `DonThuoc` (`MaDonThuoc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HoaDonThuoc`
--

/*!40000 ALTER TABLE `HoaDonThuoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `HoaDonThuoc` ENABLE KEYS */;

--
-- Table structure for table `Khoa`
--

DROP TABLE IF EXISTS `Khoa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Khoa` (
  `MaKhoa` int NOT NULL AUTO_INCREMENT,
  `TenKhoa` varchar(255) NOT NULL,
  PRIMARY KEY (`MaKhoa`),
  UNIQUE KEY `TenKhoa` (`TenKhoa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Khoa`
--

/*!40000 ALTER TABLE `Khoa` DISABLE KEYS */;
/*!40000 ALTER TABLE `Khoa` ENABLE KEYS */;

--
-- Table structure for table `LichKham`
--

DROP TABLE IF EXISTS `LichKham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LichKham` (
  `MaLichKham` int NOT NULL AUTO_INCREMENT,
  `MaGoi` varchar(20) DEFAULT NULL,
  `MaBacSi` int NOT NULL,
  `ThoiGianBatDau` datetime NOT NULL,
  `ThoiGianKetThuc` datetime DEFAULT NULL,
  `TrangThai` varchar(100) DEFAULT NULL,
  `MaDinhDanhTam` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`MaLichKham`),
  KEY `MaBacSi` (`MaBacSi`),
  KEY `LichKham_ibfk_1` (`MaGoi`),
  CONSTRAINT `LichKham_ibfk_1` FOREIGN KEY (`MaGoi`) REFERENCES `GoiDichVu` (`MaGoi`),
  CONSTRAINT `LichKham_ibfk_2` FOREIGN KEY (`MaBacSi`) REFERENCES `BacSi` (`MaBacSi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LichKham`
--

/*!40000 ALTER TABLE `LichKham` DISABLE KEYS */;
/*!40000 ALTER TABLE `LichKham` ENABLE KEYS */;

--
-- Table structure for table `LichLamViec`
--

DROP TABLE IF EXISTS `LichLamViec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LichLamViec` (
  `MaLich` int NOT NULL AUTO_INCREMENT,
  `MaBacSi` int NOT NULL,
  `NgayLam` date NOT NULL,
  `CaLam` varchar(50) DEFAULT NULL,
  `GhiChu` text,
  PRIMARY KEY (`MaLich`),
  KEY `MaBacSi` (`MaBacSi`),
  CONSTRAINT `LichLamViec_ibfk_1` FOREIGN KEY (`MaBacSi`) REFERENCES `BacSi` (`MaBacSi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LichLamViec`
--

/*!40000 ALTER TABLE `LichLamViec` DISABLE KEYS */;
/*!40000 ALTER TABLE `LichLamViec` ENABLE KEYS */;

--
-- Table structure for table `NhaCungCap`
--

DROP TABLE IF EXISTS `NhaCungCap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NhaCungCap` (
  `MaNCC` int NOT NULL AUTO_INCREMENT,
  `TenNCC` varchar(255) DEFAULT NULL,
  `DiaChi` varchar(255) DEFAULT NULL,
  `SoDienThoai` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`MaNCC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NhaCungCap`
--

/*!40000 ALTER TABLE `NhaCungCap` DISABLE KEYS */;
/*!40000 ALTER TABLE `NhaCungCap` ENABLE KEYS */;

--
-- Table structure for table `PhieuKham`
--

DROP TABLE IF EXISTS `PhieuKham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PhieuKham` (
  `MaPhieuKham` varchar(20) NOT NULL,
  `MaLichKham` int NOT NULL,
  `MaBacSi` int NOT NULL,
  `ThoiGianVao` datetime DEFAULT NULL,
  `TrieuChungSoBo` text,
  PRIMARY KEY (`MaPhieuKham`),
  KEY `MaLichKham` (`MaLichKham`),
  KEY `MaBacSi` (`MaBacSi`),
  CONSTRAINT `PhieuKham_ibfk_1` FOREIGN KEY (`MaLichKham`) REFERENCES `LichKham` (`MaLichKham`),
  CONSTRAINT `PhieuKham_ibfk_2` FOREIGN KEY (`MaBacSi`) REFERENCES `BacSi` (`MaBacSi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PhieuKham`
--

/*!40000 ALTER TABLE `PhieuKham` DISABLE KEYS */;
/*!40000 ALTER TABLE `PhieuKham` ENABLE KEYS */;

--
-- Table structure for table `PhieuNhap`
--

DROP TABLE IF EXISTS `PhieuNhap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PhieuNhap` (
  `MaPhieuNhap` varchar(20) NOT NULL,
  `MaNCC` int NOT NULL,
  `NgayNhap` datetime DEFAULT NULL,
  `NguoiNhap` varchar(255) DEFAULT NULL,
  `TongTienNhap` decimal(18,2) DEFAULT NULL,
  PRIMARY KEY (`MaPhieuNhap`),
  KEY `MaNCC` (`MaNCC`),
  CONSTRAINT `PhieuNhap_ibfk_1` FOREIGN KEY (`MaNCC`) REFERENCES `NhaCungCap` (`MaNCC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PhieuNhap`
--

/*!40000 ALTER TABLE `PhieuNhap` DISABLE KEYS */;
/*!40000 ALTER TABLE `PhieuNhap` ENABLE KEYS */;

--
-- Table structure for table `Thuoc`
--

DROP TABLE IF EXISTS `Thuoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Thuoc` (
  `MaThuoc` varchar(20) NOT NULL,
  `TenThuoc` varchar(255) NOT NULL,
  `HoatChat` varchar(255) DEFAULT NULL,
  `DonViTinh` varchar(50) DEFAULT NULL,
  `DonGiaBan` decimal(18,2) DEFAULT NULL,
  `SoLuongTon` int DEFAULT NULL,
  PRIMARY KEY (`MaThuoc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Thuoc`
--

/*!40000 ALTER TABLE `Thuoc` DISABLE KEYS */;
/*!40000 ALTER TABLE `Thuoc` ENABLE KEYS */;

--
-- Dumping routines for database 'PhongKham'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-29 11:08:41
