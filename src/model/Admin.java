/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author jesse
 */
public class Admin {
    private int adminId;
    private int userId;
    private String pin;//unique code for registering students
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    //getters & setters
    public int getAdminId(){ return adminId;}
    public void setAdminId(int adminId){ this.adminId= adminId;}
    
    public int getUserId(){ return userId;}
    public void setUserId(int userId){ this.userId= userId;}
    
    public String getPin(){ return pin;}
    public void setPin(String pin){ this.pin= pin;}
    
    public LocalDateTime getCreatedAt(){ return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt= createdAt;}
    
    public LocalDateTime getUpdatedAt(){ return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt= updatedAt;}
    
    @Override
    public String toString(){
        return "AdminID: "+ adminId+ "|PIN:"+ pin;
    }
    
}
