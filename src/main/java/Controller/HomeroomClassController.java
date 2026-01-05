package Controller;

import Model.DiemModel;
import Model.LopModel;
import Model.SinhVienModel;
import View.HomeroomClassPanel;
import javax.swing.JOptionPane;
import java.util.ArrayList;

/**
 * Controller for Homeroom Class Panel
 */
public class HomeroomClassController {

    private HomeroomClassPanel view;
    private LopModel lopModel;
    private DiemModel diemModel;
    private SinhVienModel sinhVienModel;
    private String username; // Teacher's username (magv)
    private String homeroomClass; // Homeroom class code
    private String selectedSubject;

    public HomeroomClassController(HomeroomClassPanel view, String username) {
        this.view = view;
        this.username = username;
        this.lopModel = new LopModel();
        this.diemModel = new DiemModel();
        this.sinhVienModel = new SinhVienModel();

        init();
        setupListeners();
    }

    private void init() {
        // Check if teacher is homeroom for a class
        homeroomClass = lopModel.getHomeroomClassByTeacher(username);

        if (homeroomClass == null || homeroomClass.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Bạn không phải là giáo viên chủ nhiệm của lớp nào!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Load class info
        LopModel classInfo = lopModel.getLopByMalop(homeroomClass);
        if (classInfo != null) {
            ArrayList<SinhVienModel> students = sinhVienModel.getSinhVienByLop(homeroomClass);
            int studentCount = students.size();
            view.setClassInfo(classInfo.getTenlop(), studentCount);
        }

        // Load all subjects for the homeroom class
        ArrayList<String> subjects = lopModel.getAllSubjectsForClass(homeroomClass);
        view.loadSubjects(subjects);

        System.out.println("DEBUG: Loaded homeroom class " + homeroomClass + " with " + subjects.size() + " subjects");
    }

    private void setupListeners() {
        // Subject selection listener
        view.addSubjectListener(e -> {
            selectedSubject = view.getSelectedSubject();
            if (selectedSubject != null && homeroomClass != null) {
                loadGrades();
            }
        });

        // Refresh button
        view.getBtnRefresh().addActionListener(e -> {
            if (selectedSubject != null) {
                loadGrades();
                JOptionPane.showMessageDialog(view, "Đã làm mới dữ liệu!");
            }
        });

        // Export button
        view.getBtnExport().addActionListener(e -> {
            JOptionPane.showMessageDialog(view, "Chức năng xuất Excel đang phát triển!");
        });
    }

    private void loadGrades() {
        if (homeroomClass == null || selectedSubject == null) {
            return;
        }

        // Load grades for all students in class for selected subject
        ArrayList<DiemModel> grades = diemModel.getDiemByLopAndMon(homeroomClass, selectedSubject);
        view.loadGradeData(grades);

        // Load student names - get all students in class and match by masv
        ArrayList<SinhVienModel> allStudents = sinhVienModel.getSinhVienByLop(homeroomClass);
        ArrayList<String> studentNames = new ArrayList<>();
        for (DiemModel diem : grades) {
            String name = "";
            for (SinhVienModel sv : allStudents) {
                if (sv.getMasv().equals(diem.getMasv())) {
                    name = sv.getHoten();
                    break;
                }
            }
            studentNames.add(name);
        }
        view.setStudentNames(studentNames);

        System.out.println("DEBUG: Loaded " + grades.size() + " grade records for subject " + selectedSubject);
    }
}
