package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KhoaSoNienKhoaPanel extends JPanel {

    private JTextField tfNamHoc;
    private JComboBox<String> cbHocKy;
    private JButton btnLuu;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);

    private int userType = 0; // Default Admin

    public KhoaSoNienKhoaPanel() {
        this(0);
    }

    public KhoaSoNienKhoaPanel(int userType) {
        this.userType = userType;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(BACKGROUND_COLOR);
        JLabel title = new JLabel("Cấu Hình Niên Khóa & Học Kỳ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Nam Hoc
        JLabel lblNamHoc = new JLabel("Năm Học:");
        lblNamHoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(lblNamHoc, gbc);

        tfNamHoc = new JTextField(20);
        tfNamHoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfNamHoc.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(tfNamHoc, gbc);

        // Hoc Ky
        JLabel lblHocKy = new JLabel("Học Kỳ:");
        lblHocKy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(lblHocKy, gbc);

        cbHocKy = new JComboBox<>(new String[] { "1", "2", "3" });
        cbHocKy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbHocKy.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(cbHocKy, gbc);

        // Save Button
        btnLuu = new JButton("Lưu Cấu Hình");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(SUCCESS_COLOR);
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.setPreferredSize(new Dimension(150, 40));

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        contentPanel.add(btnLuu, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Add wrapper to center content card
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(BACKGROUND_COLOR);
        wrapper.add(contentPanel);
        add(wrapper, BorderLayout.CENTER);

        // Read-only logic for non-admins
        if (userType != 0) {
            tfNamHoc.setEditable(false);
            cbHocKy.setEnabled(false);
            btnLuu.setVisible(false);
            title.setText("Thông Tin Niên Khóa & Học Kỳ (Xem)");
        }
    }

    public void addSaveListener(java.awt.event.ActionListener listener) {
        btnLuu.addActionListener(listener);
    }

    public String getNamHoc() {
        return tfNamHoc.getText().trim();
    }

    public void setNamHoc(String namHoc) {
        tfNamHoc.setText(namHoc);
    }

    public int getHocKy() {
        try {
            return Integer.parseInt((String) cbHocKy.getSelectedItem());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void setHocKy(int hocKy) {
        cbHocKy.setSelectedItem(String.valueOf(hocKy));
    }
}
