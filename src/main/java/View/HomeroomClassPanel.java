package View;

import Model.DiemModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Panel for homeroom teachers to view their class grades
 */
public class HomeroomClassPanel extends JPanel {

    // Colors
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    // Components
    private JLabel lblClassName;
    private JLabel lblStudentCount;
    private JComboBox<String> cbSubject;
    private JTable tblGrades;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnExport;

    private String username;

    public HomeroomClassPanel(String username) {
        this.username = username;
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Class info labels
        lblClassName = new JLabel("Lớp: --");
        lblClassName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClassName.setForeground(PRIMARY_COLOR);

        lblStudentCount = new JLabel("Sĩ số: 0");
        lblStudentCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStudentCount.setForeground(TEXT_SECONDARY);

        // Subject dropdown
        cbSubject = new JComboBox<>();
        cbSubject.addItem("-- Chọn môn học --");
        cbSubject.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSubject.setPreferredSize(new Dimension(300, 35));

        // Buttons
        btnRefresh = createButton("Làm mới", PRIMARY_COLOR);
        btnExport = createButton("Xuất Excel", SUCCESS_COLOR);

        // Table
        String[] columns = { "Mã SV", "Họ tên", "Chuyên cần", "Giữa kỳ", "Cuối kỳ", "Tổng kết", "Xếp loại" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGrades = new JTable(tableModel);
        setupTable();
    }

    private void setupTable() {
        tblGrades.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblGrades.setRowHeight(35);
        tblGrades.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblGrades.getTableHeader().setBackground(new Color(240, 240, 240));
        tblGrades.getTableHeader().setForeground(TEXT_PRIMARY);
        tblGrades.setSelectionBackground(new Color(184, 207, 229)); // Lighter blue for better visibility
        tblGrades.setSelectionForeground(Color.BLACK); // Black text for selected rows
        tblGrades.setGridColor(new Color(230, 230, 230));
        tblGrades.setShowVerticalLines(true);
        tblGrades.setIntercellSpacing(new Dimension(1, 1));

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < 7; i++) {
            tblGrades.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        tblGrades.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblGrades.getColumnModel().getColumn(1).setPreferredWidth(200);
        for (int i = 2; i < 7; i++) {
            tblGrades.getColumnModel().getColumn(i).setPreferredWidth(80);
        }
    }

    private void setupLayout() {
        // Header panel with class info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.add(lblClassName);
        infoPanel.add(lblStudentCount);

        headerPanel.add(infoPanel, BorderLayout.WEST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(CARD_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(10, 20, 10, 20)));

        JLabel lblSubject = new JLabel("Môn học:");
        lblSubject.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(lblSubject);
        filterPanel.add(cbSubject);
        filterPanel.add(btnRefresh);
        filterPanel.add(btnExport);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JScrollPane scrollPane = new JScrollPane(tblGrades);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add to main panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Public methods for controller
    public void setClassInfo(String className, int studentCount) {
        lblClassName.setText("Lớp: " + className);
        lblStudentCount.setText("Sĩ số: " + studentCount + " học sinh");
    }

    public void loadSubjects(ArrayList<String> subjects) {
        cbSubject.removeAllItems();
        cbSubject.addItem("-- Chọn môn học --");
        for (String subject : subjects) {
            cbSubject.addItem(subject);
        }
    }

    public String getSelectedSubject() {
        String selected = (String) cbSubject.getSelectedItem();
        if (selected != null && !selected.startsWith("--") && selected.contains(" - ")) {
            return selected.split(" - ")[0]; // Return subject code
        }
        return null;
    }

    public void loadGradeData(ArrayList<DiemModel> grades) {
        tableModel.setRowCount(0);
        for (DiemModel diem : grades) {
            Object[] row = {
                    diem.getMasv(),
                    getSinhVienName(diem.getMasv()),
                    String.format("%.1f", diem.getDiemcc()),
                    String.format("%.1f", diem.getDiemgk()),
                    String.format("%.1f", diem.getDiemck()),
                    String.format("%.1f", diem.getDiemtongket()),
                    getGradeClassification(diem.getDiemtongket())
            };
            tableModel.addRow(row);
        }
    }

    private String getSinhVienName(String masv) {
        // This will be populated by controller with student data
        return "";
    }

    private String getGradeClassification(double score) {
        if (score >= 8.5)
            return "Giỏi";
        if (score >= 7.0)
            return "Khá";
        if (score >= 5.5)
            return "Trung bình";
        if (score >= 4.0)
            return "Yếu";
        return "Kém";
    }

    public void setStudentNames(ArrayList<String> names) {
        for (int i = 0; i < Math.min(names.size(), tableModel.getRowCount()); i++) {
            tableModel.setValueAt(names.get(i), i, 1);
        }
    }

    public void addSubjectListener(java.awt.event.ActionListener listener) {
        cbSubject.addActionListener(listener);
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JButton getBtnExport() {
        return btnExport;
    }
}
