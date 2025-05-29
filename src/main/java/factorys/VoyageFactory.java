/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factorys;

import services.BusTrip;
import services.FlightTrip;
import models.Voyage;

/**
 *
 * @author enesaltin
 */
public class VoyageFactory {
    
    
    
    public static Voyage createVoyage(int voyageId, String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        Voyage voyage;
        if (type.equalsIgnoreCase("bus")) {
            voyage = new BusTrip(voyageId, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
            Voyage.getVoyageHashMap().put(voyageId, voyage);
            return voyage;
        }
        if (type.equalsIgnoreCase("flight")) {
            voyage = new FlightTrip(voyageId, firm, origin, destination, startTime, arrivalTime, seatCount, price, seatArrangement);
            Voyage.getVoyageHashMap().put(voyageId, voyage);
            return voyage;
        }
        return null;
    }
    
}
