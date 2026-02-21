/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import Service.GradeService;
import Service.ReportService;
import Service.TranscriptArchiveService;
import dao.AdminDAO;
import dao.EnrollmentDAO;
import dao.GradeDAO;
import dao.StudentDAO;
import dao.TranscriptArchivedDAO;
import dto.StudentTranscriptDTO;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.User;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import model.Course;
import model.Grade;
import model.TranscriptArchive;
import util.DBConnection;
import util.TranscriptUtil;

/**
 *
 * @author jesse
 */
public class TranscriptArchiveFrame extends javax.swing.JFrame {
    private User currentUser;
    private int adminId;
    private AdminDAO adminDAO;
    private StudentDAO studentDAO;
    private TranscriptArchiveService archiveService;
    private ReportService reportService;
    private GradeService gradeService;
    private CourseService courseService;
    private DefaultTableModel archiveTableModel;
    private DefaultTableModel courseTableModel;

    /**
     * Creates new form TranscriptArchiveFrame
     */
    public TranscriptArchiveFrame(User currentUser) {
        initComponents();
        this.currentUser= currentUser;
        if(!validateUserRole()){
            dispose();
            return;
        }
   
        setupServices();
        configureTables();
        setControlsEnabled(false);
        labelStatus.setText("Loading...");
        labelStatus.setForeground(Color.orange);
        loadDataAsync();
    }
    private boolean validateUserRole(){
        if(!"admin".equalsIgnoreCase(currentUser.getRole())){
            JOptionPane.showMessageDialog(this, "Access denied: Only Admin process access!", "Security Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    public void loadDataAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<ArchiveData, Void> worker= new SwingWorker<>(){
            @Override
            protected ArchiveData doInBackground() throws Exception{
                adminDAO= new AdminDAO();
                Admin admin= adminDAO.getAdminByUserId(currentUser.getId());
                if(admin== null){
                    throw new Exception("Unable to find admin record for Current user");
                }
                int adminId= admin.getAdminId();
                List<TranscriptArchive> archives= archiveService.getArchivedStudents(adminId);
                return new ArchiveData(adminId, archives);
            }
            @Override
            protected void done(){
                try{
                    ArchiveData data= get();
                    TranscriptArchiveFrame.this.adminId= data.adminId;
                    //populate table
                    archiveTableModel.setRowCount(0);
                    for(TranscriptArchive archive : data.archives){
                    String name= archive.getFirstName()+ " "+ archive.getLastName();
                    archiveTableModel.addRow(new Object[]{
                    archive.getId(), name, archive.getDepartment(),
                    String.format("%.2f", archive.getGPA()),
                    archive.getArchivedType(), archive.getArchivedAt().toLocalDate().toString()
                });
            }
             labelStatus.setText("Loaded "+ data.archives.size() + "archived records.");
             labelStatus.setForeground(Color.blue);
             setControlsEnabled(true);
             
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TranscriptArchiveFrame.this, "Error loading archives: "+ e.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
                    new AdminDashboardFrame(currentUser).setVisible(true);
                    TranscriptArchiveFrame.this.dispose();
                }
            }
        };
        worker.execute();
    }
    private void setControlsEnabled(boolean enabled){
        SearchButton.setEnabled(enabled);
        RefreshButton.setEnabled(enabled);
        ViewBtn.setEnabled(enabled);
        Export.setEnabled(enabled);
        Deletebtn.setEnabled(enabled);
        SearchField.setEnabled(enabled);
        jTable1.setEnabled(enabled);
    }
    private static class ArchiveData{
        final int adminId;
        final List<TranscriptArchive> archives;
        
        ArchiveData(int adminId, List<TranscriptArchive> archives){
            this.adminId= adminId;
            this.archives= archives;
        }
    }
    
    private void setupServices(){
        try{
        studentDAO= new StudentDAO();
        EnrollmentDAO enrollmentDAO= new EnrollmentDAO();
        TranscriptArchivedDAO transcriptDAO= new TranscriptArchivedDAO();
        AdminDAO adminDAO= new AdminDAO();
        gradeService= new GradeService();
        courseService= new CourseService();
        reportService= new ReportService();
        archiveService= new TranscriptArchiveService(transcriptDAO, gradeService,studentDAO, enrollmentDAO, adminDAO);           
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Services couldn't setup" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           }
    }
    private void configureTables(){
        archiveTableModel= new DefaultTableModel(
                new Object[]{"ID", "Name", "Department", "GPA", "Type", "Date"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jTable1.setModel(archiveTableModel);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        courseTableModel= new DefaultTableModel(
               new Object[]{"Course", "Score", "Grade", "Grade Point", "Units"}, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jTable2.setModel(courseTableModel);    
    }
    
   
   
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        SearchField = new javax.swing.JTextField();
        SearchButton = new javax.swing.JButton();
        RefreshButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        ViewBtn = new javax.swing.JButton();
        Export = new javax.swing.JButton();
        Deletebtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        Namelabel = new javax.swing.JLabel();
        GPA = new javax.swing.JLabel();
        datelabel = new javax.swing.JLabel();
        Deptlabel = new javax.swing.JLabel();
        Typelabel = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("TRANSCRIPT ARCHIVE MANAGEMENT");

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 1, 15)); // NOI18N
        jLabel2.setText("Search:");

        SearchField.setFont(new java.awt.Font("Footlight MT Light", 0, 14)); // NOI18N

        SearchButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        SearchButton.setText("Search");
        SearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchButtonActionPerformed(evt);
            }
        });

        RefreshButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel3.setText("Archived Students Table");

        jTable1.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Department", "GPA", "Type", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
        }

        ViewBtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        ViewBtn.setText("View Transcript");
        ViewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewBtnActionPerformed(evt);
            }
        });

        Export.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Export.setText("Export PDF");
        Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportActionPerformed(evt);
            }
        });

        Deletebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Deletebtn.setText("Delete Permanently");
        Deletebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeletebtnActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jLabel4.setText("Transcript Preview");

        jPanel2.setBackground(new java.awt.Color(255, 153, 153));

        Namelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        Namelabel.setText("*");

        GPA.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        GPA.setText("*");

        datelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        datelabel.setText("*");

        Deptlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        Deptlabel.setText("*");

        Typelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        Typelabel.setText("*");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Deptlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Typelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(GPA, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Namelabel)
                    .addComponent(datelabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(GPA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Deptlabel)
                    .addComponent(Typelabel))
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jLabel10.setText("Courses");

        jTable2.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Course", "Score", "Grade", "Grade Point", "Units"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
            jTable2.getColumnModel().getColumn(4).setResizable(false);
        }

        jButton4.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jButton4.setText("Back");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(148, 148, 148)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 76, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(ViewBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56)
                                .addComponent(Export, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addComponent(Deletebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchButton)
                    .addComponent(RefreshButton))
                .addGap(24, 24, 24)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ViewBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Export, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Deletebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        // TODO add your handling code here:
        String keyword= SearchField.getText().trim();
        if(keyword.isEmpty()){
            loadDataAsync();
            return;
        }
        setControlsEnabled(false);
        labelStatus.setText("Searching...");
        labelStatus.setForeground(Color.orange);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<List<TranscriptArchive>, Void> worker= new SwingWorker<>(){
            @Override
            protected List<TranscriptArchive> doInBackground() throws Exception{
                return archiveService.searchArchived(adminId, keyword);
            }
            @Override
            protected void done(){
                try{
                    List<TranscriptArchive> results= get();
                    archiveTableModel.setRowCount(0);
                     for(TranscriptArchive archive : results){
                String name= archive.getFirstName()+ " "+ archive.getLastName();
                archiveTableModel.addRow(new Object[]{
                    archive.getId(), name, archive.getDepartment(),
                    String.format("%.2f", archive.getGPA()),
                    archive.getArchivedType(), archive.getArchivedAt().toLocalDate().toString()
                });
            }
                labelStatus.setText("Found " + results.size() + " record(s) for '"+ keyword+ "'.");
                labelStatus.setForeground(Color.green);     
                }catch(Exception ex){
            JOptionPane.showMessageDialog(TranscriptArchiveFrame.this, "Search error: "+ ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            labelStatus.setText("Search Error: "+ ex.getMessage());
            labelStatus.setForeground(Color.red);
            }finally{
                    setControlsEnabled(true);
                    setCursor(oldCursor);
                }
        }
        };
        worker.execute();
    }//GEN-LAST:event_SearchButtonActionPerformed

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        // TODO add your handling code here:
        Namelabel.setText("Student: ");
        GPA.setText("GPA: ");
        datelabel.setText("Archived: ");
        Deptlabel.setText("Department: ");
        Typelabel.setText("Archived Type: ");
        courseTableModel.setRowCount(0);
        SearchField.setText("");
        loadDataAsync();
       
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
        if(selectedRow== -1){
            JOptionPane.showMessageDialog(this, "Pls select a transcript record to export");
            labelStatus.setText("Pls select a transcript record to export");
            labelStatus.setForeground(Color.orange);
            return;
        }
        int archiveId= (int) jTable1.getValueAt(selectedRow, 0);
        setControlsEnabled(false);
        labelStatus.setText("Loading Transcript...");
        labelStatus.setForeground(Color.orange);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<String, Void> worker= new SwingWorker<>(){
            @Override
            protected String doInBackground() throws Exception{
                TranscriptArchive archive= archiveService.getArchivedStudentById(adminId, archiveId);
                if(archive== null){
                    throw new Exception("Archive not found");
            }
              StudentTranscriptDTO transcript;  
              try(Connection conn= DBConnection.getConnection()){
              transcript= reportService.generateStudentTranscript(archive.getStudentId(), adminId, conn);
              if(transcript== null){
                  throw new Exception("Failed to generate transcript data");
              }
            }
              reportService.exportTranscriptArchiveToPDF(transcript, archive);
              return archive.getFirstName()+ " "+ archive.getLastName();
        }
            @Override
            protected void done(){
                try{
                    String studentName= get();
                    labelStatus.setText("PDF exported successfully for "+ studentName);
                    labelStatus.setForeground(Color.green);
                }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(TranscriptArchiveFrame.this, "Error exporting transcript: "+ e.getMessage());
            labelStatus.setText("Error Failed!");
            labelStatus.setForeground(Color.red);
        }finally{
                    setControlsEnabled(true);
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_ExportActionPerformed

    private void ViewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewBtnActionPerformed
        // TODO add your handling code here:
        int selectedRow= jTable1.getSelectedRow();
        if(selectedRow== -1){
            JOptionPane.showMessageDialog(this, "Pls select a record to view", "No selection", JOptionPane.WARNING_MESSAGE);
            labelStatus.setText("Pls select a record first");
            labelStatus.setForeground(Color.red);
            return;
        }
        int archiveId= (int) jTable1.getValueAt(selectedRow, 0);
        setControlsEnabled(false);
        labelStatus.setText("Loading Transcript...");
        labelStatus.setForeground(Color.orange);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<ViewData, Void> worker= new SwingWorker<>(){
            @Override
            protected ViewData doInBackground() throws Exception{
                TranscriptArchive archive= archiveService.getArchivedStudentById(adminId, archiveId);
                if(archive== null){
                    throw new Exception("Archive not found");
                }
                List<Grade> grades= TranscriptUtil.fromJson(archive.getTranscriptJson());
                List<Course> allCourses= courseService.getCourseByAdminId(adminId);
                Map<Integer, Course> courseMap= new HashMap<>();
                for(Course c : allCourses){
                    courseMap.put(c.getCourseId(), c);
                }
                return new ViewData(archive, grades, courseMap);
            }
            @Override
            protected void done(){
                try{
                    ViewData data= get();
                    String dateText= (data.archive.getArchivedAt() != null) ? data.archive.getArchivedAt().toLocalDate().toString() : "N/A";
            Namelabel.setText("Student: "+ data.archive.getFirstName()+ " "+ data.archive.getLastName());
            Deptlabel.setText("Department: "+ data.archive.getDepartment());
            GPA.setText("GPA: "+ String.format("%.2f", data.archive.getGPA()));
            Typelabel.setText("Archived Type: "+ data.archive.getArchivedType());
            datelabel.setText("Archived: "+ dateText);
            
            courseTableModel.setRowCount(0);
            
            if(data.grades == null || data.grades.isEmpty()){
                labelStatus.setText("No grade records found in archive.");
                labelStatus.setForeground(Color.red);
            }else{
                for(Grade g : data.grades){
                    Course course= data.courseMap.get(g.getCourseId());
                    if(course !=null){
                    courseTableModel.addRow(new Object[]{
                        course.getCourseCode(), g.getScore(), g.getGrade(),
                        g.getGradePoint(), course.getUnits()
                    });
                }
                }
                labelStatus.setText("Transcript loaded for "+ data.archive.getFirstName()+ " "+ data.archive.getLastName());
                labelStatus.setForeground(Color.green);
            }
                }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(TranscriptArchiveFrame.this, "Error viewing transcript: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            labelStatus.setText("Error viewing transcript");
            labelStatus.setForeground(Color.red);
        }finally{
                setControlsEnabled(true);
                setCursor(oldCursor);
            }
            }
        };
        worker.execute();
}
private static class ViewData{
    final TranscriptArchive archive;
    final List<Grade> grades;
    final Map<Integer, Course> courseMap;
    
    ViewData(TranscriptArchive archive, List<Grade> grades, Map<Integer, Course> courseMap){
        this.archive= archive;
        this.grades= grades;
        this.courseMap= courseMap;
    }
    }//GEN-LAST:event_ViewBtnActionPerformed

    private void DeletebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeletebtnActionPerformed
        // TODO add your handling code here:
        int selectedRow= jTable1.getSelectedRow();
        if(selectedRow== -1){
            JOptionPane.showMessageDialog(this, "Pls select a record to delete", "No selection", JOptionPane.WARNING_MESSAGE);
            labelStatus.setText("pls select a record first");
            labelStatus.setForeground(Color.red);
            return;
        }
        try{
             int archiveid= (int) jTable1.getValueAt(selectedRow, 0);
            TranscriptArchive archive= archiveService.getArchivedStudentById(adminId, archiveid);
            if(archive== null){
                JOptionPane.showMessageDialog(this, "Archive not found");
                return;
        }
            int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this Archived record for:\n"+ archive.getFirstName()+ " "+ archive.getLastName()
                    + "?\n\n"+ "This results to complete removal of the student details and it cannot be reversed!",
                "Confirm Complete Removal", JOptionPane.YES_NO_OPTION);
        if(confirm== JOptionPane.YES_OPTION){
            setControlsEnabled(false);
            labelStatus.setText("Loading Transcript...");
            labelStatus.setForeground(Color.orange);
            Cursor oldCursor= getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
                @Override
                protected Boolean doInBackground() throws Exception{
                    return archiveService.deleteArchive(archiveid);
                }
                @Override
                protected void done(){
                    try{
                        boolean deleted= get();
                        if(deleted){
                        labelStatus.setText("Archived Student has been deleted Successfuly");
                        labelStatus.setForeground(Color.blue);
                        Namelabel.setText("Student: ");
                        GPA.setText("GPA: ");
                        datelabel.setText("Archived: ");
                        Deptlabel.setText("Department: ");
                        Typelabel.setText("Archived Type: ");
                        courseTableModel.setRowCount(0);
                        
                        archiveTableModel.removeRow(selectedRow);
                    }else{
                        labelStatus.setText("Failed to delete!");
                        labelStatus.setForeground(Color.red);  
                        }
                }catch(Exception e){
            e.printStackTrace();
            labelStatus.setText("Error deleting archived Student!");
            labelStatus.setForeground(Color.red);
        }finally{
                    setControlsEnabled(true);
                    setCursor(oldCursor);
                }
            }       
            };
            worker.execute();
        }
        }catch(Exception e){
            e.printStackTrace();
            labelStatus.setText("Error deleting archived Student!");
            labelStatus.setForeground(Color.red);
        }        

    }//GEN-LAST:event_DeletebtnActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new AdminDashboardFrame(currentUser).setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TranscriptArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TranscriptArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TranscriptArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TranscriptArchiveFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TranscriptArchiveFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Deletebtn;
    private javax.swing.JLabel Deptlabel;
    private javax.swing.JButton Export;
    private javax.swing.JLabel GPA;
    private javax.swing.JLabel Namelabel;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JTextField SearchField;
    private javax.swing.JLabel Typelabel;
    private javax.swing.JButton ViewBtn;
    private javax.swing.JLabel datelabel;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel labelStatus;
    // End of variables declaration//GEN-END:variables
}
