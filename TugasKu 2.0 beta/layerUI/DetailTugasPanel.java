package layerUI;

// ===== Imports =====
import Main.MainApp;
import Main.Session;
import DataAccessObject.TaskDAO;
import DataAccessObject.MatkulDAO;
import javax.swing.JOptionPane;
import ModelLayer.Class.Task;
import ModelLayer.Class.matkul;
import java.util.List;

// ===== Class Declaration =====
public class DetailTugasPanel extends javax.swing.JPanel {
        // ===== Fields =====
    private MainApp mainApp;
    private Session userSession;
    private Task currentTask = null;
    private matkul contextMatkulForNewTask; // To store matkul context for a new task
    private javax.swing.ButtonGroup statusButtonGroup;

    // ===== Constructor =====
    //Creates new form DetailTugasPanel
    public DetailTugasPanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();
    }

    // ===== Public API Methods =====
    //Loads the given task into the panel for editing, or clears fields for a new task.
    public void loadTask(Task task) {
        this.currentTask = task;
        this.contextMatkulForNewTask = null; // Clear new task context when loading an existing task
        if (task != null) {
            jTextField1.setText(task.getTitle());
            jTextArea1.setText(task.getDescription());
            if (task.getDeadline() != null) {
                java.util.Date date = task.getDeadline() != null ? java.sql.Date.valueOf(task.getDeadline()) : null;
                jDateChooser1.setDate(date);
            } else {
                jDateChooser1.setDate(null);
            }
            // Priority: 2=High, 1=Medium, 0=Low
            switch (task.getPriority()) {
                case 2 -> jComboBox1.setSelectedItem("High");
                case 1 -> jComboBox1.setSelectedItem("Medium");
                default -> jComboBox1.setSelectedItem("Low");
            }
            // Status
            if (task.getCurrentStatus() == Enumeration.TaskStatus.BELUM_SELESAI) {
                jRadioButton2.setSelected(true); // Belum Selesai
                jRadioButton1.setSelected(false);
            } else {
                jRadioButton1.setSelected(true); // Sudah Selesai
                jRadioButton2.setSelected(false);
            }
            System.out.println("DEBUG: DetailTugasPanel.loadTask - Loaded existing task: " + task.getTitle());
        } else {
            // Clear fields for new task (currentTask is already null)
            jTextField1.setText("");
            jTextArea1.setText("");
            jDateChooser1.setDate(null);
            jComboBox1.setSelectedIndex(0); // Default to Low priority
            jRadioButton1.setSelected(false); // Sudah Selesai
            jRadioButton2.setSelected(true);  // Belum Selesai (default for new task)
            System.out.println("DEBUG: DetailTugasPanel.loadTask - Cleared fields for new task (task is null).");
        }
    }

    // Prepares the panel for creating a new task with the given matkul context.
    public void prepareForNewTask(matkul contextMatkul) {
        loadTask(null); // This will set currentTask to null and clear fields
        this.contextMatkulForNewTask = contextMatkul;
        System.out.println("DEBUG: DetailTugasPanel.prepareForNewTask - contextMatkulForNewTask set to: " + (this.contextMatkulForNewTask != null ? this.contextMatkulForNewTask.getTitle() : "null"));
        if (contextMatkul == null) {
             System.err.println("ERROR: DetailTugasPanel.prepareForNewTask called with null contextMatkul. New tasks cannot be created without course context.");
             return;
        }
    }

    // ===== Event Handlers =====
    // Handles the "Back" button action. Navigates back to the correct DetailJadwalPanel context.
    private void jToggleButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBackActionPerformed
        ModelLayer.Class.matkul refreshMatkulContext = null;

        if (this.currentTask != null && this.currentTask.getIdJm() != null) {
            // If a task was loaded and has a schedule link
            MatkulDAO matkulDAO = new MatkulDAO();
            refreshMatkulContext = matkulDAO.findMatkulByJadwalMatkulId(this.currentTask.getIdJm());
        } else if (this.contextMatkulForNewTask != null) {
            // If panel was prepared for a new task with a specific matkul context
            refreshMatkulContext = this.contextMatkulForNewTask;
        } else if (mainApp.getDetailJadwalPanel() != null) {
            // Fallback: try to get the matkul DetailJadwalPanel was last displaying
            refreshMatkulContext = mainApp.getDetailJadwalPanel().getCurrentDisplayingMatkul();
        }
        // As a very last resort, if still null, try HomePagePanel's last selected.
        // However, this is less ideal and might be the source of previous issues if reached.
        if (refreshMatkulContext == null && mainApp.getHomePagePanel() != null) {
            refreshMatkulContext = mainApp.getHomePagePanel().getLastSelectedMatkul();
        }
        if (mainApp.getDetailJadwalPanel() != null && refreshMatkulContext != null) {
            System.out.println("DEBUG: DetailTugasPanel (Back Button) - Refreshing DetailJadwalPanel for matkul: " + refreshMatkulContext.getTitle());
            mainApp.getDetailJadwalPanel().showTasksForMatkul(refreshMatkulContext);
        } else if (mainApp.getDetailJadwalPanel() != null) {
            System.out.println("DEBUG: DetailTugasPanel (Back Button) - Could not determine matkul context for refreshing DetailJadwalPanel. DetailJadwalPanel might show its last state.");
        }
        mainApp.navigateTo(MainApp.TAMBAH_EDIT_APP_SCREEN); // Navigate back to DetailJadwalPanel
    }//GEN-LAST:event_jToggleButtonBackActionPerformed

    // Handles the "Delete" button action for deleting the current task.
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (currentTask != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this task?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                TaskDAO taskDAO = new TaskDAO();
                boolean deleted = taskDAO.deleteById(currentTask.getItemId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Task deleted!");
                    // Instead of just clearing fields, go back to DetailJadwalPanel for the correct matkul
                    ModelLayer.Class.matkul refreshMatkulContext = null;
                    if (currentTask.getIdJm() != null) {
                        MatkulDAO matkulDAO = new MatkulDAO();
                        refreshMatkulContext = matkulDAO.findMatkulByJadwalMatkulId(currentTask.getIdJm());
                    }
                    if (refreshMatkulContext == null && mainApp.getDetailJadwalPanel() != null) {
                        refreshMatkulContext = mainApp.getDetailJadwalPanel().getCurrentDisplayingMatkul();
                    }
                    if (refreshMatkulContext != null && mainApp.getDetailJadwalPanel() != null) {
                        mainApp.getDetailJadwalPanel().showTasksForMatkul(refreshMatkulContext);
                    }
                    mainApp.navigateTo(MainApp.TAMBAH_EDIT_APP_SCREEN);
                    return;
                }
            }
        } else {
            // No task selected, just clear fields (cancel creation)
            loadTask(null);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Handles the "Save/Edit" button action for saving or editing the current task.
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String title = jTextField1.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harus isi judul untuk save tugas. Ya iyalah masa tugas tanpa judul",
            "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Date date = jDateChooser1.getDate();
        if (date == null) {
            JOptionPane.showMessageDialog(this, "isi tanggal nya. format Tanggal-Bulan-Tahun eg. 01-12-1234 atau tombol kalender di bagian kanan", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String description = jTextArea1.getText();
        java.time.LocalDate deadline = new java.sql.Date(date.getTime()).toLocalDate();
        String priorityStr = (String) jComboBox1.getSelectedItem();
        int priority = switch (priorityStr) {
            case "High" -> 2;
            case "Medium" -> 1;
            default -> 0;
        };
        Enumeration.TaskStatus status = jRadioButton2.isSelected() ? Enumeration.TaskStatus.BELUM_SELESAI : Enumeration.TaskStatus.SUDAH_SELESAI;


        TaskDAO taskDAO = new TaskDAO();
        if (currentTask != null) { // Editing an existing task
            // ... (existing update logic for currentTask) ...
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setDeadline(deadline);
            currentTask.setPriority(priority);
            currentTask.setCurrentStatus(status);
            // idUser and idJm should already be set in currentTask
            System.out.println("DEBUG: DetailTugasPanel - Updating task. ID_User: " + currentTask.getIdUser() + ", ID_JM: " + currentTask.getIdJm());

            boolean updated = taskDAO.update(currentTask);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Task updated!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update task.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } else { // Creating a new task
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDeadline(deadline);
            newTask.setPriority(priority);
            newTask.setCurrentStatus(status);

            // Set idUser
            if (userSession != null && userSession.getCurrentUser() != null) {
                newTask.setIdUser(userSession.getCurrentUser().getIdUser());
            } else {
                JOptionPane.showMessageDialog(this, "Error: User session not found. Cannot create task.", "Session Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Determine and set idJm using contextMatkulForNewTask
            matkul matkulForThisNewTask = this.contextMatkulForNewTask; // Use the context passed from DetailJadwalPanel
            Integer selectedIdJm = null;

            System.out.println("DEBUG: DetailTugasPanel - Creating new task. Context matkul: " + (matkulForThisNewTask != null ? matkulForThisNewTask.getTitle() : "null"));

            if (matkulForThisNewTask != null) {
                DataAccessObject.JadwalMatkulDAO jmDao = new DataAccessObject.JadwalMatkulDAO();
                List<Integer> idJms = jmDao.findIdJmsByMatkulId(matkulForThisNewTask.getIdMatkul());
                System.out.println("DEBUG: DetailTugasPanel - idJms found for matkul " + matkulForThisNewTask.getIdMatkul() + ": " + idJms);


                if (idJms != null && !idJms.isEmpty()) {
                    if (idJms.size() == 1) {
                        selectedIdJm = idJms.get(0);
                    } else {
                        selectedIdJm = idJms.get(0); // Defaulting to the first id_jm
                        System.err.println("Warning: Matkul '" + matkulForThisNewTask.getTitle() +
                                           "' has multiple schedule links. Using the first one: " + selectedIdJm);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Cannot create task: The selected course ('" + matkulForThisNewTask.getTitle() + "') is not linked to any schedule.",
                        "Scheduling Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cannot create task: No course context provided for the new task.",
                    "Context Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedIdJm == null) {
                JOptionPane.showMessageDialog(this,
                    "Cannot create task: Failed to determine the schedule link (id_jm) for the course.",
                    "Internal Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            newTask.setIdJm(selectedIdJm);
            System.out.println("DEBUG: DetailTugasPanel - Attempting to create task with ID_User: " + newTask.getIdUser() + ", ID_JM: " + newTask.getIdJm());
            boolean created = taskDAO.create(newTask); // or taskDAO.addTask(newTask)
            if (created) {
                JOptionPane.showMessageDialog(this, "Task created!");
                this.contextMatkulForNewTask = null; // Clear context after successful creation
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create task.", "Creation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // After saving or attempting to save, navigate back and refresh
        ModelLayer.Class.matkul refreshMatkulContext = null;
        if (currentTask != null && currentTask.getIdJm() != null) { // Task was edited
            MatkulDAO matkulDAO = new MatkulDAO();
            refreshMatkulContext = matkulDAO.findMatkulByJadwalMatkulId(currentTask.getIdJm());
        } else if (this.contextMatkulForNewTask != null) { // New task was created (or attempted) with context
            refreshMatkulContext = this.contextMatkulForNewTask;
        }
        // This 'else if' block is the fallback logic
        if (refreshMatkulContext == null && mainApp.getDetailJadwalPanel() != null) {
            ModelLayer.Class.matkul djpMatkul = mainApp.getDetailJadwalPanel().getCurrentDisplayingMatkul();
            if (djpMatkul != null) {
                System.out.println("DEBUG: DetailTugasPanel (Save/Edit Fallback) - Refreshing DetailJadwalPanel using DetailJadwalPanel's current matkul: " + djpMatkul.getTitle());
                refreshMatkulContext = djpMatkul;
            } else {
                // As a very last resort, try HomePagePanel's last selected
                ModelLayer.Class.matkul homePageMatkul = mainApp.getHomePagePanel().getLastSelectedMatkul();
                if (homePageMatkul != null) {
                    System.out.println("DEBUG: DetailTugasPanel (Save/Edit Fallback) - Refreshing DetailJadwalPanel using HomePagePanel's last matkul: " + homePageMatkul.getTitle());
                    refreshMatkulContext = homePageMatkul;
                } else {
                     System.out.println("DEBUG: DetailTugasPanel (Save/Edit Fallback) - Could not determine matkul context for refreshing DetailJadwalPanel.");
                }
            }
        }


        if (mainApp.getDetailJadwalPanel() != null && refreshMatkulContext != null) {
             System.out.println("DEBUG: DetailTugasPanel (Save/Edit) - Refreshing DetailJadwalPanel for matkul: " + refreshMatkulContext.getTitle());
            mainApp.getDetailJadwalPanel().showTasksForMatkul(refreshMatkulContext);
        } else if (mainApp.getDetailJadwalPanel() != null) {
             System.out.println("DEBUG: DetailTugasPanel (Save/Edit) - Could not determine matkul context for refreshing DetailJadwalPanel. DetailJadwalPanel might show its last state.");
        }
        mainApp.navigateTo(MainApp.TAMBAH_EDIT_APP_SCREEN); // Navigate back to DetailJadwalPanel
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    }//GEN-LAST:event_jComboBox1ActionPerformed

    // Auto Generated Code - Do not modify
    private void initComponents() {

        // Inisialisasi Komponen
        jLabel1 = new javax.swing.JLabel(); // Label untuk judul panel
        jToggleButtonBack = new javax.swing.JToggleButton(); // Tombol untuk kembali ke DetailJadwalPanel
        jLabel2 = new javax.swing.JLabel(); // Label untuk Nama Tugas
        jTextField1 = new javax.swing.JTextField(); // Text field untuk Nama Tugas
        jLabel4 = new javax.swing.JLabel(); // Label untuk Deadline
        jLabel5 = new javax.swing.JLabel(); // Label untuk Prioritas
        jComboBox1 = new javax.swing.JComboBox<>(); // Combo box for Prioritas
        jLabel3 = new javax.swing.JLabel(); // Label untuk Deskripsi Tugas
        jScrollPane1 = new javax.swing.JScrollPane(); // Scroll pane untuk Deskripsi Tugas
        jTextArea1 = new javax.swing.JTextArea(); // Text area untuk Deskripsi Tugas
        jRadioButton2 = new javax.swing.JRadioButton(); // Belum Selesai
        jRadioButton1 = new javax.swing.JRadioButton(); // Sudah Selesai
        jButton2 = new javax.swing.JButton(); // Save/Edit button
        jButton1 = new javax.swing.JButton(); // Delete button
        jDateChooser1 = new com.toedter.calendar.JDateChooser(); // Date chooser for Deadline
        jDateChooser1.setDateFormatString("dd-MM-yyyy"); // Set format to day-month-year
        jDateChooser1.getDateEditor().setEnabled(true);  // Allow manual input
        statusButtonGroup = new javax.swing.ButtonGroup();
        statusButtonGroup.add(jRadioButton1); // Sudah Selesai
        statusButtonGroup.add(jRadioButton2); // Belum Selesai

        // Set properties for components
        // Set up the main title label
        jLabel1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 36)); // Large italic font
        jLabel1.setText("TugasKu"); // Panel title

        // Configure the Back button
        jToggleButtonBack.setText("Back");
        jToggleButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jToggleButtonBackActionPerformed(evt); // Handle back navigation
            }
        });

        // Label for the task name
        jLabel2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        jLabel2.setText("Nama Tugas");

        // Text field for entering the task name
        jTextField1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTextField1.setText(""); // Default to empty
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextField1ActionPerformed(evt); // Optional: handle enter key
            }
        });

        // Label for the deadline
        jLabel4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        jLabel4.setText("Deadline");

        // Label for the priority
        jLabel5.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        jLabel5.setText("Prioritas");

        // Combo box for selecting priority (High, Medium, Low)
        jComboBox1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "High", "Medium", "Low" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jComboBox1ActionPerformed(evt); // Optional: handle priority change
            }
        });

        // Label for the description section
        jLabel3.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        jLabel3.setText("Deskripsi");

        // Text area for entering the task description
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        jScrollPane1.setViewportView(jTextArea1);

        // Radio button for "Belum Selesai" (Not Finished)
        jRadioButton2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 24));
        jRadioButton2.setText("Belum Selesai");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jRadioButton2ActionPerformed(evt); // Optional: handle status change
            }
        });

        // Radio button for "Sudah Selesai" (Finished)
        jRadioButton1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 24));
        jRadioButton1.setText("Sudah Selesai");

        // Button for saving or editing the task
        jButton2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        jButton2.setText("Edit/Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt); // Save or update task
            }
        });

        // Button for deleting the task
        jButton1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt); // Delete task
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(179, 179, 179)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox1, 0, 302, Short.MAX_VALUE)
                            .addComponent(jTextField1)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jToggleButtonBack)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jButton1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jButton2))
                            .addComponent(jRadioButton1))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToggleButtonBack)
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        // Group the radio buttons for mutual exclusion
        statusButtonGroup = new javax.swing.ButtonGroup();
        statusButtonGroup.add(jRadioButton1); // Sudah Selesai
        statusButtonGroup.add(jRadioButton2); // Belum Selesai
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButtonBack;
    // End of variables declaration//GEN-END:variables
}