package View;

import Model.LopModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Panel phân công giáo viên quản lý lớp cho Admin
 */
public class PhanCongGiaoVienPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    private JTable tblPhanCong;
    private DefaultTableModel tableModel;

    private JComboBox<String> cbGiaoVien;
    private JComboBox<String> cbMonHoc; // New
    private JComboBox<String> cbLop;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    public PhanCongGiaoVienPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        cbGiaoVien = new JComboBox<>();
        cbGiaoVien.addItem("-- Chọn giáo viên --");
        cbMonHoc = new JComboBox<>();
        cbMonHoc.addItem("-- Chọn môn học --");
        cbLop = new JComboBox<>();
        cbLop.addItem("-- Chọn lớp --");

        btnThem = createButton("Thêm phân công", SUCCESS_COLOR);
        btnSua = createButton("Sửa phân công", PRIMARY_COLOR);
        btnXoa = createButton("Xóa phân công", DANGER_COLOR);
        btnLamMoi = createButton("Làm mới", Color.GRAY);

        // Table
        String[] columns = { "Mã GV", "Tên Giáo viên", "Môn học", "Mã lớp" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPhanCong = new JTable(tableModel);
        tblPhanCong.setRowHeight(30);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        return btn;
    }

    private void setupLayout() {
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Phân lớp cho Giáo viên");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Center - Split form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createTablePanel());
        splitPane.setDividerLocation(350);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel("Thêm phân công");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Giáo viên:", cbGiaoVien));
        formPanel.add(createFormField("Môn học:", cbMonHoc)); // Add to layout
        formPanel.add(createFormField("Lớp:", cbLop));

        formPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        formPanel.add(buttonPanel);

        return formPanel;
    }

    private JPanel createFormField(String label, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(CARD_COLOR);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldLabel.setPreferredSize(new Dimension(120, 25));

        component.setPreferredSize(new Dimension(200, 35));

        fieldPanel.add(fieldLabel, BorderLayout.WEST);
        fieldPanel.add(component, BorderLayout.CENTER);

        return fieldPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JScrollPane scrollPane = new JScrollPane(tblPhanCong);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // Public methods
    public void loadGiaoVien(ArrayList<String> listGV) {
        cbGiaoVien.removeAllItems();
        cbGiaoVien.addItem("-- Chọn giáo viên --");
        for (String gv : listGV) {
            cbGiaoVien.addItem(gv);
        }
    }

    public void loadLop(ArrayList<LopModel> listLop) {
        cbLop.removeAllItems();
        cbLop.addItem("-- Chọn lớp --");
        for (LopModel lop : listLop) {
            cbLop.addItem(lop.getMalop() + " - " + lop.getTenlop());
        }
    }

    public void loadMonHoc(ArrayList<String> listMH) {
        cbMonHoc.removeAllItems();
        cbMonHoc.addItem("-- Chọn môn học --");
        for (String mh : listMH) {
            cbMonHoc.addItem(mh);
        }
    }

    public String getSelectedGiaoVien() {
        String selected = (String) cbGiaoVien.getSelectedItem();
        if (selected == null || selected.equals("-- Chọn giáo viên --")) {
            return null;
        }
        return selected.split(" - ")[0]; // Lấy mã GV
    }

    public String getSelectedMonHoc() {
        String selected = (String) cbMonHoc.getSelectedItem();
        if (selected == null || selected.equals("-- Chọn môn học --")) {
            return null;
        }
        return selected.split(" - ")[0]; // Lấy mã MH
    }

    public String getSelectedLop() {
        String selected = (String) cbLop.getSelectedItem();
        if (selected == null || selected.equals("-- Chọn lớp --")) {
            return null;
        }
        return selected.split(" - ")[0]; // Lấy mã lớp
    }

    public void loadTableData(ArrayList<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void addActionListener(ActionListener listener) {
        btnThem.addActionListener(listener);
        btnSua.addActionListener(listener);
        btnXoa.addActionListener(listener);
        btnLamMoi.addActionListener(listener);
    }

    public void fillForm(String magv, String malop, String mamon) {
        if (magv != null && !magv.isEmpty()) {
            for (int i = 0; i < cbGiaoVien.getItemCount(); i++) {
                String item = cbGiaoVien.getItemAt(i);
                if (item.startsWith(magv + " - ")) {
                    cbGiaoVien.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbGiaoVien.setSelectedIndex(0);
        }

        // Logic to match mamon will happen in controller after loading subjects,
        // effectively handled by calling setSelectedIndex if controller matches items.
        // We will overload or just assume controller manages the redraw of monhoc list.

        if (malop != null && !malop.isEmpty()) {
            for (int i = 0; i < cbLop.getItemCount(); i++) {
                String item = cbLop.getItemAt(i);
                if (item.startsWith(malop + " - ")) {
                    cbLop.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbLop.setSelectedIndex(0);
        }
    }

    public void clearForm() {
        cbGiaoVien.setSelectedIndex(0);
        cbMonHoc.removeAllItems();
        cbMonHoc.addItem("-- Chọn môn học --");
        cbLop.setSelectedIndex(0);
        tblPhanCong.clearSelection();
    }

    public void addTableMouseListener(java.awt.event.MouseListener listener) {
        tblPhanCong.addMouseListener(listener);
    }

    public JTable getTable() {
        return tblPhanCong;
    }

    public JComboBox<String> getCbGiaoVien() {
        return cbGiaoVien;
    }

    public JComboBox<String> getCbMonHoc() {
        return cbMonHoc;
    }
}
