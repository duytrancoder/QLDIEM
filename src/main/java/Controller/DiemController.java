/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import View.DiemPanel;
import Model.DiemModel;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author noqok
 */
public class DiemController implements MouseListener, ActionListener {

    private DiemPanel view;
    private DiemModel model;
    int pos = 0;
    int check = 0;

    public DiemController(DiemPanel view, DiemModel model) {
        this.view = view;
        this.model = model;
        view.on_off(true, false);

        view.addActionListerner(this);
        view.addTableMouseAction(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
       int row = view.getTblDiem().getSelectedRow();
        if (row >=0){
            DiemModel nv = model.getDs().get(row);
            view.fillform(nv);
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

    private String validateInput(DiemModel d) {
        if (d.getMasv() == null || d.getMasv().trim().isEmpty()) {
            return "Mã sinh viên không được để trống!";
        }

        if (d.getMamon() == null || d.getMamon().trim().isEmpty()) {
            return "Mã môn học không được để trống!";
        }

        if (d.getHocky() < 1 || d.getHocky() > 3) {
            return "Học kỳ phải là 1, 2 hoặc 3!";
        }

        if (d.getNamhoc() == null || d.getNamhoc().trim().isEmpty()) {
            return "Năm học không được để trống!";
        }

        if (d.getDiemcc() < 0 || d.getDiemcc() > 10) {
            return "Điểm chuyên cần phải từ 0 đến 10!";
        }

        if (d.getDiemgk() < 0 || d.getDiemgk() > 10) {
            return "Điểm giữa kỳ phải từ 0 đến 10!";
        }

        if (d.getDiemck() < 0 || d.getDiemck() > 10) {
            return "Điểm cuối kỳ phải từ 0 đến 10!";
        }

        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Thêm")) {
            check = 1;
            view.clearform();
            view.on_off(false, true);

        } else if (command.equals("Sửa")) {
            if (view.getTblDiem().getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng để sửa!");
                return;
            }
            check = 2;
            view.on_off(false, true);

        } else if (command.equals("Xóa")) {
            int row = view.getTblDiem().getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng để xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa?");
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(view.getTblDiem().getValueAt(row, 0).toString());
                if (model.deleteDiem(id)) {
                    JOptionPane.showMessageDialog(view, "Xóa thành công!");
                    view.loadtable(model.getDs());

                    view.clearform();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa thất bại!");
                }
            }
        } else if (command.equals("Xác nhận")) {
            DiemModel d = view.getformdata();

            String validationError = validateInput(d);
            if (validationError != null) {
                JOptionPane.showMessageDialog(view, validationError);
                return;
            }

            if (check == 1) {
                if (model.insertDiem(d)) {
                    JOptionPane.showMessageDialog(view, "Thêm thành công!");
                } else {
                    JOptionPane.showMessageDialog(view, "Thêm thất bại!");
                }
            } else if (check == 2) {
                if (model.updateDiem(d)) {
                    JOptionPane.showMessageDialog(view, "Sửa thành công!");
                } else {
                    JOptionPane.showMessageDialog(view, "Sửa thất bại!");
                }
            }

            check = 0;
            view.loadtable(model.getDs());
            view.on_off(true, false);
            view.clearform();
        } else if (command.equals("Hủy")) {
            check = 0;
            view.clearform();
            view.on_off(true, false);
        }else if (command.equals("🔍")) {
           String keyword = view.getSearchKeyword(); 
        if (keyword.isEmpty()) {
            view.loadtable(model.getDs());
        } else {
            ArrayList<DiemModel> kq = model.search(keyword);
            view.loadtable(kq);
        }
        }
    }

    

    
}
