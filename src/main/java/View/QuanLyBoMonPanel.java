package View;

import Model.BoMonModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class QuanLyBoMonPanel extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    private JTextField tfMaBoMon;
    private JTextField tfTenBoMon;

    // Checkbox List Components
    private JList<CheckListItem> listSubjects;
    private DefaultListModel<CheckListItem> listModelSubjects;

    private JTable tblBoMon;
    private DefaultTableModel tableModelBoMon;

    private JButton btnThemBM;
    private JButton btnSuaBM;
    private JButton btnXoaBM;
    private JButton btnLamMoiBM;

    public QuanLyBoMonPanel() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Form Fields
        tfMaBoMon = createTextField();
        tfTenBoMon = createTextField();

        // Init Checkbox List
        listModelSubjects = new DefaultListModel<>();
        listSubjects = new JList<>(listModelSubjects);
        listSubjects.setCellRenderer(new CheckListRenderer());
        listSubjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add mouse listener for toggling checks
        listSubjects.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                JList<CheckListItem> list = (JList<CheckListItem>) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                if (index >= 0) {
                    CheckListItem item = list.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected());
                    list.repaint(list.getCellBounds(index, index));
                }
            }
        });

        // Buttons
        btnThemBM = createButton("Thêm", SUCCESS_COLOR);
        btnSuaBM = createButton("Sửa", PRIMARY_COLOR);
        btnXoaBM = createButton("Xóa", DANGER_COLOR);
        btnLamMoiBM = createButton("Làm mới", Color.GRAY);

        // Table
        String[] columns = { "Mã BM", "Tên Bộ Môn", "Các Môn Giảng Dạy" };
        tableModelBoMon = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBoMon = new JTable(tableModelBoMon);
        tblBoMon.setRowHeight(30);
        tblBoMon.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblBoMon.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblBoMon.getColumnModel().getColumn(2).setPreferredWidth(400);
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

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        JLabel lblTitle = new JLabel("QUẢN LÝ BỘ MÔN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Center Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createTablePanel());
        splitPane.setDividerLocation(380);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel("Thông tin bộ môn");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Mã bộ môn:", tfMaBoMon));
        formPanel.add(createFormField("Tên bộ môn:", tfTenBoMon));

        // Subject List
        JLabel lblSubjects = new JLabel("Chọn môn học (Tick chọn):");
        lblSubjects.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubjects.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblSubjects);
        formPanel.add(Box.createVerticalStrut(5));

        JScrollPane scrollSubjects = new JScrollPane(listSubjects);
        scrollSubjects.setPreferredSize(new Dimension(300, 150));
        scrollSubjects.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scrollSubjects.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(scrollSubjects);

        formPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnPanel.setBackground(CARD_COLOR);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnPanel.add(btnThemBM);
        btnPanel.add(btnSuaBM);
        btnPanel.add(btnXoaBM);
        btnPanel.add(btnLamMoiBM);

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
        JScrollPane scrollPane = new JScrollPane(tblBoMon);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    // --- Helpers ---
    public JButton getBtnThemBM() {
        return btnThemBM;
    }

    public JButton getBtnSuaBM() {
        return btnSuaBM;
    }

    public JButton getBtnXoaBM() {
        return btnXoaBM;
    }

    public JButton getBtnLamMoiBM() {
        return btnLamMoiBM;
    }

    public JTable getTblBoMon() {
        return tblBoMon;
    }

    public BoMonModel getFormData() {
        String mabomon = tfMaBoMon.getText().trim();
        String tenbomon = tfTenBoMon.getText().trim();

        // Collect checked items
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listModelSubjects.getSize(); i++) {
            CheckListItem item = listModelSubjects.getElementAt(i);
            if (item.isSelected()) {
                String val = item.toString();
                if (val.contains(" - ")) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(val.split(" - ")[0]);
                }
            }
        }
        return new BoMonModel(mabomon, tenbomon, sb.toString());
    }

    public void setFormData(BoMonModel bm) {
        tfMaBoMon.setText(bm.getMabomon());
        tfTenBoMon.setText(bm.getTenbomon());

        // Reset and check items
        String csv = bm.getCacMon();
        // First uncheck all
        for (int i = 0; i < listModelSubjects.getSize(); i++) {
            listModelSubjects.getElementAt(i).setSelected(false);
        }

        if (csv != null && !csv.isEmpty()) {
            String[] codes = csv.split(",");
            for (String code : codes) {
                code = code.trim();
                for (int i = 0; i < listModelSubjects.getSize(); i++) {
                    CheckListItem item = listModelSubjects.getElementAt(i);
                    if (item.toString().startsWith(code + " - ")) {
                        item.setSelected(true);
                        break;
                    }
                }
            }
        }
        listSubjects.repaint();
    }

    public void clearForm() {
        tfMaBoMon.setText("");
        tfTenBoMon.setText("");
        for (int i = 0; i < listModelSubjects.getSize(); i++) {
            listModelSubjects.getElementAt(i).setSelected(false);
        }
        listSubjects.repaint();
        tblBoMon.clearSelection();
    }

    public void loadSubjects(ArrayList<String> subjects) {
        listModelSubjects.clear();
        for (String s : subjects) {
            listModelSubjects.addElement(new CheckListItem(s));
        }
    }

    public void loadTableBoMon(ArrayList<BoMonModel> list) {
        tableModelBoMon.setRowCount(0);
        for (BoMonModel bm : list) {
            tableModelBoMon.addRow(new Object[] { bm.getMabomon(), bm.getTenbomon(), bm.getCacMon() });
        }
    }

    // --- Inner Classes for Checkbox List ---
    // REMOVED: Using top-level View.CheckListItem and View.CheckListRenderer
}
