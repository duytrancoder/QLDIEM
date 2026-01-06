-- ============================================
-- SCRIPT DATABASE HOÀN CHỈNH CHO DỰ ÁN QLDIEM
-- Phiên bản: DATABASE.sql (Hợp nhất toàn bộ)
-- Bao gồm: Tất cả bảng, dữ liệu mẫu, và cập nhật schema
-- ============================================
-- Copy toàn bộ nội dung này và paste vào MySQL (phpMyAdmin)

-- ============================================
-- 1. XÓA DATABASE CŨ (NẾU CÓ) VÀ TẠO MỚI
-- ============================================

DROP DATABASE IF EXISTS `quanlydiem`;
CREATE DATABASE `quanlydiem` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quanlydiem`;

-- ============================================
-- 2. TẠO CÁC BẢNG
-- ============================================

-- Bảng: tblkhoa
CREATE TABLE `tblkhoa` (
  `makhoa` varchar(10) NOT NULL,
  `tenkhoa` varchar(100) NOT NULL,
  PRIMARY KEY (`makhoa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblbomon (Bộ môn)
CREATE TABLE `tblbomon` (
  `mabomon` varchar(20) NOT NULL,
  `tenbomon` varchar(100) NOT NULL,
  `cacmon` TEXT DEFAULT NULL COMMENT 'Danh sách các môn học (phân cách bởi dấu phẩy)',
  PRIMARY KEY (`mabomon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tbluser (Phải tạo trước vì giáo viên và sinh viên tham chiếu)
CREATE TABLE `tbluser` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `type` int(11) NOT NULL DEFAULT 2 COMMENT '0=Admin, 1=Giáo viên, 2=Sinh viên',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblmonhoc
CREATE TABLE `tblmonhoc` (
  `mamon` varchar(20) NOT NULL,
  `tenmon` varchar(100) NOT NULL,
  `sotinchi` int(11) NOT NULL CHECK (`sotinchi` > 0),
  PRIMARY KEY (`mamon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblgiaovien (Mỗi giáo viên thuộc 1 bộ môn và có thể dạy nhiều môn)
CREATE TABLE `tblgiaovien` (
  `magv` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `gioitinh` varchar(10) DEFAULT NULL,
  `ngaysinh` date DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `sdt` varchar(15) DEFAULT NULL,
  `makhoa` varchar(10) DEFAULT NULL,
  `mabomon` varchar(20) DEFAULT NULL COMMENT 'Bộ môn của giáo viên',
  `mamon` varchar(20) DEFAULT NULL COMMENT 'Môn học chính (legacy, deprecated)',
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`magv`),
  UNIQUE KEY `username` (`username`),
  KEY `makhoa` (`makhoa`),
  KEY `mamon` (`mamon`),
  KEY `fk_giaovien_bomon` (`mabomon`),
  CONSTRAINT `tblgiaovien_ibfk_1` FOREIGN KEY (`makhoa`) REFERENCES `tblkhoa` (`makhoa`),
  CONSTRAINT `tblgiaovien_ibfk_2` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`),
  CONSTRAINT `tblgiaovien_ibfk_3` FOREIGN KEY (`username`) REFERENCES `tbluser` (`username`),
  CONSTRAINT `fk_giaovien_bomon` FOREIGN KEY (`mabomon`) REFERENCES `tblbomon` (`mabomon`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tbl_giangday (Phân công giảng dạy - Giáo viên dạy môn học nào)
CREATE TABLE `tbl_giangday` (
  `magv` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  PRIMARY KEY (`magv`, `mamon`),
  KEY `fk_giangday_monhoc` (`mamon`),
  CONSTRAINT `fk_giangday_giaovien` FOREIGN KEY (`magv`) REFERENCES `tblgiaovien` (`magv`) ON DELETE CASCADE,
  CONSTRAINT `fk_giangday_monhoc` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblclass
CREATE TABLE `tblclass` (
  `malop` varchar(20) NOT NULL,
  `tenlop` varchar(100) NOT NULL,
  `makhoa` varchar(10) DEFAULT NULL,
  `magvcn` varchar(20) DEFAULT NULL COMMENT 'Giáo viên chủ nhiệm',
  PRIMARY KEY (`malop`),
  KEY `makhoa` (`makhoa`),
  KEY `magvcn` (`magvcn`),
  CONSTRAINT `tblclass_ibfk_1` FOREIGN KEY (`makhoa`) REFERENCES `tblkhoa` (`makhoa`),
  CONSTRAINT `tblclass_ibfk_2` FOREIGN KEY (`magvcn`) REFERENCES `tblgiaovien` (`magv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblphancong (Phân công giáo viên quản lý lớp)
CREATE TABLE `tblphancong` (
  `magv` varchar(20) NOT NULL,
  `malop` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  PRIMARY KEY (`magv`, `malop`, `mamon`),
  KEY `malop` (`malop`),
  KEY `fk_phancong_monhoc` (`mamon`),
  CONSTRAINT `tblphancong_ibfk_1` FOREIGN KEY (`magv`) REFERENCES `tblgiaovien` (`magv`),
  CONSTRAINT `tblphancong_ibfk_2` FOREIGN KEY (`malop`) REFERENCES `tblclass` (`malop`),
  CONSTRAINT `fk_phancong_monhoc` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblsinhvien
CREATE TABLE `tblsinhvien` (
  `masv` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `ngaysinh` date DEFAULT NULL,
  `gioitinh` varchar(10) DEFAULT NULL,
  `diachi` varchar(200) DEFAULT NULL,
  `malop` varchar(20) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`masv`),
  UNIQUE KEY `username` (`username`),
  KEY `malop` (`malop`),
  CONSTRAINT `tblsinhvien_ibfk_1` FOREIGN KEY (`malop`) REFERENCES `tblclass` (`malop`),
  CONSTRAINT `tblsinhvien_ibfk_2` FOREIGN KEY (`username`) REFERENCES `tbluser` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tbldiem
CREATE TABLE `tbldiem` (
  `masv` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  `hocky` int(11) NOT NULL,
  `namhoc` varchar(20) DEFAULT NULL,
  `diemcc` double DEFAULT NULL CHECK (`diemcc` BETWEEN 0 AND 10),
  `diemgk` double DEFAULT NULL CHECK (`diemgk` BETWEEN 0 AND 10),
  `diemck` double DEFAULT NULL CHECK (`diemck` BETWEEN 0 AND 10),
  `diemtongket` double DEFAULT NULL COMMENT 'Tự động tính: CC*0.1 + GK*0.3 + CK*0.6',
  PRIMARY KEY (`masv`,`mamon`,`hocky`),
  KEY `mamon` (`mamon`),
  CONSTRAINT `tbldiem_ibfk_1` FOREIGN KEY (`masv`) REFERENCES `tblsinhvien` (`masv`),
  CONSTRAINT `tbldiem_ibfk_2` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. THÊM DỮ LIỆU MẪU
-- ============================================

-- Dữ liệu: tblkhoa
INSERT INTO `tblkhoa` (`makhoa`, `tenkhoa`) VALUES
('CNTT', 'Công Nghệ Thông Tin'),
('KT', 'Kinh Tế'),
('NN', 'Ngoại Ngữ');

-- Dữ liệu: tblbomon (Bộ môn)
INSERT INTO `tblbomon` (`mabomon`, `tenbomon`, `cacmon`) VALUES
('BM01', 'Bộ môn Toán - Tin', 'MH01,MH04'),
('BM02', 'Bộ môn Ngữ văn', 'MH02'),
('BM03', 'Bộ môn Ngoại ngữ', 'MH03'),
('BM04', 'Bộ môn Khoa học tự nhiên', 'MH05');

-- Dữ liệu: tblmonhoc
INSERT INTO `tblmonhoc` (`mamon`, `tenmon`, `sotinchi`, `mabomon`) VALUES
('MH01', 'Toán', 3, 'BM01'),
('MH02', 'Văn', 3, 'BM02'),
('MH03', 'Anh', 2, 'BM03'),
('MH04', 'Lý', 3, 'BM01'),
('MH05', 'Hóa', 2, 'BM04');

-- Dữ liệu: tbluser
INSERT INTO `tbluser` (`username`, `password`, `type`) VALUES
('admin', '123456', 0),
('gv001', '123456', 1),
('gv002', '123456', 1),
('gv003', '123456', 1),
('gv004', '123456', 1),
('gv005', '123456', 1),
('sv001', '123456', 2),
('sv002', '123456', 2),
('sv003', '123456', 2),
('sv004', '123456', 2),
('sv005', '123456', 2),
('sv006', '123456', 2),
('sv007', '123456', 2),
('sv008', '123456', 2),
('sv009', '123456', 2),
('sv010', '123456', 2),
('sv011', '123456', 2),
('sv012', '123456', 2),
('sv013', '123456', 2),
('sv014', '123456', 2),
('sv015', '123456', 2);

-- Dữ liệu: tblgiaovien (Cập nhật với mabomon)
INSERT INTO `tblgiaovien` (`magv`, `hoten`, `gioitinh`, `ngaysinh`, `email`, `sdt`, `makhoa`, `mabomon`, `mamon`, `username`) VALUES
('GV001', 'Nguyễn Văn Toán', 'Nam', '1980-05-15', 'toan@abc.edu.vn', '0901234567', 'CNTT', 'BM01', 'MH01', 'gv001'),
('GV002', 'Trần Thị Văn', 'Nữ', '1985-08-20', 'van@abc.edu.vn', '0902234567', 'CNTT', 'BM02', 'MH02', 'gv002'),
('GV003', 'Lê Hoàng Anh', 'Nam', '1979-12-05', 'anh@abc.edu.vn', '0903234567', 'NN', 'BM03', 'MH03', 'gv003'),
('GV004', 'Phạm Văn Lý', 'Nam', '1982-03-10', 'ly@abc.edu.vn', '0904234567', 'CNTT', 'BM01', 'MH04', 'gv004'),
('GV005', 'Hoàng Thị Hóa', 'Nữ', '1983-07-25', 'hoa@abc.edu.vn', '0905234567', 'CNTT', 'BM04', 'MH05', 'gv005');

-- Dữ liệu: tbl_giangday (Phân công giảng dạy)
-- GV001 dạy Toán
-- GV002 dạy Văn
-- GV003 dạy Anh
-- GV004 dạy Lý và Toán (trợ giảng)
-- GV005 dạy Hóa
INSERT INTO `tbl_giangday` (`magv`, `mamon`) VALUES
('GV001', 'MH01'),
('GV002', 'MH02'),
('GV003', 'MH03'),
('GV004', 'MH04'),
('GV004', 'MH01'),
('GV005', 'MH05');

-- Dữ liệu: tblclass (3 lớp)
INSERT INTO `tblclass` (`malop`, `tenlop`, `makhoa`, `magvcn`) VALUES
('L01', 'Lớp 10A1', 'CNTT', 'GV001'),
('L02', 'Lớp 10A2', 'CNTT', 'GV002'),
('L03', 'Lớp 11A1', 'CNTT', 'GV003');

-- Dữ liệu: tblsinhvien (Mỗi lớp 5 sinh viên)
-- Lớp L01 (5 sinh viên)
INSERT INTO `tblsinhvien` (`masv`, `hoten`, `ngaysinh`, `gioitinh`, `diachi`, `malop`, `username`) VALUES
('SV001', 'Lê Văn An', '2007-01-01', 'Nam', 'Hà Nội', 'L01', 'sv001'),
('SV002', 'Nguyễn Thị Bình', '2007-02-15', 'Nữ', 'Hà Nội', 'L01', 'sv002'),
('SV003', 'Trần Văn Cường', '2007-03-20', 'Nam', 'Hà Nội', 'L01', 'sv003'),
('SV004', 'Phạm Thị Dung', '2007-04-10', 'Nữ', 'Hà Nội', 'L01', 'sv004'),
('SV005', 'Hoàng Văn Em', '2007-05-25', 'Nam', 'Hà Nội', 'L01', 'sv005');

-- Lớp L02 (5 sinh viên)
INSERT INTO `tblsinhvien` (`masv`, `hoten`, `ngaysinh`, `gioitinh`, `diachi`, `malop`, `username`) VALUES
('SV006', 'Lý Thị Phương', '2007-06-01', 'Nữ', 'Nam Định', 'L02', 'sv006'),
('SV007', 'Vũ Văn Giang', '2007-07-15', 'Nam', 'Nam Định', 'L02', 'sv007'),
('SV008', 'Đỗ Thị Hoa', '2007-08-20', 'Nữ', 'Nam Định', 'L02', 'sv008'),
('SV009', 'Bùi Văn Khoa', '2007-09-10', 'Nam', 'Nam Định', 'L02', 'sv009'),
('SV010', 'Đinh Thị Lan', '2007-10-25', 'Nữ', 'Nam Định', 'L02', 'sv010');

-- Lớp L03 (5 sinh viên)
INSERT INTO `tblsinhvien` (`masv`, `hoten`, `ngaysinh`, `gioitinh`, `diachi`, `malop`, `username`) VALUES
('SV011', 'Ngô Văn Minh', '2006-11-01', 'Nam', 'Hải Phòng', 'L03', 'sv011'),
('SV012', 'Lê Thị Nga', '2006-12-15', 'Nữ', 'Hải Phòng', 'L03', 'sv012'),
('SV013', 'Phan Văn Oanh', '2006-01-20', 'Nam', 'Hải Phòng', 'L03', 'sv013'),
('SV014', 'Võ Thị Phượng', '2006-02-10', 'Nữ', 'Hải Phòng', 'L03', 'sv014'),
('SV015', 'Đặng Văn Quang', '2006-03-25', 'Nam', 'Hải Phòng', 'L03', 'sv015');

-- Dữ liệu: tblphancong (Phân công giáo viên quản lý lớp)
-- GV001 (Toán) quản lý L01 và L02
-- GV002 (Văn) quản lý L01 và L03
-- GV003 (Anh) quản lý L02 và L03
-- GV004 (Lý) quản lý L01
-- GV005 (Hóa) quản lý L02 và L03
INSERT INTO `tblphancong` (`magv`, `malop`, `mamon`) VALUES
('GV001', 'L01', 'MH01'),
('GV001', 'L02', 'MH01'),
('GV002', 'L01', 'MH02'),
('GV002', 'L03', 'MH02'),
('GV003', 'L02', 'MH03'),
('GV003', 'L03', 'MH03'),
('GV004', 'L01', 'MH04'),
('GV004', 'L01', 'MH01'),
('GV005', 'L02', 'MH05'),
('GV005', 'L03', 'MH05');

-- Dữ liệu: tbldiem (Một số điểm mẫu)
-- Điểm cho sinh viên lớp L01
INSERT INTO `tbldiem` (`masv`, `mamon`, `hocky`, `namhoc`, `diemcc`, `diemgk`, `diemck`, `diemtongket`) VALUES
('SV001', 'MH01', 1, '2024-2025', 10, 8, 9, 8.8),
('SV001', 'MH02', 1, '2024-2025', 9, 7, 8, 7.8),
('SV001', 'MH04', 1, '2024-2025', 8, 7, 8, 7.7),
('SV002', 'MH01', 1, '2024-2025', 8, 7, 8, 7.7),
('SV002', 'MH02', 1, '2024-2025', 9, 8, 9, 8.7),
('SV002', 'MH04', 1, '2024-2025', 7, 6, 7, 6.7),
('SV003', 'MH01', 1, '2024-2025', 7, 6, 7, 6.7),
('SV003', 'MH02', 1, '2024-2025', 8, 7, 8, 7.7),
('SV003', 'MH04', 1, '2024-2025', 9, 8, 9, 8.7),
('SV004', 'MH01', 1, '2024-2025', 9, 8, 9, 8.7),
('SV004', 'MH02', 1, '2024-2025', 8, 7, 8, 7.7),
('SV004', 'MH04', 1, '2024-2025', 7, 6, 7, 6.7),
('SV005', 'MH01', 1, '2024-2025', 6, 5, 6, 5.7),
('SV005', 'MH02', 1, '2024-2025', 7, 6, 7, 6.7),
('SV005', 'MH04', 1, '2024-2025', 8, 7, 8, 7.7);

-- Điểm cho sinh viên lớp L02
INSERT INTO `tbldiem` (`masv`, `mamon`, `hocky`, `namhoc`, `diemcc`, `diemgk`, `diemck`, `diemtongket`) VALUES
('SV006', 'MH01', 1, '2024-2025', 7, 6, 7, 6.7),
('SV006', 'MH03', 1, '2024-2025', 8, 7, 8, 7.7),
('SV006', 'MH05', 1, '2024-2025', 9, 8, 9, 8.7),
('SV007', 'MH01', 1, '2024-2025', 8, 7, 8, 7.7),
('SV007', 'MH03', 1, '2024-2025', 7, 6, 7, 6.7),
('SV007', 'MH05', 1, '2024-2025', 8, 7, 8, 7.7),
('SV008', 'MH01', 1, '2024-2025', 9, 8, 9, 8.7),
('SV008', 'MH03', 1, '2024-2025', 8, 7, 8, 7.7),
('SV008', 'MH05', 1, '2024-2025', 7, 6, 7, 6.7),
('SV009', 'MH01', 1, '2024-2025', 6, 5, 6, 5.7),
('SV009', 'MH03', 1, '2024-2025', 7, 6, 7, 6.7),
('SV009', 'MH05', 1, '2024-2025', 8, 7, 8, 7.7),
('SV010', 'MH01', 1, '2024-2025', 8, 7, 8, 7.7),
('SV010', 'MH03', 1, '2024-2025', 9, 8, 9, 8.7),
('SV010', 'MH05', 1, '2024-2025', 8, 7, 8, 7.7);

-- Điểm cho sinh viên lớp L03
INSERT INTO `tbldiem` (`masv`, `mamon`, `hocky`, `namhoc`, `diemcc`, `diemgk`, `diemck`, `diemtongket`) VALUES
('SV011', 'MH02', 1, '2024-2025', 9, 8, 9, 8.7),
('SV011', 'MH03', 1, '2024-2025', 10, 9, 9, 9.1),
('SV011', 'MH05', 1, '2024-2025', 8, 7, 8, 7.7),
('SV012', 'MH02', 1, '2024-2025', 8, 7, 8, 7.7),
('SV012', 'MH03', 1, '2024-2025', 9, 8, 9, 8.7),
('SV012', 'MH05', 1, '2024-2025', 7, 6, 7, 6.7),
('SV013', 'MH02', 1, '2024-2025', 7, 6, 7, 6.7),
('SV013', 'MH03', 1, '2024-2025', 8, 7, 8, 7.7),
('SV013', 'MH05', 1, '2024-2025', 9, 8, 9, 8.7),
('SV014', 'MH02', 1, '2024-2025', 9, 8, 9, 8.7),
('SV014', 'MH03', 1, '2024-2025', 7, 6, 7, 6.7),
('SV014', 'MH05', 1, '2024-2025', 8, 7, 8, 7.7),
('SV015', 'MH02', 1, '2024-2025', 8, 7, 8, 7.7),
('SV015', 'MH03', 1, '2024-2025', 9, 8, 9, 8.7),
('SV015', 'MH05', 1, '2024-2025', 7, 6, 7, 6.7);

-- ============================================
-- 4. TẠO INDEX ĐỂ TỐI ƯU HIỆU SUẤT
-- ============================================

CREATE INDEX `idx_diem_namhoc` ON `tbldiem`(`namhoc`);
CREATE INDEX `idx_diem_hocky` ON `tbldiem`(`hocky`);
CREATE INDEX `idx_sinhvien_hoten` ON `tblsinhvien`(`hoten`);
CREATE INDEX `idx_sinhvien_malop` ON `tblsinhvien`(`malop`);
CREATE INDEX `idx_phancong_magv` ON `tblphancong`(`magv`);
CREATE INDEX `idx_giaovien_mabomon` ON `tblgiaovien`(`mabomon`);
CREATE INDEX `idx_monhoc_mabomon` ON `tblmonhoc`(`mabomon`);

-- ============================================
-- 5. KIỂM TRA DỮ LIỆU
-- ============================================

-- Xem tổng số bản ghi
SELECT 'tblkhoa' AS bang, COUNT(*) AS so_luong FROM tblkhoa
UNION ALL
SELECT 'tblbomon', COUNT(*) FROM tblbomon
UNION ALL
SELECT 'tbluser', COUNT(*) FROM tbluser
UNION ALL
SELECT 'tblmonhoc', COUNT(*) FROM tblmonhoc
UNION ALL
SELECT 'tblgiaovien', COUNT(*) FROM tblgiaovien
UNION ALL
SELECT 'tbl_giangday', COUNT(*) FROM tbl_giangday
UNION ALL
SELECT 'tblclass', COUNT(*) FROM tblclass
UNION ALL
SELECT 'tblphancong', COUNT(*) FROM tblphancong
UNION ALL
SELECT 'tblsinhvien', COUNT(*) FROM tblsinhvien
UNION ALL
SELECT 'tbldiem', COUNT(*) FROM tbldiem;

-- Kiểm tra phân công giáo viên
SELECT 
    gv.magv,
    gv.hoten,
    bm.tenbomon AS bo_mon,
    GROUP_CONCAT(DISTINCT mh.tenmon ORDER BY mh.tenmon SEPARATOR ', ') AS cac_mon_day,
    GROUP_CONCAT(DISTINCT c.malop ORDER BY c.malop SEPARATOR ', ') AS cac_lop_quan_ly
FROM tblgiaovien gv
LEFT JOIN tblbomon bm ON gv.mabomon = bm.mabomon
LEFT JOIN tbl_giangday gd ON gv.magv = gd.magv
LEFT JOIN tblmonhoc mh ON gd.mamon = mh.mamon
LEFT JOIN tblphancong pc ON gv.magv = pc.magv
LEFT JOIN tblclass c ON pc.malop = c.malop
GROUP BY gv.magv, gv.hoten, bm.tenbomon
ORDER BY gv.magv;

-- Kiểm tra sinh viên trong từng lớp
SELECT 
    c.malop,
    c.tenlop,
    COUNT(sv.masv) AS so_sinh_vien,
    GROUP_CONCAT(sv.hoten ORDER BY sv.masv SEPARATOR ', ') AS danh_sach_sinh_vien
FROM tblclass c
LEFT JOIN tblsinhvien sv ON c.malop = sv.malop
GROUP BY c.malop, c.tenlop
ORDER BY c.malop;

-- Kiểm tra bộ môn và các môn học
SELECT 
    bm.mabomon,
    bm.tenbomon,
    COUNT(DISTINCT mh.mamon) AS so_mon_hoc,
    GROUP_CONCAT(DISTINCT mh.tenmon ORDER BY mh.tenmon SEPARATOR ', ') AS danh_sach_mon_hoc,
    COUNT(DISTINCT gv.magv) AS so_giao_vien
FROM tblbomon bm
LEFT JOIN tblmonhoc mh ON bm.mabomon = mh.mabomon
LEFT JOIN tblgiaovien gv ON bm.mabomon = gv.mabomon
GROUP BY bm.mabomon, bm.tenbomon
ORDER BY bm.mabomon;


-- ============================================
-- HOÀN TẤT!
-- ============================================
-- Database đã được tạo thành công với:
-- - 10 bảng (tblkhoa, tblbomon, tbluser, tblmonhoc, tblgiaovien, tbl_giangday, 
--            tblclass, tblphancong, tblsinhvien, tbldiem)
-- - 4 bộ môn
-- - 5 môn học (được phân bổ vào các bộ môn)
-- - 3 lớp, mỗi lớp 5 sinh viên (tổng 15 sinh viên)
-- - 5 giáo viên, mỗi giáo viên thuộc 1 bộ môn và có thể dạy nhiều môn
-- - Phân công giảng dạy (tbl_giangday)
-- - Phân công giáo viên quản lý lớp (mỗi giáo viên quản lý 1-2 lớp)
-- - Dữ liệu điểm mẫu đầy đủ cho tất cả sinh viên

-- Tài khoản test:
-- Admin: admin/123456
-- Giáo viên: 
--   gv001/123456 (Nguyễn Văn Toán - Bộ môn Toán-Tin, dạy Toán, quản lý L01, L02)
--   gv002/123456 (Trần Thị Văn - Bộ môn Ngữ văn, dạy Văn, quản lý L01, L03)
--   gv003/123456 (Lê Hoàng Anh - Bộ môn Ngoại ngữ, dạy Anh, quản lý L02, L03)
--   gv004/123456 (Phạm Văn Lý - Bộ môn Toán-Tin, dạy Lý và Toán, quản lý L01)
--   gv005/123456 (Hoàng Thị Hóa - Bộ môn KHTN, dạy Hóa, quản lý L02, L03)
-- Sinh viên: sv001-sv015/123456

-- CHÚ Ý:
-- - Bảng tblbomon: Quản lý các bộ môn
-- - Bảng tbl_giangday: Quản lý phân công giảng dạy (giáo viên dạy môn học nào)
-- - Cột mabomon trong tblgiaovien: Giáo viên thuộc bộ môn nào
-- - Cột mabomon trong tblmonhoc: Môn học thuộc bộ môn nào
-- - Cột mamon trong tblgiaovien: Deprecated (legacy), khuyến nghị sử dụng tbl_giangday
