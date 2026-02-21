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
public class Course {
    private int courseId;
    private int adminId;
    private String courseCode;
    private String courseName;
    private String description;
    private int units;
    private String gradingSystem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    //getter & setters
    public int getCourseId(){ return courseId;}
    public void setCourseId(int courseId){ this.courseId= courseId;}
    
    public int getAdminId(){ return adminId;}
    public void setAdminId(int adminId){ this.adminId= adminId;}
    
    public String getCourseCode(){ return courseCode;}
    public void setCourseCode(String courseCode){ this.courseCode= courseCode;}
    
    public String getCourseName(){ return courseName;}
    public void setCourseName(String courseName){ this.courseName= courseName;}
    
    public String getDescription(){ return description;}
    public void setDescription(String description){ this.description= description;}
    
    public int getUnits(){ return units;}
    public void setUnits(int units){ this.units= units;}
    
    public String getGradingSystem(){ return gradingSystem;}
    public void setGradingSystem(String gradingSystem){ this.gradingSystem= gradingSystem;}
    
    public LocalDateTime getCreatedAt(){ return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt= createdAt;}
    
    public LocalDateTime getUpdatedAt(){ return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt= updatedAt;}
    
    @Override
    public String toString(){
        return courseCode+ "-"+ courseName+ "("+ units+ "units)";
    }
}
