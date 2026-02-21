/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.GradingScaleDAO;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import model.GradingScale;
import java.sql.Connection;
import util.DBConnection;

/**
 *
 * @author jesse
 */
public class GradingScaleService {
    
    private GradingScaleDAO gradingScaleDAO;
    private final ConcurrentHashMap<Integer, Object> adminLocks= new ConcurrentHashMap<>();
    public GradingScaleService(){
        this.gradingScaleDAO= new GradingScaleDAO();
    }
    
    //create a new grading scale for an admin
    public boolean createGradingScale(GradingScale scale, int adminId){
        Object lock= adminLocks.computeIfAbsent(adminId, k -> new Object());
        synchronized(lock){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            scale.setAdminId(adminId);
            
            //check if admin already has a grading system
            String existingSystem= gradingScaleDAO.getExistingScale(adminId, conn);
            if(existingSystem != null && !existingSystem.equalsIgnoreCase(scale.getGradingSystem())){
                System.err.println("Admin already uses"+ existingSystem+ "system. cannot mix systems.");
                    return false;
            }
            List<GradingScale> existingScales= gradingScaleDAO.getAllScales(scale.getGradingSystem(), adminId, conn);
                
                //validate overlap in score ranges 
                validateNoOverlap(scale, existingScales, null);
                gradingScaleDAO.createScale(scale, conn);
                conn.commit();
                return true;
        }catch(Exception e){
            System.err.println("Error creating grading scale:"+ e.getMessage());
            return false;
        }
    }
    }
    
    //get all grading scales
    public List<GradingScale> getGradingScalesByAdmin(int adminId, String gradingSystem){
        try(Connection conn= DBConnection.getConnection()){
            return gradingScaleDAO.getAllScales(gradingSystem, adminId, conn);
        }catch(Exception e){
            System.err.println("Error fetching grading scale:"+ e.getMessage());
            return null;
    }
}
    
    //get grading scale
    public GradingScale getGradingScaleById(int id, int adminId){
        try{
            return gradingScaleDAO.getScaleById(id, adminId);
        }catch(Exception e){
            System.err.println("Error fetching grading scale:"+ e.getMessage());
            return null;
        }
    }
    
    //find the correct scale for a given score
    public GradingScale findScaleForScore(double score, int adminId, String gradingSystem){
        try{
            return gradingScaleDAO.findScaleForScore(adminId, gradingSystem, score);
        }catch(Exception e){
            System.err.println("Error finding grading scale for score:"+ e.getMessage());
            return null;
        }
    }
    
    // update
    public boolean updateGradingScale(GradingScale scale, int adminId){
        Object lock= adminLocks.computeIfAbsent(adminId, k -> new Object());
        synchronized(lock){
            
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            scale.setAdminId(adminId);
            
            List<GradingScale> existingScales= gradingScaleDAO.getAllScales(scale.getGradingSystem(), adminId, conn);
            validateNoOverlap(scale, existingScales, scale.getId());
            boolean updated= gradingScaleDAO.updateScale(scale, conn);
            if(!updated){
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        }catch(Exception e){
            System.err.println("Error updating grading scale:"+ e.getMessage());
            return false;
        }
        }
    }
    
    //delete grading scale
    public boolean deleteGradingScale(int id, int adminId){
        Object lock= adminLocks.computeIfAbsent(adminId, k -> new Object());
        synchronized(lock){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            boolean deleted= gradingScaleDAO.deleteScale(id, adminId, conn);
            if(!deleted){
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        }catch(Exception e){
            System.err.println("Error deleting grading scale:"+ e.getMessage());
            return false;
        }
        }
    }
    private void validateNoOverlap(GradingScale scale, List<GradingScale> existing, Integer excluded){
        for(GradingScale gs : existing){
            if(excluded != null && gs.getId() == excluded) continue;
            boolean overlap= scale.getMinScore() <= gs.getMaxScore() && scale.getMaxScore() >= gs.getMinScore();
            
            if(overlap){
                throw new IllegalStateException("Overlapping grading scale");
            }
        }
    }
}
