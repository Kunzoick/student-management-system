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
public class User {
    private int id;
    private String username;
    private String password;// stores hashed password
    private String role;// admin or student
    private String firstName;
    private String lastName;
    private LocalDateTime lastLoginDate;
    private boolean firstLogin= true;
    private boolean isDefaultPassword= true;
    private String remarks;
   
    //getters and setters
    public int getId(){ return id;}
    public void setId(int id){ this.id= id;}
    
    public String getUsername(){ return username;}
    public void setUsername(String username){ this.username= username;}
    
    public String getPassword(){ return password;}
    public void setPassword(String password){ this.password= password;}
    
    public String getRole(){ return role;}
    public void setRole(String role){ this.role= role;}
    
    public String getFirstName(){ return firstName;}
    public void setFirstName(String firstName){ this.firstName= firstName;}
    
    public String getLastName(){ return lastName;}
    public void setLastName(String lastName){ this.lastName= lastName;}
    
    public LocalDateTime getLastLoginDate(){ return lastLoginDate;}
    public void setLastLoginDate(LocalDateTime lastLoginDate){ this.lastLoginDate= lastLoginDate;}
    
    public boolean getFirstLogin(){ return firstLogin;}
    public void setFirstLogin(boolean firstLogin){ this.firstLogin= firstLogin;}
    
    public boolean getIsDefaultPassword(){ return isDefaultPassword;}
    public void setIsDefaultPassword(boolean isDefaultPassword){ this.isDefaultPassword= isDefaultPassword;}
    
    public String getRemarks(){ return remarks;}
    public void setRemarks(String remarks){ this.remarks= remarks;}
    
    @Override
    public String toString(){
        return username+ "("+ role+ ")";
    }
    
}
