-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th12 12, 2025 lúc 03:48 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `quanlydiem`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tblclass`
--

CREATE TABLE `tblclass` (
  `malop` varchar(20) NOT NULL,
  `tenlop` varchar(100) NOT NULL,
  `makhoa` varchar(10) DEFAULT NULL,
  `magvcn` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tblclass`
--

INSERT INTO `tblclass` (`malop`, `tenlop`, `makhoa`, `magvcn`) VALUES
('L01', 'CNTT K15A', 'CNTT', 'GV001'),
('L02', 'CNTT K15B', 'CNTT', 'GV003'),
('L03', 'Kinh Tế K16', 'KT', 'GV002');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbldiem`
--

CREATE TABLE `tbldiem` (
  `masv` varchar(20) NOT NULL,
  `mamon` varchar(20) NOT NULL,
  `hocky` int(11) NOT NULL,
  `namhoc` varchar(20) DEFAULT NULL,
  `diemcc` double DEFAULT NULL CHECK (`diemcc` between 0 and 10),
  `diemgk` double DEFAULT NULL CHECK (`diemgk` between 0 and 10),
  `diemck` double DEFAULT NULL CHECK (`diemck` between 0 and 10),
  `diemtongket` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbldiem`
--

INSERT INTO `tbldiem` (`masv`, `mamon`, `hocky`, `namhoc`, `diemcc`, `diemgk`, `diemck`, `diemtongket`) VALUES
('SV001', 'MH01', 1, '2023-2024', 10, 8, 9, 8.8),
('SV001', 'MH02', 1, '2023-2024', 9, 7, 6.5, 7),
('SV002', 'MH01', 1, '2023-2024', 8, 5, 4, 5),
('SV002', 'MH02', 1, '2023-2024', 10, 9, 9.5, 9.4),
('SV003', 'MH06', 2, '2023-2024', 7, 7, 7, 7),
('SV008', 'MH01', 1, '2023-2024', 5, 4, 3, 3.5),
('SV008', 'MH01', 2, '2023-2024', 10, 8, 8, 8.2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tblgiaovien`
--

CREATE TABLE `tblgiaovien` (
  `magv` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `gioitinh` varchar(10) DEFAULT NULL,
  `ngaysinh` date DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `sdt` varchar(15) DEFAULT NULL,
  `makhoa` varchar(10) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tblgiaovien`
--

INSERT INTO `tblgiaovien` (`magv`, `hoten`, `gioitinh`, `ngaysinh`, `email`, `sdt`, `makhoa`, `username`) VALUES
('GV001', 'Nguyễn Văn An', 'Nam', '1980-05-15', 'an@abc.edu.vn', '0901', 'CNTT', 'gv001'),
('GV002', 'Trần Thị Bích', 'Nữ', '1985-08-20', 'bich@abc.edu.vn', '0902', 'KT', 'gv002'),
('GV003', 'Lê Hoàng Dũng', 'Nam', '1979-12-05', 'dung@abc.edu.vn', '0903', 'CNTT', 'gv003');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tblkhoa`
--

CREATE TABLE `tblkhoa` (
  `makhoa` varchar(10) NOT NULL,
  `tenkhoa` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tblkhoa`
--

INSERT INTO `tblkhoa` (`makhoa`, `tenkhoa`) VALUES
('CNTT', 'Công Nghệ Thông Tin'),
('KT', 'Kinh Tế'),
('NN', 'Ngoại Ngữ');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tblmonhoc`
--

CREATE TABLE `tblmonhoc` (
  `mamon` varchar(20) NOT NULL,
  `tenmon` varchar(100) NOT NULL,
  `sotinchi` int(11) NOT NULL CHECK (`sotinchi` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tblmonhoc`
--

INSERT INTO `tblmonhoc` (`mamon`, `tenmon`, `sotinchi`) VALUES
('MH01', 'Lập trình Java', 3),
('MH02', 'Cơ sở dữ liệu', 3),
('MH03', 'Kinh tế vĩ mô', 2),
('MH06', 'Lập trình Web', 3);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tblsinhvien`
--

CREATE TABLE `tblsinhvien` (
  `masv` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `ngaysinh` date DEFAULT NULL,
  `gioitinh` varchar(10) DEFAULT NULL,
  `diachi` varchar(200) DEFAULT NULL,
  `malop` varchar(20) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tblsinhvien`
--

INSERT INTO `tblsinhvien` (`masv`, `hoten`, `ngaysinh`, `gioitinh`, `diachi`, `malop`, `username`) VALUES
('SV001', 'Lê Văn Tèo', '2003-01-01', 'Nam', 'Hà Nội', 'L01', 'sv001'),
('SV002', 'Nguyễn Thị Tý', '2003-05-05', 'Nữ', 'Nam Định', 'L01', 'sv002'),
('SV003', 'Trần Văn Ba', '2003-09-09', 'Nam', 'Hải Phòng', 'L02', 'sv003'),
('SV008', 'Lý Thị Tám', '2003-12-12', 'Nữ', 'Cần Thơ', 'L01', 'sv008');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbluser`
--

CREATE TABLE `tbluser` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `type` int(11) NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbluser`
--

INSERT INTO `tbluser` (`username`, `password`, `type`) VALUES
('admin', '123456', 0),
('gv001', '123456', 1),
('gv002', '123456', 1),
('gv003', '123456', 1),
('sv001', '123456', 2),
('sv002', '123456', 2),
('sv003', '123456', 2),
('sv008', '123456', 2);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `tblclass`
--
ALTER TABLE `tblclass`
  ADD PRIMARY KEY (`malop`),
  ADD KEY `makhoa` (`makhoa`),
  ADD KEY `magvcn` (`magvcn`);

--
-- Chỉ mục cho bảng `tbldiem`
--
ALTER TABLE `tbldiem`
  ADD PRIMARY KEY (`masv`,`mamon`,`hocky`),
  ADD KEY `mamon` (`mamon`);

--
-- Chỉ mục cho bảng `tblgiaovien`
--
ALTER TABLE `tblgiaovien`
  ADD PRIMARY KEY (`magv`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `makhoa` (`makhoa`);

--
-- Chỉ mục cho bảng `tblkhoa`
--
ALTER TABLE `tblkhoa`
  ADD PRIMARY KEY (`makhoa`);

--
-- Chỉ mục cho bảng `tblmonhoc`
--
ALTER TABLE `tblmonhoc`
  ADD PRIMARY KEY (`mamon`);

--
-- Chỉ mục cho bảng `tblsinhvien`
--
ALTER TABLE `tblsinhvien`
  ADD PRIMARY KEY (`masv`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `malop` (`malop`);

--
-- Chỉ mục cho bảng `tbluser`
--
ALTER TABLE `tbluser`
  ADD PRIMARY KEY (`username`);

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `tblclass`
--
ALTER TABLE `tblclass`
  ADD CONSTRAINT `tblclass_ibfk_1` FOREIGN KEY (`makhoa`) REFERENCES `tblkhoa` (`makhoa`),
  ADD CONSTRAINT `tblclass_ibfk_2` FOREIGN KEY (`magvcn`) REFERENCES `tblgiaovien` (`magv`);

--
-- Các ràng buộc cho bảng `tbldiem`
--
ALTER TABLE `tbldiem`
  ADD CONSTRAINT `tbldiem_ibfk_1` FOREIGN KEY (`masv`) REFERENCES `tblsinhvien` (`masv`),
  ADD CONSTRAINT `tbldiem_ibfk_2` FOREIGN KEY (`mamon`) REFERENCES `tblmonhoc` (`mamon`);

--
-- Các ràng buộc cho bảng `tblgiaovien`
--
ALTER TABLE `tblgiaovien`
  ADD CONSTRAINT `tblgiaovien_ibfk_1` FOREIGN KEY (`makhoa`) REFERENCES `tblkhoa` (`makhoa`),
  ADD CONSTRAINT `tblgiaovien_ibfk_2` FOREIGN KEY (`username`) REFERENCES `tbluser` (`username`);

--
-- Các ràng buộc cho bảng `tblsinhvien`
--
ALTER TABLE `tblsinhvien`
  ADD CONSTRAINT `tblsinhvien_ibfk_1` FOREIGN KEY (`malop`) REFERENCES `tblclass` (`malop`),
  ADD CONSTRAINT `tblsinhvien_ibfk_2` FOREIGN KEY (`username`) REFERENCES `tbluser` (`username`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
