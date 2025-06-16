package layerUI;

// ===== Imports =====
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import DataAccessObject.TaskDAO;
import Main.MainApp;
import Main.Session;
import ModelLayer.Class.Task;

// ===== Class Declaration =====
public class DetailJadwalPanel extends javax.swing.JPanel { 

    // ===== Fields =====
    private MainApp mainApp;
    private Session userSession;
    private List<Task> belumSelesaiTasks = new ArrayList<>();
    private List<Task> sudahSelesaiTasks = new ArrayList<>();
    private ModelLayer.Class.matkul currentDisplayingMatkul;

    // ===== Constructor =====
    public DetailJadwalPanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();
        setupTableListeners();
    }

    // ===== Public API Methods =====
    //Loads the details of a jadwal based on hari, sesi, and judul.
    //Placeholder for future implementation.
    public void loadJadwalDetail(Object hari, Object sesi, Object judul) {
        System.out.println("Loading Jadwal Detail: Hari=" + hari + ", Sesi=" + sesi + ", Judul=" + judul);
    }

    //Loads tasks for the selected matkul.
    public void loadTasksForSelectedMatkul(int idMatkul) {
        System.out.println("DEBUG: loadTasksForSelectedMatkul called with idMatkul = " + idMatkul);
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("No user logged in, skipping task loading.");
            return;
        }
        int userId = userSession.getCurrentUser().getIdUser();
        TaskDAO taskDAO = new TaskDAO();

        // Load unfinished tasks
        this.belumSelesaiTasks = taskDAO.findByUserMatkulAndStatus(userId, idMatkul, 0);
        DefaultTableModel model1 = (DefaultTableModel) jTableTugasPenting1.getModel();
        model1.setRowCount(0);
        for (Task task : belumSelesaiTasks) {
            model1.addRow(new Object[]{
                task.getTitle(),
                task.getDescription(),
                task.getDeadline() != null ? task.getDeadline().toString() : "",
                getPriorityText(task.getPriority())
            });
        }

        // Load finished tasks
        this.sudahSelesaiTasks = taskDAO.findByUserMatkulAndStatus(userId, idMatkul, 1);
        DefaultTableModel model2 = (DefaultTableModel) jTableTugasPenting2.getModel();
        model2.setRowCount(0);
        for (Task task : sudahSelesaiTasks) {
            model2.addRow(new Object[]{
                task.getTitle(),
                task.getDescription(),
                task.getDeadline() != null ? task.getDeadline().toString() : "",
                getPriorityText(task.getPriority())
            });
        }
        System.out.println("DEBUG: BELUM_SELESAI tasks count: " + belumSelesaiTasks.size());
        System.out.println("DEBUG: SUDAH_SELESAI tasks count: " + sudahSelesaiTasks.size());
    }

    // Returns the currently displaying matkul.
    public ModelLayer.Class.matkul getCurrentDisplayingMatkul() {
        return this.currentDisplayingMatkul;
    }

    //Shows tasks for the selected matkul.
    public void showTasksForMatkul(ModelLayer.Class.matkul selectedMatkul) {
        System.out.println("DEBUG: DetailJadwalPanel.showTasksForMatkul called with: " + (selectedMatkul != null ? selectedMatkul.getTitle() : "null"));
        this.currentDisplayingMatkul = selectedMatkul;
        if (selectedMatkul != null) {
            jLabelJudulJadwal.setText(selectedMatkul.getTitle());
            loadTasksForSelectedMatkul(selectedMatkul.getIdMatkul());
        } else {
            jLabelJudulJadwal.setText("No Matkul Selected");
            ((DefaultTableModel) jTableTugasPenting1.getModel()).setRowCount(0);
            ((DefaultTableModel) jTableTugasPenting2.getModel()).setRowCount(0);
            if (this.belumSelesaiTasks != null) this.belumSelesaiTasks.clear();
            if (this.sudahSelesaiTasks != null) this.sudahSelesaiTasks.clear();
        }
    }

    // ===== Private Helper Methods =====

    // Sets up mouse and selection listeners for the tables.
    private void setupTableListeners() {
        // Double-click listeners
        jTableTugasPenting1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    jTableTugasPenting1.setRowSelectionInterval(jTableTugasPenting1.rowAtPoint(e.getPoint()), jTableTugasPenting1.rowAtPoint(e.getPoint()));
                    loadSelectedTaskFromAnyTable();
                }
            }
        });
        jTableTugasPenting2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    jTableTugasPenting2.setRowSelectionInterval(jTableTugasPenting2.rowAtPoint(e.getPoint()), jTableTugasPenting2.rowAtPoint(e.getPoint()));
                    loadSelectedTaskFromAnyTable();
                }
            }
        });

        // Single selection mode
        jTableTugasPenting1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableTugasPenting2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Only one table can have a selected row at a time
        jTableTugasPenting1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && jTableTugasPenting1.getSelectedRow() != -1) {
                    jTableTugasPenting2.clearSelection();
                }
            }
        });
        jTableTugasPenting2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && jTableTugasPenting2.getSelectedRow() != -1) {
                    jTableTugasPenting1.clearSelection();
                }
            }
        });
    }

    // Converts priority integer to text.
    private String getPriorityText(int priority) {
        switch (priority) {
            case 0: return "Low";
            case 1: return "Medium";
            case 2: return "High";
            default: return "Unknown";
        }
    }

    // Loads the selected task from either table when a double-click occurs.
    private void loadSelectedTaskFromAnyTable() {
        if (jTableTugasPenting1.getSelectedRow() != -1) {
            loadSelectedTaskFromTable(jTableTugasPenting1, belumSelesaiTasks);
        } else if (jTableTugasPenting2.getSelectedRow() != -1) {
            loadSelectedTaskFromTable(jTableTugasPenting2, sudahSelesaiTasks);
        } else {
            System.out.println("No task selected for double-click.");
        }
    }

    // Loads the selected task for detail view, or opens the panel to create a new task if none is selected.
    private void loadSelectedTaskOrOpenNew() {
        Task selectedTask = null;
        int row1 = jTableTugasPenting1.getSelectedRow();
        int row2 = jTableTugasPenting2.getSelectedRow();

        if (row1 != -1 && row1 < belumSelesaiTasks.size()) {
            selectedTask = belumSelesaiTasks.get(row1);
        } else if (row2 != -1 && row2 < sudahSelesaiTasks.size()) {
            selectedTask = sudahSelesaiTasks.get(row2);
        }

        if (selectedTask != null) {
            System.out.println("DEBUG: DetailJadwalPanel - Loading selected Task ID: " + selectedTask.getItemId());
            mainApp.getDetailTugasPanel().loadTask(selectedTask);
        } else {
            System.out.println("DEBUG: DetailJadwalPanel - No task selected, opening DetailTugasPanel for new task for matkul: " +
                (this.currentDisplayingMatkul != null ? this.currentDisplayingMatkul.getTitle() : "null"));
            if (this.currentDisplayingMatkul != null) {
                mainApp.getDetailTugasPanel().prepareForNewTask(this.currentDisplayingMatkul);
            } else {
                mainApp.getDetailTugasPanel().prepareForNewTask(null);
                JOptionPane.showMessageDialog(this, "Cannot create new task: No course selected in the previous view.", "Context Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        mainApp.navigateTo(MainApp.DETAIL_TUGAS_SCREEN);
    }

    //Loads the selected task from the specified table and task list, then navigates to the detail screen.
    private void loadSelectedTaskFromTable(javax.swing.JTable table, List<Task> taskList) {
        int row = table.getSelectedRow();
        if (row != -1 && row < taskList.size()) {
            Task selectedTask = taskList.get(row);
            System.out.println("Selected Task ID: " + selectedTask.getItemId());
            mainApp.getDetailTugasPanel().loadTask(selectedTask);
            mainApp.navigateTo(MainApp.DETAIL_TUGAS_SCREEN);
        }
    }

    // ===== Event Handlers =====
    //Handles the "Back" button action. Navigates back to the home screen and reloads relevant data.
    private void jToggleButtonBackActionPerformed(java.awt.event.ActionEvent evt) {
        mainApp.getHomePagePanel().loadJadwalTugasTable();
        mainApp.getHomePagePanel().loadMatkulList();
        mainApp.getHomePagePanel().loadAllTasksToTables();
        mainApp.navigateTo(MainApp.HOME_SCREEN);
    }

    //Handles the "Lihat Detail Tugas / Tugas Baru" button action.
    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {
        loadSelectedTaskOrOpenNew();
    }

    // ===== Auto-Generated GUI Code and Variable Declarations =====
    private void initComponents() {

        // Inisialisasi komponen
        jLabel1 = new javax.swing.JLabel(); // Label for the title
        jToggleButtonBack = new javax.swing.JToggleButton(); // Button to go back
        jLabelJudulJadwal = new javax.swing.JLabel(); // Label for the schedule title
        jLabelTugasDeadline = new javax.swing.JLabel(); // Label for "Tugas Belum Selesai"
        jScrollPane2 = new javax.swing.JScrollPane(); // Scroll pane for the first table
        jTableTugasPenting1 = new javax.swing.JTable(); // Table for "Tugas Belum Selesai"
        jScrollPane3 = new javax.swing.JScrollPane(); // Scroll pane for the second table
        jTableTugasPenting2 = new javax.swing.JTable(); // Table for "Tugas Selesai"
        jLabelJadwal = new javax.swing.JLabel(); // Label for "Tugas Selesai"
        jButtonDetail = new javax.swing.JButton(); // Button to view task details or create a new task

        // Set up the main title label
        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 36)); // Bold italic, large font
        jLabel1.setText("TugasKu");

        // Configure the "Back" toggle button
        jToggleButtonBack.setText("Back");
        jToggleButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jToggleButtonBackActionPerformed(evt); // Go back to previous screen
            }
        });

        // Set up the schedule/course title label
        jLabelJudulJadwal.setFont(new java.awt.Font("Segoe UI", 1, 36)); // Bold, large font
        jLabelJudulJadwal.setText("Judul");

        // Label for the "Tugas Belum Selesai" (unfinished tasks) section
        jLabelTugasDeadline.setFont(new java.awt.Font("Segoe UI", 1, 18)); // Bold, medium font
        jLabelTugasDeadline.setText("Tugas Belum Selesai");

        // Table for unfinished tasks (not editable)
        jTableTugasPenting1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
            return false; // Make all cells non-editable
            }
        });
        jScrollPane2.setViewportView(jTableTugasPenting1);

        // Table for finished tasks (not editable)
        jTableTugasPenting2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
            return false;  // Make all cells non-editable
            }
        });
        jScrollPane3.setViewportView(jTableTugasPenting2);

        // Label for the "Tugas Selesai" (finished tasks) section
        jLabelJadwal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // Bold, medium font
        jLabelJadwal.setText("Tugas Selesai");

        // Button to view task details or create a new task
        jButtonDetail.setFont(new java.awt.Font("sansserif", 0, 18)); // Regular, medium font
        jButtonDetail.setText(" Lihat Detail Tugas / Tugas Baru");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonDetailActionPerformed(evt); // Open detail or new task
            }
        });

        // Layout setup using GroupLayout. do not modify this section manually or edit it minimally.
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabelTugasDeadline)
                                .addGap(135, 135, 135))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonDetail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(9, 9, 9)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jToggleButtonBack)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                                    .addComponent(jLabelJudulJadwal))
                                .addComponent(jLabel1))
                            .addContainerGap(200, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabelJadwal)
                        .addGap(179, 179, 179))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabelJudulJadwal))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToggleButtonBack)))
                .addGap(18, 18, 18)
                .addComponent(jLabelTugasDeadline)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelJadwal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                .addComponent(jButtonDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variable declarations
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelJadwal;
    private javax.swing.JLabel jLabelJudulJadwal;
    private javax.swing.JLabel jLabelTugasDeadline;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableTugasPenting1;
    private javax.swing.JTable jTableTugasPenting2;
    private javax.swing.JToggleButton jToggleButtonBack;
}