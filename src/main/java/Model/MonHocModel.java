package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class MonHocModel {
    private String mamon;
    private String tenmon;
    private int sotinchi;

    public MonHocModel() {
    }

    public MonHocModel(String mamon, String tenmon, int sotinchi) {
        this.mamon = mamon;
        this.tenmon = tenmon;
        this.sotinchi = sotinchi;
    }

    public MonHocModel(String mamon, String tenmon) {
        this(mamon, tenmon, 0);
    }

    public String getMamon() {
        return mamon;
    }

    public void setMamon(String mamon) {
        this.mamon = (mamon != null) ? mamon.trim() : null;
    }

    public String getTenmon() {
        return tenmon;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = (tenmon != null) ? tenmon.trim() : null;
    }

    public int getSotinchi() {
        return sotinchi;
    }

    public void setSotinchi(int sotinchi) {
        this.sotinchi = sotinchi;
    }

    public ArrayList<MonHocModel> getMonHocByCodes(String csvMamon) {
        ArrayList<MonHocModel> list = new ArrayList<>();
        if (csvMamon == null || csvMamon.trim().isEmpty()) {
            return list;
        }

        String[] codes = csvMamon.split(",");
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM tblmonhoc WHERE mamon IN (");
        for (int i = 0; i < codes.length; i++) {
            queryBuilder.append("?");
            if (i < codes.length - 1) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(")");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < codes.length; i++) {
                ps.setString(i + 1, codes[i].trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MonHocModel(
                            rs.getString("mamon"),
                            rs.getString("tenmon"),
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
                        rs.getInt("sotinchi")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addMonHoc(MonHocModel mh) {
        String query = "INSERT INTO tblmonhoc (mamon, tenmon, sotinchi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, (mh.getMamon() != null) ? mh.getMamon().trim() : "");
            ps.setString(2, (mh.getTenmon() != null) ? mh.getTenmon().trim() : "");
            ps.setInt(3, mh.getSotinchi());
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
            ps.setString(1, (mh.getTenmon() != null) ? mh.getTenmon().trim() : "");
            ps.setInt(2, mh.getSotinchi());
            ps.setString(3, (mh.getMamon() != null) ? mh.getMamon().trim() : "");
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
            ps.setString(1, (mamon != null) ? mamon.trim() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
