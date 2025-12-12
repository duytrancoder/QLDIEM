/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author noqok
 */
import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class DiemModel {

    private String masv;
    private String mamon;
    private int hocky;
    private String namhoc;
    private double diemcc;
    private double diemgk;
    private double diemck;
    private double diemtongket;

    public DiemModel() {
    }

    public DiemModel( String masv, String mamon, int hocky, String namhoc, double diemcc, double diemgk, double diemck, double diemtongket) {
        this.masv = masv;
        this.mamon = mamon;
        this.hocky = hocky;
        this.namhoc = namhoc;
        this.diemcc = diemcc;
        this.diemgk = diemgk;
        this.diemck = diemck;
        this.diemtongket = diemtongket; // Store the DB value but getter will calculate it
    }

   

    public String getMasv() {
        return masv;
    }

    public void setMasv(String masv) {
        this.masv = masv;
    }

    public String getMamon() {
        return mamon;
    }

    public void setMamon(String mamon) {
        this.mamon = mamon;
    }

    public int getHocky() {
        return hocky;
    }

    public void setHocky(int hocky) {
        this.hocky = hocky;
    }

    public String getNamhoc() {
        return namhoc;
    }

    public void setNamhoc(String namhoc) {
        this.namhoc = namhoc;
    }

    public double getDiemcc() {
        return diemcc;
    }

    public void setDiemcc(double diemcc) {
        this.diemcc = diemcc;
    }

    public double getDiemgk() {
        return diemgk;
    }

    public void setDiemgk(double diemgk) {
        this.diemgk = diemgk;
    }

    public double getDiemck() {
        return diemck;
    }

    public void setDiemck(double diemck) {
        this.diemck = diemck;
    }

    public double getDiemtongket() {
        return (diemcc * 10 + diemgk * 30 + diemck * 60) / 100;
    }

    public String validate() {
        if (masv == null || masv.trim().isEmpty()) {
            return "Mã sinh viên không được để trống!";
        }

        if (mamon == null || mamon.trim().isEmpty()) {
            return "Mã môn học không được để trống!";
        }

        if (hocky < 1 || hocky > 3) {
            return "Học kỳ phải là 1, 2 hoặc 3!";
        }

        if (namhoc == null || namhoc.trim().isEmpty()) {
            return "Năm học không được để trống!";
        }

        if (diemcc < 0 || diemcc > 10) {
            return "Điểm chuyên cần phải từ 0 đến 10!";
        }

        if (diemgk < 0 || diemgk > 10) {
            return "Điểm giữa kỳ phải từ 0 đến 10!";
        }

        if (diemck < 0 || diemck > 10) {
            return "Điểm cuối kỳ phải từ 0 đến 10!";
        }

        return null;
    }

    public ArrayList<DiemModel> getDs() {
        ArrayList<DiemModel> list = new ArrayList<>();
        String query = "SELECT * FROM tbldiem";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DiemModel d = new DiemModel(
                        rs.getString("masv"),
                        rs.getString("mamon"),
                        rs.getInt("hocky"),
                        rs.getString("namhoc"),
                        rs.getDouble("diemcc"),
                        rs.getDouble("diemgk"),
                        rs.getDouble("diemck"),
                        rs.getDouble("diemtongket") // Nếu DB ko lưu cột này thì bỏ qua
                );
                list.add(d);
               
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertDiem(DiemModel d) {
        String validationError = d.validate();
        if (validationError != null) {
            System.out.println("Validation error: " + validationError);
            return false;
        }

        String query = "INSERT INTO tbldiem (masv, mamon, hocky, namhoc, diemcc, diemgk, diemck, diemtongket) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, d.getMasv());
            ps.setString(2, d.getMamon());
            ps.setInt(3, d.getHocky());
            ps.setString(4, d.getNamhoc());
            ps.setDouble(5, d.getDiemcc());
            ps.setDouble(6, d.getDiemgk());
            ps.setDouble(7, d.getDiemck());
            ps.setDouble(8, d.getDiemtongket());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDiem(DiemModel d) {
        String validationError = d.validate();
        if (validationError != null) {
            System.out.println("Validation error: " + validationError);
            return false;
        }

        String query = "UPDATE tbldiem SET masv=?, mamon=?, hocky=?, namhoc=?, diemcc=?, diemgk=?, diemck=?, diemtongket=? WHERE masv=?";
        try (
                Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, d.getMasv());
            ps.setString(2, d.getMamon());
            ps.setInt(3, d.getHocky());
            ps.setString(4, d.getNamhoc());
            ps.setDouble(5, d.getDiemcc());
            ps.setDouble(6, d.getDiemgk());
            ps.setDouble(7, d.getDiemck());
            ps.setDouble(8, d.getDiemtongket());
            ps.setString(9, d.getMasv());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDiem(int id) {
        String query = "DELETE FROM tbldiem WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

public ArrayList<DiemModel> search(String masvCanTim) { 
    ArrayList<DiemModel> list = new ArrayList<>();
    String query = "SELECT * FROM tbldiem WHERE masv LIKE ?"; 
    try (Connection conn = DatabaseConnection.getConnection(); 
         PreparedStatement ps = conn.prepareStatement(query)) {
        
        ps.setString(1, "%" + masvCanTim + "%"); 
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {                
            DiemModel d = new DiemModel(
                rs.getString("masv"),
                rs.getString("mamon"),
                rs.getInt("hocky"),
                rs.getString("namhoc"),
                rs.getDouble("diemcc"),
                rs.getDouble("diemgk"),
                rs.getDouble("diemck"),
                rs.getDouble("diemtongket")
            );
            list.add(d);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return list;
}

}
