/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author jesse
 */
public class StudentTranscriptDTO {
    private int studentId;
    private String firstName;
    private String lastName;
    private String department;
    private String gender;
    private LocalDateTime enrollmentDate;
    private double gpa;
    private List<StudentReportDTO> grades;
    
    public StudentTranscriptDTO(int studentId, String firstName, String lastName, String department, String gender, LocalDateTime enrollmentDate, double gpa, List<StudentReportDTO> grades){
        this.studentId= studentId;
        this.lastName= lastName;
        this.firstName= firstName;
        this.department= department;
        this.gender= gender;
        this.enrollmentDate= enrollmentDate;
        this.gpa= gpa;
        this.grades= grades;
    }
    //getters & setters
    public int getStudentId(){ return studentId;}
    public void setStudentId(int studentId){ this.studentId= studentId;}
    
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
    
    public double getGpa(){ return gpa;}
    public void setGpa(double gpa){ this.gpa= gpa;}
    
    public List<StudentReportDTO> getGrades(){ return grades;}
    public void setGrades(List<StudentReportDTO> grades){ this.grades= grades;}
}
