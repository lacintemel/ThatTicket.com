/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands;

import models.Voyage;
import services.Admin;

/**
 *
 * @author enesaltin
 */
public class AddVoyageCommand implements Command{
    Voyage voyage;
    Admin admin;

    public AddVoyageCommand(Voyage voyage, Admin admin) {
        this.voyage = voyage;
        this.admin = admin;
    }

    @Override
    public void execute() {
        Voyage.getVoyageHashMap().put(voyage.getVoyageId(), voyage);
        admin.notifyObservers(voyage);
    }

    @Override
    public void undo() {
        Voyage.getVoyageHashMap().remove(voyage.getVoyageId());
    }
    
    
    
}
