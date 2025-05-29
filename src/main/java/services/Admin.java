/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import commands.AddVoyageCommand;
import factorys.VoyageFactory;
import commands.UpdateVoyageCommand;
import commands.DeleteVoyageCommand;
import commands.CommandCaller;
import java.util.ArrayList;
import models.Customer;
import models.User;
import models.Voyage;

/**
 *
 * @author enesaltin
 */
public class Admin extends User implements Subject{
    
    private String adminCode; 
    private static ArrayList<Observer> observers = new ArrayList<>();

    
 
    public Admin(String id,String adminCode, String name, String email, String password){
        super(id,name, email, password, "Admin");
        this.adminCode = adminCode;
        
        commandCaller = new CommandCaller();
       

    }
    
    public Admin(){
        super();
    }

    public static ArrayList<Observer> getObservers() {
        return observers;
    }
    
   
 
    
    
    public void register(String id,String adminCode, String name, String email, String password){
        if( Director.getAdminCodes().contains(adminCode) ){
            Director.getAdminCodes().remove(adminCode);
            Admin admin = new Admin(id,adminCode, name, email, password);
            User.getUsersHashMap().put(admin.getEmail(), admin);
        }
        else{
            throw new IllegalArgumentException("Geçersiz admin kodu!");
        }
    }
    
    public void addVoyage(int voyageId,String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement){
        // 1. Veritabanına kaydet
        DatabaseService.addVoyage(type, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);

        // 2. Komut deseni ile de işlemi gerçekleştir
        Voyage voyage = VoyageFactory.createVoyage(voyageId, type, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
        AddVoyageCommand addVoyage = new AddVoyageCommand(voyage, this);
        commandCaller.executeCommand(addVoyage);
    }
    
    public void deleteVoyage(int id){        
        Voyage voyage = Voyage.getVoyageHashMap().get(id);
        DeleteVoyageCommand deleteVoyage = new DeleteVoyageCommand(voyage, this);
        commandCaller.executeCommand(deleteVoyage);
    }
    
    public void updateVoyage(int voyageId,int id, String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount,double price, String seatArrangement){
        Voyage voyage = VoyageFactory.createVoyage(voyageId, type, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
        UpdateVoyageCommand updateVoyage = new UpdateVoyageCommand(voyage, this, id);
        commandCaller.executeCommand(updateVoyage);
    }
    
    public void undoLastCommand(){
        commandCaller.undoLast();
    }

    @Override
    public void notifyObservers(Voyage voyage) {
        String type = voyage.getType();
        String origin = voyage.getOrigin();
        String destination = voyage.getDestination();
        String startTime = voyage.getStartTime();
        
        for (Observer o : Admin.getObservers()){
            Customer customer = (Customer) o;
            for(ArrayList<String> desires : customer.getDesiredVoyages()){
                if (type.equalsIgnoreCase(desires.get(0)) && origin.equalsIgnoreCase(desires.get(1)) 
                           && destination.equalsIgnoreCase(desires.get(2)) && startTime.equalsIgnoreCase(desires.get(3))) {
                    o.updateNotifications(voyage);
                }
            }
        }
    }
    
}
