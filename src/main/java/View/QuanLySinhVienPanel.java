package View;

import Model.SinhVienModel;
import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Panel quản lý sinh viên cho Admin (thêm sinh viên vào lớp)
 */
public class QuanLySinhVienPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    private JTable tblSinhVien;
    private DefaultTableModel tableModel;

    private JTextField tfMasv;
    private JTextField tfHoten;
    private DatePicker dpNgaysinh;
    private JComboBox<String> cbGioitinh;
    private JTextField tfDiachi;
    private JComboBox<String> cbLop;

    // Search fields
    private JTextField tfSearch;
    private JButton btnTimKiem;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    public QuanLySinhVienPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Form fields
        tfMasv = createTextField();
        tfHoten = createTextField();
        com.github.lgooddatepicker.components.DatePickerSettings dateSettings = new com.github.lgooddatepicker.components.DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setAllowKeyboardEditing(false);

        dpNgaysinh = new DatePicker(dateSettings);
        dpNgaysinh.getComponentDateTextField().setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        dpNgaysinh.setPreferredSize(new java.awt.Dimension(200, 35));
        cbGioitinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
        tfDiachi = createTextField();
        cbLop = new JComboBox<>();
        cbLop.addItem("-- Chọn lớp --");

        // Search components
        tfSearch = new JTextField();
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfSearch.setPreferredSize(new Dimension(200, 35));

        btnTimKiem = createButton("Tìm", PRIMARY_COLOR);

        // Buttons
        btnThem = createButton("Thêm", SUCCESS_COLOR);
        btnSua = createButton("Sửa", PRIMARY_COLOR);
        btnXoa = createButton("Xóa", DANGER_COLOR);
        btnLamMoi = createButton("Làm mới", Color.GRAY);

        // Table
        String[] columns = { "Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "Lớp" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSinhVien = new JTable(tableModel);
        tblSinhVien.setRowHeight(30);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(200, 35));
        return tf;
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

        JLabel titleLabel = new JLabel("Quản lý Sinh viên");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.add(new JLabel("Tìm kiếm: "));
        searchPanel.add(tfSearch);
        searchPanel.add(btnTimKiem);

        topPanel.add(searchPanel, BorderLayout.EAST);

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

        JLabel formTitle = new JLabel("Thông tin sinh viên");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Mã SV:", tfMasv));
        formPanel.add(createFormField("Họ tên:", tfHoten));
        formPanel.add(createFormField("Ngày sinh:", dpNgaysinh));
        formPanel.add(createFormField("Giới tính:", cbGioitinh));
        formPanel.add(createFormField("Địa chỉ:", tfDiachi));
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

        if (component instanceof JComboBox) {
            component.setPreferredSize(new Dimension(200, 35));
        }

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

        JScrollPane scrollPane = new JScrollPane(tblSinhVien);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // Public methods
    public void loadTableData(ArrayList<SinhVienModel> data) {
        tableModel.setRowCount(0);
        for (SinhVienModel sv : data) {
            Object[] row = {
                    sv.getMasv(),
                    sv.getHoten(),
                    formatDate(sv.getNgaysinh()), // Format Date
                    sv.getGioitinh(),
                    sv.getDiachi(),
                    sv.getMalop()
            };
            tableModel.addRow(row);
        }
    }

    private String formatDate(String mysqldate) {
        if (mysqldate == null || mysqldate.isEmpty()) {
            return "";
        }
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(mysqldate);
            return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return mysqldate;
        }
    }

    public void loadLop(ArrayList<String> listLop) {
        cbLop.removeAllItems();
        cbLop.addItem("-- Chọn lớp --");
        for (String lop : listLop) {
            cbLop.addItem(lop);
        }
    }

    public SinhVienModel getFormData() {
        SinhVienModel sv = new SinhVienModel();
        sv.setMasv(tfMasv.getText().trim());
        sv.setHoten(tfHoten.getText().trim());
        java.time.LocalDate date = dpNgaysinh.getDate();
        sv.setNgaysinh(date != null ? date.toString() : "");
        sv.setGioitinh((String) cbGioitinh.getSelectedItem());
        sv.setDiachi(tfDiachi.getText().trim());

        // Get malop with improved error handling
        String lopSelection = (String) cbLop.getSelectedItem();
        if (lopSelection != null && !lopSelection.equals("-- Chọn lớp --")) {
            if (lopSelection.contains(" - ")) {
                sv.setMalop(lopSelection.split(" - ")[0]); // Lấy mã lớp
            } else {
                sv.setMalop(lopSelection); // Nếu không có format thì dùng trực tiếp
            }
        }

        return sv;
    }

    public void fillForm(SinhVienModel sv) {
        tfMasv.setText(sv.getMasv());
        tfHoten.setText(sv.getHoten());
        String ngaysinh = sv.getNgaysinh();
        if (ngaysinh != null && !ngaysinh.isEmpty()) {
            try {
                // Try parsing standard SQL format first (yyyy-MM-dd)
                dpNgaysinh.setDate(java.time.LocalDate.parse(ngaysinh));
            } catch (Exception e) {
                try {
                    // Try parsing display format (dd/MM/yyyy)
                    dpNgaysinh.setDate(java.time.LocalDate.parse(ngaysinh,
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } catch (Exception ex) {
                    dpNgaysinh.clear();
                }
            }
        } else {
            dpNgaysinh.clear();
        }
        cbGioitinh.setSelectedItem(sv.getGioitinh());
        tfDiachi.setText(sv.getDiachi());

        // Find and select the correct class
        String malop = sv.getMalop();
        if (malop != null) {
            for (int i = 0; i < cbLop.getItemCount(); i++) {
                String item = cbLop.getItemAt(i);
                if (item != null && item.startsWith(malop + " - ")) {
                    cbLop.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbLop.setSelectedIndex(0);
        }

    }

    public void clearForm() {
        tfMasv.setText("");
        tfHoten.setText("");
        dpNgaysinh.clear();
        cbGioitinh.setSelectedIndex(0);
        tfDiachi.setText("");
        cbLop.setSelectedIndex(0);
    }

    public void addActionListener(ActionListener listener) {
        btnThem.addActionListener(listener);
        btnSua.addActionListener(listener);
        btnXoa.addActionListener(listener);
        btnLamMoi.addActionListener(listener);
        btnTimKiem.addActionListener(listener);
    }

    public String getSearchKeyword() {
        return tfSearch.getText().trim();
    }

    public void addTableMouseListener(MouseListener listener) {
        tblSinhVien.addMouseListener(listener);
    }

    public JTable getTable() {
        return tblSinhVien;
    }
}
