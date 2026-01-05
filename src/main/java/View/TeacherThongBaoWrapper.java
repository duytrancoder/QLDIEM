package View;

import javax.swing.*;
import java.awt.*;

public class TeacherThongBaoWrapper extends JPanel {

    private JTabbedPane tabbedPane;
    private QuanLyThongBaoPanel sendPanel;
    private XemThongBaoPanel viewPanel;

    public TeacherThongBaoWrapper(QuanLyThongBaoPanel sendPanel, XemThongBaoPanel viewPanel) {
        this.sendPanel = sendPanel;
        this.viewPanel = viewPanel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("Gửi Thông Báo", sendPanel);
        tabbedPane.addTab("Xem Thông Báo (Admin)", viewPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public QuanLyThongBaoPanel getSendPanel() {
        return sendPanel;
    }

    public XemThongBaoPanel getViewPanel() {
        return viewPanel;
    }
}
