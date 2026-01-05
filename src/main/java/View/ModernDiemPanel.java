package View;

import Model.DiemModel;
import Model.LopModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Modern DiemPanel with beautiful UI
 */
public class ModernDiemPanel extends JPanel {

    // Colors
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    // Components
    private JTable tblDiem;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField tfMaSV;
    private JTextField tfMaMon;
    private JComboBox<String> cbHocKy;
    private JTextField tfNamHoc;
    private JTextField tfDiemCC;
    private JTextField tfDiemGK;
    private JTextField tfDiemCK;
    private JTextField tfDiemTK;
    private JTextField tfSearch;
    private JComboBox<String> cbLop; // Dropdown chọn lớp (cho giáo viên)
    private JLabel lblLop; // Label cho dropdown lớp
    private JComboBox<String> cbMonHoc; // Dropdown chọn môn (cho giáo viên)
    private JLabel lblMonHoc; // Label cho dropdown môn

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLuu;
    private JButton btnHuy;
    private JButton btnSearch;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnRefresh;

    private String currentUser;
    private int userType;
    private boolean isEditing = false;
    private String globalNamHoc = "";
    private int globalHocKy = 1;

    public ModernDiemPanel(String username, int userType) {
        this.currentUser = username;
        this.userType = userType;
        initComponents();
        setupLayout();
        setupPermissions();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Initialize form components
        tfMaSV = createTextField();
        tfMaSV = createTextField();
        tfMaMon = createTextField();
        cbHocKy = new JComboBox<>(new String[] { "1", "2", "3" });
        cbHocKy.setEnabled(false); // Global locked
        tfNamHoc = createTextField();
        tfNamHoc.setEditable(false); // Global locked
        tfDiemCC = createTextField();
        tfDiemGK = createTextField();
        tfDiemCK = createTextField();
        tfDiemTK = createTextField();
        tfDiemTK.setEditable(false);
        tfDiemTK.setBackground(new Color(240, 240, 240));
        tfSearch = createTextField();

        // Style combo box
        cbHocKy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbHocKy.setPreferredSize(new Dimension(200, 35));

        // Add document listeners for automatic calculation
        setupAutoCalculation();

        // Initialize buttons
        btnThem = createButton("Thêm", SUCCESS_COLOR);
        btnSua = createButton("Sửa", PRIMARY_COLOR);
        btnXoa = createButton("Xóa", DANGER_COLOR);
        btnLuu = createButton("Lưu", SUCCESS_COLOR);
        btnHuy = createButton("Hủy", TEXT_SECONDARY);
        btnSearch = createButton("Tìm", PRIMARY_COLOR);
        btnExport = createButton("Xuất Excel", SUCCESS_COLOR);
        btnImport = createButton("Nhập Excel", PRIMARY_COLOR);
        btnRefresh = createButton("Làm mới", TEXT_SECONDARY);

        // Dropdown chọn lớp (cho giáo viên)
        cbLop = new JComboBox<>();
        cbLop.addItem("-- Chọn lớp --");
        cbLop.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLop.setPreferredSize(new Dimension(200, 35));
        lblLop = new JLabel("Chọn lớp:");
        lblLop.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLop.setForeground(TEXT_PRIMARY);

        // Dropdown chọn môn học (cho giáo viên)
        cbMonHoc = new JComboBox<>();
        cbMonHoc.addItem("-- Chọn môn --");
        cbMonHoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbMonHoc.setPreferredSize(new Dimension(200, 35));
        lblMonHoc = new JLabel("Chọn môn học:");
        lblMonHoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMonHoc.setForeground(TEXT_PRIMARY);

        // Initially hide save/cancel buttons
        btnLuu.setVisible(false);
        btnHuy.setVisible(false);

        // Initialize table
        createTable();
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        textField.setPreferredSize(new Dimension(200, 35));
        return textField;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = button.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void createTable() {
        String[] columns;
        if (userType == 2) { // Sinh viên
            columns = new String[] {
                    "Mã Môn", "ĐG Thường Xuyên", "Giữa Kỳ", "Cuối Kỳ", "Tổng Kết", "Xếp Loại"
            };
        } else { // Giáo viên & Admin
            columns = new String[] {
                    "Mã SV", "Tên Sinh Viên", "ĐG Thường Xuyên", "Giữa Kỳ", "Cuối Kỳ", "Tổng Kết", "Xếp Loại"
            };
        }

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        tblDiem = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        tblDiem.setRowSorter(rowSorter);

        // Style table
        tblDiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblDiem.setRowHeight(35);
        tblDiem.setGridColor(new Color(233, 236, 239));
        tblDiem.setSelectionBackground(new Color(63, 81, 181, 50));
        tblDiem.setSelectionForeground(TEXT_PRIMARY);
        tblDiem.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblDiem.getTableHeader().setBackground(new Color(248, 249, 250));
        tblDiem.getTableHeader().setForeground(TEXT_PRIMARY);
        tblDiem.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        int startCenterIndex = (userType == 2) ? 1 : 2; // Student starts at 1, Teacher at 2
        for (int i = startCenterIndex; i < columns.length; i++) {
            tblDiem.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer for grade column (Last column)
        tblDiem.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new GradeCellRenderer());
    }

    private void setupLayout() {
        // Main container
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top panel - Title and toolbar
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel - Split into form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createTablePanel());
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Quản lý Điểm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        // Subtitle based on user role
        String subtitle = "";
        switch (userType) {
            case 0:
                subtitle = "Quản lý toàn bộ điểm sinh viên";
                break;
            case 1:
                subtitle = "Nhập và chỉnh sửa điểm sinh viên";
                break;
            case 2:
                subtitle = "Xem điểm cá nhân của bạn";
                break;
        }

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbarPanel.setBackground(BACKGROUND_COLOR);
        toolbarPanel.add(btnExport);
        toolbarPanel.add(btnImport);

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(toolbarPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        // Form title
        JLabel formTitle = new JLabel("Thông tin điểm");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        // Dropdown chọn lớp (chỉ hiện cho giáo viên)
        if (userType == 1) {
            JPanel lopPanel = new JPanel(new BorderLayout(5, 5));
            lopPanel.setBackground(CARD_COLOR);
            lopPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            lopPanel.add(lblLop, BorderLayout.WEST);
            lopPanel.add(cbLop, BorderLayout.CENTER);
            formPanel.add(lopPanel);
            formPanel.add(Box.createVerticalStrut(10));
        }

        // Form fields
        // Form fields - REORDERED: Year/Semester at TOP
        formPanel.add(createFormField("Năm học:", tfNamHoc));
        formPanel.add(createFormField("Học kỳ:", cbHocKy));
        formPanel.add(createFormField("Mã sinh viên:", tfMaSV));
        formPanel.add(createFormField("Mã môn học:", tfMaMon));
        formPanel.add(createFormField("Điểm đánh giá thường xuyên:", tfDiemCC));
        formPanel.add(createFormField("Điểm giữa kỳ:", tfDiemGK));
        formPanel.add(createFormField("Điểm cuối kỳ:", tfDiemCK));
        formPanel.add(createFormField("Điểm tổng kết:", tfDiemTK));

        formPanel.add(Box.createVerticalStrut(20));

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        buttonPanel.add(btnRefresh);

        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(buttonPanel);

        return formPanel;
    }

    private JPanel createFormField(String label, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(CARD_COLOR);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldLabel.setForeground(TEXT_PRIMARY);
        fieldLabel.setPreferredSize(new Dimension(120, 25));

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

        // Search panel - Use BoxLayout to ensure all components are visible
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(CARD_COLOR);

        // Row 1: Search box
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.setBackground(CARD_COLOR);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchLabel.setForeground(TEXT_PRIMARY);

        tfSearch.setPreferredSize(new Dimension(250, 35));
        btnSearch.setPreferredSize(new Dimension(45, 35));

        searchRow.add(searchLabel);
        searchRow.add(tfSearch);
        searchRow.add(btnSearch);

        // Row 2: Class and Subject dropdowns (for teachers)
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterRow.setBackground(CARD_COLOR);

        filterRow.add(lblLop);
        filterRow.add(cbLop);
        filterRow.add(Box.createHorizontalStrut(15)); // Spacer
        filterRow.add(lblMonHoc);
        filterRow.add(cbMonHoc);

        searchPanel.add(searchRow);
        searchPanel.add(filterRow);

        // Table with scroll
        JScrollPane scrollPane = new JScrollPane(tblDiem);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void setupPermissions() {
        if (userType == 2) { // Sinh viên - chỉ xem
            btnThem.setVisible(false);
            btnSua.setVisible(false);
            btnXoa.setVisible(false);
            btnImport.setVisible(false);
            btnRefresh.setVisible(false); // Hide Refresh button

            // Disable form fields
            tfMaSV.setEditable(false);
            tfMaMon.setEditable(false);
            cbHocKy.setEnabled(false);
            tfNamHoc.setEditable(false);
            tfDiemCC.setEditable(false);
            tfDiemGK.setEditable(false);
            tfDiemCK.setEditable(false);
            tfDiemTK.setEditable(false);
        } else if (userType == 1) { // Giáo viên
            btnThem.setVisible(false);
            btnLuu.setVisible(false);
            btnHuy.setVisible(false);

            // Giáo viên: Edit directly
            tfMaSV.setEditable(false);
            tfMaMon.setEditable(false);
            tfNamHoc.setEditable(false);
            cbHocKy.setEnabled(false);

            // Allow editing scores
            tfDiemCC.setEditable(true);
            tfDiemGK.setEditable(true);
            tfDiemCK.setEditable(true);
        }
    }

    // Custom cell renderer for grade classification
    private class GradeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String grade = (value != null) ? value.toString() : "";
                switch (grade) {
                    case "Xuất sắc":
                        c.setBackground(new Color(40, 167, 69, 50));
                        setForeground(new Color(40, 167, 69));
                        break;
                    case "Giỏi":
                        c.setBackground(new Color(32, 201, 151, 50));
                        setForeground(new Color(32, 201, 151));
                        break;
                    case "Khá":
                        c.setBackground(new Color(255, 193, 7, 50));
                        setForeground(new Color(255, 133, 27));
                        break;
                    case "Trung bình":
                        c.setBackground(new Color(255, 108, 55, 50));
                        setForeground(new Color(255, 108, 55));
                        break;
                    case "Yếu (Kém)":
                        c.setBackground(new Color(220, 53, 69, 50));
                        setForeground(new Color(220, 53, 69));
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        setForeground(TEXT_PRIMARY);
                        break;
                }
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));

            return c;
        }
    }

    // Public methods for data operations
    public void loadTableData(ArrayList<DiemModel> data) {
        tableModel.setRowCount(0);
        for (DiemModel diem : data) {
            Object[] row;
            if (userType == 2) { // Student
                // Columns: "Mã môn", "CC (10%)", "GK (30%)", "CK (60%)", "Tổng kết", "Xếp loại"
                row = new Object[] {
                        diem.getMamon(),
                        String.format("%.1f", diem.getDiemcc()),
                        String.format("%.1f", diem.getDiemgk()),
                        String.format("%.1f", diem.getDiemck()),
                        String.format("%.1f", diem.getDiemtongket()),
                        getGradeClassification(diem.getDiemtongket())
                };
            } else { // Teacher / Admin
                // Columns: "Mã SV", "Tên Sinh Viên", "CC (10%)", "GK (30%)", "CK (60%)", "Tổng
                // kết", "Xếp loại"
                row = new Object[] {
                        diem.getMasv(),
                        diem.getTenSV() != null ? diem.getTenSV() : "",
                        String.format("%.1f", diem.getDiemcc()),
                        String.format("%.1f", diem.getDiemgk()),
                        String.format("%.1f", diem.getDiemck()),
                        String.format("%.1f", diem.getDiemtongket()),
                        getGradeClassification(diem.getDiemtongket())
                };
            }
            tableModel.addRow(row);
        }
    }

    private String getGradeClassification(double score) {
        if (score >= 9.0 && score <= 10.0)
            return "Xuất sắc";
        else if (score >= 8.0 && score < 9.0)
            return "Giỏi";
        else if (score >= 6.5 && score < 8.0)
            return "Khá";
        else if (score >= 5.0 && score < 6.5)
            return "Trung bình";
        else
            return "Yếu (Kém)";
    }

    public void fillForm(DiemModel diem) {
        if (userType == 1) { // Teacher
            // For teacher, these are set globally, but we can display them
            cbHocKy.setSelectedItem(String.valueOf(diem.getHocky()));
            tfNamHoc.setText(diem.getNamhoc());
            // Map student specific fields
            tfMaSV.setText(diem.getMasv());
            tfMaMon.setText(diem.getMamon());

            tfDiemCC.setText(String.valueOf(diem.getDiemcc()));
            tfDiemGK.setText(String.valueOf(diem.getDiemgk()));
            tfDiemCK.setText(String.valueOf(diem.getDiemck()));
            tfDiemTK.setText(String.format("%.2f", diem.getDiemtongket()));
        } else {
            tfMaSV.setText(diem.getMasv());
            tfMaMon.setText(diem.getMamon());
            cbHocKy.setSelectedItem(String.valueOf(diem.getHocky()));
            tfNamHoc.setText(diem.getNamhoc());
            tfDiemCC.setText(String.valueOf(diem.getDiemcc()));
            tfDiemGK.setText(String.valueOf(diem.getDiemgk()));
            tfDiemCK.setText(String.valueOf(diem.getDiemck()));
            tfDiemTK.setText(String.format("%.2f", diem.getDiemtongket()));
        }
    }

    public void setGlobalSettings(String namhoc, int hocky) {
        this.globalNamHoc = namhoc;
        this.globalHocKy = hocky;
        // Update fields if currently empty or just initialized
        if (tfNamHoc.getText().isEmpty()) {
            tfNamHoc.setText(namhoc);
        }
        // Force update regardless? probably yes.
        tfNamHoc.setText(namhoc);
        cbHocKy.setSelectedItem(String.valueOf(hocky));

        // Disable them just in case
        tfNamHoc.setEditable(false);
        cbHocKy.setEnabled(false);
    }

    public void clearForm() {
        tfMaSV.setText("");
        tfMaMon.setText("");
        // Reset to global settings
        cbHocKy.setSelectedItem(String.valueOf(globalHocKy));
        tfNamHoc.setText(globalNamHoc);

        tfDiemCC.setText("");
        tfDiemGK.setText("");
        tfDiemCK.setText("");
        tfDiemTK.setText("");
    }

    public DiemModel getFormData() {
        // Validate form data first
        String validationError = validateFormData();
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        DiemModel diem = new DiemModel();
        // Don't force uppercase - keep original format
        diem.setMasv(tfMaSV.getText().trim());
        diem.setMamon(tfMaMon.getText().trim());

        // Handle values for Teacher
        if (userType == 1) {
            diem.setHocky(globalHocKy);
            String nh = tfNamHoc.getText().trim();
            if (nh.isEmpty())
                nh = globalNamHoc;
            diem.setNamhoc(nh);
        } else {
            diem.setHocky(Integer.parseInt(cbHocKy.getSelectedItem().toString()));
            diem.setNamhoc(tfNamHoc.getText().trim());
        }

        try {
            double cc = Double.parseDouble(tfDiemCC.getText().trim());
            double gk = Double.parseDouble(tfDiemGK.getText().trim());
            double ck = Double.parseDouble(tfDiemCK.getText().trim());

            diem.setDiemcc(cc);
            diem.setDiemgk(gk);
            diem.setDiemck(ck);

            // Auto calculate total score
            diem.setDiemtongket(diem.calculateDiemtongket());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Vui lòng nhập điểm hợp lệ (0-10)");
        }

        return diem;
    }

    public void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    public void setEditingMode(boolean editing) {
        this.isEditing = editing;

        if (userType == 1) {
            // Teacher Mode: enforced visibility
            btnThem.setVisible(false);
            btnSua.setVisible(true);
            btnXoa.setVisible(true);
            btnLuu.setVisible(false);
            btnHuy.setVisible(false);
            return;
        }

        if (userType == 2) {
            // Student: Always hide buttons
            btnThem.setVisible(false);
            btnSua.setVisible(false);
            btnXoa.setVisible(false);
            btnLuu.setVisible(false);
            btnHuy.setVisible(false);
            return;
        }

        // Toggle button visibility
        btnThem.setVisible(!editing);
        btnSua.setVisible(!editing);
        btnXoa.setVisible(!editing);
        btnLuu.setVisible(editing);
        btnHuy.setVisible(editing);

        // Enable/disable form fields
        boolean canEdit = editing && userType != 2;
        tfMaSV.setEditable(canEdit);
        tfMaMon.setEditable(canEdit);
        // cbHocKy & tfNamHoc are ALWAYS locked to global settings
        cbHocKy.setEnabled(false);
        tfNamHoc.setEditable(false);

        tfDiemCC.setEditable(canEdit);
        tfDiemGK.setEditable(canEdit);
        tfDiemCK.setEditable(canEdit);
    }

    // Event handler methods
    public void addActionListener(ActionListener listener) {
        btnThem.addActionListener(listener);
        btnSua.addActionListener(listener);
        btnXoa.addActionListener(listener);
        btnLuu.addActionListener(listener);
        btnHuy.addActionListener(listener);
        btnSearch.addActionListener(listener);
        btnExport.addActionListener(listener);
        btnImport.addActionListener(listener);
        btnRefresh.addActionListener(listener);
    }

    public void addTableMouseListener(MouseListener listener) {
        tblDiem.addMouseListener(listener);
    }

    public JTable getTable() {
        return tblDiem;
    }

    public void updateTotalScore() {
        try {
            double cc = Double.parseDouble(tfDiemCC.getText().trim());
            double gk = Double.parseDouble(tfDiemGK.getText().trim());
            double ck = Double.parseDouble(tfDiemCK.getText().trim());

            double total = (cc + (gk * 2) + (ck * 3)) / 6.0;
            tfDiemTK.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            tfDiemTK.setText("");
        }
    }

    // Methods cho dropdown lớp
    public JComboBox<String> getCbLop() {
        return cbLop;
    }

    public void setLopVisible(boolean visible) {
        lblLop.setVisible(visible);
        cbLop.setVisible(visible);
    }

    public void loadLop(ArrayList<LopModel> listLop) {
        cbLop.removeAllItems();
        cbLop.addItem("-- Chọn lớp --");
        for (LopModel lop : listLop) {
            cbLop.addItem(lop.getMalop() + " - " + lop.getTenlop());
        }
    }

    public String getSelectedLop() {
        String selected = (String) cbLop.getSelectedItem();
        if (selected == null || selected.equals("-- Chọn lớp --")) {
            return null;
        }
        // Lấy mã lớp (phần trước dấu " - ")
        return selected.split(" - ")[0];
    }

    public void addLopItemListener(ItemListener listener) {
        cbLop.addItemListener(listener);
    }

    /**
     * Programmatically select a class by its code
     */
    public void selectLopByCode(String malop) {
        for (int i = 0; i < cbLop.getItemCount(); i++) {
            String item = cbLop.getItemAt(i);
            if (item != null && item.startsWith(malop + " - ")) {
                cbLop.setSelectedIndex(i);
                return;
            }
        }
    }

    // Methods for teacher subject restriction
    public void setTeacherSubject(String mamon, String tenmon) {
        // Hiển thị tên môn học trên UI (có thể thêm label hoặc title)
    }

    // Methods for dynamic subject selection
    public void loadMonHoc(ArrayList<String> subjects) {
        cbMonHoc.removeAllItems();
        cbMonHoc.addItem("-- Chọn môn --");
        for (String s : subjects) {
            cbMonHoc.addItem(s);
        }
    }

    public String getSelectedMonHoc() {
        String selection = (String) cbMonHoc.getSelectedItem();
        if (selection != null && !selection.startsWith("--") && selection.contains(" - ")) {
            return selection.split(" - ")[0]; // Return subject code
        }
        return null;
    }

    public void setMonHocVisible(boolean visible) {
        lblMonHoc.setVisible(visible);
        cbMonHoc.setVisible(visible);
    }

    public void addMonHocChangeListener(java.awt.event.ItemListener listener) {
        cbMonHoc.addItemListener(listener);
    }

    public void lockSubjectField(String mamon, String tenmon) {
        // Khóa trường môn học và set giá trị mặc định
        tfMaMon.setText(mamon);
        tfMaMon.setEditable(false);
        tfMaMon.setBackground(new Color(240, 240, 240));
        tfMaMon.setToolTipText("Giáo viên chỉ được nhập điểm cho môn: " + tenmon);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setLopChangeListener(java.awt.event.ItemListener listener) {
        if (cbLop != null) {
            cbLop.addItemListener(listener);
        }
    }

    public String getSearchKeyword() {
        return tfSearch.getText().trim();
    }

    /**
     * Setup automatic score calculation when user inputs scores
     */
    private void setupAutoCalculation() {
        // Add document listeners to score fields
        tfDiemCC.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }
        });

        tfDiemGK.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }
        });

        tfDiemCK.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalScore();
            }
        });
    }

    /**
     * Enhanced validation for form data
     */
    public String validateFormData() {
        String masv = tfMaSV.getText().trim();
        String mamon = tfMaMon.getText().trim();
        String namhoc = tfNamHoc.getText().trim();
        String diemCC = tfDiemCC.getText().trim();
        String diemGK = tfDiemGK.getText().trim();
        String diemCK = tfDiemCK.getText().trim();

        // Check global fallback for Teacher
        if (userType == 1) {
            if (namhoc.isEmpty())
                namhoc = globalNamHoc;
        }

        // Basic validation
        if (masv.isEmpty())
            return "Vui lòng nhập mã sinh viên";
        if (mamon.isEmpty())
            return "Vui lòng nhập mã môn học";
        if (namhoc == null || namhoc.isEmpty())
            return "Vui lòng nhập năm học";
        if (diemCC.isEmpty())
            return "Vui lòng nhập điểm đánh giá thường xuyên";
        if (diemGK.isEmpty())
            return "Vui lòng nhập điểm giữa kỳ";
        if (diemCK.isEmpty())
            return "Vui lòng nhập điểm cuối kỳ";

        // Validate score ranges
        try {
            double cc = Double.parseDouble(diemCC);
            double gk = Double.parseDouble(diemGK);
            double ck = Double.parseDouble(diemCK);

            if (cc < 0 || cc > 10)
                return "Điểm đánh giá thường xuyên phải từ 0-10";
            if (gk < 0 || gk > 10)
                return "Điểm giữa kỳ phải từ 0-10";
            if (ck < 0 || ck > 10)
                return "Điểm cuối kỳ phải từ 0-10";
        } catch (NumberFormatException e) {
            return "Điểm phải là số hợp lệ";
        }

        // Validate student ID format (allow uppercase, lowercase letters and numbers)
        if (!masv.matches("^[A-Za-z0-9]{3,10}$")) {
            return "Mã sinh viên không hợp lệ: '" + masv + "' (cần 3-10 ký tự, chỉ chữ và số)";
        }

        // Validate subject code format (allow uppercase, lowercase letters and numbers)
        if (!mamon.matches("^[A-Za-z0-9]{2,12}$")) {
            return "Mã môn học không hợp lệ: '" + mamon + "' (cần 2-12 ký tự, chỉ chữ và số)";
        }

        // Validate academic year format
        if (!namhoc.matches("^\\d{4}-\\d{4}$")) {
            return "Năm học không hợp lệ: '" + namhoc + "' (cần định dạng YYYY-YYYY, ví dụ: 2024-2025)";
        }

        return null; // All validations passed
    }

    /**
     * Show validation error message
     */
    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show success message
     */
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Helper to enable/disable score fields specifically (for teachers)
     */
    public void setScoreFieldsEditable(boolean editable) {
        tfDiemCC.setEditable(editable);
        tfDiemGK.setEditable(editable);
        tfDiemCK.setEditable(editable);
    }

    /**
     * Get subject code field for external access (for teacher pre-fill)
     */
    public JTextField getSubjectField() {
        return tfMaMon;
    }

    /**
     * Get academic year field for external access (for teacher pre-fill)
     */
    public JTextField getAcademicYearField() {
        return tfNamHoc;
    }
}