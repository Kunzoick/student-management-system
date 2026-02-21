/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.StudentService;
import dao.AdminDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Student;
import model.User;
import java.sql.Connection;
import javax.swing.SwingWorker;
import model.Admin;
import util.DBConnection;


/**
 *
 * @author jesse
 */
public class ManageStudentFrame extends javax.swing.JFrame {
    private final StudentService studentService= new StudentService();
    private final User currentUser;
    private AdminDAO adminDAO= new AdminDAO();
    private int adminId;
    private DefaultTableModel studentTableModel;

    /**
     * Creates new form ManageStudentFrame
     */
    public ManageStudentFrame(User user) {
        initComponents();
        this.currentUser= user;
        setupTable();
        labelStatus.setText("Loading students...");
        labelStatus.setForeground(Color.blue);
        loadData();
    }
    private void loadData(){
        setButtonsEnabled(false);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<LoadResult, Void> worker= new SwingWorker<>(){
            @Override
            protected LoadResult doInBackground()throws Exception{
                Admin admin= adminDAO.getAdminByUserId(currentUser.getId());
                if(admin== null){
                    throw new Exception("Unable tp find admin record for current user");
                }
                int adminId= admin.getAdminId();
                List<Student> students= studentService.getAllStudents(adminId);
                return new LoadResult(adminId, students);
            }
            @Override
            protected void done(){
                try{
                    LoadResult result= get();
                    ManageStudentFrame.this.adminId= result.adminId;
                    populateTable(result.students);
                    labelStatus.setText("Students loaded successfully");
                    labelStatus.setForeground(Color.green);
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ManageStudentFrame.this, "Error loading data:"+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    new AdminDashboardFrame(currentUser).setVisible(true);
                    ManageStudentFrame.this.dispose();
                }finally{
                    setCursor(oldCursor);
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    //jtable and table model
    private void setupTable(){
        studentTableModel= new DefaultTableModel(new String[]{"#", "Id", "First Name", "Last Name", "Gender", "DOB", "Department", "Enrollment Date", "Status"},
                0){
                    //make table noneditable
                   @Override
                   public boolean isCellEditable(int row, int column){
                       return false;
                   }
                };
        jTable.setModel(studentTableModel);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);  
    }
    private void populateTable(List<Student> students){
        studentTableModel.setRowCount(0);//clear table
        int index= 1;
        for(Student s : students){
            studentTableModel.addRow(new Object[]{
                index++,//for display order
                s.getStudentId(), s.getFirstName(), s.getLastName(), s.getGender(),
                s.getDOB(), s.getDepartment(), s.getEnrollmentDate(), s.getStatus()
            });
        }
    }
    private static class LoadResult{
        final int adminId;
        final List<Student> students;
        
        LoadResult(int adminId, List<Student> students){
            this.adminId= adminId;
            this.students= students;
        }
    }
    //refresh would be called in buttons
    public void refreshTable(){
        loadData();
    }
    private void updateTableRow(int studentId){
        for(int i= 0; i< studentTableModel.getRowCount(); i++){
            if((Integer)studentTableModel.getValueAt(i, 1)== studentId){
                try(Connection conn= DBConnection.getConnection()){
                    Student s= studentService.getStudentById(studentId, adminId, conn);
                    if(s != null){
                        studentTableModel.setValueAt(s.getFirstName(), i, 2);
                        studentTableModel.setValueAt(s.getLastName(), i, 3);
                        studentTableModel.setValueAt(s.getGender(), i, 4);
                        studentTableModel.setValueAt(s.getDOB(), i, 5);
                        studentTableModel.setValueAt(s.getDepartment(), i, 6);
                        studentTableModel.setValueAt(s.getEnrollmentDate(), i, 7);
                        studentTableModel.setValueAt(s.getStatus(), i, 8); 
                    }
                }catch(Exception e){
                    refreshTable();
                }
                break;
            }
        }
    }
    //for selected items on the jtable
    private Integer getSelectedId(){
        int row= jTable.getSelectedRow();
        if(row== -1) return null;
        return(Integer) studentTableModel.getValueAt(row, 1);// id is first column
    }
    //show message 
    private void showMessage(String message, boolean isError){
        labelStatus.setText(message);
        labelStatus.setForeground(isError ? java.awt.Color.red : java.awt.Color.green);
    }
    private void setButtonsEnabled(boolean enabled){
        AddButton.setEnabled(enabled);
        EditButton.setEnabled(enabled);
        DeleteButton.setEnabled(enabled);
        SearchButton.setEnabled(enabled);
        RefreshButton.setEnabled(enabled);
        DAButton.setEnabled(enabled);
        Gradutton.setEnabled(enabled);
        ArchiveModebtn.setEnabled(enabled);
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
        AddButton = new javax.swing.JButton();
        EditButton = new javax.swing.JButton();
        Gradutton = new javax.swing.JButton();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        DeleteButton = new javax.swing.JButton();
        DAButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        ArchiveModebtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("        MANAGE STUDENTS");

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

        AddButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        AddButton.setText("Add Student");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        EditButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        EditButton.setText("Edit Student");
        EditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditButtonActionPerformed(evt);
            }
        });

        Gradutton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Gradutton.setText("Graduated");
        Gradutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GraduttonActionPerformed(evt);
            }
        });

        jTable.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "First Name", "Last Name", "Gender", "DOB", "Department", "Enrollment Date", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable.setAlignmentX(1.0F);
        jScrollPane.setViewportView(jTable);

        jLabel3.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jLabel3.setText("INFO");

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        DeleteButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        DeleteButton.setText("Delete Student");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        DAButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        DAButton.setText("Deactive/Active");
        DAButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DAButtonActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jButton1.setText("BACK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        ArchiveModebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        ArchiveModebtn.setText("Archive Mode");
        ArchiveModebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArchiveModebtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(41, 41, 41)
                                        .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(EditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(120, 120, 120)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(DAButton, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                            .addComponent(Gradutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ArchiveModebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(10, 10, 10)))))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchButton)
                    .addComponent(RefreshButton))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DAButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Gradutton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ArchiveModebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DAButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DAButtonActionPerformed
        // TODO add your handling code here:
        Integer studentId= getSelectedId();
        if(studentId== null){
            showMessage("Pls select a student to update status.", true);
            return;
        }
        try{
            Student student;
            try(Connection conn= DBConnection.getConnection()){
               student= studentService.getStudentById(studentId, adminId, conn);
            }
            if(student== null){
                showMessage("Student not found.", true);
                return;
            }
            String currentStatus= student.getStatus();
            if(currentStatus== null) currentStatus= "Active";
            
            String newStatus;
            if("Active".equalsIgnoreCase(currentStatus)){
                newStatus= "Inactive";
            }else{
                newStatus= "Active";
            }
            boolean updated= studentService.updateStudentStatus(studentId, newStatus, adminId);
            if(updated){
                showMessage("Student "+ newStatus.toLowerCase()+ " successfully.", false);
                updateTableRow(studentId);
            }else{
                showMessage("Failed to update student status", true);
            }
        }catch(Exception e){
            e.printStackTrace();
            showMessage("Error:"+ e.getMessage(), true);
        }
    }//GEN-LAST:event_DAButtonActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        // TODO add your handling code here:
        new AddstudentFrame(currentUser, this).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void EditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditButtonActionPerformed
        // TODO add your handling code here:
        Integer studentId= getSelectedId();
        if(studentId== null){
            showMessage("Pls select a student to edit.", true);
            return;
        }
        new EditstudentFrame(currentUser, studentId).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_EditButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        // TODO add your handling code here:
        Integer studentId= getSelectedId();
        if(studentId== null){
            showMessage("Pls select a student to delete.", true);
            return;
        }
        int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm!= JOptionPane.YES_OPTION) return;
        setButtonsEnabled(false);
        
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showMessage("Deleting student...", false);
        
        SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
            @Override
            protected Boolean doInBackground() throws Exception{
                Connection conn= null;
                try{
                    conn= DBConnection.getConnection();
                    conn.setAutoCommit(false);
                
                    boolean deleted= studentService.deleteStudent(studentId, adminId, conn);
                    if(deleted){
                        conn.commit();
                        return true;
                }else{
                    conn.rollback();
                    return false;
                }
                }catch(Exception e){
                    if(conn != null){
                        try{ conn.rollback(); } catch(Exception ignore){}
                    }
                    throw e;
                }finally{
                    if(conn != null){
                        try{
                            conn.setAutoCommit(true);
                            conn.close();
                        }catch(Exception ignore){}
                    }
                }
            }
            @Override
            protected void done(){
                try{
                    boolean deleted= get();
                    if(deleted){
                        showMessage("Student deleted successfully", false);
                        updateTableRow(studentId);
                    }else{
                        showMessage("Failed to delete student", true);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    showMessage("Error deleting student", true);
                }finally{
                    setCursor(oldCursor);
                    setButtonsEnabled(true);
                }
            }
        };
          worker.execute();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        // TODO add your handling code here:
        String Keyword= SearchField.getText().trim();
        if(Keyword.isEmpty()){
            showMessage("Pls enter a search keyword.", true);
            return;
        }
        SearchButton.setEnabled(false);
        showMessage("Searching...", false);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<List<Student>, Void> worker= new SwingWorker<>(){
            @Override
            protected List<Student> doInBackground() throws Exception{
                return studentService.searchStudents(adminId, Keyword);
            }
            @Override
            protected void done(){
                try{
       
        List<Student> students= get();
         studentTableModel.setRowCount(0);
         int index= 1;
        for(Student s : students){
            studentTableModel.addRow(new Object[]{
                index++,
                s.getStudentId(), s.getFirstName(), s.getLastName(), s.getGender(),
                s.getDOB(), s.getDepartment(), s.getEnrollmentDate(), s.getStatus()
            });
        }
        showMessage(students.isEmpty() ? "No students found." : students.size()+ " student(s) found.", false);
        }catch(Exception e){
            showMessage("Search Error:"+ e.getMessage(), true);
        }finally{
                    setCursor(oldCursor);
                    SearchButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_SearchButtonActionPerformed

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        // TODO add your handling code here:
        refreshTable();
        SearchField.setText("");
        showMessage("Student list refreshed", false);
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void GraduttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GraduttonActionPerformed
        // TODO add your handling code here:
        Integer studentId= getSelectedId();
        if(studentId== null){
            showMessage("Pls select a student", true);
            return;
        }
        try(Connection conn= DBConnection.getConnection()){
            Student student= studentService.getStudentById(studentId, adminId, conn);
            if(student== null){
                showMessage("Student not found", true);
                return;
            }
            String newStatus= "Graduated";
            boolean updated= studentService.updateStudentStatus(studentId, newStatus, adminId);
            if(updated){
                showMessage("Student marked as Graduated successfully", false);
                updateTableRow(studentId);
            }else{
                showMessage("Failed to mark as graduated", true);
            }
        }catch(Exception e){
           e.printStackTrace(); 
           showMessage("Error"+ e.getMessage(), true);
        }
    }//GEN-LAST:event_GraduttonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        new AdminDashboardFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ArchiveModebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ArchiveModebtnActionPerformed
        // TODO add your handling code here:
        Integer studentId= getSelectedId();
        if(studentId== null){
            showMessage("Pls select a student to archive", true);
            return;
        }
        try(Connection conn= DBConnection.getConnection()){
            Student student= studentService.getStudentById(studentId, adminId, conn);
            if(student== null){
                showMessage("Student not found", true);
                return;
            }
            ArchiveModeFrame archiveFrame= new ArchiveModeFrame(currentUser, adminId, student);
            archiveFrame.setVisible(true);
            this.dispose();
        }catch(Exception e){
            e.printStackTrace();
            showMessage("Error loading archive mode: "+ e.getMessage(), true);
        }
    }//GEN-LAST:event_ArchiveModebtnActionPerformed

    /**
     * @param args the command line arguments
     */
    //public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
/*        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageStudentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageStudentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageStudentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageStudentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageStudentFrame().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton ArchiveModebtn;
    private javax.swing.JButton DAButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton EditButton;
    private javax.swing.JButton Gradutton;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JTextField SearchField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel labelStatus;
    // End of variables declaration//GEN-END:variables
}
