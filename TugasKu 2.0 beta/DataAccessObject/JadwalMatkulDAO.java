package DataAccessObject;

// ===== Imports =====
import db.DBConnectionUtil;
import ModelLayer.Class.jadwal_matkul;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ===== Class Declaration =====
public class JadwalMatkulDAO {

    // ===== Public CRUD Methods =====
    // Find id_jm by id_matkul and id_jadwal.
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

    // Find all id_jm entries associated with a given id_matkul.
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
            e.printStackTrace();
        }
        return idJms;
    }

    // Find id_matkul by id_jm.
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

    // Find id_jadwal by id_jm.
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

    // Create a jadwal_matkul entry.
    public boolean create(jadwal_matkul jm) {
        String sql = "INSERT INTO jadwal_matkul (id_matkul, id_jadwal, id_user) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, jm.getIdMatkul());
            pstmt.setInt(2, jm.getIdJadwal());
            pstmt.setInt(3, jm.getIdUser());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        jm.setIdJm(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a jadwal_matkul entry by id_jm.
    public boolean deleteByIdJm(int idJm) {
        TaskDAO taskDAO = new TaskDAO();
        boolean tasksDeleted = taskDAO.deleteTasksByIdJm(idJm);
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

    // ===== Additional Public Methods =====

    // Find jadwal_matkul by user ID.
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
                jm.setIdUser(rs.getInt("id_user"));
                list.add(jm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Find schedule details (hari, sesi, title) for a user.
    public List<Object[]> findSchedulesDetailsForUser(int userId) {
        List<Object[]> scheduleDetailsList = new ArrayList<>();
        String sql = """
            SELECT j.hari, j.sesi, m.title
            FROM jadwal_matkul jm
            JOIN jadwal j ON jm.id_jadwal = j.id_jadwal
            JOIN matkul m ON jm.id_matkul = m.id_matkul
            WHERE jm.id_user = ?
            ORDER BY m.title, j.hari, j.sesi
            """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("hari");
                row[1] = rs.getInt("sesi");
                row[2] = rs.getString("title");
                scheduleDetailsList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching schedule details for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return scheduleDetailsList;
    }

    // Find id_jm by id_matkul, id_jadwal, and id_user.
    public Integer findIdJmByMatkulAndJadwalAndUser(int idMatkul, int idJadwal, int idUser) {
        String sql = "SELECT id_jm FROM jadwal_matkul WHERE id_matkul = ? AND id_jadwal = ? AND id_user = ?";
        try (Connection conn = db.DBConnectionUtil.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMatkul);
            pstmt.setInt(2, idJadwal);
            pstmt.setInt(3, idUser);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_jm");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
