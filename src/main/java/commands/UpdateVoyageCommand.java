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
        System.out.println("[UpdateVoyageCommand.execute] Çalıştı! Yeni voyageId=" + voyage.getVoyageId() + ", eski id=" + id);
        Voyage old = Voyage.getVoyageHashMap().get(id);
        oldVoyage = new Voyage(
            old.getVoyageId(),
            old.getType(),
            old.getFirm(),
            old.getOrigin(),
            old.getDestination(),
            old.getStartTime(),
            old.getArrivalTime(),
            old.getSeatCount(),
            old.getPrice(),
            old.getSeatArrangement()
        );
        System.out.println("[UpdateVoyageCommand.execute] oldVoyage=" + oldVoyage);
        Voyage.getVoyageHashMap().remove(id);
        Voyage.getVoyageHashMap().put(voyage.getVoyageId(), voyage);
        services.DatabaseService.updateVoyageInDB(voyage);
        admin.notifyObservers(voyage);
    }

    @Override
    public void undo() {
        System.out.println("[UpdateVoyageCommand.undo] çalıştı! oldVoyage=" + oldVoyage);
        Voyage.getVoyageHashMap().remove(voyage.getVoyageId());
        Voyage.getVoyageHashMap().put(id, oldVoyage);
        services.DatabaseService.updateVoyageInDB(oldVoyage);
        admin.notifyObservers(oldVoyage);
    }
}
