package View;

import Model.ThongBaoModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class XemThongBaoPanel extends JPanel {

    private JPanel contentPanel;
    private JLabel lblNoData;

    public XemThongBaoPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(250, 250, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Bảng tin Nhà trường");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(63, 81, 181));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(250, 250, 250));
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // No Data Label
        lblNoData = new JLabel("Chưa có thông báo nào.", SwingConstants.CENTER);
        lblNoData.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblNoData.setForeground(Color.GRAY);
        lblNoData.setVisible(false);
    }

    public void loadThongBao(ArrayList<ThongBaoModel> list) {
        contentPanel.removeAll();

        if (list.isEmpty()) {
            contentPanel.add(lblNoData);
            lblNoData.setVisible(true);
        } else {
            lblNoData.setVisible(false);
            for (ThongBaoModel tb : list) {
                contentPanel.add(createThongBaoCard(tb));
                contentPanel.add(Box.createVerticalStrut(15)); // Spacing
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createThongBaoCard(ThongBaoModel tb) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Max height constraint

        // Header: Title + Date
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(tb.getTieude());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(33, 37, 41));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        JLabel lblDate = new JLabel(sdf.format(tb.getNgaygui()));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(Color.GRAY);

        top.add(lblTitle, BorderLayout.CENTER);
        top.add(lblDate, BorderLayout.EAST);

        // Sender Info
        JLabel lblSender = new JLabel(
                "Người gửi: " + (tb.getTenNguoiGui() != null ? tb.getTenNguoiGui() : tb.getNguoigui()));
        lblSender.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSender.setForeground(new Color(63, 81, 181));
        lblSender.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Content
        JTextArea txtContent = new JTextArea(tb.getNoidung());
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContent.setForeground(new Color(50, 50, 50));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setEditable(false);
        txtContent.setBorder(null);
        txtContent.setBackground(Color.WHITE);

        card.add(top, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(lblSender, BorderLayout.NORTH);
        centerPanel.add(txtContent, BorderLayout.CENTER);

        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }
}
