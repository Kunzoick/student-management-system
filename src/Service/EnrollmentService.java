/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import model.Enrollment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Course;
import util.DBConnection;

/**
 *
 * @author jesse
 */
public class EnrollmentService {
   private EnrollmentDAO enrollmentDAO= new EnrollmentDAO();
   
   //enroll student
   public boolean enrollStudent(int studentId, int courseId, int adminId, String enrolledBy){
       
     try(Connection conn= DBConnection.getConnection()){
         conn.setAutoCommit(false);
         if(enrollmentDAO.isAlreadyEnrolled(studentId, courseId, adminId, conn)){
             conn.rollback();
             return false;
         }
         Enrollment enrollment= new Enrollment();
         enrollment.setStudentId(studentId);
         enrollment.setCourseId(courseId);
         enrollment.setAdminId(adminId);
         enrollment.setEnrolledBy(enrolledBy);
         
         boolean created= enrollmentDAO.createEnrollment(enrollment, conn);
         conn.commit();
         return created;
     }catch(SQLException e){
         System.err.println("Error enrolling student:"+ e.getMessage());
         return false;
     }
   }
    
    //get all enrollments for a course
   public List<Enrollment> getEnrollmentsByCourse(int courseId, int adminId){
       try{
           return enrollmentDAO.getEnrollmentsByCourse(courseId, adminId);
       }catch(Exception e){
         System.err.println("Error fetching course enrollment:"+ e.getMessage());
         return null;
     }
   }
   
   
   //get all enrollments for a student
   public List<Enrollment> getEnrollmentsByStudent(int studentId, int adminId){
       try{
           return enrollmentDAO.getEnrollmentsByStudent(studentId, adminId);
       }catch(Exception e){
         System.err.println("Error fetching student enrollment:"+ e.getMessage());
         return null;
     }
   }
   public List<Integer> getEnrollmentCourse(int studentId, int adminId){
       try{
           return enrollmentDAO.getEnrollmentCourseIdByStudent(studentId, adminId);
       }catch(Exception e){
           System.err.println("Error fetching course Ids: "+ e.getMessage());
           return null;
       }
   }
   public int CountUnitEnrolled(int studentId, int adminId){
       try{
           return enrollmentDAO.countUnitsEnrolled(studentId, adminId);
       }catch(Exception e){
           System.err.println("Error counting total units enrolled: "+ e.getMessage());
           return 0;
       }
   }
   
   //remove a student's enrollment
   public boolean removeEnrollment(int adminId, int enrollmentId){
       try{
           return enrollmentDAO.deleteEnrollment(enrollmentId, adminId);
       }catch(Exception e){
         System.err.println("Error deleting enrollment:"+ e.getMessage());
         return false;
     }
   }
   
   // get all enrollments for an admin
   public List<Enrollment> getAllEnrollments(int adminId){
       try{
           return enrollmentDAO.getAllEnrollments(adminId);
       }catch(Exception e){
         System.err.println("Error fetching all enrollments:"+ e.getMessage());
         return null;
     }
   }
   
   public int countCourseByStudent(int studentId, int adminId){
       try{
           List<Enrollment> enrollments= enrollmentDAO.getEnrollmentsByStudent(studentId, adminId);
           return enrollments != null ? enrollments.size() : 0;
       }catch(Exception e){
           System.err.println("Error counting enrollments: "+ e.getMessage());
           return 0;
       }
   }
   public int calculateTotalUnits(int studentId, int adminId){
       try{
           List<Enrollment> enrollments= enrollmentDAO.getEnrollmentsByStudent(studentId, adminId);
           if(enrollments == null || enrollments.isEmpty()) return 0;
           CourseDAO courseDAO= new CourseDAO();
           List<Course> allCourses= courseDAO.getAllCourses(adminId);
           Map<Integer, Course> courseMap= new HashMap<>();
           for(Course c : allCourses){
               courseMap.put(c.getCourseId(), c);
           }
           int totalUnits= 0;
           for(Enrollment e : enrollments){
               Course c= courseMap.get(e.getCourseId());
               if(c !=null){
                   totalUnits += c.getUnits();
               }
           }
           return totalUnits;
       }catch(Exception e){
           System.err.println("Error calculating total units: "+ e.getMessage());
           return 0;
       }
   }
   public List<Course> getCoursesForStudent(int studentId, int adminId){
       List<Course> list= new ArrayList<>();
       try{
           List<Enrollment> enrollments= enrollmentDAO.getEnrollmentsByStudent(studentId, adminId);
           if(enrollments== null || enrollments.isEmpty()) return list;
           CourseDAO courseDAO= new CourseDAO();
           List<Course> allCourses= courseDAO.getAllCourses(adminId);
           Map<Integer, Course> courseMap= new HashMap<>();
           for(Course c : allCourses){
               courseMap.put(c.getCourseId(), c);
           }
           for(Enrollment e : enrollments){
               Course c= courseMap.get(e.getCourseId());
               if(c != null) list.add(c);
           }
       }catch(Exception e){
           System.err.println("Error fetching enrolled courses: "+ e.getMessage());
       }
       return list;
   }
}
