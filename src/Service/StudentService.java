/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.StudentDAO;
import java.util.List;
import model.Student;
import model.User;
import java.sql.Connection;
import java.util.Arrays;

/**
 *
 * @author jesse
 */
public class StudentService {
    
    private final StudentDAO studentDAO;
    private final AuthService authService;
public StudentService(){
  this.studentDAO= new StudentDAO();
  this.authService= new AuthService();
}

//register a student using admin pin validation
public boolean registerStudentWithPIn(User user, String adminPin, Student student){
    try{
        return authService.registerStudent(user, adminPin, student);
    }catch(Exception e){
        System.err.println("Error registering student:"+ e.getMessage());
        return false;
    }
}    

//fetch student by id
public Student getStudentById(int StudentId, int adminId, Connection conn){
    try{
        return studentDAO.getStudentById(StudentId, adminId, conn);
    }catch(Exception e){
        System.err.println("Error fetching student by id:"+ e.getMessage());
        return null;
    }
}

public Student getStudentByUserId(int userId){
    try{
        return studentDAO.getStudentByUserId(userId);
    }catch(Exception e){
        System.err.println("Error fetching student by userId: "+ e.getMessage());
        return null;
    }
}

//fetch all students
public List<Student> getAllStudents(int adminId){
    try{
        return studentDAO.getAllStudents(adminId);
    }catch(Exception e){
        System.err.println("Error fetching all students:"+ e.getMessage());
        return null;
    }
}

//search student
public List<Student> searchStudents(int adminId, String Keyword){
    try{
    return studentDAO.searchStudentByAdmin(adminId, Keyword);
    }catch(Exception e){
        System.err.println("Error searching student"+ e.getMessage());
        return null;
    }
}

//update an existing student
public boolean updateStudent(Student student){
    try{
        return studentDAO.updateStudent(student);
    }catch(Exception e){
        System.err.println("Error updating student:"+ e.getMessage());
        return false;
    }
}

// delete student by id
public boolean deleteStudent(int studentId, int adminId, Connection conn){
    try{
        return studentDAO.deleteStudent(studentId, adminId, conn);
    }catch(Exception e){
        System.err.println("Error deleting student:"+ e.getMessage());
        return false;
    }
}

//update student status
public boolean updateStudentStatus(int studentId, String status, int adminId){
    if(!Arrays.asList("active", "inactive", "graduated", "archived").contains(status)){
        System.err.println("Invalid status:"+ status);
        return false;
    }
    return studentDAO.updateStatus(studentId, status, adminId);
}

public int countStudentByadmin(int adminId){
    return studentDAO.CountStudentByAdmin(adminId);
}
public int countActiveStudents(int adminId){
    return studentDAO.countActiveStudentsByAdmin(adminId);
}
}
