package cst8218.chab0109.orbgame.business;

import cst8218.chab0109.orbgame.entity.Orb;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Schedule;
import jakarta.ejb.EJB;
import java.util.List;

/*
 * Name : Souhail chabli
 * Student id: 041124852
 * course:CST8218 
 * LAb section:302
*/
/*
 * This is the game clock that makes all the balls move automatically - it runs every second and updates each ball's position to create the bouncing animation.
 */
@Singleton
@Startup
public class AdvanceTime {

    @EJB
    private OrbFacade orbFacade; // Inject OrbFacade

    @Schedule(hour="*", minute="*", second="*", persistent=false)
    public void simulateTimeStep() {
       //System.out.println("=== SCHEDULER STARTED ===");
        try {
            List<Orb> orbs = orbFacade.findAll();
           // System.out.println("Found " + orbs.size() + " orbs in database");
            
            if (orbs.isEmpty()) {
              //  System.out.println("No orbs found to update");
                return;
            }
            
            for (Orb orb : orbs) {
              //  System.out.println("Processing Orb ID: " + orb.getId());
                orb.timeStep();
              // System.out.println("Calling orbFacade.edit() for Orb ID: " + orb.getId());
                orbFacade.edit(orb);
               //System.out.println("Successfully saved Orb ID: " + orb.getId()); 
            }
           // System.out.println("=== SCHEDULER COMPLETED ===");
        } catch (Exception e) {
           // System.out.println("ERROR in scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}