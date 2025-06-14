package services;

import commands.AddVoyageCommand;
import factorys.VoyageFactory;
import commands.UpdateVoyageCommand;
import commands.DeleteVoyageCommand;
import commands.CommandCaller;
import java.util.ArrayList;
import models.User;
import models.Voyage;
import models.Customer;

/**
 *
 * @author enesaltin
 */
public class Admin extends User implements Subject{
    
    private static Admin instance = null;
    private String adminCode; 
    private ArrayList<Observer> observers = new ArrayList<>();

    
 
    // Protected constructor for inheritance
    public Admin(String id, String adminCode, String name, String email, String password) {
        super(id, name, email, password, "Admin");
        this.adminCode = adminCode;
        
        commandCaller = new CommandCaller();
        this.observers = new ArrayList<>();
    }
    
    // Default constructor for inheritance
    protected Admin() {
        super();
        commandCaller = new CommandCaller();
        this.observers = new ArrayList<>();
    }
    
    public static Admin getInstance(String id, String adminCode, String name, String email, String password) {
        if (instance == null) {
            instance = new Admin(id, adminCode, name, email, password);
        }
        return instance;
    }
    
    public static Admin getInstance() {
        return instance;
    }

    public ArrayList<Observer> getObservers() {
        return observers;
    }
    
   
 
    
    
    public void register(String id, String adminCode, String name, String email, String password) {
        System.out.println("\n=== Admin Kaydı Başlıyor ===");
        System.out.println("Admin Kodu: " + adminCode);
        System.out.println("İsim: " + name);
        System.out.println("Email: " + email);

        // Singleton instance'ı oluştur
        instance = getInstance(id, adminCode, name, email, password);
        
        // HashMap'e ekle
        User.getUsersHashMap().put(instance.getEmail(), instance);
        System.out.println("Admin HashMap'e eklendi. Toplam kullanıcı sayısı: " + User.getUsersHashMap().size());
        
        // Veritabanına kaydet
        try {
            DatabaseService.addUser(instance);  // Admin instance'ını direkt olarak kullan
            System.out.println("Admin veritabanına kaydedildi");
        } catch (Exception e) {
            System.err.println("Admin veritabanına kaydedilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== Admin Kaydı Tamamlandı ===\n");
    }
    
    public void addVoyage(int voyageId, String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        System.out.println("\n=== Yeni Sefer Ekleme Başladı ===");
        System.out.println("Admin ID: " + getId());
        System.out.println("Observer sayısı: " + observers.size());
        
        // 1. Veritabanına kaydet
        int dbVoyageId = DatabaseService.addVoyage(type, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
        System.out.println("Sefer veritabanına kaydedildi. ID: " + dbVoyageId);

        // 2. Komut deseni ile de işlemi gerçekleştir
        Voyage voyage = VoyageFactory.createVoyage(voyageId, type, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
        AddVoyageCommand addVoyage = new AddVoyageCommand(voyage, this);
        commandCaller.executeCommand(addVoyage);
        System.out.println("Sefer komut deseni ile eklendi");
        
        // 3. Observer pattern ile bildirimleri gönder
        System.out.println("Bildirimler gönderiliyor...");
        notifyObservers(voyage);
        
        System.out.println("=== Yeni Sefer Ekleme Tamamlandı ===\n");
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
        System.out.println("\n=== Bildirim Gönderme Başladı ===");
        System.out.println("Toplam observer sayısı: " + observers.size());
        
        for (Observer observer : observers) {
            try {
                if (observer instanceof Customer) {
                    Customer customer = (Customer) observer;
                    System.out.println("Observer'a bildirim gönderiliyor: " + customer.getName());
                    observer.updateNotifications(voyage);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to notify observer: " + e.getMessage());
            }
        }
        System.out.println("=== Bildirim Gönderme Tamamlandı ===\n");
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    public static Admin getAdminByEmail(String email) {
        if (instance != null && instance.getEmail().equals(email)) {
            return instance;
        }
        return null;
    }
}
