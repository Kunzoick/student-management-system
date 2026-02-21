/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import dao.AdminDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Course;
import model.User;
import util.DBConnection;
import java.sql.Connection;
import javax.swing.SwingWorker;
import model.Admin;

/**
 *
 * @author jesse
 */
public class ManageCourseFrame extends javax.swing.JFrame {
    private User currentUser;
    private CourseService courseService= new CourseService();
    private AdminDAO adminDAO= new AdminDAO();
    private int adminId;
    private DefaultTableModel courseTableModel;

    /**
     * Creates new form ManageCourseFrame
     */
    public ManageCourseFrame(User user) {
        initComponents();
        this.currentUser= user;
        setupTable();
        labelStatus.setText("Loading courses...");
        labelStatus.setForeground(Color.blue);
        loadData();
    }
    private void loadData(){
        setButtonsEnabled(false);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<LoadResult, Void> worker= new SwingWorker<>(){
            @Override
            protected LoadResult doInBackground() throws Exception{
               Admin admin= adminDAO.getAdminByUserId(currentUser.getId());
               if(admin == null){
                   throw new Exception("Unable to find admin record for currentUser");
               }
               int adminId= admin.getAdminId();
               List<Course> courses= courseService.getCourseByAdminId(adminId);
               return new LoadResult(adminId, courses);
            }
            @Override
            protected void done(){
                try{
                    LoadResult result= get();
                    ManageCourseFrame.this.adminId= result.adminId;
                    populateTable(result.courses);
                    labelStatus.setText("Courses loaded successfully");
                    labelStatus.setForeground(Color.green);
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ManageCourseFrame.this, "Error fetching adminId:"+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    new AdminDashboardFrame(currentUser).setVisible(true);
                    ManageCourseFrame.this.dispose();
                }finally{
                     setCursor(oldCursor);
                    setButtonsEnabled(true);
                }
            }
        };
        worker.execute();
    }
    private void populateTable(List<Course> courses){
        courseTableModel.setRowCount(0);
        int index= 1;
        for(Course c : courses){
            courseTableModel.addRow(new Object[]{
                index++,//for display order
                c.getCourseId(), c.getCourseCode(), c.getCourseName(), 
                c.getDescription(), c.getUnits()
            });
        }
    }
    private static class LoadResult{
        final int adminId;
        final List<Course> courses;
        
        LoadResult(int adminId, List<Course> courses){
            this.adminId= adminId;
            this.courses= courses;
        }
    }
    
    //jtable and table model
    private void setupTable(){
        courseTableModel= new DefaultTableModel(new String[]{"#", "CourseId", "Code", "Title", "Description", "Units"},0)
                {
                    //make table noneditable
                   @Override
                   public boolean isCellEditable(int row, int column){
                       return false;
                   }
                };
        jTable.setModel(courseTableModel);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);  
    }

     public void refreshTable(){
        loadData();
    }
    
    //for selected items on the jtable
    private Integer getSelectedId(){
        int row= jTable.getSelectedRow();
        if(row== -1) return null;
        return(Integer) courseTableModel.getValueAt(row, 1);// id is first column
    }
    //show message 
    private void showMessage(String message, boolean isError){
        labelStatus.setText(message);
        labelStatus.setForeground(isError ? java.awt.Color.red : java.awt.Color.green);
    }
    private void setButtonsEnabled(boolean enabled){
        DeleteButton.setEnabled(enabled);
        EditButton.setEnabled(enabled);
        AddButton.setEnabled(enabled);
        SearchButton.setEnabled(enabled);
        RefreshButton.setEnabled(enabled);
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
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        DeleteButton = new javax.swing.JButton();
        Backbtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("        MANAGE COURSES");

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
        AddButton.setText("Add Course");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        EditButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        EditButton.setText("Edit Course");
        EditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditButtonActionPerformed(evt);
            }
        });

        jTable.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "CourseID", "Code", "Title", "Description", "Units"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        labelStatus.setText("jLabel4");

        DeleteButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        DeleteButton.setText("Delete Course");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        Backbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Backbtn.setText("BACK");
        Backbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(183, 183, 183))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(Backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(EditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37))))
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
                    .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
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

    private void BackbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackbtnActionPerformed
        // TODO add your handling code here:
        new AdminDashboardFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BackbtnActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        // TODO add your handling code here:
        Integer courseId= getSelectedId();
        if(courseId== null){
            showMessage("Pls select a course to delete.", true);
            return;
        }
        int confirm= JOptionPane.showConfirmDialog(this,"Are you sure you want to delete this Course",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if(confirm== JOptionPane.YES_OPTION){
            try{ 
                boolean deleted= courseService.deleteCourse(courseId, adminId);
                if(deleted){
                    showMessage("Course deleted successfully", false);
                    refreshTable();
                }else{
                    showMessage("Failed to delete course.", true);
                }
            }catch(Exception e){
                e.printStackTrace();
                showMessage("Error deleting Course", true);
            }
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void EditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditButtonActionPerformed
        // TODO add your handling code here:
      Integer courseId= getSelectedId();
      if(courseId== null){
          showMessage("Pls select a course to edit", true);
          return;
      }
      new EditcourseFrame(currentUser, courseId).setVisible(true);
      this.dispose();
    }//GEN-LAST:event_EditButtonActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        // TODO add your handling code here:
        new AddCoursesFrame(currentUser, this).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        // TODO add your handling code here:
        refreshTable();
        SearchField.setText("");
        showMessage("Course list refreshed", false);
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        // TODO add your handling code here:
        String Keyword= SearchField.getText().trim();
        if(Keyword.isEmpty()){
            showMessage("Pls enter a search keyword.", true);
            return;
        }
        try{
            courseTableModel.setRowCount(0);
        List<Course> courses= courseService.searchCourses(adminId, Keyword);
        int index= 1;
        for(Course c : courses){
            courseTableModel.addRow(new Object[]{
                index++,
                c.getCourseId(), c.getCourseCode(), c.getCourseName(), 
                c.getDescription(), c.getUnits()
            });
        }
        showMessage(courses.isEmpty() ? "No courses found." : courses.size()+ " course(s) found.", false);
        }catch(Exception e){
            showMessage("Search failed"+ e.getMessage(), true);
        }
       
    }//GEN-LAST:event_SearchButtonActionPerformed

    /**
     * @param args the command line arguments
     *
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
            java.util.logging.Logger.getLogger(ManageCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageCourseFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton Backbtn;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton EditButton;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JTextField SearchField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel labelStatus;
    // End of variables declaration//GEN-END:variables
}
