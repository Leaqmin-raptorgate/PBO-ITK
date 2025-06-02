package ModelLayer.Class;

import java.time.LocalDate;
import ModelLayer.Abstract.ScheduleItem;
import Enumeration.TaskStatus;

public class Task extends ScheduleItem {

    private String description;
    private LocalDate deadline;       // Stored as TEXT "YYYY-MM-DD" in DB
    private TaskStatus currentStatus; // Mapped to status_selesai INTEGER (0/1) in DB
    private int priority;             // 0: Low, 1: Medium, 2: High (as per your DB schema)
    private int idUser;               // Foreign key to user table
    private Integer idJm; // FK to ScheduledCourse (jadwal_matkul)

    // Full constructor
    public Task(int taskId, String title, String description, LocalDate deadline,
                TaskStatus status, int priority, int idUser, Integer idJm) {
        super(taskId, title); // Call ScheduleItem constructor (itemId is now int)
        this.description = description;
        this.deadline = deadline;
        this.currentStatus = status;
        this.priority = priority;
        this.idUser = idUser;
        this.idJm = idJm;
    }

    // No-args constructor (useful for DAOs)
    public Task() {
        super(); // Calls ScheduleItem's no-args constructor
    }

    // --- Getters ---
    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public TaskStatus getCurrentStatus() {
        return currentStatus;
    }

    public int getPriority() {
        return priority;
    }

    public int getIdUser() {
        return idUser;
    }

    public Integer getIdJm() { // Returns Integer, can be null
        return idJm;
    }

    // Convenience getter for taskId (from ScheduleItem.itemId)
    public int getTaskId() {
        return getItemId();
    }

    // --- Setters ---
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    // For status, we use updateStatus method primarily
    // but a direct setter might be needed by DAO during object hydration
    public void setCurrentStatus(TaskStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setPriority(int priority) {
        // You might want to add validation here (e.g., 0, 1, or 2)
        this.priority = priority;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setIdJm(Integer idJm) { this.idJm = idJm; } // Takes Integer, can be null

    // Convenience setter for taskId (from ScheduleItem.itemId)
    public void setTaskId(int taskId) {
        setItemId(taskId);
    }

    // Method to update status with conditional logic based on integer priority
    public void updateStatus(TaskStatus newStatus) {
        System.out.println("Task '" + getTitle() + "' (ID: " + getItemId() + "): Status changing from " +
                           this.currentStatus + " to " + newStatus +
                           ", Priority Level: " + this.priority);
        this.currentStatus = newStatus; // Update the status in the object

        // Conditional logic based on integer priority
        // Assuming 0: Low, 1: Medium, 2: High as per your DB comment
        switch (this.priority) {
            case 2: // High priority
                System.out.println(">> HIGH PRIORITY Task Action: Performing special logging and notification.");
                // Placeholder for actual distinct logic for high priority
                break;
            case 1: // Medium priority
                System.out.println(">> MEDIUM PRIORITY Task Action: Standard logging procedure.");
                // Placeholder for actual distinct logic for medium priority
                break;
            case 0: // Low priority
            default: // Also catches any other priority numbers not explicitly handled
                System.out.println(">> LOW/DEFAULT PRIORITY Task Action: Minimal actions taken.");
                // Placeholder for actual distinct logic for low/default priority
                break;
        }
        // In a real application, you would call TaskDAO here to persist this change to the database
        // and then ensure the UI is refreshed.
    }

    // Implementation for abstract methods from ScheduleItem
    @Override
    public String getItemType() {
        return "Task";
    }

    @Override
    public String getDisplaySummary() {
        String priorityStr;
        switch (priority) {
            case 2: priorityStr = "High"; break;
            case 1: priorityStr = "Medium"; break;
            case 0: priorityStr = "Low"; break;
            default: priorityStr = "Unknown (" + priority + ")"; break;
        }
        return "Task: " + getTitle() + " (Due: " + deadline + ", Priority: " + priorityStr + ", Status: " + currentStatus + ")";
    }

    @Override
    public String toString() {
        return "Task{" +
               "taskId=" + getItemId() +
               ", title='" + getTitle() + '\'' +
               ", description='" + description + '\'' +
               ", deadline=" + deadline +
               ", currentStatus=" + currentStatus +
               ", priority=" + priority +
               ", idUser=" + idUser +
               ", idJm=" + (idJm == null ? "N/A" : idJm) +
               '}';
    }
}