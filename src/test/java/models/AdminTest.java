package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import services.Admin;

public class AdminTest {
    
    private Admin admin;
    
    @BeforeEach
    void setUp() {
        // Her test öncesi yeni bir admin oluştur
        admin = new Admin("1", "ADM001", "Admin User", "admin@example.com", "admin123");
    }
    
    @Test
    void testAdminCreation() {
        assertNotNull(admin);
        assertEquals("1", admin.getId());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("Admin User", admin.getName());
        assertEquals("Admin", admin.getUser_type());
    }
    
    @Test
    void testAdminModification() {
        // Admin bilgilerini güncelle
        admin.setEmail("newadmin@example.com");
        admin.setName("New Admin");
        admin.setPassword("newadmin123");
        
        // Değişiklikleri kontrol et
        assertEquals("newadmin@example.com", admin.getEmail());
        assertEquals("New Admin", admin.getName());
        assertEquals("newadmin123", admin.getPassword());
    }
    
    @Test
    void testAdminType() {
        // Admin tipini kontrol et
        assertEquals("Admin", admin.getUser_type());
        assertTrue(admin.getUser_type().equals("Admin"));
        assertFalse(admin.getUser_type().equals("Customer"));
    }
} 