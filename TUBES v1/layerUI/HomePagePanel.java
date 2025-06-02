package layerUI;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

import Main.MainApp;
import Main.Session;
import ModelLayer.Class.Task;
import ModelLayer.Class.matkul;
import ModelLayer.Class.jadwal_matkul; // Assuming you have a model for jadwal_matkul

import javax.swing.table.DefaultTableModel;

import DataAccessObject.MatkulDAO;
import DataAccessObject.TaskDAO;
import DataAccessObject.JadwalMatkulDAO; // Assuming you have this DAO

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingUtilities; // Import SwingUtilities

/**
 *
 * @author madit
 */
public class HomePagePanel extends javax.swing.JPanel {
    private MainApp mainApp;
    private Session userSession;
    private DefaultTableModel tableModelTugasPenting;
    private DefaultTableModel tableModelTugasDeadline;
    private DetailJadwalPanel detailJadwalPanel;

    private List<Task> tugasPentingList = new java.util.ArrayList<>();
    private List<Task> tugasDeadlineList = new java.util.ArrayList<>();
    private List<Task> jadwalTaskList = new java.util.ArrayList<>(); // Renamed for clarity

    private void handleTaskDoubleClick(int row, Object taskTitle, String tableType) {
        System.out.println("Double-clicked row: " + row + ", Task: " + taskTitle + ", Table: " + tableType);
        if (mainApp != null) {
            if (tableType.equals("Jadwal")) {
                Object hari = jTableTugasPenting2.getValueAt(row, 0);
                Object sesi = jTableTugasPenting2.getValueAt(row, 1);
                Object judul = jTableTugasPenting2.getValueAt(row, 2);
                System.out.println("DEBUG: Jadwal row values - Hari: " + hari + ", Sesi: " + sesi + ", Judul: " + judul);

                matkul selectedMatkul = findMatkulByTitle(judul.toString());
                System.out.println("DEBUG: findMatkulByTitle('" + judul + "') returned: " + selectedMatkul);

                if (detailJadwalPanel != null && selectedMatkul != null) {
                    System.out.println("DEBUG: Calling detailJadwalPanel.showTasksForMatkul(" + selectedMatkul + ")");
                    detailJadwalPanel.showTasksForMatkul(selectedMatkul);
                } else {
                    System.out.println("DEBUG: detailJadwalPanel or selectedMatkul is null in double-click handler");
                }
                mainApp.navigateTo(MainApp.TAMBAH_EDIT_APP_SCREEN);
            } else {
                Task selectedTask = getTaskFromTableRow(row, tableType);
                System.out.println("DEBUG: Selected Task for detail: " + selectedTask);
                // Find the matkul for this task
                matkul m = findMatkulForTask(selectedTask);
                lastSelectedMatkul = m;
                mainApp.getDetailTugasPanel().loadTask(selectedTask);
                mainApp.navigateTo(MainApp.DETAIL_TUGAS_SCREEN);
            }
        }
    }

        // Helper: You need to implement this to get the Task object from the row and table
    private Task getTaskFromTableRow(int row, String tableType) {
        if (tableType.equals("Tugas Penting")) {
            return (row >= 0 && row < tugasPentingList.size()) ? tugasPentingList.get(row) : null;
        } else if (tableType.equals("Tugas Deadline")) {
            return (row >= 0 && row < tugasDeadlineList.size()) ? tugasDeadlineList.get(row) : null;
        } else if (tableType.equals("Jadwal")) {
            return (row >= 0 && row < jadwalTaskList.size()) ? jadwalTaskList.get(row) : null;
        }
        return null;
    }
    
    private matkul findMatkulByTitle(String title) {
        System.out.println("DEBUG: Searching for matkul with title: " + title);
        for (int i = 0; i < jComboBoxListJadwal.getItemCount(); i++) {
            matkul m = jComboBoxListJadwal.getItemAt(i);
            System.out.println("DEBUG: Checking matkul: " + m.getTitle());
            if (m.getTitle().equals(title)) {
                System.out.println("DEBUG: Found matkul: " + m);
                return m;
            }
        }
        System.out.println("DEBUG: No matkul found for title: " + title);
        return null;
    }

        private void navigateTo(String homeScreen) {
        mainApp.navigateTo(MainApp.HOME_SCREEN); // or any other screen constant
    }

    private ModelLayer.Class.matkul lastSelectedMatkul = null;

    private ModelLayer.Class.matkul findMatkulForTask(Task task) {
        if (task == null) return null;
        int idJm = task.getIdJm(); // Make sure Task has getIdJm()
        MatkulDAO matkulDAO = new MatkulDAO();
        ModelLayer.Class.matkul m = matkulDAO.findMatkulByJadwalMatkulId(idJm);
        return m;
    }

    public matkul getLastSelectedMatkul() {
        return lastSelectedMatkul;
    }

    public ModelLayer.Class.jadwal getLastSelectedJadwal() {
        // Return the jadwal object selected by the user
        // You may need to store this when the user selects a schedule
        return null; // Placeholder to satisfy return type
    }

    /**
     * Creates new form HomePagePanel
     */

    public void setDetailJadwalPanel(DetailJadwalPanel detailJadwalPanel) {
        this.detailJadwalPanel = detailJadwalPanel;
    }

    public HomePagePanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();

        // Initialize table models here
        tableModelTugasPenting = new DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        jTableTugasPenting.setModel(tableModelTugasPenting);

        tableModelTugasDeadline = new DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        jTableTugasPenting1.setModel(tableModelTugasDeadline);

        loadMatkulList();
        // Remove this line:
        // loadAllTasksToTables();

        // --- Double-click listeners ---
        jTableTugasPenting.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Check for left click
                    int row = jTableTugasPenting.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        Object taskTitle = jTableTugasPenting.getValueAt(row, 0);
                        handleTaskDoubleClick(row, taskTitle, "Tugas Penting");
                    }
                }
            }
        });
        jTableTugasPenting1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Check for left click
                    int row = jTableTugasPenting1.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        Object taskTitle = jTableTugasPenting1.getValueAt(row, 0);
                        handleTaskDoubleClick(row, taskTitle, "Tugas Deadline");
                    }
                }
            }
        });
        jTableTugasPenting2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Check for left click
                    int row = jTableTugasPenting2.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        Object taskTitle = jTableTugasPenting2.getValueAt(row, 2); // Assuming title is at column 2 for this table
                        handleTaskDoubleClick(row, taskTitle, "Jadwal");
                    }
                }
            }
        });

        // --- Single-selection coordination ---
        jTableTugasPenting.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableTugasPenting1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableTugasPenting2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jTableTugasPenting.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTableTugasPenting.getSelectedRow() != -1) {
                jTableTugasPenting1.clearSelection();
                jTableTugasPenting2.clearSelection();
            }
        });
        jTableTugasPenting1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTableTugasPenting1.getSelectedRow() != -1) {
                jTableTugasPenting.clearSelection();
                jTableTugasPenting2.clearSelection();
            }
        });
        jTableTugasPenting2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTableTugasPenting2.getSelectedRow() != -1) {
                jTableTugasPenting.clearSelection();
                jTableTugasPenting1.clearSelection();
            }
        });

        // --- Load initial data ---
        loadAllTasksToTables();
        loadJadwalTugasTable();
    }

    public void loadAllTasksToTables() {
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("User not logged in, skipping task loading.");
            return;
        }

        TaskDAO taskDAO = new TaskDAO();
        int userId = userSession.getCurrentUser().getIdUser();
        System.out.println("Loading tasks for user ID: " + userId);

        List<Task> tasks = taskDAO.findByUserId(userId);
        System.out.println("Number of tasks retrieved: " + tasks.size());

        // Sort by priority descending, then by deadline ascending (nearest first)
        tugasPentingList.clear();
        tugasPentingList.addAll(tasks.stream()
            .sorted(
                Comparator.comparingInt(Task::getPriority).reversed()
                .thenComparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
            )
            .toList());

        // Fill jTableTugasPenting (sorted by priority and deadline)
        updateTableModel(tableModelTugasPenting, tugasPentingList);

        // Fill jTableTugasPenting1 (sorted by deadline only)
        tugasDeadlineList.clear();
        tugasDeadlineList.addAll(tasks.stream()
            .sorted(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())))
            .toList());
        updateTableModel(tableModelTugasDeadline, tugasDeadlineList);
    }

    public void loadMatkulList() {
    if (userSession == null || !userSession.isUserLoggedIn()) {
        System.out.println("No user logged in, skipping matkul loading.");
        return;
    }
    int userId = userSession.getCurrentUser().getIdUser();
    MatkulDAO matkulDAO = new MatkulDAO();
       List<matkul> matkulList = matkulDAO.findByUserId(userId);
    jComboBoxListJadwal.removeAllItems();
    for (matkul m : matkulList) {
        jComboBoxListJadwal.addItem(m);
    }
    // Add "Tambah Jadwal" option
    matkul tambahJadwal = new matkul();
    tambahJadwal.setTitle("Tambah Jadwal");
    jComboBoxListJadwal.addItem(tambahJadwal);

    if (jComboBoxListJadwal.getItemCount() > 0) {
        jComboBoxListJadwal.setSelectedIndex(0);
    }
}
    public void loadJadwalTugasTable() {
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("No user logged in, skipping jadwal tugas loading.");
            return;
        }
        int userId = userSession.getCurrentUser().getIdUser();
        // TaskDAO taskDAO = new TaskDAO(); // No longer needed here for this specific table
        JadwalMatkulDAO jmDAO = new JadwalMatkulDAO(); // Use JadwalMatkulDAO
        List<Object[]> jadwalList = jmDAO.findSchedulesDetailsForUser(userId); // Call the new method

        DefaultTableModel model = (DefaultTableModel) jTableTugasPenting2.getModel();
        model.setRowCount(0); // Clear existing rows

        java.util.HashSet<String> uniqueRows = new java.util.HashSet<>();
        for (Object[] row : jadwalList) {
            // Create a unique key based on all columns
            String key = row[0] + "|" + row[1] + "|" + row[2];
            if (uniqueRows.add(key)) { // Only add if not already present
            model.addRow(row);
            }
        }
        // model.fireTableDataChanged(); // Not strictly necessary if setRowCount(0) and addRow are on EDT, but good practice
        SwingUtilities.invokeLater(model::fireTableDataChanged); // Ensure UI update is on EDT
    }
    private void updateTableModel(DefaultTableModel model, List<Task> tasks) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0); // Clear existing rows
            for (Task task : tasks) {
                Object[] rowData = {
                    task.getTitle(),
                    task.getDescription(),
                    task.getDeadline() != null ? task.getDeadline().toString() : "",
                    mapPriority(task.getPriority())
                };
                model.addRow(rowData);
                System.out.println("Added row: " + task.getTitle() + ", " + task.getDescription()); // Debugging
            }
            model.fireTableDataChanged(); // Notify the table
        });
    }

    private String mapPriority(int priority) {
        return switch (priority) {
            case 2 -> "High";
            case 1 -> "Medium";
            default -> "Low";
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabelTugasPenting = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTugasPenting = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableTugasPenting1 = new javax.swing.JTable();
        jLabelTugasDeadline = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableTugasPenting2 = new javax.swing.JTable();
        jLabelJadwal = new javax.swing.JLabel();
        jComboBoxListJadwal = new javax.swing.JComboBox<>();
        jLabelJadwal1 = new javax.swing.JLabel();
        jButtonAddOrEdit = new javax.swing.JButton();
        jButtonDetail = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel1.setText("TugasKu");

        jLabelTugasPenting.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTugasPenting.setText("Tugas Penting");

        jTableTugasPenting.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Judul", "Deskripsi", "Deadline", "Prioritas"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        jScrollPane1.setViewportView(jTableTugasPenting);

        jTableTugasPenting1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Judul", "Deskripsi", "Deadline", "Prioritas"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        jScrollPane2.setViewportView(jTableTugasPenting1);

        jLabelTugasDeadline.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTugasDeadline.setText("Tugas Dekat Deadline");

        jTableTugasPenting2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Hari", "Sesi", "Judul"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        jScrollPane3.setViewportView(jTableTugasPenting2);

        jLabelJadwal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelJadwal.setText("Jadwal");

        jComboBoxListJadwal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBoxListJadwal.setMaximumRowCount(100);
        jComboBoxListJadwal.setModel(new javax.swing.DefaultComboBoxModel<ModelLayer.Class.matkul>());
        jComboBoxListJadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxListJadwalActionPerformed(evt);
            }
        });

        jLabelJadwal1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelJadwal1.setText("Jadwal");

        jButtonAddOrEdit.setText("Tambah/Edit");
        jButtonAddOrEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddOrEditActionPerformed(evt);
            }
        });

        jButtonDetail.setText("Lihat Detail Jadwal");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTugasDeadline)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabelTugasPenting)))
                .addGap(149, 149, 149))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(207, 207, 207)
                        .addComponent(jLabelJadwal))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(137, 137, 137)
                        .addComponent(jLabelJadwal1)
                        .addGap(50, 50, 50)
                        .addComponent(jComboBoxListJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel1)))
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jButtonAddOrEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDetail)
                        .addGap(33, 33, 33))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabelTugasPenting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTugasDeadline)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelJadwal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelJadwal1)
                    .addComponent(jComboBoxListJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddOrEdit)
                    .addComponent(jButtonDetail))
                .addContainerGap(45, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddOrEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddOrEditActionPerformed
        matkul selectedMatkul = (matkul) jComboBoxListJadwal.getSelectedItem();
        JadwalPanel jadwalPanel = mainApp.getJadwalPanel();

        if (selectedMatkul != null && !"Tambah Jadwal".equals(selectedMatkul.getTitle())) {
            JadwalMatkulDAO jmDAO = new JadwalMatkulDAO();
            List<Integer> idJms = jmDAO.findIdJmsByMatkulId(selectedMatkul.getIdMatkul());

            if (!idJms.isEmpty()) {
                Integer idJmToEdit = idJms.get(0);
                jadwalPanel.loadJadwalMatkul(idJmToEdit);
            } else {
                jadwalPanel.prepareNewJadwalMatkul(selectedMatkul);
            }
        } else {
            // "Tambah Jadwal" selected or nothing selected
            jadwalPanel.prepareNewJadwalMatkul(null);
        }
        mainApp.navigateTo(MainApp.DETAIL_SCREEN);
    }//GEN-LAST:event_jButtonAddOrEditActionPerformed

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailActionPerformed
        matkul selectedMatkul = (matkul) jComboBoxListJadwal.getSelectedItem();
        if (selectedMatkul == null || "Tambah Jadwal".equals(selectedMatkul.getTitle())) {
            javax.swing.JOptionPane.showMessageDialog(this, "Pilih Matkul terlebih dahulu atau tambah baru");
            return;
        }
        if (detailJadwalPanel != null) {
            detailJadwalPanel.showTasksForMatkul(selectedMatkul);
            mainApp.navigateTo(MainApp.TAMBAH_EDIT_APP_SCREEN);
        }
    }//GEN-LAST:event_jButtonDetailActionPerformed

    private void jComboBoxListJadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxListJadwalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxListJadwalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddOrEdit;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JComboBox<ModelLayer.Class.matkul> jComboBoxListJadwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelJadwal;
    private javax.swing.JLabel jLabelJadwal1;
    private javax.swing.JLabel jLabelTugasDeadline;
    private javax.swing.JLabel jLabelTugasPenting;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableTugasPenting;
    private javax.swing.JTable jTableTugasPenting1;
    private javax.swing.JTable jTableTugasPenting2;
    // End of variables declaration//GEN-END:variables
}