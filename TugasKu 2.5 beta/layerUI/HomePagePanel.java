package layerUI;

// ===== Imports =====
import Main.MainApp;
import Main.Session;
import ModelLayer.Class.Task;
import ModelLayer.Class.matkul;
import DataAccessObject.MatkulDAO;
import DataAccessObject.TaskDAO;
import DataAccessObject.JadwalMatkulDAO;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;


// ===== Class Declaration =====
public class HomePagePanel extends javax.swing.JPanel {

    // ===== Fields =====
    private MainApp mainApp;
    private Session userSession;
    private DefaultTableModel tableModelTugasPenting;
    private DefaultTableModel tableModelTugasDeadline;
    private DetailJadwalPanel detailJadwalPanel;

    // Lists to hold tasks for different tables
    private List<Task> tugasPentingList = new java.util.ArrayList<>();
    private List<Task> tugasDeadlineList = new java.util.ArrayList<>();
    private List<Task> jadwalTaskList = new java.util.ArrayList<>();

    // Variable to hold the last selected matkul for context in detail views        
    private ModelLayer.Class.matkul lastSelectedMatkul = null;

    // ===== Constructor =====
    public HomePagePanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();

        // ===== Initialize table models =====
        tableModelTugasPenting = new DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableTugasPenting.setModel(tableModelTugasPenting);

        tableModelTugasDeadline = new DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableTugasPenting1.setModel(tableModelTugasDeadline);

        // ===== Custom cell renderer for coloring rows based on deadline =====
        DefaultTableCellRenderer deadlineRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Reset to default
                c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                // Deadline is in column 2 (index 2)
                Object deadlineObj = table.getValueAt(row, 2);
                if (deadlineObj != null && !deadlineObj.toString().isEmpty()) {
                    try {
                        java.time.LocalDate deadline = java.time.LocalDate.parse(deadlineObj.toString());
                        java.time.LocalDate today = java.time.LocalDate.now();
                        if (deadline.isBefore(today)) {
                            c.setBackground(new Color(255, 204, 204)); // Light red
                        } else if (deadline.isEqual(today)) {
                            c.setBackground(new Color(255, 255, 153)); // Yellow
                        }
                    } catch (Exception ex) {
                        // Ignore parse errors, keep default color
                    }
                }
                return c;
            }
        };

        // ===== Apply renderer to all columns of both tables =====
        javax.swing.JTable[] tables = {jTableTugasPenting, jTableTugasPenting1};
        for (javax.swing.JTable table : tables) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(deadlineRenderer);
            }
        }

        // ===== Load data and listeners =====
        loadMatkulList();
        setupTableListeners();
        loadAllTasksToTables();
        loadJadwalTugasTable();
    }

    // ===== Public API Methods =====
    // Method to set the DetailJadwalPanel instance
    public void setDetailJadwalPanel(DetailJadwalPanel detailJadwalPanel) {
        this.detailJadwalPanel = detailJadwalPanel;
    }

    // Method to get the last selected matkul
    public matkul getLastSelectedMatkul() {
        return lastSelectedMatkul;
    }

    // Method to get the last selected jadwal (not implemented, returns null)
    public ModelLayer.Class.jadwal getLastSelectedJadwal() {
        return null;
    }

    // Method to navigate to a specific screen
    public void navigateTo(String homeScreen) {
        mainApp.navigateTo(MainApp.HOME_SCREEN);
    }

    // Method to load all tasks into the tables
    public void loadAllTasksToTables() {
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("User not logged in, skipping task loading.");
            return;
        }
        TaskDAO taskDAO = new TaskDAO();
        int userId = userSession.getCurrentUser().getIdUser();
        List<Task> allTasks = taskDAO.findByUserId(userId);

        // Filter out completed tasks
        List<Task> incompleteTasks = allTasks.stream()
            .filter(task -> task.getCurrentStatus() != Enumeration.TaskStatus.SUDAH_SELESAI)
            .toList();

        // Sort by priority descending, then by deadline ascending
        tugasPentingList.clear();
        tugasPentingList.addAll(incompleteTasks.stream()
            .sorted(
                Comparator.comparingInt(Task::getPriority).reversed()
                .thenComparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
            )
            .toList());

        updateTableModel(tableModelTugasPenting, tugasPentingList);

        // Sort by deadline only
        tugasDeadlineList.clear();
        tugasDeadlineList.addAll(incompleteTasks.stream()
            .sorted(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))).toList());
        updateTableModel(tableModelTugasDeadline, tugasDeadlineList);
    }

    // Method to load the list of matkul into the combo box
    public void loadMatkulList() {
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("Tidak Ada user yang login, skipping matkul loading.");
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

    // Method to load the jadwal tugas table
    public void loadJadwalTugasTable() {
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("Tidak Ada user yang login, skipping jadwal tugas loading.");
            return;
        }
        int userId = userSession.getCurrentUser().getIdUser();
        JadwalMatkulDAO jmDAO = new JadwalMatkulDAO();
        List<Object[]> jadwalList = jmDAO.findSchedulesDetailsForUser(userId);

        DefaultTableModel model = (DefaultTableModel) jTableTugasPenting2.getModel();
        model.setRowCount(0);

        java.util.HashSet<String> uniqueRows = new java.util.HashSet<>();
        for (Object[] row : jadwalList) {
            String key = row[0] + "|" + row[1] + "|" + row[2];
            if (uniqueRows.add(key)) {
                model.addRow(row);
            }
        }
        SwingUtilities.invokeLater(model::fireTableDataChanged);
    }

    // ===== Private Helper Methods =====
    // Method to set up table listeners for double-click actions
    private void setupTableListeners() {
        // Double-click listeners
        jTableTugasPenting.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
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
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
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
                if (e.getClickCount() == 2 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    int row = jTableTugasPenting2.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        Object taskTitle = jTableTugasPenting2.getValueAt(row, 2);
                        handleTaskDoubleClick(row, taskTitle, "Jadwal");
                    }
                }
            }
        });

        // Single-selection coordination
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
    }

    // Method to update the table model with new task data
    private void updateTableModel(DefaultTableModel model, List<Task> tasks) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            for (Task task : tasks) {
                Object[] rowData = {
                    task.getTitle(),
                    task.getDescription(),
                    task.getDeadline() != null ? task.getDeadline().toString() : "",
                    mapPriority(task.getPriority())
                };
                model.addRow(rowData);
            }
            model.fireTableDataChanged();
        });
    }

    // Method to map priority integer to string representation
    private String mapPriority(int priority) {
        return switch (priority) {
            case 2 -> "High";
            case 1 -> "Medium";
            default -> "Low";
        };
    }

    // Method to handle double-click on a task row
    private void handleTaskDoubleClick(int row, Object taskTitle, String tableType) {
        System.out.println("Double-clicked row: " + row + ", Task: " + taskTitle + ", Table: " + tableType);
        if (mainApp != null) {
            if (tableType.equals("Jadwal")) {
                Object judul = jTableTugasPenting2.getValueAt(row, 2);
                matkul selectedMatkul = findMatkulByTitle(judul.toString());
                if (detailJadwalPanel != null && selectedMatkul != null) {
                    detailJadwalPanel.showTasksForMatkul(selectedMatkul);
                }
                mainApp.navigateTo(MainApp.TAMBAH_EDIT_APP_SCREEN);
            } else {
                Task selectedTask = getTaskFromTableRow(row, tableType);
                matkul m = findMatkulForTask(selectedTask);
                lastSelectedMatkul = m;
                if (mainApp.getDetailJadwalPanel() != null && m != null) {
                    mainApp.getDetailJadwalPanel().showTasksForMatkul(m);
                }
                mainApp.getDetailTugasPanel().loadTask(selectedTask);
                mainApp.navigateTo(MainApp.DETAIL_TUGAS_SCREEN);
            }
        }
    }

    // Method to get the Task object from the specified table row
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

    // Method to find a matkul by its title in the combo box
    private matkul findMatkulByTitle(String title) {
        for (int i = 0; i < jComboBoxListJadwal.getItemCount(); i++) {
            matkul m = jComboBoxListJadwal.getItemAt(i);
            if (m.getTitle().equals(title)) {
                return m;
            }
        }
        return null;
    }

    // Method to find a matkul for a given task
    private ModelLayer.Class.matkul findMatkulForTask(Task task) {
        if (task == null) return null;
        int idJm = task.getIdJm();
        MatkulDAO matkulDAO = new MatkulDAO();
        return matkulDAO.findMatkulByJadwalMatkulId(idJm);
    }

    // ===== Event Handler =====
    // Action performed when the "Tambah/Edit" button is clicked
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

    // Action performed when the "Lihat Detail Jadwal" button is clicked
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

    // Action performed when the combo box for jadwal selection is changed
    private void jComboBoxListJadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxListJadwalActionPerformed
    }//GEN-LAST:event_jComboBoxListJadwalActionPerformed

    // ===== Auto-generated GUI Code and Variable Declarations =====
    private void initComponents() {
        // Inisialisasi komponen GUI
        jLabel1 = new javax.swing.JLabel(); // Label for the title
        jLabelTugasPenting = new javax.swing.JLabel(); // Label for "Tugas Penting"
        jScrollPane1 = new javax.swing.JScrollPane(); // Scroll pane for the Tugas Penting table
        jTableTugasPenting = new javax.swing.JTable(); // Table for important tasks
        jScrollPane2 = new javax.swing.JScrollPane(); // Scroll pane for the Tugas Dekat Deadline table
        jTableTugasPenting1 = new javax.swing.JTable(); // Table for tasks near deadline
        jLabelTugasDeadline = new javax.swing.JLabel(); // Label for "Tugas Dekat Deadline"
        jScrollPane3 = new javax.swing.JScrollPane(); // Scroll pane for the Jadwal table
        jTableTugasPenting2 = new javax.swing.JTable(); // Table for schedule tasks
        jLabelJadwal = new javax.swing.JLabel(); // Label for "Jadwal"
        jComboBoxListJadwal = new javax.swing.JComboBox<>(); // Combo box for selecting schedules
        jLabelJadwal1 = new javax.swing.JLabel(); // Label for "Jadwal"
        jButtonAddOrEdit = new javax.swing.JButton(); // Button to add or edit schedules 
        jButtonDetail = new javax.swing.JButton(); // Button to view schedule details

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