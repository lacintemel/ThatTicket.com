/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands;

import models.Voyage;
import services.Admin;
import services.DatabaseService;

/**
 *
 * @author enesaltin
 */
public class AddVoyageCommand implements Command{
    private final Voyage voyage;
    private final Admin admin;
    private int generatedId;

    public AddVoyageCommand(Voyage voyage, Admin admin) {
        this.voyage = voyage;
        this.admin = admin;
    }

    @Override
    public void execute() {
        // Add to database first to get the generated ID
        String sql = "INSERT INTO voyages (type, firm, origin, destination, start_time, arrival_time, seat_count, price, seat_arrangement) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var pstmt = DatabaseService.getConnection().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
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
            
            // Get the generated ID
            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                    System.out.println("Generated ID for new voyage: " + generatedId);
                    // Update voyage with the generated ID
                    voyage.setVoyageId(generatedId);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to add voyage to database: " + e.getMessage());
        }

        // Add to memory
        Voyage.getVoyageHashMap().put(generatedId, voyage);
        // Notify observers
        admin.notifyObservers(voyage);
    }

    @Override
    public void undo() {
        System.out.println("Undoing AddVoyageCommand for voyage ID: " + generatedId);
        try {
            // Remove from memory
            Voyage.getVoyageHashMap().remove(generatedId);
            
            // Remove from database using the generated ID
            String sql = "DELETE FROM voyages WHERE id = ?";
            try (var pstmt = DatabaseService.getConnection().prepareStatement(sql)) {
                pstmt.setInt(1, generatedId);
                int affectedRows = pstmt.executeUpdate();
                System.out.println("Deleted rows from database: " + affectedRows);
            }
            
            // Notify observers about the removal
            admin.notifyObservers(null);
        } catch (java.sql.SQLException e) {
            System.err.println("Error during undo: " + e.getMessage());
            throw new RuntimeException("Failed to undo voyage addition: " + e.getMessage());
        }
    }
}
