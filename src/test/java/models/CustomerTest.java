package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    
    private Customer customer;
    
    @BeforeEach
    void setUp() {
        // Her test öncesi yeni bir müşteri oluştur
        customer = new Customer("1", "Test User", "5551234567", "test@example.com", "password123");
    }
    
    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals("1", customer.getId());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("Test User", customer.getName());
        assertEquals("Customer", customer.getUser_type());
    }
    
    @Test
    void testCustomerModification() {
        // Müşteri bilgilerini güncelle
        customer.setEmail("new@example.com");
        customer.setName("New Name");
        customer.setPassword("newpassword123");
        
        // Değişiklikleri kontrol et
        assertEquals("new@example.com", customer.getEmail());
        assertEquals("New Name", customer.getName());
        assertEquals("newpassword123", customer.getPassword());
    }
    
    @Test
    void testCustomerType() {
        // Müşteri tipini kontrol et
        assertEquals("Customer", customer.getUser_type());
        assertTrue(customer.getUser_type().equals("Customer"));
        assertFalse(customer.getUser_type().equals("Admin"));
    }
} 