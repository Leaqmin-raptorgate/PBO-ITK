package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionUtil {
    // Store the database file in the working directory.
    private static final String DB_FILENAME = System.getProperty("user.dir") + File.separator + "TugasKu.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILENAME;

    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Method to initialize the database schema if the DB file doesn't exist
    public static void initializeDatabase() {
        File dbFile = new File(DB_FILENAME);
        boolean dbNeedsInitialization = !dbFile.exists();

        if (dbNeedsInitialization) {
            System.out.println("Database file '" + DB_FILENAME + "' not found. Creating new database and schema...");
        } else {
            System.out.println("Existing database file '" + DB_FILENAME + "' found. Ensuring schema integrity (using IF NOT EXISTS)...");
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Table: user (for login)
            stmt.execute("CREATE TABLE IF NOT EXISTS user (" +
                         "id_user INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "username TEXT NOT NULL UNIQUE, " +
                         "password TEXT NOT NULL" +
                         ");");
            if (dbNeedsInitialization) 
                System.out.println("- Table 'user' created.");

            // Table: jadwal (schedule)
            stmt.execute("CREATE TABLE IF NOT EXISTS jadwal (" +
                         "id_jadwal INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "hari TEXT NOT NULL, " +
                         "sesi INTEGER NOT NULL" +
                         ");");
            if (dbNeedsInitialization) 
                System.out.println("- Table 'jadwal' created.");

            // Table: matkul (course)
            stmt.execute("CREATE TABLE IF NOT EXISTS matkul (" +
                         "id_matkul INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "title TEXT NOT NULL UNIQUE, " +
                         "description TEXT" +
                         ");");
            if (dbNeedsInitialization) 
                System.out.println("- Table 'matkul' created.");

            // Table: jadwal_matkul (junction table for jadwal and matkul)
            stmt.execute("CREATE TABLE IF NOT EXISTS jadwal_matkul (" +
                         "id_jm INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "id_matkul INTEGER NOT NULL, " +
                         "id_jadwal INTEGER NOT NULL, " +
                         "id_user INTEGER NOT NULL, " +
                         "FOREIGN KEY (id_matkul) REFERENCES matkul (id_matkul) ON DELETE CASCADE ON UPDATE CASCADE, " +
                         "FOREIGN KEY (id_jadwal) REFERENCES jadwal (id_jadwal) ON DELETE CASCADE ON UPDATE CASCADE, " +
                         "FOREIGN KEY (id_user) REFERENCES user (id_user) ON DELETE CASCADE ON UPDATE CASCADE, " +
                         "UNIQUE (id_matkul, id_jadwal, id_user)" +
                         ");");
            if (dbNeedsInitialization) 
                System.out.println("- Table 'jadwal_matkul' created.");

            // Table: Tugas (tasks/assignments)
            stmt.execute("CREATE TABLE IF NOT EXISTS Tugas (" +
                         "id_tugas INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "title TEXT NOT NULL, " +
                         "description TEXT, " +
                         "status_selesai INTEGER NOT NULL DEFAULT 0, " +
                         "priority INTEGER NOT NULL DEFAULT 0, " +
                         "deadline TEXT, " +
                         "id_jm INTEGER, " +
                         "id_user INTEGER NOT NULL, " +
                         "FOREIGN KEY (id_jm) REFERENCES jadwal_matkul (id_jm) ON DELETE SET NULL ON UPDATE CASCADE, " +
                         "FOREIGN KEY (id_user) REFERENCES user (id_user) ON DELETE CASCADE ON UPDATE CASCADE" +
                         ");");
            if (dbNeedsInitialization) 
                System.out.println("- Table 'Tugas' created.");

            if (dbNeedsInitialization) {
                System.out.println("Database schema initialized successfully.");
            } else {
                System.out.println("Schema check complete. Tables ensured (IF NOT EXISTS).");
            }

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Main method for quick testing of database initialization
    public static void main(String[] args) {
        System.out.println("Attempting to initialize database...");
        initializeDatabase();
        System.out.println("Database initialization attempt finished.");

        try (Connection c = getConnection()) {
            System.out.println("Successfully connected to the database: " + DB_FILENAME);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database after initialization: " + e.getMessage());
        }
    }
}
