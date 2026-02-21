/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.UserDAO;
import model.User;
import java.sql.Connection;
import util.DBConnection;
import util.PasswordUtil;

/**
 *
 * @author jesse
 */
public class ForgotPasswordService {
    private UserDAO userDAO;
    private NotificationService notificationService;
    
    public ForgotPasswordService(UserDAO userDAO){
        this.userDAO= userDAO;
        this.notificationService= NotificationService.getInstance();
    }
    
    public boolean resetPassword(String username, String firstName, String lastName, String newPassword){
        if(newPassword== null || newPassword.length() <6 || newPassword.length() >16){
            return false;
        }
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            
            //fetch user
            User user=  userDAO.getByUsername(username);
            if(user== null){
                return false;
            }
            if(!firstName.equalsIgnoreCase(user.getFirstName()) || !lastName.equalsIgnoreCase(user.getLastName())){
                return false;
            }
            
            //hash new password
            //String hashedPassword= PasswordUtil.hashPassword(newPassword);
            
            //update password transactionally
            boolean updated= userDAO.updatePassword(user.getId(), newPassword, conn);
            if(updated){
                conn.commit();
            }else{
                conn.rollback();
            }
            return updated;
        }catch(Exception e){
            System.err.println("Error: "+e.getMessage());
            return false;
        }
    }
}