/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author noqok
 */
import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class DiemModel {

    // Constants for grade calculation weights
    private static final double WEIGHT_CC = 0.1;  // 10%
    private static final double WEIGHT_GK = 0.3;  // 30%
    private static final double WEIGHT_CK = 0.6;  // 60%

    private String masv;
    private String mamon;
    private int hocky;
    private String namhoc;
    private double diemcc;
    private double diemgk;
    private double diemck;
    private double diemtongket;

    public DiemModel() {
    }

    public DiemModel( String masv, String mamon, int hocky, String namhoc, double diemcc, double diemgk, double diemck, double diemtongket) {
        this.masv = masv;
        this.mamon = mamon;
        this.hocky = hocky;
        this.namhoc = namhoc;
        this.diemcc = diemcc;
        this.diemgk = diemgk;
        this.diemck = diemck;
        this.diemtongket = diemtongket; 
    }

   

    public String getMasv() {
        return masv;
    }

    public void setMasv(String masv) {
        this.masv = masv;
    }

    public String getMamon() {
        return mamon;
    }

    public void setMamon(String mamon) {
        this.mamon = mamon;
    }

    public int getHocky() {
        return hocky;
    }

    public void setHocky(int hocky) {
        this.hocky = hocky;
    }

    public String getNamhoc() {
        return namhoc;
    }

    public void setNamhoc(String namhoc) {
        this.namhoc = namhoc;
    }

    public double getDiemcc() {
        return diemcc;
    }

    public void setDiemcc(double diemcc) {
        this.diemcc = diemcc;
    }

    public double getDiemgk() {
        return diemgk;
    }

    public void setDiemgk(double diemgk) {
        this.diemgk = diemgk;
    }

    public double getDiemck() {
        return diemck;
    }

    public void setDiemck(double diemck) {
        this.diemck = diemck;
    }

    public double getDiemtongket() {
        return diemtongket;
    }

    public void setDiemtongket(double diemtongket) {
        this.diemtongket = diemtongket;
    }

    /**
     * Tính điểm tổng kết dựa trên công thức:
     * Điểm tổng kết = (Điểm CC × 10%) + (Điểm GK × 30%) + (Điểm CK × 60%)
     */
    public double calculateDiemtongket() {
        return (diemcc * WEIGHT_CC) + (diemgk * WEIGHT_GK) + (diemck * WEIGHT_CK);
    }

    public String validate() {
        if (masv == null || masv.trim().isEmpty()) {
            return "Mã sinh viên không được để trống!";
        }

        if (mamon == null || mamon.trim().isEmpty()) {
            return "Mã môn học không được để trống!";
        }

        if (hocky < 1 || hocky > 3) {
            return "Học kỳ phải là 1, 2 hoặc 3!";
        }

        if (namhoc == null || namhoc.trim().isEmpty()) {
            return "Năm học không được để trống!";
        }

        if (diemcc < 0 || diemcc > 10) {
            return "Điểm chuyên cần phải từ 0 đến 10!";
        }

        if (diemgk < 0 || diemgk > 10) {
            return "Điểm giữa kỳ phải từ 0 đến 10!";
        }

        if (diemck < 0 || diemck > 10) {
            return "Điểm cuối kỳ phải từ 0 đến 10!";
        }

        return null;
    }

    // Giữ lại phương thức này cho Admin/GV
    public ArrayList<DiemModel> getAllDiem() {
        ArrayList<DiemModel> list = new ArrayList<>();
        String query = "SELECT * FROM tbldiem";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket") // Nếu DB ko lưu cột này thì bỏ qua
                );
                list.add(d);
               
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách điểm: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy danh sách điểm: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Phương thức mới cho Sinh viên
    public ArrayList<DiemModel> getDiemByUsername(String username) {
        ArrayList<DiemModel> list = new ArrayList<>();
        // Lấy masv từ username, sau đó lấy điểm theo masv
        String query = "SELECT d.* FROM tbldiem d JOIN tblsinhvien sv ON d.masv = sv.masv WHERE sv.username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket")
                    );
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy điểm theo username: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy điểm theo username: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Lấy điểm của sinh viên trong một lớp (cho giáo viên)
     */
    public ArrayList<DiemModel> getDiemByLop(String malop) {
        ArrayList<DiemModel> list = new ArrayList<>();
        String query = "SELECT d.* FROM tbldiem d " +
                       "JOIN tblsinhvien sv ON d.masv = sv.masv " +
                       "WHERE sv.malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, malop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket")
                    );
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy điểm theo lớp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy điểm theo lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Lấy điểm của sinh viên trong một lớp theo môn học cụ thể (cho giáo viên)
     */
    public ArrayList<DiemModel> getDiemByLopAndMon(String malop, String mamon) {
        ArrayList<DiemModel> list = new ArrayList<>();
        String query = "SELECT d.* FROM tbldiem d " +
                       "JOIN tblsinhvien sv ON d.masv = sv.masv " +
                       "WHERE sv.malop = ? AND d.mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, malop);
            ps.setString(2, mamon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket")
                    );
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy điểm theo lớp và môn: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy điểm theo lớp và môn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Lấy điểm theo môn học của giáo viên (tất cả lớp)
     */
    public ArrayList<DiemModel> getDiemByMon(String mamon) {
        ArrayList<DiemModel> list = new ArrayList<>();
        String query = "SELECT * FROM tbldiem WHERE mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, mamon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket")
                    );
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy điểm theo môn: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy điểm theo môn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Lấy mã sinh viên theo username
     */
    public String getMasvByUsername(String username) {
        String query = "SELECT masv FROM tblsinhvien WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("masv");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy mã sinh viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy mã sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra sinh viên có thuộc lớp không
     */
    public boolean checkSinhVienTrongLop(String masv, String malop) {
        String query = "SELECT COUNT(*) FROM tblsinhvien WHERE masv = ? AND malop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, masv);
            ps.setString(2, malop);
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
        return false;
    }

    public ArrayList<DiemModel> getDs() {
        return getAllDiem();
    }

    public boolean insertDiem(DiemModel d) {
        String validationError = d.validate();
        if (validationError != null) {
            System.out.println("Validation error: " + validationError);
            return false;
        }

        // Kiểm tra sinh viên tồn tại
        if (!checkSinhVienExists(d.getMasv())) {
            System.out.println("Lỗi: Mã sinh viên không tồn tại: " + d.getMasv());
            return false;
        }

        // Kiểm tra môn học tồn tại
        if (!checkMonHocExists(d.getMamon())) {
            System.out.println("Lỗi: Mã môn học không tồn tại: " + d.getMamon());
            return false;
        }

        // Tính điểm tổng kết trước khi insert
        d.setDiemtongket(d.calculateDiemtongket());

        String query = "INSERT INTO tbldiem (masv, mamon, hocky, namhoc, diemcc, diemgk, diemck, diemtongket) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query))
        {
            ps.setString(1, d.getMasv());
            ps.setString(2, d.getMamon());
            ps.setInt(3, d.getHocky());
            ps.setString(4, d.getNamhoc());
            ps.setDouble(5, d.getDiemcc());
            ps.setDouble(6, d.getDiemgk());
            ps.setDouble(7, d.getDiemck());
            ps.setDouble(8, d.getDiemtongket());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Kiểm tra duplicate key error
            if (e.getErrorCode() == 1062 || e.getMessage().contains("Duplicate entry")) {
                System.err.println("Lỗi: Sinh viên đã có điểm môn này ở học kỳ này!");
            } else if (e.getErrorCode() == 1452) {
                System.err.println("Lỗi: Mã sinh viên hoặc môn học không tồn tại trong hệ thống!");
            } else {
                System.err.println("Lỗi SQL khi thêm điểm: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi thêm điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDiem(DiemModel d) {
        String validationError = d.validate();
        if (validationError != null) {
            System.out.println("Validation error: " + validationError);
            return false;
        }

        // Tính điểm tổng kết trước khi update
        d.setDiemtongket(d.calculateDiemtongket());

        // Khóa chính của tbldiem là (masv, mamon, hocky)
        String query = "UPDATE tbldiem SET diemcc=?, diemgk=?, diemck=?, diemtongket=?, namhoc=? WHERE masv=? AND mamon=? AND hocky=?";
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query))
        {
            ps.setDouble(1, d.getDiemcc());
            ps.setDouble(2, d.getDiemgk());
            ps.setDouble(3, d.getDiemck());
            ps.setDouble(4, d.getDiemtongket());
            ps.setString(5, d.getNamhoc());
            ps.setString(6, d.getMasv());
            ps.setString(7, d.getMamon());
            ps.setInt(8, d.getHocky());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi cập nhật điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDiem(String masv, String mamon, int hocky) {
        String query = "DELETE FROM tbldiem WHERE masv=? AND mamon=? AND hocky=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query))
        {
            ps.setString(1, masv);
            ps.setString(2, mamon);
            ps.setInt(3, hocky);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<DiemModel> search(String keyword) { 
        ArrayList<DiemModel> list = new ArrayList<>();
        String query = "SELECT d.* FROM tbldiem d " +
                       "JOIN tblsinhvien sv ON d.masv = sv.masv " +
                       "JOIN tblmonhoc mh ON d.mamon = mh.mamon " +
                       "WHERE d.masv LIKE ? OR sv.hoten LIKE ? OR d.mamon LIKE ? OR mh.tenmon LIKE ? OR d.namhoc LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern); // Mã SV
            ps.setString(2, searchPattern); // Tên SV  
            ps.setString(3, searchPattern); // Mã môn
            ps.setString(4, searchPattern); // Tên môn
            ps.setString(5, searchPattern); // Năm học
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {                
                    DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket")
                    );
                    list.add(d);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi SQL khi tìm kiếm điểm: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Lỗi không xác định khi tìm kiếm điểm: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Kiểm tra sinh viên có tồn tại trong database không
     */
    private boolean checkSinhVienExists(String masv) {
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
            System.err.println("Lỗi SQL khi kiểm tra sinh viên: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra môn học có tồn tại trong database không
     */
    private boolean checkMonHocExists(String mamon) {
        String query = "SELECT COUNT(*) FROM tblmonhoc WHERE mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, mamon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra môn học: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra môn học: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // --- Thống kê cho Dashboard ---
    
    public int getDiemRecordCount() {
        String query = "SELECT COUNT(*) FROM tbldiem";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getSubjectCount() {
        String query = "SELECT COUNT(*) FROM tblmonhoc";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public double getSystemAverageScore() {
        String query = "SELECT AVG(diemtongket) FROM tbldiem";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Math.round(rs.getDouble(1) * 100.0) / 100.0;
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    // Cho Sinh viên
    public int getStudentSubjectCount(String masv) {
        String query = "SELECT COUNT(DISTINCT mamon) FROM tbldiem WHERE masv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, masv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public double getStudentAverageScore(String masv) {
        String query = "SELECT AVG(diemtongket) FROM tbldiem WHERE masv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, masv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Math.round(rs.getDouble(1) * 100.0) / 100.0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    public int getPassedCount(String masv) {
        String query = "SELECT COUNT(*) FROM tbldiem WHERE masv = ? AND diemtongket >= 4.0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, masv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getImprovementCount(String masv) {
        String query = "SELECT COUNT(*) FROM tbldiem WHERE masv = ? AND diemtongket < 4.0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, masv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
    // Cho Giáo viên
    public int getGradedCountByTeacher(String magv) {
        // Đếm số lượng điểm thuộc môn của giáo viên dạy, trong các lớp giáo viên quản lý (ước lượng)
        // Hoặc đơn giản là đếm tất cả điểm của môn mà giáo viên này phụ trách
        String query = "SELECT COUNT(d.masv) FROM tbldiem d " +
                       "JOIN tblgiaovien gv ON d.mamon = gv.mamon " +
                       "WHERE gv.magv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public double getAverageScoreByTeacher(String magv) {
        String query = "SELECT AVG(d.diemtongket) FROM tbldiem d " +
                       "JOIN tblgiaovien gv ON d.mamon = gv.mamon " +
                       "WHERE gv.magv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) return Math.round(rs.getDouble(1) * 100.0) / 100.0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }
    
     public int getGioiCountByTeacher(String magv) {
        String query = "SELECT COUNT(d.masv) FROM tbldiem d " +
                       "JOIN tblgiaovien gv ON d.mamon = gv.mamon " +
                       "WHERE gv.magv = ? AND d.diemtongket >= 8.0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, magv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

}
