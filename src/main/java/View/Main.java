package View;

import javax.swing.*;
import Controller.ModernLoginController;
import Model.LoginModel;

/**
 * Main class to run the Student Grade Management System
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Set Look and Feel to default Swing look
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            // If system L&F is not available, use default
            System.out.println("Could not set system look and feel: " + e.getMessage());
        }

        // Run Schema Update (Important for Dept/Subject refactor)
        try {
            connection.UpdateSchemaV2.updateDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi cập nhật CSDL: " + e.getMessage());
        }

        // Run the application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Create login model
                LoginModel loginModel = new LoginModel();

                // Create and show login view
                ModernLoginView loginView = new ModernLoginView();

                // Create login controller
                ModernLoginController loginController = new ModernLoginController(loginView, loginModel);

                // Show the login window
                loginView.setVisible(true);

                System.out.println("=== HỆ THỐNG QUẢN LÝ ĐIỂM SINH VIÊN ===");
                System.out.println("Ứng dụng đã khởi động thành công!");
                System.out.println("Tài khoản mặc định:");
                System.out.println("- Admin: username=admin, password=admin");
                System.out.println("- Giáo viên: username=gv001, password=password");
                System.out.println("- Sinh viên: username=sv001, password=password");
                System.out.println("=====================================");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Lỗi khởi động ứng dụng: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}