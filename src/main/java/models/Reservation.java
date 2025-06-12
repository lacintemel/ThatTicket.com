package models;

public class Reservation {
    private Customer customer;
    private Voyage voyage;
    private int seatNumber;

    public Reservation(Customer customer, Voyage voyage, int seatNumber) {
        this.customer = customer;
        this.voyage = voyage;
        this.seatNumber = seatNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public int getSeatNumber() {
        return seatNumber;
    }
} 