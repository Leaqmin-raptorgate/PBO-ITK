package ModelLayer.Class;

// ===== Class Declaration =====
public class matkul {
    private int id_matkul;
    private String title;
    private String description;
    private int idUser; // Add this field

    // ===== Constructors =====
    // Parameterized constructor to initialize all fields
    public matkul(int id_matkul, String title, String description, int idUser) {
        this.id_matkul = id_matkul;
        this.title = title;
        this.description = description;
        this.idUser = idUser;
    }

    public matkul() {
    }

    // ===== Getters =====
    public int getIdMatkul() {
        return id_matkul;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIdUser() {
        return idUser;
    }

    // ===== Setters =====
    public void setIdMatkul(int idMatkul) {
        this.id_matkul = idMatkul;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    // ===== Utility Methods =====
    // This method is used to display the material name in JComboBox
    @Override
    public String toString() {
        return title;
    }
}