package ModelLayer.Class;

public class ScheduledCourse {
    private int id_jm;       // Primary key from jadwal_matkul (PK)
    private int id_jadwal;   // Foreign Key to jadwal table
    private int id_matkul;   // Foreign Key to matkul table

    // Optional: Hold actual Jadwal and Matkul objects for convenience
    // private Jadwal jadwal;
    // private Matkul matkul;

    public ScheduledCourse(int id_jm, int id_jadwal, int id_matkul) {
        this.id_jm = id_jm;
        this.id_jadwal = id_jadwal;
        this.id_matkul = id_matkul;
    }

    public ScheduledCourse() {}

    // Getters
    public int getIdJm() {
        return id_jm;
    }

    public int getIdJadwal() {
        return id_jadwal;
    }

    public int getIdMatkul() {
        return id_matkul;
    }

    // Setters
    public void setIdJm(int id_jm) {
        this.id_jm = id_jm;
    }

    public void setIdJadwal(int id_jadwal) {
        this.id_jadwal = id_jadwal;
    }

    public void setIdMatkul(int id_matkul) {
        this.id_matkul = id_matkul;
    }

    @Override
    public String toString() {
        return "ScheduledCourse{" +
               "id_jm=" + id_jm +
               ", id_jadwal=" + id_jadwal +
               ", id_matkul=" + id_matkul +
               '}';
    }
}