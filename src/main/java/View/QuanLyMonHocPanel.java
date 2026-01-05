package View;

import Model.MonHocModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyMonHocPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    private JTextField tfMaMon;
    private JTextField tfTenMon;

    private JTable tblMonHoc;
    private DefaultTableModel tableModel;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    public QuanLyMonHocPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        tfMaMon = createTextField();
        tfTenMon = createTextField();

        btnThem = createButton("Thêm", SUCCESS_COLOR);
        btnSua = createButton("Sửa", PRIMARY_COLOR);
        btnXoa = createButton("Xóa", DANGER_COLOR);
        btnLamMoi = createButton("Làm mới", Color.GRAY);

        // Update columns - Removed Sotinchi
        String[] columns = { "Mã Môn", "Tên Môn" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMonHoc = new JTable(tableModel);
        tblMonHoc.setRowHeight(30);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(200, 35));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return tf;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void setupLayout() {
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);

        JLabel lblTitle = new JLabel("QUẢN LÝ MÔN HỌC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        topPanel.add(lblTitle, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

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

        JLabel formTitle = new JLabel("Thông tin môn học");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Mã môn học:", tfMaMon));
        formPanel.add(createFormField("Tên môn học:", tfTenMon));
        // Removed sotinchi field

        formPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnPanel.setBackground(CARD_COLOR);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnPanel.add(btnThem);
        btnPanel.add(btnSua);
        btnPanel.add(btnXoa);
        btnPanel.add(btnLamMoi);

        formPanel.add(btnPanel);
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    private JPanel createFormField(String label, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(CARD_COLOR);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldLabel.setPreferredSize(new Dimension(100, 25));

        fieldPanel.add(fieldLabel, BorderLayout.WEST);
        fieldPanel.add(component, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_COLOR);
        wrapper.add(fieldPanel, BorderLayout.CENTER);
        wrapper.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        return wrapper;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JScrollPane scrollPane = new JScrollPane(tblMonHoc);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // Getters
    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnSua() {
        return btnSua;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JTable getTblMonHoc() {
        return tblMonHoc;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public MonHocModel getFormData() {
        // Return default sotinchi = 3 to Controller
        return new MonHocModel(tfMaMon.getText().trim(), tfTenMon.getText().trim(), null, 3);
    }

    public void setFormData(MonHocModel mh) {
        tfMaMon.setText(mh.getMamon());
        tfTenMon.setText(mh.getTenmon());
    }

    public void clearForm() {
        tfMaMon.setText("");
        tfTenMon.setText("");
        tblMonHoc.clearSelection();
    }
}
