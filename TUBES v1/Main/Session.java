package Main;

import ModelLayer.Class.User; // Assuming your User model is in com.tugasku.model

public class Session {

    private User currentUser;

    public Session() {
        this.currentUser = null; // No user logged in initially
    }

    /**
     * Sets the currently logged-in user.
     * Typically called after a successful login.
     * @param user The User object representing the logged-in user.
     */
    public void loginUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("Session: User '" + user.getUsername() + "' logged in.");
        }
    }

    /**
     * Clears the current user session.
     * Typically called on logout.
     */
    public void logoutUser() {
        if (this.currentUser != null) {
            System.out.println("Session: User '" + this.currentUser.getUsername() + "' logged out.");
        }
        this.currentUser = null;
    }

    /**
     * Gets the currently logged-in User object.
     * @return The current User object, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return this.currentUser != null;
    }
}