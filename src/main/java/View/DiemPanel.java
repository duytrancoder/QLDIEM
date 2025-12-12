/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import Model.DiemModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.table.TableModel;

/**
 *
 * @author noqok
 */
public class DiemPanel extends JPanel {

    private DiemModel d;
    private JLabel lblmsv;
    private JLabel lblmm;
    private JLabel lblhk;
    private JLabel lblnh;
    private JLabel lblcc;
    private JLabel lblgk;
    private JLabel lblck;
    private JLabel lbltk;
    private JLabel lblsearch;

    private JTextField tfmsv;
    private JTextField tfmm;
    private JTextField tfhk;
    private JTextField tfnh;
    private JTextField tfcc;
    private JTextField tfgk;
    private JTextField tfck;
    private JTextField tftk;
    private JTextField tfsearch;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnXacnhan;
    private JButton btnHuy;
    private JButton btnSearch;

    private JTable tblDiem;
    private DefaultTableModel model;

    public DiemPanel() {

        Display();
        ArrayList<DiemModel> ds = new ArrayList<>();
        loadtable(ds);
    }

    public void Display() {

        this.setSize(900, 750);

        lblmsv = new JLabel("Mã Sinh Viên");
        lblmm = new JLabel("Mã Môn Học");
        lblhk = new JLabel("Học Kỳ");
        lblnh = new JLabel("Năm Học");
        lblcc = new JLabel("Điểm Chuyên Cần");
        lblgk = new JLabel("Điểm Giữa Kỳ");
        lblck = new JLabel("Điểm Cuối Kỳ");
        lbltk = new JLabel("Điểm Tổng Kết");
        lblsearch = new JLabel("Tìm Kiếm");
        tfmsv = new JTextField(20);
        tfmm = new JTextField(20);
        tfhk = new JTextField(20);
        tfnh = new JTextField(20);
        tfcc = new JTextField(20);
        tfgk = new JTextField(20);
        tfck = new JTextField(20);
        tftk = new JTextField(20);
        tfsearch = new JTextField(20);

        JPanel pnmsv = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pnmm = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pnhk = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pnnh = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pncc = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pngk = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pnck = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pntk = new JPanel(new GridLayout(1, 2, 5, 5));
        JPanel pns = new JPanel(new GridLayout(1, 3, 5, 5));
        pnmsv.add(lblmsv);
        pnmsv.add(tfmsv);
        pnmm.add(lblmm);
        pnmm.add(tfmm);
        pnhk.add(lblhk);
        pnhk.add(tfhk);
        pnnh.add(lblnh);
        pnnh.add(tfnh);
        pncc.add(lblcc);
        pncc.add(tfcc);
        pngk.add(lblgk);
        pngk.add(tfgk);
        pnck.add(lblck);
        pnck.add(tfck);
        pntk.add(lbltk);
        pntk.add(tftk);
        tftk.setEditable(false);
        pns.add(lblsearch);
        pns.add(tfsearch);
        btnSearch = new JButton("🔍");
        pns.add(btnSearch);
        JPanel pnForm = new JPanel(new GridLayout(10, 1, 5, 5));
        pnForm.add(pnmsv);
        pnForm.add(pnmm);
        pnForm.add(pnhk);
        pnForm.add(pnnh);
        pnForm.add(pncc);
        pnForm.add(pngk);
        pnForm.add(pnck);
        pnForm.add(pntk);
        pnForm.add(pns);

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnXacnhan = new JButton("Xác nhận");
        btnHuy = new JButton("Hủy");

        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnButton.add(btnThem);
        pnButton.add(btnSua);
        pnButton.add(btnXoa);
        pnButton.add(btnXacnhan);
        pnButton.add(btnHuy);

        String[] tencot = {
            "MSV", "Mã Môn", "Học Kỳ", "Năm Học",
            "CC", "GK", "CK", "Tổng Kết"
        };
        model = new DefaultTableModel(tencot, 0);
        tblDiem = new JTable(model);

        JPanel pnTable = new JPanel();
        pnTable.add(tblDiem.getTableHeader());
        pnTable.add(tblDiem);

        this.setLayout(new GridLayout(3, 1, 5, 5));
        this.add(pnForm);
        this.add(pnButton);
        this.add(pnTable);

    }

    public JTable getTblDiem() {
        return tblDiem;
    }

    public void addActionListerner(ActionListener a) {
        btnThem.addActionListener(a);
        btnSua.addActionListener(a);
        btnXoa.addActionListener(a);
        btnXacnhan.addActionListener(a);
        btnHuy.addActionListener(a);
        btnSearch.addActionListener(a);
        
    }

    public void addTableMouseAction(MouseListener a) {
        tblDiem.addMouseListener(a);

    }

    public DiemModel getformdata() {
       

        String msv = tfmsv.getText().trim();
        String mm = tfmm.getText().trim();
        String nh = tfnh.getText().trim();

        int hk = 1;
        try {
            String textHk = tfhk.getText().trim();
            if (!textHk.isEmpty()) {
                hk = Integer.parseInt(textHk);
            }
        } catch (Exception e) {
            hk = 1;
        }

        double cc = 0.0;
        double gk = 0.0;
        double ck = 0.0;

        try {
            if (!tfcc.getText().trim().isEmpty()) {
                cc = Double.parseDouble(tfcc.getText().trim());
            }
        } catch (Exception e) {
            cc = 0.0;
        }

        try {
            if (!tfgk.getText().trim().isEmpty()) {
                gk = Double.parseDouble(tfgk.getText().trim());
            }
        } catch (Exception e) {
            gk = 0.0;
        }

        try {
            if (!tfck.getText().trim().isEmpty()) {
                ck = Double.parseDouble(tfck.getText().trim());
            }
        } catch (Exception e) {
            ck = 0.0;
        }

        double tk = (cc * 10 + gk * 30 + ck * 60) / 100;

        return new DiemModel( msv, mm, hk, nh, cc, gk, ck, tk);
    }

    public void fillform(DiemModel d) {
        if (d == null) {
            return;
        }

        tfmsv.setText(d.getMasv());
        tfmm.setText(d.getMamon());
        tfhk.setText(String.valueOf(d.getHocky()));
        tfnh.setText(d.getNamhoc());
        tfcc.setText(String.valueOf(d.getDiemcc()));
        tfgk.setText(String.valueOf(d.getDiemgk()));
        tfck.setText(String.valueOf(d.getDiemck()));
        tftk.setText(String.valueOf(d.getDiemtongket()));
    }

    public void clearform() {
        tfmsv.setText("");
        tfmm.setText("");
        tfhk.setText("");
        tfnh.setText("");
        tfcc.setText("");
        tfgk.setText("");
        tfck.setText("");
        tftk.setText("");

    }

    public void loadtable(ArrayList<DiemModel> d) {
        model.setRowCount(0);
        for (DiemModel b : d) {

            Object[] dulieubang = {
                b.getMasv(),
                b.getMamon(),
                b.getHocky(),
                b.getNamhoc(),
                b.getDiemcc(),
                b.getDiemgk(),
                b.getDiemck(),
                b.getDiemtongket()

            };
            model.addRow(dulieubang);
        }
    }

    public void on_off(boolean a, boolean b) {
        btnThem.setVisible(a);
        btnSua.setVisible(a);
        btnXoa.setVisible(a);
        btnXacnhan.setVisible(b);
        btnHuy.setVisible(b);
    }
    
    public String getSearchKeyword() {
    return tfsearch.getText().trim();
}
}
