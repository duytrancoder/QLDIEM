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
    private boolean isEditing = false;
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
            case "Lưu":
                handleSave();
                break;
            case "Hủy":
                handleCancel();
                break;
        }
    }
    
    private void handleAdd() {
        view.clearForm();
        view.setEditingMode(true);
        isEditing = false;
        currentSV = null;
    }
    
    private void handleEdit() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng để sửa!");
            return;
        }
        
        // Sử dụng currentSV đã được fill từ mouseClick
        if (currentSV != null) {
            view.setEditingMode(true);
            isEditing = true;
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
    
    private void handleSave() {
        try {
            SinhVienModel sv = view.getFormData();
            
            // Debug: Print form data
            System.out.println("Debug - Form data:");
            System.out.println("- MASV: " + sv.getMasv());
            System.out.println("- Họ tên: " + sv.getHoten());
            System.out.println("- Mã lớp: " + sv.getMalop());
            System.out.println("- Username: " + sv.getUsername());
            
            // Validate
            if (sv.getMasv().isEmpty() || sv.getHoten().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã SV và Họ tên không được để trống!");
                return;
            }
            
            if (sv.getMalop() == null || sv.getMalop().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn lớp cho sinh viên!");
                return;
            }
            
            if (sv.getUsername().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Username không được để trống!");
                return;
            }
            
            // Check if student ID already exists (chỉ khi thêm mới hoặc đổi mã sinh viên)
            if (!isEditing && model.isExistMasv(sv.getMasv())) {
                JOptionPane.showMessageDialog(view, "Mã sinh viên đã tồn tại! Vui lòng chọn mã khác.");
                return;
            }
            
            if (isEditing && currentSV != null && !currentSV.getMasv().equals(sv.getMasv())) {
                if (model.isExistMasv(sv.getMasv())) {
                    JOptionPane.showMessageDialog(view, "Mã sinh viên đã tồn tại! Vui lòng chọn mã khác.");
                    return;
                }
            }
            
            // Check if username already exists (chỉ khi thêm mới hoặc đổi username)
            if (!isEditing && model.isExistUsername(sv.getUsername())) {
                JOptionPane.showMessageDialog(view, "Username đã tồn tại! Vui lòng chọn username khác.");
                return;
            }
            
            if (isEditing && currentSV != null && !currentSV.getUsername().equals(sv.getUsername())) {
                if (model.isExistUsername(sv.getUsername())) {
                    JOptionPane.showMessageDialog(view, "Username đã tồn tại! Vui lòng chọn username khác.");
                    return;
                }
            }
            
            boolean success;
            if (isEditing) {
                success = model.capNhatSinhVien(sv);
            } else {
                success = model.themSinhVien(sv);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(view, isEditing ? "Sửa thành công!" : "Thêm thành công!");
                loadData();
                view.setEditingMode(false);
                view.clearForm();
                isEditing = false;
                currentSV = null;
            } else {
                JOptionPane.showMessageDialog(view, "Thao tác thất bại! Vui lòng kiểm tra lại dữ liệu.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void handleCancel() {
        view.setEditingMode(false);
        view.clearForm();
        isEditing = false;
        currentSV = null;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getTable().getSelectedRow();
        if (row >= 0 && !isEditing) {
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
                sv.setUsername(table.getValueAt(row, 6).toString());
                
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
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
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
        loadLop();  // Reload class dropdown
    }
}

