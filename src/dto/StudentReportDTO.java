/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author jesse
 */
public class StudentReportDTO {
    private int studentId;
    private String firstName;
    private String lastName;
    private String gender;
    private String department;
    private int courseId;
    private String courseCode;
    private String courseName;
    private double score;
    private String grade;
    private double gradePoint;
    private int Units;
    
    public StudentReportDTO(int studentId, String firstName, String lastName, String gender, String department, int courseId, String courseCode, String courseName, double score, String grade, double gradePoint, int Units){
        this.studentId= studentId;
        this.score= score;
        this.courseCode= courseCode;
        this.courseId= courseId;
        this.department= department;
        this.courseName= courseName;
        this.firstName= firstName;
        this.lastName= lastName;
        this.grade= grade;
        this.gradePoint= gradePoint;
        this.gender= gender;
        this.Units= Units;
    }
    
    //Getters & setters
    public int getStudentId(){ return studentId;}
    public void setStudentId(int studentId){ this.studentId= studentId;}
    
    public String getFIrstName(){ return firstName;}
    public void setFirstName(String firstName){this.firstName= firstName;}
    
    public String getLastName(){ return lastName;}
    public void setLastName(String lastName){ this.lastName= lastName;}
    
    public String getGender(){ return gender;}
    public void setGender(String gender){ this.gender= gender;}
    
    public String getDepartment(){ return department;}
    public void setDepartment(String department){ this.department= department;}
    
    public int getCourseId(){ return courseId;}
    public void setCourseId(int courseId){ this.courseId= courseId;}
    
    public String getCourseName(){ return courseName;}
    public void setCourseName(String courseName){ this.courseName= courseName;}
    
    public String getCourseCode(){ return courseCode;}
    public void setCourseCode(String courseCode){ this.courseCode= courseCode;}
    
    public double getScore(){ return score;}
    public void setScore(double score){ this.score= score;}
    
    public String getGrade(){ return grade;}
    public void setGrade(String grade){this.grade= grade;}
    
    public double getGradePoint(){ return gradePoint;}
    public void setGradePoint(double gradePoint){ this.gradePoint= gradePoint;}
    
    public int getUnits(){ return Units;}
    public void setUnits(int Units){ this.Units= Units;}
}
