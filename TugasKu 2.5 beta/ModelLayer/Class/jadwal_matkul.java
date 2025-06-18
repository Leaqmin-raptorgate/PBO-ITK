package ModelLayer.Class;

// ===== Class Declaration =====
public class jadwal_matkul {
    private int id_jm;
    private int id_matkul;
    private int id_jadwal;
    private int id_user; // If schedules are user-specific

    // Constructors
    public jadwal_matkul() {}

    // Getters and Setters
    public int getIdJm() {
        return id_jm;
    }

    public void setIdJm(int id_jm) {
        this.id_jm = id_jm;
    }

    public int getIdMatkul() {
        return id_matkul;
    }

    public void setIdMatkul(int id_matkul) {
        this.id_matkul = id_matkul;
    }

    public int getIdJadwal() {
        return id_jadwal;
    }

    public void setIdJadwal(int id_jadwal) {
        this.id_jadwal = id_jadwal;
    }

    public int getIdUser() {
        return id_user;
    }

    public void setIdUser(int id_user) {
        this.id_user = id_user;
    }
}