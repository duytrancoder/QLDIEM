package Controller;

import Model.BoMonModel;
import Model.MonHocModel;
import View.QuanLyBoMonPanel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class QuanLyBoMonController {

    private QuanLyBoMonPanel view;
    private BoMonModel boMonModel;
    private MonHocModel monHocModel;

    public QuanLyBoMonController(QuanLyBoMonPanel view) {
        this.view = view;
        this.boMonModel = new BoMonModel();
        this.monHocModel = new MonHocModel();

        initController();
    }

    private void initController() {
        loadDataBoMon();
        loadAvailableSubjects();

        // --- Department Actions ---
        view.getBtnThemBM().addActionListener(e -> addBoMon());
        view.getBtnSuaBM().addActionListener(e -> updateBoMon());
        view.getBtnXoaBM().addActionListener(e -> deleteBoMon());
        view.getBtnLamMoiBM().addActionListener(e -> refreshBoMon());

        view.getTblBoMon().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBoMonSelection();
            }
        });
    }

    private void loadDataBoMon() {
        ArrayList<BoMonModel> list = boMonModel.getAllBoMon();
        view.loadTableBoMon(list);
    }

    private void loadAvailableSubjects() {
        // Load ALL subjects for the selection list
        // Ideally, format: "MAMON - Ten Mon"
        ArrayList<MonHocModel> list = monHocModel.getAllMonHoc(); // We need a getAll method in MonHocModel
        ArrayList<String> subjectStrings = new ArrayList<>();
        for (MonHocModel mh : list) {
            subjectStrings.add(mh.getMamon() + " - " + mh.getTenmon());
        }
        view.loadSubjects(subjectStrings);
    }

    private void handleBoMonSelection() {
        JTable tbl = view.getTblBoMon();
        int row = tbl.getSelectedRow();
        if (row >= 0) {
            String mabomon = tbl.getValueAt(row, 0).toString();
            String tenbomon = tbl.getValueAt(row, 1).toString();
            // Col 2 is subjects csv
            Object subObj = tbl.getValueAt(row, 2);
            String cacMon = subObj != null ? subObj.toString() : "";

            BoMonModel bm = new BoMonModel(mabomon, tenbomon, cacMon);
            view.setFormData(bm);
        }
    }

    // --- CRUD Bo Mon ---
    private void addBoMon() {
        BoMonModel bm = view.getFormData();
        if (bm.getMabomon().isEmpty() || bm.getTenbomon().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin bộ môn!");
            return;
        }
        if (boMonModel.addBoMon(bm)) {
            JOptionPane.showMessageDialog(view, "Thêm bộ môn thành công!");
            loadDataBoMon();
            refreshBoMon();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thất bại (Mã có thể đã tồn tại)!");
        }
    }

    private void updateBoMon() {
        BoMonModel bm = view.getFormData();
        if (boMonModel.updateBoMon(bm)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            loadDataBoMon();
            refreshBoMon();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
        }
    }

    private void deleteBoMon() {
        BoMonModel bm = view.getFormData();
        if (bm.getMabomon().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa chọn bộ môn để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa bộ môn " + bm.getTenbomon()
                        + "?\nTất cả môn học sẽ bị hủy phân công khỏi bộ môn này!",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (boMonModel.deleteBoMon(bm.getMabomon())) {
                JOptionPane.showMessageDialog(view, "Xóa thành công!");
                loadDataBoMon();
                refreshBoMon();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại!");
            }
        }
    }

    public void refreshBoMon() {
        view.clearForm();
        loadDataBoMon();
        loadAvailableSubjects(); // Refresh subject list too
        view.getTblBoMon().clearSelection();
    }
}
