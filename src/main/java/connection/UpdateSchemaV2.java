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

            // 1. Create tblbomon
            try {
                String sql = "CREATE TABLE IF NOT EXISTS tblbomon (" +
                        "mabomon VARCHAR(20) PRIMARY KEY," +
                        "tenbomon VARCHAR(100) NOT NULL" +
                        ")";
                stmt.execute(sql);
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
                    try {
                        stmt.execute("ALTER TABLE tblphancong DROP PRIMARY KEY");
                    } catch (SQLException e) {
                        // Ignore if no PK
                    }
                    stmt.execute("ALTER TABLE tblphancong ADD COLUMN mamon VARCHAR(20) NOT NULL DEFAULT ''");
                    stmt.execute("ALTER TABLE tblphancong ADD PRIMARY KEY (magv, malop, mamon)");
                    System.out.println("SUCCESS: Updated tblphancong schema (Added mamon + New PK).");
                } else {
                    System.out.println("INFO: tblphancong already has mamon.");
                }
            } catch (SQLException e) {
                System.out.println("ERROR tblphancong: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Schema Update Finished.");
    }
}
