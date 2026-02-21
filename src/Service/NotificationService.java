/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import dto.NotificationDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 *
 * @author jesse
 */
public class NotificationService {
    private static final int DEFAULT_MAX_NOTIFICATIONS= 40;
    private final int maxNotifications;
    private final Map<Integer, Deque<NotificationDTO>> notifications;
    
    //singleton
    private static final NotificationService INSTANCE= new NotificationService(DEFAULT_MAX_NOTIFICATIONS);
    
    public static NotificationService getInstance(){
        return INSTANCE;
    }
    private NotificationService(int maxNotifications){
        this.maxNotifications= maxNotifications;
        this.notifications= new ConcurrentHashMap<>();
    }
    //logging
    private void log(String message, int userId, int triggeredById, String type, String source){
        NotificationDTO dto= new NotificationDTO(message, userId, triggeredById, type, source);
        
        Deque<NotificationDTO> deque= notifications.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());
        synchronized(deque){
        deque.addLast(dto);
        
        //maintain
        while(deque.size() > maxNotifications){
            deque.removeFirst();
        }
        }
    }
    //API shortcuts
    public void info(String message, int userId){
        log(message, userId, userId, "INFO", "SYSTEM");
    }
    public void alert(String message, int userId){
        log(message, userId, userId, "ALERT", "SYSTEM");
    }
    
    //student
    public void studentselfRegistered(int studentId, int adminId){
        String msg= "Your account has been successfully created.";
        log(msg, studentId, studentId, "INFO", "STUDENT");
    }
    //admin
    public void studentRegisteredByAdmin(int studentId, int adminId){
        String msg= "An admin created your student account.";
        log(msg, studentId, adminId, "INFO", "ADMIN");
    }
    public void studentEnrolledbyAdmin(int studentId, int courseId, int adminId){
        String msg= "Admin enrolled you in a course Id: "+ courseId;
        log(msg, studentId, adminId, "INFO", "ADMIN");
    }
    public void gradeAssign(int studentId, int courseId, double score, int adminId){
        String msg= String.format("Your grade for Course %d has been updated (score: %.2f).", courseId, score);
        log(msg, studentId, adminId, "INFO", "ADMIN");
    }
    public void adminUpdate(int affectedStudentId, String message, int adminId){
        log(message, affectedStudentId, adminId, "ALERT", "ADMIN");
    }
    public void adminSelfAction(int adminId, String message){
        log(message, adminId, adminId, "INFO", "ADMIN");
    }
    public void adminSystemEvent(int adminId, String message){
        log(message, adminId, 0, "INFO", "SYSTEM");
    }
    //student
    public void studentSelfEnrolled(int studentId, int courseId){
        String msg= "You successfully enrolled in Course Id: "+ courseId;
        log(msg, studentId, studentId, "INFO", "STUDENT");
    }
    public void stuentSelfAction(int studentId, String message){
        log(message, studentId, studentId, "INFO", "STUDENT");
    }
    
    //API
    public List<String> getRecentNotificationMessages(int userId, int limit){
        Deque<NotificationDTO> deque= notifications.getOrDefault(userId, new ConcurrentLinkedDeque<>());
        if(deque.isEmpty()) return Collections.emptyList();
        
        return deque.stream().skip(Math.max(0, deque.size() - limit))
                .map(NotificationDTO::toDisplay)
                .collect(Collectors.toList());
    }
    public List<String> getRecentNotifications(int userId){
        return getRecentNotificationMessages(userId, 10);
    }
    public void clearNotification(int userId){
        notifications.remove(userId);
    }
    public void clearAll(){
        notifications.clear();
    }
}
