package Controller;

import Model.SinhVienModel;
import Model.LopModel;
import View.QuanLySinhVienPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Controller cho quản lý sinh viên (Admin)
 */
public class QuanLySinhVienController implements ActionListener, MouseListener {

    private QuanLySinhVienPanel view;
    private SinhVienModel model;
    private LopModel lopModel;
    private SinhVienModel currentSV = null;

    public QuanLySinhVienController(QuanLySinhVienPanel view) {
        this.view = view;
        this.model = new SinhVienModel();
        this.lopModel = new LopModel();

        view.addActionListener(this);
        view.addTableMouseListener(this);

        loadData();
        loadLop();
    }

    private void loadData() {
        ArrayList<SinhVienModel> list = model.getAllSinhVien();
        view.loadTableData(list);
    }

    private void loadLop() {
        ArrayList<LopModel> listLop = lopModel.getAllLop();
        ArrayList<String> listLopString = new ArrayList<>();
        for (LopModel lop : listLop) {
            listLopString.add(lop.getMalop() + " - " + lop.getTenlop());
        }
        view.loadLop(listLopString);
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
            // Nếu từ khóa rỗng, load lại toàn bộ dữ liệu
            loadData();
        } else {
            // Tìm kiếm theo từ khóa
            ArrayList<SinhVienModel> list = model.searchSinhVien(keyword);
            view.loadTableData(list);
        }
    }

    private void handleAdd() {
        try {
            SinhVienModel sv = view.getFormData();

            // Validate
            if (sv.getMasv().isEmpty() || sv.getHoten().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã SV và Họ tên không được để trống!");
                return;
            }

            if (sv.getMalop() == null || sv.getMalop().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn lớp cho sinh viên!");
                return;
            }

            // Auto-generate username from Masv
            sv.setUsername(sv.getMasv());

            // Check if student ID already exists
            if (model.isExistMasv(sv.getMasv())) {
                JOptionPane.showMessageDialog(view, "Mã sinh viên đã tồn tại! Vui lòng chọn mã khác.");
                return;
            }

            boolean success = model.themSinhVien(sv);

            if (success) {
                JOptionPane.showMessageDialog(view, "Thêm thành công!");
                loadData();
                view.clearForm();
                currentSV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại! Vui lòng kiểm tra lại dữ liệu.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleEdit() {
        if (currentSV == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sinh viên từ bảng để sửa!");
            return;
        }

        try {
            SinhVienModel sv = view.getFormData();

            // Validate
            if (sv.getMasv().isEmpty() || sv.getHoten().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã SV và Họ tên không được để trống!");
                return;
            }

            // Check if student ID changed and already exists
            if (!currentSV.getMasv().equals(sv.getMasv())) {
                // Changing ID is debatable but let's assume we allow it if we update the key,
                // but usually PK updates are tricky.
                // Assuming we use PK in WHERE clause, we must use OLD ID.
                // But capNhatSinhVien uses sv.getMasv() in WHERE clause ?
                // Let's check GiaoVienModel: UPDATE ... WHERE masv=?
                // So if we change the ID in the form, the WHERE clause won't match if we pass
                // new ID.
                // Unless we pass old ID separately.
                // For now, let's forbid changing ID or assume ID field is disabled/readonly?
                // If ID field is editable, users expect to change it.
                // However, standard SQL update usually updates other fields based on ID.
                // If user changes ID in text field, we need to know which record to update.
                // currentSV has the OLD ID.

                // If user changed ID, we technically are creating meaning "changing ID".
                // But our model usually updates based on the ID passed in the object.
                // So if we pass new ID, it tries update ... where masv = newID. Failure.

                // Let's forbid changing ID for simplicity or check if model supports it.
                // Model: UPDATE tblsinhvien SET ... WHERE masv=?
                // So we can't change ID.

                JOptionPane.showMessageDialog(view, "Không thể thay đổi Mã Sinh Viên!");
                return;
            }

            if (sv.getMalop() == null || sv.getMalop().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn lớp cho sinh viên!");
                return;
            }

            boolean success = model.capNhatSinhVien(sv);

            if (success) {
                JOptionPane.showMessageDialog(view, "Sửa thành công!");
                loadData();
                view.clearForm();
                currentSV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Sửa thất bại! Vui lòng kiểm tra lại dữ liệu.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleDelete() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sinh viên cần xóa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentSV == null) {
            // Nếu chưa click, lấy thông tin từ bảng
            JTable table = view.getTable();
            SinhVienModel sv = new SinhVienModel();
            sv.setMasv(table.getValueAt(row, 0).toString());
            sv.setHoten(table.getValueAt(row, 1).toString());
            currentSV = sv;
        }

        String message = String.format("Bạn có chắc muốn xóa sinh viên:\n\n" +
                "Mã SV: %s\nHọ tên: %s\n\n" +
                "⚠️ Lưu ý: Sẽ xóa tất cả điểm số và tài khoản của sinh viên này!",
                currentSV.getMasv(), currentSV.getHoten());

        int confirm = JOptionPane.showConfirmDialog(view, message,
                "Xác nhận xóa sinh viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = model.xoaSinhVien(currentSV.getMasv());
                if (success) {
                    JOptionPane.showMessageDialog(view,
                            "Đã xóa thành công sinh viên " + currentSV.getHoten() + "!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    view.clearForm();
                    currentSV = null;
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Xóa thất bại! Vui lòng kiểm tra lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view,
                        "Có lỗi xảy ra khi xóa sinh viên:\n" + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleRefresh() {
        view.clearForm();
        currentSV = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getTable().getSelectedRow();
        if (row >= 0) {
            try {
                // Lấy dữ liệu từ bảng và fill vào form
                JTable table = view.getTable();
                SinhVienModel sv = new SinhVienModel();

                sv.setMasv(table.getValueAt(row, 0).toString());
                sv.setHoten(table.getValueAt(row, 1).toString());
                sv.setNgaysinh(table.getValueAt(row, 2).toString());
                sv.setGioitinh(table.getValueAt(row, 3).toString());
                sv.setDiachi(table.getValueAt(row, 4).toString());
                sv.setMalop(table.getValueAt(row, 5).toString());

                // Set default username from ID
                sv.setUsername(sv.getMasv());

                view.fillForm(sv);
                currentSV = sv;

                System.out.println("Selected student: " + sv.getHoten() + " (" + sv.getMasv() + ")");
            } catch (Exception ex) {
                System.err.println("Lỗi khi fill form: " + ex.getMessage());
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

    /**
     * Refresh lop data when class list changes
     */
    public void refreshLopData() {
        loadLop();
    }

    /**
     * Refresh all data (students and classes)
     */
    public void refreshAllData() {
        loadData(); // Reload student table
        loadLop(); // Reload class dropdown
    }
}
