/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Student;
import util.DBConnection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jesse
 */
public class StudentDAO {
    
    //register Stuent
    public boolean registerStudent(Student student, Connection conn){
        String sql= "INSERT INTO students(userId, adminId, firstName, lastName, gender, dob, department, enrollmentDate, status, createdAt, updatedAt)"+ "VALUES(?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";
        
        try(PreparedStatement stmt= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, student.getUserId());
            stmt.setInt(2, student.getAdminId());
            stmt.setString(3, student.getFirstName());
            stmt.setString(4, student.getLastName());
            stmt.setString(5, student.getGender());
            stmt.setDate(6, student.getDOB() !=null ?
                    Date.valueOf(student.getDOB()): null);
            stmt.setString(7, student.getDepartment());
            stmt.setDate(8, student.getEnrollmentDate() !=null ?
                    Date.valueOf(student.getEnrollmentDate()): null);
            stmt.setString(9, student.getStatus() !=null ?
                    student.getStatus() : "active");
            
            int rowsInserted= stmt.executeUpdate();
            if(rowsInserted >0){
                try(ResultSet rs= stmt.getGeneratedKeys()){
                    if(rs.next()){
                        student.setStudentId(rs.getInt(1));//set generated studentId
                    }
                }
                return true;
            }
            
        }catch(SQLException e){
            System.err.println("Error registering student: "+ e.getMessage());
            return false;
        }
        return false;
    }
    
    //get student by id
    public Student getStudentById(int studentId, int adminId, Connection conn){
        String sql= "SELECT * FROM students WHERE studentId= ? AND adminId= ?";
        Student student= null;
        
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId);
            stmt.setInt(2, adminId);
           try(ResultSet rs= stmt.executeQuery()){
            
            if(rs.next()){
                student= mapResultSetToStudent(rs);
            }
           }
        }catch(SQLException e){
         System.err.println("Error fetching student: "+ e.getMessage());  
        }
        return student;
    }
    
    //get student by userId
    public Student getStudentByUserId(int userId){
        String sql= "SELECT * FROM students WHERE userId= ?";
        Student student= null;
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, userId);
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                student= mapResultSetToStudent(rs);
            }
            }
        }catch(SQLException e){
            System.err.println("Error fetching student by userId"+ e.getMessage());
        }
        return student;
    }
    
    //get all students(admin view)
    public List<Student> getAllStudents(int adminId){
        List<Student> students= new ArrayList<>();
        String sql= "SELECT * FROM students WHERE adminId= ?";
        
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
                stmt.setInt(1, adminId);
               try(ResultSet rs= stmt.executeQuery()){
            while(rs.next()){
                students.add(mapResultSetToStudent(rs));
            }
                }
        }catch(SQLException e){
         System.err.println("Error fetching students: "+ e.getMessage());  
        }
        return students;
    }
    
    //Update student(Admin use)
    public boolean updateStudent(Student student){
        String sql= "UPDATE students SET firstName=?, lastName=?, gender=?, dob=?, department=?, enrollmentDate=?, updatedAt=CURRENT_TIMESTAMP WHERE studentId=? AND adminId=?";
        
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getGender());
            stmt.setDate(4, student.getDOB() !=null ?
                    Date.valueOf(student.getDOB()): null);
            stmt.setString(5, student.getDepartment());
            stmt.setDate(6, student.getEnrollmentDate() !=null ?
                    Date.valueOf(student.getEnrollmentDate()): null);
            stmt.setInt(7, student.getStudentId());
            stmt.setInt(8,student.getAdminId());// only updates your own students
            
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
         System.err.println("Error updating student: "+ e.getMessage());
         return false;
        }
    }
    
    //update status
    public boolean updateStatus(int studentId, String newStatus, int adminId){
        String sql= "UPDATE students SET status= ? WHERE studentId= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, newStatus);
            stmt.setInt(2, studentId);
            stmt.setInt(3, adminId);
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error updating status:"+ e.getMessage());
            return false;
        }
    }
    //Auto archive
    public List<Student> getStudentForAutoArchive(int adminId, Connection conn) throws SQLException{
        String sql= "SELECT * FROM students WHERE adminId= ? AND TIMESTAMPDIFF(YEAR, enrollmentDate, NOW()) >=6";
        List<Student> students= new ArrayList<>();
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }
    
    //delete students(Admin use)
    public boolean deleteStudent(int studentId, int adminId, Connection conn){
        String sql= "DELETE FROM students WHERE studentId=? AND adminId=?";
        
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, studentId); 
            stmt.setInt(2, adminId);return stmt.executeUpdate() >0;
        }catch(SQLException e){
         System.err.println("Error deleting students: "+ e.getMessage());
         return false;
        }
    }
    
    //count all students for an admin
    public int CountStudentByAdmin(int adminId){
        String sql= "SELECT COUNT(*) FROM students WHERE adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return rs.getInt(1);
            }
            }
        }catch(SQLException e){
            System.err.println("Error counting students"+ e.getMessage());
        }
        return 0;
    }
    
    //count only active students
    public int countActiveStudentsByAdmin(int adminId){
        String sql= "SELECT COUNT(*) FROM students WHERE adminId= ? AND status= 'active'";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
           try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return rs.getInt(1);
            }
           }
        }catch(SQLException e){
            System.err.println("Error counting active students"+ e.getMessage());
        }
        return 0;
    }
    
    //search student
    public List<Student> searchStudentByAdmin(int adminId, String Keyword){
        List<Student> list= new ArrayList<>();
        String sql= "SELECT * FROM students WHERE adminId= ? AND (firstName LIKE ? OR lastName LIKE ? OR "
                + "department LIKE ? OR studentId LIKE ?)";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            String pattern= "%" + Keyword + "%";
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);
            try(ResultSet rs= stmt.executeQuery()){
            
            while(rs.next()){
                list.add(mapResultSetToStudent(rs));
            }
            }
        }catch(SQLException e){
            System.err.println("Error searching Student"+ e.getMessage());
        }
        return list;
    }
    
    //map resultset= student object
    private Student mapResultSetToStudent(ResultSet rs)throws SQLException{
        Student student= new Student();
        student.setStudentId(rs.getInt("studentId"));
        student.setUserId(rs.getInt("userId"));
        student.setAdminId(rs.getInt("adminId"));
        student.setFirstName(rs.getString("firstName"));
        student.setLastName(rs.getString("lastName"));
        student.setGender(rs.getString("gender"));
        Date dob= rs.getDate("dob"); if(dob !=null)
            student.setDOB(dob.toLocalDate());
        student.setDepartment(rs.getString("department"));
        Date enrollment= rs.getDate("enrollmentDate"); if(enrollment !=null)
            student.setEnrollmentDate(enrollment.toLocalDate());
        student.setStatus(rs.getString("status"));
        
         Timestamp created= rs.getTimestamp("createdAt");
        Timestamp updated= rs.getTimestamp("updatedAt");
        if(created != null)
            student.setCreatedAt(created.toLocalDateTime());
        if(updated != null)
            student.setUpdatedAt(updated.toLocalDateTime());
        return student;
    }
}
