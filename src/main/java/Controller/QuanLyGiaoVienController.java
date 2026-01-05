package Controller;

import Model.GiaoVienModel;
import View.QuanLyGiaoVienPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Controller cho quản lý giáo viên
 */
public class QuanLyGiaoVienController implements ActionListener, MouseListener {

    private QuanLyGiaoVienPanel view;
    private GiaoVienModel model;
    private GiaoVienModel currentGV = null;

    public QuanLyGiaoVienController(QuanLyGiaoVienPanel view) {
        this.view = view;
        this.model = new GiaoVienModel();

        view.addActionListener(this);
        view.addTableMouseListener(this);

        loadData();

        loadMonHoc();
    }

    private void loadData() {
        ArrayList<GiaoVienModel> list = model.getAllGiaoVien();
        view.loadTableData(list);
    }

    private void loadMonHoc() {
        // TODO: Load từ Model.MonHocModel
        ArrayList<String> listMonHoc = new ArrayList<>();
        listMonHoc.add("MH01 - Toán");
        listMonHoc.add("MH02 - Văn");
        listMonHoc.add("MH03 - Anh");
        listMonHoc.add("MH04 - Lý");
        listMonHoc.add("MH05 - Hóa");
        view.loadMonHoc(listMonHoc);
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

            // Validate basic fields
            if (gv.getMagv() == null || gv.getMagv().trim().isEmpty() ||
                    gv.getHoten() == null || gv.getHoten().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã GV và Họ tên không được để trống!");
                return;
            }

            if (gv.getMamon() == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn Môn học!");
                return;
            }

            // Default username = Magv
            gv.setUsername(gv.getMagv());

            // Check for duplicates
            if (model.isExistMagv(gv.getMagv())) {
                JOptionPane.showMessageDialog(view, "Mã giáo viên đã tồn tại! Vui lòng chọn mã khác.");
                return;
            }

            // Validate date format if provided
            String ngaysinh = gv.getNgaysinh();
            if (ngaysinh != null && !ngaysinh.trim().isEmpty()) {
                try {
                    java.sql.Date.valueOf(ngaysinh); // Validate format yyyy-mm-dd
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(view,
                            "Ngày sinh không đúng định dạng! Vui lòng nhập theo định dạng yyyy-mm-dd (ví dụ: 1980-05-15)");
                    return;
                }
            }

            boolean success = model.themGiaoVien(gv);

            if (success) {
                JOptionPane.showMessageDialog(view, "Thêm thành công!");
                loadData();
                view.clearForm();
                currentGV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại! Vui lòng kiểm tra lại dữ liệu.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleEdit() {
        if (currentGV == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên từ bảng để sửa!");
            return;
        }

        try {
            GiaoVienModel gv = view.getFormData();

            // Validate basic fields
            if (gv.getMagv() == null || gv.getMagv().trim().isEmpty() ||
                    gv.getHoten() == null || gv.getHoten().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã GV và Họ tên không được để trống!");
                return;
            }

            if (gv.getMamon() == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn Môn học!");
                return;
            }

            // Check if ID changed
            if (!currentGV.getMagv().equals(gv.getMagv())) {
                JOptionPane.showMessageDialog(view, "Không thể thay đổi Mã Giáo Viên!");
                return;
            }

            // Validate date format if provided
            String ngaysinh = gv.getNgaysinh();
            if (ngaysinh != null && !ngaysinh.trim().isEmpty()) {
                try {
                    java.sql.Date.valueOf(ngaysinh); // Validate format yyyy-mm-dd
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(view,
                            "Ngày sinh không đúng định dạng! Vui lòng nhập theo định dạng yyyy-mm-dd (ví dụ: 1980-05-15)");
                    return;
                }
            }

            boolean success = model.capNhatGiaoVien(gv);

            if (success) {
                JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
                loadData();
                view.clearForm();
                currentGV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại! Vui lòng kiểm tra lại dữ liệu.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleDelete() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên cần xóa!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentGV == null) {
            // Nếu chưa click, lấy thông tin từ bảng
            String magv = view.getTable().getValueAt(row, 0).toString();
            currentGV = model.getGiaoVienByMagv(magv);
        }

        if (currentGV == null) {
            JOptionPane.showMessageDialog(view, "Không thể lấy thông tin giáo viên!");
            return;
        }

        String message = String.format("Bạn có chắc muốn xóa giáo viên:\n\n" +
                "Mã GV: %s\nHọ tên: %s\n\n" +
                "⚠️ Lưu ý: Sẽ xóa tất cả phân công và tài khoản của giáo viên này!",
                currentGV.getMagv(), currentGV.getHoten());

        int confirm = JOptionPane.showConfirmDialog(view, message,
                "Xác nhận xóa giáo viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = model.xoaGiaoVien(currentGV.getMagv());
                if (success) {
                    JOptionPane.showMessageDialog(view,
                            "Đã xóa thành công giáo viên " + currentGV.getHoten() + "!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    view.clearForm();
                    currentGV = null;
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Xóa thất bại! Vui lòng kiểm tra lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view,
                        "Có lỗi xảy ra khi xóa giáo viên:\n" + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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
                // Lấy dữ liệu từ bảng và fill vào form (Optimized: Scrape directly from table)
                JTable table = view.getTable();
                GiaoVienModel gv = new GiaoVienModel();

                gv.setMagv(table.getValueAt(row, 0).toString());
                gv.setHoten(table.getValueAt(row, 1).toString());
                gv.setGioitinh(table.getValueAt(row, 2).toString());
                gv.setNgaysinh(table.getValueAt(row, 3).toString());
                gv.setEmail(table.getValueAt(row, 4).toString());
                gv.setSdt(table.getValueAt(row, 5).toString());
                gv.setMamon(table.getValueAt(row, 6).toString());
                // table.getValueAt(row, 7) is TenMon

                // Set default username from ID just in case
                gv.setUsername(gv.getMagv());

                view.fillForm(gv);
                currentGV = gv;
                System.out.println("Selected teacher (from table): " + gv.getHoten());
            } catch (Exception ex) {
                System.err.println("Lỗi khi fill form giáo viên: " + ex.getMessage());
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
