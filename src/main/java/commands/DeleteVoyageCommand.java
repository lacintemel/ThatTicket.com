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
    private Voyage deletedVoyage;
    

    public DeleteVoyageCommand(Voyage voyage, Admin admin) {
        this.voyage = voyage;
        this.admin = admin;        
    }

    @Override
    public void execute() {
        deletedVoyage = Voyage.getVoyageHashMap().get(voyage.getVoyageId());
        Voyage.getVoyageHashMap().remove(voyage.getVoyageId());
        services.DatabaseService.deleteVoyageFromDB(voyage.getVoyageId()); // DB'den sil
    }

    @Override
    public void undo() {
        if (deletedVoyage != null) {
            Voyage.getVoyageHashMap().put(deletedVoyage.getVoyageId(), deletedVoyage);
            services.DatabaseService.addVoyageToDB(deletedVoyage); // DB'ye geri ekle
            admin.notifyObservers(deletedVoyage);
        }
    }
    
}
