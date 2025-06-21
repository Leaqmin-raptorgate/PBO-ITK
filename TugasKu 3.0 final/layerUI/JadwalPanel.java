package layerUI;

// ===== Imports =====
import Main.MainApp;
import Main.Session;
import ModelLayer.Class.jadwal;
import ModelLayer.Class.matkul;
import ModelLayer.Class.jadwal_matkul;
import DataAccessObject.JadwalDAO;
import DataAccessObject.MatkulDAO;
import DataAccessObject.JadwalMatkulDAO;
import DataAccessObject.TaskDAO;
import javax.swing.JOptionPane;

// ===== Class Declaration =====
public class JadwalPanel extends javax.swing.JPanel {

    // ===== Fields =====
    private MainApp mainApp;
    private Session userSession;
    private Integer currentEditingIdJm = null; // To store the id_jm if editing
    private matkul currentEditingMatkul = null; // To store matkul if editing its schedule
    private jadwal currentEditingJadwal = null; // To store jadwal if editing

    // ===== Constructor =====
    // Creates new form JadwalPanel
    public JadwalPanel(MainApp mainApp, Session userSession) {
        this.mainApp = mainApp;
        this.userSession = userSession;
        initComponents();
        jButtonSaveDetail.setEnabled(true); // Or false, depending on your preferred initial state
    }

    // ===== Public API Methods =====

    // Loads a jadwal_matkul by its id_jm.
    public void loadJadwalMatkul(Integer idJm) {
        this.currentEditingIdJm = idJm;
        clearFields();

        if (idJm == null) {
            System.err.println("ERROR: loadJadwalMatkul called with null idJm.");
            prepareNewJadwalMatkul(null);
            return;
        }

        System.out.println("DEBUG: JadwalPanel - Loading data for id_jm: " + idJm);
        JadwalMatkulDAO jmDAO = new JadwalMatkulDAO();

        Integer idMatkul = jmDAO.findMatkulIdByIdJm(idJm);
        Integer idJadwal = jmDAO.findJadwalIdByIdJm(idJm);

        if (idMatkul != null) {
            MatkulDAO matkulDAO = new MatkulDAO();
            this.currentEditingMatkul = matkulDAO.findById(idMatkul);
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
            this.currentEditingJadwal = jadwalDAO.findById(idJadwal);
            if (this.currentEditingJadwal != null) {
                jComboBox1.setSelectedItem(this.currentEditingJadwal.getHari());
                jComboBox2.setSelectedItem(String.valueOf(this.currentEditingJadwal.getSesi()));
            } else {
                System.err.println("ERROR: Jadwal not found for id_jadwal: " + idJadwal);
            }
        } else {
            System.err.println("ERROR: id_jadwal not found for id_jm: " + idJm);
        }

        jTextField1.setEditable(true); // Allow editing the matkul title
        jComboBox1.setEnabled(true);
        jComboBox2.setEnabled(true);
        jButtonSaveDetail.setEnabled(true);
        jButtonDel.setEnabled(true);
        jButtonEditDetail.setEnabled(true);
    }

    // Prepares the panel for adding a new jadwal_matkul.
    public void prepareNewJadwalMatkul(matkul existingMatkul) {
        this.currentEditingIdJm = null;
        this.currentEditingMatkul = existingMatkul;
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
        jButtonDel.setEnabled(false);
        jButtonEditDetail.setEnabled(false);
        System.out.println("DEBUG: JadwalPanel - Prepared for new Jadwal/Matkul. Existing Matkul: " + (existingMatkul != null ? existingMatkul.getTitle() : "None"));
    }

    // ===== Private Helper Methods =====
    // Clears the input fields in the panel.
    private void clearFields() {
        jTextField1.setText("");
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
    }

    // ===== Event Handlers =====
    // Handles the action when the "Edit" button is clicked.
    private void jButtonEditDetailActionPerformed(java.awt.event.ActionEvent evt) {
    }

    // Handles the action when the "Save" button is clicked.
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
            int userId = userSession.getCurrentUser().getIdUser();

            // --- Custom Duplicate Checks ---
            java.util.List<jadwal_matkul> allJm = jmDAO.findByUserId(userId);
            Integer editingIdJm = this.currentEditingIdJm;

            for (jadwal_matkul jm : allJm) {
                // Skip current editing if editing
                if (editingIdJm != null && jm.getIdJm() == editingIdJm) continue;
                matkul m = matkulDAO.findById(jm.getIdMatkul());
                jadwal j = jadwalDAO.findById(jm.getIdJadwal());
                if (m == null || j == null) continue;

                // Check: same title, different date
                if (m.getTitle().equalsIgnoreCase(matkulTitle) &&
                    (!j.getHari().equals(hari) || j.getSesi() != sesi)) {
                    JOptionPane.showMessageDialog(this, "Ada mata kuliah yang sama di jadwal yang berbeda. Cek lagi judul dan tanggal", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Check: different title, same date
                if (!m.getTitle().equalsIgnoreCase(matkulTitle) &&
                    j.getHari().equals(hari) && j.getSesi() == sesi) {
                    JOptionPane.showMessageDialog(this, "Ada mata kuliah yang sudah terpakai jadwal yang dipilih. Cek lagi judul dan tanggal", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            // --- End Custom Duplicate Checks ---

            if (this.currentEditingIdJm != null) { // Editing existing jadwal_matkul
                if (matkulToSave == null) { // cek matkulToSave
                    JOptionPane.showMessageDialog(this, "Error: Data matkul untuk diedit tidak lengkap.", "Edit Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 1. Update judul matkul jika berubah 
                if (!matkulToSave.getTitle().equals(matkulTitle)) {
                    matkulToSave.setTitle(matkulTitle);
                    boolean matkulUpdated = matkulDAO.update(matkulToSave);
                    if (!matkulUpdated) {
                        // Pesan error spesifik jika gagal karena duplikat
                        if (matkulDAO.findByTitle(matkulTitle, userId) != null) {
                             JOptionPane.showMessageDialog(this, "Judul mata kuliah sudah ada.", "Update Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                             JOptionPane.showMessageDialog(this, "Gagal memperbarui judul matkul.", "Update Error", JOptionPane.ERROR_MESSAGE);
                        }
                        return;
                    }
                }

                // 2. Temukan ID jadwal yang BARU berdasarkan pilihan ComboBox
                jadwal newJadwal = jadwalDAO.findByHariAndSesi(hari, sesi);

                // 3. Update tabel JADWAL_MATKUL untuk menunjuk ke id_jadwal yang baru
                boolean jmUpdated = jmDAO.updateJadwalForJm(this.currentEditingIdJm, newJadwal.getIdJadwal());

                if (jmUpdated) {
                    JOptionPane.showMessageDialog(this, "Jadwal berhasil diperbarui!");
                    mainApp.getHomePagePanel().loadJadwalTugasTable();
                    mainApp.getHomePagePanel().loadMatkulList();
                    mainApp.getHomePagePanel().loadAllTasksToTables();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui referensi jadwal.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }

            } else { // Creating new
                if (matkulToSave == null) {
                    matkulToSave = matkulDAO.findByTitle(matkulTitle, userId);
                    if (matkulToSave == null) {
                        matkulToSave = new matkul();
                        matkulToSave.setTitle(matkulTitle);
                        matkulToSave.setIdUser(userId);
                        int newMatkulId = matkulDAO.create(matkulToSave);
                        if (newMatkulId > 0) {
                            matkulToSave.setIdMatkul(newMatkulId);
                        } else {
                            throw new Exception("Gagal membuat matkul baru.");
                        }
                    }
                }
                jadwal existingJadwal = jadwalDAO.findByHariAndSesi(hari, sesi);
                if (existingJadwal == null) {
                    jadwalToSave = new jadwal();
                    jadwalToSave.setHari(hari);
                    jadwalToSave.setSesi(sesi);
                    int newJadwalId = jadwalDAO.create(jadwalToSave);
                    if (newJadwalId > 0) {
                        jadwalToSave.setIdJadwal(newJadwalId);
                    } else {
                        throw new Exception("Gagal membuat jadwal baru.");
                    }
                } else {
                    jadwalToSave = existingJadwal;
                }

                Integer existingIdJm = jmDAO.findIdJmByMatkulAndJadwalAndUser(matkulToSave.getIdMatkul(), jadwalToSave.getIdJadwal(), userId);
                if (existingIdJm != null) {
                    JOptionPane.showMessageDialog(this, "Jadwal untuk matkul ini sudah ada.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                jadwal_matkul newJm = new jadwal_matkul();
                newJm.setIdMatkul(matkulToSave.getIdMatkul());
                newJm.setIdJadwal(jadwalToSave.getIdJadwal());
                newJm.setIdUser(userSession.getCurrentUser().getIdUser());
                boolean jmCreated = jmDAO.create(newJm);

                if (jmCreated) {
                    JOptionPane.showMessageDialog(this, "Jadwal baru berhasil disimpan!");
                    mainApp.getHomePagePanel().loadJadwalTugasTable();
                    mainApp.getHomePagePanel().loadMatkulList();
                    mainApp.getHomePagePanel().loadAllTasksToTables();
                } else {
                    throw new Exception("Gagal menyimpan link jadwal matkul.");
                }
            }
            mainApp.navigateTo(MainApp.HOME_SCREEN);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Handles the action when the "Back" button is clicked.
    private void jToggleButtonBackActionPerformed(java.awt.event.ActionEvent evt) {
        mainApp.navigateTo(MainApp.HOME_SCREEN);
    }

    // Handles the action when the "Delete" button is clicked.
    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {
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

            Integer idMatkul = jmDAO.findMatkulIdByIdJm(this.currentEditingIdJm);

            boolean tasksDeleted = taskDAO.deleteTasksByIdJm(this.currentEditingIdJm);

            if (!tasksDeleted) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tugas terkait.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean deleted = jmDAO.deleteByIdJm(this.currentEditingIdJm);
            if (deleted) {
                if (idMatkul != null && jmDAO.findIdJmsByMatkulId(idMatkul).isEmpty()) {
                    matkulDAO.deleteById(idMatkul);
                }
                JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus.");
                mainApp.navigateTo(MainApp.HOME_SCREEN);
                mainApp.getHomePagePanel().loadJadwalTugasTable();
                mainApp.getHomePagePanel().loadMatkulList();
                mainApp.getHomePagePanel().loadAllTasksToTables();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
    }//GEN-LAST:event_jComboBox2ActionPerformed

    // ===== Auto-generated GUI Code and Variable Declarations =====
    private void initComponents() {
        // --- GUI Initialization Code (unchanged) ---
        jLabel1 = new javax.swing.JLabel(); // Label for the title of the panel
        jToggleButtonBack = new javax.swing.JToggleButton(); //  Tombol untuk kembali ke HomePagePanel
        jTextField1 = new javax.swing.JTextField(); // TextField untuk Judul Matkul
        jLabelJudulMatkul = new javax.swing.JLabel();
        jLabelHari = new javax.swing.JLabel(); // <-- Renamed from jLabelTugasDeadline
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabelSesi = new javax.swing.JLabel(); // <-- Renamed from jLabelTugasDeadline1
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

        jLabelJudulMatkul.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelJudulMatkul.setText("Judul Matkul"); // Changed label

        jLabelHari.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelHari.setText("Hari"); // Changed label

        jComboBox1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Hari", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu" }));// SELECT HARI SENIN-MINGGU
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabelSesi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelSesi.setText("Sesi"); // Changed label

        jComboBox2.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Sesi", "1", "2", "3", "4" })); //SESI 1-4
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
                                .addComponent(jLabelHari)) // <-- Renamed here
                            .addGroup(layout.createSequentialGroup()
                                .addGap(131, 131, 131)
                                .addComponent(jLabelSesi)) // <-- Renamed here
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(90,90,90) // Adjusted for "Judul Matkul"
                                .addComponent(jLabelJudulMatkul))))) // <-- Renamed here
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
                .addComponent(jLabelJudulMatkul) // <-- Renamed here
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelHari) // <-- Renamed here
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelSesi) // <-- Renamed here
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDel;
    private javax.swing.JButton jButtonEditDetail;
    private javax.swing.JButton jButtonSaveDetail;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelHari;
    private javax.swing.JLabel jLabelSesi;
    private javax.swing.JLabel jLabelJudulMatkul;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButtonBack;
    // End of variables declaration//GEN-END:variables
}