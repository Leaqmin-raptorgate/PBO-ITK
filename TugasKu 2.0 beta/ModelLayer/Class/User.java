package ModelLayer.Class;

// ===== Imports =====
import java.util.ArrayList;
import java.util.List;

// ===== Class Declaration =====
public class User {

    // ===== Fields =====
    private int idUser; // Unique identifier for the user
    private String username; // Username for the user, must be unique
    private String password; // Password for the user

    // Associations
    private List<Task> userTasks; // List of tasks associated with the user
    private List<CourseMaterial> userCourseMaterials; // List of course materials associated with the user

    // ===== Constructors =====
    // This constructor initializes the user with an ID, username, and password
    public User(int idUser, String username, String password) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.userTasks = new ArrayList<>();
        this.userCourseMaterials = new ArrayList<>();
    }

    // Default constructor initializes empty lists for tasks and course materials
    public User() {
        this.userTasks = new ArrayList<>();
        this.userCourseMaterials = new ArrayList<>();
    }

    // ===== Getters =====
    // This method returns the user's ID
    public int getIdUser() {
        return idUser;
    }

    // This method returns the user's username
    public String getUsername() {
        return username;
    }

    // This method returns the user's password
    public String getPassword() {
        return password;
    }

    // This method returns the list of tasks associated with the user
    public List<Task> getUserTasks() {
        return userTasks;
    }

    // This method returns the list of course materials associated with the user
    public List<CourseMaterial> getUserCourseMaterials() {
        return userCourseMaterials;
    }

    // ===== Setters =====
    // This method allows for setting the user's ID
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    // This method allows for setting the user's username   
    public void setUsername(String username) {
        this.username = username;
    }

    // This method allows for setting the user's password  
    public void setPassword(String password) {
        this.password = password;
    }

    // This method allows for setting the user's tasks
    public void setUserTasks(List<Task> tasks) {
        this.userTasks = tasks;
    }

    // This method allows for setting the user's course materials
    public void setUserCourseMaterials(List<CourseMaterial> materials) {
        this.userCourseMaterials = materials;
    }

    // ===== Task Management =====
    // This method allows for adding a task to the user's task list
    public void addTask(Task task) {
        if (task != null) {
            this.userTasks.add(task);
        }
    }

    // This method allows for removing a task from the user's task list
    public void removeTask(Task task) {
        this.userTasks.remove(task);
    }

    // ===== CourseMaterial Management =====
    public void addCourseMaterial(CourseMaterial material) {
        this.userCourseMaterials.add(material);
    }

    // ===== Utility Methods =====
    // This method is primarily for debugging and logging purposes
    @Override
    public String toString() {
        return "User{" +
               "idUser=" + idUser +
               ", username='" + username + '\'' +
               '}';
    }
}