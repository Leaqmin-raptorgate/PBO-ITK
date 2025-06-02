package layerUI;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

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
import ModelLayer.Class.matkul; // Ensure matkul is imported

/**
 *
 * @author madit
 */
public class DetailJadwalPanel extends javax.swing.JPanel {
    private MainApp mainApp;
    private Session userSession;

    // Add these fields to your class
    private List<Task> belumSelesaiTasks = new ArrayList<>();
    private List<Task> sudahSelesaiTasks = new ArrayList<>();
    private ModelLayer.Class.matkul currentDisplayingMatkul; // To store the matkul being displayed

    public void loadJadwalDetail(Object hari, Object sesi, Object judul) {
    System.out.println("Loading Jadwal Detail: Hari=" + hari + ", Sesi=" + sesi + ", Judul=" + judul);
    }

    /**
     * Creates new form DetailJadwalPanel
     */
    public DetailJadwalPanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();
        // Optionally: loadJadwalDetails();

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

        jTableTugasPenting1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableTugasPenting2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add listeners to coordinate selection:
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
    public void showTasksForMatkul(ModelLayer.Class.matkul selectedMatkul, int status) {
        System.out.println("DEBUG: showTasksForMatkul called with id = " + selectedMatkul.getIdMatkul() + ", status = " + status);
        loadTasksForSelectedMatkul(selectedMatkul.getIdMatkul());
    }

    public void loadTasksForSelectedMatkul(int idMatkul) {
        System.out.println("DEBUG: loadTasksForSelectedMatkul called with idMatkul = " + idMatkul);
        if (userSession == null || !userSession.isUserLoggedIn()) {
            System.out.println("No user logged in, skipping task loading.");
            return;
        }
        int userId = userSession.getCurrentUser().getIdUser();
        TaskDAO taskDAO = new TaskDAO();

        // BELUM_SELESAI (status = 0)
        this.belumSelesaiTasks = taskDAO.findByUserMatkulAndStatus(userId, idMatkul, 0);
        DefaultTableModel model1 = (DefaultTableModel) jTableTugasPenting1.getModel();
        model1.setRowCount(0);
        for (Task task : belumSelesaiTasks) {
            model1.addRow(new Object[]{
                task.getTitle(),
                task.getDescription(),
                task.getDeadline() != null ? task.getDeadline().toString() : "",
                task.getPriority()
            });
        }

        // SUDAH_SELESAI (status = 1)
        this.sudahSelesaiTasks = taskDAO.findByUserMatkulAndStatus(userId, idMatkul, 1);
        DefaultTableModel model2 = (DefaultTableModel) jTableTugasPenting2.getModel();
        model2.setRowCount(0);
        for (Task task : sudahSelesaiTasks) {
            model2.addRow(new Object[]{
                task.getTitle(),
                task.getDescription(),
                task.getDeadline() != null ? task.getDeadline().toString() : "",
                task.getPriority()
            });
        }
        System.out.println("DEBUG: BELUM_SELESAI tasks count: " + belumSelesaiTasks.size());
        System.out.println("DEBUG: SUDAH_SELESAI tasks count: " + sudahSelesaiTasks.size());
    }
        public ModelLayer.Class.matkul getCurrentDisplayingMatkul() {
            return this.currentDisplayingMatkul;
        }

        public void showTasksForMatkul(ModelLayer.Class.matkul selectedMatkul) {
            System.out.println("DEBUG: DetailJadwalPanel.showTasksForMatkul called with: " + (selectedMatkul != null ? selectedMatkul.getTitle() : "null"));
            this.currentDisplayingMatkul = selectedMatkul; // Store the current matkul
            if (selectedMatkul != null) {
                jLabelJudulJadwal.setText(selectedMatkul.getTitle());
                loadTasksForSelectedMatkul(selectedMatkul.getIdMatkul());
            } else {
                jLabelJudulJadwal.setText("No Matkul Selected");
                // Clear tables if matkul is null
                ((DefaultTableModel) jTableTugasPenting1.getModel()).setRowCount(0);
                ((DefaultTableModel) jTableTugasPenting2.getModel()).setRowCount(0);
                if (this.belumSelesaiTasks != null) this.belumSelesaiTasks.clear();
                if (this.sudahSelesaiTasks != null) this.sudahSelesaiTasks.clear();
            }
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
        jToggleButtonBack = new javax.swing.JToggleButton();
        jLabelJudulJadwal = new javax.swing.JLabel();
        jLabelTugasDeadline = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableTugasPenting1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableTugasPenting2 = new javax.swing.JTable();
        jLabelJadwal = new javax.swing.JLabel();
        jButtonDetail = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel1.setText("TugasKu");

        jToggleButtonBack.setText("Back");
        jToggleButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBackActionPerformed(evt);
            }
        });

        jLabelJudulJadwal.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelJudulJadwal.setText("Judul");

        jLabelTugasDeadline.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTugasDeadline.setText("Tugas Belum Selesai");

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

        jTableTugasPenting2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Judul", "Deskripsi", "Deadline", "Prioritas"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        });
        jScrollPane3.setViewportView(jTableTugasPenting2);

        jLabelJadwal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelJadwal.setText("Tugas Selesai");

        jButtonDetail.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jButtonDetail.setText(" Lihat Detail Tugas / Tugas Baru");
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

    private void jToggleButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBackActionPerformed
        mainApp.getHomePagePanel().loadJadwalTugasTable();
        mainApp.getHomePagePanel().loadMatkulList();
        mainApp.getHomePagePanel().loadAllTasksToTables();
        mainApp.navigateTo(MainApp.HOME_SCREEN); // or another screen as needed
    }//GEN-LAST:event_jToggleButtonBackActionPerformed

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailActionPerformed
        loadSelectedTaskOrOpenNew();
    }//GEN-LAST:event_jButtonDetailActionPerformed

    // Call this method from both double-click and button
    private void loadSelectedTaskFromAnyTable() {
        if (jTableTugasPenting1.getSelectedRow() != -1) {
            loadSelectedTaskFromTable(jTableTugasPenting1, belumSelesaiTasks);
        } else if (jTableTugasPenting2.getSelectedRow() != -1) {
            loadSelectedTaskFromTable(jTableTugasPenting2, sudahSelesaiTasks);
        } else {
            // This case should ideally not be reached if loadSelectedTaskOrOpenNew is used by the button
            System.out.println("No task selected for double-click.");
        }
    }

    private void loadSelectedTaskOrOpenNew() {
        Task selectedTask = null;
        if (jTableTugasPenting1.getSelectedRow() != -1) {
            int row = jTableTugasPenting1.getSelectedRow();
            if (row < belumSelesaiTasks.size()) {
                selectedTask = belumSelesaiTasks.get(row);
            }
        } else if (jTableTugasPenting2.getSelectedRow() != -1) {
            int row = jTableTugasPenting2.getSelectedRow();
            if (row < sudahSelesaiTasks.size()) {
                selectedTask = sudahSelesaiTasks.get(row);
            }
        }

        if (selectedTask != null) {
            System.out.println("DEBUG: DetailJadwalPanel - Loading selected Task ID: " + selectedTask.getItemId());
            mainApp.getDetailTugasPanel().loadTask(selectedTask); // For existing task
        } else {
            System.out.println("DEBUG: DetailJadwalPanel - No task selected, opening DetailTugasPanel for new task for matkul: " + (this.currentDisplayingMatkul != null ? this.currentDisplayingMatkul.getTitle() : "null"));
            if (this.currentDisplayingMatkul != null) {
                mainApp.getDetailTugasPanel().prepareForNewTask(this.currentDisplayingMatkul); // Pass current matkul context
            } else {
                // This case should ideally be prevented by UI flow,
                // but as a fallback, tell DetailTugasPanel no context is available.
                mainApp.getDetailTugasPanel().prepareForNewTask(null);
                JOptionPane.showMessageDialog(this, "Cannot create new task: No course selected in the previous view.", "Context Error", JOptionPane.ERROR_MESSAGE);
                return; // Don't navigate if no context
            }
        }
        mainApp.navigateTo(MainApp.DETAIL_TUGAS_SCREEN);
    }

    // Example handler method
    private void handleTaskDoubleClick(int row, Object taskTitle, javax.swing.JTable table, List<Task> taskList) {
        loadSelectedTaskFromTable(table, taskList);
    }

    private void loadSelectedTaskFromTable(javax.swing.JTable table, List<Task> taskList) {
        int row = table.getSelectedRow();
        if (row != -1 && row < taskList.size()) {
            Task selectedTask = taskList.get(row);
            // Now you can load the task, e.g.:
            System.out.println("Selected Task ID: " + selectedTask.getItemId()); // Use getItemId from ScheduleItem
            mainApp.getDetailTugasPanel().loadTask(selectedTask);
            mainApp.navigateTo(MainApp.DETAIL_TUGAS_SCREEN);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables


}