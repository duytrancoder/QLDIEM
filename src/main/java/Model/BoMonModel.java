package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class BoMonModel {
    private String mabomon;
    private String tenbomon;

    public BoMonModel() {
    }

    public BoMonModel(String mabomon, String tenbomon) {
        this.mabomon = mabomon;
        this.tenbomon = tenbomon;
    }

    private String cacMon; // List of subject codes (comma separated)

    public BoMonModel(String mabomon, String tenbomon, String cacMon) {
        this.mabomon = mabomon;
        this.tenbomon = tenbomon;
        this.cacMon = cacMon;
    }

    public String getMabomon() {
        return mabomon;
    }

    public void setMabomon(String mabomon) {
        this.mabomon = mabomon;
    }

    public String getTenbomon() {
        return tenbomon;
    }

    public void setTenbomon(String tenbomon) {
        this.tenbomon = tenbomon;
    }

    public String getCacMon() {
        return cacMon;
    }

    public void setCacMon(String cacMon) {
        this.cacMon = cacMon;
    }

    public ArrayList<BoMonModel> getAllBoMon() {
        ArrayList<BoMonModel> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Source of truth: The 'cacmon' column in tblbomon itself.
            // This allows M:N relationship (one subject in multiple departments).
            String query = "SELECT mabomon, tenbomon, cacmon FROM tblbomon ORDER BY mabomon";
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    list.add(new BoMonModel(
                            rs.getString("mabomon"),
                            rs.getString("tenbomon"),
                            rs.getString("cacmon")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addBoMon(BoMonModel bm) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Department with its own subject list
            String query = "INSERT INTO tblbomon (mabomon, tenbomon, cacmon) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, bm.getMabomon().trim());
                ps.setString(2, bm.getTenbomon().trim());
                ps.setString(3, bm.getCacMon()); // Save subject list
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public boolean updateBoMon(BoMonModel bm) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update Department and its subject list
            String query = "UPDATE tblbomon SET tenbomon = ?, cacmon = ? WHERE mabomon = ?";
            boolean updated = false;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, bm.getTenbomon().trim());
                ps.setString(2, bm.getCacMon()); // Save subject list
                ps.setString(3, bm.getMabomon().trim());
                updated = ps.executeUpdate() > 0;
            }

            if (!updated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public boolean deleteBoMon(String mabomon) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 2. Delete Department
            String query = "DELETE FROM tblbomon WHERE mabomon = ?";
            boolean deleted = false;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, mabomon.trim());
                deleted = ps.executeUpdate() > 0;
            }

            if (deleted) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
}
