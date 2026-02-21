/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import Service.EnrollmentService;
import Service.GradeService;
import Service.NotificationService;
import Service.StudentService;
import dao.UserDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import model.Course;
import model.Enrollment;
import model.Student;
import model.User;

/**
 *
 * @author jesse
 */
public class StudentDashboardFrame extends javax.swing.JFrame {
    private Student currentStudent;
    private User currentUser;
    private int studentUserId;
    private int studentId;
    private int adminId;
    
    private NotificationService notificationService= NotificationService.getInstance();
    private GradeService gradeService= new GradeService();
    private CourseService courseService= new CourseService();
    private EnrollmentService enrollmentService= new EnrollmentService();
    private StudentService studentService= new StudentService();
    

    /**
     * Creates new form StudentDashboardFrame
     */
    public StudentDashboardFrame(Student student) {
        this.currentStudent= student;
        this.studentUserId= student.getUserId();
        this.studentId= student.getStudentId();
        this.adminId= student.getAdminId();
        initComponents();
        setupDashboard();
        setLoadingState(true);
        loadDataAsync();
    }
    private void setupDashboard(){
        loglabel.setText("Logged in as: "+ currentStudent.getFirstName()+ " "+ currentStudent.getLastName());
        Idlabel.setText("Student ID: "+ currentStudent.getStudentId());
        deptlabel.setText("Program: "+ currentStudent.getDepartment());
        adminlabel.setText("Admin ID: "+ currentStudent.getAdminId());
        //notification console
        notificationArea.setEditable(false);
        notificationArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        notificationArea.setBackground(new Color(245, 245, 245));
        notificationArea.setForeground(Color.DARK_GRAY);
        notificationArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1), "Live Notifications",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
        notificationArea.setText("");
    }
    private void setLoadingState(boolean loading){
        performancebtn.setEnabled(!loading);
        profilebtn.setEnabled(!loading);
        coursesbtn.setEnabled(!loading);
        enrollbtn.setEnabled(!loading);
        settings.setEnabled(!loading);
        
        if(loading){
            Gpalabel.setText("-> Current GPA: Loading....");
            courselabel.setText("-> Courses Enrolled: Loading...");
            unitlabel.setText("-> Total Units: Loading...");
            appendNotification("Loading dashboard data...");
        }
    }
    
    private void loadDataAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Data, Void> worker= new SwingWorker<>(){
            @Override
            protected Data doInBackground() throws Exception{
                //fetch user
                UserDAO userDAO= new UserDAO();
                User user= userDAO.getById(currentStudent.getUserId());
                if(user== null){
                    throw new Exception("User session not found");
                }
                double gpa= gradeService.calculateGPA(studentId, adminId);
                List<Enrollment> enrollments= enrollmentService.getEnrollmentsByStudent(studentId, adminId);
                List<Course> allCourses= courseService.getCourseByAdminId(adminId);
                Map<Integer, Course> courseMap= new HashMap<>();
                for(Course c : allCourses){
                    courseMap.put(c.getCourseId(), c);
                }
                int courseCount= (enrollments != null) ? enrollments.size() : 0;
                int totalUnits= 0;
                
                if(enrollments != null){
                    for(Enrollment e : enrollments){
                        Course course= courseMap.get(e.getCourseId());
                        if(course != null){
                            totalUnits += course.getUnits();
                        }
                    }
                }
                //for fast in-memory notification load
                List<String> notifications= notificationService.getRecentNotifications(studentUserId);
                return new Data(user, gpa, courseCount, totalUnits, notifications);
            }
            @Override
            protected void done(){
                try{
                    Data data= get();
                    currentUser= data.user;
                    Gpalabel.setText("-> Current GPA: "+ String.format("%.2f", data.gpa));
                    courselabel.setText("Courses Enrolled: "+ data.courseCount);
                    unitlabel.setText("Total Units: "+ data.totalUnits);
                    appendNotification("Welcome to your Student Dashboard");
                    data.notifications.forEach(StudentDashboardFrame.this::appendNotification);
                    setLoadingState(false);
                }catch(Exception e){
                    e.printStackTrace();
                    Gpalabel.setText("-> Current GPA: Error");
                    courselabel.setText("Course Enrolled: Error");
                    unitlabel.setText("Total Units: Error");
                    appendNotification("Error loading dashboard: "+ e.getMessage());
                    JOptionPane.showMessageDialog(StudentDashboardFrame.this, "Error laoding dashboard data:\n"+ e.getMessage()+ "\n\nPls try logging in again.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    StudentDashboardFrame.this.dispose();
                    new loginFrame().setVisible(true);
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
   private static class Data{
       final User user;
       final double gpa;
       final int courseCount;
       final int totalUnits;
       final List<String> notifications;
       
       Data(User user, double gpa, int courseCount, int totalUnits, List<String> notifications){
           this.user= user;
           this.gpa= gpa;
           this.courseCount= courseCount;
           this.totalUnits= totalUnits;
           this.notifications= notifications;
       }
   }
    
    private void appendNotification(String message){
        notificationArea.append(message + "\n");
        notificationArea.setCaretPosition(notificationArea.getDocument().getLength());
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        loglabel = new javax.swing.JLabel();
        Idlabel = new javax.swing.JLabel();
        deptlabel = new javax.swing.JLabel();
        adminlabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        performancebtn = new javax.swing.JButton();
        profilebtn = new javax.swing.JButton();
        coursesbtn = new javax.swing.JButton();
        enrollbtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificationArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        Gpalabel = new javax.swing.JLabel();
        courselabel = new javax.swing.JLabel();
        unitlabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        settings = new javax.swing.JButton();
        logout = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("        STUDENT DASHBOARD");

        loglabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        Idlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        deptlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        adminlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loglabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deptlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(adminlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(94, 94, 94))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loglabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adminlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deptlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        performancebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        performancebtn.setText("PERFORMANCE");
        performancebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performancebtnActionPerformed(evt);
            }
        });

        profilebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        profilebtn.setText("PROFILE");
        profilebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilebtnActionPerformed(evt);
            }
        });

        coursesbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        coursesbtn.setText("MY COURSES");
        coursesbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coursesbtnActionPerformed(evt);
            }
        });

        enrollbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        enrollbtn.setText("ENROLL COURSES");
        enrollbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enrollbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(profilebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(performancebtn, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(enrollbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .addComponent(coursesbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(63, 63, 63))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(profilebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coursesbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(performancebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enrollbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        notificationArea.setColumns(20);
        notificationArea.setRows(5);
        jScrollPane1.setViewportView(notificationArea);

        jLabel9.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jLabel9.setText("Quick Stats:");

        Gpalabel.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        Gpalabel.setText("*");

        courselabel.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        courselabel.setText("*");

        unitlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        unitlabel.setText("*");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Gpalabel, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(courselabel, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(231, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Gpalabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(courselabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(unitlabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setText("MAIN ACTIONS");

        jLabel8.setText("NOTIFICATIONS");

        settings.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        settings.setText("SETTINGS");
        settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });

        logout.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        logout.setText("LOGOUT");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(143, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(181, 181, 181))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(settings, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(12, 12, 12)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(settings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void performancebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performancebtnActionPerformed
        // TODO add your handling code here:
        if(currentUser != null){
            PerformanceFrame pF= new PerformanceFrame(currentUser);
            pF.setVisible(true);
            this.dispose();
        }else{
            JOptionPane.showMessageDialog(this, "Unable to load performance: User session not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_performancebtnActionPerformed

    private void profilebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilebtnActionPerformed
        // TODO add your handling code here:
            if(currentUser != null){
                ProfileFrame pf= new ProfileFrame(currentUser);
                pf.setVisible(true);
                this.dispose();
            }else{
                JOptionPane.showMessageDialog(this, "Unable to load profile", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }//GEN-LAST:event_profilebtnActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        // TODO add your handling code here:
        int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to Logout", "Confirm logout", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            dispose();
            new loginFrame().setVisible(true);
        }
    }//GEN-LAST:event_logoutActionPerformed

    private void settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsActionPerformed
        // TODO add your handling code here:
            if(currentUser != null){
                SettingsFrame sf= new SettingsFrame(currentUser);
                sf.setVisible(true);
                this.dispose();
            }else{
                JOptionPane.showMessageDialog(this, "Unable to load Settiings", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }//GEN-LAST:event_settingsActionPerformed

    private void enrollbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enrollbtnActionPerformed
        // TODO add your handling code here:
        new EnrollCoursesFrame(currentStudent).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_enrollbtnActionPerformed

    private void coursesbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coursesbtnActionPerformed
        // TODO add your handling code here:
        new MyCourseFrame(currentStudent).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_coursesbtnActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
         Set the Nimbus look and feel */
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
            java.util.logging.Logger.getLogger(StudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StudentDashboardFrame().setVisible(true);
            }
        });
    }
*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Gpalabel;
    private javax.swing.JLabel Idlabel;
    private javax.swing.JLabel adminlabel;
    private javax.swing.JLabel courselabel;
    private javax.swing.JButton coursesbtn;
    private javax.swing.JLabel deptlabel;
    private javax.swing.JButton enrollbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel loglabel;
    private javax.swing.JButton logout;
    private javax.swing.JTextArea notificationArea;
    private javax.swing.JButton performancebtn;
    private javax.swing.JButton profilebtn;
    private javax.swing.JButton settings;
    private javax.swing.JLabel unitlabel;
    // End of variables declaration//GEN-END:variables
}
