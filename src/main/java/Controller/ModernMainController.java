package Controller;

import Model.DiemModel;
import View.ModernMainLayout;
import View.ModernDiemPanel;
import View.QuanLyLopPanel;
import View.PhanCongGiaoVienPanel;
import View.QuanLySinhVienPanel;
import View.QuanLyGiaoVienPanel;
import View.KhoaSoNienKhoaPanel;
import View.QuanLyBoMonPanel;
import View.QuanLyMonHocPanel; // New
import Controller.QuanLyLopController;
import Controller.PhanCongGiaoVienController;
import Controller.QuanLySinhVienController;
import Controller.QuanLyGiaoVienController;
import Controller.QuanLyBoMonController;
import Controller.QuanLyMonHocController;
import Controller.HomeroomClassController;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modern MainController with enhanced functionality
 */
public class ModernMainController {

    private ModernMainLayout view;
    private String username;
    private int userType;
    private ModernDiemPanel diemPanel;

    // Controllers as instance variables for refresh capability
    private QuanLyLopController lopController;
    private PhanCongGiaoVienController phanCongController;
    private QuanLySinhVienController svController;
    private QuanLyGiaoVienController gvController;
    private QuanLyBoMonController boMonController;
    private QuanLyMonHocController monHocController; // New

    public ModernMainController(ModernMainLayout view, String username, int userType) {
        this.view = view;
        this.username = username;
        this.userType = userType;

        initializePanels();
        setupEventHandlers();

        // Show dashboard by default
        view.showPanel("DASHBOARD");
        updateDashboardStats(); // Update stats immediately
        view.setVisible(true);
    }

    private void initializePanels() {
        // Initialize DiemPanel with modern design
        diemPanel = new ModernDiemPanel(username, userType);
        view.getMainContentPanel().add(diemPanel, "DIEM");

        // Initialize DiemController
        ModernDiemController diemController = new ModernDiemController(diemPanel, username, userType);

        // Initialize Admin panels
        if (userType == 0) {
            // Quản lý Lớp
            QuanLyLopPanel lopPanel = new QuanLyLopPanel();
            view.getMainContentPanel().add(lopPanel, "LOP");
            lopController = new QuanLyLopController(lopPanel, this); // Pass main controller for refresh

            // Phân lớp cho Giáo viên
            PhanCongGiaoVienPanel phanCongPanel = new PhanCongGiaoVienPanel();
            view.getMainContentPanel().add(phanCongPanel, "PHANCONG");
            phanCongController = new PhanCongGiaoVienController(phanCongPanel);

            // Quản lý Sinh viên
            QuanLySinhVienPanel svPanel = new QuanLySinhVienPanel();
            view.getMainContentPanel().add(svPanel, "SINHVIEN");
            svController = new QuanLySinhVienController(svPanel);

            // Quản lý Giáo viên
            QuanLyGiaoVienPanel gvPanel = new QuanLyGiaoVienPanel();
            view.getMainContentPanel().add(gvPanel, "GIAOVIEN");
            gvController = new QuanLyGiaoVienController(gvPanel);

            // Quan Ly Bo Mon
            QuanLyBoMonPanel bmPanel = new QuanLyBoMonPanel();
            view.getMainContentPanel().add(bmPanel, "BOMON");
            boMonController = new QuanLyBoMonController(bmPanel);

            // Quan Ly Mon Hoc (New)
            QuanLyMonHocPanel mhPanel = new QuanLyMonHocPanel();
            view.getMainContentPanel().add(mhPanel, "MONHOC");
            monHocController = new QuanLyMonHocController(mhPanel, this); // Pass main controller

            // Thông báo (Admin)
            View.QuanLyThongBaoPanel tbPanel = new View.QuanLyThongBaoPanel(0); // 0=Admin
            view.getMainContentPanel().add(tbPanel, "THONGBAO");

            new Controller.QuanLyThongBaoController(tbPanel, username, 0, "Admin");

            // Khóa sổ & Niên khóa
            KhoaSoNienKhoaPanel khoaSoPanel = new KhoaSoNienKhoaPanel();
            view.getMainContentPanel().add(khoaSoPanel, "KHOASO");

        } else if (userType == 1) { // Teacher
            // Thông báo (Teacher) -> Wrapper
            View.QuanLyThongBaoPanel sendPanel = new View.QuanLyThongBaoPanel(1); // Manage
            View.XemThongBaoPanel viewPanel = new View.XemThongBaoPanel(); // View Admin's
            View.TeacherThongBaoWrapper wrapper = new View.TeacherThongBaoWrapper(sendPanel, viewPanel);

            view.getMainContentPanel().add(wrapper, "THONGBAO");
            String realName = fetchRealName(username, 1);

            // Controller manages both
            new Controller.QuanLyThongBaoController(wrapper, username, 1, realName);

            // Homeroom panel for teachers
            View.HomeroomClassPanel homeroomPanel = new View.HomeroomClassPanel(username);
            view.getMainContentPanel().add(homeroomPanel, "HOMEROOM");
            new Controller.HomeroomClassController(homeroomPanel, username);

            // Khóa sổ & Niên khóa (Read-Only for Teacher)
            KhoaSoNienKhoaPanel khoaSoPanel = new KhoaSoNienKhoaPanel(userType);
            view.getMainContentPanel().add(khoaSoPanel, "KHOASO");

        } else if (userType == 2) { // Student
            String studentClass = fetchStudentClass(username);
            View.XemThongBaoPanel tbPanel = new View.XemThongBaoPanel();
            view.getMainContentPanel().add(tbPanel, "THONGBAO");
            new Controller.QuanLyThongBaoController(tbPanel, username, studentClass);
        }
    }

    private void setupEventHandlers() {
        // Navigation event handlers
        view.onDashboardClick(e -> {
            updateDashboardStats(); // Update when clicking Dashboard
            view.showPanel("DASHBOARD");
        });
        view.onDiemClick(e -> view.showPanel("DIEM"));

        if (userType == 0) { // Admin
            view.onSinhVienClick(e -> view.showPanel("SINHVIEN"));
            view.onGiaoVienClick(e -> {
                if (gvController != null)
                    gvController.refreshAll();
                view.showPanel("GIAOVIEN");
            });
            view.onLopClick(e -> {
                if (phanCongController != null)
                    phanCongController.refreshAll();
                view.showPanel("PHANCONG");
            }); // Phân lớp
            view.onQuanLyLopClick(e -> view.showPanel("LOP")); // Quản lý lớp
            view.onQuanLyBoMonClick(e -> {
                if (boMonController != null)
                    boMonController.refreshBoMon();
                view.showPanel("BOMON");
            });
            view.onMonHocClick(e -> view.showPanel("MONHOC")); // Quản lý môn học (New)
            view.onKhoaSoClick(e -> view.showPanel("KHOASO")); // Khóa sổ
            view.onThongBaoClick(e -> view.showPanel("THONGBAO"));
        } else if (userType == 1) { // Teacher
            view.onHomeroomClick(e -> view.showPanel("HOMEROOM"));
            view.onThongBaoClick(e -> view.showPanel("THONGBAO"));
        } else if (userType == 2) { // Student
            view.onThongBaoClick(e -> view.showPanel("THONGBAO"));
        }

        // Logout handler
        view.onDangXuatClick(e -> handleLogout());
    }

    // Helper to get Real Name
    private String fetchRealName(String username, int type) {
        String query = "";
        if (type == 1) { // Teacher
            query = "SELECT hoten FROM tblgiaovien WHERE username = ?";
        } else {
            return "Admin"; // Default for admin
        }

        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("hoten");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username; // Fallback
    }

    // Helper to get Student Class
    private String fetchStudentClass(String username) {
        String query = "SELECT malop FROM tblsinhvien WHERE masv = ?"; // Assuming masv == username
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("malop");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Refresh all class-related data across panels
     */
    public void refreshClassData() {
        if (userType == 0) { // Admin only
            if (svController != null) {
                svController.refreshLopData();
            }
            if (phanCongController != null) {
                phanCongController.refreshLopData();
            }
        }
        updateDashboardStats(); // Update stats on data change
    }

    /**
     * Refresh all data across panels (including student data)
     */
    public void refreshAllData() {
        if (userType == 0) { // Admin only
            refreshClassData();
            if (svController != null) {
                svController.refreshAllData();
            }
            if (boMonController != null) {
                boMonController.refreshBoMon();
            }
            if (monHocController != null) {
                monHocController.loadData(); // Assuming we make this public or add refresh
            }
        }
        updateDashboardStats(); // Update stats on data change
    }

    /**
     * Refresh all data when subjects are modified
     */
    public void refreshSubjectRelatedData() {
        if (userType == 0) {
            if (boMonController != null) {
                boMonController.refreshBoMon();
            }
            if (phanCongController != null) {
                phanCongController.refreshAll();
            }
            updateDashboardStats();
        }
    }

    /**
     * Update Dashboard Statistics
     */
    public void updateDashboardStats() {
        System.out.println("=== updateDashboardStats called ===");
        System.out.println("Username: " + username);
        System.out.println("UserType: " + userType);

        try {
            Model.SinhVienModel svModel = new Model.SinhVienModel();
            Model.GiaoVienModel gvModel = new Model.GiaoVienModel();
            Model.LopModel lopModel = new Model.LopModel();
            Model.DiemModel diemModel = new Model.DiemModel();

            if (userType == 0) { // Admin
                view.updateStat("SV", String.valueOf(svModel.getStudentCount()));
                view.updateStat("GV", String.valueOf(gvModel.getTeacherCount()));
                view.updateStat("Lớp", String.valueOf(lopModel.getClassCount()));
                view.updateStat("Môn", String.valueOf(diemModel.getSubjectCount()));
            } else if (userType == 1) { // Giáo viên - new statistics
                System.out.println("Processing teacher statistics...");

                // Convert username to magv (e.g., gv001 -> GV001)
                String magv = username.toUpperCase();
                System.out.println("Derived magv from username: " + magv);

                if (magv != null && !magv.isEmpty()) {
                    // 1. Count subjects assigned to teacher
                    int subjectCount = countSubjectsByTeacher(magv);
                    System.out.println("Subject count: " + subjectCount);

                    // 2. Count classes managed by teacher
                    int classCount = countClassesByTeacher(magv);
                    System.out.println("Class count: " + classCount);

                    // 3. Count total students in managed classes
                    int totalStudents = countStudentsByTeacher(magv);
                    System.out.println("Total students: " + totalStudents);

                    // 4. Count students in homeroom class
                    int homeroomStudents = countHomeroomStudents(magv);
                    System.out.println("Homeroom students: " + homeroomStudents);

                    view.updateStat("Môn", String.valueOf(subjectCount));
                    view.updateStat("Lớp", String.valueOf(classCount));
                    view.updateStat("SV", String.valueOf(totalStudents));
                    view.updateStat("SVCN", String.valueOf(homeroomStudents));
                    System.out.println("Stats updated successfully");
                } else {
                    System.out.println("ERROR: magv is null or empty");
                }
            } else if (userType == 2) { // Sinh viên - new statistics
                String masv = diemModel.getMasvByUsername(username);
                if (masv != null) {
                    // 1. Count total subjects
                    int subjectCount = countSubjectsByStudent(masv);

                    // 2. Calculate average grade
                    double avgGrade = calculateAverageGrade(masv);

                    // 3. Get classification based on average
                    String classification = getGradeClassification(avgGrade);

                    view.updateStat("Môn", String.valueOf(subjectCount));
                    view.updateStat("TB", avgGrade > 0 ? String.format("%.2f", avgGrade) : "N/A");
                    view.updateStat("XL", classification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper methods for teacher statistics
    private int countSubjectsByTeacher(String magv) {
        System.out.println("countSubjectsByTeacher called with magv: " + magv);
        String sql = "SELECT COUNT(DISTINCT mamon) FROM tbl_giangday WHERE magv = ?";
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, magv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Subject count result: " + count);
                    return count;
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR in countSubjectsByTeacher: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private int countClassesByTeacher(String magv) {
        String sql = "SELECT COUNT(DISTINCT malop) FROM tblphancong WHERE magv = ?";
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, magv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countStudentsByTeacher(String magv) {
        String sql = "SELECT COUNT(DISTINCT s.masv) " +
                "FROM tblsinhvien s " +
                "JOIN tblphancong p ON s.malop = p.malop " +
                "WHERE p.magv = ?";
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, magv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countHomeroomStudents(String magv) {
        String sql = "SELECT COUNT(s.masv) " +
                "FROM tblsinhvien s " +
                "JOIN tblclass c ON s.malop = c.malop " +
                "WHERE c.magvcn = ?";
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, magv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper methods for student statistics
    private int countSubjectsByStudent(String masv) {
        String sql = "SELECT COUNT(DISTINCT mamon) FROM tbldiem WHERE masv = ?";
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, masv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double calculateAverageGrade(String masv) {
        String sql = "SELECT AVG(diemtongket) FROM tbldiem WHERE masv = ?";
        try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, masv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : avg;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private String getGradeClassification(double avg) {
        if (avg >= 9.0)
            return "Xuất sắc";
        if (avg >= 8.0)
            return "Giỏi";
        if (avg >= 6.5)
            return "Khá";
        if (avg >= 5.0)
            return "Trung bình";
        if (avg > 0)
            return "Yếu";
        return "N/A";
    }

    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            view.dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    Model.LoginModel loginModel = new Model.LoginModel();
                    View.ModernLoginView loginView = new View.ModernLoginView();
                    Controller.ModernLoginController loginController = new Controller.ModernLoginController(loginView,
                            loginModel);
                    loginView.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}