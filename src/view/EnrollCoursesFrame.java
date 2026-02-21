/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.CourseService;
import Service.EnrollmentService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.Course;
import model.Student;

/**
 *
 * @author jesse
 */
public class EnrollCoursesFrame extends javax.swing.JFrame {
    private int StudentId;
    private int adminId;
    private Student currentStudent;
    private EnrollmentService enrollmentService;
    private CourseService courseService;
    private DefaultTableModel courseTableModel;
    
    private Map<String, Course> courseCache= new HashMap<>();
    private int currentUnitsEnrolled= 0;

    /**
     * Creates new form EnrollCoursesFrame
     */ 
    public EnrollCoursesFrame(Student currentStudent) {
        this.currentStudent= currentStudent;
        this.StudentId= currentStudent.getStudentId();
        this.adminId= currentStudent.getAdminId();
        this.enrollmentService= new EnrollmentService();
        this.courseService= new CourseService();

        initComponents();        
        namelabel.setText("Student: "+ currentStudent.getFirstName()+ " "+ currentStudent.getLastName());
        ConfigureTable();
        loadDataAsync();

    }
    private void ConfigureTable(){
        courseTableModel= new DefaultTableModel(new Object[]{"Code", "Course Name", "Units", "ACTION"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return column== 3;//only action column is editable
            }
        };
        jTable.setModel(courseTableModel);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private static class EnrollmentData{
        final List<Course> courses;
        final int currentUnits;
        final String errorMessage;
        
        EnrollmentData(List<Course> courses, int currentUnits, String errorMessage){
            this.courses= courses;
            this.currentUnits= currentUnits;
            this.errorMessage= errorMessage;
        }
    }
    private void loadDataAsync(){
        setControlsEnabled(false);
        labelStatus.setText("Loading Courses...");
        labelStatus.setForeground(Color.orange);
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<EnrollmentData, Void> worker= new SwingWorker<>(){
            @Override
            protected EnrollmentData doInBackground() throws Exception{
                try{
                    List<Course> courses= courseService.getCourseByAdminId(adminId);
                    int units= enrollmentService.CountUnitEnrolled(StudentId, adminId);
                    return new EnrollmentData(courses, units, null);
                }catch(Exception e){
                    e.printStackTrace();
                    return new EnrollmentData(null, 0, e.getMessage());
                }
            }
            @Override
            protected void done(){
                try{
                    EnrollmentData data= get();
                    if(data.errorMessage != null){
                        labelStatus.setText("Error loading data: "+ data.errorMessage);
                        labelStatus.setForeground(Color.red);
                        return;
                    }
                    currentUnitsEnrolled= data.currentUnits;
                    unitlabel.setText("Units Registered: "+ currentUnitsEnrolled);
                    //populate course table and cache
                    courseTableModel.setRowCount(0);
                    courseCache.clear();
                    
                    if(data.courses== null || data.courses.isEmpty()){
                        labelStatus.setText("No courses available");
                        labelStatus.setForeground(Color.orange);
                    }else{
                        for(Course c : data.courses){
                            courseTableModel.addRow(new Object[]{
                                c.getCourseCode(), c.getCourseName(), c.getUnits(), "Enroll"
                            });
                            courseCache.put(c.getCourseCode(), c);
                        }
                        addEnrollButtonTable();
                        labelStatus.setText("Loaded "+ data.courses.size()+ " course(s) - Click 'Enroll' to register");
                        labelStatus.setForeground(Color.green);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    labelStatus.setText("Error displaying courses: "+ e.getMessage());
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
        jTable.setEnabled(enabled);
    }
    
    /*private void totalUnits(){
        try{
            int totalUnits= enrollmentService.CountUnitEnrolled(StudentId, adminId);
            unitlabel.setText(String.valueOf("Units Registered: "+ totalUnits));
        }catch(Exception e){
            labelStatus.setText("Error calculating Units.");
            labelStatus.setForeground(Color.red);
        }
    }
    private void loadCourseTable(){
        courseTableModel.setRowCount(0);
        try{
            List<Course> courseList= courseService.getCourseByAdminId(adminId);
            for(Course c : courseList){
                courseTableModel.addRow(new Object[]{
                    
                });
            }
            addEnrollButtonTable();
        }catch(Exception e){
            labelStatus.setText("Error loading courses.");
            labelStatus.setForeground(Color.red);
            e.printStackTrace();
        }
    }
    */
    private void addEnrollButtonTable(){
        jTable.getColumn("ACTION").setCellRenderer(new ButtonRenderer());
        jTable.getColumn("ACTION").setCellEditor(new ButtonEditor(new JTextField()));
    }
    //button renderer and editor
    class ButtonRenderer extends JButton implements TableCellRenderer{ 
        public ButtonRenderer(){ setOpaque(true);}
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column){
            setText("Enroll");
            return this;
        }
    }
    class ButtonEditor extends DefaultCellEditor{
        private JButton btn;
        private String label;
        private boolean clicked;
        private int row;
        
        public ButtonEditor(JTextField txt){
            super(txt);
            btn= new JButton();
            btn.addActionListener((ActionEvent e) -> {
                fireEditingStopped();
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column){
            this.label= "Enroll";
            btn.setText(label);
            this.row= row;
            clicked= true;
            return btn;
        }
        @Override
        public Object getCellEditorValue(){
            if(clicked){
                enrollCourse(row);
            }
            clicked= false;
            return label;
    }
        @Override
        public boolean stopCellEditing(){
            clicked= false;
            return super.stopCellEditing();
        }
    }
    private void enrollCourse(int row){
            String courseCode= courseTableModel.getValueAt(row, 0).toString();
            Course course= courseCache.get(courseCode);
            if(course== null){
                labelStatus.setText("Course not Found.");
                labelStatus.setForeground(Color.red);
                return;
            }
           if(currentUnitsEnrolled + course.getUnits() > 52){
               JOptionPane.showMessageDialog(this, String.format("""
                                                                 Cannot enroll in %s - %s
                                                                 
                                                                 Current units: %d
                                                                 Course units: %d
                                                                 Total would be: %d
                                                                 
                                                                 Maximum allowed: 52 units""", 
                       course.getCourseCode(), course.getCourseName(), currentUnitsEnrolled, course.getUnits(), currentUnitsEnrolled + 
                               course.getUnits()), "Unit Limit exceeded", JOptionPane.WARNING_MESSAGE);
               labelStatus.setText("Unit limit exceeded");
               labelStatus.setForeground(Color.orange);
               return;
           }
           int confirm= JOptionPane.showConfirmDialog(this, String.format("Enroll in:\n\n%s - %s\nUnits: %d\n\nConfirm enrollment?", 
                   course.getCourseCode(), course.getCourseName(), course.getUnits()), "Confirm enrollment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
           if(confirm != JOptionPane.YES_OPTION) return;
           enrollCourseAsync(course);
    }
    private void enrollCourseAsync(Course course){
        setControlsEnabled(false);
        labelStatus.setText("Enrolling in "+ course.getCourseCode()+ "...");
        labelStatus.setForeground(Color.orange);
        Cursor oldCursor= getCursor();
        
        SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
            @Override
            protected Boolean doInBackground() throws Exception{
                return enrollmentService.enrollStudent(StudentId, course.getCourseId(), adminId, "student");
            }
            @Override
            protected void done(){
                try{
                    boolean enrolled= get();
                    if(enrolled){
                        currentUnitsEnrolled += course.getUnits();
                        unitlabel.setText("Units Registered: "+ currentUnitsEnrolled);
                        labelStatus.setText("Successfully enrolled in: "+ course.getCourseCode()+ " (Total units: "+ currentUnitsEnrolled+ ")");
                        labelStatus.setForeground(Color.green);
                        JOptionPane.showMessageDialog(EnrollCoursesFrame.this, String.format("Successfully enrolled in:\n\n%s - %s\n\nTotal units: %d", course.getCourseCode(), course.getCourseName()
                                , currentUnitsEnrolled), "Enrollment Successful", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        labelStatus.setText("Enrollment failed: Already enrolled or error occurred");
                        labelStatus.setForeground(Color.red);
                        JOptionPane.showMessageDialog(EnrollCoursesFrame.this, "Could not enroll in "+ course.getCourseCode()+ "\n\n"+ "You may already be enroled in this course.",
                                "Enrollment Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    labelStatus.setText("Error durong enrollment: "+ e.getMessage());
                    labelStatus.setForeground(Color.red);
                     JOptionPane.showMessageDialog(
                        EnrollCoursesFrame.this, "Error during enrollment:\n" + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }finally{
                    setControlsEnabled(true);
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
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
        namelabel = new javax.swing.JLabel();
        unitlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        refresh = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText("ENROLL COURSES");

        namelabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        namelabel.setText("Student: Kunzoick implemnet");

        unitlabel.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        unitlabel.setText("Units Registered: 35");

        jTable.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Code", "Course name", "Units", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable.setColumnSelectionAllowed(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setResizable(false);
            jTable.getColumnModel().getColumn(1).setResizable(false);
            jTable.getColumnModel().getColumn(2).setResizable(false);
            jTable.getColumnModel().getColumn(3).setResizable(false);
        }

        refresh.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        refresh.setText("Refresh");
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jButton2.setText("Back");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        labelStatus.setText("Status: Ready");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(222, 222, 222)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(unitlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(refresh, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        // TODO add your handling code here:
        loadDataAsync();
    }//GEN-LAST:event_refreshActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        new StudentDashboardFrame(currentStudent).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(EnrollCoursesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EnrollCoursesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EnrollCoursesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EnrollCoursesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EnrollCoursesFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel namelabel;
    private javax.swing.JButton refresh;
    private javax.swing.JLabel unitlabel;
    // End of variables declaration//GEN-END:variables
}
