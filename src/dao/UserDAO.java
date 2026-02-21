/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.User;
import util.DBConnection;
import util.PasswordUtil;


import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.Timestamp;
/**
 *
 * @author jesse
 */
public class UserDAO {
    
    //register new user(student or admin)
    public boolean register(User user, Connection conn) throws SQLException{
        String sql= "INSERT INTO users(username, password, role, firstName, lastName, firstLogin, isDefaultPassword) VALUES(?,?,?,?,?,?,?)";
        try(PreparedStatement stmt= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setBoolean(6, user.getFirstLogin());
            stmt.setBoolean(7, user.getIsDefaultPassword());
            
            int rowsInserted= stmt.executeUpdate();
            if(rowsInserted >0){
                try(ResultSet rs= stmt.getGeneratedKeys()){
                    if(rs.next()){
                        user.setId(rs.getInt(1));//set generated user id
                    }
                }
                return true;
            }
            return false;
        }
        }
    
    //login
    public User login(String username, String password, Connection conn) throws SQLException{
        String sql= "SELECT * FROM users WHERE username=?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, username);
            try(ResultSet rs= stmt.executeQuery()){
            if(!rs.next())return null;
                
                //verify hashed password
                if(!PasswordUtil.verifyPassword(password, rs.getString("password"))){
                return null;
            }
                User user= new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setFirstLogin(rs.getBoolean("firstLogin"));
                user.setIsDefaultPassword(rs.getBoolean("isDefaultPassword"));
                
                //covert sql timestamp to java.time,localdatetime as i am getting error on the rs.getTimeStamp
                Timestamp ts= rs.getTimestamp("lastLoginDate");
                if(ts != null) user.setLastLoginDate(ts.toLocalDateTime());
                
                //update lastlogindate
                updateLastLogin(user.getId(), conn);
                return user;
                }
            }
    }
    //--wrapper--
    public User login(String username, String password){
        try(Connection conn= DBConnection.getConnection()){
            return login(username, password, conn);
        }catch(SQLException e){
            System.err.println("Login error: "+ e.getMessage());
            return null;
        }
    }
    
    //update credentials
    public boolean updateCredentials(int userId, String newUsername, String newPassword, Connection conn) throws SQLException {
        if(isUsernameTaken(newUsername)){
            return false;
        }
        String sql= "UPDATE users SET username= ?, password= ?, firstLogin= false, isDefaultPassword= false WHERE id= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, newUsername);
            stmt.setString(2,PasswordUtil.hashPassword(newPassword));
            stmt.setInt(3, userId);
            return stmt.executeUpdate() >0;
        }
    }
    
     //update password
        public boolean updatePassword(int userId, String newPassword, Connection conn)throws SQLException{
            String sql= "UPDATE users SET password= ?, firstLogin= false, isDefaultPassword= false WHERE id= ?";
            try(PreparedStatement stmt= conn.prepareStatement(sql)){
                stmt.setString(1, PasswordUtil.hashPassword(newPassword));
                stmt.setInt(2, userId);
                return stmt.executeUpdate() >0;
            }
        }
        
        //update username
        public boolean updateUsername(int userId, String newUsername, Connection conn) throws SQLException {
            if(isUsernameTaken(newUsername)){
                return false;
            }
        String sql= "UPDATE users SET username= ? WHERE id= ?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() >0;
        }
    }
        
            
    //update last login
    private void updateLastLogin(int userId, Connection conn) throws SQLException{
        String sql= "UPDATE users SET lastLoginDate= CURRENT_TIMESTAMP WHERE id=?";
        try(PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    //check for uniqueness of username
    public boolean isUsernameTaken(String username) throws SQLException{
        String sql= "SELECT COUNT(*) FROM users WHERE username= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, username);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1) >0;
                }
            }
        }
        return false;
    }
    public User  getByUsername(String username)throws SQLException{
        String sql= "SELECT * FROM users WHERE username= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, username);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    User user= new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        }
        return null;//username not found
    }
    public User getById(int userId){
        String sql= "SELECT * FROM users WHERE id= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, userId);
            try(ResultSet rs= stmt.executeQuery()){
                if(rs.next()){
                    User user= new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        }catch(SQLException e){
            System.err.println("Error fetching user by id: "+ e.getMessage());
        }
        return null;
    }
    
    //fetch all users
    public List<User> getAllUsers(){
        List<User> users= new ArrayList<>();
        String sql= "SELECT * FROM users";
        try(Connection conn= DBConnection.getConnection();
        Statement stmt= conn.createStatement();
                ResultSet rs= stmt.executeQuery(sql)){
            while(rs.next()){
                User u= new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setFirstName(rs.getString("firstName"));
                u.setLastName(rs.getString("lastName"));
                //converted timestamp here also
                Timestamp ts= rs.getTimestamp("lastLoginDate");
                if(ts != null){
                    u.setLastLoginDate(ts.toLocalDateTime());
                }else{
                    u.setLastLoginDate(null);
                }
                
                users.add(u);
            }
        }catch(SQLException e){
           System.err.println("Error fetching users"+ e.getMessage());
        }
     return users;   
    }  
    
    //verify that username + names exist
    public boolean verifyUserDetails(String username, String firstName, String lastName){
        String sql= "SELECT id FROM users WHERE username= ? AND firstName= ? AND lastName= ?";
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            
            try(ResultSet rs= stmt.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            System.err.println("Error wrong details");
            return false;
        }
    }
}
