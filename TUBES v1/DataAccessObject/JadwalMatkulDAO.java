package DataAccessObject;

import db.DBConnectionUtil;
import ModelLayer.Class.jadwal_matkul; // Import the model
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalMatkulDAO {

    public Integer findIdJmByMatkulAndJadwal(int idMatkul, int idJadwal) {
        String sql = "SELECT id_jm FROM jadwal_matkul WHERE id_matkul = ? AND id_jadwal = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMatkul);
            pstmt.setInt(2, idJadwal);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_jm");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all id_jm entries associated with a given id_matkul.
     * A matkul might be scheduled multiple times (different id_jadwal, thus different id_jm).
     * @param idMatkul The ID of the matkul.
     * @return A list of id_jm values, or an empty list if none are found.
     */
    public List<Integer> findIdJmsByMatkulId(int idMatkul) {
        List<Integer> idJms = new ArrayList<>();
        String sql = "SELECT id_jm FROM jadwal_matkul WHERE id_matkul = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMatkul);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                idJms.add(rs.getInt("id_jm"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this error more formally
        }
        return idJms;
    }

    // New method: Find id_matkul by id_jm
    public Integer findMatkulIdByIdJm(int idJm) {
        String sql = "SELECT id_matkul FROM jadwal_matkul WHERE id_jm = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idJm);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_matkul");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // New method: Find id_jadwal by id_jm
    public Integer findJadwalIdByIdJm(int idJm) {
        String sql = "SELECT id_jadwal FROM jadwal_matkul WHERE id_jm = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idJm);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_jadwal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // New method: Create a jadwal_matkul entry
    public boolean create(jadwal_matkul jm) {
        // Assuming id_user is also part of your jadwal_matkul table
        // If not, remove id_user from the SQL and parameter setting
        String sql = "INSERT INTO jadwal_matkul (id_matkul, id_jadwal, id_user) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, jm.getIdMatkul());
            pstmt.setInt(2, jm.getIdJadwal());
            pstmt.setInt(3, jm.getIdUser()); // Make sure id_user is set in the jm object before calling this

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        jm.setIdJm(generatedKeys.getInt(1)); // Set the generated id_jm back to the object
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // New method: Delete a jadwal_matkul entry by id_jm
    public boolean deleteByIdJm(int idJm) {
        // Before deleting from jadwal_matkul, consider deleting associated tasks
        // Or handle this with ON DELETE CASCADE in your database schema for the Tugas table's foreign key to id_jm
        TaskDAO taskDAO = new TaskDAO(); // Assuming TaskDAO has a method to delete by id_jm
        boolean tasksDeleted = taskDAO.deleteTasksByIdJm(idJm); // You need to implement this in TaskDAO
        
        if (!tasksDeleted) {
            System.err.println("Warning: Could not delete all tasks associated with id_jm: " + idJm + ". Aborting delete of jadwal_matkul entry.");
            // return false; // Or proceed with caution depending on requirements
        }


        String sql = "DELETE FROM jadwal_matkul WHERE id_jm = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idJm);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // New method: Find jadwal_matkul by user ID
    public List<jadwal_matkul> findByUserId(int idUser) {
        List<jadwal_matkul> list = new ArrayList<>();
        String sql = "SELECT * FROM jadwal_matkul WHERE id_user = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUser);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                jadwal_matkul jm = new jadwal_matkul();
                jm.setIdJm(rs.getInt("id_jm"));
                jm.setIdMatkul(rs.getInt("id_matkul"));
                jm.setIdJadwal(rs.getInt("id_jadwal"));
                jm.setIdUser(rs.getInt("id_user")); // Assuming id_user is part of jadwal_matkul
                // Set other fields as necessary
                list.add(jm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> findSchedulesDetailsForUser(int userId) {
        List<Object[]> scheduleDetailsList = new ArrayList<>();
        String sql = """
            SELECT j.hari, j.sesi, m.title
            FROM jadwal_matkul jm
            JOIN jadwal j ON jm.id_jadwal = j.id_jadwal
            JOIN matkul m ON jm.id_matkul = m.id_matkul
            WHERE jm.id_user = ?
            ORDER BY m.title, j.hari, j.sesi
            """; // Added ORDER BY for consistent display

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("hari");
                row[1] = rs.getInt("sesi"); // Assuming sesi is an INT
                row[2] = rs.getString("title"); // Matkul title
                scheduleDetailsList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching schedule details for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return scheduleDetailsList;
    }
}
