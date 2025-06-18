package commands;

import java.util.ArrayList;
import models.Customer;
import models.Seat;
import models.Voyage;
import services.DatabaseService;

/**
 *
 * @author enesaltin
 */
public class CancelReservationCommand implements Command{
    
    private Customer customer;
    private Voyage voyage;
    private final int seatNumber;
    private ArrayList<Seat> customerSeats;
    private String gender;
    private boolean isAdmin = false;
    private int userIdFromDb = -1;

    public CancelReservationCommand(Customer customer, Voyage voyage, int seatNumber, String gender, boolean isAdmin) {
        if (!isAdmin && customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        this.voyage = voyage;
        this.seatNumber = seatNumber;
        this.gender = gender;
        this.isAdmin = isAdmin;
    }

    @Override
    public void execute() {
        System.out.println("[CancelReservationCommand.execute] userId=" + (customer != null ? customer.getId() : "null") + ", voyageId=" + voyage.getVoyageId() + ", seatNumber=" + seatNumber + ", gender=" + gender + ", isAdmin=" + isAdmin);
        if (isAdmin) {
            // 1. Önce bilgileri çek
            try {
                java.sql.Connection conn = services.DatabaseService.getConnection();
                String sql = "SELECT user_id, gender FROM reservations WHERE voyage_id = ? AND seat_number = ?";
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, voyage.getVoyageId());
                    pstmt.setInt(2, seatNumber);
                    java.sql.ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        userIdFromDb = rs.getInt("user_id");
                        gender = rs.getString("gender");
                    }
                }
            } catch (Exception e) {
                System.err.println("Admin reservation info fetch failed: " + e.getMessage());
            }
            // 2. Sonra sil
            services.DatabaseService.deleteReservationByVoyageAndSeat(voyage.getVoyageId(), seatNumber);
            return;
        }
        DatabaseService.deleteReservation(Integer.parseInt(customer.getId()), voyage.getVoyageId(), seatNumber);
    }
    

    @Override
    public void undo() {
        System.out.println("[CancelReservationCommand.undo] userId=" + (customer != null ? customer.getId() : "null") + ", voyageId=" + voyage.getVoyageId() + ", seatNumber=" + seatNumber + ", gender=" + gender + ", isAdmin=" + isAdmin);
        if (isAdmin) {
            if (userIdFromDb != -1 && gender != null) {
                services.DatabaseService.addReservation(userIdFromDb, voyage.getVoyageId(), seatNumber, gender);
            }
            return;
        }
        DatabaseService.addReservation(Integer.parseInt(customer.getId()), voyage.getVoyageId(), seatNumber, gender);
    }
}
