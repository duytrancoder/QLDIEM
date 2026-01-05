package Model;

import connection.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class ThongBaoModel {
    private int id;
    private String tieude;
    private String noidung;
    private String nguoigui; // Username
    private String tenNguoiGui; // Real name
    private String loai; // 'TOAN_TRUONG', 'LOP'
    private String phamvi; // NULL or Malop
    private String trangThai; // 'HIEN', 'AN'
    private Timestamp ngaygui;
    private String groupIds; // Transient field for aggregated IDs

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public ThongBaoModel() {
        verifyTableExist();
    }

    public ThongBaoModel(int id, String tieude, String noidung, String nguoigui, String tenNguoiGui, String loai,
            String phamvi, String trangThai, Timestamp ngaygui) {
        this.id = id;
        this.tieude = tieude;
        this.noidung = noidung;
        this.nguoigui = nguoigui;
        this.tenNguoiGui = tenNguoiGui;
        this.loai = loai;
        this.phamvi = phamvi;
        this.trangThai = trangThai;
        this.ngaygui = ngaygui;
    }

    private void verifyTableExist() {
        String sql = "CREATE TABLE IF NOT EXISTS tblthongbao (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "tieude VARCHAR(255) NOT NULL," +
                "noidung TEXT," +
                "nguoigui VARCHAR(50)," +
                "ten_nguoi_gui VARCHAR(100)," +
                "loai VARCHAR(20)," +
                "phamvi VARCHAR(50)," +
                "trang_thai VARCHAR(20) DEFAULT 'HIEN'," +
                "ngaygui DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public String getNguoigui() {
        return nguoigui;
    }

    public void setNguoigui(String nguoigui) {
        this.nguoigui = nguoigui;
    }

    public String getTenNguoiGui() {
        return tenNguoiGui;
    }

    public void setTenNguoiGui(String tenNguoiGui) {
        this.tenNguoiGui = tenNguoiGui;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getPhamvi() {
        return phamvi;
    }

    public void setPhamvi(String phamvi) {
        this.phamvi = phamvi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Timestamp getNgaygui() {
        return ngaygui;
    }

    public void setNgaygui(Timestamp ngaygui) {
        this.ngaygui = ngaygui;
    }

    // Methods

    // 1. Get All for Admin (See everything except Hidden/Deleted) - Aggregated
    public ArrayList<ThongBaoModel> getAll() {
        return getAllAggregated();
    }

    public ArrayList<ThongBaoModel> getAllAggregated() {
        ArrayList<ThongBaoModel> list = new ArrayList<>();
        // Group by Sender, Title, Content, Time to aggregate Scopes
        String sql = "SELECT MIN(id) as id, GROUP_CONCAT(id) as group_ids, tieude, noidung, nguoigui, ten_nguoi_gui, loai, "
                +
                "GROUP_CONCAT(phamvi SEPARATOR ', ') as phamvi, trang_thai, ngaygui " +
                "FROM tblthongbao WHERE trang_thai = 'HIEN' " +
                "GROUP BY tieude, noidung, nguoigui, ten_nguoi_gui, loai, ngaygui " +
                "ORDER BY ngaygui DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ThongBaoModel t = mapResultSetToModel(rs);
                t.setGroupIds(rs.getString("group_ids"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Get for Student (School-wide OR their Class)
    public ArrayList<ThongBaoModel> getForStudent(String malop) {
        ArrayList<ThongBaoModel> list = new ArrayList<>();
        // Logic: Type is SCHOOL OR (Type is CLASS AND Scope is My Class)
        // AND Status must be 'HIEN'
        String sql = "SELECT * FROM tblthongbao WHERE (loai = 'TOAN_TRUONG' OR (loai = 'LOP' AND phamvi = ?)) AND trang_thai = 'HIEN' ORDER BY ngaygui DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, malop);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToModel(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Get for Teacher (School-wide OR Sent by Me) - Aggregated
    public ArrayList<ThongBaoModel> getForTeacher(String username) {
        ArrayList<ThongBaoModel> list = new ArrayList<>();
        // Logic: Only Sent by Me
        // Exclude 'AN' (Deleted) items so they disappear from list.
        String sql = "SELECT MIN(id) as id, GROUP_CONCAT(id) as group_ids, tieude, noidung, nguoigui, ten_nguoi_gui, loai, "
                +
                "GROUP_CONCAT(phamvi SEPARATOR ', ') as phamvi, trang_thai, ngaygui " +
                "FROM tblthongbao " +
                "WHERE (nguoigui = ?) AND trang_thai = 'HIEN' " +
                "GROUP BY tieude, noidung, nguoigui, ten_nguoi_gui, loai, ngaygui " +
                "ORDER BY ngaygui DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ThongBaoModel t = mapResultSetToModel(rs);
                    t.setGroupIds(rs.getString("group_ids"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // New: Add with specific timestamp for batching
    public boolean add(ThongBaoModel tb, java.sql.Timestamp timestamp) throws SQLException {
        String sql = "INSERT INTO tblthongbao (tieude, noidung, nguoigui, ten_nguoi_gui, loai, phamvi, trang_thai, ngaygui) VALUES (?, ?, ?, ?, ?, ?, 'HIEN', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tb.getTieude());
            stmt.setString(2, tb.getNoidung());
            stmt.setString(3, tb.getNguoigui());
            stmt.setString(4, tb.getTenNguoiGui());
            stmt.setString(5, tb.getLoai());
            stmt.setString(6, tb.getPhamvi());
            stmt.setTimestamp(7, timestamp);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean add(ThongBaoModel tb) throws SQLException {
        return add(tb, new java.sql.Timestamp(System.currentTimeMillis()));
    }

    // Batch Update
    public boolean updateBatch(ThongBaoModel tb, String groupIds) throws SQLException {
        // We only update Title, Content, Sender info.
        String sql = "UPDATE tblthongbao SET tieude=?, noidung=?, nguoigui=?, ten_nguoi_gui=? WHERE FIND_IN_SET(id, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tb.getTieude());
            stmt.setString(2, tb.getNoidung());
            stmt.setString(3, tb.getNguoigui());
            stmt.setString(4, tb.getTenNguoiGui());
            stmt.setString(5, groupIds);
            return stmt.executeUpdate() > 0;
        }
    }

    // Single Update
    public boolean update(ThongBaoModel tb) throws SQLException {
        String sql = "UPDATE tblthongbao SET tieude = ?, noidung = ?, loai = ?, phamvi = ?, ten_nguoi_gui = ?, ngaygui = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tb.getTieude());
            stmt.setString(2, tb.getNoidung());
            stmt.setString(3, tb.getLoai());
            stmt.setString(4, tb.getPhamvi());
            stmt.setString(5, tb.getTenNguoiGui());
            stmt.setInt(6, tb.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Batch Delete
    public boolean deleteBatch(String groupIds) throws SQLException {
        String sql = "UPDATE tblthongbao SET trang_thai = 'AN' WHERE FIND_IN_SET(id, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, groupIds);
            return stmt.executeUpdate() > 0;
        }
    }

    // Soft delete
    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE tblthongbao SET trang_thai = 'AN' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private ThongBaoModel mapResultSetToModel(ResultSet rs) throws SQLException {
        return new ThongBaoModel(
                rs.getInt("id"),
                rs.getString("tieude"),
                rs.getString("noidung"),
                rs.getString("nguoigui"),
                rs.getString("ten_nguoi_gui"),
                rs.getString("loai"),
                rs.getString("phamvi"),
                rs.getString("trang_thai"),
                rs.getTimestamp("ngaygui"));
    }
}
