package ModelLayer.Class;

// ===== Class Declaration =====
public class jadwal {
    private int id_jadwal;
    private String judul;    // Title or material
    private String hari;     // e.g., "Senin", "Selasa"
    private int sesi;        // Session number
    private int idUser;      // Owner's user ID

    // ===== Constructors =====
    public jadwal(int id_jadwal, String judul, String hari, int sesi, int idUser) {
        this.id_jadwal = id_jadwal;
        this.judul = judul;
        this.hari = hari;
        this.sesi = sesi;
        this.idUser = idUser;
    }

    public jadwal() {}

    // Getters
    public int getIdJadwal() {
        return id_jadwal;
    }

    public String getJudul() {
        return judul;
    }

    public String getHari() {
        return hari;
    }

    public int getSesi() {
        return sesi;
    }

    public int getIdUser() {
        return idUser;
    }

    // Setters
    public void setIdJadwal(int id_jadwal) {
        this.id_jadwal = id_jadwal;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public void setSesi(int sesi) {
        this.sesi = sesi;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    // ===== Utility Methods =====
    @Override
    public String toString() {
        return "Jadwal{" +
               "idJadwal=" + id_jadwal +
               ", judul='" + judul + '\'' +
               ", hari='" + hari + '\'' +
               ", sesi=" + sesi +
               ", idUser=" + idUser +
               '}';
    }
}
