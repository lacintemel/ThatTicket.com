package services;

import models.Customer;
import models.Voyage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Statement;

public class DatabaseServiceTest {
    
    @BeforeEach
    void setUp() {
        System.out.println("\n=== Test başlıyor ===");
        // Her test öncesi veritabanını temizle ve yeniden başlat
        DatabaseService.initialize();
        
        // Tüm tabloları temizle
        try (Statement stmt = DatabaseService.getConnection().createStatement()) {
            stmt.execute("DELETE FROM reservations");
            stmt.execute("DELETE FROM voyages");
            stmt.execute("DELETE FROM users");
            stmt.execute("DELETE FROM notifications");
            System.out.println("Veritabanı temizlendi");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database: " + e.getMessage());
        }
    }
    
    @Test
    void testUserRegistration() {
        System.out.println("\n--- Kullanıcı Kayıt Testi ---");
        // Test kullanıcı kaydı
        String email = "test1@test.com";  // Benzersiz email
        String password = "test123";
        String name = "Test User";
        
        // Kullanıcıyı kaydet
        Customer user = new Customer("1", name, "5551234567", email, password);
        DatabaseService.addUser(user);
        System.out.println("Kullanıcı kaydedildi: " + email);
        
        // Kullanıcının kaydedildiğini doğrula
        Customer savedUser = (Customer) DatabaseService.getUserByEmail(email);
        assertNotNull(savedUser);
        assertEquals(email, savedUser.getEmail());
        assertEquals(name, savedUser.getName());
        System.out.println("Kullanıcı doğrulaması başarılı");
    }
    
    @Test
    void testVoyageCreation() {
        System.out.println("\n--- Sefer Oluşturma Testi ---");
        // Test sefer oluşturma
        String type = "Bus";
        String firm = "Test Firm";
        String origin = "Istanbul";
        String destination = "Ankara";
        String startTime = "2024-03-20 10:00";
        String arrivalTime = "2024-03-20 14:00";
        int seatCount = 40;
        double price = 100.0;
        String seatArrangement = "2-2";
        
        // Seferi oluştur ve veritabanına kaydet
        int voyageId = DatabaseService.addVoyage(type, firm, origin, destination, 
            startTime, arrivalTime, seatCount, price, seatArrangement);
        System.out.println("Sefer oluşturuldu. ID: " + voyageId);
        
        // Seferin kaydedildiğini doğrula
        Voyage savedVoyage = DatabaseService.getVoyageById(voyageId);
        assertNotNull(savedVoyage);
        assertEquals(origin, savedVoyage.getOrigin());
        assertEquals(destination, savedVoyage.getDestination());
        assertEquals(price, savedVoyage.getPrice());
        System.out.println("Sefer doğrulaması başarılı");
    }
    
    @Test
    void testReservationCreation() {
        System.out.println("\n--- Rezervasyon Oluşturma Testi ---");
        // Test rezervasyon oluşturma
        // Önce bir kullanıcı oluştur
        String email = "test2@test.com";  // Benzersiz email
        String password = "test123";
        String name = "Reservation Test User";
        Customer user = new Customer("2", name, "5551234567", email, password);
        DatabaseService.addUser(user);
        System.out.println("Test kullanıcısı oluşturuldu: " + email);
        
        // Bir sefer oluştur
        int voyageId = DatabaseService.addVoyage("Bus", "Test Firm", "Istanbul", "Ankara",
            "2024-03-20 10:00", "2024-03-20 14:00", 40, 100.0, "2-2");
        System.out.println("Test seferi oluşturuldu. ID: " + voyageId);
        
        // Rezervasyon yap
        int seatNumber = 1;
        String gender = "Male";
        DatabaseService.addReservation(
            Integer.parseInt(user.getId()),
            voyageId,
            seatNumber,
            gender
        );
        System.out.println("Rezervasyon yapıldı. Koltuk: " + seatNumber);
        
        // Rezervasyonun yapıldığını doğrula
        var reservations = DatabaseService.getReservationsForUser(Integer.parseInt(user.getId()));
        assertFalse(reservations.isEmpty());
        assertEquals(voyageId, reservations.get(0).voyageId);
        assertEquals(seatNumber, reservations.get(0).seatNumber);
        System.out.println("Rezervasyon doğrulaması başarılı");
    }
} 