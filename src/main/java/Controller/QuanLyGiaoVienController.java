package Controller;

import Model.GiaoVienModel;
import Model.BoMonModel;
import Model.MonHocModel;
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
    private ArrayList<BoMonModel> cachedBoMons = new ArrayList<>();

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
            // Robust check: ensure it's not the default prompt and has the separator
            if (selection != null && selection.contains(" - ")
                    && !selection.startsWith("--")) {
                String mabomon = selection.split(" - ")[0].trim();

                // Find the cacmon list for this mabomon from cache
                String csvMamon = "";
                for (BoMonModel bm : cachedBoMons) {
                    if (bm.getMabomon().equalsIgnoreCase(mabomon)) {
                        csvMamon = bm.getCacMon();
                        break;
                    }
                }
                loadMonHocForBoMon(csvMamon);
            } else {
                view.loadMonHoc(new ArrayList<>()); // Clear if no valid selection
            }
        });
    }

    public void refreshAll() {
        loadBoMonList(); // Reload depts
        loadData(); // Reload teachers
        view.loadMonHoc(new ArrayList<>()); // Clear subject list
    }

    private void loadData() {
        ArrayList<GiaoVienModel> list = model.fetchAllTeachers();
        view.loadTableData(list);
    }

    private void loadBoMonList() {
        cachedBoMons = boMonModel.getAllBoMon();
        ArrayList<String> strings = new ArrayList<>();
        for (BoMonModel bm : cachedBoMons) {
            if (bm.getMabomon() != null) {
                strings.add(bm.getMabomon().trim() + " - " + bm.getTenbomon());
            }
        }
        view.loadBoMon(strings);
    }

    private void loadMonHocForBoMon(String csvMamon) {
        ArrayList<MonHocModel> list = monHocModel.getMonHocByCodes(csvMamon);
        ArrayList<String> strings = new ArrayList<>();
        for (MonHocModel mh : list) {
            strings.add(mh.getMamon().trim() + " - " + mh.getTenmon());
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
        refreshAll();
        view.clearForm();
        currentGV = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getTable().getSelectedRow();
        if (row >= 0) {
            try {
                JTable table = view.getTable();
                String magv = table.getValueAt(row, 0).toString();
                GiaoVienModel gv = model.getGiaoVienByMagv(magv);

                if (gv != null) {
                    view.fillForm(gv);
                    // fillForm might trigger combo box action (async or immediate)
                    // We wait a tiny bit or just call it after ensuring form is filled
                    SwingUtilities.invokeLater(() -> {
                        view.setSelectedSubjects(gv.getCacMon());
                    });
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
