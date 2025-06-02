package ModelLayer.Class;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int idUser;
    private String username;
    private String password; // Hashed password

    // Associations
    private List<Task> userTasks;
    private List<CourseMaterial> userCourseMaterials;

    public User(int idUser, String username, String password) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.userTasks = new ArrayList<>();
        this.userCourseMaterials = new ArrayList<>();
    }

    public User() {
        this.userTasks = new ArrayList<>();
        this.userCourseMaterials = new ArrayList<>();
    }

    // --- Getters and Setters ---
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // Password should be hashed before setting
    public void setPassword(String password) {
        this.password = password;
    }

    // --- Task management ---
    public List<Task> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<Task> tasks) {
        this.userTasks = tasks;
    }

    public void addTask(Task task) {
        if (task != null) {
            this.userTasks.add(task);
            // Optionally, if you want the Task to reference its owner:
            // task.setOwningUser(this);
        }
    }

    public void removeTask(Task task) {
        this.userTasks.remove(task);
    }

    // --- CourseMaterial management ---
    public List<CourseMaterial> getUserCourseMaterials() {
        return userCourseMaterials;
    }

    public void setUserCourseMaterials(List<CourseMaterial> materials) {
        this.userCourseMaterials = materials;
    }

    public void addCourseMaterial(CourseMaterial material) {
        this.userCourseMaterials.add(material);
    }

    @Override
    public String toString() {
        return "User{" +
               "idUser=" + idUser +
               ", username='" + username + '\'' +
               '}';
    }
}