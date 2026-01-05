package View;

import Model.GiaoVienModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class QuanLyGiaoVienPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    private JTable tblGiaoVien;
    private DefaultTableModel tableModel;

    private JTextField tfMagv;
    private JTextField tfHoten;
    private JComboBox<String> cbGioitinh;
    private JTextField dpNgaysinh;
    private JTextField tfEmail;
    private JTextField tfSdt;

    private JComboBox<String> cbBoMon;
    private JList<CheckListItem> listMonHoc; // Checkbox List
    private DefaultListModel<CheckListItem> listModelMonHoc;
    private JScrollPane scrollMonHoc;

    private JTextField tfSearch;
    private JButton btnTimKiem;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private JLabel lblMonHocHint;

    public QuanLyGiaoVienPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Form fields
        tfMagv = createTextField();
        tfHoten = createTextField();
        cbGioitinh = new JComboBox<>(new String[] { "Nam", "Nữ" });

        dpNgaysinh = new JTextField();
        dpNgaysinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dpNgaysinh.setPreferredSize(new Dimension(200, 35));
        tfEmail = createTextField();
        tfSdt = createTextField();

        cbBoMon = new JComboBox<>();
        cbBoMon.setPreferredSize(new Dimension(200, 35));

        listModelMonHoc = new DefaultListModel<>();
        listMonHoc = new JList<>(listModelMonHoc);
        listMonHoc.setCellRenderer(new CheckListRenderer());
        listMonHoc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Mouse Listener for toggling
        listMonHoc.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                JList<CheckListItem> list = (JList<CheckListItem>) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                if (index >= 0) {
                    CheckListItem item = list.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected());
                    list.repaint(list.getCellBounds(index, index));
                }
            }
        });

        scrollMonHoc = new JScrollPane(listMonHoc);
        scrollMonHoc.setPreferredSize(new Dimension(200, 150)); // Taller

        lblMonHocHint = new JLabel("Click để chọn/bỏ chọn môn");
        lblMonHocHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblMonHocHint.setForeground(Color.GRAY);

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
        String[] columns = { "Mã GV", "Họ tên", "Giới tính", "Ngày sinh", "Email", "SĐT", "Bộ Môn", "Môn dạy" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGiaoVien = new JTable(tableModel);
        tblGiaoVien.setRowHeight(30);
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

        JLabel titleLabel = new JLabel("Quản lý Giáo viên");
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
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel("Thông tin giáo viên");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Mã GV:", tfMagv));
        formPanel.add(createFormField("Họ tên:", tfHoten));
        formPanel.add(createFormField("Giới tính:", cbGioitinh));
        formPanel.add(createFormField("Ngày sinh:", dpNgaysinh));
        formPanel.add(createFormField("Email:", tfEmail));
        formPanel.add(createFormField("SĐT:", tfSdt));

        formPanel.add(createFormField("Bộ môn:", cbBoMon));
        formPanel.add(createFormFieldList("Môn dạy:", scrollMonHoc));

        JPanel pnlHint = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlHint.setBackground(CARD_COLOR);
        pnlHint.add(lblMonHocHint);
        formPanel.add(pnlHint);

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

    private JPanel createFormFieldList(String label, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(CARD_COLOR);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Taller for List

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

        JScrollPane scrollPane = new JScrollPane(tblGiaoVien);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    public void loadTableData(ArrayList<GiaoVienModel> data) {
        tableModel.setRowCount(0);
        for (GiaoVienModel gv : data) {
            Object[] row = {
                    gv.getMagv(),
                    gv.getHoten(),
                    gv.getGioitinh(),
                    formatDate(gv.getNgaysinh()),
                    gv.getEmail(),
                    gv.getSdt(),
                    gv.getTenbomon(), // Show Dept Name
                    gv.getTenCacMon() // Show comma separated Subjects
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

    public void loadBoMon(ArrayList<String> listBoMon) {
        cbBoMon.removeAllItems();
        cbBoMon.addItem("-- Chọn Bộ Môn --");
        for (String bm : listBoMon) {
            cbBoMon.addItem(bm);
        }
    }

    public void loadMonHoc(ArrayList<String> subjects) {
        listModelMonHoc.clear();
        for (String mon : subjects) {
            listModelMonHoc.addElement(new CheckListItem(mon));
        }
    }

    public JComboBox<String> getCbBoMon() {
        return cbBoMon;
    }

    public GiaoVienModel getFormData() {
        GiaoVienModel gv = new GiaoVienModel();
        gv.setMagv(tfMagv.getText().trim());
        gv.setHoten(tfHoten.getText().trim());
        gv.setGioitinh((String) cbGioitinh.getSelectedItem());
        String dateStr = dpNgaysinh.getText().trim();
        gv.setNgaysinh(dateStr);
        gv.setEmail(tfEmail.getText().trim());
        gv.setSdt(tfSdt.getText().trim());

        // Get Bo Mon
        String bmSel = (String) cbBoMon.getSelectedItem();
        if (bmSel != null && bmSel.contains(" - ")) {
            gv.setMabomon(bmSel.split(" - ")[0]);
        }

        // Get Mon Hocs
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listModelMonHoc.getSize(); i++) {
            CheckListItem item = listModelMonHoc.getElementAt(i);
            if (item.isSelected()) {
                String val = item.toString();
                if (val.contains(" - ")) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(val.split(" - ")[0]);
                }
            }
        }
        gv.setCacMon(sb.toString());

        return gv;
    }

    public void fillForm(GiaoVienModel gv) {
        tfMagv.setText(gv.getMagv() != null ? gv.getMagv() : "");
        tfHoten.setText(gv.getHoten() != null ? gv.getHoten() : "");
        cbGioitinh.setSelectedItem(gv.getGioitinh() != null ? gv.getGioitinh() : "Nam");
        String ngaysinh = gv.getNgaysinh();
        if (ngaysinh != null && !ngaysinh.isEmpty()) {
            dpNgaysinh.setText(ngaysinh);
        } else {
            dpNgaysinh.setText("");
        }
        tfEmail.setText(gv.getEmail() != null ? gv.getEmail() : "");
        tfSdt.setText(gv.getSdt() != null ? gv.getSdt() : "");

        // Select Bo Mon
        if (gv.getMabomon() != null) {
            for (int i = 0; i < cbBoMon.getItemCount(); i++) {
                String item = cbBoMon.getItemAt(i);
                if (item.startsWith(gv.getMabomon() + " - ")) {
                    cbBoMon.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbBoMon.setSelectedIndex(0);
        }

        // Select Mon Hocs
        // Note: Controller will load subjects into listMonHoc based on cbBoMon
        // selection
        // We need to wait for list to populate? or list is populated because fillForm
        // is called after selecting row
        // which triggers controller to load data. Use setSelectedIndices
        // This is tricky. Controller should handle selection after list load.
        // We will store the "subjects to select" in a temporary property or simply
        // return them via logic.
        // Here we can't easily select if data isn't loaded.
        // The controller should handle "Fill Form" -> "Set Bo Mon" -> "Load Subjects"
        // -> "Select Subjects".
    }

    // Helper to select subjects roughly
    public void setSelectedSubjects(String csvSubjects) {
        // First uncheck all
        for (int i = 0; i < listModelMonHoc.getSize(); i++) {
            listModelMonHoc.getElementAt(i).setSelected(false);
        }

        if (csvSubjects == null || csvSubjects.isEmpty()) {
            listMonHoc.repaint();
            return;
        }

        String[] codes = csvSubjects.split(",");
        for (String code : codes) {
            code = code.trim();
            for (int i = 0; i < listModelMonHoc.getSize(); i++) {
                CheckListItem item = listModelMonHoc.getElementAt(i);
                if (item.toString().startsWith(code + " - ")) {
                    item.setSelected(true);
                    break;
                }
            }
        }
        listMonHoc.repaint();
    }

    public void clearForm() {
        tfMagv.setText("");
        tfHoten.setText("");
        cbGioitinh.setSelectedIndex(0);
        dpNgaysinh.setText("");
        tfEmail.setText("");
        tfSdt.setText("");
        cbBoMon.setSelectedIndex(0);
        // listMonHoc will be cleared or reloaded by controller
        if (listModelMonHoc != null)
            listModelMonHoc.clear();
        tblGiaoVien.clearSelection();
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
        tblGiaoVien.addMouseListener(listener);
    }

    public JTable getTable() {
        return tblGiaoVien;
    }
}
