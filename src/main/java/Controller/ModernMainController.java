package Controller;

import Model.DiemModel;
import View.ModernMainLayout;
import View.ModernDiemPanel;
import View.QuanLyLopPanel;
import View.PhanCongGiaoVienPanel;
import View.QuanLySinhVienPanel;
import View.QuanLyGiaoVienPanel;
import View.KhoaSoNienKhoaPanel; // Import new panel
import Controller.QuanLyLopController;
import Controller.PhanCongGiaoVienController;
import Controller.QuanLySinhVienController;
import Controller.QuanLyGiaoVienController;
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

            // Thông báo (Admin)
            View.QuanLyThongBaoPanel tbPanel = new View.QuanLyThongBaoPanel(0); // 0=Admin
            view.getMainContentPanel().add(tbPanel, "THONGBAO");
            // Assuming "Admin" is the real name, or fetch it. For now detailed name
            // fetching is complex, use username.
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
            view.onGiaoVienClick(e -> view.showPanel("GIAOVIEN")); // Quản lý giáo viên
            view.onLopClick(e -> view.showPanel("PHANCONG")); // Phân lớp
            view.onQuanLyLopClick(e -> view.showPanel("LOP")); // Quản lý lớp
            view.onKhoaSoClick(e -> view.showPanel("KHOASO")); // Khóa sổ
            view.onThongBaoClick(e -> view.showPanel("THONGBAO"));
        } else if (userType == 1) { // Teacher
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
            // Refresh class data first
            refreshClassData();

            // Refresh student data (in case student-class relationship changed)
            if (svController != null) {
                svController.refreshAllData();
            }
        }
        updateDashboardStats(); // Update stats on data change
    }

    /**
     * Update Dashboard Statistics
     */
    public void updateDashboardStats() {
        try {
            // Models
            Model.SinhVienModel svModel = new Model.SinhVienModel();
            Model.GiaoVienModel gvModel = new Model.GiaoVienModel();
            Model.LopModel lopModel = new Model.LopModel();
            Model.DiemModel diemModel = new Model.DiemModel();

            if (userType == 0) { // Admin
                view.updateStat("SV", String.valueOf(svModel.getStudentCount()));
                view.updateStat("GV", String.valueOf(gvModel.getTeacherCount()));
                view.updateStat("Lớp", String.valueOf(lopModel.getClassCount()));
                // view.updateStat("Điểm", String.valueOf(diemModel.getDiemRecordCount())); //
                // Removed
                view.updateStat("Môn", String.valueOf(diemModel.getSubjectCount()));
                // view.updateStat("TB", String.valueOf(diemModel.getSystemAverageScore())); //
                // Removed
            } else if (userType == 1) { // Giáo viên

                String magv = null;
                // Quick hack to get magv
                try (java.sql.Connection conn = connection.DatabaseConnection.getConnection();
                        java.sql.PreparedStatement ps = conn
                                .prepareStatement("SELECT magv FROM tblgiaovien WHERE username = ?")) {
                    ps.setString(1, username);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next())
                            magv = rs.getString(1);
                    }
                }

                if (magv != null) {
                    // Get actual student count using the new method
                    int studentCount = svModel.getStudentCountByTeacher(magv);
                    view.updateStat("SV", String.valueOf(studentCount));

                    // Use the methods we added to DiemModel, passing magv
                    view.updateStat("Điểm", String.valueOf(diemModel.getGradedCountByTeacher(magv)));
                    // view.updateStat("TB",
                    // String.valueOf(diemModel.getAverageScoreByTeacher(magv))); // Removed
                    view.updateStat("Lớp", String.valueOf(lopModel.getClassCountByTeacher(magv)));
                    view.updateStat("Môn", "1"); // Generally 1
                    // view.updateStat("DiemGioi",
                    // String.valueOf(diemModel.getGioiCountByTeacher(magv))); // Removed
                }

            } else if (userType == 2) { // Sinh viên
                String masv = diemModel.getMasvByUsername(username);
                if (masv != null) {
                    view.updateStat("Môn", String.valueOf(diemModel.getStudentSubjectCount(masv)));
                    view.updateStat("TB", String.valueOf(diemModel.getStudentAverageScore(masv)));
                    view.updateStat("HT", String.valueOf(diemModel.getPassedCount(masv)));
                    view.updateStat("CB", String.valueOf(diemModel.getImprovementCount(masv)));
                    view.updateStat("XH", "..."); // Unimplemented
                    view.updateStat("TinChi", String.valueOf(diemModel.getPassedCount(masv) * 3)); // Assume 3 credits
                                                                                                   // per subject
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            // Return to login screen
            SwingUtilities.invokeLater(() -> {
                try {
                    // Create login model
                    Model.LoginModel loginModel = new Model.LoginModel();

                    // Create and show login view
                    View.ModernLoginView loginView = new View.ModernLoginView();

                    // Create login controller
                    Controller.ModernLoginController loginController = new Controller.ModernLoginController(loginView,
                            loginModel);

                    // Show the login window
                    loginView.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}