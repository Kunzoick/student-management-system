/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author jesse
 */
public class NotificationDTO {
    private String message;
    private int userId;
    private int triggeredById; // who trigged the response
    private String type;
    private String source;
    private long timestamp;
    
    public NotificationDTO(String message, int userId, int triggeredById, String type, String source){
        this.message= message;
        this.userId= userId;
        this.triggeredById= triggeredById;
        this.type= (type == null ? "INFO" : type);
        this.source= (source == null ? "SYSTEM" : source);
        this.timestamp= System.currentTimeMillis();
    }
    //getters
    public String getMessage(){ return message;}
    public int getUserId(){ return userId;}
    public int getTriggeredById(){ return triggeredById;}
    public String getType(){ return type;}
    public String getSource(){ return source;}
    public long getTimeStamp(){ return timestamp;}
    
    //returns a ready to display formatted string 
    public String toDisplay(){
        java.time.LocalDateTime ldt= java.time.Instant.ofEpochMilli(timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        String time= ldt.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:a"));
        String prefix= source.equalsIgnoreCase("ADMIN") ? "Admin" : "System";
        return String.format("[%s] %s > %s", time, prefix, message);
    }
    
}
