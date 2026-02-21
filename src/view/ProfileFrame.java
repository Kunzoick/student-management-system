/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.AdminService;
import Service.CourseService;
import Service.GradeService;
import Service.StudentService;
import dao.AdminDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.Student;
import model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingWorker;
import model.Course;
import model.Enrollment;

/**
 *
 * @author jesse
 */
public class ProfileFrame extends javax.swing.JFrame {
    private User currentUser;
    private Student currentStudent;
    private AdminDAO adminDAO= new AdminDAO();
    private StudentDAO studentDAO= new StudentDAO();
    private AdminService adminService= new AdminService();
    private StudentService studentService= new StudentService();
    private CourseService courseService= new CourseService();
    private EnrollmentDAO enrollmentDAO= new EnrollmentDAO();
    private GradeService gradeService= new GradeService();
    private DefaultTableModel tableModel;

    /**
     * Creates new form ProfileFrame
     */
    public ProfileFrame(User currentUser) {
        initComponents();
        this.currentUser= currentUser;
        userlabel.setText(currentUser.getUsername());
        statuslabel.setText("Loading...");
        GPA.setText("Loading..");
        loadDataAsync();
    }
    private void loadDataAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<ProfileData, Void> worker= new SwingWorker<>(){
            @Override
            protected ProfileData doInBackground() throws Exception{
                Student student= studentDAO.getStudentByUserId(currentUser.getId());
                if(student== null){
                    throw new Exception("Student record not found");
                }
                Admin admin= adminDAO.getAdminByadminId(student.getAdminId());
                List<Enrollment> enrollments= enrollmentDAO.getEnrollmentsByStudent(student.getStudentId(), student.getAdminId());
                List<Course> allCourses= courseService.getCourseByAdminId(student.getAdminId());
                Map<Integer, Course> courseMap= new HashMap<>();
                for(Course c : allCourses){
                    courseMap.put(c.getCourseId(), c);
                }
                List<EnrollmentCourse> enrollmentCourses= new ArrayList<>();
                Set<Integer> addedCourseIds= new HashSet<>();
                for(Enrollment e : enrollments){
                    int courseId= e.getCourseId();
                    if(!addedCourseIds.contains(courseId)){
                        Course course= courseMap.get(courseId);
                        if(course != null){
                            enrollmentCourses.add(new EnrollmentCourse(course.getCourseCode(), course.getCourseName()));
                            addedCourseIds.add(courseId);
                        }else{
                            enrollmentCourses.add(new EnrollmentCourse("Unknown", "Course not found(ID: "+ courseId+ ")"));
                            
                        }
                    }
                }
                double gpa= gradeService.calculateGPA(student.getStudentId(), student.getAdminId());
                return new ProfileData(student, admin, enrollmentCourses, gpa);
            }
            @Override
            protected void done(){
                try{
                    ProfileData data= get();
                    namelabel.setText(data.student.getFirstName()+ " "+ data.student.getLastName());
                   Idlabel.setText(String.valueOf(data.student.getStudentId()));
                   reglabel.setText(formatDate(data.student.getCreatedAt()));
                   if(data.admin !=null){
                       adminlabel.setText(String.valueOf(data.student.getAdminId()));
                   }else{
                       adminlabel.setText("N/A");
                   }
                   if("Graduated".equalsIgnoreCase(data.student.getStatus())){
                       gradlabel.setText(formatDate(data.student.getUpdatedAt()));
                   }else{
                       gradlabel.setText("N/A");
                   }
                   statuslabel.setText(data.student.getStatus());
                   
                   currentStudent= data.student;
                   //setup table
                   tableModel= new DefaultTableModel(new String[]{"Course Code", "Course Name"}, 0){
                @Override
                public boolean isCellEditable(int row, int column){
                return false;
            }
        };
                jTable1.setModel(tableModel);
                jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jTable1.getTableHeader().setReorderingAllowed(false);
                
                if(data.enrollmentCourses.isEmpty()){
                    tableModel.addRow(new Object[]{"-", "No completed courses found"});
                    GPA.setText("Total GPA: N/A");
                }else{
                    for(EnrollmentCourse ec : data.enrollmentCourses){
                        tableModel.addRow(new Object[]{ec.courseCode, ec.courseName});
                    }
                    //set gpa with colours
            if(data.gpa <= 0.0){
                GPA.setText("Total GPA: N/A");
                GPA.setForeground(Color.gray);
            }else{
                GPA.setText(String.format("Total GPA: %.2f", data.gpa));
                if(data.gpa >= 3.5){
                    GPA.setForeground(Color.green);
                }else if(data.gpa >= 3.0){
                    GPA.setForeground(Color.blue);
                }else if(data.gpa >= 2.5){
                    GPA.setForeground(Color.orange);
                }else if(data.gpa >= 1.5){
                    GPA.setForeground(Color.pink);
                }else{
                    GPA.setForeground(Color.red);
                }
            }                    
                }
        }catch(Exception e){
             e.printStackTrace();
            JOptionPane.showMessageDialog(ProfileFrame.this, "Error loading profile: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            GPA.setText("Total GPA: N/A");
            statuslabel.setText("Error");
            
            }finally{
            setCursor(oldCursor);
}
        }
    };
    worker.execute();
    }
    
    private static class ProfileData{
        final Student student;
        final Admin admin;
        final List<EnrollmentCourse> enrollmentCourses;
        final double gpa;
        
        ProfileData(Student student, Admin admin, List<EnrollmentCourse> enrollmentCourses, double gpa){
            this.student= student;
            this.admin= admin;
            this.enrollmentCourses= enrollmentCourses;
            this.gpa= gpa;
        }
    }
    private static class EnrollmentCourse{
        final String courseCode;
        final String courseName;
        
        EnrollmentCourse(String courseCode, String courseName){
            this.courseCode= courseCode;
            this.courseName= courseName;
        }
    }
    private String formatDate(LocalDateTime dateTime){
        if(dateTime== null) return "N/A";
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
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
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        namelabel = new javax.swing.JLabel();
        Idlabel = new javax.swing.JLabel();
        adminlabel = new javax.swing.JLabel();
        reglabel = new javax.swing.JLabel();
        gradlabel = new javax.swing.JLabel();
        userlabel = new javax.swing.JLabel();
        statuslabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        GPA = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 0, 14)); // NOI18N
        jLabel1.setText("STUDENT PROFILE");

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel2.setText("Full Name:");

        jLabel3.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel3.setText("Student ID:");

        jLabel4.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel4.setText("Admin ID:");

        jLabel5.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel5.setText("Registered On:");

        jLabel6.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel6.setText("Graduated on:");

        jLabel7.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel7.setText("Username:");

        jLabel8.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel8.setText("Status:");

        namelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        Idlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        adminlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        reglabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        gradlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        userlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        statuslabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(adminlabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(reglabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(gradlabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(userlabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(statuslabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(144, 144, 144))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adminlabel, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reglabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gradlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statuslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Course Code", "Course Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel9.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel9.setText("Completed Courses");

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(187, 187, 187))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(GPA, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(GPA, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jButton1)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        if("admin".equalsIgnoreCase(currentUser.getRole())){
            new AdminDashboardFrame(currentUser).setVisible(true);
        }else if("student".equalsIgnoreCase(currentUser.getRole())){
            if(currentStudent != null && "graduated".equalsIgnoreCase(currentStudent.getStatus())){
               GradStudentDashboardFrame gradFrame= new GradStudentDashboardFrame(currentUser);
               gradFrame.setGraduatedMode(true);
               gradFrame.setVisible(true);
            }else if(currentStudent != null){
            new StudentDashboardFrame(currentStudent).setVisible(true);
            }else{
                JOptionPane.showMessageDialog(this, "Error: Student record not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Unknown User role", "User Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel 
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
            java.util.logging.Logger.getLogger(ProfileFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProfileFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProfileFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProfileFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProfileFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel GPA;
    private javax.swing.JLabel Idlabel;
    private javax.swing.JLabel adminlabel;
    private javax.swing.JLabel gradlabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel namelabel;
    private javax.swing.JLabel reglabel;
    private javax.swing.JLabel statuslabel;
    private javax.swing.JLabel userlabel;
    // End of variables declaration//GEN-END:variables
}
