package connection;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) {
        String sql = "CREATE TABLE IF NOT EXISTS tblthongbao (" +
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
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table tblthongbao created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
