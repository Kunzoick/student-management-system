/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import java.sql.Connection;
import dao.AdminDAO;
import dao.StudentDAO;
import dao.UserDAO;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import model.Admin;
import model.Student;
import model.User;
import util.DBConnection;
import util.PasswordUtil;
import util.PasswordUtil.PasswordGenerator;
import java.sql.SQLException;

/**
 *
 * @author jesse
 */
public class AdminService {
    
    private AdminDAO adminDAO= new AdminDAO();
    private UserDAO userDAO= new UserDAO();
    private StudentDAO studentDAO= new StudentDAO();
    private NotificationService notificationService= NotificationService.getInstance();
    
    //register a new admin and generate a unique admin pin
    public boolean registerAdmin(User user, Admin admin){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
            //2..insert into users table
            boolean userCreated= userDAO.register(user, conn);
            if(!userCreated){
                conn.rollback();
                return false;
            }
            
            //3..generate unique admin pin
            String pin= generateUniqueAdminPin(conn);
            admin.setPin(pin);
            admin.setUserId(user.getId());//link user to admin
            
            //4..insert into admin table
            boolean adminCreated= adminDAO.createAdmin(admin, conn);
            if(!adminCreated){
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        }catch(SQLException e){
            conn.rollback();
            System.err.println("Error registering admin:"+ e.getMessage());
            return false;
        }finally{
            conn.setAutoCommit(true);
        }
    }catch(SQLException e){
        System.err.println("Transaction error: "+ e.getMessage());
        return false;
    }
    }
    
    //register Student by admin
    public boolean registerStudentByAdmin(String firstName, String lastName, String gender, String department, LocalDate dob, LocalDate enrollmentDate, int adminId){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            
            try{
            //generate default credentials
            String base= firstName.toLowerCase() + "."+ lastName.toLowerCase();
            String suffix= String.valueOf(System.currentTimeMillis());
            String combined= base+ "."+ suffix;
            String defaultUsername= combined.length()> 15 ? combined.substring(0, 15) : combined;
            String defaultPassword= PasswordUtil.PasswordGenerator.generateRandomPassword(12);
            String hashedPassword= PasswordUtil.hashPassword(defaultPassword);
            
            //create user record
            User newUser= new User();
            newUser.setUsername(defaultUsername);
            newUser.setPassword(hashedPassword);
            newUser.setRole("student");
            newUser.setFirstLogin(true);
            newUser.setIsDefaultPassword(true);
            
            if(!userDAO.register(newUser, conn)){
                conn.rollback();
                return false;
            }
            
            //create student profile
            Student student= new Student();
            student.setUserId(newUser.getId());
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setGender(gender);
            student.setDepartment(department);
            student.setDOB(dob);
            student.setEnrollmentDate(enrollmentDate);
            student.setAdminId(adminId);
            student.setStatus("Active");
            
            if(!studentDAO.registerStudent(student, conn)){
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        }catch(SQLException e){
            conn.rollback();
            System.err.println("Error registering student by admin"+ e.getMessage());
            return false;
        }finally{
            conn.setAutoCommit(true);
        }
    }catch(SQLException e){
            System.err.println("Connection error: "+ e.getMessage());
            return false;
            }
        }
    
    //get admin pin
    public boolean updateAdmin(int adminId, String newPin, int userId){
        try(Connection conn= DBConnection.getConnection()){
            return adminDAO.updatePin(adminId, newPin, userId, conn);
        }catch(Exception e){
            System.err.println("Error updating admin pin:"+ e.getMessage());
            return false;
        }
    }
    public Admin getAdminByUserId(int userId){
        try{
            return adminDAO.getAdminByUserId(userId);
        }catch(Exception e){
            System.err.println("Error fetching admin by userId: "+ e.getMessage());
            return null;
        }
    }
    
    //delete user linked to admin
    public boolean deleteAdmin(int adminId, int userId){
        try{
            Admin admin= adminDAO.getAdminById(adminId, userId);
            if(admin== null){
                System.err.println("Admin not found"+ adminId);
                return false;
            }
            return adminDAO.deactivateAdmin(adminId, userId);
   
        }catch(Exception e){
            System.err.println("Error deleting admin:"+ e.getMessage());
            return false;
        }
    }
    
    //get all admin
    public List<Admin> getAllAdmins(){
        try{
            return adminDAO.getAllAdmins();
        }catch(Exception e){
            System.err.println("Error fetching admin:"+ e.getMessage());
            return null;
        }
    }
    
    //generate random 6-digit pin -> using SecureRandom is better as it helps in making sure no two users have the same random pin
    private String generateRandomPin(){
        String chars= "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom rand= new SecureRandom();        
        StringBuilder pin= new StringBuilder();
        int length= 6;
        for(int i= 0; i<length; i++){
            pin.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return pin.toString();
    }
    private String generateUniqueAdminPin(Connection conn) throws SQLException{
        int maxAttempts= 10;
        for(int attempt= 0; attempt < maxAttempts; attempt++){
            String pin= generateRandomPin();
            //check if Pin exists in DB
            if(adminDAO.pinExists(pin, conn)){
                return pin;
            }
        }
        throw new SQLException("Failed to generate Unique PIN after "+ maxAttempts+ "attempts");
    }
    
    public String regenerateAdminPin(int adminId, int userId){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
            String newPin= generateUniqueAdminPin(conn);
            boolean updated= adminDAO.updatePin(adminId, newPin, userId, conn);
            if(!updated){
                conn.rollback();
                return null;
            }
            conn.commit();
            return newPin;
            }catch(Exception e){
                conn.rollback();
                throw e;
            }finally{
                conn.setAutoCommit(true);
            }
        }catch(Exception e){
            System.err.println("Error regenerating adminPin: "+ e.getMessage());
            return null;
        }
    }
}
/* would i have an issue with this code  List<Student> students= studentService.getAllStudents(adminId); if i have this code in my db public Student getStudentByUserId(int userId){
        String sql= "SELECT * FROM students WHERE userId= ?";
        Student student= null;
        try(Connection conn= DBConnection.getConnection();
                PreparedStatement stmt= conn.prepareStatement(sql)){
            stmt.setInt(1, userId);
            ResultSet rs= stmt.executeQuery();
            if(rs.next()){
                student= mapResultSetToStudent(rs);
            }
        }catch(SQLException e){
            System.err.println("Error fetching student by userId"+ e.getMessage());
        }
        return student;
    } 
*/
 