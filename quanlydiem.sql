DROP DATABASE IF EXISTS QuanLyDiem;
CREATE DATABASE QuanLyDiem 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE QuanLyDiem;


CREATE TABLE tbluser (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    type INT NOT NULL DEFAULT 2
);


CREATE TABLE tblkhoa (
    makhoa VARCHAR(10) PRIMARY KEY,
    tenkhoa VARCHAR(100) NOT NULL -- Đã đổi NVARCHAR thành VARCHAR
);


CREATE TABLE tblgiaovien (
    magv VARCHAR(20) PRIMARY KEY,
    hoten VARCHAR(100) NOT NULL,
    gioitinh VARCHAR(10),
    ngaysinh DATE,
    email VARCHAR(100),
    sdt VARCHAR(15),
    makhoa VARCHAR(10),
    username VARCHAR(50) UNIQUE,
    FOREIGN KEY (makhoa) REFERENCES tblkhoa(makhoa),
    FOREIGN KEY (username) REFERENCES tbluser(username)
);


CREATE TABLE tblclass (
    malop VARCHAR(20) PRIMARY KEY,
    tenlop VARCHAR(100) NOT NULL,
    makhoa VARCHAR(10),
    magvcn VARCHAR(20),
    FOREIGN KEY (makhoa) REFERENCES tblkhoa(makhoa),
    FOREIGN KEY (magvcn) REFERENCES tblgiaovien(magv)
);


CREATE TABLE tblsinhvien (
    masv VARCHAR(20) PRIMARY KEY,
    hoten VARCHAR(100) NOT NULL,
    ngaysinh DATE,
    gioitinh VARCHAR(10),
    diachi VARCHAR(200),
    malop VARCHAR(20),
    username VARCHAR(50) UNIQUE,
    FOREIGN KEY (malop) REFERENCES tblclass(malop),
    FOREIGN KEY (username) REFERENCES tbluser(username)
);


CREATE TABLE tblmonhoc (
    mamon VARCHAR(20) PRIMARY KEY,
    tenmon VARCHAR(100) NOT NULL,
    sotinchi INT NOT NULL CHECK (sotinchi > 0)
);


CREATE TABLE tbldiem (
    id INT AUTO_INCREMENT PRIMARY KEY,
    masv VARCHAR(20),
    mamon VARCHAR(20),
    hocky INT NOT NULL,
    namhoc VARCHAR(20),
    diemcc DOUBLE CHECK (diemcc BETWEEN 0 AND 10),
    diemgk DOUBLE CHECK (diemgk BETWEEN 0 AND 10),
    diemck DOUBLE CHECK (diemck BETWEEN 0 AND 10),
    diemtongket DOUBLE,
    FOREIGN KEY (masv) REFERENCES tblsinhvien(masv),
    FOREIGN KEY (mamon) REFERENCES tblmonhoc(mamon),
    UNIQUE (masv, mamon, hocky, namhoc)
);