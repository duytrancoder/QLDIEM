package Controller;

import Model.LopModel;

import Model.GiaoVienModel;
import View.QuanLyLopPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Controller cho quản lý lớp học
 */
public class QuanLyLopController implements ActionListener, MouseListener {

    private QuanLyLopPanel view;
    private LopModel model;

    private GiaoVienModel gvModel;
    private ModernMainController mainController; // Add main controller reference

    public QuanLyLopController(QuanLyLopPanel view) {
        this(view, null);
    }

    public QuanLyLopController(QuanLyLopPanel view, ModernMainController mainController) {
        this.view = view;
        this.model = new LopModel();

        this.gvModel = new GiaoVienModel();
        this.mainController = mainController;

        view.addActionListener(this);
        view.addTableMouseListener(this);

        loadData();

        loadGiaoVien();
    }

    private void loadData() {
        ArrayList<LopModel> list = model.getAllLop();
        view.loadTableData(list);
    }

    private void loadGiaoVien() {
        ArrayList<GiaoVienModel> listGV = gvModel.getAllGiaoVien();
        ArrayList<String> gvNames = new ArrayList<>();
        for (GiaoVienModel gv : listGV) {
            gvNames.add(gv.getMagv() + " - " + gv.getHoten());
        }
        view.loadGiaoVien(gvNames);
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
        }
    }

    private void handleAdd() {
        LopModel lopData = view.getFormData();

        if (lopData.getMalop().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập mã lớp!");
            return;
        }

        if (lopData.getTenlop().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập tên lớp!");
            return;
        }

        if (model.isExistMalop(lopData.getMalop())) {
            JOptionPane.showMessageDialog(view, "Mã lớp đã tồn tại! Vui lòng chọn mã khác.");
            return;
        }

        if (lopData.addLop()) {
            JOptionPane.showMessageDialog(view, "Thêm lớp thành công!");
            view.clearForm();
            loadData();
            if (mainController != null) {
                mainController.refreshClassData();
            }
        } else {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm lớp!");
        }
    }

    private void handleEdit() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng để sửa!");
            return;
        }

        String selectedMalop = view.getTable().getValueAt(row, 0).toString();
        LopModel lopData = view.getFormData();

        if (lopData.getMalop().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập mã lớp!");
            return;
        }

        if (lopData.getTenlop().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập tên lớp!");
            return;
        }

        // Prevent changing Malop (Primary Key)
        if (!selectedMalop.equals(lopData.getMalop())) {
            JOptionPane.showMessageDialog(view, "Không được thay đổi Mã Lớp!");
            view.fillForm(model.getLopByMalop(selectedMalop)); // Reset form to selected data
            return;
        }

        if (lopData.updateLop()) {
            JOptionPane.showMessageDialog(view, "Cập nhật lớp thành công!");
            view.clearForm();
            loadData();
            if (mainController != null) {
                mainController.refreshClassData();
            }
        } else {
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật lớp!");
        }
    }

    private void handleDelete() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng để xóa!");
            return;
        }

        String malop = view.getTable().getValueAt(row, 0).toString();
        String tenlop = view.getTable().getValueAt(row, 1).toString();

        // Kiểm tra lớp có sinh viên không
        if (model.hasStudents(malop)) {
            JOptionPane.showMessageDialog(view,
                    "Không thể xóa lớp \"" + tenlop + "\" vì lớp này đang có sinh viên!\n" +
                            "Vui lòng chuyển sinh viên sang lớp khác trước khi xóa.",
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa lớp \"" + tenlop + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (model.deleteLop(malop)) {
                JOptionPane.showMessageDialog(view, "Xóa lớp thành công!");
                loadData();

                // Refresh class data in other panels
                if (mainController != null) {
                    mainController.refreshClassData();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa lớp. Vui lòng thử lại!");
            }
        }
    }

    private void handleRefresh() {
        view.clearForm();
        view.getTable().clearSelection();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getTable().getSelectedRow();
        if (row >= 0) {
            // Lấy dữ liệu từ bảng và fill vào form (Optimized: Scrape directly from table)
            JTable table = view.getTable();
            LopModel lop = new LopModel();

            lop.setMalop(table.getValueAt(row, 0).toString());
            lop.setTenlop(table.getValueAt(row, 1).toString());
            // table.getValueAt(row, 2) is "Magvcn", handled by fillForm which expects ID
            Object gvValue = table.getValueAt(row, 2);
            lop.setMagvcn(gvValue != null ? gvValue.toString() : "");

            view.fillForm(lop);
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
