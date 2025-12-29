package View;

import Model.GiaoVienModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Panel quản lý giáo viên cho Admin
 */
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
    private JTextField tfNgaysinh;
    private JTextField tfEmail;
    private JTextField tfSdt;
    private JComboBox<String> cbKhoa;
    private JComboBox<String> cbMonHoc;
    private JTextField tfUsername;
    
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLuu;
    private JButton btnHuy;
    
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
        cbGioitinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        tfNgaysinh = createTextField();
        tfEmail = createTextField();
        tfSdt = createTextField();
        cbKhoa = new JComboBox<>();
        cbMonHoc = new JComboBox<>();
        tfUsername = createTextField();
        
        // Buttons
        btnThem = createButton("Thêm", SUCCESS_COLOR);
        btnSua = createButton("Sửa", PRIMARY_COLOR);
        btnXoa = createButton("Xóa", DANGER_COLOR);
        btnLuu = createButton("Lưu", SUCCESS_COLOR);
        btnHuy = createButton("Hủy", Color.GRAY);
        
        btnLuu.setVisible(false);
        btnHuy.setVisible(false);
        
        // Table
        String[] columns = {"Mã GV", "Họ tên", "Giới tính", "Ngày sinh", "Email", "SĐT", "Khoa", "Môn học", "Username"};
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
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel formTitle = new JLabel("Thông tin giáo viên");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));
        
        formPanel.add(createFormField("Mã GV:", tfMagv));
        formPanel.add(createFormField("Họ tên:", tfHoten));
        formPanel.add(createFormField("Giới tính:", cbGioitinh));
        formPanel.add(createFormField("Ngày sinh:", tfNgaysinh));
        formPanel.add(createFormField("Email:", tfEmail));
        formPanel.add(createFormField("SĐT:", tfSdt));
        formPanel.add(createFormField("Khoa:", cbKhoa));
        formPanel.add(createFormField("Môn học:", cbMonHoc));
        formPanel.add(createFormField("Username:", tfUsername));
        
        formPanel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
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
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JScrollPane scrollPane = new JScrollPane(tblGiaoVien);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    // Public methods
    public void loadTableData(ArrayList<GiaoVienModel> data) {
        tableModel.setRowCount(0);
        for (GiaoVienModel gv : data) {
            Object[] row = {
                gv.getMagv(),
                gv.getHoten(),
                gv.getGioitinh(),
                gv.getNgaysinh(),
                gv.getEmail(),
                gv.getSdt(),
                gv.getMakhoa(),
                gv.getMamon(),
                gv.getUsername()
            };
            tableModel.addRow(row);
        }
    }
    
    public void loadKhoa(ArrayList<String> listKhoa) {
        cbKhoa.removeAllItems();
        for (String khoa : listKhoa) {
            cbKhoa.addItem(khoa);
        }
    }
    
    public void loadMonHoc(ArrayList<String> listMonHoc) {
        cbMonHoc.removeAllItems();
        for (String mon : listMonHoc) {
            cbMonHoc.addItem(mon);
        }
    }
    
    public GiaoVienModel getFormData() {
        GiaoVienModel gv = new GiaoVienModel();
        gv.setMagv(tfMagv.getText().trim());
        gv.setHoten(tfHoten.getText().trim());
        gv.setGioitinh((String) cbGioitinh.getSelectedItem());
        gv.setNgaysinh(tfNgaysinh.getText().trim());
        gv.setEmail(tfEmail.getText().trim());
        gv.setSdt(tfSdt.getText().trim());
        
        // Get khoa
        String khoaSelection = (String) cbKhoa.getSelectedItem();
        gv.setMakhoa(khoaSelection);
        
        // Get mon hoc - extract mã môn từ selection "MH01 - Toán"
        String monSelection = (String) cbMonHoc.getSelectedItem();
        if (monSelection != null && monSelection.contains(" - ")) {
            gv.setMamon(monSelection.split(" - ")[0]); // Lấy mã môn
        } else {
            gv.setMamon(monSelection); // Nếu không có format thì dùng trực tiếp
        }
        
        gv.setUsername(tfUsername.getText().trim());
        return gv;
    }
    
    public void fillForm(GiaoVienModel gv) {
        tfMagv.setText(gv.getMagv() != null ? gv.getMagv() : "");
        tfHoten.setText(gv.getHoten() != null ? gv.getHoten() : "");
        cbGioitinh.setSelectedItem(gv.getGioitinh() != null ? gv.getGioitinh() : "Nam");
        tfNgaysinh.setText(gv.getNgaysinh() != null ? gv.getNgaysinh() : "");
        tfEmail.setText(gv.getEmail() != null ? gv.getEmail() : "");
        tfSdt.setText(gv.getSdt() != null ? gv.getSdt() : "");
        
        // Set khoa
        if (gv.getMakhoa() != null) {
            cbKhoa.setSelectedItem(gv.getMakhoa());
        } else {
            cbKhoa.setSelectedIndex(-1);
        }
        
        // Set môn học - tìm và select đúng item
        if (gv.getMamon() != null) {
            boolean found = false;
            for (int i = 0; i < cbMonHoc.getItemCount(); i++) {
                String item = cbMonHoc.getItemAt(i);
                if (item != null && item.startsWith(gv.getMamon() + " - ")) {
                    cbMonHoc.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cbMonHoc.setSelectedIndex(-1);
            }
        } else {
            cbMonHoc.setSelectedIndex(-1);
        }
        
        tfUsername.setText(gv.getUsername() != null ? gv.getUsername() : "");
    }
    
    public void clearForm() {
        tfMagv.setText("");
        tfHoten.setText("");
        cbGioitinh.setSelectedIndex(0);
        tfNgaysinh.setText("");
        tfEmail.setText("");
        tfSdt.setText("");
        cbKhoa.setSelectedIndex(-1);
        cbMonHoc.setSelectedIndex(-1);
        tfUsername.setText("");
    }
    
    public void setEditingMode(boolean editing) {
        btnThem.setVisible(!editing);
        btnSua.setVisible(!editing);
        btnXoa.setVisible(!editing);
        btnLuu.setVisible(editing);
        btnHuy.setVisible(editing);
    }
    
    public void addActionListener(ActionListener listener) {
        btnThem.addActionListener(listener);
        btnSua.addActionListener(listener);
        btnXoa.addActionListener(listener);
        btnLuu.addActionListener(listener);
        btnHuy.addActionListener(listener);
    }
    
    public void addTableMouseListener(MouseListener listener) {
        tblGiaoVien.addMouseListener(listener);
    }
    
    public JTable getTable() {
        return tblGiaoVien;
    }
}

