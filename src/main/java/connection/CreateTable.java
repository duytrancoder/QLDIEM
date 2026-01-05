package connection;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) {
        String sqlThongBao = "CREATE TABLE IF NOT EXISTS tblthongbao (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "tieude VARCHAR(255) NOT NULL," +
                "noidung TEXT," +
                "nguoigui VARCHAR(50)," +
                "ten_nguoi_gui VARCHAR(100)," +
                "loai VARCHAR(20)," +
                "phamvi VARCHAR(50)," +
                "trang_thai VARCHAR(20) DEFAULT 'HIEN'," +
                "ngaygui DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String sqlCauHinh = "CREATE TABLE IF NOT EXISTS tblcauhinh (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "namhoc VARCHAR(20) NOT NULL," +
                "hocky INT NOT NULL" +
                ");";

        // Insert default setting if empty
        String sqlInitSettings = "INSERT INTO tblcauhinh (namhoc, hocky) " +
                "SELECT * FROM (SELECT '2023-2024', 1) AS tmp " +
                "WHERE NOT EXISTS (SELECT * FROM tblcauhinh) LIMIT 1;";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sqlThongBao);
            System.out.println("Table tblthongbao created successfully.");

            stmt.execute(sqlCauHinh);
            System.out.println("Table tblcauhinh created successfully.");

            stmt.execute(sqlInitSettings);
            System.out.println("Default settings initialized.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
