/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import model.TranscriptArchive;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import util.DBConnection;

/**
 *
 * @author jesse
 */
public class TranscriptArchivedDAO {
    
    //archive student(System/admin)
    public boolean archiveStudent(TranscriptArchive archive, Connection conn) throws SQLException{
        String sql= "INSERT INTO studenttranscript" + " (studentId, adminId, archivedAdminId, firstName, lastName, department, gender, archivedType, enrollmentDate, gpa, transcriptJson)" +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, archive.getStudentId());
            stmt.setInt(2, archive.getAdminId());
            if(archive.getArchivedAdminId() !=null){
                stmt.setInt(3, archive.getArchivedAdminId());
            }else{
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, archive.getFirstName());
            stmt.setString(5, archive.getLastName());
            stmt.setString(6, archive.getDepartment());
            stmt.setString(7, archive.getGender());
            stmt.setString(8, archive.getArchivedType());
            stmt.setTimestamp(9, Timestamp.valueOf(archive.getEnrollmentDate()));
            stmt.setDouble(10, archive.getGPA());
            stmt.setString(11, archive.getTranscriptJson());
            return stmt.executeUpdate() >0;
        }
    }
    
    //retrieve transcript but scoped by adminID
    public List<TranscriptArchive> getArchivedStudentByAdmin(int adminId, Connection conn) throws SQLException{
        String sql= "SELECT * FROM studenttranscript WHERE adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            try(ResultSet rs= stmt.executeQuery()){
            
            List<TranscriptArchive> archives= new ArrayList<>();
            while(rs.next()){
                TranscriptArchive ta= new TranscriptArchive();
                ta.setId(rs.getInt("id"));
                ta.setStudentId(rs.getInt("studentId"));
                ta.setAdminId(rs.getInt("adminId"));
                ta.setArchivedAdminId(rs.getInt("archivedAdminId"));
                ta.setFirstName(rs.getString("firstName"));
                ta.setLastName(rs.getString("lastName"));
                ta.setArchivedType(rs.getString("archivedType"));
                ta.setGender(rs.getString("gender"));
                ta.setDepartment(rs.getString("department"));
                ta.setEnrollmentDate(rs.getTimestamp("enrollmentDate").toLocalDateTime());
                ta.setArchivedAt(rs.getTimestamp("archivedAt").toLocalDateTime());
                ta.setGPA(rs.getDouble("gpa"));
                ta.setTranscriptJson(rs.getString("transcriptJson"));
                archives.add(ta);
            }
            return archives;
        }
        }
    }
    //search student
    public List<TranscriptArchive> searchArchivedByAdmin(int adminId, String Keyword, Connection conn){
        List<TranscriptArchive> list= new ArrayList<>();
        String sql= "SELECT * FROM studenttranscript WHERE adminId= ? AND "+ "(firstName LIKE ? OR lastName LIKE ? OR "
                + "department LIKE ? OR studentId LIKE ?)";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            String pattern= "%" + Keyword + "%";
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);
            try(ResultSet rs= stmt.executeQuery()){
            
            while(rs.next()){
                TranscriptArchive ta= new TranscriptArchive();
                ta.setId(rs.getInt("id"));
                ta.setStudentId(rs.getInt("studentId"));
                ta.setAdminId(rs.getInt("adminId"));
                ta.setArchivedAdminId(rs.getInt("archivedAdminId"));
                ta.setFirstName(rs.getString("firstName"));
                ta.setLastName(rs.getString("lastName"));
                ta.setArchivedType(rs.getString("archivedType"));
                ta.setGender(rs.getString("gender"));
                ta.setDepartment(rs.getString("department"));
                ta.setEnrollmentDate(rs.getTimestamp("enrollmentDate").toLocalDateTime());
                ta.setArchivedAt(rs.getTimestamp("archivedAt").toLocalDateTime());
                ta.setGPA(rs.getDouble("gpa"));
                ta.setTranscriptJson(rs.getString("transcriptJson"));
                list.add(ta);
            }
            }
        }catch(SQLException e){
            System.err.println("Error searching Archived"+ e.getMessage());
        }
        return list;
    }
    public TranscriptArchive getArchiveStudentById(int adminId, int id, Connection conn) throws SQLException{
        String sql= "SELECT * FROM studenttranscript WHERE adminId= ? AND id= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            stmt.setInt(2, id);
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                TranscriptArchive ta= new TranscriptArchive();
                ta.setId(rs.getInt("id"));
                ta.setStudentId(rs.getInt("studentId"));
                ta.setAdminId(rs.getInt("adminId"));
                ta.setArchivedAdminId(rs.getInt("archivedAdminId"));
                ta.setFirstName(rs.getString("firstName"));
                ta.setLastName(rs.getString("lastName"));
                ta.setArchivedType(rs.getString("archivedType"));
                ta.setGender(rs.getString("gender"));
                ta.setDepartment(rs.getString("department"));
                ta.setEnrollmentDate(rs.getTimestamp("enrollmentDate").toLocalDateTime());
                ta.setArchivedAt(rs.getTimestamp("archivedAt").toLocalDateTime());
                ta.setGPA(rs.getDouble("gpa"));
                ta.setTranscriptJson(rs.getString("transcriptJson"));
                return ta;
            }
            }
            return null;
        }
    }
    public boolean deleteTranscriptArchive(int id, Connection conn) throws SQLException{
        String sql= "DELETE FROM studenttranscript WHERE id= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            return stmt.executeUpdate()> 0;
        }
    }
}
