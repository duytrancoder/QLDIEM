package Controller;

import Model.LopModel;
import Model.GiaoVienModel;
import connection.DatabaseConnection;
import View.PhanCongGiaoVienPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.ArrayList;

/**
 * Controller cho phân công giáo viên quản lý lớp
 */
public class PhanCongGiaoVienController implements ActionListener, MouseListener {

    private PhanCongGiaoVienPanel view;
    private LopModel lopModel;
    private GiaoVienModel gvModel;

    public PhanCongGiaoVienController(PhanCongGiaoVienPanel view) {
        this.view = view;
        this.lopModel = new LopModel();
        this.gvModel = new GiaoVienModel();

        view.addActionListener(this);
        view.addTableMouseListener(this);

        loadData();
        loadGiaoVien();
        loadLop();
    }

    private void loadData() {
        // Load danh sách phân công từ database với JOIN để lấy tên môn và tên khoa
        ArrayList<Object[]> data = new ArrayList<>();
        String query = "SELECT pc.magv, gv.hoten, mh.tenmon, pc.malop " +
                "FROM tblphancong pc " +
                "JOIN tblgiaovien gv ON pc.magv = gv.magv " +
                "LEFT JOIN tblmonhoc mh ON gv.mamon = mh.mamon " +
                "LEFT JOIN tblclass c ON pc.malop = c.malop " +
                "ORDER BY pc.magv, pc.malop";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("magv"),
                        rs.getString("hoten"),
                        rs.getString("tenmon") != null ? rs.getString("tenmon") : "",
                        rs.getString("malop")
                };
                data.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi load phân công: " + e.getMessage());
            e.printStackTrace();
        }
        view.loadTableData(data);
    }

    private void loadGiaoVien() {
        ArrayList<GiaoVienModel> listGV = gvModel.getAllGiaoVien();
        ArrayList<String> listGVString = new ArrayList<>();
        for (GiaoVienModel gv : listGV) {
            listGVString.add(gv.getMagv() + " - " + gv.getHoten());
        }
        view.loadGiaoVien(listGVString);
    }

    private void loadLop() {
        ArrayList<LopModel> listLop = lopModel.getAllLop();
        view.loadLop(listLop);
    }

    // Track selected assignment for updates
    private String selectedMagv = null;
    private String selectedMalop = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Thêm phân công")) {
            handleThem();
        } else if (command.equals("Sửa phân công")) {
            handleSua();
        } else if (command.equals("Xóa phân công")) {
            handleXoa();
        } else if (command.equals("Làm mới")) {
            handleRefresh();
        }
    }

    private void handleThem() {
        String magv = view.getSelectedGiaoVien();
        String malop = view.getSelectedLop();

        if (magv == null || malop == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên và lớp!");
            return;
        }

        // Validation: Check if exists
        if (lopModel.checkGiaoVienQuanLyLop(magv, malop)) {
            JOptionPane.showMessageDialog(view, "Phân công này đã tồn tại! Vui lòng kiểm tra lại.");
            return;
        }

        if (lopModel.themPhanCong(magv, malop)) {
            JOptionPane.showMessageDialog(view, "Thêm phân công thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Thêm phân công thất bại!");
        }
    }

    private void handleSua() {
        if (selectedMagv == null || selectedMalop == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phân công cần sửa từ bảng!");
            return;
        }

        String newMagv = view.getSelectedGiaoVien();
        String newMalop = view.getSelectedLop();

        if (newMagv == null || newMalop == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên và lớp mới!");
            return;
        }

        // Check if nothing changed
        if (newMagv.equals(selectedMagv) && newMalop.equals(selectedMalop)) {
            return;
        }

        // Validation: Check if the NEW assignment already exists (conflict with others)
        if (lopModel.checkGiaoVienQuanLyLop(newMagv, newMalop)) {
            JOptionPane.showMessageDialog(view, "Phân công mới bị trùng với dữ liệu đã có!");
            return;
        }

        if (lopModel.updatePhanCong(selectedMagv, selectedMalop, newMagv, newMalop)) {
            JOptionPane.showMessageDialog(view, "Cập nhật phân công thành công!");
            loadData();
            // Reset selection tracking
            selectedMagv = null;
            selectedMalop = null;
            view.clearForm();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
        }
    }

    private void handleXoa() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng để xóa!");
            return;
        }

        String magv = view.getTable().getValueAt(row, 0).toString();
        String malop = view.getTable().getValueAt(row, 3).toString(); // Cột 3 là Mã lớp

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa phân công này?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (lopModel.xoaPhanCong(magv, malop)) {
                JOptionPane.showMessageDialog(view, "Xóa phân công thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa phân công thất bại!");
            }
        }
    }

    private void handleRefresh() {
        view.clearForm();
        selectedMagv = null;
        selectedMalop = null;
    }

    /**
     * Refresh lop data when class list changes
     */
    public void refreshLopData() {
        loadLop();
    }

    public void addTableMouseListener(java.awt.event.MouseListener listener) {
        view.addTableMouseListener(listener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = view.getTable().getSelectedRow();
        if (row >= 0) {
            // Scrape data from table
            // Table columns: "Mã GV" (0), "Tên Giáo viên" (1), "Môn học" (2), "Mã lớp" (3)
            JTable table = view.getTable();
            String magv = table.getValueAt(row, 0).toString();
            String malop = table.getValueAt(row, 3).toString();

            // Update tracking variables
            selectedMagv = magv;
            selectedMalop = malop;

            view.fillForm(magv, malop);
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
