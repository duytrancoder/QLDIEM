package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

/**
 * Model cho đối tượng Giáo viên
 * Updated to support Department (mabomon) and Multiple Subjects (tbl_giangday)
 */
public class GiaoVienModel {

    private String magv;
    private String hoten;
    private String gioitinh;
    private String ngaysinh;
    private String email;
    private String sdt;
    private String mabomon;
    private String tenbomon;
    private String cacMon; // CSV string of mamon
    private String tenCacMon; // CSV string of tenmon
    private String username;

    public GiaoVienModel() {
    }

    // Full Constructor (New)
    public GiaoVienModel(String magv, String hoten, String gioitinh, String ngaysinh,
            String email, String sdt, String mabomon, String tenbomon, String cacMon, String tenCacMon,
            String username) {
        this.magv = magv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.ngaysinh = ngaysinh;
        this.email = email;
        this.sdt = sdt;
        this.mabomon = mabomon;
        this.tenbomon = tenbomon;
        this.cacMon = cacMon;
        this.tenCacMon = tenCacMon;
        this.username = username;
    }

    // Legacy Constructor 1 (Old code compatibility)
    public GiaoVienModel(String magv, String hoten, String gioitinh, String ngaysinh,
            String email, String sdt, String mamon, String username) {
        this.magv = magv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.ngaysinh = ngaysinh;
        this.email = email;
        this.sdt = sdt;
        this.cacMon = mamon; // treat single subject as CSV
        this.username = username;
    }

    // Legacy Constructor 2 (Old code compatibility)
    public GiaoVienModel(String magv, String hoten, String gioitinh, String ngaysinh,
            String email, String sdt, String mamon, String username, String tenMon) {
        this(magv, hoten, gioitinh, ngaysinh, email, sdt, mamon, username);
        this.tenCacMon = tenMon;
    }

    // Getters and Setters
    public String getMagv() {
        return magv;
    }

    public void setMagv(String magv) {
        this.magv = magv;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
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

    public String getTenCacMon() {
        return tenCacMon;
    }

    public void setTenCacMon(String tenCacMon) {
        this.tenCacMon = tenCacMon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Legacy support
    public String getMamon() {
        return cacMon != null && !cacMon.isEmpty() ? cacMon.split(",")[0].trim() : "";
    }

    public void setMamon(String mamon) {
        this.cacMon = mamon;
    }

    public String getTenMon() {
        return tenCacMon;
    }

    public void setTenMon(String tenMon) {
        this.tenCacMon = tenMon;
    }

    // -- Other methods identical to previous submission --

    /**
     * Lấy danh sách tất cả giáo viên
     */
    public ArrayList<GiaoVienModel> fetchAllTeachers() {
        ArrayList<GiaoVienModel> list = new ArrayList<>();
        // Query to get Teacher, Dept Name, and List of Subjects
        String query = "SELECT g.magv, g.hoten, g.gioitinh, g.ngaysinh, g.email, g.sdt, g.username, g.mabomon, " +
                "b.tenbomon, " +
                "GROUP_CONCAT(DISTINCT gd.mamon SEPARATOR ',') as cac_mon, " +
                "GROUP_CONCAT(DISTINCT m.tenmon SEPARATOR ', ') as ten_cac_mon " +
                "FROM tblgiaovien g " +
                "LEFT JOIN tblbomon b ON g.mabomon = b.mabomon " +
                "LEFT JOIN tbl_giangday gd ON g.magv = gd.magv " +
                "LEFT JOIN tblmonhoc m ON gd.mamon = m.mamon " +
                "GROUP BY g.magv, g.hoten, g.gioitinh, g.ngaysinh, g.email, g.sdt, g.username, g.mabomon, b.tenbomon " +
                "ORDER BY g.magv ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                GiaoVienModel gv = new GiaoVienModel(
                        rs.getString("magv"),
                        rs.getString("hoten"),
                        rs.getString("gioitinh"),
                        rs.getString("ngaysinh") != null ? rs.getString("ngaysinh") : "",
                        rs.getString("email"),
                        rs.getString("sdt"),
                        rs.getString("mabomon"),
                        rs.getString("tenbomon"),
                        rs.getString("cac_mon"), // CSV codes
                        rs.getString("ten_cac_mon"), // CSV names
                        rs.getString("username"));
                list.add(gv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm giáo viên mới
     */
    public boolean themGiaoVien(GiaoVienModel gv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Thêm user
            String checkUser = "SELECT COUNT(*) FROM tbluser WHERE username = ?";
            boolean userExists = false;
            try (PreparedStatement ps = conn.prepareStatement(checkUser)) {
                ps.setString(1, gv.getUsername());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0)
                    userExists = true;
            }

            if (!userExists) {
                String userQuery = "INSERT INTO tbluser (username, password, type) VALUES (?, ?, ?)";
                try (PreparedStatement userPs = conn.prepareStatement(userQuery)) {
                    userPs.setString(1, gv.getUsername());
                    userPs.setString(2, "123456"); // Default password
                    userPs.setInt(3, 1); // Type 1 = Giáo viên
                    userPs.executeUpdate();
                }
            }

            // 2. Thêm giáo viên vào tblgiaovien
            String gvQuery = "INSERT INTO tblgiaovien (magv, hoten, gioitinh, ngaysinh, email, sdt, mabomon, username) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement gvPs = conn.prepareStatement(gvQuery)) {
                gvPs.setString(1, gv.getMagv());
                gvPs.setString(2, gv.getHoten());
                gvPs.setString(3, gv.getGioitinh());

                String ngaysinh = gv.getNgaysinh();
                if (ngaysinh != null && !ngaysinh.trim().isEmpty()) {
                    gvPs.setDate(4, java.sql.Date.valueOf(ngaysinh));
                } else {
                    gvPs.setNull(4, java.sql.Types.DATE);
                }

                gvPs.setString(5, gv.getEmail());
                gvPs.setString(6, gv.getSdt());
                // mabomon can be null
                if (gv.getMabomon() != null && !gv.getMabomon().isEmpty())
                    gvPs.setString(7, gv.getMabomon());
                else
                    gvPs.setNull(7, java.sql.Types.VARCHAR);

                gvPs.setString(8, gv.getUsername());

                gvPs.executeUpdate();
            }

            // 3. Insert assignment into tbl_giangday
            if (gv.getCacMon() != null && !gv.getCacMon().isEmpty()) {
                String[] mons = gv.getCacMon().split(",");
                String assignQuery = "INSERT INTO tbl_giangday (magv, mamon) VALUES (?, ?)";
                try (PreparedStatement psAssign = conn.prepareStatement(assignQuery)) {
                    for (String mon : mons) {
                        psAssign.setString(1, gv.getMagv());
                        psAssign.setString(2, mon.trim());
                        psAssign.addBatch();
                    }
                    psAssign.executeBatch();
                }
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
        } catch (Exception e) {
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
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Cập nhật thông tin giáo viên
     */
    public boolean capNhatGiaoVien(GiaoVienModel gv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String query = "UPDATE tblgiaovien SET hoten=?, gioitinh=?, ngaysinh=?, email=?, sdt=?, " +
                    "mabomon=?, username=? WHERE magv=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, gv.getHoten());
                ps.setString(2, gv.getGioitinh());
                ps.setString(3, gv.getNgaysinh() != null && !gv.getNgaysinh().isEmpty() ? gv.getNgaysinh() : null);
                ps.setString(4, gv.getEmail());
                ps.setString(5, gv.getSdt());
                ps.setString(6, gv.getMabomon());
                ps.setString(7, gv.getUsername());
                ps.setString(8, gv.getMagv());
                ps.executeUpdate();
            }

            // Update assignments: Delete old, Insert new
            String deleteQuery = "DELETE FROM tbl_giangday WHERE magv=?";
            try (PreparedStatement psDel = conn.prepareStatement(deleteQuery)) {
                psDel.setString(1, gv.getMagv());
                psDel.executeUpdate();
            }

            if (gv.getCacMon() != null && !gv.getCacMon().isEmpty()) {
                String[] mons = gv.getCacMon().split(",");
                String assignQuery = "INSERT INTO tbl_giangday (magv, mamon) VALUES (?, ?)";
                try (PreparedStatement psAssign = conn.prepareStatement(assignQuery)) {
                    for (String mon : mons) {
                        psAssign.setString(1, gv.getMagv());
                        psAssign.setString(2, mon.trim());
                        psAssign.addBatch();
                    }
                    psAssign.executeBatch();
                }
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
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Xóa giáo viên
     */
    public boolean xoaGiaoVien(String magv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String getUsername = "SELECT username FROM tblgiaovien WHERE magv = ?";
            String username = null;
            try (PreparedStatement getPs = conn.prepareStatement(getUsername)) {
                getPs.setString(1, magv);
                try (ResultSet rs = getPs.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }

            // 1. Xóa phân công giảng dạy (assignment)
            String deleteGiangDay = "DELETE FROM tbl_giangday WHERE magv = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteGiangDay)) {
                ps.setString(1, magv);
                ps.executeUpdate();
            }

            // 2. Xóa phân công lớp (homeroom/class assignment)
            String deletePhancongQuery = "DELETE FROM tblphancong WHERE magv = ?";
            try (PreparedStatement pcPs = conn.prepareStatement(deletePhancongQuery)) {
                pcPs.setString(1, magv);
                pcPs.executeUpdate();
            }

            // 3. Xóa giáo viên
            String deleteGvQuery = "DELETE FROM tblgiaovien WHERE magv = ?";
            try (PreparedStatement gvPs = conn.prepareStatement(deleteGvQuery)) {
                gvPs.setString(1, magv);
                gvPs.executeUpdate();
            }

            // 4. Xóa user
            if (username != null && !username.isEmpty()) {
                String deleteUserQuery = "DELETE FROM tbluser WHERE username = ?";
                try (PreparedStatement userPs = conn.prepareStatement(deleteUserQuery)) {
                    userPs.setString(1, username);
                    userPs.executeUpdate();
                }
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
            } catch (SQLException e) {
            }
        }
    }

    public GiaoVienModel getGiaoVienByMagv(String magv) {
        String query = "SELECT g.magv, g.hoten, g.gioitinh, g.ngaysinh, g.email, g.sdt, g.username, g.mabomon, " +
                "b.tenbomon, " +
                "GROUP_CONCAT(DISTINCT gd.mamon SEPARATOR ',') as cac_mon, " +
                "GROUP_CONCAT(DISTINCT m.tenmon SEPARATOR ', ') as ten_cac_mon " +
                "FROM tblgiaovien g " +
                "LEFT JOIN tblbomon b ON g.mabomon = b.mabomon " +
                "LEFT JOIN tbl_giangday gd ON g.magv = gd.magv " +
                "LEFT JOIN tblmonhoc m ON gd.mamon = m.mamon " +
                "WHERE g.magv = ? " +
                "GROUP BY g.magv, g.hoten, g.gioitinh, g.ngaysinh, g.email, g.sdt, g.username, g.mabomon, b.tenbomon";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GiaoVienModel(
                            rs.getString("magv"),
                            rs.getString("hoten"),
                            rs.getString("gioitinh"),
                            rs.getString("ngaysinh") != null ? rs.getString("ngaysinh") : "",
                            rs.getString("email"),
                            rs.getString("sdt"),
                            rs.getString("mabomon"),
                            rs.getString("tenbomon"),
                            rs.getString("cac_mon"),
                            rs.getString("ten_cac_mon"),
                            rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistMagv(String magv) {
        String query = "SELECT COUNT(*) FROM tblgiaovien WHERE magv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTeacherCount() {
        String query = "SELECT COUNT(*) FROM tblgiaovien";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<GiaoVienModel> searchGiaoVien(String keyword) {
        ArrayList<GiaoVienModel> list = new ArrayList<>();
        String query = "SELECT g.magv, g.hoten, g.gioitinh, g.ngaysinh, g.email, g.sdt, g.username, g.mabomon, " +
                "b.tenbomon, " +
                "GROUP_CONCAT(DISTINCT gd.mamon SEPARATOR ',') as cac_mon, " +
                "GROUP_CONCAT(DISTINCT m.tenmon SEPARATOR ', ') as ten_cac_mon " +
                "FROM tblgiaovien g " +
                "LEFT JOIN tblbomon b ON g.mabomon = b.mabomon " +
                "LEFT JOIN tbl_giangday gd ON g.magv = gd.magv " +
                "LEFT JOIN tblmonhoc m ON gd.mamon = m.mamon " +
                "WHERE g.magv LIKE ? OR g.hoten LIKE ? " +
                "GROUP BY g.magv, g.hoten, g.gioitinh, g.ngaysinh, g.email, g.sdt, g.username, g.mabomon, b.tenbomon " +
                "ORDER BY g.magv ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new GiaoVienModel(
                            rs.getString("magv"),
                            rs.getString("hoten"),
                            rs.getString("gioitinh"),
                            rs.getString("ngaysinh") != null ? rs.getString("ngaysinh") : "",
                            rs.getString("email"),
                            rs.getString("sdt"),
                            rs.getString("mabomon"),
                            rs.getString("tenbomon"),
                            rs.getString("cac_mon"),
                            rs.getString("ten_cac_mon"),
                            rs.getString("username")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Legacy method: Get first subject taught by teacher (username)
     */
    public String getMonHocByUsername(String username) {
        String query = "SELECT gd.mamon FROM tbl_giangday gd " +
                "JOIN tblgiaovien g ON gd.magv = g.magv " +
                "WHERE g.username = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("mamon");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Legacy helper: Get subject name by code
     */
    public String getTenMonByMamon(String mamon) {
        String query = "SELECT tenmon FROM tblmonhoc WHERE mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, mamon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenmon");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
