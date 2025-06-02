package DataAccessObject;

import ModelLayer.Class.matkul;
import db.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatkulDAO {
    public List<matkul> findAll() {
        List<matkul> list = new ArrayList<>();
        String sql = "SELECT * FROM matkul";
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                matkul m = new matkul();
                m.setIdMatkul(rs.getInt("id_matkul"));
                m.setTitle(rs.getString("title"));
                m.setDescription(rs.getString("description"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<matkul> findByUserTasks(int userId) {
        List<matkul> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT m.id_matkul, m.title, m.description
            FROM tugas t
            JOIN jadwal_matkul jm ON t.id_jm = jm.id_jm
            JOIN matkul m ON jm.id_matkul = m.id_matkul
            WHERE t.id_user = ?
        """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                matkul m = new matkul();
                m.setIdMatkul(rs.getInt("id_matkul"));
                m.setTitle(rs.getString("title"));
                m.setDescription(rs.getString("description"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public matkul findMatkulByJadwalMatkulId(int idJm) {
        matkul result = null;
        String sql = "SELECT m.* FROM jadwal_matkul jm JOIN matkul m ON jm.id_matkul = m.id_matkul WHERE jm.id_jm = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idJm);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = new matkul();
                    result.setIdMatkul(rs.getInt("id_matkul"));
                    result.setTitle(rs.getString("title"));
                    result.setDescription(rs.getString("description"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // New method: Find matkul by ID
    public matkul findById(int idMatkul) {
        String sql = "SELECT * FROM matkul WHERE id_matkul = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMatkul);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                matkul m = new matkul();
                m.setIdMatkul(rs.getInt("id_matkul"));
                m.setTitle(rs.getString("title"));
                m.setDescription(rs.getString("description"));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // New method: Find matkul by title
    public matkul findByTitle(String title) {
        String sql = "SELECT * FROM matkul WHERE title = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                matkul m = new matkul();
                m.setIdMatkul(rs.getInt("id_matkul"));
                m.setTitle(rs.getString("title"));
                m.setDescription(rs.getString("description"));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // New method: Create matkul and return generated ID
    public int create(matkul m) {
        String sql = "INSERT INTO matkul (title, description) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getDescription());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the new id_matkul
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Or -1 to indicate failure
    }
     // Update an existing matkul record
    public boolean update(matkul matkulObj) {
        String sql = "UPDATE matkul SET title = ?, description = ? WHERE id_matkul = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matkulObj.getTitle());
            pstmt.setString(2, matkulObj.getDescription());
            pstmt.setInt(3, matkulObj.getIdMatkul());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating matkul: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // New method: Delete matkul by ID
    public boolean deleteById(int idMatkul) {
        String sql = "DELETE FROM matkul WHERE id_matkul = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMatkul);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<matkul> findByUserId(int userId) {
        List<matkul> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT m.id_matkul, m.title, m.description
            FROM jadwal_matkul jm
            JOIN matkul m ON jm.id_matkul = m.id_matkul
            WHERE jm.id_user = ?
        """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                matkul m = new matkul();
                m.setIdMatkul(rs.getInt("id_matkul"));
                m.setTitle(rs.getString("title"));
                m.setDescription(rs.getString("description"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
