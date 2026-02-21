/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 *
 * @author jesse
 */
public class PasswordUtil {
    //hash paswword-- a SHA-256 with a salt for added security(not the best thou, convert to bcrypt for live projects)
    public static String hashPassword(String password){
        try{
            //generate a random salt
            byte[] salt= getSalt();
            
            //create messsageDigest instance for SHA-256
            MessageDigest md= MessageDigest.getInstance("SHA-256");
            md.update(salt);//add salt
            byte[] hashedPassword= md.digest(password.getBytes());
            
            //combine salt + hash and encode in base64 for storage
            String saltBase64= Base64.getEncoder().encodeToString(salt);
            String hashedBase64= Base64.getEncoder().encodeToString(hashedPassword);
            
            return saltBase64+ ":"+ hashedBase64;//stores as salt:hash
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    //verify password
    public static boolean verifyPassword(String password, String stored){
        try{
            String[] parts= stored.split(":");
            if(parts.length !=2) return false;
            
            byte[] salt= Base64.getDecoder().decode(parts[0]);
            byte[] storedHash= Base64.getDecoder().decode(parts[1]);
            
            //hash the input password with the stored salt
            MessageDigest md= MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] inputHash= md.digest(password.getBytes());
            
            //compare hashes
            return MessageDigest.isEqual(storedHash, inputHash);
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException("Error verifying password", e);
        }
    }
    
    //generate randow salt
    private static byte[] getSalt(){
        SecureRandom sr= new SecureRandom();
        byte[] salt= new byte[16];// 128-bit salt
        sr.nextBytes(salt);
        return salt;
    }
    
    public static class PasswordGenerator{
        private static final String Characters= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
        private static final int DEFAULT_LENGTH= 10;
        
        public static String generateRandomPassword(int length){
            SecureRandom random= new SecureRandom();
            StringBuilder sb= new StringBuilder(length);
            for(int i= 0; i< length; i++){
                sb.append(Characters.charAt(random.nextInt(Characters.length())));
            }
            return sb.toString();
        }
        public static String generateRandomPassword(){
            return generateRandomPassword(DEFAULT_LENGTH);
        }
    }
    
}
