package cst8218.chab0109.orbgame.business;

import cst8218.chab0109.orbgame.entity.Orb;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Stateless
@Path("orbs")
public class OrbFacadeREST extends AbstractFacade<Orb> {
//dsa
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public OrbFacadeREST() {
        super(Orb.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // Count Orbs
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response countREST() {
        try {
            int count = ((Long) em.createQuery("SELECT COUNT(o) FROM Orb o").getSingleResult()).intValue();
            return Response.ok(String.valueOf(count)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // Create Orb
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createOrb(Orb entity) {
        if (entity == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Entity cannot be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ID must be null for new Orbs").build();
        }
        // Set defaults if null
        if (entity.getXSpeed() == null) entity.setXSpeed(0);
        if (entity.getYSpeed() == null) entity.setYSpeed(0);

        try {
            em.persist(entity);
            return Response.status(Response.Status.CREATED).entity(entity).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // Get all Orbs
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllOrbs() {
        try {
            List<Orb> orbs = em.createQuery("SELECT o FROM Orb o", Orb.class).getResultList();
            return Response.ok(orbs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // Get specific Orb
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response find(@PathParam("id") Long id) {
        Orb orb = em.find(Orb.class, id);
        if (orb != null) {
            return Response.ok(orb).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Get range of Orbs
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getRangeOrbs(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        try {
            List<Orb> orbs = em.createQuery("SELECT o FROM Orb o", Orb.class)
                    .setFirstResult(from)
                    .setMaxResults(to - from + 1)
                    .getResultList();
            return Response.ok(orbs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // PUT (replace) Orb
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putOrb(@PathParam("id") Long id, Orb entity) {
        Orb existing = em.find(Orb.class, id);
        if (existing == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (entity.getId() != null && !entity.getId().equals(id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID in URL does not match ID in body").build();
        }
        existing.setX(entity.getX() != null ? entity.getX() : 0);
        existing.setY(entity.getY() != null ? entity.getY() : 0);
        existing.setSize(entity.getSize() != null ? entity.getSize() : Orb.INITIAL_SIZE);
        existing.setXSpeed(entity.getXSpeed() != null ? entity.getXSpeed() : 0);
        existing.setYSpeed(entity.getYSpeed() != null ? entity.getYSpeed() : 0);

        em.merge(existing);
        return Response.ok(existing).build();
    }

    // PATCH Orb
    @PATCH
@Path("{id}")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public Response patchOrb(@PathParam("id") Long id, Orb updates) {
    Orb existing = em.find(Orb.class, id);
    if (existing == null) 
        return Response.status(Response.Status.NOT_FOUND).build();

    if (updates.getId() != null && !updates.getId().equals(id)) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("ID in URL does not match ID in body").build();
    }

    if (updates.getX() != null) existing.setX(updates.getX());
    if (updates.getY() != null) existing.setY(updates.getY());
    if (updates.getSize() != null) existing.setSize(updates.getSize());

    // Safe speed update using your helper method
    if (updates.getXSpeed() != null || updates.getYSpeed() != null) {
        Integer newX = (updates.getXSpeed() != null) ? updates.getXSpeed() : existing.getXSpeed();
        Integer newY = (updates.getYSpeed() != null) ? updates.getYSpeed() : existing.getYSpeed();
        existing.setSpeedDirectly(newX, newY);
    }

    em.merge(existing);
    return Response.ok(existing).build();
}

    // DELETE Orb
    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Long id) {
        Orb orb = em.find(Orb.class, id);
        if (orb != null) {
            em.remove(orb);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // PUT on root not allowed
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response putRootNotAllowed() {
        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                .entity("PUT on root resource is not allowed").build();
    }
}
