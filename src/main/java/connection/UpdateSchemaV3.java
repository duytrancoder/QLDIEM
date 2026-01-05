package connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Schema Update V3: Add cacmon column to tblbomon for storing subject lists
 */
public class UpdateSchemaV3 {

    public static void addCacmonColumn() {
        String alterQuery = "ALTER TABLE tblbomon ADD COLUMN IF NOT EXISTS cacmon TEXT";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(alterQuery);
            System.out.println("✓ Schema updated: Added 'cacmon' column to tblbomon");

        } catch (SQLException e) {
            // Column might already exist
            System.out.println("Note: cacmon column may already exist - " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        addCacmonColumn();
    }
}
