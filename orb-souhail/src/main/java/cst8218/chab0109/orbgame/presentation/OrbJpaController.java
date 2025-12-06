package cst8218.chab0109.orbgame.presentation;
/*
 * This is the database manager for the bouncing balls - it handles all the 
 * saving, loading, updating, and deleting of ball (Orb) data from the database.
 */

/*
 * Name : Souhail chabli
 * Student id: 041124852
 * course: CST8218 
 * Lab section: 302
*/

import cst8218.chab0109.orbgame.entity.Orb;
import cst8218.chab0109.orbgame.presentation.exceptions.NonexistentEntityException;
import cst8218.chab0109.orbgame.presentation.exceptions.RollbackFailureException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 * OrbJpaController handles CRUD operations for Orb entities using
 * manual JTA (UserTransaction) instead of EJBs.
 *
 * Responsibilities:
 * - Create, edit, delete Orb records
 * - Query Orb tables with pagination support
 * - Handle transaction begin/commit/rollback manually
 */
public class OrbJpaController implements Serializable {

    /** Creates controller with transaction + entity manager factory */
    public OrbJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }

    /** For managing transactions manually (begin/commit/rollback) */
    private UserTransaction utx = null;

    /** Used to create EntityManager instances */
    private EntityManagerFactory emf = null;

    /**
     * Returns a new EntityManager object for DB operations.
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Creates a new Orb entity in the database.
     * Uses manual transaction control.
     */
    public void create(Orb orb) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();         // Start transaction
            em = getEntityManager();
            em.persist(orb);     // Insert into DB
            utx.commit();        // Commit transaction
        } catch (Exception ex) {
            try {
                utx.rollback();  // Rollback on error
            } catch (Exception re) {
                throw new RollbackFailureException(
                    "An error occurred attempting to roll back the transaction.", 
                    re
                );
            }
            throw ex;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Updates an existing Orb in the database.
     * Uses merge() and manual transaction boundaries.
     */
    public void edit(Orb orb) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();         // Start transaction
            em = getEntityManager();
            orb = em.merge(orb); // Update the entity
            utx.commit();        // Commit changes
        } catch (Exception ex) {
            try {
                utx.rollback();  // Undo changes on failure
            } catch (Exception re) {
                throw new RollbackFailureException(
                    "An error occurred attempting to roll back the transaction.", 
                    re
                );
            }

            // Check if the Orb no longer exists in DB
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = orb.getId();
                if (findOrb(id) == null) {
                    throw new NonexistentEntityException(
                        "The orb with id " + id + " no longer exists."
                    );
                }
            }
            throw ex;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Deletes an Orb based on its ID.
     */
    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;

        try {
            utx.begin();       // Start transaction
            em = getEntityManager();
            Orb orb;

            // Try to reference the Orb
            try {
                orb = em.getReference(Orb.class, id);
                orb.getId(); // triggers loading or throws exception
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException(
                    "The orb with id " + id + " no longer exists.", 
                    enfe
                );
            }

            // Remove entity
            em.remove(orb);
            utx.commit();      // Commit deletion
        } catch (Exception ex) {
            try {
                utx.rollback(); // Undo transaction if something failed
            } catch (Exception re) {
                throw new RollbackFailureException(
                    "An error occurred attempting to roll back the transaction.", 
                    re
                );
            }
            throw ex;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Returns ALL Orb entities.
     */
    public List<Orb> findOrbEntities() {
        return findOrbEntities(true, -1, -1);
    }

    /**
     * Returns a RANGE of Orb entities (used for pagination).
     */
    public List<Orb> findOrbEntities(int maxResults, int firstResult) {
        return findOrbEntities(false, maxResults, firstResult);
    }

    /**
     * Private helper for performing queries with or without limits.
     */
    private List<Orb> findOrbEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();

        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orb.class));
            Query q = em.createQuery(cq);

            // Apply pagination only when needed
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }

            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds a single Orb by its primary key.
     */
    public Orb findOrb(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orb.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Returns the number of Orb rows in the database.
     */
    public int getOrbCount() {
        EntityManager em = getEntityManager();

        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orb> rt = cq.from(Orb.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);

            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
