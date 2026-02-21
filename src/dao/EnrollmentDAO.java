/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Enrollment;
import util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Date;
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
public class EnrollmentDAO {
    
    //create a new enrollment(link student > course,validated by admin)
    public boolean createEnrollment(Enrollment enrollment, Connection conn) throws SQLException {
    String sql = "INSERT INTO enrollments (studentId, courseId, adminId, enrollmentDate, enrolledBy, createdAt, updatedAt) " +
                 "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, enrollment.getStudentId());
        stmt.setInt(2, enrollment.getCourseId());
        stmt.setInt(3, enrollment.getAdminId());
        stmt.setString(5, enrollment.getEnrolledBy());

        if (enrollment.getEnrollmentDate() != null) {
            stmt.setDate(4, Date.valueOf(enrollment.getEnrollmentDate()));  // LocalDate → SQL Date
        } else {
            stmt.setNull(4, java.sql.Types.DATE);
        }

        int rowsInserted = stmt.executeUpdate();
        if (rowsInserted > 0) {
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    enrollment.setEnrollmentId(rs.getInt(1)); // capture generated PK
                }
            }
            return true;
        }
    }
    return false;
}

 //get enrolled by id
    public Enrollment getEnrollmentById(int enrollmentId){
        String sql= "SELECT * FROM enrollments WHERE enrollmentId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, enrollmentId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToEnrollment(rs);
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching enrollment by ID:"+ e.getMessage());
        }
        return null;
    }
    
    //check if student already enrolled in a course
    public boolean isAlreadyEnrolled(int studentId, int courseId, int adminId, Connection conn){
        String sql= "SELECT COUNT(*) FROM enrollments WHERE studentId= ? AND courseId= ? AND adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1) >0;
                }
            }
        }catch(SQLException e){
            System.err.println("Error checking enrollment"+ e.getMessage());
        }
        return false;
    }
    
    //get all enrollments
    public List<Enrollment> getAllEnrollments(int adminId){
        List<Enrollment> enrollments= new ArrayList<>();
        String sql= "SELECT * FROM enrollments WHERE adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }   
        }catch(SQLException e){
            System.err.println("Error fetching enrollments:"+ e.getMessage());
    }
        return enrollments;
    }
    
    //get enrollments by student
    public List<Enrollment> getEnrollmentsByStudent(int studentId, int adminId){
        List<Enrollment> enrollments= new ArrayList<>();
        String sql= "SELECT * FROM enrollments WHERE studentId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }   
        }catch(SQLException e){
            System.err.println("Error fetching enrollments:"+ e.getMessage());
    }
        return enrollments;
    }
    
     //get enrollments by scourse
    public List<Enrollment> getEnrollmentsByCourse(int courseId, int adminId){
        List<Enrollment> enrollments= new ArrayList<>();
        String sql= "SELECT * FROM enrollments WHERE courseId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, courseId);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }   
        }catch(SQLException e){
            System.err.println("Error fetching enrollments by course:"+ e.getMessage());
    }
        return enrollments;
    }
    
    //update enrollment(change course/ date)
    public boolean updateEnrollment(Enrollment enrollment){
        String sql= "UPDATE enrollments SET courseId= ?, enrollmentDate= ?, updatedAt= CURRENT_TIMESTAMP "+ "WHERE enrollmentId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            
            stmt.setInt(1, enrollment.getCourseId());
            
            if(enrollment.getEnrollmentDate() !=null){
                stmt.setDate(2, Date.valueOf(enrollment.getEnrollmentDate()));
            }else{
                stmt.setNull(2, java.sql.Types.DATE);
            }
            
            stmt.setInt(3, enrollment.getEnrollmentId());
            stmt.setInt(4, enrollment.getAdminId());
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error updating enrollment:"+ e.getMessage());
        }
        return false;
    }
    //get a list of course ids the student is already enrolled in
    public List<Integer> getEnrollmentCourseIdByStudent(int studentId, int adminId){
        List<Integer> courseIds= new ArrayList<>();
        String sql= "SELECT courseId FROM enrollments WHERE studentId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    courseIds.add(rs.getInt("courseId"));
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching enrolled course IDs: "+ e.getMessage());
        }
        return courseIds;
    }
    //count total units the student is currently enrolled in
    public int countUnitsEnrolled(int studentId, int adminId){
        int totalUnits= 0;
        String sql= "SELECT SUM(c.units) AS totalUnits " +
                "FROM enrollments e JOIN courses c ON e.courseId = c.courseId "+
                "WHERE e.studentId= ? AND e.adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt=  conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    totalUnits= rs.getInt("totalUnits");
                }
            }
        }catch(SQLException e){
            System.err.println("Error counting total units enrolled: "+ e.getMessage());
        }
        return totalUnits;
    }
    
    //delete enrollment
    public boolean deleteEnrollment(int enrollmentId, int adminId){
        String sql= "DELETE FROM enrollments WHERE enrollmentId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, enrollmentId);
            stmt.setInt(2, adminId);
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error deleting enrollment:"+ e.getMessage());
        }
        return false;
    }
    
    //delete enrollment by student
    public boolean deleteEnrollmentByStudent(int studentId, int adminId, Connection conn){
        String sql= "DELETE FROM enrollments WHERE studentId= ? AND adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, adminId);
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error deleting student enrollment:"+ e.getMessage());
        }
        return false;
    }
    
    //map result set
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException{
        Enrollment enrollment= new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollmentId"));
        enrollment.setStudentId(rs.getInt("studentId"));
        enrollment.setCourseId(rs.getInt("courseId"));
        enrollment.setAdminId(rs.getInt("adminId"));
        
        Date enrollDate= rs.getDate("enrollmentDate");
        if(enrollDate !=null) 
            enrollment.setEnrollmentDate(enrollDate.toLocalDate());
        Timestamp created= rs.getTimestamp("createdAt");
        Timestamp updated= rs.getTimestamp("updatedAt");
        if(created !=null) 
            enrollment.setCreatedAt(created.toLocalDateTime());
        if(updated !=null) 
            enrollment.setUpdatedAt(updated.toLocalDateTime());
        return enrollment;
    }
}
