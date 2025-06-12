// src/main/java/services/DatabaseService.java
package services;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import models.Voyage;
import factorys.VoyageFactory;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:data/transport.db";
    private static Connection connection;
    private static HashMap<String, User> usersCache = new HashMap<>();

    // Veritabanı başlatma
    public static void initialize() {
        try {
            // SQLite sürücüsünü yükle
            Class.forName("org.sqlite.JDBC");
            
            // Data klasörünü oluştur
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Veritabanı bağlantısını oluştur
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            loadUsersIntoCache();
            loadVoyagesIntoCache();
            System.out.println("Veritabanı başarıyla başlatıldı!");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Tabloları oluştur
    private static void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Users tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "user_type TEXT NOT NULL)");

            // Voyages tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS voyages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT NOT NULL," +
                "firm TEXT NOT NULL," +
                "origin TEXT NOT NULL," +
                "destination TEXT NOT NULL," +
                "start_time TEXT NOT NULL," +
                "arrival_time TEXT NOT NULL," +
                "seat_count INTEGER NOT NULL," +
                "price REAL NOT NULL," +
                "seat_arrangement TEXT NOT NULL)");

            // Reservations tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "voyage_id INTEGER," +
                "seat_number INTEGER," +
                "gender TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id)," +
                "FOREIGN KEY (voyage_id) REFERENCES voyages(id))");

            // Notifications tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "message TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcıları cache'e yükle
    private static void loadUsersIntoCache() {
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user;
                if ("admin".equalsIgnoreCase(rs.getString("user_type"))) {
                    user = Admin.getInstance(
                        rs.getString("id"),
                        "1111",
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                } else {
                    user = new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("user_type")
                    );
                }
                usersCache.put(user.getEmail(), user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Voyage'ları cache'e yükle
    private static void loadVoyagesIntoCache() {
        String sql = "SELECT * FROM voyages";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int voyageId = rs.getInt("id");
                String type = rs.getString("type");
                String firm = rs.getString("firm");
                String origin = rs.getString("origin");
                String destination = rs.getString("destination");
                String startTime = rs.getString("start_time");
                String arrivalTime = rs.getString("arrival_time");
                int seatCount = rs.getInt("seat_count");
                double price = rs.getDouble("price");
                String seatArrangement = rs.getString("seat_arrangement");

                Voyage voyage = VoyageFactory.createVoyage(
                    voyageId,
                    type,
                    firm,
                    origin,
                    destination,
                    startTime,
                    arrivalTime,
                    seatCount,
                    price,
                    seatArrangement
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcı ekle
    public static boolean addUser(User user) {
        String sql = "INSERT INTO users (name, email, password, user_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUser_type());
            pstmt.executeUpdate();
            
            // Cache'i güncelle
            usersCache.put(user.getEmail(), user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kullanıcı güncelle
    public static boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, user_type = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getUser_type());
            pstmt.executeUpdate();
            
            // Cache'i güncelle
            usersCache.put(user.getEmail(), user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kullanıcı sil
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
            // Cache'den kaldır
            usersCache.values().removeIf(user -> user.getId().equals(userId));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Email ile kullanıcı getir
    public static User getUserByEmail(String email) {
        // Önce cache'den kontrol et
        User cachedUser = usersCache.get(email);
        if (cachedUser != null) {
            return cachedUser;
        }

        // Cache'de yoksa veritabanından al
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user;
                if ("admin".equalsIgnoreCase(rs.getString("user_type"))) {
                    user = Admin.getInstance(
                        rs.getString("id"),
                        "1111",
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                } else {
                    user = new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("user_type")
                    );
                }
                usersCache.put(email, user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tüm kullanıcıları getir
    public static List<User> getAllUsers() {
        return new ArrayList<>(usersCache.values());
    }

    // Login kontrolü
    public static User login(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // Voyage ekleme fonksiyonu
    public static int addVoyage(String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        System.out.println("DatabaseService.addVoyage çağrıldı!");
        System.out.println("Seat Arrangement: " + seatArrangement);
        String sql = "INSERT INTO voyages (type, firm, origin, destination, start_time, arrival_time, price, seat_arrangement, seat_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, type);
            pstmt.setString(2, firm);
            pstmt.setString(3, origin);
            pstmt.setString(4, destination);
            pstmt.setString(5, startTime);
            pstmt.setString(6, arrivalTime);
            pstmt.setDouble(7, price);
            pstmt.setString(8, seatArrangement);
            pstmt.setInt(9, seatCount);
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("DatabaseService.addVoyage başarıyla bitti! Generated ID: " + id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Hatası: " + e.getMessage());
            return -1;
        }
    }

    public static java.util.List<BusTrip> getAllBusVoyages() {
        java.util.List<BusTrip> busTrips = new java.util.ArrayList<>();
        String sql = "SELECT * FROM voyages WHERE type = 'Bus'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BusTrip trip = new BusTrip(
                    rs.getInt("id"),
                    rs.getString("firm"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("start_time"),
                    rs.getString("arrival_time"),
                    rs.getInt("seat_count"),
                    rs.getDouble("price"),
                    rs.getString("seat_arrangement")
                );
                busTrips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busTrips;
    }

    // Belirli bir seferde rezerve edilen koltuk numaralarını döndür
    public static java.util.List<Integer> getReservedSeats(int voyageId) {
        java.util.List<Integer> reserved = new java.util.ArrayList<>();
        String sql = "SELECT seat_number FROM reservations WHERE voyage_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, voyageId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Checking reservations for voyageId: " + voyageId);
            while (rs.next()) {
                reserved.add(rs.getInt("seat_number"));
            }
            System.out.println("Reserved seats: " + reserved);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reserved;
    }

    // Rezervasyon ekle
    public static boolean addReservation(int userId, int voyageId, int seatNumber, String gender) {
        String sql = "INSERT INTO reservations (user_id, voyage_id, seat_number, gender) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, voyageId);
            pstmt.setInt(3, seatNumber);
            pstmt.setString(4, gender);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Voyage silme fonksiyonu
    public static void deleteVoyageFromDB(int voyageId) {
        String sql = "DELETE FROM voyages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, voyageId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<FlightTrip> getAllFlightVoyages() {
        java.util.List<FlightTrip> flightTrips = new java.util.ArrayList<>();
        String sql = "SELECT * FROM voyages WHERE type = 'Flight'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FlightTrip trip = new FlightTrip(
                    rs.getInt("id"),
                    rs.getString("firm"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("start_time"),
                    rs.getString("arrival_time"),
                    rs.getInt("seat_count"),
                    rs.getDouble("price"),
                    rs.getString("seat_arrangement")
                );
                flightTrips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flightTrips;
    }

    public static void addVoyageToDB(Voyage voyage) {
        Connection conn = connection;
        try {
            String sql = "INSERT INTO voyages (type, firm, origin, destination, start_time, arrival_time, seat_count, price, seat_arrangement) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, voyage.getType());
                pstmt.setString(2, voyage.getFirm());
                pstmt.setString(3, voyage.getOrigin());
                pstmt.setString(4, voyage.getDestination());
                pstmt.setString(5, voyage.getStartTime());
                pstmt.setString(6, voyage.getArrivalTime());
                pstmt.setInt(7, voyage.getSeatCount());
                pstmt.setDouble(8, voyage.getPrice());
                pstmt.setString(9, voyage.getSeatArrangement());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateVoyageInDB(Voyage voyage) {
        Connection conn = connection;
        try {
            String sql = "UPDATE voyages SET type = ?, firm = ?, origin = ?, destination = ?, start_time = ?, arrival_time = ?, seat_count = ?, price = ?, seat_arrangement = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, voyage.getType());
                pstmt.setString(2, voyage.getFirm());
                pstmt.setString(3, voyage.getOrigin());
                pstmt.setString(4, voyage.getDestination());
                pstmt.setString(5, voyage.getStartTime());
                pstmt.setString(6, voyage.getArrivalTime());
                pstmt.setInt(7, voyage.getSeatCount());
                pstmt.setDouble(8, voyage.getPrice());
                pstmt.setString(9, voyage.getSeatArrangement());
                pstmt.setInt(10, voyage.getVoyageId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, String> getReservedSeatsWithGender(int voyageId) {
        Map<Integer, String> reserved = new HashMap<>();
        String sql = "SELECT seat_number, gender FROM reservations WHERE voyage_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, voyageId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reserved.put(rs.getInt("seat_number"), rs.getString("gender"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reserved;
    }

    // Kullanıcı, sefer ve koltuk numarasına göre rezervasyon sil
    public static boolean deleteReservation(int userId, int voyageId, int seatNumber) {
        String sql = "DELETE FROM reservations WHERE user_id = ? AND voyage_id = ? AND seat_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, voyageId);
            pstmt.setInt(3, seatNumber);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kullanıcıya göre rezervasyonları döndüren fonksiyon
    public static class ReservationInfo {
        public int voyageId;
        public int seatNumber;
        public String gender;
        public String reservationDate;
        public ReservationInfo(int voyageId, int seatNumber, String gender, String reservationDate) {
            this.voyageId = voyageId;
            this.seatNumber = seatNumber;
            this.gender = gender;
            this.reservationDate = reservationDate;
        }
    }
    public static java.util.List<ReservationInfo> getReservationsForUser(int userId) {
        java.util.List<ReservationInfo> reservations = new java.util.ArrayList<>();
        String sql = "SELECT voyage_id, seat_number, gender, created_at FROM reservations WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservations.add(new ReservationInfo(
                    rs.getInt("voyage_id"),
                    rs.getInt("seat_number"),
                    rs.getString("gender"),
                    rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Bildirim ekle
    public static void addNotification(int userId, String notification) {
        String sql = "INSERT INTO notifications (user_id, message,created_at) VALUES (?, ?,CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, notification);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Bildirim başarıyla eklendi - User ID: " + userId);
            } else {
                System.out.println("Bildirim eklenemedi!");
            }
        } catch (SQLException e) {
            System.out.println("Bildirim eklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Kullanıcının bildirimlerini getir
    public static ArrayList<String[]> getNotificationsForUser(int userId) {
        ArrayList<String[]> notifications = new ArrayList<>();
        String sql = "SELECT message, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] notificationData = new String[2];
                notificationData[0] = rs.getString("message");
                notificationData[1] = rs.getString("created_at");
                notifications.add(notificationData);
            }
        } catch (SQLException e) {
            System.out.println("Bildirimler getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    // Kullanıcının bildirimlerini temizle
    public static void clearNotificationsForUser(int userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affected = pstmt.executeUpdate();
            System.out.println(userId + " ID'li kullanıcının " + affected + " bildirimi silindi.");
        } catch (SQLException e) {
            System.out.println("Bildirimler silinirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Kullanıcının bildirim tercihlerini kaydet
    public static void addNotificationPreference(int userId, String type, String origin, String destination, String schedule) {
        String sql = "INSERT INTO notification_preferences (user_id, type, origin, destination, schedule, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setString(3, origin);
            pstmt.setString(4, destination);
            pstmt.setString(5, schedule);
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("✅ Bildirim tercihi başarıyla kaydedildi!");
                System.out.println("User ID: " + userId);
                System.out.println("Sefer Tipi: " + type);
                System.out.println("Kalkış: " + origin);
                System.out.println("Varış: " + destination);
                System.out.println("Tarih: " + schedule);
            } else {
                System.out.println("❌ Bildirim tercihi kaydedilemedi!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Bildirim tercihi kaydedilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Admin kullanıcılarını getir
    public static ArrayList<String[]> getAdmins() {
        ArrayList<String[]> admins = new ArrayList<>();
        String sql = "SELECT id, name, email, password FROM users WHERE user_type = 'Admin'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] adminInfo = new String[4];
                adminInfo[0] = rs.getString("id");
                adminInfo[1] = rs.getString("name");
                adminInfo[2] = rs.getString("email");
                adminInfo[3] = rs.getString("password");
                admins.add(adminInfo);
            }
            
            System.out.println("Toplam " + admins.size() + " admin bulundu");
            for (String[] admin : admins) {
                System.out.println("Admin ID: " + admin[0] + ", İsim: " + admin[1] + ", Email: " + admin[2]);
            }
            
        } catch (SQLException e) {
            System.err.println("Adminler getirilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        
        return admins;
    }

    public static int getNextUserId() {
        try {
            String query = "SELECT MAX(CAST(id AS INTEGER)) as max_id FROM users";
            ResultSet rs = connection.createStatement().executeQuery(query);
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return maxId + 1;
            }
            return 1; // If no users exist yet, start with ID 1
        } catch (SQLException e) {
            System.err.println("Error getting next user ID: " + e.getMessage());
            return 1; // Default to 1 if there's an error
        }
    }
}