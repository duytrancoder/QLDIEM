package Controller;

import Model.ThongBaoModel;
import Model.LopModel;
import View.QuanLyThongBaoPanel;
import View.XemThongBaoPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import View.TeacherThongBaoWrapper;

public class QuanLyThongBaoController {

    private QuanLyThongBaoPanel adminView;
    private XemThongBaoPanel studentView;
    private ThongBaoModel model;
    private String currentUser;
    private int userType; // 0: Admin, 1: Teacher, 2: Student
    private String studentClass; // For student filtering
    private String realName; // For display

    private TeacherThongBaoWrapper teacherWrapper;

    // Commander for Admin
    public QuanLyThongBaoController(QuanLyThongBaoPanel view, String username, int type, String realName) {
        this.adminView = view;
        this.currentUser = username;
        this.userType = type;
        this.realName = realName;
        this.model = new ThongBaoModel();

        initController();
        loadData();
    }

    // New: Commander for Teacher (Wrapper)
    public QuanLyThongBaoController(View.TeacherThongBaoWrapper wrapper, String username, int type, String realName) {
        this.teacherWrapper = wrapper;
        this.adminView = wrapper.getSendPanel();
        this.studentView = wrapper.getViewPanel(); // Reuse
                                                   // student
                                                   // view
                                                   // for
                                                   // "View
                                                   // Admin
                                                   // Notifications"
        this.currentUser = username;
        this.userType = type;
        this.realName = realName;
        this.model = new ThongBaoModel();

        initController();

        // Load data for both panels
        loadData(); // Loads "Sent by Me" into adminView (Send Panel)
        loadTeacherViewData(); // Loads "From Admin" into studentView (View Panel)
    }

    // Commander for Student View
    public QuanLyThongBaoController(XemThongBaoPanel view, String username, String maLop) {
        this.studentView = view;
        this.currentUser = username;
        this.studentClass = maLop;
        this.userType = 2; // Student
        this.model = new ThongBaoModel();

        loadStudentData();
    }

    private void initController() {
        if (adminView == null)
            return;

        // Load classes for teacher
        if (userType == 1) {
            LopModel lopModel = new LopModel();
            ArrayList<LopModel> classes = lopModel.getLopByGiaoVien(currentUser);
            ArrayList<String> classStrings = new ArrayList<>();
            for (LopModel l : classes) {
                classStrings.add(l.getMalop() + " - " + l.getTenlop());
            }
            adminView.setClassList(classStrings);
        }

        adminView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                switch (cmd) {
                    case "Đăng bài":
                        handleThem();
                        break;
                    case "Sửa":
                        handleSua();
                        break;
                    case "Xóa":
                        handleXoa();
                        break;
                    case "Làm mới":
                        loadData();
                        adminView.clearForm();
                        break;
                }
            }
        });

        // Update: Capture selection correctly via List
        adminView.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = adminView.getTable().getSelectedRow();
                if (row >= 0 && row < currentList.size()) {
                    ThongBaoModel t = currentList.get(row);
                    adminView.fillForm(t);
                }
            }
        });
    }

    private ArrayList<ThongBaoModel> currentList = new ArrayList<>();

    // Loads data into the "Send/Manage" panel (adminView)
    public void loadData() {
        if (adminView == null)
            return;

        if (userType == 0) {
            currentList = model.getAllAggregated();
        } else if (userType == 1) {
            currentList = model.getForTeacher(currentUser);
        }
        adminView.loadTableData(currentList);
    }

    // Loads data into the "View" panel (studentView) for Teacher
    public void loadTeacherViewData() {
        if (studentView == null)
            return;
        ArrayList<ThongBaoModel> list = model.getForStudent("###");
        studentView.loadThongBao(list);
    }

    public void loadStudentData() {
        if (studentView == null)
            return;
        currentList = model.getForStudent(studentClass);
        studentView.loadThongBao(currentList);
    }

    // Helper to get selected model from row
    private ThongBaoModel getSelectedModel() {
        int row = adminView.getTable().getSelectedRow();
        if (row >= 0 && row < currentList.size()) {
            return currentList.get(row);
        }
        return null;
    }

    private void handleThem() {
        ThongBaoModel t = adminView.getFormData();
        if (t.getTieude().isEmpty() || t.getNoidung().isEmpty()) {
            JOptionPane.showMessageDialog(adminView, "Vui lòng nhập tiêu đề và nội dung!");
            return;
        }

        t.setNguoigui(currentUser);
        t.setTenNguoiGui(realName);
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

        try {
            if (userType == 1) { // Teacher: Loop through selected classes
                ArrayList<String> selectedClasses = adminView.getSelectedClasses();
                if (selectedClasses.isEmpty()) {
                    JOptionPane.showMessageDialog(adminView, "Vui lòng chọn ít nhất một lớp!");
                    return;
                }

                boolean allSuccess = true;
                for (String lop : selectedClasses) {
                    t.setPhamvi(lop);
                    if (!model.add(t, now))
                        allSuccess = false; // Use same timestamp
                }

                if (allSuccess) {
                    JOptionPane.showMessageDialog(adminView, "Đăng bài thành công!");
                } else {
                    JOptionPane.showMessageDialog(adminView, "Đăng bài có lỗi xảy ra!");
                }
                loadData();
                adminView.clearForm();

            } else { // Admin (Single post)
                if (model.add(t, now)) {
                    JOptionPane.showMessageDialog(adminView, "Đăng bài thành công!");
                    loadData();
                    adminView.clearForm();
                } else {
                    JOptionPane.showMessageDialog(adminView, "Đăng bài thất bại!");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(adminView, "Lỗi khi đăng bài: " + ex.getMessage());
        }
    }

    private void handleSua() {
        ThongBaoModel selected = getSelectedModel();
        if (selected == null) {
            JOptionPane.showMessageDialog(adminView, "Vui lòng chọn thông báo để sửa!");
            return;
        }

        // Access Control: Teacher cannot edit Admin's post
        if (userType == 1 && !selected.getNguoigui().equals(currentUser)) {
            JOptionPane.showMessageDialog(adminView, "Bạn không có quyền sửa thông báo của Admin!");
            return;
        }

        ThongBaoModel t = adminView.getFormData();
        t.setId(selected.getId()); // Use ID of main record

        // Check for changes (Title/Content)
        boolean contentChanged = !t.getTieude().equals(selected.getTieude()) ||
                !t.getNoidung().equals(selected.getNoidung());

        boolean scopeChanged = false;
        ArrayList<String> newClasses = new ArrayList<>();

        // Scope Check for Teacher
        if (userType == 1) {
            newClasses = adminView.getSelectedClasses();
            // Parse old scope from "MH01, MH02" string
            String oldScopeStr = selected.getPhamvi();
            java.util.List<String> oldClasses = new ArrayList<>();
            if (oldScopeStr != null && !oldScopeStr.isEmpty()) {
                String[] parts = oldScopeStr.split(", ");
                for (String p : parts)
                    oldClasses.add(p);
            }

            // Compare lists (Sort to ensure order doesn't affect equality)
            java.util.Collections.sort(newClasses);
            java.util.Collections.sort(oldClasses);

            scopeChanged = !newClasses.equals(oldClasses);
        }

        if (!contentChanged && !scopeChanged) {
            JOptionPane.showMessageDialog(adminView, "Chưa có dữ liệu nào được sửa!");
            return;
        }

        // Handle User Identity for Update
        if (userType == 0) {
            t.setNguoigui(currentUser);
            t.setTenNguoiGui(realName);
        } else {
            t.setNguoigui(selected.getNguoigui());
            t.setTenNguoiGui(selected.getTenNguoiGui());
        }

        try {
            if (scopeChanged) {
                // Critical: Scope changed means we must reshuffle the notification rows.
                // 1. Soft Delete the old batch
                if (selected.getGroupIds() != null && !selected.getGroupIds().isEmpty()) {
                    model.deleteBatch(selected.getGroupIds());
                } else {
                    model.delete(selected.getId());
                }

                // 2. Add New records for the NEW scope
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                boolean allSuccess = true;

                if (newClasses.isEmpty()) {
                    JOptionPane.showMessageDialog(adminView, "Lỗi: Danh sách lớp trống!");
                    return; // Should check this earlier?
                }

                for (String lop : newClasses) {
                    t.setPhamvi(lop);
                    if (!model.add(t, now))
                        allSuccess = false;
                }

                if (allSuccess) {
                    JOptionPane.showMessageDialog(adminView, "Cập nhật (thay đổi lớp) thành công!");
                } else {
                    JOptionPane.showMessageDialog(adminView, "Cập nhật có lỗi rải rác!");
                }

            } else {
                // Scope NOT changed, just update Content via Batch
                if (selected.getGroupIds() != null && !selected.getGroupIds().isEmpty()) {
                    if (model.updateBatch(t, selected.getGroupIds())) {
                        JOptionPane.showMessageDialog(adminView, "Cập nhật thành công!");
                    } else {
                        JOptionPane.showMessageDialog(adminView, "Cập nhật thất bại!");
                    }
                } else {
                    // Single update fallback
                    if (model.update(t)) {
                        JOptionPane.showMessageDialog(adminView, "Cập nhật thành công!");
                    } else {
                        JOptionPane.showMessageDialog(adminView, "Cập nhật thất bại!");
                    }
                }
            }

            loadData();
            adminView.clearForm();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(adminView, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private void handleXoa() {
        ThongBaoModel selected = getSelectedModel();
        if (selected == null) {
            JOptionPane.showMessageDialog(adminView, "Vui lòng chọn thông báo để xóa!");
            return;
        }

        // Access Control: Teacher cannot delete Admin's post
        if (userType == 1 && !selected.getNguoigui().equals(currentUser)) {
            JOptionPane.showMessageDialog(adminView, "Bạn không có quyền xóa thông báo của Admin!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(adminView,
                "Bạn có chắc muốn xóa thông báo này không?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success;
                if (selected.getGroupIds() != null && !selected.getGroupIds().isEmpty()) {
                    success = model.deleteBatch(selected.getGroupIds());
                } else {
                    success = model.delete(selected.getId());
                }

                if (success) {
                    JOptionPane.showMessageDialog(adminView, "Đã xóa thông báo!");
                    loadData();
                    adminView.clearForm();
                } else {
                    JOptionPane.showMessageDialog(adminView, "Thất bại!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(adminView, "Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }
}
