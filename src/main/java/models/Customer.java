package models;

import commands.ReservationCommand;
import commands.CommandCaller;
import java.util.ArrayList;
import java.util.HashMap;
import services.Admin;
import services.Observer;

/**
 *
 * @author enesaltin
 */
public class Customer extends User implements Observer{
    
    private String phoneNumber;
    private final ArrayList<ArrayList<String>> desiredVoyages = new ArrayList<>();
    private HashMap<Voyage, ArrayList<Seat>> CustomersVoyageHashMap = new HashMap<>();
    private ArrayList<String> notifications = new ArrayList<>();
    
    public Customer(String id,String name,String phoneNumber, String email, String password){
        super(id,name, email, password, "Customer");
        this.phoneNumber= phoneNumber;
        commandCaller = new CommandCaller();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashMap<Voyage, ArrayList<Seat>> getCustomersVoyageHashMap() {
        return CustomersVoyageHashMap;
    }

    public void setCustomersVoyageHashMap(HashMap<Voyage, ArrayList<Seat>> CustomersVoyageHashMap) {
        this.CustomersVoyageHashMap = CustomersVoyageHashMap;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }
    
    public ArrayList<ArrayList<String>> getDesiredVoyages() {
        return desiredVoyages;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    /// @param name/
    /// @param phoneNumber
    /// @param email
    /// @param password
    
    public void register(String id,String name, String phoneNumber, String email, String password){
        Customer customer = new Customer(id,name, phoneNumber, email, password);
        getUsersHashMap().put(customer.getEmail(), customer);
    }
    
    public void reservation(int id, int[] seatNumbers){
        for (int seatNumber: seatNumbers){
            reservation(id, seatNumber);
        }
    }
    
    public void reservation(int id, int seatNumber){
        ReservationCommand reservation = new ReservationCommand(this, Voyage.getVoyageHashMap().get(id), seatNumber);
        commandCaller.executeCommand(reservation);
    }
    
    public void cancelReservation(int id, int[] seatNumbers){
        for (int seatNumber: seatNumbers){
            cancelReservation(id, seatNumber);
        }
    }
    
    public void cancelReservation(int id, int seatNumber){
        ReservationCommand cancelReservation = new ReservationCommand(this, Voyage.getVoyageHashMap().get(id), seatNumber);
        commandCaller.executeCommand(cancelReservation);
    }
    
    public void undoLastCommand(){
        commandCaller.undoLast();
    }
    
    public void addDesiredVoyage(String type, String origin, String destination, String schedule){
        ArrayList<String> voyageInfo = new ArrayList<>();
        voyageInfo.add(type);
        voyageInfo.add(origin);
        voyageInfo.add(destination);
        voyageInfo.add(schedule);
        this.desiredVoyages.add(voyageInfo);
        Admin.getObservers().add(this);
        
    }
    
    @Override
    public void updateNotifications(Voyage voyage) {
        
        String notification = "📢 [Bildirim] Sayın " + getName() +
            ", " + voyage.getOrigin() + " çıkışlı ve " + voyage.getStartTime()+
            " tarihli yeni bir sefer bulundu.";
        
        notifications.add(notification);        
    }

    
}
