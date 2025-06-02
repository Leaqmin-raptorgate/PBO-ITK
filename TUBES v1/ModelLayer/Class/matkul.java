package ModelLayer.Class;

public class matkul {
    private int id_matkul;
    private String title;
    private String description;

    // private List<Task> tasksForThisMatkul; // Optional: if Matkul directly holds its tasks

    public matkul(int id_matkul, String title, String description) {
        this.id_matkul = id_matkul;
        this.title = title;
        this.description = description;
        // this.tasksForThisMatkul = new ArrayList<>(); // Uncomment if adding list of tasks
    }

    public matkul() {
        // this.tasksForThisMatkul = new ArrayList<>(); // Uncomment if adding list of tasks
    }

    // Getters
    public int getIdMatkul() {
        return id_matkul;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setIdMatkul(int idMatkul) {
        this.id_matkul = idMatkul;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Optional: Methods to manage list of tasks if included
    // public List<Task> getTasksForThisMatkul() { return tasksForThisMatkul; }
    // public void setTasksForThisMatkul(List<Task> tasks) { this.tasksForThisMatkul = tasks; }
    // public void addTask(Task task) { this.tasksForThisMatkul.add(task); }

    @Override
    public String toString() {
        return title; // This will show only the material name in JComboBox
    }
}

