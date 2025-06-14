package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VoyageTest {
    
    private Voyage voyage;
    
    @BeforeEach
    void setUp() {
        // Her test öncesi yeni bir sefer oluştur
        voyage = new Voyage(
            1, "Bus", "Test Firm", "Istanbul", "Ankara",
            "2024-03-20 10:00", "2024-03-20 14:00", 40, 100.0, "2-2"
        );
    }
    
    @Test
    void testVoyageCreation() {
        assertNotNull(voyage);
        assertEquals("Bus", voyage.getType());
        assertEquals("Test Firm", voyage.getFirm());
        assertEquals("Istanbul", voyage.getOrigin());
        assertEquals("Ankara", voyage.getDestination());
        assertEquals("2024-03-20 10:00", voyage.getStartTime());
        assertEquals("2024-03-20 14:00", voyage.getArrivalTime());
        assertEquals(100.0, voyage.getPrice());
        assertEquals(40, voyage.getSeatCount());
    }
    
    @Test
    void testSeatManagement() {
        // Koltuk sayısını kontrol et
        assertEquals(40, voyage.getSeatCount());
        
        // Koltuk düzenini kontrol et
        assertEquals("2-2", voyage.getSeatArrangement());
        
        // Koltukları kontrol et
        assertNotNull(voyage.getSeats());
        assertEquals(40, voyage.getSeats().size());
        
        // İlk koltuğu kontrol et
        Seat firstSeat = voyage.getSeats().get(0);
        assertEquals(1, firstSeat.getNumber());
    }
    
    @Test
    void testVoyageModification() {
        // Sefer bilgilerini güncelle
        voyage.setPrice(150.0);
        voyage.setFirm("New Firm");
        voyage.setOrigin("Izmir");
        voyage.setDestination("Antalya");
        
        // Değişiklikleri kontrol et
        assertEquals(150.0, voyage.getPrice());
        assertEquals("New Firm", voyage.getFirm());
        assertEquals("Izmir", voyage.getOrigin());
        assertEquals("Antalya", voyage.getDestination());
    }
} 