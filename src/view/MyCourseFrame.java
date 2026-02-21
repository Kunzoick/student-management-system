/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import Service.EnrollmentService;
import Service.GradeService;
import dao.EnrollmentDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import model.Course;
import model.Enrollment;
import model.Grade;
import model.Student;

/**
 *
 * @author jesse
 */
public class MyCourseFrame extends javax.swing.JFrame {
    private Student currentStudent;
    private int studentId;
    private int adminId;
    private EnrollmentService enrollmentService;
    private CourseService courseService;
    private GradeService gradeService;
    private DefaultTableModel tableModel;
    //store both enrollmentid and course object for fast drop operation
    private Map<Integer, EnrollmentCourseData> courseDataMap= new HashMap<>();

    /**
     * Creates new form MyCourseFrame
     */
    public MyCourseFrame(Student student) {
        initComponents();
        this.currentStudent= student;
        this.studentId= student.getStudentId();
        this.adminId= student.getAdminId();
        this.enrollmentService= new EnrollmentService();
        this.courseService= new CourseService();
        this.gradeService= new GradeService();
        
        loadProfile();
        setupTable();
        loadCoursesAsync();
    }
    private static class EnrollmentCourseData{
        final int enrollmentId;
        final Course course;
        final Grade grade;
        
        EnrollmentCourseData(int enrollmentId, Course course, Grade grade){
            this.enrollmentId= enrollmentId;
            this.course= course;
            this.grade= grade;
        }
    }
    private static class CourseLoadResult{
        final List<EnrollmentCourseData> courseData;
        final String errorMessage;
        
        CourseLoadResult(List<EnrollmentCourseData> courseData, String errorMessage){
            this.courseData= courseData;
            this.errorMessage= errorMessage;
        }
    }
    
    private void loadProfile(){
        namelabel.setText("Student: [ "+ currentStudent.getFirstName()+ " "+ currentStudent.getLastName()+ " ]");
        adminlabel.setText("Admin ID: [ "+ currentStudent.getAdminId()+ " ]");
        adminlabel.setForeground(Color.blue);
    }
    private void setupTable(){
        tableModel= new DefaultTableModel(new Object[]{"Course Code", "Course Name", "Units", "Score", "Grade", "GP"}, 
            0){
                    //make table noneditable
                  @Override
                  public boolean isCellEditable(int row, int column){
                      return false;
                  }
                };
        jTable.setModel(tableModel);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);
    }
    private void loadCoursesAsync(){
        setControlsEnabled(false);
        labelStatus.setText("Loading courses...");
        labelStatus.setForeground(Color.orange);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<CourseLoadResult, Void> worker= new SwingWorker<>(){
            @Override
            protected CourseLoadResult doInBackground() throws Exception{
                try{
                    List<Enrollment> enrollments= enrollmentService.getEnrollmentsByStudent(studentId, adminId);
                    if(enrollments == null || enrollments.isEmpty()){
                        return new CourseLoadResult(new ArrayList<>(), null);
                    }
                    List<Course> allCourses= courseService.getCourseByAdminId(adminId);
                    Map<Integer, Course> courseMap= new HashMap<>();
                    for(Course c : allCourses){
                        courseMap.put(c.getCourseId(), c);
                    }
                    List<Grade> allGrades= gradeService.getGradeByStudent(studentId, adminId);
                    Map<Integer, Grade> gradeMap= new HashMap<>();
                    for(Grade g : allGrades){
                        gradeMap.put(g.getCourseId(), g);
                    }
                    List<EnrollmentCourseData> courseDataList= new ArrayList<>();
                    for(Enrollment enroll : enrollments){
                        Course course= courseMap.get(enroll.getCourseId());
                        if(course== null) continue;
                        Grade grade= gradeMap.get(enroll.getCourseId());
                        courseDataList.add(new EnrollmentCourseData(enroll.getEnrollmentId(), course, grade));
                    }
                    return new CourseLoadResult(courseDataList, null);
                }catch(Exception e){
                    e.printStackTrace();
                    return new CourseLoadResult(null, e.getMessage());
                }
            }
            @Override
            protected void done(){
                try{
                    CourseLoadResult result= get();
                    tableModel.setRowCount(0);
                    courseDataMap.clear();
                    
                    if(result.errorMessage != null){
                        labelStatus.setText("Error loading courses: "+ result.errorMessage);
                        labelStatus.setForeground(Color.red);
                        return;
                    }
                    if(result.courseData.isEmpty()){
                        labelStatus.setText("No courses enrolled yet!");
                        labelStatus.setForeground(Color.red);
                        return;
                    }
                    //populate table
                    for(EnrollmentCourseData data : result.courseData){
                        tableModel.addRow(new Object[]{
                            data.course.getCourseCode(), data.course.getCourseName(), data.course.getUnits(),
                            data.grade != null ? data.grade.getScore() : "N/A", data.grade !=null ? data.grade.getGrade() : "N/A",
                            data.grade !=null ? data.grade.getGradePoint() : "N/A"
                        });
                        courseDataMap.put(data.course.getCourseId(), data);//store complete data for drop operation
                    }
                    labelStatus.setText("Loaded "+ tableModel.getRowCount()+ " Course(s)");
                    labelStatus.setForeground(Color.green);
                }catch(Exception e){
                    e.printStackTrace();
                    labelStatus.setText("Error diusplaying courses: "+ e.getMessage());
                    labelStatus.setForeground(Color.red);
                }finally{
                    setControlsEnabled(true);
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
    private void setControlsEnabled(boolean enabled){
        refresh.setEnabled(enabled);
        drop.setEnabled(enabled);
        jTable.setEnabled(enabled);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        namelabel = new javax.swing.JLabel();
        adminlabel = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        refresh = new javax.swing.JButton();
        drop = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("MY COURSES");

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Course code", "Course Title", "Units", "Score", "Grade", "GP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setResizable(false);
            jTable.getColumnModel().getColumn(1).setResizable(false);
            jTable.getColumnModel().getColumn(2).setResizable(false);
            jTable.getColumnModel().getColumn(3).setResizable(false);
            jTable.getColumnModel().getColumn(4).setResizable(false);
            jTable.getColumnModel().getColumn(5).setResizable(false);
        }

        namelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        adminlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        labelStatus.setText(" ");

        refresh.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        refresh.setText("Refresh");
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        drop.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        drop.setText("Drop Course");
        drop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropActionPerformed(evt);
            }
        });

        jButton3.setText("Back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
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
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addGap(19, 19, 19))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(adminlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(drop, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adminlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refresh)
                    .addComponent(drop))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        // TODO add your handling code here:
        loadProfile();
        loadCoursesAsync();
    }//GEN-LAST:event_refreshActionPerformed

    private void dropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropActionPerformed
        // TODO add your handling code here:
        int selectedRow= jTable.getSelectedRow();
        if(selectedRow == -1){
            labelStatus.setText("Pls select a course to drop!");
            labelStatus.setForeground(Color.red);
            return;
        }
        String code= tableModel.getValueAt(selectedRow, 0).toString();
        EnrollmentCourseData selectedCourse= null;
        for(EnrollmentCourseData data : courseDataMap.values()){
            if(data.course.getCourseCode().equals(code)){
                selectedCourse= data;
                break;
            }
        }
        if(selectedCourse == null){
            labelStatus.setText("Error: Course data not found");
            labelStatus.setForeground(Color.red);
            return;
        }
        if(selectedCourse.grade !=null){
            JOptionPane.showMessageDialog(this, """
                                                You cannot drop this course because a grade already exists.
                                                
                                                Course: """
                    + selectedCourse.course.getCourseCode()+ "-"+ selectedCourse.course.getCourseName()+ "\n"+ "Grade : "+ 
                    selectedCourse.grade.getGrade()+ "(Score: "+ selectedCourse.grade.getScore()+ ")", "Drop not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
            String message= String.format("Are you sure you want to drop: \n\n%s - %s\n\nThis action cannot be undone.",selectedCourse.course.getCourseCode(), 
                                           selectedCourse.course.getCourseName());
            int confirm= JOptionPane.showConfirmDialog(this, message, "Confirm Drop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm != JOptionPane.YES_OPTION) return;
            dropCourseAsync(selectedCourse);
    }
        private void dropCourseAsync(EnrollmentCourseData courseData){  
           setControlsEnabled(false);
           labelStatus.setText("Dropping course....");
           labelStatus.setForeground(Color.orange);
           Cursor oldCursor= getCursor();
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           
           SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
               @Override
               protected Boolean doInBackground() throws Exception{
                   return enrollmentService.removeEnrollment(adminId, courseData.enrollmentId);
               }
               @Override
               protected void done(){
                   try{
                       boolean removed= get();
                       if(removed){
                           labelStatus.setText("Course dropped successfully.");
                           labelStatus.setForeground(Color.green);
                           loadCoursesAsync();
                       }else{
                           labelStatus.setText("Unable to drop course. Pls try again.");
                           setControlsEnabled(true);
                       }
                   }catch(Exception e){
                       e.printStackTrace();
                       labelStatus.setText("Error dropping course: "+ e.getMessage());
                       labelStatus.setForeground(Color.red);
                       setControlsEnabled(true);
               }finally{
                       setCursor(oldCursor);
                   }
        } 
           };
           worker.execute();
    }//GEN-LAST:event_dropActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        new StudentDashboardFrame(currentStudent).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(MyCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyCourseFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyCourseFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminlabel;
    private javax.swing.JButton drop;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel namelabel;
    private javax.swing.JButton refresh;
    // End of variables declaration//GEN-END:variables
}
