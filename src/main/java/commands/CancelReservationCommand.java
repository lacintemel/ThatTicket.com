/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands;

import java.util.ArrayList;
import models.Customer;
import models.Seat;
import models.Voyage;

/**
 *
 * @author enesaltin
 */
public class CancelReservationCommand implements Command{
    
    private Customer customer;
    private Voyage voyage;
    private final int seatNumber;
    private ArrayList<Seat> customerSeats;

    public CancelReservationCommand(Customer customer, Voyage voyage, int seatNumber) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        this.voyage = voyage;
        this.seatNumber = seatNumber;
        this.customerSeats = customer.getCustomersVoyageHashMap().get(voyage);
    }

    @Override
    public void execute() {
        Seat seat = Voyage.getVoyageHashMap().get(this.voyage.getVoyageId()).getSeats().get(seatNumber - 1);
        seat.emptySeat();
        customerSeats.remove(seat);
        if(customerSeats.size() == 0){
            customer.getCustomersVoyageHashMap().remove(voyage);
        }
    }
    

    @Override
    public void undo() {        
        Seat seat = voyage.getSeats().get(seatNumber - 1);
        seat.sellSeat(customer);
        if (customer.getCustomersVoyageHashMap().containsKey(voyage)){
            customerSeats.add(seat);
        }
        else{
            ArrayList<Seat> seats = new ArrayList<>();
            seats.add(seat);
            customer.getCustomersVoyageHashMap().put(voyage, seats);
        }
    }
    
    
}
