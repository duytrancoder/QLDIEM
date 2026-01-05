package connection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class UpdateSchema {
    public static void main(String[] args) {
        String sqlBoMon = "CREATE TABLE IF NOT EXISTS tblbomon (" +
                "mabomon VARCHAR(20) PRIMARY KEY," +
                "tenbomon VARCHAR(100) NOT NULL" +
                ");";

        String sqlGiangDay = "CREATE TABLE IF NOT EXISTS tbl_giangday (" +
                "magv VARCHAR(20)," +
                "mamon VARCHAR(20)," +
                "PRIMARY KEY (magv, mamon)" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            if (conn == null) {
                System.out.println("Connection is NULL!");
                return;
            }

            try {
                stmt.execute(sqlBoMon);
                System.out.println("Created tblbomon");
            } catch (SQLException e) {
                System.out.println("Error tblbomon: " + e.getMessage() + " | State: " + e.getSQLState());
            }

            try {
                stmt.execute(sqlGiangDay);
                System.out.println("Created tbl_giangday");
            } catch (SQLException e) {
                System.out.println("Error tbl_giangday: " + e.getMessage() + " | State: " + e.getSQLState());
            }

            try {
                stmt.execute("ALTER TABLE tblmonhoc ADD COLUMN mabomon VARCHAR(20)");
                System.out.println("Altered tblmonhoc");
            } catch (SQLException e) {
                System.out.println("Error tblmonhoc: " + e.getMessage() + " | State: " + e.getSQLState());
            }

            try {
                stmt.execute("ALTER TABLE tblgiaovien ADD COLUMN mabomon VARCHAR(20)");
                System.out.println("Altered tblgiaovien");
            } catch (SQLException e) {
                System.out.println("Error tblgiaovien: " + e.getMessage() + " | State: " + e.getSQLState());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
