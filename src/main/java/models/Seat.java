/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author enesaltin
 */
public class Seat {
    
    private int id;
    private int number;
    private String status;
    private Customer takenByWho;

    public Seat(int id, int number) {
        this.id = id;
        this.number = number;
        this.status = "Empty";
        this.takenByWho = null;
    }
    
    public void sellSeat(Customer customer){
        this.status = "Reserved";
        this.takenByWho = customer;
    }
    
    public void emptySeat(){
        this.status = "Empty";
        this.takenByWho = null;
    }

    public Customer getTakenByWho() {
        return takenByWho;
    }

    public void setTakenByWho(Customer takenByWho) {
        this.takenByWho = takenByWho;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
}
