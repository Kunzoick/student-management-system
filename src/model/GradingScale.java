/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package model;

/**
 *
 * @author jesse
 */
public class GradingScale {
    private int id;
    private String gradingSystem;
    private double minScore;
    private double maxScore;
    private String grade;
    private double gradePoint;
    private int adminId;
    
    public int getId(){ return id;}
    public void setId(int id){ this.id= id;}
    
    public String getGradingSystem(){ return gradingSystem;}
    public void setGradingSystem(String gradingSystem){ this.gradingSystem= gradingSystem;}
    
    public double getMinScore(){ return minScore;}
    public void setMInScore(double minScore){ this.minScore= minScore;}
    
    public double getMaxScore(){ return maxScore;}
    public void setMaxScore(double maxScore){ this.maxScore= maxScore;}
    
    public String getGrade(){ return grade;}
    public void setGrade(String grade){ this.grade= grade;}
    
    public double getGradePoint(){ return gradePoint;}
    public void setGradePoint(double gradePoint){ this.gradePoint= gradePoint;}
    
    public int getAdminId(){ return adminId;}
    public void setAdminId(int adminId){ this.adminId= adminId;}
    
    @Override 
    public String toString(){
        return gradingSystem+ "-point|"+ grade+ "("+ minScore+ "-"+ maxScore+ ")";
    }
}
