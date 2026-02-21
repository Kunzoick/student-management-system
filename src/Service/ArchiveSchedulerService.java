/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.AdminDAO;
import util.DBConnection;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author jesse
 */
public class ArchiveSchedulerService {
    private TranscriptArchiveService transcriptArchiveService;
    private AdminDAO adminDAO= new AdminDAO();
    public ArchiveSchedulerService(TranscriptArchiveService transcriptArchiveService){
        this.transcriptArchiveService= transcriptArchiveService;
    }
    
    //run this once when app or admin logs in
    public void checkAutoArchive() throws Exception{
        //load all admins
        List<Integer> adminIds;
        try(Connection conn= DBConnection.getConnection()){
            adminIds= adminDAO.getAllAdmins(conn);
        }catch(SQLException e){
            System.err.println("Failed to load admins: "+ e.getMessage());
            return;
        }
        LocalDateTime now= LocalDateTime.now();
        //process each admin in its own transaction
        for(int adminId : adminIds){
            try(Connection conn= DBConnection.getConnection()){
                conn.setAutoCommit(false);
                try{
                    //check if this admin needs archiving
                    String lastRun= getMetadata(conn, "lastArchiveRun", adminId);
                    LocalDateTime lastRunTime= lastRun != null ? LocalDateTime.parse(lastRun) : null;
                    if(lastRunTime== null || lastRunTime.isBefore(now.minusDays(1))){
                        transcriptArchiveService.autoArchiveEligibleStudents(adminId, conn);
                        upsertMetadata(conn, "lastArchiveRun", now.toString(), adminId);
                    }
                    conn.commit();
                }catch(Exception e){
                    try{
                        conn.rollback();
                    }catch(SQLException rollbackEx){
                        System.err.println("Rollback failed for admin "+ adminId+ ": "+ rollbackEx.getMessage());
                    }
                    System.err.println("Archive failed for admin "+ adminId+ ": "+ e.getMessage());
                }finally{
                    try{
                        conn.setAutoCommit(true);
                    }catch(SQLException ex){
                        System.err.println("Failed to reset autoCommit for admin "+ adminId+ ": "+ ex.getMessage());
                    }
                }
            }catch(SQLException e){
                System.err.println("Connection error for admin "+ adminId + ": "+ e.getMessage());
            }
        }
    }
    
    private String getMetadata(Connection conn, String key, int adminId) throws SQLException{
        String sql= "SELECT valueText FROM systemmetadata WHERE keyName= ? AND adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, key);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()) return rs.getString("valueText");
            }
        }
        return null;
    }
    
    private void upsertMetadata(Connection conn, String key, String value, int adminId) throws SQLException{
        String sql= "INSERT INTO systemmetadata(keyName, valueText, adminId) VALUES(?,?, ?) "+ "ON DUPLICATE KEY UPDATE valueText= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.setInt(3, adminId);
            stmt.setString(4, value);
            stmt.executeUpdate();
        }
    }
}
