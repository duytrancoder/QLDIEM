-- ============================================
-- SCRIPT DATABASE HOÀN CHỈNH CHO DỰ ÁN QLDIEM
-- Phiên bản: 2.0 (Hợp nhất toàn bộ thay đổi kiến trúc)
-- ============================================

-- 1. XÓA DATABASE CŨ VÀ TẠO MỚI
DROP DATABASE IF EXISTS `quanlydiem`;
CREATE DATABASE `quanlydiem` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quanlydiem`;

-- ============================================
-- 2. TẠO CÁC BẢNG (Thứ tự quan trọng do khóa ngoại)
-- ============================================

-- Bảng: tblkhoa (Khoá đào tạo)
CREATE TABLE `tblkhoa` (
  `makhoa` varchar(10) NOT NULL,
  `tenkhoa` varchar(100) NOT NULL,
  PRIMARY KEY (`makhoa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblbomon (Bộ môn - Quản lý danh sách môn học qua CSV `cacmon`)
CREATE TABLE `tblbomon` (
  `mabomon` varchar(20) NOT NULL,
  `tenbomon` varchar(100) NOT NULL,
  `cacmon` TEXT DEFAULT NULL COMMENT 'Danh sách mã môn học (phân cách bởi dấu phẩy)',
  PRIMARY KEY (`mabomon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tbluser (Tài khoản người dùng)
CREATE TABLE `tbluser` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `type` int(11) NOT NULL DEFAULT 2 COMMENT '0=Admin, 1=Giáo viên, 2=Sinh viên',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblmonhoc (Môn học - Không còn phụ thuộc trực tiếp vào mabomon)
CREATE TABLE `tblmonhoc` (
  `mamon` varchar(20) NOT NULL,
  `tenmon` varchar(100) NOT NULL,
  `sotinchi` int(11) NOT NULL DEFAULT 3,
  PRIMARY KEY (`mamon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblgiaovien (Giáo viên - Thuộc 1 bộ môn)
CREATE TABLE `tblgiaovien` (
  `magv` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `gioitinh` varchar(10) DEFAULT NULL,
  `ngaysinh` date DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `sdt` varchar(15) DEFAULT NULL,
  `mabomon` varchar(20) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`magv`),
  UNIQUE KEY `username` (`username`),
  CONSTRAINT `fk_giaovien_bomon` FOREIGN KEY (`mabomon`) REFERENCES `tblbomon` (`mabomon`) ON DELETE SET NULL,
  CONSTRAINT `fk_giaovien_user` FOREIGN KEY (`username`) REFERENCES `tbluser` (`username`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tbl_giangday (N:M - Giáo viên dạy được những môn nào)
CREATE TABLE `tbl_giangday` (
  `magv` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  PRIMARY KEY (`magv`, `mamon`),
  CONSTRAINT `fk_giangday_gv` FOREIGN KEY (`magv`) REFERENCES `tblgiaovien` (`magv`) ON DELETE CASCADE,
  CONSTRAINT `fk_giangday_mh` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblclass (Lớp học)
CREATE TABLE `tblclass` (
  `malop` varchar(20) NOT NULL,
  `tenlop` varchar(100) NOT NULL,
  `makhoa` varchar(10) DEFAULT NULL,
  `magvcn` varchar(20) DEFAULT NULL COMMENT 'Giáo viên chủ nhiệm (HomeRoom)',
  PRIMARY KEY (`malop`),
  CONSTRAINT `fk_class_khoa` FOREIGN KEY (`makhoa`) REFERENCES `tblkhoa` (`makhoa`),
  CONSTRAINT `fk_class_gvcn` FOREIGN KEY (`magvcn`) REFERENCES `tblgiaovien` (`magv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblphancong (Phân công Giáo viên dạy Lớp nào Môn nào)
CREATE TABLE `tblphancong` (
  `magv` varchar(20) NOT NULL,
  `malop` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  PRIMARY KEY (`magv`, `malop`, `mamon`),
  CONSTRAINT `fk_pc_gv` FOREIGN KEY (`magv`) REFERENCES `tblgiaovien` (`magv`),
  CONSTRAINT `fk_pc_lop` FOREIGN KEY (`malop`) REFERENCES `tblclass` (`malop`),
  CONSTRAINT `fk_pc_mh` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblsinhvien (Sinh viên)
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
  CONSTRAINT `fk_sv_lop` FOREIGN KEY (`malop`) REFERENCES `tblclass` (`malop`),
  CONSTRAINT `fk_sv_user` FOREIGN KEY (`username`) REFERENCES `tbluser` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tbldiem (Quản lý điểm số)
CREATE TABLE `tbldiem` (
  `masv` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  `hocky` int(11) NOT NULL,
  `namhoc` varchar(20) DEFAULT NULL,
  `diemcc` double DEFAULT 0,
  `diemgk` double DEFAULT 0,
  `diemck` double DEFAULT 0,
  `diemtongket` double DEFAULT 0,
  PRIMARY KEY (`masv`,`mamon`,`hocky`),
  CONSTRAINT `fk_diem_sv` FOREIGN KEY (`masv`) REFERENCES `tblsinhvien` (`masv`),
  CONSTRAINT `fk_diem_mh` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng: tblcauhinh (Cấu hình hệ thống)
CREATE TABLE `tblcauhinh` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `ten_truong` varchar(200) DEFAULT 'Trường Đại học Công Nghệ',
  `namhoc` varchar(20) DEFAULT '2024-2025',
  `hocky` int DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. THÊM DỮ LIỆU MẪU (Sạch, Đồng bộ)
-- ============================================

-- Bảng: tblkhoa
INSERT INTO `tblkhoa` (`makhoa`, `tenkhoa`) VALUES
('CNTT', 'Công Nghệ Thông Tin'),
('KT', 'Kinh Tế'),
('NN', 'Ngoại Ngữ');

-- Bảng: tblmonhoc
INSERT INTO `tblmonhoc` (`mamon`, `tenmon`, `sotinchi`) VALUES
('MH01', 'Giải tích 1', 3),
('MH02', 'Cấu trúc dữ liệu', 4),
('MH03', 'Tiếng Anh chuyên ngành', 2),
('MH04', 'Kinh tế học', 3),
('MH05', 'Lập trình Java', 3);

-- Bảng: tblbomon
INSERT INTO `tblbomon` (`mabomon`, `tenbomon`, `cacmon`) VALUES
('BM01', 'Bộ môn Khoa học máy tính', 'MH02,MH05'),
('BM02', 'Bộ môn Toán lý', 'MH01'),
('BM03', 'Bộ môn Ngoại ngữ', 'MH03'),
('BM04', 'Bộ môn Quản trị kinh doanh', 'MH04');

-- Bảng: tbluser (Admin, 5 Giáo viên, 10 Sinh viên)
INSERT INTO `tbluser` (`username`, `password`, `type`) VALUES
('admin', '123456', 0),
('gv001', '123456', 1), ('gv002', '123456', 1), ('gv003', '123456', 1), ('gv004', '123456', 1), ('gv005', '123456', 1),
('sv001', '123456', 2), ('sv002', '123456', 2), ('sv003', '123456', 2), ('sv004', '123456', 2), ('sv005', '123456', 2),
('sv006', '123456', 2), ('sv007', '123456', 2), ('sv008', '123456', 2), ('sv009', '123456', 2), ('sv010', '123456', 2);

-- Bảng: tblgiaovien
INSERT INTO `tblgiaovien` (`magv`, `hoten`, `gioitinh`, `ngaysinh`, `email`, `sdt`, `mabomon`, `username`) VALUES
('GV001', 'Hà Quang Vinh', 'Nam', '1980-01-01', 'vinhhq@abc.edu.vn', '0912345601', 'BM01', 'gv001'),
('GV002', 'Trần Thị Thuý', 'Nữ', '1985-05-10', 'thuytt@abc.edu.vn', '0912345602', 'BM01', 'gv002'),
('GV003', 'Lê Hoàng Minh', 'Nam', '1978-03-22', 'minhlh@abc.edu.vn', '0912345603', 'BM02', 'gv003'),
('GV004', 'Phạm Hồng Nhung', 'Nữ', '1982-11-12', 'nhunghp@abc.edu.vn', '0912345604', 'BM03', 'gv004'),
('GV005', 'Vũ Anh Tuấn', 'Nam', '1983-09-09', 'tuanva@abc.edu.vn', '0912345605', 'BM04', 'gv005');

-- Bảng: tbl_giangday (Giáo viên dạy được những môn nào)
INSERT INTO `tbl_giangday` (`magv`, `mamon`) VALUES
('GV001', 'MH02'), ('GV001', 'MH05'), -- GV001 dạy 2 môn
('GV002', 'MH05'),
('GV003', 'MH01'),
('GV004', 'MH03'),
('GV005', 'MH04');

-- Bảng: tblclass
INSERT INTO `tblclass` (`malop`, `tenlop`, `makhoa`, `magvcn`) VALUES
('L01', 'CNTT Khóa 1', 'CNTT', 'GV001'),
('L02', 'CNTT Khóa 2', 'CNTT', 'GV002'),
('L03', 'Kinh Tế K1', 'KT', 'GV005');

-- Bảng: tblphancong (Giao việc cụ thể)
INSERT INTO `tblphancong` (`magv`, `malop`, `mamon`) VALUES
('GV001', 'L01', 'MH05'), -- Dạy Java cho L01
('GV001', 'L02', 'MH02'), -- Dạy CTDL cho L02
('GV002', 'L01', 'MH05'), -- Trợ giảng Java cho L01 (M:N PK Test)
('GV003', 'L01', 'MH01'),
('GV004', 'L03', 'MH03'),
('GV005', 'L03', 'MH04');

-- Bảng: tblsinhvien
INSERT INTO `tblsinhvien` (`masv`, `hoten`, `ngaysinh`, `malop`, `username`) VALUES
('SV001', 'Nguyễn Thị Hoa', '2004-01-20', 'L01', 'sv001'),
('SV002', 'Trần Văn Tú', '2004-03-15', 'L01', 'sv002'),
('SV003', 'Lê Thu Hà', '2004-05-10', 'L01', 'sv003'),
('SV004', 'Phạm Minh Châu', '2004-07-05', 'L02', 'sv004'),
('SV005', 'Vũ Đức Nam', '2004-09-25', 'L02', 'sv005');

-- Bảng: tblcauhinh
INSERT INTO `tblcauhinh` (`ten_truong`, `namhoc`, `hocky`) VALUES
('Học viện Công Nghệ', '2024-2025', 1);

-- ============================================
-- 4. TỐI ƯU HÓA (INDEXES)
-- ============================================
CREATE INDEX `idx_sv_hoten` ON `tblsinhvien` (`hoten`);
CREATE INDEX `idx_gv_hoten` ON `tblgiaovien` (`hoten`);

-- ============================================
-- HOÀN TẤT!
-- ============================================
