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
public class ReservationCommand implements Command{
    
    private Customer customer;
    private Voyage voyage;
    private final int seatNumber;
    private ArrayList<Seat> customerSeats;
    private String gender;
    
    public ReservationCommand(Customer customer, Voyage voyage, int seatNumber, String gender) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        this.voyage = voyage;
        this.seatNumber = seatNumber;
        this.gender = gender;
        this.customerSeats = customer.getCustomersVoyageHashMap().get(voyage);
    }

    @Override
    public void execute() {
  
    }

    @Override
    public void undo() {

    }
            
          
            
}
