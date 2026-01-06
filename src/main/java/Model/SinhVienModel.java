package Model;

import connection.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class SinhVienModel {
    private String username;
    private String masv;
    private String hoten;
    private String ngaysinh;
    private String gioitinh;
    private String diachi;
    private String malop;
    private String tenLop; // New field for display (transient)

    public SinhVienModel(String username, String masv, String hoten, String ngaysinh, String gioitinh, String diachi,
            String malop, String tenLop) {
        this.username = username;
        this.masv = masv;
        this.hoten = hoten;
        this.ngaysinh = ngaysinh;
        this.gioitinh = gioitinh;
        this.diachi = diachi;
        this.malop = malop;
        this.tenLop = tenLop;
    }

    public SinhVienModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMasv() {
        return masv;
    }

    public void setMasv(String masv) {
        this.masv = masv;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getMalop() {
        return malop;
    }

    public void setMalop(String malop) {
        this.malop = malop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public ArrayList<SinhVienModel> ds = new ArrayList<>();

    public SinhVienModel getData(String username) throws SQLException {
        SinhVienModel sv = null;
        String query = "SELECT * FROM tblsinhvien WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sv = new SinhVienModel(
                            rs.getString("username"),
                            rs.getString("masv"),
                            rs.getString("hoten"),
                            rs.getString("ngaysinh"),
                            rs.getString("gioitinh"),
                            rs.getString("diachi"),
                            rs.getString("malop"),
                            null); // tenLop is null here
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return sv;
    }

    /**
     * Get student by student ID (masv)
     */
    public SinhVienModel getSinhVienByMasv(String masv) {
        SinhVienModel sv = null;
        String query = "SELECT * FROM tblsinhvien WHERE masv = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, masv);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sv = new SinhVienModel(
                            rs.getString("username"),
                            rs.getString("masv"),
                            rs.getString("hoten"),
                            rs.getString("ngaysinh"),
                            rs.getString("gioitinh"),
                            rs.getString("diachi"),
                            rs.getString("malop"),
                            null);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by masv: " + e.getMessage());
            e.printStackTrace();
        }

        return sv;
    }

    /**
     * Lấy danh sách sinh viên trong một lớp
     */
    public ArrayList<SinhVienModel> getSinhVienByLop(String malop) {
        ArrayList<SinhVienModel> list = new ArrayList<>();
        String query = "SELECT * FROM tblsinhvien WHERE malop = ? ORDER BY masv";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, malop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SinhVienModel sv = new SinhVienModel(
                            rs.getString("username"),
                            rs.getString("masv"),
                            rs.getString("hoten"),
                            rs.getString("ngaysinh"),
                            rs.getString("gioitinh"),
                            rs.getString("diachi"),
                            rs.getString("malop"),
                            null);
                    list.add(sv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy sinh viên theo lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy sinh viên theo lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy tất cả sinh viên
     */
    /**
     * Lấy tất cả sinh viên
     */
    public ArrayList<SinhVienModel> getAllSinhVien() {
        ArrayList<SinhVienModel> list = new ArrayList<>();
        // Use LEFT JOIN to get class name
        String query = "SELECT sv.*, c.tenlop FROM tblsinhvien sv LEFT JOIN tblclass c ON sv.malop = c.malop ORDER BY sv.masv ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                SinhVienModel sv = new SinhVienModel(
                        rs.getString("username"),
                        rs.getString("masv"),
                        rs.getString("hoten"),
                        rs.getString("ngaysinh"),
                        rs.getString("gioitinh"),
                        rs.getString("diachi"),
                        rs.getString("malop"),
                        rs.getString("tenlop")); // Get tenlop from join
                list.add(sv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách sinh viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy danh sách sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm sinh viên vào lớp (cải tiến với transaction)
     */
    public boolean themSinhVien(SinhVienModel sv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Thêm user account vào tbluser
            String userQuery = "INSERT INTO tbluser (username, password, type) VALUES (?, ?, ?)";
            try (PreparedStatement userPs = conn.prepareStatement(userQuery)) {
                userPs.setString(1, sv.getUsername());
                userPs.setString(2, "123456"); // Default password
                userPs.setInt(3, 2); // Type 2 = Sinh viên
                userPs.executeUpdate();
            }

            // 2. Thêm sinh viên vào tblsinhvien
            String svQuery = "INSERT INTO tblsinhvien (masv, hoten, ngaysinh, gioitinh, diachi, malop, username) VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement svPs = conn.prepareStatement(svQuery)) {
                svPs.setString(1, sv.getMasv());
                svPs.setString(2, sv.getHoten());
                svPs.setString(3, sv.getNgaysinh());
                svPs.setString(4, sv.getGioitinh());
                svPs.setString(5, sv.getDiachi());
                svPs.setString(6, sv.getMalop());
                svPs.setString(7, sv.getUsername());
                svPs.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi SQL khi thêm sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi không xác định khi thêm sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }

    /**
     * Cập nhật thông tin sinh viên
     */
    public boolean capNhatSinhVien(SinhVienModel sv) {
        String query = "UPDATE tblsinhvien SET hoten=?, ngaysinh=?, gioitinh=?, diachi=?, malop=? WHERE masv=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, sv.getHoten());
            ps.setString(2, sv.getNgaysinh());
            ps.setString(3, sv.getGioitinh());
            ps.setString(4, sv.getDiachi());
            ps.setString(5, sv.getMalop());
            ps.setString(6, sv.getMasv());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi cập nhật sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra mã sinh viên đã tồn tại chưa
     */
    public boolean isExistMasv(String masv) {
        String query = "SELECT COUNT(*) FROM tblsinhvien WHERE masv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, masv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra mã sinh viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra mã sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean isExistUsername(String username) {
        String query = "SELECT COUNT(*) FROM tbluser WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra username: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa sinh viên (cần xóa điểm trước, sau đó xóa sinh viên và user)
     */
    public boolean xoaSinhVien(String masv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Lấy username trước khi xóa
            String getUsername = "SELECT username FROM tblsinhvien WHERE masv = ?";
            String username = null;
            try (PreparedStatement getPs = conn.prepareStatement(getUsername)) {
                getPs.setString(1, masv);
                try (ResultSet rs = getPs.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }

            // 1. Xóa điểm của sinh viên từ tbldiem trước (vì có foreign key)
            String deleteDiemQuery = "DELETE FROM tbldiem WHERE masv = ?";
            try (PreparedStatement diemPs = conn.prepareStatement(deleteDiemQuery)) {
                diemPs.setString(1, masv);
                diemPs.executeUpdate();
                System.out.println("Đã xóa điểm của sinh viên: " + masv);
            }

            // 2. Xóa sinh viên từ tblsinhvien
            String deleteSvQuery = "DELETE FROM tblsinhvien WHERE masv = ?";
            try (PreparedStatement svPs = conn.prepareStatement(deleteSvQuery)) {
                svPs.setString(1, masv);
                int rowsAffected = svPs.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không tìm thấy sinh viên với mã: " + masv);
                }
                System.out.println("Đã xóa sinh viên: " + masv);
            }

            // 3. Xóa user account từ tbluser (nếu có)
            if (username != null && !username.isEmpty()) {
                String deleteUserQuery = "DELETE FROM tbluser WHERE username = ?";
                try (PreparedStatement userPs = conn.prepareStatement(deleteUserQuery)) {
                    userPs.setString(1, username);
                    userPs.executeUpdate();
                    System.out.println("Đã xóa user: " + username);
                }
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi SQL khi xóa sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi không xác định khi xóa sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }

    /**
     * Lấy tổng số sinh viên (cho Dashboard)
     */
    public int getStudentCount() {
        String query = "SELECT COUNT(*) FROM tblsinhvien";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đếm sinh viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi đếm sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số lượng sinh viên được dạy bởi giáo viên (theo phân công)
     */
    public int getStudentCountByTeacher(String magv) {
        String query = "SELECT COUNT(DISTINCT s.masv) FROM tblsinhvien s " +
                "JOIN tblphancong p ON s.malop = p.malop WHERE p.magv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đếm sinh viên theo giáo viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi đếm sinh viên theo giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tìm kiếm sinh viên theo từ khóa (Mã SV hoặc Họ tên)
     */
    /**
     * Tìm kiếm sinh viên theo từ khóa (Mã SV hoặc Họ tên)
     */
    public ArrayList<SinhVienModel> searchSinhVien(String keyword) {
        ArrayList<SinhVienModel> list = new ArrayList<>();
        // Use LOWER() for case-insensitive search and JOIN for class name
        String query = "SELECT sv.*, c.tenlop FROM tblsinhvien sv " +
                "LEFT JOIN tblclass c ON sv.malop = c.malop " +
                "WHERE LOWER(sv.masv) LIKE LOWER(?) OR LOWER(sv.hoten) LIKE LOWER(?) " +
                "ORDER BY sv.masv";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SinhVienModel sv = new SinhVienModel(
                            rs.getString("username"),
                            rs.getString("masv"),
                            rs.getString("hoten"),
                            rs.getString("ngaysinh"),
                            rs.getString("gioitinh"),
                            rs.getString("diachi"),
                            rs.getString("malop"),
                            rs.getString("tenlop"));
                    list.add(sv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tìm kiếm sinh viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi tìm kiếm sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}
