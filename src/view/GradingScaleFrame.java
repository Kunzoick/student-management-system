/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Service.GradingScaleService;
import dao.AdminDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.GradingScale;
import model.User;

/**
 *
 * @author jesse
 */
public class GradingScaleFrame extends javax.swing.JFrame {
    private User currentUser;
    private Admin currentAdmin;
    private GradingScaleService gradingScaleService;
    private DefaultTableModel tableModel;

    /**
     * Creates new form GradingScaleFrame
     */
    public GradingScaleFrame(User currentUser) {
        initComponents();
        this.currentUser= currentUser;
        this.gradingScaleService= new GradingScaleService();
        this.currentAdmin= new AdminDAO().getAdminByUserId(currentUser.getId());
        setupTable();
        setLocationRelativeTo(null);
        setControlsEnabled(false);
        labelStatus.setText("Loading...");
        labelStatus.setForeground(Color.orange);
        loadDataAsync();
        
    }
    //---setup table---
    private void setupTable(){
        tableModel= new DefaultTableModel(
                new String[]{"ID", "#", "Min Score", "Max Score", "Grade", "Grade Point"},0
        ){
            @Override
            public boolean isCellEditable(int row, int column){
                return column != 0 && column != 1;// ID & # are non editable 
            }
        };
       jTable.setModel(tableModel);
       jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       jTable.getTableHeader().setReorderingAllowed(false);
       jTable.getColumnModel().getColumn(0).setMinWidth(0);//hide id column
       jTable.getColumnModel().getColumn(0).setMaxWidth(0);
       jTable.getColumnModel().getColumn(0).setWidth(0);
    }
    private void loadDataAsync(){
        Cursor oldCursor= getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Admin, Void> worker= new SwingWorker<>(){
            @Override
            protected Admin doInBackground() throws Exception{
                AdminDAO adminDAO= new AdminDAO();
                Admin admin= adminDAO.getAdminByUserId(currentUser.getId());
                if(admin== null){
                    throw new Exception("Admin not found for user");
                }
                return admin;
            }
            @Override
            protected void done(){
                try{
                    currentAdmin= get();
                    setTitle("Manage Grading Scales - Admin ID:"+ currentAdmin.getAdminId());
                    loadAllGradingScale();
                    setControlsEnabled(true);
                }catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GradingScaleFrame.this, "Failed to load admin data: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    new AdminDashboardFrame(currentUser).setVisible(true);
                    GradingScaleFrame.this.dispose();
                }finally{
                    setCursor(oldCursor);
                }
            }
        };
        worker.execute();
    }
    private void setControlsEnabled(boolean enabled){
        Loadbtn.setEnabled(enabled);
        Rangebtn.setEnabled(enabled);
        Savebtn.setEnabled(enabled);
        Addbtn.setEnabled(enabled);
        Deletebtn.setEnabled(enabled);
        Updatebtn.setEnabled(enabled);
        Box.setEnabled(enabled);
        jTable.setEnabled(enabled);
    }
    //load all grading scale for this admin
    private void loadAllGradingScale(){
        tableModel.setRowCount(0);
        try{
            String selectedSystem= Box.getSelectedItem().toString();
            //fetch scales for the admin and system
            List<GradingScale> scales= gradingScaleService.getGradingScalesByAdmin(currentAdmin.getAdminId(), selectedSystem);
            if(scales== null || scales.isEmpty()){
                showMessage("No grading scales found for "+ selectedSystem, true);
                return;
            }
            int index= 1;
            for(GradingScale s : scales){
                tableModel.addRow(new Object[]{
                    s.getId(),
                    index++,
                    s.getMinScore(),
                    s.getMaxScore(),
                    s.getGrade(),
                    s.getGradePoint()
                });
            }
            showMessage(selectedSystem + "grading scales loaded successfully", false);
        }catch(Exception e){
            showMessage("Error loading grading scales: "+ e.getMessage(), true);
        }
    }
    private void refreshTable(){
        loadDataAsync();
    }
    //get selcted grading scale
    private Integer getSelectedRowIndex(){
        int row= jTable.getSelectedRow();
        if(row== -1) return null;
        return row;
    }
    private void showMessage(String message, boolean isError){
        labelStatus.setText(message);
        labelStatus.setForeground(isError ? java.awt.Color.red : java.awt.Color.green);
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
        Loadbtn = new javax.swing.JButton();
        Rangebtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        Backbtn = new javax.swing.JButton();
        Box = new javax.swing.JComboBox<>();
        Savebtn = new javax.swing.JButton();
        Addbtn = new javax.swing.JButton();
        Deletebtn = new javax.swing.JButton();
        Updatebtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Bodoni MT Black", 1, 18)); // NOI18N
        jLabel1.setText(" MANAGE GRADINGSCALE");

        jLabel2.setFont(new java.awt.Font("Footlight MT Light", 0, 12)); // NOI18N
        jLabel2.setText("Grading System:");

        Loadbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Loadbtn.setText("Load");
        Loadbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadbtnActionPerformed(evt);
            }
        });

        Rangebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Rangebtn.setText("Add Range");
        Rangebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RangebtnActionPerformed(evt);
            }
        });

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Min Score", "Max score", "Grade", "Grade Point"
            }
        ));
        jScrollPane1.setViewportView(jTable);

        Backbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Backbtn.setText("Back");
        Backbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackbtnActionPerformed(evt);
            }
        });

        Box.setFont(new java.awt.Font("Footlight MT Light", 0, 14)); // NOI18N
        Box.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4-Point", "5-point", "7-point" }));

        Savebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Savebtn.setText("Save");
        Savebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SavebtnActionPerformed(evt);
            }
        });

        Addbtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Addbtn.setText("Add Row");
        Addbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddbtnActionPerformed(evt);
            }
        });

        Deletebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Deletebtn.setText("Delete Row");
        Deletebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeletebtnActionPerformed(evt);
            }
        });

        Updatebtn.setFont(new java.awt.Font("Footlight MT Light", 1, 12)); // NOI18N
        Updatebtn.setText("Update");
        Updatebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdatebtnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        jLabel5.setText("Status:");

        labelStatus.setFont(new java.awt.Font("Footlight MT Light", 0, 13)); // NOI18N
        labelStatus.setText("jLabel6");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(Deletebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(Updatebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(12, 12, 12)
                                .addComponent(Box, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Loadbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(Rangebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(Savebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 5, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Addbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(Backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(167, 167, 167))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Box, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Savebtn)
                        .addComponent(Rangebtn)
                        .addComponent(Loadbtn)))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Deletebtn)
                    .addComponent(Updatebtn)
                    .addComponent(Addbtn))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
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

    private void DeletebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeletebtnActionPerformed
        // TODO add your handling code here:
        Integer selectedRow= getSelectedRowIndex();
        if(selectedRow== null){
            showMessage("Select a row to delete.", true);
            return;
        }
        try{
            int scaleId= Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            int confirm= JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this grading scale?", "Confimation", JOptionPane.YES_NO_OPTION);
            if(confirm== JOptionPane.YES_OPTION){
            boolean deleted= gradingScaleService.deleteGradingScale(scaleId, currentAdmin.getAdminId());
            if(deleted){
                tableModel.removeRow(selectedRow);
                showMessage("Grading scale has been deleted successfully", false);
            }else{
                showMessage("Failed to delete grading scale, Check if record exists", true);
            }
            }
        }catch(Exception e){
            e.printStackTrace();
            showMessage("Error deleting grading scale: "+ e.getMessage(), true);
        }
    }//GEN-LAST:event_DeletebtnActionPerformed

    private void SavebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SavebtnActionPerformed
        // TODO add your handling code here:
        try{
            String selectedSystem= Box.getSelectedItem().toString();
            List<GradingScale> scalesToSave= new ArrayList<>();
            for(int i= 0; i< tableModel.getRowCount(); i++){
                double min= Double.parseDouble(tableModel.getValueAt(i, 2).toString());
                double max= Double.parseDouble(tableModel.getValueAt(i, 3).toString());
                String grade= tableModel.getValueAt(i, 4).toString().trim();
                double point= Double.parseDouble(tableModel.getValueAt(i, 5).toString());
                
                //validation
                if(grade.isEmpty()){
                    showMessage("Row "+ (i+1)+ ": Grade cannot be empty", true);
                    return;
                }
                if(min > max){
                    showMessage("Row "+ (i+1)+ ": Min score cannot be greater than max score", true);
                    return;
                }
                if(min < 0 || max > 100){
                    showMessage("Row "+ (i+1)+ ": Scores must be between 0 and 100", true);
                    return;
                }
                
                GradingScale scale= new GradingScale();
                scale.setGradingSystem(selectedSystem);
                scale.setMInScore(min);
                scale.setMaxScore(max);
                scale.setGrade(grade);
                scale.setGradePoint(point);
                scalesToSave.add(scale);
            }
            
                setControlsEnabled(false);
                labelStatus.setText("Saving...");
                labelStatus.setForeground(Color.orange);
                int adminId= currentAdmin.getAdminId();
                Cursor oldCursor= getCursor();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                SwingWorker<SaveResult, Void> worker= new SwingWorker<>(){
                    @Override
                    protected SaveResult doInBackground() throws Exception{
                        int successCount= 0;
                        int skipCount= 0;
                        
                        for(GradingScale scale : scalesToSave){
                            boolean created= gradingScaleService.createGradingScale(scale, adminId);
                            if(created){
                                successCount++;
                            }else{
                                skipCount++;
                            }
                        }
                        return new SaveResult(successCount, skipCount);
                    }
                    @Override
                    protected void done(){
                        try{
                            SaveResult result= get();
                            loadAllGradingScale();
                            if(result.skipCount > 0){
                                showMessage(String.format("Saved %d scales(s), Skipped %d duplicate/overlapping", result.successCount, result.skipCount), true);
                            }else{
                                showMessage(result.successCount+ "grading scale(s) saved successfully", false);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            showMessage("Error saving grading scales: "+ e.getMessage(), true);
                        }finally{
                            setControlsEnabled(true);
                            setCursor(oldCursor);
                        }
                    }
                };
                worker.execute();           
               
        }catch(NumberFormatException e){
            showMessage("Invalid number format in table. Pls check all values.", true);
        }catch(Exception e){
            showMessage("Error preparing save: "+ e.getMessage(), true);
            e.printStackTrace();
        }
}
        private static class SaveResult{
            final int successCount;
            final int skipCount;
            
            SaveResult(int successCount, int skipCount){
                this.successCount= successCount;
                this.skipCount= skipCount;
            }
    }//GEN-LAST:event_SavebtnActionPerformed

    private void RangebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RangebtnActionPerformed
        // TODO add your handling code here
        String selectedSystem= Box.getSelectedItem().toString();
        //clear table before adding new ranges
        tableModel.setRowCount(0);
        switch(selectedSystem.toLowerCase()){
            case "4-point" -> {
                tableModel.addRow(new Object[]{null, 70.00, 100.00, "A", 4.0});
                tableModel.addRow(new Object[]{null, 60.00, 69.99, "B", 3.0});
                tableModel.addRow(new Object[]{null, 50.00, 59.99, "C", 2.0});
                tableModel.addRow(new Object[]{null, 45.00, 49.99, "D", 1.0});
                tableModel.addRow(new Object[]{null, 0, 44.99, "F", 0.0});
            }
            case "5-point" -> {
                tableModel.addRow(new Object[]{null, 70.00, 100.00, "A", 5.0});
                tableModel.addRow(new Object[]{null, 60.00,69.99, "B", 4.0});
                tableModel.addRow(new Object[]{null, 50.00, 59.99, "C", 3.0});
                tableModel.addRow(new Object[]{null, 45.00, 49.99, "D", 2.0});
                tableModel.addRow(new Object[]{null, 40.00, 44.99, "E", 1.0});
                tableModel.addRow(new Object[]{null, 0, 39.99, "F", 0.0});
            }
            case "7-point" -> {
                tableModel.addRow(new Object[]{null, 85.00, 100.00, "A+", 7.0});
                tableModel.addRow(new Object[]{null, 75.00, 84.99, "A-", 6.0});
                tableModel.addRow(new Object[]{null, 66.00, 74.99, "B+", 5.0});
                tableModel.addRow(new Object[]{null, 60.00, 65.99, "B", 4.0});
                tableModel.addRow(new Object[]{null, 50.00, 59.99, "C", 3.0});
                tableModel.addRow(new Object[]{null, 45.00, 49.99, "D", 2.0});
                tableModel.addRow(new Object[]{null, 40.00, 44.99, "E", 1.0});
                tableModel.addRow(new Object[]{null, 0, 39.99, "F", 0.0});
            }
            default -> {
                showMessage("Unknown grading system selected.", true);
                return;
            }
        }
        showMessage("Default "+ selectedSystem + "ranges loaded. Click 'Save' to persist.", false);
    }//GEN-LAST:event_RangebtnActionPerformed

    private void LoadbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadbtnActionPerformed
        // TODO add your handling code here:
        try{
            loadAllGradingScale();
            showMessage("Grading scales loaded successfully", false);
        }catch(Exception e){
            showMessage("Error loading grading scales:"+ e.getMessage(), true);
        }
    }//GEN-LAST:event_LoadbtnActionPerformed

    private void UpdatebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdatebtnActionPerformed
        // TODO add your handling code here:
        Integer selectedRow= getSelectedRowIndex();
        if(selectedRow== null){
            showMessage("Select a row to update.", true);
            return;
        }
        try{
            int adminId= currentAdmin.getAdminId();
            int scaleId= Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            double min= Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
            double max= Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString());
            String grade= tableModel.getValueAt(selectedRow, 4).toString().trim();
            double point= Double.parseDouble(tableModel.getValueAt(selectedRow, 5).toString());
            String system= Box.getSelectedItem().toString();
            
            if(grade.isEmpty()){
                showMessage("Grade cannot be empty.", true);
                return;
            }
            if(min > max){
                showMessage ("Min score cannot be greater than max score.", true);
                return;
            }
            if(min < 0 || max > 100){
                showMessage("Scores must be between 0 and 100", true);
                return;
            }
            
            GradingScale scale= new GradingScale();
            scale.setId(scaleId);
            scale.setAdminId(adminId);
            scale.setGradingSystem(system);
            scale.setMInScore(min);
            scale.setMaxScore(max);
            scale.setGrade(grade);
            scale.setGradePoint(point);
            
            setControlsEnabled(false);
            labelStatus.setText("Updating...");
            labelStatus.setForeground(Color.orange);
            Cursor oldCursor= getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            SwingWorker<Boolean, Void> worker= new SwingWorker<>(){
                @Override
                protected Boolean doInBackground() throws Exception{
                    return gradingScaleService.updateGradingScale(scale, adminId);
                }
                @Override
                protected void done(){
                    try{
                        boolean updated= get();
                        if(updated){
                            refreshTable();
                            showMessage("Range updated successfully. ", false);
                        }else{
                            showMessage("Update failed. check for overlapping ranges.", true);
                        }
                    }catch(Exception e){
                        showMessage("Unexpected error: "+e.getMessage(), true);
                        e.printStackTrace(); 
                    }finally{
                        setControlsEnabled(true);
                        setCursor(oldCursor);
                    }
                }
            };
            worker.execute();
        }catch(NumberFormatException e){
        showMessage("Invalid number format. Please check your input.", true);
    }catch(Exception e){
        showMessage("Unexpected error: " + e.getMessage(), true);
        e.printStackTrace();
    }
    }//GEN-LAST:event_UpdatebtnActionPerformed

    private void AddbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddbtnActionPerformed
        // TODO add your handling code here:
        if(tableModel.getRowCount() >= 20){
            showMessage("Maximum 20 grading scales allowed", true);
            return;
        }
        tableModel.addRow(new Object[]{
            null,//id(will be set after save)
            tableModel.getRowCount() + 1, "", "", "", "" //serial number and columns
        });
        showMessage("Blank row added. Enter range details directly in the table.", false);
    }//GEN-LAST:event_AddbtnActionPerformed

    private void BackbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackbtnActionPerformed
        // TODO add your handling code here:
        new AdminDashboardFrame(currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BackbtnActionPerformed

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
            java.util.logging.Logger.getLogger(GradingScaleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GradingScaleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GradingScaleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GradingScaleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GradingScaleFrame().setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Addbtn;
    private javax.swing.JButton Backbtn;
    private javax.swing.JComboBox<String> Box;
    private javax.swing.JButton Deletebtn;
    private javax.swing.JButton Loadbtn;
    private javax.swing.JButton Rangebtn;
    private javax.swing.JButton Savebtn;
    private javax.swing.JButton Updatebtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel labelStatus;
    // End of variables declaration//GEN-END:variables
}
