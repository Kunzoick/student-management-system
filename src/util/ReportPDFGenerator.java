/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import dto.StudentReportDTO;
import dto.StudentTranscriptDTO;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 *
 * @author jesse
 */
public class ReportPDFGenerator {
    public static void exportStudentTranscript(StudentTranscriptDTO transcript, String filePath){
        Document document= new Document(PageSize.A4, 50, 50, 50, 50);
        try{
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        Font titleFont= FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title= new Paragraph("Student Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));//blank line
       
        //student info
        Font infoFont= FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph studentInfo= new Paragraph( "Student Id: "+ transcript.getStudentId()+ "\n" +
                "Name: "+ transcript.getFirstName()+ " "+ transcript.getLastName()+ "\n" +
                "Gender: "+ transcript.getGender()+ "\n" +
                "Department: "+ transcript.getDepartment()+ "\n" +
                "Enrollment Date: "+ transcript.getEnrollmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+ "\n" +
                "GPA: "+ String.format("%.2f", transcript.getGpa()), infoFont);
        document.add(studentInfo);
        document.add(new Paragraph(" "));
        
        //table for grades
        PdfPTable table= new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        //column widths
        float[] columnWidths= {1f, 2f, 2f, 1f, 1f, 1f}; 
        table.setWidths(columnWidths);
        //table header
        Font headFont= FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        String[] headers= {"#", "Course Code", "Course Name", "Score", "Grade", "Grade Point"};
        for(String header : headers){
            PdfPCell cell= new PdfPCell(new Phrase(header, headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
        }
        List<StudentReportDTO> grades= transcript.getGrades();
        int index= 1;
        for(StudentReportDTO g : grades){
            table.addCell(centerCell(String.valueOf(index++)));
            table.addCell(centerCell(g.getCourseCode()));
            table.addCell(centerCell(g.getCourseName()));
            table.addCell(centerCell(String.format("%.2f", g.getScore())));
            table.addCell(centerCell(g.getGrade()));
            table.addCell(centerCell(String.format("%.2f",g.getGradePoint())));
        }
        document.add(table);
        document.close();       
    }catch(Exception e){
        e.printStackTrace();
        System.err.println("Error generating PDF: "+ e.getMessage());
    }
    }
    private static PdfPCell centerCell(String text){
        PdfPCell cell= new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell; 
    }
}
