package Controller;

import Model.CauHinhModel;
import View.KhoaSoNienKhoaPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KhoaSoNienKhoaController implements ActionListener {

    private KhoaSoNienKhoaPanel view;
    private ModernMainController mainController;

    public KhoaSoNienKhoaController(KhoaSoNienKhoaPanel view, ModernMainController mainController) {
        this.view = view;
        this.mainController = mainController;

        // Listen to view events
        this.view.addSaveListener(this);

        // Load initial data
        loadSettings();
    }

    private void loadSettings() {
        CauHinhModel model = new CauHinhModel().getGlobalSettings();
        if (model != null) {
            view.setNamHoc(model.getNamhoc());
            view.setHocKy(model.getHocky());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Lưu Cấu Hình")) {
            saveSettings();
        }
    }

    private void saveSettings() {
        String namHoc = view.getNamHoc();
        int hocKy = view.getHocKy();

        if (namHoc.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập năm học!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = new CauHinhModel().updateSettings(namHoc, hocKy);
            if (success) {
                JOptionPane.showMessageDialog(view, "Cập nhật cấu hình thành công!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                if (mainController != null) {
                    mainController.refreshGlobalSettings();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
