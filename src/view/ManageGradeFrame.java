/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.AdminService;
import Service.CourseService;
import Service.GradeService;
import Service.GradingScaleService;
import Service.StudentService;
import dao.AdminDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.Course;
import model.Student;
import model.User;
import util.DBConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import model.Grade;
import model.GradingScale;

/**
 *
 * @author jesse
 */
public class ManageGradeFrame extends javax.swing.JFrame {
    private User currentUser;
    private AdminDAO adminDAO= new AdminDAO();
    private AdminService adminService= new AdminService();
    private StudentService studentService= new StudentService();
    private CourseService courseService= new CourseService();
    private GradeService gradeService= new GradeService();
    private int adminId;
    private DefaultTableModel gradeTableModel;

    /**
     * Creates new form ManageGradeFrame
     */
    public ManageGradeFrame(User currentUser) {
        initComponents();
        this.currentUser= currentUser;
        setupTable();
        setControlsEnabled(false);
        labelStatus.setText("Loading...");
        labelStatus.setForeground(Color.orange);
        loadDataAsync();
        
    }
    private void loadDataAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<GradeFrameData, Void> worker= new SwingWorker<>(){
            @Override
            protected GradeFrameData doInBackground() throws Exception{
                Admin admin= adminDAO.getAdminByUserId(currentUser.getId());
                if(admin== null){
                    throw new Exception("Admin not found");
                }
                int adminId= admin.getAdminId();
                List<Student> students= studentService.getAllStudents(adminId);
                List<Course> courses= courseService.getCourseByAdminId(adminId);
                return new GradeFrameData(adminId, students, courses);
            }
            @Override
            protected void done(){
                try{
                    GradeFrameData data= get();
                    ManageGradeFrame.this.adminId= data.adminId;
                    
                    StudentBox.removeAllItems();
                    for(Student s : data.students){
                        StudentBox.addItem(s.getFirstName()+ " "+ s.getLastName()+ "(ID"+ s.getStudentId()+ ")");
                    }
                    CourseBox.removeAllItems();
                    for(Course c : data.courses){
                        CourseBox.addItem(c.getCourseCode()+ "-"+ c.getCourseName()+ "(ID"+ c.getCourseId()+ ")");
                    }
                    setControlsEnabled(true);
                    labelStatus.setText("Ready");
                    labelStatus.setForeground(Color.green);
                }catch(Exception e){
                    e.printStackTrace();
                    labelStatus.setText("Error loading data: "+ e.getMessage());
                    labelStatus.setForeground(Color.red);
                    JOptionPane.showMessageDialog(ManageGradeFrame.this, "Failed to load data: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    new AdminDashboardFrame(currentUser).setVisible(true);
                    ManageGradeFrame.this.dispose();
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
    private static class GradeFrameData{
        final int adminId;
        final List<Student> students;
        final List<Course> courses;
        
        GradeFrameData(int adminId, List<Student> students, List<Course> courses){
            this.adminId= adminId;
            this.students= students;
            this.courses= courses;
        }
    }
    private void setControlsEnabled(boolean enabled){
        Addbtn.setEnabled(enabled);
        Deletebtn.setEnabled(enabled);
        GPAbtn.setEnabled(enabled);
        jButton1.setEnabled(enabled);
        StudentBox.setEnabled(enabled);
        CourseBox.setEnabled(enabled);
    }
    private void setupTable(){
        gradeTableModel= new DefaultTableModel(new Object[]{"Course", "Score", "Grade", "Grade Point", "Units"},0)
                {    
                    //make table noneditable
                  @Override
                  public boolean isCellEditable(int row, int column){
                      return false;
                  }
                };
        jTable.setModel(gradeTableModel);
        jTable.getTableHeader().setReorderingAllowed(false);
    }
    private void refreshTable(int studentId){
        gradeTableModel.setRowCount(0);
        double totalPoints= 0;
        double totalUnits= 0;
        try{
            List<Grade> grades= gradeService.getGradeByStudent(studentId, adminId);
            //fetch all courses once
            List<Course> allCourses= courseService.getCourseByAdminId(adminId);
            //create map for 0(1) lookup
            Map<Integer, Course> courseMap= new HashMap<>();
            for(Course c : allCourses){
                courseMap.put(c.getCourseId(), c);
            }
            for(Grade g : grades){
                Course course= courseMap.get(g.getCourseId());
                if(course != null){
                    gradeTableModel.addRow(new Object[]{
                        course.getCourseName(), g.getScore(), g.getGrade(), g.getGradePoint(), course.getUnits()
                    });
                    totalPoints += g.getGradePoint() * course.getUnits();
                    totalUnits += course.getUnits();
                }
            }
            double gpa= (totalUnits >0) ? totalPoints / totalUnits : 0;
            totalPointslabel.setText(String.format("%.2f", totalPoints));
            totalUnitslabel.setText(String.format("%.2f", totalUnits));
            GPAlabel.setText(String.format("%.2f", gpa));
            labelStatus.setText("Grades refreshed successfully");
            labelStatus.setForeground(Color.green);
        }catch(Exception e){
            e.printStackTrace();
            labelStatus.setText("Error refreshing Grade table:"+ e.getMessage());
            labelStatus.setForeground(Color.red);
        }
    }
    private String getGradingSystemForAdmin(){
        GradingScaleService gradingScaleService= new GradingScaleService();
        //get all scales for admin
        List<GradingScale> scales= gradingScaleService.getGradingScalesByAdmin(adminId, "");
        if(scales== null || scales.isEmpty()) return null;
        return scales.get(0).getGradingSystem();
    }
    private int extractStudentId(String text){
        if(text== null) return -1;
        //pattern like (ID222) or (ID 222)
        Pattern pattern= Pattern.compile("\\(ID\\s*(\\d+)\\s*\\)");
        Matcher matcher= pattern.matcher(text);
        if(matcher.find()){
            try{
                return Integer.parseInt(matcher.group(1));
            }catch(NumberFormatException e){
                return -1;
            }
        }
        return -1;
    }
    private int extractCourseId(String text){
        if(text== null) return -1;
        Pattern pattern= Pattern.compile("\\(ID\\s*(\\d+)\\s*\\)");
        Matcher matcher= pattern.matcher(text);
        if(matcher.find()){
       try{
           return Integer.parseInt(matcher.group(1));
       }catch(NumberFormatException e){
           return -1;
           }
       }
        return -1;
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
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        StudentBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        CourseBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        Scorelabel = new javax.swing.JTextField();
        Addbtn = new javax.swing.JButton();
        GPAbtn = new javax.swing.JButton();
        Deletebtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        totalPointslabel = new javax.swing.JLabel();
        totalUnitslabel = new javax.swing.JLabel();
        GPAlabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel2.setText("Student :");

        StudentBox.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        StudentBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel3.setText("Course :");

        CourseBox.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        CourseBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jLabel4.setText("Score :");

        Scorelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        Addbtn.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        Addbtn.setText("Add Grade");
        Addbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddbtnActionPerformed(evt);
            }
        });

        GPAbtn.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        GPAbtn.setText("Compute GPA");
        GPAbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GPAbtnActionPerformed(evt);
            }
        });

        Deletebtn.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        Deletebtn.setText("Delete Grade");
        Deletebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeletebtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Scorelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StudentBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(Addbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(Deletebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(176, 176, 176)
                        .addComponent(GPAbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(204, 204, 204)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CourseBox, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(StudentBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CourseBox, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(Scorelabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Addbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Deletebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GPAbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("        MANAGE GRADES");

        jLabel5.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jLabel5.setText("GRADES");

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Course", "Score", "Grade", "Grade Point", "Units"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable);

        jPanel3.setBackground(new java.awt.Color(255, 153, 153));

        jLabel6.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel6.setText("GPA Summary :");

        jLabel7.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel7.setText("Total Points :");

        jLabel8.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel8.setText("Total Units :");

        jLabel9.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel9.setText("GPA :");

        totalPointslabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        totalUnitslabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        GPAlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(GPAlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(totalPointslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(totalUnitslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalPointslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalUnitslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GPAlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 16, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jLabel13.setText("Status");

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N

        jButton5.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        jButton5.setText("Back");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(171, 171, 171)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        new AdminDashboardFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void AddbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddbtnActionPerformed
        // TODO add your handling code here:
            String studenttext= (String) StudentBox.getSelectedItem();
            String coursetext= (String) CourseBox.getSelectedItem();
            String scoretext= Scorelabel.getText().trim();
            
            if(studenttext== null || studenttext.startsWith("Item") || coursetext== null || coursetext.startsWith("Item") || scoretext.isEmpty()){
                JOptionPane.showMessageDialog(this, "Pls fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(studenttext.startsWith("Item") || coursetext.startsWith("Item")){
                JOptionPane.showMessageDialog(this, "Pls select valid student and course", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            double scoreValue;
            try{
                scoreValue= Double.parseDouble(scoretext);
                if(scoreValue <0 || scoreValue >100){
                    JOptionPane.showMessageDialog(this, "Score must be between 0 & 100", "Invalid Score", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(this, "Score must be a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int studentId= extractStudentId(studenttext);
            int courseId= extractCourseId(coursetext);
            if(studentId== -1 || courseId== -1){
                JOptionPane.showMessageDialog(this, "Invalid student or course selection", "Error", JOptionPane.ERROR_MESSAGE);
                return;  
        }
            Addbtn.setEnabled(false);
            Deletebtn.setEnabled(false);
            GPAbtn.setEnabled(false);
            labelStatus.setText("Adding grade...");
            labelStatus.setForeground(Color.orange);
            
            Cursor oldCursor= getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
                private String gradingSystem;
                @Override 
                protected Boolean doInBackground() throws Exception{
                   gradingSystem= getGradingSystemForAdmin();
                   if(gradingSystem== null){
                       throw new Exception("No grading system found for this admin");
                   }
                       return gradeService.assignGrade(studentId, courseId, adminId, scoreValue, gradingSystem);
                   }
                   @Override
                   protected void done(){
                    try{
                        boolean success= get();
                        if(success){
                        refreshTable(studentId);
                        Scorelabel.setText("");
                        labelStatus.setText("Grade added successfully!");
                        labelStatus.setForeground(Color.green);
                    }else{
                            JOptionPane.showMessageDialog(ManageGradeFrame.this, "Failed to add grade. Check grading scale", "Error", JOptionPane.ERROR_MESSAGE);
                            labelStatus.setText("Failed to add grade");
                            labelStatus.setForeground(Color.red);
                            }
                    }catch(Exception e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ManageGradeFrame.this, "Error: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        labelStatus.setText("Error: "+ e.getMessage());
                        labelStatus.setForeground(Color.red);
                    }finally{
                        Addbtn.setEnabled(true);
                        Deletebtn.setEnabled(true);
                        GPAbtn.setEnabled(true);
                    }
                }
            };
              worker.execute();
    }//GEN-LAST:event_AddbtnActionPerformed

    private void DeletebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeletebtnActionPerformed
        // TODO add your handling code here:
        try{
            String studentText= (String) StudentBox.getSelectedItem();
            String courseText= (String) CourseBox.getSelectedItem();
            
            if(studentText== null || studentText.startsWith("Item") || courseText== null || courseText.startsWith("Item")){
                labelStatus.setText("Select both student and course to delete grade");
                labelStatus.setForeground(Color.red);
                return;
            }
            int studentId= extractStudentId(studentText);
            int courseId= extractCourseId(courseText);
            if(studentId== -1 || courseId== -1){
                labelStatus.setText("Invalid student or course selection");
                labelStatus.setForeground(Color.red);
                return;
            }
            //get the grade object for gradeId
            Grade grade= gradeService.getGrade(studentId, courseId, adminId);
            if(grade== null){
                labelStatus.setText("No grade found fot this student course combination");
                labelStatus.setForeground(Color.red);
                return;
            }
            int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this grade?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if(confirm== JOptionPane.YES_OPTION){
                boolean deleted= gradeService.deleteGrade(grade.getGradeId(), adminId);
                if(deleted){
                    refreshTable(studentId);
                    Scorelabel.setText("");
                    labelStatus.setText("Grade deleted successfully!");
                    labelStatus.setForeground(Color.green);
                }else{
                        labelStatus.setText("Grade not found or could not be delted");
                        labelStatus.setForeground(Color.red);
                        }
                }
            }catch(Exception e){
                e.printStackTrace();
                labelStatus.setText("Error deleting grade:"+ e.getMessage());
                labelStatus.setForeground(Color.red);
        }
    }//GEN-LAST:event_DeletebtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String studentText= (String) StudentBox.getSelectedItem();
        if(studentText== null){
            labelStatus.setText("Pls select a student first");
            labelStatus.setForeground(Color.red);
            return;
        }
        int studentId= extractStudentId(studentText);
        if(studentId == -1){
            labelStatus.setText("Invalid student selection");
            labelStatus.setForeground(Color.red);
            return;
        }
        refreshTable(studentId);
        labelStatus.setText("Grades refreshed");
        labelStatus.setForeground(Color.green);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void GPAbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GPAbtnActionPerformed
        // TODO add your handling code here:
        String studentText= (String) StudentBox.getSelectedItem();
        if(studentText== null){
            labelStatus.setText("Pls select a student to cslculate GPA");
            labelStatus.setForeground(Color.red);
            return;
        }
        int studentId= extractStudentId(studentText);
        if(studentId == -1){
            labelStatus.setText("Invalid student selection");
            labelStatus.setForeground(Color.red);
            return;
        }
        double gpa= gradeService.calculateGPA(studentId, adminId);
        JOptionPane.showMessageDialog(this, "Average GPA of student"+ String.format("%.2f", gpa), "GPA result", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_GPAbtnActionPerformed

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
            java.util.logging.Logger.getLogger(ManageGradeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageGradeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageGradeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageGradeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageGradeFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Addbtn;
    private javax.swing.JComboBox<String> CourseBox;
    private javax.swing.JButton Deletebtn;
    private javax.swing.JButton GPAbtn;
    private javax.swing.JLabel GPAlabel;
    private javax.swing.JTextField Scorelabel;
    private javax.swing.JComboBox<String> StudentBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel totalPointslabel;
    private javax.swing.JLabel totalUnitslabel;
    // End of variables declaration//GEN-END:variables
}
