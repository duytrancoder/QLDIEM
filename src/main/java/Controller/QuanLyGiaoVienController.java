package Controller;

import Model.GiaoVienModel;
import Model.BoMonModel;
import Model.MonHocModel; // Ensure this exists and has getMonHocByBoMon
import View.QuanLyGiaoVienPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class QuanLyGiaoVienController implements ActionListener, MouseListener {

    private QuanLyGiaoVienPanel view;
    private GiaoVienModel model;
    private BoMonModel boMonModel;
    private MonHocModel monHocModel;
    private GiaoVienModel currentGV = null;

    public QuanLyGiaoVienController(QuanLyGiaoVienPanel view) {
        this.view = view;
        this.model = new GiaoVienModel();
        this.boMonModel = new BoMonModel();
        this.monHocModel = new MonHocModel();

        view.addActionListener(this);
        view.addTableMouseListener(this);

        setupComboBoxListener();

        loadData();
        loadBoMonList();
    }

    private void setupComboBoxListener() {
        view.getCbBoMon().addActionListener(e -> {
            String selection = (String) view.getCbBoMon().getSelectedItem();
            if (selection != null && selection.contains(" - ")) {
                String mabomon = selection.split(" - ")[0];
                loadMonHocForBoMon(mabomon);
            } else {
                view.loadMonHoc(new ArrayList<>()); // Clear if no valid selection
            }
        });
    }

    private void loadData() {
        ArrayList<GiaoVienModel> list = model.fetchAllTeachers();
        view.loadTableData(list);
    }

    private void loadBoMonList() {
        ArrayList<BoMonModel> list = boMonModel.getAllBoMon();
        ArrayList<String> strings = new ArrayList<>();
        for (BoMonModel bm : list) {
            strings.add(bm.getMabomon() + " - " + bm.getTenbomon());
        }
        view.loadBoMon(strings);
    }

    // Updated to load subjects based on Dept
    private void loadMonHocForBoMon(String mabomon) {
        ArrayList<MonHocModel> list = monHocModel.getMonHocByBoMon(mabomon);
        ArrayList<String> strings = new ArrayList<>();
        for (MonHocModel mh : list) {
            strings.add(mh.getMamon() + " - " + mh.getTenmon());
        }
        view.loadMonHoc(strings);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
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
            case "Làm mới":
                handleRefresh();
                break;
            case "Tìm":
                handleTimKiem();
                break;
        }
    }

    private void handleTimKiem() {
        String keyword = view.getSearchKeyword();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            ArrayList<GiaoVienModel> list = model.searchGiaoVien(keyword);
            view.loadTableData(list);
        }
    }

    private void handleAdd() {
        try {
            GiaoVienModel gv = view.getFormData();

            if (gv.getMagv() == null || gv.getMagv().trim().isEmpty() ||
                    gv.getHoten() == null || gv.getHoten().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã GV và Họ tên không được để trống!");
                return;
            }

            // Check if Dept is selected
            if (gv.getMabomon() == null || gv.getMabomon().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn Bộ môn!");
                return;
            }

            gv.setUsername(gv.getMagv());

            if (model.isExistMagv(gv.getMagv())) {
                JOptionPane.showMessageDialog(view, "Mã giáo viên đã tồn tại!");
                return;
            }

            boolean success = model.themGiaoVien(gv);
            if (success) {
                JOptionPane.showMessageDialog(view, "Thêm thành công!");
                loadData();
                view.clearForm();
                currentGV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleEdit() {
        if (currentGV == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên để sửa!");
            return;
        }

        try {
            GiaoVienModel gv = view.getFormData();

            if (!currentGV.getMagv().equals(gv.getMagv())) {
                JOptionPane.showMessageDialog(view, "Không thể thay đổi Mã Giáo Viên!");
                return;
            }

            boolean success = model.capNhatGiaoVien(gv);

            if (success) {
                JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
                loadData();
                view.clearForm();
                currentGV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleDelete() {
        if (currentGV == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa giáo viên " + currentGV.getHoten() + "?\nToàn bộ phân công sẽ bị xóa.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = model.xoaGiaoVien(currentGV.getMagv());
            if (success) {
                JOptionPane.showMessageDialog(view, "Xóa thành công!");
                loadData();
                view.clearForm();
                currentGV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại!");
            }
        }
    }

    private void handleRefresh() {
        view.clearForm();
        currentGV = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getTable().getSelectedRow();
        if (row >= 0) {
            try {
                JTable table = view.getTable();
                // We fetch fresh from DB because table might have truncated CSVs
                String magv = table.getValueAt(row, 0).toString();
                GiaoVienModel gv = model.getGiaoVienByMagv(magv); // Fetch full data including CSVs

                if (gv != null) {
                    view.fillForm(gv);
                    // Force selection of subjects after list reload
                    // fillForm trigger cbBoMon action -> loads list -> wait -> select
                    // Since Swing is single threaded, action should fire immediately?
                    // We might need to manually set selection after fillForm
                    view.setSelectedSubjects(gv.getCacMon());
                    currentGV = gv;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
}
