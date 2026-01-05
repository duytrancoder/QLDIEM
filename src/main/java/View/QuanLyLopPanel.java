package View;

import Model.LopModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Panel quản lý lớp học cho Admin
 */
public class QuanLyLopPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    private JTable tblLop;
    private DefaultTableModel tableModel;

    private JTextField tfMalop;
    private JTextField tfTenlop;

    private JComboBox<String> cbGiaoVienCN;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    public QuanLyLopPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Form fields
        tfMalop = createTextField();
        tfTenlop = createTextField();

        cbGiaoVienCN = new JComboBox<>();

        // Buttons
        btnThem = createButton("Thêm", SUCCESS_COLOR);
        btnSua = createButton("Sửa", PRIMARY_COLOR);
        btnXoa = createButton("Xóa", DANGER_COLOR);
        btnLamMoi = createButton("Làm mới", Color.GRAY);
        // Table

        String[] columns = { "Mã Lớp", "Tên Lớp", "Giáo viên CN" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLop = new JTable(tableModel);
        tblLop.setRowHeight(30);
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

        // Top panel - Title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Quản lý Lớp học");
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

        JLabel formTitle = new JLabel("Thông tin lớp");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Mã lớp:", tfMalop));
        formPanel.add(createFormField("Tên lớp:", tfTenlop));

        formPanel.add(createFormField("Giáo viên CN:", cbGiaoVienCN));

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

        JScrollPane scrollPane = new JScrollPane(tblLop);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // Public methods
    public void loadTableData(ArrayList<LopModel> data) {
        tableModel.setRowCount(0);
        for (LopModel lop : data) {
            Object[] row = {
                    lop.getMalop(),
                    lop.getTenlop(),
                    // lop.getMakhoa(), // Removed
                    lop.getMagvcn()
            };
            tableModel.addRow(row);
        }
    }

    public void loadGiaoVien(ArrayList<String> listGV) {
        cbGiaoVienCN.removeAllItems();
        cbGiaoVienCN.addItem("-- Chọn giáo viên --");
        for (String gv : listGV) {
            cbGiaoVienCN.addItem(gv);
        }
    }

    public LopModel getFormData() {
        LopModel lop = new LopModel();
        lop.setMalop(tfMalop.getText().trim());
        lop.setTenlop(tfTenlop.getText().trim());

        // Get giao vien
        String gvcn = (String) cbGiaoVienCN.getSelectedItem();
        if (gvcn != null && !gvcn.equals("-- Chọn giáo viên --")) {
            lop.setMagvcn(gvcn.split(" - ")[0]); // Lấy mã GV
        }

        return lop;
    }

    public void fillForm(LopModel lop) {
        tfMalop.setText(lop.getMalop());
        tfTenlop.setText(lop.getTenlop());

        // Find and select giao vien
        String magvcn = lop.getMagvcn();
        if (magvcn != null) {
            for (int i = 0; i < cbGiaoVienCN.getItemCount(); i++) {
                String item = cbGiaoVienCN.getItemAt(i);
                if (item.startsWith(magvcn + " - ")) {
                    cbGiaoVienCN.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbGiaoVienCN.setSelectedIndex(0);
        }
    }

    public void clearForm() {
        tfMalop.setText("");
        tfTenlop.setText("");

        cbGiaoVienCN.setSelectedIndex(0);
    }

    public void addActionListener(ActionListener listener) {
        btnThem.addActionListener(listener);
        btnSua.addActionListener(listener);
        btnXoa.addActionListener(listener);
        btnLamMoi.addActionListener(listener);
    }

    public void addTableMouseListener(MouseListener listener) {
        tblLop.addMouseListener(listener);
    }

    public JTable getTable() {
        return tblLop;
    }
}
