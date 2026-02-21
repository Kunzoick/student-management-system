/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Grade;
import model.GradingScale;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 * @author jesse
 */
public class GradeDAO {
    
    //--upsert
    public boolean upsertGrade(Grade grade, int adminId, Connection conn) throws SQLException{
        String sql= "SELECT gradeId FROM grades "+ "JOIN students ON grades.studentId = students.studentId "+
                "JOIN courses ON grades.courseId = courses.courseId "+ "WHERE grades.studentId = ? AND grades.courseId = ? "+
                "AND students.adminId = ? AND courses.adminId = ?";
        
        int existingId= -1;
        try(PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, grade.getStudentId());
            ps.setInt(2, grade.getCourseId());
            ps.setInt(3, adminId);
            ps.setInt(4, adminId);
            
            try(ResultSet rs= ps.executeQuery()){
                if(rs.next()){
                    existingId= rs.getInt("gradeId");
                }
            }
        }
        if(existingId >0){
            return updateGrade(existingId, grade, conn);
        }else{
            return insertGrade(grade, conn);
        }
    }
    //--wrapper--
    public boolean upsertGrade(Grade grade, int adminId){
        try(Connection conn= DBConnection.getConnection()){
            return upsertGrade(grade, adminId, conn);
        }catch(SQLException e){
            System.err.println("Error upserting grade: "+ e.getMessage());
            return false;
        }
    }
    
    //--private helpers
    private boolean updateGrade(int gradeId, Grade grade, Connection conn) throws SQLException{
        String sql= "UPDATE grades SET score = ?, grade = ?, gradePoint = ?, updatedAt = CURRENT_TIMESTAMP "+
                "WHERE gradeId = ?";
        try(PreparedStatement ps= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setDouble(1, grade.getScore());
            ps.setString(2, grade.getGrade());
            ps.setDouble(3, grade.getGradePoint());
            ps.setInt(4, gradeId);
            return ps.executeUpdate() >0;
        }
    }
   
    private boolean insertGrade(Grade grade, Connection conn) throws SQLException{
        String sql= "INSERT INTO grades (studentId, courseId, score, grade, gradePoint, createdAt, updatedAt) "+
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try(PreparedStatement ps= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, grade.getStudentId());
            ps.setInt(2, grade.getCourseId());
            ps.setDouble(3, grade.getScore());
            ps.setString(4, grade.getGrade());
            ps.setDouble(5, grade.getGradePoint());
            
            int affected= ps.executeUpdate();
            if(affected >0){
                try(ResultSet rs= ps.getGeneratedKeys()){
                    if(rs.next()){
                        grade.setGradeId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Delete grade by ID (admin scoped).
     */
    public boolean deleteGrade(int gradeId, int adminId) {
        // MySQL supports DELETE ... FROM ... JOIN ...
        String sql = "DELETE grades FROM grades " +
                     "JOIN students ON grades.studentId = students.studentId " +
                     "JOIN courses ON grades.courseId = courses.courseId " +
                     "WHERE grades.gradeId = ? AND students.adminId = ? AND courses.adminId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gradeId);
            ps.setInt(2, adminId); 
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting grade: " + e.getMessage());
            return false;
        }
    }
    
    //delete for student
     public boolean deleteGradeByStudent(int studentId, int adminId, Connection conn) {
        // MySQL supports DELETE ... FROM ... JOIN ...
        String sql = "DELETE grades FROM grades " +
                     "JOIN students ON grades.studentId = students.studentId " +
                     "JOIN courses ON grades.courseId = courses.courseId " +
                     "WHERE grades.studentId = ? AND students.adminId = ? AND courses.adminId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, adminId); 
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student grade: " + e.getMessage());
            return false;
        }
    }

    
     // Get all grades for a student (admin scoped). 
    public List<Grade> getGradesByStudent(int studentId, int adminId, Connection conn) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT grades.* FROM grades " +
                     "JOIN students ON grades.studentId = students.studentId " +
                     "WHERE grades.studentId = ? AND students.adminId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching grades by student: " + e.getMessage());
        }
        return grades;
    }
    public boolean hasGradeForStudentCourse(int studentId, int courseId, int adminId){
        String sql= "SELECT COUNT(*) FROM grades g " +
            "JOIN students s ON g.studentId = s.studentId " +
            "JOIN courses c ON g.courseId = c.courseId " +
            "WHERE g.studentId= ? AND g.courseId= ? " +
            "AND s.adminId= ? AND c.adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, adminId);
            stmt.setInt(4, adminId);
            ResultSet rs= stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1) >0;
            }
        }catch(SQLException e){
            System.err.println("Error checking grade existence: "+ e.getMessage());            
        }
        return false;
    }

    /**
     * Get all grades for a course (admin scoped).
     */
    public List<Grade> getGradesByCourse(int courseId, int adminId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT grades.* FROM grades " +
                     "JOIN courses ON grades.courseId = courses.courseId " +
                     "WHERE grades.courseId = ? AND courses.adminId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching grades by course: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Get specific grade for a student in a course (admin scoped).
     */
    public Grade getGrade(int studentId, int courseId, int adminId) {
        String sql = "SELECT grades.* FROM grades " + 
                     "JOIN students ON grades.studentId = students.studentId " + 
                     "JOIN courses ON grades.courseId = courses.courseId " + 
                     "WHERE grades.studentId = ? AND grades.courseId = ? " + 
                     "AND students.adminId = ? AND courses.adminId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, adminId);
            stmt.setInt(4, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching grade: " + e.getMessage());
        }
        return null;
    }
    
    //calculate average GPA across all students under an admin
  public double calculateAverageGPAForAdmin(int adminId) {
    String sql = "SELECT AVG(studentGPA.gpa) " +
                 "FROM ( " +
                 "  SELECT students.studentId, " +
                 "       SUM(grades.gradePoint * courses.units) / SUM(courses.units) AS gpa " +
                 "  FROM grades " +
                 "  JOIN students ON grades.studentId = students.studentId " +
                 "  JOIN courses ON grades.courseId = courses.courseId " +
                 "  WHERE students.adminId = ? AND courses.adminId = ? " +
                 "  GROUP BY students.studentId " +
                 ") AS studentGPA";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, adminId);
        stmt.setInt(2, adminId);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1); // directly return the first column
            }
        }
    } catch (SQLException e) {
        System.err.println("Error calculating average GPA for admin: " + e.getMessage());
    }
    return 0.0;
               
    }

    /**
     * Helper: Map DB row → Grade object (null-safe timestamps)
     */
    private Grade mapResultSet(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("gradeId"));
        grade.setStudentId(rs.getInt("studentId"));
        grade.setCourseId(rs.getInt("courseId"));
        grade.setScore(rs.getDouble("score"));
        grade.setGrade(rs.getString("grade"));
        grade.setGradePoint(rs.getDouble("gradePoint"));

        Timestamp createdTs = rs.getTimestamp("createdAt");
        if (createdTs != null) grade.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updatedAt");
        if (updatedTs != null) grade.setUpdatedAt(updatedTs.toLocalDateTime());

        return grade;
    }
}
