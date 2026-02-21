/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import Service.GradeService;
import Service.ReportService;
import Service.StudentService;
import dao.AdminDAO;
import dao.StudentDAO;
import dto.StudentTranscriptDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.Student;
import model.User;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import model.Course;
import model.Grade;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import util.DBConnection;
import util.ReportPDFGenerator;
/**
 *
 * @author jesse
 */
public class PerformanceFrame extends javax.swing.JFrame {
    private User currentUser;
    private Student currentStudent;
    int adminId;
    private DefaultTableModel tableModel;
    private GradeService gradeService;
    private AdminDAO adminDAO;
    private StudentService studentService;
    private CourseService courseService;

    /**
     * Creates new form PerformanceFrame
     */
    public PerformanceFrame(User currentUser) {
        initComponents();
        this.currentUser= currentUser;
        if(!validateUserRole()){
            dispose();
            return;
        }
        if("student".equalsIgnoreCase(currentUser.getRole())){
            fetchCurrentStudent();
        }
   
        this.gradeService= new GradeService();
        this.studentService= new StudentService();
        this.courseService= new CourseService();
        this.adminDAO= new AdminDAO();
        setupTable();
        setLocationRelativeTo(null);
        labelStatus.setText("Loading...");
        labelStatus.setForeground(Color.orange);
        
        Box.setEnabled(false);
        ExportBtn.setEnabled(false);
        ViewBtn.setEnabled(false);
        //for load base on role
        if("admin".equalsIgnoreCase(currentUser.getRole())){
            loadAdminViewAsync();
        }else if("student".equalsIgnoreCase(currentUser.getRole())){
            loadStudentViewAsync();
        }
    }
    private boolean validateUserRole(){
        String role= currentUser.getRole();
        if(!"admin".equalsIgnoreCase(role) && !"student".equalsIgnoreCase(role)){
            JOptionPane.showMessageDialog(this, "Access denied: Only Admin process access!", "Security Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private void fetchCurrentStudent(){
        try{
            StudentDAO studentDAO= new StudentDAO();
            this.currentStudent= studentDAO.getStudentByUserId(currentUser.getId());
            if(this.currentStudent== null){
                JOptionPane.showMessageDialog(this, "Unable to find student record", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error fetching student data: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadAdminViewAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<AdminData, Void> worker= new SwingWorker<>(){
            @Override
            protected AdminData doInBackground() throws Exception{
                Admin admin= adminDAO.getAdminByUserId(currentUser.getId());
                if(admin== null){
                    throw new Exception("Unable to find admin record");
                }
                int adminId= admin.getAdminId();
                List<Student> students= studentService.getAllStudents(adminId);
                return new AdminData(adminId, students);
            }
            @Override
            protected void done(){
                try{
                    AdminData data= get();
                    PerformanceFrame.this.adminId= data.adminId;
                    jLabel2.setVisible(true);
                    Box.setVisible(true);
                    loadingStudents= true;
                    Box.removeAllItems();
                    if(data.students.isEmpty()){
                        labelStatus.setText("No students found");
                        labelStatus.setForeground(Color.red);
                        Box.setEnabled(false);
                    }else{
                        for(Student s : data.students){
                            Box.addItem(s);
                        }
                        Box.setEnabled(true);
                        setupListeners();// only for admin
                        labelStatus.setText("Select a student to view performance");
                    }
                    loadingStudents= false;
                    ExportBtn.setEnabled(true);
                    ViewBtn.setEnabled(true);
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PerformanceFrame.this, "Error loading data: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    navigateBack();
                    PerformanceFrame.this.dispose();
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
    private void loadStudentViewAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<StudentData, Void> worker= new SwingWorker<>(){
            @Override
            protected StudentData doInBackground() throws Exception{
                StudentDAO studentDAO= new StudentDAO();
                Student student= studentDAO.getStudentByUserId(currentUser.getId());
                if(student== null){
                    throw new Exception("Unable to find student record");
                }
                int adminId= student.getAdminId();
                return new StudentData(adminId, student);
            }
            @Override
            protected void done(){
                try{
                    StudentData data= get();
                    PerformanceFrame.this.adminId= data.adminId;
                    PerformanceFrame.this.currentStudent= data.student;
                    jLabel2.setVisible(false);
                    Box.setVisible(false);
                    loadPerformanceAsync(data.student.getStudentId());
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PerformanceFrame.this, "Error loading data: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    navigateBack();
                    PerformanceFrame.this.dispose();
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
     private static class AdminData{
         final int adminId;
         final List<Student> students;
         
         AdminData(int adminId, List<Student> students){
             this.adminId= adminId;
             this.students= students;
         }
     }   
      private static class StudentData{
          final int adminId;
          final Student student;
          
          StudentData(int adminId, Student student){
              this.adminId= adminId;
              this.student= student;
          }
      }  
        
    
    private void setupTable(){
        tableModel= new DefaultTableModel(
                new String[]{"Course", "Score", "Grade", "Grade Point", "Units"},0
        ){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jTable.setModel(tableModel);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);
    }
    //load students into combobox
    private boolean loadingStudents= true;
     //listeners for admin
     private void setupListeners(){
         Box.addActionListener(evt -> {
             if(loadingStudents)return;
             Student selected= (Student) Box.getSelectedItem();
             if(selected != null){
                 loadPerformanceAsync(selected.getStudentId());
             }
         });
     }
     private void loadPerformanceAsync(int studentId){
         Box.setEnabled(false);
         ExportBtn.setEnabled(false);
         ViewBtn.setEnabled(false);
         labelStatus.setText("Loading performance...");
         labelStatus.setForeground(Color.orange);
         GPAlabel.setText("GPA: ...");
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
         SwingWorker<PerformanceResult, Void> worker= new SwingWorker<>(){
             @Override
             protected PerformanceResult doInBackground() throws Exception{
                 List<Grade> grades= gradeService.getGradeByStudent(studentId, adminId);
                 if(grades== null || grades.isEmpty()){
                     return new PerformanceResult(null, 0.0);
                 }
                 List<Course> allCourses= courseService.getCourseByAdminId(adminId);
                 Map<Integer, Course> courseMap= new HashMap<>();
                 for(Course c : allCourses){
                     courseMap.put(c.getCourseId(), c);
                 }
                 List<PerformanceRow> rows= new ArrayList<>();
                 double totalPoints= 0;
                 double totalUnits= 0;
                 for(Grade g : grades){
                     Course course= courseMap.get(g.getCourseId());
                     if(course != null){
                         rows.add(new PerformanceRow(course.getCourseCode(), g.getScore(), g.getGrade(), 
                     g.getGradePoint(), course.getUnits()
                         ));
                        totalPoints += g.getGradePoint() * course.getUnits();
                        totalUnits += course.getUnits();
                     }
                 }
                 double gpa= totalUnits > 0 ? totalPoints / totalUnits : 0;
                 return new PerformanceResult(rows, gpa);
             }
             @Override
             protected void done(){
                 try{
                     PerformanceResult result= get();
                     tableModel.setRowCount(0);
                     if(result.rows == null || result.rows.isEmpty()){
                         GPAlabel.setText("GPA: N/A");
                         GPAlabel.setForeground(Color.blue);
                         showMessage("No grades found for this student.", true);
                     }else{
                         for(PerformanceRow row: result.rows){
                             tableModel.addRow(new Object[]{
                                 row.courseCode, row.score, row.grade, row.gradePoint, row.units
                             });
                         }
                     }
                     GPAlabel.setText(String.format("GPA: %.2f", result.gpa));
                     GPAlabel.setForeground(Color.blue);
                     showMessage("Performance loaded successfully ("+ result.rows.size() + "Courses)", false);
                 }catch(Exception e){
                     e.printStackTrace();
                    GPAlabel.setText("GPA: N/A");
                    tableModel.setRowCount(0);
                    showMessage("Error loading Performance: "+ e.getMessage(), true);
                 }finally{
                     if("admin".equalsIgnoreCase(currentUser.getRole())){
                         Box.setEnabled(true);                         
                     }
                    ExportBtn.setEnabled(true);
                    ViewBtn.setEnabled(true);
                    setCursor(oldCursor);
                 }
             }
         };
         worker.execute();
     }
     private static class PerformanceResult{
         final List<PerformanceRow> rows;
         final double gpa;
         
         PerformanceResult(List<PerformanceRow> rows, double gpa){
             this.rows= rows;
             this.gpa= gpa;
         } 
     }
     private static class PerformanceRow{
         final String courseCode;
         final double score;
         final String grade;
         final double gradePoint;
         final int units;
         
         PerformanceRow(String courseCode, double score, String grade, double gradePoint, int units){
             this.courseCode= courseCode;
             this.score= score;
             this.grade= grade;
             this.gradePoint= gradePoint;
             this.units= units;
         }
         
     }
     private void showMessage(String message, boolean isError){
         labelStatus.setText(message);
         labelStatus.setForeground(isError ? Color.red : Color.green);
     }
     private void navigateBack(){
         if("admin".equalsIgnoreCase(currentUser.getRole())){
            new AdminDashboardFrame(currentUser).setVisible(true);
        }else if("student".equalsIgnoreCase(currentUser.getRole())){
            if(currentStudent == null){
                StudentDAO studentDAO= new StudentDAO();
                currentStudent= studentDAO.getStudentByUserId(currentUser.getId());
            }
             if(currentStudent != null && "graduated".equalsIgnoreCase(currentStudent.getStatus())){
               GradStudentDashboardFrame gradFrame= new GradStudentDashboardFrame(currentUser);
               gradFrame.setGraduatedMode(true);
               gradFrame.setVisible(true);
            }else if(currentStudent != null){
            new StudentDashboardFrame(currentStudent).setVisible(true);
        }else{
            JOptionPane.showMessageDialog(this, "Error: student record not found", "Error", JOptionPane.ERROR_MESSAGE);
            showMessage("Unknown user role", true);
            new loginFrame().setVisible(true);
        }
     }else{
         JOptionPane.showMessageDialog(this, "Unknown user role: "+ currentUser.getRole(), "Error", JOptionPane.ERROR_MESSAGE);
         new loginFrame().setVisible(true);
}

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
        Box = new javax.swing.JComboBox<Student>(new javax.swing.DefaultComboBoxModel<Student>())
        ;
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        label = new javax.swing.JLabel();
        ExportBtn = new javax.swing.JButton();
        ViewBtn = new javax.swing.JButton();
        backbtn = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();
        GPAlabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("PERFORMANCE VIEWER");

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jLabel2.setText("Select Student :");

        Box.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        Box.setModel(new javax.swing.DefaultComboBoxModel<Student>()
        );
        Box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BoxActionPerformed(evt);
            }
        });

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
        ));
        jScrollPane1.setViewportView(jTable);

        label.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        label.setText("Status:");

        ExportBtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        ExportBtn.setText("Export Report PDF");
        ExportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportBtnActionPerformed(evt);
            }
        });

        ViewBtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        ViewBtn.setText("View Chart");
        ViewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewBtnActionPerformed(evt);
            }
        });

        backbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        backbtn.setText("Back");
        backbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backbtnActionPerformed(evt);
            }
        });

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(192, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(170, 170, 170))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 29, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Box, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(GPAlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(ExportBtn)
                .addGap(34, 34, 34)
                .addComponent(ViewBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(backbtn)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Box, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                    .addComponent(GPAlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ExportBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ViewBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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

    private void BoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BoxActionPerformed

    private void backbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backbtnActionPerformed
        // TODO add your handling code here:
        this.dispose();
        navigateBack();
    }//GEN-LAST:event_backbtnActionPerformed

    private void ExportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportBtnActionPerformed
        // TODO add your handling code here:
        Student selectedStudent= (Student) Box.getSelectedItem();
            if(selectedStudent== null){
                showMessage("Pls select a student first.", true);
                return;
            }
            ExportBtn.setEnabled(false);
            ViewBtn.setEnabled(false);
            Box.setEnabled(false);
            labelStatus.setText("Generating report...");
            labelStatus.setForeground(Color.blue);
            Cursor oldCursor= getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            SwingWorker<String, Void> worker= new SwingWorker<>(){
                @Override
                protected String doInBackground() throws Exception{
                    try(Connection conn= DBConnection.getConnection()){
                        ReportService reportService= new ReportService();
                        StudentTranscriptDTO transcript= reportService.generateStudentTranscript(selectedStudent.getStudentId(), adminId, conn);
                        if(transcript== null){
                            throw new Exception("No Report data available for this student.");
                        }
                            //generate file path
                            String filePath= "Student_Report_"+ selectedStudent.getStudentId() + ".pdf";
                            ReportPDFGenerator.exportStudentTranscript(transcript, filePath);
                            return filePath;
                        }
                }
                        @Override 
                        protected void done(){
                        try{
                            String filePath= get();
                            showMessage("Report exported successfully: "+ filePath, false);
                            //optionally open file
                            int choice= JOptionPane.showConfirmDialog(PerformanceFrame.this, "Report exported successfully!\n" + filePath+ "\n\nOpen the file?", 
                                    "Export success", JOptionPane.YES_NO_OPTION);
                            if(choice== JOptionPane.YES_OPTION){
                                try{
                                    Desktop.getDesktop().open(new File(filePath));
                                }catch(Exception ex){
                                    showMessage("Couldn't open file: "+ ex.getMessage(), true);
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            showMessage("Error exporting report: "+ e.getMessage(), true);
                        }finally{
                            ExportBtn.setEnabled(true);
                            ViewBtn.setEnabled(true);
                            Box.setEnabled(true);
                            setCursor(oldCursor);
                        }
                        
                    }
                    };
                    worker.execute();
    }//GEN-LAST:event_ExportBtnActionPerformed

    private void ViewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewBtnActionPerformed
        // TODO add your handling code here:
        try{
            Student selectedStudent= (Student) Box.getSelectedItem();
            if(selectedStudent== null){
                showMessage("Pls select a student first.", true);
                return;
            }
            if(tableModel.getRowCount()== 0){
                showMessage("No data to visualize. Load a student's performance first.", true);
                return;
            }
            DefaultCategoryDataset dataset= new DefaultCategoryDataset();
            double maxScore= Double.NEGATIVE_INFINITY;
            //collect valid data
            for(int i= 0; i <tableModel.getRowCount(); i++){
                Object courseObj= tableModel.getValueAt(i, 0);
                Object scoreObj= tableModel.getValueAt(i, 1);
                if(courseObj == null || scoreObj == null) continue;
                String course= courseObj.toString().trim();
                if(course.isEmpty()) continue;
                
                double score;
                try{
                    score= Double.parseDouble(scoreObj.toString());
                }catch(NumberFormatException ex){
                    continue;//skip invalid numbers
                }
                dataset.addValue(score, "score", course);
                maxScore= Math.max(maxScore, score);
            }
            if(dataset.getColumnCount() == 0){
                showMessage("No valid score data found for chart", true);
                return;
            }
            //create a bar chart
            String title= "Student Performance Chart (GPA: "+ GPAlabel.getText().replace("GPA:", "").trim()+ ")";
            JFreeChart chart= ChartFactory.createBarChart(title, "Course Code", "Score", dataset,
                     PlotOrientation.VERTICAL, false, true, false); // false(hide legend), true(tooltips), false(urls)
            chart.setBackgroundPaint(Color.white);
            chart.getTitle().setPaint(new Color(0, 70, 140));
            //access plot for styling
            var plot= chart.getCategoryPlot();
            plot.setBackgroundPaint(new Color(245, 245, 245));
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            NumberAxis rangeAxis= (NumberAxis) plot.getRangeAxis();
            double upper= (Double.isFinite(maxScore) ? Math.max(100, maxScore + 5) : 100);
            rangeAxis.setRange(0, upper);
            
            BarRenderer renderer= (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(70, 130, 180));
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("{1} : {2}", NumberFormat.getInstance()));
            //display
            JDialog dialog= new JDialog(this, "Performance Chart", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            ChartPanel chartPanel= new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(750, 500));
            // close button
            JButton btnClose= new JButton("Close");
            btnClose.addActionListener(ev -> dialog.dispose());
            JPanel buttonPanel= new JPanel();
            buttonPanel.add(btnClose);
            //layout dialog
            dialog.setLayout(new BorderLayout());
            dialog.add(chartPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }catch(Exception e){
            showMessage("Error generating chart: "+ e.getMessage(), true);
            e.printStackTrace();
        }
    }//GEN-LAST:event_ViewBtnActionPerformed

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
            java.util.logging.Logger.getLogger(PerformanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PerformanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PerformanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PerformanceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PerformanceFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Student> Box;
    private javax.swing.JButton ExportBtn;
    private javax.swing.JLabel GPAlabel;
    private javax.swing.JButton ViewBtn;
    private javax.swing.JButton backbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel label;
    private javax.swing.JLabel labelStatus;
    // End of variables declaration//GEN-END:variables
}
