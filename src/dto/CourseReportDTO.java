/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author jesse
 */
public class CourseReportDTO {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int totalStudents;
    private double averageScore;
    private double averageGradePoint;
    
    public CourseReportDTO(int courseId, String courseCode, String courseName, int totalStudents, double averageScore, double averageGradePoint){
        this.courseId= courseId;
        this.courseName= courseName;
        this.courseCode= courseCode;
        this.totalStudents= totalStudents;
        this.averageGradePoint= averageGradePoint;
        this.averageScore= averageScore;
    }
    //getters & setters
    public int getCourseId(){ return courseId;}
    public void setCourseId(int courseId){ this.courseId= courseId;}
    
    public String getCourseCode(){ return courseCode;}
    public void setCourseCode(String courseCode){ this.courseCode= courseCode;}
    
    public String getCourseName(){ return courseName;}
    public void setCourseName(String courseName){ this.courseName= courseName;}
    
    public int getTotalStudents(){ return totalStudents;}
    public void setTotalStudents(int totalStudents){ this.totalStudents= totalStudents;}
    
    public double getAverageScore(){ return averageScore;}
    public void setAverageScore(double averageScore){ this.averageScore= averageScore;}
    
    public double getAverageGradePoint(){ return averageGradePoint;}
    public void setAverageGradePoint(double averagePoint){ this.averageGradePoint= averageGradePoint;}
    
}
