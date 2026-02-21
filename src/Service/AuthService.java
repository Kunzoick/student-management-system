/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.AdminDAO;
import dao.StudentDAO;
import dao.UserDAO;
import model.Enrollment;
import model.Student;
import model.User;
import util.AuthUtil;
import util.DBConnection;
import util.PasswordUtil;
import java.sql.Connection;
import model.Admin;

/**
 *
 * @author jesse
 */
public class AuthService {
    

    private AuthUtil authutil= new AuthUtil();
    private UserDAO userDAO= new UserDAO();
    private StudentDAO studentDAO= new StudentDAO();
    private AdminDAO adminDAO= new AdminDAO();
    private ForgotPasswordService forgotPasswordService= new ForgotPasswordService(userDAO);
    private NotificationService notificationService= NotificationService.getInstance();
    
  
    //register new Student with admin Validation
    public boolean registerStudent(User user, String adminPin, Student student){
        try{
            if(userDAO.isUsernameTaken(user.getUsername())){
                return false;
                
            }
            return authutil.registerStudentwithPin(student, user, adminPin);//delegate to Authutil
        }catch(Exception e){
            System.err.println("Error registering student:"+ e.getMessage());
            return false;
        }
    }
    
    public User login(String username, String password){
        User user= userDAO.login(username, password);
        
        if(user ==null){
            System.out.println("Invalid username or password");
            return null;
        }
        if(user.getRole()== null || user.getRole().trim().isEmpty()){
            System.err.println("User role not defined for username: "+ username);
            return null;
        }
        //handles different roles
        switch(user.getRole().toLowerCase()){
            case "admin" -> {
                if(!adminDAO.isAdminActive(user.getId())){
                    System.out.println("This admin account is inactive. Contact sysetm support");
                    return null;
                }
            }
                
            case "student" -> {
                Student student= studentDAO.getStudentByUserId(user.getId());
                if(student== null){
                    System.err.println("No student record found for this user.");
                    return null;
                }
                //handlde student status
                String status= student.getStatus();
                if(status !=null){
                    switch(status.toLowerCase()){
                        case "inactive":
                            System.out.println("Your account is currently inactive. Contact your administrator.");
                            return null;
                            
                        case "graduated":
                            System.out.println("Welcome, graduate! You have limited access(view only mode)");
                            user.setRemarks("graduated");
                            break;
                            
                        case "archived":
                            System.out.println("Your account has been archived and is no longer accessible");
                            return null;
                            
                        case "active":
                        default:
                            break;
                    }
                }
                //handle default password or first login
                if(user.getIsDefaultPassword()){
                    System.out.println("You are still using your default password. please update it");
                }
                break;
            }
                }
        if(user.getFirstLogin()){
            System.out.println("Welcome! pls update your credentials.");
        }
        return user;
    }
    
    //Enrol student in a course with Admin pin validation
    public boolean enrollStudent(Enrollment enrollment, String adminPIn){
        try{
            return authutil.enrollStudentWithPin(enrollment, adminPIn);
        }catch(Exception e){
             System.err.println("Error enrolling student:"+ e.getMessage());
             return false;
        }
    }
    //forgot password
    public boolean resetPassword(String username, String firstName, String lastName, String newPassword, String adminPin){
        try{
            User user= userDAO.getByUsername(username);
            if(user== null){
                return false;
            }
            //if a user is a student, verify admin pin before reset
            if("student".equalsIgnoreCase(user.getRole())){
                if(adminPin== null || adminPin.isEmpty()){
                    System.err.println("Admin PIN required for student password reset");
                    return false;
                }
                //get student adminid
                Student student= studentDAO.getStudentByUserId(user.getId());
                if(student== null){
                    System.err.println("Student record not found: "+ user.getId());
                    return false;
                }
                Admin admin= adminDAO.getAdminByadminId(student.getAdminId());
                if(admin== null){
                    System.err.println("Admin not found or inactive for student");
                    return false;
                }
                if(!adminPin.equals(admin.getPin())){
                    System.err.println("Invalid admiin PIN for this student");
                    return false;
                }
        }
        return forgotPasswordService.resetPassword(username, firstName, lastName, newPassword);
    }catch(Exception e){
        notificationService.alert("Password reset failed"+ e.getMessage(), 0);
                return false;
    }
    }
    //updates
    //1..password only
    public boolean updatePassword(int userId, String Currentpassword, String newPassword, String username){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            
            //fetch current user
            User user= userDAO.getByUsername(username);
            if(user== null){
                return false;
            }
            //verify old password
            if(!PasswordUtil.verifyPassword(Currentpassword, user.getPassword())){
                return false;
            }
            //validate new password
            if(newPassword== null || newPassword.length() <6 || newPassword.length() >16){
                return false;
            }
            boolean updated= userDAO.updatePassword(userId, newPassword, conn);
            if(updated){
                conn.commit();
                notificationService.info("password successfully updated!", userId);
                return true;
            }else{
                conn.rollback();
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }
    //2..username only
    public boolean updateUsername(int userId, String newUsername){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            //check if username already exists
            if(userDAO.isUsernameTaken(newUsername)){
               return false;
            }
            boolean updated= userDAO.updateUsername(userId, newUsername, conn);
            if(updated){
                conn.commit();
                notificationService.info("Username updated successfully!", userId);
                return true;
            }else{
                conn.rollback();
                return false;
            }
        }catch(Exception e){
            System.err.println("Error :"+ e.getMessage());
            return false;
        }
    }
    //3..update credentials
    public boolean updateCredentials(int userId, String newUsername, String newPassword){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            if(userDAO.isUsernameTaken(newUsername)){
                return false;
            }
            //validate password
            if(newPassword== null || newPassword.length() <6 || newPassword.length() >16){
                return false;
            }
            boolean updated= userDAO.updateCredentials(userId, newUsername, newPassword, conn);
            if(updated){
                conn.commit();
                notificationService.info("Credentials updated successfully!", userId);
                return true;
            }else{
                conn.rollback();
                return false;
            }
        }catch(Exception e){
            System.err.println("Error updating credentials:"+ e.getMessage());
            return false;
        }
    }
    // verify password
    public boolean verify(String username, String password){
        try{
            User user= userDAO.getByUsername(username);
            if(user== null) return false;
            
            String storedHash= user.getPassword();
            return PasswordUtil.verifyPassword(password, storedHash);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
    
    

