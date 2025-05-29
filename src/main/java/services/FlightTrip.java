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
public class FlightTrip extends Voyage {
    
    public FlightTrip(int voyageId, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        super(voyageId, "Flight", firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
        // Ensure seats are created
        if (this.seats == null || this.seats.isEmpty()) {
            this.seats = new java.util.ArrayList<>();
            for (int i = 0; i < seatCount; i++) {
                this.seats.add(new models.Seat(voyageId + i, i + 1));
            }
        }
    }

    public java.util.ArrayList<models.Seat> getSeats() {
        return this.seats;
    }
    
    
}
