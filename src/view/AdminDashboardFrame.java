/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.AdminService;
import Service.CourseService;
import Service.GradeService;
import Service.NotificationService;
import Service.StudentService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import model.Admin;
import model.User;

/**
 *
 * @author jesse
 */
public class AdminDashboardFrame extends javax.swing.JFrame {
    
    private final AdminService adminService= new AdminService();
    private final StudentService studentService= new StudentService();
    private final CourseService courseService= new CourseService();
    private final GradeService gradeService= new GradeService();
    private final NotificationService notificationService= NotificationService.getInstance();
    private final User currentUser;

    /**
     * Creates new form AdminDashboardFrame
     */
    public AdminDashboardFrame(User user) {
        initComponents();
        this.currentUser= user;
        label1.setText("Loading dashboard...");
        label2.setText("Pls wait...");
        loadDashboardData();
       
    }
    private void loadDashboardData(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<DashboardData, Void> worker= new SwingWorker<>(){
            @Override
            protected DashboardData doInBackground() throws Exception{
                 Admin admin= adminService.getAdminByUserId(currentUser.getId());
        if(admin== null){
            throw new Exception("Admin not found");
        }
        int adminId= admin.getAdminId();
        String adminPin= admin.getPin();
        int userId= currentUser.getId();
        int totalStudents= studentService.countStudentByadmin(adminId);
        int totalCourses= courseService.countCoursezByAdmin(adminId);
        int activeStudents= studentService.countActiveStudents(adminId);
        double avgGPA= gradeService.calculateAverageGpa(adminId);
        List<String> notifications= notificationService.getInstance().getRecentNotifications(userId);
        return new DashboardData(admin, totalStudents, totalCourses, activeStudents, avgGPA, notifications);

            }
            @Override
            protected void done(){
                try{
                    DashboardData data= get();
                    setupDashboard(data);
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Error loading dashboard: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    AdminDashboardFrame.this.dispose();
                    new loginFrame().setVisible(true);
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
    private void setupDashboard(DashboardData data){
        label1.setText("Welcome, "+ currentUser.getFirstName() + " "+ currentUser.getLastName()+ "!");
        label2.setText("Role: "+ currentUser.getRole() + " | Admin PIN: "+ data.adminPin);
        label3.setText("Total Students: "+ data.totalStudents);
        label5.setText("Total Courses: "+ data.totalCourses);
        label4.setText("Active Students: "+ data.activeStudents);
        label6.setText("Average GPA: "+ String.format("%.2f", data.avgGPA));
        
        //setup notifications
        setupNotificationConsole();
        data.notifications.forEach(this::appendNotification);
    }
    private static class DashboardData{
        final Admin admin;
        final String adminPin;
        final int totalStudents;
        final int totalCourses;
        final int activeStudents;
        final double avgGPA;
        final List<String> notifications;
        
        DashboardData(Admin admin, int totalStudents, int totalCourses, int activeStudents, double avgGPA, List<String> notifications){
            this.admin= admin;
            this.adminPin= admin.getPin();
            this.totalStudents= totalStudents;
            this.totalCourses= totalCourses;
            this.activeStudents= activeStudents;
            this.avgGPA= avgGPA;
            this.notifications= notifications;
        }
    }
    private void setupNotificationConsole(){
        TextArea.setEditable(false);
        TextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        TextArea.setBackground(new Color(250, 250, 250));
        TextArea.setForeground(Color.darkGray);
        TextArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1),
                "Notification Console",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));
        TextArea.setLineWrap(true);
        TextArea.setWrapStyleWord(true);
        TextArea.setText("");
    }
    private void appendNotification(String formattedmessage){
        TextArea.append(formattedmessage + "\n");
        TextArea.setCaretPosition(TextArea.getDocument().getLength());
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
        label2 = new javax.swing.JLabel();
        label1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label3 = new javax.swing.JLabel();
        label4 = new javax.swing.JLabel();
        label5 = new javax.swing.JLabel();
        label6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        studentButton = new javax.swing.JButton();
        GradingButton = new javax.swing.JButton();
        courseButton = new javax.swing.JButton();
        performanceButton = new javax.swing.JButton();
        GradeButton = new javax.swing.JButton();
        TranscriptButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextArea = new javax.swing.JTextArea();
        reGeneratebtn = new javax.swing.JButton();
        LogoutButton = new javax.swing.JButton();
        SettingsButton = new javax.swing.JButton();
        Aboutbtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("        ADMIN DASHBOARD");

        label2.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        label1.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 153, 153));

        label3.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        label4.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        label5.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        label6.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel2.setText("Dashboard Stats:");

        jPanel3.setBackground(new java.awt.Color(255, 153, 153));

        studentButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        studentButton.setText("Manage Students");
        studentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentButtonActionPerformed(evt);
            }
        });

        GradingButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        GradingButton.setText("Grading Scales");
        GradingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GradingButtonActionPerformed(evt);
            }
        });

        courseButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        courseButton.setText("Manage Courses");
        courseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                courseButtonActionPerformed(evt);
            }
        });

        performanceButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        performanceButton.setText("Performance");
        performanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performanceButtonActionPerformed(evt);
            }
        });

        GradeButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        GradeButton.setText("Manage Grades");
        GradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GradeButtonActionPerformed(evt);
            }
        });

        TranscriptButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        TranscriptButton.setText("Transcript Archive");
        TranscriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TranscriptButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(GradingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(studentButton, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(courseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .addComponent(performanceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(117, 117, 117)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TranscriptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(courseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TranscriptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(performanceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GradingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel4.setText("Quick Actions:");

        jLabel8.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel8.setText("Notification Console:");

        TextArea.setColumns(20);
        TextArea.setFont(new java.awt.Font("Footlight MT Light", 0, 14)); // NOI18N
        TextArea.setRows(5);
        jScrollPane1.setViewportView(TextArea);

        reGeneratebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        reGeneratebtn.setText("Regenerate PIN");
        reGeneratebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reGeneratebtnActionPerformed(evt);
            }
        });

        LogoutButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        LogoutButton.setText("Logout");
        LogoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutButtonActionPerformed(evt);
            }
        });

        SettingsButton.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        SettingsButton.setText("Settings");
        SettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsButtonActionPerformed(evt);
            }
        });

        Aboutbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        Aboutbtn.setText("About");
        Aboutbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(SettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(reGeneratebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(186, 186, 186)
                .addComponent(LogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(160, 160, 160))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(12, 12, 12)
                                            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
                                            .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(9, 9, 9)
                                    .addComponent(Aboutbtn)
                                    .addGap(21, 21, 21))))
                        .addContainerGap(14, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Aboutbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reGeneratebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
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

    private void reGeneratebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reGeneratebtnActionPerformed
        // TODO add your handling code here:
        reGeneratebtn.setEnabled(false);
        SwingWorker<String, Void> worker= new SwingWorker<>(){
                @Override
                protected String doInBackground() throws Exception{
                    Admin admin= adminService.getAdminByUserId(currentUser.getId());
                    if(admin == null){
                        throw new Exception("Admin not found");
                }
                    return adminService.regenerateAdminPin(admin.getAdminId(), currentUser.getId());
                }
                @Override
                protected void done(){
                    try{
                        String newPin= get();
                        if(newPin != null){
                            JOptionPane.showMessageDialog(AdminDashboardFrame.this, "New AdminPin generated: "+ newPin, "PIN regenerated", JOptionPane.INFORMATION_MESSAGE);
                            label2.setText("Role: "+ currentUser.getRole() + " | Admin PIN: "+ newPin);
                    }else{
                            JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Failed to regenerate PIN. Try again", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                }catch(Exception e){
                    JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Error: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }finally{
                        reGeneratebtn.setEnabled(true);
                    }
                }    
            };
        worker.execute();
    }//GEN-LAST:event_reGeneratebtnActionPerformed

    private void TranscriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TranscriptButtonActionPerformed
        // TODO add your handling code here:
        new TranscriptArchiveFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_TranscriptButtonActionPerformed

    private void studentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentButtonActionPerformed
        // TODO add your handling code here:
        new ManageStudentFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_studentButtonActionPerformed

    private void courseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_courseButtonActionPerformed
        // TODO add your handling code here:
        new ManageCourseFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_courseButtonActionPerformed

    private void GradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GradeButtonActionPerformed
        // TODO add your handling code here:
        new ManageGradeFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_GradeButtonActionPerformed

    private void GradingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GradingButtonActionPerformed
        // TODO add your handling code here:
        new GradingScaleFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_GradingButtonActionPerformed

    private void performanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performanceButtonActionPerformed
        // TODO add your handling code here:
        new PerformanceFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_performanceButtonActionPerformed

    private void LogoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutButtonActionPerformed
        // TODO add your handling code here:
        notificationService.clearNotification(currentUser.getId());
        new loginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_LogoutButtonActionPerformed

    private void SettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsButtonActionPerformed
        // TODO add your handling code here:
        new SettingsFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_SettingsButtonActionPerformed

    private void AboutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutbtnActionPerformed
        // TODO add your handling code here:
        String aboutMsg= """
                         Admin Portal
                         
                         You have full access to manage Courses, Students and Grades.
                         
                         Developer Info:
                         Developed by Kunzoick
                         Version beta 1.0""";
        String message= aboutMsg;
        AboutDialog dialog= new AboutDialog(this, "About", message);
        dialog.setVisible(true);
    }//GEN-LAST:event_AboutbtnActionPerformed

    /**, 
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
            java.util.logging.Logger.getLogger(AdminDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboardFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Aboutbtn;
    private javax.swing.JButton GradeButton;
    private javax.swing.JButton GradingButton;
    private javax.swing.JButton LogoutButton;
    private javax.swing.JButton SettingsButton;
    private javax.swing.JTextArea TextArea;
    private javax.swing.JButton TranscriptButton;
    private javax.swing.JButton courseButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel label4;
    private javax.swing.JLabel label5;
    private javax.swing.JLabel label6;
    private javax.swing.JButton performanceButton;
    private javax.swing.JButton reGeneratebtn;
    private javax.swing.JButton studentButton;
    // End of variables declaration//GEN-END:variables
}
