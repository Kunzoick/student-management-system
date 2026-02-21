/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author jesse
 */
public class Grade {
    private int gradeId;
    private int studentId;
    private int courseId;
    private double score;
    private String grade;
    private double gradePoint;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public int getGradeId(){ return gradeId;}
    public void setGradeId(int gradeId){ this.gradeId= gradeId;}
    
    public int getStudentId(){ return studentId;}
    public void setStudentId(int studentId){ this.studentId= studentId;}
    
    public int getCourseId(){ return courseId;}
    public void setCourseId(int courseId){ this.courseId= courseId;}
    
    public double getScore(){ return score;}
    public void setScore(double score){ this.score= score;}
    
    public String getGrade(){ return grade;}
    public void setGrade(String grade){ this.grade= grade;}
    
    public double getGradePoint(){ return gradePoint;}
    public void setGradePoint(double gradePoint){ this.gradePoint= gradePoint;}
    
    public LocalDateTime getCreatedAt(){ return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt= createdAt;}
    
    public LocalDateTime getUpdatedAt(){ return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt= updatedAt;}
    
    @Override 
    public String toString(){
        return "Score: "+ score+ "|Grade:"+ grade+ "("+ gradePoint+ ")";
    }
}
