/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import model.Grade;
import java.lang.reflect.Type;

/**
 
 * @author jesse
 */
public class TranscriptUtil {
    private static final Gson gson= new Gson();
    public static String toJson(List<Grade> grades){
        return gson.toJson(grades);
    }
    public static List<Grade> fromJson(String json){
        Type listType= new TypeToken<List<Grade>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
