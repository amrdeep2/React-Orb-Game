package cst8218.chab0109.orbgame.business;

import cst8218.chab0109.orbgame.entity.Orb;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class OrbFacade extends AbstractFacade<Orb> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;  // CHANGE THIS LINE

    public OrbFacade() {
        super(Orb.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  // CHANGE THIS LINE - just return the injected EntityManager
    }
}