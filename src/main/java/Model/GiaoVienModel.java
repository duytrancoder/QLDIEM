package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

/**
 * Model quản lý giáo viên
 */
public class GiaoVienModel {
    
    private String magv;
    private String hoten;
    private String gioitinh;
    private String ngaysinh;
    private String email;
    private String sdt;
    private String makhoa;
    private String mamon;
    private String username;
    
    public GiaoVienModel() {
    }
    
    public GiaoVienModel(String magv, String hoten, String gioitinh, String ngaysinh, 
                        String email, String sdt, String makhoa, String mamon, String username) {
        this.magv = magv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.ngaysinh = ngaysinh;
        this.email = email;
        this.sdt = sdt;
        this.makhoa = makhoa;
        this.mamon = mamon;
        this.username = username;
    }
    
    // Getters and Setters
    public String getMagv() { return magv; }
    public void setMagv(String magv) { this.magv = magv; }
    
    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }
    
    public String getGioitinh() { return gioitinh; }
    public void setGioitinh(String gioitinh) { this.gioitinh = gioitinh; }
    
    public String getNgaysinh() { return ngaysinh; }
    public void setNgaysinh(String ngaysinh) { this.ngaysinh = ngaysinh; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    public String getMakhoa() { return makhoa; }
    public void setMakhoa(String makhoa) { this.makhoa = makhoa; }
    
    public String getMamon() { return mamon; }
    public void setMamon(String mamon) { this.mamon = mamon; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    /**
     * Lấy mã môn của giáo viên theo username
     */
    public String getMonHocByUsername(String username) {
        String query = "SELECT mamon FROM tblgiaovien WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("mamon");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy môn học của giáo viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy môn học của giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy tên môn học theo mã môn
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
            System.err.println("Lỗi SQL khi lấy tên môn học: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy tên môn học: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy danh sách tất cả giáo viên
     */
    public ArrayList<GiaoVienModel> getAllGiaoVien() {
        ArrayList<GiaoVienModel> list = new ArrayList<>();
        String query = "SELECT * FROM tblgiaovien ORDER BY hoten";
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
                    rs.getString("makhoa"),
                    rs.getString("mamon"),
                    rs.getString("username")
                );
                list.add(gv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Thêm giáo viên mới (bao gồm tạo user account)
     */
    public boolean themGiaoVien(GiaoVienModel gv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Thêm user account vào tbluser trước
            String userQuery = "INSERT INTO tbluser (username, password, type) VALUES (?, ?, ?)";
            try (PreparedStatement userPs = conn.prepareStatement(userQuery)) {
                userPs.setString(1, gv.getUsername());
                userPs.setString(2, "123456"); // Default password
                userPs.setInt(3, 1); // Type 1 = Giáo viên
                userPs.executeUpdate();
            }
            
            // 2. Thêm giáo viên vào tblgiaovien
            String gvQuery = "INSERT INTO tblgiaovien (magv, hoten, gioitinh, ngaysinh, email, sdt, makhoa, mamon, username) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement gvPs = conn.prepareStatement(gvQuery)) {
                gvPs.setString(1, gv.getMagv());
                gvPs.setString(2, gv.getHoten());
                gvPs.setString(3, gv.getGioitinh());
                
                // Handle date
                String ngaysinh = gv.getNgaysinh();
                if (ngaysinh != null && !ngaysinh.trim().isEmpty()) {
                    gvPs.setDate(4, java.sql.Date.valueOf(ngaysinh));
                } else {
                    gvPs.setNull(4, java.sql.Types.DATE);
                }
                
                gvPs.setString(5, gv.getEmail());
                gvPs.setString(6, gv.getSdt());
                gvPs.setString(7, gv.getMakhoa());
                gvPs.setString(8, gv.getMamon());
                gvPs.setString(9, gv.getUsername());
                
                gvPs.executeUpdate();
            }
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi SQL khi thêm giáo viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi không xác định khi thêm giáo viên: " + e.getMessage());
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
     * Cập nhật thông tin giáo viên
     */
    public boolean capNhatGiaoVien(GiaoVienModel gv) {
        String query = "UPDATE tblgiaovien SET hoten=?, gioitinh=?, ngaysinh=?, email=?, sdt=?, " +
                       "makhoa=?, mamon=?, username=? WHERE magv=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, gv.getHoten());
            ps.setString(2, gv.getGioitinh());
            ps.setString(3, gv.getNgaysinh().isEmpty() ? null : gv.getNgaysinh());
            ps.setString(4, gv.getEmail());
            ps.setString(5, gv.getSdt());
            ps.setString(6, gv.getMakhoa());
            ps.setString(7, gv.getMamon());
            ps.setString(8, gv.getUsername());
            ps.setString(9, gv.getMagv());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật giáo viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa giáo viên (cần xóa phân công và user account trước)
     */
    public boolean xoaGiaoVien(String magv) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Lấy username trước khi xóa
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
            
            // 1. Xóa phân công của giáo viên trước (nếu có)
            String deletePhancongQuery = "DELETE FROM tblphancong WHERE magv = ?";
            try (PreparedStatement pcPs = conn.prepareStatement(deletePhancongQuery)) {
                pcPs.setString(1, magv);
                pcPs.executeUpdate();
                System.out.println("Đã xóa phân công của giáo viên: " + magv);
            }
            
            // 2. Xóa giáo viên từ tblgiaovien
            String deleteGvQuery = "DELETE FROM tblgiaovien WHERE magv = ?";
            try (PreparedStatement gvPs = conn.prepareStatement(deleteGvQuery)) {
                gvPs.setString(1, magv);
                int rowsAffected = gvPs.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không tìm thấy giáo viên với mã: " + magv);
                }
                System.out.println("Đã xóa giáo viên: " + magv);
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
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi SQL khi xóa giáo viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi không xác định khi xóa giáo viên: " + e.getMessage());
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
     * Lấy thông tin giáo viên theo mã
     */
    public GiaoVienModel getGiaoVienByMagv(String magv) {
        String query = "SELECT * FROM tblgiaovien WHERE magv=?";
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
                        rs.getString("makhoa"),
                        rs.getString("mamon"),
                        rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy giáo viên: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra mã giáo viên đã tồn tại chưa
     */
    public boolean isExistMagv(String magv) {
        String query = "SELECT COUNT(*) FROM tblgiaovien WHERE magv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra mã giáo viên: " + e.getMessage());
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
        }
        return false;
    }
}

