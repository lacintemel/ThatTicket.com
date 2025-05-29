/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import models.Voyage;

/**
 *
 * @author enesaltin
 */
public class BusTrip extends Voyage {
    

    public BusTrip(int voyageId, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        super(voyageId, "Bus", firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
        // Ensure seats are created
        if (this.seats == null || this.seats.isEmpty()) {
            this.seats = new java.util.ArrayList<>();
            for (int i = 0; i < seatCount; i++) {
                this.seats.add(new models.Seat(voyageId + i, i + 1));
            }
        }
    }

    public int getVoyageId() { return super.getVoyageId(); }
    public int getSeatCount() { return this.seats != null ? this.seats.size() : 0; }
    public String getSeatArrangement() { return this.seatArrangement; }

    // Koltuk düzenini kontrol eder (2+1, 2+2 gibi)
    public boolean isValidSeatArrangement() {
        return this.seatArrangement != null && 
               (this.seatArrangement.equals("2+1") || 
                this.seatArrangement.equals("2+2"));
    }

    // Belirli bir koltuk düzenine göre toplam koltuk sayısını hesaplar
    public int calculateTotalSeats() {
        if (this.seatArrangement.equals("2+1")) {
            return 45; // Örnek: 15 sıra x 3 koltuk
        } else if (this.seatArrangement.equals("2+2")) {
            return 48; // Örnek: 12 sıra x 4 koltuk
        }
        return 0;
    }

    // Seferin durumunu kontrol eder
    private String status; // "ACTIVE", "CANCELLED", "DELAYED", "COMPLETED"
    public String getStatus() {
        return status;
    }

    // Doluluk oranını hesaplar
    public double getOccupancyRate() {
        long reservedSeats = getSeats().stream()
            .filter(seat -> seat.getStatus().equals("RESERVED"))
            .count();
        return (double) reservedSeats / getSeats().size();
    }

    public java.util.ArrayList<models.Seat> getSeats() {
        return this.seats;
    }
}
