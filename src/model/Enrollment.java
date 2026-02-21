/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author jesse
 */
public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int courseId;
    private int adminId;
    private LocalDate enrollmentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String enrolledBy;
    
    //getters & setters
    public int getEnrollmentId(){ return enrollmentId;}
    public void setEnrollmentId(int enrollmentId){ this.enrollmentId= enrollmentId;}
    
    public int getStudentId(){ return studentId;}
    public void setStudentId(int studentId){ this.studentId= studentId;}
    
    public int getCourseId(){ return courseId;}
    public void setCourseId(int courseId){ this.courseId= courseId;}
    
    public int getAdminId(){ return adminId;}
    public void setAdminId(int adminId){ this.adminId= adminId;}
    
    public LocalDate getEnrollmentDate(){ return enrollmentDate;}
    public void setEnrollmentDate(LocalDate enrollmentDate){ this.enrollmentDate= enrollmentDate;}
    
    public LocalDateTime getCreatedAt(){ return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt= createdAt;}
    
    public LocalDateTime getUpdatedAt(){ return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt= updatedAt;}
    
    public String getEnrolledBy(){ return enrolledBy;}
    public void setEnrolledBy(String enrolledBy){ this.enrolledBy= enrolledBy;}
    
     @Override
    public String toString(){
        return "Enrollment{"+ "enrollmentId="+ enrollmentId+ ", studentId="+ studentId+ ", courseId="+ courseId+ ", adminId="+ adminId+ ", enrollmentDate="+ enrollmentDate+ ", createdAt="+ createdAt+ ", updatedAt="+ updatedAt+
                "}";
    }  
}
