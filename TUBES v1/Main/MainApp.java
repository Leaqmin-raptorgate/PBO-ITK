package Main;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

import layerUI.LoginPagePanel;
import layerUI.HomePagePanel;
import layerUI.JadwalPanel;
import layerUI.DetailJadwalPanel;
import layerUI.DetailTugasPanel;
import Main.Session;



public class MainApp {

    HomePagePanel homeApp;
    DetailJadwalPanel tambahEditAppPage; // Keep this as a field
    private Session userSession;
    private JFrame mainFrame;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public static final String LOGIN_SCREEN = "LOGIN";
    public static final String HOME_SCREEN = "HOME";
    public static final String DETAIL_TUGAS_SCREEN = "DETAIL_TUGAS";
    public static final String TAMBAH_EDIT_APP_SCREEN = "TAMBAH_EDIT_APP"; // For Jadwal Detail
    public static final String DETAIL_SCREEN = "DETAIL_APP";
    private DetailTugasPanel detailTugasPage;
    private JadwalPanel jadwalPanel;

    public MainApp() {
        userSession = new Session();
        mainFrame = new JFrame("TugasKu Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    public void setHomePagePanel(HomePagePanel panel) {
        this.homeApp = panel;
        cardPanel.add(panel, HOME_SCREEN);
    }

    public HomePagePanel getHomePagePanel() {
        return homeApp;
    }

    public DetailJadwalPanel getDetailJadwalPanel() {
        return tambahEditAppPage;
    }

    public DetailTugasPanel getDetailTugasPanel() {
    return detailTugasPage;
    }

    public JadwalPanel getJadwalPanel() {
        return jadwalPanel;
    }

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
