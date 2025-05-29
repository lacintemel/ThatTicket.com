// src/main/java/services/DatabaseService.java
package services;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
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
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    user_type TEXT NOT NULL
                )
            """);

            // Gelecekte eklenecek modeller için tablolar
            // Örnek: Transport tablosu
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,
                    name TEXT NOT NULL,
                    capacity INTEGER,
                    status TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Örnek: Voyages tablosu
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS voyages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    firm TEXT,
                    origin TEXT NOT NULL,
                    destination TEXT NOT NULL,
                    start_time TIMESTAMP,
                    arrival_time TIMESTAMP,
                    price DECIMAL(10,2),
                    seat_arrangement TEXT,
                    seat_count INTEGER,
                    type TEXT,
                    status TEXT,
                    FOREIGN KEY (transport_id) REFERENCES transports(id)
                )
            """);

            // Örnek: Reservations tablosu
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    voyage_id INTEGER,
                    seat_number INTEGER,
                    status TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (voyage_id) REFERENCES voyages(id)
                )
            """);
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
                    user = new Admin(
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
                String origin = rs.getString("from_location");
                String destination = rs.getString("to_location");
                String startTime = rs.getString("departure_time");
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
                    user = new Admin(
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
    public static boolean addVoyage(String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        System.out.println("DatabaseService.addVoyage çağrıldı!");
        System.out.println("Seat Arrangement: " + seatArrangement);
        String sql = "INSERT INTO voyages (firm, from_location, to_location, departure_time, arrival_time, price, seat_arrangement, seat_count, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firm);
            pstmt.setString(2, origin);
            pstmt.setString(3, destination);
            pstmt.setString(4, startTime);
            pstmt.setString(5, arrivalTime);
            pstmt.setDouble(6, price);
            pstmt.setString(7, seatArrangement);
            pstmt.setInt(8, seatCount);
            pstmt.setString(9, type);
            pstmt.executeUpdate();
            System.out.println("DatabaseService.addVoyage başarıyla bitti!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Hatası: " + e.getMessage());
            return false;
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
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getString("departure_time"),
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
    public static boolean addReservation(int userId, int voyageId, int seatNumber) {
        String sql = "INSERT INTO reservations (user_id, voyage_id, seat_number, status) VALUES (?, ?, ?, 'RESERVED')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, voyageId);
            pstmt.setInt(3, seatNumber);
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
                    rs.getString("from_location"),
                    rs.getString("to_location"),
                    rs.getString("departure_time"),
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
            String sql = "INSERT INTO voyages (type, firm, from_location, to_location, departure_time, arrival_time, seat_count, price, seat_arrangement) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            String sql = "UPDATE voyages SET type = ?, firm = ?, from_location = ?, to_location = ?, departure_time = ?, arrival_time = ?, seat_count = ?, price = ?, seat_arrangement = ? WHERE id = ?";
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
}