/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import Service.GradeService;
import Service.TranscriptArchiveService;
import dao.AdminDAO;
import dao.EnrollmentDAO;
import dao.GradeDAO;
import dao.StudentDAO;
import dao.TranscriptArchivedDAO;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.table.DefaultTableModel;
import model.Student;
import model.User;
import util.DBConnection;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import model.Course;
import model.Grade;

/**
 *
 * @author jesse
 */
public class ArchiveModeFrame extends javax.swing.JFrame {
    private  User currentUser;
    private Student student;
    private int adminId;
    private TranscriptArchiveService archiveService;
    private GradeService gradeService;
    private StudentDAO studentDAO;
    private CourseService courseService= new CourseService();
    private DefaultTableModel transcriptTableModel;
    
    /**
     * Creates new form ArchiveMode
     */
    public ArchiveModeFrame(User currentUser, int adminId, Student student) {
        this.currentUser= currentUser;
        this.adminId= adminId;
        this.student= student;
        initComponents();
        setupServices();
        configureTable();
        labelStatus.setText("Loading student transcript...");
        labelStatus.setForeground(Color.blue);
        loadDataAsync();
        
    }
    private void setupServices(){
        try{
        studentDAO= new StudentDAO();
        EnrollmentDAO enrollmentDAO= new EnrollmentDAO();
        TranscriptArchivedDAO transcriptDAO= new TranscriptArchivedDAO();
        AdminDAO adminDAO= new AdminDAO();
        gradeService= new GradeService();
        archiveService= new TranscriptArchiveService(transcriptDAO, gradeService,studentDAO, enrollmentDAO, adminDAO);
    }catch(Exception e){
        JOptionPane.showMessageDialog(this, "Services couldn't setup" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void configureTable(){ 
        transcriptTableModel= new DefaultTableModel(new Object[]{"Code", "Title", "Grade", "Grade Point", "Units"}, 0){
                @Override
                public boolean isCellEditable(int row, int column){
                return false;
                }
                };
        jTable.setModel(transcriptTableModel);
    }
    private void loadDataAsync(){
        setButtonsEnabled(false);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<TranscriptData, Void> worker= new SwingWorker<>(){
        @Override
        protected TranscriptData doInBackground() throws Exception{
        double gpa= gradeService.calculateGPA(student.getStudentId(), adminId);
        List<Grade> grades= gradeService.getGradeByStudent(student.getStudentId(), adminId);
        //get courses for each grade
        List<TranscriptRow> rows= new ArrayList<>();
        double totalPoints= 0;
        double totalUnits= 0;
        for(Grade g : grades){
            Course course= courseService.getCourseById(g.getCourseId(), adminId);
            if(course != null){
                rows.add(new TranscriptRow(course, g));
                totalPoints += g.getGradePoint() * course.getUnits();
                totalUnits += course.getUnits();
            }
        }
        return new TranscriptData(gpa, rows, totalPoints, totalUnits);
    }
        @Override
        protected void done(){
        try{
            TranscriptData data= get();
            displayStudentInfo(data.gpa);
            displayTranscript(data);
        }catch(Exception e){
            e.printStackTrace();
            labelStatus.setText("Error loading data: "+ e.getMessage());
            labelStatus.setForeground(Color.red);
            JOptionPane.showConfirmDialog(ArchiveModeFrame.this, "Error loading transcript: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }finally{
            setCursor(oldCursor);
            setButtonsEnabled(true);
   }

    }
    };
    worker.execute();
}
    private void displayStudentInfo(double gpa){
        if(student== null){
            labelStatus.setText("No student data found.");
            return;
        }
        Namelabel.setText(student.getFirstName() + " " + student.getLastName());
        Genderlabel.setText(student.getGender());
        Genderlabel.setForeground(Color.BLUE);
        Deptlabel.setText(student.getDepartment());
        Deptlabel.setForeground(Color.BLUE);
        Datelabel.setText(student.getEnrollmentDate() != null ? 
             student.getEnrollmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A");
        Status.setText(student.getStatus());
        GPA.setText(String.format("%.2f", gpa));
        GPA.setForeground(Color.ORANGE);
    }
    private void displayTranscript(TranscriptData data){
        transcriptTableModel.setRowCount(0);
        for(TranscriptRow row : data.rows){
            transcriptTableModel.addRow(new Object[]{
                row.course.getCourseCode(), row.grade.getScore(), row.grade.getGrade(),
                row.grade.getGradePoint(), row.course.getUnits()
            });
        }
        Unitlabel.setText(String.format("%.2f", data.totalUnits));
        Pointlabel.setText(String.format("%.2f", data.totalPoints));
        double gpa= (data.totalUnits > 0) ? data.totalPoints / data.totalUnits : 0;
        GPA2.setText(String.format("%.2f", gpa));
        GPA2.setForeground(Color.green);
        labelStatus.setText("Transcript loaded successfullt");
        labelStatus.setForeground(Color.green);
    }
    private static class TranscriptData{
        final double gpa;
        final List<TranscriptRow> rows;
        final double totalPoints;
        final double totalUnits;
        
        TranscriptData(double gpa, List<TranscriptRow> rows, double totalPoints, double totalUnits){
            this.gpa= gpa;
            this.rows= rows;
            this.totalPoints= totalPoints;
            this.totalUnits= totalUnits;
        }
    }
    private static class TranscriptRow{
        final Course course;
        final Grade grade;
        
        TranscriptRow(Course course, Grade grade){
            this.course= course;
            this.grade= grade;
        }
    }
    private void setButtonsEnabled(boolean enabled){
        Archivebtn.setEnabled(enabled);
        refreshbtn.setEnabled(enabled);
        
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
        jPanel2 = new javax.swing.JPanel();
        n = new javax.swing.JLabel();
        g = new javax.swing.JLabel();
        l = new javax.swing.JLabel();
        d = new javax.swing.JLabel();
        GPA = new javax.swing.JLabel();
        m = new javax.swing.JLabel();
        Namelabel = new javax.swing.JLabel();
        Deptlabel = new javax.swing.JLabel();
        c = new javax.swing.JLabel();
        Genderlabel = new javax.swing.JLabel();
        Datelabel = new javax.swing.JLabel();
        Status = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TextArea = new javax.swing.JTextArea();
        Typelabel = new javax.swing.JLabel();
        Archivebtn = new javax.swing.JButton();
        refreshbtn = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Pointlabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Unitlabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        GPA2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("ARCHIVE STUDENT TRANSCRIPT");

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel2.setText("Student Information");

        n.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        n.setText("Name: ");

        g.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        g.setText("Gender: ");

        l.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        l.setText("Department: ");

        d.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        d.setText("EnrollmentDate:");

        GPA.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        m.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        m.setText("Status: ");

        Namelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        Deptlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        c.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        c.setText("Current GPA: ");

        Genderlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        Datelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        Status.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(c)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GPA, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(n)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Namelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(l)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Deptlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(49, 49, 49)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(g, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Genderlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(d, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(m)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Status, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(n)
                    .addComponent(g)
                    .addComponent(Namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Genderlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(d, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(l, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Deptlabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GPA, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(c)
                        .addComponent(m, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                        .addComponent(Status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTable.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
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
                false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable);

        jLabel9.setFont(new java.awt.Font("Footlight MT Light", 1, 13)); // NOI18N
        jLabel9.setText("Transcript Summary");

        jLabel13.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        jLabel13.setText("Archival Action");

        jLabel15.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jLabel15.setText("Reason for Archiving:");

        TextArea.setColumns(20);
        TextArea.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        TextArea.setRows(5);
        jScrollPane3.setViewportView(TextArea);

        Typelabel.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Typelabel.setText("Archived Type: ADMIN (non- editable)");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Typelabel)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel15)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(Typelabel)
                .addGap(15, 15, 15))
        );

        Archivebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Archivebtn.setText("Archive Student");
        Archivebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArchivebtnActionPerformed(evt);
            }
        });

        refreshbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        refreshbtn.setText("Refresh");
        refreshbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshbtnActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jButton3.setText("Back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel4.setText("Total Points: ");

        Pointlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel6.setText("Total Units: ");

        Unitlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel8.setText("GPA: ");

        GPA2.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(162, 162, 162)
                                .addComponent(jLabel1))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Pointlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Unitlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(GPA2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(Archivebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(refreshbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(Pointlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(jLabel6))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(Unitlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(GPA2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(Archivebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
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

    private void ArchivebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ArchivebtnActionPerformed
        // TODO add your handling code here:
        String reason= TextArea.getText().trim();
        if(reason.isEmpty()){
            JOptionPane.showMessageDialog(this, "Pls provide a reason for archiving.", "Warning", JOptionPane.WARNING_MESSAGE);
            labelStatus.setText("Pls provide a reason for archiving");
            return;
        }
        int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to archive this student?, procedure cannot be reversed.", 
                "Confirm Archive", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm != JOptionPane.YES_OPTION) return;
        
        Archivebtn.setEnabled(false);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        labelStatus.setText("Archiving student... Pls wait");
        labelStatus.setForeground(Color.blue);
        
        //--progress dialog
        JDialog progressDialog= new JDialog(this, "Archiving... ", true);
        JProgressBar progressBar= new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Archiving student... Pls wait");
        progressBar.setStringPainted(true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
            @Override
            protected Boolean doInBackground() throws Exception{
                return archiveService.archiveStudentByAdmin(student.getStudentId(), adminId);
            }
            @Override
            protected void done(){
                progressDialog.dispose();
                try{
                    boolean success= get();
                    if(success){
                        labelStatus.setForeground(new Color(0, 128, 0));
                        labelStatus.setText("Student successfully archived!");
                        JOptionPane.showMessageDialog(ArchiveModeFrame.this, "Student archived successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        ManageStudentFrame msf= new ManageStudentFrame(currentUser);
                        msf.setVisible(true);
                        ArchiveModeFrame.this.dispose();
            }else{
                        labelStatus.setForeground(Color.red);
                        labelStatus.setText("Archiving failed.");
                        Archivebtn.setEnabled(true);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    labelStatus.setForeground(Color.red);
                    labelStatus.setText("Error: "+e.getMessage());
                    Archivebtn.setEnabled(true);
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
        progressDialog.setVisible(true);// blocks until done 
    }//GEN-LAST:event_ArchivebtnActionPerformed

    private void refreshbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshbtnActionPerformed
        // TODO add your handling code here:
        loadDataAsync();
        labelStatus.setText("Data refreshed");
        labelStatus.setForeground(Color.green);
    }//GEN-LAST:event_refreshbtnActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        ManageStudentFrame msf= new ManageStudentFrame(currentUser);
        msf.setVisible(true);
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
            java.util.logging.Logger.getLogger(ArchiveModeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArchiveModeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArchiveModeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArchiveModeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArchiveModeFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Archivebtn;
    private javax.swing.JLabel Datelabel;
    private javax.swing.JLabel Deptlabel;
    private javax.swing.JLabel GPA;
    private javax.swing.JLabel GPA2;
    private javax.swing.JLabel Genderlabel;
    private javax.swing.JLabel Namelabel;
    private javax.swing.JLabel Pointlabel;
    private javax.swing.JLabel Status;
    private javax.swing.JTextArea TextArea;
    private javax.swing.JLabel Typelabel;
    private javax.swing.JLabel Unitlabel;
    private javax.swing.JLabel c;
    private javax.swing.JLabel d;
    private javax.swing.JLabel g;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel l;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel m;
    private javax.swing.JLabel n;
    private javax.swing.JButton refreshbtn;
    // End of variables declaration//GEN-END:variables
}
