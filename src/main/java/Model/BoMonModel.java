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

            // Try to read with cacmon column
            String query = "SELECT mabomon, tenbomon, cacmon FROM tblbomon ORDER BY mabomon";
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    list.add(new BoMonModel(
                            rs.getString("mabomon"),
                            rs.getString("tenbomon"),
                            rs.getString("cacmon")));
                }
            } catch (SQLException e) {
                // cacmon column might not exist yet, try without it
                System.out.println("Note: cacmon column not found, using legacy query");
                String fallbackQuery = "SELECT mabomon, tenbomon FROM tblbomon ORDER BY mabomon";
                try (ResultSet rs2 = stmt.executeQuery(fallbackQuery)) {
                    while (rs2.next()) {
                        list.add(new BoMonModel(
                                rs2.getString("mabomon"),
                                rs2.getString("tenbomon"),
                                null)); // No subjects
                    }
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

            // 1. Insert Department with subject list
            String query = "INSERT INTO tblbomon (mabomon, tenbomon, cacmon) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, bm.getMabomon());
                ps.setString(2, bm.getTenbomon());
                ps.setString(3, bm.getCacMon()); // Save subject list
                ps.executeUpdate();
            }

            // Note: cacmon is stored in tblbomon for display only, not in tblmonhoc
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

            // 1. Update Department Name and subject list
            String query = "UPDATE tblbomon SET tenbomon = ?, cacmon = ? WHERE mabomon = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, bm.getTenbomon());
                ps.setString(2, bm.getCacMon()); // Save subject list
                ps.setString(3, bm.getMabomon());
                ps.executeUpdate();
            }

            // Note: cacmon is stored in tblbomon for display only, not in tblmonhoc
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

            // 1. Unassign subjects
            String unassignQuery = "UPDATE tblmonhoc SET mabomon = NULL WHERE mabomon = ?";
            try (PreparedStatement ps = conn.prepareStatement(unassignQuery)) {
                ps.setString(1, mabomon);
                ps.executeUpdate();
            }

            // 2. Delete Department
            String query = "DELETE FROM tblbomon WHERE mabomon = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, mabomon);
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
}
