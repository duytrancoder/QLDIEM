-- Create Department Table
CREATE TABLE IF NOT EXISTS tblbomon (
    mabomon VARCHAR(20) PRIMARY KEY,
    tenbomon VARCHAR(100) NOT NULL
);

-- Create Assignments Table
CREATE TABLE IF NOT EXISTS tbl_giangday (
    magv VARCHAR(20),
    mamon VARCHAR(20),
    PRIMARY KEY (magv, mamon)
);

-- Add Columns to Existing Tables
-- Ignore errors if columns already exist
ALTER TABLE tblmonhoc ADD COLUMN IF NOT EXISTS mabomon VARCHAR(20);
ALTER TABLE tblgiaovien ADD COLUMN IF NOT EXISTS mabomon VARCHAR(20);
