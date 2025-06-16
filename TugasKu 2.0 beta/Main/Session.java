package Main;

// ===== Imports =====
import ModelLayer.Class.User;

// ===== Imports =====
public class Session {

    // ===== Attributes =====
    private User currentUser;

    // ===== Constructors =====
    public Session() {
        this.currentUser = null; // No user logged in initially
    }

    // ===== Getters =====
    // Returns the currently logged-in user.
    public User getCurrentUser() {
        return this.currentUser;
    }

    // ===== Setters =====
    // Sets the current user session.
    public void loginUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("Session: User '" + user.getUsername() + "' logged in.");
        }
    }

    // ===== Session Management =====
    // Logs out the current user and clears the session.
    public void logoutUser() {
        if (this.currentUser != null) {
            System.out.println("Session: User '" + this.currentUser.getUsername() + "' logged out.");
        }
        this.currentUser = null;
    }

    // Checks if a user is currently logged in.
    public boolean isUserLoggedIn() {
        return this.currentUser != null;
    }
}