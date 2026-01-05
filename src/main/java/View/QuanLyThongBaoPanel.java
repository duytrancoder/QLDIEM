package View;

import Model.ThongBaoModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class QuanLyThongBaoPanel extends JPanel {

    private JTextField tfTieude;
    private JTextArea taNoidung;
    private JComboBox<String> cbLoai;
    private JComboBox<String> cbPhamvi; // List of Classes for Teacher

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private JTable tblThongBao;
    private DefaultTableModel tableModel;

    private int userType; // 0=Admin, 1=Teacher

    private JPanel pnlPhamVi; // Container for checkboxes
    private ArrayList<JCheckBox> listCheckBoxes; // List of checkboxes for classes

    public QuanLyThongBaoPanel(int userType) {
        this.userType = userType;
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setBackground(new Color(250, 250, 250));
        setLayout(new BorderLayout());

        // Form Fields
        tfTieude = new JTextField();
        tfTieude.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        taNoidung = new JTextArea();
        taNoidung.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taNoidung.setRows(5);
        taNoidung.setLineWrap(true);
        taNoidung.setWrapStyleWord(true);

        cbLoai = new JComboBox<>();
        cbLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (userType == 0) { // Admin
            cbPhamvi = new JComboBox<>();
            cbPhamvi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cbLoai.addItem("TOAN_TRUONG");
            cbLoai.setEnabled(false);
            cbPhamvi.addItem("Toàn trường");
            cbPhamvi.setEnabled(false);
        } else { // Teacher
            cbLoai.addItem("LOP");
            cbLoai.setEnabled(false);

            // Init Checkbox container
            pnlPhamVi = new JPanel();
            pnlPhamVi.setLayout(new BoxLayout(pnlPhamVi, BoxLayout.Y_AXIS));
            pnlPhamVi.setBackground(Color.WHITE);
            listCheckBoxes = new ArrayList<>();
        }

        // Buttons
        btnThem = createButton("Đăng bài", new Color(40, 167, 69));
        btnSua = createButton("Sửa", new Color(63, 81, 181));
        btnXoa = createButton("Xóa", new Color(220, 53, 69));
        btnLamMoi = createButton("Làm mới", Color.GRAY);

        // Table
        // Table
        String[] columns;
        if (userType == 0) { // Admin
            columns = new String[] { "Tiêu đề", "Mã giáo viên", "Người gửi", "Gửi tới", "Ngày gửi" };
        } else { // Teacher
            columns = new String[] { "Tiêu đề", "Gửi tới", "Ngày gửi" };
        }

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThongBao = new JTable(tableModel);
        tblThongBao.setRowHeight(30);
        tblThongBao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblThongBao.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Hide "Người gửi" column if Teacher?
        // User said: "In teacher page... list classes sent to".
        // Teacher knows they sent it. But Admin needs to see Sender.
        // Let's keep Sender for now.
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void setupLayout() {
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top: Title
        JLabel title = new JLabel(userType == 0 ? "Bảng tin Nhà trường (Admin)" : "Quản lý Thông báo Lớp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(63, 81, 181));
        add(title, BorderLayout.NORTH);

        // Center: Split Form & Table
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(createFormPanel());
        split.setRightComponent(createTablePanel());
        split.setDividerLocation(400);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(Color.WHITE);

        fields.add(createLabeledField("Tiêu đề:", tfTieude));
        fields.add(createLabeledField("Nội dung:", new JScrollPane(taNoidung)));

        if (userType == 1) { // Teacher: Multi-select Checkboxes
            JScrollPane scrollChecks = new JScrollPane(pnlPhamVi);
            scrollChecks.setPreferredSize(new Dimension(200, 150));
            scrollChecks.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            fields.add(createLabeledField("Gửi tới lớp (Chọn nhiều):", scrollChecks));
        } else { // Admin
            fields.add(createLabeledField("Phạm vi:", cbPhamvi));
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setBackground(Color.WHITE);
        btns.add(btnThem);
        btns.add(btnSua);
        btns.add(btnXoa);
        btns.add(btnLamMoi);

        p.add(fields, BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        p.add(new JScrollPane(tblThongBao), BorderLayout.CENTER);
        return p;
    }

    private JPanel createLabeledField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        // Adjust height flexibility
        if (comp instanceof JScrollPane) {
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            p.setPreferredSize(new Dimension(300, 150));
        } else {
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            p.setPreferredSize(new Dimension(300, 40));
        }
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setPreferredSize(new Dimension(80, 25));

        p.add(l, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public void loadTableData(ArrayList<ThongBaoModel> list) {
        tableModel.setRowCount(0);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (ThongBaoModel t : list) {
            String phamViDisplay;
            if ("TOAN_TRUONG".equals(t.getLoai())) {
                phamViDisplay = "Toàn trường";
            } else {
                phamViDisplay = t.getPhamvi(); // Should contain aggregated list for Teachers
            }

            if (userType == 0) { // Admin columns
                String maGV = "";
                // If sender is NOT admin, show ID. Assume "admin" is the only non-teacher.
                if (!"admin".equalsIgnoreCase(t.getNguoigui())) {
                    maGV = t.getNguoigui();
                }

                tableModel.addRow(new Object[] {
                        t.getTieude(),
                        maGV,
                        t.getTenNguoiGui() != null ? t.getTenNguoiGui() : t.getNguoigui(),
                        phamViDisplay,
                        t.getNgaygui() != null ? sdf.format(t.getNgaygui()) : ""
                });
            } else { // Teacher columns
                tableModel.addRow(new Object[] {
                        t.getTieude(),
                        phamViDisplay,
                        t.getNgaygui() != null ? sdf.format(t.getNgaygui()) : ""
                });
            }
        }
    }

    public void fillForm(ThongBaoModel t) {
        tfTieude.setText(t.getTieude());
        taNoidung.setText(t.getNoidung());

        if (userType == 1 && t.getPhamvi() != null) {
            // For editing, we select the checkbox matching the Scope.
            // Note: Since editing is per-record, we just check the ONE relevant box?
            // Or if design allows editing multiple? No, simpler to just View.
            // Let's clear all and check the one matching.
            for (JCheckBox cb : listCheckBoxes) {
                cb.setSelected(cb.getText().startsWith(t.getPhamvi()));
            }
        }
    }

    public void clearForm() {
        tfTieude.setText("");
        taNoidung.setText("");
        if (userType == 1) {
            for (JCheckBox cb : listCheckBoxes)
                cb.setSelected(false);
        } else {
            if (cbPhamvi.getItemCount() > 0)
                cbPhamvi.setSelectedIndex(0);
        }
    }

    // Getters
    public ThongBaoModel getFormData() {
        ThongBaoModel t = new ThongBaoModel();
        t.setTieude(tfTieude.getText().trim());
        t.setNoidung(taNoidung.getText().trim());
        t.setTrangThai("HIEN");

        if (userType == 0) {
            t.setLoai("TOAN_TRUONG");
            t.setPhamvi(null);
        } else {
            t.setLoai("LOP");
            // Phamvi will be handled by Controller iterating getSelectedClasses()
            // We can return null here or the first selected.
            // Controller should use getSelectedClasses()
        }
        return t;
    }

    public ArrayList<String> getSelectedClasses() {
        ArrayList<String> selected = new ArrayList<>();
        if (userType == 1) {
            for (JCheckBox cb : listCheckBoxes) {
                if (cb.isSelected()) {
                    // Assuming format "MaLop - TenLop"
                    selected.add(cb.getText().split(" - ")[0]);
                }
            }
        }
        return selected;
    }

    public void setClassList(ArrayList<String> classes) {
        if (userType == 1) {
            pnlPhamVi.removeAll();
            listCheckBoxes.clear();
            for (String c : classes) {
                JCheckBox cb = new JCheckBox(c);
                cb.setBackground(Color.WHITE);
                cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                pnlPhamVi.add(cb);
                listCheckBoxes.add(cb);
            }
            pnlPhamVi.revalidate();
            pnlPhamVi.repaint();
        } else {
            cbPhamvi.removeAllItems();
            for (String c : classes)
                cbPhamvi.addItem(c);
        }
    }

    public JTable getTable() {
        return tblThongBao;
    }

    public void addActionListener(ActionListener l) {
        btnThem.addActionListener(l);
        btnSua.addActionListener(l);
        btnXoa.addActionListener(l);
        btnLamMoi.addActionListener(l);
    }

    public void addTableMouseListener(MouseListener l) {
        tblThongBao.addMouseListener(l);
    }
}
