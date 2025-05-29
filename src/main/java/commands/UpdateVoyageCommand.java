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
public class UpdateVoyageCommand implements Command{
    Voyage voyage;
    Admin admin;
    int id;
    Voyage oldVoyage;

    public UpdateVoyageCommand(Voyage voyage, Admin admin, int id) {
        this.voyage = voyage;
        this.admin = admin; 
        this.id = id;
    }

    @Override
    public void execute() {
        oldVoyage = Voyage.getVoyageHashMap().get(id);
        Voyage.getVoyageHashMap().remove(id);
        Voyage.getVoyageHashMap().put(voyage.getId(), voyage);
        admin.notifyObservers(voyage);
    }

    @Override
    public void undo() {
        Voyage.getVoyageHashMap().remove(voyage.getId());
        Voyage.getVoyageHashMap().put(id, oldVoyage);
        admin.notifyObservers(oldVoyage);
    }
}
