package DataAccessObject;

import ModelLayer.Class.Task;
import Enumeration.TaskStatus; // Assuming TaskStatus is in model package
import db.DBConnectionUtil; // Your DB connection utility
import Interface.CrudOperations;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDAO implements CrudOperations<Task, Integer> {

    // Method to add a new task to the database (as requested by lecturer)
    // This is the 'Create' part of CRUD
    public boolean addTask(Task task) {
        // SQL query to insert a new task
        // Columns: title, description, status_selesai, priority, deadline, id_jm, id_user
        // id_tugas is AUTOINCREMENT
        String sql = "INSERT INTO Tugas (title, description, status_selesai, priority, deadline, id_jm, id_user) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());

            // Convert TaskStatus enum to Integer for status_selesai (0 or 1)
            pstmt.setInt(3, task.getCurrentStatus() == TaskStatus.SUDAH_SELESAI ? 1 : 0);

            pstmt.setInt(4, task.getPriority()); // Assuming priority is already an int (0,1,2) in Task model

            // Convert LocalDate deadline to String for TEXT column
            if (task.getDeadline() != null) {
                pstmt.setString(5, task.getDeadline().toString()); // Stores as "YYYY-MM-DD"
            } else {
                pstmt.setNull(5, Types.VARCHAR); // Or Types.NULL if TEXT maps to that
            }

            // Handle nullable id_jm
            if (task.getIdJm() != null) {
                pstmt.setInt(6, task.getIdJm());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            pstmt.setInt(7, task.getIdUser());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Optionally, retrieve the generated id_tugas and set it in the task object
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        task.setItemId(generatedKeys.getInt(1)); // Set the auto-generated ID
                        // task.setTaskId(generatedKeys.getInt(1)); // if you have a specific setTaskId
                    }
                }
                return true; // Task added successfully
            } else {
                return false; // Task not added
            }

        } catch (SQLException e) {
            System.err.println("Error adding task: " + e.getMessage());
            e.printStackTrace(); // For detailed error information
            return false;
        }
    }

    // --- Implementation of CrudOperations<Task, Integer> ---

    @Override
    public boolean create(Task task) {
        return addTask(task); // Delegate to the specifically requested addTask method
    }

    @Override
    public Optional<Task> findById(Integer idTugas) {
        String sql = "SELECT * FROM Tugas WHERE id_tugas = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTugas);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding task by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM Tugas";
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all tasks: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Tasks loaded: " + tasks.size());
        return tasks;
    }

    
    // Example: Find tasks by User ID
    public List<Task> findByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM Tugas WHERE id_user = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding tasks by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return tasks;
    }
    public List<Object[]> findJadwalMatkulForUser(int userId) {
    List<Object[]> list = new ArrayList<>();
    String sql = """
        SELECT j.hari, j.sesi, m.title
        FROM tugas t
        JOIN jadwal_matkul jm ON t.id_jm = jm.id_jm
        JOIN jadwal j ON jm.id_jadwal = j.id_jadwal
        JOIN matkul m ON jm.id_matkul = m.id_matkul
        WHERE t.id_user = ?
    """;
    try (Connection conn = db.DBConnectionUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            String hari = rs.getString("hari");
            int sesi = rs.getInt("sesi");
            String title = rs.getString("title");
            list.add(new Object[]{hari, sesi, title});
            }
        } catch (SQLException e) {
            System.err.println("Error finding jadwal by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Task> findByUserMatkulAndStatus(int userId, int idMatkul, int status) {
    List<Task> tasks = new ArrayList<>();
    String sql = """
        SELECT t.*
        FROM tugas t
        JOIN jadwal_matkul jm ON t.id_jm = jm.id_jm
        WHERE t.id_user = ? AND jm.id_matkul = ? AND t.status_selesai = ?
    """;
    try (Connection conn = db.DBConnectionUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, idMatkul);
            pstmt.setInt(3, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    @Override
    public boolean update(Task task) {
        String sql = "UPDATE Tugas SET title = ?, description = ?, status_selesai = ?, " +
                     "priority = ?, deadline = ?, id_jm = ?, id_user = ? WHERE id_tugas = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setInt(3, task.getCurrentStatus() == TaskStatus.SUDAH_SELESAI ? 1 : 0);
            pstmt.setInt(4, task.getPriority());
            pstmt.setString(5, task.getDeadline() != null ? task.getDeadline().toString() : null);
            if (task.getIdJm() != null) {
                pstmt.setInt(6, task.getIdJm());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setInt(7, task.getIdUser());
            pstmt.setInt(8, task.getItemId()); // Where clause for id_tugas

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer idTugas) {
        String sql = "DELETE FROM Tugas WHERE id_tugas = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTugas);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // Helper method to map a ResultSet row to a Task object
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setItemId(rs.getInt("id_tugas")); // Corresponds to id_tugas
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCurrentStatus(rs.getInt("status_selesai") == 1 ? TaskStatus.SUDAH_SELESAI : TaskStatus.BELUM_SELESAI);
        task.setPriority(rs.getInt("priority"));
        
        String deadlineStr = rs.getString("deadline");
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            try {
                task.setDeadline(LocalDate.parse(deadlineStr)); // Assumes "YYYY-MM-DD"
            } catch (java.time.format.DateTimeParseException e) {
                System.err.println("Warning: Could not parse deadline '" + deadlineStr + "' for task ID " + task.getItemId());
                task.setDeadline(null); // Or handle error more robustly
            }
        } else {
            task.setDeadline(null);
        }
        
        int idJmVal = rs.getInt("id_jm");
        if (rs.wasNull()) {
            task.setIdJm(null);
        } else {
            task.setIdJm(idJmVal);
        }
        task.setIdUser(rs.getInt("id_user"));
        return task;
    }
        public boolean deleteTasksByIdJm(int idJm) {
        String sql = "DELETE FROM Tugas WHERE id_jm = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idJm);
            pstmt.executeUpdate(); // We don't strictly need to check affectedRows here,
                                   // as it's okay if no tasks existed for that id_jm.
            return true; // Indicate success or that the operation was attempted.
        } catch (SQLException e) {
            System.err.println("Error deleting tasks by id_jm: " + idJm + " - " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate failure
        }
    }
}                                                                                             