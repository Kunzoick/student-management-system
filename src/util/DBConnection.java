/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author jesse
 */
public class DBConnection {
   private static final String URL= "jdbc:mysql://127.0.0.1:3306/student_management";
   private static final String USER= "root";
   
   //private static Connection connection;
   
   //get a connection(singleton)
   public static Connection getConnection()throws SQLException{
       //if(connection== null || connection.isClosed()){
           try{
               Class.forName("com.mysql.cj.jdbc.Driver");//loads driver
               
               Properties props= new Properties();
               props.setProperty("user", USER);
               props.setProperty("useSSL", "false");
               props.setProperty("serverTimezone", "UTC");
               
               Connection conn= DriverManager.getConnection(URL, props);
               System.out.println("Database Connected successfully");
               return conn;
           }catch(ClassNotFoundException e){
              throw new SQLException("MySQL driver not found", e); 
           }
   }
}
