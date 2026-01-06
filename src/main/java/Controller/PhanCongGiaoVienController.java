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

        view.getCbGiaoVien().addActionListener(e -> updateSubjectList());

        refreshAll(); // Ensure data is loaded immediately
    }

    public void refreshAll() {
        loadData();
        loadGiaoVien();
        loadLop();
        view.clearForm();
    }

    private void updateSubjectList() {
        String selectedGV = view.getSelectedGiaoVien();
        if (selectedGV != null && listTeachers != null) {
            // Find teacher
            GiaoVienModel target = null;
            for (GiaoVienModel gv : listTeachers) {
                if (gv.getMagv().equals(selectedGV)) {
                    target = gv;
                    break;
                }
            }

            ArrayList<String> subjects = new ArrayList<>();
            if (target != null) {
                String codes = target.getCacMon(); // e.g. "MH01,MH02"
                String names = target.getTenCacMon(); // e.g. "Toan, Ly"

                if (codes != null && !codes.isEmpty()) {
                    String[] codeArr = codes.split(",");
                    String[] nameArr = (names != null) ? names.split(", ") : new String[0];

                    for (int i = 0; i < codeArr.length; i++) {
                        String code = codeArr[i].trim();
                        String name = (i < nameArr.length) ? nameArr[i].trim() : "N/A";
                        if (!code.isEmpty()) {
                            subjects.add(code + " - " + name);
                        }
                    }
                }
            }
            view.loadMonHoc(subjects);
        } else {
            view.loadMonHoc(new ArrayList<>());
        }
    }

    private void loadData() {
        // Updated Query: Select mamon and use correct join logic
        ArrayList<Object[]> data = new ArrayList<>();
        // Note: LEFT JOIN on monhoc to get name is fine.
        String query = "SELECT pc.magv, gv.hoten, mh.tenmon, pc.malop, pc.mamon " +
                "FROM tblphancong pc " +
                "JOIN tblgiaovien gv ON pc.magv = gv.magv " +
                "LEFT JOIN tblmonhoc mh ON pc.mamon = mh.mamon " +
                "LEFT JOIN tblclass c ON pc.malop = c.malop " +
                "ORDER BY pc.magv, pc.malop, pc.mamon";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("magv"),
                        rs.getString("hoten"),
                        rs.getString("tenmon") != null ? rs.getString("tenmon") : "",
                        rs.getString("malop"),
                        rs.getString("mamon") // Hidden column index 4
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
        listTeachers = gvModel.fetchAllTeachers(); // Store list
        ArrayList<String> listGVString = new ArrayList<>();
        for (GiaoVienModel gv : listTeachers) {
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
    private String selectedMamon = null; // New tracking
    private ArrayList<GiaoVienModel> listTeachers; // Store loaded teachers

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
        String mamon = view.getSelectedMonHoc();

        if (magv == null || malop == null || mamon == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên, môn học và lớp!");
            return;
        }

        // Validation
        if (lopModel.checkGiaoVienQuanLyLop(magv, malop, mamon)) {
            JOptionPane.showMessageDialog(view, "Phân công này đã tồn tại!");
            return;
        }

        if (lopModel.themPhanCong(magv, malop, mamon)) {
            JOptionPane.showMessageDialog(view, "Thêm phân công thành công!");
            loadData();
            view.clearForm();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm phân công thất bại!");
        }
    }

    private void handleSua() {
        if (selectedMagv == null || selectedMalop == null || selectedMamon == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phân công cần sửa từ bảng!");
            return;
        }

        String newMagv = view.getSelectedGiaoVien();
        String newMalop = view.getSelectedLop();
        String newMamon = view.getSelectedMonHoc();

        if (newMagv == null || newMalop == null || newMamon == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn giáo viên, môn và lớp mới!");
            return;
        }

        // Check if nothing changed
        if (newMagv.equals(selectedMagv) && newMalop.equals(selectedMalop) && newMamon.equals(selectedMamon)) {
            return;
        }

        // Validation: Check if the NEW assignment already exists (conflict)
        if (lopModel.checkGiaoVienQuanLyLop(newMagv, newMalop, newMamon)) {
            JOptionPane.showMessageDialog(view, "Phân công mới bị trùng với dữ liệu đã có!");
            return;
        }

        if (lopModel.updatePhanCong(selectedMagv, selectedMalop, selectedMamon, newMagv, newMalop, newMamon)) {
            JOptionPane.showMessageDialog(view, "Cập nhật phân công thành công!");
            loadData();
            selectedMagv = null;
            selectedMalop = null;
            selectedMamon = null;
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
        // Determine mamon from table row (needs to be available)
        // Table columns: "Mã GV" (0), "Tên GV" (1), "Môn học" (2), "Mã lớp" (3)
        // Môn học in table is name. We need mamon.
        // It's better to store mamon in table model (hidden) or fetch data again.
        // Simplest: Add hidden column or scrape. For now, we scrape if distinct.
        // Wait, loadData sets columns 0,1,2,3.
        // We will update loadData to include mamon in hidden column or just use name to
        // find code?
        // Better: Update loadData to include mamon as column 4 (hidden) or just use
        // logic.
        // Let's rely on stored Data or re-retireval.
        // Actually, let's look at `loadData` update below. I will update table model to
        // have 5 cols.

        String mamon = view.getTable().getModel().getValueAt(row, 4).toString();
        String malop = view.getTable().getValueAt(row, 3).toString();

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa phân công này?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (lopModel.xoaPhanCong(magv, malop, mamon)) {
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
        selectedMamon = null;
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
            JTable table = view.getTable();
            String magv = table.getValueAt(row, 0).toString();
            String malop = table.getValueAt(row, 3).toString();
            String mamon = "";
            if (table.getColumnCount() > 4) {
                // Check if value is null before toString
                Object val = table.getModel().getValueAt(row, 4);
                mamon = (val != null) ? val.toString() : "";
            }

            // Update tracking variables
            selectedMagv = magv;
            selectedMalop = malop;
            selectedMamon = mamon;

            view.fillForm(magv, malop, mamon);
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
