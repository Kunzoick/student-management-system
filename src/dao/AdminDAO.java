/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Admin;
import util.DBConnection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.sql.Statement;

/**
 *
 * @author jesse
 */
public class AdminDAO {
    
    //create admin entry
    public boolean createAdmin(Admin admin, Connection conn){
        String sql= "INSERT INTO admins(userId, pin, createdAt, updatedAt) VALUES(?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";
        try(PreparedStatement stmt= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                   
                   stmt.setInt(1, admin.getUserId());
                   stmt.setString(2, admin.getPin());
                   
                   int rowsInserted= stmt.executeUpdate();
                   if(rowsInserted >0){
                       try(ResultSet rs= stmt.getGeneratedKeys()){
                           if(rs.next()){
                               admin.setAdminId(rs.getInt(1));//set generated adminId
                           }
                       }
                       return true;
                   }
               }catch(SQLException e){
                       System.err.println("Error creating admin: "+ e.getMessage());
                       }
                        return false;
        }
               
    // fetch admin by userid
    public Admin getAdminByUserId(int userId){
        String sql= "SELECT * FROM admins WHERE userId = ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            
            stmt.setInt(1, userId);
           try(ResultSet rs= stmt.executeQuery()){
            
            if(rs.next()){
                return mapResultSetToAdmin(rs);
            }
           }
        }catch(SQLException e){
            System.err.println("Error fetching admin by userId: "+ e.getMessage());
        }
        return null;
    }
   
    public Admin getAdminByPin(String pin, Connection conn){
        String sql= "SELECT * FROM admins WHERE pin = ? AND isActive = TRUE";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, pin);
                try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return mapResultSetToAdmin(rs);
            }
                }
        }catch(SQLException e){
            System.err.println("Error fetching admin by PIN: "+ e.getMessage());           
        }
        return null;
    }
    
    //fetch admin by admin Id
    public Admin getAdminById(int adminId , int userId){
        String sql= "SELECT * FROM admins WHERE adminId = ? AND userId= ? AND isActive= TRUE";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            
            stmt.setInt(1, adminId);
            stmt.setInt(2, userId);
            
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return mapResultSetToAdmin(rs);
            }
            }
        }catch(SQLException e){
            System.err.println("Error fetching admin by adminId: "+ e.getMessage());
        }
        return null;
    }
     public Admin getAdminByadminId(int adminId){
        String sql= "SELECT * FROM admins WHERE adminId = ? AND isActive= TRUE";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            
            stmt.setInt(1, adminId);
            
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return mapResultSetToAdmin(rs);
            }
            }
        }catch(SQLException e){
            System.err.println("Error fetching admin by adminId: "+ e.getMessage());
        }
        return null;
    } 
    
    //update admin pin
    public boolean updatePin(int adminId, String newPin, int userId, Connection conn){
      String sql= "UPDATE admins SET pin= ?, updatedAt= CURRENT_TIMESTAMP "+ "WHERE adminId= ? AND userId= ?";
      try(PreparedStatement stmt= conn.prepareStatement(sql)){
          
          stmt.setString(1, newPin);
          stmt.setInt(2, adminId);
          stmt.setInt(3, userId);
          
          return stmt.executeUpdate() >0;
      }catch(SQLException e){
        System.err.println("Error updating admin pin: "+ e.getMessage());  
      }
      return false;
    }
    
    public boolean pinExists(String pin, Connection conn) throws SQLException{
        String sql= "SELECT 1 FROM admins WHERE pin = ? AND isActive = TRUE LIMIT 1";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, pin);
            try(ResultSet rs= stmt.executeQuery()){
                return rs.next();
            }
        }
    }
    
    //fetch all admins
    public List<Admin> getAllAdmins(){
        List<Admin> admins= new ArrayList<>();
        String sql= "SELECT * FROM admins";
        
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql);
        ResultSet rs= stmt.executeQuery()){
            
            while(rs.next()){
                admins.add(mapResultSetToAdmin(rs));
            }
        }catch(SQLException e){
            System.err.println("Error fetching admin: "+ e.getMessage());
        }
        return admins;
    }
    
    //is admin active
    public boolean isAdminActive(int userId){
        String sql= "SELECT isActive FROM admins WHERE userId= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, userId);
            try(ResultSet rs= stmt.executeQuery()){;
            if(rs.next()){
                return rs.getBoolean("isActive");
            }
            }
        }catch(SQLException e){
            System.err.println("Error checking admin active status"+ e.getMessage());
        }
        return false;
    }
    
    //soft delete- makes admin not able to log in or manage account
    public boolean deactivateAdmin(int adminId, int userId){
        String sql= "UPDATE admins SET isActive= FALSE, updatedAt= CURRENT_TIMESTAMP "+ "WHERE adminId= ? AND userId= ?";
        
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
           
            stmt.setInt(1, adminId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() >0;
        }catch(SQLException e){
            System.err.println("Error deactivating admin: "+ e.getMessage());
    }
        return false;
    }
    
    public List<Integer> getAllAdmins(Connection conn) throws SQLException{
        List<Integer> adminIds= new ArrayList<>();
        String sql= "SELECT adminId FROM admins WHERE isActive= true ";
        try(PreparedStatement stmt= conn.prepareStatement(sql);
                ResultSet rs= stmt.executeQuery()){
            while(rs.next()){
                adminIds.add(rs.getInt("adminId"));
            }
        }
        return adminIds;
    }
    
    //validate AdminPin 
    public boolean validatePin(String adminPin){
        String sql= "SELECT COUNT(*) FROM admins WHERE pin= ? AND isActive= TRUE";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, adminPin);
            try(ResultSet rs= stmt.executeQuery()){
            if(rs.next()){
                return rs.getInt(1)> 0;
            }
            }
        }catch(SQLException e){
            System.err.println("Error validating adminPin:"+ e.getMessage());
        }
        return false;
    }
    
    //Map resultset= admin object
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException{
        Admin admin= new Admin();
        admin.setAdminId(rs.getInt("adminId"));
        admin.setUserId(rs.getInt("userId"));
        admin.setPin(rs.getString("pin"));
        
        Timestamp created= rs.getTimestamp("createdAt");
        Timestamp updated= rs.getTimestamp("updatedAt");
        if(created != null)
            admin.setCreatedAt(created.toLocalDateTime());
        if(updated != null)
            admin.setUpdatedAt(updated.toLocalDateTime());
        return admin;
    }
}
