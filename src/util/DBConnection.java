/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author jesse
 */
public class DBConnection {
   private static final String URL;
   private static final String USER;
   private static final String PASSWORD;
   
   static{
       try(InputStream input= DBConnection.class.getClassLoader().getResourceAsStream("config.properties")){
           Properties props= new Properties();
           props.load(input);
           URL= props.getProperty("db.url");
           USER= props.getProperty("db.user");
           PASSWORD= props.getProperty("db.password");
       }catch(Exception e){
           throw new RuntimeException("MYSQL driver not found", e);
       }
   }
   
   //get a connection(singleton)
   public static Connection getConnection()throws SQLException{
       //if(connection== null || connection.isClosed()){
           try{
               Class.forName("com.mysql.cj.jdbc.Driver");//loads driver
               
               Properties props= new Properties();
               props.setProperty("user", USER);
               props.setProperty("password", PASSWORD);
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
