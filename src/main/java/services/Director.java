/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import java.util.ArrayList;

/**
 *
 * @author enesaltin
 */
public class Director extends Admin{
    
    String id = "director";
    String password = "director";
    private static ArrayList<String> adminCodes = new ArrayList<>();

    
    public Director(){
        super();
        
         
       adminCodes.add("ADM001");
       adminCodes.add("ADM002");
       adminCodes.add("ADM003");
      
    }

    public static ArrayList<String> getAdminCodes() {
        return adminCodes;
    }

    public static void setAdminCodes(ArrayList<String> adminCodes) {
        Director.adminCodes = adminCodes;
    }
    
    
    
    public static void addAdminCode(String adminCode){
        adminCodes.add(adminCode);
    }
     
    public void changeId(String newId){
        this.id = newId;
    }
    
    public void changePassword(String newPassword){
        this.password = newPassword;
    }
    
}
