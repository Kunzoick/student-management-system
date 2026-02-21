/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import dao.AdminDAO;
import dao.StudentDAO;
import dao.UserDAO;
import model.Admin;
import model.Student;
import model.User;
import java.sql.Connection;
import dao.EnrollmentDAO;
import model.Enrollment;
import java.sql.SQLException;
/**
 *
 * @author jesse
 */
public class AuthUtil {
    private final AdminDAO adminDAO= new AdminDAO();
    private final UserDAO userDAO= new UserDAO();  
    private final StudentDAO studentDAO= new StudentDAO();
    private final EnrollmentDAO enrollmentDAO= new EnrollmentDAO();
    
    //register a student(create user+ student)
    public boolean registerStudentwithPin(Student student, User user, String adminPin){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);// start transaction
            try{
            //1.. validate admin pin
            Admin admin= adminDAO.getAdminByPin(adminPin, conn);
            if(admin== null){
                System.err.println("Invalid admin PIN!");
                return false;
            }
            //link student to validate admin
            student.setAdminId(admin.getAdminId());
            
            //2.. insert user
            if(!userDAO.register(user, conn)){
                System.err.println("Failed to create user account");
                return false;
            }
            
            //3.. insert student
            student.setUserId(user.getId());//link student to created user
            if(!studentDAO.registerStudent(student, conn)){
                System.err.println("Failed to create student record");
                return false;
            }
            
            conn.commit();//if all good save changes
            return true;
        }catch(Exception e){
            try{
                conn.rollback();
            }catch(SQLException rollbackEx){
                e.addSuppressed(rollbackEx);
            }
            System.err.println("Transaction error:"+ e.getMessage());
            return false;
        }finally{
            try{
                conn.setAutoCommit(true);
            }catch(SQLException ex){
                System.err.println("Failde to reset autocommit: "+ ex.getMessage());
            }
        }
    }catch(SQLException e){
       System.err.println("Database connection failed: "+ e.getMessage());
       return false;
    }
    }
    
    //enroll student into a course with admin pin validation 
    public boolean enrollStudentWithPin(Enrollment enrollment, String adminPin){
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
                
            
            //1..Validate admin pin
            Admin admin= adminDAO.getAdminByPin(adminPin, conn);
            if(admin== null){
                System.err.println("Invalid admin Pin");
                return false;
            }
            if(admin.getAdminId() != enrollment.getAdminId()){
                System.err.println("Admin PIN does not match enrollment's admin!");
                return false;
            }
            
            //2. mark enrollment as admin-driven
            enrollment.setEnrolledBy("admin");
            
            //3.. insert enrollment
            if(!enrollmentDAO.createEnrollment(enrollment, conn)){
                System.err.println("Failed to crreate enrollment");
                return false;
            }
            
            conn.commit();//if all good save changes
            return true;
        }catch(Exception e){
            try{
                conn.rollback();
            }catch(SQLException rollbackEx){
                e.addSuppressed(rollbackEx);
            }
            System.err.println("Error enrolling student:"+ e.getMessage());
            return false;
        }finally{
                try{
                    conn.setAutoCommit(true);
                }catch(SQLException ex){
                    System.err.println("Failed to reset autocommit: "+ ex.getMessage());
                }
            }
        }catch(SQLException e){
            System.err.println("Database connection failed: "+ e.getMessage());
            return false;
        }
    }
    }

