/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.CourseDAO;
import dao.GradeDAO;
import dao.GradingScaleDAO;
import java.util.List;
import model.Grade;
import model.GradingScale;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import model.Course;
import util.DBConnection;
import java.sql.SQLException;
/**
 *
 * @author jesse
 */
public class GradeService {
    private GradeDAO gradeDAO= new GradeDAO();
    private GradingScaleDAO gradingScaleDAO= new GradingScaleDAO();
    
    //assign or update a grade for a student
    public boolean assignGrade(int studentId, int courseId, int adminId, double score, String gradingSystem){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
        try{
            GradingScale scale= gradingScaleDAO.findScaleForScore(adminId, gradingSystem, score);
            if(scale== null){
                System.err.println("No grading scale found for score: "+ score);
                return false;
            }
            Grade grade= new Grade();
            grade.setStudentId(studentId);
            grade.setCourseId(courseId);
            grade.setScore(score);
            grade.setGrade(scale.getGrade());
            grade.setGradePoint(scale.getGradePoint());
            
            boolean success= gradeDAO.upsertGrade(grade, adminId, conn);
            if(success){
                conn.commit();
                return true;
            }else{
                conn.rollback();
                return false;
            }
        }catch(Exception e){
            try{
                conn.rollback();
            } catch(SQLException rollbackEx){
                // Log rollback failure but don't throw
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Error assigning grade: " + e.getMessage());
            return false;
            
        } finally{
            try{
                conn.setAutoCommit(true);
            } catch(SQLException ex){
                // Log but don't throw - connection is closing anyway
                System.err.println("Failed to reset autoCommit: " + ex.getMessage());
            }
        }
        
    } catch(SQLException e){
        System.err.println("Database connection error: " + e.getMessage());
        return false;
    }
    }
    
    //fetch single grade
    public Grade getGrade(int studentId, int courseId, int adminId){
        return gradeDAO.getGrade(studentId, courseId, adminId);
    }
    
   //all grade for student
    public List<Grade> getGradeByStudent(int studentId, int adminId){
        try(Connection conn= DBConnection.getConnection()){
        return gradeDAO.getGradesByStudent(studentId, adminId, conn);
    }catch(Exception e){
        System.err.println("Error fetching grades by student: "+ e.getMessage());
        return Collections.emptyList();
    }
    }
    
    //all grade for course
    public List<Grade> getGradeByCourse(int courseId, int adminId){
        return gradeDAO.getGradesByCourse(courseId, adminId);
    }
    
    public double calculateAverageGpa(int adminId){
        return gradeDAO.calculateAverageGPAForAdmin(adminId);
    }
    
    //delete by gradeId
    public boolean deleteGrade(int gradeId, int adminId){
        return gradeDAO.deleteGrade(gradeId, adminId);
    }
    
    public boolean hasGrade(int studentId, int courseId, int adminId){
        try{
            return gradeDAO.hasGradeForStudentCourse(studentId, courseId, adminId);
        }catch(Exception e){
            System.err.println("Error checking grade: "+ e.getMessage());
            return false;
        }
    }
    
    //gpa calculation
    public double computeGPA(int studentId, int adminId, Connection conn){
        List<Grade> grades= gradeDAO.getGradesByStudent(studentId, adminId, conn);
        if(grades== null || grades.isEmpty()) return 0.0;
        
        CourseDAO courseDAO= new CourseDAO();
        List<Course> allCourses= courseDAO.getAllCourses(adminId);
        Map<Integer, Course> courseMap= new HashMap<>();
        for(Course c : allCourses){
            courseMap.put(c.getCourseId(), c);
        }
        double totalPoints= 0.0;
        int totalUnits= 0;
        for(Grade g : grades){
            Course course= courseMap.get(g.getCourseId());
            if(course== null){
                System.err.println("Warning: Course not found for ID: "+ g.getCourseId());
                continue;
            }
            int units= course.getUnits();
            totalPoints += g.getGradePoint() * units;
            totalUnits += units;
        }
        return totalUnits > 0 ? totalPoints / totalUnits : 0.0;
    }
    public double calculateGPA(int studentId, int adminId){
        try(Connection conn= DBConnection.getConnection()){
            return computeGPA(studentId, adminId, conn);
        }catch(Exception e){
            System.err.println("Error calculating GPA"+ e.getMessage());
            return 0.0;
        }
    }
}
