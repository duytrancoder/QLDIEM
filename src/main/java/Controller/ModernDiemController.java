package Controller;

import Model.DiemModel;
import Model.LopModel;
import Model.CauHinhModel;
import View.ModernDiemPanel;
import utils.ExcelExporter;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import Model.SinhVienModel;

/**
 * Modern DiemController with enhanced functionality and smooth operations
 */
public class ModernDiemController implements ActionListener, MouseListener {

    private ModernDiemPanel view;
    private DiemModel model;
    private LopModel lopModel;
    private String username;
    private int userType;
    private boolean isEditing = false;
    private DiemModel currentDiem = null;
    private String selectedLop = null; // Lớp đang được chọn (cho giáo viên)
    private String selectedSubject = null; // Môn học được chọn (cho giáo viên)
    private String magv = null; // Mã giáo viên
    private String globalNamHoc = "";
    private int globalHocKy = 1;

    public ModernDiemController(ModernDiemPanel view, String username, int userType) {
        this.view = view;
        this.username = username;
        this.userType = userType;
        this.model = new DiemModel();
        this.lopModel = new LopModel();

        setupEventHandlers();
        setupTeacherSubjectAndClass();
        loadGlobalSettings(); // Load global config first
        loadInitialData();
        setupAutoCalculation();
    }

    public void loadGlobalSettings() {
        CauHinhModel ch = new CauHinhModel().getGlobalSettings();
        if (ch != null) {
            this.globalNamHoc = ch.getNamhoc();
            this.globalHocKy = ch.getHocky();
        } else {
            // Fallback to defaults if DB is empty or fails
            this.globalNamHoc = "2024-2025";
            this.globalHocKy = 1;
        }
        view.setGlobalSettings(globalNamHoc, globalHocKy);
    }

    private void setupTeacherSubjectAndClass() {
        if (userType == 1) { // Giáo viên
            // Username chính là mã giáo viên (magv)
            magv = username;
            System.out.println("DEBUG: Teacher username/magv=" + magv);

            if (magv != null) {
                // Check if teacher is homeroom for a class
                String homeroomClass = lopModel.getHomeroomClassByTeacher(magv);
                boolean isHomeroom = (homeroomClass != null && !homeroomClass.isEmpty());

                ArrayList<LopModel> listLop = lopModel.getLopByGiaoVien(magv);
                System.out.println("DEBUG: Found " + listLop.size() + " classes for teacher " + magv);
                if (isHomeroom) {
                    System.out.println("DEBUG: Teacher is homeroom for class " + homeroomClass);
                }

                view.loadLop(listLop);
                view.setLopVisible(true);
                view.setMonHocVisible(true); // Show subject dropdown

                // If homeroom, auto-select homeroom class
                if (isHomeroom) {
                    selectedLop = homeroomClass;
                    view.selectLopByCode(homeroomClass); // Select in dropdown
                    // Load ALL subjects for homeroom class
                    ArrayList<String> allSubjects = lopModel.getAllSubjectsForClass(homeroomClass);
                    view.loadMonHoc(allSubjects);
                    System.out.println("DEBUG: Loaded " + allSubjects.size() + " subjects for homeroom class");
                }

                // Listener cho dropdown lớp → load môn học
                view.setLopChangeListener(e -> {
                    selectedLop = view.getSelectedLop();
                    if (selectedLop != null && !selectedLop.isEmpty()) {
                        // If this is homeroom class, load ALL subjects, otherwise teacher's subjects
                        ArrayList<String> subjects;
                        if (isHomeroom && selectedLop.equals(homeroomClass)) {
                            subjects = lopModel.getAllSubjectsForClass(selectedLop);
                            System.out.println("DEBUG: Loaded ALL " + subjects.size() + " subjects for homeroom class");
                        } else {
                            subjects = lopModel.getMonHocByGiaoVienAndLop(magv, selectedLop);
                            System.out.println(
                                    "DEBUG: Loaded " + subjects.size() + " teacher subjects for class " + selectedLop);
                        }
                        view.loadMonHoc(subjects);
                        // Clear table until subject is selected
                        view.loadTableData(new ArrayList<>());
                    } else {
                        view.loadMonHoc(new ArrayList<>());
                        view.loadTableData(new ArrayList<>());
                    }
                });

                // Listener cho dropdown môn học → load điểm
                view.addMonHocChangeListener(e -> {
                    selectedSubject = view.getSelectedMonHoc();
                    if (selectedSubject != null && selectedLop != null) {
                        loadDiemForTeacher();
                    }
                });
            } else {
                System.err.println("ERROR: Username is null or empty");
                view.setLopVisible(false);
                view.setMonHocVisible(false);
            }
        }
    }

    private void setupEventHandlers() {
        view.addActionListener(this);
        view.addTableMouseListener(this);

        // Setup search functionality - search sẽ được thực hiện khi nhấn nút Tìm
        // Real-time search có thể được thêm sau nếu cần
    }

    private void loadInitialData() {
        try {
            ArrayList<DiemModel> data;
            if (userType == 2) { // Sinh viên - load with subject and teacher names
                ArrayList<Object[]> detailedData = model.getDiemByUsernameWithDetails(username);
                view.getTableModel().setRowCount(0);
                for (Object[] row : detailedData) {
                    view.getTableModel().addRow(row);
                }
                showStatusMessage("Đã tải " + detailedData.size() + " bản ghi điểm", MessageType.SUCCESS);
            } else if (userType == 1) { // Giáo viên - đợi chọn lớp và môn
                view.loadTableData(new ArrayList<>());
                showStatusMessage("Vui lòng chọn lớp và môn học để xem điểm", MessageType.INFO);
            } else { // Admin
                data = model.getAllDiem();
                view.loadTableData(data);
                showStatusMessage("Đã tải " + data.size() + " bản ghi điểm", MessageType.SUCCESS);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showStatusMessage("Lỗi khi tải dữ liệu: " + e.getMessage(), MessageType.ERROR);
        }
    }

    private void loadDiemForTeacher() {
        if (selectedSubject == null || selectedLop == null)
            return;

        ArrayList<DiemModel> list = new ArrayList<>();
        // 1. Get all students
        SinhVienModel svModel = new SinhVienModel();
        ArrayList<SinhVienModel> students = svModel.getSinhVienByLop(selectedLop);

        // 2. Get existing grades
        ArrayList<DiemModel> existingGrades = model.getDiemByLopAndMon(selectedLop, selectedSubject);

        // Map for quick lookup
        Map<String, List<DiemModel>> gradeMap = new HashMap<>();
        for (DiemModel d : existingGrades) {
            gradeMap.computeIfAbsent(d.getMasv(), k -> new ArrayList<>()).add(d);
        }

        // 3. Merge
        for (SinhVienModel sv : students) {
            if (gradeMap.containsKey(sv.getMasv())) {
                list.addAll(gradeMap.get(sv.getMasv()));
            } else {
                // Placeholder
                DiemModel d = new DiemModel();
                d.setMasv(sv.getMasv());
                d.setTenSV(sv.getHoten()); // Fix: Populate student name
                d.setMamon(selectedSubject);
                // Use Global Settings
                d.setHocky(globalHocKy);
                d.setNamhoc(globalNamHoc);

                d.setDiemcc(0.0);
                d.setDiemgk(0.0);
                d.setDiemck(0.0);
                d.setDiemtongket(0.0);
                list.add(d);
            }
        }

        view.loadTableData(list);
    }

    private void setupAutoCalculation() {
        // Add document listeners for automatic total score calculation
        // This would require access to the text fields - simplified for now
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            switch (command) {
                case "Thêm":
                    handleAdd();
                    break;
                case "Sửa":
                    handleEdit();
                    break;
                case "Xóa":
                    handleDelete();
                    break;
                case "Lưu":
                    handleSave();
                    break;
                case "Hủy":
                    handleCancel();
                    break;
                case "Tìm":
                    performSearch();
                    break;
                case "Xuất Excel":
                    handleExportExcel();
                    break;
                case "Làm mới":
                    handleRefresh();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showStatusMessage("Lỗi: " + ex.getMessage(), MessageType.ERROR);
        }
    }

    private void handleAdd() {
        if (userType == 2) {
            showStatusMessage("Sinh viên không có quyền thêm điểm", MessageType.WARNING);
            return;
        }

        // Disable Add for teacher as per new requirement
        if (userType == 1) {
            showStatusMessage("Vui lòng chọn sinh viên từ danh sách và sử dụng chức năng Sửa để nhập điểm.",
                    MessageType.WARNING);
            return;
        }

        // Giáo viên phải chọn lớp trước
        if (userType == 1 && selectedLop == null) {
            showStatusMessage("Vui lòng chọn lớp trước khi thêm điểm", MessageType.WARNING);
            return;
        }

        view.clearForm();
        view.setEditingMode(true);
        isEditing = false;
        currentDiem = null;

        // Pre-fill some fields for teacher
        view.getAcademicYearField().setText(globalNamHoc);

        showStatusMessage("Nhập thông tin điểm mới", MessageType.INFO);
    }

    private void handleEdit() {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            showStatusMessage("Vui lòng chọn một dòng để sửa", MessageType.WARNING);
            return;
        }

        if (userType == 2) {
            showStatusMessage("Sinh viên không có quyền sửa điểm", MessageType.WARNING);
            return;
        }

        // Kiểm tra quyền môn học cho giáo viên
        if (userType == 1 && selectedSubject == null) {
            showStatusMessage("Giáo viên chưa được phân công môn học", MessageType.WARNING);
            return;
        }

        // Direct update for teachers
        if (userType == 1) {
            isEditing = true; // Treat as edit/update operation
            handleSave(); // Redirect to save logic immediately
            return;
        }

        // Admin flow - keep existing behavior
        try {
            JTable table = view.getTable();
            DiemModel diem = new DiemModel();

            if (userType == 1) { // Teacher
                diem.setMasv(getSafeString(table, selectedRow, 0));
                diem.setDiemcc(getSafeDouble(table, selectedRow, 2));
                diem.setDiemgk(getSafeDouble(table, selectedRow, 3));
                diem.setDiemck(getSafeDouble(table, selectedRow, 4));
                diem.setDiemtongket(getSafeDouble(table, selectedRow, 5));

                diem.setMamon(selectedSubject);
                diem.setNamhoc(globalNamHoc);
                diem.setHocky(globalHocKy);
            } else {
                // Admin or others (Old logic fallback or broken)
                diem.setMasv(getSafeString(table, selectedRow, 0));
                diem.setDiemcc(getSafeDouble(table, selectedRow, 2));
                diem.setDiemgk(getSafeDouble(table, selectedRow, 3));
                diem.setDiemck(getSafeDouble(table, selectedRow, 4));
                diem.setDiemtongket(getSafeDouble(table, selectedRow, 5));
            }

            currentDiem = diem;
            view.fillForm(currentDiem);
            view.setEditingMode(true);
            isEditing = true;

            showStatusMessage("Đang chỉnh sửa điểm của sinh viên " + diem.getMasv(), MessageType.INFO);

        } catch (Exception e) {
            showStatusMessage("Lỗi khi lấy dữ liệu: " + e.getMessage(), MessageType.ERROR);
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            showStatusMessage("Vui lòng chọn một dòng để xóa", MessageType.WARNING);
            return;
        }

        if (userType == 2) {
            showStatusMessage("Sinh viên không có quyền xóa điểm", MessageType.WARNING);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn xóa điểm này?\n(Nếu xóa, sinh viên sẽ trở về trạng thái chưa có điểm)",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String masv;
                String mamon;
                int hocky;

                if (userType == 2) {
                    masv = model.getMasvByUsername(username);
                    mamon = getSafeString(view.getTable(), selectedRow, 0);
                    hocky = globalHocKy;
                } else {
                    masv = getSafeString(view.getTable(), selectedRow, 0);
                    mamon = selectedSubject;
                    hocky = globalHocKy;
                }

                if (model.deleteDiem(masv, mamon, hocky)) {
                    if (userType == 1 && selectedLop != null) {
                        loadDiemForTeacher();
                    } else if (userType == 1) {
                        loadDiemForTeacher();
                    } else if (userType == 2) {
                        ArrayList<DiemModel> data = model.getDiemByUsername(username);
                        view.loadTableData(data);
                    } else {
                        ArrayList<DiemModel> data = model.getAllDiem();
                        view.loadTableData(data);
                    }
                    view.clearForm();
                    showStatusMessage("Đã xóa điểm thành công", MessageType.SUCCESS);
                } else {
                    loadDiemForTeacher();
                    showStatusMessage("Đã đặt lại điểm về 0", MessageType.SUCCESS);
                }
            } catch (Exception e) {
                showStatusMessage("Lỗi khi xóa: " + e.getMessage(), MessageType.ERROR);
            }
        }
    }

    private void handleSave() {
        try {
            DiemModel diem = view.getFormData();

            if (userType == 1) {
                if (selectedSubject == null) {
                    showStatusMessage("Giáo viên chưa được phân công môn học", MessageType.WARNING);
                    return;
                }
                if (!diem.getMamon().equals(selectedSubject)) {
                    showStatusMessage("Giáo viên chỉ có thể nhập điểm cho môn " + selectedSubject, MessageType.WARNING);
                    return;
                }
            }

            if (userType == 1 && selectedLop != null) {
                if (!model.checkSinhVienTrongLop(diem.getMasv(), selectedLop)) {
                    showStatusMessage("Lỗi: Sinh viên " + diem.getMasv() + " không thuộc lớp đã chọn!",
                            MessageType.ERROR);
                    return;
                }
            }

            if (!isEditing && checkDiemExists(diem.getMasv(), diem.getMamon(), diem.getHocky())) {
                showStatusMessage("Sinh viên đã có điểm môn này ở học kỳ " + diem.getHocky() + "!",
                        MessageType.WARNING);
                return;
            }

            boolean success;
            String message;

            if (isEditing) {
                boolean exists = checkDiemExists(diem.getMasv(), diem.getMamon(), diem.getHocky());
                if (exists) {
                    success = model.updateDiem(diem);
                    message = success ? "Cập nhật điểm thành công" : "Cập nhật điểm thất bại";
                } else {
                    success = model.insertDiem(diem);
                    message = success ? "Nhập điểm thành công" : "Nhập điểm thất bại";
                }
            } else {
                success = model.insertDiem(diem);
                message = success ? "Thêm điểm thành công" : "Thêm điểm thất bại";
            }

            if (success) {
                if (userType == 1 && selectedLop != null) {
                    loadDiemForTeacher();
                } else if (userType == 1) {
                    loadDiemForTeacher();
                } else if (userType == 2) {
                    ArrayList<DiemModel> data = model.getDiemByUsername(username);
                    view.loadTableData(data);
                } else {
                    ArrayList<DiemModel> data = model.getAllDiem();
                    view.loadTableData(data);
                }

                view.setEditingMode(false);
                view.clearForm();
                isEditing = false;
                currentDiem = null;
                showStatusMessage(message, MessageType.SUCCESS);
                view.showSuccessMessage(message);
            } else {
                showStatusMessage(message, MessageType.ERROR);
            }

        } catch (IllegalArgumentException e) {
            view.showValidationError(e.getMessage());
            showStatusMessage(e.getMessage(), MessageType.WARNING);
        } catch (Exception e) {
            showStatusMessage("Lỗi khi lưu: " + e.getMessage(), MessageType.ERROR);
        }
    }

    private void handleCancel() {
        view.setEditingMode(false);
        view.clearForm();
        isEditing = false;
        currentDiem = null;
        showStatusMessage("Đã hủy thao tác", MessageType.INFO);
    }

    private void performSearch() {
        String keyword = view.getSearchKeyword();
        if (keyword.isEmpty()) {
            if (userType == 1) {
                loadDiemForTeacher();
                showStatusMessage("Đã làm mới dữ liệu", MessageType.INFO);
            } else if (userType == 2) {
                ArrayList<DiemModel> data = model.getDiemByUsername(username);
                view.loadTableData(data);
                showStatusMessage("Đã làm mới dữ liệu", MessageType.INFO);
            } else {
                ArrayList<DiemModel> data = model.getAllDiem();
                view.loadTableData(data);
                showStatusMessage("Đã làm mới dữ liệu", MessageType.INFO);
            }
        } else {
            ArrayList<DiemModel> kq = searchWithPermission(keyword);
            view.loadTableData(kq);
            showStatusMessage("Tìm thấy " + kq.size() + " kết quả cho '" + keyword + "'", MessageType.INFO);
        }
    }

    private ArrayList<DiemModel> searchWithPermission(String keyword) {
        ArrayList<DiemModel> allResults = model.search(keyword);
        ArrayList<DiemModel> filteredResults = new ArrayList<>();

        if (userType == 0) {
            return allResults;
        } else if (userType == 1) {
            for (DiemModel diem : allResults) {
                if (selectedSubject != null && selectedSubject.equals(diem.getMamon())) {
                    if (selectedLop != null) {
                        if (model.checkSinhVienTrongLop(diem.getMasv(), selectedLop)) {
                            filteredResults.add(diem);
                        }
                    } else {
                        filteredResults.add(diem);
                    }
                }
            }
        } else if (userType == 2) {
            for (DiemModel diem : allResults) {
                if (isStudentGrade(diem.getMasv())) {
                    filteredResults.add(diem);
                }
            }
        }
        return filteredResults;
    }

    private boolean isStudentGrade(String masv) {
        String currentMasv = model.getMasvByUsername(username);
        return masv.equals(currentMasv);
    }

    private void handleExportExcel() {
        String className = view.getSelectedLop();
        String subject = view.getSelectedMonHoc();
        String fileName = "BangDiem";
        if (className != null && !className.isEmpty())
            fileName += "_" + className;
        if (subject != null && !subject.isEmpty())
            fileName += "_" + subject;
        javax.swing.table.DefaultTableModel tableModel = (javax.swing.table.DefaultTableModel) view.getTable()
                .getModel();
        ExcelExporter.exportToExcel(null, tableModel, "Bảng điểm", fileName);
    }

    private void handleImportExcel() {
        if (userType == 2) {
            showStatusMessage("Sinh viên không có quyền nhập dữ liệu", MessageType.WARNING);
            return;
        }
        showStatusMessage("Chức năng nhập Excel đang được phát triển", MessageType.INFO);
    }

    private void handleRefresh() {
        view.clearForm();
        view.getTable().clearSelection();
        if (userType == 1)
            view.setScoreFieldsEditable(false);
        view.setEditingMode(false);
        isEditing = false;
        currentDiem = null;
        if (userType == 1) {
            loadDiemForTeacher();
        } else {
            loadInitialData();
        }
        showStatusMessage("Đã làm mới dữ liệu và đặt lại form", MessageType.INFO);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    JTable table = view.getTable();
                    DiemModel diem = new DiemModel();
                    if (userType == 2) {
                        diem.setMamon(getSafeString(table, selectedRow, 0));
                        diem.setMasv(model.getMasvByUsername(username));
                        diem.setDiemcc(getSafeDouble(table, selectedRow, 1));
                        diem.setDiemgk(getSafeDouble(table, selectedRow, 2));
                        diem.setDiemck(getSafeDouble(table, selectedRow, 3));
                        diem.setDiemtongket(getSafeDouble(table, selectedRow, 4));
                    } else {
                        diem.setMasv(getSafeString(table, selectedRow, 0));
                        diem.setDiemcc(getSafeDouble(table, selectedRow, 2));
                        diem.setDiemgk(getSafeDouble(table, selectedRow, 3));
                        diem.setDiemck(getSafeDouble(table, selectedRow, 4));
                        diem.setDiemtongket(getSafeDouble(table, selectedRow, 5));
                        if (userType == 1)
                            diem.setMamon(selectedSubject);
                        else
                            diem.setMamon(selectedSubject != null ? selectedSubject : "");
                        diem.setNamhoc(globalNamHoc);
                        diem.setHocky(globalHocKy);
                    }
                    view.fillForm(diem);
                    currentDiem = diem;
                    if (userType == 1)
                        view.setScoreFieldsEditable(true);
                    else
                        showStatusMessage("Đã chọn điểm của sinh viên " + diem.getMasv(), MessageType.INFO);
                } catch (Exception ex) {
                    showStatusMessage("Lỗi khi hiển thị thông tin: " + ex.getMessage(), MessageType.ERROR);
                }
            }
        } else if (e.getClickCount() == 2 && userType != 2 && userType != 1) {
            handleEdit();
        }
    }

    private String getSafeString(JTable table, int row, int col) {
        Object val = table.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    private double getSafeDouble(JTable table, int row, int col) {
        Object val = table.getValueAt(row, col);
        try {
            return val != null ? Double.parseDouble(val.toString().replace(",", ".")) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private String validateDiem(DiemModel diem) {
        if (diem.getMasv().isEmpty())
            return "Mã sinh viên không được để trống";
        if (diem.getMamon().isEmpty())
            return "Mã môn học không được để trống";
        if (diem.getNamhoc().isEmpty())
            return "Năm học không được để trống";
        if (diem.getDiemcc() < 0 || diem.getDiemcc() > 10)
            return "Điểm CC phải từ 0-10";
        if (diem.getDiemgk() < 0 || diem.getDiemgk() > 10)
            return "Điểm GK phải từ 0-10";
        if (diem.getDiemck() < 0 || diem.getDiemck() > 10)
            return "Điểm CK phải từ 0-10";
        return null;
    }

    enum MessageType {
        SUCCESS, ERROR, WARNING, INFO
    }

    private void showStatusMessage(String message, MessageType type) {
        SwingUtilities.invokeLater(() -> {
            String prefix = switch (type) {
                case SUCCESS -> "[OK] ";
                case ERROR -> "[LỖI] ";
                case WARNING -> "[CẢNH BÁO] ";
                case INFO -> "[INFO] ";
            };
            System.out.println(prefix + message);
            if (type == MessageType.ERROR) {
                JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private boolean checkDiemExists(String masv, String mamon, int hocky) {
        try {
            ArrayList<DiemModel> allDiem = (userType == 1) ? model.getDiemByMon(selectedSubject) : model.getAllDiem();
            for (DiemModel diem : allDiem) {
                if (diem.getMasv().equals(masv) && diem.getMamon().equals(mamon) && diem.getHocky() == hocky)
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}