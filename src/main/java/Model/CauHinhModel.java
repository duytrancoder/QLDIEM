package Model;

import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CauHinhModel {
    private int id;
    private String namhoc;
    private int hocky;

    public CauHinhModel() {
    }

    public CauHinhModel(int id, String namhoc, int hocky) {
        this.id = id;
        this.namhoc = namhoc;
        this.hocky = hocky;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamhoc() {
        return namhoc;
    }

    public void setNamhoc(String namhoc) {
        this.namhoc = namhoc;
    }

    public int getHocky() {
        return hocky;
    }

    public void setHocky(int hocky) {
        this.hocky = hocky;
    }

    // Get current global settings (Auto-init defaults if missing)
    public CauHinhModel getGlobalSettings() {
        // verifyTableExist(); // Removed excessive check, assume existing or handle in
        // init

        String sql = "SELECT * FROM tblcauhinh LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CauHinhModel(
                            rs.getInt("id"),
                            rs.getString("namhoc"),
                            rs.getInt("hocky"));
                }
            }

            // If we are here, no settings exist.
            // Insert default directly to avoid recursion
            return initDefaultSettings(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CauHinhModel initDefaultSettings(Connection conn) {
        String defaultNamHoc = "2024-2025";
        int defaultHocKy = 1;
        String sql = "INSERT INTO tblcauhinh (namhoc, hocky) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, defaultNamHoc);
            stmt.setInt(2, defaultHocKy);
            stmt.executeUpdate();
            return new CauHinhModel(0, defaultNamHoc, defaultHocKy);
        } catch (SQLException e) {
            // If table doesn't exist, create it and retry once
            if (e.getMessage().contains("tblcauhinh")) { // Simple check, better to verify exist
                verifyTableExist();
                try (PreparedStatement retryStmt = conn.prepareStatement(sql)) {
                    retryStmt.setString(1, defaultNamHoc);
                    retryStmt.setInt(2, defaultHocKy);
                    retryStmt.executeUpdate();
                    return new CauHinhModel(0, defaultNamHoc, defaultHocKy);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    private void verifyTableExist() {
        String sql = "CREATE TABLE IF NOT EXISTS tblcauhinh (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "namhoc VARCHAR(20)," +
                "hocky INT" +
                ");";
        try (Connection conn = DatabaseConnection.getConnection();
                java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update global settings
    public boolean updateSettings(String namhoc, int hocky) {
        // We know we want to update. if it doesn't exist, we insert.
        // Instead of calling getGlobalSettings (recursion risk), just try UPDATE first.

        String updateSql = "UPDATE tblcauhinh SET namhoc = ?, hocky = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, namhoc);
            stmt.setInt(2, hocky);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                return true;
            } else {
                // No rows updated, table might be empty. Insert.
                String insertSql = "INSERT INTO tblcauhinh (namhoc, hocky) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, namhoc);
                    insertStmt.setInt(2, hocky);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
