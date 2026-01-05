package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Login View with beautiful design
 */
public class ModernLoginView extends JFrame {
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color PRIMARY_DARK = new Color(48, 63, 159);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color TEXT_HINT = new Color(173, 181, 189);
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JButton btnShowPassword;
    private JLabel lblStatus;
    private boolean passwordVisible = false;
    
    public ModernLoginView() {
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        // Frame setup
        setTitle("QLDIEM - Đăng nhập hệ thống");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true); // Remove default border for custom look
        setShape(new RoundRectangle2D.Double(0, 0, 450, 600, 20, 20));
        
        // Username field
        txtUsername = createTextField("Nhập tên đăng nhập");
        
        // Password field
        txtPassword = createPasswordField("Nhập mật khẩu");
        
        // Show password button
        btnShowPassword = new JButton("👁");
        btnShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnShowPassword.setPreferredSize(new Dimension(45, 45));
        btnShowPassword.setBackground(Color.WHITE);
        btnShowPassword.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(220, 220, 220)));
        btnShowPassword.setFocusPainted(false);
        btnShowPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Login button
        btnLogin = createButton("ĐĂNG NHẬP", PRIMARY_COLOR, Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(300, 50));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Exit button
        btnExit = new JButton("✕");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnExit.setForeground(Color.WHITE);
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Status label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add placeholder functionality
        field.setForeground(TEXT_HINT);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_HINT);
                    field.setText(placeholder);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(255, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Initial state for placeholder
        field.setEchoChar((char) 0);
        field.setForeground(TEXT_HINT);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(field.getPassword());
                if (pass.equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                    if (!passwordVisible) {
                        field.setEchoChar('●');
                    }
                }
                
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 2, 2, 0, PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
                btnShowPassword.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, PRIMARY_COLOR));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                String pass = new String(field.getPassword());
                if (pass.isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_HINT);
                    field.setEchoChar((char) 0);
                }
                
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                btnShowPassword.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(220, 220, 220)));
            }
        });
        
        return field;
    }
    
    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(textColor);
        button.setBackground(backgroundColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        Color originalColor = backgroundColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        // Simple white content pane
        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout()); // Use BorderLayout to hold the layered pane
        
        // Add subtle double border to the window itself
        contentPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        setContentPane(contentPane);
        
        // Let's use a LayeredPane for the exit button + centered content
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(450, 600));
        
        // 1. Background/Content Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setSize(450, 600); // Allow it to fill the window
        mainPanel.setLocation(0,0);
        
        // 2. Card Panel
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        // Elegant border: Double line border
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Logo
        JLabel logoLabel = new JLabel("QLDIEM", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(PRIMARY_COLOR);
        cardPanel.add(logoLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 40, 0);
        JLabel subtitleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        cardPanel.add(subtitleLabel, gbc);
        
        // Inputs
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtUsername, gbc);
        
        gbc.gridy++;
        // Password Container for alignment
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(Color.WHITE);
        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(btnShowPassword, BorderLayout.EAST);
        // Ensure size matches username field
        passPanel.setPreferredSize(new Dimension(300, 45));
        passPanel.setMaximumSize(new Dimension(300, 45));
        cardPanel.add(passPanel, gbc);
        
        // Status
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        cardPanel.add(lblStatus, gbc);
        
        // Button
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 30, 0);
        cardPanel.add(btnLogin, gbc);
        
        // Footer
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel footerLabel = new JLabel("© 2024 QLDIEM Team");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_HINT);
        cardPanel.add(footerLabel, gbc);
        
        // Add card to main panel
        mainPanel.add(cardPanel);
        
        // 3. Exit Button (Top Right)
        btnExit.setForeground(new Color(150, 150, 150));
        btnExit.setBounds(395, 5, 45, 40); // Adjusted position
        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExit.setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExit.setForeground(new Color(150, 150, 150));
            }
        });
        
        // Add to LayeredPane
        // JLayeredPane uses null layout by default, so setBounds is required for components added to it
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(btnExit, JLayeredPane.PALETTE_LAYER);
        
        contentPane.add(layeredPane, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        // Show/hide password
        btnShowPassword.addActionListener(e -> togglePasswordVisibility());
        
        // Enter key for login
        txtPassword.addActionListener(e -> btnLogin.doClick());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        
        // Exit button
        btnExit.addActionListener(e -> System.exit(0));
        
        // Add drag functionality for undecorated frame
        MouseInputAdapter dragHandler = new MouseInputAdapter();
        addMouseListener(dragHandler);
        addMouseMotionListener(dragHandler);
    }
    
    // Drag helper
    private class MouseInputAdapter extends java.awt.event.MouseAdapter {
        private int posX = 0, posY = 0;
        
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            posX = e.getX();
            posY = e.getY();
        }
        
        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
            setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
        }
    }
    
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        String currentPass = new String(txtPassword.getPassword());
        
        // Only toggle echo char if text is not the placeholder
        if (!currentPass.equals("Nhập mật khẩu")) {
            if (passwordVisible) {
                txtPassword.setEchoChar((char) 0);
                btnShowPassword.setText("✕");
            } else {
                txtPassword.setEchoChar('●');
                btnShowPassword.setText("👁");
            }
        }
    }
    
    // Public methods
    public String getUsername() {
        String username = txtUsername.getText();
        return username.equals("Nhập tên đăng nhập") ? "" : username;
    }
    
    public String getPassword() {
        String password = new String(txtPassword.getPassword());
        return password.equals("Nhập mật khẩu") ? "" : password;
    }
    
    public void setStatus(String message, boolean isError) {
        lblStatus.setText(message);
        lblStatus.setForeground(isError ? Color.RED : new Color(40, 167, 69));
    }
    
    public void clearForm() {
        txtUsername.setText("Nhập tên đăng nhập");
        txtUsername.setForeground(TEXT_HINT);
        
        txtPassword.setText("Nhập mật khẩu");
        txtPassword.setForeground(TEXT_HINT);
        txtPassword.setEchoChar((char) 0);
        
        passwordVisible = false; // Reset state
        btnShowPassword.setText("👁");
        
        lblStatus.setText(" ");
        btnLogin.setEnabled(true);
        btnLogin.setText("ĐĂNG NHẬP");
    }
    
    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }
    
    public JButton getBtnLogin() {
        return btnLogin;
    }
    
    public JTextField getTxtUsername() { return txtUsername; } // Added getter
}