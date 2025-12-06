package cst8218.chab0109.orbgame.business;

import cst8218.chab0109.orbgame.entity.Orb;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Facade class responsible for performing CRUD operations on Orb entities.
 *
 * This class extends AbstractFacade<Orb>, which provides the standard 
 * create, edit, remove, find, findAll, and count operations. The OrbFacade
 * simply supplies the correct EntityManager and entity type.
 *
 * The @Stateless annotation indicates that this is a stateless session bean
 * used by the application server to handle business logic for Orb objects.
 */
@Stateless
public class OrbFacade extends AbstractFacade<Orb> {

    /**
     * Injected EntityManager that connects to the persistence unit defined
     * in persistence.xml. This is used for all JPA database operations.
     */
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Constructor passes the Orb entity class to the AbstractFacade so that
     * it knows which JPA entity type it should operate on.
     */
    public OrbFacade() {
        super(Orb.class);
    }

    /**
     * Provides the EntityManager used by the inherited CRUD methods.
     *
     * @return The EntityManager managed by the container.
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
