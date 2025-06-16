package DataAccessObject;

// ===== Imports =====
import ModelLayer.Class.jadwal;
import db.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ===== Class Declaration =====
public class JadwalDAO {

    // ===== Public CRUD Methods =====

    // Create a new jadwal record and set the generated ID back to the object.
    public int create(jadwal jadwalObj) { 
        String sql = "INSERT INTO jadwal (hari, sesi) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, jadwalObj.getHari());
            pstmt.setInt(2, jadwalObj.getSesi());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if(rs.next()){
                        int newId = rs.getInt(1);
                        jadwalObj.setIdJadwal(newId); // Set ID back to object
                        return newId; // Return the new id_jadwal
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating jadwal: " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Or -1 to indicate failure
    }

    // Retrieve a jadwal record by its ID.
    public jadwal findById(int id) {
        String sql = "SELECT * FROM jadwal WHERE id_jadwal = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToJadwal(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving jadwal: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Find jadwal by hari and sesi.
    public jadwal findByHariAndSesi(String hari, int sesi) {
        String sql = "SELECT * FROM jadwal WHERE hari = ? AND sesi = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hari);
            pstmt.setInt(2, sesi);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToJadwal(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update an existing jadwal record.
    public boolean update(jadwal jadwalObj) {
        String sql = "UPDATE jadwal SET hari = ?, sesi = ? WHERE id_jadwal = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jadwalObj.getHari());
            pstmt.setInt(2, jadwalObj.getSesi());
            pstmt.setInt(3, jadwalObj.getIdJadwal());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating jadwal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete a jadwal record by its ID.
    public boolean deleteJadwal(int id) {
        String sql = "DELETE FROM jadwal WHERE id_jadwal = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting jadwal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all jadwal records.
    public List<jadwal> getAllJadwals() {
        List<jadwal> list = new ArrayList<>();
        String sql = "SELECT * FROM jadwal";
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToJadwal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving jadwals: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ===== Private Helper Methods =====
    // Maps a ResultSet row to a jadwal object.
    private jadwal mapResultSetToJadwal(ResultSet rs) throws SQLException {
        jadwal jadObj = new jadwal();
        jadObj.setIdJadwal(rs.getInt("id_jadwal"));
        jadObj.setHari(rs.getString("hari"));
        jadObj.setSesi(rs.getInt("sesi"));
        return jadObj;
    }
}
