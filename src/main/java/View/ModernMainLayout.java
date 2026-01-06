package View;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * MainLayout hiện đại với Material Design
 */
public class ModernMainLayout extends JFrame {

    // Colors - Material Design inspired
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181); // Indigo
    private static final Color PRIMARY_DARK = new Color(48, 63, 159); // Dark Indigo
    private static final Color ACCENT_COLOR = new Color(255, 64, 129); // Pink
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250); // Light Gray
    private static final Color SIDEBAR_COLOR = new Color(33, 37, 41); // Dark Gray
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JPanel sidebarPanel;
    private JLabel userInfoLabel;
    private JLabel roleLabel;

    // Menu buttons
    private ModernButton btnDashboard;
    private ModernButton btnDiem;
    private ModernButton btnSinhVien;
    private ModernButton btnGiaoVien;
    private ModernButton btnLop;
    private ModernButton btnQuanLyLop;
    private ModernButton btnQuanLyBoMon; // New Button
    private ModernButton btnKhoaSo;
    private ModernButton btnThongBao;
    private ModernButton btnHomeroomClass; // Homeroom button for teachers

    private ModernButton btnMonHoc;
    private ModernButton btnBaoCao;
    private ModernButton btnCaiDat;
    private ModernButton btnDangXuat;

    private String currentUser;
    private int userType;

    public ModernMainLayout(String username, int userType) {
        this.currentUser = username;
        this.userType = userType;
        initComponents();
        setupLayout();
        setupRolePermissions();
    }

    private void initComponents() {
        setTitle("QLDIEM - Hệ thống Quản lý Điểm sinh viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize components
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BACKGROUND_COLOR);

        createSidebar();
        createMenuButtons();
        createContentArea();
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(280, getHeight()));

        // Header with logo and user info
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(SIDEBAR_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo
        JLabel logoLabel = new JLabel("QLDIEM", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User info
        userInfoLabel = new JLabel(currentUser, SwingConstants.CENTER);
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userInfoLabel.setForeground(Color.WHITE);
        userInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        roleLabel = new JLabel(getRoleName(userType), SwingConstants.CENTER);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(TEXT_SECONDARY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(userInfoLabel);
        headerPanel.add(roleLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        sidebarPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMenuButtons() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Create buttons
        btnDashboard = new ModernButton("Tổng quan", PRIMARY_COLOR);
        btnDiem = new ModernButton("Quản lý Điểm", PRIMARY_COLOR);

        // Homeroom button for teachers
        btnHomeroomClass = new ModernButton("Lớp chủ nhiệm", PRIMARY_COLOR);
        btnHomeroomClass.setVisible(false); // Hidden by default, shown for homeroom teachers

        btnSinhVien = new ModernButton("Sinh viên", PRIMARY_COLOR);
        btnGiaoVien = new ModernButton("Giáo viên", PRIMARY_COLOR);
        btnLop = new ModernButton("Phân lớp", PRIMARY_COLOR);
        btnQuanLyLop = new ModernButton("Quản lý Lớp", PRIMARY_COLOR);
        btnQuanLyBoMon = new ModernButton("Quản lý Bộ môn", PRIMARY_COLOR); // New
        btnKhoaSo = new ModernButton("Khóa sổ & NK", PRIMARY_COLOR);
        btnThongBao = new ModernButton("Thông báo", PRIMARY_COLOR);

        btnMonHoc = new ModernButton("Môn học", PRIMARY_COLOR);
        btnBaoCao = new ModernButton("Báo cáo", SUCCESS_COLOR);
        btnCaiDat = new ModernButton("Cài đặt", TEXT_SECONDARY);
        btnDangXuat = new ModernButton("Đăng xuất", ACCENT_COLOR);

        // Add buttons to menu
        // Admin menu order: Dashboard, Students, Teachers, Manage Classes, Assign
        // Classes, Departments, Subjects, Lock & Year, Notifications
        menuPanel.add(btnDashboard);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnDiem);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnHomeroomClass); // Homeroom button
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnSinhVien);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnGiaoVien);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnQuanLyLop); // Quản lý lớp
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnLop); // Phân lớp
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnQuanLyBoMon); // Quản lý bộ môn
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnMonHoc); // Môn học
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnKhoaSo); // Khóa sổ & NK
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnThongBao); // Thông báo
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnBaoCao);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnCaiDat);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnDangXuat);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);
    }

    private void createContentArea() {
        JPanel dashboardPanel = createDashboard();
        mainContentPanel.add(dashboardPanel, "DASHBOARD");
    }

    private JPanel createDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BACKGROUND_COLOR);
        dashboard.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel welcomeLabel = new JLabel("Chào mừng, " + currentUser + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        JLabel timeLabel = new JLabel(getCurrentTime());
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(TEXT_SECONDARY);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(BACKGROUND_COLOR);
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(timeLabel);

        headerPanel.add(welcomePanel);
        dashboard.add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = createStatsPanel();
        dashboard.add(statsPanel, BorderLayout.CENTER);

        return dashboard;
    }

    private java.util.Map<String, JLabel> statLabels = new java.util.HashMap<>();

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();

        if (userType == 0) { // Admin
            statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            statsPanel.setBackground(BACKGROUND_COLOR);
            statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            statsPanel.add(createStatsCard("SV", "Tổng Sinh viên", "...", SUCCESS_COLOR));
            statsPanel.add(createStatsCard("GV", "Tổng Giáo viên", "...", PRIMARY_COLOR));
            statsPanel.add(createStatsCard("Lớp", "Tổng Lớp", "...", ACCENT_COLOR));
            statsPanel.add(createStatsCard("Môn", "Tổng Môn", "...", SUCCESS_COLOR));
        } else if (userType == 1) { // Giáo viên - 4 cards in 2x2 grid
            statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            statsPanel.setBackground(BACKGROUND_COLOR);
            statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            statsPanel.add(createStatsCard("Môn", "Môn phụ trách", "...", PRIMARY_COLOR));
            statsPanel.add(createStatsCard("Lớp", "Lớp quản lý", "...", ACCENT_COLOR));
            statsPanel.add(createStatsCard("SV", "SV được dạy", "...", SUCCESS_COLOR));
            statsPanel.add(createStatsCard("SVCN", "SV lớp chủ nhiệm", "...", WARNING_COLOR));
        } else { // Sinh viên - 4 cards in 2x2 grid
            statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            statsPanel.setBackground(BACKGROUND_COLOR);
            statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            statsPanel.add(createStatsCard("Môn", "Tổng môn học", "...", PRIMARY_COLOR));
            statsPanel.add(createStatsCard("TB", "Điểm trung bình", "...", SUCCESS_COLOR));
            statsPanel.add(createStatsCard("XL", "Xếp loại hiện tại", "...", ACCENT_COLOR));
            statsPanel.add(createStatsCard("Info", "Thông tin", "---", TEXT_SECONDARY));
        }

        return statsPanel;
    }

    public void updateStat(String key, String value) {
        if (statLabels.containsKey(key)) {
            statLabels.get(key).setText(value);
        }
    }

    private JPanel createStatsCard(String icon, String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(accentColor);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statLabels.put(icon, valueLabel);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 0, 0, 50)));
    }

    private void setupRolePermissions() {
        switch (userType) {
            case 2: // Sinh viên
                btnSinhVien.setVisible(false);
                btnGiaoVien.setVisible(false);
                btnLop.setVisible(false);
                btnQuanLyLop.setVisible(false);
                btnQuanLyBoMon.setVisible(false); // Hide
                btnKhoaSo.setVisible(false);
                btnMonHoc.setVisible(false);
                btnBaoCao.setVisible(false);
                btnCaiDat.setVisible(false);
                break;
            case 1: // Giáo viên
                btnSinhVien.setVisible(false);
                btnGiaoVien.setVisible(false);
                btnLop.setVisible(false);
                btnQuanLyLop.setVisible(false);
                btnQuanLyBoMon.setVisible(false); // Hide
                btnKhoaSo.setVisible(false);
                btnMonHoc.setVisible(false);
                btnBaoCao.setVisible(false);
                btnCaiDat.setVisible(false);
                btnHomeroomClass.setVisible(true); // Show homeroom button for all teachers
                break;
            case 0: // Admin
                btnDiem.setVisible(false);

                btnBaoCao.setVisible(false);
                btnCaiDat.setVisible(false);
                break;
        }
    }

    private String getRoleName(int userType) {
        switch (userType) {
            case 0:
                return "Quản trị viên";
            case 1:
                return "Giáo viên";
            case 2:
                return "Sinh viên";
            default:
                return "Người dùng";
        }
    }

    private String getCurrentTime() {
        return java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy - HH:mm",
                        java.util.Locale.forLanguageTag("vi-VN")));
    }

    // Event handlers
    public void onDashboardClick(ActionListener l) {
        btnDashboard.addActionListener(l);
    }

    public void onDiemClick(ActionListener l) {
        btnDiem.addActionListener(l);
    }

    public void onHomeroomClick(ActionListener l) {
        btnHomeroomClass.addActionListener(l);
    }

    public void onSinhVienClick(ActionListener l) {
        btnSinhVien.addActionListener(l);
    }

    public void onGiaoVienClick(ActionListener l) {
        btnGiaoVien.addActionListener(l);
    }

    public void onLopClick(ActionListener l) {
        btnLop.addActionListener(l);
    }

    public void onQuanLyLopClick(ActionListener l) {
        btnQuanLyLop.addActionListener(l);
    }

    public void onQuanLyBoMonClick(ActionListener l) {
        btnQuanLyBoMon.addActionListener(l);
    } // New

    public void onKhoaSoClick(ActionListener l) {
        btnKhoaSo.addActionListener(l);
    }

    public void onThongBaoClick(ActionListener l) {
        btnThongBao.addActionListener(l);
    }

    public void onMonHocClick(ActionListener l) {
        btnMonHoc.addActionListener(l);
    }

    public void onBaoCaoClick(ActionListener l) {
        btnBaoCao.addActionListener(l);
    }

    public void onCaiDatClick(ActionListener l) {
        btnCaiDat.addActionListener(l);
    }

    public void onDangXuatClick(ActionListener l) {
        btnDangXuat.addActionListener(l);
    }

    public JPanel getMainContentPanel() {
        return mainContentPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
        resetButtonStates();
        switch (panelName) {
            case "DASHBOARD":
                btnDashboard.setSelected(true);
                break;
            case "DIEM":
                btnDiem.setSelected(true);
                break;
            case "SINHVIEN":
                btnSinhVien.setSelected(true);
                break;
            case "GIAOVIEN":
                btnGiaoVien.setSelected(true);
                break;
            case "LOP":
                btnQuanLyLop.setSelected(true);
                break;
            case "BOMON":
                btnQuanLyBoMon.setSelected(true);
                break; // New
            case "PHANCONG":
                btnLop.setSelected(true);
                break;
            case "THONGBAO":
                btnThongBao.setSelected(true);
                break;
            case "MONHOC":
                btnMonHoc.setSelected(true);
                break;
            case "BAOCAO":
                btnBaoCao.setSelected(true);
                break;
            case "CAIDAT":
                btnCaiDat.setSelected(true);
                break;
        }
    }

    private void resetButtonStates() {
        btnDashboard.setSelected(false);
        btnDiem.setSelected(false);
        btnSinhVien.setSelected(false);
        btnGiaoVien.setSelected(false);
        btnLop.setSelected(false);
        btnQuanLyLop.setSelected(false);
        btnQuanLyBoMon.setSelected(false); // Reset
        btnKhoaSo.setSelected(false);
        btnThongBao.setSelected(false);
        btnMonHoc.setSelected(false);
        btnBaoCao.setSelected(false);
        btnCaiDat.setSelected(false);
    }
}