package models;

import commands.ReservationCommand;
import commands.CommandCaller;
import java.util.ArrayList;
import java.util.HashMap;
import services.Admin;
import services.Observer;
import services.DatabaseService;

/**
 *
 * @author enesaltin
 */
public class Customer extends User implements Observer{
    
    private String phoneNumber;
    private ArrayList<ArrayList<String>> desiredVoyages = new ArrayList<>();
    private HashMap<Voyage, ArrayList<Seat>> CustomersVoyageHashMap = new HashMap<>();
    private ArrayList<String[]> notifications = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    
    public Customer(String id,String name,String phoneNumber, String email, String password){
        super(id,name, email, password, "Customer");
        this.phoneNumber= phoneNumber;
        commandCaller = new CommandCaller();
        this.notifications = new ArrayList<>();
        this.desiredVoyages = new ArrayList<>();
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

    public ArrayList<String[]> getNotifications() {
        System.out.println("Bildirimler getiriliyor...");
        System.out.println("Müşteri ID: " + getId());
        
        // Veritabanından bildirimleri al
        try {
            notifications = DatabaseService.getNotificationsForUser(Integer.parseInt(getId()));
            System.out.println("Toplam " + notifications.size() + " bildirim bulundu");
        } catch (Exception e) {
            System.out.println("Bildirimler getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }

    public void setNotifications(ArrayList<String[]> notifications) {
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
    
    public void reservation(int id, int[] seatNumbers, String gender){
        for (int seatNumber: seatNumbers){
            reservation(id, seatNumber, gender);
        }
    }
    
    public void reservation(int id, int seatNumber, String gender){
        ReservationCommand reservation = new ReservationCommand(this, Voyage.getVoyageHashMap().get(id), seatNumber, gender);
        commandCaller.executeCommand(reservation);
    }
    
    public void cancelReservation(int id, int[] seatNumbers, String gender){
        for (int seatNumber: seatNumbers){
            cancelReservation(id, seatNumber, gender);
        }
    }
    
    public void cancelReservation(int id, int seatNumber, String gender){
        ReservationCommand cancelReservation = new ReservationCommand(this, Voyage.getVoyageHashMap().get(id), seatNumber, gender);
        commandCaller.executeCommand(cancelReservation);
    }
    
    public void undoLastCommand(){
        commandCaller.undoLast();
    }
    
    public void addDesiredVoyage(String type, String origin, String destination, String schedule) {
        System.out.println("İstenen sefer ekleniyor..."); // Debug için
        
        ArrayList<String> voyageInfo = new ArrayList<>();
        voyageInfo.add(type);
        voyageInfo.add(origin);
        voyageInfo.add(destination);
        voyageInfo.add(schedule);
        this.desiredVoyages.add(voyageInfo);
        
        System.out.println("İstenen seferler: " + this.desiredVoyages); // Debug için
        
        // Admin singleton instance'ını al
        Admin admin = Admin.getInstance();
        if (admin != null) {
            System.out.println("Admin bulundu: " + admin.getName() + " (ID: " + admin.getId() + ")");
            admin.addObserver(this);
            System.out.println("Admin'e observer olarak eklendi. Toplam observer sayısı: " + admin.getObservers().size());
        } else {
            System.out.println("UYARI: Admin bulunamadı! Bildirimler gönderilemeyecek!");
        }
    }

    @Override
    public void updateNotifications(Voyage voyage) {
        System.out.println("Bildirim güncelleniyor..."); // Debug için
        
        // Kullanıcının bu sefere abone olup olmadığını kontrol et
        boolean isInterested = false;
        for (ArrayList<String> desiredVoyageInfo : desiredVoyages) {
            String desiredType = desiredVoyageInfo.get(0);
            String desiredOrigin = desiredVoyageInfo.get(1);
            String desiredDestination = desiredVoyageInfo.get(2);
            
            if (voyage.getType().equalsIgnoreCase(desiredType) &&
                voyage.getOrigin().equalsIgnoreCase(desiredOrigin) &&
                voyage.getDestination().equalsIgnoreCase(desiredDestination)) {
                isInterested = true;
                break;
            }
        }
        
        if (isInterested) {
            System.out.println("\n=== Bildirim Gönderiliyor (İlgili Sefer Bulundu) ===");
            System.out.println("Müşteri: " + getName() + " (" + getId() + ")");
            
            String notification = "📢 [Yeni Sefer] " + voyage.getOrigin() + " - " + 
                voyage.getDestination() + " arasında yeni bir " + 
                voyage.getType() + " seferi eklendi. Tarih: " + voyage.getStartTime();
            
            try {
                int userId = Integer.parseInt(getId());
                System.out.println("User ID: " + userId);
                System.out.println("Bildirim mesajı: " + notification);
                
                // Bildirimi veritabanına ekle
                DatabaseService.addNotification(userId, notification);
                System.out.println("✅ Bildirim başarıyla veritabanına eklendi!");
                
                // Bildirimi kontrol et
                ArrayList<String[]> userNotifications = DatabaseService.getNotificationsForUser(userId);
                if (userNotifications != null && !userNotifications.isEmpty()) {
                    System.out.println("✅ Bildirim veritabanında bulundu!");
                    for (String[] notif : userNotifications) {
                        System.out.println("Bildirim Mesajı: " + notif[0]);
                        System.out.println("Bildirim Zamanı: " + notif[1]);
                    }
                } else {
                    System.out.println("❌ Bildirim veritabanında bulunamadı!");
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Kullanıcı ID'si geçersiz: " + getId());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("❌ Bildirim eklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Kullanıcı " + getName() + " bu seferle (" + voyage.getOrigin() + " -> " + voyage.getDestination() + ") ilgilenmiyor. Bildirim gönderilmedi.");
        }
    }

    public void addNotification(String notification) {
        System.out.println("Bildirim ekleniyor: " + notification);
        System.out.println("Müşteri ID: " + getId());
        
        // Veritabanına kaydet
        try {
            DatabaseService.addNotification(Integer.parseInt(getId()), notification);
            // Listeye ekle
            String[] notificationArray = {notification, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())};
            notifications.add(notificationArray);
            System.out.println("Bildirim başarıyla eklendi");
        } catch (Exception e) {
            System.out.println("Bildirim eklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearNotifications() {
        // Veritabanından bildirimleri temizle
        DatabaseService.clearNotificationsForUser(Integer.parseInt(getId()));
        notifications.clear();
    }

    public void makeReservation(Voyage voyage, int seatNumber) {
        System.out.println("Rezervasyon yapılıyor...");
        
        // Rezervasyon yap
        Reservation reservation = new Reservation(this, voyage, seatNumber);
        reservations.add(reservation);
        
        // Rezervasyon bildirimi oluştur
        String notification = "🎫 [Rezervasyon] " + voyage.getOrigin() + " - " + voyage.getDestination() + 
            " arasındaki " + voyage.getType() + " seferi için " + seatNumber + 
            " numaralı koltuk rezervasyonunuz yapıldı. Tarih: " + voyage.getStartTime();
        
        // Bildirimi kaydet
        addNotification(notification);
        System.out.println("Rezervasyon ve bildirim tamamlandı");
    }

}
