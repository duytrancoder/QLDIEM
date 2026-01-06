package connection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class UpdateSchemaV2 {
    public static void updateDatabase() {
        System.out.println("Starting Schema Update...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("FATAL: Connection is NULL. Check DatabaseConnection.java or MySQL server.");
                return;
            }
            System.out.println("Connection established.");

            Statement stmt = conn.createStatement();

            // 1. Create tblbomon & add cacmon column
            try {
                String sql = "CREATE TABLE IF NOT EXISTS tblbomon (" +
                        "mabomon VARCHAR(20) PRIMARY KEY," +
                        "tenbomon VARCHAR(100) NOT NULL" +
                        ")";
                stmt.execute(sql);

                // Add cacmon column if not exists
                try {
                    stmt.execute("ALTER TABLE tblbomon ADD COLUMN cacmon TEXT");
                    System.out.println("SUCCESS: Added cacmon to tblbomon.");
                } catch (SQLException e) {
                    // Ignore duplicate column error
                }

                System.out.println("SUCCESS: Processed tblbomon.");
            } catch (SQLException e) {
                System.out.println("ERROR tblbomon: " + e.getMessage());
            }

            // 2. Create tbl_giangday
            try {
                String sql = "CREATE TABLE IF NOT EXISTS tbl_giangday (" +
                        "magv VARCHAR(20)," +
                        "mamon VARCHAR(20)," +
                        "PRIMARY KEY (magv, mamon)" +
                        ")";
                stmt.execute(sql);
                System.out.println("SUCCESS: Processed tbl_giangday.");
            } catch (SQLException e) {
                System.out.println("ERROR tbl_giangday: " + e.getMessage());
            }

            // 3. Alter tblmonhoc
            try {
                stmt.execute("ALTER TABLE tblmonhoc ADD COLUMN mabomon VARCHAR(20)");
                System.out.println("SUCCESS: Added mabomon to tblmonhoc.");
            } catch (SQLException e) {
                if (e.getMessage().toLowerCase().contains("duplicate") || e.getErrorCode() == 1060) {
                    System.out.println("INFO: Column mabomon already exists in tblmonhoc.");
                } else {
                    System.out.println("ERROR tblmonhoc: " + e.getMessage());
                }
            }

            // 4. Alter tblgiaovien
            try {
                stmt.execute("ALTER TABLE tblgiaovien ADD COLUMN mabomon VARCHAR(20)");
                System.out.println("SUCCESS: Added mabomon to tblgiaovien.");
            } catch (SQLException e) {
                if (e.getMessage().toLowerCase().contains("duplicate") || e.getErrorCode() == 1060) {
                    System.out.println("INFO: Column mabomon already exists in tblgiaovien.");
                } else {
                    System.out.println("ERROR tblgiaovien: " + e.getMessage());
                }
            }

            // 5. Alter tblphancong (Add mamon and update PK)
            try {
                // Check if mamon exists first
                boolean colExists = false;
                try (java.sql.ResultSet rs = conn.getMetaData().getColumns(null, null, "tblphancong", "mamon")) {
                    if (rs.next())
                        colExists = true;
                }

                if (!colExists) {
                    stmt.execute("ALTER TABLE tblphancong ADD COLUMN mamon VARCHAR(20) NOT NULL DEFAULT ''");
                    System.out.println("SUCCESS: Added mamon to tblphancong.");
                }

                // Force PK upgrade to (magv, malop, mamon) if it's not already
                try {
                    // We must drop before adding if we want to change the columns in it
                    stmt.execute("ALTER TABLE tblphancong DROP PRIMARY KEY");
                } catch (SQLException e) {
                    // No existing PK, ignore
                }

                try {
                    stmt.execute("ALTER TABLE tblphancong ADD PRIMARY KEY (magv, malop, mamon)");
                    System.out.println("SUCCESS: Enforced (magv, malop, mamon) PK on tblphancong.");
                } catch (SQLException e) {
                    System.out.println("INFO: PK already set or could not be updated: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println("ERROR tblphancong: " + e.getMessage());
            }

            // 6. Create tblcauhinh and init default
            try {
                String sql = "CREATE TABLE IF NOT EXISTS tblcauhinh (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "ten_truong VARCHAR(200) DEFAULT 'Trường Đại học Công Nghệ'," +
                        "namhoc VARCHAR(20) DEFAULT '2024-2025'," +
                        "hocky INT DEFAULT 1" +
                        ")";
                stmt.execute(sql);

                // Check if table is empty
                java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tblcauhinh");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO tblcauhinh (namhoc, hocky) VALUES ('2024-2025', 1)");
                    System.out.println("SUCCESS: Initialized default settings in tblcauhinh.");
                }
                System.out.println("SUCCESS: Processed tblcauhinh.");
            } catch (SQLException e) {
                System.out.println("ERROR tblcauhinh: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Schema Update Finished.");
    }
}
