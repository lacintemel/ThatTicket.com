/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author enesaltin
 */
public class Voyage{
    
    private static int idStartValue = 1000;
    protected double price;
    private int id;  
    private String type;
    private String firm;
    private String origin;
    private String destination;
    private String startTime;
    private String arrivalTime;
    protected ArrayList<Seat> seats;
    protected String seatArrangement;
    private static HashMap<Integer, Voyage> VoyageHashMap = new HashMap<>();
    private String status; // "ACTIVE", "CANCELLED", "DELAYED", "COMPLETED"
    private int voyageId;

    

    public Voyage(int voyageId, String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        this.voyageId = voyageId;
        this.type = type;
        this.firm = firm;
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        seats = new ArrayList<>();
        this.seatArrangement = seatArrangement;
        int a = 0;
        for (int i = 0; i < seatCount; i++) {
            seats.add(new Seat(voyageId + a, i + 1));
            a++;
        }
    }

    public Voyage(String type, String firm, String origin, String destination, String startTime, String arrivalTime, int seatCount, double price, String seatArrangement) {
        this.id = idStartValue;
        this.type = type;
        this.firm = firm;
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        seats = new ArrayList<>();
        this.seatArrangement = seatArrangement;
        int a = 0;
        for (int i = 0; i < seatCount; i++) {
            seats.add(new Seat(id + a, i + 1));
            a++;
        }
        idStartValue += 1000;
    }

    public Voyage(String type, String firm, String origin, String destination, String startTime, int seatCount, double price) {
        this.id = idStartValue;
        this.type = type;
        this.firm = firm;
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
        this.price = price;
        seats = new ArrayList<>();
        int a = 0;
        for (int i = 0; i < seatCount; i++) {
            seats.add(new Seat(id + a, i + 1));
            a++;
        }
        idStartValue += 1000;
    }

    
    
    @Override
    public String toString() {
        return "Voyage{" + "id=" + id + ", firm=" + firm + ", origin=" + origin + ", destination=" + destination + ", startTime=" + startTime + ", arrivalTime=" + arrivalTime + ", seats=" + seats + ", price=" + price + ", seatArrangement=" + seatArrangement + '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }   

    public int getId() {
        return id;
    }
    
    public static HashMap<Integer, Voyage> getVoyageHashMap() {
        return VoyageHashMap;
    }

    public static int getIdStartValue() {
        return idStartValue;
    }

    public static void setIdStartValue(int idStartValue) {
        Voyage.idStartValue = idStartValue;
    }

    public String getFirm() {
        return firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }
    
    ////////////////////////////////////////////////////////
    
    
    public String getStatus() {
        return status;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getSeatArrangement() { return seatArrangement; }
    public void setSeatArrangement(String seatArrangement) { this.seatArrangement = seatArrangement; }

    public int getVoyageId() { return voyageId; }
    public void setVoyageId(int voyageId) { this.voyageId = voyageId; }

    public int getSeatCount() {
        return seats.size();
    }

    public void setSeatCount(int seatCount) {
        seats.clear();
        int a = 0;
        for (int i = 0; i < seatCount; i++) {
            seats.add(new Seat(voyageId + a, i + 1));
            a++;
        }
    }
}
