/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package view;

import Service.CourseService;
import Service.GradeService;
import Service.StudentService;
import dao.AdminDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import model.Admin;
import model.Student;
import model.User;

/**
 *
 * @author jesse
 */
public class GradStudentDashboardFrame extends javax.swing.JFrame {
    private User currentUser;
    private Student currentStudent;
    private boolean graduateMode= false;
    private GradeService gradeService= new GradeService();
    private StudentService studentService= new StudentService();
    private CourseService courseService= new CourseService();
    private AdminDAO adminDAO= new AdminDAO();


    /** Creates new form GradStudentDashboardFrame */
    public GradStudentDashboardFrame(User user) {
        initComponents();
        this.currentUser= user;
        Welcomelabel.setText("Welcome, Graduate!");
        labelStatus.setText("Loading...");
        labelStatus.setForeground(Color.orange);
        setButtonsEnabled(false);
        loadDataAsync();
    }
    public void setGraduatedMode(boolean graduatedMode){
        this.graduateMode= graduatedMode;
        adjustUIForGraduationStatus();
    }
    private void adjustUIForGraduationStatus(){
        if(graduateMode){
            Welcomelabel.setText("Welcome, Graduate!");
        }else{
            Welcomelabel.setText("Welcome, Student!");
        }
    }
    
    public void loadDataAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<DashBoardData, Void> worker= new SwingWorker<>(){
            @Override
            protected DashBoardData doInBackground() throws Exception{
                Student student= studentService.getStudentByUserId(currentUser.getId());
                if(student== null){
                    throw new Exception("Student record not found");
                }
                if(!"graduated".equalsIgnoreCase(student.getStatus())){
                    throw new IllegalStateException("Access denied: student not graduated, Contact your Admin.");
                }
                Admin admin= adminDAO.getAdminByadminId(student.getAdminId());
                return new DashBoardData(student, admin);
            }
            @Override
            protected void done(){
                try{
                    DashBoardData data= get();
                    currentStudent= data.student;
  
                    namelabel.setText("Name: "+ data.student.getFirstName()+ " "+ data.student.getLastName());
                    Idlabel.setText("Student ID: "+ data.student.getStudentId());
                    Adminlabel.setText("Admin: "+ (data.admin != null ? data.admin.getAdminId() : "N/A"));
                    if(data.student.getUpdatedAt() != null){
                        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDate= data.student.getUpdatedAt().format(formatter);
                        Yearlabel.setText("Graduated On: "+ formattedDate);
                    }else{
                    Yearlabel.setText("Graduated On: N/A");
                    }
                    labelStatus.setText("Dashboard loaded successfully");
                    setButtonsEnabled(true);
                    setGraduatedMode(true);
                }catch (IllegalStateException e) {
                    try{
                        Student student= studentService.getStudentByUserId(currentUser.getId());
                        if(student != null){
                        JOptionPane.showMessageDialog(GradStudentDashboardFrame.this, """
                                                                                  Access denied. Student is not graduated.
                                                                                  You will be redirected to the regular Student Dashboard""",
                            "Access Denied",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    new StudentDashboardFrame(currentStudent).setVisible(true);  
                        }else{
                            JOptionPane.showMessageDialog(GradStudentDashboardFrame.this, "Student record not found.\nYou will be returned to the login Screen", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            new loginFrame().setVisible(true);
                        }
                    }catch(Exception ex){
                        new loginFrame().setVisible(true);
                    }
                    dispose();
                    
                }catch(Exception e){
                    e.printStackTrace();
                    labelStatus.setText("Error loading student info: "+ e.getMessage());
                    labelStatus.setForeground(Color.red);
                    new loginFrame().setVisible(true);
                    dispose();
                }finally{
                    setButtonsEnabled(true);
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
    private boolean isGraduateMode(){
        return graduateMode;
    }
    private void setButtonsEnabled(boolean enabled){
        performancebtn.setEnabled(enabled);
        profilebtn.setEnabled(enabled);
        settingsbtn.setEnabled(enabled);
        Logoutbtn.setEnabled(true);
    }
    private static class DashBoardData{
        final Student student;
        final Admin admin;
        
        DashBoardData(Student student, Admin admin){
            this.student= student;
            this.admin= admin;
        }
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Welcomelabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        namelabel = new javax.swing.JLabel();
        Idlabel = new javax.swing.JLabel();
        Yearlabel = new javax.swing.JLabel();
        Adminlabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        performancebtn = new javax.swing.JButton();
        profilebtn = new javax.swing.JButton();
        helpbtn = new javax.swing.JButton();
        settingsbtn = new javax.swing.JButton();
        Logoutbtn = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("Graduated Student Portal");

        Welcomelabel.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 153, 153));

        jLabel3.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel3.setText("Student Info Summary Panel");

        namelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        namelabel.setText("Name: John Doe");

        Idlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        Idlabel.setText("Student ID: STU-000123");

        Yearlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        Yearlabel.setText("Graduation Year: 2025");

        Adminlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        Adminlabel.setText("Admin: Admin name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 93, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(86, 86, 86))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Adminlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Yearlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Yearlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Adminlabel, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 153, 153));

        performancebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        performancebtn.setText("View Performance");
        performancebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performancebtnActionPerformed(evt);
            }
        });

        profilebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        profilebtn.setText("Profile");
        profilebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilebtnActionPerformed(evt);
            }
        });

        helpbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        helpbtn.setText("Help/About");
        helpbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpbtnActionPerformed(evt);
            }
        });

        settingsbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        settingsbtn.setText("Settings");
        settingsbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(helpbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(settingsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(performancebtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(profilebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(performancebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(profilebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(helpbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(settingsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Logoutbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Logoutbtn.setText("Logout");
        Logoutbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutbtnActionPerformed(evt);
            }
        });

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Welcomelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(Logoutbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(jLabel1)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Welcomelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 12, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Logoutbtn)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void performancebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performancebtnActionPerformed
        // TODO add your handling code here:
        PerformanceFrame PF= new PerformanceFrame(currentUser);
        PF.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_performancebtnActionPerformed

    private void profilebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilebtnActionPerformed
        // TODO add your handling code here:
        ProfileFrame pf= new ProfileFrame(currentUser);
        pf.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_profilebtnActionPerformed

    private void settingsbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsbtnActionPerformed
        // TODO add your handling code here:
        new SettingsFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_settingsbtnActionPerformed

    private void LogoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutbtnActionPerformed
        // TODO add your handling code here:
        int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to Logout?", "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(confirm== JOptionPane.YES_OPTION){
            this.dispose();
            new loginFrame().setVisible(true);
        }
    }//GEN-LAST:event_LogoutbtnActionPerformed

    private void helpbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpbtnActionPerformed
        // TODO add your handling code here:
        String gradMsg= """
                        Graduated Student Portal
                        
                        Thank you for Using Student Management System!
                        - You can still view your performance records.
                        - You cannot enroll in new courses.
                        - To obtain an official transcript, Pls contact your admin.
                        
                        Developer Info:
                        Developed by Kunzoick
                        Version: 1.0 Beta""";
        String message= gradMsg;
        AboutDialog dialog= new AboutDialog(this, "Help / About", message);
        dialog.setVisible(true);
    }//GEN-LAST:event_helpbtnActionPerformed

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
            java.util.logging.Logger.getLogger(GradStudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GradStudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GradStudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GradStudentDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GradStudentDashboardFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Adminlabel;
    private javax.swing.JLabel Idlabel;
    private javax.swing.JButton Logoutbtn;
    private javax.swing.JLabel Welcomelabel;
    private javax.swing.JLabel Yearlabel;
    private javax.swing.JButton helpbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel namelabel;
    private javax.swing.JButton performancebtn;
    private javax.swing.JButton profilebtn;
    private javax.swing.JButton settingsbtn;
    // End of variables declaration//GEN-END:variables

}
