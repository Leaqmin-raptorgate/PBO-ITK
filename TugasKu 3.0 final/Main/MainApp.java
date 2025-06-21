package Main;

// ===== Imports =====
import javax.swing.*;
import java.awt.*;
import layerUI.LoginPagePanel;
import layerUI.HomePagePanel;
import layerUI.JadwalPanel;
import layerUI.DetailJadwalPanel;
import layerUI.DetailTugasPanel;

// ===== Class Declaration =====
public class MainApp {

    // ===== Fields =====
    private HomePagePanel homeApp;
    private DetailJadwalPanel tambahEditAppPage; // Keep this as a field
    private Session userSession;
    private JFrame mainFrame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private DetailTugasPanel detailTugasPage;
    private JadwalPanel jadwalPanel;

    // ===== Constants for Screen Names =====
    public static final String LOGIN_SCREEN = "LOGIN"; // For Login Page
    public static final String HOME_SCREEN = "HOME"; // For Home Page
    public static final String DETAIL_TUGAS_SCREEN = "DETAIL_TUGAS"; // For Detail Tugas Page
    public static final String TAMBAH_EDIT_APP_SCREEN = "TAMBAH_EDIT_APP"; // For Jadwal Detail Page
    public static final String DETAIL_SCREEN = "DETAIL_APP"; // For Jadwal Page

    // ===== Constructors =====
    public MainApp() {
        userSession = new Session();
        mainFrame = new JFrame("TugasKu Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setMinimumSize(new Dimension(500, 700));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        LoginPagePanel loginPage = new LoginPagePanel(this, userSession);
        detailTugasPage = new DetailTugasPanel(this, userSession);
        tambahEditAppPage = new DetailJadwalPanel(this, userSession); // Create DetailJadwalPanel here
        jadwalPanel = new JadwalPanel(this, userSession);

        cardPanel.add(loginPage, LOGIN_SCREEN);
        cardPanel.add(detailTugasPage, DETAIL_TUGAS_SCREEN);
        cardPanel.add(tambahEditAppPage, TAMBAH_EDIT_APP_SCREEN);
        cardPanel.add(jadwalPanel, DETAIL_SCREEN);

        mainFrame.add(cardPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        navigateTo(LOGIN_SCREEN);
    }

    // ===== Getters =====\
    //HomePagePanel is the main panel for the home screen
    public HomePagePanel getHomePagePanel() {
        return homeApp;
    }

    // DetailJadwalPanel is used for detail info about the task inside the schedule
    public DetailJadwalPanel getDetailJadwalPanel() {
        return tambahEditAppPage;
    }

    // DetailTugasPanel is used for detail info about the task
    public DetailTugasPanel getDetailTugasPanel() {
        return detailTugasPage;
    }

    // JadwalPanel is used for add or edit detail info of the schedule
    public JadwalPanel getJadwalPanel() {
        return jadwalPanel;
    }

    // ===== Setters =====
    // Sets the HomePagePanel and adds it to the card layout
    public void setHomePagePanel(HomePagePanel panel) {
        this.homeApp = panel;
        cardPanel.add(panel, HOME_SCREEN);
    }

    // ===== Navigation & Utility Methods =====
    // This method navigates to the specified screen using CardLayout
    public void navigateTo(String screenName) {
        cardLayout.show(cardPanel, screenName);
        System.out.println("Navigated to: " + screenName); // For debugging
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            db.DBConnectionUtil.initializeDatabase();
            new MainApp();
        });
    }
}
