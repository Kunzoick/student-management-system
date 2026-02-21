/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Statement;
import model.Course;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

/**
 *
 * @author jesse
 */
public class CourseDAO {
    //create a course
    public boolean createCourse(Course course){
        String sql= "INSERT INTO courses(adminId, courseCode, courseName, description, units, gradingSystem, createdAt, updatedAt) "+ "VALUES(?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, course.getAdminId());
            stmt.setString(2, course.getCourseCode());
            stmt.setString(3, course.getCourseName());
            stmt.setString(4, course.getDescription());
            stmt.setInt(5, course.getUnits());
            stmt.setString(6, course.getGradingSystem());
            
            int rowsInserted= stmt.executeUpdate();
            if(rowsInserted >0){
                try(ResultSet rs= stmt.getGeneratedKeys()){
                    if(rs.next()){
                        course.setCourseId(rs.getInt(1));//set generated courseId
                    }
                }
                return true;
            }
        }catch(SQLException e){
            System.err.println("Error creating course:"+ e.getMessage());
        }
        return false;
    }
    
    //get course by id
    public Course getCourseById(int courseId, int adminId){
        String sql= "SELECT * FROM courses WHERE courseId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, courseId);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToCourse(rs);
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching course by id:"+ e.getMessage());
        }
        return null;
    }
    
    //get course by code
     public Course getCourseByCode(String courseCode, int adminId){
        String sql= "SELECT * FROM courses WHERE courseCode= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, courseCode);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToCourse(rs);
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching course by code:"+ e.getMessage());
        }
        return null;
    }
    
    //get all course form the adminID
    public List<Course> getAllCourses(int adminId){
        List<Course> courses= new ArrayList<>();
        String sql= "SELECT * FROM courses WHERE adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
                try(ResultSet rs= stmt.executeQuery()){
            
            while(rs.next()){
                courses.add(mapResultSetToCourse(rs));
            }
                }
        }catch(SQLException e){
            System.err.println("Error fetching courses:"+ e.getMessage());
        }
        return courses;
    }
    
    //update course
    public boolean updateCourse(Course course){
        String sql= "UPDATE courses SET courseCode=?, courseName=?, description=?, units=?, gradingSystem=?, updatedAt=CURRENT_TIMESTAMP WHERE courseId=?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getUnits());
            stmt.setString(5, course.getGradingSystem());
            stmt.setInt(6, course.getCourseId());
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error Updating course:"+ e.getMessage());
        }
        return false;
    }
    
    //delete courses
    public boolean deleteCourse(int courseId, int adminId, Connection conn){
        String sql= "DELETE FROM courses WHERE courseId= ? AND adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, courseId);
            stmt.setInt(2, adminId);
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error delecting courses:"+ e.getMessage());
        }
        return false;
    }
    
    //count course by admin
    public int countCoursesByAdmin(int adminId){
        String sql= "SELECT COUNT(*) FROM courses WHERE adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return rs.getInt(1);
            }
            }
        }catch(SQLException e){
            System.err.println("Error counting courses"+ e.getMessage());
        }
        return 0;
    }
    
    //search course
    public List<Course> searchCourseByAdmin(int adminId, String Keyword){
        List<Course> list= new ArrayList<>();
        String sql= "SELECT * FROM courses WHERE adminId= ? AND "+ "(courseCode LIKE ? OR courseName LIKE ?)";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            String pattern= "%" + Keyword + "%";
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try(ResultSet rs= stmt.executeQuery()){
            
            while(rs.next()){
                list.add(mapResultSetToCourse(rs));
            }
            }
        }catch(SQLException e){
            System.err.println("Error searching Course"+ e.getMessage());
        }
        return list;
    }
    
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException{
        Course course= new Course();
        course.setCourseId(rs.getInt("courseId"));
        course.setAdminId(rs.getInt("adminId"));
        course.setCourseCode(rs.getString("courseCode"));
        course.setCourseName(rs.getString("courseName"));
        course.setDescription(rs.getString("description"));
        course.setUnits(rs.getInt("units"));
        course.setGradingSystem(rs.getString("gradingSystem"));
        Timestamp created= rs.getTimestamp("createdAt");
        Timestamp updated= rs.getTimestamp("updatedAt");
         if(created != null)
            course.setCreatedAt(created.toLocalDateTime());
        if(updated != null)
            course.setUpdatedAt(updated.toLocalDateTime());
        return course;
        
    }
    
}
