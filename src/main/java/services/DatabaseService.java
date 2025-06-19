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

    // Veritabanı bağlantısını döndür
    public static Connection getConnection() {
        return connection;
    }

    // Veritabanı başlatma
    public static void initialize() {
        System.out.println("\n=== DATABASE INITIALIZATION STARTED ===");
        try {
            // SQLite sürücüsünü yükle
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ SQLite JDBC driver loaded successfully");
            
            System.out.println("Creating data directory...");
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
            
            System.out.println("=== DATABASE INITIALIZATION COMPLETED SUCCESSFULLY ===\n");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("❌ DATABASE INITIALIZATION FAILED: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    // Tabloları oluştur
    private static void createTables() {
        System.out.println("   Creating database tables...");
        try (Statement stmt = connection.createStatement()) {
            // Users tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "user_type TEXT NOT NULL)");
            System.out.println("   ✅ Users table created/verified");

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
            System.out.println("   ✅ Voyages table created/verified");

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
            System.out.println("   ✅ Reservations table created/verified");

            // Notifications tablosu
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "message TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id))");
            System.out.println("   ✅ Notifications table created/verified");
            
            System.out.println("   ✅ All database tables created/verified successfully");
        } catch (SQLException e) {
            System.err.println("   ❌ Failed to create tables: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create tables: " + e.getMessage());
        }
    }

    // Kullanıcıları cache'e yükle
    private static void loadUsersIntoCache() {
        System.out.println("   Loading users from database into cache...");
        String sql = "SELECT * FROM users";
        int userCount = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                userCount++;
                User user;
                if ("admin".equalsIgnoreCase(rs.getString("user_type"))) {
                    user = Admin.getInstance(
                        rs.getString("id"),
                        "1111",
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                    System.out.println("   - Loaded admin user: " + user.getName() + " (ID: " + user.getId() + ")");
                } else {
                    user = new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("user_type")
                    );
                    System.out.println("   - Loaded regular user: " + user.getName() + " (ID: " + user.getId() + ")");
                }
                usersCache.put(user.getEmail(), user);
            }
        } catch (SQLException e) {
            System.err.println("   ❌ Failed to load users into cache: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load users into cache: " + e.getMessage());
        }
    }

    // Voyage'ları cache'e yükle
    private static void loadVoyagesIntoCache() {
        System.out.println("   Loading voyages from database into cache...");
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
            System.err.println("   ❌ Failed to load voyages into cache: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load voyages into cache: " + e.getMessage());
        }
    }

    // Kullanıcı ekle
    public static boolean addUser(User user) {
        System.out.println("\n=== ADDING USER TO DATABASE ===");
        System.out.println("User details: " + user.getName() + " (" + user.getEmail() + ") - Type: " + user.getUser_type());
        
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
            System.err.println("❌ Failed to add user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add user: " + e.getMessage());
        }
    }

    // Kullanıcı güncelle
    public static boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, user_type = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUser_type());
            pstmt.executeUpdate();
            
            // Cache'i güncelle
            usersCache.put(user.getEmail(), user);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    // Kullanıcı sil
    public static boolean deleteUser(String userId) {
        try {
            // Önce kullanıcının tüm rezervasyonlarını sil
            String deleteReservationsSql = "DELETE FROM reservations WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteReservationsSql)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
            }
            
            // Sonra kullanıcıyı sil
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteUserSql)) {
                pstmt.setString(1, userId);
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    // Cache'den kaldır
                    usersCache.values().removeIf(user -> user.getId().equals(userId));
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
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
            throw new RuntimeException("Failed to get user by email: " + e.getMessage());
        }
        return null;
    }

    // Tüm kullanıcıları getir
    public static List<User> getAllUsers() {
        return new ArrayList<>(usersCache.values());
    }

    // Login kontrolü
    public static User login(String email, String password) {
        System.out.println("\n=== USER LOGIN ATTEMPT ===");
        System.out.println("Email: " + email);
        
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("✅ Login successful for user: " + user.getName() + " (Type: " + user.getUser_type() + ")");
            System.out.println("=== LOGIN COMPLETED ===\n");
            return user;
        } else {
            System.out.println("❌ Login failed - Invalid credentials");
            System.out.println("=== LOGIN FAILED ===\n");
            return null;
        }
    }

    // Voyage ekleme fonksiyonu
    public static int addVoyage(String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        System.out.println("DatabaseService.addVoyage çağrıldı!");
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
                    System.out.println("✅ Voyage added successfully with ID: " + id);
                    System.out.println("=== VOYAGE ADDITION COMPLETED ===\n");
                    return id;
                }
            }
            System.out.println("❌ Failed to get generated ID");
            return -1;
        } catch (SQLException e) {
            System.err.println("❌ Failed to add voyage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add voyage: " + e.getMessage());
        }
    }

    public static java.util.List<BusTrip> getAllBusVoyages() {
        // Önce voyage cache'ini temizle
        Voyage.getVoyageHashMap().clear();
        
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
                // Voyage'ı hashmap'e ekle
                Voyage.getVoyageHashMap().put(trip.getVoyageId(), trip);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all bus voyages: " + e.getMessage());
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
            while (rs.next()) {
                reserved.add(rs.getInt("seat_number"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get reserved seats: " + e.getMessage());
        }
        return reserved;
    }

    // Rezervasyon ekle
    public static boolean addReservation(int userId, int voyageId, int seatNumber, String gender) {
        System.out.println("\n=== ADDING RESERVATION TO DATABASE ===");
        System.out.println("Reservation details:");
        System.out.println("  - User ID: " + userId);
        System.out.println("  - Voyage ID: " + voyageId);
        System.out.println("  - Seat Number: " + seatNumber);
        System.out.println("  - Gender: " + gender);
        
        String sql = "INSERT INTO reservations (user_id, voyage_id, seat_number, gender) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, voyageId);
            pstmt.setInt(3, seatNumber);
            pstmt.setString(4, gender);
            pstmt.executeUpdate();
            System.out.println("✅ Reservation added successfully");
            System.out.println("=== RESERVATION ADDITION COMPLETED ===\n");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Failed to add reservation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add reservation: " + e.getMessage());
        }
    }

    // Voyage silme fonksiyonu
    public static void deleteVoyageFromDB(int voyageId) {
        System.out.println("\n=== DELETING VOYAGE FROM DATABASE ===");
        System.out.println("Voyage ID: " + voyageId);
        
        String sql = "DELETE FROM voyages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, voyageId);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("✅ Voyage deleted successfully. Affected rows: " + affectedRows);
            System.out.println("=== VOYAGE DELETION COMPLETED ===\n");
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete voyage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete voyage: " + e.getMessage());
        }
    }

    public static java.util.List<FlightTrip> getAllFlightVoyages() {
        // Önce voyage cache'ini temizle
        Voyage.getVoyageHashMap().clear();
        
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
                // Voyage'ı hashmap'e ekle
                Voyage.getVoyageHashMap().put(trip.getVoyageId(), trip);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all flight voyages: " + e.getMessage());
        }
        return flightTrips;
    }

    public static void addVoyageToDB(Voyage voyage) {
        System.out.println("\n=== ADDING VOYAGE OBJECT TO DATABASE ===");
        System.out.println("Voyage details:");
        System.out.println("  - ID: " + voyage.getVoyageId());
        System.out.println("  - Type: " + voyage.getType());
        System.out.println("  - Firm: " + voyage.getFirm());
        System.out.println("  - Route: " + voyage.getOrigin() + " → " + voyage.getDestination());
        
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
                System.out.println("✅ Voyage object added to database successfully");
                System.out.println("=== VOYAGE OBJECT ADDITION COMPLETED ===\n");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to add voyage to database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add voyage to database: " + e.getMessage());
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
            System.err.println("❌ Failed to update voyage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update voyage: " + e.getMessage());
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
            throw new RuntimeException("Failed to get reserved seats with gender: " + e.getMessage());
        }
        return reserved;
    }

    // Kullanıcı, sefer ve koltuk numarasına göre rezervasyon sil
    public static boolean deleteReservation(int userId, int voyageId, int seatNumber) {
        // System.out.println("Rezervasyon silme işlemi başladı:");
        // System.out.println("User ID: " + userId);
        // System.out.println("Voyage ID: " + voyageId);
        // System.out.println("Seat Number: " + seatNumber);

        // Önce rezervasyonun detaylarını kontrol et
        String checkDetailsSql = "SELECT * FROM reservations WHERE user_id = ? OR voyage_id = ? OR seat_number = ?";
        try (PreparedStatement checkDetailsStmt = connection.prepareStatement(checkDetailsSql)) {
            checkDetailsStmt.setInt(1, userId);
            checkDetailsStmt.setInt(2, voyageId);
            checkDetailsStmt.setInt(3, seatNumber);
            ResultSet rs = checkDetailsStmt.executeQuery();
            
            // System.out.println("\nMevcut rezervasyonlar:");
            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                // System.out.println("Rezervasyon bulundu:");
                // System.out.println("User ID: " + rs.getInt("user_id"));
                // System.out.println("Voyage ID: " + rs.getInt("voyage_id"));
                // System.out.println("Seat Number: " + rs.getInt("seat_number"));
                // System.out.println("Gender: " + rs.getString("gender"));
                // System.out.println("Created At: " + rs.getString("created_at"));
                // System.out.println("---");
            }
            if (!foundAny) {
                // System.out.println("Hiç rezervasyon bulunamadı!");
            }
        } catch (SQLException e) {
            // System.err.println("Rezervasyon detayları kontrol edilirken hata: " + e.getMessage());
        }

        String sql = "DELETE FROM reservations WHERE user_id = ? AND voyage_id = ? AND seat_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, voyageId);
            pstmt.setInt(3, seatNumber);
            
            // Önce rezervasyonun var olup olmadığını kontrol et
            String checkSql = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND voyage_id = ? AND seat_number = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, voyageId);
                checkStmt.setInt(3, seatNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // System.out.println("Rezervasyon bulunamadı!");
                    return false;
                }
            }

            int affected = pstmt.executeUpdate();
            // System.out.println("Silinen kayıt sayısı: " + affected);
            return affected > 0;
     
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete reservation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete reservation: " + e.getMessage());
        }
    }

    // Kullanıcıya göre rezervasyonları döndüren fonksiyon
    public static class ReservationInfo {
        public int voyageId;
        public int seatNumber;
        public String gender;
        public String reservationDate;
        public String userName;
        public String userEmail;
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
            throw new RuntimeException("Failed to get reservations for user: " + e.getMessage());
        }
        return reservations;
    }

    // Tüm rezervasyonları getir (Admin için)
    public static java.util.List<ReservationInfo> getAllReservations() {
        java.util.List<ReservationInfo> reservations = new java.util.ArrayList<>();
        String sql = "SELECT r.voyage_id, r.seat_number, r.gender, r.created_at, u.name as user_name, u.email as user_email " +
                    "FROM reservations r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "ORDER BY r.created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReservationInfo info = new ReservationInfo(
                    rs.getInt("voyage_id"),
                    rs.getInt("seat_number"),
                    rs.getString("gender"),
                    rs.getString("created_at")
                );
                info.userName = rs.getString("user_name");
                info.userEmail = rs.getString("user_email");
                reservations.add(info);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all reservations: " + e.getMessage());
        }
        return reservations;
    }

    // Bildirim ekle
    public static void addNotification(int userId, String notification) {
        System.out.println("\n=== ADDING NOTIFICATION TO DATABASE ===");
        System.out.println("User ID: " + userId);
        System.out.println("Notification: " + notification);
        
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, notification);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                // System.out.println("Bildirim başarıyla eklendi - User ID: " + userId);
            } else {
                // System.out.println("Bildirim eklenemedi!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to add notification: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add notification: " + e.getMessage());
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
            throw new RuntimeException("Failed to get notifications for user: " + e.getMessage());
        }
        return notifications;
    }

    // Kullanıcının bildirimlerini temizle
    public static void clearNotificationsForUser(int userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affected = pstmt.executeUpdate();
            // System.out.println(userId + " ID'li kullanıcının " + affected + " bildirimi silindi.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear notifications for user: " + e.getMessage());
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
                // System.out.println("✅ Bildirim tercihi başarıyla kaydedildi!");
                // System.out.println("User ID: " + userId);
                // System.out.println("Sefer Tipi: " + type);
                // System.out.println("Kalkış: " + origin);
                // System.out.println("Varış: " + destination);
                // System.out.println("Tarih: " + schedule);
            } else {
                // System.out.println("❌ Bildirim tercihi kaydedilemedi!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add notification preference: " + e.getMessage());
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
            
            // System.out.println("Toplam " + admins.size() + " admin bulundu");
            // for (String[] admin : admins) {
            //     System.out.println("Admin ID: " + admin[0] + ", İsim: " + admin[1] + ", Email: " + admin[2]);
            // }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get admins: " + e.getMessage());
        }
        
        return admins;
    }

    public static int getNextUserId() {
        try {
            String query = "SELECT MAX(CAST(id AS INTEGER)) as max_id FROM users";
            ResultSet rs = connection.createStatement().executeQuery(query);
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return maxId + 1; // If no users exist yet, start with ID 1
            }
            return 1; // If no users exist yet, start with ID 1
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get next user ID: " + e.getMessage());
        }
    }

    public static boolean deleteReservationsByUserId(int userId) {
        try {
            String sql = "DELETE FROM reservations WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reservations by user ID: " + e.getMessage());
        }
    }

    public static List<Object[]> getReservationsByUserId(int userId) {
        List<Object[]> reservations = new ArrayList<>();
        try {
            String sql = "SELECT id, seat_number, gender, created_at FROM reservations WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Object[] reservation = new Object[4];
                    reservation[0] = rs.getInt("id");
                    reservation[1] = rs.getInt("seat_number");
                    reservation[2] = rs.getString("gender");
                    reservation[3] = rs.getString("created_at");
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get reservations by user ID: " + e.getMessage());
        }
        return reservations;
    }

    public static Voyage getVoyageById(int voyageId) {
        try {
            String sql = "SELECT * FROM voyages WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, voyageId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return new Voyage(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("firm"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("start_time"),
                        rs.getString("arrival_time"),
                        rs.getInt("seat_count"),
                        rs.getDouble("price"),
                        rs.getString("seat_arrangement")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get voyage by ID: " + e.getMessage());
        }
        return null;
    }

    // Admin için sefer ve koltuk numarasına göre rezervasyon sil
    public static boolean deleteReservationByVoyageAndSeat(int voyageId, int seatNumber) {
        System.out.println("Admin rezervasyon silme işlemi başladı:");
        System.out.println("Voyage ID: " + voyageId);
        System.out.println("Seat Number: " + seatNumber);

        String sql = "DELETE FROM reservations WHERE voyage_id = ? AND seat_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, voyageId);
            pstmt.setInt(2, seatNumber);
            
            // Önce rezervasyonun var olup olmadığını kontrol et
            String checkSql = "SELECT COUNT(*) FROM reservations WHERE voyage_id = ? AND seat_number = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, voyageId);
                checkStmt.setInt(2, seatNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Rezervasyon bulunamadı!");
                    return false;
                }
            }

            int affected = pstmt.executeUpdate();
            System.out.println("Silinen kayıt sayısı: " + affected);
            return affected > 0;
     
        } catch (SQLException e) {
            System.err.println("Rezervasyon silme hatası: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Rezervasyon silinirken bir hata oluştu: " + e.getMessage());
        }
    }

    public static void loadAllVoyages() {
        System.out.println("\n=== LOADING ALL VOYAGES FROM DATABASE ===");
        // Önce mevcut voyage'ları temizle
        Voyage.getVoyageHashMap().clear();
        System.out.println("   Cleared existing voyage cache");
        
        String sql = "SELECT * FROM voyages";
        int voyageCount = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                voyageCount++;
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
                
                // Voyage'ı hashmap'e ekle
                Voyage.getVoyageHashMap().put(voyageId, voyage);
                System.out.println("   - Loaded voyage: " + type + " from " + origin + " to " + destination + " (ID: " + voyageId + ")");
            }
            System.out.println("✅ Loaded " + voyageCount + " voyages from database into cache");
            System.out.println("=== VOYAGE LOADING COMPLETED ===\n");
        } catch (SQLException e) {
            System.err.println("❌ Failed to load voyages: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Voyage verileri yüklenirken hata oluştu: " + e.getMessage());
        }
    }
}