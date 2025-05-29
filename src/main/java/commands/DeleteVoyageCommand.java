/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commands;
import services.Admin;
import models.Voyage;

/**
 *
 * @author enesaltin
 */
public class DeleteVoyageCommand implements Command{
    
    Voyage voyage;
    Admin admin;
    

    public DeleteVoyageCommand(Voyage voyage, Admin admin) {
        this.voyage = voyage;
        this.admin = admin;        
    }

    @Override
    public void execute() {
        Voyage.getVoyageHashMap().remove(voyage.getId());
    }

    @Override
    public void undo() {
        Voyage.getVoyageHashMap().put(voyage.getId(), voyage);
        admin.notifyObservers(voyage);
    }
    
}
