package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class MonHocModel {
    private String mamon;
    private String tenmon;
    private String mabomon;
    private int sotinchi;

    public MonHocModel() {
    }

    public MonHocModel(String mamon, String tenmon, String mabomon, int sotinchi) {
        this.mamon = mamon;
        this.tenmon = tenmon;
        this.mabomon = mabomon;
        this.sotinchi = sotinchi;
    }

    // Constructor matching legacy calls (default sotinchi = 3 or 0)
    public MonHocModel(String mamon, String tenmon, String mabomon) {
        this(mamon, tenmon, mabomon, 0);
    }

    public String getMamon() {
        return mamon;
    }

    public void setMamon(String mamon) {
        this.mamon = mamon;
    }

    public String getTenmon() {
        return tenmon;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = tenmon;
    }

    public String getMabomon() {
        return mabomon;
    }

    public void setMabomon(String mabomon) {
        this.mabomon = mabomon;
    }

    public int getSotinchi() {
        return sotinchi;
    }

    public void setSotinchi(int sotinchi) {
        this.sotinchi = sotinchi;
    }

    public ArrayList<MonHocModel> getMonHocByBoMon(String mabomon) {
        ArrayList<MonHocModel> list = new ArrayList<>();
        String query = "SELECT * FROM tblmonhoc WHERE mabomon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, mabomon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MonHocModel(
                            rs.getString("mamon"),
                            rs.getString("tenmon"),
                            rs.getString("mabomon"),
                            rs.getInt("sotinchi")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<MonHocModel> getAllMonHoc() {
        ArrayList<MonHocModel> list = new ArrayList<>();
        String query = "SELECT * FROM tblmonhoc";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new MonHocModel(
                        rs.getString("mamon"),
                        rs.getString("tenmon"),
                        rs.getString("mabomon"),
                        rs.getInt("sotinchi")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addMonHoc(MonHocModel mh) {
        String query = "INSERT INTO tblmonhoc (mamon, tenmon, mabomon, sotinchi) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, mh.getMamon());
            ps.setString(2, mh.getTenmon());
            ps.setString(3, mh.getMabomon());
            ps.setInt(4, mh.getSotinchi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMonHoc(MonHocModel mh) {
        String query = "UPDATE tblmonhoc SET tenmon = ?, sotinchi = ? WHERE mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, mh.getTenmon());
            ps.setInt(2, mh.getSotinchi());
            ps.setString(3, mh.getMamon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMonHoc(String mamon) {
        String query = "DELETE FROM tblmonhoc WHERE mamon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, mamon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
