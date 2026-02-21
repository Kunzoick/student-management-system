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
public class TranscriptArchive {
    private int id;
    private int studentId;
    private int adminId;// owner of the student
    private Integer archivedAdminId;// null if system archives it 
    private String firstName;
    private String archivedType;// system or admin
    private String lastName;
    private String gender;
    private String department;
    private LocalDateTime enrollmentDate; 
    private LocalDateTime archivedAt;
    private double gpa;
    private String transcriptJson;
    
    //getters & setters
    public int getId(){ return id;}
    public void setId(int id){ this.id= id;}
    
    public int getStudentId(){ return studentId;}
    public void setStudentId(int studentId){ this.studentId= studentId;}
    
    public int getAdminId(){ return adminId;}
    public void setAdminId(int adminId){ this.adminId= adminId;}
    
    public Integer getArchivedAdminId(){ return archivedAdminId;}
    public void setArchivedAdminId(Integer archivedAdminId){ this.archivedAdminId= archivedAdminId;}
    
    public String getFirstName(){ return firstName;}
    public void setFirstName(String firstName){ this.firstName= firstName;}
    
    public String getLastName(){ return lastName;}
    public void setLastName(String lastName){ this.lastName= lastName;}
    
    public String getGender(){ return gender;}
    public void setGender(String gender){ this.gender= gender;}
    
    public String getDepartment(){ return department;}
    public void setDepartment(String department){ this.department= department;}
    
    public LocalDateTime getEnrollmentDate(){ return enrollmentDate;}
    public void setEnrollmentDate(LocalDateTime enrollmentDate){ this.enrollmentDate= enrollmentDate;}
    
    public String getArchivedType(){ return archivedType;}
    public void setArchivedType(String archivedType){ this.archivedType= archivedType;}
    
    public LocalDateTime getArchivedAt(){ return archivedAt;}
    public void setArchivedAt(LocalDateTime archivedAt){ this.archivedAt= archivedAt;}
    
    public double getGPA(){ return gpa;}
    public void setGPA(double gpa){ this.gpa= gpa;}
    
    public String getTranscriptJson(){ return transcriptJson;}
    public void setTranscriptJson(String transcriptJson){ this.transcriptJson= transcriptJson;}
    
    @Override
    public String toString(){
        return "TranscriptArchive("+
                "id="+ id+ ", studentId="+ studentId+ ", adminId="+ adminId+ "' gpa="+ gpa+ ", archivedAt="+ archivedAt+ ", archivedBy"+ archivedType+ ")";
    }
}
