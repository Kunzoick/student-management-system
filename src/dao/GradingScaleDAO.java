/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.GradingScale;
import util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jesse
 */
public class GradingScaleDAO {
    
    //create grading scale
    public boolean createScale(GradingScale scale, Connection conn){
        String sql= "INSERT INTO grading_scale(gradingSystem, minScore, maxScore, grade, gradePoint, adminId)"+ "VALUES(?,?,?,?,?,?)";
        try(PreparedStatement stmt= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, scale.getGradingSystem());
            stmt.setDouble(2, scale.getMinScore());
            stmt.setDouble(3, scale.getMaxScore());
            stmt.setString(4, scale.getGrade());
            stmt.setDouble(5, scale.getGradePoint());
            stmt.setInt(6, scale.getAdminId());
            
            int rowsInserted= stmt.executeUpdate();
            if(rowsInserted >0){
                try(ResultSet rs= stmt.getGeneratedKeys()){
                    if(rs.next()){
                        scale.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }catch(SQLException e){
            System.err.println("Error creating grading scale:"+ e.getMessage());
        }
        return false;
    }
    
    //read(by id)
    public GradingScale getScaleById(int id, int adminId){
        String sql= "SELECT * FROM grading_scale WHERE id= ? AND adminId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.setInt(2, adminId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToScale(rs);
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching grading scale by id:"+ e.getMessage());
        }
        return null;
    }
    
    
    //read all 
    public List<GradingScale> getAllScales(String gradingSystem, int adminId, Connection conn){
        List<GradingScale> scales= new ArrayList<>();
        String sql= "SELECT * FROM grading_scale WHERE adminId= ? AND gradingSystem= ? ORDER BY minScore DESC";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            stmt.setString(2, gradingSystem);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    scales.add(mapResultSetToScale(rs));
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching grading scales:"+ e.getMessage());
        }
        return scales;
    }
    
    public String getExistingScale(int adminId, Connection conn){
        String sql= "SELECT gradingSystem FROM grading_scale WHERE adminId= ? LIMIT 1";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return rs.getString("gradingSystem");
            }
            }
        }catch(SQLException e){
            System.err.println("Error fetching existing grading System: "+e.getMessage());
        }
        return null;
    }
    
    //Find scale for score
    public GradingScale findScaleForScore(int adminId, String gradingSystem, double score){
        String sql= "SELECT * FROM grading_scale WHERE adminId= ? AND gradingSystem= ? AND ? BETWEEN minScore AND maxScore LIMIT 1";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, adminId);
            stmt.setString(2, gradingSystem);
            stmt.setDouble(3, score);
            
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToScale(rs);
                }
            }
        }catch(SQLException e){
            System.err.println("Error finding grading scale for score:"+ e.getMessage());
        }
        return null;
    }
    
    //update
    public boolean updateScale(GradingScale scale, Connection conn){
        String sql = "UPDATE grading_scale SET gradingSystem= ?, minScore= ?, maxScore= ?, grade= ?, gradePoint= ? WHERE id= ? AND adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            
            stmt.setString(1, scale.getGradingSystem());
            stmt.setDouble(2, scale.getMinScore());
            stmt.setDouble(3, scale.getMaxScore());
            stmt.setString(4, scale.getGrade());
            stmt.setDouble(5, scale.getGradePoint());
            stmt.setInt(6, scale.getId());
            stmt.setInt(7, scale.getAdminId());
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error updating  grading scale:"+ e.getMessage());
        }
        return false;
    }
    
    //delete
    public boolean deleteScale(int id, int adminId, Connection conn){
        String sql= "DELETE FROM grading_scale WHERE id= ? AND adminId= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.setInt(2, adminId);
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error deleting grading scale:"+ e.getMessage());
        }
        return false;
    }
    
    //map resultset
    private GradingScale mapResultSetToScale(ResultSet rs) throws SQLException{
        GradingScale scale= new GradingScale();
        scale.setId(rs.getInt("id"));
        scale.setGradingSystem(rs.getString("gradingSystem"));
        scale.setMInScore(rs.getDouble("minScore"));
        scale.setMaxScore(rs.getDouble("maxScore"));
        scale.setGrade(rs.getString("grade"));
        scale.setGradePoint(rs.getDouble("gradePoint"));
        scale.setAdminId(rs.getInt("adminId"));
        return scale;
    }
}

