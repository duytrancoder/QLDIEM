package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

/**
 * Model để quản lý lớp học
 */
public class LopModel {

    private String malop;
    private String tenlop;

    private String magvcn;

    public LopModel() {
    }

    public LopModel(String malop, String tenlop, String magvcn) {
        this.malop = malop;
        this.tenlop = tenlop;
        this.magvcn = magvcn;
    }

    // Getters and Setters
    public String getMalop() {
        return malop;
    }

    public void setMalop(String malop) {
        this.malop = malop;
    }

    public String getTenlop() {
        return tenlop;
    }

    public void setTenlop(String tenlop) {
        this.tenlop = tenlop;
    }

    public String getMagvcn() {
        return magvcn;
    }

    public void setMagvcn(String magvcn) {
        this.magvcn = magvcn;
    }

    /**
     * Lấy danh sách tất cả lớp
     */
    public ArrayList<LopModel> getAllLop() {
        ArrayList<LopModel> list = new ArrayList<>();
        String query = "SELECT * FROM tblclass ORDER BY tenlop";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LopModel lop = new LopModel(
                        rs.getString("malop"),
                        rs.getString("tenlop"),
                        // rs.getString("makhoa"), // Removed
                        rs.getString("magvcn"));
                list.add(lop);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy danh sách lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách lớp mà giáo viên được phân công quản lý
     */
    public ArrayList<LopModel> getLopByGiaoVien(String magv) {
        ArrayList<LopModel> list = new ArrayList<>();
        String query = "SELECT DISTINCT c.malop, c.tenlop, c.magvcn FROM tblclass c " +
                "JOIN tblphancong pc ON c.malop = pc.malop " +
                "WHERE pc.magv = ? ORDER BY c.tenlop";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LopModel lop = new LopModel(
                            rs.getString("malop"),
                            rs.getString("tenlop"),
                            // rs.getString("makhoa"),
                            rs.getString("magvcn"));
                    list.add(lop);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lớp của giáo viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy lớp của giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách môn học mà giáo viên dạy cho lớp cụ thể (từ tblphancong)
     */
    public ArrayList<String> getMonHocByGiaoVienAndLop(String magv, String malop) {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT DISTINCT pc.mamon, m.tenmon " +
                "FROM tblphancong pc " +
                "JOIN tblmonhoc m ON pc.mamon = m.mamon " +
                "WHERE pc.magv = ? AND pc.malop = ? " +
                "ORDER BY m.tenmon";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            ps.setString(2, malop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mamon = rs.getString("mamon");
                    String tenmon = rs.getString("tenmon");
                    list.add(mamon + " - " + tenmon);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy môn học của giáo viên cho lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy môn học của giáo viên cho lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy TẤT CẢ môn học được dạy trong lớp (cho GVCN xem)
     */
    public ArrayList<String> getAllSubjectsForClass(String malop) {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT DISTINCT m.mamon, m.tenmon " +
                "FROM tblphancong pc " +
                "JOIN tblmonhoc m ON pc.mamon = m.mamon " +
                "WHERE pc.malop = ? " +
                "ORDER BY m.tenmon";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, malop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mamon = rs.getString("mamon");
                    String tenmon = rs.getString("tenmon");
                    list.add(mamon + " - " + tenmon);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy môn học cho lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy môn học cho lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy mã giáo viên từ username
     */
    public String getMagvByUsername(String username) {
        String query = "SELECT magv FROM tblgiaovien WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("magv");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy mã giáo viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy mã giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kiểm tra giáo viên đã là chủ nhiệm lớp khác chưa
     * 
     * @param magv         Mã giáo viên
     * @param excludeMalop Mã lớp cần loại trừ (khi update)
     * @return true nếu giáo viên đang làm chủ nhiệm lớp khác
     */
    public boolean isTeacherAlreadyHomeroom(String magv, String excludeMalop) {
        if (magv == null || magv.trim().isEmpty()) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM tblclass WHERE magvcn = ?";
        if (excludeMalop != null && !excludeMalop.trim().isEmpty()) {
            query += " AND malop != ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            if (excludeMalop != null && !excludeMalop.trim().isEmpty()) {
                ps.setString(2, excludeMalop);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra chủ nhiệm: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra chủ nhiệm: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy mã lớp mà giáo viên đang làm chủ nhiệm
     * 
     * @param magv Mã giáo viên
     * @return Mã lớp chủ nhiệm, null nếu không là chủ nhiệm
     */
    public String getHomeroomClassByTeacher(String magv) {
        if (magv == null || magv.trim().isEmpty()) {
            return null;
        }

        String query = "SELECT malop FROM tblclass WHERE magvcn = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("malop");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lớp chủ nhiệm: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy lớp chủ nhiệm: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kiểm tra giáo viên có quyền quản lý lớp không
     */
    public boolean checkGiaoVienQuanLyLop(String magv, String malop, String mamon) {
        String query = "SELECT COUNT(*) FROM tblphancong WHERE magv = ? AND malop = ? AND mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            ps.setString(2, malop);
            ps.setString(3, mamon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra quyền: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra quyền: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm lớp mới
     */
    public boolean addLop() {
        String query = "INSERT INTO tblclass (malop, tenlop, magvcn) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, this.malop);
            ps.setString(2, this.tenlop);
            ps.setString(3, this.magvcn);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi thêm lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin lớp
     */
    public boolean updateLop() {
        String query = "UPDATE tblclass SET tenlop = ?, magvcn = ? WHERE malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, this.tenlop);
            ps.setString(2, this.magvcn);
            ps.setString(3, this.malop);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi cập nhật lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa lớp (cải tiến)
     */
    public boolean deleteLop(String malop) {
        // Kiểm tra xem lớp có sinh viên không
        if (hasStudents(malop)) {
            // Có sinh viên thì không cho xóa
            return false;
        }

        String query = "DELETE FROM tblclass WHERE malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, malop);
            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi xóa lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra lớp có sinh viên hay không
     */
    public boolean hasStudents(String malop) {
        String query = "SELECT COUNT(*) FROM tblsinhvien WHERE malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, malop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra sinh viên trong lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra sinh viên trong lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return true; // Mặc định là có sinh viên để tránh xóa nhầm
    }

    /**
     * Lấy thông tin lớp theo mã lớp
     */
    public LopModel getLopByMalop(String malop) {
        String query = "SELECT * FROM tblclass WHERE malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, malop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LopModel(
                            rs.getString("malop"),
                            rs.getString("tenlop"),
                            // rs.getString("makhoa"),
                            rs.getString("magvcn"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy thông tin lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy thông tin lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kiểm tra mã lớp đã tồn tại chưa
     */
    public boolean isExistMalop(String malop) {
        String query = "SELECT COUNT(*) FROM tblclass WHERE malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, malop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra mã lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra mã lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm phân công giáo viên quản lý lớp
     */
    public boolean themPhanCong(String magv, String malop, String mamon) {
        String query = "INSERT INTO tblphancong (magv, malop, mamon) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            ps.setString(2, malop);
            ps.setString(3, mamon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Giáo viên đã được phân công lớp này rồi!");
            } else {
                System.err.println("Lỗi SQL khi thêm phân công: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi thêm phân công: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa phân công giáo viên quản lý lớp
     */
    public boolean xoaPhanCong(String magv, String malop, String mamon) {
        String query = "DELETE FROM tblphancong WHERE magv = ? AND malop = ? AND mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            ps.setString(2, malop);
            ps.setString(3, mamon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa phân công: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi xóa phân công: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật phân công giáo viên quản lý lớp
     */
    public boolean updatePhanCong(String oldMagv, String oldMalop, String oldMamon, String newMagv, String newMalop,
            String newMamon) {
        String query = "UPDATE tblphancong SET magv = ?, malop = ?, mamon = ? WHERE magv = ? AND malop = ? AND mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, newMagv);
            ps.setString(2, newMalop);
            ps.setString(3, newMamon);
            ps.setString(4, oldMagv);
            ps.setString(5, oldMalop);
            ps.setString(6, oldMamon);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Phân công mới bị trùng lặp!");
            } else {
                System.err.println("Lỗi SQL khi cập nhật phân công: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi cập nhật phân công: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tổng số lớp (cho Dashboard)
     */
    public int getClassCount() {
        String query = "SELECT COUNT(*) FROM tblclass";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đếm lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số lượng lớp giáo viên được phân công
     */
    public int getClassCountByTeacher(String magv) {
        String query = "SELECT COUNT(*) FROM tblphancong WHERE magv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đếm lớp của giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
