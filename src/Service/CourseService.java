/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.CourseDAO;
import java.util.List;
import model.Course;
import util.DBConnection;
import java.sql.Connection;

/**
 *
 * @author jesse
 */
public class CourseService {
    private final CourseDAO courseDAO;
    public CourseService(){
        this.courseDAO= new CourseDAO();
        }
    
    //create a new coourse
    public boolean createCourse(Course course){
        try{
            return courseDAO.createCourse(course);
        }catch(Exception e){
            System.err.println("Error creating course:"+ e.getMessage());
            return false;
        }
    }
    
    //get course by id
    public Course getCourseById(int courseId, int adminId){
        try{
            return courseDAO.getCourseById(courseId, adminId);
        }catch(Exception e){
            System.err.println("Error fetching course:"+ e.getMessage());
            return null;
        }
    }
    
    //get course by code
    public Course getCourseByCode(String courseCode, int adminId){
        try{
            return courseDAO.getCourseByCode(courseCode, adminId);
        }catch(Exception e){
            System.err.println("Error fetching course:"+ e.getMessage());
            return null;
        }
    }
    
    //search course
public List<Course> searchCourses(int adminId, String Keyword){
    try{
    return courseDAO.searchCourseByAdmin(adminId, Keyword);
    }catch(Exception e){
        System.err.println("Error searching course"+ e.getMessage());
        return null;
    }
}
    
    //get all courses for a specific admin
    public List<Course> getCourseByAdminId(int adminId){
        try{
            return courseDAO.getAllCourses(adminId);
        }catch(Exception e){
            System.err.println("Error fetching courses for admin:"+ e.getMessage());
            return  null;
        }
    }
    
    public int countCoursezByAdmin(int adminId){
        return courseDAO.countCoursesByAdmin(adminId);
    }
    
    //update course
    public void updateCourse(Course course){
        try{
            courseDAO.updateCourse(course);
        }catch(Exception e){
            System.err.println("Error updating course:"+ e.getMessage());
        }
    }
    
    //delete course
    public boolean deleteCourse(int courseId, int adminId){
        try(Connection conn= DBConnection.getConnection()){
            return courseDAO.deleteCourse(courseId, adminId, conn);
        }catch(Exception e){
            System.err.println("Error deleting course:"+ e.getMessage());
            return false;
        }
    }
}
