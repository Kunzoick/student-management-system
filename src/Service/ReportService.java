/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dto.CourseReportDTO;
import dto.StudentReportDTO;
import dto.StudentTranscriptDTO;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import model.Course;
import model.Grade;
import model.Student;
import java.sql.Connection;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.TranscriptArchive;
/**
 *
 * @author jesse
 */
public class ReportService {
    private GradeService gradeService;
    private StudentService studentService;
    private CourseService courseService;
    
    public ReportService(){
        this.gradeService= new GradeService();
        this.studentService= new StudentService();
        this.courseService= new CourseService();
    }
    //--generate student transcript
    public StudentTranscriptDTO generateStudentTranscript(int studentId, int adminId, Connection conn){
        Student student= studentService.getStudentById(studentId, adminId, conn);
        if(student== null) return null;
        
        List<Grade> grades= gradeService.getGradeByStudent(studentId, adminId);
        
        List<Course> allCourses= courseService.getCourseByAdminId(adminId);
        Map<Integer, Course> courseMap= allCourses.stream().collect(Collectors.toMap(Course::getCourseId, c -> c));
        
        List<StudentReportDTO> gradeDTOs= new ArrayList<>();
        for(Grade g : grades){
            Course course= courseMap.get(g.getCourseId());
            if(course != null){
            gradeDTOs.add(new StudentReportDTO( student.getStudentId(), student.getFirstName(), student.getLastName(), student.getGender(), student.getDepartment(), 
                                                course.getCourseId(), course.getCourseCode(), course.getCourseName(), g.getScore(), g.getGrade(), g.getGradePoint(), course.getUnits()));
        }
        }
        
        double gpa= gradeService.computeGPA(studentId, adminId, conn);
        return new StudentTranscriptDTO(student.getStudentId(), student.getFirstName(), student.getLastName(), student.getDepartment(), student.getGender(),
                                         student.getEnrollmentDate().atStartOfDay(), gpa, gradeDTOs);
    }
    
    //generate course performance
    public CourseReportDTO generateCourseReport(int courseId, int adminId){
        List<Grade> grades= gradeService.getGradeByCourse(courseId, adminId);
        Course course= courseService.getCourseById(courseId, adminId);
        
        int totalStudents= grades.size();
        double sumScore= 0;
        double sumGradePoint= 0;
        for(Grade g : grades){
            sumScore += g.getScore();
            sumGradePoint += g.getGradePoint();
        }
        double averageScore= totalStudents >0 ? sumScore / totalStudents : 0;
        double averageGradePoint= totalStudents >0 ? sumGradePoint / totalStudents : 0;
        return new CourseReportDTO(course.getCourseId(), course.getCourseCode(), course.getCourseName(), totalStudents, averageScore, averageGradePoint);
    }
    
    //generate all students grade report
    public List<StudentReportDTO> generateAllStudentGrade(int adminId, Connection conn){
        List<StudentReportDTO> allgrade= new ArrayList<>();
        List<Student> students= studentService.getAllStudents(adminId);
        
        List<Course> allCourses= courseService.getCourseByAdminId(adminId);
        Map<Integer, Course> courseMap= new HashMap<>();
        for(Course c : allCourses){
            courseMap.put(c.getCourseId(), c);
        }
        for(Student student : students){
            List<Grade> grades= gradeService.getGradeByStudent(student.getStudentId(), adminId);
            for(Grade g : grades){
                Course course= courseMap.get(g.getCourseId());//0(1)
                if(course !=null){
                allgrade.add(new StudentReportDTO(student.getStudentId(), student.getFirstName(), student.getLastName(), student.getDepartment(), student.getGender(),
                                                   course.getCourseId(), course.getCourseCode(), course.getCourseName(), g.getScore(), g.getGrade(), g.getGradePoint(), course.getUnits()));
            }
            }
        }
        return allgrade;
    }
    public void exportTranscriptArchiveToPDF(StudentTranscriptDTO transcript, TranscriptArchive archive){
        Document document= new Document(PageSize.A4, 50, 50, 50, 50);
        try{
            String safeName= (transcript.getFirstName()+ "_"+ transcript.getLastName()).replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName= "Transcript_" + safeName + ".pdf";
            File dir= new File("transcripts_archive");
            if(!dir.exists() && !dir.mkdirs()){
                throw new IOException("Failed to create directory: "+ dir.getAbsolutePath());
            }
            File outputFile= new File(dir, fileName);
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            
            document.open();
            
            Font titleFont= new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont= new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            Font normalFont= new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font boldFont= new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            //title
            Paragraph title= new Paragraph("Student Transcript Archive", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15f);
            document.add(title);
            document.add(Chunk.NEWLINE);
            //student info
            PdfPTable infoTable= new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.addCell(getCell("Name: "+ transcript.getFirstName()+ " "+ transcript.getLastName(), normalFont));
            infoTable.addCell(getCell("Gender: "+ transcript.getGender(), normalFont));
            infoTable.addCell(getCell("Department: "+ transcript.getDepartment(), normalFont));
            infoTable.addCell(getCell("GPA: "+ String.format("%.2f", transcript.getGpa()), boldFont));
            infoTable.addCell(getCell("Archive Type: "+ (archive.getArchivedType() != null ? archive.getArchivedType() : "N/A"), normalFont));
            infoTable.addCell(getCell("Enrollment: " + (transcript.getEnrollmentDate() != null ? transcript.getEnrollmentDate().toLocalDate().toString() : "N/A"), 
                    normalFont));
            infoTable.addCell(getCell("Archived: "+ (archive.getArchivedAt() != null ? archive.getArchivedAt().toString() : "N/A"), normalFont));
            
            document.add(infoTable);
            //transcript table
            Paragraph transcriptLabel= new Paragraph("Transcript Details", headerFont);
            transcriptLabel.setSpacingBefore(10f);
            transcriptLabel.setSpacingAfter(5f);
            document.add(transcriptLabel);
            
            PdfPTable table= new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{2, 3, 2, 2, 1, 1});
            addTableHeader(table, new String[]{"Course Code", "Course Title", "Score", "Grade", "Grade Point", "Unit"}, boldFont);
            if(transcript.getGrades() != null && !transcript.getGrades().isEmpty()){
                for(StudentReportDTO grade : transcript.getGrades()){
                    table.addCell(new Phrase(grade.getCourseCode(), normalFont));
                    table.addCell(new Phrase(grade.getCourseName(), normalFont));
                    table.addCell(new Phrase(String.valueOf(grade.getScore()), normalFont));
                    table.addCell(new Phrase(grade.getGrade(), normalFont));
                    table.addCell(new Phrase(String.format("%.2f", grade.getGradePoint()), normalFont));
                    table.addCell(new Phrase(String.format("%d", grade.getUnits()), normalFont));
                }
            }else{
                PdfPCell emptyCell= new PdfPCell(new Phrase("No grades available.", normalFont));
                emptyCell.setColspan(5);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(emptyCell);
            }
            document.add(table);
            //footer
            Paragraph footer= new Paragraph("Generated by Student Management System @ 2025", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20f);
            document.add(footer);
            
            document.close();
            JOptionPane.showMessageDialog(null, "Transcript exported successfully to:\n " + outputFile.getAbsolutePath());
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Error exporting transcript: "+ e.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to export transcript: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }finally{
            if(document.isOpen()){
                document.close();// always close document
            }
        }
    }
    private PdfPCell getCell(String text, Font font){
        PdfPCell cell= new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
    private void addTableHeader(PdfPTable table, String[] headers, Font headerFont){
        for(String header : headers){
            PdfPCell headerCell= new PdfPCell(new Phrase(header, headerFont));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
    }
} 