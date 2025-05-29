/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import commands.CommandCaller;
import java.util.HashMap;

/**
 *
 * @author enesaltin
 */
public class User {
    private  String id;
    private String name;
    private String email;
    private String password;
    private String user_type;
    private static HashMap<String, User> UsersHashMap = new HashMap<>();
    public CommandCaller commandCaller;
    
    public User(String id,String name, String email, String password, String user_type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.user_type = user_type;
    }
    
    public User(){
        
    }

    public User login(String email, int password){
        
        if (UsersHashMap.get(email).getPassword().equals(password)){
            System.out.println("giris basarılı");
            return UsersHashMap.get(email);
        }
        
        {
            throw new IllegalArgumentException("Geçersiz parola!");
        }
    }
    
    public void displayVoyages(){
         
    }
    public void searchVoyages(){
        
    }   
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public static HashMap<String, User> getUsersHashMap() {
        return UsersHashMap;
    }

    
}
