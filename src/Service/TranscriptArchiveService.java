/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dao.AdminDAO;
import dao.EnrollmentDAO;
import dao.GradeDAO;
import dao.StudentDAO;
import dao.TranscriptArchivedDAO;
import util.DBConnection;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Grade;
import model.Student;
import model.TranscriptArchive;
import util.TranscriptUtil;

/**
 *
 * @author jesse
 */
public class TranscriptArchiveService {
    private TranscriptArchivedDAO transcriptArchivedDAO;
    private GradeService gradeService;
    private StudentDAO studentDAO;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;
    private AdminDAO adminDAO;
    
    public TranscriptArchiveService(TranscriptArchivedDAO transcriptDAO, GradeService gradeService, StudentDAO studentDAO, EnrollmentDAO enrollmentDAO, AdminDAO adminDAO){
    this.transcriptArchivedDAO= transcriptDAO;
    this.gradeService= gradeService;
    this.studentDAO= studentDAO;
    this.enrollmentDAO= enrollmentDAO;
    this.adminDAO= adminDAO;
}
    //archive student(manual by admin)
    public boolean archiveStudentByAdmin(int studentId, int adminId) throws Exception{
        try(Connection conn= DBConnection.getConnection()){
            conn.setAutoCommit(false);
            
            try{
            Student student= studentDAO.getStudentById(studentId, adminId, conn);
            if(student== null){
                conn.rollback();
                return false;
            }
             
            double gpa= gradeService.computeGPA(studentId, adminId, conn);
            List<Grade> grades= gradeService.getGradeByStudent(studentId, adminId);
            String transcriptJson= TranscriptUtil.toJson(grades);
            
            TranscriptArchive ta= new TranscriptArchive();
            ta.setStudentId(studentId);
            ta.setAdminId(adminId);
            ta.setArchivedAdminId(adminId);
            ta.setArchivedType("ADMIN");
            ta.setFirstName(student.getFirstName());
            ta.setLastName(student.getLastName());
            ta.setGender(student.getGender());
            ta.setDepartment(student.getDepartment());
            ta.setEnrollmentDate(student.getEnrollmentDate().atStartOfDay());
            ta.setGPA(gpa);
            ta.setTranscriptJson(transcriptJson);
            
            boolean saved= transcriptArchivedDAO.archiveStudent(ta, conn);
            if(!saved){
                conn.rollback();
                return false;
            }
            //hard delete
            gradeDAO.deleteGradeByStudent(studentId, adminId, conn);
            enrollmentDAO.deleteEnrollmentByStudent(studentId, adminId, conn);
            studentDAO.deleteStudent(studentId, adminId, conn);
            conn.commit();
            return true;
        }catch(Exception e){
            conn.rollback();
            throw e;
        }finally{
                conn.setAutoCommit(true);
            }
        }
    }
    
    //archive student(automatic by system after 6yrs)
    public boolean archiveStudentBySystem(int studentId, int adminId, Connection conn) throws Exception{
        Student student= studentDAO.getStudentById(studentId, adminId, conn);
        if(student== null) return false;
        
        //check 6year eligibility
        if(student.getEnrollmentDate().plusYears(6).isAfter(LocalDate.now())){
            return false;//not yet 6years
        }
        double gpa= gradeService.computeGPA(studentId, adminId, conn);
        List<Grade> grades= gradeDAO.getGradesByStudent(studentId, adminId, conn);
        String transcriptJson= TranscriptUtil.toJson(grades);
        
        TranscriptArchive ta= new TranscriptArchive();
        ta.setStudentId(studentId);
            ta.setAdminId(adminId);
            ta.setArchivedAdminId(adminId);
            ta.setArchivedType("SYSTEM");
            ta.setFirstName(student.getFirstName());
            ta.setLastName(student.getLastName());
            ta.setGender(student.getGender());
            ta.setDepartment(student.getDepartment());
            ta.setEnrollmentDate(student.getEnrollmentDate().atStartOfDay());
            ta.setGPA(gpa);
            ta.setTranscriptJson(transcriptJson);
            
            boolean saved= transcriptArchivedDAO.archiveStudent(ta, conn);
            if(!saved){
                return false;
            }
            
            gradeDAO.deleteGradeByStudent(studentId, adminId, conn);
            enrollmentDAO.deleteEnrollmentByStudent(studentId, adminId, conn);
            studentDAO.deleteStudent(studentId, adminId, conn);
            return true;
    }
    
    //get all archived students under one admin
    public List<TranscriptArchive> getArchivedStudents(int adminId) throws Exception{
        try(Connection conn= DBConnection.getConnection()){
            return transcriptArchivedDAO.getArchivedStudentByAdmin(adminId, conn);
        }
    }
    public TranscriptArchive getArchivedStudentById(int adminId, int id) throws Exception{
        try(Connection conn= DBConnection.getConnection()){
            return transcriptArchivedDAO.getArchiveStudentById(adminId, id, conn);
        }catch(Exception e){
            System.err.println("Error fetching archived student with this Id: "+ e.getMessage());
            return null;
        }       
    }
    
    //auto archive eligible students
    public void autoArchiveEligibleStudents(int adminId, Connection conn) throws Exception{
            List<Student> students= studentDAO.getStudentForAutoArchive(adminId, conn);
            for(Student student : students){
                boolean archived= archiveStudentBySystem(student.getStudentId(),adminId,conn);
                if(!archived){
                    conn.rollback();
                    throw new Exception("Failed to auto archive studentId"+ student.getStudentId());
                }
            }
    }
    
    //auto archive for all admins
    //manual bulk archive
    public Map<Integer, String> autoArchiveForAllAdmins() throws Exception{
        Map<Integer, String> results= new HashMap<>();
        try(Connection conn= DBConnection.getConnection()){
            List<Integer> adminIds= adminDAO.getAllAdmins(conn);
            for(int adminId : adminIds){
                try{
                autoArchiveEligibleStudents(adminId, conn);
                results.put(adminId, "SUCCESS");
                }catch(Exception e){
                    String errorMsg= "Auto-archive failed: "+ e.getMessage();
                    System.err.println("Admin "+ adminId+ ": "+ errorMsg);
                    results.put(adminId, errorMsg);
                }
            }
        }
        return results;
    }
    //search Archived
public List<TranscriptArchive> searchArchived(int adminId, String Keyword){
    try(Connection conn= DBConnection.getConnection()){
    return transcriptArchivedDAO.searchArchivedByAdmin(adminId, Keyword, conn);
    }catch(Exception e){
        System.err.println("Error searching student"+ e.getMessage());
        return null;
    }
}
public boolean deleteArchive(int id) throws Exception{
    try(Connection conn= DBConnection.getConnection()){
        return transcriptArchivedDAO.deleteTranscriptArchive(id, conn);
    }
}
}
