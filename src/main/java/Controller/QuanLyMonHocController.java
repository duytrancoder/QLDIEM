package Controller;

import Model.MonHocModel;
import View.QuanLyMonHocPanel;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class QuanLyMonHocController {

    private QuanLyMonHocPanel view;
    private MonHocModel model;
    private ModernMainController mainController;

    public QuanLyMonHocController(QuanLyMonHocPanel view, ModernMainController mainController) {
        this.view = view;
        this.mainController = mainController;
        this.model = new MonHocModel();

        initController();
    }

    private void initController() {
        loadData();

        // Button Actions
        view.getBtnThem().addActionListener(e -> addMonHoc());
        view.getBtnSua().addActionListener(e -> updateMonHoc());
        view.getBtnXoa().addActionListener(e -> deleteMonHoc());
        view.getBtnLamMoi().addActionListener(e -> refresh());

        // Table Click
        view.getTblMonHoc().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblMonHoc().getSelectedRow();
                if (row >= 0) {
                    fillForm(row);
                }
            }
        });
    }

    public void loadData() {
        ArrayList<MonHocModel> list = model.getAllMonHoc();
        view.getTableModel().setRowCount(0);
        for (MonHocModel mh : list) {
            // Remove sotinchi from table row
            view.getTableModel().addRow(new Object[] { mh.getMamon(), mh.getTenmon() });
        }
    }

    private void addMonHoc() {
        MonHocModel mh = view.getFormData();
        if (mh.getMamon().isEmpty() || mh.getTenmon().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Removed sotinchi validation as it's defaulted in View/Model

        if (model.addMonHoc(mh)) {
            JOptionPane.showMessageDialog(view, "Thêm môn học thành công!");
            loadData();
            refresh();
            if (mainController != null) {
                mainController.refreshSubjectRelatedData();
            }
        } else {
            // Check if exist
            boolean exists = false;
            for (int i = 0; i < view.getTableModel().getRowCount(); i++) {
                if (view.getTableModel().getValueAt(i, 0).equals(mh.getMamon())) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!\nMã môn '" + mh.getMamon() + "' đã tồn tại.");
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại! Vui lòng kiểm tra lại thông tin.");
            }
        }
    }

    private void updateMonHoc() {
        MonHocModel mh = view.getFormData();
        if (mh.getMamon().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa chọn môn học để sửa!");
            return;
        }

        if (model.updateMonHoc(mh)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            loadData();
            refresh();
            if (mainController != null) {
                mainController.refreshSubjectRelatedData();
            }
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
        }
    }

    private void deleteMonHoc() {
        String mamon = view.getFormData().getMamon();
        if (mamon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa chọn môn học để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa môn " + mamon + "?\nCảnh báo: Việc này có thể ảnh hưởng đến phân công giảng dạy!",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (model.deleteMonHoc(mamon)) {
                JOptionPane.showMessageDialog(view, "Xóa thành công!");
                loadData();
                refresh();
                if (mainController != null) {
                    mainController.refreshSubjectRelatedData();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại! Môn học có thể đang được sử dụng.");
            }
        }
    }

    public void refresh() {
        view.clearForm();
        view.getBtnThem().setEnabled(true);
        view.getBtnSua().setEnabled(true);
        view.getBtnXoa().setEnabled(true);
    }

    private void fillForm(int row) {
        Object valMa = view.getTableModel().getValueAt(row, 0);
        Object valTen = view.getTableModel().getValueAt(row, 1);

        String mamon = (valMa != null) ? valMa.toString() : "";
        String tenmon = (valTen != null) ? valTen.toString() : "";

        view.setFormData(new MonHocModel(mamon, tenmon, 3)); // 3 is default
    }
}
