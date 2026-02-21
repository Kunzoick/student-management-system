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
public class Student {
    private int studentId;
    private int userId;
    private int adminId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private String department;
    private LocalDate enrollmentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    
    //getter & setters
    public int getStudentId(){ return studentId;}
    public void setStudentId(int studentId){ this.studentId= studentId;}
    
    public int getUserId(){ return userId;}
    public void setUserId(int userId){ this.userId= userId;}
    
    public int getAdminId(){ return adminId;}
    public void setAdminId(int adminId){ this.adminId= adminId;}
    
    public String getFirstName(){ return firstName;}
    public void setFirstName(String firstName){ this.firstName= firstName;}
    
    public String getLastName(){ return lastName;}
    public void setLastName(String lastName){ this.lastName= lastName;}
    
    public String getGender(){ return gender;}
    public void setGender(String gender){ this.gender= gender;}
    
    public LocalDate getDOB(){ return dob;}
    public void setDOB(LocalDate dob){ this.dob= dob;}
    
    public String getDepartment(){ return department;}
    public void setDepartment(String department){ this.department= department;}
    
    public LocalDate getEnrollmentDate(){ return enrollmentDate;}
    public void setEnrollmentDate(LocalDate enrollmentDate){ this.enrollmentDate= enrollmentDate;}
    
    public LocalDateTime getCreatedAt(){ return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt= createdAt;}
    
    public LocalDateTime getUpdatedAt(){ return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt= updatedAt;}
    
    public String getStatus(){ return status;}
    public void setStatus(String status){ this.status= status;}
    
    @Override
    public String toString(){
        return firstName+ ""+ lastName+ "(ID"+ studentId + ")";
    }
}
