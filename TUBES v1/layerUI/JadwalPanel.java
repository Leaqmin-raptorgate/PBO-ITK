package layerUI;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

import Main.MainApp;
import Main.Session;
import ModelLayer.Class.jadwal;
import ModelLayer.Class.matkul;
import ModelLayer.Class.jadwal_matkul; // Create this model if it doesn't exist
import DataAccessObject.JadwalDAO;
import DataAccessObject.MatkulDAO;
import DataAccessObject.JadwalMatkulDAO;
import DataAccessObject.TaskDAO;
import javax.swing.JOptionPane;

/**
 *
 * @author madit
 */
public class JadwalPanel extends javax.swing.JPanel {
    private MainApp mainApp;
    private Session userSession;
    private Integer currentEditingIdJm = null; // To store the id_jm if editing
    private matkul currentEditingMatkul = null; // To store matkul if editing its schedule
    private jadwal currentEditingJadwal = null; // To store jadwal if editing

    /**
     * Creates new form JadwalPanel
     */
    public JadwalPanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();
        // Initially, save button might be disabled until fields are valid or edit mode is active
        jButtonSaveDetail.setEnabled(true); // Or false, depending on your preferred initial state
    }

    public void loadJadwalMatkul(Integer idJm) {
        this.currentEditingIdJm = idJm;
        clearFields();

        if (idJm == null) {
            System.err.println("ERROR: loadJadwalMatkul called with null idJm.");
            prepareNewJadwalMatkul(null); // Fallback to new
            return;
        }

        System.out.println("DEBUG: JadwalPanel - Loading data for id_jm: " + idJm);
        JadwalMatkulDAO jmDAO = new JadwalMatkulDAO();
        // You'll need a method in JadwalMatkulDAO to get matkul and jadwal details from id_jm
        // For example, a method that returns a wrapper object or populates matkul and jadwal objects.
        // Let's assume jmDAO can give us id_matkul and id_jadwal from id_jm.

        // Simplified: Fetch matkul and jadwal separately using ids from jadwal_matkul
        // This requires methods in JadwalMatkulDAO to get id_matkul and id_jadwal from id_jm
        Integer idMatkul = jmDAO.findMatkulIdByIdJm(idJm); // You need to create this DAO method
        Integer idJadwal = jmDAO.findJadwalIdByIdJm(idJm); // You need to create this DAO method

        if (idMatkul != null) {
            MatkulDAO matkulDAO = new MatkulDAO();
            this.currentEditingMatkul = matkulDAO.findById(idMatkul); // Assuming findById exists
            if (this.currentEditingMatkul != null) {
                jTextField1.setText(this.currentEditingMatkul.getTitle());
            } else {
                 System.err.println("ERROR: Matkul not found for id_matkul: " + idMatkul);
            }
        } else {
            System.err.println("ERROR: id_matkul not found for id_jm: " + idJm);
        }

        if (idJadwal != null) {
            JadwalDAO jadwalDAO = new JadwalDAO();
            this.currentEditingJadwal = jadwalDAO.findById(idJadwal); // Assuming findById exists
            if (this.currentEditingJadwal != null) {
                jComboBox1.setSelectedItem(this.currentEditingJadwal.getHari());
                jComboBox2.setSelectedItem(String.valueOf(this.currentEditingJadwal.getSesi()));
            } else {
                System.err.println("ERROR: Jadwal not found for id_jadwal: " + idJadwal);
            }
        } else {
             System.err.println("ERROR: id_jadwal not found for id_jm: " + idJm);
        }

        jTextField1.setEditable(false); // Usually, title isn't changed when editing schedule details
        jComboBox1.setEnabled(true);
        jComboBox2.setEnabled(true);
        jButtonSaveDetail.setEnabled(true);
        jButtonDel.setEnabled(true); // Enable delete if editing
        jButtonEditDetail.setEnabled(true); // Or manage edit state differently
    }

    public void prepareNewJadwalMatkul(matkul existingMatkul) {
        this.currentEditingIdJm = null;
        this.currentEditingMatkul = existingMatkul; // Could be null if completely new
        this.currentEditingJadwal = null;
        clearFields();

        if (existingMatkul != null) {
            jTextField1.setText(existingMatkul.getTitle());
            jTextField1.setEditable(false); // Matkul title is fixed if we're adding a schedule to it
        } else {
            jTextField1.setEditable(true); // Allow entering new matkul title
        }
        jComboBox1.setEnabled(true);
        jComboBox2.setEnabled(true);
        jButtonSaveDetail.setEnabled(true);
        jButtonDel.setEnabled(false); // Cannot delete if new
        jButtonEditDetail.setEnabled(false); // No existing to edit
        System.out.println("DEBUG: JadwalPanel - Prepared for new Jadwal/Matkul. Existing Matkul: " + (existingMatkul != null ? existingMatkul.getTitle() : "None"));
    }

    private void clearFields() {
        jTextField1.setText("");
        jComboBox1.setSelectedIndex(0); // "Select Hari"
        jComboBox2.setSelectedIndex(0); // "Select Sesi"
    }

    private void jButtonSaveDetailActionPerformed(java.awt.event.ActionEvent evt) {
        String matkulTitle = jTextField1.getText().trim();
        String hari = (String) jComboBox1.getSelectedItem();
        String sesiStr = (String) jComboBox2.getSelectedItem();

        if (matkulTitle.isEmpty() || "Select Hari".equals(hari) || "Select Sesi".equals(sesiStr)) {
            JOptionPane.showMessageDialog(this, "Judul, Hari, dan Sesi harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int sesi;
        try {
            sesi = Integer.parseInt(sesiStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sesi tidak valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MatkulDAO matkulDAO = new MatkulDAO();
        JadwalDAO jadwalDAO = new JadwalDAO();
        JadwalMatkulDAO jmDAO = new JadwalMatkulDAO();

        matkul matkulToSave = this.currentEditingMatkul;
        jadwal jadwalToSave = this.currentEditingJadwal;

        try {
            if (this.currentEditingIdJm != null) { // Editing existing jadwal_matkul
                if (matkulToSave == null || jadwalToSave == null) {
                     JOptionPane.showMessageDialog(this, "Error: Data edit tidak lengkap.", "Edit Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                // Update jadwal details
                jadwalToSave.setHari(hari);
                jadwalToSave.setSesi(sesi);
                boolean jadwalUpdated = jadwalDAO.update(jadwalToSave); // Assuming update method exists

                if (jadwalUpdated) {
                    JOptionPane.showMessageDialog(this, "Jadwal berhasil diperbarui!");
                    // Refresh HomePagePanel tables and lists
                    mainApp.getHomePagePanel().loadJadwalTugasTable();
                    mainApp.getHomePagePanel().loadMatkulList();
                    mainApp.getHomePagePanel().loadAllTasksToTables();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui jadwal.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }

            } else { // Creating new
                // Check if matkul exists or create new
                if (matkulToSave == null) { // Completely new matkul
                    matkulToSave = matkulDAO.findByTitle(matkulTitle); // Check if matkul with this title exists
                    if (matkulToSave == null) {
                        matkulToSave = new matkul();
                        matkulToSave.setTitle(matkulTitle);
                        // matkulToSave.setDescription(""); // Add description if your model has it
                        int newMatkulId = matkulDAO.create(matkulToSave); // Assuming create returns new ID
                        if (newMatkulId > 0) {
                            matkulToSave.setIdMatkul(newMatkulId);
                        } else {
                            throw new Exception("Gagal membuat matkul baru.");
                        }
                    }
                }
                // else: matkulToSave was passed from HomePagePanel (adding schedule to existing matkul)

                // Check if jadwal exists or create new
                jadwal existingJadwal = jadwalDAO.findByHariAndSesi(hari, sesi); // You need this DAO method
                if (existingJadwal == null) {
                    jadwalToSave = new jadwal();
                    jadwalToSave.setHari(hari);
                    jadwalToSave.setSesi(sesi);
                    // jadwalToSave.setJudul(matkulTitle); // Or some other logic for jadwal's own title
                    int newJadwalId = jadwalDAO.create(jadwalToSave); // Assuming create returns new ID
                    if (newJadwalId > 0) {
                        jadwalToSave.setIdJadwal(newJadwalId);
                    } else {
                        throw new Exception("Gagal membuat jadwal baru.");
                    }
                } else {
                    jadwalToSave = existingJadwal;
                }

                // Link matkul and jadwal in jadwal_matkul
                // Check if this link already exists
                Integer existingIdJm = jmDAO.findIdJmByMatkulAndJadwal(matkulToSave.getIdMatkul(), jadwalToSave.getIdJadwal());
                if (existingIdJm != null) {
                    JOptionPane.showMessageDialog(this, "Jadwal untuk matkul ini sudah ada.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                jadwal_matkul newJm = new jadwal_matkul();
                newJm.setIdMatkul(matkulToSave.getIdMatkul());
                newJm.setIdJadwal(jadwalToSave.getIdJadwal());
                newJm.setIdUser(userSession.getCurrentUser().getIdUser()); // <-- THIS IS IMPORTANT!
                boolean jmCreated = jmDAO.create(newJm); // Assuming create returns boolean for success

                if (jmCreated) {
                    JOptionPane.showMessageDialog(this, "Jadwal baru berhasil disimpan!");
                    mainApp.getHomePagePanel().loadJadwalTugasTable(); // Refresh table on HomePage
                    mainApp.getHomePagePanel().loadMatkulList(); // Refresh matkul list
                    mainApp.getHomePagePanel().loadAllTasksToTables();
                } else {
                    throw new Exception("Gagal menyimpan link jadwal matkul.");
                }
            }
            mainApp.navigateTo(MainApp.HOME_SCREEN); // Go back home after save
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void jToggleButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBackActionPerformed
        mainApp.navigateTo(MainApp.HOME_SCREEN);
    }//GEN-LAST:event_jToggleButtonBackActionPerformed

    private void jButtonEditDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditDetailActionPerformed
        // Enable fields for editing if they were disabled
        // jTextField1.setEditable(true); // If title can be edited
        jComboBox1.setEnabled(true);
        jComboBox2.setEnabled(true);
        jButtonSaveDetail.setEnabled(true);
    }//GEN-LAST:event_jButtonEditDetailActionPerformed

    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelActionPerformed
        if (this.currentEditingIdJm == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada jadwal yang dipilih untuk dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus jadwal ini? Ini juga akan menghapus semua tugas terkait.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JadwalMatkulDAO jmDAO = new JadwalMatkulDAO();
            TaskDAO taskDAO = new TaskDAO();
            MatkulDAO matkulDAO = new MatkulDAO();

            // Get id_matkul before deleting jadwal_matkul
            Integer idMatkul = jmDAO.findMatkulIdByIdJm(this.currentEditingIdJm);

            // Delete all tasks associated with this id_jm
            boolean tasksDeleted = taskDAO.deleteTasksByIdJm(this.currentEditingIdJm);

            if (!tasksDeleted) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tugas terkait.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean deleted = jmDAO.deleteByIdJm(this.currentEditingIdJm);
            if (deleted) {
                // --- ORPHAN CLEANUP ---
                // Check if this matkul is still referenced by any jadwal_matkul
                if (idMatkul != null && jmDAO.findIdJmsByMatkulId(idMatkul).isEmpty()) {
                    matkulDAO.deleteById(idMatkul);
                }
                // --- END ORPHAN CLEANUP ---

                JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus.");
                mainApp.navigateTo(MainApp.HOME_SCREEN);
                mainApp.getHomePagePanel().loadJadwalTugasTable();
                mainApp.getHomePagePanel().loadMatkulList();
                mainApp.getHomePagePanel().loadAllTasksToTables();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButtonDelActionPerformed

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
        jTextField1 = new javax.swing.JTextField();
        jLabelTugasPenting = new javax.swing.JLabel();
        jLabelTugasDeadline = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabelTugasDeadline1 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButtonEditDetail = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();
        jButtonSaveDetail = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel1.setText("JadwalKu"); // Changed title for clarity

        jToggleButtonBack.setText("Back");
        jToggleButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBackActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        // jTextField1.setText("jTextField1"); // Initial text removed
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabelTugasPenting.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTugasPenting.setText("Judul Matkul"); // Changed label

        jLabelTugasDeadline.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTugasDeadline.setText("Hari");

        jComboBox1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Hari", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabelTugasDeadline1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTugasDeadline1.setText("Sesi");

        jComboBox2.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Sesi", "1", "2", "3", "4" })); // Example Sesi
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jButtonEditDetail.setText("Edit");
        jButtonEditDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditDetailActionPerformed(evt);
            }
        });

        jButtonDel.setText("Hapus");
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });

        jButtonSaveDetail.setText("Save");
        jButtonSaveDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveDetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jButtonDel)
                .addGap(80, 80, 80)
                .addComponent(jButtonEditDetail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jButtonSaveDetail)
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jToggleButtonBack)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(132, 132, 132)
                                .addComponent(jLabelTugasDeadline))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(131, 131, 131)
                                .addComponent(jLabelTugasDeadline1))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(90,90,90) // Adjusted for "Judul Matkul"
                                .addComponent(jLabelTugasPenting)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jToggleButtonBack)
                .addGap(16, 16, 16)
                .addComponent(jLabelTugasPenting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTugasDeadline)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelTugasDeadline1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(250, 250, 250) // Adjusted spacing
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDel)
                    .addComponent(jButtonEditDetail)
                    .addComponent(jButtonSaveDetail))
                .addContainerGap(47, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDel;
    private javax.swing.JButton jButtonEditDetail;
    private javax.swing.JButton jButtonSaveDetail;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTugasDeadline;
    private javax.swing.JLabel jLabelTugasDeadline1;
    private javax.swing.JLabel jLabelTugasPenting;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButtonBack;
    // End of variables declaration//GEN-END:variables
}
